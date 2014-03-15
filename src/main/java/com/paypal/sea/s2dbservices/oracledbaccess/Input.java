/**
 *
 */
package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Request")
public class Input {

    private String stageName;
    private String cloneVersion;
    private String cloneCycle;
    private String cloneOption;

    public Input() {
        stageName = "";
        cloneVersion = "";
        cloneCycle = "";
        cloneOption = "";
    }

    public String getStageName() {
        return stageName;
    }

    public String getCloneVersion() {
        return cloneVersion;
    }

    public String getCloneCycle() {
        return cloneCycle;
    }

    public String getCloneOption() {
        return cloneOption;
    }

    public void setStageName(String name) {
        stageName = name;
    }

    public void setCloneVersion(String ver) {
        cloneVersion = ver;
    }

    public void setCloneCycle(String cycle) {
        cloneCycle = cycle;
    }

    public void setCloneOption(String option) {
        cloneOption = option;
    }

}
