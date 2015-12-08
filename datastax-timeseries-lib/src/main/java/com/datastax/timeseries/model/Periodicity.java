package com.datastax.timeseries.model;

import org.joda.time.Duration;

public enum Periodicity {	
	
	MINUTE (new Duration(60*1000)),
	MINUTE_5 (new Duration(5*60*1000)),
	MINUTE_15 (new Duration(15*60*1000)),
	MINUTE_30 (new Duration(30*60*1000)),
	HOUR (new Duration(60*60*1000));
	
	Duration duration;
	
	Periodicity(Duration duration){
		this.duration = duration;
	}
	
	public Duration getDuration(){
		return this.duration;
	}
}
