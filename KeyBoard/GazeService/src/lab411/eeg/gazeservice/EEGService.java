package lab411.eeg.gazeservice;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
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
	double[] mSignalEEG = new double[128];
    
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
		super.onCreate();
		mSignal = new ArrayList<Emokit_Frame>();
		index = 0;
		start = 0;
		
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
		Toast.makeText(getApplicationContext(), "Stop Service", 0).show();
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
			Log.i("TAG", "Device opened : " + check_open);
			if (check_open != 1)
				return;
			int x = 0;
			
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
							new EEGHandling().start();
							
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
					Toast.makeText(getApplicationContext(), "finish", 0).show();
				}
			});
		}
	}

	private Handler handler = new Handler();

	class EEGHandling extends Thread {
		@Override
		public void run() {
		
			int sum1F8 = 0;
			int sum2F8 = 0;
			int sum3F8 = 0;
			for (int i = 0; i < 24; i++) {
				sum1F8 += mSignal.get(i).F8;
			}
			for (int i = 32; i < 96; i++) {
				sum2F8 += mSignal.get(i).F8;
			}
			for (int i = 104; i < mSignal.size(); i++) {
				sum3F8 += mSignal.get(i).F8;
			}

			float avg1F8 = sum1F8 / 24;
			float avg2F8 = sum2F8 / 64;
			float avg3F8 = sum3F8 / 24;

			int sum1F7 = 0;
			int sum2F7 = 0;
			int sum3F7 = 0;
			for (int i = 0; i < 24; i++) {
				sum1F7 += mSignal.get(i).F7;
			}
			for (int i = 32; i < 96; i++) {
				sum2F7 += mSignal.get(i).F7;
			}
			for (int i = 104; i < mSignal.size(); i++) {
				sum3F7 += mSignal.get(i).F7;
			}
			//
			int sum1AF3 = 0;
			int sum2AF3 = 0;
			int sum3AF3 = 0;

			for (int i = 0; i < 24; i++) {
				sum1AF3 += mSignal.get(i).AF3;
			}
			for (int i = 32; i < 96; i++) {
				sum2AF3 += mSignal.get(i).AF3;
			}
			for (int i = 104; i < mSignal.size(); i++) {
				sum3AF3 += mSignal.get(i).AF3;
			}
			float avg1AF3 = sum1AF3 / 24;
			float avg2AF3 = sum2AF3 / 64;
			float avg3AF3 = sum3AF3 / 24;

			int sum1AF4 = 0;
			int sum2AF4 = 0;
			int sum3AF4 = 0;

			for (int i = 0; i < 24; i++) {
				sum1AF4 += mSignal.get(i).AF4;
			}
			for (int i = 32; i < 96; i++) {
				sum2AF4 += mSignal.get(i).AF4;
			}
			for (int i = 104; i < mSignal.size(); i++) {
				sum3AF4 += mSignal.get(i).AF4;
			}
			float avg1AF4 = sum1AF4 / 24;
			float avg2AF4 = sum2AF4 / 64;
			float avg3AF4 = sum3AF4 / 24;
			//
			float avg1F7 = sum1F7 / 24;
			float avg2F7 = sum2F7 / 64;
			float avg3F7 = sum3F7 / 24;
			
			
			int F8 = 0;
			int F7 = 0;
			int AF3 = 0;
			int AF4 = 0;
			if (avg1F8 - avg2F8 > 50 && avg3F8 - avg2F8 > 50) {
				
				F8 = -1;
			}
			if (avg2F8 - avg1F8 > 50 && avg2F8 - avg3F8 > 50) {
				
				F8 = 1;
			}

			if (avg1F7 - avg2F7 > 25 && avg3F7 - avg2F7 > 25) {
				
				F7 = -1;
			}
			if (avg2F7 - avg1F7 > 25 && avg2F7 - avg3F7 > 25) {
				
				F7 = 1;
			}

			
			if (avg1AF3 - avg2AF3 > 70 && avg3AF3 - avg2AF3 > 70) {
				
				AF3 = -1;
			}
			if (avg1AF4 - avg2AF4 > 70 && avg3AF4 - avg2AF4 > 70) {
				
				AF4 = -1;
			}
			
			if (avg2AF3 - avg1AF3 > 50 && avg2AF3 - avg3AF3 > 50) {
				
				AF3 = 1;
			}
			if (avg2AF4 - avg1AF4 > 50 && avg2AF4 - avg3AF4 > 50) {
				
				AF4 = 1;
			}
			//
			int t = 0;
			
			if (F8 != 0 && F7 != 0 )  {
				Intent s = new Intent();
				s.setAction("EEGKeyCodeReceived");
				if (F8 == 1 && F7 == -1)  {
					
					s.putExtra("key", 2);
					sendBroadcast(s);
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(GAZE_RIGHT);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "RIGHT ", 0)
									.show();
						}
					});
					mSignal.clear();
					
					t=1;
					
				}
				if (F8 == -1 && F7 == 1)  {
					
					s.putExtra("key", 1);
					sendBroadcast(s);
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(GAZE_LEFT);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "LEFT ", 0)
									.show();
						}
					});
					mSignal.clear();
					
					t=1;
				
				}
				
			}
			
			if (AF3 != 0 && AF4 != 0 && t==0) {
				if (AF3 == -1 && AF4 == -1) {
				
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent s = new Intent("EEGKeyCodeReceived");
							s.putExtra("key", 4);
							getApplicationContext().sendBroadcast(s);
							Intent intent = new Intent(GAZE_DOWN);
							getApplicationContext().sendBroadcast(intent);
							Toast.makeText(getApplicationContext(), "DOWN ", 0)
									.show();
						}
					});
					
					mSignal.clear();
					t=1;
				}
				if (AF3 == 1 && AF4 == 1) {
					
					int indexup=0;
					int indexdown=0;
					boolean check12=false;
					for(int i=0;i<40;i++){
						for(int j=i+1;j<(i+10);j++) {
							int m=mSignal.get(i).AF3;
							int n=mSignal.get(j).AF3;
							if(n-m>100){
								indexup=j;
								check12=true;
							}
								
						}
						if(check12)
							break;
					}
					check12=false;
					for(int i=75;i<110;i++) {
						for(int j=i+1;j<(i+10);j++) {
							int m=mSignal.get(i).AF3;
							int n=mSignal.get(j).AF3;
							if(m-n>100){
								indexdown=i;
								check12=true;
							}
						}
						if(check12)
							break;
					}
					if(indexdown-indexup > 55) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Intent s = new Intent("EEGKeyCodeReceived");
								s.putExtra("key", 3);
								getApplicationContext().sendBroadcast(s);
								Intent intent = new Intent(EYE_BLINK);
								getApplicationContext().sendBroadcast(intent);
								Toast.makeText(getApplicationContext(), "BLINK ", 0)
										.show();
							}
						});
						
						mSignal.clear();
						t=1;
						
					}
				    	
				}
			}
      }
	
	}

}
