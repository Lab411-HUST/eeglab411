package lab411.eeg.processSignal;

public class XdawnFilterEpoch {
	double mStartTime=0;
	double mEndTime=0;
	double[][] mSignal=new double[3][8];   
	
	public XdawnFilterEpoch(double starttime,double endtime,double [][] signal){
		mStartTime=starttime;
		mEndTime=endtime;
		mSignal=signal;
	}
	public double getStartTime(){
		return mStartTime;
	}
	public double getEndTime(){
		return mEndTime;
	}
	public double[][] getSignal(){
		return mSignal;
	}   
}
