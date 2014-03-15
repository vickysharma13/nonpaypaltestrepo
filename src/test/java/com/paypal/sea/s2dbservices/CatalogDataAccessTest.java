package com.paypal.sea.s2dbservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.*;

import com.paypal.sea.s2dbservices.StringConstants.DBSTATUS;
import com.paypal.sea.s2dbservices.oracledbaccess.CapacityCheck;
import com.paypal.sea.s2dbservices.oracledbaccess.CloneDetails;
import com.paypal.sea.s2dbservices.oracledbaccess.CloneHistory;
import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy;
import com.paypal.sea.s2dbservices.oracledbaccess.DatabaseServer;
import com.paypal.sea.s2dbservices.oracledbaccess.DbServer;
import com.paypal.sea.s2dbservices.oracledbaccess.Input;
import com.paypal.sea.s2dbservices.oracledbaccess.Metric;
import com.paypal.sea.s2dbservices.oracledbaccess.MetricsSet;
import com.paypal.sea.s2dbservices.oracledbaccess.OracleConnection2;
import com.paypal.sea.s2dbservices.oracledbaccess.CatalogDataAccess;
import com.paypal.sea.s2dbservices.oracledbaccess.ServerDetails;
import com.paypal.sea.s2dbservices.oracledbaccess.SnapshotDetail;
import com.paypal.sea.s2dbservices.oracledbaccess.StageDBDataAccess;
import com.paypal.sea.s2dbservices.oracledbaccess.StageDetails;
import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy.StageNames;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CatalogDataAccess.class })
public class CatalogDataAccessTest {
	OracleConnection2 mockOc2 = mock(OracleConnection2.class);
	Input mockInput = mock(Input.class);
	StageDetails mockStageDetails = mock(StageDetails.class);
	ReadProperty mockReadProperty = mock(ReadProperty.class);
	Metric mockMetric = mock(Metric.class);
	DriverManager mockDm = mock(DriverManager.class);
	Connection mockConn = mock(Connection.class);
	CatalogDataAccess obj = new CatalogDataAccess(mockOc2, mockReadProperty);

	String queryString = "";
	String queryString1 = "";
	String queryString2 = "";
	String queryString3 = "";
	Vector<String> vs = new Vector<String>();
	Vector<String> vs1 = new Vector<String>();
	Vector<String> vs2 = new Vector<String>();
	Vector<String> vs3 = new Vector<String>();
	Vector<String> vsEmpty = new Vector<String>();
	Vector<Vector<String>> rs = new Vector<Vector<String>>();
	Vector<Vector<String>> rs1 = new Vector<Vector<String>>();
	Vector<Vector<String>> rsEmpty = new Vector<Vector<String>>();
	String testID = "1000";
	String testPod = "AAA";
	String testStage = "stage2p12345";
	String testDbServer = "lvsvmdb01";
	String testCloneVersion = "99RQALVS11G";
	String testSnapshotVersion = "99RQALVS11G06";
	String testCloneCycle = "RQA";
	String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";
	String testCloneOption2 = "BASIC";
	String testDate = "01-jan-13";
	String testUsername = "clocapp";
	String testUserPay = "pay";
	String testPasswordPay = "pay";
	String testPassword = "clocappstg";
	String testPort = "2126";
	String testMeta = "METADB";
	String testStatus = "SUCCESS";
	StringConstants.DBSTATUS testDbStatus = StringConstants.DBSTATUS.DBUP;
	StringConstants.DBSTATUS testDbStatusNA = StringConstants.DBSTATUS.NA;
	String testFiler = "lvsvna01";
	String testHal = "HAL_LVS1";
	String testExtendedStatus = "Clone Completed.";
	String testCauseOfFailure = "completed clone.";
	String testMasterStageName = "stage2p2345";

	private enum NO_OF_COLUMNS {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE
	};

	@Test
	public void testInsertMetaData() {

		queryString = "insert into pods values (" + testID
				+ ",null,'NO',sysdate,'" + testStage.substring(5) + "','"
				+ testStage + "','" + testDbServer + "','Ready',null,null,'"
				+ testPod + "','" + testStage + "','FLEX','" + testFiler
				+ "','11g DB','" + testHal
				+ "','NO',null,'YES',null,null,null,'YES')";

		queryString1 = "insert into pod_sids values (" + testID
				+ ",'QADB11G','Y','" + testPod + "',null,null)";

		queryString2 = "insert into pod_sids values (" + testID
				+ ",'BASIC','Y','" + testPod + "',null,null)";

		queryString3 = "insert into pod_sids values (" + testID
				+ ",'PAYPILOT11G','Y','" + testPod + "',null,null)";

		String queryString4 = "insert into pod_sids values (" + testID
				+ ",'PAY11G','Y','" + testPod + "',null,null)";

		String queryString5 = "insert into gsm_ops values(gsm_ops_seq.nextval,'"
				+ testCloneVersion
				+ "','"
				+ testStage
				+ "',sysdate,sysdate,sysdate,sysdate,'QUEUED',null,'"
				+ testCloneOption
				+ "','"
				+ testMeta
				+ "','"
				+ testCloneCycle
				+ "',null,null,'" + testSnapshotVersion + "')";

		String queryString6 = "delete from unused_pods where pod='" + testPod
				+ "'";

		when(mockOc2.runUpdate(queryString)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString1)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString2)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString3)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString4)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString5)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString6)).thenReturn(1).thenReturn(0);

		when(mockOc2.doCommit()).thenReturn(0).thenReturn(1);

		assertEquals("first return", 0, obj.insertMetadata(
				Integer.parseInt(testID), testStage.substring(5), testStage,
				testDbServer, testPod, testFiler, testHal, testCloneVersion,
				testCloneOption, testCloneCycle, testSnapshotVersion));
		assertEquals("second return", 0, obj.insertMetadata(
				Integer.parseInt(testID), testStage.substring(5), testStage,
				testDbServer, testPod, testFiler, testHal, testCloneVersion,
				testCloneOption, testCloneCycle, testSnapshotVersion));
		assertEquals(0, obj.insertMetadata(Integer.parseInt(testID),
				testStage.substring(5), testStage, testDbServer, testPod,
				testFiler, testHal, testCloneVersion, testCloneOption,
				testCloneCycle, testSnapshotVersion));
		assertEquals(0, obj.insertMetadata(Integer.parseInt(testID),
				testStage.substring(5), testStage, testDbServer, testPod,
				testFiler, testHal, testCloneVersion, testCloneOption,
				testCloneCycle, testSnapshotVersion));
		assertEquals(0, obj.insertMetadata(Integer.parseInt(testID),
				testStage.substring(5), testStage, testDbServer, testPod,
				testFiler, testHal, testCloneVersion, testCloneOption,
				testCloneCycle, testSnapshotVersion));
		assertEquals(0, obj.insertMetadata(Integer.parseInt(testID),
				testStage.substring(5), testStage, testDbServer, testPod,
				testFiler, testHal, testCloneVersion, testCloneOption,
				testCloneCycle, testSnapshotVersion));
		assertEquals("last return", 0, obj.insertMetadata(
				Integer.parseInt(testID), testStage.substring(5), testStage,
				testDbServer, testPod, testFiler, testHal, testCloneVersion,
				testCloneOption, testCloneCycle, testSnapshotVersion));
		assertEquals(1, obj.insertMetadata(Integer.parseInt(testID),
				testStage.substring(5), testStage, testDbServer, testPod,
				testFiler, testHal, testCloneVersion, testCloneOption,
				testCloneCycle, testSnapshotVersion));
	}

	@Test
	public void testFindDbInfo() {

		String testSIDList = "BASIC,PAY11G,PAYPILOT11G";
		String testCycleForClone = "FQA";
		String testDBCloneFrom = "104RQALVS11G";
		queryString = "select database_server, webserver_dba, current_Status, monitor_db, version from pods "
				+ "where lower(WEBSERVER_FRIENDLY) like "
				+ "lower(\'"
				+ testStage + "\')";

		queryString1 = "select SNAPSHOT_VERSION from snapshots where MASTER_VERSION ='"
				+ testDBCloneFrom + "' and current_status='ACTIVE'";

		queryString2 = "select REQUEST_ID,snapshot_version,DB_STAGE_TO,CLONE_END_TIME, "
				+ "CURRENT_STATUS,SID_LIST,CYCLE_FOR_CLONE,DB_CLONE_FROM from gsm_ops where "
				+ "lower(DB_STAGE_TO) like "
				+ "lower(\'"
				+ testStage
				+ "\')"
				+ " and CLONE_END_TIME=(select max(CLONE_END_TIME) from "
				+ "gsm_ops where lower(DB_STAGE_TO) like lower(\'"
				+ testStage
				+ "\')) order by 1 desc";
		String queryString4 = "select ss.MASTER_VERSION, ss.SNAPSHOT_VERSION, ss.CREATED,"
				+ "pods.webserver_dba "
				+ "from snapshots ss, pods where lower(ss.current_status) like 'active' "
				+ "and pods.snapshot_id = ss.snapshot_id order by ss.CREATED";
		String queryString5 = "select WEBSERVER_FRIENDLY from pods where webserver_Dba like '"
				+ testPod + "' and lower(current_status) = 'ready'";

		StageDetails mStg = new StageDetails();
		StageDetails mStg_invalid = new StageDetails();
		Vector<String> vs4 = new Vector<String>();
		StageDBDataAccess mockSdb = mock(StageDBDataAccess.class);

		vs.add(testDbServer);
		vs.add(testPod);
		vs.add("Ready");
		vs.add("YES");
		vs.add(testCloneVersion);

		vs2.add(testSnapshotVersion);

		vs1.add(testID);
		vs1.add(testCloneVersion);
		vs1.add(testStage);
		vs1.add(testDate);
		vs1.add("SUCCESS");
		vs1.add(testSIDList);
		vs1.add(testCycleForClone);
		vs1.add(testDBCloneFrom);

		vs4.add(testCloneVersion);
		vs4.add(testSnapshotVersion);
		vs4.add(testDate);
		vs4.add(testPod);
		rs.add(vs4);

		Vector<String> vs5 = new Vector<String>();
		vs5.add(testMasterStageName);

		Vector<String> vs6 = new Vector<String>();
		vs5.add(testStage);

		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.FIVE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs2);
		when(
				mockOc2.runQuerySingleRow(queryString2,
						NO_OF_COLUMNS.EIGHT.ordinal())).thenReturn(vs1);
		when(
				mockOc2.runQueryMultipleRows(queryString4,
						NO_OF_COLUMNS.FOUR.ordinal())).thenReturn(rs);
		when(
				mockOc2.runQuerySingleRow(queryString5,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs5)
				.thenReturn(vs6);
		try {
			whenNew(StageDBDataAccess.class).withNoArguments().thenReturn(
					mockSdb);
		} catch (Exception e) {
		}
		when(mockSdb.connectStageDB(testStage, mStg)).thenReturn(0);
		when(mockSdb.connectStageDB(testMasterStageName, mStg)).thenReturn(0);

		mStg_invalid = obj.findDBInfo(testStage);
		assertEquals(testStage, mStg_invalid.getStageName());
		assertEquals(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
				mStg_invalid.getErrorCode());
		assertEquals("", mStg_invalid.getCloneVersion());
		assertEquals("", mStg_invalid.getDBServerName());
		assertEquals("", mStg_invalid.getCloneCycle());
		assertEquals("", mStg_invalid.getCloneDate());
		assertEquals("", mStg_invalid.getCloneStatus());
		assertEquals("", mStg_invalid.getCloneType());
		assertEquals("", mStg_invalid.getMasterStageName());
		assertEquals("", mStg_invalid.getPodName());
		assertEquals(testDbStatusNA, mStg_invalid.getDBStatus());
		assertEquals(testDbStatusNA, mStg_invalid.getDbStatusPay());
		assertEquals(testDbStatusNA, mStg_invalid.getDbStatusPayPilot());

		mStg = obj.findDBInfo(testStage);
		assertEquals(testStage, mStg.getStageName());
		assertEquals(testDbServer, mStg.getDBServerName());
		assertEquals(testPod, mStg.getPodName());
		assertTrue(mStg.getIsCloneable());
		assertEquals(testCloneVersion, mStg.getCloneVersion());
		assertEquals(testSnapshotVersion, mStg.getLatestVersion());
		assertEquals(testDate, mStg.getCloneDate());
		assertEquals(testStatus, mStg.getCloneStatus());
		assertEquals(testSIDList, mStg.getCloneType());
		assertEquals(testCycleForClone, mStg.getCloneCycle());
		assertEquals(testSnapshotVersion, mStg.getLatestVersion());

		/*
		 * mStg = obj.findDBInfo(testStage); assertEquals(testStage,
		 * mStg.getStageName()); assertEquals(testDbServer,
		 * mStg.getDBServerName()); assertEquals(testPod, mStg.getPodName());
		 * assertFalse(mStg.getIsCloneable()); assertEquals(testCloneVersion,
		 * mStg.getCloneVersion()); assertEquals(testSnapshotVersion,
		 * mStg.getLatestVersion()); assertEquals(testDate,
		 * mStg.getCloneDate()); assertEquals(testStatus,
		 * mStg.getCloneStatus()); assertEquals(testSIDList,
		 * mStg.getCloneType()); assertEquals(testCycleForClone,
		 * mStg.getCloneCycle()); assertEquals(testSnapshotVersion,
		 * mStg.getLatestVersion());
		 */

		/*
		 * mStg = obj.findDBInfo(testStage); assertEquals(testStage,
		 * mStg.getStageName()); assertEquals(testDbServer,
		 * mStg.getDBServerName()); assertEquals(testPod, mStg.getPodName());
		 * assertTrue(mStg.getIsCloneable()); assertEquals(testCloneVersion,
		 * mStg.getCloneVersion()); assertEquals(testSnapshotVersion,
		 * mStg.getLatestVersion());
		 * 
		 * mStg = obj.findDBInfo(testStage); assertEquals(testStage,
		 * mStg.getStageName()); assertEquals(testDbServer,
		 * mStg.getDBServerName()); assertEquals(testPod, mStg.getPodName());
		 * mStg.setIsCloneable(false); assertFalse(mStg.getIsCloneable());
		 * assertEquals(testCloneVersion, mStg.getCloneVersion());
		 * assertEquals(testSnapshotVersion, mStg.getLatestVersion());
		 * assertEquals(testDbStatusNA, mStg.getDbStatusPay());
		 * assertEquals(testDbStatusNA, mStg.getDbStatusPayPilot());
		 * assertEquals(StringConstants.DBSTATUS.NA, mStg.getDBStatus());
		 * assertEquals(StringConstants.ERROR_CODES.NO_ERROR,
		 * mStg.getErrorCode()); assertEquals(0, mStg.getExceptionCode());
		 */
	}

	@Test
	public void testCreateCloneRequest() {
		queryString = "select MASTER_VERSION, SNAPSHOT_VERSION, CREATED "
				+ "from snapshots where lower(current_status) like 'active'"
				+ " and upper(MASTER_VERSION) like '"
				+ testCloneVersion.toUpperCase() + "'"
				+ " order by MASTER_VERSION ";
		queryString1 = "select current_status from gsm_ops "
				+ "where request_id = (select max(request_id) from gsm_ops where lower(db_Stage_to) like '"
				+ testStage + "')";
		queryString2 = "select gsm_ops_seq.nextval from dual";
		queryString3 = "insert into gsm_ops values" + "(" + testID + ",'"
				+ testCloneVersion + "','" + testStage
				+ "',sysdate,sysdate,sysdate,sysdate,'QUEUED'," + "null,'"
				+ testCloneOption + "','METADB','" + testCloneCycle
				+ "',null,null,'" + testSnapshotVersion + "')";

		vs1.add("inprogress");
		vs.add(testCloneVersion);
		vs.add(testSnapshotVersion);
		vs2.add(testID);

		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.THREE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs).thenReturn(vs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs1)
				.thenReturn(vsEmpty);
		when(
				mockOc2.runQuerySingleRow(queryString2,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs1)
				.thenReturn(vs2);
		when(mockReadProperty.getValue("Metadata_Db_sid")).thenReturn("METADB");
		when(mockOc2.runUpdate(queryString3)).thenReturn(0);

		assertEquals(StringConstants.ERROR_CODES.VERSION_NOT_EXISTS.ordinal(),
				obj.createCloneRequest(testStage, testCloneVersion,
						testCloneCycle, testCloneOption, testID));
		assertEquals(StringConstants.ERROR_CODES.ALREADY_IN_QUEUE.ordinal(),
				obj.createCloneRequest(testStage, testCloneVersion,
						testCloneCycle, testCloneOption, testID));
		assertEquals(0, obj.createCloneRequest(testStage, testCloneVersion,
				testCloneCycle, testCloneOption, testID));
	}

	@Test
	public void testGetPid() {

		queryString = "select POD_SEQ.nextval from dual";

		vs.add(testID);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);

		assertEquals(vs.get(0), obj.getPID());
		assertEquals(null, obj.getPID());

	}

	@Test
	public void testGetHal() {

		queryString = "select substr(hal_process,8,1) from  pods where pod_id=(select max(pod_id) from pods)";
		vs.add("HAL_LVS1");
		vs1.add(null);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty).thenReturn(vs1);

		assertEquals("HAL_LVS1", obj.getHal());
		assertEquals("0", obj.getHal());
		assertEquals("0", obj.getHal());

	}

	@Test
	public void testCheckRemovalInProgress() {
		queryString = "select IN_PROGRESS from unused_pods where POD='"
				+ testPod + "'";
		vs.add("1");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		assertEquals(1, obj.checkRemovalInProgress(testPod));
		assertEquals(0, obj.checkRemovalInProgress(testPod));
	}

	@Test
	public void testRemoveFromReuse() {
		queryString = "delete from unused_pods where pod='" + testPod + "'";
		obj.removeFromReuse(testPod);
		verify(mockOc2).runUpdate(queryString);
	}

	@Test
	public void testSaveMetrics() {
		queryString = "insert into api_metrics (api_type, elapsed_time_in_ms, api_call_date,"
				+ " error_code, stage_name) values('test',100,sysdate,0,'"
				+ testStage + "')";
		obj.saveMetrics("test", 100, 0, testStage);
		verify(mockOc2).runUpdate(queryString);
	}

	@Test
	public void testSaveMetricsWithException() {

		int testCode = 1;
		String testException = "Exception";
		queryString = "insert into api_metrics "
				+ "(api_type, elapsed_time_in_ms, api_call_date, error_code, stage_name, exception_code, exception_message) values('test',100,sysdate,0,'"
				+ testStage + "'," + testCode + ",'" + testException + "')";
		obj.saveMetrics("test", 100, 0, testStage, testCode, testException);
		verify(mockOc2).runUpdate(queryString);
	}

	@Test
	public void testRunQueryInTransaction() {
		queryString = "Test query";
		when(mockOc2.runUpdate(queryString)).thenReturn(0).thenReturn(1);
		assertEquals(1, obj.runQueryInTransaction(queryString));
		assertEquals(0, obj.runQueryInTransaction(queryString));
	}

	@Test
	public void testGetMetrics() {
		String startDate = "01-jan-13";
		String endDate = "30-apr-13";
		queryString = "SELECT TRUNC(API_CALL_DATE), API_TYPE,"
				+ " COUNT(CASE WHEN error_code = 0 THEN 1 END) AS SUCCESS_COUNT,"
				+ " COUNT(CASE WHEN error_code > 0 THEN 1 END) AS ERROR_COUNT,"
				+ " AVG (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS TIME,"
				+ " MAX (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS MAX_TIME,"
				+ " MIN (CASE WHEN error_code = 0 THEN ELAPSED_TIME_IN_MS END) AS MIN_TIME"
				+ " FROM api_metrics WHERE API_CALL_DATE > '" + startDate
				+ "' AND API_CALL_DATE < '" + endDate
				+ "' GROUP BY TRUNC(API_CALL_DATE),API_TYPE ORDER BY 1";

		List<MetricsSet> retResult = new ArrayList<MetricsSet>();
		vs.add("2013-08-27 00:00:00");
		vs.add("details");
		vs.add("1000");
		vs.add("500");
		vs.add("12.333");
		vs.add("25");
		vs.add("4");
		rs.add(vs);

		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.SEVEN.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);
		assertEquals(null, obj.getMetrics(startDate, endDate));
		retResult = obj.getMetrics("01-jan-13", "30-apr-13");

		Metric met = new Metric();
		met.setCount(1000);
		met.setErrorCount(500);
		met.setMaxTime(25);
		met.setMinTime(4);
		met.setType("details");
		met.setAverageTimeElapsed(12.333f);

		List<Metric> metricList = retResult.get(0).getMetricsList();
		assertEquals("2013-08-27 00:00:00", retResult.get(0).getDate());
		assertEquals(met.getType(), metricList.get(0).getType());
		assertEquals(met.getCount(), metricList.get(0).getCount());
		assertEquals(met.getErrorCount(), metricList.get(0).getErrorCount());
		assertEquals(met.getMaxTime(), metricList.get(0).getMaxTime());
		assertEquals(met.getAverageTimeElapsed(), metricList.get(0)
				.getAverageTimeElapsed(), 0.001f);
	}

	@Test
	public void testSavePodForReuse() {
		queryString = "insert into unused_pods values('" + testPod + "',1)";
		obj.savePodForReuse(testPod);
		verify(mockOc2).runUpdate(queryString);
	}

	@Test
	public void testRemoveMetadata() {
		queryString = "delete from gsm_ops where lower(DB_STAGE_TO)='"
				+ testStage + "'";
		queryString1 = "delete from pod_sids where WEBSERVER_DBA='" + testPod
				+ "'";
		queryString2 = "delete from pods where WEBSERVER_DBA='" + testPod + "'";
		obj.removeMetadata(testStage, testPod);
		verify(mockOc2).runUpdate(queryString);
		verify(mockOc2).runUpdate(queryString1);
		verify(mockOc2).runUpdate(queryString2);
	}

	@Test
	public void testValidateInput() {
		queryString = "select MASTER_VERSION, SNAPSHOT_VERSION, CREATED "
				+ "from snapshots where lower(current_status) like 'active'"
				+ " and upper(MASTER_VERSION) like '99RQALVS11G'"
				+ " order by MASTER_VERSION ";
		vs.add("99RQALVS11G");
		vs.add("99RQALVS11G06");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.THREE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		when(mockInput.getCloneVersion()).thenReturn("99RQALVS11G");
		when(mockInput.getCloneCycle()).thenReturn("FQA").thenReturn("QQA");
		when(mockInput.getCloneOption()).thenReturn(testCloneOption)
				.thenReturn("PAY");
		assertEquals("", obj.validateInput(mockInput));
		assertEquals(
				"Incorrect version. Cycle should be FQA or RQA. Incorrect clone option.",
				obj.validateInput(mockInput));
	}

	@Test
	public void testGetSnapshotVersion() {
		queryString = "select MASTER_VERSION, SNAPSHOT_VERSION, CREATED "
				+ "from snapshots where lower(current_status) like 'active'"
				+ " and upper(MASTER_VERSION) like '99RQALVS11G'"
				+ " order by MASTER_VERSION ";
		vs.add("99RQALVS11G");
		vs.add("99RQALVS11G06");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.THREE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		assertEquals("99RQALVS11G06", obj.getSnapshotVersion("99RQALVS11G"));
		assertEquals(null, obj.getSnapshotVersion("99RQALVS11G"));
	}

	@Test
	public void testGetFiler() {
		queryString = "select filer from server_capacity where DATABASE_SERVER='"
				+ testDbServer + "'";
		vs.add("lvsna01");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		assertEquals("lvsna01", obj.getFiler(testDbServer));
		assertEquals(null, obj.getFiler(testDbServer));
	}

	@Test
	public void testGetMasterVersion() {
		queryString = "select SNAPSHOT_VERSION from snapshots where MASTER_VERSION ='"
				+ testCloneVersion + "' and current_status='ACTIVE'";
		vs.add("99RQALVS11G06");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		assertEquals("99RQALVS11G06", obj.getMasterVersion(testCloneVersion));
		assertEquals(null, obj.getMasterVersion(testCloneVersion));
	}

	@Test
	public void testGetDbServer() {
		queryString = "select DATABASE_SERVER from server_capacity";
		queryString1 = "SELECT ((select CAPACITY from server_capacity where DATABASE_SERVER = '"
				+ testDbServer
				+ "' and IS_ACTIVE ='1') - (select count(*) from pods where DATABASE_SERVER= '"
				+ testDbServer + "')) as AVAILABLE from dual";
		vs.add(testDbServer);
		rs.add(vs);
		vs1.add("2");
		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs1);
		assertEquals(null, obj.getDbServer());
		assertEquals(testDbServer, obj.getDbServer());
	}

	@Test
	public void testGetMaxPod() {
		queryString = "select max(webserver_dba) from pods";
		vs.add(testPod);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		assertEquals(testPod, obj.getMaxPod());
		assertEquals(null, obj.getMaxPod());
	}

	@Test
	public void testCheckUnusedPod() {
		queryString = "select pod from unused_pods where IN_PROGRESS=0";
		queryString1 = "select POD_ID from pods where WEBSERVER_DBA='"
				+ testPod + "'";
		vs.add(testPod);
		rs.add(vs);
		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);

		assertEquals(null, obj.checkUnusedPod());
		assertEquals(testPod, obj.checkUnusedPod());
		assertEquals(null, obj.checkUnusedPod());
	}

	@Test
	public void testCheckIfChildStage() {

		String testStage = "stage2p1025";
		queryString = "select WEBSERVER_FRIENDLY from pods where webserver_Dba like '"
				+ testPod + "' and lower(current_status) = 'ready'";
		queryString1 = "select REQUEST_ID,snapshot_version,DB_STAGE_TO,CLONE_END_TIME, "
				+ "CURRENT_STATUS,SID_LIST,CYCLE_FOR_CLONE,DB_CLONE_FROM from gsm_ops where "
				+ "lower(DB_STAGE_TO) like "
				+ "lower(\'"
				+ testStage
				+ "\')"
				+ " and CLONE_END_TIME=(select max(CLONE_END_TIME) from "
				+ "gsm_ops where lower(DB_STAGE_TO) like lower(\'"
				+ testStage
				+ "\')) order by 1 desc";

		vs.add(testStage);
		vs1.add(testID);
		vs1.add(1, testCloneVersion);
		vs1.add(2, testStage);
		vs1.add(3, "01-JAN-13");
		vs1.add(4, "SUCCESS");
		vs1.add(5, testCloneOption);

		when(mockStageDetails.getPodName()).thenReturn(testPod);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.EIGHT.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs1);

		assertEquals(0, obj.checkIfChildStage(mockStageDetails));
		assertEquals(0, obj.checkIfChildStage(mockStageDetails));
		assertEquals(0, obj.checkIfChildStage(mockStageDetails));
		verify(mockStageDetails).setCloneDate("01-JAN-13");
		verify(mockStageDetails).setCloneStatus("SUCCESS");
		verify(mockStageDetails).setCloneType(testCloneOption);
	}

	@Test
	public void testFindUniqueName() {
		assertEquals("testing 1 of 9", "HB0", obj.findUniqueName("HAZ"));
		assertEquals("testing 2 of 9", "E11", obj.findUniqueName("E10"));
		assertEquals("testing 3 of 9", "E20", obj.findUniqueName("E1Z"));
		assertEquals("testing 4 of 9", "E1A", obj.findUniqueName("E19"));
		assertEquals("testing 5 of 9", "89A", obj.findUniqueName("899"));
		assertEquals("testing 6 of 9", null, obj.findUniqueName("99"));
		assertEquals("testing 7 of 9", null, obj.findUniqueName("9229"));
		assertEquals("testing 8 of 9", "E9A", obj.findUniqueName("E99"));
		assertEquals("testing 9 of 9", "000", obj.findUniqueName("ZZZ"));

	}

	@Test
	public void testGetSnapshotVersions() {
		queryString = "SELECT master_version, snapshot_version, created FROM snapshots WHERE current_status = 'ACTIVE' order by created";
		Vector<SnapshotDetail> retResult = new Vector<SnapshotDetail>();
		vs.add(testCloneVersion);
		vs.add(testSnapshotVersion);
		vs.add(testDate);
		vs.add(testPod);
		rs.add(vs);

		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.THREE.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);

		assertEquals(null, obj.getSnapshotVersions());
		retResult = obj.getSnapshotVersions();

		SnapshotDetail sd = new SnapshotDetail();
		sd.setMajorVersion(testCloneVersion);
		sd.setSnapshotDate(testDate);
		sd.setVersion(testSnapshotVersion);

		assertEquals(sd.getMajorVersion(), retResult.firstElement()
				.getMajorVersion());
		assertEquals(sd.getSnapshotDate(), retResult.firstElement()
				.getSnapshotDate());
		assertEquals(sd.getVersion(), retResult.firstElement().getVersion());

	}

	@Test
	public void testGetCloneHistory() {
		String testStage = "stage2p1025";
		queryString = "select REQUEST_ID, nvl(to_char(CLONE_END_TIME),' '), nvl(CURRENT_STATUS,' '), nvl(EXTENDED_STATUS,' '), nvl(CAUSE_OF_FAILURE,' '), nvl(SNAPSHOT_VERSION,' ') "
				+ "from gsm_ops where lower(DB_STAGE_TO) like "
				+ "lower(\'"
				+ testStage + "\')" + " order by CLONE_END_TIME";
		Vector<CloneHistory> retResult = new Vector<CloneHistory>();
		vs.add(testID);
		vs.add(testDate);
		vs.add(testStatus);
		vs.add(testExtendedStatus);
		vs.add(testCauseOfFailure);
		vs.add(testSnapshotVersion);
		rs.add(vs);

		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.SIX.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);

		assertEquals(null, obj.getCloneHistory(testStage));
		retResult = obj.getCloneHistory(testStage);

		CloneHistory ch = new CloneHistory();
		ch.setEndTime(testDate);
		ch.setCurrentStatus(testStatus);
		ch.setExtendedStatus(testExtendedStatus);
		ch.setFailureCause(testCauseOfFailure);
		ch.setSnapshotVersion(testSnapshotVersion);

		assertEquals(ch.getEndTime(), retResult.firstElement().getEndTime());
		assertEquals(ch.getCurrentStatus(), retResult.firstElement()
				.getCurrentStatus());
		assertEquals(ch.getExtendedStatus(), retResult.firstElement()
				.getExtendedStatus());
		assertEquals(ch.getFailureCause(), retResult.firstElement()
				.getFailureCause());
		assertEquals(ch.getSnapshotVersion(), retResult.firstElement()
				.getSnapshotVersion());
	}

	@Test
	public void testGetStagesAll() {
		queryString = "select unique DATABASE_SERVER,WEBSERVER_FRIENDLY from pods where DATABASE_SERVER is not null order by DATABASE_SERVER,WEBSERVER_FRIENDLY";
		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.TWO.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);
		assertEquals(null, obj.getStages("all"));
	}

	@Test
	public void testGetStages() {
		queryString1 = "select unique DATABASE_SERVER,WEBSERVER_FRIENDLY from pods where DATABASE_SERVER =\'"
				+ testDbServer
				+ "\' order by DATABASE_SERVER,WEBSERVER_FRIENDLY";
		Vector<ServerDetails> retResult = new Vector<ServerDetails>();
		vs.add(testDbServer);
		vs.add(testStage);
		vs1.add(testDbServer + "2");
		vs1.add(testMasterStageName);
		rs.add(vs);
		rs.add(vs1);
		when(
				mockOc2.runQueryMultipleRows(queryString1,
						NO_OF_COLUMNS.TWO.ordinal())).thenReturn(rs);
		retResult = obj.getStages(testDbServer);
		ServerDetails ch = new ServerDetails(testDbServer, null);
		assertEquals(ch.getServerName(), retResult.firstElement()
				.getServerName());
	}

	@Test
	public void testGetHierarchy() {

		String master = "stage2gg043";
		String testStage = "stage2gg043";
		List<StageNames> childStages = new ArrayList<StageNames>();
		Hierarchy.StageNames child = new Hierarchy.StageNames();
		child.setStage("stage2fn07");
		childStages.add(child);

		queryString = "select WEBSERVER_FRIENDLY from pods WHERE WEBSERVER_DBA in (select WEBSERVER_DBA from pods where WEBSERVER_FRIENDLY='"
				+ testStage + "') AND CURRENT_STATUS = 'Ready'";
		queryString1 = "select WEBSERVER_FRIENDLY from pods WHERE WEBSERVER_DBA in (select WEBSERVER_DBA from pods where WEBSERVER_FRIENDLY='"
				+ testStage + "') AND CURRENT_STATUS = 'NOT Ready'";

		vs.add("stage2gg043");
		vs1.add("stage2fn07");
		rs1.add(vs1);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);

		when(
				mockOc2.runQueryMultipleRows(queryString1,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs1);

		assertEquals(null, obj.getHierarchy(testStage));
		Hierarchy retResult = obj.getHierarchy(testStage);

		Hierarchy ch = new Hierarchy(master, childStages);
		assertEquals(ch.getmasterStage(), retResult.getmasterStage());

		assertEquals(ch.getchildStages().get(0).getStage(), retResult
				.getchildStages().get(0).getStage());

	}

	@Test
	public void testPointother() {
		String testFromStage = "fromstage";
		String testFromPod = "formpod";
		String testFromServerName = "formservername";
		String testToStage = "tostage";
		String testToPod = "topod";
		String testToServerName = "toservername";

		queryString = "select STAGE_NAME from DB_POD_HISTORY where STAGE_NAME='"
				+ testFromStage + "'";
		queryString1 = "insert into DB_POD_HISTORY (STAGE_NAME,LAST_POD_NAME,LAST_UPDATED,LAST_DBSERVER_NAME)";
		queryString1 += " values ('";
		queryString1 += testFromStage + "','" + testFromPod + "',sysdate,'"
				+ testFromServerName + "')";
		queryString2 = "update pods set MONITOR_GG = 'NO', MONITOR_DB = 'NO', current_status = 'NOT Ready', webserver_dba = '";
		queryString2 += testToPod + "', database_server = '" + testToServerName
				+ "'";
		queryString2 += " where webserver_dbafriendly = '" + testFromStage
				+ "'";
		vs.add(testStage);

		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs)
				.thenReturn(vsEmpty);
		when(mockOc2.runUpdate(queryString1)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString2)).thenReturn(1).thenReturn(0);
		when(mockOc2.doCommit()).thenReturn(0).thenReturn(1);

		assertEquals(StringConstants.ERROR_CODES.POINT_DB_ALREADY,
				obj.pointother(testFromStage, testFromPod, testFromServerName,
						testToStage, testToPod, testToServerName));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointother(testFromStage, testFromPod, testFromServerName,
						testToStage, testToPod, testToServerName));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointother(testFromStage, testFromPod, testFromServerName,
						testToStage, testToPod, testToServerName));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointother(testFromStage, testFromPod, testFromServerName,
						testToStage, testToPod, testToServerName));
		assertEquals(StringConstants.ERROR_CODES.NO_ERROR, obj.pointother(
				testFromStage, testFromPod, testFromServerName, testToStage,
				testToPod, testToServerName));
	}

	@Test
	public void testPointSame() {
		String testRevertstage = "revertstage";
		String testServerName = "servername";
		queryString = "select STAGE_NAME from DB_POD_HISTORY where STAGE_NAME='"
				+ testRevertstage + "'";

		queryString1 = "select last_pod_name, last_dbserver_name from db_pod_history where ";
		queryString1 += " stage_name = '" + testRevertstage + "'";

		queryString2 = "update pods set MONITOR_GG = 'YES', MONITOR_DB = 'YES', current_status = 'Ready', webserver_dba = '";
		queryString2 += testPod + "', database_server = '" + testServerName
				+ "'";
		queryString2 += " where webserver_dbafriendly = '" + testRevertstage
				+ "'";

		queryString3 = "delete from db_pod_history where stage_name = '";
		queryString3 += testRevertstage + "'";

		vs.add(testStage);
		vs1.add(testPod);
		vs1.add(testServerName);

		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);
		when(
				mockOc2.runQuerySingleRow(queryString1,
						NO_OF_COLUMNS.TWO.ordinal())).thenReturn(vs1);
		when(mockOc2.runUpdate(queryString2)).thenReturn(1).thenReturn(0);
		when(mockOc2.runUpdate(queryString3)).thenReturn(1).thenReturn(0);
		when(mockOc2.doCommit()).thenReturn(0).thenReturn(1);

		assertEquals(StringConstants.ERROR_CODES.POINT_DB_ALREADY,
				obj.pointsame(testRevertstage));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointsame(testRevertstage));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointsame(testRevertstage));
		assertEquals(StringConstants.ERROR_CODES.INTERNAL_ERROR,
				obj.pointsame(testRevertstage));
		assertEquals(StringConstants.ERROR_CODES.NO_ERROR,
				obj.pointsame(testRevertstage));
	}

	@Test
	public void testCheckAvailableCapacity() {
		int testMaxCapacity = 3550;
		int testTotalProvisioned = 3223;

		queryString = "select sum(capacity) as total from server_capacity where IS_ACTIVE=1";
		Vector<String> mrs = new Vector<String>();
		mrs.add(new Integer(testMaxCapacity).toString());
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(mrs);
		assertEquals(testMaxCapacity, Integer.parseInt(mrs.firstElement()));

		queryString = "select count(*) as provisioned from pods where database_server in (select database_server from server_capacity where IS_ACTIVE=1)";
		Vector<String> mrs2 = new Vector<String>();
		mrs2.add(new Integer(testTotalProvisioned).toString());
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(mrs2);
		assertEquals(testTotalProvisioned,
				Integer.parseInt(mrs2.firstElement()));

		queryString = "select database_server, server_capacity, pod_count, (server_capacity - pod_count)"
				+ " AS available from (select a.database_server, server_capacity, total_capacity, pod_count,sum(pod_count)"
				+ " over () total_provisioned from (select database_server, server_capacity, sum(server_capacity) "
				+ "over () total_capacity from   (select database_server, sum(capacity) server_capacity "
				+ "from server_capacity where is_active = '1' group by database_server)) a, (select database_server, pod_count"
				+ " from   (select database_server, count(*) pod_count "
				+ "from pods Group by database_server)) b Where a.database_server = b.database_server (+)) "
				+ "order by 4 desc";
		int testServerCapacity = 65;
		int testAvailableCapacity = 30;
		int testPodCount = 35;
		vs.add(testDbServer);
		vs.add(new Integer(testServerCapacity).toString());
		vs.add(new Integer(testPodCount).toString());
		vs.add(new Integer(testAvailableCapacity).toString());
		rs.add(vs);
		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.FOUR.ordinal())).thenReturn(rsEmpty)
				.thenReturn(rs);
		assertEquals(null, obj.checkAvailableCapacity());

		CapacityCheck capacityCheck = obj.checkAvailableCapacity();
		assertEquals(testMaxCapacity, capacityCheck.getMaxCapacity());
		assertEquals((testMaxCapacity - testTotalProvisioned),
				capacityCheck.getAvailableCapacity());

		List<DatabaseServer> serversList = capacityCheck.getServerList();
		assertEquals(rs.firstElement().get(0), serversList.get(0)
				.getServerName());
		assertEquals(Integer.parseInt(rs.firstElement().get(1)), serversList
				.get(0).getTotalCapacity());
		assertEquals(Integer.parseInt(rs.firstElement().get(2)), serversList
				.get(0).getTotalProvisioned());
		assertEquals(Integer.parseInt(rs.firstElement().get(3)), serversList
				.get(0).getAvailableCapacity());
	}

	@Test
	public void testGetDbServersList() {
		queryString = "SELECT DISTINCT database_server FROM  pods UNION SELECT DISTINCT host FROM lvs_spl_db_list ORDER BY 1";
		vs.add("lvspgdb01");
		rs.add(vs);
		rsEmpty = null;
		when(
				mockOc2.runQueryMultipleRows(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(rs)
				.thenReturn(rsEmpty);
		assertNull(rsEmpty);
		Vector<DbServer> result = new Vector<DbServer>();
		DbServer serverList = new DbServer("lvspgdb01");
		result = obj.getDbServersList();
		assertEquals(serverList.getServerName(),
				result.get(NO_OF_COLUMNS.ZERO.ordinal()).getServerName());
	}

	@Test
	public void testGetCloneDetailsByID() {
		queryString = "select DB_CLONE_FROM, DB_STAGE_TO, SNAPSHOT_VERSION, nvl(to_char(CLONE_START_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), "
				+ "nvl(to_char(CLONE_END_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), nvl(to_char(QUEUE_START_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), "
				+ "nvl(to_char(QUEUE_END_TIME,'DD-Mon-YYYY HH24:MI:SS'),' '), nvl(CURRENT_STATUS,' '), nvl(EXTENDED_STATUS,' '), "
				+ "SID_LIST, CYCLE_FOR_CLONE, nvl(CAUSE_OF_FAILURE,' ') from gsm_ops where REQUEST_ID = "
				+ testID;
		CloneDetails retResult = new CloneDetails();
		vs.add(testDbServer);
		vs.add(testStage);
		vs.add(testSnapshotVersion);
		vs.add(testDate);
		vs.add(testDate);
		vs.add(testDate);
		vs.add(testDate);
		vs.add(testStatus);
		vs.add(testStatus);
		vs.add(testCloneOption);
		vs.add(testCloneCycle);
		vs.add(testExtendedStatus);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.TWELVE.ordinal())).thenReturn(vs);
		retResult = obj.getCloneDetailsByID(testID);
		CloneDetails expected = new CloneDetails();
		expected.setCloneStartTime(testDate);
		assertEquals(expected.getCloneStartTime(),
				retResult.getCloneStartTime());
	}

	@Test
	public void testGetRequestID() {
		queryString = "select gsm_ops_seq.nextval from dual";
		vs.add(testID);
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vs);
		assertEquals(testID, obj.getRequestID());
	}

	@Test
	public void testUpdateListenerStatus() {
		queryString = "update server_capacity set reload_inprogress = '1' where database_server = '"
				+ testDbServer + "'";
		when(mockOc2.runUpdate(queryString)).thenReturn(1);
		obj.updateListenerStatus(testDbServer, 1);
		verify(mockOc2).runUpdate(queryString);
	}

	@Test
	public void testIsListenerReloadInProgress() {
		queryString = "select RELOAD_INPROGRESS from server_capacity where DATABASE_SERVER='"
				+ testDbServer + "'";
		vs.add("0");
		when(
				mockOc2.runQuerySingleRow(queryString,
						NO_OF_COLUMNS.ONE.ordinal())).thenReturn(vsEmpty)
				.thenReturn(vs);
		assertEquals(true, obj.isListenerReloadInProgress(testDbServer));
		assertEquals(false, obj.isListenerReloadInProgress(testDbServer));
	}
}