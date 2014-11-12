package be.stehey.foxy.backtester.listener;

import be.stehey.foxy.backtester.MessageBus;
import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.MarketEvent;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class MyListener implements UpdateListener {
	private MessageBus messageBus;
	
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		EventBean event = newEvents[0];
		System.out.println("timestamp: " + event.get("time") + "(" + event.get("timestamp") + ") => close:" + event.get("close"));
		Event marketEvent = new MarketEvent();
		messageBus.publishEvent(marketEvent);
	}

	public MyListener(MessageBus messageBus) {
		super();
		this.messageBus = messageBus;
	}

	public MyListener() {
		super();
	}
}
