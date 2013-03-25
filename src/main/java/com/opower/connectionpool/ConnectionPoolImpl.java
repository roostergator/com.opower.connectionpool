package com.opower.connectionpool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EventListener;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;
import javax.swing.event.EventListenerList;

/**
 * Provides an implementation of the ConnectionPool interface.
 * <p>
 * This implementation supports the definition of minimum and maximum bounds for the number of 
 * connections maintained by the connection pool.  While the maximum bounds will not be exceeded,
 * is is possible for the minimum bounds to be under run if the underlying data source fails to
 * return a connection in response to a call to {@link javax.sql.DataSource#getConnection()}.
 * <p>
 * Additionally, the connection pool can attempt to recover connections that have been idle for 
 * some duration.  A connection is considered idle if no method calls have been made against the 
 * public interface for a defined period of time.  A recovered connection is no longer available 
 * for use and any call to the public interface (except {@link java.sql.Connection#isClosed()})
 * will throw an exception. Also, an attempt will be made to close any CallableStatement, 
 * PreparedStatement and Statement instances that have been retrieved using the connection.
 * <p>
 * Lastly, the pool will attempt to close any open CallableStatements, PreparedStatements and 
 * Statements for recovered or release connections. However, it is recommended that all resources
 * be closed by consumers once no longer in use.
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
	
	/** 
	 * Gets a connection from this connection pool. This method will throw an exception if any of
	 * the following are true:
	 * 
	 * <ul>
	 * <li>The connection pool does not have any unused connections and creating a new connection
	 * would exceed the {@link #getMaximumConnections} property.
	 * <li>The connection pool does not have any unused connections and creating a new connection
	 * would not exceed the {@link #getMaximumConnections} property and the data source throws an 
	 * exception when trying to create a new connection.
	 * <li>
	 * </ul>
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
	
	/**
	 * Releases a connection back to this connection pool.  This method will throw an exception if
	 * any of the following are true:
	 * 
	 * <ul>
	 * <li>The connection is null.
	 * <li>The connection did not originate from the connection pool.
	 * </ul>
	 * 
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
	 * <p>
	 * Note that this wrapper does not actually pool the underlying callable statement. The only
	 * additional functionality is as follows:
	 * 
	 * <ul>
	 * <li>Attempts to close the underlying callable statement if the corresponding connection is
	 * released. However, it is recommended that unused resources be closed when no longer in use.
	 * <li>Prevents direct access to the physical connection through calls to {@link #getConnection()}.
	 * </ul>
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledCallableStatement extends WrappedCallableStatement implements PooledConnectionListener {
		
		/**
		 * Initializes a new instance of the PooledCallableStatement class.
		 * @param pooledConnection The pooled connection for the pooled callable statement.
		 * @param callableStatement the callable statement for the pooled callable statement.
		 */
		public PooledCallableStatement(PooledConnection pooledConnection, CallableStatement callableStatement) throws SQLException {
			super(callableStatement);
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
				e.printStackTrace();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#executeQuery()
		 */
		public ResultSet executeQuery() throws SQLException {
			return new PooledResultSet(this, this.getCallableStatement().executeQuery());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#executeQuery(java.lang.String)
		 */
		public ResultSet executeQuery(String sql) throws SQLException {
			return new PooledResultSet(this, this.getCallableStatement().executeQuery(sql));
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getResultSet()
		 */
		public ResultSet getResultSet() throws SQLException {
			return new PooledResultSet(this, this.getCallableStatement().getResultSet());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			return this.pooledConnection;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.CallableStatement#getGeneratedKeys()
		 */
		public ResultSet getGeneratedKeys() throws SQLException {
			return new PooledResultSet(this, this.getCallableStatement().getGeneratedKeys());
		}
		
		private PooledConnection pooledConnection;
		
	}
	
	/**
	 * Wraps an instance of the Connection interface for use within a connection pool.
	 * 
	 * While this wrapper delegates most methods to the underlying connection, the following methods
	 * have custom functionality:
	 * 
	 * <ul>
	 * <li> @see com.opower.connectionpool.ConnectionPoolImpl.PooledConnection#close - Releases the 
	 * underlying connection to the connection pool and so that is is no longer available from the 
	 * pooled connection.
	 * <li> @see com.opower.connectionpool.ConnectionPoolImpl.PooledConnection#isClosed - Indicates 
	 * whether the underlying connection is closed or has been released to the connection pool. 
	 * </ul>
	 * 
	 * Additionally, any method that returns the following types wraps the return value from the 
	 * underlying connection in another wrapper to prevent direct access to the underlying connection:
	 * 
	 * <ul>
	 * <li> @see java.sql.DatabaseMetaData - Wrapped in a {@link PooledDatabaseMetaData}.
	 * <li> @see java.sql.CallableStatement - Wrapped in a {@link PooledCallableStatement}.
	 * <li> @see java.sql.PreparedStatement - Wrapped in a {@link PooledPreparedStatement}.
	 * <li> @see java.sql.ResultSet - Wrapped in a {@link PooledResultSet}.
	 * <li> @see java.sql.Statement - Wrapped in a {@link PooledStatement}.
	 * </ul>
	 * 
	 * Lastly, this implementation supports pooled connection listeners and notifies them when the 
	 * connection is closed (i.e., released to the connection pool).
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledConnection extends WrappedConnection {
		
		/**
		 * Initializes a new instance of the PooledConnection class.
		 * @param connection The connection wrapped by the pooled connection.
		 * @param timeout The number of milliseconds before the idle connection is automatically released to the connection pool. This value must be greater than or equal to zero.  A value of zero will disable automatic timeouts.
		 */
		public PooledConnection(Connection connection, long timeout) {
			super(connection);
			this.last = new AtomicLong(System.currentTimeMillis());
			this.released = new AtomicBoolean(false);
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
		private ConnectionPool getConnectionPool() {
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
			return new PooledStatement(this, this.getConnection().createStatement());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String)
		 */
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String)
		 */
		public CallableStatement prepareCall(String sql) throws SQLException {
			return new PooledCallableStatement(this, this.getConnection().prepareCall(sql));
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
			Connection connection = this.getUncheckedConnection();
			return (this.released.get() || connection == null || connection.isClosed());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#getMetaData()
		 */
		public DatabaseMetaData getMetaData() throws SQLException {
			return new PooledDatabaseMetaData(this, this.getConnection().getMetaData());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#createStatement(int, int)
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledStatement(this, this.getConnection().createStatement(resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return new PooledCallableStatement(this, this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#createStatement(int, int, int)
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledStatement(this, this.getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return new PooledCallableStatement(this, this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
		 */
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql, autoGeneratedKeys));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
		 */
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql, columnIndexes));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
		 */
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return new PooledPreparedStatement(this, this.getConnection().prepareStatement(sql, columnNames));
		}
		
		private AtomicLong last;
		private AtomicBoolean released;
		private long timeout;
		private Timer timer;
		private EventListenerList listeners;
		
		/**
		 * Gets the connection wrapped by the pooled connection or throws an exception if the 
		 * connection has been released to the connection pool.
		 * @return The connection wrapped by the pool.
		 */
		protected Connection getConnection() {
			this.last.set(System.currentTimeMillis());
			Connection connection = this.getUncheckedConnection();
			if (this.released.get()) {
				throw new IllegalStateException("The connection has already been released to the connection pool."); 
			}
			return connection;
		}
		
		/**
		 * Gets the connection wrapped by the pooled connection or null if the connection has
		 * been released to the connection pool.
		 * @return The connection wrapped by the pool.
		 */
		protected Connection getUncheckedConnection() {
			return super.getConnection();
		}
		
		/**
		 * Releases underlying connection from the pooled connection and returns its value.
		 * @return The underlying connection;
		 */
		private Connection release() {
			Connection connection = null;
			if (!this.released.getAndSet(true)) {
				connection = this.getUncheckedConnection();
				this.setConnection(null);
				PooledConnectionListener[] listeners = this.listeners.getListeners(PooledConnectionListener.class);
				for (PooledConnectionListener listener : listeners) {
					listener.pooledConnectionClosed();
				}
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
				long delay = connection.timeout - (System.currentTimeMillis() - connection.last.get());
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
	 * <p>
	 * Note that this wrapper does not actually pool the underlying database meta data. The only
	 * additional functionality is as follows:
	 * 
	 * <ul>
	 * <li>Prevents direct access to the physical connection through calls to {@link #getConnection()}.
	 * </ul>
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledDatabaseMetaData extends WrappedDatabaseMetaData {

		/**
		 * Initializes a new instance of the PooledDatabaseMetaData class.
		 * @param pooledConnection The pooled connection for the pooled database meta data.
		 * @param databaseMetaData the database meta data for the pooled database meta data.
		 */
		public PooledDatabaseMetaData(PooledConnection pooledConnection, DatabaseMetaData databaseMetaData) {
			super(databaseMetaData);
			this.pooledConnection = pooledConnection;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getProcedures(catalog, schemaPattern, procedureNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
		 */
		public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getTables(catalog, schemaPattern, tableNamePattern, types));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSchemas()
		 */
		public ResultSet getSchemas() throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getSchemas());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCatalogs()
		 */
		public ResultSet getCatalogs() throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getCatalogs());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTableTypes()
		 */
		public ResultSet getTableTypes() throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getTableTypes());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getColumnPrivileges(catalog, schema, table, columnNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getTablePrivileges(String catalog,	String schemaPattern, String tableNamePattern) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getTablePrivileges(catalog, schemaPattern, tableNamePattern));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
		 */
		public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getBestRowIdentifier(catalog, schema, table, scope, nullable));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getVersionColumns(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getPrimaryKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getImportedKeys(String catalog, String schema,	String table) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getImportedKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getExportedKeys(String catalog, String schema,	String table) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getExportedKeys(catalog, schema, table));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getCrossReference(primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getTypeInfo()
		 */
		public ResultSet getTypeInfo() throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getTypeInfo());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
		 */
		public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getIndexInfo(catalog, schema, table, unique, approximate));
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
		 */
		public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
			return new PooledResultSet(null, this.getDatabaseMetaData().getUDTs(catalog, schemaPattern, typeNamePattern, types));
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
		 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
			return this.getDatabaseMetaData().getSuperTypes(catalog, schemaPattern, typeNamePattern);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
			return this.getDatabaseMetaData().getSuperTables(catalog, schemaPattern, tableNamePattern);
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
			return this.getDatabaseMetaData().getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
		}
		
		private PooledConnection pooledConnection;
		
	}
	
	/**
	 * Wraps an instance of the PreparedStatement interface for use within a connection pool. 
	 * <p>
	 * Note that this wrapper does not actually pool the underlying prepared statement. The only
	 * additional functionality is as follows:
	 * 
	 * <ul>
	 * <li>Attempts to close the underlying prepared statement if the corresponding connection is
	 * released. However, it is recommended that unused resources be closed when no longer in use.
	 * <li>Prevents direct access to the physical connection through calls to {@link #getConnection()}.
	 * </ul>
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledPreparedStatement extends WrappedPreparedStatement implements PooledConnectionListener {

		/**
		 * Initializes a new instance of the PooledPreparedStatement class.
		 * @param pooledConnection The pooled connection for the pooled prepared statement.
		 * @param preparedStatement The prepared statement wrapped by the pooled prepared statement.
		 */
		public PooledPreparedStatement(PooledConnection pooledConnection, PreparedStatement preparedStatement) throws SQLException {
			super(preparedStatement);
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
				e.printStackTrace();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#executeQuery()
		 */
		public ResultSet executeQuery() throws SQLException {
			return new PooledResultSet(this, this.getPreparedStatement().executeQuery());
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#executeQuery(java.lang.String)
		 */
		public ResultSet executeQuery(String sql) throws SQLException {
			return new PooledResultSet(this, this.getPreparedStatement().executeQuery(sql));
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#getResultSet()
		 */
		public ResultSet getResultSet() throws SQLException {
			return new PooledResultSet(this, this.getPreparedStatement().getResultSet());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			return this.pooledConnection;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.PreparedStatement#getGeneratedKeys()
		 */
		public ResultSet getGeneratedKeys() throws SQLException {
			return new PooledResultSet(this, this.getPreparedStatement().getGeneratedKeys());
		}
		
		private PooledConnection pooledConnection;
		
	}
	
	/**
	 * Wraps an instance of the ResultSet interface for use within a connection pool. 
	 * <p>
	 * Note that this wrapper does not actually pool the underlying result set. The only additional
	 * functionality is as follows:
	 * 
	 * <ul>
	 * <li>Prevents direct access to the physical statement through calls to {@link #getStatement()}.
	 * </ul>
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledResultSet extends WrappedResultSet {

		/**
		 * Initializes a new instance of the PooledResultSet class.
		 * @param statement The statement to be returned for calls to {@link #getStatement()}.
		 * @param resultSet The result set wrapped by the pooled result set.
		 */
		public PooledResultSet(Statement statement, ResultSet resultSet) {
			super(resultSet);
			this.statement = statement; 
		}

		/*
		 * (non-Javadoc)
		 * @see java.sql.ResultSet#getStatement()
		 */
		public Statement getStatement() throws SQLException {
			return this.statement;
		}
		
		private Statement statement;
		
	}
	
	/**
	 * Wraps an instance of the Statement interface for use within a connection pool. 
	 * <p>
	 * Note that this wrapper does not actually pool the underlying statement. The only additional
	 * functionality is as follows:
	 * 
	 * <ul>
	 * <li>Attempts to close the underlying statement if the corresponding connection is released. 
	 * However, it is recommended that unused resources be closed when no longer in use.
	 * <li>Prevents direct access to the physical connection through calls to {@link #getConnection()}.
	 * Instead a {@link com.opower.connectionpool.ConnectionPoolImpl.PooledConnection} is returned.
	 * </ul>
	 * 
	 * @see com.opower.connectionpool.ConnectionPoolImpl
	 */
	private class PooledStatement extends WrappedStatement implements PooledConnectionListener {
		
		/**
		 * Initializes a new instance of the PooledStatement class.
		 * @param pooledConnection The pooled connection for the pooled statement.
		 * @param statement The statement wrapped by the pooled statement.
		 * @throws SQLException Thrown if a data base error occurs.
		 */
		public PooledStatement(PooledConnection pooledConnection, Statement statement) throws SQLException {
			super(statement);
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
				e.printStackTrace();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#executeQuery(java.lang.String)
		 */
		public ResultSet executeQuery(String sql) throws SQLException {
			return new PooledResultSet(this, this.getStatement().executeQuery(sql));
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.sql.Statement#getResultSet()
		 */
		public ResultSet getResultSet() throws SQLException {
			return new PooledResultSet(this, this.getStatement().getResultSet());
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
		 * @see java.sql.Statement#getGeneratedKeys()
		 */
		public ResultSet getGeneratedKeys() throws SQLException {
			return new PooledResultSet(this, this.getStatement().getGeneratedKeys());
		}

		private PooledConnection pooledConnection;
		
	}
		
}
