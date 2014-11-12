package be.stehey.foxy.backtester.model;

import java.util.Date;

public class SignalEvent extends Event {

	// Handles the event of sending a signal from a Strategy object.
	// This is received by a portfolio object and acted upon.

	private String symbol;
	private long timestamp;
	private String signalType;

	public SignalEvent() {
		super();
		this.type = "SIGNAL";
	}

	public SignalEvent(String symbol, long timestamp, String signalType) {
		
		// Initializes the SignalEvent
		
		// parameters:
		// symbol - the ticker symbol, e.g. 'KBCA'
		// timestamp - the timestamp at which the signal was generated
		// signalType - 'LONG' or 'SHORT' or 'EXIT'
		
		this.type = "SIGNAL";
		this.symbol = symbol;
		this.timestamp = timestamp;
		this.signalType = signalType;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSignalType() {
		return signalType;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

}
