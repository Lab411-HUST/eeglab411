package lab411.eeg.processSignal;

public class XdawnSpatialFilter {
   private int mSampleCount;
   private double mCoefficient[][]=new double[3][14];
   public XdawnSpatialFilter(int sampleNumber,double[][] coefficient) {
	   mSampleCount=sampleNumber;
	   mCoefficient=coefficient; 
   }
   public double[][] SpatialFilter(double input[][]) {
	   double Oput[][]=new double[3][mSampleCount];
	   for(int i=0;i<3;i++)
		   for(int j=0;j<mSampleCount;j++) {
			   double sum=0;
			   for(int k=0;k<14;k++)
			   sum+=mCoefficient[i][k]*input[k][j];
			   Oput[i][j]=sum;
		   }
	   return Oput;
   }
}
