package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.ShareDBInput;

public class ShareDBInputTest {
	ShareDBInput shareobj = new ShareDBInput();
	String testfromStageName = "stage2p1025";
	String testtoStageName = "stage2p1019";

	// specify the input to the stage

	@Test
	public void testSetAndGetfromStageName() {
		shareobj.setFromStageName(testfromStageName);
		assertEquals(testfromStageName, shareobj.getFromStageName());
	}

	public void testSetAndGettoStageName() {
		shareobj.setToStageName(testtoStageName);
		assertEquals(testtoStageName, shareobj.getToStageName());
	}
}