package lab411.eeg.gazeservice;

import java.io.DataOutputStream;

import java.util.ArrayList;
import java.util.List;



import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import lab411.eeg.process_signal.LowPassFilter;
import lab411.eeg.process_signal.daubechies;
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
	public static final int BLINK=0;
	public static final int UP_EDGE=1;
	public static final int DOWN_EDGE=2;
	LowPassFilter filter; // Filter
	daubechies mDaubechies;
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
		filter= new LowPassFilter(601, Math.PI / 8);
		// PI/8 ~ 0 -> 8Hz
		filter.Hamming();
		mDaubechies=new daubechies();
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
						if (index < 20) {//10
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
		public double[] OnFilter(double [] input) {
			int l =input.length;
			double Output[]=new double[l];
			double signal[] = new double[l + 600];
			for (int i = 0; i < signal.length; i++) {
				if (i >= 0 && i <= 300) {
					signal[i] = input[0];
				} else if (i >= (signal.length - 301)
						&& i <= (signal.length - 1)) {
					signal[i] = input[l-1];
				} else {
					signal[i] = input[i - 300];
				}
			}
			//
			double[] signal_convolution = filter.Filt(signal);
			// cut signal
			for (int i = 0; i < signal_convolution.length; i++) {
				if (i >= ((int) filter.M + 300)
						&& i < ((int) filter.M + 300) + l) {
					Output[i - ((int) filter.M + 300)] = signal_convolution[i];
				}
			}
			//
			return Output;
		}
		//Detect Fixation
        boolean DetectFixation(double []input){
        	boolean state=false;
        	int max=0;
        	int t=0,k=-1; 
        	for(int i=0;i<input.length;i++) {
        		double a=input[i];
        		if(Math.abs(a)>35) { 
        		t=1; 
        		if(k>max)
        		max=k;
        		k=-1;
        		}
        		if(t==1)
        		k++;
        	}
        	if(max>3)
        	state=true;
        	
        	return state;
        }
		// Detect Saccade
		int DetectSaccade(double []input,int type) {
			int state=0;
			double s[]=new double[input.length];
			s=mDaubechies.forwardTrans(input);
			double coefficients[]=new double[16];
			coefficients[0]=0;
			for(int i=17;i<32;i++)
			coefficients[i-16]=s[i];
			//
			boolean _coefficient=DetectFixation(coefficients);
			switch (type) {
			case 0:
				int k=0;
				for(int i=0;i<16;i++) {
					if(Math.abs(coefficients[i])>35)
						k++;
				}
				
				if(k>2)
				state=1;
				k=0;
				break;
			case 1:
				int h=0;
				int h1=0;
				for(int i=0;i<8;i++)
					if(Math.abs(coefficients[i])>35)
						h++;
				for(int i=8;i<16;i++)
					if(Math.abs(coefficients[i])>35)
						h1++;
				if((h>0)&&(h1>0)&&_coefficient)
					state=1;
				else 
					state=0;
				break;
			case 2:
				
				int t=0;
				int t1=0;
				for(int i=0;i<8;i++)
					if(Math.abs(coefficients[i])>35)
						t++;
				for(int i=8;i<16;i++)
					if(Math.abs(coefficients[i])>35)
						t1++;
				if((t>0)&&(t1>0)&&_coefficient)
					state=1;
				else 
					state=0;
				break;
			default:
				break;
			}
			return state;
		}
		@Override
		public void run() {
		    long starttimerend =System.currentTimeMillis();
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
					double mF7[]=new double[128];
					double mF8[]=new double[128];
					for(int i=0;i<128;i++) {
						mF7[i]=mSignal.get(i).F7;
						mF8[i]=mSignal.get(i).F8;
					}
						
					int up=DetectSaccade(mF8, UP_EDGE);
					int down=DetectSaccade(mF7, DOWN_EDGE);
					if(up==1||down==1) {
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
					
				}
				if (F8 == -1 && F7 == 1)  {
					
					
					double mF7[]=new double[128];
					double mF8[]=new double[128];
					for(int i=0;i<128;i++) {
						mF7[i]=mSignal.get(i).F7;
						mF8[i]=mSignal.get(i).F8;
					}
						
					int up=DetectSaccade(mF8, DOWN_EDGE);
					int down=DetectSaccade(mF7, UP_EDGE);
					if(up==1||down==1) {
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
				
			}
			
			if (AF3 != 0 && AF4 != 0 && t==0) {
				if (AF3 == -1 && AF4 == -1) {
					double af3[]=new double[128];
					double af4[]=new double[128];
					for(int i=0;i<128;i++) {
						af3[i]=mSignal.get(i).AF3;
						af4[i]=mSignal.get(i).AF4;
					}
						
					int down1=DetectSaccade(af3, DOWN_EDGE);
					int down=DetectSaccade(af4, DOWN_EDGE);
					if(down1==1||down==1) {
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
					
				}
				if (AF3 == 1 && AF4 == 1) {
					
					double af3[]=new double[128];
					double af4[]=new double[128];
					for(int i=0;i<128;i++) {
						af3[i]=mSignal.get(i).AF3;
						af4[i]=mSignal.get(i).AF4;
					}
						
					int up1=DetectSaccade(af3,BLINK);
					int up=DetectSaccade(af4, BLINK);
					if(up1==1||up==1) {
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
