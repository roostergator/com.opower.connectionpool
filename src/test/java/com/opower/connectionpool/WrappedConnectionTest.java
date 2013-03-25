package com.opower.connectionpool;

import java.sql.Connection;

import org.junit.Test;

/**
 * Provides unit tests for the {@link com.opower.connectionpool.WrappedConnection WrappedConnection} 
 * class.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedConnectionTest {
	
	/**
	 * Test the wrapper functionality using a {@link com.opower.connectionpool.WrapperTester WrapperTester}.
	 */
	@Test
	public void test() {
		WrapperTester wrapperTester = new WrapperTester(Connection.class);
		Connection connection = (Connection)wrapperTester.getWrappedValue();
		WrappedConnection wrappedConnection = new WrappedConnection(connection);
		wrapperTester.run(wrappedConnection);
	}
	
}
