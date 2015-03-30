package com.example.processSignal;

public class StimulationEpoch {
	public double mStartTime;
	public double mEndTime;
	public double[][] mSignal=new double[3][19];
	public StimulationEpoch(double starttime,double endtime,double [][] signal){
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
