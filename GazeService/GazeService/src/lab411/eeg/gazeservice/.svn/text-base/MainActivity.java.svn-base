package lab411.eeg.gazeservice;

import lab411.eeg.gazeservice.EEGService.EEGBinder;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv_Status;
	private boolean mBound;
	private EEGService mService;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
			tv_Status.setText("Service Disconnected!");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((EEGBinder) service).getInstance();
			mBound = true;
			tv_Status.setText("Service Connected!");
		}
	};

	@Override
	protected void onDestroy() {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_Status = (TextView) findViewById(R.id.tv_status);

		Intent service = new Intent(this, EEGService.class);
		this.bindService(service, mConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
