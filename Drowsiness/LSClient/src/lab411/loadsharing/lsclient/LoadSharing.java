package lab411.loadsharing.lsclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoadSharing extends Activity {
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

	/* Init Graph 4 */
	private XYMultipleSeriesDataset mDataSet4 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender4 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries4;
	private GraphicalView mChartView4;
	
	/* Init Graph 5 */
	private XYMultipleSeriesDataset mDataSet5 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender5 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries5;
	private GraphicalView mChartView5;
	
	int x = 0;
	int x_realtime = 0;
	
	int [] cpu_processor = new int[4];
	int cpu_usage;
	int totalMemory;
	int availableMemory;
	int ram_process;
	
	private boolean run1, run2, run3;
	String train_result, detect_result;
	double realtime;
	
	ProgressBar pb_cpu, pb_ram;
	TextView tv_ram, tv_cpu, tv_ramprocess;
	EditText edt_minp, edt_maxp;
	Button btn_setWorkingMemory;
	Button btn_pctrain, btn_detect;
	TextView tv_detect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_sharing);
		pb_cpu = (ProgressBar)findViewById(R.id.pb_cpu);
		pb_ram = (ProgressBar)findViewById(R.id.pb_ram);
		tv_cpu = (TextView)findViewById(R.id.tv_cpu);
		tv_ram = (TextView)findViewById(R.id.tv_ram);
		tv_ramprocess = (TextView)findViewById(R.id.tv_ramprocess);
		tv_detect = (TextView) findViewById(R.id.tv_detect);
		
		edt_maxp = (EditText)findViewById(R.id.edt_pcmax);
		edt_minp = (EditText)findViewById(R.id.edt_pcmin);
		
		btn_setWorkingMemory = (Button)findViewById(R.id.btn_process);
		btn_pctrain = (Button)findViewById(R.id.btn_pctrain);
		btn_detect = (Button)findViewById(R.id.btn_detect);
		graph1Init();
		graph2Init();
		graph3Init();
		graph4Init();
		graph5Init();
		Performance p = new Performance();
		p.start();
		
		btn_setWorkingMemory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btn_pctrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				NetTrain n = new NetTrain();
				n.start();
			}
		});
		btn_detect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DrawRealTime drt = new DrawRealTime();
				Thread t = new Thread(drt);
				t.start();
			}
		});
	}
	
	class Performance extends Thread{
		Socket socket;
    	DataOutputStream dos;
    	DataInputStream dis;
    	byte[]data;
    	DecimalFormat df = new DecimalFormat("#.00");
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		run1 = true;
    		try {
    			socket = new Socket(PCServer.IP, PCServer.PORT);
        		dos=new DataOutputStream(socket.getOutputStream());
    			String str = "perform";
    			data = str.getBytes();
    			dos.write(data);
    			
    			dis=new DataInputStream(socket.getInputStream());
				data = new byte[200];
				dis.read(data);
				String m = new String(data);
				String [] m1 = m.split("xx"); 
				totalMemory = Integer.parseInt(m1[0]);
				LoadSharing.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb_cpu.setMax(100);
						pb_ram.setMax(totalMemory);
					}
				});
				while(run1)
	    		{
    				dis=new DataInputStream(socket.getInputStream());
    				data = new byte[100];
    				dis.read(data);
    				String s = new String(data);
    				String [] s1 = s.split("x");
					availableMemory = Integer.parseInt(s1[0]);
					ram_process = Integer.parseInt(s1[1]);
					cpu_usage = Integer.parseInt(s1[2]);
					String [] s2 = s1[3].split("z");
    				for(int i=0; i<4; i++){
    					cpu_processor[i] = Integer.parseInt(s2[i]);
    				}
    				LoadSharing.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pb_cpu.setProgress(cpu_usage);
							pb_ram.setProgress(availableMemory);
							tv_cpu.setText(Integer.toString(cpu_usage) + " %");
							tv_ram.setText(df.format((double)availableMemory/1024) + "/" + df.format((double)totalMemory/1024)+"GB");
							tv_ramprocess.setText(Integer.toString(ram_process)+"MB");
							mCurrentSeries1.add(x, cpu_processor[0]);
							mRender1.setXAxisMin(x - 200);
							mRender1.setXAxisMax(x);
							mChartView1.repaint();

							mCurrentSeries2.add(x, cpu_processor[1]);
							mRender2.setXAxisMin(x - 200);
							mRender2.setXAxisMax(x);
							mChartView2.repaint();

							mCurrentSeries3.add(x, cpu_processor[2]);
							mRender3.setXAxisMin(x - 200);
							mRender3.setXAxisMax(x);
							mChartView3.repaint();

							mCurrentSeries4.add(x, cpu_processor[3]);
							mRender4.setXAxisMin(x - 200);
							mRender4.setXAxisMax(x);
							mChartView4.repaint();
							x++;
						}
					});
	    		}
    			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	class NetTrain extends Thread{
		Socket socket;
    	DataOutputStream dos;
    	DataInputStream dis;
    	byte[]data;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				socket = new Socket(PCServer.IP, PCServer.PORT);
        		dos=new DataOutputStream(socket.getOutputStream());
    			String str = "train";
    			data = str.getBytes();
    			dos.write(data);
    			dis=new DataInputStream(socket.getInputStream());
        		data = new byte[20];
        		dis.read(data);
        		train_result = new String(data);
        		
    			LoadSharing.this.runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					Toast.makeText(getApplicationContext(), train_result, Toast.LENGTH_LONG).show();
    				}
    			});
			} catch (IOException e) {}
		}
	}
	
	class DrawRealTime implements Runnable
	{	
		Socket socket;
		DataOutputStream dos;
		byte[]data;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				socket = new Socket(PCServer.IP, PCServer.PORT);
        		dos=new DataOutputStream(socket.getOutputStream());
    			String str = "detect";
    			data = str.getBytes();
    			dos.write(data);
				
				Detect dt = new Detect(socket);
				dt.start();
				RealTime rt = new RealTime();
				rt.start();
				
				InputStream is = getResources().openRawResource(R.raw.data); 
	    		InputStreamReader isr = new InputStreamReader(is); 
	    		BufferedReader br = new BufferedReader(isr); 
	    		String s="";
	    		StringBuilder sb = new StringBuilder();
	    		int count = 0;
	    		while ((s = br.readLine()) != null) {
	    			realtime = Double.parseDouble(s);
	    			LoadSharing.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCurrentSeries5.add(x_realtime, realtime);
							mRender5.setXAxisMin(x_realtime - 2000);
							mRender5.setXAxisMax(x_realtime);
							mChartView5.repaint();
							x_realtime++;
						}
					});
					sb.append(s + "\n");
					count ++;
					if(count == 50)
					{
						sb.append("x");
						dos=new DataOutputStream(socket.getOutputStream());
						data = sb.toString().getBytes();
			    		dos.write(data);
			    		sb.delete(0, sb.length());
			    		count = 0;
					}
					Thread.sleep(10);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class Detect extends Thread
	{
		Socket socket;

    	DataInputStream dis;
    	DataOutputStream dos;
    	byte[]data;
		public Detect(Socket s)
		{
			socket = s;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			run2 = true;
			try {
				while(run2)
				{
					dis=new DataInputStream(socket.getInputStream());
					data = new byte[50];
					dis.read(data);
					detect_result = new String(data);
					LoadSharing.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), detect_result.toString(), Toast.LENGTH_LONG).show();
						}
					});
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class RealTime extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			run3 = true;
			try {
				while(run3)
				{
					LoadSharing.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							int tmp = x_realtime/100;
							int h = tmp/3600;
							int mi = (tmp - h*3600)/60;
							int sec = tmp - h*3600 - mi*60;
							tv_detect.setText(Integer.toString(h)+":"+Integer.toString(mi)+":"+Integer.toString(sec));
						}
					});
					Thread.sleep(1000);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void graph1Init() {
		mRender1.setApplyBackgroundColor(true);
		mRender1.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender1.setAxisTitleTextSize(15);
		mRender1.setChartTitleTextSize(15);
		mRender1.setLabelsTextSize(15);
		mRender1.setLegendTextSize(15);
		mRender1.setMargins(new int[] { 10, 30, 10, 10 });
		mRender1.setPointSize(2.0f);
		mRender1.setXAxisMin(0);
		mRender1.setXAxisMax(200);
		mRender1.setYAxisMax(100);
		mRender1.setYAxisMin(0);
		mRender1.setXTitle("Connect");
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap1);
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

		XYSeries series = new XYSeries("CPU 1");
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
		mRender2.setAxisTitleTextSize(15);
		mRender2.setChartTitleTextSize(15);
		mRender2.setLabelsTextSize(15);
		mRender2.setLegendTextSize(15);
		mRender2.setMargins(new int[] { 10, 30, 10, 10 });
		mRender2.setPointSize(2.0f);
		mRender2.setXAxisMin(0);
		mRender2.setXAxisMax(200);
		mRender2.setYAxisMax(100);
		mRender2.setYAxisMin(0);
		mRender2.setXTitle("Training Networks + Detect Signal");
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap2);
			mChartView2 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet2, mRender2);
			mRender2.setClickEnabled(true);
			mRender2.setSelectableBuffer(10);
			mChartView2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView2.repaint();
		}

		XYSeries series = new XYSeries("CPU 2");
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
		mRender3.setAxisTitleTextSize(15);
		mRender3.setChartTitleTextSize(15);
		mRender3.setLabelsTextSize(15);
		mRender3.setLegendTextSize(15);
		mRender3.setMargins(new int[] { 10, 30, 10, 10 });
		mRender3.setPointSize(2.0f);
		mRender3.setXAxisMin(0);
		mRender3.setXAxisMax(200);
		mRender3.setYAxisMax(100);
		mRender3.setYAxisMin(0);
		mRender3.setXTitle("");
		if (mChartView3 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap3);
			mChartView3 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet3, mRender3);
			mRender3.setClickEnabled(true);
			mRender3.setSelectableBuffer(10);
			mChartView3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView3, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView3.repaint();
		}

		XYSeries series = new XYSeries("CPU 3");
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

	private void graph4Init() {
		mRender4.setApplyBackgroundColor(true);
		mRender4.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender4.setAxisTitleTextSize(15);
		mRender4.setChartTitleTextSize(15);
		mRender4.setLabelsTextSize(15);
		mRender4.setLegendTextSize(15);
		mRender4.setMargins(new int[] { 10, 30, 10, 10 });
		mRender4.setPointSize(2.0f);
		mRender4.setXAxisMin(0);
		mRender4.setXAxisMax(200);
		mRender4.setYAxisMax(100);
		mRender4.setYAxisMin(0);
		if (mChartView4 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap4);
			mChartView4 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet4, mRender4);
			mRender4.setClickEnabled(true);
			mRender4.setSelectableBuffer(10);
			mChartView4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView4, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView4.repaint();
		}

		XYSeries series = new XYSeries("CPU 4");
		mDataSet4.addSeries(series);
		mCurrentSeries4 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender4.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView4.repaint();
	}
	
	private void graph5Init() {
		mRender5.setApplyBackgroundColor(true);
		mRender5.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender5.setAxisTitleTextSize(15);
		mRender5.setChartTitleTextSize(15);
		mRender5.setLabelsTextSize(15);
		mRender5.setLegendTextSize(15);
		mRender5.setMargins(new int[] { 10, 30, 10, 10 });
		mRender5.setPointSize(2.0f);
		mRender5.setXAxisMin(0);
		mRender5.setXAxisMax(2000);
		mRender5.setYAxisMax(50);
		mRender5.setYAxisMin(-50);
		if (mChartView5 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap5);
			mChartView5 = ChartFactory.getLineChartView(
					getApplicationContext(), mDataSet5, mRender5);
			mRender5.setClickEnabled(true);
			mRender5.setSelectableBuffer(10);
			mChartView5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView5, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView5.repaint();
		}
		
		XYSeries series = new XYSeries("Pz-Oz channel");
		mDataSet5.addSeries(series);
		mCurrentSeries5 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender5.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView5.repaint();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		run1 = false;
		run2 = false;
		run3 = false;
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_sharing, menu);
		return true;
	}

}
