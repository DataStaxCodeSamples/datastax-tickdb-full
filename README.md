Tick Data example
========================================================

This is a simple example of using C* as a tick data store for financial market data.

## Running the demo

You will need a java runtime (preferably 7) along with maven 3 to run this demo. Start DSE 4.5.X or a cassandra 2.X instance on your local machine. This demo just runs as a standalone process on the localhost.

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
Note : This will drop the keyspace "datastax_tickdata_binary_demo" and create a new one. All existing data will be lost.

The schema can be found in src/main/resources/cql/

To specify contact points use the contactPoints command line parameter e.g. '-DcontactPoints=192.168.25.100,192.168.25.101'
The contact points can take mulitple points in the IP,IP,IP (no spaces).

To create the a single node cluster with replication factor of 1 for standard localhost setup, run the following

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup"

To run the insert of historic data, run (change noOfDays to the number of historic days you require.)

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.tickdata.Main" (-DcontactPoints=<node0> -DnoOfDays=30)

This will create 2 threads, 1 for binary inserts and 1 for tickdata inserts.

To read a ticker

	  mvn clean compile exec:java -Dexec.mainClass="com.datastax.tickdata.Read" (-Dsymbol=NASDAQ-AAPL-2015-09-16)


Start the server by running

  ./run_server.sh

###Querying.

//Todays data
http://localhost:7001/datastax-tickdb/rest/tickdb/get/NASDAQ/AAPL

//To and From dates
http://localhost:7001/datastax-tickdb/rest/tickdb/get/bydatetime/NASDAQ/AAPL/20150914000000/20150917000000

//To and from dates broken into minute chunks
http://localhost:7001/datastax-tickdb/rest/tickdb/get/bydatetime/NASDAQ/AAPL/20150914000000/20150917000000/MINUTE

//To and from dates broken into minute chunks and shown as candlesticks
http://localhost:7001/datastax-tickdb/rest/tickdb/get/candlesticks/NASDAQ/AAPL/20150914000000/20150917000000/MINUTE_5

###Services

For all exchanges and symbols, run daily conversion of tick data to binary data for long term storage and retrieval

  http://localhost:7001/datastax-tickdb/rest/tickdb/get/rundailyconversion

For a specific symbol and todays date, run daily conversion of tick data to binary data for long term storage and retrieval

  http://localhost:7001/datastax-tickdb/rest/tickdb/get/rundailyconversionbysymbol/NASDAQ/AAPL

For a specific symbol and date, run daily conversion of tick data to binary data for long term storage and retrieval

  http://localhost:7001/datastax-tickdb/rest/tickdb/get/rundailyconversionbysymbolanddate/NASDAQ/AAPL/20150917000000


###Notes
Dates are in format - yyyyMMddHHmmss

Periodicity's are
MINUTE
MINUTE_5
MINUTE_15
MINUTE_30
HOUR


To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
