package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.PegaCloneInput;

public class PegaCloneInputTest {

    PegaCloneInput pegaObj = new PegaCloneInput();

    String testDbServer = "lvsvmdb01";
    String testVersion = "10G";

    @Test
    public void testGetAndSetSource() {
        pegaObj.setSource(testDbServer);
        assertEquals(testDbServer, pegaObj.getSource());
    }

    @Test
    public void testGetAndSetTarget() {
        pegaObj.setTarget(testDbServer);
        assertEquals(testDbServer, pegaObj.getTarget());
    }

    @Test
    public void testGetAndSetVersion() {
        pegaObj.setVersion(testVersion);
        assertEquals(testVersion, pegaObj.getVersion());
    }

}
