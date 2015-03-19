package com.example.heso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import com.htd.hust.coadaptp300.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    static {
    	System.loadLibrary("heso");
    }
   public native static double[] heso();
   public native static double[] signal(double[] a,boolean j,double[] c);
   public native double[] zi(double[] a);
   double[] heso=new double[18];
   
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

	private List<Emokit_Frame> dataIn = new ArrayList<Emokit_Frame>();
	
	double x=0;
	double[] arr=new double[32];
	public int mIntail=0;
	Filter kfilt;
	double[] hj=new double[8];
	
	Emokit_Frame k = new Emokit_Frame();  
	int index=0;
	String data="";
	double nums=0;
  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.icarealtime_main);

		double b[]=new double[18];
		b=heso();
		heso=b;
		String s="";
		for(int i=0;i<18;i++) {
			s+=b[i]+" ; ";
		}

//		Toast.makeText(MainActivity.this, s+"", Toast.LENGTH_SHORT).show();
		graph1Init();
		graph2Init();
		graph3Init();
		new Thread(new Run()).start();
		
	}

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
		mRender1.setXAxisMax(256);
		mRender1.setYAxisMax(10000);
		mRender1.setYAxisMin(8000);
		mRender1.setXTitle("Time");
		mRender1.setYTitle("Amplitude");
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.parent_grap1);
			mChartView1 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet1, mRender1);
			mRender1.setClickEnabled(true);
			mRender1.setSelectableBuffer(10);
			mChartView1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView1.repaint();
		}

		XYSeries series = new XYSeries("Original signal");
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
		mRender2.setXAxisMax(256);
		mRender2.setYAxisMax(100);
		mRender2.setYAxisMin(-100);
		mRender2.setXTitle("Time");
		mRender2.setYTitle("Amplitude");
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.parent_grap2);
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

		XYSeries series = new XYSeries("Original signal");
		mDataSet2.addSeries(series);
		mCurrentSeries2 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
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
		mRender3.setXAxisMax(256);
		mRender3.setYAxisMax(10000);
		mRender3.setYAxisMin(8000);
		mRender3.setXTitle("Time");
		mRender3.setYTitle("Amplitude");
		if (mChartView3 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.parent_grap3);
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

		XYSeries series = new XYSeries("Original signal");
		mDataSet3.addSeries(series);
		mCurrentSeries3 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender3.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView3.repaint();
	}

	class Run implements Runnable {

		@Override
		public void run() {
			double x=0;
			double indexs = 0;
			List<Emokit_Frame> signal = new ArrayList<Emokit_Frame>();
		
			List<Integer> list = new ArrayList<Integer>();
			List<Integer> list_process = new ArrayList<Integer>();
			try {
					BufferedReader br = new BufferedReader(new FileReader(new File(com.htd.hust.coadaptp300.MainActivity.FILE_WRITE)));
			//	BufferedReader br = new BufferedReader(new FileReader(new File("/sdcard/original_signal.txt")));
			//	BufferedReader br2 = new BufferedReader(new FileReader(new File("/sdcard/after_signal.txt")));
			//	BufferedReader br = new BufferedReader(new FileReader(new File("/sdcard/2dataF7_1.txt")));
				
				String a = "";
				String b= "";
				while ((a=br.readLine()) != null) {
					//a = br.readLine();
					System.out.println(a);
					if ((a != null) && (!a.trim().equals(""))) {
						String[] res = a.split(" ");
						k.FC6=(int)Double.parseDouble(res[1]);
						/*File file = new File(Environment.getExternalStorageDirectory().getPath()
								+File.separator+"dataout.txt");
						BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
						bw.write(String.valueOf(k.FC6));
						bw.write("\n");
						bw.close();*/
						mCurrentSeries1.add(indexs, k.FC6);
						mRender1.setXAxisMin(indexs - 64);
						mRender1.setXAxisMax(indexs);
						mChartView1.repaint();
						indexs++;
						if (list.size() == 32)
						{
							
							list_process.addAll(list);
							//doFilter(list_process);
							double[] in = new double[list_process.size()];
							for (int ind=0;ind<in.length;ind++)
							{
								in[ind] = list_process.get(ind);
							}
							int[] out = onProcessFilter(in);
							list_process.removeAll(list_process);
							/*
							Log.e("data", k.F3+"");
							mCurrentSeries1.add(x, k.F3);
							mRender1.setXAxisMin(x - 256);
							mRender1.setXAxisMax(x);
							mChartView1.repaint();
							x++;
						*/
							x++;
							Log.e("Count run", x+"");
							list.removeAll(list);
						}
						else
						{
						list.add(k.FC6);
						}
					}
					
			

				}
				
				int ind = 0;
		/*	while ((b=br2.readLine())!=null)
			{
				if ((b != null) && (!b.trim().equals(""))) {
					
					int value =(int)Double.parseDouble(b);
					
					mCurrentSeries3.add(ind, value);
					mRender3.setXAxisMin(ind - 64);
					mRender3.setXAxisMax(ind);
					mChartView3.repaint();
					ind++;
				}
			}
				br2.close();
		*/		br.close();

				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

	
			/*double[] a = new double[64];
			
			for (int j=0;j<signal.size();j++)
			{
				a[j]=signal.get(j).value(index);
			}
			double[] b = new double[32];
			Filter k = new Filter(1, 32, heso);
			String c1="";
			for(int i=0;i<2;i++){
			double c[]=new double[32];
			
			for(int j=0;j<32;j++)
			   c[j]=a[i*32+j];
			
			b=k.getSignal(c);
			
				for(int t=0;t<32;t++)
					c1+=b[t]+"\n";
			}
			data=c1;
			bw.write(data);	
			
			bw.close();
			data="";*/
/*			int y = 0;
			int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			Log.i("TAG", "Check co open duoc ko : " + check_open);
			double x = 0;
			if (check_open != 1)
				return;
			while (true) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();
				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					// Log.i("TAG", k.F3 + " " + k.FC6 + " " + k.P7 + " " + k.T8
					// + " " + k.F7 + " " + k.F8 + " " + k.T7 + " " + k.P8
					// + " " + k.AF4 + " " + k.F4 + " " + k.AF3 + " "
					// + k.O2 + " " + k.O1 + " " + k.FC5);
					if (dataIn.size() < 256) {
						dataIn.add(k);
					} else {
						dataIn.remove(0);
						dataIn.add(k);
					}

					 Phat hien co nhay mat ko 
					if (dataIn.size() >= 256) {
						if (Math.abs(dataIn.get(100).AF3 - dataIn.get(120).AF3) > thresholdMin
								&& y >= 256) {
							// Co nhay mat
							drawGraph2();
							System.out.println("Eye Blink Detected!");
							handler.sendEmptyMessage(1);
							y = 0;
						}
						y++;
					}
*/
					
				/*} catch (Exception e) {
					e.printStackTrace();
				}*/
			}

		private void doFilter(List<Integer> list_process) {
			// TODO Auto-generated method stub
			double a[]=new double[64];
			double b[]=new double[32];
			for (int i=0;i<list_process.size();i++)
			{
				a[i]=list_process.get(i);
				
			}
			
			String c1="";
			
			for(int i=0;i<2;i++){
			double c[]=new double[32];
			
			for(int j=0;j<32;j++)
			{
			   c[j]=a[i*32+j];
		//	Log.e("value c", c[j]+"");
			}
		//	Filter k=new Filter(1,32,heso);
		//	b=k.getSignal(c);
			/*double[] kl=k.getcur();
			for(int i1=0;i1<8;i1++){
				gh+=kl[i1]+" ";
			}	*/

			
				for(int t=0;t<32;t++)
					
					{
					c1+=b[t]+"\n";
				
					mCurrentSeries1.add(nums, b[t]);
					mRender1.setXAxisMin(nums - 64);
					mRender1.setXAxisMax(nums);
					mChartView1.repaint();
					nums++;
					}
				
			}
			
			
			
			data=c1;
			writeDataout();
			

		}

		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		

	}

	private void drawGraph2() {
		mCurrentSeries2.clear();
		for (int i = 0; i < dataIn.size(); i++) {
			Emokit_Frame frame = dataIn.get(i);
			mCurrentSeries2.add(i, Integer.valueOf(frame.AF3));
			mRender2.setXAxisMin(i - 256);
			mRender2.setXAxisMax(i);
			mChartView2.repaint();
		}
	}
	
	 public void writeDataout()
	 {
	 String sdcard="/sdcard/daafterfilter.txt";
	 try {
	 OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(sdcard,true));
	 writer.write(data);
	 writer.close();
	 } catch (FileNotFoundException e) {
	 e.printStackTrace();
	 } catch (IOException e) {
	 e.printStackTrace();
	 }
	 }


  
  

public int[] onProcessFilter(double[] input){
	   int[] a=new int[32];
	   double[] output=new double[40];
	   if(mIntail==0){
		   double[] ge=new double[32];
		   ge=input;
		   int c=(int)ge[0];
		   kfilt=new Filter(1,32,c);
			hj=kfilt.getcur();
			output=signal(input,true,hj);
			mIntail=1;
	   }
	   else{
		   output=signal(input,false,hj);
	   }
	   for(int t=0;t<32;t++)
			a[t]=(int)output[t];
	  for(int t=32;t<40;t++)
			hj[t-32]=output[t];
	  
	  String c1="";
		for(int i=0;i<a.length;i++)
			
		{
		c1+=a[i]+"\n";
	
		mCurrentSeries2.add(nums, a[i]);
		mRender2.setXAxisMin(nums-64);
		mRender2.setXAxisMax(nums);
		mChartView2.repaint();
		nums++;
		}

		data=c1;
		writeDataout();
	  return a;
}
}