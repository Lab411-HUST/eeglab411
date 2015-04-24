package lab411.eeg.processSignal;

public class _600Epoch {
	public double mStartTime;
	public double mEndTime;
	public double[][] mSignal=new double[14][19];
	public _600Epoch(double starttime,double endtime,double [][] signal){
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
