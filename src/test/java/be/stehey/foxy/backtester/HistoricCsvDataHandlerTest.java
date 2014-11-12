package be.stehey.foxy.backtester;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.stehey.foxy.backtester.model.HistoricCsvDataHandler;
import be.stehey.foxy.backtester.model.OHLCBar;

public class HistoricCsvDataHandlerTest {
	final MessageBus messageBus = new SimulatedMessageBus();
	final String CSV_FOLDER = "src/test/resources";
	final List<String> SYMBOLS = Arrays.asList("KBCA");
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	HistoricCsvDataHandler dataHandler;
	
	@Before
	public void setupTest() {
		dataHandler = new HistoricCsvDataHandler(messageBus, CSV_FOLDER, SYMBOLS);
	}
	
	@After
	public void teardownTest() {
		dataHandler.resetBars();
	}

	@Test
	public void testCallingUpdateBars_IsHandledProperly_WhenNoMoreDataIsAvailable() throws Exception {
		dataHandler.updateBars();
		dataHandler.updateBars();
		OHLCBar firstBar = dataHandler.getLatestBars(SYMBOLS.get(0), 1).get(0);
		dataHandler.updateBars();		
		OHLCBar secondBar = dataHandler.getLatestBars(SYMBOLS.get(0), 1).get(0);
		assertEquals(firstBar, secondBar);
	}
	
	@Test
	public void testGettingLatest2Bars_ReturnsCorrectData_AfterCallingUpdateBarsTwice() throws Exception  {
		dataHandler.updateBars();
		dataHandler.updateBars();
		List<OHLCBar> ohlcBarList = dataHandler.getLatestBars(SYMBOLS.get(0), 2);
		assertEquals(sdf.parse("2014-05-28"), ohlcBarList.get(0).getTimestamp());
		assertEquals(sdf.parse("2014-05-30"), ohlcBarList.get(1).getTimestamp());
	}
	
	@Test
	public void testDataHandler_ReadsInSymbolCsvUponConstruction() throws Exception {
		dataHandler.updateBars();
		OHLCBar expectedBar = dataHandler.getLatestBars(SYMBOLS.get(0), 1).get(0);
		assertEquals(sdf.parse("2014-05-28"), expectedBar.getTimestamp());
		assertEquals(new BigDecimal("24.76"), expectedBar.getOpenPrice());
		assertEquals(new BigDecimal("25.12"), expectedBar.getHighPrice());
		assertEquals(new BigDecimal("24.73"), expectedBar.getLowPrice());
		assertEquals(new BigDecimal("25.07"), expectedBar.getClosePrice());
	}
	
	@Test
	public void testCallingUpdateBars_Publishes_A_MarketEvent_IfNextBarIsAvailable() throws Exception {
		dataHandler.updateBars();
		assertEquals("MARKET", messageBus.getEvent().getType());
	}
	
	@Test
	public void testContinueBacktestIsSetToFalse_WhenEndOfCsvDataIsReached() throws Exception {
		dataHandler.updateBars();
		assertTrue(dataHandler.isContinueBackTest());
		dataHandler.updateBars();
		assertTrue(dataHandler.isContinueBackTest());
		dataHandler.updateBars();
		assertFalse(dataHandler.isContinueBackTest());
	}
}
