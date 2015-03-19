package lab411.eeg.emotiv;

public class LibEmotiv {
	static {
		System.loadLibrary("hid");
		System.loadLibrary("main");
	}
	
	public static native int OpenDevice(String path);
	public static native int[] ReadRawData();

}
