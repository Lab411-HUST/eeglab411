#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <jni.h>
#include "filter.h"
#include "hfd.h"
int HnSize;
double Wc1, Wc2, beta;
double besselI0(double x) {
	double ax, ans;
	double y;
	if ((ax = fabs(x)) < 3.75) {
		y = x / 3.75, y = y * y;
		ans =
				1.0
						+ y
								* (3.5156229
										+ y
												* (3.0899424
														+ y
																* (1.2067492
																		+ y
																				* (0.2659732
																						+ y
																								* (0.360768e-1
																										+ y
																												* 0.45813e-2)))));
	} else {
		y = 3.75 / ax;
		ans =
				(exp(ax) / sqrt(ax))
						* (0.39894228
								+ y
										* (0.1328592e-1
												+ y
														* (0.225319e-2
																+ y
																		* (-0.157565e-2
																				+ y
																						* (0.916281e-2
																								+ y
																										* (-0.2057706e-1
																												+ y
																														* (0.2635537e-1
																																+ y
																																		* (-0.1647633e-1
																																				+ y
																																						* 0.392377e-2))))))));
	}
	return ans;
}
/*--------------------------------------------------------*/
double wn_kaiser(int n) {
	double val1 = (double) 2.0 * n / (HnSize - 1);
	double val2 = pow(1 - val1, 2);
	double val3 = beta * sqrt(1 - val2);
	return (double) (besselI0(val3) / besselI0(beta));
}
/*--------------------------------------------------------*/
double hd(int n) {
	double val = (double) (HnSize - 1) / 2.0;
	if (n == val) {
		return ((Wc2 - Wc1) / M_PI);
	} else {
		return (sin(Wc2 * (double) (n - val)) - sin(Wc1 * (double) (n - val)))
				/ (M_PI * (n - val));
	}
}
/*--------------------------------------------------------*/
double hn(int n) {
	return hd(n) * wn_kaiser(n);
}
/*--------------------------------------------------------*/
double *getArrHn() {
	double *arr = malloc(sizeof(double) * HnSize);
	int i;
	for (i = 0; i < HnSize; i++) {
		arr[i] = hn(i);
	}
	return arr;
}
/*--------------------------------------------------------*/
double *setFilter(int *arrXn, int XnSize) {
	double *arrHn = getArrHn();
	int YnSize = HnSize + XnSize - 1;
	int max = (HnSize > XnSize) ? HnSize : XnSize;
	int min = (HnSize < XnSize) ? HnSize : XnSize;
	double *arrYn = malloc(sizeof(double) * YnSize);
	int i;
	for (i = 0; i < YnSize; i++) {
		if (i < min) {
			int j;
			arrYn[i] = 0;
			for (j = 0; j <= i; j++) {
				arrYn[i] += arrHn[j] * arrXn[i - j];
			}
		}
		if (i >= min && i < max) {
			int j;
			arrYn[i] = 0;
			for (j = 0; j < min; j++) {
				if (min == HnSize)
					arrYn[i] += arrHn[j] * arrXn[i - j];
				else
					arrYn[i] += arrHn[i - j] * arrXn[j];
			}
		}
		if (i >= max) {
			int j;
			arrYn[i] = 0;
			for (j = i + 1 - max; j < min; j++) {
				if (min == HnSize)
					arrYn[i] += arrHn[j] * arrXn[i - j];
				else
					arrYn[i] += arrHn[i - j] * arrXn[j];
			}
		}
	}
	free(arrHn);
	return arrYn;
}
/*--------------------------------------------------------*/
int *getXnAdd(int *arrXn, int XnSize) {
	int *XnAdd = malloc(sizeof(int) * (XnSize + 600));
	int i;
	for (i = 0; i < 300; i++) {
		XnAdd[i] = arrXn[0];
	}
	for (; i < XnSize + 300; i++) {
		XnAdd[i] = arrXn[i - 300];
	}
	for (; i < XnSize + 600; i++) {
		XnAdd[i] = arrXn[XnSize - 1];
	}
	return XnAdd;
}
/*--------------------------------------------------------*/

double *BandPassFilter(int *arrXn, int XnSize, int hnSize, double W1, double W2,
		double b) {
	HnSize = hnSize;
	Wc1 = W1;
	Wc2 = W2;
	beta = b;
	double *arrYnAdd = setFilter(getXnAdd(arrXn, XnSize), XnSize + 600);
	int YnAddSize = HnSize + XnSize + 599;
	double *signalBand = malloc(sizeof(double) * XnSize);
	double a = (double) (HnSize - 1) / 2.0;
	int i;
	for (i = 0; i < YnAddSize; i++) {
		if (i >= ((int) a + 300) && i < ((int) a + 300) + XnSize)
			signalBand[i - ((int) a + 300)] = arrYnAdd[i];
	}
	free(arrYnAdd);
	return signalBand;
}
JNIEXPORT jdoubleArray JNICALL Java_com_lab411_emotionsignalgraph_Filter_getYnFilter(
		JNIEnv *env, jclass cls, jintArray arrXn, int xnSize, int hnSize,
		double W1, double W2, double b) {
	jdoubleArray arrXnFilter;
	arrXnFilter = (*env)->NewDoubleArray(env, xnSize);
	jdouble *pXnFilter = (*env)->GetFloatArrayElements(env, arrXnFilter, NULL);
	int *arr1 = (*env)->GetDoubleArrayElements(env, arrXn, 0);
	double *arr2 = malloc(sizeof(double) * xnSize);
	arr2 = BandPassFilter(arr1, xnSize, hnSize, W1, W2, b);
	(*env)->SetDoubleArrayRegion(env, arrXnFilter, 0, xnSize, arr2);
	jint i;
	for (i = 0; i < xnSize; i++) {
		*(pXnFilter + i) = *(arr2 + i);
	}
	(*env)->ReleaseDoubleArrayElements(env, arrXnFilter, arr2, NULL);
	(*env)-> ReleaseIntArrayElements(env,arrXn,arr1,NULL);
	free(arr2);
	return arrXnFilter;
}


/*--------------------------------------------------------*/

JNIEXPORT jdouble JNICALL Java_com_lab411_emotionsignalgraph_Filter_getmean(
		JNIEnv *env, jclass cls, jintArray arrXn, int xnSize, int hnSize,
		double W1, double W2, double b) {
	//jdoubleArray arrXnFilter;
	//arrXnFilter = (*env)->NewDoubleArray(env, xnSize);
	//jdouble *pXnFilter = (*env)->GetFloatArrayElements(env, arrXnFilter, NULL);
	int *arr1 = (*env)->GetDoubleArrayElements(env, arrXn, NULL);
	double *arr2 = malloc(sizeof(double) * xnSize);
	arr2 = BandPassFilter(arr1, xnSize, hnSize, W1, W2, b);
	//(*env)->SetDoubleArrayRegion(env, arrXnFilter, 0, xnSize, arr2);
//	jint i;
//	for (i = 0; i < xnSize; i++) {
//		*(pXnFilter + i) = *(arr2 + i);
//	}
//	(*env)->ReleaseDoubleArrayElements(env, arrXnFilter, pXnFilter, NULL);
	jdouble power = 0;
	jdouble temp;
	jint i;
	for (i = 0; i < xnSize; i++) {
		temp = *(arr2 + i);
		power += temp * temp;
	}
	jdouble mean = (jdouble) power / xnSize;
	(*env)->ReleaseIntArrayElements(env, arrXn, arr1, NULL);
	return mean;
}
