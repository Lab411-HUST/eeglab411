#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <math.h>
#include "eegdata_conf.h"
#include "matrix.h"
#include "fastICA.h"
#include "edf2ascii.h"
#include "wavelet_daub4.h"

char *pPath;
char *pFile;

/**
 * Ham tinh Cross Correlation
 */
double calcCrossCorrelation(double* DataX, double* DataY, int Sample) {
	double DenomX = 0;
	double DenomY = 0;
	double MeanX = 0;
	double MeanY = 0;
	double CrossCorr = 0;
	int i;

	for (i = 0; i < Sample; i++) {
		if (DataX[i] < 0)
			DataX[i] = -DataX[i];
		if (DataY[i] < 0)
			DataY[i] = -DataY[i];
	}

	for (i = 0; i < Sample; i++) {
		MeanX += DataX[i];
		MeanY += DataY[i];
	}
	MeanX = MeanX / Sample;
	MeanY = MeanY / Sample;

	for (i = 0; i < Sample; i++) {
		CrossCorr += ((DataX[i] - MeanX) * (DataY[i] - MeanY));
		DenomX += pow((DataX[i] - MeanX), 2);
		DenomY += pow((DataY[i] - MeanY), 2);
	}

	CrossCorr = CrossCorr / sqrt(DenomX * DenomY);
	printf("CrossCorr = %f", CrossCorr);

	return CrossCorr;
}

static double **mat_read(FILE *fp, int *rows, int *cols) {
	int i, j;
	double **M;
	M = mat_create(*rows, *cols);
	for (i = 0; i < *rows; i++) {
		for (j = 0; j < *cols; j++) {
			fscanf(fp, "%lf ", &(M[i][j]));
		}
	}
	return M;
}

/*
JNIEXPORT jint JNICALL
Java_com_eyeblink_RemoveEyeblink_runRemove(JNIEnv *env, jobject thisObj,
		jstring javaString) {
	const char *nativeString = (*env)->GetStringUTFChars(env, javaString, 0);

	// use your string
	run(nativeString);

	(*env)->ReleaseStringUTFChars(env, javaString, nativeString);
	return 1;
}
*/

void main() {
	char* pathsFile = "/home/truongnh/eeg-lab411/SignalProcessing/RemoveEyeblink/Data/data.txt";
	run(pathsFile);
}

/*
void printfMatrix(char* name, double** A, int row, int col) {
	char* tmp = "/home/huytd92/Desktop/ICA/";
	char* paths = (char*)malloc((strlen(tmp)+strlen(name)+1)*sizeof(char));
	strcpy(paths, tmp);
 	strcat(paths, name);
	//paths[strlen(paths)] = '/0';
	printf("%s", paths);
	FILE* file = fopen(paths, "w+");
	int i, j;	
	for(i=0; i<row; i++){
		for(j=0; j<col; j++) {
			fprintf(file, "%lf\t", A[i][j]); 
		}
		fprintf(file, "\n");
	}
}
*/

void run(char *pPathFile) {
	int i, j, k;
	int Level = 3;
	int StartSample = 16000;

	int Sample = 2048;
	int Rows;
	int Cols = 14;
	int Comp = 14;

	Rows = Sample;

	//chi xu ly tung doan 256 mau

	double** RowData;
	double** DataICA, **K, **W;
	double** DataICATranspose;
	double** DataICA_IDWT;
	double* DataFp2;
	double* DataFp2_IDWT;
	double** DataRecontruction;

	double CrossCorrMax = 0;
	double CrossCorr = 0;
	int IndexCompEyeblink = 0;

	double mean1, mean2;
	int startEb, stopEb;

	FILE *Fl;

	RowData = mat_create(Rows, Cols);

	/**
	 * B1: Tinh toan ICA va Wavelet
	 * Du lieu chia lam 2 luong
	 */
	/**
	 * Luong 1: Doc du lieu cua 19 kenh, chay ICA
	 */
	//b1: doc du lieu: 256 Sample cua 19 kenh, luu vao mang RowData
	puts(">>>>>>>>>>Read data.");
	//readData(pPathFile, RowData, StartSample, StartSample+Sample);

	Fl = fopen(pPathFile, "r");
	RowData = mat_read(Fl, &Rows, &Cols);

	//b2: chay ICA voi RowData
	puts(">>>>>>>>>>Run Ica.");
	DataICA = mat_create(Rows, Comp);
	W = mat_create(Comp, Comp);
	K = mat_create(Cols, Comp);
	icaTransform(RowData, Rows, Cols, Comp, DataICA, K, W);
	puts(">>>>>>>>>.");
	

	//b3: chay Wavelet Inverse bac 3 voi tung thanh phan doc lap
	DataICATranspose = mat_create(Comp, Rows);
	DataICA_IDWT = mat_create(Comp, Rows);
	mat_transpose(DataICA, Rows, Comp, DataICATranspose);
	for (i = 0; i < Comp; i++) {
		DataICA_IDWT[i] = WaveletTransformInverse(DataICATranspose[i], Sample,
				i, Level);
	}

	/**
	 * Luong 2: Xu ly du lieu tren kenh FP2
	 */
	//b1: lay du lieu kenh Fp2
	puts(">>>>>>>>>>Get data Fp2.");
	DataFp2 = malloc(Sample * sizeof(double));
	for (i = 0; i < Sample; i++)
		DataFp2[i] = RowData[i][10];

	//b2: chay Wavelet Inverse bac 3 voi DataFp2
	puts(">>>>>>>>>>Wavelet inverse level 3 with Fp2.");
	DataFp2_IDWT = malloc(Sample * sizeof(double));
	DataFp2_IDWT = WaveletTransformInverse(DataFp2, Sample, CHANNEL_NUMBER_MAX,
			Level);


FILE* ftemp = fopen("/home/truongnh/eeg-lab411/SignalProcessing/RemoveEyeblink/Data/outWT.txt", "w+");
for(i=0; i<Sample; i++)
	fprintf(ftemp, "%lf\n", DataFp2_IDWT[i]);
fclose(ftemp);


	/**
	 * B2: Tinh Cross Correlator
	 * Xac dinh thanh phan nhieu mat
	 */
	puts(">>>>>>>>>>Calc cross correlator.");
	for (k = 0; k < Comp; k++) {
		printf("\nComp %i: ", k);
		CrossCorr = calcCrossCorrelation(DataFp2_IDWT, DataICA_IDWT[k], Sample);
		if (CrossCorrMax < CrossCorr) {
			CrossCorrMax = CrossCorr;
			IndexCompEyeblink = k;
		}
	}

	//hien thi thanh phan nhieu mat trong cac thanh phan doc lap
	printf("\nComponent Eyeblink: %i, CrossCorr: %f\n", IndexCompEyeblink,
			CrossCorrMax);

	/**
	 * B3: Loai bo nhieu mat, tai cau truc du lieu - dung ICA nguoc
	 */
	puts(">>>>>>>>>>Remove artifact. Recontruction data.");
	//de loai bo tot can xac dinh khoang co nhay mat
	/**
	 *
	 * B0: Detect thoi dien liec mat
	 * phan nay tam thoi chua cho vao
	 *
	 */
	//b1: lay du lieu kenh Fp1
	for (i = 0; i < Sample - 100; i += 50) {
		//Kenh F8: dich di tung doan 50 mau
		//tinh gia tri trung binh(GTTB) cua 50 mau
		//tinh sai khac GTTB so voi 50 mau truoc day
		//neu vuot nguong thi tinh gia tri nay tren kenh F7
		//xac dinh duoc thoi diem eyeblink
		mean1 = 0;
		mean2 = 0;
		for (j = i; j < i + 50; j++)
			mean1 += DataFp2[j];
		for (j = i + 50; j < i + 100; j++)
			mean2 += DataFp2[j];

printf("mean1 = %lf, mean2 = %lf\n", mean1, mean2);

//500 la nguong nhay mat
		if ((mean2 - mean1) > 200) {
			startEb = i;
			stopEb = i + 100;
		}
	}
		

	DataRecontruction = mat_create(Rows, Cols);
	
printf("startEb = %d, stopEb = %d", startEb, stopEb);

	icaTransformInverse(DataICA, Rows, Cols, Comp, IndexCompEyeblink,
			DataRecontruction, K, W, RowData, startEb, stopEb);
	mat_delete(RowData, Sample, CHANNEL_NUMBER_MAX);
	mat_delete(DataICA, Rows, Comp);
	mat_delete(DataICATranspose, Comp, Rows);
	mat_delete(DataICA_IDWT, Comp, Rows);
	mat_delete(K, Cols, Comp);
	mat_delete(W, Comp, Comp);
	mat_delete(DataRecontruction, Rows, Cols);
	free(DataFp2);
	free(DataFp2_IDWT);
}
