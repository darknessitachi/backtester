package be.stehey.foxy.backtester.model;

import be.stehey.foxy.backtester.MessageBus;

public abstract class Strategy {
	protected MessageBus messageBus;
	protected DataHandler dataHandler;

	public Strategy() {}
	
	public Strategy(MessageBus messageBus, DataHandler dataHandler) {
		this.dataHandler = dataHandler;
		this.messageBus = messageBus;
	}

	public MessageBus getMessageBus() {
		return messageBus;
	}

	public void setMessageBus(MessageBus messageBus) {
		this.messageBus = messageBus;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}

	public void setDataHandler(DataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public abstract void calculate_signals(Event event);
}
