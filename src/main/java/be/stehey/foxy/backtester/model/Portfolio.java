package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.stehey.foxy.backtester.MessageBus;

public abstract class Portfolio {
	
	// The Portfolio class handles the positions and market
	// value of all instruments at a resolution of a "bar"
	// i.e. secondly, minutely, 5-min, 30-min, 60-min or EOD.
	
	protected MessageBus messageBus;

	protected Map<Date, Map<String, BigDecimal>> allHoldings = new HashMap<Date, Map<String, BigDecimal>>();
	protected Map<String, BigDecimal> currentHoldings;
	
	protected Map<Date, Map<String, BigDecimal>> allPositions = new HashMap<Date, Map<String, BigDecimal>>();;
	protected Map<String, BigDecimal> currentPositions;
	
	public abstract void updateSignal(Event event);
	public abstract void updateFill(Event event);
	public abstract void updateTimeIndex(Event event);
	
	public Map<Date, Map<String, BigDecimal>> getAllHoldings() {
		return allHoldings;
	}

	public Map<String, BigDecimal> getCurrentHoldings() {
		return currentHoldings;
	}

	public Map<Date, Map<String, BigDecimal>> getAllPositions() {
		return allPositions;
	}

	public Map<String, BigDecimal> getCurrentPositions() {
		return currentPositions;
	}

	protected void dumpHoldingsForSymbols(Date timestamp, List<String> symbols) {
		System.out.println("Holdings for " + timestamp);
		for(String symbol : symbols) {
			System.out.println(symbol + " : " + currentHoldings.get(symbol));
		}
		System.out.println("total: " + currentHoldings.get("total"));
		System.out.println("commission: " + currentHoldings.get("commission"));
		System.out.println("cash: " + currentHoldings.get("cash"));
		System.out.println("------------------------------------------------");
	}

}
