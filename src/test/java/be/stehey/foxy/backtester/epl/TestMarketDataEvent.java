package be.stehey.foxy.backtester.epl;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import be.stehey.foxy.backtester.event.MarketDataEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;

public class TestMarketDataEvent {
	
	private EPServiceProvider epService;
	private SupportUpdateListener listener;
	
	@Before
	public void setUp() {
		Configuration config = new Configuration();
		config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		config.getEngineDefaults().getLogging().setEnableTimerDebug(true);
		epService = EPServiceProviderManager.getDefaultProvider(config);
		
		epService.initialize();
		
		epService.getEPAdministrator().getConfiguration().addEventType("RawMarketDataEvent", MarketDataEvent.class);
		
		listener = new SupportUpdateListener();
	}
	
	@Test
	public void testAllInputsAppearOnOutput() throws Exception {
		String query = "select * from RawMarketDataEvent";
		EPStatement statement = epService.getEPAdministrator().createEPL(query);
		
		statement.addListener(listener);
		
		epService.getEPRuntime().sendEvent(new MarketDataEvent("01.01.2010 00:00:00.000", 
				new BigDecimal("1.43283"),
				new BigDecimal("1.43283"),
				new BigDecimal("1.43267"),
				new BigDecimal("1.43267"),
				new BigDecimal("74.30")));
		
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), 
				"timestamp, open, volume".split(","),
					new Object[]{1261868400000L, 
					new BigDecimal("1.43283"), 
					new BigDecimal("74.30")});
	}
	
}
