package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.ShutDbInput;

public class ShutDbInputTest {

    ShutDbInput shutObj = new ShutDbInput();

    String testStage = "stage2p1234";

    @Test
    public void testGetAnsSetStageName() {
        shutObj.setStageName(testStage);
        assertEquals(testStage, shutObj.getStageName());
    }
}
