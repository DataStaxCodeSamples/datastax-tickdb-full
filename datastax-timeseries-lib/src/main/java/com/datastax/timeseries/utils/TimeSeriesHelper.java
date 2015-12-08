package com.datastax.timeseries.utils;

import java.util.Arrays;

public class TimeSeriesHelper {

	private String symbol;
	private long[] dates;
	private double[] values;
	
	public TimeSeriesHelper(String symbol, long[] dates, double[] values) {
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

	@Override
	public String toString() {
		return "TimeSeries [symbol=" + symbol + ", dates=" + Arrays.toString(dates) + ", values="
				+ Arrays.toString(values) + "]";
	}	
}
