package com.datastax.timeseries.model;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;

import com.google.common.primitives.Longs;

public class TimeSeries {

	private String symbol;
	private long[] dates;
	private double[] values;
	
	public TimeSeries(String symbol, long[] dates, double[] values) {
		super();
		this.symbol = symbol;
		this.dates = dates;
		this.values = values;
	}

	public String getSymbol() {
		return symbol;
	}

	public long[] getDates() {
		return dates;
	}

	public double[] getValues() {
		return values;
	}
	
	public void reverse(){
		ArrayUtils.reverse(dates);
		ArrayUtils.reverse(values);
	}

	public long highestDate(){
		if (getDates().length<1) return 0;
		return Longs.max(getDates());
	}

	public long lowestDate(){
		if (getDates().length<1) return 0;
		return Longs.min(getDates());
	}

	@Override
	public String toString() {
		return "TimeSeries [symbol=" + symbol + ", dates=" + Arrays.toString(dates) + ", values="
				+ Arrays.toString(values) + "]";
	}
	public String toFormatterString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("TimeSeries - symbol=" + symbol + "\n");
		
		for (int i=0; i < dates.length; i++){
			buffer.append(new DateTime(dates[i]).toString() + " - " + values[i] + "\n");
		}
			
		return buffer.toString(); 	
	}	
	
}
