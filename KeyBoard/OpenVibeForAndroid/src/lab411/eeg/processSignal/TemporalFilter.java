package lab411.eeg.processSignal;

public class TemporalFilter {
	private int NUMBER_CHANEL=14;
	private int NUMBER_SAMPLE_PER_CHANEL=32;
	private double [] mHesoZi=new double[8];
	private double [] mSate=new double[8];
	private double []  mCurrenSate1=new double[8];
	double[] m_vecNumCoefFilter=new double[9];
	double[] m_vecDenomCoefFilter=new double[9];
	private double [][] mCurrenState=new double[NUMBER_CHANEL][8];
	
	public TemporalFilter(double heso){
		 mHesoZi[0]=-0.0179426;
		 mHesoZi[1]=-0.0179426;
		 mHesoZi[2]=0.0538277;
		 mHesoZi[3]=0.0538277;
		 mHesoZi[4]=-0.0538277;
		 mHesoZi[5]=-0.0538277;
		 mHesoZi[6]=0.0179426;
		 mHesoZi[7]=0.0179426;
		 for(int i=0;i<8;i++) {
			 mCurrenSate1[i]=mHesoZi[i]*heso;
		 }
	   m_vecNumCoefFilter[0]=0.017942552127494446;
  	   m_vecNumCoefFilter[1]=0.0;
  	   m_vecNumCoefFilter[2]=-0.07177020850997778;
  	   m_vecNumCoefFilter[3]=0.0;
  	   m_vecNumCoefFilter[4]=0.10765531276496668;
  	   m_vecNumCoefFilter[5]=0.0;
  	   m_vecNumCoefFilter[6]=-0.07177020850997778;
  	   m_vecNumCoefFilter[7]=0.0;
  	   m_vecNumCoefFilter[8]=0.017942552127494446;
  			//}
  			//for(int i=0;i<9;i++){
  	   m_vecDenomCoefFilter[0]=1.0;
  	   m_vecDenomCoefFilter[1]=-5.450257632587539;
  	   m_vecDenomCoefFilter[2]=13.063784931494567;
  	   m_vecDenomCoefFilter[3]=-18.181474714341324;
  	   m_vecDenomCoefFilter[4]=16.22187626471586;
  	   m_vecDenomCoefFilter[5]=-9.536193194342996;
  	   m_vecDenomCoefFilter[6]=3.593826443228763;
  	   m_vecDenomCoefFilter[7]=-0.7900006652040495;
  	   m_vecDenomCoefFilter[8]=0.07844058032831629;
	}
	
    public TemporalFilter(double[] mHs ){
    	 mHesoZi[0]=-0.0179426;
		 mHesoZi[1]=-0.0179426;
		 mHesoZi[2]=0.0538277;
		 mHesoZi[3]=0.0538277;
		 mHesoZi[4]=-0.0538277;
		 mHesoZi[5]=-0.0538277;
		 mHesoZi[6]=0.0179426;
		 mHesoZi[7]=0.0179426;
		 
		 for(int i=0;i<NUMBER_CHANEL;i++) { 
			 for(int j=0;j<8;j++)
			mCurrenState[i][j]=mHesoZi[j]*mHs[i];
		 }
		 
    	   m_vecNumCoefFilter[0]=0.017942552127494446;
    	   m_vecNumCoefFilter[1]=0.0;
    	   m_vecNumCoefFilter[2]=-0.07177020850997778;
    	   m_vecNumCoefFilter[3]=0.0;
    	   m_vecNumCoefFilter[4]=0.10765531276496668;
    	   m_vecNumCoefFilter[5]=0.0;
    	   m_vecNumCoefFilter[6]=-0.07177020850997778;
    	   m_vecNumCoefFilter[7]=0.0;
    	   m_vecNumCoefFilter[8]=0.017942552127494446;
    			//}
    			//for(int i=0;i<9;i++){
    	   m_vecDenomCoefFilter[0]=1.0;
    	   m_vecDenomCoefFilter[1]=-5.450257632587539;
    	   m_vecDenomCoefFilter[2]=13.063784931494567;
    	   m_vecDenomCoefFilter[3]=-18.181474714341324;
    	   m_vecDenomCoefFilter[4]=16.22187626471586;
    	   m_vecDenomCoefFilter[5]=-9.536193194342996;
    	   m_vecDenomCoefFilter[6]=3.593826443228763;
    	   m_vecDenomCoefFilter[7]=-0.7900006652040495;
    	   m_vecDenomCoefFilter[8]=0.07844058032831629;
    }
    public double [] IIR_Filter (double[] b, double[] a, double[] data,
    		                     double[] V0 )
    {
    	int i,j,i_V0=0;
    	double Sum_a_tmp,Sum_b_tmp;
    	double Sum_tmp_Vf;
    	double V0_tmp;
    	int nb, dataSize;
    	// FIXME is it necessary to keep next line uncomment ?
    	//int na = length(a);
    	nb =b.length ;//length(b);
    	dataSize =data.length ;//length(data);
    	double[] dataFiltered=new double[dataSize];	
    	if(dataSize<nb)
    	{
    		for (i=0;i<dataSize;i++)
    		{
    			Sum_b_tmp = 0.0;
    			for (j=0;j<=i;j++)
    			{
    				Sum_b_tmp = Sum_b_tmp + (b[j] * data[i-j]);
    			}	


    			Sum_a_tmp = 0.0;
    	
    			for (j=0;j<=i;j++)
    			{
    				Sum_a_tmp = Sum_a_tmp + (a[j] * dataFiltered[i-j]);
    			}
    			dataFiltered[i] = Sum_b_tmp - Sum_a_tmp + V0[i];

    		}

    		for (i=0;i<(nb-1);i++)
    		{
    			Sum_tmp_Vf=0.0;
    			V0_tmp = 0.0;
    			for (j=0;j<(nb-1);j++)
    			{
    				if ((i+j)<(nb-1))
    				{
    					if ((dataSize-1-j) >= 0)
    					{
    						Sum_tmp_Vf = Sum_tmp_Vf + (b[i+j+1] * data[dataSize-1-j]) - (a[i+j+1]*dataFiltered[dataSize-1-j]);
    						i_V0 = i+j+1;
    					}
    					if ((dataSize-1-j) < 0)
    					{
    						V0_tmp = V0[i_V0];
    					}
    				}
    			}
    			mSate[i] = Sum_tmp_Vf +  V0_tmp;//Vf[i] = Sum_tmp_Vf +  V0_tmp;
    		}
    	}
    	else
    	{

    		for (i=0;i<nb-1;i++)
    		{
    			Sum_b_tmp = 0.0;
    			for (j=0;j<=i;j++)
    			{
    				Sum_b_tmp = Sum_b_tmp + (b[j] * data[i-j]);
    			}	
    			Sum_a_tmp = 0.0;
    	
    			for (j=0;j<=i;j++)
    			{
    				Sum_a_tmp = Sum_a_tmp + (a[j] * dataFiltered[i-j]);
    			}
    			dataFiltered[i] = Sum_b_tmp - Sum_a_tmp + V0[i];	

    		}
    	
    	
    		for (i=nb-1;i<dataSize;i++)
    		{
    			Sum_b_tmp = 0.0;
    			for (j=0;j<nb;j++)
    			{
    				Sum_b_tmp = Sum_b_tmp + (b[j] * data[i-j]);
    			}
    			Sum_a_tmp = 0.0;
    			for (j=0;j<nb;j++)
    			{
    				Sum_a_tmp = Sum_a_tmp + (a[j] * dataFiltered[i-j]);
    			}	
    			dataFiltered[i] = Sum_b_tmp - Sum_a_tmp;
    		}
    	
    		for (i=0;i<nb-1;i++)
    		{	
    			Sum_tmp_Vf=0.0;
    			for (j=i;j<nb-1;j++)
    			{
    				Sum_tmp_Vf = Sum_tmp_Vf + (b[j+1] * data[dataSize-1-j+i]) - (a[j+1] * dataFiltered[dataSize-1-j+i]);
    			}


    			mSate[i] = Sum_tmp_Vf;//Vf[i] = Sum_tmp_Vf;
    		}
    	}
    	
        return dataFiltered;  

    }
    public double[] filterOneChanel(double [] input){
    	double[] output=new double[32];
    	double[] state=new double[8];
    	for(int i=0;i<8;i++)
    	state[i]=mCurrenSate1[i];
    	output=IIR_Filter(m_vecNumCoefFilter, m_vecDenomCoefFilter, input, state);
    	for(int j=0;j<8;j++)
    	mCurrenSate1[j]=mSate[j];
    	return output;
    }
    public double[][] onFilterMutilChanale(double[][] mInput){
    	  double[][] mOutput=new double[NUMBER_CHANEL][NUMBER_SAMPLE_PER_CHANEL];
    	  double []  mInput1=new double[NUMBER_SAMPLE_PER_CHANEL];
    	  double []  mOutput1=new double[NUMBER_SAMPLE_PER_CHANEL];
    	  for(int i=0;i<NUMBER_CHANEL;i++) {
    		  for(int j=0;j<NUMBER_SAMPLE_PER_CHANEL;j++)
    		   mInput1[j]=mInput[i][j];
    		  // 
    		  double[] mSate1=new double[8];
    		  for(int t=0;t<8;t++)
    		  mSate1[t]=mCurrenState[i][t];
    		  mOutput1= IIR_Filter(m_vecNumCoefFilter,m_vecDenomCoefFilter, mInput1, mSate1);
    		  for(int t=0;t<8;t++) {
        	   mCurrenState[i][t]=mSate[t];
        	   mSate[t]=0;
    		  }
    		  for(int k=0;k<NUMBER_SAMPLE_PER_CHANEL;k++)
    			  mOutput[i][k]=mOutput1[k];
    	  }
    	  return mOutput;
    }

}
