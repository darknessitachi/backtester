package be.stehey.foxy.backtester;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.stehey.foxy.backtester.model.BuyAndHoldStrategy;
import be.stehey.foxy.backtester.model.DataHandler;
import be.stehey.foxy.backtester.model.HistoricCsvDataHandler;
import be.stehey.foxy.backtester.model.SignalEvent;
import be.stehey.foxy.backtester.model.Strategy;

public class BuyAndHoldStrategyTest {
	private MessageBus messageBus = new SimulatedMessageBus();
	private DataHandler dataHandler;
	private Strategy strategy;
	
	@Before
	public void setup() {
		final String csvFolder = "src/test/resources";
		final String SYMBOL = "KBCA";
		dataHandler = new HistoricCsvDataHandler(messageBus, csvFolder, Arrays.asList(SYMBOL));
		dataHandler.updateBars();
		strategy = new BuyAndHoldStrategy(messageBus, dataHandler);
	}
	
	@After
	public void teardownTest() {
		dataHandler.resetBars();
	}

	@Test
	public void testCalculateSignals_GeneratesSignalEvent_WhenCalled() throws Exception {
		strategy.calculate_signals(messageBus.getEvent());
		assertEquals((new SignalEvent()).getType(), messageBus.getEvent().getType());
	}
}
