package be.stehey.foxy.backtester.model;

import be.stehey.foxy.backtester.model.Event;

public class MarketEvent extends Event {

	// Handles the event of receiving a new market update with
	// corresponding bars
	
	public MarketEvent() {
		super();
		this.type = "MARKET";
	}
	
}
