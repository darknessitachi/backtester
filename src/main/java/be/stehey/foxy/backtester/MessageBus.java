package be.stehey.foxy.backtester;

import java.util.ArrayList;
import java.util.List;

import be.stehey.foxy.backtester.model.Event;

public abstract class MessageBus {
	protected List<Event> events;
	
	public abstract void publishEvent(Event event);
	public abstract Event getEvent();
	
	public MessageBus() {
		super();
		this.events = new ArrayList<Event>();
	}
	
	public MessageBus(List<Event> events) {
		super();
		this.events = events;
	}
	
	
}
