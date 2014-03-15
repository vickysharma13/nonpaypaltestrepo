package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import com.paypal.sea.s2dbservices.oracledbaccess.DbServer;

public class DbServerTest {
String serverNameTemp = "lvspgdb01";
DbServer serverTemp = new DbServer(serverNameTemp);

 @Test
 public void addTest()
 {
    assertEquals(serverNameTemp,serverTemp.getServerName());	
 }
 
 @Test
  public void addTest1()
 {
	 serverTemp.setServerName(null);
	 assertNull(serverTemp.getServerName());
 }
}
