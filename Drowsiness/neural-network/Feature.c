#include "Feature.h"

int getLengthDownSample(int size)
{
	return (int)(size/2);
}
int getLengthDWT(int size)
{
	return getLengthDownSample(size+3);
}
double *downSample(double *signal, int size)
{
	int n = getLengthDownSample(size);
	double *result = (double*) malloc(sizeof(double)*n);
	int i,j;
	j=1;
	for(i=0; i<n; i++){
		result[i] = signal[j];
		j=j+2;
	}
	return result;
}
double *conv(double *A, double *B, int lenA, int lenB){
    int size=lenA+lenB-1;
    int i,j,k;
    double tmp;

    double *result = (double *) malloc(sizeof(double)*(size));

    for (i=0;i<size;i++){
        k = i;
        tmp = 0.0;
        for (j=0; j<lenB; j++)
        {
            if(k>=0 && k<lenA)
                tmp += A[k]*B[j];
 
            k = k-1;
            result[i] = tmp;
        }
    }
    return result;
}
double *dwt_low(double *signal, int lenS)
{
	double *h = (double*) malloc(sizeof(double)*4);
	h[0] = -0.12940952255092145;
	h[1] = 0.22414386804185735;
	h[2] = 0.836516303737469;
	h[3] = 0.48296291314469025;
	double *tmp = conv(signal, h, lenS, 4);
	double *result = downSample(tmp, lenS+3);
	free(tmp);
	free(h);
	return result;
}
double *dwt_high(double *signal, int lenS)
{
	double *g = (double*) malloc(sizeof(double)*4);
	g[0] = -0.48296291314469025;
	g[1] = 0.836516303737469;
	g[2] = -0.22414386804185735;
	g[3] = -0.12940952255092145;
	double *tmp = conv(signal, g, lenS, 4);
	double *result = downSample(tmp, lenS+3);
	free(tmp);
	free(g);
	return result;
}
double meanEnergy(double *input, int lenI, int N)
{
	int i;
	double sum=0;
	for(i=0; i<lenI; i++){
		sum += pow(input[i], 2);
	}
	return (double) (sum/N);
}
double standardDeviation(double *input, int lenI)
{
	int i;
	double sum=0;
	for(i=0; i<lenI; i++){
		sum += input[i];
	}
	double mean = sum/lenI;
	sum = 0;
	for(i=0; i<lenI; i++){
		sum += pow(input[i] - mean, 2);
	}
	return sqrt(sum/lenI);
}
extraction feature_extraction(double *signal, int lenS)
{
	extraction e;
	int l1 = getLengthDWT(lenS);
	int l2 = getLengthDWT(l1);
	int l3 = getLengthDWT(l2);
	int l4 = getLengthDWT(l3);
	int l5 = getLengthDWT(l4);
	int l6 = getLengthDWT(l5);
	
	double *ca1 = dwt_low(signal, lenS);
	double *cd1 = dwt_high(signal, lenS);
	double *ca2 = dwt_low(ca1, l1);
	double *cd2 = dwt_high(ca1, l1);
	double *ca3 = dwt_low(ca2, l2);
	double *cd3 = dwt_high(ca2, l2);
	double *ca4 = dwt_low(ca3, l3);
	double *cd4 = dwt_high(ca3, l3);
	double *ca5 = dwt_low(ca4, l4);
	double *cd5 = dwt_high(ca4, l4);
	double *ca6 = dwt_low(ca5, l5);
	double *cd6 = dwt_high(ca5, l5);
	
	e.E1 = meanEnergy(cd1, l1, lenS);
	e.E2 = meanEnergy(cd2, l2, lenS);
	e.E3 = meanEnergy(cd3, l3, lenS);
	e.E4 = meanEnergy(cd4, l4, lenS);
	e.E5 = meanEnergy(cd5, l5, lenS);
	e.E6 = meanEnergy(cd6, l6, lenS);
	e.E7 = standardDeviation(signal, lenS);

	free(ca1); free(cd1);
	free(ca2); free(cd2);
	free(ca3); free(cd3);
	free(ca4); free(cd4);
	free(ca5); free(cd5);
	free(ca6); free(cd6);

	return e;
}
