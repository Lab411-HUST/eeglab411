package com.lab411.votingclassifier;

import java.util.ArrayList;

public class MainProcess {
	static
	{
		System.loadLibrary("FeatureAggregator");
	}
	public static native String Process(ArrayList<double[]> inputMatrix, double[] coefficients);
}
