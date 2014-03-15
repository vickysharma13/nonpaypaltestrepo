package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.ArrayList;
import java.util.List;

public class Hierarchy {
	
	String masterStage;
	List<StageNames> childStages = new ArrayList<StageNames>();
	
	public Hierarchy(String masterStage, List<StageNames> childStages) {
		this.masterStage = masterStage;
		this.childStages = childStages;
	}
		
	public String getmasterStage()
	{
		return masterStage;
	}
	
	
	public List<StageNames> getchildStages()
	{
		return childStages;
	}
	
    public static class StageNames{
    	
    	String stage;
        
    	public String getStage()
    	{
    		return stage;
    	}
    	
    	public void setStage(String stageName) {
    		stage = stageName;
    	}
    }

}


