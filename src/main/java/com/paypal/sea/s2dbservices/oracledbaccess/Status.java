package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "status")
public class Status {

    private String message;

    public Status() {
        message = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        message = msg;
    }

}
