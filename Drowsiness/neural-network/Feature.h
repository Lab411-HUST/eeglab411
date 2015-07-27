#ifndef _FEATURE_H_
#define _FEATURE_H_

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

	typedef struct{
		double E1,E2,E3,E4,E5, E6; //Mean quadratic value or Energy
		double E7;	//Standard deviation
	}extraction;
	int getLengthDownSample(int size);
	int getLengthDWT(int size);
	double *downSample(double *signal, int size);
	double *conv(double *A, double *B, int lenA, int lenB);
	double *dwt_low(double *signal, int lenS);
	double *dwt_high(double *signal, int lenS);
	double meanEnergy(double *input, int lenI, int N);
	double standardDeviation(double *input, int lenI);
	extraction feature_extraction(double *signal, int lenS);
#endif