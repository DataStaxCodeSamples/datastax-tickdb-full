package com.datastax.tickdata;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.LongBuffer;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.LongArrayList;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.timeseries.model.TimeSeries;

public class TickDataBinaryDao {
	
	private static Logger logger = LoggerFactory.getLogger(TickDataBinaryDao.class);

	private Session session;
	private static String keyspaceName = "datastax_tickdata_binary_demo";
	private static String tableNameTick = keyspaceName + ".tick_data";

	private static final String INSERT_INTO_TICK = "Insert into " + tableNameTick + " (symbol,dates,ticks) values (?, ?,?);";
	private static final String SELECT_FROM_TICK = "Select symbol, dates, ticks from " + tableNameTick + " where symbol = ?";

	private PreparedStatement insertStmtTick;
	private PreparedStatement selectStmtTick;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.zzz"); 

	public TickDataBinaryDao(String[] contactPoints) {

		String remoteDC = PropertyHelper.getProperty("remoteDC", "");
		int replicasRemoteDC = Integer.parseInt(PropertyHelper.getProperty("replicasRemoteDC", "2"));
		
		Cluster cluster = Cluster.builder()
				.withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy(remoteDC, replicasRemoteDC)))
				.withRetryPolicy(new LoggingRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE))
				.addContactPoints(contactPoints).build();
		
		this.session = cluster.connect();

		this.insertStmtTick = session.prepare(INSERT_INTO_TICK);		
		this.insertStmtTick.setConsistencyLevel(ConsistencyLevel.ONE);
		this.selectStmtTick = session.prepare(SELECT_FROM_TICK);		
		this.selectStmtTick.setConsistencyLevel(ConsistencyLevel.QUORUM);
	}
	
	public TimeSeries getTimeSeries(String symbol){
		
		BoundStatement boundStmt = new BoundStatement(this.selectStmtTick);
		boundStmt.setString(0, symbol);
		
		ResultSet resultSet = session.execute(boundStmt);		
		
		DoubleArrayList valueArray = new DoubleArrayList(10000);
		LongArrayList dateArray = new LongArrayList(10000);

		if (resultSet.isExhausted()){
			logger.info("No results found for symbol : " + symbol);
			dateArray.trimToSize();
			valueArray.trimToSize();
			
			return new TimeSeries(symbol, dateArray.elements(), valueArray.elements());
		}
		
		Row row = resultSet.one();
		
		LongBuffer dates = row.getBytes("dates").asLongBuffer();		
		DoubleBuffer ticks = row.getBytes("ticks").asDoubleBuffer();
		
		while (dates.hasRemaining()){
			dateArray.add(dates.get());
			valueArray.add(ticks.get());	
		}
		
		dateArray.trimToSize();
		valueArray.trimToSize();
		
		return new TimeSeries(symbol, dateArray.elements(), valueArray.elements());
	}

	public void insertTimeSeries(TimeSeries timeSeries) throws Exception{
		logger.info("Writing Binary : " + timeSeries.getSymbol() + " - " + timeSeries.getDates().length);
		
		BoundStatement boundStmt = new BoundStatement(this.insertStmtTick);
		
		ByteBuffer datesBuffer = ByteBuffer.allocate(4_000_000*8);
		ByteBuffer pricesBuffer = ByteBuffer.allocate(4_000_000*8);
		
		long[] dates = timeSeries.getDates();
		double[] values = timeSeries.getValues();
		
		for (int i=0; i <dates.length; i++) {
			
			datesBuffer.putLong(dates[i]);
			pricesBuffer.putDouble(values[i]);
		}
				
		session.execute(boundStmt.bind(timeSeries.getSymbol(), datesBuffer.flip(), pricesBuffer.flip()));		
		
		datesBuffer.clear();
		pricesBuffer.clear();
		
		return;
	}

	private String fillNumber(int num) {
		return num < 10 ? "0" + num : "" + num;
	}
}
