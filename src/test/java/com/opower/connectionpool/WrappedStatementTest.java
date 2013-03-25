package com.opower.connectionpool;

import java.sql.Statement;

import org.junit.Test;

/**
 * Provides unit tests for the {@link com.opower.connectionpool.WrappedStatement WrappedStatement} 
 * class.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedStatementTest {
	
	/**
	 * Test the wrapper functionality using a {@link com.opower.connectionpool.WrapperTester WrapperTester}.
	 */
	@Test
	public void test() {
		WrapperTester wrapperTester = new WrapperTester(Statement.class);
		Statement statement = (Statement)wrapperTester.getWrappedValue();
		WrappedStatement wrappedStatement = new WrappedStatement(statement);
		wrapperTester.run(wrappedStatement);
	}
	
}
