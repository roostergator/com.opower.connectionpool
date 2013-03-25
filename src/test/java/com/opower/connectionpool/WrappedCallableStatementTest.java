package com.opower.connectionpool;

import java.sql.CallableStatement;

import org.junit.Test;

/**
 * Provides unit tests for the {@link com.opower.connectionpool.WrappedCallableStatement WrappedCallableStatement} 
 * class.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedCallableStatementTest {
	
	/**
	 * Test the wrapper functionality using a {@link com.opower.connectionpool.WrapperTester WrapperTester}.
	 */
	@Test
	public void test() {
		WrapperTester wrapperTester = new WrapperTester(CallableStatement.class);
		CallableStatement callableStatement = (CallableStatement)wrapperTester.getWrappedValue();
		WrappedCallableStatement wrappedCallabeStatement = new WrappedCallableStatement(callableStatement);
		wrapperTester.run(wrappedCallabeStatement);
	}
	
}
