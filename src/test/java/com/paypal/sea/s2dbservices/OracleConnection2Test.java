package com.paypal.sea.s2dbservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.junit.Test;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.paypal.sea.s2dbservices.oracledbaccess.OracleConnection2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OracleConnection2.class })
public class OracleConnection2Test {

    Connection mockConn = mock(Connection.class);
    ReadProperty mockRd = mock(ReadProperty.class);
    Statement mockSt = mock(Statement.class);
    ResultSet mockRs = mock(ResultSet.class);
    OracleConnection2 obj = new OracleConnection2(mockConn, mockRd);

    String queryString;
    String testUsername = "autostgdba";
    String testPass = "dba";
    String testDbServer = "lvsdb01";
    String testPort = "2126";
    String testMeta = "METADB";
    Vector<String> vs = new Vector<String>();
    Vector<Vector<String>> rs = new Vector<Vector<String>>();

    @Test
    public void testConnectSuccessful() {
    	PowerMockito.when(mockRd.getValue("db_server")).thenReturn(testDbServer);
        PowerMockito.when(mockRd.getValue("Port_number")).thenReturn(testPort);
        PowerMockito.when(mockRd.getValue("Metadata_Db_sid")).thenReturn(testMeta);
        PowerMockito.when(mockRd.getValue("Username")).thenReturn(testUsername);
        PowerMockito.when(mockRd.getValue("Password")).thenReturn(testPass);
        PowerMockito.when(mockRd.getValue("Timeout_for_connect_database")).thenReturn("10");
        
        OracleConnection2 oc2Spy = PowerMockito.spy(obj);
        StringConstants.DBSTATUS STATUS = null;
        try {
			PowerMockito.doReturn(StringConstants.DBSTATUS.DBUP).when(oc2Spy,
					"connect", testDbServer, testPort, testMeta, testUsername,
					testPass);
			STATUS = org.powermock.reflect.Whitebox
					.<StringConstants.DBSTATUS> invokeMethod(oc2Spy, "connect");
			PowerMockito.verifyPrivate(oc2Spy, times(1)).invoke("connect",
					testDbServer, testPort, testMeta, testUsername, testPass);
		} catch (Exception e) {
			e.printStackTrace();
		}
        assertEquals(StringConstants.DBSTATUS.DBUP, STATUS);
    }
    
    @Test
    public void testConnectWithArgumentsSuccessful() {
        String url = "jdbc:oracle:thin:@" + testDbServer + ":" + testPort + ":"
                + testMeta;
		PowerMockito.when(mockRd.getValue("Timeout_for_connect_database"))
				.thenReturn("10");
        StringConstants.DBSTATUS STATUS = null;
        try {
        	PowerMockito.mockStatic(DriverManager.class);
			PowerMockito.when(
					DriverManager.getConnection(url, testUsername, testPass))
					.thenReturn(mockConn);
			STATUS = org.powermock.reflect.Whitebox
					.<StringConstants.DBSTATUS> invokeMethod(obj, "connect",
							testDbServer, testPort, testMeta, testUsername,
							testPass);
        } catch (Exception e) {
        }
        assertEquals(StringConstants.DBSTATUS.DBUP, STATUS);
    }
    
    @Test
    public void testConnectWithArgumentsClassNotFoundException() {
        String url = "jdbc:oracle:thin:@" + testDbServer + ":" + testPort + ":"
                + testMeta;
		PowerMockito.when(mockRd.getValue("Timeout_for_connect_database"))
				.thenReturn("10");
        StringConstants.DBSTATUS STATUS = null;
        try {
        	PowerMockito.mockStatic(Class.class);
			PowerMockito.when(
					Class.forName("oracle.jdbc.driver.OracleDriver"))
					.thenThrow(new ClassNotFoundException("Mock class not found"));
			STATUS = org.powermock.reflect.Whitebox
					.<StringConstants.DBSTATUS> invokeMethod(obj, "connect",
							testDbServer, testPort, testMeta, testUsername,
							testPass);
        } catch (Exception e) {
        }
        assertEquals(StringConstants.DBSTATUS.DBERROR, STATUS);
    }
    
    @Test
    public void testConnectWithArgumentsSQLExceptionForDBDOWN() {
        String url = "jdbc:oracle:thin:@" + testDbServer + ":" + testPort + ":"
                + testMeta;
		PowerMockito.when(mockRd.getValue("Timeout_for_connect_database"))
				.thenReturn("10");
		StringConstants.DBSTATUS[] STATUS_ARRAY = new StringConstants.DBSTATUS[4];
		try {
			PowerMockito.mockStatic(DriverManager.class);
			PowerMockito
					.when(DriverManager.getConnection(url, testUsername,
							testPass))
					.thenThrow(
							new SQLException("mock SQL Exception", "db down",
									12505))
					.thenThrow(
							new SQLException("mock SQL Exception", "db down",
									12528))
					.thenThrow(
							new SQLException("mock SQL Exception", "db down",
									12526))
					.thenThrow(
							new SQLException("mock SQL Exception", "db down",
									1033));
			for (int i = 0; i < STATUS_ARRAY.length; i++) {
				STATUS_ARRAY[i] = org.powermock.reflect.Whitebox
						.<StringConstants.DBSTATUS> invokeMethod(obj,
								"connect", testDbServer, testPort, testMeta,
								testUsername, testPass);
			}
		} catch (Exception e) {
		}
		for (StringConstants.DBSTATUS STATUS : STATUS_ARRAY) {
			assertEquals(StringConstants.DBSTATUS.DBDOWN, STATUS);
		}
    }

    @Test
    public void testConnectWithArgumentsSQLExceptionForDBERROR() {
		String url = "jdbc:oracle:thin:@" + testDbServer + ":" + testPort + ":"
				+ testMeta;
		PowerMockito.when(mockRd.getValue("Timeout_for_connect_database"))
				.thenReturn("10");
		StringConstants.DBSTATUS STATUS = null;
		try {
			PowerMockito.mockStatic(DriverManager.class);
			PowerMockito.when(
					DriverManager.getConnection(url, testUsername, testPass))
					.thenThrow(
							new SQLException("mock SQL Exception", "db error",
									17002));
			STATUS = org.powermock.reflect.Whitebox
					.<StringConstants.DBSTATUS> invokeMethod(obj, "connect",
							testDbServer, testPort, testMeta, testUsername,
							testPass);
		} catch (Exception e) {
		}
		assertEquals(StringConstants.DBSTATUS.DBERROR, STATUS);
    }
    
    @Test
    public void testCloseConnection() {

        try {
            doNothing().doThrow(new SQLException()).when(mockConn).close();
        } catch (SQLException e) {
        }

        obj.closeConnection();
        obj.closeConnection();
        try {
            verify(mockConn, times(2)).close();
        } catch (SQLException e) {
        }
    }

    @Test
    public void testrunQueryMultipleRows() {

        queryString = "Test query";
        try {
            when(mockConn.createStatement()).thenReturn(mockSt);
            when(mockSt.executeQuery(queryString)).thenReturn(mockRs)
                    .thenThrow(new SQLException());
            doNothing().doThrow(new SQLException()).when(mockSt).close();
            when(mockRs.next()).thenReturn(false);
        } catch (SQLException e1) {
        }

        rs = obj.runQueryMultipleRows(queryString, 1);
        assertTrue(rs.isEmpty());
        rs = obj.runQueryMultipleRows(queryString, 1);
        assertTrue(rs.isEmpty());
    }

    @Test
    public void testrunQuerySingleRow() {

        queryString = "Test query";
        try {
            when(mockConn.createStatement()).thenReturn(mockSt);
            when(mockSt.executeQuery(queryString)).thenReturn(mockRs)
                    .thenThrow(new SQLException());
            doNothing().doThrow(new SQLException()).when(mockSt).close();
            when(mockRs.next()).thenReturn(false);
        } catch (SQLException e1) {
        }

        vs = obj.runQuerySingleRow(queryString, 1);
        assertTrue(vs.isEmpty());
        vs = obj.runQuerySingleRow(queryString, 1);
        assertTrue(vs.isEmpty());
    }

    @Test
    public void testSetCommit() {

        try {
            doThrow(new SQLException()).when(mockConn).setAutoCommit(true);
        } catch (SQLException e) {
        }
        obj.setCommit(true);
        try {
            verify(mockConn).setAutoCommit(true);
        } catch (SQLException e) {
        }
    }

    @Test
    public void testDoRollBack() {

        try {
            doNothing().doThrow(new SQLException()).when(mockConn)
                    .setAutoCommit(true);
        } catch (SQLException e) {
        }
        obj.doRollback();
        obj.doRollback();
        try {
            verify(mockConn, times(2)).setAutoCommit(true);
            verify(mockConn, times(2)).rollback();
        } catch (SQLException e) {
        }
    }

    @Test
    public void testDoCommit() {

        try {
            doNothing().doThrow(new SQLException()).when(mockConn)
                    .setAutoCommit(true);
        } catch (SQLException e) {
        }
        assertEquals(1, obj.doCommit());
        assertEquals(0, obj.doCommit());
        try {
            verify(mockConn, times(2)).setAutoCommit(true);
            verify(mockConn, times(2)).commit();
        } catch (SQLException e) {
        }
    }

    @Test
    public void testGetExceptionMessage() {
        assertEquals(null, obj.getExceptionMessage());
    }

    @Test
    public void testgetExceptionCode() {
        assertEquals(0, obj.getExceptionCode());
    }

    @Test
    public void testRunUpdate() {

        queryString = "Test query";
        try {
            when(mockConn.createStatement()).thenReturn(mockSt);
            when(mockSt.executeUpdate(queryString)).thenReturn(1)
                    .thenThrow(new SQLException("e1", "e11", 1)).thenReturn(1);
            doNothing().doThrow(new SQLException("e2", "e21", 2)).when(mockSt)
                    .close();

        } catch (SQLException e1) {
        }

        assertEquals(0, obj.runUpdate(queryString));
        assertEquals(1, obj.runUpdate(queryString));
        // doNothing().doThrow(new
        // SQLException("e2","e21",2)).when(mockSt).close(); throws exception
        // the 2nd time. does nothing first time
        assertEquals(1, obj.runUpdate(queryString));
    }

}
