package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "StartDBRequest")
public class StartDbInput {

    private String stageName;

    public StartDbInput() {
        stageName = "";
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String name) {
        stageName = name;
    }

}
