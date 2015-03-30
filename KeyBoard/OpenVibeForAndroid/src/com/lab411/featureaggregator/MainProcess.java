package com.lab411.featureaggregator;

import java.util.List;



public class MainProcess {
	static
	{
		System.loadLibrary("FeatureAggregator");
	}
	public static native FeatureVector[] FeatureAggregator (List<double[][]> targetdata, List<double[][]> nontargetdata);
}
