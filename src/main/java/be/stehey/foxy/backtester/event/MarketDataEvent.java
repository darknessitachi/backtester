package be.stehey.foxy.backtester.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MarketDataEvent implements Serializable {
	private String time;
	private long timestamp;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal close;
	private BigDecimal volume;
	
	public MarketDataEvent() {
		super();
	}
	
	public MarketDataEvent(String time, BigDecimal open, BigDecimal high,
			BigDecimal low, BigDecimal close, BigDecimal volume) {
		super();
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
	
	public long getTimestamp() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss.SSS");
		return sdf.parse(time).getTime();
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

}
