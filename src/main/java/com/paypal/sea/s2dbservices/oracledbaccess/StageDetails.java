package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

import com.paypal.sea.s2dbservices.StringConstants;

@XmlRootElement(name = "stageinfo")
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder = { "stageName", "podName", "DBServerName",
// "cloneVersion", "cloneDate", "cloneStatus", "cloneType", "DBStatus"})
public class StageDetails {

    private String mPodName;
    private String mStageName;
    private String mMasterStageName;
    private boolean mIsCloneable;
    private String mDbServerName;
    private String mCloneVersion;
    private String mLastCloneDate;
    private String mLastCloneStatus;
    private String mCloneType; // Basic for master, pay/paypilot
    private StringConstants.DBSTATUS mDbStatus;
    private StringConstants.DBSTATUS mDbStatusPay;
    private StringConstants.DBSTATUS mDbStatusPayPilot;
    private String mLatestVersion;
    private String mCloneCycle;
    private int mExceptionCode;
    private String mExceptionMessage;
    private StringConstants.ERROR_CODES mErrorCode;

    public StageDetails() {
        mCloneType = "";
        mCloneVersion = "";
        mLastCloneDate = "";
        mLastCloneStatus = "";
        mMasterStageName = "";
        mPodName = "";
        mDbServerName = "";
        mLatestVersion = "";
        mCloneCycle = "";
        mErrorCode = StringConstants.ERROR_CODES.STAGE_NOT_FOUND;
        mDbStatus = StringConstants.DBSTATUS.NA;
        mDbStatusPay = StringConstants.DBSTATUS.NA;
        mDbStatusPayPilot = StringConstants.DBSTATUS.NA;
    }

    public String getLatestVersion() {
        return mLatestVersion;
    }

    public void setLatestVersion(String status) {
        mLatestVersion = status;
    }

    public StringConstants.DBSTATUS getDbStatusPay() {
        return mDbStatusPay;
    }

    public void setDbStatusPay(StringConstants.DBSTATUS status) {
        mDbStatusPay = status;
    }

    public StringConstants.DBSTATUS getDbStatusPayPilot() {
        return mDbStatusPayPilot;
    }

    public void setDbStatusPayPilot(StringConstants.DBSTATUS status) {
        mDbStatusPayPilot = status;
    }

    public StringConstants.DBSTATUS getDBStatus() {
        return mDbStatus;
    }

    public void setDBStatus(StringConstants.DBSTATUS status) {
        mDbStatus = status;
    }

    public String getCloneStatus() {
        return mLastCloneStatus;
    }

    public void setCloneStatus(String status) {
        mLastCloneStatus = status;
    }

    public String getCloneDate() {
        return mLastCloneDate;
    }

    public void setCloneDate(String lastCloneDate) {
        mLastCloneDate = lastCloneDate;
    }

    public String getStageName() {
        return mStageName;
    }

    public void setStageName(String stageName) {
        mStageName = stageName;
    }

    public String getCloneType() {
        return mCloneType;
    }

    public void setCloneType(String cloneType) {
        mCloneType = cloneType;
    }

    public String getCloneVersion() {
        return mCloneVersion;
    }

    public void setCloneVersion(String cloneVersion) {
        if (cloneVersion != null) {
            mCloneVersion = cloneVersion;
        }
    }

    public String getPodName() {
        return mPodName;
    }

    public void setPodName(String podName) {
        mPodName = podName;
    }

    public String getDBServerName() {
        return mDbServerName;
    }

    public void setDBServerName(String dbServerName) {
        mDbServerName = dbServerName;
    }

    public boolean getIsCloneable() {
        return mIsCloneable;
    }

    public void setIsCloneable(boolean isCloneable) {
        mIsCloneable = isCloneable;
    }

    public String getMasterStageName() {
        return mMasterStageName;
    }

    public void setMasterStageName(String masterStageName) {
        mMasterStageName = masterStageName;
    }

    public String getCloneCycle() {
        return mCloneCycle;
    }

    public void setCloneCycle(String cloneCycle) {
        mCloneCycle = cloneCycle;
    }

    public void setErrorCode(StringConstants.ERROR_CODES errCode) {
        mErrorCode = errCode;
    }

    public StringConstants.ERROR_CODES getErrorCode() {
        return mErrorCode;
    }

    public void setExceptionMessage(String msg) {
        mExceptionMessage = msg;
    }

    public String getExceptionMessage() {
        return mExceptionMessage;
    }

    public void setExceptionCode(int code) {
        mExceptionCode = code;
    }

    public int getExceptionCode() {
        return mExceptionCode;
    }
}
