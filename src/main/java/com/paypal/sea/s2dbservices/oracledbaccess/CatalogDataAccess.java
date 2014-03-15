package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import com.paypal.sea.s2dbservices.MyLogger;
import com.paypal.sea.s2dbservices.ReadProperty;
import com.paypal.sea.s2dbservices.StringConstants;
import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy.StageNames;

public class CatalogDataAccess {
	private static final int POD_NAME_LENGTH = 3;
	private OracleConnection2 mCatConn;
	private long mBegTime;
	private long mEndTime;
	private ReadProperty rd;

	private enum DPH_COL_POS {
		POD_NAME_POS, POD_SERVER_NAME_POS, NO_OF_COLUMNS
	};

	private enum SS_COL_POS {
		MAJOR_VER_POS, MINOR_VER_POS, CREATED_DATE_POS, SNAPSHOT_NO_OF_COLUMNS
	};

	private enum METRICS_COL_POS {
		DATE_POS, API_TYPE_POS, COUNT_POS, ERROR_COUNT_POS, ELAPSED_TIME_POS, MAX_TIME_POS, MIN_TIME_POS, METRICS_NO_OF_COLUMNS
	};

	private enum SERVER_CAPACITY_POS {
		NAME_POS, CAPACITY_POS, POD_COUNT_POS, AVAILABLE_POS, CAPACITY_NO_OF_COLUMNS
	};

	private enum CLONEHIST_COL_POS {
		REQUEST_ID_POS, END_TIME_POS, CURRENT_STATUS_POS, EXTENDED_STATUS_POS, FAILURE_CAUSE_POS, SNAPSHOT_VERSION_POS, CLONE_HISTORY_NO_OF_COLUMNS
	};

	private enum STAGE_COL_POS {
		STAGE_NO_OF_COLUMNS
	};

	private enum CLONEDET_COL_POS {
		CLONE_REQ_ID_POS, CLONE_VERSION_POS, Dmmp_POS, CLONE_DATE_POS, CLONE_STATUS_POS, CLONE_TYPE_POS, CLONE_CYCLE_POS, CLONE_MASTER_VER_POS, CLONE_NO_OF_COLUMNS
	};

	private enum MASTERSTAGE_COL_POS {
		MS_NAME_POS, MASTERSTAGE_NO_OF_COLUMNS
	};

	private enum POD_COL_POS {
		POD_SERVER_NAME_POS, POD_NAME_POS, CURRENT_STATUS_POS, MONITOR_DB_POS, VERSION_POS, POD_NO_OF_COLUMNS
	};

	private enum CLONE_STATUS_POS {
		GSM_STATUS_POS, CLONE_STATUS_NO_OF_COLUMNS
	};

	private enum SINGLE_VALUE_COL_POS {
		VALUE_POS, VALUE_NO_OF_COL
	};

	private enum CLONE_STATUS_COL_POS {
		MASTER_VERSION_POS, STAGE_NAME_POS, CLONE_VER_POS, CLONE_START_TIME_POS, CLONE_END_TIME_POS, QUEUE_START_TIME_POS, QUEUE_END_TIME_POS, CURRENT_STATUS_POS, EXTENDED_STATUS_POS, CLONE_TYPE_POS, CLONE_CYCLE_POS, LOG_POS, CLONE_STATUS_NO_OF_COL
	};

	public CatalogDataAccess() {
		mCatConn = new OracleConnection2();
		rd = new ReadProperty();
	}

	public CatalogDataAccess(OracleConnection2 oc2, ReadProperty obj) {
		mCatConn = oc2;
		rd = obj;
	}

	public Vector<SnapshotDetail> getSnapshotVersions() {
		String queryStr;
		queryStr = "SELECT master_version, snapshot_version, created FROM snapshots WHERE current_status = 'ACTIVE' order by created";

		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				SS_COL_POS.SNAPSHOT_NO_OF_COLUMNS.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		Vector<SnapshotDetail> retResult = new Vector<SnapshotDetail>();
		SnapshotDetail sd = null;
		for (Vector<String> vs : mrs) {
			sd = new SnapshotDetail();
			sd.setMajorVersion(vs.get(SS_COL_POS.MAJOR_VER_POS.ordinal()));
			sd.setVersion(vs.get(SS_COL_POS.MINOR_VER_POS.ordinal()));
			sd.setSnapshotDate(vs.get(SS_COL_POS.CREATED_DATE_POS.ordinal()));
			retResult.add(sd);
		}
		return retResult;
	}

	public Hierarchy getHierarchy(String stage) {

		String queryStr1, queryStr4;
		queryStr1 = "select WEBSERVER_FRIENDLY from pods WHERE WEBSERVER_DBA in (select WEBSERVER_DBA from pods where WEBSERVER_FRIENDLY='"
				+ stage + "') AND CURRENT_STATUS = 'Ready'";

		queryStr4 = "select WEBSERVER_FRIENDLY from pods WHERE WEBSERVER_DBA in (select WEBSERVER_DBA from pods where WEBSERVER_FRIENDLY='"
				+ stage + "') AND CURRENT_STATUS = 'NOT Ready'";

		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr1, 1);

		Vector<Vector<String>> nrs = mCatConn
				.runQueryMultipleRows(queryStr4, 1);
		if (mrs.size() == SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()
				&& nrs.size() == SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()) {
			return null;
		}
		String master = "";
		if (mrs.size() > SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()) {
			master = mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
		}

		List<StageNames> childStages = new ArrayList<Hierarchy.StageNames>();
		Hierarchy.StageNames child = new Hierarchy.StageNames();

		for (Vector<String> i : nrs) {
			child.setStage(i.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()));
			childStages.add(child);
		}

		return new Hierarchy(master, childStages);
	}

	public Vector<CloneHistory> getCloneHistory(String stage) {
		String queryStr;
		queryStr = "select REQUEST_ID, nvl(to_char(CLONE_END_TIME),' '), nvl(CURRENT_STATUS,' '), nvl(EXTENDED_STATUS,' '), nvl(CAUSE_OF_FAILURE,' '), nvl(SNAPSHOT_VERSION,' ') "
				+ "from gsm_ops where lower(DB_STAGE_TO) like "
				+ "lower(\'"
				+ stage + "\')" + " order by CLONE_END_TIME";
		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				CLONEHIST_COL_POS.CLONE_HISTORY_NO_OF_COLUMNS.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		Vector<CloneHistory> resultSet = new Vector<CloneHistory>();
		CloneHistory ch = null;
		for (Vector<String> vs : mrs) {
			ch = new CloneHistory();
			ch.setRequestID(vs.get(CLONEHIST_COL_POS.REQUEST_ID_POS.ordinal()));
			ch.setEndTime(vs.get(CLONEHIST_COL_POS.END_TIME_POS.ordinal()));
			ch.setCurrentStatus(vs.get(CLONEHIST_COL_POS.CURRENT_STATUS_POS
					.ordinal()));
			ch.setExtendedStatus(vs.get(CLONEHIST_COL_POS.EXTENDED_STATUS_POS
					.ordinal()));
			ch.setFailureCause(vs.get(CLONEHIST_COL_POS.FAILURE_CAUSE_POS
					.ordinal()));
			ch.setSnapshotVersion(vs.get(CLONEHIST_COL_POS.SNAPSHOT_VERSION_POS
					.ordinal()));
			resultSet.add(ch);
		}
		return resultSet;
	}

	public Vector<ServerDetails> getStages(String server) {

		Vector<ServerDetails> resultSet = new Vector<ServerDetails>();
		String queryStr2;

		if (server.equals("all")) {
			queryStr2 = "is not null";
		} else {
			queryStr2 = "=\'" + server + "\'";
		}
		String queryStr1 = "select unique DATABASE_SERVER,WEBSERVER_FRIENDLY from pods where DATABASE_SERVER ";
		String queryStr3 = " order by DATABASE_SERVER,WEBSERVER_FRIENDLY";
		String queryString = queryStr1 + queryStr2 + queryStr3;

		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryString,
				STAGE_COL_POS.STAGE_NO_OF_COLUMNS.ordinal() + 2);

		if (mrs.size() == 0) {
			return null;
		}

		List<ServerDetails.StageNames> stageNames = null;
		String prevServer = "";
		String currentServer = "";

		for (Vector<String> result : mrs) {
			currentServer = result.get(STAGE_COL_POS.STAGE_NO_OF_COLUMNS
					.ordinal());
			if (prevServer == "") {
				prevServer = currentServer;
				stageNames = new ArrayList<ServerDetails.StageNames>();
			}
			if (!(prevServer.equals(currentServer))) {
				resultSet.add(new ServerDetails(prevServer, stageNames));
				prevServer = currentServer;
				stageNames = new ArrayList<ServerDetails.StageNames>();
			}
			ServerDetails.StageNames stage = new ServerDetails.StageNames();
			stage.setStage(result.get(STAGE_COL_POS.STAGE_NO_OF_COLUMNS
					.ordinal() + 1));
			stageNames.add(stage);
		}
		resultSet.add(new ServerDetails(prevServer, stageNames));
		return resultSet;
	}

	public CloneDetails getCloneDetailsByID(String requestID) {
		String queryStr;
		queryStr = "select DB_CLONE_FROM, DB_STAGE_TO, SNAPSHOT_VERSION, nvl(to_char(CLONE_START_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), "
				+ "nvl(to_char(CLONE_END_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), nvl(to_char(QUEUE_START_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), "
				+ "nvl(to_char(QUEUE_END_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), nvl(CURRENT_STATUS,' '), nvl(EXTENDED_STATUS,' '), "
				+ "SID_LIST, CYCLE_FOR_CLONE, nvl(CAUSE_OF_FAILURE,' ') from gsm_ops where REQUEST_ID = "
				+ requestID;
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				CLONE_STATUS_COL_POS.CLONE_STATUS_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		CloneDetails result = new CloneDetails();
		result.setMasterVersion(mrs.get(CLONE_STATUS_COL_POS.MASTER_VERSION_POS
				.ordinal()));
		result.setStageName(mrs.get(CLONE_STATUS_COL_POS.STAGE_NAME_POS
				.ordinal()));
		result.setCloneVersion(mrs.get(CLONE_STATUS_COL_POS.CLONE_VER_POS
				.ordinal()));
		result.setCloneStartTime(mrs
				.get(CLONE_STATUS_COL_POS.CLONE_START_TIME_POS.ordinal()));
		result.setCloneEndTime(mrs.get(CLONE_STATUS_COL_POS.CLONE_END_TIME_POS
				.ordinal()));
		result.setQueueStartTime(mrs
				.get(CLONE_STATUS_COL_POS.QUEUE_START_TIME_POS.ordinal()));
		result.setQueueEndTime(mrs.get(CLONE_STATUS_COL_POS.QUEUE_END_TIME_POS
				.ordinal()));
		result.setCurrentStatus(mrs.get(CLONE_STATUS_COL_POS.CURRENT_STATUS_POS
				.ordinal()));
		result.setExtendedStatus(mrs
				.get(CLONE_STATUS_COL_POS.EXTENDED_STATUS_POS.ordinal()));
		result.setCloneType(mrs.get(CLONE_STATUS_COL_POS.CLONE_TYPE_POS
				.ordinal()));
		result.setCloneCycle(mrs.get(CLONE_STATUS_COL_POS.CLONE_CYCLE_POS
				.ordinal()));
		result.setLog(mrs.get(CLONE_STATUS_COL_POS.LOG_POS.ordinal()));
		return result;
	}

	public String getSnapshotVersion(String majorVersion) {
		String queryStr;
		queryStr = "select MASTER_VERSION, SNAPSHOT_VERSION, CREATED "
				+ "from snapshots where lower(current_status) like 'active'"
				+ " and upper(MASTER_VERSION) like '"
				+ majorVersion.toUpperCase() + "'"
				+ " order by MASTER_VERSION ";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SS_COL_POS.SNAPSHOT_NO_OF_COLUMNS.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		return mrs.get(SS_COL_POS.MINOR_VER_POS.ordinal());
	}

	public StageDetails findDBInfo(String stageName) {
		mBegTime = System.currentTimeMillis();
		StageDetails mStg = new StageDetails();
		String queryStr;
		Vector<String> rs;
		mBegTime = System.currentTimeMillis();

		queryStr = "select database_server, webserver_dba, current_Status, monitor_db, version from pods "
				+ "where lower(WEBSERVER_FRIENDLY) like "
				+ "lower(\'"
				+ stageName + "\')";

		rs = mCatConn.runQuerySingleRow(queryStr,
				POD_COL_POS.POD_NO_OF_COLUMNS.ordinal());
		mStg.setStageName(stageName);

		if (rs.size() == 0) {
			// mStg.setErrorCode (StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
			return mStg;
		}
		mStg.setCloneVersion(rs.get(POD_COL_POS.VERSION_POS.ordinal()));
		mStg.setPodName(rs.get(POD_COL_POS.POD_NAME_POS.ordinal()));
		mStg.setDBServerName(rs.get(POD_COL_POS.POD_SERVER_NAME_POS.ordinal()));
		String currentStatus = rs.get(POD_COL_POS.CURRENT_STATUS_POS.ordinal());
		boolean isCloneable = currentStatus.equalsIgnoreCase("ready");
		mStg.setIsCloneable(isCloneable);

		if (!mStg.getIsCloneable()) {
			String masterStageName = getMasterStageName(mStg);
			if (!stageName.equals(masterStageName)) {
				mStg.setMasterStageName(masterStageName);
			}
			rs = getCloneDetails(masterStageName);
		} else {
			rs = getCloneDetails(stageName);
		}
		if (rs != null) {
			mStg.setCloneVersion(rs.get(CLONEDET_COL_POS.CLONE_VERSION_POS
					.ordinal()));
			mStg.setCloneDate(rs.get(CLONEDET_COL_POS.CLONE_DATE_POS.ordinal()));
			mStg.setCloneStatus(rs.get(CLONEDET_COL_POS.CLONE_STATUS_POS
					.ordinal()));
			mStg.setCloneType(rs.get(CLONEDET_COL_POS.CLONE_TYPE_POS.ordinal()));
			mStg.setCloneCycle(rs.get(CLONEDET_COL_POS.CLONE_CYCLE_POS
					.ordinal()));
			mStg.setLatestVersion(getMasterVersion(rs
					.get(CLONEDET_COL_POS.CLONE_MASTER_VER_POS.ordinal())));
		}

		String cloneType = mStg.getCloneType();
		String cloneVersion = mStg.getCloneVersion();
		StageDBDataAccess sdb = new StageDBDataAccess();
		if (cloneType != null && cloneType.length() > 0) {
			if (mStg.getMasterStageName().length() > 0) {
				sdb.connectStageDB(mStg.getMasterStageName(), mStg);
			} else {
				sdb.connectStageDB(stageName, mStg);
			}
		} else if ((mStg.getIsCloneable() == false && mStg.getMasterStageName()
				.length() == 0) || (cloneVersion.length() > 0)) {
			sdb.connectStageDB(stageName, mStg);
		}
		StringConstants.DBSTATUS dbs = mStg.getDBStatus();
		if ((dbs == StringConstants.DBSTATUS.DBUP)
				|| (dbs == StringConstants.DBSTATUS.DBDOWN)) {
			mStg.setErrorCode(StringConstants.ERROR_CODES.NO_ERROR);
		} else if (dbs == StringConstants.DBSTATUS.DBERROR) {
			mStg.setErrorCode(StringConstants.ERROR_CODES.INTERNAL_ERROR);
		} else {
			mStg.setErrorCode(StringConstants.ERROR_CODES.NO_ERROR);
		}
		return mStg;
	}

	private String getMasterStageName(StageDetails mStg) {
		String queryStr = "select WEBSERVER_FRIENDLY from pods where webserver_Dba like '"
				+ mStg.getPodName() + "' and lower(current_status) = 'ready'";
		Vector<String> rs = mCatConn.runQuerySingleRow(queryStr,
				MASTERSTAGE_COL_POS.MASTERSTAGE_NO_OF_COLUMNS.ordinal());

		if (rs.size() == 0) // not a child stage
		{
			return mStg.getStageName();
		}
		return (rs.get(MASTERSTAGE_COL_POS.MS_NAME_POS.ordinal()));
	}

	public String findUniqueName(String beginPodName) {
		if (beginPodName.length() != POD_NAME_LENGTH) {
			return null;
		}
		String temps = beginPodName.toUpperCase();
		char ltr3 = temps.charAt(2); // get the last letter
		char ltr2 = temps.charAt(1); //
		char ltr1 = temps.charAt(0); //

		if (isNextSeries(ltr3) == 1) {
			ltr3 = getNextChar(ltr3);
			if (isNextSeries(ltr2) == 1) {
				ltr2 = getNextChar(ltr2);
				if (isNextSeries(ltr1) == 1) {
					ltr1 = getNextChar(ltr1);
				} else {
					ltr1 = getNextChar(ltr1);
				}
			} else {
				ltr2 = getNextChar(ltr2);
			}
		} else {
			ltr3 = getNextChar(ltr3);
		}

		String rets = Character.toString(ltr1) + Character.toString(ltr2)
				+ Character.toString(ltr3);
		rets = rets.toUpperCase();
		return rets;
	}

	private static int isNextSeries(char currentChar) {
		int retn = 0;
		if (currentChar == 'Z') {
			retn = 1;
			// incr next letter
		}
		return retn;
	}

	/*
	 * returns next char given the current char. the sequence is A-Z-0-9 For if
	 * currentChar is A, returns B For if currentChar is Z, returns 0 For if
	 * currentChar is 0, returns 1 For if currentChar is 9, returns A
	 */

	private static char getNextChar(char currentChar) {
		char retc = currentChar;
		if (currentChar == 'Z') {
			retc = '0';
		} else if (currentChar == '9') {
			retc = 'A';
			// incr next letter
		} else {
			++retc;
		}
		return retc;
	}

	public String getRequestID() {
		String sequenceName = "gsm_ops_seq";
		String queryStr = "select " + sequenceName + ".nextval from dual";
		Vector<String> rs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		return rs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
	}

	public int createCloneRequest(String stageName, String majorDbVersion,
			String cloneCycle, String cloneOption, String requestID) {
		String minorVersion = getSnapshotVersion(majorDbVersion);
		if (minorVersion == null || minorVersion.length() == 0) {
			return StringConstants.ERROR_CODES.VERSION_NOT_EXISTS.ordinal();
		}
		String subQuery = "(select max(request_id) from gsm_ops where lower(db_Stage_to) like '"
				+ stageName + "')";

		String queryStr = "select current_status from gsm_ops "
				+ "where request_id = " + subQuery;
		Vector<String> rs = mCatConn.runQuerySingleRow(queryStr,
				CLONE_STATUS_POS.CLONE_STATUS_NO_OF_COLUMNS.ordinal());

		int retValue = 1;
		if (rs.size() == 0
				|| rs.get(CLONE_STATUS_POS.GSM_STATUS_POS.ordinal())
						.equalsIgnoreCase("success")
				|| rs.get(CLONE_STATUS_POS.GSM_STATUS_POS.ordinal())
						.equalsIgnoreCase("failed")) {

			String target_metadata_db = rd.getValue("Metadata_Db_sid");
			synchronized (this) {
				queryStr = "insert into gsm_ops values" + "(" + requestID
						+ ",'" + majorDbVersion + "','" + stageName
						+ "',sysdate,sysdate,sysdate,sysdate,'QUEUED',"
						+ "null,'" + cloneOption + "','" + target_metadata_db
						+ "','" + cloneCycle + "',null,null,'" + minorVersion
						+ "')";

				mCatConn.setCommit(true);
				retValue = mCatConn.runUpdate(queryStr);
			}
		} else {
			String currentStatus = rs.get(CLONE_STATUS_POS.GSM_STATUS_POS
					.ordinal());
			if (currentStatus.equalsIgnoreCase("queued")
					|| currentStatus.equalsIgnoreCase("inprogress")) {
				return StringConstants.ERROR_CODES.ALREADY_IN_QUEUE.ordinal(); // already
																				// in
																				// queue
			}
		}
		MyLogger.getInstance().log(Level.FINE,
				queryStr + " return value : " + retValue);
		return retValue;
	}

	private Vector<String> getCloneDetails(String stageName) {
		mBegTime = System.currentTimeMillis();

		String queryStr = "select REQUEST_ID,snapshot_version,DB_STAGE_TO,CLONE_END_TIME, "
				+ "CURRENT_STATUS,SID_LIST,CYCLE_FOR_CLONE,DB_CLONE_FROM from gsm_ops where "
				+ "lower(DB_STAGE_TO) like "
				+ "lower(\'"
				+ stageName
				+ "\')"
				+ " and CLONE_END_TIME=(select max(CLONE_END_TIME) from "
				+ "gsm_ops where lower(DB_STAGE_TO) like lower(\'"
				+ stageName
				+ "\')) order by 1 desc";
		Vector<String> rs = mCatConn.runQuerySingleRow(queryStr,
				CLONEDET_COL_POS.CLONE_NO_OF_COLUMNS.ordinal());
		mEndTime = System.currentTimeMillis();
		MyLogger.getInstance().log(
				Level.FINE,
				"Time taken to query(catlogdb) :" + (mEndTime - mBegTime)
						+ " ms");
		if (rs.size() == 0) // no clone yet
		{
			return null;
		}
		return rs;
	}

	public int checkIfChildStage(StageDetails mStg) {
		String queryStr = "select WEBSERVER_FRIENDLY from pods where webserver_Dba like '"
				+ mStg.getPodName() + "' and lower(current_status) = 'ready'";
		Vector<String> rs = mCatConn.runQuerySingleRow(queryStr,
				MASTERSTAGE_COL_POS.MASTERSTAGE_NO_OF_COLUMNS.ordinal());
		mEndTime = System.currentTimeMillis();
		MyLogger.getInstance().log(
				Level.INFO,
				"Time taken to query(catlogdb) :" + (mEndTime - mBegTime)
						+ " ms");

		if (rs.size() == 0) // not a child stage
		{
			return 0;
		}
		String masterStageName = rs.get(MASTERSTAGE_COL_POS.MS_NAME_POS
				.ordinal());

		mStg.setMasterStageName(masterStageName);
		rs = getCloneDetails(masterStageName);

		if (rs == null) {
			return 0; // child stage and no clone
		}
		mStg.setCloneVersion(rs.get(CLONEDET_COL_POS.CLONE_VERSION_POS
				.ordinal()));
		mStg.setCloneDate(rs.get(CLONEDET_COL_POS.CLONE_DATE_POS.ordinal()));
		mStg.setCloneStatus(rs.get(CLONEDET_COL_POS.CLONE_STATUS_POS.ordinal()));
		mStg.setCloneType(rs.get(CLONEDET_COL_POS.CLONE_TYPE_POS.ordinal()));
		return 0;
	}

	public String validateInput(Input request) {
		String minorVersion = getSnapshotVersion(request.getCloneVersion());
		String cycle = request.getCloneCycle();
		String option = request.getCloneOption();
		String error = "";
		if (minorVersion == null || minorVersion.length() == 0) {
			error += "Incorrect version.";
		}
		if (!(cycle.equals("FQA") || cycle.equals("RQA"))) {
			error += " Cycle should be FQA or RQA.";
		}
		if (!(option.equals("BASIC") || option.equals("BASIC,PAY11G") || option
				.equals("BASIC,PAY11G,PAYPILOT11G"))) {
			error += " Incorrect clone option.";
		}
		return error;
	}

	public String getPID() {
		String queryStr;
		queryStr = "select POD_SEQ.nextval from dual";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		return mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
	}

	public String getHal() {
		String queryStr;
		queryStr = "select substr(hal_process,8,1) from  pods where pod_id=(select max(pod_id) from pods)";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return "0";
		}
		if (mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()) == null) {
			return "0";
		}

		return mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
	}

	public int insertMetadata(int pod_id, String webserver, String stage,
			String db_server, String pod, String filer, String hal,
			String clone_ver, String clone_option, String clone_cycle,
			String master_ver) {
		String queryStr;

		mCatConn.setCommit(false);

		queryStr = "insert into pods values (" + pod_id
				+ ",null,'NO',sysdate,'" + webserver + "','" + stage + "','"
				+ db_server + "','Ready',null,null,'" + pod + "','" + stage
				+ "','FLEX','" + filer + "','11g DB','" + hal
				+ "','NO',null,'YES',null,null,null,'YES')";
		if (runQueryInTransaction(queryStr) == 0) {
			return 0;
		}

		queryStr = "insert into pod_sids values (" + pod_id
				+ ",'QADB11G','Y','" + pod + "',null,null)";
		if (runQueryInTransaction(queryStr) == 0) {
			return 0;
		}

		queryStr = "insert into pod_sids values (" + pod_id + ",'BASIC','Y','"
				+ pod + "',null,null)";
		if (runQueryInTransaction(queryStr) == 0) {
			return 0;
		}

		if (clone_option.contains("PILOT")) {
			queryStr = "insert into pod_sids values (" + pod_id
					+ ",'PAYPILOT11G','Y','" + pod + "',null,null)";
			if (runQueryInTransaction(queryStr) == 0) {
				return 0;
			}
		}

		if (clone_option.contains("PAY")) {
			queryStr = "insert into pod_sids values (" + pod_id
					+ ",'PAY11G','Y','" + pod + "',null,null)";
			if (runQueryInTransaction(queryStr) == 0) {
				return 0;
			}
		}

		String target_metadata = rd.getValue("Metadata_Db_sid");
		queryStr = "insert into gsm_ops values(gsm_ops_seq.nextval,'"
				+ clone_ver + "','" + stage
				+ "',sysdate,sysdate,sysdate,sysdate,'QUEUED',null,'"
				+ clone_option + "','" + target_metadata + "','" + clone_cycle
				+ "',null,null,'" + master_ver + "')";
		if (runQueryInTransaction(queryStr) == 0) {
			return 0;
		}

		queryStr = "delete from unused_pods where pod='" + pod + "'";
		if (runQueryInTransaction(queryStr) == 0) {
			return 0;
		}

		if (mCatConn.doCommit() == 0) {
			return 0;
		}
		mCatConn.setCommit(true);
		return 1;
	}

	public void removeMetadata(String stageName, String pod) {
		String queryStr;
		queryStr = "delete from gsm_ops where lower(DB_STAGE_TO)='" + stageName
				+ "'";
		mCatConn.runUpdate(queryStr);
		queryStr = "delete from pod_sids where WEBSERVER_DBA='" + pod + "'";
		mCatConn.runUpdate(queryStr);
		queryStr = "delete from pods where WEBSERVER_DBA='" + pod + "'";
		mCatConn.runUpdate(queryStr);
	}

	public String getMaxPod() {
		String queryStr;
		queryStr = "select max(webserver_dba) from pods";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		return mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
	}

	public String checkUnusedPod() {
		String queryStr;
		String ret_pod;
		queryStr = "select pod from unused_pods where IN_PROGRESS=0";
		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());

		if (mrs.size() == 0)
			return null;

		for (Vector<String> vs : mrs) {
			ret_pod = vs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
			if (performDoubleCheck(ret_pod)) {
				return ret_pod;
			}
		}
		return null;
	}

	private boolean performDoubleCheck(String pod) {
		String queryStr;
		queryStr = "select POD_ID from pods where WEBSERVER_DBA='" + pod + "'";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return true;
		}
		queryStr = "delete from unused_pods where pod='" + pod + "'";
		mCatConn.runUpdate(queryStr);
		return false;
	}

	public String getDbServer() {

		String queryStr;
		queryStr = "select DATABASE_SERVER from server_capacity";

		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());

		if (mrs.size() == 0) {
			return null;
		}

		for (Vector<String> vs : mrs) {

			String server = vs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
			String query = "SELECT ((select CAPACITY from server_capacity where DATABASE_SERVER = '"
					+ server
					+ "' and IS_ACTIVE ='1') - (select count(*) from pods where DATABASE_SERVER= '"
					+ server + "')) as AVAILABLE from dual";

			Vector<String> mrs1 = mCatConn.runQuerySingleRow(query,
					SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());

			if (mrs1.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()) == null) {
				continue;
			}
			if (Integer.parseInt(mrs1.get(SINGLE_VALUE_COL_POS.VALUE_POS
					.ordinal())) > 0) {
				MyLogger.getInstance().log(Level.INFO,
						"Selected db_server is : " + vs);
				return server;
			}
		}
		return null;
	}

	public String getFiler(String db_server) {

		String queryStr;
		queryStr = "select filer from server_capacity where DATABASE_SERVER='"
				+ db_server + "'";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		return mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());
	}

	public String getMasterVersion(String clone_ver) {

		String queryStr;
		queryStr = "select SNAPSHOT_VERSION from snapshots where MASTER_VERSION ='"
				+ clone_ver + "' and current_status='ACTIVE'";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}
		return mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal());

	}

	public int runQueryInTransaction(String queryStr) {
		int ret = mCatConn.runUpdate(queryStr);
		if (ret != 0) {
			mCatConn.doRollback();
			return 0;
		}
		return 1;
	}

	public void saveMetrics(String apiType, long elapsedTime, int errorCode,
			String stageName) {
		String queryStr = "insert into api_metrics (api_type, elapsed_time_in_ms, api_call_date, error_code, stage_name) values('"
				+ apiType
				+ "',"
				+ elapsedTime
				+ ",sysdate,"
				+ errorCode
				+ ",'"
				+ stageName + "')";
		mCatConn.runUpdate(queryStr);
		mCatConn.closeConnection();
	}

	public void saveMetrics(String apiType, long elapsedTime, int errorCode,
			String stageName, int exceptionCode, String exceptionMessage) {
		String queryStr = "insert into api_metrics "
				+ "(api_type, elapsed_time_in_ms, api_call_date, error_code, stage_name, exception_code, exception_message) values('"
				+ apiType + "'," + elapsedTime + ",sysdate," + errorCode + ",'"
				+ stageName + "'," + exceptionCode + ",'" + exceptionMessage
				+ "')";
		mCatConn.runUpdate(queryStr);
		mCatConn.closeConnection();
	}

	public void saveServerMetrics(String apiType, long elapsedTime,
			int errorCode, String serverName) {
		String queryStr = "insert into api_metrics (api_type, elapsed_time_in_ms, api_call_date, error_code, server_name) values('"
				+ apiType
				+ "',"
				+ elapsedTime
				+ ",sysdate,"
				+ errorCode
				+ ",'"
				+ serverName + "')";
		mCatConn.runUpdate(queryStr);
	}

	public void saveServerMetrics(String apiType, long elapsedTime,
			int errorCode, String serverName, int exceptionCode,
			String exceptionMessage, String stageName) {
		String queryStr = "insert into api_metrics "
				+ "(api_type, elapsed_time_in_ms, api_call_date, error_code, server_name, exception_code, exception_message, stage_name) values('"
				+ apiType + "'," + elapsedTime + ",sysdate," + errorCode + ",'"
				+ serverName + "'," + exceptionCode + ",'" + exceptionMessage
				+ "','" + stageName + "')";
		mCatConn.runUpdate(queryStr);
	}

	public List<MetricsSet> getMetrics(String startDate, String endDate) {
		String queryStr;

		queryStr = "SELECT TRUNC(API_CALL_DATE), API_TYPE,"
				+ " COUNT(CASE WHEN error_code = 0 THEN 1 END) AS SUCCESS_COUNT,"
				+ " COUNT(CASE WHEN error_code > 0 THEN 1 END) AS ERROR_COUNT,"
				+ " AVG (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS TIME,"
				+ " MAX (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS MAX_TIME,"
				+ " MIN (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS MIN_TIME"
				+ " FROM api_metrics WHERE API_CALL_DATE > '" + startDate
				+ "' AND API_CALL_DATE < '" + endDate
				+ "' GROUP BY TRUNC(API_CALL_DATE),API_TYPE ORDER BY 1";

		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				METRICS_COL_POS.METRICS_NO_OF_COLUMNS.ordinal());
		if (mrs.size() == 0) {
			return null;
		}

		List<MetricsSet> metSet = new ArrayList<MetricsSet>();
		List<Metric> retResult = new ArrayList<Metric>();
		String date = mrs.firstElement()
				.get(METRICS_COL_POS.DATE_POS.ordinal());

		Metric met = null;
		for (Vector<String> vs : mrs) {
			if (!vs.get(METRICS_COL_POS.DATE_POS.ordinal()).equals(date)) {
				metSet.add(new MetricsSet(date, retResult));
				date = vs.get(METRICS_COL_POS.DATE_POS.ordinal());
				retResult = new ArrayList<Metric>();
			}
			met = new Metric();
			met.setType(vs.get(METRICS_COL_POS.API_TYPE_POS.ordinal()));
			met.setCount(Long.parseLong(vs.get(METRICS_COL_POS.COUNT_POS
					.ordinal())));
			met.setErrorCount(Long.parseLong(vs
					.get(METRICS_COL_POS.ERROR_COUNT_POS.ordinal())));
			try {
				met.setAverageTimeElapsed(Double.parseDouble(vs
						.get(METRICS_COL_POS.ELAPSED_TIME_POS.ordinal())));
				met.setMaxTime(Long.parseLong(vs
						.get(METRICS_COL_POS.MAX_TIME_POS.ordinal())));
				met.setMinTime(Long.parseLong(vs
						.get(METRICS_COL_POS.MIN_TIME_POS.ordinal())));
			} catch (Exception e) {
				met.setAverageTimeElapsed(0);
				met.setMaxTime(0);
				met.setMinTime(0);
			}
			retResult.add(met);
		}
		metSet.add(new MetricsSet(date, retResult));
		return metSet;
	}

	public void savePodForReuse(String pod) {
		String queryStr = "insert into unused_pods values('" + pod + "',1)";
		mCatConn.runUpdate(queryStr);
	}

	public void removeFromReuse(String pod) {
		String queryStr = "delete from unused_pods where pod='" + pod + "'";
		mCatConn.runUpdate(queryStr);
	}

	public int checkRemovalInProgress(String pod) {
		String queryStr = "select IN_PROGRESS from unused_pods where POD='"
				+ pod + "'";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return 0;
		}
		return Integer.parseInt(mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS
				.ordinal()));
	}

	public StringConstants.ERROR_CODES pointother(String fromStage,
			String fromPod, String fromServerName, String toStage,
			String toPod, String toServerName) {

		String checkquery = "select STAGE_NAME from DB_POD_HISTORY where STAGE_NAME='"
				+ fromStage + "'";
		Vector<String> chs = mCatConn.runQuerySingleRow(checkquery,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (chs.size() != 0)// check if the stage name already exists
		{
			return StringConstants.ERROR_CODES.POINT_DB_ALREADY;
		}

		String saveQuery = "insert into DB_POD_HISTORY (STAGE_NAME,LAST_POD_NAME,LAST_UPDATED,LAST_DBSERVER_NAME)";
		saveQuery += " values ('";
		saveQuery += fromStage + "','" + fromPod + "',sysdate,'"
				+ fromServerName + "')";

		String updateQuery = "update pods set MONITOR_GG = 'NO', MONITOR_DB = 'NO', current_status = 'NOT Ready', webserver_dba = '";
		updateQuery += toPod + "', database_server = '" + toServerName + "'";
		updateQuery += " where webserver_dbafriendly = '" + fromStage + "'";

		mCatConn.setCommit(false);
		if (runQueryInTransaction(saveQuery) == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		if (runQueryInTransaction(updateQuery) == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		if (mCatConn.doCommit() == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		mCatConn.setCommit(true);
		return StringConstants.ERROR_CODES.NO_ERROR;
	}

	public StringConstants.ERROR_CODES pointsame(String revertstage) {
		mBegTime = System.currentTimeMillis();

		String checkquery = "select STAGE_NAME from DB_POD_HISTORY where STAGE_NAME='"
				+ revertstage + "'";
		Vector<String> chs = mCatConn.runQuerySingleRow(checkquery,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (chs.size() == 0) // check if the stage name is present in the
								// DB_POD_HISTORY
		{
			// if not present the return
			return StringConstants.ERROR_CODES.POINT_DB_ALREADY;
		}

		String query = "select last_pod_name, last_dbserver_name from db_pod_history where ";
		query += " stage_name = '" + revertstage + "'";
		Vector<String> rs = mCatConn.runQuerySingleRow(query,
				DPH_COL_POS.NO_OF_COLUMNS.ordinal());

		String podName = rs.get(DPH_COL_POS.POD_NAME_POS.ordinal());
		String serverName = rs.get(DPH_COL_POS.POD_SERVER_NAME_POS.ordinal());

		String updateQuery = "update pods set MONITOR_GG = 'YES', MONITOR_DB = 'YES', current_status = 'Ready', webserver_dba = '";
		updateQuery += podName + "', database_server = '" + serverName + "'";
		updateQuery += " where webserver_dbafriendly = '" + revertstage + "'";

		String deleteQuery = "delete from db_pod_history where stage_name = '";
		deleteQuery += revertstage + "'";

		MyLogger.getInstance().log(Level.INFO, updateQuery);
		MyLogger.getInstance().log(Level.INFO, deleteQuery);

		mCatConn.setCommit(false);
		if (runQueryInTransaction(updateQuery) == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		if (runQueryInTransaction(deleteQuery) == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		if (mCatConn.doCommit() == 0) {
			return StringConstants.ERROR_CODES.INTERNAL_ERROR;
		}
		mCatConn.setCommit(true);
		return StringConstants.ERROR_CODES.NO_ERROR;
	}

	public CapacityCheck checkAvailableCapacity() {

		CapacityCheck retObj = new CapacityCheck();

		String queryStr;
		Vector<String> mrs;
		queryStr = "select sum(capacity) as total from server_capacity where IS_ACTIVE=1";
		mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		int maxCapacity = Integer.parseInt(mrs
				.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()));

		queryStr = "select count(*) as provisioned from pods where database_server in (select database_server from server_capacity where IS_ACTIVE=1)";
		mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		int provisioned = Integer.parseInt(mrs
				.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()));

		queryStr = "select database_server, server_capacity, pod_count, (server_capacity - pod_count)"
				+ " AS available from (select a.database_server, server_capacity, total_capacity, pod_count,sum(pod_count)"
				+ " over () total_provisioned from (select database_server, server_capacity, sum(server_capacity) "
				+ "over () total_capacity from   (select database_server, sum(capacity) server_capacity "
				+ "from server_capacity where is_active = '1' group by database_server)) a, (select database_server, pod_count"
				+ " from   (select database_server, count(*) pod_count "
				+ "from pods Group by database_server)) b Where a.database_server = b.database_server (+)) "
				+ "order by 4 desc";

		Vector<Vector<String>> vs = mCatConn.runQueryMultipleRows(queryStr,
				SERVER_CAPACITY_POS.CAPACITY_NO_OF_COLUMNS.ordinal());
		if (vs.size() == 0) {
			return null;
		}

		List<DatabaseServer> serverList = new ArrayList<DatabaseServer>();
		for (Vector<String> row : vs) {
			serverList.add(new DatabaseServer(row
					.get(SERVER_CAPACITY_POS.NAME_POS.ordinal()), Integer
					.parseInt(row.get(SERVER_CAPACITY_POS.CAPACITY_POS
							.ordinal())), Integer.parseInt(row
					.get(SERVER_CAPACITY_POS.AVAILABLE_POS.ordinal())), Integer
					.parseInt(row.get(SERVER_CAPACITY_POS.POD_COUNT_POS
							.ordinal()))));
		}

		retObj.setMaxCapacity(maxCapacity);
		retObj.setAvailableCapacity(maxCapacity - provisioned);
		retObj.setServerList(serverList);
		return retObj;
	}

	public void updateListenerStatus(String dbServer, int status) {
		String queryStr = "update server_capacity set reload_inprogress = '"
				+ status + "' where database_server = '" + dbServer + "'";
		mCatConn.runUpdate(queryStr);
	}

	public boolean isListenerReloadInProgress(String dbServer) {
		String queryStr = "select RELOAD_INPROGRESS from server_capacity where DATABASE_SERVER='"
				+ dbServer + "'";
		Vector<String> mrs = mCatConn.runQuerySingleRow(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return true;
		}
		return (mrs.get(SINGLE_VALUE_COL_POS.VALUE_POS.ordinal()).equals("1"));
	}

	// Added by Chandra Gaurav
	public Vector<DbServer> getDbServersList() {
		String queryStr;
		queryStr = "SELECT DISTINCT database_server FROM  pods UNION SELECT DISTINCT host FROM lvs_spl_db_list ORDER BY 1";

		Vector<Vector<String>> mrs = mCatConn.runQueryMultipleRows(queryStr,
				SINGLE_VALUE_COL_POS.VALUE_NO_OF_COL.ordinal());
		if (mrs.size() == 0) {
			return null;
		}

		Vector<DbServer> result = new Vector<DbServer>();
		for (Vector<String> dbs : mrs) {
			result.add(new DbServer(dbs.get(SINGLE_VALUE_COL_POS.VALUE_POS
					.ordinal())));
		}
		return result;
	}
	// Method ended here
}
