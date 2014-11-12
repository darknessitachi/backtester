package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.stehey.foxy.backtester.MessageBus;

public class NaivePortfolio extends Portfolio {

	// The NaivePortfolio object is designed to send orders to
	// a brokerage object with a constant quantity size blindly,
	// i.e. without any risk management or position sizing. It is
	// used to test simpler strategies such as BuyAndHoldStrategy.

	private MessageBus messageBus;
	private DataHandler dataHandler;
	private Date startDate;
	private BigDecimal initialCapital;
	private List<String> symbolList;
		
	public NaivePortfolio() { super(); }
	
	public NaivePortfolio(MessageBus messageBus, DataHandler dataHandler, Date startDate, BigDecimal initialCapital) {
		super();
		this.messageBus = messageBus;
		this.dataHandler = dataHandler;
		this.startDate = startDate;
		this.initialCapital = initialCapital;
		this.symbolList = dataHandler.getSymbolList();
		
		constructAllPositions();
		this.currentPositions = constructCurrentPositions();
				
		constructAllHoldings();
		this.currentHoldings = constructCurrentHoldings();
	}
	
	@Override
	public void updateTimeIndex(Event event) {
		// Add a new record to the positions matrix for the current
		// market data bar. This reflects the PREVIOUS bar, i.e. all
		// current market data at this stage is known (OHLCVI).
		//
		// Makes use of the MarketEvent from the events queue.
		
		if(event.getType() != "MARKET")
			return;
		
		Map<String, OHLCBar> bars = new HashMap<String, OHLCBar>();
		for (String symbol : symbolList) {
			List<OHLCBar> latestBars = dataHandler.getLatestBars(symbol, 1);
			if (latestBars!=null)
				bars.put(symbol, latestBars.get(0));
		}
		
		// Update positions
		Map<String, BigDecimal> positions = new HashMap<String, BigDecimal>();
		for (String symbol : symbolList) {
			positions.put(symbol, currentPositions.get(symbol));
		}
		allPositions.put(bars.get(symbolList.get(0)).getTimestamp(), positions);
		currentPositions = positions;
		
		// Update holdings
		Map<String, BigDecimal> holdings = new HashMap<String, BigDecimal>();
		holdings.put("cash", currentHoldings.get("cash"));
		holdings.put("commission", currentHoldings.get("commission"));
		for (String symbol : symbolList) {
			// Approximation of real value
			BigDecimal marketValue = currentPositions.get(symbol).multiply(bars.get(symbol).getClosePrice());
			holdings.put(symbol, marketValue);
			holdings.put("total", currentHoldings.get("cash").add(marketValue));
		}
		allHoldings.put(bars.get(symbolList.get(0)).getTimestamp(), holdings);
		currentHoldings = holdings;
		dumpHoldingsForSymbols(bars.get(symbolList.get(0)).getTimestamp(), symbolList);
	}

	@Override
	public void updateSignal(Event event) {
		if (event.getType() != "SIGNAL")
			return;
		OrderEvent orderEvent = generateNaiveOrder((SignalEvent)event);
		messageBus.publishEvent(orderEvent);
	}

	@Override
	public void updateFill(Event event) {
		if(event.getType() != "FILL")
			return;
		updatePositionsFromFill((FillEvent)event);
		updateHoldingsFromFill((FillEvent)event);
	}

	private void constructAllPositions() {
		allPositions.put(startDate, constructCurrentPositions());
	}

	private Map<String, BigDecimal> constructCurrentPositions() {
		Map<String, BigDecimal> positions = new HashMap<String, BigDecimal>();
		for (String symbol : symbolList) {
			positions.put(symbol, new BigDecimal("0"));
		}
		return positions;
	}

	private void constructAllHoldings() {
		allHoldings.put(startDate, constructCurrentHoldings());
	}

	private Map<String, BigDecimal> constructCurrentHoldings() {
		Map<String, BigDecimal> holdings = new HashMap<String, BigDecimal>();
		for (String symbol : symbolList) {
			holdings.put(symbol, new BigDecimal("0.0"));
		}
		holdings.put("cash", initialCapital);
		holdings.put("commission", new BigDecimal("0.0"));
		holdings.put("total", initialCapital);
		return holdings;
	}
	
	private void updatePositionsFromFill(FillEvent fillEvent) {
		// Takes a FillEvent object and updates the position matrix
		// to reflect the new position.
		
		// parameters:
		// fillEvent - the FillEvent object to update the positions with
		int fillDirection = 0;
		if(fillEvent.getDirection() == "BUY")
			fillDirection = 1;
		if(fillEvent.getDirection() == "SELL")
			fillDirection = -1;
		
		// Update positions list with new quantities
		currentPositions.put(fillEvent.getSymbol(), currentPositions.get(fillEvent.getSymbol()).add(fillEvent.getQuantity().multiply(new BigDecimal(fillDirection))));
	}

	private void updateHoldingsFromFill(FillEvent fillEvent) {
		// Takes a FillEvent object and updates the holdings matrix
		// to reflect the new position.
		
		// parameters:
		// fillEvent - the FillEvent object to update the holdings with
		int fillDirection = 0;
		if(fillEvent.getDirection() == "BUY")
			fillDirection = 1;
		if(fillEvent.getDirection() == "SELL")
			fillDirection = -1;
		
		// Update holdings list with new quantities
		BigDecimal fillCost = dataHandler.getLatestBars(fillEvent.getSymbol(), 1).get(0).getClosePrice();
		BigDecimal cost = fillCost.multiply(new BigDecimal(fillDirection)).multiply(fillEvent.getQuantity());
		
		currentHoldings.put(fillEvent.getSymbol(), currentHoldings.get(fillEvent.getSymbol()).add(cost));
		currentHoldings.put("commission", currentHoldings.get("commission").add(fillEvent.getCommission()));
		currentHoldings.put("cash", currentHoldings.get("cash").subtract(cost).subtract(fillEvent.getCommission()));
		currentHoldings.put("total", currentHoldings.get("total").subtract(cost).subtract(fillEvent.getCommission()));
	}
	
	private OrderEvent generateNaiveOrder(SignalEvent signal) {
		// Simply transacts an OrderEvent object as a constant quantity
		// sizing of the signal object, without risk management or
		// position sizing considerations.
		
		// parameters:
		// signal - the SignalEvent signal information
		
		OrderEvent order = null;
		
		String symbol = signal.getSymbol();
		String direction = signal.getSignalType();
		//BigDecimal strength = signal.getStrength();
		
		BigDecimal marketQuantity = new BigDecimal("100");
		BigDecimal currentQuantity = currentPositions.get(symbol);
		String orderType = "MKT";
		
		if (direction == "LONG" && currentQuantity.compareTo(new BigDecimal("0")) == 0)
			order = new OrderEvent(symbol, orderType, marketQuantity, "BUY");

		if (direction == "SHORT" && currentQuantity.compareTo(new BigDecimal("0")) == 0)
			order = new OrderEvent(symbol, orderType, marketQuantity, "SELL");

		if (direction == "EXIT" && currentQuantity.compareTo(new BigDecimal("0")) > 0)
			order = new OrderEvent(symbol, orderType, currentQuantity.abs(), "SELL");

		if (direction == "EXIT" && currentQuantity.compareTo(new BigDecimal("0")) < 0)
			order = new OrderEvent(symbol, orderType, currentQuantity.abs(), "BUY");
		
		return order;
	}
	
}
