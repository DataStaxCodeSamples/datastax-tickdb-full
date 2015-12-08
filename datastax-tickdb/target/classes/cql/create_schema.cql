create keyspace if not exists datastax_tickdb WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': '2', 'DC2':2 };

use datastax_tickdb; 

CREATE TABLE if not exists tick_data ( 
	symbol text,
	date timestamp,
	value double,
	PRIMARY KEY (symbol, date)
) WITH CLUSTERING ORDER BY (date DESC);


truncate tick_data;
	