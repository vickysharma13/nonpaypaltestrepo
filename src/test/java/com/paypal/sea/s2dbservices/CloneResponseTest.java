package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.CloneResponse;

public class CloneResponseTest {
	
	CloneResponse response = new CloneResponse();

	private String testMessage = "message";
	private String testID = "1002306";

	@Test
	public void testGetAndSetMessage() {
		response.setMessage(testMessage);
		assertEquals(testMessage, response.getMessage());
	}

	@Test
	public void testGetAndSetRequestID() {
		response.setRequestID(testID);
		assertEquals(testID, response.getRequestID());
	}

}
