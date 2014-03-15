package com.paypal.sea.s2dbservices.oracledbaccess;

public class Metric {
    private String mType; // minor version in the db
    private long mCount;
    private long mErrorCount;
    private double mTimeElapsed;
    private long mMaxTime;
    private long mMinTime;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public long getCount() {
        return mCount;
    }

    public void setCount(long count) {
        mCount = count;
    }

    public long getErrorCount() {
        return mErrorCount;
    }

    public void setErrorCount(long count) {
        mErrorCount = count;
    }

    public double getAverageTimeElapsed() {
        return mTimeElapsed;
    }

    public void setAverageTimeElapsed(double time) {
        mTimeElapsed = Math.round(time);
    }

    public long getMaxTime() {
        return mMaxTime;
    }

    public void setMaxTime(long time) {
        mMaxTime = time;
    }

    public long getMinTime() {
        return mMinTime;
    }

    public void setMinTime(long time) {
        mMinTime = time;
    }

}