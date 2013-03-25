package com.opower.connectionpool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Wraps an instance of the @see java.sql.PreparedStatement interface. This implementation 
 * delegates all calls to the wrapped prepared statement and provides no additional functionality.
 * It is intended to serve as a starting point for classes that need to customize the behavior of
 * existing prepared statement instances.
 *
 * @author Joshua Mark Rutherford
 */
public class WrappedPreparedStatement implements PreparedStatement {

	/**
	 * Initializes a new instance of the WrappedPreparedStatement class.
	 * @param preparedStatement The prepared statement wrapped by the wrapped prepared statement.
	 */
	public WrappedPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return this.getPreparedStatement().executeQuery(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {
		return this.getPreparedStatement().executeUpdate(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#close()
	 */
	public void close() throws SQLException {
		this.getPreparedStatement().close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return this.getPreparedStatement().getMaxFieldSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		this.getPreparedStatement().setMaxFieldSize(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return this.getPreparedStatement().getMaxRows();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {
		this.getPreparedStatement().setMaxRows(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.getPreparedStatement().setEscapeProcessing(enable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return this.getPreparedStatement().getQueryTimeout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		this.getPreparedStatement().setQueryTimeout(seconds);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#cancel()
	 */
	public void cancel() throws SQLException {
		this.getPreparedStatement().cancel();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.getPreparedStatement().getWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.getPreparedStatement().clearWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {
		this.getPreparedStatement().setCursorName(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		return this.getPreparedStatement().execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		return this.getPreparedStatement().getResultSet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return this.getPreparedStatement().getUpdateCount();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return this.getPreparedStatement().getMoreResults();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		this.getPreparedStatement().setFetchDirection(direction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return this.getPreparedStatement().getFetchDirection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		this.getPreparedStatement().setFetchSize(rows);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.getPreparedStatement().getFetchSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return this.getPreparedStatement().getResultSetConcurrency();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return this.getPreparedStatement().getResultSetType();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		this.getPreparedStatement().addBatch(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		this.getPreparedStatement().clearBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		return this.getPreparedStatement().executeBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.getPreparedStatement().getConnection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return this.getPreparedStatement().getMoreResults(current);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.getPreparedStatement().getGeneratedKeys();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getPreparedStatement().executeUpdate(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.getPreparedStatement().executeUpdate(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.getPreparedStatement().executeUpdate(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getPreparedStatement().execute(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return this.getPreparedStatement().execute(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return this.getPreparedStatement().execute(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return this.getPreparedStatement().getResultSetHoldability();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		return this.getPreparedStatement().executeQuery();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return this.getPreparedStatement().executeUpdate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.getPreparedStatement().setNull(parameterIndex, sqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.getPreparedStatement().setBoolean(parameterIndex, x);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.getPreparedStatement().setByte(parameterIndex, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.getPreparedStatement().setShort(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.getPreparedStatement().setInt(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.getPreparedStatement().setLong(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.getPreparedStatement().setFloat(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.getPreparedStatement().setDouble(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.getPreparedStatement().setBigDecimal(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		this.getPreparedStatement().setString(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.getPreparedStatement().setBytes(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.getPreparedStatement().setDate(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.getPreparedStatement().setTime(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.getPreparedStatement().setTimestamp(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getPreparedStatement().setAsciiStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getPreparedStatement().setUnicodeStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getPreparedStatement().setBinaryStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		this.getPreparedStatement().clearParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		this.getPreparedStatement().setObject(parameterIndex, x, targetSqlType, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.getPreparedStatement().setObject(parameterIndex, x, targetSqlType);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.getPreparedStatement().setObject(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return this.getPreparedStatement().execute();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		this.getPreparedStatement().addBatch();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.getPreparedStatement().setCharacterStream(parameterIndex, reader, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		this.getPreparedStatement().setRef(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		this.getPreparedStatement().setBlob(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		this.getPreparedStatement().setClob(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		this.getPreparedStatement().setArray(i, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.getPreparedStatement().getMetaData();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.getPreparedStatement().setDate(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.getPreparedStatement().setTime(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.getPreparedStatement().setTimestamp(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		this.getPreparedStatement().setNull(paramIndex, sqlType, typeName);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.getPreparedStatement().setURL(parameterIndex, x);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.getPreparedStatement().getParameterMetaData();
	}
	
	/**
	 * Gets the prepared statement wrapped by the wrapped prepared statement.
	 * @return The prepared statement wrapped by the wrapped prepared statement.
	 */
	protected PreparedStatement getPreparedStatement() {
		return this.preparedStatement;
	}
	
	/**
	 * Gets the prepared statement wrapped by the wrapped prepared statement.
	 * @param value The prepared statement wrapped by the wrapped prepared statement.
	 */
	protected void setPreparedStatement(PreparedStatement value) {
		this.preparedStatement = value;
	}
	
	private PreparedStatement preparedStatement;
	
}
