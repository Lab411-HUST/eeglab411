/*
 * <Copyright project =Samsung_Reran; file=samsung.lab411.emotiv> 
 * 
 * <company> HUST-Laboratory 411-Samsung EEG project</company>
 * 
 * <author> PhongTran </author>
 * 
 * <email> phongtran0715@gmail.com@gmail.com </email>
 * 
 * <date> Apr 29, 2014 - 3:17:40 PM </date>
 * 
 * <purpose> </purpose>
 */

package lab411.eeg.emotiv;

// TODO: Auto-generated Javadoc
/**
 * The Class LibraryEmotiv.
 */
public class LibEmotiv {

	/**
	 * Library capture data from headset
	 *
	 * @param path the path
	 * @return the int
	 */
	public static native int OpenDevice(String path);
	
	/**
	 * Read raw data.
	 *
	 * @return the int[]
	 */
	public static native int[] ReadRawData();
	
	static {
		System.loadLibrary("hid");
		System.loadLibrary("main");
	}
}
