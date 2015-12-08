package com.datastax.tickdata.model;

import org.joda.time.DateTime;

public class TickData {
	private String key;
	private double value;
	private DateTime time;
	
	public TickData(String key, double value){
		this.key = key;
		this.value = value;
		this.time = null;
	}

	public TickData(String key, double value, DateTime time){
		this.key = key;
		this.value = value;
		this.time = time;
	}

	public String getKey() {
		return key;
	}

	public double getValue() {
		return value;
	}
	
	public DateTime getTime(){
		return this.time;
	}

	@Override
	public String toString() {
		return "TickData [key=" + key + ", value=" + value + "]";
	}
}
