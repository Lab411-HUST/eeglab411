package com.example.openvibeforandroid;

public class XdawnTrainer {
	static
	{
		System.loadLibrary("Filter");
	}
	
    public static native int xDawn(double[][] input, double chunk);
	
	public static native int getValues(double[][] input);
	
	public static native double[][] calculateXDawn (double[][] input1, double[][] input2);
	
	public static native int xDawnProcess (double[][] signal,double[][] erp, int[] index, double chunkCount, int erpSize);

}
