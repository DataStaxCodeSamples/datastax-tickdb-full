package com.datastax.tickdata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.tickdata.TickDataDao;
import com.datastax.timeseries.model.TimeSeries;

public class TimeSeriesReader implements Future<TimeSeries> {

	private static Logger logger = LoggerFactory.getLogger(TimeSeriesReader.class);
	private TimeSeries timeSeries;
	private boolean finished = false;
	private Thread t;

	public TimeSeriesReader(final TickDataDao dao, final String exchange, final String symbol, final DateTime startTime, final DateTime endTime) {
		
		t = new Thread(new Runnable(){
			@Override
			public void run() {
				logger.debug("Getting date from TickData store");
				timeSeries = dao.getTickData(exchange + "-" + symbol, startTime.getMillis(), endTime.getMillis());
				finished = true;
			}				
		});
		
		t.start();
	}
	
	TimeSeries getTimeSeries(){
		return this.timeSeries;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (mayInterruptIfRunning){
			t.interrupt();
			return true;
		}else{
			t.destroy();
			return true;
		}			
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		return finished;
	}

	@Override
	public TimeSeries get() throws InterruptedException, ExecutionException {			
		while(!isDone()){
			Thread.sleep(1);				
		}

		return timeSeries;
	}

	@Override
	public TimeSeries get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		return get();
	}
}