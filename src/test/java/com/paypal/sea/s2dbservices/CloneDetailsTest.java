package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.CloneDetails;

public class CloneDetailsTest {

	CloneDetails cd = new CloneDetails();

	private String testStageName = "stage2p1234";
	private String testCloneVersion = "100RQAG06";
	private String testTime = "00:00:00";
	private String testStatus = "Success";
	private String testCloneType = "Basic";
	private String testCloneCycle = "RQA";
	private String testLog = "Clone Successful";
	private String testMasterVersion = "100RQA";

	@Test
	public void testGetAndSetStageName() {
		cd.setStageName(testStageName);
		assertEquals(testStageName, cd.getStageName());
	}

	@Test
	public void testGetAndSetCloneVersion() {
		cd.setCloneVersion(testCloneVersion);
		assertEquals(testCloneVersion, cd.getCloneVersion());
		cd.setMasterVersion(testMasterVersion);
		assertEquals(testMasterVersion, cd.getMasterVersion());
	}

	@Test
	public void testGetAndSetCloneTime() {
		cd.setCloneEndTime(testTime);
		cd.setCloneStartTime(testTime);
		cd.setQueueEndTime(testTime);
		cd.setQueueStartTime(testTime);
		assertEquals(testTime, cd.getQueueEndTime());
		assertEquals(testTime, cd.getQueueStartTime());
		assertEquals(testTime, cd.getCloneEndTime());
		assertEquals(testTime, cd.getCloneStartTime());
	}

	@Test
	public void testGetAndSetStatus() {
		cd.setCurrentStatus(testStatus);
		cd.setExtendedStatus(testStatus);
		assertEquals(testStatus, cd.getCurrentStatus());
		assertEquals(testStatus, cd.getExtendedStatus());
	}

	@Test
	public void testGetAndSetCloneType() {
		cd.setCloneType(testCloneType);
		assertEquals(testCloneType, cd.getCloneType());
	}

	@Test
	public void testGetAndSetCloneCycle() {
		cd.setCloneCycle(testCloneCycle);
		assertEquals(testCloneCycle, cd.getCloneCycle());
	}

	@Test
	public void testGetAndSetLog() {
		cd.setLog(testLog);
		assertEquals(testLog, cd.getLog());
	}

}
