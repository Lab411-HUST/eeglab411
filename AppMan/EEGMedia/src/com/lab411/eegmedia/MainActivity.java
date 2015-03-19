package com.lab411.eegmedia;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnPreparedListener {

	private List<MediaItems> data = new ArrayList<MediaItems>();

	private ListView mList;
	private Adapter adapter;

	private MediaPlayer mMedia;
	public Button btn_Next;
	public Button btn_Prev;
	public Button btn_volume_up, btn_volume_down;
	public ToggleButton tbtn_Pause_Resume;
	private TextView tv_CurrentTime;
	private TextView tv_TotalTime;
	private SeekBar mSeek;
	private RatingBar rateBar;
	private boolean isPlay = true;
	private AppDatabase db;
	AudioManager audioManager;
	int actualVolume, maxVolume;
	public int rate;
	int[] Screenario;

	// EEGCapture:
	public ArrayList<Emokit_Frame> mSignal;
	public boolean run = true;

	public static final String GAZE_LEFT = "com.lab411.gazeleft";
	public static final String GAZE_RIGHT = "com.lab411.gazeright";
	public static final String GAZE_DOWN = "com.lab411.gazedown";
	public static final String EYE_BLINK = "com.lab411.eyeblink";
	public static final String EEG_DATA = "com.lab411.eegdata";

	public static final String ACTION = "com.lab411.emotional.action";
	public static final String ACTION_VALUE = "com.lab411.action_value";
	public static final String RATE = "com.lab411.rate";
	public static final String RATE_VALUE = "com.lab411.rate_value";
	public static final int ACTION_START = 1;
	public static final int ACTION_STOP = 0;

	Animation anicue_prev, anicue_next, anicue_pr, anicue_volumeUp,
			anicue_volumeDown;
	int idTab = -1;
	private Handler handler = new Handler();
	// Load BCI:
	static {
		System.loadLibrary("bci");
	}

	private native int[] BCI();

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(GAZE_LEFT)) {
				idTab = idTab - 1;
				if (idTab < 0) {
					idTab = 4;
				}
				// setAni(idTab);

			}
			if (action.equals(GAZE_RIGHT)) {
				idTab = idTab + 1;
				if (idTab > 4) {
					idTab = 0;
				}
				// setAni(idTab);

			}
			if (action.equals(GAZE_DOWN)) {
				// if (mMedia != null) {
				// int total = mMedia.getDuration();
				// int current = mMedia.getCurrentPosition();
				// current += 10;
				// if (current > total) {
				// current = total;
				// }
				// mMedia.seekTo(current);
				// }
				// onBackPressed();
			}
			if (action.equals(EYE_BLINK)) {
				// switch (idTab) {
				// case 0:
				// if(actualVolume>0)actualVolume--;
				// audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,actualVolume
				// , 0);
				// break;
				// case 1:
				// prev();
				// break;
				// case 2:
				// pause_resume();
				// break;
				// case 3:
				// next();
				// break;
				// case 4:
				// if(actualVolume<maxVolume)actualVolume++;
				// audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,actualVolume
				// , 0);
				// break;
				// }

			}

			if (action.equals(RATE)) {
				rate = intent.getIntExtra(RATE_VALUE, 0);
				Log.d("TEST  RECEIVE  RATE", "received" + rate);
				if (rate != 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							rateBar.setRating(rate);
						}
					});

				}
			}
		}
	};

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter(GAZE_DOWN));
		registerReceiver(receiver, new IntentFilter(GAZE_LEFT));
		registerReceiver(receiver, new IntentFilter(GAZE_RIGHT));
		registerReceiver(receiver, new IntentFilter(EYE_BLINK));
		registerReceiver(receiver, new IntentFilter(RATE));
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSignal = new ArrayList<Emokit_Frame>();
		// EEGCapture:
		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}

		btn_Next = (Button) findViewById(R.id.btn_next);
		btn_Prev = (Button) findViewById(R.id.btn_prev);
		btn_volume_down = (Button) findViewById(R.id.BtnVolumeDown);
		btn_volume_up = (Button) findViewById(R.id.BtnVolumeUp);
		tbtn_Pause_Resume = (ToggleButton) findViewById(R.id.tbtn_pause_resume);
		mList = (ListView) findViewById(R.id.list);
		tv_CurrentTime = (TextView) findViewById(R.id.tv_time);
		tv_TotalTime = (TextView) findViewById(R.id.tv_duration);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		Toast.makeText(this, "volume" + actualVolume + " max: " + maxVolume,
				Toast.LENGTH_LONG).show();
		;
		mSeek = (SeekBar) findViewById(R.id.seek);

		rateBar = (RatingBar) findViewById(R.id.ratingBar);
		rateBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float f, boolean b) {
				// TODO Auto-generated method stub
				MediaItems item = data.get(mList.getCheckedItemPosition());
				item.setRate((int) f);
				if (f == 0) {
					db.deleteSong(item.getName());
				} else if ((int) db.GetRate(item.getName()) == 0) {
					db.insertSong(item.getName(), (int) f);
				} else {
					db.updateSong(item.getName(), (int) f);
				}

			}
		});

		tv_CurrentTime.setText("00:00");
		tv_TotalTime.setText("00:00");

		// idTab = 0;
		anicue_pr = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		anicue_prev = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		anicue_next = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		anicue_volumeDown = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		anicue_volumeUp = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		// Start animation.
		// btn_volume_down.startAnimation(anicue_prev);
		// mMedia = new MediaPlayer();

		db = new AppDatabase(this);
		// db.insertSong("klsjdf", 4);
		// db.updateSong("klsjdf", 3);
		// db.deleteSong("klsjdf");
		// AudioManager
		// AuM=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

		// Toast.makeText(this,
		// "fail"+db.GetRate("klsjdf"),Toast.LENGTH_LONG).show();;
		btn_volume_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (actualVolume > 0)
					actualVolume--;
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						actualVolume, 0);
				// String command ="input keyevent ";
				// Runtime runtime =Runtime.getRuntime();
				// try {
				// runtime.exec(new String[] { "su", "-c", command +25});
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }

			}
		});
		btn_volume_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (actualVolume < maxVolume)
					actualVolume++;
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						actualVolume, 0);
				// TODO Auto-generated method stub
				// String command ="input keyevent ";
				// Runtime runtime =Runtime.getRuntime();
				// try {
				// runtime.exec(new String[] { "su", "-c", command +24});
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				//
			}
		});
		try {
			scanFile(new File("/sdcard/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(data.size());
		adapter = new Adapter(getApplicationContext(), 1, data);
		mList.setAdapter(adapter);

		mList.setItemChecked(0, true);
		rateBar.setRating(data.get(0).getRate());

		btn_Next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				idTab = 2;
				stopEmotinalService();
				next();
				startEmotinalService();
			}
		});

		btn_Prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				idTab = 0;
				stopEmotinalService();
				prev();
				startEmotinalService();
			}
		});

		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				stopEmotinalService();
				MediaItems item = (MediaItems) parent.getAdapter().getItem(pos);
				rateBar.setRating(item.getRate());
				Log.d("TEST_ITEM_CLICK", item.getLink() + " " + item.getRate());
				try {
					if (mMedia != null)
						mMedia.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					mMedia = new MediaPlayer();
					mMedia.setDataSource(item.getLink());
					mMedia.setOnPreparedListener(MainActivity.this);
					mMedia.prepareAsync();
					startEmotinalService();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		tbtn_Pause_Resume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				idTab = 1;
				if (tbtn_Pause_Resume.isChecked()) {
					if (mMedia == null) {
						try {
							mMedia = new MediaPlayer();
							mMedia.setDataSource(data.get(
									mList.getCheckedItemPosition()).getLink());
							mMedia.setOnPreparedListener(MainActivity.this);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						mMedia.prepareAsync();
						startEmotinalService();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						mMedia.pause();
						mMedia.stop();
						stopEmotinalService();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		new Thread(mCounter).start();

		mSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				try {
					if (mMedia != null) {
						mMedia.seekTo(seekBar.getProgress());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		new MenuInflater(this).inflate(R.menu.option, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.StartAni) {
			Run();
			// Start capture data:
			new EEGCapture().start();

		}
		if (item.getItemId() == R.id.StopAni) {

		}
		return super.onOptionsItemSelected(item);
	}

	private Vector<Integer> generateRun() {
		Vector<Integer> vec = new Vector<Integer>();
		Random random = new Random();
		int counter = 0;
		int oldIndex = -1;

		int[] max = new int[5];
		for (int i = 0; i < 5; i++) {
			max[i] = 29;
		}
		Screenario=new int[130];
		while (counter < 130) {
			int newIndex;
			do {
				// index 0 - 11
				newIndex = random.nextInt(5);

			} while (newIndex == oldIndex || max[newIndex] == 0);
			max[newIndex]--;
			oldIndex = newIndex;
			vec.add(newIndex);
			Screenario[counter]=newIndex;
			counter++;
			Log.d("SCREENARIO", "INDEX: "+newIndex);
		}
		return vec;
	}

	private void Run() {
		Vector<Integer> vec = generateRun();
		new Run(vec).start();
	}

	class Run extends Thread {

		private Vector<Integer> vec;

		public Run(Vector<Integer> vec) {
			this.vec = vec;
		}

		public void run() {
			if (vec.isEmpty()) {
				//Ghi file kich ban.
				writeData(Screenario, "script.txt");
				return;
			}
			int blinkIndex = vec.firstElement();
			// final int originalBlinkIndex = blinkIndex;
			// final List<Button> target = new ArrayList<Button>();

			final Button temp;
			final int BackgroundID;
			switch (blinkIndex) {

			case 0:
				temp = btn_volume_down;
				BackgroundID = R.drawable.volumedown;
				// animator = ObjectAnimator.ofInt(btn_volume_down,"textColor",
				// Color.WHITE);
				break;
			case 1:
				temp = btn_Prev;
				BackgroundID = R.drawable.button_prev_selector;
				// animator = ObjectAnimator.ofInt(btn_Prev,
				// "textColor", Color.WHITE);
				break;
			case 2:
				temp = tbtn_Pause_Resume;
				BackgroundID = R.drawable.togglebutton_start_pause_selector;
				// animator = ObjectAnimator.ofInt(tbtn_Pause_Resume,
				// "textColor", Color.WHITE);
				break;
			case 3:
				temp = btn_Next;
				BackgroundID = R.drawable.button_next_selector;
				// animator = ObjectAnimator.ofInt(btn_Next,
				// "textColor", Color.WHITE);
				break;
			case 4:
				temp = btn_volume_up;
				BackgroundID = R.drawable.volumeup;
				// animator = ObjectAnimator.ofInt(btn_volume_up,
				// "textColor", Color.WHITE);
				break;
			default:
				temp = tbtn_Pause_Resume;
				BackgroundID = R.drawable.button_next_selector;
			}
			// for (int i = 0; i < target.size(); i++) {
			// final TextView tv = target.get(i);
			// final int counter = i;

			final ObjectAnimator animator = ObjectAnimator.ofInt(temp,
					"backgroundColor", Color.rgb(0xff, 0xff, 0xff),
					Color.rgb(0x00, 0x00, 0x00));
			// "textColor", Color.WHITE);
			animator.setDuration(230L);
			animator.setEvaluator(new ArgbEvaluator());
			animator.setInterpolator(new DecelerateInterpolator(2));

			animator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					temp.setBackgroundResource(BackgroundID);

					vec.removeElementAt(0);
					new Run(vec).start();

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

	@Override
	protected void onDestroy() {
		isPlay = false;
		if (mMedia != null) {
			try {
				mMedia.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		stopEmotinalService();
		super.onDestroy();
	}

	private String toTime(int time) {
		time = time / 1024;
		int m = time / 60;
		int s = time % 60;
		String result = String.valueOf(m) + ":"
				+ String.valueOf(String.format("%02d", s));
		return result;
	}

	private Runnable mCounter = new Runnable() {

		@Override
		public void run() {
			try {
				while (isPlay) {
					if (mMedia != null) {
						final int total = mMedia.getDuration();
						final int current = mMedia.getCurrentPosition();
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_CurrentTime.setText(toTime(current));
								tv_TotalTime.setText(toTime(total));
								mSeek.setMax(total);
								mSeek.setProgress(current);
							}
						});
						Thread.sleep(1000);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private void next() {
		int index = mList.getCheckedItemPosition();
		index++;
		if (index >= mList.getCount()) {
			index = 0;
		}
		MediaItems item = data.get(index);
		try {
			if (mMedia != null)
				mMedia.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mMedia = new MediaPlayer();
			mMedia.setDataSource(item.getLink());
			mMedia.setOnPreparedListener(MainActivity.this);
			mMedia.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mList.setItemChecked(index, true);
		rateBar.setRating(item.getRate());
	}

	private void prev() {
		int index = mList.getCheckedItemPosition();
		index--;
		if (index < 0) {
			index = data.size() - 1;
		}
		MediaItems item = data.get(index);
		try {
			if (mMedia != null)
				mMedia.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mMedia = new MediaPlayer();
			mMedia.setDataSource(item.getLink());
			mMedia.setOnPreparedListener(MainActivity.this);
			mMedia.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mList.setItemChecked(index, true);
		rateBar.setRating(item.getRate());
	}

	private void pause_resume() {
		tbtn_Pause_Resume.setChecked(!tbtn_Pause_Resume.isChecked());
		if (tbtn_Pause_Resume.isChecked()) {
			if (mMedia == null) {
				try {
					mMedia = new MediaPlayer();
					mMedia.setDataSource(data.get(
							mList.getCheckedItemPosition()).getLink());
					mMedia.setOnPreparedListener(MainActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				mMedia.prepareAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				mMedia.pause();
				mMedia.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void scanFile(File dir) {
		try {
			File files[] = dir.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					if (file.getName().endsWith(".mp3")
							|| file.getName().endsWith(".MP3")
							|| file.getName().endsWith(".WMA")
							|| file.getName().endsWith(".wma")) {
						MediaItems items = new MediaItems(file.getName(),
								file.getPath(), db.GetRate(file.getName()));// rate
																			// 0
																			// start
																			// if
																			// this
																			// song
																			// is
																			// new
						data.add(items);
						System.out.println(file.getName());
					}
				} else {
					scanFile(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		tbtn_Pause_Resume.setChecked(true);
		isPlay = true;
	}

	public void setAni(int view) {
		switch (view) {
		case 1:
			btn_Prev.startAnimation(anicue_prev);
			btn_volume_down.clearAnimation();
			tbtn_Pause_Resume.clearAnimation();
			break;
		case 2:
			tbtn_Pause_Resume.startAnimation(anicue_pr);
			btn_Prev.clearAnimation();
			btn_Next.clearAnimation();
			break;
		case 3:
			btn_Next.startAnimation(anicue_next);
			btn_volume_up.clearAnimation();
			tbtn_Pause_Resume.clearAnimation();
			break;
		case 4:
			btn_volume_up.startAnimation(anicue_next);
			btn_Next.clearAnimation();
			btn_volume_down.clearAnimation();
			break;
		case 0:
			btn_volume_down.startAnimation(anicue_next);
			btn_Prev.clearAnimation();
			btn_volume_up.clearAnimation();
			break;
		}
	}

	private void startEmotinalService() {
		Intent intent = new Intent(ACTION);
		intent.putExtra(ACTION_VALUE, ACTION_START);
		getApplicationContext().sendBroadcast(intent);
		Log.d("TEST_START_SERVICE", "Start");
	}

	private void stopEmotinalService() {
		Intent intent = new Intent(ACTION);
		intent.putExtra(ACTION_VALUE, ACTION_STOP);
		getApplicationContext().sendBroadcast(intent);
		Log.d("TEST_STOP_SERVICE", "Stop");
	}

	/**
	 * EEGCapture Thread:
	 */

	private class EEGCapture extends Thread {
		int index = 0;
		int result[] = new int[8];
		int windowsize =512*8;
		// 8 channels:
		int AF3[] = new int[windowsize];
		int AF4[] = new int[windowsize];
		int F3[] = new int[windowsize];
		int F4[] = new int[windowsize];
		int P7[] = new int[windowsize];
		int P8[] = new int[windowsize];
		int O1[] = new int[windowsize];
		int O2[] = new int[windowsize];
		int signal[] = new int[6 + windowsize*8 + 80];
		int weight[] = { 882, 735, -247, -819, -135, 998, 1729, 1718, 752,
				-960, -2431, -2967, -2971, -2842, -2301, -1280, -615, -684,
				-367, 693, 1081, 293, -708, -1606, -2341, -2051, -711, 611,
				1449, 1576, 738, -308, -579, -430, -876, -1502, -989, -44,
				-338, -821, 488, 2350, 2367, 968, -369, -1429, -2069, -2128,
				-1971, -1825, -1763, -2199, -3904, -6466, -7226, -4831, -2180,
				-2277, -4088, -5139, -4727, -3256, -1674, -1284, -1886, -2231,
				-2256, -2035, -882, 645, 686, -855, -1616, -684, -12, -504,
				-949, -965, -1137, -1338 };

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {

			int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			Log.i("TAG", "Check co open duoc ko : " + check_open);
			if (check_open != 1)
				return;
			Log.d("TAG", "Writing FIR value");
			signal[0] = -4000;
			signal[1] = -3581;
			signal[2] = -3025;
			signal[3] = -1972;
			signal[4] = -974;
			signal[5] = -394;
			for (int i = 6 + windowsize*8; i < signal.length; i++) {
				signal[i] = weight[i - signal.length + 80];
			}
			Log.d("TAG", "Start Capture data");
			while (true) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));

					/*
					 * if (start < 128) { start++; continue; }
					 */
					Log.d("TAG", mSignal.size() + "");
					if (mSignal.size() < windowsize) {
						mSignal.add(k);
					} else {
						Log.d("TAG", index + "");
					//	if (index < 80) {
							/*index++;
							mSignal.remove(0);
							mSignal.add(k);*/
						//} else {
						//	index = 0;
							//Log.d("TAG", "Start save data");
							for (int i = 0; i < windowsize; i++) {
								AF3[i] = mSignal.get(i).AF3;
								AF4[i] = mSignal.get(i).AF4;
								F3[i] = mSignal.get(i).F3;
								F4[i] = mSignal.get(i).F4;
								P7[i] = mSignal.get(i).P7;
								P8[i] = mSignal.get(i).P8;
								O1[i] = mSignal.get(i).O1;
								O2[i] = mSignal.get(i).O2;
							}
							//Save data to file:
							writeData(AF3, "AF3.txt");
							writeData(AF4, "AF4.txt");
							writeData(F3, "F3.txt");
							writeData(F4, "F4.txt");
							writeData(P7, "P7.txt");
							writeData(P8, "P8.txt");
							writeData(O1, "O1.txt");
							writeData(O2, "O2.txt");
							Log.d("TAG", "Start merger data");
							for (int j = 6; j < signal.length - 80; j++) {
								if (j < 6+ windowsize) {
									signal[j] = AF3[j - 6];
								} else if (j < 6 + 2*windowsize) {
									signal[j] = AF4[j - 6 - windowsize];
								} else if (j < 6 + 3*windowsize) {
									signal[j] = F3[j - 6 -2*windowsize];

								} else if (j < 6+ 4 * windowsize) {
									signal[j] = F4[j -6- 3 * windowsize];
								} else if (j < 6+ 5 * windowsize) {
									signal[j] = P7[j - 6- 4 * windowsize];
								} else if (j < 6+ 6 * windowsize) {
									signal[j] = P8[j - 6-5 * windowsize];

								} else if (j < 6+ 7 * windowsize) {
									signal[j] = O1[j - 6- 6 * windowsize];
								} else if (j < 6+ 8 * windowsize) {
									signal[j] = O2[j - 6 - 7 * windowsize];
								}
							}
							Log.d("TAG", "Save to file");
							// Start BCIBench.
							int o = writeData(signal);
							if (o != -1) {
								result = BCI();
								
								for (int i = 0; i < 8; i++) {
									Log.d("TAG", result[i] + "/n");
								}
								writeData(result, "result.txt");
								
							} else {
								Log.d("TAG", "Loi ghi file");
							}
							
							Log.d("TAG", "Capture Finish");
							//float endtime = System.currentTimeMillis();
							break;//Dung thu khi xu ly xong.
						//}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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

	public int writeData(int[] a) {
		int o = 0;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("/sdcard/P3000/P300_data.txt", false);
			PrintWriter pw = new PrintWriter(fos);
			for (int i = 0; i < a.length; i++) {
				pw.println(a[i]);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			o = -1;
		}
		return o;

	}

	public int writeData(int[] a, String filename) {
		int o = 0;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("/sdcard/P3000/"+filename, false);
			PrintWriter pw = new PrintWriter(fos);
			for (int i = 0; i < a.length; i++) {
				pw.println(a[i]);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			o = -1;
		}
		return o;

	}
}

class Adapter extends ArrayAdapter<MediaItems> {

	private LayoutInflater inflater;

	public Adapter(Context context, int resource, List<MediaItems> objects) {
		super(context, resource, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class Holder {
		TextView tv_Label;
		TextView tv_Name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.itemslist, null);
			Holder holder = new Holder();
			holder.tv_Label = (TextView) view.findViewById(R.id.tv_label);
			holder.tv_Name = (TextView) view.findViewById(R.id.tv_name);
			view.setTag(holder);
		}
		Holder holder = (Holder) view.getTag();
		holder.tv_Label.setText(String.valueOf(position + 1));
		holder.tv_Name.setText(getItem(position).getName());
		return view;
	}

}
