package com.datastax.timeseries.utils;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;
import com.google.common.primitives.Longs;

public class PeriodicityProcessor {
	private static Logger logger = LoggerFactory.getLogger(FunctionProcessor.class);
	
	public static TimeSeries getTimeSeriesByPeriod (TimeSeries timeSeries, Periodicity periodicity, DateTime startTime){
		
		List<Long> newDates = createDatesByPeriodicity(periodicity, startTime.getMillis(), timeSeries); //Use last date as enddate.
				
		double[] doubles = new double[newDates.size()];
		
		long[] oldDates = timeSeries.getDates();
		double[] values = timeSeries.getValues();
		
		int counter = 0;
		double lastValue = 0;
		
		for (int i=0; i < oldDates.length; i++){
			
			long date = oldDates[i];
			
			if (date > newDates.get(counter)){
				doubles[counter] = lastValue;

				while (date >= newDates.get(counter)){
					doubles[counter] = lastValue;
					counter ++;
				}
			}
			
			lastValue = values[i];
			
			if (i == oldDates.length-1){
				doubles[counter] = lastValue;
			}			
		}
		
		return new TimeSeries(timeSeries.getSymbol(), Longs.toArray(newDates), doubles);		
	}

	public static List<Long> createDatesByPeriodicity(Periodicity periodicity, long startTime, TimeSeries timeSeries) {
		
		long endTime = timeSeries.getDates()[timeSeries.getDates().length-1];
		
		List<Long> newDates = new ArrayList<Long>();
		
		//Only add if we have a valid value for the date.
		if (startTime > timeSeries.getDates()[0]){
			newDates.add(startTime);
		}

		while (startTime < endTime){
			startTime = startTime + periodicity.getDuration().getMillis();			

			if (startTime > timeSeries.getDates()[0]){
				newDates.add(startTime);
			}
		}
		
		return newDates;
	}
	
	
	public static void main(String[] args){
		
	}
}
