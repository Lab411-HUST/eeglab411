package lab411.eeg.openvibeforandroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import lab411.eeg.openvibeforandroid.*;
import lab411.eeg.p300processing.MainProcess;
import lab411.eeg.processSignal.BaseEpoch;
import lab411.eeg.processSignal.Config;
import lab411.eeg.processSignal.FilterEpoch;
import lab411.eeg.processSignal.Stimulation;
import lab411.eeg.processSignal.StimulationEpoch;
import lab411.eeg.processSignal.TemporalFilter;
import lab411.eeg.processSignal.XdawnFilterEpoch;
import lab411.eeg.processSignal.XdawnSpatialFilter;
import lab411.eeg.processSignal._600Epoch;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.openvibeforandroid.R;

public class MainActivity extends Activity implements Config {

	//

	private Handler handler = new Handler();
	private boolean mRun = false;
	private boolean mDownSampleRun = false;
	double[][] m_signal = new double[14][32];
	double mStartCaptuter = 0;
	double mStartFlash = 0;
	double mStartDown = 0;
	double mCaputerTime = 0;// dang test
	private long mEpochIndex = 0;
	private TemporalFilter filter;
	// cac hang doi
	Queue<BaseEpoch> mBaseEpoch = new LinkedList<BaseEpoch>(); // luu cac epoch
																// co ban
	/*
	 * luu cac epoch sau loc va sau downsample
	 */
	// Queue<FilterEpoch> mFilterEpochs=new LinkedList<FilterEpoch>();
	// Queue<double[][]> sauloc=new LinkedList<double[][]>();
	// Queue<double[][]> truocloc=new LinkedList<double[][]>();
	// thiet lap graphic de ve tin hieu
	/* Init Graph 1 */
	private XYMultipleSeriesDataset mDataSet1 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender1 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries1;
	private GraphicalView mChartView1;
	private Timer drawTimer;
	private TimerTask drawTask;
	Intent in = new Intent("com.lab411.loc");
	Intent m600Epoch = new Intent("com.lab411.600Epoch");
	Intent mFlash1 = new Intent("com.lab411.processFlash");
	Intent mDecimation = new Intent("com.lab411.draw");
	int mIntail = 0;
	int x = 0, y = 0;
	// xu ly epoch 600
	// private Timer Epoch600Timer ;
	// private TimerTask Epoch600Task;
	Queue<_600Epoch> m600EpochData = new LinkedList<_600Epoch>();
	ArrayList<_600Epoch> mTargetData = new ArrayList<_600Epoch>();
	ArrayList<_600Epoch> mNonTargetData = new ArrayList<_600Epoch>();
	ArrayList<XdawnFilterEpoch> mEpochArray = new ArrayList<XdawnFilterEpoch>();
	boolean mTrainning = true;
	public long mStartProcess600 = 0;
	// xu ly tin hieu kich thich
	Queue<Stimulation> mStimulationData = new LinkedList<Stimulation>();
	ArrayList<Stimulation> mStimulation = new ArrayList<Stimulation>();
	// hang doi nay luu tru du lieu
	// kich thich du tru
	Queue<Stimulation> mStimulationHistory = new LinkedList<Stimulation>();
	// lu du lieu sau downsample
	// ArrayList<double[][]> mDownSampleData=new ArrayList<double[][]>();
	ArrayList<FilterEpoch> mDownSampleData1 = new ArrayList<FilterEpoch>();
	int index = 0;// chi so hang hoac cot dc nhay
	// xu ly xDawn Spatil Filter
	double coefficientsXdawn[][] = new double[3][14];
	private int mXdawnIntail = 0;
	XdawnSpatialFilter mXdawnFilter;
	Intent inXdawn = new Intent("com.lab411.xDawnFilter");
	Queue<double[][]> queXdawn = new LinkedList<double[][]>();
	boolean mxDwanFilterState = false;
	// xu ly flash
	private int[] mScenarios = new int[144];
	private int mNumberTrail = 0;
	private int mNumberFlash = 0;
	private ArrayList<String> mTrail = new ArrayList<String>();
	private boolean mRow = true;
	private int mArrRow[] = new int[6];
	private int mArrCol[] = new int[6];
	private String[][] mCharacter = {
			new String[] { "A", "B", "C", "D", "E", "F" },
			new String[] { "G", "H", "I", "J", "K", "L" },
			new String[] { "M", "N", "O", "P", "Q", "R" },
			new String[] { "S", "T", "U", "V", "W", "X" },
			new String[] { "Y", "Z", "1", "2", "3", "4" },
			new String[] { "5", "6", "7", "8", "9", "0" } };

	private List<TableRow> rows;
	private List<TextView> character;
	private LinearLayout root;
	private LayoutInflater inflater;
	private TableLayout parent;
	private TextView tv_Counter;
	private Timer flashTimer;
	private TimerTask flashTask;
	// test thoi gian chay 1 ham xu ly
	long startfash = 0, endfash = 0;
	long startdow = 0, enddow = 0;
	boolean mFlash = false;
	// test
	long enddraw = 0;
	String timeepoch = "";
	int solanNhay = 0;
	String thoigianepoch = "";
	String thoigianflash = "";
	String epochAndFlash = "";//
	ArrayList<Integer> flashIndex = new ArrayList<Integer>();
	// ham xu ly ve tin hieu
	// xu ly feature Vector
	ArrayList<double[]> mFeature = new ArrayList<double[]>();
	//XDawn coefficients path
	public static final String pathXDawn = Environment.getExternalStorageDirectory().getPath()
			+ File.separator + "openvibe"+File.separator+"coefficientsXdawn.txt";
	//Coefficients Path
	public static final String pathClassifier = Environment.getExternalStorageDirectory().getPath()
			+ File.separator+"openvibe"+File.separator+"coefficientTrainer.txt";
	//coefficients array
	double[] coefficients = new double[58];
	//Index of target using in online scenario
	int rowNo = 0, colNo =0;
	
	private BroadcastReceiver rce = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("com.lab411.loc")) {
				if (!mBaseEpoch.isEmpty()) {
					double[][] sig = new double[14][32];
					double[][] filterdata = new double[14][32];
					double[][] down = new double[14][8];
					BaseEpoch baseepoch = mBaseEpoch.poll();
					sig = baseepoch.getSignal();
					filterdata = onFilter2(sig);
					// tinh downsample
					int h = 0;
					// dowmsample tin hieu
					for (int k = 0; k < 14; k++) {
						h = 0;
						for (int l = 0; l < 8; l++) {
							double sum = 0;
							for (int jk = 0; jk < 4; jk++) {
								sum += filterdata[k][h];
								h++;
							}
							down[k][l] = sum / 4;

						}
					}

					/*
					 * for(int i=0;i<32;i++){ mLoc+=filterdata[2][i]+"\n"; }
					 * for(int j=0;j<8;j++ ) mDownsample+=down[2][j]+"\n";
					 */

				}

				// sauloc.add(mOutput);
				/*
				 * double[][] fitlert=new double[NUMBER_CHANEL][32];
				 * fitlert=onFilter2(input);
				 */
				/*
				 * for(int i=0;i<32;i++){ mCurrentSeries1.add(x, truoc[4][i]);
				 * mRender1.setXAxisMin(x - 512); mRender1.setXAxisMax(x);
				 * mChartView1.repaint(); x++; //} } for(int j=0;j<8;j++){
				 * mCurrentSeries2.add(x, down[4][j]); mRender2.setXAxisMin(x -
				 * 512); mRender2.setXAxisMax(x); mChartView2.repaint(); y++; }
				 */
			}
			// RealTime XDawn Filter
			if (intent.getAction().equals("com.lab411.xDawnFilter")) {
				if (!queXdawn.isEmpty()) {
					double a[][] = new double[3][8];
					a = queXdawn.poll();
					for (int i = 0; i < 8; i++) {
						mCurrentSeries1.add(x, a[0][i]);
						mRender1.setXAxisMin(x - 512);
						mRender1.setXAxisMax(x);
						mChartView1.repaint();
						x++;
					}
				}

			}
			if (intent.getAction().equals("com.lab411.600Epoch")) {

			}
			if (intent.getAction().equals("com.lab411.processFlash")) {
				// long ty=System.currentTimeMillis();
				mNumberFlash=0;
				try {

					mFlash = false;
					Thread.sleep(1500);
					if(mTrainning)
					displayTaget(mTrail.get(mNumberTrail));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// long hj=System.currentTimeMillis()-ty;
				// Log.d("thoi gian tre nhay",hj+"" );
				// Toast.makeText(getApplicationContext(), "A",
				// Toast.LENGTH_SHORT).show();
				mNumberFlash=0;
				mFlash = true;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p300);
		graph1Init();
		// graph2Init();
		parent = (TableLayout) findViewById(R.id.parent);
		root = (LinearLayout) findViewById(R.id.root);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rows = new ArrayList<TableRow>();
		character = new ArrayList<TextView>();

		for (int i = 0; i < 6; i++) {
			View view = inflater.inflate(R.layout.row, null);
			TableRow row = (TableRow) view.findViewById(R.id.row);
			rows.add(row);
			for (int j = 0; j < 6; j++) {
				TextView tv = new TextView(this);
				tv.setTextSize(48);
				tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				tv.setTextColor(Color.rgb(70, 70, 70));
				tv.setGravity(Gravity.CENTER);
				tv.setText(mCharacter[i][j]);
				TableRow.LayoutParams params = new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT, 1);
				row.addView(tv, params);
				character.add(tv);
			}
			parent.addView(row);
		}

		/*
		 * tv_Counter = (TextView) findViewById(R.id.tv_counter);
		 * tv_Counter.setVisibility(View.GONE);
		 */
		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kichban, menu);
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// filter=new TemporalFilter(mHs)
		mNumberTrail = 0;// cho trainning 3 ki tu thoi
		mNumberFlash = 0;
		mRow = true;
		mXdawnIntail = 0;
		for (int i = 0; i < 6; i++) {
			mArrRow[i] = i;
			mArrCol[i] = i + 6;
		}
		registerReceiver(rce, new IntentFilter("com.lab411.xDawnFilter"));
		registerReceiver(rce, new IntentFilter("com.lab411.loc"));
		registerReceiver(rce, new IntentFilter("com.lab411.600Epoch"));
		registerReceiver(rce, new IntentFilter("com.lab411.processFlash"));
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (mRun) {
			// test tat ung dung
			flashTimer.purge();
			flashTimer.cancel();
			drawTimer.purge();
			drawTimer.cancel();
			// processRecycle();
		}
		mRun = false;
		mDownSampleRun = false;
		mIntail = 0;
		double t = mStartDown - mStartCaptuter;
		String c = mStartCaptuter + "\n";
		// writeData("/sdcard/DongBo/time.txt",c);
		// writeData("/sdcard/DongBo/thoigianepoch.txt",thoigianepoch);
		// writeData("/sdcard/DongBo/epochandFalst.txt",epochAndFlash);
		// writeData("/sdcard/DongBo/thoiflash.txt",thoigianflash);
		// writeData("/sdcard/DongBo/mDownsample.txt",mAfterDow);
		// writeData("/sdcard/DongBo/loc.txt",mBeforDow);
		// ghi du lieu epoch 600
		/*
		 * String d=""; for(int i=0;i<m600EpochData.size();i++) { double
		 * data[][]=new double[14][19]; _600Epoch ep=m600EpochData.poll();
		 * //data=ep.getSignal(); double time=ep.mStartTime;
		 * d="time :"+time+"\n"; }
		 */
		Toast.makeText(getApplicationContext(), "stop", 0).show();
		unregisterReceiver(rce);
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.clean:
			processRecycle();
			solanNhay = 0;
			break;
		case R.id.capture:
			// setup
			mScenarios = getScenariosFlash();
			if (mTrail.size() > 0)
				mTrail.removeAll(mTrail);
			mTrail.add("O");
			// mTrail.add("K");
			// mTrail.add("H");
			mRun = true;
			mIntail = 0;
			mDownSampleRun = true;
			// flash
			
			timeepoch = "";
			thoigianepoch = "";
			thoigianflash = "";
			epochAndFlash = "";
			mStartProcess600 = 0;
			mNumberTrail = 0;
			mNumberFlash = -1;
			mFlash = false;
			mTrainning = true;
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTrainning = true;
					CaptureSignal();
					processDownSample();
					sendBroadcast(mFlash1);
					processFlash();

				}
			});

			break;
		case R.id.xdawn:
			/*
			 * String chanel1=""; String chanel2=""; String chanel3=""; String
			 * chanel4=""; String chanel5=""; String chanel6=""; String
			 * chanel7=""; String chanel8=""; String chanel9=""; String
			 * chanel10=""; String chanel11=""; String chanel12=""; String
			 * chanel13=""; String chanel14=""; String data=""; for(int
			 * i=0;i<mDownSampleData1.size();i++) { double
			 * a[][]=mDownSampleData1.get(i).getSignal(); for(int k=0;k<14;k++)
			 * { for(int j=0;j<8;j++) { switch (k) { case 0:
			 * chanel1+=a[k][j]+",\n"; break; case 1: chanel2+=a[k][j]+",\n";
			 * break; case 2: chanel3+=a[k][j]+",\n"; break; case 3:
			 * chanel4+=a[k][j]+",\n"; break; case 4: chanel5+=a[k][j]+",\n";
			 * break; case 5: chanel6+=a[k][j]+",\n"; break; case 6:
			 * chanel7+=a[k][j]+",\n"; break; case 7: chanel8+=a[k][j]+",\n";
			 * break; case 8: chanel9+=a[k][j]+",\n"; break; case 9:
			 * chanel10+=a[k][j]+",\n"; break; case 10: chanel11+=a[k][j]+",\n";
			 * break; case 11: chanel12+=a[k][j]+",\n"; break; case 12:
			 * chanel13+=a[k][j]+",\n"; break; case 13: chanel14+=a[k][j]+",\n";
			 * break; default: break; } } data+=a[k][j]+"\n"; data+="\n"; }
			 * //data+="hj\n"; } //String erp=""; String erp1=""; String
			 * erp2=""; String erp3=""; String erp4=""; String erp5=""; String
			 * erp6=""; String erp7=""; String erp8=""; String erp9=""; String
			 * erp10=""; String erp11=""; String erp12=""; String erp13="";
			 * String erp14=""; for(int i=0;i<mTargetData.size();i++) { double
			 * b[][]=mTargetData.get(i).getSignal();
			 * //erp+="hj "+mTargetData.get(i).mStartTime+"\n"; for(int
			 * k=0;k<14;k++) { for(int j=0;j<19;j++) { switch (k) { case 0:
			 * erp1+=b[k][j]+",\n"; break; case 1: erp2+=b[k][j]+",\n"; break;
			 * case 2: erp3+=b[k][j]+",\n"; break; case 3: erp4+=b[k][j]+",\n";
			 * break; case 4: erp5+=b[k][j]+",\n"; break; case 5:
			 * erp6+=b[k][j]+",\n"; break; case 6: erp7+=b[k][j]+",\n"; break;
			 * case 7: erp8+=b[k][j]+",\n"; break; case 8: erp9+=b[k][j]+",\n";
			 * break; case 9: erp10+=b[k][j]+",\n"; break; case 10:
			 * erp11+=b[k][j]+",\n"; break; case 11: erp12+=b[k][j]+",\n";
			 * break; case 12: erp13+=b[k][j]+",\n"; break; case 13:
			 * erp14+=b[k][j]+",\n"; break; default: break; } }
			 * 
			 * //erp+=b[k][j]+"\n"; //erp+="\n"; } }
			 * writeData("/sdcard/DongBo/chanel1.txt",chanel1);
			 * writeData("/sdcard/DongBo/chanel2.txt",chanel2);
			 * writeData("/sdcard/DongBo/chanel3.txt",chanel3);
			 * writeData("/sdcard/DongBo/chanel4.txt",chanel4);
			 * writeData("/sdcard/DongBo/chanel5.txt",chanel5);
			 * writeData("/sdcard/DongBo/chanel6.txt",chanel6);
			 * writeData("/sdcard/DongBo/chanel7.txt",chanel7);
			 * writeData("/sdcard/DongBo/chanel8.txt",chanel8);
			 * writeData("/sdcard/DongBo/chanel9.txt",chanel9);
			 * writeData("/sdcard/DongBo/chanel10.txt",chanel10);
			 * writeData("/sdcard/DongBo/chanel11.txt",chanel11);
			 * writeData("/sdcard/DongBo/chanel12.txt",chanel12);
			 * writeData("/sdcard/DongBo/chanel13.txt",chanel13);
			 * writeData("/sdcard/DongBo/chanel14.txt",chanel14);
			 * 
			 * writeData("/sdcard/DongBo/erp1.txt",erp1);
			 * writeData("/sdcard/DongBo/erp2.txt",erp2);
			 * writeData("/sdcard/DongBo/erp3.txt",erp3);
			 * writeData("/sdcard/DongBo/erp4.txt",erp4);
			 * writeData("/sdcard/DongBo/erp5.txt",erp5);
			 * writeData("/sdcard/DongBo/erp6.txt",erp6);
			 * writeData("/sdcard/DongBo/erp7.txt",erp7);
			 * writeData("/sdcard/DongBo/erp8.txt",erp8);
			 * writeData("/sdcard/DongBo/erp9.txt",erp9);
			 * writeData("/sdcard/DongBo/erp10.txt",erp10);
			 * writeData("/sdcard/DongBo/erp11.txt",erp11);
			 * writeData("/sdcard/DongBo/erp12.txt",erp12);
			 * writeData("/sdcard/DongBo/erp13.txt",erp13);
			 * writeData("/sdcard/DongBo/erp14.txt",erp14); //
			 */

			int chuckCount = mDownSampleData1.size();
			Log.e("Chunk count", chuckCount + "");
			double signal[][] = new double[14][8 * chuckCount];

			// lay tin hieu
			for (int i = 0; i < mDownSampleData1.size(); i++) {
				double signalData[][] = new double[14][8];
				signalData = mDownSampleData1.get(i).getSignal();
				for (int j = 0; j < 14; j++) {
					for (int k = 0; k < 8; k++) {
						signal[j][i * 8 + k] = signalData[j][k];
					}
				}

			}
			// lay tin hieu erp
			int erpCount = mTargetData.size();
			double erpSignal[][] = new double[14][19];
			for (int i = 0; i < mTargetData.size(); i++) {
				double erpData[][] = new double[14][19];
				erpData = mTargetData.get(i).getSignal();
				for (int j = 0; j < 14; j++) {
					for (int k = 0; k < 19; k++) {
						erpSignal[j][k] += erpData[j][k];
					}
				}
			}
			for (int i = 0; i < 14; i++)
				for (int j = 0; j < 19; j++) {
					double t = erpSignal[i][j] / erpCount;
					erpSignal[i][j] = t;
				}
			// lay mang chi so index
			if (!flashIndex.isEmpty())
				flashIndex.removeAll(flashIndex);
			int index[] = new int[erpCount];
			for (int i = 0; i < mTargetData.size(); i++) {
				double time = mTargetData.get(i).getStartTime();
				double endTime = mDownSampleData1.get(chuckCount - 1)
						.getEndTime();
				int in = (int) (time * 8 * chuckCount / endTime);
				index[i] = in;
				flashIndex.add(in);
			}
			// ghi index
			/*
			 * String indexData=""; for(int i=0;i<erpCount;i++) {
			 * indexData+=index[i]+"\n"; }
			 * writeData("/sdcard/DongBo/dataIndex.txt",indexData); String
			 * Infor=""; Infor="ChuchCount "+chuckCount +" ErpCount "+erpCount;
			 * writeData("/sdcard/DongBo/Information.txt",Infor);
			 */
			/*
			 * int[] chiso={205,220,258,273,331,345,384,417,427,461,519,
			 * 533,572,577,601,625,664,669,722,746,799,813};
			 */
			double count = chuckCount;
			// String
			// klo=mSigNal[1]+" "+signal[0][23]+" "+erpSignal[4][4]+" "+count+" "+
			// +erpCount;
			// writeData("/sdcard/Xdawn/tinhieu.txt",klo);
			MainProcess
					.xDawnTrainer(signal, erpSignal, index, count, erpCount,pathXDawn);
			Toast.makeText(getApplicationContext(), "Xdawn finished",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.classifier:
			double coffi[] = readData(pathXDawn);
			String data = "";
			for (int i = 0; i < 42; i++) {
				data += coffi[i] + "\n";
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 14; j++) {
					coefficientsXdawn[i][j] = coffi[i * 14 + j];
				}
			}
			// Loc XDawn Filter
			/*
			 * double coefficient[][]={ {-5.598922e-02,
			 * 2.379672e-01,-1.702793e-01,-5.231787e-01,-2.740221e-01,
			 * 4.736639e-02, -1.173849e-02, 1.279642e-01,-8.597277e-02,
			 * -7.081522e-01, -9.132012e-02, 1.568739e-01, 3.017847e-02,
			 * 3.084083e-02}, {3.582795e-03 ,-2.661370e-02, -2.611766e-02,
			 * 8.925336e-02, -5.736295e-01, -2.627797e-02, 6.893228e-02,
			 * 3.658609e-02, -5.554216e-02, 7.897854e-01, -1.532178e-01,
			 * -2.559945e-03 ,6.118981e-02 ,2.695562e-02}, {2.797072e-02,
			 * -2.770093e-02, -9.377719e-02, -7.924409e-02, -6.582637e-01,
			 * 1.528031e-01, -1.135437e-01, -1.132216e-01, 1.588273e-03,
			 * 6.973587e-01, 6.982634e-02, 7.529501e-02, -5.633607e-02,
			 * 3.122263e-02} };
			 */

			XdawnSpatialFilter sp = new XdawnSpatialFilter(19,
					coefficientsXdawn);
			// Luu List Target va Non Target
			ArrayList<double[][]> targetFilter = new ArrayList<double[][]>();
			ArrayList<double[][]> nontargetFilter = new ArrayList<double[][]>();
			for (int i = 0; i < mTargetData.size(); i++) {
				double[][] array = mTargetData.get(i).mSignal;
				double[][] output = sp.SpatialFilter(array);
				targetFilter.add(output);
			}
			for (int i = 0; i < mNonTargetData.size(); i++) {
				double[][] array = mNonTargetData.get(i).mSignal;
				double[][] output = sp.SpatialFilter(array);
				nontargetFilter.add(output);
			}
			// Convert ve mang 1 chieu
			int length = targetFilter.size() + nontargetFilter.size();
			double[] totalFeatureVector = new double[length * 57];
			Log.e("Target Size", mTargetData.size() + "");
			Log.e("NonTarget Size", mNonTargetData.size() + "");
			Log.e("Total Length", length + "");
			// Xay dung Label
			double[] totalLabelFeature = new double[length];
			for (int i = 0; i < targetFilter.size(); i++) {
				double[][] arr = targetFilter.get(i);
				int length1 = arr.length;
				int length2 = arr[0].length;
				for (int j = 0; j < length1; j++) {
					for (int k = 0; k < length2; k++) {
						totalFeatureVector[i * 57 + j * 19 + k] = arr[j][k];
					}
				}
				totalLabelFeature[i] = 1;
			}
			for (int i = 0; i < nontargetFilter.size(); i++) {
				double[][] arr = nontargetFilter.get(i);
				int length1 = arr.length;
				int length2 = arr[0].length;
				for (int j = 0; j < length1; j++) {
					for (int k = 0; k < length2; k++) {
						totalFeatureVector[(i + targetFilter.size()) * 57 + j
								* 19 + k] = arr[j][k];
					}
				}
				totalLabelFeature[i + targetFilter.size()] = 2;
			}

			// Xay dung index
			int[] indexFeature = new int[length];
			for (int i = 0; i < length; i++) {
				indexFeature[i] = i;
			}
			
			
			final String fvPath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator+"eegkeyboard"+File.separator+"fvs.txt";
			final String indexPath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + "eegkeyboard" + File.separator+"indexs.txt";
			final String labelPath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + "eegkeyboard" + File.separator + "labels.txt";
			
			File fvFile = new File(fvPath);
			if (!fvFile.exists())
				try {
					fvFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			File indexFile = new File(indexPath);
			if (!indexFile.exists())
				try {
					indexFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			File labelFile = new File(labelPath);
			if (!labelFile.exists())
				try {
					labelFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));
			
				for (int i = 0 ; i < indexFeature.length; i++)
				{
					bw.write(String.valueOf(indexFeature[i]));
					bw.write(",");
				}
				
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				BufferedWriter bw1 = new BufferedWriter(new FileWriter(labelFile));
				for (int i = 0; i< totalLabelFeature.length; i++)
				{
					bw1.write(String.valueOf((int)totalLabelFeature[i]));
					bw1.write(",");
				}
				bw1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(fvFile));
				
				for (int i =0; i < totalFeatureVector.length; i++)
				{
					
				
					int r = (i % 57);
					if (r == 0)
					{
						bw2.write("{");
						bw2.write(String.valueOf(totalFeatureVector[i]));
					}
					else if (r == 56)
					{
						bw2.write(",");
						bw2.write(String.valueOf(totalFeatureVector[i]));
						bw2.write("}");
						bw2.write("\n");
					}
					else
					{
					bw2.write(",");
					bw2.write(String.valueOf(totalFeatureVector[i]));
					
					}
				}
			
				bw2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			double value = MainProcess.TestTrain(totalFeatureVector,
					indexFeature, totalLabelFeature, true, false, 20,pathClassifier);
			Toast.makeText(
					getApplicationContext(),
					"Classifier Trainer Done ! Value accuracy is " + value
							+ "% (optimistic)", Toast.LENGTH_LONG).show();
			

			break;
		case R.id.xdawnfilter:
			double coffi2[] = readData(pathXDawn);
			String data2 = "";
			for (int i = 0; i < 42; i++) {
				data2 += coffi2[i] + "\n";
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 14; j++) {
					coefficientsXdawn[i][j] = coffi2[i * 14 + j];
				}
			}
			
			mxDwanFilterState = true;
			mScenarios = getScenariosFlash();
			if (mTrail.size() > 0)
				mTrail.removeAll(mTrail);
			
			
		//	mTrail.add("A");
		//	mTrail.add("V");
		//	mTrail.add("G");
			mTrail.add("O");
			mTrainning = false;
			mRun = true;
			mIntail = 0;
			mDownSampleRun = true;
			mStartProcess600 = 0;
			mNumberTrail = 0;
			mNumberFlash = -1;
			mFlash = false;

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					CaptureSignal();
					processDownSample();
					sendBroadcast(mFlash1);
					processFlash();

				}
			});

			break;
		case R.id.online:
			//Clean
			processRecycle();
			solanNhay = 0;
			
			double coffi3[] = readData(pathXDawn);
			String data3 = "";
			for (int i = 0; i < 42; i++) {
				data3 += coffi3[i] + "\n";
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 14; j++) {
					coefficientsXdawn[i][j] = coffi3[i * 14 + j];
				}
			}
			readCoefficients(pathClassifier);
			mxDwanFilterState = false;
			mScenarios = getScenariosFlash();
			if (mTrail.size() > 0)
				mTrail.removeAll(mTrail);
			//Randomize character
			Random rand = new Random();
			rowNo = rand.nextInt(6);
			colNo = rand.nextInt(6);
			String character = mCharacter[rowNo][colNo];
		
			mTrail.add(character);
			// mTrail.add("V");
			// mTrail.add("G");
			// mTrail.add("O");
			mTrainning = false;
			mRun = true;
			mIntail = 0;
			mDownSampleRun = true;
			mStartProcess600 = 0;
			mNumberTrail = 0;
			mNumberFlash = -1;
			mFlash = false;

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				
					CaptureSignal();
					processDownSample();
					sendBroadcast(mFlash1);
					processFlash();

				}
			});

			//

			/*
			 * int size=mDownSampleData1.size(); double result[][]=new
			 * double[14][8*size];
			 * 
			 * //lay tin hieu for(int i=0;i<mDownSampleData1.size();i++) {
			 * double signalData[][]=new double[14][8];
			 * signalData=mDownSampleData1.get(i).getSignal(); for(int
			 * j=0;j<14;j++) { for(int k=0;k<8;k++) {
			 * result[j][i*8+k]=signalData[j][k]; } }
			 * 
			 * } double[][] afterfilter =
			 * XdawnTrainer.calculateXDawn(result,coef); //lay du lieu sau loc
			 * 
			 * String filterdata=""; if(!flashIndex.isEmpty()) {
			 * filterdata+=flashIndex.size()+" \n"; filterdata+="\n"; for(int
			 * i=0;i<flashIndex.size();i++) { int in=flashIndex.get(i); for(int
			 * j=0;j<3;j++) { for(int k=in;k<(in+19);k++)
			 * filterdata+=afterfilter[k][j]+"\n"; filterdata+="\n"; }
			 * filterdata+="hj\n"; }
			 * writeData("/sdcard/DongBo/ERPfilter.txt",filterdata); }
			 */
			// Toast.makeText(getApplicationContext(), "Online done", 0).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * loai bo tat ca cac du lieu con trong hang doi
	 */
	public void processRecycle() {
		if (!mBaseEpoch.isEmpty()) {
			mBaseEpoch.removeAll(mBaseEpoch);
		}
		if (!m600EpochData.isEmpty()) {
			m600EpochData.removeAll(m600EpochData);
		}
		if (!mTargetData.isEmpty()) {
			mTargetData.removeAll(mTargetData);
		}
		if (!mNonTargetData.isEmpty()) {
			mNonTargetData.removeAll(mNonTargetData);
		}
		if (!mStimulationData.isEmpty()) {
			mStimulationData.removeAll(mStimulationData);
		}
		if (!mStimulation.isEmpty()) {
			mStimulation.removeAll(mStimulation);
		}
		if (!mDownSampleData1.isEmpty()) {
			mDownSampleData1.removeAll(mDownSampleData1);
		}
		if (!mStimulationHistory.isEmpty()) {
			mStimulationHistory.removeAll(mStimulationHistory);
		}
		if (!mEpochArray.isEmpty()) {
			mEpochArray.removeAll(mEpochArray);
		}
	}

	//Display Target
	public void displayTaget(String text) {
		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_SHORT);
		LinearLayout toastLayout = (LinearLayout) toast.getView();
		TextView toastTV = (TextView) toastLayout.getChildAt(0);
		toastTV.setTextSize(30);
		toast.show();
	}

	public int[] random() {
		int output[] = { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 };
		int i = 0;

		while (true) {
			Random ran = new Random();
			int j = ran.nextInt(12);
			boolean test = false;
			for (int k = 0; k <= i; k++) {
				if (output[k] == j) {
					test = true;
					break;
				}

			}
			if (!test) {
				output[i] = j;
				i++;
			}
			if (i == 12)
				break;

		}
		return output;
	}

	public int[] getScenariosFlash() {
		int ouput[] = new int[144];
		for (int i = 0; i < 12; i++) {
			int a[] = random();
			for (int j = 0; j < 12; j++)
				ouput[i * 12 + j] = a[j];
		}
		return ouput;
	}

	public void onFlash() {
		startfash = System.currentTimeMillis();
		/*
		 * if(mRow) mRow=false; else mRow=true;
		 */
		final List<TextView> target = new ArrayList<TextView>();
		/*
		 * while(true){ Random ran=new Random(); int j=ran.nextInt(6); if(mRow)
		 * { if()
		 * 
		 * }else {
		 * 
		 * } }
		 */

		/*
		 * if (mRow) { index=j; for (int i = 0; i < 6; i++) {
		 * target.add(character.get(j * 6 + i)); } } else { index=j+6; for (int
		 * i = 0; i < 6; i++) {
		 * 
		 * target.add(character.get(j)); j=j+6;
		 * 
		 * } }
		 */

		//
		
		solanNhay++;
		int j = mScenarios[mNumberFlash];
		if (j < 6) {
			index = j;
			for (int i = 0; i < 6; i++) {
				target.add(character.get(j * 6 + i));
			}
		} else {
			index = j;
			j = j - 6;
			for (int i = 0; i < 6; i++) {

				target.add(character.get(j));
				j = j + 6;

			}
		}

		//
		
		
		// lay thoi gian
		double sth = 0;
		if (mNumberTrail < mTrail.size()) {
			if (mTrainning) {
				
				sth = System.currentTimeMillis() - mStartCaptuter;
				thoigianflash += sth + "\n";
				if (index == 2 || index == 8) {
					double lableStartTime = (double) System.currentTimeMillis();
					Stimulation stimulation = new Stimulation(lableStartTime,
							index, 1);
					mStimulationData.add(stimulation);
					// mStimulation.add(stimulation);

				} else {
					double nonLableSatrtTime = (double) System
							.currentTimeMillis();
					Stimulation stimulation1 = new Stimulation(
							nonLableSatrtTime, index, 0);
					mStimulationData.add(stimulation1);
					// mStimulation.add(stimulation1);
				}
			} else {
				double SatrtTime = (double) System.currentTimeMillis();
				Stimulation stimulation2 = new Stimulation(SatrtTime, index, 1);
				// mStimulationData.add(stimulation2);
				mStimulation.add(stimulation2);
				
				sth = System.currentTimeMillis() - mStartCaptuter;
				thoigianflash += sth + "\n";
				if ((index == rowNo) || (index == colNo + 6))
				{
					double labelStart = (double) System.currentTimeMillis();
					Stimulation stimulationTarget = new Stimulation(labelStart, index, 1);
					mStimulationData.add(stimulationTarget);
				}
				else
				{
					double nonlabelStart = (double) System.currentTimeMillis();
					Stimulation stimulationNonTarget = new Stimulation(nonlabelStart, index, 0);
					mStimulationData.add(stimulationNonTarget);
				}
			}
		}
		//
		// int mState=0;
		for (int i = 0; i < target.size(); i++) {
			final TextView tv = target.get(i);
			final ObjectAnimator animator = ObjectAnimator.ofInt(tv,
					"textColor", Color.rgb(70, 70, 70),
					Color.rgb(255, 255, 255));
			animator.setDuration(100);// 100ms
			animator.setEvaluator(new ArgbEvaluator());
			animator.setInterpolator(new DecelerateInterpolator(2));

			animator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {
					// test cho truong hop trainning
					//

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					tv.setTextColor(Color.rgb(70, 70, 70));
					endfash = System.currentTimeMillis() - startfash;
					Log.d("Fasl", endfash + "");
				}

				@Override
				public void onAnimationCancel(Animator arg0) {

				}
			});
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					animator.start();
				}
			});

		}
		//
		if (mNumberFlash == 143) {
			// mSateFlash=true;
			// khoi dong moi
			solanNhay=0;
			mStartProcess600 = 0;// test moi
			mFlash = false;
			mNumberFlash = 0;
			mNumberTrail++;
			if (mNumberTrail < mTrail.size()) {
				sendBroadcast(mFlash1);
			}
		}
		mNumberFlash++;

	}

	public void processFlash() {
		mNumberFlash = 0;
		// mSateFlash=false;

		flashTimer = new Timer();
		flashTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mFlash)
					onFlash();

				if (mNumberTrail == mTrail.size()) {

					flashTimer.purge();
					flashTimer.cancel();
					/*
					 * handler.post(new Runnable() {
					 * 
					 * @Override public void run() { // TODO Auto-generated
					 * method stub //Toast.makeText(getApplicationContext(),
					 * "finish flash", 0).show(); try { Thread.sleep(1000); }
					 * catch (InterruptedException e) { // TODO Auto-generated
					 * catch block e.printStackTrace(); } mRun=false; mIntail=0;
					 * mDownSampleRun=false; mDownSampleRun=false;
					 * drawTimer.purge(); drawTimer.cancel();
					 * 
					 * //processRecycle();
					 * Toast.makeText(getApplicationContext(), "out "+solanNhay,
					 * 0).show();
					 * 
					 * } });
					 */
				}

			}
		};
		flashTimer.schedule(flashTask, 0, 250);
	}

	public void processDownSample() {
		mStartDown = (double) System.currentTimeMillis();
		// mStartDown
		drawTimer = new Timer();
		drawTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (!mBaseEpoch.isEmpty()) {
					startdow = System.currentTimeMillis();
					double[][] sig = new double[14][32];
					double[][] filterdata = new double[14][32];
					double[][] down = new double[14][8];
					double start = 0, end = 0;
					BaseEpoch baseepoch = mBaseEpoch.poll();
					sig = baseepoch.getSignal();
					start = baseepoch.getStartTime();
					timeepoch += start + "\n";
					end = baseepoch.getEndTime();
					filterdata = onFilter2(sig);
					// tinh downsample
					int h = 0;
					// dowmsample tin hieu
					for (int k = 0; k < 14; k++) {
						h = 0;
						for (int l = 0; l < 8; l++) {
							double sum = 0;
							for (int jk = 0; jk < 4; jk++) {
								sum += filterdata[k][h];
								h++;
							}
							down[k][l] = sum / 4;

						}
					}
					// test Decimation
					/*
					 * for(int i=0;i<32;i++){ mBeforDow+=filterdata[4][i]+"\n";
					 * } for(int j=0;j<8;j++ ){ mAfterDow+=down[4][j]+ "\n"; }
					 */
					// /mDataFilter.add(down);
					// mDownSampleData.add(down);
					FilterEpoch filter = new FilterEpoch(start, end, down);
					// mDownSampleData1.add(filter);// cai nay cho xu ly
					// Xdawn,classifier
					// mEpochArray.add(filter);
					// sendBroadcast(mDecimation); tam dung de test loc va
					// decimation
					// enddow=System.currentTimeMillis()-startdow;

					// Log.d("Dow-Loc", enddow+"");

					// xu ly epoch600
					if (mTrainning) {
						mDownSampleData1.add(filter);
						mStartProcess600++;
						if (mStartProcess600 > 18)
							processEpoch600();
					} else {
						// if()
						if (mxDwanFilterState) {
							double[][] xdawnOput = xDawnSpatilFilter(down);
							queXdawn.add(xdawnOput);
							sendBroadcast(inXdawn);
						} else {
							double[][] xdawnOput = xDawnSpatilFilter(down);
							XdawnFilterEpoch mXdawnFilterEpoch1 = new XdawnFilterEpoch(
									start, end, xdawnOput);
							mEpochArray.add(mXdawnFilterEpoch1);
							mStartProcess600++;
						
							mDownSampleData1.add(filter);
							if (mStartProcess600 > 18)
							{
								processEpoch600_();
								processEpoch600();
							}
						
						}
					}
					enddow = System.currentTimeMillis() - startdow;
					Log.d("Dow-Loc", enddow + "");
				}

				// sendBroadcast(in);
			}
		};
		drawTimer.schedule(drawTask, 150, 15);

	}

	// bo loc xDAWN spatil Filter
	public double[][] xDawnSpatilFilter(double intput[][]) {
		double output[][] = new double[3][8];
		if (mXdawnIntail == 0) {
			mXdawnFilter = new XdawnSpatialFilter(8, coefficientsXdawn);
			output = mXdawnFilter.SpatialFilter(intput);
		} else {
			output = mXdawnFilter.SpatialFilter(intput);
		}
		return output;

	}

	// lay epoch 600 voi du lieu sau XDAWN Filter
	public void processEpoch600_() {
		if (mStimulation.size() > 0) {
			if (mEpochArray.size() > 3) {
				double time = 0, start = 0;
				Stimulation st = mStimulation.get(0);
				time = st.getTime() - mStartCaptuter;
				// xac dinh epoch chua thoi diem bat dau
				double data[][] = new double[3][32];
				double time1 = mEpochArray.get(0).getEndTime();
				Log.d("epoc600_ ", "tg bat dau " + time1);

				int index = (int) ((time - time1) / 250);
				
				Log.e("Index", index + "");
				if ((index + 3) < mEpochArray.size() && index > 0) {

					// cat doan du lieu chua 4 epoch lien tiep
					for (int i = 0; i < 4; i++) {
						double sample[][] = new double[3][8];
						sample = mEpochArray.get(index + i).getSignal();
						for (int j = 0; j < 3; j++)
							for (int k = 0; k < 8; k++)
								data[j][i * 8 + k] = sample[j][k];
					}
					// lay chi so cat
					start = mEpochArray.get(index).getStartTime();
					int dex = (int) ((time - start) / 31.25);
					double epoch600[][] = new double[3][19];
					for (int k = 0; k < 3; k++)
						for (int i = dex; i < (dex + 19); i++) {
							epoch600[k][i - dex] = data[k][i];
						}
					// lu thoi gian epoch 600
					double start600 = time;// st.getTime();//
					double end600 = start600 + 600;
					StimulationEpoch ep = new StimulationEpoch(start600,
							end600, epoch600);
					// the du lieu vao
					// m600EpochData.add(ep);
					double feature[] = new double[58];
					feature[57] = st.getIndex();
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 19; j++)
							feature[i * 19 + j] = epoch600[i][j];
					mFeature.add(feature);

					// long end1=System.currentTimeMillis()-st1;
					// Log.d("epoch600", end1+" ");
					int y = mEpochArray.size();
					int k = mStimulation.size();
					Log.d("epoch600", index + " " + y + " " + k);
					mStimulation.remove(0);
					mEpochArray.remove(0);

				}
			}
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// Toast.makeText(getApplicationContext(), "finish flash",
					// 0).show();
					
					final String featurePath = Environment.getExternalStorageDirectory().getPath()
							+File.separator + "eegkeyboard" + File.separator + "featureVectors.txt";
					
					String character = MainProcess
							.VotingProcess(mFeature,coefficients);
					Toast.makeText(getApplicationContext(),
							"Character result is:" + character,
							Toast.LENGTH_LONG).show();

					File featureFile = new File(featurePath);
					if (!featureFile.exists())
						try {
							featureFile.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					try {
						BufferedWriter buffer = new BufferedWriter(new FileWriter(featureFile));
						for (int i = 0; i < mFeature.size(); i++)
						{
							buffer.write("{");
							double[] f = mFeature.get(i);
							for (int j = 0; j < f.length - 1; j++)
							{
								buffer.write(String.valueOf(f[j]));
								buffer.write(",");
							}
							buffer.write("}");
							buffer.write("\n");
						}
						buffer.write("\n");
						buffer.write("{");
						for (int i = 0;i<mFeature.size(); i++)
						{
							double[] f = mFeature.get(i);
							buffer.write(String.valueOf((int)f[57]));
							buffer.write(",");
							
						}
						buffer.write("}");
						buffer.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mRun = false;
					mIntail = 0;
					mDownSampleRun = false;
					drawTimer.purge();
					drawTimer.cancel();

					
				//	Toast.makeText(getApplicationContext(), "out " + solanNhay,
				//			Toast.LENGTH_SHORT).show();

				}
			});
		}
	}

	public void processEpoch600() {
	
		if (mStimulationData.size() > 0) {
			long st1 = System.currentTimeMillis();
			if (mDownSampleData1.size() > 3) {
				double time = 0, start = 0;
				Stimulation st = mStimulationData.poll();
				time = st.getTime() - mStartCaptuter;
				// xac dinh epoch chua thoi diem bat dau
				double data[][] = new double[14][32];
				int index = (int) (time / 250);
				// int stSize=mStimulationData.size();
				// Log.d("chi so kt", stSize+" ");
				int size = mDownSampleData1.size();
				// Log.d("kt mdowsample", size+" ");
				double processTime = st1 - mStartCaptuter;
				// Log.d("so luong epoch",mEpochIndex+ "");
				// Log.d("thoi gian xu ly",processTime+"");
				// Log.d("thoi gian bt nhay", time+"");
				// Log.d("chi index", index+" ");
				//
				FilterEpoch filter = mDownSampleData1.get(index);
				start = filter.getStartTime();
				if ((index + 3) < mDownSampleData1.size()) {
					// cat doan du lieu chua 4 epoch lien tiep
					for (int i = 0; i < 4; i++) {
						double sample[][] = new double[14][8];
						sample = mDownSampleData1.get(index + i).getSignal();
						for (int j = 0; j < 14; j++)
							for (int k = 0; k < 8; k++)
								data[j][i * 8 + k] = sample[j][k];
					}
					// lay chi so cat
					
					int dex = (int) ((time - start) / 31.25);
					double epoch600[][] = new double[14][19];
					for (int k = 0; k < 14; k++)
						for (int i = dex; i < (dex + 19); i++) {
							epoch600[k][i - dex] = data[k][i];
						}
					// lu thoi gian epoch 600
					double start600 = time;// st.getTime();//
					double end600 = start600 + 600;
					_600Epoch ep = new _600Epoch(start600, end600, epoch600);
					if (mTrainning) {
						int i = st.getLable();
						if (i == 1) {
							mTargetData.add(ep);
						} else {
							mNonTargetData.add(ep);
						}
					} else {
						m600EpochData.add(ep);
						
						int i = st.getLable();
						
						if (i == 1) {
							mTargetData.add(ep);
						} else {
							mNonTargetData.add(ep);
						}
				
					}
					long end1 = System.currentTimeMillis() - st1;
					Log.d("epoch600", end1 + " ");
				}
			}
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// Toast.makeText(getApplicationContext(), "finish flash",
					// 0).show();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mRun = false;
					mIntail = 0;
					mDownSampleRun = false;
					drawTimer.purge();
					drawTimer.cancel();

					// processRecycle();
			//		Toast.makeText(getApplicationContext(), "out " + solanNhay,
			//				Toast.LENGTH_SHORT).show();

				}
			});
		}

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
		mRender1.setXAxisMax(512);
		mRender1.setYAxisMax(200);
		mRender1.setYAxisMin(-200);
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

	/*
	 * private void graph2Init() { mRender2.setApplyBackgroundColor(true);
	 * mRender2.setBackgroundColor(Color.argb(255, 30, 30, 30));
	 * mRender2.setAxisTitleTextSize(20); mRender2.setChartTitleTextSize(20);
	 * mRender2.setLabelsTextSize(20); mRender2.setLegendTextSize(22);
	 * mRender2.setMargins(new int[] { 10, 40, 10, 10 });
	 * mRender2.setZoomButtonsVisible(true); mRender2.setPointSize(2.0f);
	 * mRender2.setXAxisMin(0); mRender2.setXAxisMax(512);
	 * mRender2.setYAxisMax(100); mRender2.setYAxisMin(-30);
	 * mRender2.setXTitle("Time"); mRender2.setYTitle("Applifier"); if
	 * (mChartView2 == null) { LinearLayout layout = (LinearLayout)
	 * findViewById(R.id.grap2); mChartView2 = ChartFactory.getLineChartView(
	 * getApplicationContext(), mDataSet2, mRender2);
	 * mRender2.setClickEnabled(true); mRender2.setSelectableBuffer(10);
	 * mChartView2.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub
	 * 
	 * } }); layout.addView(mChartView2, new LinearLayout.LayoutParams(
	 * LinearLayout.LayoutParams.MATCH_PARENT,
	 * LinearLayout.LayoutParams.MATCH_PARENT)); } else { mChartView2.repaint();
	 * }
	 * 
	 * XYSeries series = new XYSeries("Filter signal");
	 * mDataSet2.addSeries(series); mCurrentSeries2 = series; XYSeriesRenderer
	 * renderer = new XYSeriesRenderer(); renderer.setColor(Color.YELLOW);
	 * mRender2.addSeriesRenderer(renderer);
	 * renderer.setPointStyle(PointStyle.POINT); renderer.setFillPoints(true);
	 * renderer.setLineWidth(2.0f);
	 * 
	 * mChartView2.repaint(); }
	 */
	public double[][] onFilter2(double[][] intput) {

		double[][] Oput = new double[NUMBER_CHANEL][NUMBER_SAMPLE_PER_CHANEL];

		if (mIntail == 0 && mDownSampleRun) {
			double[] heso = new double[NUMBER_CHANEL];
			for (int i = 0; i < NUMBER_CHANEL; i++)
				heso[i] = intput[i][0];

			filter = new TemporalFilter(heso);
			Oput = filter.onFilterMutilChanale(intput);
			final double jk = Oput[0][0];
			mIntail = 1;
			/*handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), jk + "",
							Toast.LENGTH_SHORT).show();

				}
			});*/
		} else {
			Oput = filter.onFilterMutilChanale(intput);

		}
		return Oput;

	}

	// thu tin hieu
	public void CaptureSignal() {
		// final ArrayList<Integer> arr=new ArrayList<Integer>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
				/*handler.post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								String.valueOf(check_open), Toast.LENGTH_SHORT)
								.show();
					}
				});*/
				if (check_open != 1)
					return;

				int x = 0, x1 = 0;
				double end = 0;
				double start = 0;
				mEpochIndex = 0;
				// mStartCaptuter=(double)System.currentTimeMillis();
				while (mRun) {
					int[] res = LibEmotiv.ReadRawData();
					byte[] data = int2byte(res);
					Emokit_Frame k = new Emokit_Frame();
					try {
						k = AES.get_data(AES.get_raw_unenc(data));

						m_signal[0][x] = k.AF3;
						m_signal[1][x] = k.F7;
						m_signal[2][x] = k.F3;
						m_signal[3][x] = k.FC5;
						m_signal[4][x] = k.T7;
						m_signal[5][x] = k.P7;
						m_signal[6][x] = k.O1;
						m_signal[7][x] = k.O2;
						m_signal[8][x] = k.P8;
						m_signal[9][x] = k.T8;
						m_signal[10][x] = k.FC6;
						m_signal[11][x] = k.F4;
						m_signal[12][x] = k.F8;
						m_signal[13][x] = k.AF4;

						x++;

						if (x == 32) {
							x = 0;
							if (x1 == 1) {
								double end1 = (double) System
										.currentTimeMillis() - mStartCaptuter;
								thoigianepoch += end1 + "\n";
								start = mEpochIndex * BETTWEN_EPOCH_TIME; // thoi
																			// gian
																			// bat
																			// dau
																			// epoch
								end = start + DURATION; // thoi gian ket thuc
								BaseEpoch epoch = new BaseEpoch(start, end,
										m_signal);
								mBaseEpoch.add(epoch);
								mEpochIndex++;
							} else {
								x1 = 1;
								mStartCaptuter = (double) System
										.currentTimeMillis();
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "finish ",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}).start();
	}

	//
	public void writeData(String path, String data) {
		String sdcard = path;// "/sdcard/DongBo/time.txt";//"/sdcard/dafiter.txt";
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(sdcard));
			writer.write(data);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double[] readData(String a) {
		double[] ar = new double[42];// 96
		String sdcard = a;// "/sdcard/eegkeyboard/coefficientsXdawn.txt";//"/sdcard/2dataF7_1.txt";
		try {
			Scanner scan = new Scanner(new File(sdcard));

			String c;
			int i = 0;
			while (scan.hasNext()) {
				c = scan.nextLine() + "";
				ar[i] = Double.parseDouble(c);
				i++;
			}
			scan.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ar;
	}
	
	public void readCoefficients (String path)
	{
		File coefFile = new File(path);
		try {
			BufferedReader br2 = new BufferedReader(new FileReader(coefFile));
			String a = "";
			int count = 0;
			while ((a = br2.readLine()) != null) {
				
				if ((a != null) && (!a.trim().equals(""))) {
					
						coefficients[count] = Double.parseDouble(a);
						count++;
				}
			}
			br2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double[] readSignal(String a, int size) {
		double[] ar = new double[size];// 96
		String sdcard = a;// "/sdcard/2dataF7_1.txt";
		try {
			Scanner scan = new Scanner(new File(sdcard));

			String c;
			int i = 0;
			while (scan.hasNext()) {
				c = scan.nextLine() + "";
				ar[i] = Double.parseDouble(c);
				i++;
			}
			scan.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ar;
	}

	// thiet lap cai dat de lay du lieu
	public byte[] int2byte(int[] src) {
		byte[] res = new byte[src.length];
		for (int i = 0; i < src.length; i++) {
			res[i] = (byte) src[i];
		}
		return res;
	}

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
}
