package com.datastax.timeseries.model;

import java.util.Arrays;

public class DataPoints extends TimeSeries{

	private String[] points;
	
	public DataPoints(String name, long[] times, double[] values, String[] points) {
		super(name, times, values);
		this.points = points;
	}

	public String[] getPoints() {
		return points;
	}

	@Override
	public String toString() {
		return super.toString() + " DataPoints [points=" + Arrays.toString(points) + "]";
	}
}
