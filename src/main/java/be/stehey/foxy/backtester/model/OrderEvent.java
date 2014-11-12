package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;

public class OrderEvent extends Event {
	// Handles the event of sending an Order to an execution system.
	// The order contains a symbol (e.g. "GOOG"), a type (market or limit),
	// a quantity and a direction.
	
	private String symbol; 
	private String orderType;
	
	public String getSymbol() {
		return symbol;
	}

	public String getOrderType() {
		return orderType;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getDirection() {
		return direction;
	}

	private BigDecimal quantity;
	private String direction;

	public OrderEvent() {
		super();
		this.type = "ORDER";
	};

	public OrderEvent(String symbol, String orderType, BigDecimal quantity,
			String direction) {
		super();
		this.type = "ORDER";
		this.symbol = symbol;
		this.orderType = orderType;
		this.quantity = quantity;
		this.direction = direction;
	}

	public void printOrder() {
		System.out.println("Order: Symbol=" + symbol + ", Type=" + type + ", Quantity=" + quantity.toString() + ", Direction=" + direction);
	}
}
