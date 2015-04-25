package la411.eeg.svm;

public class Feature {
	public static native float Root_Mean_Square(int length, float arr[]);
	public static native float Simple_Square_Integral(int length, float arr[]);
	public static native float Waveform_Length(int length, float arr[]);
	public static native float Mean_Absolute_Value(int length, float arr[]);
	public static native float Modified_Mean_Absolute_Value_1(int length, float arr[]);
	public static native float Modified_Mean_Absolute_Value_2(int length, float arr[]);
	public static native float Slope_Sign_Change(int length, float arr[],float Threshold);
	public static native void writeFeatureInFile(String label,float RMS, float SSI, float WL, float MAV, float MMAV1, String outputFilePath);
	public static native void writeFeatureFile(String label, float feature,String outputFile);
	public static native void writeFileTrainingSet(String label,float feature1, String output);
	//Load library
	static
	{
		System.loadLibrary("feature"); 
		
	}
}
