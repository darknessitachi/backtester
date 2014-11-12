package be.stehey.foxy.backtester;

import java.math.BigDecimal;
import java.util.Date;

import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.FillEvent;
import be.stehey.foxy.backtester.model.OrderEvent;

public class SimulatedExecutionHandler extends ExecutionHandler {

	// The simulated execution handler simply converts all order
	// objects into their equivalent fill objects automatically
	// without latency, slippage or fill-ratio issues.
	
	// This allows a straight-forward "first go" test of any strategy,
	// before implementation with a more sophisticated execution
	// handler.

	public SimulatedExecutionHandler() {
		super();
	}

	public SimulatedExecutionHandler(MessageBus messageBus) {
		super();
		this.messageBus = messageBus;
	}

	@Override
	public void executeOrder(Event event) {
		// Simply converts Order objects into Fill objects naively,
		// i.e. without any latency, slippage or fill ratio problems.
		
		// parameters:
		// event - containing an Event object with order information.
		
		if(event.getType() != "ORDER")
			return;
		OrderEvent orderEvent = (OrderEvent) event;
		FillEvent fillEvent = new FillEvent(new Date().getTime(), orderEvent.getSymbol(), "ARCA", orderEvent.getQuantity(), orderEvent.getDirection(), new BigDecimal("0"), null);
		messageBus.publishEvent(fillEvent);
	}

}
