package lab411.eeg.emotionalservice;

public class Calculate {
	public static native double getmean(int[] arrXn, int xnSize, int hnSize,
			double W1, double W2, double b);

	public static native double gethfd(double[] arrXn, int xnSize);

	//(signalFc6, signalFc6.length, 1024,2 * Math.PI * 2 / 128,2 * Math.PI * 30 / 128, 1.5)
	public static native double[] getYnFilter(int[] arrXn, int xnSize,
			int hnSize, double W1, double W2, double b);

	static {
		System.loadLibrary("filter");
		System.loadLibrary("hfd");
	}
}
