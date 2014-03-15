package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MetricsSet {
	
    private String date;
    List<Metric> metricsList = new ArrayList<Metric>();
	
	public MetricsSet(String date, List<Metric> metricsList) {
		this.date = date;
		this.metricsList = metricsList;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public List<Metric> getMetricsList() {
		return this.metricsList;
	}

}
