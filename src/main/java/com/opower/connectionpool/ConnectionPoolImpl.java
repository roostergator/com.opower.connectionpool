package com.opower.connectionpool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;
import javax.swing.event.EventListenerList;

/**
 * Provides an implementation of the ConnectionPool interface.
 * 
 * This implementation supports the definition of minimum and maximum bounds for the number of 
 * connections maintained by the connection pool.  While the maximum bounds will not be exceeded,
 * is is possible for the minimum bounds to be under run if the underlying data source fails to
 * return a connection in response to a call to @see {@link javax.sql.DataSource#getConnection()}.
 * 
 * Additionally, the connection pool can attempt to recover connections that have been idle for 
 * some duration.  A connection is considered idle if no method calls have been made against the 
 * public interface for a defined period of time.  A recovered connection is no longer available 
 * for use and any call to the public interface (except @see {@link java.sql.Connection#isClosed()})
 * will throw an exception. Also, an attempt will be made to close any CallableStatement, 
 * PreparedStatement and Statement instances that have been retrieved using the connection.
 * 
 * @author Joshua Mark Rutherford
 */
public class ConnectionPoolImpl implements ConnectionPool {
	
	/**
	 * Defines the default minimum number of connections within a connection pool.
	 */
	public static final int DEFAULT_MINIMUM_CONNECTIONS = 0;
	
	/**
	 * Defines the default maximum number of connections within a connection pool.
	 */
	public static final int DEFAULT_MAXIMUM_CONNECTIONS = Integer.MAX_VALUE;
		
	/**
	 * Defines the default number of milliseconds before an idle connection is automatically returned to the connection pool.
	 */
	public static final long DEFAULT_TIMEOUT = 0;
	
	/**
	 * Initializes a new instance of the ConnectionPoolImpl class.
	 * @param dataSource The data source for the connection pool.
	 * @throws SQLException
	 */
	public ConnectionPoolImpl(DataSource dataSource) throws SQLException {
		this(dataSource, ConnectionPoolImpl.DEFAULT_MINIMUM_CONNECTIONS);
	}
	
	/**
	 * Initializes a new instance of the ConnectionPoolImpl class with a minimum number of connections.
	 * @param dataSource The data source for the connection pool.
	 * @param minimumConnections The minimum number of connections for the connection pool. Valid values are greater than or equal to zero and less than or equal to ConnectionPoolImpl.DEFAULT_MAXIMUM_CONNECTIONS.
	 * @throws SQLException
	 */
	public ConnectionPoolImpl(DataSource dataSource, int minimumConnections) throws SQLException {
		this(dataSource, minimumConnections, ConnectionPoolImpl.DEFAULT_MAXIMUM_CONNECTIONS);
	}
	
	/**
	 * Initializes a new instance of the ConnectionPoolImpl class with a minimum and maximum number of connections.
	 * @param dataSource The data source for the connection pool.
	 * @param minimumConnections The minimum number of connections for the connection pool. This value must be greater than or equal to zero and less than or equal to maximumConnections.
	 * @param maximumConnections The maximum number of connections for the connection pool. This value must be greater than than zero and greater than or equal to minimumConnections.
	 * @throws SQLException
	 */
	public ConnectionPoolImpl(DataSource dataSource, int minimumConnections, int maximumConnections) throws SQLException {
		this(dataSource, minimumConnections, maximumConnections, ConnectionPoolImpl.DEFAULT_TIMEOUT);
	}
	
	/**
	 * Initializes a new instance of the ConnectionPoolImpl class with a minimum and maximum number of connections and a timeout.
	 * @param dataSource The data source for the connection pool.
	 * @param minimumConnections The minimum number of connections for the connection pool. This value must be greater than or equal to zero and less than or equal to maximumConnections.
	 * @param maximumConnections The maximum number of connections for the connection pool. This value must be greater than than zero and greater than or equal to minimumConnections.
	 * @param timeout The number of milliseconds before and idle connection is automatically released to the connection pool. This value must be greater than or equal to zero.  A value of zero will disable automatic timeouts. 
	 * @throws SQLException
	 */
	public ConnectionPoolImpl(DataSource dataSource, int minimumConnections, int maximumConnections, long timeout) throws SQLException {
		if (dataSource == null) {
			throw new IllegalArgumentException("Data source cannot be null.");
		}
		if (minimumConnections < 0) {
			throw new IllegalArgumentException("Minimum number of connections cannot be less than zero.");
		}
		if (maximumConnections < 1) {
			throw new IllegalArgumentException("Maximum number of connections cannot be less than one.");
		}
		if (minimumConnections > maximumConnections) {
			throw new IllegalArgumentException("Minimum number of connections cannot be greater than maximum number of connections.");
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout cannot be less than zero.");
		}
		this.dataSource = dataSource;
		this.currentConnections = new AtomicInteger(0);
		this.maximumConnections = maximumConnections;
		this.minimumConnections = minimumConnections;
		this.queue = new ConcurrentLinkedQueue<Connection>();
		this.timeout = timeout;
		for (int i = 0; i < this.minimumConnections; i++) {
			this.currentConnections.incrementAndGet();
			if (!this.queue.offer(this.dataSource.getConnection())) {
				this.currentConnections.decrementAndGet();
			}
		}
	}
	
	/**
	 * Gets the current number of connections in the connection pool.
	 * @return The current number of connection in the connection pool.
	 */
	public int getCurrentConnections() {
		return this.currentConnections.get();
	}
	
	/**
	 * Gets the maximum number of connections in the connection pool.
	 * @return The maximum number of connections in the connection pool.
	 */
	public int getMaximumConnections() {
		return this.maximumConnections;
	}
	
	/**
	 * Gets the minimum number of connections in the connection pool.
	 * @return The minimum number of connections in the connection pool.
	 */
	public int getMinimumConnections() {
		return this.minimumConnections;
	}
	
	/**
	 * Gets the number of milliseconds before and idle connection is automatically released to the connection pool.
	 * @return The number of milliseconds before and idle connection is automatically released to the connection pool.
	 */
	public long getTimeout() {
		return this.timeout;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.opower.connectionpool.ConnectionPool#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		Connection connection = this.queue.poll();
		if (connection == null) {
			int currentConnections = this.currentConnections.incrementAndGet();
			if (currentConnections > this.maximumConnections) {
				this.currentConnections.decrementAndGet();
				throw new SQLException("Maximum number of pooled connections has been reached.");
			} else {
				try {
					connection = this.dataSource.getConnection();
				} catch (SQLException e) {
					this.currentConnections.decrementAndGet();
					throw e;
				}
			}
		} else {
			if (connection.isClosed()) {
				connection = this.getConnection();
			}
		}
		return new PooledConnection(connection, this.timeout);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.opower.connectionpool.ConnectionPool#releaseConnection(java.sql.Connection)
	 */
	public void releaseConnection(Connection connection) throws SQLException {
		if (connection == null) {
			throw new SQLException("Null connection cannot be released.");
		}
		if (!(connection instanceof PooledConnection)) {
			throw new SQLException("Connection does not belong to the connection pool.");
		}
		PooledConnection pooledConnection = (PooledConnection)connection;
		if (pooledConnection.getConnectionPool() != this) {
			throw new SQLException("Connection does not belong to the connection pool.");
		}
		connection = pooledConnection.release();
		if (connection != null) {
			if (connection.isClosed()) {
				if (this.currentConnections.decrementAndGet() < this.minimumConnections) {
					this.currentConnections.incrementAndGet();
					if (!this.queue.offer(this.dataSource.getConnection())) {
						this.currentConnections.decrementAndGet();
					}
				}
			} else {
				if (!this.queue.offer(connection)) {
					this.currentConnections.decrementAndGet();
				}
			}
		}
	}
	
	private DataSource dataSource;
	private AtomicInteger currentConnections;
	private int maximumConnections;
	private int minimumConnections;
	private Queue<Connection> queue;
	private long timeout;
	
	/**
	 * Wraps an instance of the CallableStatement interface for use within a connection pool.
	 * 
	 * Note that this wrapper does not actually pool the underlying callable statement but it does
	 * attempt to close the underlying callable statement if the corresponding connection is 
	 * released. However, it is recommended that unused resources be closed when no longer in use.
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledCallableStatement extends PooledPreparedStatement implements CallableStatement {
		
		/**
		 * Initializes a new instance of the PooledCallableStatement class.
		 * @param pooledConnection the pooled connection class for the pooled callable statement.
		 * @param callableStatement the callable statement wrapped by the pooled callable statement.
		 */
		public PooledCallableStatement(PooledConnection pooledConnection, CallableStatement callableStatement) throws SQLException {
			super(pooledConnection, callableStatement);
			this.callableStatement = callableStatement;
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(int, int)
		 */
		public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
			this.callableStatement.registerOutParameter(parameterIndex, sqlType);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
		 */
		public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
			this.callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#wasNull()
		 */
		public boolean wasNull() throws SQLException {
			return this.callableStatement.wasNull();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getString(int)
		 */
		public String getString(int parameterIndex) throws SQLException {
			return this.callableStatement.getString(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBoolean(int)
		 */
		public boolean getBoolean(int parameterIndex) throws SQLException {
			return this.callableStatement.getBoolean(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getByte(int)
		 */
		public byte getByte(int parameterIndex) throws SQLException {
			return this.callableStatement.getByte(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getShort(int)
		 */
		public short getShort(int parameterIndex) throws SQLException {
			return this.callableStatement.getShort(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getInt(int)
		 */
		public int getInt(int parameterIndex) throws SQLException {
			return this.callableStatement.getInt(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getLong(int)
		 */
		public long getLong(int parameterIndex) throws SQLException {
			return this.callableStatement.getLong(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getFloat(int)
		 */
		public float getFloat(int parameterIndex) throws SQLException {
			return this.callableStatement.getFloat(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDouble(int)
		 */
		public double getDouble(int parameterIndex) throws SQLException {
			return this.callableStatement.getDouble(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBigDecimal(int, int)
		 */
		@Deprecated
		public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
			return this.callableStatement.getBigDecimal(parameterIndex, scale);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBytes(int)
		 */
		public byte[] getBytes(int parameterIndex) throws SQLException {
			return this.callableStatement.getBytes(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDate(int)
		 */
		public Date getDate(int parameterIndex) throws SQLException {
			return this.callableStatement.getDate(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTime(int)
		 */
		public Time getTime(int parameterIndex) throws SQLException {
			return this.callableStatement.getTime(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTimestamp(int)
		 */
		public Timestamp getTimestamp(int parameterIndex) throws SQLException {
			return this.callableStatement.getTimestamp(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getObject(int)
		 */
		public Object getObject(int parameterIndex) throws SQLException {
			return this.callableStatement.getObject(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBigDecimal(int)
		 */
		public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
			return this.callableStatement.getBigDecimal(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
		 */
		public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
			return this.callableStatement.getObject(i, map);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getRef(int)
		 */
		public Ref getRef(int i) throws SQLException {
			return this.callableStatement.getRef(i);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBlob(int)
		 */
		public Blob getBlob(int i) throws SQLException {
			return this.callableStatement.getBlob(i);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getClob(int)
		 */
		public Clob getClob(int i) throws SQLException {
			return this.callableStatement.getClob(i);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getArray(int)
		 */
		public Array getArray(int i) throws SQLException {
			return this.callableStatement.getArray(i);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
		 */
		public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
			return this.callableStatement.getDate(parameterIndex, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
		 */
		public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
			return this.callableStatement.getTime(parameterIndex, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
		 */
		public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
			return this.callableStatement.getTimestamp(parameterIndex, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
		 */
		public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
			this.callableStatement.registerOutParameter(paramIndex, sqlType, typeName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
		 */
		public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
			this.callableStatement.registerOutParameter(parameterName, sqlType);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
		 */
		public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
			this.callableStatement.registerOutParameter(parameterName, sqlType, scale);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
		 */
		public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
			this.callableStatement.registerOutParameter(parameterName, sqlType, typeName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getURL(int)
		 */
		public URL getURL(int parameterIndex) throws SQLException {
			return this.callableStatement.getURL(parameterIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
		 */
		public void setURL(String parameterName, URL val) throws SQLException {
			this.callableStatement.setURL(parameterName, val);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
		 */
		public void setNull(String parameterName, int sqlType) throws SQLException {
			this.callableStatement.setNull(parameterName, sqlType);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
		 */
		public void setBoolean(String parameterName, boolean x) throws SQLException {
			this.callableStatement.setBoolean(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
		 */
		public void setByte(String parameterName, byte x) throws SQLException {
			this.callableStatement.setByte(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
		 */
		public void setShort(String parameterName, short x) throws SQLException {
			this.callableStatement.setShort(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
		 */
		public void setInt(String parameterName, int x) throws SQLException {
			this.callableStatement.setInt(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
		 */
		public void setLong(String parameterName, long x) throws SQLException {
			this.callableStatement.setLong(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
		 */
		public void setFloat(String parameterName, float x) throws SQLException {
			this.callableStatement.setFloat(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
		 */
		public void setDouble(String parameterName, double x) throws SQLException {
			this.callableStatement.setDouble(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String, java.math.BigDecimal)
		 */
		public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
			this.callableStatement.setBigDecimal(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setString(java.lang.String, java.lang.String)
		 */
		public void setString(String parameterName, String x) throws SQLException {
			this.callableStatement.setString(parameterName, x);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
		 */
		public void setBytes(String parameterName, byte[] x) throws SQLException {
			this.callableStatement.setBytes(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
		 */
		public void setDate(String parameterName, Date x) throws SQLException {
			this.callableStatement.setDate(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
		 */
		public void setTime(String parameterName, Time x) throws SQLException {
			this.callableStatement.setTime(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp)
		 */
		public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
			this.callableStatement.setTimestamp(parameterName, x);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
		 */
		public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
			this.callableStatement.setAsciiStream(parameterName, x, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
		 */
		public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
			this.callableStatement.setBinaryStream(parameterName, x, length);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
		 */
		public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
			this.callableStatement.setObject(parameterName, x, targetSqlType, scale);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
		 */
		public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
			this.callableStatement.setObject(parameterName, x, targetSqlType);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object)
		 */
		public void setObject(String parameterName, Object x) throws SQLException {
			this.callableStatement.setObject(parameterName, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
		 */
		public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
			this.callableStatement.setCharacterStream(parameterName, reader, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
		 */
		public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
			this.callableStatement.setDate(parameterName, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
		 */
		public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
			this.callableStatement.setTime(parameterName, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
		 */
		public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
			this.callableStatement.setTimestamp(parameterName, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
		 */
		public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
			this.callableStatement.setNull(parameterName, sqlType, typeName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getString(java.lang.String)
		 */
		public String getString(String parameterName) throws SQLException {
			return this.callableStatement.getString(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
		 */
		public boolean getBoolean(String parameterName) throws SQLException {
			return this.callableStatement.getBoolean(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getByte(java.lang.String)
		 */
		public byte getByte(String parameterName) throws SQLException {
			return this.callableStatement.getByte(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getShort(java.lang.String)
		 */
		public short getShort(String parameterName) throws SQLException {
			return this.callableStatement.getShort(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getInt(java.lang.String)
		 */
		public int getInt(String parameterName) throws SQLException {
			return this.callableStatement.getInt(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getLong(java.lang.String)
		 */
		public long getLong(String parameterName) throws SQLException {
			return this.callableStatement.getLong(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getFloat(java.lang.String)
		 */
		public float getFloat(String parameterName) throws SQLException {
			return this.callableStatement.getFloat(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDouble(java.lang.String)
		 */
		public double getDouble(String parameterName) throws SQLException {
			return this.callableStatement.getDouble(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBytes(java.lang.String)
		 */
		public byte[] getBytes(String parameterName) throws SQLException {
			return this.callableStatement.getBytes(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDate(java.lang.String)
		 */
		public Date getDate(String parameterName) throws SQLException {
			return this.callableStatement.getDate(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTime(java.lang.String)
		 */
		public Time getTime(String parameterName) throws SQLException {
			return this.callableStatement.getTime(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
		 */
		public Timestamp getTimestamp(String parameterName) throws SQLException {
			return this.callableStatement.getTimestamp(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getObject(java.lang.String)
		 */
		public Object getObject(String parameterName) throws SQLException {
			return this.callableStatement.getObject(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
		 */
		public BigDecimal getBigDecimal(String parameterName) throws SQLException {
			return this.callableStatement.getBigDecimal(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
		 */
		public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
			return this.callableStatement.getObject(parameterName, map);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getRef(java.lang.String)
		 */
		public Ref getRef(String parameterName) throws SQLException {
			return this.callableStatement.getRef(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getBlob(java.lang.String)
		 */
		public Blob getBlob(String parameterName) throws SQLException {
			return this.callableStatement.getBlob(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getClob(java.lang.String)
		 */
		public Clob getClob(String parameterName) throws SQLException {
			return this.callableStatement.getClob(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getArray(java.lang.String)
		 */
		public Array getArray(String parameterName) throws SQLException {
			return this.callableStatement.getArray(parameterName);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
		 */
		public Date getDate(String parameterName, Calendar cal) throws SQLException {
			return this.callableStatement.getDate(parameterName, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
		 */
		public Time getTime(String parameterName, Calendar cal) throws SQLException {
			return this.callableStatement.getTime(parameterName, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
		 */
		public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
			return this.callableStatement.getTimestamp(parameterName, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getURL(java.lang.String)
		 */
		public URL getURL(String parameterName) throws SQLException {
			return this.callableStatement.getURL(parameterName);
		}

		private CallableStatement callableStatement;
		
	}
	
	/**
	 * Wraps an instance of the Connection interface for use within a connection pool.
	 * 
	 * While this wrapper delegates most methods to the underlying connection, the following methods
	 * have custom functionality:
	 * 
	 * <ul>
	 * <li> @see com.opower.connectionpool.ConnectionPoolImpl.PooledConnection#close - Releases the underlying connection to the connection pool and so that is is no longer available from the pooled connection.
	 * <li> @see com.opower.connectionpool.ConnectionPoolImpl.PooledConnection#isClosed - Indicates whether the underlying connection is closed or has been released to the connection pool. 
	 * </ul>
	 * 
	 * Additionally, any method that returns the following types wraps the return value from the 
	 * underlying connection in another wrapper to prevent direct access to the underlying connection:
	 * 
	 * <ul>
	 * <li> @see java.sql.DatabaseMetaData - Wrapped in a @see com.opower.connectionpool.ConnectionPoolImpl.PooledDatabaseMetaData.
	 * <li> @see java.sql.CallableStatement - Wrapped in a @see com.opower.connectionpool.ConnectionPoolImpl.PooledCallableStatement.
	 * <li> @see java.sql.PreparedStatement - Wrapped in a @see com.opower.connectionpool.ConnectionPoolImpl.PooledPreparedStatement.
	 * <li> @see java.sql.ResultSet - Wrapped in a @see com.opower.connectionpool.ConnectionPoolImpl.PooledResultSet.
	 * <li> @see java.sql.Statement - Wrapped in a @see com.opower.connectionpool.ConnectionPoolImpl.PooledStatement.
	 * </ul>
	 * 
	 * Lastly, this implementation supports pooled connection listeners and notifies them when the 
	 * connection is closed (i.e., released to the connection pool).
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledConnection implements Connection {
		
		/**
		 * Initializes a new instance of the PooledConnection class.
		 * @param connection The connection wrapped by the pooled connection.
		 * @param timeout The number of milliseconds before the idle connection is automatically released to the connection pool. This value must be greater than or equal to zero.  A value of zero will disable automatic timeouts.
		 */
		public PooledConnection(Connection connection, long timeout) {
			this.connection = new AtomicReference<Connection>(connection);
			this.last = new AtomicLong(System.currentTimeMillis());
			this.listeners = new EventListenerList();
			this.timeout = timeout;
			if (this.timeout > 0) {
				this.timer = new Timer(true);
				this.timer.schedule(new TimeoutTask(), this.timeout);
			}
		}		
		
		/**
		 * Gets the connection pool to which the pooled connection belongs.
		 * @return The connection pool to which the pooled connection belongs.
		 */
		public ConnectionPool getConnectionPool() {
			return ConnectionPoolImpl.this;
		}
		
		/**
		 * Adds a pooled connection event listener to the pooled connection.
		 * @param listener The pooled connection event listener to add.
		 */
		public void addPooledConnectionListener(PooledConnectionListener listener) {
			this.listeners.add(PooledConnectionListener.class, listener);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#createStatement()
		 */
		public Statement createStatement() throws SQLException {
			return new PooledStatement(this, this.ensureConnection().createStatement());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String)
		 */
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String)
		 */
		public CallableStatement prepareCall(String sql) throws SQLException {
			return new PooledCallableStatement(this, this.ensureConnection().prepareCall(sql));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#nativeSQL(java.lang.String)
		 */
		public String nativeSQL(String sql) throws SQLException {
			return this.ensureConnection().nativeSQL(sql);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setAutoCommit(boolean)
		 */
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			this.ensureConnection().setAutoCommit(autoCommit);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getAutoCommit()
		 */
		public boolean getAutoCommit() throws SQLException {
			return this.ensureConnection().getAutoCommit();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#commit()
		 */
		public void commit() throws SQLException {
			this.ensureConnection().commit();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#rollback()
		 */
		public void rollback() throws SQLException {
			this.ensureConnection().rollback();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#close()
		 */
		public void close() throws SQLException {
			ConnectionPoolImpl.this.releaseConnection(this);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#isClosed()
		 */
		public boolean isClosed() throws SQLException {
			Connection connection = this.getConnection();
			return (connection == null || connection.isClosed());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getMetaData()
		 */
		public DatabaseMetaData getMetaData() throws SQLException {
			return new PooledDatabaseMetaData(this, this.ensureConnection().getMetaData());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setReadOnly(boolean)
		 */
		public void setReadOnly(boolean readOnly) throws SQLException {
			this.ensureConnection().setReadOnly(readOnly);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#isReadOnly()
		 */
		public boolean isReadOnly() throws SQLException {
			return this.ensureConnection().isReadOnly();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setCatalog(java.lang.String)
		 */
		public void setCatalog(String catalog) throws SQLException {
			this.ensureConnection().setCatalog(catalog);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getCatalog()
		 */
		public String getCatalog() throws SQLException {
			return this.ensureConnection().getCatalog();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setTransactionIsolation(int)
		 */
		public void setTransactionIsolation(int level) throws SQLException {
			this.ensureConnection().setTransactionIsolation(level);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getTransactionIsolation()
		 */
		public int getTransactionIsolation() throws SQLException {
			return this.ensureConnection().getTransactionIsolation();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getWarnings()
		 */
		public SQLWarning getWarnings() throws SQLException {
			return this.ensureConnection().getWarnings();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#clearWarnings()
		 */
		public void clearWarnings() throws SQLException {
			this.ensureConnection().clearWarnings();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#createStatement(int, int)
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledStatement(this, this.ensureConnection().createStatement(resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql, resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledCallableStatement(this, this.ensureConnection().prepareCall(sql, resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getTypeMap()
		 */
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return this.ensureConnection().getTypeMap();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setTypeMap(java.util.Map)
		 */
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			this.ensureConnection().setTypeMap(map);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setHoldability(int)
		 */
		public void setHoldability(int holdability) throws SQLException {
			this.ensureConnection().setHoldability(holdability);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getHoldability()
		 */
		public int getHoldability() throws SQLException {
			return this.ensureConnection().getHoldability();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setSavepoint()
		 */
		public Savepoint setSavepoint() throws SQLException {
			return this.ensureConnection().setSavepoint();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#setSavepoint(java.lang.String)
		 */
		public Savepoint setSavepoint(String name) throws SQLException {
			return this.ensureConnection().setSavepoint(name);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#rollback(java.sql.Savepoint)
		 */
		public void rollback(Savepoint savepoint) throws SQLException {
			this.ensureConnection().rollback(savepoint);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
		 */
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			this.ensureConnection().releaseSavepoint(savepoint);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#createStatement(int, int, int)
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledStatement(this, this.ensureConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledCallableStatement(this, this.ensureConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
		 */
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql, autoGeneratedKeys));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
		 */
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql, columnIndexes));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
		 */
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return new PooledPreparedStatement(this, this.ensureConnection().prepareStatement(sql, columnNames));
		}
		
		private AtomicReference<Connection> connection;
		private AtomicLong last;
		private long timeout;
		private Timer timer;
		private EventListenerList listeners;
		
		/**
		 * Gets the connection wrapped by the pooled connection or null if the connection
		 * has been released to the connection pool.
		 */
		private Connection getConnection() {
			this.last.set(System.currentTimeMillis());
			return this.connection.get();
		}
		
		/**
		 * Gets the connection wrapped by the pooled connection or throws an exception if 
		 * the connection has been released to the connection pool.
		 * @return The connection wrapped by the pooled connection.
		 * @throws SQLException if the connection has been released to the pool.
		 */
		private Connection ensureConnection() throws SQLException {
			Connection connection = this.getConnection();
			if (connection == null) {
				throw new SQLException("The connection has been released to the pool.");
			}
			return connection;
		}
		
		/**
		 * Releases underlying connection from the pooled connection and returns its value.
		 * @return The underlying connection;
		 */
		private Connection release() {
			Connection connection = this.connection.getAndSet(null);
			PooledConnectionListener[] listeners = this.listeners.getListeners(PooledConnectionListener.class);
			for (PooledConnectionListener listener : listeners) {
				listener.pooledConnectionClosed();
			}
			return connection;
		}
		
		/**
		 * Provides a timer task that automatically releases idle pooled connections to the connection pool.
		 */
		private class TimeoutTask extends TimerTask {

			/*
			 * (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				PooledConnection connection = PooledConnection.this;
				long delay = connection.last.addAndGet(connection.timeout) - System.currentTimeMillis();
				if (delay > 0) {
					connection.timer.schedule(new TimeoutTask(), delay);
				} else {
					connection.timer.cancel();
					try {
						connection.getConnectionPool().releaseConnection(connection);
					} catch (SQLException e) {
						
					}
				}
			}
			
		}
		
	}
	
	/**
	 * Provides an interface for classes that listen to pooled connections.
	 */
	private interface PooledConnectionListener extends EventListener {
		
		/**
		 * Called when a pooled connection is closed or released.
		 */
		public void pooledConnectionClosed();
		
	}
	
	/**
	 * Wraps an instance of the DatabaseMetaData interface for use within a connection pool.
	 * 
	 * Note that this wrapper does not actually pool or otherwise manage the underlying database 
	 * meta data. The only purpose of this class is to prevent unintended access to the underlying 
	 * connection through a call to @see {@link java.sql.DataBaseMetaData#getConnection()}.
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledDatabaseMetaData implements DatabaseMetaData {

		/**
		 * Initializes a new instance of the PooledDatabaseMetaData class.
		 * @param pooledConnection the instance of the PooledConnection class for the pooled database meta data.
		 * @param databaseMetaData the instance of the DatabaseMetaData interface wrapped by the pooled database meta data.
		 */
		public PooledDatabaseMetaData(PooledConnection pooledConnection, DatabaseMetaData databaseMetaData) {
			this.databaseMetaData = databaseMetaData;
			this.pooledConnection = pooledConnection;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
		 */
		public boolean allProceduresAreCallable() throws SQLException {
			return this.databaseMetaData.allProceduresAreCallable();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
		 */
		public boolean allTablesAreSelectable() throws SQLException {
			return this.databaseMetaData.allTablesAreSelectable();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getURL()
		 */
		public String getURL() throws SQLException {
			return this.databaseMetaData.getURL();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getUserName()
		 */
		public String getUserName() throws SQLException {
			return this.databaseMetaData.getUserName();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#isReadOnly()
		 */
		public boolean isReadOnly() throws SQLException {
			return this.databaseMetaData.isReadOnly();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
		 */
		public boolean nullsAreSortedHigh() throws SQLException {
			return this.databaseMetaData.nullsAreSortedHigh();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
		 */
		public boolean nullsAreSortedLow() throws SQLException {
			return this.databaseMetaData.nullsAreSortedLow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
		 */
		public boolean nullsAreSortedAtStart() throws SQLException {
			return this.databaseMetaData.nullsAreSortedAtStart();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
		 */
		public boolean nullsAreSortedAtEnd() throws SQLException {
			return this.databaseMetaData.nullsAreSortedAtEnd();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
		 */
		public String getDatabaseProductName() throws SQLException {
			return this.databaseMetaData.getDatabaseProductName();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
		 */
		public String getDatabaseProductVersion() throws SQLException {
			return this.databaseMetaData.getDatabaseProductVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDriverName()
		 */
		public String getDriverName() throws SQLException {
			return this.databaseMetaData.getDriverName();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDriverVersion()
		 */
		public String getDriverVersion() throws SQLException {
			return this.databaseMetaData.getDriverVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
		 */
		public int getDriverMajorVersion() {
			return this.databaseMetaData.getDriverMajorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
		 */
		public int getDriverMinorVersion() {
			return this.databaseMetaData.getDriverMinorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#usesLocalFiles()
		 */
		public boolean usesLocalFiles() throws SQLException {
			return this.databaseMetaData.usesLocalFiles();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
		 */
		public boolean usesLocalFilePerTable() throws SQLException {
			return this.databaseMetaData.usesLocalFilePerTable();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
		 */
		public boolean supportsMixedCaseIdentifiers() throws SQLException {
			return this.databaseMetaData.supportsMixedCaseIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
		 */
		public boolean storesUpperCaseIdentifiers() throws SQLException {
			return this.databaseMetaData.storesMixedCaseIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
		 */
		public boolean storesLowerCaseIdentifiers() throws SQLException {
			return this.databaseMetaData.storesLowerCaseIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
		 */
		public boolean storesMixedCaseIdentifiers() throws SQLException {
			return this.databaseMetaData.storesMixedCaseIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
		 */
		public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
			return this.databaseMetaData.supportsMixedCaseQuotedIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
		 */
		public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
			return this.databaseMetaData.storesUpperCaseQuotedIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
		 */
		public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
			return this.databaseMetaData.storesLowerCaseQuotedIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
		 */
		public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
			return this.databaseMetaData.storesMixedCaseQuotedIdentifiers();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
		 */
		public String getIdentifierQuoteString() throws SQLException {
			return this.databaseMetaData.getIdentifierQuoteString();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSQLKeywords()
		 */
		public String getSQLKeywords() throws SQLException {
			return this.databaseMetaData.getSQLKeywords();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getNumericFunctions()
		 */
		public String getNumericFunctions() throws SQLException {
			return this.databaseMetaData.getNumericFunctions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getStringFunctions()
		 */
		public String getStringFunctions() throws SQLException {
			return this.databaseMetaData.getStringFunctions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSystemFunctions()
		 */
		public String getSystemFunctions() throws SQLException {
			return this.databaseMetaData.getSystemFunctions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
		 */
		public String getTimeDateFunctions() throws SQLException {
			return this.databaseMetaData.getTimeDateFunctions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSearchStringEscape()
		 */
		public String getSearchStringEscape() throws SQLException {
			return this.databaseMetaData.getSearchStringEscape();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
		 */
		public String getExtraNameCharacters() throws SQLException {
			return this.databaseMetaData.getExtraNameCharacters();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
		 */
		public boolean supportsAlterTableWithAddColumn() throws SQLException {
			return this.databaseMetaData.supportsAlterTableWithAddColumn();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
		 */
		public boolean supportsAlterTableWithDropColumn() throws SQLException {
			return this.databaseMetaData.supportsAlterTableWithDropColumn();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
		 */
		public boolean supportsColumnAliasing() throws SQLException {
			return this.databaseMetaData.supportsColumnAliasing();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
		 */
		public boolean nullPlusNonNullIsNull() throws SQLException {
			return this.databaseMetaData.nullPlusNonNullIsNull();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsConvert()
		 */
		public boolean supportsConvert() throws SQLException {
			return this.databaseMetaData.supportsConvert();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
		 */
		public boolean supportsConvert(int fromType, int toType) throws SQLException {
			return this.databaseMetaData.supportsConvert(fromType, toType);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
		 */
		public boolean supportsTableCorrelationNames() throws SQLException {
			return this.databaseMetaData.supportsTableCorrelationNames();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
		 */
		public boolean supportsDifferentTableCorrelationNames() throws SQLException {
			return this.databaseMetaData.supportsDifferentTableCorrelationNames();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
		 */
		public boolean supportsExpressionsInOrderBy() throws SQLException {
			return this.databaseMetaData.supportsExpressionsInOrderBy();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
		 */
		public boolean supportsOrderByUnrelated() throws SQLException {
			return this.databaseMetaData.supportsOrderByUnrelated();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsGroupBy()
		 */
		public boolean supportsGroupBy() throws SQLException {
			return this.databaseMetaData.supportsGroupBy();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
		 */
		public boolean supportsGroupByUnrelated() throws SQLException {
			return this.databaseMetaData.supportsGroupByUnrelated();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
		 */
		public boolean supportsGroupByBeyondSelect() throws SQLException {
			return this.databaseMetaData.supportsGroupByBeyondSelect();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
		 */
		public boolean supportsLikeEscapeClause() throws SQLException {
			return this.databaseMetaData.supportsLikeEscapeClause();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
		 */
		public boolean supportsMultipleResultSets() throws SQLException {
			return this.databaseMetaData.supportsMultipleResultSets();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
		 */
		public boolean supportsMultipleTransactions() throws SQLException {
			return this.databaseMetaData.supportsMultipleTransactions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
		 */
		public boolean supportsNonNullableColumns() throws SQLException {
			return this.databaseMetaData.supportsMinimumSQLGrammar();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
		 */
		public boolean supportsMinimumSQLGrammar() throws SQLException {
			return this.databaseMetaData.supportsMinimumSQLGrammar();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
		 */
		public boolean supportsCoreSQLGrammar() throws SQLException {
			return this.databaseMetaData.supportsCoreSQLGrammar();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
		 */
		public boolean supportsExtendedSQLGrammar() throws SQLException {
			return this.databaseMetaData.supportsExtendedSQLGrammar();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
		 */
		public boolean supportsANSI92EntryLevelSQL() throws SQLException {
			return this.databaseMetaData.supportsANSI92EntryLevelSQL();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
		 */
		public boolean supportsANSI92IntermediateSQL() throws SQLException {
			return this.databaseMetaData.supportsANSI92IntermediateSQL();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
		 */
		public boolean supportsANSI92FullSQL() throws SQLException {
			return this.databaseMetaData.supportsANSI92FullSQL();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
		 */
		public boolean supportsIntegrityEnhancementFacility() throws SQLException {
			return this.databaseMetaData.supportsIntegrityEnhancementFacility();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOuterJoins()
		 */
		public boolean supportsOuterJoins() throws SQLException {
			return this.databaseMetaData.supportsOuterJoins();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
		 */
		public boolean supportsFullOuterJoins() throws SQLException {
			return this.databaseMetaData.supportsFullOuterJoins();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
		 */
		public boolean supportsLimitedOuterJoins() throws SQLException {
			return this.databaseMetaData.supportsLimitedOuterJoins();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSchemaTerm()
		 */
		public String getSchemaTerm() throws SQLException {
			return this.databaseMetaData.getSchemaTerm();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getProcedureTerm()
		 */
		public String getProcedureTerm() throws SQLException {
			return this.databaseMetaData.getProcedureTerm();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCatalogTerm()
		 */
		public String getCatalogTerm() throws SQLException {
			return this.databaseMetaData.getCatalogTerm();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#isCatalogAtStart()
		 */
		public boolean isCatalogAtStart() throws SQLException {
			return this.databaseMetaData.isCatalogAtStart();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCatalogSeparator()
		 */
		public String getCatalogSeparator() throws SQLException {
			return this.databaseMetaData.getCatalogSeparator();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
		 */
		public boolean supportsSchemasInDataManipulation() throws SQLException {
			return this.databaseMetaData.supportsSchemasInDataManipulation();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
		 */
		public boolean supportsSchemasInProcedureCalls() throws SQLException {
			return this.databaseMetaData.supportsSchemasInProcedureCalls();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
		 */
		public boolean supportsSchemasInTableDefinitions() throws SQLException {
			return this.databaseMetaData.supportsSchemasInTableDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
		 */
		public boolean supportsSchemasInIndexDefinitions() throws SQLException {
			return this.databaseMetaData.supportsSchemasInIndexDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
		 */
		public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
			return this.databaseMetaData.supportsSchemasInPrivilegeDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
		 */
		public boolean supportsCatalogsInDataManipulation() throws SQLException {
			return this.databaseMetaData.supportsCatalogsInDataManipulation();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
		 */
		public boolean supportsCatalogsInProcedureCalls() throws SQLException {
			return this.databaseMetaData.supportsCatalogsInProcedureCalls();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
		 */
		public boolean supportsCatalogsInTableDefinitions() throws SQLException {
			return this.databaseMetaData.supportsCatalogsInTableDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
		 */
		public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
			return this.databaseMetaData.supportsCatalogsInIndexDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
		 */
		public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
			return this.databaseMetaData.supportsCatalogsInPrivilegeDefinitions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
		 */
		public boolean supportsPositionedDelete() throws SQLException {
			return this.databaseMetaData.supportsPositionedDelete();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
		 */
		public boolean supportsPositionedUpdate() throws SQLException {
			return this.databaseMetaData.supportsPositionedUpdate();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
		 */
		public boolean supportsSelectForUpdate() throws SQLException {
			return this.databaseMetaData.supportsSelectForUpdate();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
		 */
		public boolean supportsStoredProcedures() throws SQLException {
			return this.databaseMetaData.supportsStoredProcedures();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
		 */
		public boolean supportsSubqueriesInComparisons() throws SQLException {
			return this.databaseMetaData.supportsSubqueriesInComparisons();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
		 */
		public boolean supportsSubqueriesInExists() throws SQLException {
			return this.databaseMetaData.supportsSubqueriesInExists();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
		 */
		public boolean supportsSubqueriesInIns() throws SQLException {
			return this.databaseMetaData.supportsSubqueriesInIns();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
		 */
		public boolean supportsSubqueriesInQuantifieds() throws SQLException {
			return this.databaseMetaData.supportsSubqueriesInQuantifieds();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
		 */
		public boolean supportsCorrelatedSubqueries() throws SQLException {
			return this.databaseMetaData.supportsCorrelatedSubqueries();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsUnion()
		 */
		public boolean supportsUnion() throws SQLException {
			return this.databaseMetaData.supportsUnion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsUnionAll()
		 */
		public boolean supportsUnionAll() throws SQLException {
			return this.databaseMetaData.supportsUnionAll();	
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
		 */
		public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
			return this.databaseMetaData.supportsOpenCursorsAcrossCommit();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
		 */
		public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
			return this.databaseMetaData.supportsOpenCursorsAcrossRollback();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
		 */
		public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
			return this.databaseMetaData.supportsOpenStatementsAcrossCommit();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
		 */
		public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
			return this.databaseMetaData.supportsOpenStatementsAcrossRollback();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
		 */
		public int getMaxBinaryLiteralLength() throws SQLException {
			return this.databaseMetaData.getMaxBinaryLiteralLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
		 */
		public int getMaxCharLiteralLength() throws SQLException {
			return this.databaseMetaData.getMaxCharLiteralLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
		 */
		public int getMaxColumnNameLength() throws SQLException {
			return this.databaseMetaData.getMaxColumnNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
		 */
		public int getMaxColumnsInGroupBy() throws SQLException {
			return this.databaseMetaData.getMaxColumnsInGroupBy();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
		 */
		public int getMaxColumnsInIndex() throws SQLException {
			return this.databaseMetaData.getMaxColumnsInIndex();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
		 */
		public int getMaxColumnsInOrderBy() throws SQLException {
			return this.databaseMetaData.getMaxColumnsInOrderBy();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
		 */
		public int getMaxColumnsInSelect() throws SQLException {
			return this.databaseMetaData.getMaxColumnsInSelect();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
		 */
		public int getMaxColumnsInTable() throws SQLException {
			return this.databaseMetaData.getMaxColumnsInTable();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxConnections()
		 */
		public int getMaxConnections() throws SQLException {
			return this.databaseMetaData.getMaxConnections();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
		 */
		public int getMaxCursorNameLength() throws SQLException {
			return this.databaseMetaData.getMaxCursorNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxIndexLength()
		 */
		public int getMaxIndexLength() throws SQLException {
			return this.databaseMetaData.getMaxIndexLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
		 */
		public int getMaxSchemaNameLength() throws SQLException {
			return this.databaseMetaData.getMaxSchemaNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
		 */
		public int getMaxProcedureNameLength() throws SQLException {
			return this.databaseMetaData.getMaxProcedureNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
		 */
		public int getMaxCatalogNameLength() throws SQLException {
			return this.databaseMetaData.getMaxCatalogNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxRowSize()
		 */
		public int getMaxRowSize() throws SQLException {
			return this.databaseMetaData.getMaxRowSize();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
		 */
		public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
			return this.databaseMetaData.doesMaxRowSizeIncludeBlobs();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxStatementLength()
		 */
		public int getMaxStatementLength() throws SQLException {
			return this.databaseMetaData.getMaxStatementLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxStatements()
		 */
		public int getMaxStatements() throws SQLException {
			return this.databaseMetaData.getMaxStatements();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
		 */
		public int getMaxTableNameLength() throws SQLException {
			return this.databaseMetaData.getMaxTableNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
		 */
		public int getMaxTablesInSelect() throws SQLException {
			return this.databaseMetaData.getMaxTablesInSelect();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
		 */
		public int getMaxUserNameLength() throws SQLException {
			return this.databaseMetaData.getMaxUserNameLength();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
		 */
		public int getDefaultTransactionIsolation() throws SQLException {
			return this.databaseMetaData.getDefaultTransactionIsolation();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsTransactions()
		 */
		public boolean supportsTransactions() throws SQLException {
			return this.databaseMetaData.supportsTransactions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
		 */
		public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
			return this.databaseMetaData.supportsTransactionIsolationLevel(level);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
		 */
		public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
			return this.databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
		 */
		public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
			return this.databaseMetaData.supportsDataManipulationTransactionsOnly();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
		 */
		public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
			return this.databaseMetaData.dataDefinitionCausesTransactionCommit();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
		 */
		public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
			return this.databaseMetaData.dataDefinitionIgnoredInTransactions();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
		 */
		public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSchemas()
		 */
		public ResultSet getSchemas() throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getSchemas());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCatalogs()
		 */
		public ResultSet getCatalogs() throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getCatalogs());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTableTypes()
		 */
		public ResultSet getTableTypes() throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getTableTypes());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getColumnPrivileges(catalog, schema, table, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getTablePrivileges(String catalog,	String schemaPattern, String tableNamePattern) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
		 */
		public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getBestRowIdentifier(catalog, schema, table, scope, nullable));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getVersionColumns(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getPrimaryKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getImportedKeys(String catalog, String schema,	String table) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getImportedKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getExportedKeys(String catalog, String schema,	String table) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getExportedKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getCrossReference(primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTypeInfo()
		 */
		public ResultSet getTypeInfo() throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getTypeInfo());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
		 */
		public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
			return new PooledResultSet(null, this.databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
		 */
		public boolean supportsResultSetType(int type) throws SQLException {
			return this.databaseMetaData.supportsResultSetType(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
		 */
		public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
			return this.databaseMetaData.supportsResultSetConcurrency(type, concurrency);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
		 */
		public boolean ownUpdatesAreVisible(int type) throws SQLException {
			return this.databaseMetaData.ownUpdatesAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
		 */
		public boolean ownDeletesAreVisible(int type) throws SQLException {
			return this.databaseMetaData.ownDeletesAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
		 */
		public boolean ownInsertsAreVisible(int type) throws SQLException {
			return this.databaseMetaData.ownInsertsAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
		 */
		public boolean othersUpdatesAreVisible(int type) throws SQLException {
			return this.databaseMetaData.othersUpdatesAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
		 */
		public boolean othersDeletesAreVisible(int type) throws SQLException {
			return this.databaseMetaData.othersDeletesAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
		 */
		public boolean othersInsertsAreVisible(int type) throws SQLException {
			return this.databaseMetaData.othersInsertsAreVisible(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
		 */
		public boolean updatesAreDetected(int type) throws SQLException {
			return this.databaseMetaData.updatesAreDetected(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
		 */
		public boolean deletesAreDetected(int type) throws SQLException {
			return this.databaseMetaData.deletesAreDetected(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
		 */
		public boolean insertsAreDetected(int type) throws SQLException {
			return this.databaseMetaData.insertsAreDetected(type);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
		 */
		public boolean supportsBatchUpdates() throws SQLException {
			return this.databaseMetaData.supportsBatchUpdates();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
		 */
		public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
			return this.databaseMetaData.getUDTs(catalog, schemaPattern, typeNamePattern, types);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			return this.pooledConnection;
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsSavepoints()
		 */
		public boolean supportsSavepoints() throws SQLException {
			return this.databaseMetaData.supportsSavepoints();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsNamedParameters()
		 */
		public boolean supportsNamedParameters() throws SQLException {
			return this.databaseMetaData.supportsNamedParameters();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
		 */
		public boolean supportsMultipleOpenResults() throws SQLException {
			return this.databaseMetaData.supportsMultipleOpenResults();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
		 */
		public boolean supportsGetGeneratedKeys() throws SQLException {
			return this.databaseMetaData.supportsGetGeneratedKeys();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
			return this.databaseMetaData.getSuperTypes(catalog, schemaPattern, typeNamePattern);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
			return this.databaseMetaData.getSuperTables(catalog, schemaPattern, tableNamePattern);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
			return this.databaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
		 */
		public boolean supportsResultSetHoldability(int holdability) throws SQLException {
			return this.databaseMetaData.supportsResultSetHoldability(holdability);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getResultSetHoldability()
		 */
		public int getResultSetHoldability() throws SQLException {
			return this.databaseMetaData.getResultSetHoldability();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
		 */
		public int getDatabaseMajorVersion() throws SQLException {
			return this.databaseMetaData.getDatabaseMajorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
		 */
		public int getDatabaseMinorVersion() throws SQLException {
			return this.databaseMetaData.getDatabaseMinorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
		 */
		public int getJDBCMajorVersion() throws SQLException {
			return this.databaseMetaData.getJDBCMajorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
		 */
		public int getJDBCMinorVersion() throws SQLException {
			return this.databaseMetaData.getJDBCMinorVersion();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSQLStateType()
		 */
		public int getSQLStateType() throws SQLException {
			return this.databaseMetaData.getSQLStateType();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
		 */
		public boolean locatorsUpdateCopy() throws SQLException {
			return this.databaseMetaData.locatorsUpdateCopy();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#supportsStatementPooling()
		 */
		public boolean supportsStatementPooling() throws SQLException {
			return this.databaseMetaData.supportsStatementPooling();
		}
		
		private PooledConnection pooledConnection;
		private DatabaseMetaData databaseMetaData;
		
	}
	
	/**
	 * Wraps an instance of the PreparedStatement interface for use within a connection pool.
	 * 
	 * Note that this wrapper does not actually pool the underlying prepared statement but it does
	 * attempt to close the underlying prepared statement if the corresponding connection is 
	 * released. However, it is recommended that unused resources be closed when no longer in use.
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledPreparedStatement extends PooledStatement implements PreparedStatement {

		/**
		 * Initializes a new instance of the PooledPreparedStatement class.
		 * @param pooledConnection the pooled connection for the pooled prepared statement.
		 * @param preparedStatement the prepared statement wrapped by the pooled prepared statement.
		 */
		public PooledPreparedStatement(PooledConnection pooledConnection, PreparedStatement preparedStatement) throws SQLException {
			super(pooledConnection, preparedStatement);
			this.preparedStatement = preparedStatement;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#executeQuery()
		 */
		public ResultSet executeQuery() throws SQLException {
			return new PooledResultSet(this, this.preparedStatement.executeQuery());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#executeUpdate()
		 */
		public int executeUpdate() throws SQLException {
			return this.preparedStatement.executeUpdate();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setNull(int, int)
		 */
		public void setNull(int parameterIndex, int sqlType) throws SQLException {
			this.preparedStatement.setNull(parameterIndex, sqlType);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
		 */
		public void setBoolean(int parameterIndex, boolean x) throws SQLException {
			this.preparedStatement.setBoolean(parameterIndex, x);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setByte(int, byte)
		 */
		public void setByte(int parameterIndex, byte x) throws SQLException {
			this.preparedStatement.setByte(parameterIndex, x);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setShort(int, short)
		 */
		public void setShort(int parameterIndex, short x) throws SQLException {
			this.preparedStatement.setShort(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setInt(int, int)
		 */
		public void setInt(int parameterIndex, int x) throws SQLException {
			this.preparedStatement.setInt(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setLong(int, long)
		 */
		public void setLong(int parameterIndex, long x) throws SQLException {
			this.preparedStatement.setLong(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setFloat(int, float)
		 */
		public void setFloat(int parameterIndex, float x) throws SQLException {
			this.preparedStatement.setFloat(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setDouble(int, double)
		 */
		public void setDouble(int parameterIndex, double x) throws SQLException {
			this.preparedStatement.setDouble(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
		 */
		public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
			this.preparedStatement.setBigDecimal(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
		 */
		public void setString(int parameterIndex, String x) throws SQLException {
			this.preparedStatement.setString(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setBytes(int, byte[])
		 */
		public void setBytes(int parameterIndex, byte[] x) throws SQLException {
			this.preparedStatement.setBytes(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
		 */
		public void setDate(int parameterIndex, Date x) throws SQLException {
			this.preparedStatement.setDate(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
		 */
		public void setTime(int parameterIndex, Time x) throws SQLException {
			this.preparedStatement.setTime(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
		 */
		public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
			this.preparedStatement.setTimestamp(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
		 */
		public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
			this.preparedStatement.setAsciiStream(parameterIndex, x, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
		 */
		@Deprecated
		public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
			this.preparedStatement.setUnicodeStream(parameterIndex, x, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
		 */
		public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
			this.preparedStatement.setBinaryStream(parameterIndex, x, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#clearParameters()
		 */
		public void clearParameters() throws SQLException {
			this.preparedStatement.clearParameters();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
		 */
		public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
			this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
		 */
		public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
			this.preparedStatement.setObject(parameterIndex, x, targetSqlType);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
		 */
		public void setObject(int parameterIndex, Object x) throws SQLException {
			this.preparedStatement.setObject(parameterIndex, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#execute()
		 */
		public boolean execute() throws SQLException {
			return this.preparedStatement.execute();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#addBatch()
		 */
		public void addBatch() throws SQLException {
			this.preparedStatement.addBatch();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
		 */
		public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
			this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
		 */
		public void setRef(int i, Ref x) throws SQLException {
			this.preparedStatement.setRef(i, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
		 */
		public void setBlob(int i, Blob x) throws SQLException {
			this.preparedStatement.setBlob(i, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
		 */
		public void setClob(int i, Clob x) throws SQLException {
			this.preparedStatement.setClob(i, x);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
		 */
		public void setArray(int i, Array x) throws SQLException {
			this.preparedStatement.setArray(i, x);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#getMetaData()
		 */
		public ResultSetMetaData getMetaData() throws SQLException {
			return this.preparedStatement.getMetaData();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
		 */
		public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
			this.preparedStatement.setDate(parameterIndex, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
		 */
		public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
			this.preparedStatement.setTime(parameterIndex, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
		 */
		public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
			this.preparedStatement.setTimestamp(parameterIndex, x, cal);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
		 */
		public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
			this.preparedStatement.setNull(paramIndex, sqlType, typeName);	
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
		 */
		public void setURL(int parameterIndex, URL x) throws SQLException {
			this.preparedStatement.setURL(parameterIndex, x);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#getParameterMetaData()
		 */
		public ParameterMetaData getParameterMetaData() throws SQLException {
			return this.preparedStatement.getParameterMetaData();
		}
		
		private PreparedStatement preparedStatement;
		
	}
	
	/**
	 * Wraps an instance of the ResultSet interface for use within a connection pool.
	 * 
	 * Note that this wrapper does not actually pool or otherwise manage the underlying result set.
	 * The only purpose of this class is to prevent unintended access to the underlying statement 
	 * through a call to @see {@link java.sql.ResultSet#getStatement()}.
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledResultSet implements ResultSet {

		/**
		 * Initializes a new instance of the PooledResultSet class.
		 * @param pooledStatement the pooled statement that produced the pooled result set.
		 * @param resultSet the result set wrapped by the pooled result set.
		 */
		public PooledResultSet(PooledStatement pooledStatement, ResultSet resultSet) {
			this.pooledStatement = pooledStatement;
			this.resultSet = resultSet; 
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#next()
		 */
		public boolean next() throws SQLException {
			return this.resultSet.next();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#close()
		 */
		public void close() throws SQLException {
			this.resultSet.close();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#wasNull()
		 */
		public boolean wasNull() throws SQLException {
			return this.resultSet.wasNull();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getString(int)
		 */
		public String getString(int columnIndex) throws SQLException {
			return this.resultSet.getString(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBoolean(int)
		 */
		public boolean getBoolean(int columnIndex) throws SQLException {
			return this.resultSet.getBoolean(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getByte(int)
		 */
		public byte getByte(int columnIndex) throws SQLException {
			return this.resultSet.getByte(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getShort(int)
		 */
		public short getShort(int columnIndex) throws SQLException {
			return this.resultSet.getShort(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getInt(int)
		 */
		public int getInt(int columnIndex) throws SQLException {
			return this.resultSet.getInt(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getLong(int)
		 */
		public long getLong(int columnIndex) throws SQLException {
			return this.resultSet.getLong(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getFloat(int)
		 */
		public float getFloat(int columnIndex) throws SQLException {
			return this.resultSet.getFloat(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDouble(int)
		 */
		public double getDouble(int columnIndex) throws SQLException {
			return this.resultSet.getDouble(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBigDecimal(int, int)
		 */
		@Deprecated
		public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
			return this.resultSet.getBigDecimal(columnIndex, scale);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBytes(int)
		 */
		public byte[] getBytes(int columnIndex) throws SQLException {
			return this.resultSet.getBytes(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDate(int)
		 */
		public Date getDate(int columnIndex) throws SQLException {
			return this.resultSet.getDate(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTime(int)
		 */
		public Time getTime(int columnIndex) throws SQLException {
			return this.resultSet.getTime(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTimestamp(int)
		 */
		public Timestamp getTimestamp(int columnIndex) throws SQLException {
			return this.resultSet.getTimestamp(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getAsciiStream(int)
		 */
		public InputStream getAsciiStream(int columnIndex) throws SQLException {
			return this.resultSet.getAsciiStream(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getUnicodeStream(int)
		 */
		@Deprecated
		public InputStream getUnicodeStream(int columnIndex) throws SQLException {
			return this.resultSet.getUnicodeStream(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBinaryStream(int)
		 */
		public InputStream getBinaryStream(int columnIndex) throws SQLException {
			return this.resultSet.getBinaryStream(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getString(java.lang.String)
		 */
		public String getString(String columnName) throws SQLException {
			return this.resultSet.getString(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBoolean(java.lang.String)
		 */
		public boolean getBoolean(String columnName) throws SQLException {
			return this.resultSet.getBoolean(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getByte(java.lang.String)
		 */
		public byte getByte(String columnName) throws SQLException {
			return this.resultSet.getByte(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getShort(java.lang.String)
		 */
		public short getShort(String columnName) throws SQLException {
			return this.resultSet.getShort(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getInt(java.lang.String)
		 */
		public int getInt(String columnName) throws SQLException {
			return this.resultSet.getInt(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getLong(java.lang.String)
		 */
		public long getLong(String columnName) throws SQLException {
			return this.resultSet.getLong(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getFloat(java.lang.String)
		 */
		public float getFloat(String columnName) throws SQLException {
			return this.resultSet.getFloat(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDouble(java.lang.String)
		 */
		public double getDouble(String columnName) throws SQLException {
			return this.resultSet.getDouble(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
		 */
		@Deprecated
		public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
			return this.resultSet.getBigDecimal(columnName, scale);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBytes(java.lang.String)
		 */
		public byte[] getBytes(String columnName) throws SQLException {
			return this.resultSet.getBytes(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDate(java.lang.String)
		 */
		public Date getDate(String columnName) throws SQLException {
			return this.resultSet.getDate(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTime(java.lang.String)
		 */
		public Time getTime(String columnName) throws SQLException {
			return this.resultSet.getTime(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
		 */
		public Timestamp getTimestamp(String columnName) throws SQLException {
			return this.resultSet.getTimestamp(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
		 */
		public InputStream getAsciiStream(String columnName) throws SQLException {
			return this.resultSet.getAsciiStream(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
		 */
		@Deprecated
		public InputStream getUnicodeStream(String columnName) throws SQLException {
			return this.resultSet.getUnicodeStream(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
		 */
		public InputStream getBinaryStream(String columnName) throws SQLException {
			return this.resultSet.getBinaryStream(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getWarnings()
		 */
		public SQLWarning getWarnings() throws SQLException {
			return this.resultSet.getWarnings();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#clearWarnings()
		 */
		public void clearWarnings() throws SQLException {
			this.resultSet.clearWarnings();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getCursorName()
		 */
		public String getCursorName() throws SQLException {
			return this.resultSet.getCursorName();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getMetaData()
		 */
		public ResultSetMetaData getMetaData() throws SQLException {
			return this.resultSet.getMetaData();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getObject(int)
		 */
		public Object getObject(int columnIndex) throws SQLException {
			return this.resultSet.getObject(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getObject(java.lang.String)
		 */
		public Object getObject(String columnName) throws SQLException {
			return this.resultSet.getObject(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#findColumn(java.lang.String)
		 */
		public int findColumn(String columnName) throws SQLException {
			return this.resultSet.findColumn(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getCharacterStream(int)
		 */
		public Reader getCharacterStream(int columnIndex) throws SQLException {
			return this.resultSet.getCharacterStream(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
		 */
		public Reader getCharacterStream(String columnName) throws SQLException {
			return this.resultSet.getCharacterStream(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBigDecimal(int)
		 */
		public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
			return this.resultSet.getBigDecimal(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
		 */
		public BigDecimal getBigDecimal(String columnName) throws SQLException {
			return this.resultSet.getBigDecimal(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#isBeforeFirst()
		 */
		public boolean isBeforeFirst() throws SQLException {
			return this.resultSet.isBeforeFirst();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#isAfterLast()
		 */
		public boolean isAfterLast() throws SQLException {
			return this.resultSet.isAfterLast();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#isFirst()
		 */
		public boolean isFirst() throws SQLException {
			return this.resultSet.isFirst();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#isLast()
		 */
		public boolean isLast() throws SQLException {
			return this.resultSet.isLast();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#beforeFirst()
		 */
		public void beforeFirst() throws SQLException {
			this.resultSet.beforeFirst();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#afterLast()
		 */
		public void afterLast() throws SQLException {
			this.resultSet.afterLast();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#first()
		 */
		public boolean first() throws SQLException {
			return this.resultSet.first();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#last()
		 */
		public boolean last() throws SQLException {
			return this.resultSet.last();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getRow()
		 */
		public int getRow() throws SQLException {
			return this.resultSet.getRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#absolute(int)
		 */
		public boolean absolute(int row) throws SQLException {
			return this.resultSet.absolute(row);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#relative(int)
		 */
		public boolean relative(int rows) throws SQLException {
			return this.resultSet.relative(rows);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#previous()
		 */
		public boolean previous() throws SQLException {
			return this.resultSet.previous();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#setFetchDirection(int)
		 */
		public void setFetchDirection(int direction) throws SQLException {
			this.resultSet.setFetchDirection(direction);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getFetchDirection()
		 */
		public int getFetchDirection() throws SQLException {
			return this.resultSet.getFetchDirection();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#setFetchSize(int)
		 */
		public void setFetchSize(int rows) throws SQLException {
			this.resultSet.setFetchSize(rows);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getFetchSize()
		 */
		public int getFetchSize() throws SQLException {
			return this.resultSet.getFetchSize();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getType()
		 */
		public int getType() throws SQLException {
			return this.resultSet.getType();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getConcurrency()
		 */
		public int getConcurrency() throws SQLException {
			return this.resultSet.getConcurrency();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#rowUpdated()
		 */
		public boolean rowUpdated() throws SQLException {
			return this.resultSet.rowUpdated();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#rowInserted()
		 */
		public boolean rowInserted() throws SQLException {
			return this.resultSet.rowInserted();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#rowDeleted()
		 */
		public boolean rowDeleted() throws SQLException {
			return this.resultSet.rowDeleted();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateNull(int)
		 */
		public void updateNull(int columnIndex) throws SQLException {
			this.resultSet.updateNull(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBoolean(int, boolean)
		 */
		public void updateBoolean(int columnIndex, boolean x) throws SQLException {
			this.resultSet.updateBoolean(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateByte(int, byte)
		 */
		public void updateByte(int columnIndex, byte x) throws SQLException {
			this.resultSet.updateByte(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateShort(int, short)
		 */
		public void updateShort(int columnIndex, short x) throws SQLException {
			this.resultSet.updateShort(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateInt(int, int)
		 */
		public void updateInt(int columnIndex, int x) throws SQLException {
			this.resultSet.updateInt(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateLong(int, long)
		 */
		public void updateLong(int columnIndex, long x) throws SQLException {
			this.resultSet.updateLong(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateFloat(int, float)
		 */
		public void updateFloat(int columnIndex, float x) throws SQLException {
			this.resultSet.updateFloat(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateDouble(int, double)
		 */
		public void updateDouble(int columnIndex, double x) throws SQLException {
			this.resultSet.updateDouble(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
		 */
		public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
			this.resultSet.updateBigDecimal(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateString(int, java.lang.String)
		 */
		public void updateString(int columnIndex, String x) throws SQLException {
			this.resultSet.updateString(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBytes(int, byte[])
		 */
		public void updateBytes(int columnIndex, byte[] x) throws SQLException {
			this.resultSet.updateBytes(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
		 */
		public void updateDate(int columnIndex, Date x) throws SQLException {
			this.resultSet.updateDate(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
		 */
		public void updateTime(int columnIndex, Time x) throws SQLException {
			this.resultSet.updateTime(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
		 */
		public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
			this.resultSet.updateTimestamp(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
		 */
		public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
			this.resultSet.updateAsciiStream(columnIndex, x, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
		 */
		public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
			this.resultSet.updateBinaryStream(columnIndex, x, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
		 */
		public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
			this.resultSet.updateCharacterStream(columnIndex, x, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
		 */
		public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
			this.resultSet.updateObject(columnIndex, x, scale);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
		 */
		public void updateObject(int columnIndex, Object x) throws SQLException {
			this.resultSet.updateObject(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateNull(java.lang.String)
		 */
		public void updateNull(String columnName) throws SQLException {
			this.resultSet.updateNull(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
		 */
		public void updateBoolean(String columnName, boolean x) throws SQLException {
			this.resultSet.updateBoolean(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
		 */
		public void updateByte(String columnName, byte x) throws SQLException {
			this.resultSet.updateByte(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
		 */
		public void updateShort(String columnName, short x) throws SQLException {
			this.resultSet.updateShort(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
		 */
		public void updateInt(String columnName, int x) throws SQLException {
			this.resultSet.updateInt(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
		 */
		public void updateLong(String columnName, long x) throws SQLException {
			this.resultSet.updateLong(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
		 */
		public void updateFloat(String columnName, float x) throws SQLException {
			this.resultSet.updateFloat(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
		 */
		public void updateDouble(String columnName, double x) throws SQLException {
			this.resultSet.updateDouble(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
		 */
		public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
			this.resultSet.updateBigDecimal(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
		 */
		public void updateString(String columnName, String x) throws SQLException {
			this.resultSet.updateString(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
		 */
		public void updateBytes(String columnName, byte[] x) throws SQLException {
			this.resultSet.updateBytes(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
		 */
		public void updateDate(String columnName, Date x) throws SQLException {
			this.resultSet.updateDate(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
		 */
		public void updateTime(String columnName, Time x) throws SQLException {
			this.resultSet.updateTime(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
		 */
		public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
			this.resultSet.updateTimestamp(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
		 */
		public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
			this.resultSet.updateAsciiStream(columnName, x, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
		 */
		public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
			this.resultSet.updateBinaryStream(columnName, x, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
		 */
		public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
			this.resultSet.updateCharacterStream(columnName, reader, length);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
		 */
		public void updateObject(String columnName, Object x, int scale) throws SQLException {
			this.resultSet.updateObject(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
		 */
		public void updateObject(String columnName, Object x) throws SQLException {
			this.resultSet.updateObject(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#insertRow()
		 */
		public void insertRow() throws SQLException {
			this.resultSet.insertRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateRow()
		 */
		public void updateRow() throws SQLException {
			this.resultSet.updateRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#deleteRow()
		 */
		public void deleteRow() throws SQLException {
			this.resultSet.deleteRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#refreshRow()
		 */
		public void refreshRow() throws SQLException {
			this.resultSet.refreshRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#cancelRowUpdates()
		 */
		public void cancelRowUpdates() throws SQLException {
			this.resultSet.cancelRowUpdates();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#moveToInsertRow()
		 */
		public void moveToInsertRow() throws SQLException {
			this.resultSet.moveToInsertRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#moveToCurrentRow()
		 */
		public void moveToCurrentRow() throws SQLException {
			this.resultSet.moveToCurrentRow();
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getStatement()
		 */
		public Statement getStatement() throws SQLException {
			return this.pooledStatement;
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getObject(int, java.util.Map)
		 */
		public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
			return this.resultSet.getObject(i, map);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getRef(int)
		 */
		public Ref getRef(int i) throws SQLException {
			return this.resultSet.getRef(i);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBlob(int)
		 */
		public Blob getBlob(int i) throws SQLException {
			return this.resultSet.getBlob(i);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getClob(int)
		 */
		public Clob getClob(int i) throws SQLException {
			return this.resultSet.getClob(i);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getArray(int)
		 */
		public Array getArray(int i) throws SQLException {
			return this.resultSet.getArray(i);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
		 */
		public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
			return this.resultSet.getObject(colName, map);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getRef(java.lang.String)
		 */
		public Ref getRef(String colName) throws SQLException {
			return this.resultSet.getRef(colName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getBlob(java.lang.String)
		 */
		public Blob getBlob(String colName) throws SQLException {
			return this.resultSet.getBlob(colName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getClob(java.lang.String)
		 */
		public Clob getClob(String colName) throws SQLException {
			return this.resultSet.getClob(colName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getArray(java.lang.String)
		 */
		public Array getArray(String colName) throws SQLException {
			return this.resultSet.getArray(colName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
		 */
		public Date getDate(int columnIndex, Calendar cal) throws SQLException {
			return this.resultSet.getDate(columnIndex, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
		 */
		public Date getDate(String columnName, Calendar cal) throws SQLException {
			return this.resultSet.getDate(columnName, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
		 */
		public Time getTime(int columnIndex, Calendar cal) throws SQLException {
			return this.resultSet.getTime(columnIndex, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
		 */
		public Time getTime(String columnName, Calendar cal) throws SQLException {
			return this.resultSet.getTime(columnName, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
		 */
		public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
			return this.resultSet.getTimestamp(columnIndex, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
		 */
		public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
			return this.resultSet.getTimestamp(columnName, cal);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getURL(int)
		 */
		public URL getURL(int columnIndex) throws SQLException {
			return this.resultSet.getURL(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getURL(java.lang.String)
		 */
		public URL getURL(String columnName) throws SQLException {
			return this.resultSet.getURL(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
		 */
		public void updateRef(int columnIndex, Ref x) throws SQLException {
			this.resultSet.updateRef(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
		 */
		public void updateRef(String columnName, Ref x) throws SQLException {
			this.resultSet.updateRef(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
		 */
		public void updateBlob(int columnIndex, Blob x) throws SQLException {
			this.resultSet.updateBlob(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
		 */
		public void updateBlob(String columnName, Blob x) throws SQLException {
			this.resultSet.updateBlob(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
		 */
		public void updateClob(int columnIndex, Clob x) throws SQLException {
			this.resultSet.updateClob(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
		 */
		public void updateClob(String columnName, Clob x) throws SQLException {
			this.resultSet.updateClob(columnName, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
		 */
		public void updateArray(int columnIndex, Array x) throws SQLException {
			this.resultSet.updateArray(columnIndex, x);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
		 */
		public void updateArray(String columnName, Array x) throws SQLException {
			this.resultSet.updateArray(columnName, x);
		}
		
		private PooledStatement pooledStatement;
		private ResultSet resultSet;
		
	}
	
	/**
	 * Wraps an instance of the Statement interface for use within a connection pool. 
	 * 
	 * Note that this wrapper does not actually pool the underlying statement but it does
	 * attempt to close the underlying statement if the corresponding connection is released.
	 * However, it is recommended that unused resources be closed when no longer in use.
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledStatement implements PooledConnectionListener, Statement {
		
		/**
		 * Initializes a new instance of the PooledStatement class.
		 * @param pooledConnection the pooled connection for the pooled statement.
		 * @param statement the statement wrapped by the pooled statement.
		 */
		public PooledStatement(PooledConnection pooledConnection, Statement statement) throws SQLException {
			this.statement = statement;
			this.pooledConnection = pooledConnection;
			this.pooledConnection.addPooledConnectionListener(this);
			if (this.pooledConnection.isClosed()) {
				this.close();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.opower.connectionpool.ConnectionPoolImpl.PooledConnectionListener#pooledConnectionClosed()
		 */
		public void pooledConnectionClosed() {
			try {
				this.close();
			} catch (SQLException e) {

			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeQuery(java.lang.String)
		 */
		public ResultSet executeQuery(String sql) throws SQLException {
			return new PooledResultSet(this, this.statement.executeQuery(sql));
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeUpdate(java.lang.String)
		 */
		public int executeUpdate(String sql) throws SQLException {
			return this.statement.executeUpdate(sql);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#close()
		 */
		public void close() throws SQLException {
			this.statement.close();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getMaxFieldSize()
		 */
		public int getMaxFieldSize() throws SQLException {
			return this.statement.getMaxFieldSize();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setMaxFieldSize(int)
		 */
		public void setMaxFieldSize(int max) throws SQLException {
			this.statement.setMaxFieldSize(max);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getMaxRows()
		 */
		public int getMaxRows() throws SQLException {
			return this.statement.getMaxRows();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setMaxRows(int)
		 */
		public void setMaxRows(int max) throws SQLException {
			this.statement.setMaxRows(max);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setEscapeProcessing(boolean)
		 */
		public void setEscapeProcessing(boolean enable) throws SQLException {
			this.statement.setEscapeProcessing(enable);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getQueryTimeout()
		 */
		public int getQueryTimeout() throws SQLException {
			return this.statement.getQueryTimeout();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setQueryTimeout(int)
		 */
		public void setQueryTimeout(int seconds) throws SQLException {
			this.statement.setQueryTimeout(seconds);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#cancel()
		 */
		public void cancel() throws SQLException {
			this.statement.cancel();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getWarnings()
		 */
		public SQLWarning getWarnings() throws SQLException {
			return this.statement.getWarnings();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#clearWarnings()
		 */
		public void clearWarnings() throws SQLException {
			this.statement.clearWarnings();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setCursorName(java.lang.String)
		 */
		public void setCursorName(String name) throws SQLException {
			this.statement.setCursorName(name);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#execute(java.lang.String)
		 */
		public boolean execute(String sql) throws SQLException {
			return this.statement.execute(sql);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getResultSet()
		 */
		public ResultSet getResultSet() throws SQLException {
			return new PooledResultSet(this, this.statement.getResultSet());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getUpdateCount()
		 */
		public int getUpdateCount() throws SQLException {
			return this.statement.getUpdateCount();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getMoreResults()
		 */
		public boolean getMoreResults() throws SQLException {
			return this.statement.getMoreResults();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setFetchDirection(int)
		 */
		public void setFetchDirection(int direction) throws SQLException {
			this.statement.setFetchDirection(direction);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getFetchDirection()
		 */
		public int getFetchDirection() throws SQLException {
			return this.statement.getFetchDirection();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#setFetchSize(int)
		 */
		public void setFetchSize(int rows) throws SQLException {
			this.statement.setFetchSize(rows);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getFetchSize()
		 */
		public int getFetchSize() throws SQLException {
			return this.statement.getFetchSize();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getResultSetConcurrency()
		 */
		public int getResultSetConcurrency() throws SQLException {
			return this.statement.getResultSetConcurrency();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getResultSetType()
		 */
		public int getResultSetType() throws SQLException {
			return this.statement.getResultSetType();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#addBatch(java.lang.String)
		 */
		public void addBatch(String sql) throws SQLException {
			this.statement.addBatch(sql);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#clearBatch()
		 */
		public void clearBatch() throws SQLException {
			this.statement.clearBatch();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeBatch()
		 */
		public int[] executeBatch() throws SQLException {
			return this.statement.executeBatch();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			return this.pooledConnection;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getMoreResults(int)
		 */
		public boolean getMoreResults(int current) throws SQLException {
			return this.statement.getMoreResults();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getGeneratedKeys()
		 */
		public ResultSet getGeneratedKeys() throws SQLException {
			return new PooledResultSet(this, this.statement.getGeneratedKeys());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
		 */
		public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
			return this.statement.executeUpdate(sql, autoGeneratedKeys);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
		 */
		public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
			return this.statement.executeUpdate(sql, columnIndexes);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
		 */
		public int executeUpdate(String sql, String[] columnNames) throws SQLException {
			return this.statement.executeUpdate(sql, columnNames);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#execute(java.lang.String, int)
		 */
		public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
			return this.statement.execute(sql, autoGeneratedKeys);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#execute(java.lang.String, int[])
		 */
		public boolean execute(String sql, int[] columnIndexes) throws SQLException {
			return this.statement.execute(sql, columnIndexes);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
		 */
		public boolean execute(String sql, String[] columnNames) throws SQLException {
			return this.statement.execute(sql, columnNames);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getResultSetHoldability()
		 */
		public int getResultSetHoldability() throws SQLException {
			return this.statement.getResultSetHoldability();
		}

		private PooledConnection pooledConnection;
		private Statement statement;
		
		
	}
		
}
