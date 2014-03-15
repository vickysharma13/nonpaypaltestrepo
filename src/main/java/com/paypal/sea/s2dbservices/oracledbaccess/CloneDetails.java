package com.paypal.sea.s2dbservices.oracledbaccess;

public class CloneDetails {

	private String stageName;
	private String cloneVersion;
	private String cloneStartTime;
	private String cloneEndTime;
	private String queueStartTime;
	private String queueEndTime;
	private String currentStatus;
	private String extendedStatus;
	private String cloneType;
	private String cloneCycle;
	private String log;
	private String masterVersion;

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getCloneVersion() {
		return cloneVersion;
	}

	public void setCloneVersion(String cloneVersion) {
		this.cloneVersion = cloneVersion;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getExtendedStatus() {
		return extendedStatus;
	}

	public void setExtendedStatus(String extendedStatus) {
		this.extendedStatus = extendedStatus;
	}

	public String getCloneType() {
		return cloneType;
	}

	public void setCloneType(String cloneType) {
		this.cloneType = cloneType;
	}

	public String getCloneCycle() {
		return cloneCycle;
	}

	public void setCloneCycle(String cloneCycle) {
		this.cloneCycle = cloneCycle;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getMasterVersion() {
		return masterVersion;
	}

	public void setMasterVersion(String masterVersion) {
		this.masterVersion = masterVersion;
	}

	public String getCloneStartTime() {
		return cloneStartTime;
	}

	public void setCloneStartTime(String cloneStartTime) {
		this.cloneStartTime = cloneStartTime;
	}

	public String getCloneEndTime() {
		return cloneEndTime;
	}

	public void setCloneEndTime(String cloneEndTime) {
		this.cloneEndTime = cloneEndTime;
	}

	public String getQueueStartTime() {
		return queueStartTime;
	}

	public void setQueueStartTime(String queueStartTime) {
		this.queueStartTime = queueStartTime;
	}

	public String getQueueEndTime() {
		return queueEndTime;
	}

	public void setQueueEndTime(String queueEndTime) {
		this.queueEndTime = queueEndTime;
	}
}
