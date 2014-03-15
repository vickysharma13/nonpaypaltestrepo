package com.paypal.sea.s2dbservices.oracledbaccess;

public class CloneHistory {

	private String requestID; // REQUEST_ID
	private String endTime; // CLONE_END_TIME
	private String currentStatus; // CURRENT_STATUS
	private String extendedStatus; // EXTENDED_STATUS
	private String failureCause; // CAUSE_OF_FAILURE
	private String snapshotVersion; // SNAPSHOT_VERSION
	
	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	
	public String getFailureCause() {
		return failureCause;
	}
	
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}
	
	public String getSnapshotVersion() {
		return snapshotVersion;
	}
	
	public void setSnapshotVersion(String snapshotVersion) {
		this.snapshotVersion = snapshotVersion;
	}
}