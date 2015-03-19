/*
 * <Copyright project =Samsung_Reran; file=samsung.lab411.svm> 
 * 
 * <company> HUST-Laboratory 411-Samsung EEG project</company>
 * 
 * <author> PhongTran </author>
 * 
 * <email> phongtran0715@gmail.com@gmail.com </email>
 * 
 * <date> Jun 2, 2014 - 6:17:20 PM </date>
 * 
 * <purpose> </purpose>
 */
package samsung.lab411.svm;

import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class LibSVM.
 */
public class LibSVM {
	//Training function
			/**
	 * Train classifier native.
	 *
	 * @param trainingFile the training file
	 * @param kernelType the kernel type
	 * @param cost the cost
	 * @param gamma the gamma
	 * @param isProb the is prob
	 * @param modelFile the model file
	 * @return the int
	 */
	public static native int trainClassifierNative(String trainingFile,int kernelType,
					int cost, float gamma, int isProb, String modelFile);		
			

			/**
	 * Do classification native.
	 *
	 * @param probability_estimate the probability_estimate
	 * @param classifyFile Path of file input test
	 * @param modelFile path of model file
	 * @param outputFile the output file
	 * @return the int
	 */
	public static native int doClassificationNative(int probability_estimate,
			String classifyFile, String modelFile, String outputFile);
	
	// svm native			

	// Load the native library
	static {
		try {
			System.loadLibrary("signal2");
		} catch (UnsatisfiedLinkError ule) {
			//nothing to do
			Log.d("xxx","can not load lib!");
		}
	}
}
