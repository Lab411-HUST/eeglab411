#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include "predict.h"
#include "./svm/svm-predict.h"

#define LOG_TAG "PREDICT"
#define PARA_LEN 12
int predict(int probability_estimate,const char *classifyFile, const char *modelFile, const char *outputFile)
{
	//LOGD("Cai DECK");
	//LOGD("%s, %s, %s, %d", classifyFile,modelFile,outputFile,probability_estimate);
	int cmdLenght=6;
	char *cmd[cmdLenght];
    cmd[0]="donotcare";
    cmd[1]= "-b";
    cmd[2] = (char *)calloc(PARA_LEN, sizeof(char));
        sprintf(cmd[2], "%d", probability_estimate);
    int l = strlen(classifyFile);
    cmd[3]=(char *)calloc(l+1, sizeof(char));
    strncpy(cmd[3], classifyFile, l);
    cmd[3][l] ='\0';

    l = strlen(modelFile);
    cmd[4]=(char *)calloc(l+1, sizeof(char));
    strncpy(cmd[4], modelFile,l);
    cmd[4][l]='\0';

    l = strlen(outputFile);
    cmd[5]=(char *)calloc(l+1,sizeof(char));
    strncpy(cmd[5],outputFile,l);
    cmd[5][l]='\0';
    //LOGD("%s, %s", cmdLenght, cmd);

    int result = svmpredict(cmdLenght, cmd);
    free(cmd[2]);
    cmd[2] = NULL;
    free(cmd[3]);
    cmd[3] = NULL;
    free(cmd[4]);
    cmd[4] = NULL;
    free(cmd[5]);
    cmd[5] = NULL;
   // LOGD("CMD svm predict: (%s, %s )", cmdLenght, cmd);
    return result;
}
