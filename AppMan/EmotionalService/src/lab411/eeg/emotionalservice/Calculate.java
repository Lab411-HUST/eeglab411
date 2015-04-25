package lab411.eeg.emotionalservice;

import java.util.List;

public class Calculate {
	public static native double getmean(int[] arrXn, int xnSize, int hnSize,
			double W1, double W2, double b);

	public static native double gethfd(double[] arrXn, int xnSize);

	//(signalFc6, signalFc6.length, 1024,2 * Math.PI * 2 / 128,2 * Math.PI * 30 / 128, 1.5)
	public static native double[] getYnFilter(int[] arrXn, int xnSize,
			int hnSize, double W1, double W2, double b);
	public static native double calcPower(double[] arrXn, int xnSize);
	static {
		System.loadLibrary("filter");
		System.loadLibrary("hfd");
		System.loadLibrary("power");
	}
	public static double mean(List<Double> in) {
		double s = 0;
		for (int i = 0; i < in.size(); i++) {
			s += in.get(i);
		}
		double mean = s / in.size();
		return mean;
	}
}
