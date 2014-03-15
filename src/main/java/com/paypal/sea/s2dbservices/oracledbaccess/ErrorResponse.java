package com.paypal.sea.s2dbservices.oracledbaccess;

public class ErrorResponse {
	
	private String message;
	private int code;
	
	public ErrorResponse(String msg, int code) {
		this.message = msg;
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
