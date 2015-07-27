#include <stdio.h>
#include <stdlib.h>

#include "dataEntry.h"
#include "neuralNetwork.h"
#include "Feature.h"

double *readDouble(char *file){
    int i;
    int size=0;
    char junk[10];
    double *val = malloc(sizeof(double)*10000000);
    FILE *fo = fopen (file,"rt");
 
    while(!feof(fo)) 
    {
        fscanf(fo,"%lf",&val[size]);
        fscanf(fo,"%[ \n\t]s",junk);
        size++;
    }
    fclose(fo);
    double *result = malloc(sizeof(double)*size);
    for(i=0;i<size;i++)
    {
        result[i]=val[i];
    }
    free(val);
    return result;
}

int main(int argc, char const *argv[])
{
	int i,j, k;
	dataSet d = loadDataFile("data.txt", 20, 7, 2, 4);
	net ne = initializeNet(7, 10, 2);
	setLearningParameters(&ne, 0.01, 0.7);
	useStochasticLearning(&ne);
	trainNetwork(&ne, d, 0.001, 100000);
	testNetwork(&ne, d);
	netSave(ne, "NET.txt");
	netFree(&ne);
	dataFree(&d);
	
	return 0;
}