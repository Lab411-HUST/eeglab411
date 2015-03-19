//#include "stdafx.h"
#include "math.h"
#include <stdio.h>
#include <stdlib.h>

int *bubbleS(int *arr, int len)
{
	int p, i;
    int h;
    for (p = 0; p < len - 1; p++) 
	{
        for(i = 0; i < len - 1; i++) 
		{
            if(arr[i] > arr[i + 1]) 
			{
                h = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = h;
            }
        }
    }
	
    return arr;
}

int *run_p300_process(int *arr, int len, int lowerIndex, int upperIndex)
{
	int i,k;
	int *temp; 
	int lowerValue, upperValue;
	temp = int_vector_generation(len);
	temp=bubbleS(arr, len);
	lowerValue = temp[lowerIndex];
	upperValue = temp[upperIndex];
	for(i=0;i<len;i++)
	{
		if(arr[i] > upperValue)
			arr[i] = upperValue;

		if(arr[i] < lowerValue)
			arr[i] = lowerValue;
	}


	return arr;
}


long int *p300_process( int *input , int interval,  int sample_time, int lowerIndex, int upperIndex)
{
	int i, j, k, l;
	
	int *temp_out;
	long int  *output;

	temp_out = int_vector_generation(sample_time);
	output = vector_generation(sample_time);

	
	j=0;
	for(i=0; i<sample_time; i=i+interval)
	{
		temp_out[j] = input[i];
		j++;
	}
	

	output = run_p300_process(temp_out, j,lowerIndex, upperIndex);
	
	
	

	return output;

}

