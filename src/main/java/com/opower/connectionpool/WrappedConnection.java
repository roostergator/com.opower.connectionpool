package com.opower.connectionpool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * Wraps an instance of the @see java.sql.Connection interface. This implementation delegates 
 * all calls to the wrapped connection and provides no additional functionality.  It is intended
 * to serve as a starting point for classes that need to customize the behavior of existing 
 * connection instances.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedConnection implements Connection {

	/**
	 * Initializes a new instance of the WrappedConnection class.
	 * @param connection The connection wrapped by the wrapped connection.
	 */
	public WrappedConnection(Connection connection) {
		this.connection = connection;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		return this.getConnection().createStatement();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.getConnection().prepareStatement(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.connection.prepareCall(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		return this.connection.nativeSQL(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.getConnection().setAutoCommit(autoCommit);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return this.getConnection().getAutoCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		this.getConnection().commit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		this.getConnection().rollback();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		this.getConnection().close();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return this.getConnection().isClosed();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.getConnection().getMetaData();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.getConnection().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return this.getConnection().isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		this.getConnection().setCatalog(catalog);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return this.getConnection().getCatalog();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		this.getConnection().setTransactionIsolation(level);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return this.getConnection().getTransactionIsolation();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.getConnection().getWarnings();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.getConnection().clearWarnings();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.getConnection().createStatement(resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.getConnection().getTypeMap();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.getConnection().setTypeMap(map);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		this.getConnection().setHoldability(holdability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return this.getConnection().getHoldability();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		return this.getConnection().setSavepoint();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.getConnection().setSavepoint(name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		this.getConnection().rollback(savepoint);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.getConnection().releaseSavepoint(savepoint);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return this.getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getConnection().prepareStatement(sql, autoGeneratedKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return this.getConnection().prepareStatement(sql, columnIndexes);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return this.getConnection().prepareStatement(sql, columnNames);
	}
	

	/**
	 * Gets the connection wrapped by the wrapped connection.
	 * @return The connection wrapped by the wrapped connection.
	 */
	protected Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * Gets the connection wrapped by the wrapped connection.
	 * @param value The connection wrapped by the wrapped connection.
	 */
	protected void setConnection(Connection value) {
		this.connection = value;
	}
	
	private Connection connection;
	
}
