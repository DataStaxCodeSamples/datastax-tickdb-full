package com.datastax.timeseries.utils;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.timeseries.model.CandleStick;
import com.datastax.timeseries.model.CandleStickSeries;
import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;

import cern.colt.list.DoubleArrayList;

public class CandleStickProcessor {
	private static Logger logger = LoggerFactory.getLogger(CandleStickProcessor.class);

	public static CandleStickSeries createCandleStickSeries(TimeSeries timeSeries, Periodicity periodicity,
			DateTime startTime) {

		CandleStickSeries candleStickSeries = new CandleStickSeries(timeSeries.getSymbol());
		List<Long> candleStickTimePoints = FunctionProcessor.createDatesByPeriodicity(periodicity,
				startTime.getMillis(), timeSeries);

		long[] oldDates = timeSeries.getDates();
		double[] oldValues = timeSeries.getValues();

		int counter = 0;
		double lastValue = oldValues[0];

		DoubleArrayList doubles = new DoubleArrayList();

		for (int i = 0; i < oldDates.length; i++) {

			long date = oldDates[i];
			doubles.add(lastValue);

			if (date > candleStickTimePoints.get(counter)) {

				// Need to create a candlestick for what we have already
				CandleStick candleStick = createCandleStickFromArrayList(doubles);

				if (candleStick != null) {
					candleStick.setStartTime(candleStickTimePoints.get(counter).longValue());
					candleStickSeries.addCandleStick(candleStick);
					doubles.clear();
				}

				while (date >= candleStickTimePoints.get(counter)) {
					counter++;
				}
			}

			lastValue = oldValues[i];

			if (i == oldDates.length - 1) {
				doubles.add(lastValue);
			}

		}
		// Add last candle stick.
		CandleStick candleStick = createCandleStickFromArrayList(doubles);
		candleStick.setStartTime(candleStickTimePoints.get(counter).longValue());

		if (candleStick != null)
			candleStickSeries.addCandleStick(candleStick);

		return candleStickSeries;
	}

	public static CandleStick createCandleStickFromArrayList(DoubleArrayList doubles) {
		if (doubles == null)
			return null;

		doubles.trimToSize();

		if (doubles.size() == 0)
			return null;

		double open = doubles.get(0);
		double close = doubles.get(doubles.size() - 1);

		doubles.sort();

		double low = doubles.get(0);
		double high = doubles.get(doubles.size() - 1);

		return new CandleStick(high, low, open, close);
	}
}
