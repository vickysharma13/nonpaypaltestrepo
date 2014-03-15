package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import com.paypal.sea.s2dbservices.MyLogger;
import com.paypal.sea.s2dbservices.StringConstants;

public class StageDBDataAccess {
	private long mBegTime;
	private long mEndTime;
	private static final int ORA_17002 = 17002;
	private static final int ORA_12518 = 12518;
	private static final int ORA_12519 = 12519;
	private static final int TIMEOUT = 120;
	private static final int LISTENER_RELOAD_ON = 1;
	private static final int LISTENER_RELOAD_OFF = 0;
	private static Lock lock = new ReentrantLock();

	public int connectStageDB(String stageName, StageDetails mStg) {
		String pod = mStg.getPodName();
		String sidname = "qadba";
		String cloneType = mStg.getCloneType().toLowerCase();
		String userName = "clocapp";
		String passwd = "clocappstg";
		sidname += pod;
		String qualifiedDbName = mStg.getDBServerName() + ".qa.paypal.com";

		MyLogger.getInstance().log(Level.FINE,
				"db : " + qualifiedDbName + " sid :" + sidname);

		mBegTime = System.currentTimeMillis();

		OracleConnection2 mConn = new OracleConnection2(qualifiedDbName,
				"2126", sidname, userName, passwd);
		StringConstants.DBSTATUS ret = mConn.getDBStatus();

		if (ret == StringConstants.DBSTATUS.DBERROR) {

			int exceptionCode = mConn.getExceptionCode();
			if (exceptionCode == ORA_17002 || exceptionCode == ORA_12518
					|| exceptionCode == ORA_12519) {
				ret = reloadListenerAndConnect(mConn, mStg);
			}
			mStg.setExceptionCode(mConn.getExceptionCode());
			mStg.setExceptionMessage(mConn.getExceptionMessage());
		}

		mStg.setDBStatus(ret);
		mConn.closeConnection();
		mEndTime = System.currentTimeMillis();

		MyLogger.getInstance().log(
				Level.FINE,
				"Time taken to connectStageDB() " + (mEndTime - mBegTime)
						+ " ms");

		if (ret == StringConstants.DBSTATUS.DBERROR) {
			return 0;
		}

		if (cloneType.contains("pay")) {
			sidname = "qadbb";
			userName = "pay";
			passwd = "pay";
			sidname += pod;
			MyLogger.getInstance().log(Level.INFO,
					"db : " + qualifiedDbName + " sid :" + sidname);
			mBegTime = System.currentTimeMillis();

			mConn = new OracleConnection2(qualifiedDbName, "2126", sidname,
					userName, passwd);

			mStg.setDbStatusPay(mConn.getDBStatus());
			mConn.closeConnection();
			mEndTime = System.currentTimeMillis();

			MyLogger.getInstance().log(
					Level.INFO,
					"Time taken to connectStageDB() " + (mEndTime - mBegTime)
							+ " ms");
		}

		if (cloneType.contains("paypilot")) {
			// stageConn = new OracleConnection2();
			sidname = "qadbc";
			userName = "pay";
			passwd = "pay";
			sidname += pod;
			MyLogger.getInstance().log(Level.INFO,
					"db : " + qualifiedDbName + " sid :" + sidname);

			mBegTime = System.currentTimeMillis();
			mConn = new OracleConnection2(qualifiedDbName, "2126", sidname,
					userName, passwd);

			mStg.setDbStatusPayPilot(mConn.getDBStatus());
			mConn.closeConnection();

			mEndTime = System.currentTimeMillis();

			MyLogger.getInstance().log(
					Level.INFO,
					"Time taken to connectStageDB() " + (mEndTime - mBegTime)
							+ " ms");
		}
		mConn.closeConnection();
		return 0;
	}

	private StringConstants.DBSTATUS reloadListenerAndConnect(
			OracleConnection2 mConn, StageDetails mStg) {

		DbConnection db = new DbConnection();
		CatalogDataAccess mODA = new CatalogDataAccess();
		String dbServer = mStg.getDBServerName();
		StringConstants.DBSTATUS ret = StringConstants.DBSTATUS.DBERROR;

		if (mODA.isListenerReloadInProgress(dbServer)) {
			ret = waitAndConnect(mConn, mStg, mODA);
		} else {

			if (lock.tryLock()) {
				try {
					mODA.updateListenerStatus(dbServer, LISTENER_RELOAD_ON);
					Long beginTime = System.currentTimeMillis();
					int retCode = db.reloadListener(mStg.getDBServerName());
					if (retCode == StringConstants.ERROR_CODES.NO_ERROR
							.ordinal()) {
						MyLogger.getInstance().log(Level.INFO,
								"Listener reload successful for " + dbServer);
						mODA.saveServerMetrics("listener_reload",
								(System.currentTimeMillis() - beginTime),
								StringConstants.ERROR_CODES.NO_ERROR.ordinal(),
								dbServer, mConn.getExceptionCode(),
								mConn.getExceptionMessage(),
								mStg.getStageName());
						ret = connect(mStg, mConn);
					} else if (retCode == StringConstants.ERROR_CODES.LISTENER_FLAG_OFF
							.ordinal()
							|| retCode == StringConstants.ERROR_CODES.SCRIPT_NAME_INCORRECT
									.ordinal()) {
						mODA.saveServerMetrics("listener_reload",
								(System.currentTimeMillis() - beginTime),
								retCode, dbServer, mConn.getExceptionCode(),
								mConn.getExceptionMessage(),
								mStg.getStageName());
					} else {
						MyLogger.getInstance().log(Level.SEVERE,
								"Listener reload failed for " + dbServer);
						mODA.saveServerMetrics("listener_reload", (System
								.currentTimeMillis() - beginTime),
								StringConstants.ERROR_CODES.INTERNAL_ERROR
										.ordinal(), dbServer, mConn
										.getExceptionCode(), mConn
										.getExceptionMessage(), mStg
										.getStageName());
					}
				} finally {
					mODA.updateListenerStatus(dbServer, LISTENER_RELOAD_OFF);
					lock.unlock();
				}
			} else {
				ret = waitAndConnect(mConn, mStg, mODA);
			}
		}
		return ret;
	}

	private StringConstants.DBSTATUS waitAndConnect(OracleConnection2 mConn,
			StageDetails mStg, CatalogDataAccess mODA) {
		StringConstants.DBSTATUS ret = StringConstants.DBSTATUS.DBERROR;
		String dbServer = mStg.getDBServerName();
		MyLogger.getInstance().log(Level.INFO, "Waiting for listener reload");
		int count = 0;
		while (count++ < TIMEOUT) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!mODA.isListenerReloadInProgress(dbServer)) {
				ret = connect(mStg, mConn);
				break;
			}
		}
		return ret;
	}

	private StringConstants.DBSTATUS connect(StageDetails mStg,
			OracleConnection2 mConn) {
		String dbServer = mStg.getDBServerName();
		String pod = mStg.getPodName();
		String sidname = "qadba";
		String userName = "clocapp";
		String passwd = "clocappstg";
		sidname += pod;
		String qualifiedDbName = dbServer + ".qa.paypal.com";
		mConn = new OracleConnection2(qualifiedDbName, "2126", sidname,
				userName, passwd);
		return (mConn.getDBStatus());
	}

}
