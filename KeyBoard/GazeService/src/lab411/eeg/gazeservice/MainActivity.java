package lab411.eeg.gazeservice;



import lab411.eeg.gazeservice.EEGService.EEGBinder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.app.Service;

import android.content.ComponentName;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	
	private Handler handler = new Handler();
	private boolean mBound;
	private EEGService mService;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
			//tv_Status.setText("Service Disconnected!");
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Service Disconnected!", 0).show();
				}
			});
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((EEGBinder) service).getInstance();
			mBound = true;
             handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Service Connected!", 0).show();	
				}
			});
			
		}
	};
	
	
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent service = new Intent(MainActivity.this, EEGService.class);
		MainActivity.this.bindService(service, mConnection, Service.BIND_AUTO_CREATE);	
		
		
		
	}
     
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
