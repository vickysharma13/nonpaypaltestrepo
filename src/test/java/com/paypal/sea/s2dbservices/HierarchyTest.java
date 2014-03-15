package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy;
import com.paypal.sea.s2dbservices.oracledbaccess.Hierarchy.StageNames;

public class HierarchyTest {
	String master="stage2gg043";
	String child1="stage2fn07";
	Hierarchy ch;
	List<StageNames> childStages = new ArrayList<StageNames>();
	Hierarchy.StageNames child = new Hierarchy.StageNames();
 
	@Test
	public void testGetmasterStage() {
		child.setStage("stage2fn07");
		childStages.add(child);
    	ch = new Hierarchy(master,childStages) ; 
		assertEquals(master,ch.getmasterStage());
	}

	@Test
	public void testGetchildStages() {
		child.setStage("stage2fn07");
		childStages.add(child);
    	ch = new Hierarchy(master,childStages) ; 
		assertEquals(childStages,ch.getchildStages());
	}

	@Test
	public void testgetStage()
	{
		child.setStage("stage2fn07");
		assertEquals(child1,child.getStage());
	}
}
