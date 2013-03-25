package com.opower.connectionpool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Wraps an instance of the @see java.sql.Statement interface. This implementation delegates all 
 * calls to the wrapped statement and provides no additional functionality.  It is intended to
 * serve as a starting point for classes that need to customize the behavior of existing 
 * statement instances.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedStatement implements Statement {
	
	/**
	 * Initializes a new instance of the WrappedStatement class.
	 * @param statement The statement wrapped by the wrapped statement.
	 */
	public WrappedStatement(Statement statement) {
		this.statement = statement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return this.getStatement().executeQuery(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {
		return this.getStatement().executeUpdate(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#close()
	 */
	public void close() throws SQLException {
		this.getStatement().close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return this.getStatement().getMaxFieldSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		this.getStatement().setMaxFieldSize(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return this.getStatement().getMaxRows();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {
		this.getStatement().setMaxRows(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.getStatement().setEscapeProcessing(enable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return this.getStatement().getQueryTimeout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		this.getStatement().setQueryTimeout(seconds);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		this.getStatement().cancel();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.getStatement().getWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.getStatement().clearWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {
		this.getStatement().setCursorName(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		return this.getStatement().execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		return this.getStatement().getResultSet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return this.getStatement().getUpdateCount();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return this.getStatement().getMoreResults();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		this.getStatement().setFetchDirection(direction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return this.getStatement().getFetchDirection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		this.getStatement().setFetchSize(rows);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.getStatement().getFetchSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return this.getStatement().getResultSetConcurrency();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return this.getStatement().getResultSetType();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		this.getStatement().addBatch(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		this.getStatement().clearBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		return this.getStatement().executeBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.getStatement().getConnection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return this.getStatement().getMoreResults(current);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.getStatement().getGeneratedKeys();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getStatement().executeUpdate(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.getStatement().executeUpdate(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.getStatement().executeUpdate(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getStatement().execute(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return this.getStatement().execute(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return this.getStatement().execute(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return this.getStatement().getResultSetHoldability();
	}

	/**
	 * Gets the statement wrapped by the wrapped statement.
	 * @return The statement wrapped by the wrapped statement.
	 */
	protected Statement getStatement() {
		return this.statement;
	}
	
	/**
	 * Gets the statement wrapped by the wrapped statement.
	 * @param value The statement wrapped by the wrapped statement.
	 */
	protected void setStatement(Statement value) {
		this.statement = value;
	}
	
	private Statement statement;
		
}
