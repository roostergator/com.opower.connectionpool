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
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * Wraps an instance of the @see java.sql.CallableStatement interface. This implementation 
 * delegates all calls to the wrapped callable statement and provides no additional functionality.
 * It is intended to serve as a starting point for classes that need to customize the behavior of
 * existing callable instances.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedCallableStatement implements CallableStatement {

	/**
	 * Initializes a new instance of the WrappedCallableStatement class.
	 * @param callableStatement The callable statement wrapped by the wrapped callable statement.
	 */
	public WrappedCallableStatement(CallableStatement callableStatement) {
		this.callableStatement = callableStatement;
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		this.getCallableStatement().registerOutParameter(parameterIndex, sqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		this.getCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return this.getCallableStatement().wasNull();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getString(int)
	 */
	public String getString(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getString(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getBoolean(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	public byte getByte(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getByte(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	public short getShort(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getShort(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	public int getInt(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getInt(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	public long getLong(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getLong(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	public float getFloat(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getFloat(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	public double getDouble(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getDouble(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return this.getCallableStatement().getBigDecimal(parameterIndex, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getBytes(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	public Date getDate(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getDate(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	public Time getTime(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getTime(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getTimestamp(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	public Object getObject(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getObject(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getBigDecimal(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
		return this.getCallableStatement().getObject(i, map);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return this.getCallableStatement().getRef(i);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return this.getCallableStatement().getBlob(i);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		return this.getCallableStatement().getClob(i);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return this.getCallableStatement().getArray(i);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return this.getCallableStatement().getDate(parameterIndex, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return this.getCallableStatement().getTime(parameterIndex, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return this.getCallableStatement().getTimestamp(parameterIndex, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
	 */
	public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
		this.getCallableStatement().registerOutParameter(paramIndex, sqlType, typeName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
	 */
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		this.getCallableStatement().registerOutParameter(parameterName, sqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
	 */
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		this.getCallableStatement().registerOutParameter(parameterName, sqlType, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
	 */
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		this.getCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	public URL getURL(int parameterIndex) throws SQLException {
		return this.getCallableStatement().getURL(parameterIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	public void setURL(String parameterName, URL val) throws SQLException {
		this.getCallableStatement().setURL(parameterName, val);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
	 */
	public void setNull(String parameterName, int sqlType) throws SQLException {
		this.getCallableStatement().setNull(parameterName, sqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		this.getCallableStatement().setBoolean(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
	 */
	public void setByte(String parameterName, byte x) throws SQLException {
		this.getCallableStatement().setByte(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
	 */
	public void setShort(String parameterName, short x) throws SQLException {
		this.getCallableStatement().setShort(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
	 */
	public void setInt(String parameterName, int x) throws SQLException {
		this.getCallableStatement().setInt(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
	 */
	public void setLong(String parameterName, long x) throws SQLException {
		this.getCallableStatement().setLong(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
	 */
	public void setFloat(String parameterName, float x) throws SQLException {
		this.getCallableStatement().setFloat(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
	 */
	public void setDouble(String parameterName, double x) throws SQLException {
		this.getCallableStatement().setDouble(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		this.getCallableStatement().setBigDecimal(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String parameterName, String x) throws SQLException {
		this.getCallableStatement().setString(parameterName, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
	 */
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		this.getCallableStatement().setBytes(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate(String parameterName, Date x) throws SQLException {
		this.getCallableStatement().setDate(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime(String parameterName, Time x) throws SQLException {
		this.getCallableStatement().setTime(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		this.getCallableStatement().setTimestamp(parameterName, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		this.getCallableStatement().setAsciiStream(parameterName, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		this.getCallableStatement().setBinaryStream(parameterName, x, length);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
	 */
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		this.getCallableStatement().setObject(parameterName, x, targetSqlType, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
	 */
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		this.getCallableStatement().setObject(parameterName, x, targetSqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object)
	 */
	public void setObject(String parameterName, Object x) throws SQLException {
		this.getCallableStatement().setObject(parameterName, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		this.getCallableStatement().setCharacterStream(parameterName, reader, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		this.getCallableStatement().setDate(parameterName, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		this.getCallableStatement().setTime(parameterName, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		this.getCallableStatement().setTimestamp(parameterName, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
	 */
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		this.getCallableStatement().setNull(parameterName, sqlType, typeName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	public String getString(String parameterName) throws SQLException {
		return this.getCallableStatement().getString(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String parameterName) throws SQLException {
		return this.getCallableStatement().getBoolean(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	public byte getByte(String parameterName) throws SQLException {
		return this.getCallableStatement().getByte(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	public short getShort(String parameterName) throws SQLException {
		return this.getCallableStatement().getShort(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	public int getInt(String parameterName) throws SQLException {
		return this.getCallableStatement().getInt(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	public long getLong(String parameterName) throws SQLException {
		return this.getCallableStatement().getLong(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	public float getFloat(String parameterName) throws SQLException {
		return this.getCallableStatement().getFloat(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	public double getDouble(String parameterName) throws SQLException {
		return this.getCallableStatement().getDouble(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String parameterName) throws SQLException {
		return this.getCallableStatement().getBytes(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	public Date getDate(String parameterName) throws SQLException {
		return this.getCallableStatement().getDate(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	public Time getTime(String parameterName) throws SQLException {
		return this.getCallableStatement().getTime(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return this.getCallableStatement().getTimestamp(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	public Object getObject(String parameterName) throws SQLException {
		return this.getCallableStatement().getObject(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return this.getCallableStatement().getBigDecimal(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return this.getCallableStatement().getObject(parameterName, map);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	public Ref getRef(String parameterName) throws SQLException {
		return this.getCallableStatement().getRef(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	public Blob getBlob(String parameterName) throws SQLException {
		return this.getCallableStatement().getBlob(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	public Clob getClob(String parameterName) throws SQLException {
		return this.getCallableStatement().getClob(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	public Array getArray(String parameterName) throws SQLException {
		return this.getCallableStatement().getArray(parameterName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return this.getCallableStatement().getDate(parameterName, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return this.getCallableStatement().getTime(parameterName, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return this.getCallableStatement().getTimestamp(parameterName, cal);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	public URL getURL(String parameterName) throws SQLException {
		return this.getCallableStatement().getURL(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return this.getCallableStatement().executeQuery(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String sql) throws SQLException {
		return this.getCallableStatement().executeUpdate(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#close()
	 */
	public void close() throws SQLException {
		this.getCallableStatement().close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return this.getCallableStatement().getMaxFieldSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		this.getCallableStatement().setMaxFieldSize(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return this.getCallableStatement().getMaxRows();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws SQLException {
		this.getCallableStatement().setMaxRows(max);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.getCallableStatement().setEscapeProcessing(enable);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return this.getCallableStatement().getQueryTimeout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		this.getCallableStatement().setQueryTimeout(seconds);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#cancel()
	 */
	public void cancel() throws SQLException {
		this.getCallableStatement().cancel();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.getCallableStatement().getWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.getCallableStatement().clearWarnings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String name) throws SQLException {
		this.getCallableStatement().setCursorName(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String)
	 */
	public boolean execute(String sql) throws SQLException {
		return this.getCallableStatement().execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		return this.getCallableStatement().getResultSet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return this.getCallableStatement().getUpdateCount();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return this.getCallableStatement().getMoreResults();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		this.getCallableStatement().setFetchDirection(direction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return this.getCallableStatement().getFetchDirection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		this.getCallableStatement().setFetchSize(rows);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.getCallableStatement().getFetchSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return this.getCallableStatement().getResultSetConcurrency();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return this.getCallableStatement().getResultSetType();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch(java.lang.String)
	 */
	public void addBatch(String sql) throws SQLException {
		this.getCallableStatement().addBatch(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearBatch()
	 */
	public void clearBatch() throws SQLException {
		this.getCallableStatement().clearBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeBatch()
	 */
	public int[] executeBatch() throws SQLException {
		return this.getCallableStatement().executeBatch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.getCallableStatement().getConnection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMoreResults(int)
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return this.getCallableStatement().getMoreResults(current);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.getCallableStatement().getGeneratedKeys();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, int)
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getCallableStatement().executeUpdate(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, int[])
	 */
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.getCallableStatement().executeUpdate(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.getCallableStatement().executeUpdate(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, int)
	 */
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return this.getCallableStatement().execute(sql, autoGeneratedKeys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, int[])
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return this.getCallableStatement().execute(sql, columnIndexes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute(java.lang.String, java.lang.String[])
	 */
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return this.getCallableStatement().execute(sql, columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return this.getCallableStatement().getResultSetHoldability();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		return this.getCallableStatement().executeQuery();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return this.getCallableStatement().executeUpdate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.getCallableStatement().setNull(parameterIndex, sqlType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.getCallableStatement().setBoolean(parameterIndex, x);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.getCallableStatement().setByte(parameterIndex, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.getCallableStatement().setShort(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.getCallableStatement().setInt(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.getCallableStatement().setLong(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.getCallableStatement().setFloat(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.getCallableStatement().setDouble(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.getCallableStatement().setBigDecimal(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		this.getCallableStatement().setString(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.getCallableStatement().setBytes(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.getCallableStatement().setDate(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.getCallableStatement().setTime(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.getCallableStatement().setTimestamp(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getCallableStatement().setAsciiStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getCallableStatement().setUnicodeStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.getCallableStatement().setBinaryStream(parameterIndex, x, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		this.getCallableStatement().clearParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		this.getCallableStatement().setObject(parameterIndex, x, targetSqlType, scale);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.getCallableStatement().setObject(parameterIndex, x, targetSqlType);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.getCallableStatement().setObject(parameterIndex, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return this.getCallableStatement().execute();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		this.getCallableStatement().addBatch();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.getCallableStatement().setCharacterStream(parameterIndex, reader, length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int i, Ref x) throws SQLException {
		this.getCallableStatement().setRef(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		this.getCallableStatement().setBlob(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int i, Clob x) throws SQLException {
		this.getCallableStatement().setClob(i, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int i, Array x) throws SQLException {
		this.getCallableStatement().setArray(i, x);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.getCallableStatement().getMetaData();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.getCallableStatement().setDate(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.getCallableStatement().setTime(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.getCallableStatement().setTimestamp(parameterIndex, x, cal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		this.getCallableStatement().setNull(paramIndex, sqlType, typeName);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.getCallableStatement().setURL(parameterIndex, x);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.getCallableStatement().getParameterMetaData();
	}
	
	/**
	 * Gets the callable statement wrapped by the wrapped callable statement.
	 * @return The callable statement wrapped by the wrapped callable statement.
	 */
	protected CallableStatement getCallableStatement() {
		return this.callableStatement;
	}
	
	/**
	 * Gets the callable statement wrapped by the wrapped callable statement.
	 * @param value The callable statement wrapped by the wrapped callable statement.
	 */
	protected void setCallableStatement(CallableStatement value) {
		this.callableStatement = value;
	}
	
	private CallableStatement callableStatement;
	
}