package com.datastax.tickdata.query;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import cern.colt.list.DoubleArrayList;

import com.datastax.tickdata.TickDataDao;
import com.datastax.timeseries.model.CandleStick;
import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;
import com.datastax.timeseries.utils.CandleStickProcessor;
import com.datastax.timeseries.utils.FunctionProcessor;
import com.datastax.timeseries.utils.TechnicalAnalysis;

public class TestQueryProcessor {
	private DateTimeFormatter parser = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");

	@Test
	public void testCandleStick() {

		DoubleArrayList doubles = new DoubleArrayList();
		doubles.add(2);
		doubles.add(1);
		doubles.add(3);
		doubles.add(6);
		doubles.add(4);
		doubles.add(3);
		doubles.add(4);
		doubles.trimToSize();
		
		CandleStick candleStick = CandleStickProcessor.createCandleStickFromArrayList(doubles);
		System.out.println(candleStick);

		Assert.assertEquals(6, candleStick.getHigh(), .001);
		Assert.assertEquals(1, candleStick.getLow(), .001);
		Assert.assertEquals(2, candleStick.getOpen(), .001);
		Assert.assertEquals(4, candleStick.getClose(), .001);
	}

	@Test
	public void testMovingAverage() {
		TickDataDao dao = new TickDataDao(new String[] { "localhost" });

		TimeSeries tickData = dao.getTickData("NASDAQ-AAPL-2014-03-28", parser.parseDateTime("2014-03-28 10:15:02").getMillis(),
				parser.parseDateTime("2014-03-28 10:19:02").getMillis());

		// Need to reverse for Technical analysis
		tickData.reverse();

		TimeSeries byPeriod = TechnicalAnalysis.calculateMovingAverage(tickData, 40);

		System.out.println(byPeriod.toFormatterString());
	}
}
