package com.paypal.sea.s2dbservices.oracledbaccess;

public class DbServer {
	
	private String serverName;
	
	public DbServer(String serverName) {
		this.setServerName(serverName);
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
