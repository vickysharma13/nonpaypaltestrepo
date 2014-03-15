package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.StageDetails;

public class StageDetailsTest {

    StageDetails sdObj = new StageDetails();

    String testID = "1000";
    long testValue = 1000;
    String testPod = "AAA";
    String testStage = "stage2p1234";
    String testDbServer = "lvsvmdb01";
    String testCloneVersion = "99RQALVS11G";
    String testSnapshotVersion = "99RQALVS11G06";
    String testCloneCycle = "RQA";
    String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";
    String testDate = "01-jan-13";
    String testStatus = "SUCCESS";
    StringConstants.DBSTATUS testDbStatus = StringConstants.DBSTATUS.DBDOWN;
    String testType = "apiName";
    String testVersion = "10G";
    int testExceptionCode = 0;
    String testExceptionMessage = "Test Message";
    StringConstants.ERROR_CODES testErrorCode = StringConstants.ERROR_CODES.NO_ERROR;

    @Test
    public void testGetAndSetCloneCycle() {
        sdObj.setCloneCycle(testCloneCycle);
        assertEquals(testCloneCycle, sdObj.getCloneCycle());
    }

    @Test
    public void testGetAndSetCloneDate() {
        sdObj.setCloneDate(testDate);
        assertEquals(testDate, sdObj.getCloneDate());
    }

    @Test
    public void testGetAndSetCloneStatus() {
        sdObj.setCloneStatus(testStatus);
        assertEquals(testStatus, sdObj.getCloneStatus());
    }

    @Test
    public void testGetAndSetCloneType() {
        sdObj.setCloneType(testType);
        assertEquals(testType, sdObj.getCloneType());
    }

    @Test
    public void testGetAndSetCloneVersion() {
        sdObj.setCloneVersion(testCloneVersion);
        assertEquals(testCloneVersion, sdObj.getCloneVersion());
    }

    @Test
    public void testGetAndSetDBServerName() {
        sdObj.setDBServerName(testDbServer);
        assertEquals(testDbServer, sdObj.getDBServerName());
    }

    @Test
    public void testGetAndSetDBStatus() {
        sdObj.setDBStatus(testDbStatus);
        assertEquals(testDbStatus, sdObj.getDBStatus());
    }

    @Test
    public void testGetAndSetStatusPay() {
        sdObj.setDbStatusPay(testDbStatus);
        assertEquals(testDbStatus, sdObj.getDbStatusPay());
    }

    @Test
    public void testGetAndSetStatusPayPilot() {
        sdObj.setDbStatusPayPilot(testDbStatus);
        assertEquals(testDbStatus, sdObj.getDbStatusPayPilot());
    }

    @Test
    public void testGetAndSetIsCloneable() {
        sdObj.setIsCloneable(true);
        assertEquals(true, sdObj.getIsCloneable());
    }

    @Test
    public void testGetAndSetLatestVersion() {
        sdObj.setLatestVersion(testSnapshotVersion);
        assertEquals(testSnapshotVersion, sdObj.getLatestVersion());
        ;
    }

    @Test
    public void testGetAndSetStageName() {
        sdObj.setStageName(testStage);
        assertEquals(testStage, sdObj.getStageName());
    }

    @Test
    public void testGetAndSetMasterStageName() {

        sdObj.setMasterStageName(testStage);
        assertEquals(testStage, sdObj.getMasterStageName());
    }

    @Test
    public void testGetAndSetPodName() {
        sdObj.setPodName(testPod);
        assertEquals(testPod, sdObj.getPodName());
    }

    @Test
    public void testGetAndSetErrorCode() {
        sdObj.setErrorCode(testErrorCode);
        assertEquals(testErrorCode, sdObj.getErrorCode());
    }

    @Test
    public void testGetAndSetExceptionCode() {
        sdObj.setExceptionCode(testExceptionCode);
        assertEquals(testExceptionCode, sdObj.getExceptionCode());
    }

    @Test
    public void testGetAndSetExceptionMessage() {
        sdObj.setExceptionMessage(testExceptionMessage);
        assertEquals(testExceptionMessage, sdObj.getExceptionMessage());
    }

}
