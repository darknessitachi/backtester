package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;
import java.text.ParseException;

import be.stehey.foxy.backtester.event.MarketDataEvent;

import com.espertech.esper.client.EPServiceProvider;

public class SimulationPortfolio {

	private String strategyName;
	private BigDecimal initialCapital;
	
	public SimulationPortfolio() { super(); }

	public void init(String strategyName) {
		this.strategyName = strategyName;
    }

	public void setInitialCapital(BigDecimal initialCapital) {
		this.initialCapital = initialCapital;
	}

	public static class SignalEventSubscriber {
		public void update(SignalEvent event) {
			System.out.println("SimulationPortfolio ==> " + String.valueOf(event.getTimestamp()));
		}
	}
}