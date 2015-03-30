package com.lab411.classifiertrainer;

import java.util.List;

import com.lab411.featureaggregator.FeatureVector;

public class MainProcess {
	static
	{
		System.loadLibrary("FeatureAggregator");
	}
	public static native boolean CalculateTraining (int startIndex, int stopIndex, double[] featureData, int[] featureIndex,double[] featureLabel, int featureLength, String path);
	public static native double TestTrain(double[] featureVectorData,int[] featureIndex, double[] featureLabel, boolean receivedTrain,boolean isRandomize, int partitionCount, String path);
}
