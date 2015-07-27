#include "dataEntry.h"

dataSet loadDataFile(const char* filename, int size, int nI, int nT, int eSize)
{
	int i,j,k;
	dataSet d;
	d.nTS = (int) (0.8*size/eSize);
	d.nVS = (int) ((size - d.nTS*eSize)/eSize);
	
	d.trainingSet = (epoch*) malloc(sizeof(epoch)*d.nTS);
	d.validationSet = (epoch*) malloc(sizeof(epoch)*d.nVS);

	for(i=0; i<d.nTS; i++){
		d.trainingSet[i].epochSize = eSize;
		d.trainingSet[i].epochSet = (vector*) malloc(sizeof(vector)*eSize);
		for(j=0; j<eSize; j++){
			d.trainingSet[i].epochSet[j].nInput = nI;
			d.trainingSet[i].epochSet[j].nTarget = nT;
			d.trainingSet[i].epochSet[j].pattern = (double*) malloc(sizeof(double)*nI);
			d.trainingSet[i].epochSet[j].target = (double*) malloc(sizeof(double)*nT);
		}
	}

	for(i=0; i<d.nVS; i++){
		d.validationSet[i].epochSize = eSize;
		d.validationSet[i].epochSet = (vector*) malloc(sizeof(vector)*eSize);
		for(j=0; j<eSize; j++){
			d.validationSet[i].epochSet[j].nInput = nI;
			d.validationSet[i].epochSet[j].nTarget = nT;
			d.validationSet[i].epochSet[j].pattern = (double*) malloc(sizeof(double)*nI);
			d.validationSet[i].epochSet[j].target = (double*) malloc(sizeof(double)*nT);
		}
	}

	FILE *fo = fopen(filename,"rt");
	char * line = NULL;
	size_t len = 0;
	ssize_t read;

	for(i=0; i<d.nTS; i++){
		for(j=0; j<eSize; j++){
			read = getline(&line, &len, fo);
			char *s = strtok(line, "\n");
	   		char *token;
	   		token = strtok(s, "\t");
	   		d.trainingSet[i].epochSet[j].pattern[0] = atof(token);
	   		for(k=1; k<nI; k++){
	   			token = strtok(NULL, "\t");
	   			d.trainingSet[i].epochSet[j].pattern[k] = atof(token);
	   		}
	   		for(k=0; k<nT; k++){
	   			token = strtok(NULL, "\t");
	   			d.trainingSet[i].epochSet[j].target[k] = atof(token);
	   		}
		}
	}
	for(i=0; i<d.nVS; i++){
		for(j=0; j<eSize; j++){
			read = getline(&line, &len, fo);
			char *s = strtok(line, "\n");
	   		char *token;
	   		token = strtok(s, "\t");
	   		d.validationSet[i].epochSet[j].pattern[0] = atof(token);
	   		for(k=1; k<nI; k++){
	   			token = strtok(NULL, "\t");
	   			d.validationSet[i].epochSet[j].pattern[k] = atof(token);
	   		}
	   		for(k=0; k<nT; k++){
	   			token = strtok(NULL, "\t");
	   			d.validationSet[i].epochSet[j].target[k] = atof(token);
	   		}
		}
	}
	fclose(fo);
	free(line);
	return d;
}
void vecFree(vector *v)
{
	free((*v).pattern);
	free((*v).target);
}
void epochFree(epoch *e)
{
	int i;
	for(i=0; i<(*e).epochSize; i++){
		vecFree(&((*e).epochSet[i]));
	}
	free((*e).epochSet);
}
void dataFree(dataSet *d)
{
	int i;
	for(i=0; i<(*d).nTS; i++){
		epochFree(&((*d).trainingSet[i]));
	}
	free((*d).trainingSet);
	for(i=0; i<(*d).nVS; i++){
		epochFree(&((*d).validationSet[i]));
	}
	free((*d).validationSet);
}
