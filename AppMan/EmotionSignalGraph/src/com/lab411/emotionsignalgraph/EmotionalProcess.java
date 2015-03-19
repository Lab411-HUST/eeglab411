package com.lab411.emotionsignalgraph;

import java.util.List;

public class EmotionalProcess {

	static {
		System.loadLibrary("emotion");
		// System.loadLibrary("filter");
	}

	public static double[] power(double[] in){
		double[] out = new double[in.length];
		for(int i = 0; i<out.length;i++){
			out[i] = in[i]*in[i];
		}
		return out;
	}

	public static double mean(double[] in) {
		double out = 0;
		double sum = 0;
		for (int i = 0; i < in.length; i++) {
			sum += in[i]*in[i];
		}
		out = sum / in.length;
		return out;
	}

}