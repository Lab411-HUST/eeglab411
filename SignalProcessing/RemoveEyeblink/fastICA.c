#include "fastICA.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <time.h>
#include "matrix.h"

//#include "eegdata_conf.h"
//#include "svdcmp.h"

/*
 * Functions for matrix elements transformations
 * used in mat_apply_fx().
 */
static scal fx_inv(scal x, scal par) {
	return (1 / x);
}

static scal fx_inv_sqrt(scal x, scal par) {
	return (1 / sqrt(x));
}

static scal fx_div_c(scal x, scal par) {
	return (x / par);
}

static scal fx_rand(scal x, scal par) {
	return (scal) rand() / RAND_MAX;
}

static scal fx_tanh(scal x, scal par) {
	return tanh(ALPHA * x);
}

static scal fx_1sub_sqr(scal x, scal par) {
	return (ALPHA * (1 - x * x));
}

static double **mat_read(FILE *fp, int *rows, int *cols) {
	int i, j;
	double **M;
	M = mat_create(*rows, *cols);

	for (i = 0; i < *rows; i++) {
		for (j = 0; j < *cols; j++)
			fscanf(fp, "%lf ", &(M[i][j]));
	}
	return M;
}

void icaTransform(double** X, int rows, int cols, int comp, double **S,
		double** K, double** W) {
	double **A;
	FILE * OutputFile;
	int i, j;

	A = mat_create(comp, comp);

	//thuc hien ICA
	fastICA(X, rows, cols, comp, K, W, A, S);
	puts(">>>>>>>>>>...run ica complete.");

//In cac thanh phan doc lap	
	FILE * file = fopen("/home/truongnh/eeg-lab411/SignalProcessing/RemoveEyeblink/Data/outICA.txt", "w+");
	for(i=0; i<rows; i++) {
		for(j=0; j<cols; j++) {
			fprintf(file, "%lf\t", S[i][j]);
		}
		fprintf(file, "\n");
	}
	fclose(file);


	/*
	 //in cac thanh phan doc lap ra
	 for (j = 0; j < comp; j++) {
	 if(j==0)
	 OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA", "wb");
	 else
	 OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA", "at");
	 fprintf(OutputFile, "********************Comp %i\n", j);
	 for (i = 0; i < rows; i++) {
	 fprintf(OutputFile, "%f\n", S[i][j]);
	 }
	 fclose(OutputFile);
	 }*/
	mat_delete(A, comp, comp);
}

void icaTransformInverse(double** S, int rows, int cols, int comp, int comp_rm,
		double** X, double** K, double** W, double** RowData, int startEb,
		int stopEb) {
	double **A;
	double *Total;
	FILE * OutputFile;
	int i, j;

	A = mat_create(comp, comp);
	Total = malloc(cols * sizeof(double));

	for (j = 0; j < cols; j++) {
		Total[j] = 0;
		for (i = 0; i < rows; i++)
			Total[j] = Total[j] + RowData[i][j];
		Total[j] = Total[j] / rows;
	}

puts("Before");
	for (j = startEb; j < stopEb; j++)
		//for (j = 40; j < 90; j++)
		S[j][comp_rm] = 0;//////////////////////////////////////////////////////////////////////////////////

puts("Debug");

	mat_mult(K, cols, comp, W, comp, comp, A); //A[cols, comp]
	mat_inverse(A, comp, W);
	mat_mult(S, rows, comp, W, comp, comp, X);
	//strcat(pPathFile, "_recontruction");
	
	

	OutputFile = fopen("/home/truongnh/eeg-lab411/SignalProcessing/RemoveEyeblink/Data/Output.txt",
			"wb");
	for (i = 0; i < rows; i++) {
		for (j = 0; j < cols; j++) {
			if(j == comp_rm)
				fprintf(OutputFile, "%f\n", X[i][j] + Total[j]);
		}
		//fprintf(OutputFile, "\n");
	}
	fclose(OutputFile);

	for (j = 0; j < cols; j++) {
		for (i = 0; i < rows; i++)
			X[i][j] = X[i][j] + Total[j];
	}

	free(Total);
}

/**
 * ICA function. Computes the W matrix from the
 * preprocessed data.
 */
static mat ICA_compute(mat X, int rows, int cols) {
	mat TXp, GWX, W, Wd, W1, D, TU, TMP;
	vect d, lim;
	int i, it;

	FILE *OutputFile;

	clock_t clock1, clock2;
	float time;

	//char ascii_path[512];
	//strcpy(ascii_path, "/storage/sdcard0/NickGun/EEG/Log.txt");
	//FILE *Log;

	// matrix creation
	TXp = mat_create(cols, rows);
	GWX = mat_create(rows, cols);
	W = mat_create(rows, rows);
	Wd = mat_create(rows, rows);
	D = mat_create(rows, rows);
	TMP = mat_create(rows, rows);
	TU = mat_create(rows, rows);
	W1 = mat_create(rows, rows);
	d = vect_create(rows);

	// W rand init
	mat_apply_fx(W, rows, rows, fx_rand, 0);

	// sW <- La.svd(W)
	mat_copy(W, rows, rows, Wd);
	svdcmp(Wd, rows, rows, d, D);

	// W <- sW$u %*% diag(1/sW$d) %*% t(sW$u) %*% W
	mat_transpose(Wd, rows, rows, TU);
	vect_apply_fx(d, rows, fx_inv, 0);
	mat_diag(d, rows, D);
	mat_mult(Wd, rows, rows, D, rows, rows, TMP);
	mat_mult(TMP, rows, rows, TU, rows, rows, D);
	mat_mult(D, rows, rows, W, rows, rows, Wd); // W = Wd

	// W1 <- W
	mat_copy(Wd, rows, rows, W1);

	// lim <- rep(1000, maxit); it = 1
	lim = vect_create(MAX_ITERATIONS);
	for (i = 0; i < MAX_ITERATIONS; i++)
		lim[i] = 1000;
	it = 0;

	// t(X)/p
	mat_transpose(X, rows, cols, TXp);
	mat_apply_fx(TXp, cols, rows, fx_div_c, cols);

	while (lim[it] > TOLERANCE && it < MAX_ITERATIONS) {
		// wx <- W %*% X
		mat_mult(Wd, rows, rows, X, rows, cols, GWX);

		// gwx <- tanh(alpha * wx)
		mat_apply_fx(GWX, rows, cols, fx_tanh, 0);

		// v1 <- gwx %*% t(X)/p
		mat_mult(GWX, rows, cols, TXp, cols, rows, TMP); // V1 = TMP

		// g.wx <- alpha * (1 - (gwx)^2)
		mat_apply_fx(GWX, rows, cols, fx_1sub_sqr, 0);

		// v2 <- diag(apply(g.wx, 1, FUN = mean)) %*% W
		mat_mean_rows(GWX, rows, cols, d);
		mat_diag(d, rows, D);
		mat_mult(D, rows, rows, Wd, rows, rows, TU); // V2 = TU

		// W1 <- v1 - v2
		mat_sub(TMP, TU, rows, rows, W1);

		// sW1 <- La.svd(W1)
		mat_copy(W1, rows, rows, W);
		svdcmp(W, rows, rows, d, D);

		// W1 <- sW1$u %*% diag(1/sW1$d) %*% t(sW1$u) %*% W1
		mat_transpose(W, rows, rows, TU);
		vect_apply_fx(d, rows, fx_inv, 0);
		mat_diag(d, rows, D);
		mat_mult(W, rows, rows, D, rows, rows, TMP);
		mat_mult(TMP, rows, rows, TU, rows, rows, D);
		mat_mult(D, rows, rows, W1, rows, rows, W); // W1 = W

		// lim[it + 1] <- max(Mod(Mod(diag(W1 %*% t(W))) - 1))
		mat_transpose(Wd, rows, rows, TU); //chuyen vi
		mat_mult(W, rows, rows, TU, rows, rows, TMP); //TMP=WxTU
		lim[it + 1] = fabs(mat_max_diag(TMP, rows, rows) - 1);

		if(lim[it+1]<0.1)
			break;


		/*
		OutputFile = fopen("/storage/sdcard0/Nickgun/EEG/data/lim",
					"at");
		fprintf(OutputFile, "%f \n", lim[it+1]);

		fclose(OutputFile);
		// W <- W1
		*/
		mat_copy(W, rows, rows, Wd);

		it++;
	}

	// clean up
	mat_delete(TXp, cols, rows);
	mat_delete(GWX, rows, cols);
	mat_delete(W, rows, rows);
	mat_delete(D, rows, rows);
	mat_delete(TMP, rows, rows);
	mat_delete(TU, rows, rows);
	mat_delete(W1, rows, rows);
	vect_delete(d);

	return Wd;
}

/*
 * Prints matrix M to stdout
 */
static void mat_print(double **M, int rows, int cols) {
	int i, j;

	for (i = 0; i < rows; i++) {
		for (j = 0; j < cols; j++) {
			printf("%0.6f", M[i][j]);
			if (j < cols - 1)
				printf(" ");
		}
		printf("\n");
	}
}

/**
 * Main FastICA function. Centers and whitens the input
 * matrix, calls the ICA computation function ICA_compute()
 * and computes the output matrixes.
 */
void fastICA(mat X, int rows, int cols, int compc, mat K, mat W, mat A, mat S) {
	mat XT, V, TU, D, X1, _A;
	vect scale, d;
	clock_t clock1, clock2;
	float time;
	//char ascii_path[512];
	//strcpy(ascii_path, "/storage/sdcard0/NickGun/EEG/Log.txt");
	//FILE *Log;

	//chu thich voi truong hop 14 kenh, 2s (256mau) du lieu, 14 thanh phan doc lap>>> cols = 14, rows = 256, compc = 14
	// matrix creation
	XT = mat_create(cols, rows); //14x256
	X1 = mat_create(compc, rows); //14x256
	V = mat_create(cols, cols); //14x14
	D = mat_create(cols, cols); //14x14
	TU = mat_create(cols, cols); //14x14
	scale = vect_create(cols); //14
	d = vect_create(cols); //14

	clock1 = clock();
	/*
	 * CENTERING
	 */
	mat_center(X, rows, cols, scale); //tru di gia tri trung binh cua moi cot

	clock2 = clock();
	time = (clock2 - clock1) / CLOCKS_PER_SEC;
	//Log = fopen(ascii_path, "wb");
	//fprintf(Log, "CENTERING %f \n", time);
	//fclose(Log);

	clock1 = clock();
	/*
	 * WHITENING
	 */

	// X <- t(X); V <- X %*% t(X)/rows
	mat_transpose(X, rows, cols, XT); //XT la chuyen vi cua ma tran X[256][14] >>> XT[14][256]
	mat_apply_fx(X, rows, cols, fx_div_c, rows); //lay tung gia tri cua X[i][j] chia cho 14
	mat_mult(XT, cols, rows, X, rows, cols, V); //V=XT*X >>>V[14][14]

	// La.svd(V)
	svdcmp(V, cols, cols, d, D); // V = s$u, d = s$d, D = s$v

	// D <- diag(c(1/sqrt(d))
	vect_apply_fx(d, cols, fx_inv_sqrt, 0);
	mat_diag(d, cols, D);

	// K <- D %*% t(U)
	mat_transpose(V, cols, cols, TU);
	mat_mult(D, cols, cols, TU, cols, cols, V); // K = V

	// X1 <- K %*% X
	mat_mult(V, compc, cols, XT, cols, rows, X1);

	clock2 = clock();
	time = (clock2 - clock1) / CLOCKS_PER_SEC;
	//Log = fopen(ascii_path, "at");
	//fprintf(Log, "WHITENING %f \n", time);
	//fclose(Log);

	clock1 = clock();
	/*
	 * FAST ICA
	 */
	_A = ICA_compute(X1, compc, rows);

	clock2 = clock();
	time = (clock2 - clock1) / CLOCKS_PER_SEC;
	//Log = fopen(ascii_path, "at");
	//fprintf(Log, "FASTICA %f \n", time);
	//fclose(Log);

	clock1 = clock();
	/*
	 * OUTPUT
	 */

	// X <- t(x)
	mat_transpose(XT, cols, rows, X);
	mat_decenter(X, rows, cols, scale);

	// K
	mat_transpose(V, compc, cols, K);

	// w <- a %*% K; S <- w %*% X
	mat_mult(_A, compc, compc, V, compc, cols, D);
	mat_mult(D, compc, cols, XT, cols, rows, X1);

	// S
	mat_transpose(X1, compc, rows, S);

	// A <- t(w) %*% solve(w * t(w))
	mat_transpose(D, compc, compc, TU);
	mat_mult(D, compc, compc, TU, compc, compc, V);
	mat_inverse(V, compc, D); //ham nay tinh mat tran ngich dao
	mat_mult(TU, compc, compc, D, compc, compc, V);
	// A
	mat_transpose(V, compc, compc, A);

	// W
	mat_transpose(_A, compc, compc, W);

	// cleanup
	mat_delete(XT, cols, rows);
	mat_delete(X1, compc, rows);
	mat_delete(V, cols, cols);
	mat_delete(D, cols, cols);
	mat_delete(TU, cols, cols);
	vect_delete(scale);
	vect_delete(d);

	clock2 = clock();
	time = (clock2 - clock1) / CLOCKS_PER_SEC;
	//Log = fopen(ascii_path, "at");
	//fprintf(Log, "OUTPUT %f \n", time);
	//fclose(Log);
}
