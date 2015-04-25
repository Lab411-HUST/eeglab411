package lab411.eeg.filedata;

public class FileData {
	
	static {
		System.loadLibrary("filedata");
	}
	public static native void writeFiledata(String pathFile ,String data);
	
	public static native int readDataFileOutput(String pathFileOutput);
	
}
