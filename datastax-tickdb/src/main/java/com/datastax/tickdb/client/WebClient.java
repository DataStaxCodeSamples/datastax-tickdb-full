package com.datastax.tickdb.client;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.datastax.demo.service.DailyConversionService;
import com.datastax.demo.service.TickDBService;
import com.datastax.timeseries.model.CandleStickSeries;
import com.datastax.timeseries.model.Periodicity;
import com.datastax.timeseries.model.TimeSeries;
import com.datastax.timeseries.utils.PeriodicityProcessor;

@Path("/tickdb/")
public class WebClient {

	private TickDBService tickDBService = TickDBService.getInstance();
	private DailyConversionService dailyConversionService = new DailyConversionService();
	
	private DateTimeFormatter parser = DateTimeFormat.forPattern("yyyyMMddHHmmss");
	
	@GET
	@Path("/get/bydatetime/{exchange}/{symbol}/{fromdate}/{todate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeSeries(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol,
			@PathParam("fromdate") String fromDateString, @PathParam("todate") String toDateString){
			
		DateTime fromDate = parser.parseDateTime(fromDateString);
		DateTime toDate = parser.parseDateTime(toDateString);
					
		return Response.status(201).entity(tickDBService.getTimeSeries(exchange, symbol, fromDate, toDate)).build();
	}

	@GET
	@Path("/get/{exchange}/{symbol}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeSeries(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol){
								
		TimeSeries result = tickDBService.getTimeSeries(exchange, symbol, new DateTime().withMillisOfDay(0), DateTime.now());
		return Response.status(201).entity(result).build();
	}
	
	@GET
	@Path("/get/bydatetime/{exchange}/{symbol}/{fromdate}/{todate}/{periodicity}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeSeriesPeriodicity(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol,
			@PathParam("fromdate") String fromDateString, @PathParam("todate") String toDateString,
			@PathParam("periodicity") String periodicityString){
			
		DateTime fromDate = parser.parseDateTime(fromDateString);
		DateTime toDate = parser.parseDateTime(toDateString);
					
		TimeSeries timeSeries = tickDBService.getTimeSeries(exchange, symbol, fromDate, toDate);
		
		timeSeries.reverse();
		
		if (periodicityString != null){
			timeSeries = PeriodicityProcessor.getTimeSeriesByPeriod(timeSeries, Periodicity.valueOf(periodicityString), fromDate);
		}
		
		return Response.status(201).entity(timeSeries).build();
	}

	@GET
	@Path("/get/candlesticks/{exchange}/{symbol}/{fromdate}/{todate}/{periodicity}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCandleStickSeries(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol,
			@PathParam("fromdate") String fromDateString, @PathParam("todate") String toDateString,
			@PathParam("periodicity") String periodicityString){

		DateTime fromDate = parser.parseDateTime(fromDateString);
		DateTime toDate = parser.parseDateTime(toDateString);
			
		Periodicity periodicity = Periodicity.valueOf(periodicityString);
		
		CandleStickSeries candleStickSeries = tickDBService.getCandleStickSeries(exchange, symbol, fromDate, toDate, periodicity);
		
		return Response.status(201).entity(candleStickSeries).build();
	}
	
	@GET
	@Path("/get/rundailyconversion")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runDailyConversion(){
		
		dailyConversionService.runTickDataToBinaryConversion(new DateTime());
		return Response.status(201).entity(new Boolean(true)).build();
	}
	@GET
	@Path("/get/rundailyconversionbysymbol/{exchange}/{symbol}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runTickDataToBinaryConversion(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol){
		return this.runTickDataToBinaryConversionWithDate(exchange, symbol, null);
	}
	
	@GET
	@Path("/get/rundailyconversionbysymbolanddate/{exchange}/{symbol}/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runTickDataToBinaryConversionWithDate(@PathParam("exchange") String exchange, @PathParam("symbol") String symbol,
			@PathParam("date") String dateString){
		
		DateTime date;
		
		if (dateString ==null || dateString.equals("")){
			date = DateTime.now();
		}else{
			date = parser.parseDateTime(dateString);
		}
		
		dailyConversionService.runTickDataToBinaryConversionForSymbol(exchange, symbol, date);
		return Response.status(201).entity(new Boolean(true)).build();		
	}

}
