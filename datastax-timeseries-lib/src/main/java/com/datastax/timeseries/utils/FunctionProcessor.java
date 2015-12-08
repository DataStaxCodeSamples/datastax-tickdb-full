package com.datastax.timeseries.utils;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;
import com.google.common.primitives.Longs;

public class FunctionProcessor {
	private static Logger logger = LoggerFactory.getLogger(FunctionProcessor.class);

	public static TimeSeries getTimeSeriesByPeriod(TimeSeries timeSeries, Periodicity periodicity, DateTime startTime) {

		// Use last date as enddate.
		List<Long> newDates = createDatesByPeriodicity(periodicity, startTime.getMillis(), timeSeries);

		double[] doubles = new double[newDates.size()];

		long[] oldDates = timeSeries.getDates();
		double[] values = timeSeries.getValues();

		int counter = 0;
		double lastValue = 0;

		for (int i = 0; i < oldDates.length; i++) {

			long date = oldDates[i];

			if (date > newDates.get(counter)) {
				doubles[counter] = lastValue;

				while (date >= newDates.get(counter)) {
					doubles[counter] = lastValue;
					counter++;
				}
			}

			lastValue = values[i];

			if (i == oldDates.length - 1) {
				doubles[counter] = lastValue;
			}
		}

		return new TimeSeries(timeSeries.getSymbol(), Longs.toArray(newDates), doubles);
	}

	public static TimeSeries getSumTimeSeriesByPeriod(TimeSeries timeSeries, Periodicity periodicity, DateTime startTime) {

		// Use last date as enddate.
		List<Long> newDates = createDatesByPeriodicity(periodicity, startTime.getMillis(), timeSeries);

		double[] doubles = new double[newDates.size()];

		long[] oldDates = timeSeries.getDates();
		double[] values = timeSeries.getValues();

		int counter = 1;
		double sum = 0;

		for (int i = 0; i < oldDates.length; i++) {

			long date = oldDates[i];

			if (date > newDates.get(counter)) {
				
				doubles[counter] = sum;
				sum = 0;
				while (date >= newDates.get(counter)) {					
					counter ++;
				}				
			}
			
			sum = sum + values[i];
			
			if (i == oldDates.length - 1) {
				doubles[counter] = sum;
			}
		}

		return new TimeSeries(timeSeries.getSymbol(), Longs.toArray(newDates), doubles);
	}

	public static TimeSeries getAvgTimeSeriesByPeriod(TimeSeries timeSeries, Periodicity periodicity, DateTime startTime) {

		// Use last date as enddate.
		List<Long> newDates = createDatesByPeriodicity(periodicity, startTime.getMillis(), timeSeries);

		double[] doubles = new double[newDates.size()];

		long[] oldDates = timeSeries.getDates();
		double[] values = timeSeries.getValues();

		int counter = 1;
		int valueCounter = 0;
		double sum = 0;

		for (int i = 0; i < oldDates.length; i++) {

			long date = oldDates[i];

			if (date > newDates.get(counter)) {
				
				doubles[counter] = sum/valueCounter;
				sum = 0;
				valueCounter = 0;
				
				while (date >= newDates.get(counter)) {					
					counter ++;
				}				
			}
			
			sum = sum + values[i];
			valueCounter++;
			
			if (i == oldDates.length - 1) {
				doubles[counter] = sum/valueCounter;
			}
		}

		return new TimeSeries(timeSeries.getSymbol(), Longs.toArray(newDates), doubles);
	}
	
	public static List<Long> createDatesByPeriodicity(Periodicity periodicity, long startTime, TimeSeries timeSeries) {

		List<Long> newDates = new ArrayList<Long>();
		if (timeSeries.getDates().length == 0){
			return newDates;
		}
		long endTime = timeSeries.getDates()[timeSeries.getDates().length - 1];		

		// Only add if we have a valid value for the date.
		if (startTime > timeSeries.getDates()[0]) {
			newDates.add(startTime);
		}

		while (startTime < endTime) {
			startTime = startTime + periodicity.getDuration().getMillis();

			if (startTime > timeSeries.getDates()[0]) {
				newDates.add(startTime);
			}
		}

		return newDates;
	}

	public static void main(String[] args) {

	}
}
