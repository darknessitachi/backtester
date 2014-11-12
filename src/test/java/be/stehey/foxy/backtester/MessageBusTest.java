package be.stehey.foxy.backtester;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.MarketEvent;
import be.stehey.foxy.backtester.model.SignalEvent;

public class MessageBusTest {
	private MessageBus messageBus;
	
	@Before
	public void setUp() {
		messageBus = new SimulatedMessageBus();
	}
	
	@Test
	public void testGetEvent_ReturnsAndRemovesOldestMessageFromQueue_WhenQueueNotEmpty() throws Exception {
		Event event1 = new MarketEvent();
		messageBus.publishEvent(event1);
		Event event2 = new SignalEvent();
		messageBus.publishEvent(event2);
		assertEquals(messageBus.getEvent().getType(), event1.getType());
		assertEquals(messageBus.getEvent().getType(), event2.getType());
	}

	@Test
	public void testGetEvent_ReturnsNull_WhenQueueEmpty() throws Exception {
		assertNull(messageBus.getEvent());
	}
}
