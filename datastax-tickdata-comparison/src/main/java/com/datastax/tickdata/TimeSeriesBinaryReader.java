package com.datastax.tickdata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.tickdata.TickDataBinaryDao;
import com.datastax.timeseries.model.TimeSeries;

public class TimeSeriesBinaryReader implements Future<TimeSeries> {

	private static Logger logger = LoggerFactory.getLogger(TimeSeriesBinaryReader.class);
	private TimeSeries timeSeries;
	private boolean finished = false;
	private Thread t;

	public TimeSeriesBinaryReader(final TickDataBinaryDao binaryDao, final String key) {
		
		t = new Thread(new Runnable(){
			@Override
			public void run() {
				logger.debug("Getting date from Binary store");
				timeSeries = binaryDao.getTimeSeries(key);
				finished = true;
			}				
		});
		
		t.start();
	}
	
	TimeSeries getTimeSeries(){
		this.timeSeries.reverse();
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

		return getTimeSeries();
	}

	@Override
	public TimeSeries get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		return get();
	}
}