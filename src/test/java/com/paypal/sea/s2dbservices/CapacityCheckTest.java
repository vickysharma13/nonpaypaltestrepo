package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.CapacityCheck;
import com.paypal.sea.s2dbservices.oracledbaccess.DatabaseServer;

public class CapacityCheckTest {

	CapacityCheck capChkObj = new CapacityCheck();
	int testAvailableCapacity;
	int testMaxCapacity;
	List<DatabaseServer> testServerList;
	
	@Test
	public void testSetAndGetMaxCapacity() {
		testMaxCapacity = 3;
		capChkObj.setMaxCapacity(testMaxCapacity);
		assertEquals(testMaxCapacity, capChkObj.getMaxCapacity());
	}

	@Test
	public void testSetAndGetAvailableCapacity() {
		testAvailableCapacity = 2;
		capChkObj.setAvailableCapacity(testAvailableCapacity);
		assertEquals(testAvailableCapacity, capChkObj.getAvailableCapacity());
	}

	@Test
	public void testSetAndGetServerList() {
		testServerList = new ArrayList<DatabaseServer>();
		testServerList.add(new DatabaseServer("lvsvmdb32", 65, 35, 30));
		testServerList.add(new DatabaseServer("lvsvmdb50", 35, 18, 17));
		capChkObj.setServerList(testServerList);
		assertEquals(testServerList, capChkObj.getServerList());
	}
}
