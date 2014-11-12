package be.stehey.foxy.backtester;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.stehey.foxy.backtester.model.DataHandler;
import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.FillEvent;
import be.stehey.foxy.backtester.model.HistoricCsvDataHandler;
import be.stehey.foxy.backtester.model.NaivePortfolio;
import be.stehey.foxy.backtester.model.Portfolio;
import be.stehey.foxy.backtester.model.SignalEvent;

public class NaivePortfolioTest {
	private final String SYMBOL = "KBCA";
	private MessageBus messageBus;
	private DataHandler dataHandler;
	private Portfolio portfolio;

	@Before
	public void setUp() throws Exception {
		final String CSV_FOLDER = "src/test/resources";
		messageBus = new SimulatedMessageBus();
		dataHandler = new HistoricCsvDataHandler(messageBus, CSV_FOLDER, Arrays.asList(SYMBOL));
		dataHandler.updateBars();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = sdf.parse("28/05/2014");
		portfolio = new NaivePortfolio(messageBus, dataHandler, startDate,
				new BigDecimal("100000.0"));
		portfolio.updateTimeIndex(messageBus.getEvent());
	}
	
	@After
	public void teardownTest() {
		dataHandler.resetBars();
	}

	@Test
	public void testInitializesAllPositionsToZero_onConstruction()
			throws Exception {
		Map<Date, Map<String, BigDecimal>> expectedPositions = new HashMap<Date, Map<String, BigDecimal>>();
		Map<String, BigDecimal> positionForSymbol = new HashMap<String, BigDecimal>();
		positionForSymbol.put(SYMBOL, new BigDecimal(0));
		expectedPositions.put(new SimpleDateFormat("dd/MM/yyyy").parse("28/05/2014"), positionForSymbol );
		assertEquals(expectedPositions, portfolio.getAllPositions());
	}
	
	@Test
	public void testGeneratesNaiveOrder_uponReceiptOfSignalEvent() 
			throws Exception {
		Event signalEvent = new SignalEvent(SYMBOL, new SimpleDateFormat("dd/MM/yyyy").parse("28/05/2014").getTime(), "LONG");
		portfolio.updateSignal(signalEvent);
		assertEquals("ORDER" , messageBus.getEvent().getType());
	}
	
	@Test
	public void testUpdatesPositions_uponReceiptOfFillEvent()
		throws Exception {
		Event fillEvent = new FillEvent(123, SYMBOL, "NYSE", new BigDecimal("100"), "BUY", new BigDecimal("2.5"), null);
		portfolio.updateFill(fillEvent);
		Map<String, BigDecimal> expectedPositions = new HashMap<String, BigDecimal>();
		expectedPositions.put(SYMBOL, new BigDecimal(100));
		assertEquals(expectedPositions, portfolio.getCurrentPositions());
	}

	@Test
	public void testUpdatesHoldings_uponReceiptOfFillEvent()
		throws Exception {
		Event fillEvent = new FillEvent(123, SYMBOL, "NYSE", new BigDecimal("100"), "BUY", new BigDecimal("2.5"), null);
		portfolio.updateFill(fillEvent);
		Map<String, BigDecimal> expectedHoldings = new HashMap<String, BigDecimal>();
		expectedHoldings.put("total", new BigDecimal("97491.7500"));
		expectedHoldings.put("commission", new BigDecimal("1.2500"));
		expectedHoldings.put("cash", new BigDecimal("97491.7500"));
		expectedHoldings.put(SYMBOL, new BigDecimal("2507.00"));
		assertEquals(expectedHoldings, portfolio.getCurrentHoldings());

		dataHandler.updateBars();
		portfolio.updateTimeIndex(messageBus.getEvent());
		expectedHoldings.put("total", new BigDecimal("100002.7500"));
		expectedHoldings.put("commission", new BigDecimal("1.2500"));
		expectedHoldings.put("cash", new BigDecimal("97491.7500"));
		expectedHoldings.put(SYMBOL, new BigDecimal("2511.00"));
		assertEquals(expectedHoldings, portfolio.getCurrentHoldings());

	}
}
