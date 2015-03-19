int LDAtest(long int *inputs, int *weights)
{
	int i, j;
	int sum ;
	int output;
	const int number_of_features = 80;


	//output = int_vector_generation(number_of_trial);


	sum = 0;
	for(i=0; i<number_of_features; i++)
	{
		sum += inputs[i]*weights[i];
	}

	if(sum > 0)
		output= 1;
	else
		output= 0;
	
	

	return output;
}
