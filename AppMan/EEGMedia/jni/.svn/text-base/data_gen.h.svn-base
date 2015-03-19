//#include "stdafx.h"
#include "math.h"
#include <stdio.h>
#include <stdlib.h>


int **data_generation(FILE *fp, int first_dimension, int second_dimension)
{
	int i,j;
	int **data;

        
	int inval;

	data = (int **)malloc((unsigned)(first_dimension*sizeof(int *)));
	for(i=0;i<first_dimension; i++)
		data[i] = (int *)malloc(second_dimension*sizeof(int));
	for(i=0;i<first_dimension; i++)
		for(j=0;j<second_dimension; j++)
		{
			fscanf(fp, "%d", &inval);
			data[i][j] = inval;
		}
	
	return data;
}

int **array_generation(int first_dimension, int second_dimension)
{
	int i,j;
	int **data;

	data = (int **)malloc((unsigned)(first_dimension*sizeof(int *)));
	for(i=0;i<first_dimension; i++)
		data[i] = (int *)malloc(second_dimension*sizeof(int));


	
	return data;
}

long int *vector_generation(long int length)
{
	int i,j;
	long int *data;

	data = (long int *)malloc((unsigned)(length*sizeof(long int)));



	
	return data;
}

int *int_vector_generation(long int length)
{
	int i,j;
	int *data;

	data = ( int *)malloc((unsigned)(length*sizeof( int)));
	
	return data;
}

int *int_vector_generation_withinitialization(FILE *fp,long int length)
{
	int i,j;
	int *data;
	int inval;


	data = ( int *)malloc((unsigned)(length*sizeof( int)));
	for(j=0;j<length; j++)
	{
		fscanf(fp, "%d", &inval);
		data[j] = inval;
	}

	return data;
}
long int *longint_vector_generation_withinitialization(FILE *fp,long int length)
{
	long int i,j;
	long int *data;
	int inval;

	data = ( long int *)malloc((unsigned)(length*sizeof( long int)));
	for(j=0;j<length; j++)
	{
		fscanf(fp, "%d", &inval);
		data[j] = inval;
	}
	return data;
}
