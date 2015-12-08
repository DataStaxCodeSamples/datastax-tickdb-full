package com.datastax.tickdata;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
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
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.tickdata.model.TickData;
import com.datastax.timeseries.model.TimeSeries;

public class TickDataDao {
	
	private static Logger logger = LoggerFactory.getLogger(TickDataDao.class);
	
	private AtomicLong TOTAL_POINTS = new AtomicLong(0);
	private Session session;
	private static String keyspaceName = "datastax_tickdata_demo";
	private static String tableNameTick = keyspaceName + ".tick_data";

	private static final String INSERT_INTO_TICK = "Insert into " + tableNameTick + " (symbol,date,value) values (?, ?,?);";
	private static final String SELECT_FROM_TICK_RANGE = "Select symbol, date as date, value from " + tableNameTick + " where symbol = ? and date > ? and date < ?";
	private static final String SELECT_FROM_TICK = "Select symbol, date as date, value from " + tableNameTick + " where symbol = ?";

	private static final String SELECT_ALL = "Select * from " + tableNameTick;
	
	private PreparedStatement insertStmtTick;
	private PreparedStatement selectStmtTick;
	private PreparedStatement selectRangeStmtTick;
	private Cluster cluster;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.zzz"); 

	public TickDataDao(String[] contactPoints) {

		String remoteDC = PropertyHelper.getProperty("remoteDC", "");
		int replicasRemoteDC = Integer.parseInt(PropertyHelper.getProperty("replicasRemoteDC", "2"));
		
		cluster = Cluster.builder()
				.withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy(remoteDC, replicasRemoteDC)))
				.withRetryPolicy(new LoggingRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE))
				.addContactPoints(contactPoints).build();

		
		this.session = cluster.connect();

		this.insertStmtTick = session.prepare(INSERT_INTO_TICK);		
		this.insertStmtTick.setConsistencyLevel(ConsistencyLevel.ONE);
		this.selectStmtTick = session.prepare(SELECT_FROM_TICK);		
		this.selectStmtTick.setConsistencyLevel(ConsistencyLevel.ONE);
		this.selectRangeStmtTick = session.prepare(SELECT_FROM_TICK_RANGE);		
		this.selectRangeStmtTick.setConsistencyLevel(ConsistencyLevel.ONE);
	}
	
	public TimeSeries getTickData(String symbol){
		
		BoundStatement boundStmt = new BoundStatement(this.selectStmtTick);
		boundStmt.setString(0, symbol);
		
		ResultSet resultSet = session.execute(boundStmt);		
		Iterator<Row> iterator = resultSet.iterator();
		
		DoubleArrayList values = new DoubleArrayList();
		LongArrayList dates = new LongArrayList();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			dates.add(row.getDate("date").getTime());
			values.add(row.getDouble("value"));
		}

		dates.trimToSize();
		values.trimToSize();
		
		return new TimeSeries(symbol, dates.elements(), values.elements());
	}

	
	public TimeSeries getTickData(String symbol, long startTime, long endTime){
		
		BoundStatement boundStmt = new BoundStatement(this.selectRangeStmtTick);
		boundStmt.setString(0, symbol);
		boundStmt.setDate(1, new DateTime(startTime).toDate());
		boundStmt.setDate(2, new DateTime(endTime).toDate());
		
		ResultSet resultSet = session.execute(boundStmt);		
		Iterator<Row> iterator = resultSet.iterator();
		
		DoubleArrayList values = new DoubleArrayList();
		LongArrayList dates = new LongArrayList();

		while (iterator.hasNext()) {
			Row row = iterator.next();

			dates.add(row.getDate("date").getTime());
			values.add(row.getDouble("value"));
		}

		dates.trimToSize();
		values.trimToSize();
		
		return new TimeSeries(symbol, dates.elements(), values.elements());
	}
	
	public void insertTickData(TickData tickData) throws Exception{
		
		BoundStatement boundStmt = new BoundStatement(this.insertStmtTick);		
		DateTime dateTime = tickData.getTime() != null ? tickData.getTime() : DateTime.now();
		
		boundStmt.setString(0, tickData.getKey());
		boundStmt.setDate(1, new Timestamp(dateTime.getMillis()));
		boundStmt.setDouble(2, tickData.getValue());

		session.executeAsync(boundStmt);
			
		TOTAL_POINTS.incrementAndGet();				
	}

	public void insertTickData(List<TickData> list) throws Exception{
		BoundStatement boundStmt = new BoundStatement(this.insertStmtTick);
		List<ResultSetFuture> results = new ArrayList<ResultSetFuture>();
		
		for (TickData tickData : list) {
			
			DateTime dateTime = tickData.getTime() != null ? tickData.getTime() : DateTime.now();
						
			boundStmt.setString(0, tickData.getKey());
			boundStmt.setDate(1, new Timestamp(dateTime.getMillis()));
			boundStmt.setDouble(2, tickData.getValue());

			results.add(session.executeAsync(boundStmt));
						
			TOTAL_POINTS.incrementAndGet();			
		}
				
		for (ResultSetFuture future : results) {
			future.getUninterruptibly();
		}
		return;
	}
	
	/*public void selectAllHistoricData(int fetchSize){
		Statement stmt = new SimpleStatement(SELECT_ALL, cluster, null);
		stmt.setFetchSize(fetchSize);
		ResultSet rs = session.execute(SELECT_ALL);
		
		Iterator<Row> iterator = rs.iterator();
		
		while (iterator.hasNext()){
			iterator.next().getDouble("value");
		}		
	}*/

	private String fillNumber(int num) {
		return num < 10 ? "0" + num : "" + num;
	}

	public long getTotalPoints() {
		return TOTAL_POINTS.get();
	}
}
