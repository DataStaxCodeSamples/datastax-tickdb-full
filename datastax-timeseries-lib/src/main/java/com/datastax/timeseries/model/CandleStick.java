package com.datastax.timeseries.model;

import org.joda.time.DateTime;

public class CandleStick {

	private double high;
	private double low;
	private double open;
	private double close;
	private long startTime;
	
	public CandleStick(double high, double low, double open, double close) {
		super();
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
	}
	public double getHigh() {
		return high;
	}
	public double getLow() {
		return low;
	}
	public double getOpen() {
		return open;
	}
	public double getClose() {
		return close;
	}
	
	public String getStartTime(){
		return new DateTime(startTime).toString();
	}
	
	public void setStartTime(long startTime) {
		this.startTime =startTime;		
	}

	@Override
	public String toString() {
		return "CandleStick for " + new DateTime(startTime).toString() + " [high=" + high + ", low=" + low + ", open=" + open + ", close=" + close + "]\n";
	}
}
