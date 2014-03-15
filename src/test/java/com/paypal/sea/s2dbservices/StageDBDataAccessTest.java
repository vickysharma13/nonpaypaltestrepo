/*package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.cglib.core.MethodWrapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.paypal.sea.s2dbservices.StringConstants.DBSTATUS;
import com.paypal.sea.s2dbservices.oracledbaccess.CatalogDataAccess;
import com.paypal.sea.s2dbservices.oracledbaccess.DbConnection;
import com.paypal.sea.s2dbservices.oracledbaccess.StageDBDataAccess;
import com.paypal.sea.s2dbservices.oracledbaccess.OracleConnection2;
import com.paypal.sea.s2dbservices.oracledbaccess.StageDetails;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ StageDBDataAccess.class })
public class StageDBDataAccessTest {
	OracleConnection2 mockOc2 = mock(OracleConnection2.class);
	StageDetails mockSd = mock(StageDetails.class);
	CatalogDataAccess mockODA = mock(CatalogDataAccess.class);
	DbConnection mockDbConn = mock(DbConnection.class);
	String testID = "1000";
	String testPod = "AAA";
	String testStage = "stage2p12345";
	String testDbServer = "lvsvmdb00";
	StageDBDataAccess obj = new StageDBDataAccess();

	@Test
	public void testConnectStageDb() {
		String testDbServer = "lvsvmdb01";
		String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";

		int testCode = 1;
		String testException = "Exception";

		StageDetails testStageDetails = new StageDetails();
		testStageDetails.setCloneType(testCloneOption);
		testStageDetails.setPodName(testPod);
		testStageDetails.setDBServerName(testDbServer);

		try {
			whenNew(OracleConnection2.class).withNoArguments().thenReturn(
					mockOc2);
			whenNew(OracleConnection2.class).withArguments(Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString()).thenReturn(
					mockOc2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		when(mockOc2.getExceptionCode()).thenReturn(testCode);
		when(mockOc2.getExceptionMessage()).thenReturn(testException);
		when(mockOc2.getDBStatus()).thenReturn(DBSTATUS.DBERROR).thenReturn(
				DBSTATUS.DBUP);
		// when(mockDbConn.reloadListener(testDbServer)).thenReturn(0);
		Mockito.doNothing().when(mockOc2).closeConnection();

		assertEquals(0, obj.connectStageDB(testStage, testStageDetails));
		assertEquals(testCode, testStageDetails.getExceptionCode());
		assertEquals(testException, testStageDetails.getExceptionMessage());
		assertEquals(DBSTATUS.DBERROR, testStageDetails.getDBStatus());

		assertEquals(0, obj.connectStageDB(testStage, testStageDetails));
		assertEquals(DBSTATUS.DBUP, testStageDetails.getDBStatus());
		assertEquals(DBSTATUS.DBUP, testStageDetails.getDbStatusPay());
		assertEquals(DBSTATUS.DBUP, testStageDetails.getDbStatusPayPilot());
	}

	@Test
	public void testConnectStageDbWaitForListenerReload() {
		String testDbServer = "lvsvmdb01";
		String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";

		int testCode = 12519;
		String testException = "Exception";

		StageDetails testStageDetails = new StageDetails();
		testStageDetails.setCloneType(testCloneOption);
		testStageDetails.setPodName(testPod);
		testStageDetails.setDBServerName(testDbServer);

		try {
			whenNew(OracleConnection2.class).withNoArguments().thenReturn(
					mockOc2);
			whenNew(OracleConnection2.class).withArguments(Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString()).thenReturn(
					mockOc2);
			whenNew(DbConnection.class).withNoArguments()
					.thenReturn(mockDbConn);
			whenNew(CatalogDataAccess.class).withNoArguments().thenReturn(
					mockODA);

		} catch (Exception e) {
			e.printStackTrace();
		}
		when(mockOc2.getExceptionCode()).thenReturn(testCode);
		when(mockOc2.getExceptionMessage()).thenReturn(testException);
		when(mockOc2.getDBStatus()).thenReturn(DBSTATUS.DBERROR);
		when(mockODA.isListenerReloadInProgress(testDbServer)).thenReturn(true);
		Mockito.doNothing().when(mockOc2).closeConnection();

		assertEquals(0, obj.connectStageDB(testStage, testStageDetails));
		assertEquals(testCode, testStageDetails.getExceptionCode());
		assertEquals(testException, testStageDetails.getExceptionMessage());
		assertEquals(DBSTATUS.DBERROR, testStageDetails.getDBStatus());
	}

	@Test
	public void testConnectStageDbListenerReload() {
		String testDbServer = "lvsvmdb01";
		String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";

		int testCode = 12519;
		String testException = "Exception";

		StageDetails testStageDetails = new StageDetails();
		testStageDetails.setCloneType(testCloneOption);
		testStageDetails.setPodName(testPod);
		testStageDetails.setDBServerName(testDbServer);

		try {
			whenNew(OracleConnection2.class).withNoArguments().thenReturn(
					mockOc2);
			whenNew(OracleConnection2.class).withArguments(Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString()).thenReturn(
					mockOc2);
			whenNew(DbConnection.class).withNoArguments()
					.thenReturn(mockDbConn);
			whenNew(CatalogDataAccess.class).withNoArguments().thenReturn(
					mockODA);

		} catch (Exception e) {
			e.printStackTrace();
		}
		when(mockOc2.getExceptionCode()).thenReturn(testCode);
		when(mockOc2.getExceptionMessage()).thenReturn(testException);
		when(mockOc2.getDBStatus()).thenReturn(DBSTATUS.DBERROR)
				.thenReturn(DBSTATUS.DBUP).thenReturn(DBSTATUS.DBERROR);
		when(mockODA.isListenerReloadInProgress(testDbServer))
				.thenReturn(false);
		when(mockDbConn.reloadListener(testDbServer)).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR.ordinal()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR.ordinal());
		Mockito.doNothing().when(mockOc2).closeConnection();

		assertEquals(0, obj.connectStageDB(testStage, testStageDetails));
		assertEquals(testCode, testStageDetails.getExceptionCode());
		assertEquals(testException, testStageDetails.getExceptionMessage());
		assertEquals(DBSTATUS.DBUP, testStageDetails.getDBStatus());
		assertEquals(0, obj.connectStageDB(testStage, testStageDetails));
		assertEquals(testCode, testStageDetails.getExceptionCode());
		assertEquals(testException, testStageDetails.getExceptionMessage());
		assertEquals(DBSTATUS.DBERROR, testStageDetails.getDBStatus());
	}

}*/
