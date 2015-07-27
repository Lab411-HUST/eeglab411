#include "neuralNetwork.h"

net initializeNet(int in, int hidden, int out)
{
	net ne;

	ne.nInput = in;
	ne.nHidden = hidden;
	ne.nOutput = out;

	int i,j;
	ne.inputNeurons = (double*) malloc(sizeof(double)*in);
	for(i=0; i<in; i++){
		ne.inputNeurons[i] = 0;
	}
	ne.hiddenNeurons = (double*) malloc(sizeof(double)*hidden);
	for(i=0; i<hidden; i++){
		ne.hiddenNeurons[i] = 0;
	}
	ne.outputNeurons = (double*) malloc(sizeof(double)*out);
	for(i=0; i<out; i++){
		ne.outputNeurons[i] = 0;
	}

	ne.wInputHidden = (double**) malloc(sizeof(double*)*in);
	for(i=0; i<in; i++){
		ne.wInputHidden[i] = (double*) malloc(sizeof(double)*hidden);
		for(j=0; j<hidden; j++){
			ne.wInputHidden[i][j] = 0;
		}
	}
	ne.wHiddenOutput = (double**) malloc(sizeof(double*)*hidden);
	for(i=0; i<hidden; i++){
		ne.wHiddenOutput[i] = (double*) malloc(sizeof(double)*out);
		for(j=0; j<out; j++){
			ne.wHiddenOutput[i][j] = 0;
		}
	}

	ne.deltaInputHidden = (double**) malloc(sizeof(double*)*in);
	for(i=0; i<in; i++){
		ne.deltaInputHidden[i] = (double*) malloc(sizeof(double)*hidden);
		for(j=0; j<hidden; j++){
			ne.deltaInputHidden[i][j] = 0;
		}
	}
	ne.deltaHiddenOutput = (double**) malloc(sizeof(double*)*hidden);
	for(i=0; i<hidden; i++){
		ne.deltaHiddenOutput[i] = (double*) malloc(sizeof(double)*out);
		for(j=0; j<out; j++){
			ne.deltaHiddenOutput[i][j] = 0;
		}
	}
	
	initializeWeights(&ne, true);

	ne.hiddenErrorGradients = (double*) malloc(sizeof(double)*hidden);
	for(i=0; i<hidden; i++){
		ne.hiddenErrorGradients[i] = 0;
	}

	ne.outputErrorGradients = (double*) malloc(sizeof(double)*out);
	for(i=0; i<out; i++){
		ne.outputErrorGradients[i] = 0;
	}

	ne.learningRate = LEARNING_RATE;
	ne.momentum = MOMENTUM;

	ne.useBatch = false;

	ne.mse = 0;

	return ne;
}

void netFree(net *ne){
	free((*ne).inputNeurons);
	free((*ne).hiddenNeurons);
	free((*ne).outputNeurons);
	int i,j;
	for(i=0; i<(*ne).nInput; i++){
		free((*ne).wInputHidden[i]);
	}
	free((*ne).wInputHidden);

	for(i=0; i<(*ne).nHidden; i++){
		free((*ne).wHiddenOutput[i]);
	}
	free((*ne).wHiddenOutput);

	for(i=0; i<(*ne).nInput; i++){
		free((*ne).deltaInputHidden[i]);
	}
	free((*ne).deltaInputHidden);

	for(i=0; i<(*ne).nHidden; i++){
		free((*ne).deltaHiddenOutput[i]);
	}
	free((*ne).deltaHiddenOutput);
	
	free((*ne).hiddenErrorGradients);
	free((*ne).outputErrorGradients);
}

void initializeWeights(net *ne, bool bo)
{
	int i,j;
	if(bo){
		srand((unsigned)time(NULL));
	}
	for(i=0; i<(*ne).nInput; i++)
	{
		for(j=0; j<(*ne).nHidden; j++){
			(*ne).wInputHidden[i][j] = (double) rand() / (RAND_MAX + 1.0) - 0.5;
			(*ne).deltaInputHidden[i][j] = 0;
		}
	}
	for(i=0; i<(*ne).nHidden; i++)
	{
		for(j=0; j<(*ne).nOutput; j++){
			(*ne).wHiddenOutput[i][j] = (double)rand() / (RAND_MAX + 1.0) - 0.5;
			(*ne).deltaHiddenOutput[i][j] = 0;
		}
	}
}

void resetWeights(net *ne)
{
	initializeWeights(ne, false);
}

void loadNet(net *ne, char *fileName)
{
	int  i,j;
	FILE *fo = fopen(fileName,"rt");
	char * line = NULL;
	size_t len = 0;
	ssize_t read;
	read = getline(&line, &len, fo);
	read = getline(&line, &len, fo);
	char *s1 = strtok(line, "\n");
	char *token;
	token = strtok(s1, "\t");
	(*ne).nInput = atoi(token);
	token = strtok(NULL, "\t");
	(*ne).nHidden = atoi(token);
	token = strtok(NULL, "\t");
	(*ne).nOutput = atoi(token);
	read = getline(&line, &len, fo);
	for(i=0; i<(*ne).nInput; i++){
		read = getline(&line, &len, fo);
		char *s2 = strtok(line, "\n");
		char *token;
		token = strtok(s2, "\t");
		(*ne).wInputHidden[i][0] = atof(token);
		for(j=1; j<(*ne).nHidden; j++){
			token = strtok(NULL, "\t");
			(*ne).wInputHidden[i][j] = atof(token);
		}
	}
	read = getline(&line, &len, fo);
	for(i=0; i<(*ne).nHidden; i++){
		read = getline(&line, &len, fo);
		char *s2 = strtok(line, "\n");
		char *token;
		token = strtok(s2, "\t");
		(*ne).wHiddenOutput[i][0] = atof(token);
		for(j=1; j<(*ne).nOutput; j++){
			token = strtok(NULL, "\t");
			(*ne).wHiddenOutput[i][j] = atof(token);
		}
	}
	read = getline(&line, &len, fo);
	read = getline(&line, &len, fo);
	char *s3 = strtok(line, "\n");
	token = strtok(s3, "\t");
	(*ne).learningRate = atof(token);
	token = strtok(NULL, "\t");
	(*ne).momentum = atof(token);

	fclose(fo);
	free(line);
}

void setLearningParameters(net *ne, double lr, double m)
{
	(*ne).learningRate = lr;
	(*ne).momentum = m;
}

//enable batch learning
void useBatchLearning(net *ne)
{
	(*ne).useBatch = true;
}

//enable stochastic learning
void useStochasticLearning(net *ne)
{
	(*ne).useBatch = false;
}

double activationFunction(double x)
{
	return (double) 1.0/(1+exp(-x));
}

void feedForward(net *ne, double *inputs)
{
	int i, j;
	for(i=0; i<(*ne).nInput; i++){
		(*ne).inputNeurons[i] = inputs[i];
	}
	for(i=0; i<(*ne).nHidden; i++){
		(*ne).hiddenNeurons[i] = 0;
		for(j=0; j<(*ne).nInput; j++){
			(*ne).hiddenNeurons[i] += (*ne).inputNeurons[j] * (*ne).wInputHidden[j][i];
		}
		(*ne).hiddenNeurons[i] = activationFunction((*ne).hiddenNeurons[i]);
	}
	for(i=0; i<(*ne).nOutput; i++){
		(*ne).outputNeurons[i] = 0;
		for(j=0; j<(*ne).nHidden; j++){
			(*ne).outputNeurons[i] += (*ne).hiddenNeurons[j] * (*ne).wHiddenOutput[j][i];
		}
		(*ne).outputNeurons[i] = activationFunction((*ne).outputNeurons[i]);
	}
}
void errorGradients(net *ne, double* desiredValues)
{
	int i, j;
	double sum;
	for(i=0; i<(*ne).nOutput; i++){
		(*ne).outputErrorGradients[i] = (*ne).outputNeurons[i]*(1 - (*ne).outputNeurons[i])*(desiredValues[i] - (*ne).outputNeurons[i]);
	}
	for(i=0; i<(*ne).nHidden; i++){
		sum = 0;
		for(j=0; j<(*ne).nOutput; j++){
			sum += (*ne).outputErrorGradients[j] * (*ne).wHiddenOutput[i][j];
		}
		(*ne).hiddenErrorGradients[i] = (*ne).hiddenNeurons[i] * (1 - (*ne).hiddenNeurons[i]) * sum;
	}
}
void stochasticLearning(net *ne, epoch e)
{
	int i, j, k;
	
	(*ne).mse =0;
	for(k=0; k<e.epochSize; k++){
		feedForward(ne, e.epochSet[k].pattern);
		for(i=0; i<(*ne).nOutput; i++){
			(*ne).mse += 0.5*pow((*ne).outputNeurons[i] - e.epochSet[k].target[i], 2);
		}
		errorGradients(ne, e.epochSet[k].target);
		for(i=0; i<(*ne).nOutput; i++){
			for(j=0; j<(*ne).nHidden; j++){
				(*ne).deltaHiddenOutput[j][i] = (*ne).learningRate * (*ne).hiddenNeurons[j] * (*ne).outputErrorGradients[i] + (*ne).momentum * (*ne).deltaHiddenOutput[j][i];
			}
		}
		for(i=0; i<(*ne).nHidden; i++){
			for(j=0; j<(*ne).nInput; j++){
				(*ne).deltaInputHidden[j][i] = (*ne).learningRate * (*ne).inputNeurons[j] * (*ne).hiddenErrorGradients[i] + (*ne).momentum * (*ne).deltaInputHidden[j][i];
			}
		}
		for(i=0; i<(*ne).nInput; i++){
			for(j=0; j<(*ne).nHidden; j++){
				(*ne).wInputHidden[i][j] += (*ne).deltaInputHidden[i][j];
			}
		}
		for(i=0; i<(*ne).nHidden; i++){
			for(j=0; j<(*ne).nOutput; j++){
				(*ne).wHiddenOutput[i][j] += (*ne).deltaHiddenOutput[i][j];
			}
		}
	}
}
void batchLearning(net *ne, epoch e)
{
	int i, j, k;
	(*ne).mse =0;
	for(k=0; k<e.epochSize; k++){
		feedForward(ne, e.epochSet[k].pattern);
		for(i=0; i<(*ne).nOutput; i++){
			(*ne).mse += 0.5*pow((*ne).outputNeurons[i] - e.epochSet[k].target[i], 2);
		}
		errorGradients(ne, e.epochSet[k].target);
		for(i=0; i<(*ne).nOutput; i++){
			for(j=0; j<(*ne).nHidden; j++){
				(*ne).deltaHiddenOutput[j][i] += (*ne).learningRate * (*ne).hiddenNeurons[j] * (*ne).outputErrorGradients[i] ;
			}
		}
		for(i=0; i<(*ne).nHidden; i++){
			for(j=0; j<(*ne).nInput; j++){
				(*ne).deltaInputHidden[j][i] += (*ne).learningRate * (*ne).inputNeurons[j] * (*ne).hiddenErrorGradients[i] ;
			}
		}
	}
	for(i=0; i<(*ne).nInput; i++){
		for(j=0; j<(*ne).nHidden; j++){
			(*ne).wInputHidden[i][j] += (*ne).deltaInputHidden[i][j];
			(*ne).deltaInputHidden[i][j] = 0;
		}
	}
	for(i=0; i<(*ne).nHidden; i++){
		for(j=0; j<(*ne).nOutput; j++){
			(*ne).wHiddenOutput[i][j] += (*ne).deltaHiddenOutput[i][j];
			(*ne).deltaHiddenOutput[i][j] = 0;
		}
	}
}

void trainNetwork(net *ne, dataSet d, double MAXERROR, int LOOP)
{
	FILE *fo = fopen("ERROR.txt","wt");

	int count = 0;
	int i = 0;
	do
	{
		if((*ne).useBatch){
			batchLearning(ne, d.trainingSet[i]);
		}else{
			stochasticLearning(ne, d.trainingSet[i]);
		}

		fprintf(fo,"%s\t%d\n","Epoch",i);
		fprintf(fo,"%lf\n",(*ne).mse);

		if(i == (d.nTS-1))
			i=0;
		else
			i++;
		count ++;
		
		if((*ne).mse < MAXERROR){
			break;
		}
	}while(count < LOOP);
    fclose(fo);
}
void getRoundedOutputValue(net *ne)
{
	int i;
	for(i=0; i<(*ne).nOutput; i++){
		if((*ne).outputNeurons[i] < 0.1)
			(*ne).outputNeurons[i] = 0.0;
		else if((*ne).outputNeurons[i] > 0.9)
			(*ne).outputNeurons[i] = 1.0;
		else
			(*ne).outputNeurons[i] = -1.0;
	}
}	

bool compare(double *a, double *b, int size)
{
	int i;
	for(i=0; i<size; i++){
		if(a[i]!=b[i])
			return false;
	}
	return true;
}

void testNetwork(net *ne, dataSet d)
{
	double incorrectResults = 0;
	double mse = 0;
	int i, j, k;
	for(i=0; i<d.nVS; i++){
		printf("Epoch %d\n", i);
		mse = 0;
		for(j=0; j<d.validationSet[i].epochSize; j++){
			feedForward(ne, d.validationSet[i].epochSet[j].pattern);
			
			int m;
			for(m=0; m<(*ne).nOutput; m++){
				printf("%lf\t", (*ne).outputNeurons[m]);
			}
			printf("\n");


			for(k=0; k<(*ne).nOutput; k++){
				mse += 0.5 * pow((*ne).outputNeurons[k] - d.validationSet[i].epochSet[j].target[k], 2);
			}
			getRoundedOutputValue(ne);
			if(compare((*ne).outputNeurons, d.validationSet[i].epochSet[j].target, (*ne).nOutput))
				incorrectResults++;		
		}
		printf("MSE: %lf\n", mse);
	}
	double accuracy = incorrectResults * 100 / (d.nVS * d.validationSet[0].epochSize);
	printf("Accuracy: %.2lf ", accuracy);
	printf("%s\n", "%");
}

void netSave(net ne, char *fileName)
{
	int i,j;
	FILE *fo = fopen(fileName,"wt");
	
    fprintf(fo,"%s","nInput\tnHidden\tnOutput\n");
    fprintf(fo,"%d\t%d\t%d\n",ne.nInput, ne.nHidden, ne.nOutput);
    fprintf(fo,"%s","Weights Input Hidden\n");
    for(i=0; i<ne.nInput; i++){
    	for(j=0; j<ne.nHidden; j++){
    		fprintf(fo,"%lf\t", ne.wInputHidden[i][j]);
    	}
    	fprintf(fo,"%s","\n");
    }
	fprintf(fo,"%s","Weights Hidden Output\n");
    for(i=0; i<ne.nHidden; i++){
    	for(j=0; j<ne.nOutput; j++){
    		fprintf(fo,"%lf\t", ne.wHiddenOutput[i][j]);
    	}
    	fprintf(fo,"%s","\n");
    }
    fprintf(fo,"%s","LearningRate\tMomentum\n");
    fprintf(fo,"%lf\t%lf",ne.learningRate, ne.momentum);
    fclose(fo);
}