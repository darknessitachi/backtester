package be.stehey.foxy.backtester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import be.stehey.foxy.backtester.model.Event;
import be.stehey.foxy.backtester.model.FillEvent;
import be.stehey.foxy.backtester.model.OrderEvent;

public class SimulatedExecutionHandlerTest {
	private static final String SYMBOL = "GOOG";
	private static final BigDecimal QUANTITY = new BigDecimal("242");
	private static final String DIRECTION = "LONG";

	@Test
	public void testGeneratesFillEvent_WhenReceivedOrderEvent() throws Exception {
		MessageBus messageBus = new SimulatedMessageBus();
		ExecutionHandler executionHandler = new SimulatedExecutionHandler(messageBus);
		OrderEvent orderEvent = new OrderEvent(SYMBOL, "MKT", QUANTITY, DIRECTION);
		executionHandler.executeOrder(orderEvent);
		Event responseEvent = messageBus.getEvent();
		assertEquals("FILL", responseEvent.getType());
		assertTrue(QUANTITY.compareTo(((FillEvent)responseEvent).getQuantity()) == 0);
		assertEquals(DIRECTION, ((FillEvent)responseEvent).getDirection());
	}
}
