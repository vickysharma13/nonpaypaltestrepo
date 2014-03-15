package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.ErrorResponse;

public class ErrorResponseTest {

	private String testMessage = "message";
	private int testCode = 1;
	ErrorResponse response = new ErrorResponse(testMessage, testCode);

	@Test
	public void testGetAndSetMessage() {
		response.setMessage(testMessage);
		assertEquals(testMessage, response.getMessage());
	}

	@Test
	public void testGetAndSetCode() {
		response.setCode(testCode);
		assertEquals(testCode, response.getCode());
	}

}
