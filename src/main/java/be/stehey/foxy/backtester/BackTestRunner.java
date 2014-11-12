package be.stehey.foxy.backtester;

import be.stehey.foxy.backtester.manager.DatasetManager;
import be.stehey.foxy.backtester.model.SimulationPortfolio;
import be.stehey.foxy.backtester.model.SimulationStrategy;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class BackTestRunner {
	private DatasetManager datasetManager;
	private SimulationStrategy strategy;
	private SimulationPortfolio portfolio;

	public void setPortfolio(SimulationPortfolio portfolio) {
		this.portfolio = portfolio;
	}

	public void setStrategy(SimulationStrategy strategy) {
		this.strategy = strategy;
	}

	public void setDatasetManager(DatasetManager datasetManager) {
		this.datasetManager = datasetManager;
	}

	public void run() {
		EPServiceProvider epService = configureEPServiceProvider();
		epService.getEPAdministrator().getConfiguration().addImport("be.stehey.foxy.backtester.annotations.*");
		
		strategy.init();
		datasetManager.init(strategy.getName());
		portfolio.init(strategy.getName());

		strategy.configureParams(33, 8);
		strategy.configureStatements();
		datasetManager.startPublishingData();

		strategy.configureParams(34, 7);
		strategy.configureStatements();
		datasetManager.startPublishingData();
	}

	private EPServiceProvider configureEPServiceProvider() {
    	EPServiceProvider epService = EPServiceProviderManager.getProvider(strategy.getName());
    	epService.initialize();
    	return epService;
	}

}
