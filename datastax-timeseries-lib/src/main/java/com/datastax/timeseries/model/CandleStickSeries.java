package com.datastax.timeseries.model;

import java.util.ArrayList;
import java.util.List;

public class CandleStickSeries {
	
	private String name;
	private List<CandleStick> candleSticks;
	
	public CandleStickSeries(String name) {
		super();
		this.name = name;
		this.candleSticks = new ArrayList<CandleStick>();
	}
	
	public CandleStickSeries(String name, List<CandleStick> candleSticks) {
		super();
		this.name = name;
		this.candleSticks = candleSticks;		
	}

	public String getName() {
		return name;
	}

	public List<CandleStick> getCandleSticks() {
		return candleSticks;
	}

	public void setCandleSticks(List<CandleStick> candleSticks) {
		this.candleSticks = candleSticks;
	}
	
	public void addCandleStick(CandleStick candleStick) {
		this.candleSticks.add(candleStick);
	}

	@Override
	public String toString() {
		return "CandleStickSeries [name=" + name + ", candleSticks=" + candleSticks.toString() + "]";
	}	
}
