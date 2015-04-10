package lab411.eeg.gazeservice;


import lab411.eeg.gazeservice.EEGService.EEGBinder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class BroadcastriceverEEG extends BroadcastReceiver{

	public static final String STARTBAS = "lab411.eeg.startbaservice";
	public static final String STOPBAS = "lab411.eeg.stopbaservice";
	public static final String BINDKEY = "lab411.eeg.bindkey";
	
	private boolean mBound;
	private EEGService mService;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
			
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((EEGBinder) service).getInstance();
			mBound = true;
			
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
