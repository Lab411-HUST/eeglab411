package lab411.eeg.p300processing;

import java.util.ArrayList;
import java.util.List;


public class MainProcess {
	
	static
	{
		System.loadLibrary("P300Processing");
	}
	
	/*
	 * Classifier Trainer's functions
	 */
	
	/**
	 * CalculateTraining:
	 * brief: calculate accuracy of each partition from FeatureVectors 
	 * @param startIndex:begin index mapping to FeatureVector's position 
	 * @param stopIndex: last index mapping to FeatureVector's position
	 * @param featureData: collection of all FeatureVectors to train (each FeatureVector has 57 values)
	 * @param featureIndex: collection of all FeatureVectors's position mapping to FeatureVectors space to train
	 * @param featureLabel: collection of all FeatureVectors's label mapping to FeatureVector's index (each FeatureVector has 1 label: 1 (Target)or 2 (NonTarget))
	 * @param featureLength: number of FeatureVectors space
	 * @param path: path to write coefficients trainer to file (external storage)
	 * @return: true if success, false if error occured.
	 */
	public static native boolean CalculateTraining (int startIndex, int stopIndex, double[] featureData, int[] featureIndex,double[] featureLabel, int featureLength, String path);
	
	/**
	 * TestTrain:
	 * brief: Do Classifier Trainer for all FeatureVectors
	 * @param featureVectorData
	 * @param featureIndex
	 * @param featureLabel
	 * @param receivedTrain
	 * @param isRandomize
	 * @param partitionCount
	 * @param path
	 * @return: final accuracy of Classifier Trainer
	 */
	public static native double TestTrain(double[] featureVectorData,int[] featureIndex, double[] featureLabel, boolean receivedTrain,boolean isRandomize, int partitionCount, String path);
	
	/*
	 * Feature Aggregator Function
	 */
	/**
	 * FeatureAggregator: aggregate all target and nontarget data
	 * @param targetdata: signal captured and decimated when flashing at row/column has target character
	 * @param nontargetdata: signal captured and decimated when flashing at row/column hasn't target character
	 * @return FeatureVectors space
	 */
	public static native FeatureVector[] FeatureAggregator (List<double[][]> targetdata, List<double[][]> nontargetdata);
	
	/*
	 * Voting Classifier Function
	 */
	/**
	 * VotingClassifier: process LDA classify and choose row & column has P300
	 * @param inputMatrix: collections of all FeatureVectors (each featurevector has 57 values)
	 * @param coefficients: coefficients after Classifier Trainer 
	 * @return Character mapping with row & column has P300
	 */
	public static native String VotingProcess(ArrayList<double[]> inputMatrix, double[] coefficients);
	
	/*
	 * XDawn Spatial Trainer Function
	 */
	/**
	 * XDawn Spatial Filter: training decimated data, calculate eigen vectors, eigen values and coefficients filters
	 * @param signal: decimated signal, from begin to end of flashing row/column
	 * @param erp: target signal: when flashing in row/column has target character (duration 600 ms)
	 * @param index: index when flashing in row/column has target character
	 * @param chunkCount: all epoch 250 ms from begin to end of flashing
	 * @param erpSize: total flashing row/column has target character 
	 * @param path: path to write coefficient filter
	 * @return 0 if success.
	 */
	public static native int xDawnTrainer(double[][] signal,double[][] erp, int[] index, double chunkCount, int erpSize, String path);

}
