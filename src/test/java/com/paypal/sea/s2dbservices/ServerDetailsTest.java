package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.paypal.sea.s2dbservices.oracledbaccess.ServerDetails;

public class ServerDetailsTest {
	ServerDetails sdetails;
	List<ServerDetails.StageNames> testStageNames = new ArrayList<ServerDetails.StageNames>();
	String testServer = "lvsvmdb13";
	String[] testStages = { "stage2p1701", "stage2p1702", "stage2p1703",
			"stage2p1704", "stage2p1705", "stage2p1706" };

	public void addTestStages() {
		for (String testStage : testStages) {
			ServerDetails.StageNames stage = new ServerDetails.StageNames();
			stage.setStage(testStage);
			testStageNames.add(stage);
		}
	}

	@Test
	public void testSetAndGetServerDetails() {
		sdetails = new ServerDetails(testServer, testStageNames);
		sdetails.setServerName(testServer);
		sdetails.setStagesOnServer(testStageNames);
		assertEquals(testServer, sdetails.getServerName());
		assertEquals(testStageNames, sdetails.getStagesOnServer());
	}

}
