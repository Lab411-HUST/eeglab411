package com.lab411.emotionsignalgraph;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	/* Init Graph 1 */
	private XYMultipleSeriesDataset mDataSet1 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender1 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries1;
	private GraphicalView mChartView1;
	/* Init Graph 2 */
	private XYMultipleSeriesDataset mDataSet2 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender2 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries2;
	private GraphicalView mChartView2;
	/* Init Graph 3 */
	private XYMultipleSeriesDataset mDataSet3 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender3 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries3;
	private GraphicalView mChartView3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		graph1Init();
		graph2Init();
		graph3Init();

		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(new underground()).start();
		//new Thread(RunWithFile).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Init grahp 1.
	 */
	private void graph1Init() {
		mRender1.setApplyBackgroundColor(true);
		mRender1.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender1.setAxisTitleTextSize(20);
		mRender1.setChartTitleTextSize(20);
		mRender1.setLabelsTextSize(20);
		mRender1.setLegendTextSize(22);
		mRender1.setMargins(new int[] { 10, 40, 10, 10 });
		mRender1.setZoomButtonsVisible(true);
		mRender1.setPointSize(2.0f);
		mRender1.setXAxisMin(0);
		mRender1.setXAxisMax(512);
		mRender1.setYAxisMax(10000);
		mRender1.setYAxisMin(7000);
		mRender1.setXTitle("Time");
		mRender1.setYTitle("Applifier");
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout_graph1);
			mChartView1 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet1, mRender1);
			mRender1.setClickEnabled(true);
			mRender1.setSelectableBuffer(10);
			layout.addView(mChartView1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView1.repaint();
		}
		XYSeries series = new XYSeries("Fc6 signal");
		mDataSet1.addSeries(series);
		mCurrentSeries1 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender1.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);
		mChartView1.repaint();
	}

	private void graph2Init() {
		mRender2.setApplyBackgroundColor(true);
		mRender2.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender2.setAxisTitleTextSize(20);
		mRender2.setChartTitleTextSize(20);
		mRender2.setLabelsTextSize(20);
		mRender2.setLegendTextSize(22);
		mRender2.setMargins(new int[] { 10, 40, 10, 10 });
		mRender2.setZoomButtonsVisible(true);
		mRender2.setPointSize(2.0f);
		mRender2.setXAxisMin(0);
		mRender2.setXAxisMax(512);
		mRender2.setYAxisMax(20);
		mRender2.setYAxisMin(1);
		mRender2.setXTitle("Time");
		mRender2.setYTitle("Applifier");
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout_graph2);
			mChartView2 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet2, mRender2);
			mRender2.setClickEnabled(true);
			mRender2.setSelectableBuffer(10);
			mChartView2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			layout.addView(mChartView2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView2.repaint();
		}

		XYSeries series = new XYSeries("Fc6 Filter");
		mDataSet2.addSeries(series);
		mCurrentSeries2 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.RED);
		mRender2.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView2.repaint();
	}

	private void graph3Init() {
		mRender3.setApplyBackgroundColor(true);
		mRender3.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender3.setAxisTitleTextSize(20);
		mRender3.setChartTitleTextSize(20);
		mRender3.setLabelsTextSize(20);
		mRender3.setLegendTextSize(22);
		mRender3.setMargins(new int[] { 10, 40, 10, 10 });
		mRender3.setZoomButtonsVisible(true);
		mRender3.setPointSize(2.0f);
		mRender3.setXAxisMin(0);
		mRender3.setXAxisMax(512);
		mRender3.setYAxisMax(5);
		mRender3.setYAxisMin(1);
		mRender3.setXTitle("Time");
		mRender3.setYTitle("Applifier");
		if (mChartView3 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout_graph3);
			mChartView3 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet3, mRender3);
			mRender3.setClickEnabled(true);
			mRender3.setSelectableBuffer(10);
			mChartView3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			layout.addView(mChartView3, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView3.repaint();
		}

		XYSeries series = new XYSeries("Arousal Fc6");
		mDataSet3.addSeries(series);
		mCurrentSeries3 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.BLUE);
		mRender3.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView3.repaint();
	}

	/**
	 * Convert int array to byte array.
	 * 
	 * @param src
	 *            source int array
	 * @return byte array.
	 */
	public byte[] int2byte(int[] src) {
		byte[] res = new byte[src.length];
		for (int i = 0; i < src.length; i++) {
			res[i] = (byte) src[i];
		}
		return res;

	}

	/**
	 * Excute command.
	 * 
	 * @param cmds
	 *            list command.
	 * @throws Exception
	 */
	public void doCmds(List<String> cmds) throws Exception {

		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd + "\n");
		}

		os.writeBytes("exit\n");
		os.flush();
		os.close();

		process.waitFor();

	}

	private List<Emokit_Frame> dataIn = new ArrayList<Emokit_Frame>();


	/**
	 * class processing signal to draw graph.
	 * 
	 * @author kidogb.
	 * 
	 */
	class underground extends Thread {
		int windowsize = 1024;
		int signalAF3[] = new int[windowsize];
		int signalAF4[] = new int[windowsize];
		int signalFc6[] = new int[windowsize];
		int signalF4[] = new int[windowsize];

		double x = 0;
		int overlap = 0;
		double mean = 0;
		float sum = 0;
		int cout = 0;
		ArrayList<Double> list_hfd = new ArrayList<Double>();

		public void run() {
			int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			Log.i("TAG", "Check co open duoc ko : " + check_open);
			if (check_open != 1) {
				Toast.makeText(getApplicationContext(), "Cannot open device",
						Toast.LENGTH_SHORT).show();
				return;
			}
			while (true) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				/*
				 * Get data from emokit. Window frame is 1024. Overlap is 10.
				 */
				try {
					if(cout < 128*56){
						k = AES.get_data(AES.get_raw_unenc(data));
						if (dataIn.size() < windowsize) {
							dataIn.add(k);
						} else {
							dataIn.remove(0);
							dataIn.add(k);
							overlap++;
						}
						// Draw graph 1: data af4.
						mCurrentSeries1.add(x, k.FC6);
						mRender1.setXAxisMin(x - 256);
						mRender1.setXAxisMax(x);
						mChartView1.repaint();

						if (overlap >= 10) {
							overlap = 0;
							/*
							 * Filter processing and calculate emotion:
							 */
							/*
							 * for (int i = 0; i < dataIn.size(); i++) { sum +=
							 * dataIn.get(i).FC6; } mean = sum / dataIn.size(); sum
							 * = 0;
							 */
							for (int i = 0; i < windowsize; i++) {
								signalFc6[i] = (int) dataIn.get(i).FC6;
								signalF4[i] = (int) dataIn.get(i).F4;
							}
							// Draw graph 2:

							// double af3 = Filter.getmean(signalF3,
							// signalF3.length, 1024, 2 * Math.PI * 8 / 128,
							// 2 * Math.PI * 12 / 128, 1.5);
							//
							// double bf3 = Filter.getmean(signalF3,
							// signalF3.length, 1024, 2 * Math.PI * 12 / 128,
							// 2 * Math.PI * 30 / 128, 1.5);
							//
							// double arousal_f3 = bf3/af3;
							// Log.d("TAG", x + ": " + arousal_f3);
							// // mean = EmotionalProcess.mean(Filter.getYnFilter(
							// // signalFC6, 256, 256, 2 * Math.PI * 2 / 128,
							// // 2 * Math.PI * 12 / 128, 1.5));
							// mCurrentSeries2.add(x, arousal_f3);
							// mRender2.setXAxisMin(x - 256);
							// mRender2.setXAxisMax(x);
							// mChartView2.repaint();
							// Draw graph 3:

							/*
							 * double af4 = Filter.getmean(signalF4,
							 * signalF4.length, 1024, 2 * Math.PI * 8 / 128, 2 *
							 * Math.PI * 12 / 128, 1.5);
							 * 
							 * double bf4 = Filter.getmean(signalF4,
							 * signalF4.length, 1024, 2 * Math.PI * 12 / 128, 2 *
							 * Math.PI * 30 / 128, 1.5);
							 */
							double hfd_arousal_fc6 = Filter.gethfd(Filter
									.getYnFilter(signalFc6, signalFc6.length, 1024,
											2 * Math.PI * 2 / 128,
											2 * Math.PI * 30 / 128, 1.5),
									signalFc6.length);
							list_hfd.add(hfd_arousal_fc6);
							// Filter.writedata(signalF3);
							/* double hfd_arousal_f3 = Filter.gethfd(signalF3, 256); */
							/*
							 * for(int i = 0; i< 256;i++){ Log.d("TAG", "signal: " +
							 * signalF3[i] +"\n"); }
							 */
							Log.d("TAG", "hfd" + ": " + hfd_arousal_fc6);
							// mean = EmotionalProcess.mean(Filter.getYnFilter(
							// signalFC6, 256, 256, 2 * Math.PI * 2 / 128,
							// 2 * Math.PI * 12 / 128, 1.5));
							mCurrentSeries3.add(x, hfd_arousal_fc6);
							mRender3.setXAxisMin(x - 256);
							mRender3.setXAxisMax(x);
							mChartView3.repaint();
						}
						x++;
						cout ++;
					}else{
						double mean = Filter.mean(list_hfd);
						//Filter.writedata(list_hfd,mean);
					/*	for(int i = 0; i<list_hfd.size();i++){
							Log.d("TAG", list_hfd.get(i) + "\t");
						}*/
						Log.d("TAG", "MEAN: " + mean);
						list_hfd.clear();
						cout =0;
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Read file
	 * 
	 */
	private Runnable RunWithFile = new Runnable() {
		public void run() {
			int windowsize = 1024;
			int number = 0;
			int overlap = 0;
			int signalAF3[] = new int[windowsize];
			int signalAF4[] = new int[windowsize];
			int signalFc6[] = new int[windowsize];
			int signalF4[] = new int[windowsize];
			try {
				int x = 0;
				Log.d("TAG", "ReadFile");
				File file = new File("sdcard/eyeblink2.txt");
				FileReader fileRead = new FileReader(file);
				BufferedReader bufferRead = new BufferedReader(fileRead);
				String line;
				while ((line = bufferRead.readLine()) != null) {
					
					String items[] = line.split("\\s+");

					// System.out.println(line);
					// get data from fc6.
					mCurrentSeries1.add(x, Integer.valueOf(items[1]));
					mRender1.setXAxisMin(x - 256);
					mRender1.setXAxisMax(x);
					mChartView1.repaint();

					Emokit_Frame k = new Emokit_Frame();
					k.F3 = Integer.valueOf(items[0]);
					k.FC6 = Integer.valueOf(items[1]);
					k.P7 = Integer.valueOf(items[2]);
					k.T8 = Integer.valueOf(items[3]);
					k.F7 = Integer.valueOf(items[4]);
					k.F8 = Integer.valueOf(items[5]);
					k.T7 = Integer.valueOf(items[6]);
					k.P8 = Integer.valueOf(items[7]);
					k.AF4 = Integer.valueOf(items[8]);
					k.F4 = Integer.valueOf(items[9]);
					k.AF3 = Integer.valueOf(items[10]);
					k.O2 = Integer.valueOf(items[11]);
					k.O1 = Integer.valueOf(items[12]);
					k.FC5 = Integer.valueOf(items[13]);
					//Log.d("TAG", "Windowsize");
					if (dataIn.size() < windowsize) {
						dataIn.add(k);
					} else {
						dataIn.remove(0);
						dataIn.add(k);
					}
					//Log.d("TAG", "Copy data");
					if(number < windowsize){
						signalFc6[number] =(int) dataIn.get(number).FC6;
						number++;
					}else{
						number = 0;
					}
					if(overlap <10){
						overlap++;
					}else{
						overlap = 0;
						Log.d("TAG", "Calculate hfd");
						double hfd_arousal_fc6 = Filter.gethfd(Filter
								.getYnFilter(signalFc6, signalFc6.length, 1024,
										2 * Math.PI * 2 / 128,
										2 * Math.PI * 30 / 128, 1.5),
								signalFc6.length);
						Log.d("TAG", "hfd" + ": " + hfd_arousal_fc6);
						mCurrentSeries3.add(x, hfd_arousal_fc6);
						mRender3.setXAxisMin(x - 256);
						mRender3.setXAxisMax(x);
						mChartView3.repaint();
					}
					x++;
				}
				fileRead.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
