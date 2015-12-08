package com.datastax.tickdata;

import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;
import com.datastax.timeseries.model.TimeSeries;

public class SymbolReader {
	private static Logger logger = LoggerFactory.getLogger(SymbolReader.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private TimeSeries timeSeries;

	public SymbolReader() {

		String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");

		DataLoader dataLoader = new DataLoader();
		List<String> exchangeSymbols = dataLoader.getExchangeData();

		logger.info("No of symbols : " + exchangeSymbols.size());

		TickDataBinaryDao binaryDao = new TickDataBinaryDao(contactPointsStr.split(","));

		DateTime startDateTime = DateTime.now();
		
		while (true) {			
			Timer timer = new Timer();
			String exchangeSymbol = exchangeSymbols.get(new Double(Math.random() * exchangeSymbols.size()).intValue());
			String symbol = exchangeSymbol + "-" + formatter.format(startDateTime.toDate());

			timeSeries = binaryDao.getTimeSeries(symbol);

			timer.end();
			logger.info("Data read " + timer.getTimeTakenMillis()+ "ms -"+ symbol + ". Total Points "
					+ timeSeries.getDates().length);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SymbolReader();
	}
}
