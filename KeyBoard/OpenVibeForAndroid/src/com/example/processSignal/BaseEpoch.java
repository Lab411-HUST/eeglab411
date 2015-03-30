package com.example.processSignal;

public class BaseEpoch {
	double mStartTime=0;
	double mEndTime=0;
	double[][] mSignal=new double[14][32];
	
	public BaseEpoch(double starttime,double endtime,double[][] signal){
		mStartTime=starttime;
		mEndTime=endtime;
		mSignal=signal;
	}
	public double getStartTime(){
		return mStartTime;
	}
	public double getEndTime(){
		return mEndTime;
	}
	public double[][] getSignal(){
		return mSignal;
	}

}
