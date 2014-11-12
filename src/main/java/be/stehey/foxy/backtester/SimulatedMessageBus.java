package be.stehey.foxy.backtester;

import be.stehey.foxy.backtester.model.Event;

public class SimulatedMessageBus extends MessageBus {

	@Override
	public void publishEvent(Event event) {
		events.add(event);
	}

	@Override
	public Event getEvent() {
		if(events.size() == 0)
			return null;
		try {
			Event returnEvent = events.get(0).clone();
			events.remove(0);
			return(returnEvent);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	private void dumpEvents(String prefix, String suffix) {
		System.out.println(prefix);
		for(Event e : events) {
			System.out.println(" event: " + e.getType());
		}
		System.out.println(suffix);
	}
}
