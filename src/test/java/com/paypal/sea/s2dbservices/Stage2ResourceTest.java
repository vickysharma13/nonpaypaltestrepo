package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;
import java.lang.ClassNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.WebApplicationException;

import com.paypal.sea.s2dbservices.oracledbaccess.*;
import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy.StageNames;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Stage2Resource.class })
public class Stage2ResourceTest {

	CatalogDataAccess mockODA = mock(CatalogDataAccess.class);
	DbConnection mockDbConn = mock(DbConnection.class);
	StageDetails mockSd = mock(StageDetails.class);
	StageDetails mockTargetSd = mock(StageDetails.class);
	ReentrantLock mockLock = mock(ReentrantLock.class);
	StartDbInput mockStart = mock(StartDbInput.class);
	ShutDbInput mockShut = mock(ShutDbInput.class);
	PegaCloneInput mockPega = mock(PegaCloneInput.class);
	SnapshotDetail mockSnapDetails = mock(SnapshotDetail.class);
	CloneHistory mockCloneHistory = mock(CloneHistory.class);
	ServerDetails mockServerDetails = mock(ServerDetails.class);
	CloneDetails mockCloneDetails = mock(CloneDetails.class);
	CapacityCheck mockCapacityCheck = mock(CapacityCheck.class);
	ShareDBInput mockShareDbInput = mock(ShareDBInput.class);
	Input mockInput = mock(Input.class);
	Metric mockMetrics = mock(Metric.class);
	Vector<DbServer> dbEmpty = new Vector<DbServer>();

	Stage2Resource obj = new Stage2Resource(mockODA, mockDbConn, mockLock);
	String stagename = "stage2gg043";
	List<StageNames> childStages = new ArrayList<StageNames>();
	Hierarchy.StageNames child = new Hierarchy.StageNames();
	String testID = "1000";
	String testPod = "AAA";
	String testStage = "stage2p1234";
	String testDbServer = "lvsvmdb01";
	String testCloneVersion = "99RQALVS11G";
	String testSnapshotVersion = "99RQALVS11G06";
	String testCloneCycle = "RQA";
	String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";
	String testDate = "01-jan-13";
	String testUsername = "clocapp";
	String testUserPay = "pay";
	String testPasswordPay = "pay";
	String testPassword = "clocappstg";
	String testPort = "2126";
	String testMeta = "METADB";
	String testStatus = "SUCCESS";
	StringConstants.DBSTATUS testDbStatus = StringConstants.DBSTATUS.DBUP;
	StringConstants.DBSTATUS testDbStatusDown = StringConstants.DBSTATUS.DBDOWN;
	String testFiler = "lvsvna01";
	String testHal = "HAL_LVS2";
	String testStartDate = "15-Apr-13";
	String testEndDate = "25-Apr-13";
	String invalidDate = "01-Apm-13";

	@Test(expected = WebApplicationException.class)
	public void testStartuprongStage() {

		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.startDb(mockStart);
	}

	@Test(expected = WebApplicationException.class)
	public void testStartupNotCloned() {

		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(
				StringConstants.DBSTATUS.DBNOTCLONED);
		obj.startDb(mockStart);
	}

	@Test(expected = WebApplicationException.class)
	public void testStartupErrorInConnectingDb() {
		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.startDb(mockStart);
	}

	@Test(expected = WebApplicationException.class)
	public void testStartupAlreadyUp() {

		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatus);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatus);
		obj.startDb(mockStart);
	}

	@Test(expected = WebApplicationException.class)
	public void testStartupFail() {

		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatusDown);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatusDown);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatusDown);
		when(mockDbConn.startup("QADBA" + testPod, testDbServer)).thenReturn(1);
		obj.startDb(mockStart);
	}

	@Test
	public void testStartupSuccessful() {

		when(mockStart.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatusDown);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatusDown);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatusDown);
		when(mockDbConn.startup("QADBA" + testPod, testDbServer)).thenReturn(0);
		when(mockDbConn.startup("QADBB" + testPod, testDbServer)).thenReturn(0);
		when(mockDbConn.startup("QADBC" + testPod, testDbServer)).thenReturn(0);
		Status expected = new Status();
		expected.setMessage("stage2p1234 DB startup successful");
		Status actual = obj.startDb(mockStart);
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	public void testIsAlive() {

		Status expected = new Status();
		expected.setMessage("1");
		Status actual = obj.testAvailability();
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdowWrongStage() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.shutdownDb(mockShut);
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdowInputError() {
		when(mockShut.getStageName()).thenReturn(null);
		obj.shutdownDb(mockShut);
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdownNotCloned() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(
				StringConstants.DBSTATUS.DBNOTCLONED);
		obj.shutdownDb(mockShut);
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdowDbError() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.shutdownDb(mockShut);
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdownAlreadyDown() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatusDown);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatusDown);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatusDown);
		obj.shutdownDb(mockShut);
	}

	@Test(expected = WebApplicationException.class)
	public void testShutdownFail() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatus);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatus);
		when(mockDbConn.shutdown("QADBA" + testPod, testDbServer))
				.thenReturn(1);
		obj.shutdownDb(mockShut);
	}

	@Test
	public void testShutdownSuccessful() {

		when(mockShut.getStageName()).thenReturn(testStage);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockSd.getDbStatusPay()).thenReturn(testDbStatus);
		when(mockSd.getDbStatusPayPilot()).thenReturn(testDbStatus);
		when(mockDbConn.shutdown("QADBA" + testPod, testDbServer))
				.thenReturn(0);
		when(mockDbConn.shutdown("QADBB" + testPod, testDbServer))
				.thenReturn(0);
		when(mockDbConn.shutdown("QADBC" + testPod, testDbServer))
				.thenReturn(0);
		Status expected = new Status();
		expected.setMessage("stage2p1234 DB shutdown successful");
		Status actual = obj.shutdownDb(mockShut);
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test(expected = WebApplicationException.class)
	public void testRemoveWrongStage() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		try {
			obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	@Test(expected = WebApplicationException.class)
	public void testRemoveDbError() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		try {
			obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	@Test(expected = WebApplicationException.class)
	public void testRemoveIfInprogress() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getCloneStatus()).thenReturn("INPROGRESS");
		try {
			obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	@Test(expected = WebApplicationException.class)
	public void testRemoveAlreadyInProgress() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getCloneStatus()).thenReturn(testStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockODA.checkRemovalInProgress(testPod)).thenReturn(1);
		try {
			obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	@Test(expected = WebApplicationException.class)
	public void testRemoveInternalError() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getCloneStatus()).thenReturn(testStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockODA.checkRemovalInProgress(testPod)).thenReturn(0);
		try {
			when(mockDbConn.removeStage(testStage, testDbServer)).thenReturn(1);
			obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void testRemoveSuccessful() {

		Status expected = new Status();
		expected.setMessage(testStage + " has been successfully removed");
		Status actual = null;

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(testDbStatus);
		when(mockSd.getCloneStatus()).thenReturn(testStatus);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockODA.checkRemovalInProgress(testPod)).thenReturn(0);
		try {
			when(mockDbConn.removeStage(testStage, testDbServer)).thenReturn(0);
			actual = obj.remove(testStage);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}

		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test(expected = WebApplicationException.class)
	public void testCloneRequestInputError() {

		when(mockODA.validateInput(mockInput)).thenReturn("Wrong Input");
		when(mockInput.getStageName()).thenReturn(testStage);
		obj.cloneRequest(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCloneRequestWrongStage() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);

		obj.cloneRequest(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCloneRequestAlreadyInQueue() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockODA.getRequestID()).thenReturn(testID);
		when(
				mockODA.createCloneRequest(testStage, testCloneVersion,
						testCloneCycle, testCloneOption, testID)).thenReturn(
				StringConstants.ERROR_CODES.ALREADY_IN_QUEUE.ordinal());

		obj.cloneRequest(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCloneRequestVersionNotExists() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockODA.getRequestID()).thenReturn(testID);
		when(
				mockODA.createCloneRequest(testStage, testCloneVersion,
						testCloneCycle, testCloneOption, testID)).thenReturn(
				StringConstants.ERROR_CODES.VERSION_NOT_EXISTS.ordinal());

		obj.cloneRequest(mockInput);
	}

	@Test
	public void testCloneRequestSuccessfuls() {

		Status expected = new Status();
		expected.setMessage("Cloning in progress");
		CloneResponse actual = null;

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getIsCloneable()).thenReturn(true);
		when(
				mockODA.createCloneRequest(testStage, testCloneVersion,
						testCloneCycle, testCloneOption, testID)).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR.ordinal());

		actual = obj.cloneRequest(mockInput);
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test(expected = WebApplicationException.class)
	public void testCloneRequestDbError() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);

		obj.cloneRequest(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbInputError() {

		when(mockODA.validateInput(mockInput))
				.thenReturn("Some error detected");
		when(mockInput.getStageName()).thenReturn(testStage);
		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbAlreadyPresent() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);

		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbCapacityFull() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		// when(mockSd.getDBStatus()).thenReturn(StringConstants.DBSTATUS.NA);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(null);

		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbInternalErrorFilerNotFound() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(null);

		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbInternalErrorMasterVersionNotFound() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(testFiler);
		when(mockODA.getMasterVersion(testCloneVersion)).thenReturn(null);

		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbInternalErrorInsertMetadataFailed() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(testFiler);
		when(mockODA.getMasterVersion(testCloneVersion)).thenReturn(
				testSnapshotVersion);
		when(
				mockODA.insertMetadata(Integer.parseInt(testID),
						testStage.substring(5), testStage, testDbServer,
						testPod, testFiler, testHal, testCloneVersion,
						testCloneOption, testCloneCycle, testSnapshotVersion))
				.thenReturn(0);

		obj.createDb(mockInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testCreateDbScriptFail() {

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(testFiler);
		when(mockODA.getMasterVersion(testCloneVersion)).thenReturn(
				testSnapshotVersion);
		when(
				mockODA.insertMetadata(Integer.parseInt(testID),
						testStage.substring(5), testStage, testDbServer,
						testPod, testFiler, testHal, testCloneVersion,
						testCloneOption, testCloneCycle, testSnapshotVersion))
				.thenReturn(1);

		when(
				mockDbConn.createDbInstance(testPod, testStage, testFiler,
						testCloneVersion, testCloneCycle, testCloneOption,
						testDbServer)).thenReturn(1);
		obj.createDb(mockInput);
	}

	@Test
	public void testCreateDbSuccessful() {

		Status expected = new Status();
		expected.setMessage("The database instance has been created. Cloning in progress.");
		Status actual = null;
		Status actualNewPod = null;

		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod).thenReturn(null);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(testFiler);
		when(mockODA.getMasterVersion(testCloneVersion)).thenReturn(
				testSnapshotVersion);
		when(
				mockODA.insertMetadata(Integer.parseInt(testID),
						testStage.substring(5), testStage, testDbServer,
						testPod, testFiler, testHal, testCloneVersion,
						testCloneOption, testCloneCycle, testSnapshotVersion))
				.thenReturn(1);
		when(mockODA.getMaxPod()).thenReturn(testPod);
		when(mockODA.findUniqueName(testPod)).thenReturn(testPod);

		when(
				mockDbConn.createDbInstance(testPod, testStage, testFiler,
						testCloneVersion, testCloneCycle, testCloneOption,
						testDbServer)).thenReturn(0);
		actual = obj.createDb(mockInput);
		actualNewPod = obj.createDb(mockInput);

		assertEquals(expected.getMessage(), actual.getMessage());
		assertEquals(expected.getMessage(), actualNewPod.getMessage());
	}

	// @Test
	public void testCreateDbException() {

		Status actual = null;
		Status actual2 = null;
		when(mockODA.validateInput(mockInput)).thenReturn("");
		when(mockInput.getStageName()).thenReturn(testStage);
		when(mockInput.getCloneVersion()).thenReturn(testCloneVersion);
		when(mockInput.getCloneCycle()).thenReturn(testCloneCycle);
		when(mockInput.getCloneOption()).thenReturn(testCloneOption);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		when(mockODA.checkUnusedPod()).thenReturn(testPod);
		when(mockODA.getDbServer()).thenReturn(testDbServer);
		when(mockODA.getPID()).thenReturn(testID);
		when(mockODA.getHal()).thenReturn("1");
		when(mockODA.getFiler(testDbServer)).thenReturn(testFiler);
		when(mockODA.getMasterVersion(testCloneVersion)).thenReturn(
				testSnapshotVersion);
		when(
				mockODA.insertMetadata(Integer.parseInt(testID),
						testStage.substring(5), testStage, testDbServer,
						testPod, testFiler, testHal, testCloneVersion,
						testCloneOption, testCloneCycle, testSnapshotVersion))
				.thenReturn(1);
		when(mockODA.getMaxPod()).thenReturn(testPod);
		when(mockODA.findUniqueName(testPod)).thenReturn(testPod);

		when(
				mockDbConn.createDbInstance(testPod, testStage, testFiler,
						testCloneVersion, testCloneCycle, testCloneOption,
						testDbServer)).thenThrow(new InterruptedException())
				.thenThrow(new IOException());
		actual = obj.createDb(mockInput);
		actual2 = obj.createDb(mockInput);
		assertEquals(null, actual.getMessage());
		assertEquals(null, actual2.getMessage());
	}

	@Test
	public void testGetStageDetailsPresentStage() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getDBStatus()).thenReturn(StringConstants.DBSTATUS.DBUP);
		assertEquals(obj.getStageDetails(testStage).getDBStatus(),
				StringConstants.DBSTATUS.DBUP);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetStageDetailsInvalidStage() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.getStageDetails(testStage);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetStageDetailsLongName() {
		obj.getStageDetails(testStage + "with-a-very-long-name");
	}

	@Test(expected = WebApplicationException.class)
	public void testGetStageDetailsDbError() {

		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.getStageDetails(testStage);
	}

	@Test
	public void testGetMetrics() {

		List<MetricsSet> actual = new ArrayList<MetricsSet>();
		List<Metric> metricList = new ArrayList<Metric>();
		Metric metric = new Metric();
		metric.setAverageTimeElapsed(200);
		metric.setCount(50);
		metric.setErrorCount(20);
		metric.setMaxTime(300);
		metric.setMinTime(100);
		metric.setType("details");
		metricList.add(metric);
		actual.add(new MetricsSet(testDate, metricList));

		when(mockODA.getMetrics(testStartDate, "26-Apr-13")).thenReturn(actual);

		List<MetricsSet> ret = obj.getMetrics(testStartDate, testEndDate);
		verify(mockODA).getMetrics(testStartDate, "26-Apr-13");
		assertEquals(ret, actual);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetMetricsFailure() {
		when(mockODA.getMetrics(testStartDate, "26-Apr-13")).thenReturn(null);
		obj.getMetrics(testStartDate, testEndDate);
		verify(mockODA).getMetrics(testStartDate, "26-Apr-13");
	}

	@Test
	public void testGetMetricsToday() {

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy",
				Locale.ENGLISH);
		Date date = new Date();
		String startDate = dateFormat.format(date).toString();
		String endDate = dateFormat.format(date).toString();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		endDate = dateFormat.format(cal.getTime()).toString();

		List<MetricsSet> actual = new ArrayList<MetricsSet>();
		List<Metric> metricList = new ArrayList<Metric>();
		Metric metric = new Metric();
		metric.setAverageTimeElapsed(200);
		metric.setCount(50);
		metric.setErrorCount(20);
		metric.setMaxTime(300);
		metric.setMinTime(100);
		metric.setType("details");
		metricList.add(metric);
		actual.add(new MetricsSet(startDate, metricList));

		when(mockODA.getMetrics(startDate, endDate)).thenReturn(actual);
		List<MetricsSet> ret = obj.getMetrics("today", "today");

		verify(mockODA).getMetrics(startDate, endDate);
		assertEquals(ret, actual);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetMetricsParseException() {
		obj.getMetrics("not_a_date", "not_a_date");
		verify(mockODA, org.mockito.Mockito.never()).getMetrics(
				org.mockito.Mockito.any(String.class),
				org.mockito.Mockito.any(String.class));
	}

	@Test(expected = WebApplicationException.class)
	public void testGetMetricsWrongInput() {

		obj.getMetrics(invalidDate, invalidDate);
	}

	@Test(expected = WebApplicationException.class)
	public void testPegaCloneRequestInputError() {
		when(mockPega.getSource()).thenReturn(testDbServer);
		when(mockPega.getTarget()).thenReturn(testDbServer);
		when(mockPega.getVersion()).thenReturn("9G");
		obj.pegaClone(mockPega);
	}

	@Test(expected = WebApplicationException.class)
	public void testPegaCloneUnsuccessful() {
		when(mockPega.getSource()).thenReturn(testDbServer);
		when(mockPega.getTarget()).thenReturn(testDbServer);
		when(mockPega.getVersion()).thenReturn("10G");
		when(mockDbConn.runPegaClone(testDbServer, testDbServer, "10G"))
				.thenReturn(14);
		obj.pegaClone(mockPega);
	}

	@Test
	public void testPegaCloneSuccessful() {

		Status expected = new Status();
		expected.setMessage("PEGA clone completed from " + testDbServer
				+ " to " + testDbServer);
		Status actual = null;
		when(mockPega.getSource()).thenReturn(testDbServer);
		when(mockPega.getTarget()).thenReturn(testDbServer);
		when(mockPega.getVersion()).thenReturn("10G");
		when(mockDbConn.runPegaClone(testDbServer, testDbServer, "10G"))
				.thenReturn(0);
		actual = obj.pegaClone(mockPega);
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test(expected = WebApplicationException.class)
	public void testPegaCloneException() {
		when(mockPega.getSource()).thenReturn(testDbServer);
		when(mockPega.getTarget()).thenReturn(testDbServer);
		when(mockPega.getVersion()).thenReturn("10G");
		when(mockDbConn.runPegaClone(testDbServer, testDbServer, "10G"))
				.thenReturn(
						StringConstants.ERROR_CODES.WRONG_NO_OF_ARGUMENTS
								.ordinal() + 1);
		obj.pegaClone(mockPega);
	}

	@Test
	public void testGetCurrentDBVersions() {

		Vector<SnapshotDetail> actual = new Vector<SnapshotDetail>();
		Vector<SnapshotDetail> ret = new Vector<SnapshotDetail>();
		ret.add(mockSnapDetails);

		when(mockODA.getSnapshotVersions()).thenReturn(ret);
		actual = obj.getCurrentDBVersions();

		verify(mockODA).getSnapshotVersions();
		assertEquals(ret, actual);
	}

	@Test
	public void testGetListOfdbServers() {
		DbServer server = new DbServer("lvspgdb01");
		Vector<DbServer> actual = new Vector<DbServer>();
		Vector<DbServer> expected = new Vector<DbServer>();
		expected.add(server);
		when(mockODA.getDbServersList()).thenReturn(expected);
		actual = obj.getListOfdbServers();
		verify(mockODA).getDbServersList();
		assertEquals(expected, actual);
	}
	
	@Test(expected = WebApplicationException.class)
	public void testGetListofDbServersIncorrectServer() {
		when(mockODA.getDbServersList()).thenReturn(null);
		obj.getListOfdbServers();
	}  

	@Test
	public void testgetStageHierarchy() {

		child.setStage("stage2fn07");
		childStages.add(child);
		Hierarchy ret = new Hierarchy(stagename, childStages);
		when(mockODA.findDBInfo(stagename)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockODA.getHierarchy(stagename)).thenReturn(ret);
		assertEquals(ret, obj.getStageHierarchy(stagename));
		verify(mockODA).getHierarchy(stagename);

	}

	@Test(expected = WebApplicationException.class)
	public void testgetStageHierarchyFailure() {
		when(mockODA.findDBInfo(stagename)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.getStageHierarchy(stagename);
	}

	@Test(expected = WebApplicationException.class)
	public void testgetStageHierarchyInternal() {
		when(mockODA.findDBInfo(stagename)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.getStageHierarchy(stagename);
	}

	@Test
	public void testGetDBCloneHistory() {
		Vector<CloneHistory> ret = new Vector<CloneHistory>();
		Vector<CloneHistory> actual = new Vector<CloneHistory>();
		ret.add(mockCloneHistory);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockODA.getCloneHistory(testStage)).thenReturn(ret);
		actual = obj.getDBCloneHistory(testStage);
		verify(mockODA).findDBInfo(testStage);
		verify(mockODA).getCloneHistory(testStage);
		assertEquals(ret, actual);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetDBCloneHistoryInvalidStage() {
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.getDBCloneHistory(testStage);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetDBCloneHistoryInternalError() {
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.getDBCloneHistory(testStage);
	}

	@Test
	public void testGetStageNames() {
		Vector<ServerDetails> ret = new Vector<ServerDetails>();
		Vector<ServerDetails> actual = new Vector<ServerDetails>();
		ret.add(mockServerDetails);
		when(mockODA.getStages(testDbServer)).thenReturn(ret);
		actual = obj.getStageNames(testDbServer);
		verify(mockODA).getStages(testDbServer);
		assertEquals(ret, actual);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetStageNamesException() {
		when(mockODA.getStages(testDbServer)).thenReturn(null);
		obj.getStageNames(testDbServer);
	}

	@Test
	public void testGetDBCloneStatus() {
		when(mockODA.getCloneDetailsByID(testID)).thenReturn(mockCloneDetails);
		CloneDetails ret = obj.getDBCloneStatus(testID);
		verify(mockODA).getCloneDetailsByID(testID);
		assertEquals(ret, mockCloneDetails);
	}

	@Test(expected = WebApplicationException.class)
	public void testGetDBCloneStatusException() {
		when(mockODA.getCloneDetailsByID(testID)).thenReturn(null);
		obj.getDBCloneStatus(testID);
	}

	@Test
	public void testGetCapacityDetails() {
		when(mockODA.checkAvailableCapacity()).thenReturn(mockCapacityCheck);
		CapacityCheck ret = obj.getCapacityDetails();
		verify(mockODA).checkAvailableCapacity();
		assertEquals(ret, mockCapacityCheck);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoWrongInput() {
		when(mockShareDbInput.getFromStageName()).thenReturn("");
		when(mockShareDbInput.getToStageName()).thenReturn(testStage);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoFromStageNotFound() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn(stagename);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoInternalError() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn(stagename);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoTargetStageNotFound() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn(stagename);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockODA.findDBInfo(stagename)).thenReturn(mockTargetSd);
		when(mockTargetSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.STAGE_NOT_FOUND);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoTargetInternalError() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn(stagename);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockODA.findDBInfo(stagename)).thenReturn(mockTargetSd);
		when(mockTargetSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoFailed() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn("");
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockODA.pointsame(testStage)).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.pointto(mockShareDbInput);
	}

	@Test(expected = WebApplicationException.class)
	public void testPointtoFailedAtTarget() {
		when(mockShareDbInput.getFromStageName()).thenReturn(testStage);
		when(mockShareDbInput.getToStageName()).thenReturn(stagename);
		when(mockODA.findDBInfo(testStage)).thenReturn(mockSd);
		when(mockSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockSd.getPodName()).thenReturn(testPod);
		when(mockSd.getDBServerName()).thenReturn(testDbServer);
		when(mockODA.findDBInfo(stagename)).thenReturn(mockTargetSd);
		when(mockTargetSd.getErrorCode()).thenReturn(
				StringConstants.ERROR_CODES.NO_ERROR);
		when(mockTargetSd.getPodName()).thenReturn(testPod);
		when(mockTargetSd.getDBServerName()).thenReturn(testDbServer);
		when(
				mockODA.pointother(testStage, testPod, testDbServer, stagename,
						testPod, testDbServer)).thenReturn(
				StringConstants.ERROR_CODES.INTERNAL_ERROR);
		obj.pointto(mockShareDbInput);
	}

}