package com.datastax.timeseries.utils;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class DateUtils {


	static public List<DateTime> getDatesBetween(DateTime dateStart, DateTime dateEnd){
		
		if (dateStart.isAfter(dateEnd)){
			return getDatesBetween(dateEnd, dateStart);
		}
		List<DateTime> dates = new ArrayList<DateTime>();
		
		// day by day:
		while(dateStart.isBefore(dateEnd)){		    
			dates.add(dateStart);
		    dateStart = dateStart.plusDays(1);
		}
		
		dates.add(dateEnd);
		return dates;		
	}
	
	public static void main(String args[]){
		
		DateTime time = new DateTime();		
		System.out.println(DateUtils.getDatesBetween(time.minusDays(10), time));			
	}
}