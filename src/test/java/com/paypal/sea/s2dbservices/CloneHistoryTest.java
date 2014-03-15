package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.CloneHistory;

public class CloneHistoryTest {
	
    CloneHistory ch = new CloneHistory();

    String testID = "1234";
    String testEndTime = "14-JUN-13";
    String testCurrentStatus = "SUCCESS";
    String testExtdStatus = "Clone Completed.";
    String testFailureCause = "completed clone.";
    String testVersion = "105RQALVS11G03";

	@Test
    public void testGetAndSetEndTime() {
        ch.setEndTime(testEndTime);
        assertEquals(testEndTime, ch.getEndTime());
    }
	
    @Test
    public void testGetAndSetCurrentStatus() {
        ch.setCurrentStatus(testCurrentStatus);
        assertEquals(testCurrentStatus, ch.getCurrentStatus());
    }

    @Test
    public void testGetAndSetExtdStatus() {
        ch.setExtendedStatus(testExtdStatus);
        assertEquals(testExtdStatus, ch.getExtendedStatus());
    }

    @Test
    public void testGetAndSetFailureCause() {
        ch.setFailureCause(testFailureCause);
        assertEquals(testFailureCause, ch.getFailureCause());
    }
    
    @Test
    public void testGetAndSetVersion() {
        ch.setSnapshotVersion(testVersion);
        assertEquals(testVersion, ch.getSnapshotVersion());
    }
    
    @Test
    public void testGetAndSetID() {
        ch.setRequestID(testID);
        assertEquals(testID, ch.getRequestID());
    }

}