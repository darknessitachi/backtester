package be.stehey.foxy.backtester.model;

import java.util.List;

import be.stehey.foxy.backtester.MessageBus;

public abstract class DataHandler {
	// DataHandler is an abstract base class providing an interface for
	// all subsequent (inherited) data handlers (both live and historic).
	
	// The goal of a (derived) DataHandler object is to output a generated
	// set of bars (OLHCVI) for each symbol requested.
	
	// This will replicate how a live strategy would function as current
	// market data would be sent "down the pipe". Thus a historic and live
	// system will be treated identically by the rest of the backtesting suite.

	protected MessageBus messageBus;
	protected List<String> symbolList;
	protected boolean continueBackTest = true;

	public void setSymbolList(List<String> symbolList) {
		this.symbolList = symbolList;
	}
	public List<String> getSymbolList() {
		return symbolList;
	}
	public boolean isContinueBackTest() {
		return continueBackTest;
	}

	public abstract List<OHLCBar> getLatestBars(String symbol, Integer numberOfBars);
	public abstract void updateBars();
	public abstract void resetBars();

}
