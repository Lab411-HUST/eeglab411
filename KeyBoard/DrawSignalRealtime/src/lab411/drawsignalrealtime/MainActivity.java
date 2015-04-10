package lab411.drawsignalrealtime;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

	/* Init Graph 4 */
	private XYMultipleSeriesDataset mDataSet4 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender4 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries4;
	private GraphicalView mChartView4;
    
	
	int x = 0;
	// 
	
    //double [] mSignal=new double[128];
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.lab411.eegdata")) {
				int[] signal = intent.getExtras().getIntArray("data");
				
               	mCurrentSeries1.add(x, signal[10]);
				mRender1.setXAxisMin(x - 512);
				mRender1.setXAxisMax(x);
				mChartView1.repaint();

				mCurrentSeries2.add(x, signal[8]);
				mRender2.setXAxisMin(x - 512);
				mRender2.setXAxisMax(x);
				mChartView2.repaint();

				mCurrentSeries3.add(x, signal[4]);
				mRender3.setXAxisMin(x - 512);
				mRender3.setXAxisMax(x);
				mChartView3.repaint();

				mCurrentSeries4.add(x, signal[5]);
				mRender4.setXAxisMin(x - 512);
				mRender4.setXAxisMax(x);
				mChartView4.repaint();
				x++;
			}
			
		}
	};
	
	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter("com.lab411.eegdata"));
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		graph1Init();
		graph2Init();
		graph3Init();
		graph4Init();

	}

	

	// GraphInit1
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
		mRender1.setYAxisMin(8000);
		mRender1.setXTitle("Time");
		mRender1.setYTitle("Applifier");
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.grap1);
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

		XYSeries series = new XYSeries("AF3");
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

	// GraphInit2
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
		mRender2.setYAxisMax(10000);
		mRender2.setYAxisMin(8000);
		mRender2.setXTitle("Time");
		mRender2.setYTitle("Applifier");
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.grap2);
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

		XYSeries series = new XYSeries("AF4");
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

	// Graph3
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
		mRender3.setYAxisMax(10000);
		mRender3.setYAxisMin(8000);
		mRender3.setXTitle("Time");
		mRender3.setYTitle("Applifier");
		if (mChartView3 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.grap3);
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

		XYSeries series = new XYSeries("F7");
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
		mRender4.setAxisTitleTextSize(20);
		mRender4.setChartTitleTextSize(20);
		mRender4.setLabelsTextSize(20);
		mRender4.setLegendTextSize(22);
		mRender4.setMargins(new int[] { 10, 40, 10, 10 });
		mRender4.setZoomButtonsVisible(true);
		mRender4.setPointSize(2.0f);
		mRender4.setXAxisMin(0);
		mRender4.setXAxisMax(512);
		mRender4.setYAxisMax(10000);
		mRender4.setYAxisMin(8000);
		mRender4.setXTitle("Time");
		mRender4.setYTitle("Applifier");
		if (mChartView4 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.grap4);
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

		XYSeries series = new XYSeries("F8");
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
