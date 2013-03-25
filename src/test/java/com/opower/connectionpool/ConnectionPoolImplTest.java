package com.opower.connectionpool;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import javax.sql.DataSource;

import org.easymock.EasyMock;

import org.junit.Test;

public class ConnectionPoolImplTest {
	
	/**
	 * Test the constructor {@link com.opower.connectionpool.ConnectionPoolImpl#ConnectionPoolImpl(DataSource).
	 * @throws SQLException
	 */
	@Test
	public void testConstructor0() throws SQLException {
		DataSource dataSource = EasyMock.createNiceMock(DataSource.class);
		EasyMock.replay(dataSource);
		ConnectionPoolImpl connectionPool;
		connectionPool = new ConnectionPoolImpl(dataSource);
		assertEquals(0, connectionPool.getMinimumConnections());
		assertEquals(Integer.MAX_VALUE, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(0, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
	}
	
	/**
	 * Test the constructor {@link com.opower.connectionpool.ConnectionPoolImpl#ConnectionPoolImpl(DataSource, int).
	 * @throws SQLException
	 */
	@Test
	public void testConstructor1() throws SQLException {
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		ConnectionPoolImpl connectionPool = new ConnectionPoolImpl(dataSource, 5);
		assertEquals(5, connectionPool.getMinimumConnections());
		assertEquals(Integer.MAX_VALUE, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(5, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
	}
	
	/**
	 * Test the constructor {@link com.opower.connectionpool.ConnectionPoolImpl#ConnectionPoolImpl(DataSource, int, int).
	 * @throws SQLException
	 */
	@Test
	public void testConstructor2() throws SQLException {
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		ConnectionPoolImpl connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		assertEquals(5, connectionPool.getMinimumConnections());
		assertEquals(10, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(5, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
	}
	
	/**
	 * Test the constructor {@link com.opower.connectionpool.ConnectionPoolImpl#ConnectionPoolImpl(DataSource, int, int, long).
	 * @throws SQLException
	 */
	@Test
	public void testConstructor3() throws SQLException {
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		ConnectionPoolImpl connectionPool = new ConnectionPoolImpl(dataSource, 5, 10, 10000);
		assertEquals(5, connectionPool.getMinimumConnections());
		assertEquals(10, connectionPool.getMaximumConnections());
		assertEquals(10000, connectionPool.getTimeout());
		assertEquals(5, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
	}
	
	/**
	 * Tests the ConnectionPoolImpl.getConnection() method.
	 * @throws SQLException
	 */
	@Test
	public void testGetConnection() throws SQLException {
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 10; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		ConnectionPoolImpl connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		for (int i = 1; i < 11; i++) {
			connectionPool.getConnection();
			assertEquals(Math.max(5, i), connectionPool.getCurrentConnections());
		}
		try {
			connectionPool.getConnection();
			fail("Exceeding maximumConnections did not result in an exception.");
		} catch (SQLException e) {

		}
		EasyMock.verify(dataSource);
	}

	/**
	 * Test the ConnectionPoolImpl.releaseConnection(Connection) method.
	 * @throws SQLException
	 */
	@Test
	public void testReleaseConnection() throws SQLException {
		DataSource dataSource = EasyMock.createMock(DataSource.class);;
		for (int i = 0; i < 10; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		Queue<Connection> connections = new LinkedList<Connection>();
		ConnectionPoolImpl connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		for (int i = 0; i < 10; i++) {
			connections.offer(connectionPool.getConnection());
		}
		for (int i = 10; i > 0; i--) {
			connectionPool.releaseConnection(connections.poll());
		}
		assertEquals(10, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
	}
	
	/**
	 * Test the connection timeout functionality.
	 * @throws SQLException
	 */
	@Test
	public void testTimeout() throws SQLException {
		Connection connection = EasyMock.createNiceMock(Connection.class);
		EasyMock.replay(connection);
		DataSource dataSource = EasyMock.createMock(DataSource.class);
		EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		EasyMock.replay(dataSource);
		ConnectionPool connectionPool = new ConnectionPoolImpl(dataSource, 1, 10, 1000);
		connection = connectionPool.getConnection();
		try {
			connection.commit();
		} catch (Exception e) {
			fail("Failed to execute method after initialization.");
		}
		try {
			Thread.sleep(300);
		} catch (Exception e) {
			fail("Sleep interupted.");
		}
		try {
			connection.commit();	
		} catch (Exception e) {
			fail("Failed to execute method to extend timeout.");
		}
		try {
			Thread.sleep(701);
		} catch (Exception e) {
			fail("Sleep interupted.");
		}
		try {
			connection.commit();
		} catch (Exception e) {
			fail("Failed to execute method after extended timeout.");
		}
		try {
			Thread.sleep(1001);
		} catch (Exception e) {
			fail("Sleep interupted.");
		}
		try {
			connection.commit();
			fail("Did not fail to execute method after timeout expired.");
		} catch (Exception e) {
		}
	}	
	
}
