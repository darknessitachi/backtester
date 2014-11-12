package be.stehey.foxy.backtester.model;

import java.math.BigDecimal;
import java.util.Date;

public class OHLCBar implements Comparable<Object> {

	private String symbol;
	private Date timestamp;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	private BigDecimal openPrice;
	private BigDecimal highPrice;
	private BigDecimal lowPrice;
	private BigDecimal closePrice;
	
	public BigDecimal getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}
	public BigDecimal getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}
	public BigDecimal getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}
	public BigDecimal getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}
	@Override
	public int compareTo(Object anotherOHLCBar) {
		 if (!(anotherOHLCBar instanceof OHLCBar))
		      throw new ClassCastException("An OHLCBar object expected.");
		    Date anotherOHLCBarTimestamp = ((OHLCBar) anotherOHLCBar).getTimestamp();  
		    return this.getTimestamp().compareTo(anotherOHLCBarTimestamp);  	
	    }
}
