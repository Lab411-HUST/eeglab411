#include "la411_eeg_svm_Feature.h"
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Root_1Mean_1Square(
		JNIEnv *je, jclass jc, jint nval, jfloatArray arr) {
	int i;
	float N = 1;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (i = 1; i < nval; i++)
		N += (nativeArr[i] * nativeArr[i]);
	//printf("array is : %f", arr[i]);
	return (sqrt(N / nval));
}
JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Simple_1Square_1Integral(
		JNIEnv *je, jclass jc, jint nval, jfloatArray arr) {
	int i;
	float sum = 1;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (i = 1; i < nval; i++) {
		sum += (nativeArr[i] * nativeArr[i]);
	}

	return sum;
}
JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Waveform_1Length(JNIEnv *je,
		jclass jc, jint nval, jfloatArray arr) {
	float WL = 0;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (int i = 1; i < nval; i++)
		WL += abs(nativeArr[i] - nativeArr[i - 1]);
	return WL;
}
JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Mean_1Absolute_1Value(
		JNIEnv *je, jclass jc, jint nval, jfloatArray arr) {
	float MAV = 0;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (int i = 0; i < nval; i++)
		MAV += abs(nativeArr[i]);
	return (MAV / nval);
}
JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Modified_1Mean_1Absolute_1Value_11(
		JNIEnv *je, jclass jc, jint nval, jfloatArray arr) {
	float MMAV1 = 0;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (int i = 0; i < nval; i++) {
		//bool b;
		// b == 0.25*nval<= i <= 0.75*nval;
		if (0.25 * nval <= i && i <= 0.75 * nval) {
			MMAV1 += abs(nativeArr[i]);
			// printf("array: %f ",arr[i]);
		}

		//printf("bool is: %b\n",b);
		else {
			// printf("array are: %f ", arr[i]);
			MMAV1 = MMAV1 + 0.5 * abs(nativeArr[i]);
		}
	}
	return (MMAV1 / nval);
}
JNIEXPORT jfloat JNICALL Java_la411_eeg_svm_Feature_Slope_1Sign_1Change(
		JNIEnv *je, jclass jc, jint nval, jfloatArray arr, jfloat Threshold) {
	int i;
	float SSC = 0;
	float *nativeArr = (float*) je->GetFloatArrayElements(arr, 0);
	for (int i = 1; i < nval; i++) {
		float f = (nativeArr[i] - nativeArr[i - 1])
				* (nativeArr[i] - nativeArr[i + 1]);
		//printf("\n f=  %8.3f\n",f);
		SSC += f >= Threshold ? 1 : 0;
	}

	return (SSC);
}
JNIEXPORT void JNICALL Java_la411_eeg_svm_Feature_writeFeatureInFile(JNIEnv *je,
		jclass jc, jstring label, jfloat RMS, jfloat SSI, jfloat WL, jfloat MAV,
		jfloat MMAV1, jstring nameOutputFile) {

	jboolean isCopy;
	const char *lb = je->GetStringUTFChars(label, &isCopy);
	const char *outPath = je->GetStringUTFChars(nameOutputFile, &isCopy);
	FILE *outPutFile;
	outPutFile = fopen(outPath, "wt");
	if (outPutFile == NULL) {
	} else {
		fprintf(outPutFile, "%s ", lb);
		fprintf(outPutFile, "1:%0.5f ", RMS);
		fprintf(outPutFile, "2:%0.5f ", SSI);
		fprintf(outPutFile, "3:%0.5f ", WL);
		fprintf(outPutFile, "4:%0.5f ", MAV);
		fprintf(outPutFile, "5:%0.5f ", MMAV1);
		fclose(outPutFile);
	}
}

JNIEXPORT void JNICALL Java_la411_eeg_svm_Feature_writeFeatureFile(JNIEnv *env,
		jclass obj, jstring label, jfloat feature, jstring nameOutputFile) {
	jboolean isCopy;
	const char *lb = env->GetStringUTFChars(label, &isCopy);
	const char *outPath = env->GetStringUTFChars(nameOutputFile, &isCopy);
	FILE *outPutFile;
	outPutFile = fopen(outPath, "wt");
	if (outPutFile == NULL) {
	} else {
		fprintf(outPutFile, "%s ", lb);
		fprintf(outPutFile, "1:%0.5f ", feature);
		fclose(outPutFile);
	}
}

JNIEXPORT void JNICALL Java_la411_eeg_svm_Feature_writeFileTrainingSet
  (JNIEnv *env,jclass obj, jstring label, jfloat feature, jstring nameOutputFile)
{
	jboolean isCopy;
		const char *lb = env->GetStringUTFChars(label, &isCopy);
		const char *outPath = env->GetStringUTFChars(nameOutputFile, &isCopy);
		FILE *outPutFile;
		outPutFile = fopen(outPath, "at");
		if (outPutFile == NULL) {
		} else {
			fprintf(outPutFile, "%s ", lb);
			fprintf(outPutFile, "1:%0.5f\n", feature);
			fclose(outPutFile);
		}
	}

