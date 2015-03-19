/**
 * @file libICA.h
 * 
 * Main FastICA functions.
 */

#ifndef LIBICA_H_
#define LIBICA_H_

#define MAX_ITERATIONS	1000
#define TOLERANCE		0.0001
#define ALPHA			1

void fastICA(double** X, int rows, int cols, int compc, double** K, double** W,
		double** A, double** S);
void icaTransform(double** X, int rows, int cols, int comp, double **S,
		double** K, double** W);
void icaTransformInverse(double** S, int rows, int cols, int comp, int comp_rm,
		double** X, double** K, double** W, double** RowData, int startEb,
		int stopEb);

#endif /*LIBICA_H_*/
