package lab411.eeg.process_signal;

public class MedianFilter {
	int mWindowSize=0;
    public MedianFilter(int windowSize) {
    	mWindowSize=windowSize;
    }
    double[] SortUp(double[] input) {
		
		for(int i=0;i<input.length;i++)
			for(int j=i+1;j<input.length;j++) {
				if(input[i]>input[j]) {
					double a=input[i];
					input[i]=input[j];
					input[j]=a;
				}
			}
				
		return input;
	}
   public double[] OnMedianFilter(double[] input) {
    	int size=mWindowSize;
		double output[]=new double[input.length];
		double input1[]=new double[input.length+2*size];
		//them pt vao dau va cuoi chuoi
		for(int i=0;i<input1.length;i++) {
			   if(i<size)
			   input1[i]=input[0];
			   if(i>=input.length)
			   input1[i]=input[input.length-1];
			   if((i>=size)&&(i<(input.length+size)))
			   input1[i]=input[i-size]; 	   
		}
		//
		for(int i=0;i<input.length;i++) {
			double arr[]=new double[2*size+1];
			for(int j=0;j<arr.length;j++)
				arr[j]=input1[i+j];
			arr=SortUp(arr);
			output[i]=arr[size];
		}
		return output;
	}
}
