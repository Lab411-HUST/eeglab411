//package lab411.eeg.emotionalservice;
//
//import java.io.DataOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import lab411.eeg.emotionalservice.EmotionalService.EmoBinder;
//import lab411.eeg.emotiv.AES;
//import lab411.eeg.emotiv.Emokit_Frame;
//import lab411.eeg.emotiv.LibEmotiv;
//import android.app.Activity;
//import android.app.Service;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class MainActivity extends Activity {
//
//	private Button btnRunning;
//	private Button btnTraining;
//	private TextView tv_Status;
//
//	public List<Emokit_Frame> mSignal;
//	public ArrayList<Double> hfdArr, hfdAveArr;
//	boolean run = true;
//	private boolean mBound;
//	private EmotionalService eService;
//
//	private ServiceConnection mConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			mBound = false;
//			tv_Status.setText("Service Disconnected!");
//		}
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			EmoBinder binder = (EmoBinder) service;
//			eService = binder.getInstance();
//			mBound = true;
//			tv_Status.setText("Service Connected!");
//		}
//	};
//
//	@Override
//	protected void onDestroy() {
//		if (mBound) {
//			unbindService(mConnection);
//			mBound = false;
//		}
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		List<String> cmds = new ArrayList<String>();
//		cmds.add("chmod 777 /dev/hidraw1");
//		SharedPreferences pref = PreferenceManager
//				.getDefaultSharedPreferences(getApplicationContext());
//
//		try {
//			doCmds(cmds);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		tv_Status = (TextView) findViewById(R.id.tv_status);
//		btnRunning = (Button) findViewById(R.id.btn_running);
//		btnRunning.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent service = new Intent(getBaseContext(),
//						EmotionalService.class);
//				getBaseContext().bindService(service, mConnection,
//						Service.BIND_AUTO_CREATE);
//			}
//		});
//
//		btnTraining = (Button) findViewById(R.id.btn_training);
//		btnTraining.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				new EEGCapture().start();
//
//			}
//		});
//	}
//
//	public void doCmds(List<String> cmds) throws Exception {
//
//		Process process = Runtime.getRuntime().exec("su");
//		DataOutputStream os = new DataOutputStream(process.getOutputStream());
//
//		for (String tmpCmd : cmds) {
//			os.writeBytes(tmpCmd + "\n");
//		}
//
//		os.writeBytes("exit\n");
//		os.flush();
//		os.close();
//
//		process.waitFor();
//
//	}
//
//	private class EEGCapture extends Thread {
//		int start = 0;
//		int windowsize = 1024;
//		int overlap = 0;
//		int number = 0;
//
//		public byte[] int2byte(int[] src) {
//			byte[] res = new byte[src.length];
//			for (int i = 0; i < src.length; i++) {
//				res[i] = (byte) src[i];
//			}
//			return res;
//		}
//
//		public void run() {
//			Log.d("TAG", "Start");
//			int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
//			Log.i("TAG", "Check co open duoc ko : " + check_open);
//			if (check_open != 1)
//				return;
//			mSignal = new ArrayList<Emokit_Frame>();
//			hfdArr = new ArrayList<Double>();
//			hfdAveArr = new ArrayList<Double>();
//
//			while (run) {
//				if (number <= 128 * 30) {
//					number++;
//					Log.d("TAG", "Start capture data: " + number);
//					int[] res = LibEmotiv.ReadRawData();
//					byte[] data = int2byte(res);
//					Emokit_Frame k = new Emokit_Frame();
//					try {
//						k = AES.get_data(AES.get_raw_unenc(data));
//						// Bo 128 mau dau.
//						if (start < 128) {
//							start++;
//							continue;
//						}
//						if (mSignal.size() < windowsize) {
//							mSignal.add(k);
//						} else {
//							if (overlap < 10) {
//								overlap++;
//								mSignal.remove(0);
//								mSignal.add(k);
//							} else {
//								overlap = 0;
//								// new EEGHandling().start();
//								int signalFC6[] = new int[mSignal.size()];
//								for (int i = 0; i < 1024; i++) {
//									signalFC6[i] = mSignal.get(i).FC6;
//								}
//								double[] signalFC6_filter = new double[signalFC6.length];
//								signalFC6_filter = Calculate.getYnFilter(
//										signalFC6, signalFC6.length, 1024,
//										2 * Math.PI * 2 / 128,
//										2 * Math.PI * 30 / 128, 1.5);
//
//								double hfd_value = Calculate.gethfd(
//										signalFC6_filter,
//										signalFC6_filter.length);
//
//								hfdArr.add(hfd_value);
//								int size = hfdArr.size();
//								if (size == 10) {
//									double hfdAve = 0.0;
//									for (int i = 0; i < 10; i++) {
//										hfdAve += hfdArr.get(i);
//									}
//									// Log.d("TAG", hfdAve + " ");
//									hfdAve = hfdAve / size;
//									hfdAveArr.add(hfdAve);
//									hfdArr.clear();
//								}
//							}
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				} else {
//					Log.d("TAG", "Write preferrences");
//					number = 0;
//					if (hfdAveArr.size() > 1) {
//						SharedPreferences pref = PreferenceManager
//								.getDefaultSharedPreferences(getApplicationContext());
//						Editor edit = pref.edit();
//						edit.putFloat("Min", (float) min(hfdAveArr));
//						edit.putFloat("Max", (float) max(hfdAveArr));
//						edit.commit();
//						Log.d("TAG", "Finish preferrences");
//						break;
//					}
//				}
//
//			}
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	public double min(ArrayList<Double> in) {
//		double min = in.get(0);
//		for (int i = 0; i < in.size(); i++) {
//			if (in.get(i) <= min) {
//				min = in.get(i);
//			}
//		}
//		return min;
//	}
//
//	public double max(ArrayList<Double> in) {
//		double max = in.get(0);
//		for (int i = 0; i < in.size(); i++) {
//			if (in.get(i) >= max) {
//				max = in.get(i);
//			}
//		}
//		return max;
//	}
//
//}
