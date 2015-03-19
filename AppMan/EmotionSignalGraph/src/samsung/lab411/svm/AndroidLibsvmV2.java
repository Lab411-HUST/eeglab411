package samsung.lab411.svm;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class AndroidLibsvmV2 extends Activity{
	
	
	static String TAG ="LAB411";
		LibSVM librarySVM =new LibSVM();
		
		/*
		 * public void MessageBox(String mess) { Toast.makeText(this, mess, 2000); }
		 */
		public void train(String trainingFilePath, String modelFilePath) {
			// Svm training
			int kernelType = 0; // Radial basis function
			int cost = 1; // Cost
			int isProb = 0;
			float gamma = 0.25f; // Gamma
			String trainingFileLoc = trainingFilePath;
			
			String modelFileLoc = modelFilePath;
			
			try
			{
				if
				(librarySVM.trainClassifierNative(trainingFileLoc, kernelType, cost, gamma,
						isProb, modelFileLoc) == -1) {
					Log.d(TAG, "training err") ;
					// finish();
				}
				Log.d(TAG, "Training successful!");
			}catch(Exception eeee)
			{
				Log.d(TAG, "LOI");
			}
			
			try {
				FileInputStream filein = new FileInputStream(
						new File(modelFileLoc));
				byte[] buffer = new byte[1024];
				int byteread;
				ByteArrayBuffer byteArray = new ByteArrayBuffer(512);
				while ((byteread = filein.read(buffer)) != -1) {
					byteArray.append(buffer, 0, byteread);
				}
				String eegsignal = new String(byteArray.toByteArray());

				String[] values = eegsignal.split("\n");
				Log.d("xxx", "Training finish!");
								
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

}
