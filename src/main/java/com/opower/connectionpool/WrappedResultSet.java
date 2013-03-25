package com.opower.connectionpool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * Wraps an instance of the @see java.sql.ResultSet interface. This implementation delegates all 
 * calls to the wrapped result set and provides no additional functionality.  It is intended to
 * serve as a starting point for classes that need to customize the behavior of existing result
 * set instances.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedResultSet implements ResultSet {

	/**
	 * Initializes a new instance of the WrappedResultSet class.
	 * @param resultSet The result set wrapped by the wrapped result set.
	 */
	public WrappedResultSet(ResultSet resultSet) {
		this.resultSet = resultSet; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException {
		return this.getResultSet().next();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {
		this.getResultSet().close();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return this.getResultSet().wasNull();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		return this.getResultSet().getString(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return this.getResultSet().getBoolean(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return this.getResultSet().getByte(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		return this.getResultSet().getShort(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		return this.getResultSet().getInt(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		return this.getResultSet().getLong(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return this.getResultSet().getFloat(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return this.getResultSet().getDouble(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return this.getResultSet().getBigDecimal(columnIndex, scale);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return this.getResultSet().getBytes(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return this.getResultSet().getDate(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return this.getResultSet().getTime(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return this.getResultSet().getTimestamp(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return this.getResultSet().getAsciiStream(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return this.getResultSet().getUnicodeStream(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return this.getResultSet().getBinaryStream(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return this.getResultSet().getString(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return this.getResultSet().getBoolean(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		return this.getResultSet().getByte(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		return this.getResultSet().getShort(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		return this.getResultSet().getInt(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		return this.getResultSet().getLong(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		return this.getResultSet().getFloat(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		return this.getResultSet().getDouble(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	@Deprecated
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return this.getResultSet().getBigDecimal(columnName, scale);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return this.getResultSet().getBytes(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		return this.getResultSet().getDate(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		return this.getResultSet().getTime(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return this.getResultSet().getTimestamp(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return this.getResultSet().getAsciiStream(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	@Deprecated
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		return this.getResultSet().getUnicodeStream(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return this.getResultSet().getBinaryStream(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.getResultSet().getWarnings();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.getResultSet().clearWarnings();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		return this.getResultSet().getCursorName();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.getResultSet().getMetaData();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return this.getResultSet().getObject(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return this.getResultSet().getObject(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		return this.getResultSet().findColumn(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return this.getResultSet().getCharacterStream(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		return this.getResultSet().getCharacterStream(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return this.getResultSet().getBigDecimal(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return this.getResultSet().getBigDecimal(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return this.getResultSet().isBeforeFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		return this.getResultSet().isAfterLast();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return this.getResultSet().isFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		return this.getResultSet().isLast();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		this.getResultSet().beforeFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		this.getResultSet().afterLast();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		return this.getResultSet().first();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		return this.getResultSet().last();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return this.getResultSet().getRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		return this.getResultSet().absolute(row);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		return this.getResultSet().relative(rows);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		return this.getResultSet().previous();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		this.getResultSet().setFetchDirection(direction);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return this.getResultSet().getFetchDirection();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		this.getResultSet().setFetchSize(rows);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.getResultSet().getFetchSize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		return this.getResultSet().getType();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		return this.getResultSet().getConcurrency();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return this.getResultSet().rowUpdated();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return this.getResultSet().rowInserted();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return this.getResultSet().rowDeleted();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		this.getResultSet().updateNull(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		this.getResultSet().updateBoolean(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		this.getResultSet().updateByte(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		this.getResultSet().updateShort(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		this.getResultSet().updateInt(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		this.getResultSet().updateLong(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		this.getResultSet().updateFloat(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		this.getResultSet().updateDouble(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		this.getResultSet().updateBigDecimal(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		this.getResultSet().updateString(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		this.getResultSet().updateBytes(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		this.getResultSet().updateDate(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		this.getResultSet().updateTime(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		this.getResultSet().updateTimestamp(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		this.getResultSet().updateAsciiStream(columnIndex, x, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		this.getResultSet().updateBinaryStream(columnIndex, x, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		this.getResultSet().updateCharacterStream(columnIndex, x, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		this.getResultSet().updateObject(columnIndex, x, scale);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		this.getResultSet().updateObject(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		this.getResultSet().updateNull(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		this.getResultSet().updateBoolean(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		this.getResultSet().updateByte(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		this.getResultSet().updateShort(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		this.getResultSet().updateInt(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		this.getResultSet().updateLong(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		this.getResultSet().updateFloat(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		this.getResultSet().updateDouble(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		this.getResultSet().updateBigDecimal(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		this.getResultSet().updateString(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		this.getResultSet().updateBytes(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		this.getResultSet().updateDate(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		this.getResultSet().updateTime(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		this.getResultSet().updateTimestamp(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		this.getResultSet().updateAsciiStream(columnName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		this.getResultSet().updateBinaryStream(columnName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		this.getResultSet().updateCharacterStream(columnName, reader, length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		this.getResultSet().updateObject(columnName, x, scale);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		this.getResultSet().updateObject(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		this.getResultSet().insertRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		this.getResultSet().updateRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		this.getResultSet().deleteRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		this.getResultSet().refreshRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		this.getResultSet().cancelRowUpdates();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		this.getResultSet().moveToInsertRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		this.getResultSet().moveToCurrentRow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		return this.getResultSet().getStatement();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
		return this.getResultSet().getObject(i, map);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return this.getResultSet().getRef(i);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return this.getResultSet().getBlob(i);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return this.getResultSet().getClob(i);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return this.getResultSet().getArray(i);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
		return this.getResultSet().getObject(colName, map);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		return this.getResultSet().getRef(colName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		return this.getResultSet().getBlob(colName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		return this.getResultSet().getClob(colName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String colName) throws SQLException {
		return this.getResultSet().getArray(colName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return this.getResultSet().getDate(columnIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return this.getResultSet().getDate(columnName, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return this.getResultSet().getTime(columnIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return this.getResultSet().getTime(columnName, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return this.getResultSet().getTimestamp(columnIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return this.getResultSet().getTimestamp(columnName, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return this.getResultSet().getURL(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		return this.getResultSet().getURL(columnName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		this.getResultSet().updateRef(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		this.getResultSet().updateRef(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		this.getResultSet().updateBlob(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		this.getResultSet().updateBlob(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		this.getResultSet().updateClob(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		this.getResultSet().updateClob(columnName, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		this.getResultSet().updateArray(columnIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		this.getResultSet().updateArray(columnName, x);
	}
	
	/**
	 * Gets the result set wrapped by the wrapped result set.
	 * @return The result set wrapped by the wrapped result set.
	 */
	protected ResultSet getResultSet() {
		return this.resultSet;
	}
	
	/**
	 * Gets the result set wrapped by the wrapped result set.
	 * @param value The result set wrapped by the wrapped result set.
	 */
	protected void setResultSet(ResultSet value) {
		this.resultSet = value;
	}
	
	private ResultSet resultSet;
	
}
