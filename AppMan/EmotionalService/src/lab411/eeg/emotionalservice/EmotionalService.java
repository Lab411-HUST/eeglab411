package lab411.eeg.emotionalservice;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class EmotionalService extends Service {
	public static final String ACTION = "com.lab411.emotional.action";
	public static final String ACTION_VALUE = "com.lab411.action_value";
	public static final String RATE = "com.lab411.rate";
	public static final String RATE_VALUE = "com.lab411.rate_value";
	public static final String CON_VALUE = "com.lab411.con_value";
	public static final int ACTION_START = 1;
	public static final int ACTION_STOP = 0;

	private boolean run = true;

	private final IBinder mBinder = new EmoBinder();

	public Vector<Emokit_Frame> mSignal;
	public Vector<Double> hfdArr,conArr;

	public int rate;
	public double con_rate;
	public int index;
	public int start;
	int windowsize = 128;
	public class EmoBinder extends Binder {
		public EmotionalService getInstance() {
			return EmotionalService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(receiver, new IntentFilter(ACTION));
		mSignal = new Vector<Emokit_Frame>();
		hfdArr = new Vector<Double>();
		conArr = new Vector<Double>();
		index = 0;
		start = 0;
		rate = 0;
		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		run = false;
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		run = false;
		return super.onUnbind(intent);
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

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION)) {
				int actions = intent.getIntExtra(ACTION_VALUE, -1);
				if (actions == ACTION_START) {
					Log.d("GET  ACTION  START", "get action start");
					run = true;
					new EEGCapture().start();
				}
				if (actions == ACTION_STOP) {
					Log.d("GET  ACTION  STop", "get action stop");
					run = false;
					mSignal.clear();
					hfdArr.clear();
				}
			}
		}

	};

	private class EEGCapture extends Thread {
		

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

			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();
				try {
					k = AES.get_data(AES.get_raw_unenc(data));

					if (start < 128) {
						start++;
						continue;
					}

					if (mSignal.size() < windowsize) {
						mSignal.add(k);
					} else {
						if (index < 64) {
							index++;
							mSignal.remove(0);
							mSignal.add(k);
						} else {
							index = 0;
							if (run) {
								startCalculate();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void startCalculate() {
		int signalFC6[] = new int[mSignal.size()];
		int signalAF3[] = new int[mSignal.size()];
		for (int i = 0; i < windowsize; i++) {
			signalFC6[i] = mSignal.get(i).FC6;
			signalAF3[i] = mSignal.get(i).AF3;
		}
		double[] signalFC6_filter = new double[signalFC6.length];
		double[] signalAF3_theta = new double[signalAF3.length];
		double[] signalAF3_beta = new double[signalAF3.length];
		signalFC6_filter = Calculate.getYnFilter(signalFC6, signalFC6.length,
				1024, 2 * Math.PI * 2 / 128, 2 * Math.PI * 30 / 128, 1.5);
		signalAF3_theta = Calculate.getYnFilter(signalFC6, signalFC6.length,
				1024, 2 * Math.PI * 4 / 128, 2 * Math.PI * 7 / 128, 1.5);
		signalAF3_beta = Calculate.getYnFilter(signalFC6, signalFC6.length,
				1024, 2 * Math.PI * 7 / 128, 2 * Math.PI * 20 / 128, 1.5);
		double hfd_value = Calculate.gethfd(signalFC6_filter,
				signalFC6_filter.length);
		double con_point = Calculate.calcPower(
				signalAF3_beta, signalAF3_beta.length)
				/ Calculate.calcPower(signalAF3_theta,
						signalAF3_theta.length);
		conArr.add(con_point);
		hfdArr.add(hfd_value);
		int size = hfdArr.size();
		if ((size % 10) == 0) {
			double hfdAve = 0.0;
			double conAve = 0.0;
			for (int i = 0; i < size; i++) {
				hfdAve += hfdArr.get(i);
				conAve += conArr.get(i);
			}
			conAve = conAve / size;
			hfdAve = hfdAve / size;
			
			Log.d("TAG", "Emotional = " + hfdAve + " ");
			Log.d("TAG", "Concentration = " + conAve + " ");
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			float min = pref.getFloat("Min", (float) 1.7);
			float max = pref.getFloat("Max", (float) 2.0);
			float con = pref.getFloat("Con", 0);
			con_rate = conAve/con;
			if (hfdAve < min) {
				rate = 1;
			}
			if ((hfdAve >= min) && (hfdAve <= max)) {
				rate = (int) (5 * (hfdAve - min) / (max - min));

			}
			if (hfdAve > max) {
				rate = 5;
			}
			Intent intent = new Intent(RATE);
			intent.putExtra(RATE_VALUE, rate);
			intent.putExtra(CON_VALUE, con_rate);
			getApplicationContext().sendBroadcast(intent);
			Log.d("TEST  SEND  RATE  VALUE", "sended " + rate);
		}
	}
}
