package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.SnapshotDetail;

public class SnapshotDetailTest {

    SnapshotDetail snapDetailObj = new SnapshotDetail();

    String testCloneVersion = "99RQALVS11G";
    String testSnapshotVersion = "99RQALVS11G06";
    String testPod = "AAA";
    String testDate = "01-jan-13";

    @Test
    public void testGetAndSetSnapshotDate() {
        snapDetailObj.setSnapshotDate(testDate);
        assertEquals(testDate, snapDetailObj.getSnapshotDate());
    }

    @Test
    public void testGetAndSetVersion() {
        snapDetailObj.setVersion(testCloneVersion);
        assertEquals(testCloneVersion, snapDetailObj.getVersion());
    }

    @Test
    public void testGetAndSetMajorVersion() {
        snapDetailObj.setMajorVersion(testSnapshotVersion);
        assertEquals(testSnapshotVersion, snapDetailObj.getMajorVersion());
    }

}
