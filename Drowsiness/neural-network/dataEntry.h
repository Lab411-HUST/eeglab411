#ifndef _DATAENTRY_H_
#define _DATAENTRY_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

	typedef struct{
		int nInput;
		int nTarget;
		double* pattern;
		double* target;
	}vector;
	
	typedef struct{
		int epochSize;
		vector* epochSet;
	}epoch;

	typedef struct{
		int nTS;
		int nVS;
		epoch* trainingSet;
		epoch* validationSet;
	}dataSet;

	dataSet loadDataFile(const char* filename, int size, int nI, int nT, int eSize);
	
	void vecFree(vector *v);
	
	void epochFree(epoch *e);

	void dataFree(dataSet *d);
	
#endif
