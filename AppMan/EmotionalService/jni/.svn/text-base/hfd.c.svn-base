#include "hfd.h"
#include "jni.h"

double* readData(char *pathFile, int N) {
	int i;
	FILE *file;
	double *signal = (double*) malloc(N * sizeof(double));
	for (i = 0; i < N; i++) {
		signal[i] = 0;
	}
	file = fopen(pathFile, "r");
	if (file != NULL) {
		for (i = 0; i < N; i++) {
			fscanf(file, "%lf", &signal[i]);
		}
	}
	fclose(file);
	return signal;
}

double lsq(int n, double *vx, double *vy) {
	double x, y, cn = 0.0, sumx = 0.0, sumy = 0.0, sumxy = 0.0, sumx2 = 0.0;
	double d1, d2;
	int i;
	cn = (double) n;
	for (i = 0; i < n; i++) {
		x = vx[i]; //x variable
		y = vy[i]; //y variable
		sumx += x;
		sumy += y;
		sumxy += x * y;
		sumx2 += x * x;
	}
	d1 = cn * sumxy - sumx * sumy;
	d2 = cn * sumx2 - sumx * sumx;
	if (fabs(d1) < 1.e-10 || fabs(d2) < 1.e-10)
		return 0.0;
	else
		return d1 / d2;
}

int kMax(int length) {
	double x = fabs(log((double) length) / log(2)) - 4;
	return (int) pow(2, x);
}

double hfd(double *signal, int N) {
	int K = kMax(N);
	int i, k, m;
	double fd, Lmki, Ng;
	double *lgLk = (double*) malloc(K * sizeof(double));
	double *lgk = (double*) malloc(K * sizeof(double));
	double **LmK = (double**) malloc(K * sizeof(double*));
	for (i = 0; i < K; i++) {
		lgLk[i] = 0.0;
		lgk[i] = 0.0;
		LmK[i] = (double*) malloc(K * sizeof(double));
	}
	for (k = 1; k <= K; k++) {
		for (m = 1; m <= k; m++) {
			Lmki = 0.0;
			for (i = 1; i <= ((N - m) / k); i++) {
				Lmki += fabs(
						signal[(m + i * k) - 1]
								- signal[(m + (i - 1) * k) - 1]);
			}
			Ng = (double) (N - 1) / (((N - m) / k) * k);
			LmK[m - 1][k - 1] = (Lmki * Ng) / k;
		}
	}
	for (k = 1; k <= K; k++) {
		for (i = 1; i <= K; i++) {
			lgLk[k - 1] += LmK[i - 1][k - 1];
		}
		lgLk[k - 1] /= k;
		lgLk[k - 1] = log(lgLk[k - 1]);
		lgk[k - 1] = log(1.0 / k);
	}
	fd = lsq(K, lgk, lgLk);
	free(LmK);
	free(lgLk);
	free(lgk);
	return fd;
}

/* Calculate hfd function */
JNIEXPORT jdouble JNICALL Java_lab411_eeg_emotionalservice_Calculate_gethfd(JNIEnv *env, jobject obj,
		 jdoubleArray signal,jint sample){
	int K = kMax(sample);
	int i,k,m;
	double fd, Lmki, Ng;
	double *lgLk = (double*) malloc(K * sizeof(double));
	double *lgk = (double*) malloc(K * sizeof(double));
	double **LmK = (double**) malloc(K * sizeof(double*));
	jboolean isCopy;
	double *signalArr = (*env)->GetDoubleArrayElements(env, signal, &isCopy);
	for(i = 0; i < K; i++){
		lgLk[i] = 0.0;
		lgk[i] = 0.0;
		LmK[i] = (double*) malloc(K * sizeof(double));
	}
	for(k = 1; k <= K; k++){
		for(m = 1; m <= k; m++){
			Lmki = 0.0;
			for(i = 1; i <= ((sample - m) / k); i++){
				Lmki += fabs(signalArr[(m+i*k)-1] - signalArr[(m+(i-1)*k)-1]);
			}
			Ng = (double) (sample - 1) / (((sample - m) / k) * k);
			LmK[m-1][k-1] = (Lmki * Ng) / k;
		}
	}
	for (k = 1; k <= K; k++){
		for(i = 1; i <= k; i++){
			lgLk[k-1] += LmK[i-1][k-1];
		}
		lgLk[k-1] /= k;
		lgLk[k-1] = log(lgLk[k-1]);
		lgk[k-1] = log(1.0 / k);
	}
	fd = lsq(K, lgk, lgLk);
	(*env)->ReleaseDoubleArrayElements(env, signal, signalArr, 0);
	free(LmK);
	free(lgLk);
	free(lgk);
	return fd;
}


