package lab411.eeg.p300processing;

import android.util.Log;



public class FeatureVector{
	double value[];
	double label;
	
	public FeatureVector() {
		// TODO Auto-generated constructor stub
		this.value = new double[57];
		for (int i = 0; i < 57; i++)
		{
			value[i] = 0;
		}
		this.label = 0;
	}
	
	public void initialize (double[] value, double label)
	{
		this.value = value;
		this.label = label;
	}

	public double[] getValue() {
		return this.value;
	}
	public void setValue(double value[]) {
		Log.d("Length:", value.length+"");
		if (this.value.length == value.length)
		{
			for (int i = 0; i<value.length;i++)
				this.value[i]=value[i];
		}
	}
	public void setValue(int index, double data)
	{
		value[index] = data;
	}
	public double getLabel() {
		return this.label;
	}
	public void setLabel(double label) {
		this.label = label;
	}
	
	public int getBufferElement ()
	{
		return value.length;
	}
	
}
