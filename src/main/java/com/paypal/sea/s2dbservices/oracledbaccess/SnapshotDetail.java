package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "dbVersion")
@XmlAccessorType(XmlAccessType.NONE)
public class SnapshotDetail {
    @XmlElement(name = "version")
    // private String mVersion; // minor version in the db
    private String mMajorVersion;
    // @XmlTransient
    // private String mMajorVersion;
    private String mVersion;
    @XmlElement(name = "snapshotDate")
    private String mCreatedOn;

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getMajorVersion() {
        return mMajorVersion;
    }

    public void setMajorVersion(String version) {
        mMajorVersion = version;
    }

    public String getSnapshotDate() {
        return mCreatedOn;
    }

    public void setSnapshotDate(String createdDate) {
        mCreatedOn = createdDate;
    }

}
