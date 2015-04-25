#include "power.h"
#include "stdio.h"
#include "jni.h"
#include "filter.h"
#define PI 3.141592653589793

//double* readFile(char* paths, int N) {
//	FILE* file = fopen(paths, "r");
//	double* signal = (double*)malloc(N*sizeof(double));
//	int i;
//	for(i=0; i<N; i++) {
//		fscanf(file, "%lf", &signal[i]);
//		//printf("%lf\n", signal[i]);
//	}
//	fclose(file);
//	return signal;
//}
//void writeFile(char* paths, double* signal, int N) {
//	FILE* file = fopen(paths, "w+");
//	int i;
//	for(i=0; i<N; i++) {
//		fprintf(file, "%lf\n", signal[i]);
//	}
//	fclose(file);
//}
//void calcEmotion(double* _af3, double* _af4, double* _f3, double* _f4, int N, double* _valence, double *_arousalAF3, double *_arousalAF4, double *_arousalF3, double *_arousalF4) {
//	//aF3P, bF3P = alpha, beta power of F3
//	//aF4P, bF3P = alpha, beta power of F4S
//	//_af3f data of F3 after filter
//
//	//int i;
//	//for(i=0; i<N; i++)
//		//printf("%d: %lf\n", i, _f3[i]);
//
//	double _aF3P;
//	power(doFilter(_f3, N, 2*PI*8/128, 2*PI*12/128), N, &_aF3P);
//	double _aF4P;
//	power(doFilter(_f4, N, 2*PI*8/128, 2*PI*12/128), N, &_aF4P);
//	double _bF3P;
//	power(doFilter(_f3, N, 2*PI*12/128, 2*PI*30/128), N, &_bF3P);
//	double _bF4P;
//	power(doFilter(_f4, N, 2*PI*12/128, 2*PI*30/128), N, &_bF4P);
//
//	double _aAF3P;
//	power(doFilter(_af3, N, 2*PI*8/128, 2*PI*12/128), N, &_aAF3P);
//	double _aAF4P;
//	power(doFilter(_af4, N, 2*PI*8/128, 2*PI*12/128), N, &_aAF4P);
//	double _bAF3P;
//	power(doFilter(_af3, N, 2*PI*12/128, 2*PI*30/128), N, &_bAF3P);
//	double _bAF4P;
// 	power(doFilter(_af4, N, 2*PI*12/128, 2*PI*30/128), N, &_bAF4P);
//
//
//	*_valence = _aF4P/_bF4P - _aF3P/_bF3P;
//	*_arousalAF3 = _bAF3P/_aAF4P;
//	*_arousalAF4 = _bAF4P/_aAF4P;
//	*_arousalF3 = _bF3P/_aF4P;
//	*_arousalF4 = _bF4P/_aF4P;
//}
//
//
//
//struct Filter CreatFilter(int N, double Wcb, double Wca) {
//	struct Filter filter;
//	filter.N = N;
//	filter.Wca = Wca;
//	filter.Wcb = Wcb;
//	filter.M = (N - 1) / 2.0;
//	filter.hn = (double *) malloc(N * sizeof(double));
//	return filter;
//}
//double gn(int n, struct Filter filter) {
//	if (filter.Wcb == 0 && filter.Wca != 0) { // Low Pass Filter
//		if (n == filter.M) {
//			return filter.Wca / PI;
//		}
//		return (sin(filter.Wca * (n - filter.M)) / (PI * (n - filter.M)));
//	}
//	if (filter.Wcb != 0 && filter.Wca == PI) { // High Pass Filter
//		if (n == filter.M) {
//			return (PI - filter.Wcb) / PI;
//		} else {
//			if (filter.N % 2 != 0) {
//				return  -1.0 * sin(filter.Wcb * (n - filter.M)) / (PI * (n - filter.M));
//			} else {
//				return -1.0 * cos((n - filter.M) * filter.Wcb) / (PI * (n - filter.M));
//			}
//		}
//	}
//	if (filter.Wcb != 0 && filter.Wca !=0 && filter.Wca != PI) { // Band Pass Filter
//		if (n == filter.M) {
//			return 0;
//		} else {
//			if (filter.N % 2 == 0) {
//				return (sin(filter.Wca * (n - filter.M)) - sin(filter.Wcb * (n - filter.M))) / (PI * (n - filter.M));
//			} else {
//				return (cos(filter.Wca * (n - filter.M)) - cos(filter.Wcb * (n - filter.M))) / (PI * (n - filter.M));
//			}
//		}
//
//	}
//}
//
//double Window_Hamming(int n, double M) {
//	return (0.54 - 0.46 * cos(PI * n / M));
//}
//
//double Window_Cut(int n) {
//	return 1.0;
//}
//
//void Hamming(struct Filter filter) {
//	int i;
//	for (i = 0; i < filter.N; i++) {
//		filter.hn[i] = Window_Hamming(i, filter.M) * gn(i, filter);
//	}
//}
//
//void Cut(struct Filter filter) {
//	int i;
//	for (i = 0; i < filter.N; i++) {
//		filter.hn[i] = Window_Cut(i) * gn(i, filter);
//	}
//}
//
//double* Filt(double *hn, int N, double *signal, int signal_size) {
//	int size = N + signal_size - 1;
//	int max = (N > signal_size) ? N : signal_size;
//	int min = (N < signal_size) ? N : signal_size;
//	double *signal_convolution = (double *) malloc(size * sizeof(double));
//	int i, j;
//	for (i = 0; i < size; i++) {
//		signal_convolution[i] = 0;
//	}
//
//	for (i = 0; i < size; i++) {
//		if (i < min) {
//			for (j = 0; j <= i; j++) {
//				signal_convolution[i] += hn[j] * signal[i - j];
//			}
//		}
//		if (i >= min && i < max) {
//			for (j = 0; j < min; j++) {
//				if (min == N) {
//					signal_convolution[i] += hn[j] * signal[i - j];
//				} else if (min == signal_size) {
//					signal_convolution[i] += hn[i - j] * signal[j];
//				}
//			}
//		}
//		if (i >= max) {
//			for (j = i + 1 - max; j < min; j++) {
//				if (min == N) {
//					signal_convolution[i] += hn[j] * signal[i - j];
//				} else if (min == signal_size) {
//					signal_convolution[i] += hn[i - j] * signal[j];
//				}
//			}
//		}
//	}
//	return signal_convolution;
//}
//
//double* doFilter(double *rawSignal, int N, double Wcb, double Wca) {
//	struct Filter filter;
//	filter = CreatFilter(9000, Wcb, Wca);
//	Hamming(filter);			// Sử dụng cửa sổ Hamming
//	double* signal_add = (double*)malloc((N+600)*sizeof(double));
//	int i, j;
//	for(i=0; i<N+600; i++) {
//		do {
//			if(i<300) {
//				signal_add[i] = rawSignal[0];
//				break;
//			}
//			if(i>=N+300) {
//				signal_add[i] = rawSignal[N-1];
//				break;
//			}
//			signal_add[i] = rawSignal[i-300];
//		} while(0);
//	}
//	double *signal_convolution = Filt(filter.hn, filter.N, signal_add, N+600);
//	double *signal_cut = (double *) malloc(N * sizeof(double));
//
//	for (i = 0; i < (N + 600 + filter.N - 1); i++) {
//		if (i >= ((int) filter.M + 300)
//			&& i < ((int) filter.M + 300) + N) {
//				signal_cut[i - ((int) filter.M + 300)] = signal_convolution[i];
//		}
//	}
//
//	free(signal_add);
//	free(signal_convolution);
//	return signal_cut;
//}
//
//void power(double* signal, int N, double* result) {
//	int i;
//	double sum = 0.0;
//	for(i=0; i<N; i++) {
//		sum += signal[i]*signal[i];
//	}
//	//printf("%lf\n", sum);
//	*result = sum;
//}
void power(double* signal, int N, double* result) {
	int i;
	double sum = 0.0;
	for (i = 0; i < N; i++) {
		sum += signal[i] * signal[i];
	}
	//printf("%lf\n", sum);
	*result = sum;
}

JNIEXPORT jdouble JNICALL Java_lab411_appman_concentratedetection_SignalProcessing_calcPower(
		JNIEnv * env, jclass jc, jdoubleArray signal) {
	jint i;
	jdouble sum = 0.0;

	jsize dataLength = (*env)->GetArrayLength(env, signal);
	jdouble *pointerData = (*env)->GetDoubleArrayElements(env, signal, NULL);
	jdouble arrayData[dataLength];
	for (i = 0; i < dataLength; i++) {
		arrayData[i] = *(pointerData + i);
	}
	for (i = 0; i < dataLength; i++) {
		//sum += signal[i] * signal[i];
		sum += arrayData[i] * arrayData[i];
	}
	return sum;
}

