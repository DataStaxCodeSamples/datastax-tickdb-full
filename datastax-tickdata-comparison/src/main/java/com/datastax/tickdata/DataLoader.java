package com.datastax.tickdata;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class DataLoader {

	private static Logger logger = LoggerFactory.getLogger(DataLoader.class);
	
	private static final CharSequence EXCHANGEDATA = "small_exchangedata";

	public DataLoader(){}

	public List<String> getExchangeData() {

		List<String> allExchangeSymbols = new ArrayList<String>();

		// Process all the files from the csv directory
		File cvsDir = new File(".", "src/main/resources/csv");

		File[] files = cvsDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});

		for (File file : files) {
			try {
				if (file.getName().contains(EXCHANGEDATA)){
					allExchangeSymbols.addAll(this.getExchangeData(file));
				}

			} catch (FileNotFoundException e) {
				logger.warn("Could not process file : " + file.getAbsolutePath(), e);
			} catch (IOException e) {
				logger.warn("Could not process file : " + file.getAbsolutePath(), e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return allExchangeSymbols;
	}

	private List<String> getExchangeData(File file) throws IOException, InterruptedException {

		CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()), CSVReader.DEFAULT_SEPARATOR,
				CSVReader.DEFAULT_QUOTE_CHARACTER, 1);
		String[] items = null;
		List<String> exchangeItems = new ArrayList<String>();

		while ((items = reader.readNext()) != null) {
			String exchange = items[0].trim();
			String symbol = items[1].trim();

			exchangeItems.add(exchange + "-" + symbol);
		}

		reader.close();
		return exchangeItems;
	}
}
