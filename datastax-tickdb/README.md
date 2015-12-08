Tick Data example
========================================================

This is a simple example of using C* as a tick data store for financial market data.

## Running the demo 

You will need a java runtime (preferably 7) along with maven 3 to run this demo. Start DSE 3.1.X or a cassandra 1.2.X instance on your local machine. This demo just runs as a standalone process on the localhost.

This demo uses quite a lot of memory so it is worth setting the MAVEN_OPTS to run maven with more memory

    export MAVEN_OPTS=-Xmx512M

## Queries

The queries that we want to be able to run is 
	
1. Get all the tick data for a symbol in an exchange (in a time range)

     select * from tick_data where symbol ='NASDAQ-NFLX-2014-01-31';
     
     select * from tick_data where symbol ='NASDAQ-NFLX-2014-01-31' and date > '2014-01-01 14:45:00' and date < '2014-01-01 15:00:00';

## Data 

The data is generated from a tick generator which uses a csv file to create random values from AMEX, NYSE and NASDAQ.

## Throughput 

To increase the throughput, add nodes to the cluster. Cassandra will scale linearly with the amount of nodes in the cluster.

## Schema Setup
Use the project https://github.com/PatrickCallaghan/datastax-tickdata-comparison to load the data in Cassandra.

Start the server by running 

mvn package 

and

mvn jetty:run

###Querying.

//Todays data
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/NASDAQ/AAPL

//To and From dates
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/bydatetime/NASDAQ/AAPL/20150914000000/20150917000000

//To and from dates broken into minute chunks 
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/bydatetime/NASDAQ/AAPL/20150914000000/20150917000000/MINUTE

//To and from dates broken into minute chunks and shown as candlesticks 
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/candlesticks/NASDAQ/AAPL/20150914000000/20150917000000/MINUTE_5

###Services

//For all exchanges and symbols, run daily conversion of tick data to binary data for long term storage and retrieval 
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/rundailyconversion

//For a specific symbol and todays date, run daily conversion of tick data to binary data for long term storage and retrieval
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/rundailyconversionbysymbol/NASDAQ/AAPL

//For a specific symbol and date, run daily conversion of tick data to binary data for long term storage and retrieval
http://52.27.154.78:7001/datastax-tickdb/rest/tickdb/get/rundailyconversionbysymbolanddate/NASDAQ/AAPL/20150917000000


###Notes
Dates are in format - yyyyMMddHHmmss

Periodicity's are 
MINUTE
MINUTE_5
MINUTE_15
MINUTE_30
HOUR





