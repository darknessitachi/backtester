package be.stehey.foxy.backtester;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import be.stehey.foxy.backtester.event.MarketDataEvent;
import be.stehey.foxy.backtester.listener.MyListener;
import be.stehey.foxy.backtester.model.BuyAndHoldStrategy;
import be.stehey.foxy.backtester.model.DataHandler;
import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.HistoricCsvDataHandler;
import be.stehey.foxy.backtester.model.NaivePortfolio;
import be.stehey.foxy.backtester.model.Portfolio;
import be.stehey.foxy.backtester.model.Strategy;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;
import com.espertech.esperio.csv.CSVInputAdapterSpec;

/**
 * Strategy simulation using:
 * - simulated message bus and execution handler
 * - naive portfolio
 * - buy & hold strategy
 * - CSV data handler fed with over 13 years of AAPL EOD financial data
 */
public class App 
{
	private static final BigDecimal INITIAL_CAPITAL = new BigDecimal("20000.00");
	private static final String SYMBOL = "AAPL";
	private static final String CSV_FOLDER = "src/test/resources";
	
    public static void main( String[] args ) throws Exception {
    	runWithBackTestRunner();
    }
    
    private static void runWithBackTestRunner() {
    	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		BackTestRunner runner = (BackTestRunner) context.getBean("backTestRunner");
		runner.run();
    }
    
    private static void runExperimentWithEsper() throws Exception {
    	Configuration config = new Configuration();
    	config.addEventType("MarketDataEvent", MarketDataEvent.class);
		config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		config.getEngineDefaults().getLogging().setEnableTimerDebug(true);
		config.configure();
    	
    	EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
    	
    	String expression = "select * from MarketDataEvent.win:length_batch(15)";
    	EPStatement statement = epService.getEPAdministrator().createEPL(expression); 
    	
    	MyListener listener = new MyListener();
    	statement.addListener(listener);

    	AdapterInputSource adapterInputSource = new AdapterInputSource("EURUSD_Candlestick_1_m_BID_01.01.2010-31.12.2010.csv");
    	CSVInputAdapterSpec spec = new CSVInputAdapterSpec(adapterInputSource, "MarketDataEvent");

    	String jmsEsperQueueExpression = "insert into ESPER.QUEUE select * from MarketDataEvent";
    	EPStatement jmsEsperQueueStatement = epService.getEPAdministrator().createEPL(jmsEsperQueueExpression);
    	
    	spec.seteventTypeName("ESPER.QUEUE");
    	CSVInputAdapter inputAdapter = new CSVInputAdapter(epService, spec);
    	
    	inputAdapter.start();
    	
    	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		JmsTemplate jmsTemplate=(JmsTemplate) context.getBean("jmsActiveMQTemplate");
		Message receivedMessage=jmsTemplate.receive("ESPER.QUEUE");
		ObjectMessage msg = (ObjectMessage)receivedMessage;
		System.out.println("Message Received :" + msg.getObject().toString());
    }
    
    private static void runClassic() throws Exception {
        System.out.println( "Start of run." );
        MessageBus messageBus = new SimulatedMessageBus();
        DataHandler dataHandler = new HistoricCsvDataHandler(messageBus, CSV_FOLDER, Arrays.asList(SYMBOL));
        Strategy strategy = new BuyAndHoldStrategy(messageBus, dataHandler);
		Portfolio portfolio = new NaivePortfolio(messageBus, dataHandler, new SimpleDateFormat("dd/MM/yyyy").parse("28/05/2014"), INITIAL_CAPITAL);
		ExecutionHandler broker = new SimulatedExecutionHandler(messageBus);
		
		// Update the bars (specific backtest code, as opposed to live trading)
		while(true) {
			if(dataHandler.isContinueBackTest())
				dataHandler.updateBars();
			else
				break;
		
			// Handle the events
			while(true) {
				Event event = messageBus.getEvent();
				if(event == null)
					break;
				switch (event.getType()) {
					case "MARKET":
						System.out.println("MARKET");
						strategy.calculate_signals(event);
						portfolio.updateTimeIndex(event);
						break;
					case "SIGNAL":
						System.out.println("SIGNAL");
						portfolio.updateSignal(event);
						break;
					case "ORDER":
						System.out.println("ORDER");
						broker.executeOrder(event);
						break;
					case "FILL":
						System.out.println("FILL");
						portfolio.updateFill(event);
						break;
				}
				sleep(0);
			}
		}
		System.out.println("End of run.");
    }
    
    private static void sleep(long period) {
    	if(period == 0)
    		return;
		try {
		    Thread.sleep(period);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
}
