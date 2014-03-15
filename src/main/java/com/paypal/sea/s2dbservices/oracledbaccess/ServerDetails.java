package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.ArrayList;
import java.util.List;

public class ServerDetails {
	
	String serverName;
	List<StageNames> stagesOnServer = new ArrayList<StageNames>();
	
	public ServerDetails(String serverName, List<StageNames> stagesOnServer) {
		this.serverName = serverName;
		this.stagesOnServer = stagesOnServer;
	}
	
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public void setStagesOnServer(List<StageNames> stages)
	{
		stagesOnServer=stages;
	}
	
	public List<StageNames> getStagesOnServer()
	{
		return stagesOnServer;
	}
	
    public static class StageNames{
    	
    	String stage;
    	
    	public String getStage() {
    		return stage;
    	} 

    	public void setStage(String stageName) {
    		stage = stageName;
    	}
    }

}
