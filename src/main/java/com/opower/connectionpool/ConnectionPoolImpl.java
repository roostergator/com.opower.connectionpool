
package com.opower.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides an implementation of the ConnectionPool interface.
 * 
 * @author Joshua Mark Rutherford
 */
public class ConnectionPoolImpl implements ConnectionPool {

	/* (non-Javadoc)
	 * @see com.opower.connectionpool.ConnectionPool#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.opower.connectionpool.ConnectionPool#releaseConnection(java.sql.Connection)
	 */
	public void releaseConnection(Connection connection) throws SQLException {
		// TODO Auto-generated method stub

	}

}
