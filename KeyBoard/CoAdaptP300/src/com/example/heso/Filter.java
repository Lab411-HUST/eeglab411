package com.example.heso;

import android.util.Log;

public class Filter {
	public int numberChanel;
	public int sizeEpoch;
	public double[] curentSate=new double[8];
	double[] m_vecNumCoefFilter=new double[9];
	double[] m_vecDenomCoefFilter=new double[9];
	double[] Zi=new double[8]; 
	public boolean check=true;
	// double[] output=new double[32];
	public Filter(int numberchanel,int sizepoch,int heso){
		numberChanel=numberchanel;
		sizeEpoch=sizepoch;
		//for(int i=0;i<9;i++){
		//m_vecNumCoefFilter[i]=heso[i];
		//m_vecDenomCoefFilter[i]=heso[i+9];
			m_vecNumCoefFilter[0]=0.0179426;
			m_vecNumCoefFilter[1]=0.0;
			m_vecNumCoefFilter[2]=-0.0717702;
			m_vecNumCoefFilter[3]=0.0;
			m_vecNumCoefFilter[4]=0.1076553;
			m_vecNumCoefFilter[5]=0.0;
			m_vecNumCoefFilter[6]=-0.0717702;
			m_vecNumCoefFilter[7]=0.0;
			m_vecNumCoefFilter[8]=0.0179426;
		//}
		//for(int i=0;i<9;i++){
			m_vecDenomCoefFilter[0]=1.0;
			m_vecDenomCoefFilter[1]=-5.4502576;
			m_vecDenomCoefFilter[2]=13.0637849;
			m_vecDenomCoefFilter[3]=-18.1814747;
			m_vecDenomCoefFilter[4]=16.2218763;
			m_vecDenomCoefFilter[5]=-9.5361932;
			m_vecDenomCoefFilter[6]=3.5938264;
			m_vecDenomCoefFilter[7]=-0.7900007;
			m_vecDenomCoefFilter[8]=0.0784406;
		//}
		//
		check=true;
		//
		Zi[0]=-0.0179426;
        Zi[1]=-0.0179426;
        Zi[2]=0.0538277;
        Zi[3]=0.0538277;
        Zi[4]=-0.0538277;
        Zi[5]=-0.0538277;
        Zi[6]=0.0179426;
        Zi[7]=0.0179426;

        for(int i=0;i<8;i++) 
		//curentSate[i]=Zi[i]*4442;//input[0];
        curentSate[i]=Zi[i]*heso;
	}

	   public double[] IIR_Filter (double[] b, double[] a, double[] data, double[] V0,
				double[] dataFiltered, double[] Vf)
		{
			int i,j,i_V0=0;
			double Sum_a_tmp,Sum_b_tmp;
			double Sum_tmp_Vf;
			double V0_tmp;
			int nb, dataSize;
			double[] state=new double[8]; 
			// FIXME is it necessary to keep next line uncomment ?
			//int na = length(a);
			nb = b.length;//length(b);
			dataSize = data.length;//length(data);
			double jk=0;
			int kj=0;
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
					jk=Sum_b_tmp - Sum_a_tmp + V0[i];								
					dataFiltered[i] = jk;
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
					Vf[i] = Sum_tmp_Vf +  V0_tmp;
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
					jk=Sum_b_tmp - Sum_a_tmp + V0[i];
					//kj=convert(jk);
					dataFiltered[i] =jk; 	
					//ouput1[i]=jk;
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
					//ouput1[i]=jk;
				}
			       // set o day
				   // output=ouput1;//dataFiltered;
				for (i=0;i<nb-1;i++)
				{	
					Sum_tmp_Vf=0.0;
					for (j=i;j<nb-1;j++)
					{
						Sum_tmp_Vf = Sum_tmp_Vf + (b[j+1] * data[dataSize-1-j+i]) - (a[j+1] * dataFiltered[dataSize-1-j+i]);
					}
					Vf[i] = Sum_tmp_Vf;
				}
				state=Vf;
			}
	       return state;
	       
		}
   public double [] getcur(){
	   return curentSate;
   }
   public double[] getSignal(double[] input){
		 
	   double[]  cur =new double[8];
	   cur=curentSate;
	   double[] output=new double[32];
	   curentSate= IIR_Filter(m_vecNumCoefFilter, m_vecDenomCoefFilter, input, cur, output,cur) ;
	  return output;
   }
   public int convert(double a){
	   String c="",d="";
	   c=Double.toString(a);
	  // boolean check=false;
	   char h;
	   for(int i=0;i<c.length();i++){
		   h=c.charAt(i);
		   if(h!='.')
			   d=d+h;
		   else
			   break;
		
	   }
	   int g=Integer.parseInt(d);
	   return g;
   }

}
