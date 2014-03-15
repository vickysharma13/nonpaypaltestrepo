package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ShutDBRequest")
public class ShutDbInput {

    private String stageName;

    public ShutDbInput() {
        stageName = "";
    }
    
    public String getStageName() {
        return stageName;
    }

    public void setStageName(String name) {
        stageName = name;
    }

}
