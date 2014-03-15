package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.DatabaseServer;

public class DatabaseServerTest {

	DatabaseServer dbserverObj = new DatabaseServer("", 0, 0, 0);
	
	int testTotalCapacity    	= 65;
	int testTotalProvisioned 	= 30;
	int testAvailableCapacity 	= 35;
	String testServerName 		= "lvsvmdb32";
	
	@Test
	public void testSetAndGetServerName() {
		dbserverObj.setServerName(testServerName);
		assertEquals(testServerName, dbserverObj.getServerName());
	}

	@Test
	public void testSetAndGetTotalCapacity() {
		dbserverObj.setTotalCapacity(testTotalCapacity);
		assertEquals(testTotalCapacity, dbserverObj.getTotalCapacity());
	}

	@Test
	public void testSetAndGetTotalProvisioned() {
		dbserverObj.setTotalProvisioned(testTotalProvisioned);
		assertEquals(testTotalProvisioned, dbserverObj.getTotalProvisioned());
	}

	@Test
	public void testSetAndGetAvailableCapacity() {
		dbserverObj.setAvailableCapacity(testAvailableCapacity);
		assertEquals(testAvailableCapacity, dbserverObj.getAvailableCapacity());
	}

}
