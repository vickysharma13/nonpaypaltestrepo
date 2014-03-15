package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.StartDbInput;

public class StartDbInputTest {

    StartDbInput startObj = new StartDbInput();

    String testStage = "stage2p1234";

    @Test
    public void testSetAndGetStageName() {
        startObj.setStageName(testStage);
        assertEquals(testStage, startObj.getStageName());
    }

}
