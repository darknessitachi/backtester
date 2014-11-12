package be.stehey.foxy.backtester.manager;

import org.springframework.jms.core.JmsTemplate;

import be.stehey.foxy.backtester.model.Strategy;

public class BackTestManager {
	private DatasetManager datasetManager;
	private SubscriptionManager subscriptionManager;
	private JmsTemplate jmsTemplate;

	private Strategy strategy;
	private String symbol;
	private String datasetFileName;

	public void configure(String symbol, Strategy strategy, String datasetFileName) {
		this.symbol = symbol;
		this.strategy = strategy;
		this.datasetFileName = datasetFileName;
		
		String queueName = "BT01_0001";
		
		datasetManager = new DatasetManager();
		//datasetManager.configure(datasetFileName, jmsTemplate, queueName, strategy);
	}
	
	public void execute() {
		datasetManager.startPublishingData();
	}


	public void setDatasetManager(DatasetManager datasetManager) {
		this.datasetManager = datasetManager;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
}
