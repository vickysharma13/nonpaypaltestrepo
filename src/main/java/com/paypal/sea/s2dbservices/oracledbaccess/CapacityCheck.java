package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.ArrayList;
import java.util.List;

public class CapacityCheck {
	
	int availableCapacity;
	int maxCapacity;
	List<DatabaseServer> serverList = new ArrayList<DatabaseServer>();

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getAvailableCapacity() {
		return availableCapacity;
	} 

	public void setAvailableCapacity(int available) {
		this.availableCapacity = available;
	}
	
	public void setServerList(List<DatabaseServer> serverList) {
		this.serverList = serverList;
	}
	
	public List<DatabaseServer> getServerList() {
		return this.serverList;
	}
}