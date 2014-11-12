package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;

public class FillEvent extends Event {
	
	// Encapsulates the notion of a filled order, as returned
	// from a brokerage. Stores the quantity of an instrument
	// actually filled and at what price. In addition, stores
	// the commission of the trade from the brokerage.
	
	private long timeIndex;
	public long getTimeIndex() {
		return timeIndex;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getExchange() {
		return exchange;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getDirection() {
		return direction;
	}

	public BigDecimal getFillCost() {
		return fillCost;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	private String symbol;
	private String exchange;
	private BigDecimal quantity;
	private String direction;
	private BigDecimal fillCost;
	private BigDecimal commission;

	public FillEvent() {}
	
	public FillEvent(long timeIndex, String symbol, String exchange, BigDecimal quantity, String direction, BigDecimal fillCost, BigDecimal commission) {
		
		// Initializes the FillEvent object. Sets the symbol, exchange,
		// quantity, direction, cost of fill and an optional commission.
		
		// If commission is not provided, the Fill object will 
		// calculate it based on the trade size and Interactive
		// Brokers fees.
		
		// parameters:
		// timeIndex - the bar-resolution when the order was filled.
		// symbol - the instrument which was filled.
		// exchange - the exchange where the order was filled.
		// quantity - the filled quantity
		// direction - the direction of fill ('BUY' or 'SELL')
		// fillCost - the holdings value in dollars
		// commission - an optional commission sent from IB
		
		super();
		this.type = "FILL";
		this.timeIndex = timeIndex;
		this.symbol = symbol;
		this.exchange = exchange;
		this.quantity = quantity;
		this.direction = direction;
		this.fillCost = fillCost;
		
		if(commission==null)
			this.commission = calculateIBCommission();
		else
			this.commission = commission;
	}

	private BigDecimal calculateIBCommission() {
		// Calculates the fees on trading based on an Interactive
		// Brokers fee structure for API, in USD.
		
		// This does not include exchange or ECN fees.
		
		// Based on "US API Directed Orders"
		// https://www.interactivebrokers.com/en/index.php?f=commission&p=stocks2
		
		BigDecimal commission = new BigDecimal("1.3");
		BigDecimal cost;
		if(this.quantity.compareTo(new BigDecimal("500")) <= 0) {
			cost = new BigDecimal("0.013").multiply(quantity);
			if(cost.compareTo(commission) > 0)
				commission = cost;
		} else {
			cost = new BigDecimal("0.008").multiply(quantity);
			if(cost.compareTo(commission) > 0)
				commission = cost;			
		}
		cost = new BigDecimal("0.005").multiply(quantity).multiply(fillCost);
		if(cost.compareTo(commission) < 0)
			commission = cost;			
		return commission;
	}

}
