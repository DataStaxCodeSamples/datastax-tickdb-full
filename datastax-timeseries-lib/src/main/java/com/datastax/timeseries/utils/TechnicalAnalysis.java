package com.datastax.timeseries.utils;

import com.datastax.timeseries.model.TimeSeries;

public class TechnicalAnalysis {

	public static TimeSeries calculateMovingAverage(TimeSeries timeSeries, int movingAveragePeriod){
		
		//NOTE TimeSeries must be starting from the least recent to most recent
		MovingAverage movingAverage = new MovingAverage(movingAveragePeriod);
		
		double[] values = timeSeries.getValues();
		double[] newValues = new double[values.length];
		
		for (int i=0; i < values.length; i++){
			movingAverage.newNum(values[i]);
			
			newValues[i] = movingAverage.getAvg();
		}
		
		return new TimeSeries(timeSeries.getSymbol(), timeSeries.getDates(), newValues);
	}
}
