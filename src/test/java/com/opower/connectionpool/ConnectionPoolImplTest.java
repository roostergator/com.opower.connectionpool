package com.opower.connectionpool;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionPoolImplTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test all of the ConnectionPoolImpl constructors.
	 * @throws SQLException
	 */
	@Test
	public void testConstructors() throws SQLException {
		DataSource dataSource;
		ConnectionPoolImpl connectionPool;
		// Test ConnectionPoolImpl.ConnectionPoolImpl(DataSource)
		dataSource = EasyMock.createNiceMock(DataSource.class);
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource);
		assertEquals(0, connectionPool.getMinimumConnections());
		assertEquals(Integer.MAX_VALUE, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(0, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
		// Test ConnectionPoolImpl.ConnectionPoolImpl(DataSource, int)
		dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource, 5);
		assertEquals(5, connectionPool.getMinimumConnections());
		assertEquals(Integer.MAX_VALUE, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(5, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
		// Test ConnectionPoolImpl.ConnectionPoolImpl(DataSource, int, int)
		dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		assertEquals(5, connectionPool.getMinimumConnections());
		assertEquals(10, connectionPool.getMaximumConnections());
		assertEquals(0, connectionPool.getTimeout());
		assertEquals(5, connectionPool.getCurrentConnections());
		EasyMock.verify(dataSource);
		// Test ConnectionPoolImpl.ConnectionPoolImpl(DataSource, int, int, long)
		dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 5; i++) {
			Connection connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			EasyMock.expect(dataSource.getConnection()).andReturn(connection);
		}
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource, 5, 10, 10000);
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
		DataSource dataSource;
		Connection connection;
		ConnectionPoolImpl connectionPool;
		Queue<Connection> connections;
		connections = new LinkedList<Connection>();
		dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 25; i++) {
			connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			connections.offer(connection);
			if (i < 10) {
				EasyMock.expect(dataSource.getConnection()).andReturn(connection);
			}
		}
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		for (int i = 0; i < 10; i++) {
			connectionPool.getConnection();
			assertEquals(Math.max(5, i + 1), connectionPool.getCurrentConnections());
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
		DataSource dataSource;
		Connection connection;
		ConnectionPoolImpl connectionPool;
		Queue<Connection> connections;
		connections = new LinkedList<Connection>();
		dataSource = EasyMock.createMock(DataSource.class);
		for (int i = 0; i < 25; i++) {
			connection = EasyMock.createNiceMock(Connection.class);
			EasyMock.replay(connection);
			if (i < 10) {
				EasyMock.expect(dataSource.getConnection()).andReturn(connection);
			}
		}
		EasyMock.replay(dataSource);
		connectionPool = new ConnectionPoolImpl(dataSource, 5, 10);
		for (int i = 0; i < 10; i++) {
			connection = connectionPool.getConnection();
			connections.offer(connection);
		}
		System.out.println(connections.size());
		try {
			connection = connectionPool.getConnection();
			fail("Exceeding maximumConnections did not result in an exception.");
		} catch (SQLException e) {
		
		}
		connection = connections.poll();
		connectionPool.releaseConnection(connection);
		connection = connectionPool.getConnection();
		connections.offer(connection);
		assertEquals(10, connectionPool.getCurrentConnections());
		for (int i = 0; i < 10; i++) {
			connection = connections.poll();
			connectionPool.releaseConnection(connection);
		}
		System.out.println(connections.size());
		EasyMock.verify(dataSource);
	}
	
}
