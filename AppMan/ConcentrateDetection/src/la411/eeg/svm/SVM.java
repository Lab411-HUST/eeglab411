package la411.eeg.svm;

public class SVM {
	public static native int trainClassifier(String trainingFile,
			int kernelType, int cost, float gamma, int isProb, String modelFile);

	public static native int doClassification(int probability_estimate,
			String classifyFile, String modelFile, String outputFile);

	public static native int test(String trainingFile,int kernelType,int cost, float gamma, int isProb, String modelFile);

	static {
		System.loadLibrary("svm");
	}
}
