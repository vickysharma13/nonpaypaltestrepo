package com.paypal.sea.s2dbservices.oracledbaccess;

import java.sql.*;
//import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import com.paypal.sea.s2dbservices.MyLogger;
import com.paypal.sea.s2dbservices.ReadProperty;
import com.paypal.sea.s2dbservices.StackTraceUtil;
import com.paypal.sea.s2dbservices.StringConstants;
import com.paypal.sea.s2dbservices.StringConstants.DBSTATUS;

public class OracleConnection2 {
	private Connection mConn = null;

	private static final int ORA_12505 = 12505;
	private static final int ORA_17002 = 17002; // TNS:listener does not
												// currently know of SID given
												// in connect descriptor
	private static final int ORA_12528 = 12528; // TNS:listener: all appropriate
												// instances are blocking new
												// connections
	private static final int ORA_12526 = 12526; // TNS:listener: all appropriate
												// instances are in restricted
												// mode
	private static final int ORA_01033 = 1033; // ORACLE initialization or
												// shutdown in progress

	private String mExceptionMessage;
	private int mExceptionCode;
	private StringConstants.DBSTATUS mConnStatus;
	private ReadProperty rd;

	public OracleConnection2() {
		rd = new ReadProperty();
		connect();
	}

	public OracleConnection2(Connection con, ReadProperty read) {
		rd = read;
		mConn = con;
	}

	public OracleConnection2(String serverName, String portNumber,
			String serviceId, String userName, String password) {
		rd = new ReadProperty();

		connect(serverName, portNumber, serviceId, userName, password);
	}

	public void closeConnection() {
		try {
			if (mConn != null) {
				mConn.close();
			}
		} catch (SQLException e) {
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException occured while closing the connection "
							+ StackTraceUtil.getStackTrace(e));
		}
	}

	private DBSTATUS connect() {
		String serverName = rd.getValue("db_server");
		String portNumber = rd.getValue("Port_number");
		String sid = rd.getValue("Metadata_Db_sid");
		String username = rd.getValue("Username");
		String password = rd.getValue("Password");

		return connect(serverName, portNumber, sid, username, password);
	}

	public StringConstants.DBSTATUS getDBStatus() {
		return mConnStatus;
	}

	private StringConstants.DBSTATUS connect(String serverName,
			String portNumber, String serviceId, String userName,
			String password) {
		if (mConn != null) {
			closeConnection();
		}

		mExceptionMessage = "";
		mExceptionCode = 0;
		mConnStatus = StringConstants.DBSTATUS.DBERROR;
		try {
			// Load the JDBC driver
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);

			// Create a connection to the database
			String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber
					+ ":" + serviceId;

			int timeOut = Integer.parseInt(rd
					.getValue("Timeout_for_connect_database"));
			DriverManager.setLoginTimeout(timeOut);

			mConn = DriverManager.getConnection(url, userName, password);

		} catch (ClassNotFoundException e) {
			MyLogger.getInstance().log(
					Level.SEVERE,
					"ClassNotFoundException while trying to make connection to DB"
							+ StackTraceUtil.getStackTrace(e));
			mExceptionCode = StringConstants.ERROR_CODES.INTERNAL_ERROR
					.ordinal();
			mExceptionMessage = e.getMessage();
			return StringConstants.DBSTATUS.DBERROR;
			// Could not find the database driver
		}

		catch (SQLException e) {
			mExceptionCode = e.getErrorCode();
			mExceptionMessage = e.getMessage();
			MyLogger.getInstance().log(
					Level.SEVERE,
					"Service Id : " + serviceId + " Exception code: "
							+ mExceptionCode + " Exception message: "
							+ mExceptionMessage);

			if (mExceptionCode == ORA_12505 || mExceptionCode == ORA_12528
					|| mExceptionCode == ORA_12526
					|| mExceptionCode == ORA_01033) // db
													// is
													// shutdown,
													// ORA-12505
			{
				mConnStatus = StringConstants.DBSTATUS.DBDOWN;
				return StringConstants.DBSTATUS.DBDOWN;
			}
			mConnStatus = StringConstants.DBSTATUS.DBERROR;
			return StringConstants.DBSTATUS.DBERROR;
		}

		mConnStatus = StringConstants.DBSTATUS.DBUP;
		return StringConstants.DBSTATUS.DBUP;
	}

	public Vector<String> runQuerySingleRow(String queryString, int colCount) {
		Statement stmt = null;
		ResultSet rset = null;

		MyLogger.getInstance().log(Level.INFO, queryString);
		Vector<String> retArray = new Vector<String>();

		mExceptionMessage = "";
		mExceptionCode = 0;
		try {
			if (mConn == null) {
				throw new CatlogDBException();
			}
			stmt = mConn.createStatement();
			rset = stmt.executeQuery(queryString);
			while (rset.next()) {
				for (int cnt = 0; cnt < colCount; ++cnt) {
					retArray.add(rset.getString(cnt + 1));
				}
			}
		} catch (SQLException e) {
			mExceptionCode = e.getErrorCode();
			mExceptionMessage = e.getMessage();
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException in runQuerySingleRow function while executing the query"
							+ StackTraceUtil.getStackTrace(e));
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return retArray;
	}

	public Vector<Vector<String>> runQueryMultipleRows(String queryString,
			int colCount) {
		Statement stmt = null;
		ResultSet rset = null;
		Vector<Vector<String>> mrs = new Vector<Vector<String>>();
		mExceptionMessage = "";
		mExceptionCode = 0;
		MyLogger.getInstance().log(Level.INFO, queryString);

		try {
			if (mConn == null) {
				throw new CatlogDBException();
			}
			stmt = mConn.createStatement();
			rset = stmt.executeQuery(queryString);
			while (rset.next()) {
				Vector<String> rs = new Vector<String>();
				for (int cnt = 0; cnt < colCount; ++cnt) {
					rs.add(rset.getString(cnt + 1));
				}
				mrs.add(rs);
			}
		} catch (SQLException e) {
			mExceptionCode = e.getErrorCode();
			mExceptionMessage = e.getMessage();
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException in runQueryMultipleRows function while executing the query"
							+ StackTraceUtil.getStackTrace(e));
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return mrs;
	}

	public int runUpdate(String queryString) {
		int retValue = 0;
		Statement stmt = null;
		mExceptionMessage = "";
		mExceptionCode = 0;

		try {
			stmt = mConn.createStatement();
			stmt.executeUpdate(queryString);
			MyLogger.getInstance().log(Level.INFO, queryString);
		} catch (SQLException e) {
			mExceptionCode = e.getErrorCode();
			mExceptionMessage = e.getMessage();
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException in runUpdate function while executing the query"
							+ StackTraceUtil.getStackTrace(e));
			retValue = 1;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				retValue = 1;
			}
		}
		return retValue;
	}

	public int doCommit() {
		try {
			mConn.commit();
			mConn.setAutoCommit(true);

		} catch (SQLException e) {
			mConn = null;
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException while doing commit"
							+ StackTraceUtil.getStackTrace(e));
			return 0;
		}
		MyLogger.getInstance().log(Level.INFO,
				"Transaction successful. COMMIT completed");
		return 1;
	}

	public void doRollback() {
		try {
			mConn.rollback();
			mConn.setAutoCommit(true);
		} catch (SQLException e) {
			mConn = null;
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException while doing rollback"
							+ StackTraceUtil.getStackTrace(e));
		}
		MyLogger.getInstance().log(Level.SEVERE,
				"Transaction unsuccessful. ROLLBACK completed");
	}

	public void setCommit(boolean state) {
		try {
			mConn.setAutoCommit(state);
		} catch (SQLException e) {
			mConn = null;
			MyLogger.getInstance().log(
					Level.SEVERE,
					"SQLException while changing autocommit status"
							+ StackTraceUtil.getStackTrace(e));
		}

	}

	public String getExceptionMessage() {
		return mExceptionMessage;
	}

	public int getExceptionCode() {
		return mExceptionCode;
	}
}
