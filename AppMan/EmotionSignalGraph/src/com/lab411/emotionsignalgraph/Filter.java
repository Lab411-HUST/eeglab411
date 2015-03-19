package com.lab411.emotionsignalgraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Filter {

	public static native double getmean(int[] arrXn, int xnSize, int hnSize,
			double W1, double W2, double b);

	public static native double gethfd(double[] arrXn, int xnSize);

	public static native double[] getYnFilter(int[] arrXn, int xnSize,
			int hnSize, double W1, double W2, double b);

	static {
		System.loadLibrary("filter");
		System.loadLibrary("hfd");
	}

	public static void writedata(ArrayList<Double> in, double mean) {
		try {
			FileWriter f = new FileWriter("/sdcard/AAA/fc6.txt");
			for (int i = 0; i < in.size(); i++) {
				f.write(in.get(i) + "\t");
			}
			f.write("\n" +"Mean: "+ mean);
			f.write("\n");
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static double mean(ArrayList<Double> in){
		double s = 0;
		for(int i = 0; i<in.size();i++){
			s+= in.get(i);
		}
		double mean = s/in.size();
		return mean;
	}
}
