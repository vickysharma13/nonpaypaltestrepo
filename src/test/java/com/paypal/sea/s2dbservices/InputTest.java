package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.Input;

public class InputTest {

    Input inputObj = new Input();

    String testStage = "stage2p1234";
    String testCloneVersion = "99RQALVS11G";
    String testCloneCycle = "RQA";
    String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";

    @Test
    public void testGetAndSetCloneCycle() {
        inputObj.setCloneCycle(testCloneCycle);
        assertEquals(testCloneCycle, inputObj.getCloneCycle());
    }

    @Test
    public void testGetAndSetCloneOption() {
        inputObj.setCloneOption(testCloneOption);
        assertEquals(testCloneOption, inputObj.getCloneOption());
    }

    @Test
    public void testGetAndSetCloneVersion() {
        inputObj.setCloneVersion(testCloneVersion);
        assertEquals(testCloneVersion, inputObj.getCloneVersion());
    }

    @Test
    public void testGetAndSetStageName() {
        inputObj.setStageName(testStage);
        assertEquals(testStage, inputObj.getStageName());
    }

}
