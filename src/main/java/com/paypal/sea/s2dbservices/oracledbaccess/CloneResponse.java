package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "status")
public class CloneResponse {

    private String message;
    private String requestID;

    public CloneResponse() {
        message = null;
        setRequestID(null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        message = msg;
    }

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

}
