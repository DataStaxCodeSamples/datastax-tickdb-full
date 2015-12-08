package com.datastax.tickdata.engine;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.LongArrayList;

import com.datastax.timeseries.model.TimeSeries;

public class TickGenerator implements Iterator<TimeSeries> {

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private static Logger logger = LoggerFactory.getLogger(TickGenerator.class);
	private int counter = 0;
	private List<String> exchangeSymbols;
	private AtomicLong tickCounter = new AtomicLong(0);
	private DateTime startDateTime;

	public TickGenerator(List<String> exchangeSymbols, DateTime startTime) {
		this.exchangeSymbols = exchangeSymbols;
		this.startDateTime = startTime;
	}

	@Override
	public boolean hasNext() {
		if (counter < this.exchangeSymbols.size()){
			return true;
		}else{
			logger.info("Comparing : " + this.startDateTime + " - " + new DateTime());
			if (this.startDateTime.getMillis() < new DateTime().getMillis()){
				
				startDateTime = startDateTime.plusDays(1);
				counter = 0;
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}else{			
				return false;
			}
		}
	}

	@Override
	public TimeSeries next() {
		String exchangeSymbol = exchangeSymbols.get(counter);

		DateTime today = startDateTime.withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(0);
		DateTime endTime = startDateTime.withHourOfDay(16).withMinuteOfHour(30).withSecondOfMinute(0);

		LongArrayList dates = new LongArrayList();
		DoubleArrayList prices = new DoubleArrayList();
		double startPrice = exchangeSymbol.hashCode() % 1000;

		while (today.isBefore(endTime.getMillis())) {

			dates.add(today.getMillis());
			prices.add(startPrice);

			startPrice = this.createRandomValue(startPrice);

			today = today.plusMillis(new Double(Math.random() * 500).intValue() + 1);
		}
		counter++;

		dates.trimToSize();
		prices.trimToSize();

		return new TimeSeries(exchangeSymbol + "-" + formatter.format(today.toDate()), dates.elements(),
				prices.elements());
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

	private double createRandomValue(double lastValue) {

		double up = Math.random() * 2;
		double percentMove = (Math.random() * 1.0) / 100;

		if (up < 1) {
			lastValue -= percentMove;
		} else {
			lastValue += percentMove;
		}

		tickCounter.incrementAndGet();

		return lastValue;
	}

	public long getCount() {
		return this.tickCounter.get();
	}
}
