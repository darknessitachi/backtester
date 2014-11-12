package be.stehey.foxy.backtester.event;

import java.io.Serializable;
import java.math.BigDecimal;

public class EMA5Event implements Serializable {
	private BigDecimal value;

	public EMA5Event() {
		super();
	}

	public EMA5Event(BigDecimal value) {
		super();
		this.value = value;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	
}
