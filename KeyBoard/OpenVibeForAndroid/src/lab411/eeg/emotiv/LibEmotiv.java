package lab411.eeg.emotiv;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class LibEmotiv extends Activity {

	static {
		System.out.println("Load Libs HeadSet");
		System.loadLibrary("hid");
		System.loadLibrary("main");  
	}

	public static native int OpenDevice(String path);

	public static native int[] ReadRawData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

}
