create keyspace if not exists datastax_tickdata_binary_demo WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': '2', 'DC2':2 };

use datastax_tickdata_binary_demo; 

CREATE TABLE if not exists tick_data ( 
	symbol text,
	dates blob,
	ticks blob,
	PRIMARY KEY (symbol)
); 
truncate tick_data;

create keyspace if not exists datastax_tickdata_demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor':3};

use datastax_tickdata_demo; 

CREATE TABLE if not exists tick_data ( 
	symbol text,
	date timestamp,
	value double,
	PRIMARY KEY (symbol, date)
) WITH CLUSTERING ORDER BY (date DESC);
	
truncate tick_data;