// the FIR filter function
int *firfilter( int *input, int *coeffs, int length )
{
    long int acc;     // accumulator for MACs
    int *coeffp; // pointer to coefficients
    int *inputp; // pointer to input samples
    int n;
    int k;
	int filter_order = 5, filterLength ;
    int  *output;
	//output = int_vector_generation(length);
	filterLength = filter_order +1;

   output = int_vector_generation(length);
   coeffp = int_vector_generation(filterLength);
	
    // apply the filter to each input sample
    for ( n = 0; n < length; n++ )
	{
        // calculate output n
        coeffp = coeffs;
        inputp = &input[filterLength - 1 + n];
        
		acc = 0;
        // perform the multiply-accumulate
        for ( k = 0; k < filterLength; k++ )
		{
            acc += (int)(*coeffp++) * (int)(*inputp--);
        }

		output[n] = acc;
        
    }

    
	return output;

}
