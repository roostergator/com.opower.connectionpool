package com.opower.connectionpool;

import java.sql.ResultSet;

import org.junit.Test;

/**
 * Provides unit tests for the {@link com.opower.connectionpool.WrappedResultSet WrappedResultSet} 
 * class.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedResultSetTest {
	
	/**
	 * Test the wrapper functionality using a {@link com.opower.connectionpool.WrapperTester WrapperTester}.
	 */
	@Test
	public void test() {
		WrapperTester wrapperTester = new WrapperTester(ResultSet.class);
		ResultSet resultSet = (ResultSet)wrapperTester.getWrappedValue();
		WrappedResultSet wrappedResultSet = new WrappedResultSet(resultSet);
		wrapperTester.run(wrappedResultSet);
	}
	
}
