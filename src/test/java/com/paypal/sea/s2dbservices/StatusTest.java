package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.Status;

public class StatusTest {

    Status statusObj = new Status();
    String testStatus = "SUCCESS";

    @Test
    public void testSetAndGetMessage() {
        statusObj.setMessage(testStatus);
        assertEquals(testStatus, statusObj.getMessage());
    }
}
