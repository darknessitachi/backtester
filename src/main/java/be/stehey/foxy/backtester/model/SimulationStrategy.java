package be.stehey.foxy.backtester.model;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.stehey.foxy.backtester.annotations.Subscriber;
import be.stehey.foxy.backtester.event.MarketDataEvent;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.DeploymentResult;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.time.CurrentTimeEvent;

public class SimulationStrategy {

	private EPServiceProvider epService;
	private EPAdministrator administrator;
	private EPRuntime epRuntime;
	private EPDeploymentAdmin deployAdmin;
	private final String name = "simulation_strategy";
	private static Map<String, Boolean> bought = new HashMap<String, Boolean>();
	private static BigDecimal openPrice;
	private static Boolean positionOpened = false;
	private static Integer winningTrades = 0;
	private static Integer loosingTrades = 0;
	private static Integer totalTrades = 0;
	private static BigDecimal pl = BigDecimal.ZERO;

	public void init() {
		epService = getEPServiceProviderForStrategy();
		administrator = epService.getEPAdministrator();
		epRuntime = epService.getEPRuntime();
		deployAdmin = administrator.getDeploymentAdmin();
		
		administrator.getConfiguration().addEventType("MarketDataEvent", MarketDataEvent.class);
		administrator.getConfiguration().addEventType("SignalEvent", MarketDataEvent.class);

		administrator.getConfiguration().addVariable("slowEMA", Integer.class, 1);
		administrator.getConfiguration().addVariable("fastEMA", Integer.class, 1);
	}
	
	public void configureParams(Integer slowEMA, Integer fastEMA) {
		epRuntime.setVariableValue("slowEMA", slowEMA);
		epRuntime.setVariableValue("fastEMA", fastEMA);
	}

	public void configureStatements() {
		Module module;
		DeploymentResult deployResult;
		try {
			module = deployAdmin.read("simulation-strategy.epl");
			deployResult = deployAdmin.deploy(module, new DeploymentOptions());
			List<EPStatement> statements = deployResult.getStatements();
			for (EPStatement stmt : statements) {
				processAnnotations(stmt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("slowEMA: " + epRuntime.getVariableValue("slowEMA"));
		System.out.println("fastEMA: " + epRuntime.getVariableValue("fastEMA"));
	}
	
	private void processAnnotations(EPStatement statement) throws Exception {
		System.out.println("processAnnotations for statement " + statement.getText());
		Annotation[] annotations = statement.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Subscriber) {
				Subscriber subscriber = (Subscriber) annotation;
				Object obj = getSubscriber(subscriber.className());
				statement.setSubscriber(obj);
			}
		}
	}

	private Object getSubscriber(String fqdn) throws Exception {		
		Class<?> cl = Class.forName(fqdn);
		return cl.newInstance();
	}

	private EPServiceProvider getEPServiceProviderForStrategy() {
		return EPServiceProviderManager.getProvider(this.name);
	}

	public SimulationStrategy() {
		super();
		calculateInitialBought();
	}

	public String getName() {
		return this.name;
	}

	public static class MarketDataEventSubscriber {
		public void update(MarketDataEvent event) {
			//try {
			//	System.out.println("SimulationStrategy::MarketDataEventSubscriber ==> " + event.getTime() + " (" + String.valueOf(event.getTimestamp()) + ")");
			//	//calculateSignals(event);
			//} catch (ParseException e) {
			//	System.out.println("SimulationStrategy::MarketDataEventSubscriber ==> " + event.getTime());
			//}
		}
		
		public void calculateSignals(MarketDataEvent event) {
			String symbol = "EURUSD";
			if (!bought.get(symbol).booleanValue()) {
				try {
					SignalEvent signalEvent = new SignalEvent(symbol, event.getTimestamp(), "LONG");
					EPServiceProviderManager.getDefaultProvider().getEPRuntime().route(signalEvent);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				System.out.println("Added SignalEvent to queue");
				bought.put(symbol, Boolean.TRUE);
			}
			return;
		}	
	}
	
	public static class Ema5EventSubscriber {
		public void update(Double ema5) {
			//System.out.println("SimulationStrategy::Ema5EventSubscriber ==> " + ema5.toString());
		}		
	}

	public static class OpenPositionSubscriber {
		public void update(String timestamp, BigDecimal underlyingSpot, Double indicatorValue) {
			if (!positionOpened) {
				//System.out.println("SimulationStrategy::OpenPositionSubscriber ==> " + underlyingSpot.toString() + " (" + timestamp + ") : " + indicatorValue.toString());
				openPrice = underlyingSpot;
				positionOpened = true;
			}
		}		
	}

	public static class ClosePositionSubscriber {
		public void update(String timestamp, BigDecimal underlyingSpot, Double indicatorValue) {
			if (positionOpened) {
				//System.out.println("SimulationStrategy::ClosePositionSubscriber ==> " + underlyingSpot.toString() + " (" + timestamp + ") : " + indicatorValue.toString());
				BigDecimal delta = underlyingSpot.subtract(openPrice);
				positionOpened = false;
				totalTrades++;
				pl = pl.add(delta);
				if (delta.compareTo(BigDecimal.ZERO) == 1) {
					winningTrades++;
					System.out.println("SimulationStrategy::ClosePositionSubscriber ==> " + timestamp + ": [+] => p/l=" + pl.toString() + " (" + (winningTrades * 100 / totalTrades) + " %: " + winningTrades + "/" + totalTrades + ")");
				} else {
					System.out.println("SimulationStrategy::ClosePositionSubscriber ==> " + timestamp + ": [-] => p/l=" + pl.toString() + " (" + (winningTrades * 100 / totalTrades) + " %: " + winningTrades + "/" + totalTrades + ")");
					loosingTrades++;
				}
			}
		}		
	}

	public void calculateInitialBought() {
		bought.put("EURUSD", Boolean.FALSE);
	}
		
}
