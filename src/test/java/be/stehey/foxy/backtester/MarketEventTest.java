package be.stehey.foxy.backtester;

import static org.junit.Assert.*;

import org.junit.Test;

import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.MarketEvent;

public class MarketEventTest {
	@Test
	public void testMarketEvent_GetsCorrectTypeAttribute_WhenInitialized() {
		Event event = new MarketEvent();
		assertTrue(event.getType().equals("MARKET"));
	}
}
