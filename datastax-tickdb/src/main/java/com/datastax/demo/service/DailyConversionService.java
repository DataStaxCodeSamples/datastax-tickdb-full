package com.datastax.demo.service;

import java.util.List;

import org.joda.time.DateTime;

public class DailyConversionService {
	
	private DataLoader dataLoader;
	private TickDBService dbService;
	
	public DailyConversionService() {
		this.dataLoader = new DataLoader();
		this.dbService = TickDBService.getInstance();
	}

	/**
	 * Convert all exchange symbols to binary for today. 
	 */
	public void runTickDataToBinaryConversion(DateTime dateTime){
		
		List<String> exchangeSymbols = dataLoader.getExchangeData();
		
		for (String exchangeSymbol : exchangeSymbols){
			String[] split = exchangeSymbol.split("-");			
			
			dbService.convertTickDataToTimeSeries(split[0], split[1], dateTime);			
		}		
	}

	public void runTickDataToBinaryConversionForSymbol(String exchange, String symbol, DateTime dateTime){
					
		dbService.convertTickDataToTimeSeries(exchange, symbol, dateTime);					
	}

	
	public static void main(String args[]){
		DailyConversionService service = new DailyConversionService();

		service.runTickDataToBinaryConversion(DateTime.now());
		
		System.exit(0);
	}
	
}
