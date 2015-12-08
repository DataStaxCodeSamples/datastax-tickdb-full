package com.datastax.timeseries.utils;

import org.apache.commons.lang.ArrayUtils;

import com.datastax.timeseries.model.TimeSeries;

public class TimeSeriesUtils {

	static public TimeSeries mergeTimeSeries(TimeSeries timeSeries1, TimeSeries timeSeries2) {
		
		if (timeSeries1 == null && timeSeries2 == null){
			return null;
		}
		if (timeSeries1 == null){
			return timeSeries2;
		}
		if (timeSeries2 == null){
			return timeSeries1;
		}

		if (timeSeries1.highestDate() > timeSeries2.highestDate()
				&& timeSeries1.lowestDate() > timeSeries2.highestDate()){
			
			long[] dates = ArrayUtils.addAll(timeSeries1.getDates(), timeSeries2.getDates());
			double[] values= ArrayUtils.addAll(timeSeries1.getValues(), timeSeries2.getValues());
			return new TimeSeries(timeSeries1.getSymbol(), dates, values);
			
		}else if (timeSeries2.highestDate() > timeSeries1.highestDate()
				&& timeSeries2.lowestDate() > timeSeries1.highestDate()){

			long[] dates = ArrayUtils.addAll(timeSeries2.getDates(), timeSeries1.getDates());
			double[] values= ArrayUtils.addAll(timeSeries2.getValues(), timeSeries1.getValues());
			return new TimeSeries(timeSeries1.getSymbol(), dates, values);
		}else{
			//TODO : Do some fancy sorting
			return timeSeries1;
		}		
	}

	static public Long[] concat(long[] a, long[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Long[] c = new Long[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
	
	static public Double[] concat(double[] a, double[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Double[] c = new Double[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

}
