#ifndef __HFD_H__
#define __HFD_H__

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define SAMPLE 256

double* readData(char *pathFile, int N);
int kMax(int n);
double hfd(double *signal,int N);
double lsq(int n, double *vx, double *vy);

#endif

