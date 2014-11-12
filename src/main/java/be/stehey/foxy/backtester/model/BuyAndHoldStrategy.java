package be.stehey.foxy.backtester.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.stehey.foxy.backtester.MessageBus;

public class BuyAndHoldStrategy extends Strategy {
	private Map<String, Boolean> bought = new HashMap<String, Boolean>();
	
	public BuyAndHoldStrategy(MessageBus messageBus, DataHandler dataHandler) {
		super(messageBus, dataHandler);
		calculateInitialBought();
	}

	private void calculateInitialBought() {
		for(String symbol : dataHandler.symbolList) {
			bought.put(symbol, Boolean.FALSE);
		}
	}

	@Override
	public void calculate_signals(Event event) {
		if(!event.getType().equalsIgnoreCase("MARKET"))
			return;
		for(String symbol : dataHandler.getSymbolList()) {
			List<OHLCBar> latest_bars = dataHandler.getLatestBars(symbol, 1);
			if (latest_bars!=null && latest_bars.size() != 0) {
				if (!bought.get(symbol).booleanValue()) {
					//SignalEvent signalEvent = new SignalEvent(symbol, latest_bars.get(0).getTimestamp(), "LONG");
					//messageBus.publishEvent(signalEvent);
System.out.println("Added SignalEvent to queue");
					bought.put(symbol, Boolean.TRUE);
				}
			}
		}
		return;
	}

}
