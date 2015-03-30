package com.example.processSignal;

public class Stimulation {
	double mTime=0; // chi so thoi gian bat dau
	int mIndex=0;// chi so hang hoac cot
	/*
	 * lable=0 : la nontarget
	 * lable=1 : la Target
	 */
	int mLable=0; 
	public Stimulation(double time,int index,int lable){
		mTime=time;
		mIndex=index;
		mLable=lable;
	}
	public double getTime() {
		return mTime;
	}
	public int getIndex() {
		return mIndex;
	}
	public int getLable() {
		return mLable;
	}
}
