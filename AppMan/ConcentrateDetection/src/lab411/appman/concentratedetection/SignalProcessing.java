package lab411.appman.concentratedetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import lab411.eeg.emotiv.Emokit_Frame;
import android.os.Environment;
import android.util.Log;

public class SignalProcessing {

	public static final int TRAINING = 1;
	public static final int RUNNING = 2;
	public static final int BOTH = 3;
	public static final String FILE_WRITE = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "Concentrate/";

	public static native double calcPower(double[] arrXn, int xnSize);

	public static native double[] getYnFilter(int[] arrXn, int xnSize,
			int hnSize, double W1, double W2, double b);

	static {
		System.loadLibrary("filter");
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

	public static int count(List<Double> in, double threshold) {
		int c = 0;
		for (int i = 0; i < in.size(); i++) {
			if (in.get(i) >= threshold) {
				c++;
			}
		}
		if (c == 0)
			return 1;
		else
			return c;
	}

	public static void WriteFile(Emokit_Frame k, int type, String time)
			throws IOException {
		String des, path;
		switch (type) {
		case TRAINING:
			des = "RAW_TRAINING";
			break;
		case RUNNING:
			des = "RAW_RUNNING";
			break;
		case BOTH:
			des = "RAW_BOTH";
			break;
		default:
			des = "RAW";
		}
		String filename = des + "_" + time + ".txt";
		path = FILE_WRITE + filename;
		File file = new File(path);
		if (!file.exists())
			file.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

		String s = k.F3 + " " + k.FC6 + " " + k.P7 + " " + k.T8 + " " + k.F7
				+ " " + k.F8 + " " + k.T7 + " " + k.P8 + " " + k.AF4 + " "
				+ k.F4 + " " + k.AF3 + " " + k.O2 + " " + k.O1 + " " + k.FC5
				+ "\n";
		bw.write(s);

		bw.close();

	}

	public static void writeData(List<Double> in, String channel, int type,
			String time) throws IOException {

		String des, name;
		switch (type) {
		case TRAINING:
			des = "INDEX_TRAINING";
			break;
		case RUNNING:
			des = "INDEX_RUNNING";
			break;
		case BOTH:
			des = "INDEX_BOTH";
			break;
		default:
			des = "INDEX";
		}
		name = channel + "_" + time + "_" + des + ".txt";
		String sdcard = "/sdcard/Concentrate/" + name;
		File file = new File(sdcard);
		if (!file.exists())
			file.createNewFile();
		String s = "";
		for (int i = 0; i < in.size(); i++) {

			s += in.get(i) + "\n";
		}
		try {
			/*
			 * OutputStreamWriter writer = new OutputStreamWriter( new
			 * FileOutputStream(sdcard,true));
			 */
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sdcard), true));
			writer.write(s);
			writer.write("\n");
			writer.close();
			Log.d("TAG", "Write file finished");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("TAG", "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("TAG", "IOException");
		}
	}

	public static void writeDataSVM(List<Double> in, int type, int label)
			throws IOException {

		String des, name;
		switch (type) {
		case TRAINING:
			des = "TRAINING";
			break;
		case RUNNING:
			des = "RUNNING";
			break;
		default:
			des = "NULL";
			break;
		}
		name = des + ".txt";
		String sdcard = "/sdcard/Concentrate/Result/" + name;
		File file = new File(sdcard);

		file.createNewFile();
		String s = "";
		String l = "";
		if (label == 0)
			l = "00";
		else
			l = "11";
		for (int i = 0; i < in.size(); i++) {

			s += l + " 1:" + in.get(i) + "\n"; // 00: relax.
		}
		try {
			/*
			 * OutputStreamWriter writer = new OutputStreamWriter( new
			 * FileOutputStream(sdcard,true));
			 */
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sdcard), true));
			writer.write(s);
			writer.write("\n");
			writer.close();
			Log.d("TAG", "Write file finished");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("TAG", "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("TAG", "IOException");
		}
	}

	public static String getCurrentTime() {
		// Get current time to set name for record file
		Calendar c = Calendar.getInstance();
		int seconds = c.get(Calendar.SECOND);
		int minutes = c.get(Calendar.MINUTE);
		int hours = c.get(Calendar.HOUR);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		String time = year + "" + month + "" + date + "_" + hours + ""
				+ minutes + "" + seconds;
		return time;
	}

	public static double fix(double in) {
		double out;
		out = (double) Math.round(in * 100000) / 100000;
		return out;
	}
}
