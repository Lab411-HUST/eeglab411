package lab411.appman.concentratedetection;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import la411.eeg.svm.Feature;
import la411.eeg.svm.SVM;
import lab411.eeg.emotiv.AES;
import lab411.eeg.emotiv.Emokit_Frame;
import lab411.eeg.emotiv.LibEmotiv;
import lab411.eeg.filedata.FileData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.concentratedetection.R;

public class MainActivity extends Activity {
	public boolean run;
	// Cam
	private static final String TAG = "CAMERA";
	Preview preview;
	Button buttonClick;
	Camera camera;
	Activity act;
	Context ctx;

	// For SVM
	public int kernelType = 0;
	public int cost = 1;
	public int isProb = 0;
	public float gamma = 0.25f;
	// Time test:
	long t1 = 0, t2 = 0;

	public SharedPreferences pref;
	public Editor edit;
	public static final int TRAINING = 1;
	public static final int RUNNING = 2;
	public static final int BOTH = 3;
	private int timer = 0;
	private List<Emokit_Frame> signal;
	private Handler handler = new Handler();
	public static final String FILE_WRITE = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "Concentrate/";
	int index = 0;
	String datain = "";
	private List<Double> concentrate_valuesAF3, concentrate_valuesAF4;
	private List<Integer> numberAF3, numberAF4;
	private int rest_occurrenceAF3, rest_occurrenceAF4, con_occurrenceAF4,
			con_occurrenceAF3;
	ImageView imgicon;
	TextView tv_level;
	ProgressBar prgbar;
	CaptureSignal capture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Cam
		ctx = this;
		act = this;
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		preview = new Preview(this,
				(SurfaceView) findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.capturelayout)).addView(preview);
		preview.setKeepScreenOn(true);

		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				Toast.makeText(ctx, "Take shot", 0).show();
			}
		});
		Toast.makeText(ctx, "LAB 411", Toast.LENGTH_LONG).show();

		// Init variables:
		concentrate_valuesAF3 = new ArrayList<Double>();
		concentrate_valuesAF4 = new ArrayList<Double>();
		numberAF3 = new ArrayList<Integer>();
		numberAF4 = new ArrayList<Integer>();
		imgicon = (ImageView) findViewById(R.id.img_icon);
		imgicon.setImageResource(R.drawable.cam64);
		// imgicon.setVisibility(View.INVISIBLE);
		tv_level = (TextView) findViewById(R.id.tvlevel);
		tv_level.setText("Time:");
		prgbar = (ProgressBar) findViewById(R.id.prgbar);
		prgbar.setProgress(0);

		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		edit = pref.edit();
		List<String> cmds = new ArrayList<String>();
		cmds.add("chmod 777 /dev/hidraw1");
		try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		int numCams = Camera.getNumberOfCameras();
		if (numCams > 0) {
			try {
				camera = Camera.open(0);
				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex) {
				Toast.makeText(ctx, "Camera not found", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SaveImageTask().execute(data);
			resetCam();
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File(sdCard.getAbsolutePath() + "/camtest");
				dir.mkdirs();

				String fileName = String.format("%d.jpg",
						System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length
						+ " to " + outFile.getAbsolutePath());

				refreshGallery(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mi_start:
			if (!run) {
				CaptureSignal capture = new CaptureSignal();
				capture.start();
				Toast.makeText(getApplicationContext(), "Start Both", 0).show();
			}
			return true;
		case R.id.mi_traning:
			if (!run) {
				TrainingConcentration capture = new TrainingConcentration(30);
				capture.start();
				Toast.makeText(getApplicationContext(), "Start Training", 0)
						.show();
			}
			return true;
		case R.id.mi_traningrelaxSVM:
			if (!run) {
				TrainingwithSVM capture = new TrainingwithSVM(15, 0);
				capture.start();
				Toast.makeText(getApplicationContext(), "Training relax", 0)
						.show();
			}
			return true;
		case R.id.mi_trainingconSVM:
			if (!run) {
				TrainingwithSVM capture = new TrainingwithSVM(15, 1);
				capture.start();
				Toast.makeText(getApplicationContext(),
						"Training concentration", 0).show();
			}
			return true;
		case R.id.mi_running:
			if (!run) {
				RunningConcentration capture = new RunningConcentration();
				capture.start();
				Toast.makeText(getApplicationContext(), "Start Running", 0)
						.show();
			}
			return true;
		case R.id.mi_running_peak:
			if (!run) {
				RunningConcentrationwithPeak capture = new RunningConcentrationwithPeak();
				capture.start();
				Toast.makeText(getApplicationContext(), "Running: detect peak",
						0).show();
			}
			return true;
		case R.id.mi_runningSVM:
			if (!run) {
				RunningwithSVM capture = new RunningwithSVM(90);
				capture.start();
				Toast.makeText(getApplicationContext(), "Running: SVM", 0)
						.show();
			}
			return true;
		case R.id.mi_stop:
			run = false;
			Toast.makeText(getApplicationContext(), "Stopped Cature", 0).show();
			return true;
		case R.id.mi_setting:
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

	/* Thu tin hieu pha training */
	class CaptureSignal extends Thread {

		public int type;
		public String time;
		public int windowsize = 30, offset = 5;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		int af3[] = new int[128];
		int af4[] = new int[128];

		public CaptureSignal() {
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.type = BOTH;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.
			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					if (signal.size() >= 128) {
						signal.remove(0);
						signal.add(k);
					} else {
						signal.add(k);
					}
					index++;
					if (index % 128 == 0) {
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});

						// Do Filter

						for (int i = 0; i < signal.size(); i++) {
							af3[i] = signal.get(i).AF3;
							af4[i] = signal.get(i).AF4;
							// Log.e("a[o]", a[0] + "");
						}
						// Filter thetaAF3 wave: 4-7Hz.
						// double start = System.currentTimeMillis();
						double[] thetaAF3 = new double[af3.length];
						double[] thetaAF4 = new double[af4.length];
						thetaAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						thetaAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						// Filter beta+ smr wave: 12-20Hz.
						double[] beta_smrAF3 = new double[af3.length];
						double[] beta_smrAF4 = new double[af4.length];
						beta_smrAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						beta_smrAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						// double stopFilter = System.currentTimeMillis();
						// Log.d("TAG", "Time filter: " + (stopFilter - start));
						// Calculate power:
						double theta_powerAF3 = SignalProcessing.calcPower(
								thetaAF3, thetaAF3.length);
						double theta_powerAF4 = SignalProcessing.calcPower(
								thetaAF4, thetaAF4.length);
						double betasmr_powerAF3 = SignalProcessing.calcPower(
								beta_smrAF3, beta_smrAF3.length);
						double betasmr_powerAF4 = SignalProcessing.calcPower(
								beta_smrAF4, beta_smrAF4.length);
						// double stopcalcPower = System.currentTimeMillis();
						// Log.d("TAG", "Time calc Power: "
						// + (stopcalcPower - stopFilter));
						// Calculate index:
						double c_indexAF3 = betasmr_powerAF3 / theta_powerAF3;
						double c_indexAF4 = betasmr_powerAF4 / theta_powerAF4;
						// double stop = System.currentTimeMillis();
						// Log.d("TAG", "Time Finish: " + (stop - start));
						Log.d("TAG", "Concentrate value: " + c_indexAF3);
						// add to list:
						concentrate_valuesAF3.add(c_indexAF3);
						concentrate_valuesAF4.add(c_indexAF4);
						Log.d("TAG", concentrate_valuesAF3.size() + "");
						// if have enough data, stop capture: 90s
						if (concentrate_valuesAF3.size() == 30) {

							double restAF3 = SignalProcessing
									.mean(concentrate_valuesAF3);
							thresholdAF3 = restAF3 + restAF3 * 0.3;
							double restAF4 = SignalProcessing
									.mean(concentrate_valuesAF4);
							thresholdAF4 = restAF4 + restAF4 * 0.3;
							Log.d("TAG", "restAF3 value:" + restAF3);
							Log.d("TAG", "restAF4 value:" + restAF4);
							Log.d("TAG", "thresholdAF3 value:" + thresholdAF3);
							Log.d("TAG", "thresholdAF4 value:" + thresholdAF4);
							// Count number index > threshold in Training
							// phase:
							rest_occurrenceAF3 = SignalProcessing.count(
									concentrate_valuesAF3, thresholdAF3);
							rest_occurrenceAF4 = SignalProcessing.count(
									concentrate_valuesAF4, thresholdAF4);
							Log.d("TAG", "number occurrence AF3:"
									+ rest_occurrenceAF3);
							Log.d("TAG", "number occurrence AF4:"
									+ rest_occurrenceAF4);

							// Notice stop training, and begin running.
							handler.post(new Runnable() {
								@Override
								public void run() {
									MediaNotification.managerOfSound(
											getApplicationContext(), 1);
									imgicon.setVisibility(View.VISIBLE);
								}
							});
						}
						// Ket thuc 30s training, bat dau dem trong cac cua so
						// 30s.
						if (concentrate_valuesAF3.size() > 30) {
							Log.d("TAG", "Size: " + occurenceAF3.size() + "");
							occurenceAF3.add(concentrate_valuesAF3
									.get(concentrate_valuesAF3.size() - 1));
							Log.d("TAG",
									"Added: "
											+ occurenceAF3.get(occurenceAF3
													.size() - 1));
							occurenceAF4.add(concentrate_valuesAF4
									.get(concentrate_valuesAF4.size() - 1));
							// Neu cua so vuot qua 30, xoa phan tu dau va them
							// phan tu cuoi.
							if (occurenceAF3.size() > 30) {
								Log.d("TAG", "Removed: " + occurenceAF3.get(0)
										+ "");
								Log.d("TAG", "Removed: " + occurenceAF4.get(0)
										+ "");
								occurenceAF3.remove(0);
								occurenceAF4.remove(0);
							}
							// if have offset, add condition here:
							if (occurenceAF3.size() == windowsize) {
								con_occurrenceAF3 = SignalProcessing.count(
										occurenceAF3, thresholdAF3);
								con_occurrenceAF4 = SignalProcessing.count(
										occurenceAF4, thresholdAF4);
								Log.d("TAG", "ConcentrateAF3: "
										+ con_occurrenceAF3);
								Log.d("TAG", "ConcentrateAF4: "
										+ con_occurrenceAF4);
								double ratio = (double) (con_occurrenceAF3 + con_occurrenceAF4)
										/ (rest_occurrenceAF3 + rest_occurrenceAF4);
								if (ratio < 1.2) {
									Log.d("TAG", "Concentrate 0: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("No concentrate");
											prgbar.setProgress(05);
										}
									});
								}
								if (ratio >= 1.2 && ratio < 1.8) {
									Log.d("TAG", "Concentrate 1: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Detected");
											prgbar.setProgress(33);
										}
									});
								}
								if (ratio >= 1.8 && ratio < 2) {
									Log.d("TAG", "Concentrate 2: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Level 2");
											prgbar.setProgress(66);
										}
									});
								}
								if (ratio >= 2) {
									Log.d("TAG", "Concentrate 3: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Level 3");
											prgbar.setProgress(100);
											// take shot
											if (t1 == 0) {
												camera.takePicture(
														shutterCallback,
														rawCallback,
														jpegCallback);
												Toast.makeText(ctx,
														"Take shot", 0).show();
												t1 = System.currentTimeMillis();
											} else {
												t2 = System.currentTimeMillis();
												if (t2 - t1 > 20000) {
													camera.takePicture(
															shutterCallback,
															rawCallback,
															jpegCallback);
													Toast.makeText(ctx,
															"Take shot", 0)
															.show();
													t1 = System
															.currentTimeMillis();
												}
											}
										}
									});
								}
								if (ratio >= 4) {
									Log.d("TAG", "Concentrate 4: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Level 4");
											/*
											 * prgbar.setProgress(100); // take
											 * shot
											 * 
											 * if (t1 == 0) {
											 * camera.takePicture(
											 * shutterCallback, rawCallback,
											 * jpegCallback);
											 * Toast.makeText(ctx, "Take shot",
											 * 0).show(); t1 =
											 * System.currentTimeMillis(); }
											 * else { t2 =
											 * System.currentTimeMillis(); if
											 * (t2 - t1 > 30000) {
											 * camera.takePicture(
											 * shutterCallback, rawCallback,
											 * jpegCallback);
											 * Toast.makeText(ctx, "Take shot",
											 * 0).show(); t1 = System
											 * .currentTimeMillis(); } }
											 */
										}
									});
								}
								numberAF3.add(con_occurrenceAF3);
								numberAF4.add(con_occurrenceAF4);
							}
						}

						if (concentrate_valuesAF3.size() == 90) {
							run = false;

							// Export to text file.
							SignalProcessing.writeData(concentrate_valuesAF3,
									"AF3", type, time);
							SignalProcessing.writeData(concentrate_valuesAF4,
									"AF4", type, time);

							handler.post(new Runnable() {

								@Override
								public void run() {
									imgicon.setVisibility(View.INVISIBLE);
									tv_level.setText("");
									Toast.makeText(getApplicationContext(),
											"Finish Both", 0).show();
								}
							});
							/*
							 * if (concentrate_valuesAF3.size() == 600) {
							 * this.stopCapture(); handler.post(new Runnable() {
							 * 
							 * @Override public void run() {
							 * imgicon.setVisibility(View.INVISIBLE);
							 * tv_level.setText("");
							 * Toast.makeText(getApplicationContext(),
							 * "Finish Both", 0).show(); } });
							 */
						}
						timer++; // count timer(Second)
					}
					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	class TrainingConcentration extends Thread {

		public int type;
		public String time;
		public int windowsize = 30, offset = 5;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		int longs; // time training(second)

		int af3[] = new int[128];
		int af4[] = new int[128];

		public TrainingConcentration(int longs) {
			this.type = TRAINING;
			this.longs = longs;
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.

			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					if (signal.size() >= 128) {
						signal.remove(0);
						signal.add(k);
					} else {
						signal.add(k);
					}
					index++;
					if (index % 128 == 0) {
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});

						// Do Filter

						for (int i = 0; i < signal.size(); i++) {
							af3[i] = signal.get(i).AF3;
							af4[i] = signal.get(i).AF4;
							// Log.e("a[o]", a[0] + "");
						}
						// Filter thetaAF3 wave: 4-7Hz.
						// double start = System.currentTimeMillis();
						double[] thetaAF3 = new double[af3.length];
						double[] thetaAF4 = new double[af4.length];
						thetaAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						thetaAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						// Filter beta+ smr wave: 12-20Hz.
						double[] beta_smrAF3 = new double[af3.length];
						double[] beta_smrAF4 = new double[af4.length];
						beta_smrAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						beta_smrAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						// double stopFilter = System.currentTimeMillis();
						// Log.d("TAG", "Time filter: " + (stopFilter - start));
						// Calculate power:
						double theta_powerAF3 = SignalProcessing.calcPower(
								thetaAF3, thetaAF3.length);
						double theta_powerAF4 = SignalProcessing.calcPower(
								thetaAF4, thetaAF4.length);
						double betasmr_powerAF3 = SignalProcessing.calcPower(
								beta_smrAF3, beta_smrAF3.length);
						double betasmr_powerAF4 = SignalProcessing.calcPower(
								beta_smrAF4, beta_smrAF4.length);
						// double stopcalcPower = System.currentTimeMillis();
						// Log.d("TAG", "Time calc Power: "
						// + (stopcalcPower - stopFilter));
						// Calculate index:
						double c_indexAF3 = betasmr_powerAF3 / theta_powerAF3;
						double c_indexAF4 = betasmr_powerAF4 / theta_powerAF4;
						// double stop = System.currentTimeMillis();
						// Log.d("TAG", "Time Finish: " + (stop - start));
						Log.d("TAG", "Concentrate value: " + c_indexAF3);
						// add to list:
						concentrate_valuesAF3.add(c_indexAF3);
						concentrate_valuesAF4.add(c_indexAF4);
						Log.d("TAG", concentrate_valuesAF3.size() + "");
						// if have enough data, stop capture: 90s
						if (concentrate_valuesAF3.size() == longs) {
							run = false; // stop training.
							double restAF3 = SignalProcessing
									.mean(concentrate_valuesAF3);
							thresholdAF3 = restAF3 + restAF3 * 0.3;
							double restAF4 = SignalProcessing
									.mean(concentrate_valuesAF4);
							thresholdAF4 = restAF4 + restAF4 * 0.3;
							Log.d("TAG", "restAF3 value:" + restAF3);
							Log.d("TAG", "restAF4 value:" + restAF4);
							Log.d("TAG", "thresholdAF3 value:" + thresholdAF3);
							Log.d("TAG", "thresholdAF4 value:" + thresholdAF4);
							// Count number index > threshold in Training
							// phase:
							rest_occurrenceAF3 = SignalProcessing.count(
									concentrate_valuesAF3, thresholdAF3);
							rest_occurrenceAF4 = SignalProcessing.count(
									concentrate_valuesAF4, thresholdAF4);
							Log.d("TAG", "number occurrence AF3:"
									+ rest_occurrenceAF3);
							Log.d("TAG", "number occurrence AF4:"
									+ rest_occurrenceAF4);
							// Save to shared preference:
							edit.putFloat("AF3Threshold", (float) thresholdAF3);
							edit.putFloat("AF4Threshold", (float) thresholdAF4);
							edit.putFloat("AF3Occurence", rest_occurrenceAF3);
							edit.putFloat("AF4Occurence", rest_occurrenceAF4);
							edit.commit();

							// Export to text file.
							SignalProcessing.writeData(concentrate_valuesAF3,
									"AF3", type, time);
							SignalProcessing.writeData(concentrate_valuesAF4,
									"AF4", type, time);

							// Notice stop training
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),
											"Training Finished", 0).show();
									MediaNotification.managerOfSound(
											getApplicationContext(), 1);
									imgicon.setVisibility(View.VISIBLE);
									tv_level.setText("");
								}
							});
						}
						timer++; // count timer(Second)
					}
					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	class RunningConcentration extends Thread {

		public int type;
		public String time;
		public int windowsize = 30, offset = 5;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		int first;
		boolean check_concentrate; // kiem tra neu tap trung thi chi chup 1 anh.
		int af3[] = new int[128];
		int af4[] = new int[128];

		public RunningConcentration() {
			this.type = RUNNING;
			this.check_concentrate = true;
			this.first = 0;
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.

			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					imgicon.setVisibility(View.VISIBLE);
				}
			});
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					if (signal.size() >= 128) {
						signal.remove(0);
						signal.add(k);
					} else {
						signal.add(k);
					}
					index++;
					if (index % 128 == 0) {
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});

						// Do Filter

						for (int i = 0; i < signal.size(); i++) {
							af3[i] = signal.get(i).AF3;
							af4[i] = signal.get(i).AF4;
							// Log.e("a[o]", a[0] + "");
						}
						// Filter thetaAF3 wave: 4-7Hz.
						// double start = System.currentTimeMillis();
						double[] thetaAF3 = new double[af3.length];
						double[] thetaAF4 = new double[af4.length];
						thetaAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						thetaAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						// Filter beta+ smr wave: 12-20Hz.
						double[] beta_smrAF3 = new double[af3.length];
						double[] beta_smrAF4 = new double[af4.length];
						beta_smrAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						beta_smrAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						// double stopFilter = System.currentTimeMillis();
						// Log.d("TAG", "Time filter: " + (stopFilter - start));
						// Calculate power:
						double theta_powerAF3 = SignalProcessing.calcPower(
								thetaAF3, thetaAF3.length);
						double theta_powerAF4 = SignalProcessing.calcPower(
								thetaAF4, thetaAF4.length);
						double betasmr_powerAF3 = SignalProcessing.calcPower(
								beta_smrAF3, beta_smrAF3.length);
						double betasmr_powerAF4 = SignalProcessing.calcPower(
								beta_smrAF4, beta_smrAF4.length);
						// double stopcalcPower = System.currentTimeMillis();
						// Log.d("TAG", "Time calc Power: "
						// + (stopcalcPower - stopFilter));
						// Calculate index:
						double c_indexAF3 = betasmr_powerAF3 / theta_powerAF3;
						double c_indexAF4 = betasmr_powerAF4 / theta_powerAF4;
						// double stop = System.currentTimeMillis();
						// Log.d("TAG", "Time Finish: " + (stop - start));
						Log.d("TAG", "Concentrate value: " + c_indexAF3);
						// add to list:
						concentrate_valuesAF3.add(c_indexAF3);
						concentrate_valuesAF4.add(c_indexAF4);
						Log.d("TAG", concentrate_valuesAF3.size() + "");
						// if have enough data, stop capture: 90s

						// Dem trong cac cua so 30s.

						Log.d("TAG", "Size: " + occurenceAF3.size() + "");
						occurenceAF3.add(concentrate_valuesAF3
								.get(concentrate_valuesAF3.size() - 1));
						Log.d("TAG",
								"Added: "
										+ occurenceAF3.get(occurenceAF3.size() - 1));
						occurenceAF4.add(concentrate_valuesAF4
								.get(concentrate_valuesAF4.size() - 1));
						// Neu cua so vuot qua 30, xoa phan tu dau va them
						// phan tu cuoi.
						if (occurenceAF3.size() > 30) {
							Log.d("TAG", "Removed: " + occurenceAF3.get(0) + "");
							Log.d("TAG", "Removed: " + occurenceAF4.get(0) + "");
							occurenceAF3.remove(0);
							occurenceAF4.remove(0);
						}
						// if have offset, add condition here:
						if (occurenceAF3.size() == windowsize) {
							Log.d("TAG", "In 1");
							thresholdAF3 = pref.getFloat("AF3Threshold", 0);
							thresholdAF4 = pref.getFloat("AF4Threshold", 0);
							rest_occurrenceAF3 = (int) pref.getFloat(
									"AF3Occurence", 1);
							rest_occurrenceAF4 = (int) pref.getFloat(
									"AF4Occurence", 1);
							Log.d("TAG", thresholdAF3 + "::" + thresholdAF4);
							if (thresholdAF3 != 0 && thresholdAF4 != 0) {
								Log.d("TAG", "In 2");
								con_occurrenceAF3 = SignalProcessing.count(
										occurenceAF3, thresholdAF3);
								con_occurrenceAF4 = SignalProcessing.count(
										occurenceAF4, thresholdAF4);
								Log.d("TAG", "ConcentrateAF3: "
										+ con_occurrenceAF3);
								Log.d("TAG", "ConcentrateAF4: "
										+ con_occurrenceAF4);
								double ratio = (double) (con_occurrenceAF3 + con_occurrenceAF4)
										/ (rest_occurrenceAF3 + rest_occurrenceAF4);
								Log.d("TAG", "Ratio = " + ratio);
								if (ratio < 1.2) {
									check_concentrate = true;
									Log.d("TAG", "Concentrate 0: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("No concentrate");
											prgbar.setProgress(05);
										}
									});
								}
								if (ratio >= 1.2 && ratio < 2) {
									check_concentrate = true;
									Log.d("TAG", "Concentrate 1: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Detected");
											prgbar.setProgress(33);
										}
									});
								}
								if (ratio >= 2 && ratio < 3) {
									check_concentrate = true;
									Log.d("TAG", "Concentrate 2: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Level 2");
											prgbar.setProgress(66);
										}
									});
								}
								if (ratio >= 3) {
									Log.d("TAG", "Concentrate 3: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
											// tv_level.setText("Concentrate Level 3");
											prgbar.setProgress(100);
											// take shot
											if (check_concentrate) {
												camera.takePicture(
														shutterCallback,
														rawCallback,
														jpegCallback);
												Toast.makeText(ctx,
														"Take photo", 0).show();
												check_concentrate = false;
											}
										}
									});
								}
								if (ratio >= 4) {
									Log.d("TAG", "Concentrate 4: "
											+ con_occurrenceAF3
											/ rest_occurrenceAF3);
									handler.post(new Runnable() {
										@Override
										public void run() {
										}
									});
								}
								numberAF3.add(con_occurrenceAF3);
								numberAF4.add(con_occurrenceAF4);
							}

						}

						if (concentrate_valuesAF3.size() == 90) {
							run = false;

							// Export to text file.
							SignalProcessing.writeData(concentrate_valuesAF3,
									"AF3", type, time);
							SignalProcessing.writeData(concentrate_valuesAF4,
									"AF4", type, time);

							handler.post(new Runnable() {

								@Override
								public void run() {
									imgicon.setVisibility(View.INVISIBLE);
									tv_level.setText("");
									prgbar.setProgress(0);
									Toast.makeText(getApplicationContext(),
											"Finish Both", 0).show();
								}
							});
							/*
							 * if (concentrate_valuesAF3.size() == 600) {
							 * this.stopCapture(); handler.post(new Runnable() {
							 * 
							 * @Override public void run() {
							 * imgicon.setVisibility(View.INVISIBLE);
							 * tv_level.setText("");
							 * Toast.makeText(getApplicationContext(),
							 * "Finish Both", 0).show(); } });
							 */
						}
						timer++; // count timer(Second)
					}
					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	class RunningConcentrationwithPeak extends Thread {

		public int type;
		public String time;
		public int windowsize = 30, offset = 5;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		boolean check_concentrate; // kiem tra neu tap trung thi chi chup 1 anh.
		int af3[] = new int[128];
		int af4[] = new int[128];

		public RunningConcentrationwithPeak() {
			this.type = RUNNING;
			check_concentrate = true;
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.
			// Get training data
			thresholdAF3 = pref.getFloat("AF3Threshold", 0);
			thresholdAF4 = pref.getFloat("AF4Threshold", 0);
			rest_occurrenceAF3 = (int) pref.getFloat("AF3Occurence", 1);
			rest_occurrenceAF4 = (int) pref.getFloat("AF4Occurence", 1);

			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					imgicon.setVisibility(View.VISIBLE);
				}
			});
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					if (signal.size() >= 128) {
						signal.remove(0);
						signal.add(k);
					} else {
						signal.add(k);
					}
					index++;
					if (index % 128 == 0) {
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});

						// Do Filter
						for (int i = 0; i < signal.size(); i++) {
							af3[i] = signal.get(i).AF3;
							af4[i] = signal.get(i).AF4;
							// Log.e("a[o]", a[0] + "");
						}
						// Filter thetaAF3 wave: 4-7Hz.
						// double start = System.currentTimeMillis();
						double[] thetaAF3 = new double[af3.length];
						double[] thetaAF4 = new double[af4.length];
						thetaAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						thetaAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 4 / 128,
								2 * Math.PI * 7 / 128, 1.5);
						// Filter beta+ smr wave: 12-20Hz.
						double[] beta_smrAF3 = new double[af3.length];
						double[] beta_smrAF4 = new double[af4.length];
						beta_smrAF3 = SignalProcessing.getYnFilter(af3,
								af3.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						beta_smrAF4 = SignalProcessing.getYnFilter(af4,
								af4.length, 1024, 2 * Math.PI * 12 / 128,
								2 * Math.PI * 20 / 128, 1.5);
						// double stopFilter = System.currentTimeMillis();
						// Log.d("TAG", "Time filter: " + (stopFilter - start));
						// Calculate power:
						double theta_powerAF3 = SignalProcessing.calcPower(
								thetaAF3, thetaAF3.length);
						double theta_powerAF4 = SignalProcessing.calcPower(
								thetaAF4, thetaAF4.length);
						double betasmr_powerAF3 = SignalProcessing.calcPower(
								beta_smrAF3, beta_smrAF3.length);
						double betasmr_powerAF4 = SignalProcessing.calcPower(
								beta_smrAF4, beta_smrAF4.length);
						// double stopcalcPower = System.currentTimeMillis();
						// Log.d("TAG", "Time calc Power: "
						// + (stopcalcPower - stopFilter));
						// Calculate index:
						double c_indexAF3 = betasmr_powerAF3 / theta_powerAF3;
						double c_indexAF4 = betasmr_powerAF4 / theta_powerAF4;
						// double stop = System.currentTimeMillis();
						// Log.d("TAG", "Time Finish: " + (stop - start));
						Log.d("TAG", "Concentrate value: " + c_indexAF3);
						// Lay gia tri nguong tu training.
						thresholdAF3 = pref.getFloat("AF3Threshold", 0);
						thresholdAF4 = pref.getFloat("AF4Threshold", 0);
						// add to list:
						concentrate_valuesAF3.add(c_indexAF3);
						concentrate_valuesAF4.add(c_indexAF4);
						Log.d("TAG", concentrate_valuesAF3.size() + "");
						// if have enough data, stop capture: 90s
						double ratio = (c_indexAF3 + c_indexAF4)
								/ ((thresholdAF3 + thresholdAF4) / 1.3);
						Log.d("TAG", "Concentration level: " + ratio);
						if (ratio < 1) {
							check_concentrate = true;
							handler.post(new Runnable() {

								@Override
								public void run() {
									prgbar.setProgress(05);
								}
							});
						}
						if (ratio > 1 && ratio < 2) {
							check_concentrate = true;
							handler.post(new Runnable() {

								@Override
								public void run() {
									prgbar.setProgress(30);
								}
							});
						}
						if (ratio >= 2 && ratio < 3) {
							check_concentrate = true;
							handler.post(new Runnable() {
								@Override
								public void run() {
									prgbar.setProgress(60);
								}
							});
						}
						if (ratio >= 3) {
							if (check_concentrate) {

								handler.post(new Runnable() {

									@Override
									public void run() {
										camera.takePicture(shutterCallback,
												rawCallback, jpegCallback);
										Toast.makeText(ctx, "Take shot", 0)
												.show();
										prgbar.setProgress(100);
									}
								});
								check_concentrate = false;
							}

						}

						if (concentrate_valuesAF3.size() == 90) {
							run = false;

							// Export to text file.
							SignalProcessing.writeData(concentrate_valuesAF3,
									"AF3", type, time);
							SignalProcessing.writeData(concentrate_valuesAF4,
									"AF4", type, time);

							handler.post(new Runnable() {

								@Override
								public void run() {
									// imgicon.setVisibility(View.INVISIBLE);
									tv_level.setText("");
									prgbar.setProgress(0);
									Toast.makeText(getApplicationContext(),
											"Finish Running", 0).show();
								}
							});
							/*
							 * if (concentrate_valuesAF3.size() == 600) {
							 * this.stopCapture(); handler.post(new Runnable() {
							 * 
							 * @Override public void run() {
							 * imgicon.setVisibility(View.INVISIBLE);
							 * tv_level.setText("");
							 * Toast.makeText(getApplicationContext(),
							 * "Finish Both", 0).show(); } });
							 */
						}
						timer++; // count timer(Second)

					}
					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class TrainingwithSVM extends Thread {

		public int type;
		public String time;
		public int windowsize = 30, offset = 0;
		boolean isfirst = true;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		int longs; // time training(second)
		int label;
		int af3[] = new int[128 * 5];
		int af4[] = new int[128 * 5];

		public TrainingwithSVM(int longs, int label) {
			this.type = TRAINING;
			this.longs = longs;
			this.label = label;
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.

			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					index++;
					if (index % 128 == 0) {
						timer++; // count timer(Second)
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});
					}
					if (signal.size() >= 128 * 5) {
						offset++;
						signal.remove(0);
						signal.add(k);
						if (offset == 128 * 2 || isfirst) {
							offset = 0;
							isfirst = false;
							// Do Filter

							for (int i = 0; i < signal.size(); i++) {
								af3[i] = signal.get(i).AF3;
								af4[i] = signal.get(i).AF4;
								// Log.e("a[o]", a[0] + "");
							}
							// Filter thetaAF3 wave: 4-7Hz.
							// double start = System.currentTimeMillis();
							double[] thetaAF3 = new double[af3.length];
							double[] thetaAF4 = new double[af4.length];
							thetaAF3 = SignalProcessing.getYnFilter(af3,
									af3.length, 1024, 2 * Math.PI * 4 / 128,
									2 * Math.PI * 7 / 128, 1.5);
							thetaAF4 = SignalProcessing.getYnFilter(af4,
									af4.length, 1024, 2 * Math.PI * 4 / 128,
									2 * Math.PI * 7 / 128, 1.5);
							// Filter beta+ smr wave: 12-20Hz.
							double[] beta_smrAF3 = new double[af3.length];
							double[] beta_smrAF4 = new double[af4.length];
							beta_smrAF3 = SignalProcessing.getYnFilter(af3,
									af3.length, 1024, 2 * Math.PI * 12 / 128,
									2 * Math.PI * 20 / 128, 1.5);
							beta_smrAF4 = SignalProcessing.getYnFilter(af4,
									af4.length, 1024, 2 * Math.PI * 12 / 128,
									2 * Math.PI * 20 / 128, 1.5);
							// double stopFilter =
							// System.currentTimeMillis();
							// Log.d("TAG", "Time filter: " + (stopFilter -
							// start));
							// Calculate power:
							double theta_powerAF3 = SignalProcessing.calcPower(
									thetaAF3, thetaAF3.length);
							double theta_powerAF4 = SignalProcessing.calcPower(
									thetaAF4, thetaAF4.length);
							double betasmr_powerAF3 = SignalProcessing
									.calcPower(beta_smrAF3, beta_smrAF3.length);
							double betasmr_powerAF4 = SignalProcessing
									.calcPower(beta_smrAF4, beta_smrAF4.length);
							// double stopcalcPower =
							// System.currentTimeMillis();
							// Log.d("TAG", "Time calc Power: "
							// + (stopcalcPower - stopFilter));
							// Calculate index:
							double c_indexAF3 = betasmr_powerAF3
									/ theta_powerAF3;
							double c_indexAF4 = betasmr_powerAF4
									/ theta_powerAF4;
							// double stop = System.currentTimeMillis();
							// Log.d("TAG", "Time Finish: " + (stop -
							// start));

							// add to list:
							// lam tron toi 5 chu so thap phan
							c_indexAF3 = SignalProcessing.fix(c_indexAF3);
							c_indexAF4 = SignalProcessing.fix(c_indexAF4);
							Log.d("TAG", "Concentrate value: " + c_indexAF3);
							concentrate_valuesAF3.add(c_indexAF3);
							concentrate_valuesAF4.add(c_indexAF4);
							Log.d("TAG", concentrate_valuesAF3.size() + "");
							// Ghi file training.

							Feature.writeFileTrainingSet(label + "",
									(float) c_indexAF3,
									"/sdcard/Concentrate/Result/TRAINING.txt");
						}
					} else {
						signal.add(k);
					}

					// if have enough data, stop capture: 90s
					if (timer == longs) {
						run = false; // stop training.
						Log.d("TAG", "Writing file...");

						// //Xoa file training
						// File f = new
						// File("/sdcard/Concentrate/Result/TRAINING.txt");
						// f.delete();

						// Create model file
						SVM.trainClassifier(
								"/sdcard/Concentrate/Result/TRAINING.txt",
								kernelType, cost, gamma, isProb,
								"/sdcard/Concentrate/Result/MODEL.txt");

						Log.d("TAG", "Finish...");
						// Notice stop training
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Training Finished", 0).show();
								MediaNotification.managerOfSound(
										getApplicationContext(), 1);
								imgicon.setVisibility(View.VISIBLE);
								tv_level.setText("");
							}
						});
					}

					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	class RunningwithSVM extends Thread {
		boolean check_concentrate;
		public int type;
		public String time;
		public int windowsize = 30, offset = 0;
		boolean isfirst = true;
		public List<Double> occurenceAF3, occurenceAF4;
		double thresholdAF4, thresholdAF3;
		int number_remove;
		int longs; // time training(second)
		int af3[] = new int[128 * 5];
		int af4[] = new int[128 * 5];
		int result;

		public RunningwithSVM(int longs) {
			check_concentrate = true;
			this.type = RUNNING;
			this.longs = longs;
			signal = new ArrayList<Emokit_Frame>();
			timer = 0;
			run = true;
			this.number_remove = 0; // to remove 128 mau dau tien.
			occurenceAF3 = new ArrayList<Double>();
			occurenceAF4 = new ArrayList<Double>();
			time = SignalProcessing.getCurrentTime(); // only get time when
														// start capture data.

			concentrate_valuesAF3.clear();
			concentrate_valuesAF4.clear();
		}

		public byte[] int2byte(int[] src) {
			byte[] res = new byte[src.length];
			for (int i = 0; i < src.length; i++) {
				res[i] = (byte) src[i];
			}
			return res;
		}

		public void run() {
			final int check_open = LibEmotiv.OpenDevice("/dev/hidraw1");
			if (check_open != 1)
				return;
			while (run) {
				int[] res = LibEmotiv.ReadRawData();
				byte[] data = int2byte(res);
				Emokit_Frame k = new Emokit_Frame();

				try {
					k = AES.get_data(AES.get_raw_unenc(data));
					number_remove++;
					if (number_remove <= 128)
						continue;
					SignalProcessing.WriteFile(k, type, time);
					index++;
					if (index % 128 == 0) {
						timer++; // count timer(Second)
						// Show timer to UI.
						handler.post(new Runnable() {

							@Override
							public void run() {
								tv_level.setText("Time: " + timer + "s");
							}
						});
					}
					if (signal.size() >= 128 * 5) {
						offset++;
						signal.remove(0);
						signal.add(k);
						if (offset == 128 * 2 || isfirst) {
							offset = 0;
							isfirst = false;
							// Do Filter

							for (int i = 0; i < signal.size(); i++) {
								af3[i] = signal.get(i).AF3;
								af4[i] = signal.get(i).AF4;
								// Log.e("a[o]", a[0] + "");
							}
							// Filter thetaAF3 wave: 4-7Hz.
							// double start = System.currentTimeMillis();
							double[] thetaAF3 = new double[af3.length];
							double[] thetaAF4 = new double[af4.length];
							thetaAF3 = SignalProcessing.getYnFilter(af3,
									af3.length, 1024, 2 * Math.PI * 4 / 128,
									2 * Math.PI * 7 / 128, 1.5);
							thetaAF4 = SignalProcessing.getYnFilter(af4,
									af4.length, 1024, 2 * Math.PI * 4 / 128,
									2 * Math.PI * 7 / 128, 1.5);
							// Filter beta+ smr wave: 12-20Hz.
							double[] beta_smrAF3 = new double[af3.length];
							double[] beta_smrAF4 = new double[af4.length];
							beta_smrAF3 = SignalProcessing.getYnFilter(af3,
									af3.length, 1024, 2 * Math.PI * 12 / 128,
									2 * Math.PI * 20 / 128, 1.5);
							beta_smrAF4 = SignalProcessing.getYnFilter(af4,
									af4.length, 1024, 2 * Math.PI * 12 / 128,
									2 * Math.PI * 20 / 128, 1.5);
							// double stopFilter =
							// System.currentTimeMillis();
							// Log.d("TAG", "Time filter: " + (stopFilter -
							// start));
							// Calculate power:
							double theta_powerAF3 = SignalProcessing.calcPower(
									thetaAF3, thetaAF3.length);
							double theta_powerAF4 = SignalProcessing.calcPower(
									thetaAF4, thetaAF4.length);
							double betasmr_powerAF3 = SignalProcessing
									.calcPower(beta_smrAF3, beta_smrAF3.length);
							double betasmr_powerAF4 = SignalProcessing
									.calcPower(beta_smrAF4, beta_smrAF4.length);
							// double stopcalcPower =
							// System.currentTimeMillis();
							// Log.d("TAG", "Time calc Power: "
							// + (stopcalcPower - stopFilter));
							// Calculate index:
							double c_indexAF3 = betasmr_powerAF3
									/ theta_powerAF3;
							double c_indexAF4 = betasmr_powerAF4
									/ theta_powerAF4;
							// double stop = System.currentTimeMillis();
							// Log.d("TAG", "Time Finish: " + (stop -
							// start));

							// add to list:
							// lam tron toi 5 chu so thap phan
							c_indexAF3 = SignalProcessing.fix(c_indexAF3);
							c_indexAF4 = SignalProcessing.fix(c_indexAF4);
							Log.d("TAG", "Concentrate value: " + c_indexAF3);
							concentrate_valuesAF3.add(c_indexAF3);
							concentrate_valuesAF4.add(c_indexAF4);
							Log.d("TAG", concentrate_valuesAF3.size() + "");
							// SVM
							Feature.writeFeatureFile("1", (float) c_indexAF3,
									"/sdcard/Concentrate/Result/RUNTIME.txt");
							SVM.doClassification(0,
									"/sdcard/Concentrate/Result/RUNTIME.txt",
									"/sdcard/Concentrate/Result/MODEL.txt",
									"/sdcard/Concentrate/Result/RESULT.txt");
							result = FileData
									.readDataFileOutput("/sdcard/Concentrate/Result/RESULT.txt");
							if (result == 1) {
								if (check_concentrate) {
									handler.post(new Runnable() {

										@Override
										public void run() {
											camera.takePicture(shutterCallback,
													rawCallback, jpegCallback);
											Toast.makeText(ctx, "Take shot", 0)
													.show();
											prgbar.setProgress(100);
											check_concentrate = false;
										}
									});
								}

							} else {
								check_concentrate = true;
								prgbar.setProgress(5);
							}
						}
					} else {
						signal.add(k);
					}

					// if have enough data, stop capture: 90s
					if (timer == longs) {
						run = false; // stop training.
						Log.d("TAG", "Writing file...");
						Log.d("TAG", "Finish...");
						// Notice stop training
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Training Finished", 0).show();
								MediaNotification.managerOfSound(
										getApplicationContext(), 1);
								imgicon.setVisibility(View.VISIBLE);
								tv_level.setText("");
							}
						});
					}

					// System.out.println(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
