package com.paypal.sea.s2dbservices;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.Metric;
import com.paypal.sea.s2dbservices.oracledbaccess.MetricsSet;

public class MetricsSetTest {

	String date = "15-Sep-2013";
    List<Metric> metricsList = getMetricsList();
	MetricsSet msetObj = new MetricsSet("", metricsList);
	
	@Test
	public void testSetAndGetDate() {
		msetObj.setDate(date);
		assertEquals(date, msetObj.getDate());
	}

	@Test
	public void testGetMetricsList() {
		assertEquals(metricsList, msetObj.getMetricsList());
	}

	private List<Metric> getMetricsList() {
		 List<Metric> metricsList = new ArrayList<Metric>();
		 Metric metric = new Metric();
		 metric.setAverageTimeElapsed(123.2);
		 metric.setCount(5);
		 metric.setErrorCount(6);
		 metric.setMaxTime(123123);
		 metric.setMinTime(000213);
		 metric.setType("details");
		 metricsList.add(metric);
		 metric = new Metric();
		 
		 metric.setAverageTimeElapsed(113.2);
		 metric.setCount(15);
		 metric.setErrorCount(16);
		 metric.setMaxTime(12312356);
		 metric.setMinTime(022213);
		 metric.setType("dbversion");
		 metricsList.add(metric);
		 
		 return metricsList;
	}
}
