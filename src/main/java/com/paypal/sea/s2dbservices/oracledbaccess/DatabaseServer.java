package com.paypal.sea.s2dbservices.oracledbaccess;

public class DatabaseServer {
	
	public int totalCapacity;
	public int totalProvisioned;
	public int availableCapacity;
	public String serverName;
	
	public DatabaseServer(String serverName, int total, int available, int provisioned) {
		this.serverName = serverName;
		this.totalCapacity = total;
		this.availableCapacity = available;
		this.totalProvisioned = provisioned;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void setTotalCapacity(int totalCapacity) {
		this.totalCapacity = totalCapacity;
	}
	
	public void setTotalProvisioned(int totalProvisioned) {
		this.totalProvisioned = totalProvisioned;
	}
	
	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public int getTotalCapacity() {
		return this.totalCapacity;
	}
	
	public int getTotalProvisioned() {
		return this.totalProvisioned;
	}

	public int getAvailableCapacity() {
		return this.availableCapacity;
	}
}
