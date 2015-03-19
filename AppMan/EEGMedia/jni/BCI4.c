/*
 This application is P300-based BCI for speller system that enables subjects to spell words by selecting the letters from the alphabet.
 The user observes a 6*6 matrix of letters, numbers and other symbols. Rows and columns of the matrix flash in a random order.
 The subject has to count silently how often the desired symbol has flashed. Whenever a row or a column containing the desired symbol flashes, the P300 EEG signal is evoked.
 This application is an image selection task [1].
 Data filterd using FIR filter. For feature generation, EEG data are down sampled to 32 Hz and single trials are extracted. A Linear Discriminant Analysis (LDA) is used as the classifier and is trained a priori.


 [1]	U. Hoffmann, J. Vesin, T. Ebrahimi, and K. Diserens, �An efficient P300-based brain-computer interface for disabled subjects,� Journal of Neuroscience methods, vol. 167, no. 1, pp. 115�125, 2008.

 */

#include <jni.h>
#include "math.h"
#include <stdio.h>
#include <stdlib.h>
#include "data_gen.h"
#include "FIR_filter.h"
#include "P300_process.h"
#include "LDA.h"

int* run(int argc) {
	int number_of_channels = 8;
	int Fs = 512;
	double processing_window = 1;
	int sample_time;
	int number_of_trial = 10;
	int filterLength = 6;

	int i, j, k, l, m;
	long int *outputdata, *tempoutputdata;
	int feature_count = 0;
	unsigned temp2;
	int **data, **data_out, **temp_data;
	int step;
	int *coeffs;
	int number_of_features = 80;
	int lowerIndex = 4;
	int upperIndex = 10;
	int interval;
	int *weights;
	int data_set = 2;
	FILE *fp;

	fp = fopen("/sdcard/P300_data.txt", "r");

//	if (data_set == 1)
//		fp = fopen("../../../Data_Set/P300_data.txt", "r");
//	else if (data_set == 2)
//		fp = fopen("../../../Data_Set/motor_imagery_data.txt", "r");
//	else
//		fp = fopen("../../../Data_Set/VEP_data.txt", "r");

	coeffs = int_vector_generation_withinitialization(fp, filterLength);

	sample_time = Fs * processing_window;

	interval = sample_time / 16;
	outputdata = vector_generation(number_of_features);
	tempoutputdata = vector_generation(sample_time);
	data_out = array_generation(1, sample_time);
	temp_data = data_generation(fp, number_of_channels,
			number_of_trial * sample_time);
	data = array_generation(number_of_channels, sample_time);

	weights = int_vector_generation_withinitialization(fp, number_of_features);

	fclose(fp);

	int* result = (int) malloc(number_of_trial * sizeof(int));

	step = 0;
	for (i = 0; i < number_of_trial; i++) {
		printf("START Trial  %d....\n", (i + 1));
		for (l = 0; l < number_of_channels; l++) {
			for (m = 0; m < sample_time; m++) {
				data[l][m] = temp_data[l][m + step];
			}
		}
		step = step + sample_time;

		printf("START P300....\n");
		feature_count = 0;
		for (j = 0; j < number_of_channels; j++) {

			data_out[0] = firfilter(data[j], coeffs, sample_time);

			tempoutputdata = p300_process(data_out[0], interval, sample_time,
					lowerIndex, upperIndex);
			for (k = 0; k < 10; k++)
				outputdata[k + feature_count] = tempoutputdata[k];

			feature_count = feature_count + 10;
		}

		printf("START LDA....\n");
		//LDA
		int class_label = LDAtest(outputdata, weights);
		result[i] = class_label;
		for (k = 55; k < 60; k++)
			printf("%d   ", outputdata[k]);

		printf("STOP Trial  %d....\n\n", (i + 1));
	}

	return result;
}

JNIEXPORT jintArray JNICALL Java_com_lab411_eegmedia_MainActivity_BCI(
		JNIEnv* env, jobject thiz) {
	int number_of_channels = 8;
	int* tmp = run(1);
	jintArray result = (*env)->NewIntArray(env, number_of_channels);
	jint* desArr = (*env)->GetIntArrayElements(env, result, 0);

	int i;
	for (i = 0; i < number_of_channels; i++) {
		desArr[i] = tmp[i];
	}
	(*env)->ReleaseIntArrayElements(env, result, desArr, 0);
	return result;
}

