package lab411.loadsharing.lsclient;

public class PCServer {
	public static int PORT;
	public static String IP;
	public static synchronized int PORT(){
        return PORT;
    }
	public static synchronized String IP(){
        return IP;
    }
    public static synchronized void setServer(int p, String i){
        PCServer.PORT = p;
        PCServer.IP = i;
    }
}
