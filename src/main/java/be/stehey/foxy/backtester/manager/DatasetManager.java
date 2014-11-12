package be.stehey.foxy.backtester.manager;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import be.stehey.foxy.backtester.event.MarketDataEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;
import com.espertech.esperio.csv.CSVInputAdapterSpec;

public class DatasetManager {
	private String datasetFileName;
	private String jmsQueueName;
	private JmsTemplate jmsTemplate;
	private String strategyName;

	private CSVInputAdapter inputAdapter;
	private EPStatement datasetDestinationStatement;

	public void init(String strategyName) {
		
		this.strategyName = strategyName;
		
		AdapterInputSource adapterInputSource = new AdapterInputSource(datasetFileName);
    	CSVInputAdapterSpec spec = new CSVInputAdapterSpec(adapterInputSource, "MarketDataEvent");
    	inputAdapter = new CSVInputAdapter(getEPServiceProviderForStrategy(), spec);
    	
		//String expression = "select * from MarketDataEvent";
		//datasetDestinationStatement = createEPL(epService, expression);
		//datasetDestinationStatement = registerStatementAndListener(epService, expression, new MarketDataListener(jmsQueueName));
	}
	
	public void startPublishingData() {
		System.out.println("************* => DatasetManager::startPublishingData");
    	inputAdapter.start();
	}
	
	private EPServiceProvider getEPServiceProviderForStrategy() {
		return EPServiceProviderManager.getProvider(this.strategyName);
	}

	public DatasetManager() {
		super();
	}
	
	public void setJmsQueueName(String jmsQueueName) {
		this.jmsQueueName = jmsQueueName;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDatasetFileName(String datasetFileName) {
		this.datasetFileName = datasetFileName;
	}
	
	class MarketDataListener implements UpdateListener {

		private String destinationQueueName;
		
		public MarketDataListener(String destinationQueueName) {
			this.destinationQueueName = destinationQueueName;
		}
		
		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			final MarketDataEvent event = (MarketDataEvent) newEvents[0].getUnderlying();
			System.out.println("MarketDataListener ==> " + newEvents.toString());
			jmsTemplate.send(destinationQueueName, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(event);
				}
			});
		}
		
	}
}
