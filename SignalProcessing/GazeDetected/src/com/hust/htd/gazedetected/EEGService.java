package com.hust.htd.gazedetected;

import java.util.ArrayList;
import java.util.List;

import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class EEGService extends Service {

	public static final String GAZE_LEFT = "com.lab411.gazeleft";
	public static final String GAZE_RIGHT = "com.lab411.gazeright";
	public static final String GAZE_DOWN = "com.lab411.gazedown";
	public static final String EYE_BLINK = "com.lab411.eyeblink";
	public static final String EEG_DATA = "com.lab411.eegdata";

	private boolean run = true;

	public List<Emokit_Frame> mSignal;
	public int index;
	public int start;
	public int rehandling;

	public class EEGBinder extends Binder {
		public EEGService getInstance() {
			return EEGService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		new EEGCapture().start();
		return new EEGBinder();
	}

	@Override
	public void onCreate() {
		mSignal = new ArrayList<>();
		index = 0;
		start = 0;
		rehandling = 300;
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		run = false;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		run = false;
		return super.onUnbind(intent);
	}

	private class EEGCapture extends Thread {

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public int[] toArray(Emokit_Frame k) {
			int[] result = new int[14];
			result[0] = k.F3;
			result[1] = k.FC6;
			result[2] = k.P7;
			result[3] = k.T8;
			result[4] = k.F7;
			result[5] = k.F8;
			result[6] = k.T7;
			result[7] = k.P8;
			result[8] = k.AF4;
			result[9] = k.F4;
			result[10] = k.AF3;
			result[11] = k.O2;
			result[12] = k.O1;
			result[13] = k.FC5;
			return result;
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

					Intent intent = new Intent(EEG_DATA);
					intent.putExtra("data", toArray(k));
					getApplicationContext().sendBroadcast(intent);

					if (start < 128) {
						start++;
						continue;
					}

					if (mSignal.size() < 128) {
						mSignal.add(k);
					} else {
						if (index < 10) {
							index++;
							mSignal.remove(0);
							mSignal.add(k);
						} else {
							index = 0;
							if (rehandling > 256) {
								// HANDLER
								new EEGHandling().start();
							} else {
								rehandling++;
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Handler handler = new Handler();

	class EEGHandling extends Thread {
		@Override
		public void run() {
			int sum1F8 = 0;
			int sum2F8 = 0;
			int sum3F8 = 0;
			for (int i = 0; i < 32; i++) {
				sum1F8 += mSignal.get(i).F8;
			}
			for (int i = 32; i < 96; i++) {
				sum2F8 += mSignal.get(i).F8;
			}
			for (int i = 96; i < mSignal.size(); i++) {
				sum3F8 += mSignal.get(i).F8;
			}

			float avg1F8 = sum1F8 / 32;
			float avg2F8 = sum2F8 / 64;
			float avg3F8 = sum3F8 / 32;

			int sum1F7 = 0;
			int sum2F7 = 0;
			int sum3F7 = 0;
			for (int i = 0; i < 32; i++) {
				sum1F7 += mSignal.get(i).F7;
			}
			for (int i = 32; i < 96; i++) {
				sum2F7 += mSignal.get(i).F7;
			}
			for (int i = 96; i < mSignal.size(); i++) {
				sum3F7 += mSignal.get(i).F7;
			}

			float avg1F7 = sum1F7 / 32;
			float avg2F7 = sum2F7 / 64;
			float avg3F7 = sum3F7 / 32;

			int F8 = 0;
			int F7 = 0;

			if (avg1F8 - avg2F8 > 50 && avg3F8 - avg2F8 > 50) {
				// System.out.println("DOWN");
				F8 = -1;
			}
			if (avg2F8 - avg1F8 > 50 && avg2F8 - avg3F8 > 50) {
				// System.out.println("up");
				F8 = 1;
			}

			if (avg1F7 - avg2F7 > 40 && avg3F7 - avg2F7 > 40) {
				// System.out.println("DOWN");
				F7 = -1;
			}
			if (avg2F7 - avg1F7 > 40 && avg2F7 - avg3F7 > 40) {
				// System.out.println("up");
				F7 = 1;
			}

			if (F8 != 0 && F7 != 0) {
				if (F8 == 1 && F7 == 1) {
					System.out.println("Blink");
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(EYE_BLINK);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "BLINK", 0)
									.show();
						}
					});
				}
				if (F8 == -1 && F7 == -1) {
					System.out.println("Gaze down");
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(GAZE_DOWN);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "DOWN", 0)
									.show();
						}
					});
				}
				if (F8 == 1 && F7 == -1) {
					System.out.println("Gaze right");
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(GAZE_RIGHT);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "RIGHT", 0)
									.show();
						}
					});
				}
				if (F8 == -1 && F7 == 1) {
					System.out.println("Gaze left");
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(GAZE_LEFT);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "LEFT", 0)
									.show();
						}
					});
				}
				mSignal.clear();
				rehandling = 0;
			}

		}
	}

}
