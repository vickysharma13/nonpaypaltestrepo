package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PEGACloneRequest")
public class PegaCloneInput {

    private String source;
    private String target;
    private String version;

    public PegaCloneInput() {
        source = "";
        target = "";
        version = "";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String name) {
        source = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String name) {
        target = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String name) {
        version = name;
    }
}