package com.datastax.tickdata.query;

import org.joda.time.DateTime;
import org.junit.Test;

import com.datastax.timeseries.model.CandleStickSeries;
import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;
import com.datastax.timeseries.utils.CandleStickProcessor;

public class TestCandleStickProcessor {

	@Test
	public void testCandleStick() {
		long[] dates = new long[] { new DateTime(2014, 3, 26, 0, 2).getMillis(),
				new DateTime(2014, 3, 27, 1, 23).getMillis(), new DateTime(2014, 3, 27, 1, 24).getMillis(),
				new DateTime(2014, 3, 27, 2, 04).getMillis(), new DateTime(2014, 3, 27, 2, 12).getMillis(),
				new DateTime(2014, 3, 27, 2, 23).getMillis(), new DateTime(2014, 3, 27, 2, 24).getMillis(),
				new DateTime(2014, 3, 27, 2, 59).getMillis(), new DateTime(2014, 3, 27, 3, 01).getMillis(),
				new DateTime(2014, 3, 27, 3, 03).getMillis(), new DateTime(2014, 3, 27, 3, 15).getMillis(),
				new DateTime(2014, 3, 27, 3, 26).getMillis(), new DateTime(2014, 3, 27, 3, 34).getMillis(),
				new DateTime(2014, 3, 27, 3, 39).getMillis(), new DateTime(2014, 3, 27, 4, 12).getMillis(),
				new DateTime(2014, 3, 27, 4, 23).getMillis(), new DateTime(2014, 3, 27, 4, 25).getMillis(),
				new DateTime(2014, 3, 27, 4, 26).getMillis(), new DateTime(2014, 3, 27, 4, 39).getMillis(),
				new DateTime(2014, 3, 27, 4, 49).getMillis(), new DateTime(2014, 3, 27, 4, 52).getMillis(),
				new DateTime(2014, 3, 27, 4, 59).getMillis(), new DateTime(2014, 3, 27, 5, 3).getMillis(),
				new DateTime(2014, 3, 27, 5, 04).getMillis(), new DateTime(2014, 3, 27, 5, 34).getMillis(),
				new DateTime(2014, 3, 27, 5, 42).getMillis(), new DateTime(2014, 3, 27, 5, 43).getMillis(),
				new DateTime(2014, 3, 27, 5, 44).getMillis(), new DateTime(2014, 3, 27, 5, 54).getMillis(),
				new DateTime(2014, 3, 27, 5, 55).getMillis(), new DateTime(2014, 3, 27, 5, 57).getMillis(),
				new DateTime(2014, 3, 27, 5, 59).getMillis(), new DateTime(2014, 3, 27, 6, 01).getMillis(),
				new DateTime(2014, 3, 27, 6, 04).getMillis(), new DateTime(2014, 3, 27, 6, 10).getMillis(),
				new DateTime(2014, 3, 27, 6, 13).getMillis(), new DateTime(2014, 3, 27, 6, 16).getMillis(),
				new DateTime(2014, 3, 27, 6, 20).getMillis(), new DateTime(2014, 3, 27, 6, 26).getMillis(),
				new DateTime(2014, 3, 27, 6, 29).getMillis(), new DateTime(2014, 3, 27, 6, 38).getMillis(),
				new DateTime(2014, 3, 27, 6, 41).getMillis(), new DateTime(2014, 3, 27, 6, 42).getMillis(),
				new DateTime(2014, 3, 27, 6, 46).getMillis(), new DateTime(2014, 3, 27, 6, 49).getMillis() };

		double[] values = { 5, 6, 4, 3, 4, 5, 6, 7, 5, 5, 6, 7, 8, 5, 5, 6, 7, 8, 9, 7, 6, 5, 6, 4, 3, 5, 6, 7, 5, 4,
				6, 3, 4, 5, 7, 5, 4, 6, 7, 5, 5, 4, 6, 7, 8 };

		TimeSeries timeSeries = new TimeSeries("test", dates, values);

		CandleStickSeries candleStickSeries = CandleStickProcessor.createCandleStickSeries(timeSeries, Periodicity.HOUR, new DateTime(2014, 3, 27, 0, 0));

		System.out.println(candleStickSeries);
	}
}
