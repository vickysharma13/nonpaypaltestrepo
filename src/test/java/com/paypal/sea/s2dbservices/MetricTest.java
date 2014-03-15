package com.paypal.sea.s2dbservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.Metric;

public class MetricTest {

    Metric metricObj = new Metric();

    String testType = "apiName";
    long testValue = 1000;

    @Test
    public void testGetAndSetErrorCount() {
        metricObj.setErrorCount(testValue);
        assertEquals(testValue, metricObj.getErrorCount());
    }

    @Test
    public void testGetAndSetMaxTime() {
        metricObj.setMaxTime(testValue);
        assertEquals(testValue, metricObj.getMaxTime());
    }

    @Test
    public void testGetAndSetMinTime() {
        metricObj.setMinTime(testValue);
        assertEquals(testValue, metricObj.getMinTime());
    }

    @Test
    public void testGetAndSetTimeElapsed() {
        metricObj.setAverageTimeElapsed(testValue);
        assertEquals(testValue, metricObj.getAverageTimeElapsed(), 1);
    }

    @Test
    public void testGetAndSetType() {
        metricObj.setType(testType);
        assertEquals(testType, metricObj.getType());
    }

    @Test
    public void testGetAndSetCount() {
        metricObj.setCount(testValue);
        assertEquals(testValue, metricObj.getCount());
    }
}