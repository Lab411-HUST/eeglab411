package com.htd.hust.coadaptp300;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.example.heso.Filter;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String[] mData = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "", "7", "8", "9", "", "", ".", "?", ",",
			"", "", "4", "5", "6", "-", "", "@", "", "$", "", "0", "1", "2",
			"3" };

	private GridAdapter mAdapter;
	private LayoutInflater inflater;
	private TableLayout parent;

	private List<TextView> mCharacter;

	private Handler handler = new Handler();

	public static final String FILE_WRITE = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "data_write.txt";

	public static final String CALL_FILTER = "com.example.heso.filter";

	double[] heso = new double[18];
	String datain = "";
	int index = 0;

	double x=0;
	double[] arr=new double[32];
	Intent in=new Intent("com.lab411.loc");
	public int mIntail=0;
	Filter k;
	double[] hj=new double[8];
	boolean check=true;
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

	private boolean isRun = true;
	private int timer = 0;
	private List<Emokit_Frame> signal;
	private List<Position> position;

	/* Thu tin hieu pha training */
	class CaptureSignal extends Thread {

		public CaptureSignal() {
			signal = new ArrayList<Emokit_Frame>();
			position = new ArrayList<Position>();
			timer = 0;
			isRun = true;
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							String.valueOf(check_open), 0).show();
				}
			});
			if (check_open != 1)
				return;
			while (isRun) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();
				
				try {
					k = AES.get_data(AES.get_raw_unenc(data));

					WriteFile(k);
					if (signal.size() >= 32) {
						signal.remove(0);
						signal.add(k);
						index++;
					}
					else
					{
					signal.add(k);
					index++;
					}
			
					if (index % 32 == 0) {
						// Do Filter
						double a[] = new double[32];
						
						for (int i = 0; i < signal.size(); i++) {
							a[i] = (double) signal.get(i).FC6;
							Log.e("a[o]", a[0]+"");
						}
						
						String s="";
						int[] arrfilter = onProcessFilter(a);
						for(int i=0;i<32;i++){

							 s += arrfilter[i] + "\n";
							
			                }
						datain = s;
						writeData();
						Toast.makeText(getApplicationContext(), "fish", 0)
								.show();

					}

					System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}
				timer++;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		parent = (TableLayout) findViewById(R.id.parent);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mAdapter = new GridAdapter(MainActivity.this, 1, mData);

		mCharacter = new ArrayList<TextView>();

		heso = com.example.heso.MainActivity.heso();

		Vector<Integer> temp = generateTrain(5);
		for (int i = 0; i < temp.size(); i++) {
			System.out.println("Generate: " + temp.get(i).intValue());
		}

		layoutInit();
		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void layoutInit() {
		for (int i = 0; i < 5; i++) {
			View view = inflater.inflate(R.layout.row, null);
			TableRow row = (TableRow) view.findViewById(R.id.row);
			for (int j = 0; j < 10; j++) {
				RelativeLayout rl = (RelativeLayout) inflater.inflate(
						R.layout.relativelayout, null);

				TextView tv = new TextView(MainActivity.this);
				tv.setTextSize(46);
				tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				tv.setTextColor(Color.rgb(70, 70, 70));
				tv.setGravity(Gravity.CENTER);
				tv.setText(mData[i * 10 + j]);
				TableRow.LayoutParams params = new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT, 1);

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				rl.addView(tv, lp);

				row.addView(rl, params);
				mCharacter.add(tv);
			}
			parent.addView(row);
		}
	}

	/* Random ra kich ban flash training */
	private Vector<Integer> generateTrain(int index) {
		Vector<Integer> vec = new Vector<Integer>();
		Random random = new Random();
		int counter = 0;
		int oldIndex = -1;
		while (counter < 100) {
			int newIndex;
			do {
				// index 0 - 14
				newIndex = random.nextInt(20);
				if (newIndex > 14)
					newIndex = index;

			} while (newIndex == oldIndex);
			oldIndex = newIndex;

			vec.add(newIndex);
			if (newIndex == index || newIndex - 5 == index) {
				if (newIndex == index)
					vec.add(newIndex + 5);
				else
					vec.add(newIndex - 5);
			} else {

			}
			counter++;
		}

		return vec;
	}

	/* Tu kich ban da co thuc hien training */
	class Train extends Thread {

		private int trainIndex;
		private Vector<Integer> vec;

		public Train(Vector<Integer> vec, int trainIndex) {
			this.trainIndex = trainIndex;
			this.vec = vec;
		}

		public void run() {
			if (vec.isEmpty()) {
				// Write File
				// Return file

				return;
			}
			int blinkIndex = vec.firstElement();
			final int originalBlinkIndex = blinkIndex;
			final List<TextView> target = new ArrayList<TextView>();
			if (blinkIndex < 10) {
				// Cols blink
				for (int j = 0; j < 5; j++) {
					target.add(mCharacter.get(j * 10 + blinkIndex));
				}

			} else {
				// Row blink
				// blinkIndex -= 5;
				System.out.println("AAAAAAAAAA: " + blinkIndex);
				for (int i = 0; i < 10; i++) {
					target.add(mCharacter.get((blinkIndex - 10) * 10 + i));
				}
			}
			for (int i = 0; i < target.size(); i++) {
				final TextView tv = target.get(i);
				final int counter = i;

				final ObjectAnimator animator = ObjectAnimator.ofInt(tv,
						"textColor", Color.WHITE);
				animator.setDuration(80L);
				animator.setEvaluator(new ArgbEvaluator());
				animator.setInterpolator(new DecelerateInterpolator(2));

				animator.addListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator arg0) {
						if (counter == target.size() / 2) {
							if (originalBlinkIndex == trainIndex
									|| originalBlinkIndex - 5 == trainIndex) {
								position.add(new Position("Target", timer));
							} else {
								position.add(new Position("Non-Target", timer));
							}
							// System.out.println(timer + "s");
						}
					}

					@Override
					public void onAnimationRepeat(Animator arg0) {
					}

					@Override
					public void onAnimationEnd(Animator arg0) {
						tv.setTextColor(Color.rgb(70, 70, 70));
						if (counter == target.size() - 1) {
							vec.removeElementAt(0);
							try {
								Thread.sleep(70L);
								new Train(vec, trainIndex).start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onAnimationCancel(Animator arg0) {

					}
				});

				handler.post(new Runnable() {

					@Override
					public void run() {
						animator.start();
					}
				});
			}

		}
	}

	public void WriteFile(Emokit_Frame k) throws IOException {
		File file = new File(FILE_WRITE);

		if (!file.exists())
			file.createNewFile();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));

		String s = k.F3 + " " + k.FC6 + " " + k.P7 + " " + k.T8 + " " + k.F7
				+ " " + k.F8 + " " + k.T7 + " " + k.P8 + " " + k.AF4 + " "
				+ k.F4 + " " + k.AF3 + " " + k.O2 + " " + k.O1 + " " + k.FC5
				+ "\n";
		bw.write(s);

		bw.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.mi_train:
			new Train(generateTrain(8), 8).start();
			new CaptureSignal().start();
			return true;
		case R.id.mi_run:
			return true;

		case R.id.mi_display:
			startActivity(new Intent(CALL_FILTER));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class Position {

		private String name; // Target OR Non-Target
		private int start; // Thoi gian bat dau Eproc

		private int index; // Vi tri hang or cot

		public Position(String name, int start) {
			this.name = name;
			this.start = start;
		}

		public Position(String name, int start, int index) {
			this.name = name;
			this.start = start;
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public int getStart() {
			return start;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	public void writeData() throws IOException {
		String sdcard = "/sdcard/dataafterfilter.txt";
		File file = new File(sdcard);
		if (!file.exists())
			file.createNewFile();
		try {
		/*	OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(sdcard,true));*/
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(sdcard),true));
			writer.write(datain);
			writer.write("\n");
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
			   k=new Filter(1,32,c);
				hj=k.getcur();
				output=com.example.heso.MainActivity.signal(input,true,hj);
				mIntail=1;
		   }
		   else{
			   output=com.example.heso.MainActivity.signal(input,false,hj);
		   }
		   for(int t=0;t<32;t++)
				a[t]=(int)output[t];
		  for(int t=32;t<40;t++)
				hj[t-32]=output[t];
		  return a;
	   }
	   
	   
	   @Override
	protected void onStop() {
		// TODO Auto-generated method stub
			check=false;
			mIntail=0;
		   super.onStop();
	}
}
