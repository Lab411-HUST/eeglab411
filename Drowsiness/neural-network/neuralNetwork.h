#ifndef _NEURALNETWORK_H_
#define _NEURALNETWORK_H_
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

//Constant Defaults!
#define LEARNING_RATE 0.001
#define MOMENTUM 0.08

#include "dataEntry.h"
	typedef struct{
		//number of neurons
		int nInput, nHidden, nOutput;
	
		//neurons
		double* inputNeurons;
		double* hiddenNeurons;
		double* outputNeurons;

		//weights
		double** wInputHidden;
		double** wHiddenOutput;
		
		//change to weights
		double** deltaInputHidden;
		double** deltaHiddenOutput;

		//error gradients
		double* hiddenErrorGradients;
		double* outputErrorGradients;

		//learning parameters
		double learningRate;
		double momentum;

		//batch learning flag
		bool useBatch;

		double mse;

	}net;
	net initializeNet(int in, int hidden, int out);
	void netFree(net *ne);
	void initializeWeights(net *ne, bool bo);
	void resetWeights(net *ne);
	void loadNet(net *ne, char *fileName);
	void setLearningParameters(net *ne, double lr, double m);
	void useBatchLearning(net *ne);
	void useStochasticLearning(net *ne);
	double activationFunction(double x);
	void feedForward(net *ne, double *inputs);
	void errorGradients(net *ne, double* desiredValues);
	void stochasticLearning(net *ne, epoch e);
	void batchLearning(net *ne, epoch e);
	void trainNetwork(net *ne, dataSet d, double MAXERROR, int LOOP);
	void getRoundedOutputValue(net *ne);
	bool compare(double *a, double *b, int size);
	void testNetwork(net *ne, dataSet d);
	void netSave(net ne, char *fileName);
#endif