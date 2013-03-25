package com.opower.connectionpool;

import java.sql.PreparedStatement;

import org.junit.Test;

/**
 * Provides unit tests for the {@link com.opower.connectionpool.WrappedPreparedStatement WrappedPreparedStatement} 
 * class.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedPreparedStatementTest {
	
	/**
	 * Test the wrapper functionality using a {@link com.opower.connectionpool.WrapperTester WrapperTester}.
	 */
	@Test
	public void test() {
		WrapperTester wrapperTester = new WrapperTester(PreparedStatement.class);
		PreparedStatement preparedStatement = (PreparedStatement)wrapperTester.getWrappedValue();
		WrappedPreparedStatement wrappedPreparedStatement = new WrappedPreparedStatement(preparedStatement);
		wrapperTester.run(wrappedPreparedStatement);
	}
	
}
