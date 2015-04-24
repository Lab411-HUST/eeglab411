#include <jni.h>
#include <lab411_eeg_p300processing_MainProcess.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include <sstream>
#include <map>
#include <vector>
#include <algorithm>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <eigen/Eigen/Dense>
#include <math.h>
#include <map>
#include <opencv2/core/eigen.hpp>
#define LOG_TAG "MainProcess"

#define LOGI(fmt, args...) __android_log_write(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

#define LOGG(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
using namespace std;
using namespace cv;
using namespace Eigen;

cv::Mat coefficients;
/*
 * Convert double to string
 */
std::string to_string(double value) {
	std::stringstream sstr;
	sstr << value;
	return sstr.str();
}

/*
 * Calculate Training Function
 */JNIEXPORT jboolean JNICALL Java_lab411_eeg_p300processing_MainProcess_CalculateTraining(
		JNIEnv *env, jclass jc, jint start, jint stop,
		jdoubleArray fv_FeatureData, jintArray fv_FeatureVectorIndex,
		jdoubleArray fv_FeatureVectorLabel, jint fv_FeatureLength,
		jstring pathFile) {
	//Copy FeatureData to *double cursor
	double *fv_FeatureDatas = env->GetDoubleArrayElements(fv_FeatureData, 0);
	//Copy FeatureVector Index
	int *fv_FeatureVectorIndexs = env->GetIntArrayElements(
			fv_FeatureVectorIndex, 0);
	//Copy FeatureVector Label
	double *fv_FeatureVectorLabels = env->GetDoubleArrayElements(
			fv_FeatureVectorLabel, 0);
	//class label
	double fv_class1, fv_class2;

	//Check false
	if (stop - start - 1 == 0) {
		LOGE("Error!");
		return false;
	}

	//feature vector count in partition
	unsigned int fv_FeatureVectorCount = fv_FeatureLength - (stop - start);
	//size of feature vector: SIZE
	unsigned int fv_FeatureVectorSize = SIZE;
//	LOGG("Total features:%d", fv_FeatureVectorCount);
	//total values: featureVectorCount * (featureVectorSize + label)
	int total_length = fv_FeatureVectorCount * fv_FeatureVectorSize;

	//buffer array to store data
	double *buffer = new double[total_length];

	//label array to store label mapping
	double *labels = new double[fv_FeatureVectorCount];

	//parse feature vectors
	for (size_t j = 0; j < fv_FeatureLength - (stop - start); j++) {
		size_t k = fv_FeatureVectorIndexs[(j < start ? j : j + (stop - start))];
		double fv_class = (double) fv_FeatureVectorLabels[(
				j < start ? j : j + (stop - start))];
		for (int i = 0; i < fv_FeatureVectorSize; i++) {
			buffer[j * SIZE + i] = fv_FeatureDatas[k * SIZE + i];
		}
		*(labels + j) = fv_class;
	}
	//Do Classification LDA train code:
	//Calculate target and non target feature
	std::map<double, int> fv_ClassLabels;
	int i;
	for (i = 0; i < fv_FeatureVectorCount; i++) {
		fv_ClassLabels[labels[i]]++;
	}

	if (fv_ClassLabels.size() != 2) {
		LOGE("Error, LDA in only training with 2 classes");
	}
	//2 classes using for trainer
	fv_class1 = fv_ClassLabels.begin()->first;
	fv_class2 = fv_ClassLabels.rbegin()->first;
	//Calculate mean of feature vector
	//meanFeatureVector1: 57x1
	cv::Mat meanFeatureVector1;
	meanFeatureVector1 = cv::Mat::zeros(cvSize(1, 57), CV_64FC1);
	//meanFeatureVector2: 57x1
	cv::Mat meanFeatureVector2;
	meanFeatureVector2 = cv::Mat::zeros(cvSize(1, 57), CV_64FC1);
	//FeatureVector matrix data: 57x1
	cv::Mat fv_matrix;
	fv_matrix = cv::Mat::zeros(cvSize(1, 57), CV_64FC1);

	for (int index = 0; index < fv_FeatureVectorCount; index++) {
		//Get data values of feature vector
		for (int i = 0; i < SIZE; i++) {
			fv_matrix.row(0).at<double>(i) = *(buffer + index * SIZE + i);
		}
		double fv_label = labels[index];

		if (fv_label == fv_class1) {
			meanFeatureVector1 += fv_matrix;
		}

		if (fv_label == fv_class2) {
			meanFeatureVector2 += fv_matrix;
		}
	}

	//Calculate average mean of feature vector
	meanFeatureVector1 /= (double) fv_ClassLabels[fv_class1];
	meanFeatureVector2 /= (double) fv_ClassLabels[fv_class2];

	//Calculate sigma
	//Sigma matrix: 57x57
	cv::Mat m_Sigma;
	//Diff matrix: 57x1
	cv::Mat m_diff;
	m_Sigma = cv::Mat::zeros(cvSize(SIZE, SIZE), CV_64FC1);

	for (int index = 0; index < fv_FeatureVectorCount; index++) {

		double fv_label = labels[index];
		int present_index = index;
		if (fv_label == fv_class1) {
			cv::Mat fv_matrixs;
			fv_matrixs = cv::Mat::zeros(cvSize(1, 57), CV_64FC1);
			//Get data values of feature vector
			for (int i = 0; i < SIZE; i++) {
				fv_matrixs.row(i).at<double>(0) = *(buffer
						+ present_index * SIZE + i);
			}
			m_diff = fv_matrixs - meanFeatureVector1;

			//Calculate outer_product for class 1
			cv::Mat res = m_diff * m_diff.t();
			m_Sigma += res / (fv_FeatureVectorCount - 2.);
		}

		if (fv_label == fv_class2) {

			cv::Mat fv_matrixs;
			fv_matrixs = cv::Mat::zeros(cvSize(1, 57), CV_64FC1);
			//Get data values of feature vector
			for (int i = 0; i < SIZE; i++) {
				fv_matrixs.row(i).at<double>(0) = *(buffer
						+ present_index * SIZE + i);
			}
			m_diff = fv_matrixs - meanFeatureVector2;

			//Calculate outer_product for class 2
			cv::Mat res = m_diff * m_diff.t();
			m_Sigma += res / (fv_FeatureVectorCount - 2.);
		}

	}

	cv::Mat m_SigmaInverse = m_Sigma.inv();

	//Coefficients: 57x1
	cv::Mat m_Coefficients = m_SigmaInverse
			* (meanFeatureVector1 - meanFeatureVector2);
	cv::Mat product = ((meanFeatureVector1 + meanFeatureVector2).t()
			* m_Coefficients);
	double value = -0.5 * product.at<double>(0, 0);

	cv::Mat fv_Coefficients(1, 1, CV_64FC1);
	fv_Coefficients.row(0).at<double>(0) = value;
	fv_Coefficients.push_back(m_Coefficients);

	coefficients = fv_Coefficients;
	//Write coefficients to file
	//Write to file
	FILE *file;
	const char* path = env->GetStringUTFChars(pathFile, 0);
	file = fopen(path, "w+");
	for (int i = 0; i < fv_Coefficients.rows; i++) {
		jstring s = env->NewStringUTF(
				to_string(fv_Coefficients.row(i).at<double>(0)).c_str());
		const char* str = env->GetStringUTFChars(s, 0);
		fprintf(file, "%s", str);
		fprintf(file, "%s", "\n");
		env->ReleaseStringUTFChars(s, str);
		env->DeleteLocalRef((jobject) s);
	}

	env->ReleaseStringUTFChars(pathFile, path);
	fclose(file);

	delete buffer;
	delete labels;

	return true;
}

/*
 * Classifier LDA
 * get result of classify
 */
double classify(cv::Mat matrixData, double fv_Classify, cv::Mat coefficients) {
	double temp = fv_Classify;
	if (((matrixData.rows + 1) != (unsigned int) coefficients.rows)) {
		LOGE("Feature Vector Size and hyper plane does not match!");
	}
	cv::Mat l_Features(1, 1, CV_64FC1);
	l_Features.row(0).at<double>(0) = 1;
	cv::Mat t = matrixData.t();
	l_Features.push_back(matrixData);

	cv::Mat l_Result = -l_Features.t() * coefficients;
//	LOGG("Value result: %lf", l_Result.row(0).at<double>(0));
	if (l_Result.row(0).at<double>(0) < 0) {
		temp = 1.0;
	} else {
		temp = 2.0;
	}

	return temp;
}
/*
 * Function GetAccuracy
 * Input: Start Index, Stop Index, Array of feature vectors, feature vector index, coefficients
 */

double getAccuracy(const size_t startIndex, const size_t stopIndex,
		double* featureVectorData, int* featureVectorIndex,
		double* featureVectorLabel) {
	size_t l_iSuccessfullTrainerCount = 0;

	if (stopIndex - startIndex == 0) {
		return 0;
	}
	double fv_Classify = 0;
	unsigned int m_FeatureVectorSize = SIZE;
	cv::Mat fv_matrix(cvSize(1, 57), CV_64FC1);

	for (size_t j = startIndex; j < stopIndex; j++) {
		size_t k = *(featureVectorIndex + j);

		for (int i = 0; i < fv_matrix.rows; i++) {
			fv_matrix.row(i).at<double>(0) =
					*(featureVectorData + k * SIZE + i);
		}
		double m_TrainerClass = (double) *(featureVectorLabel + j);

		fv_Classify = classify(fv_matrix, fv_Classify, coefficients);
		if (fv_Classify == m_TrainerClass) {
			l_iSuccessfullTrainerCount++;
		}
	}

	return (double) (l_iSuccessfullTrainerCount * 100.0)
			/ (stopIndex - startIndex);
}

/*
 * Classifier Trainer Function
 * Result: classifier coefficients and write to file
 */JNIEXPORT jdouble JNICALL Java_lab411_eeg_p300processing_MainProcess_TestTrain(
		JNIEnv *env, jclass jc, jdoubleArray featureVectorData,
		jintArray featureVectorIndex, jdoubleArray featureLabelData,
		jboolean receivedTrain, jboolean isRandomize, jint partitionCount,
		jstring pathFile) {
	//Total feature vectors
	int fv_length = env->GetArrayLength(featureLabelData);
	//FeatureVector Data
	double *fv_FeatureVectorData = env->GetDoubleArrayElements(
			featureVectorData, 0);
	//FeatureVector Label Backup
	double *fv_FeatureLabel = env->GetDoubleArrayElements(featureLabelData, 0);
	//FeatureVector Label
	double *fv_FeatureVectorLabel = env->GetDoubleArrayElements(
			featureLabelData, 0);
	//FeatureVector Index Backup
	int *fv_FeatureVectorIndexs = env->GetIntArrayElements(featureVectorIndex,
			0);
	//Set FeatureVectorCount
	std::map<double, int> fv_FeatureVectorCount;
	for (int index = 0; index < fv_length; index++) {
		fv_FeatureVectorCount[*(fv_FeatureVectorLabel + index)]++;
	}
	double fv_final = 0;
	//Received Train
	if (receivedTrain) {
		if (fv_length < partitionCount) {
			LOGE("Fewer examples than the specified partition count");

		}
		if (fv_length == 0) {
			LOGE("Fewer examples than the specified partition count");
			LOGE(
					"Received train stimulation but no training examples received\n");
		} else {
			LOGI("Data dim");
			std::vector<int> fv_FeatureVectorIndex;
			//set value of feature vector index
			for (int index = 0; index < fv_length; index++) {
				fv_FeatureVectorIndex.push_back(index);
			}
			//Accuracy value in each partition
			double fv_PartitionAccuracy = 0;
			//Final accuracy
			double fv_FinalAccuracy = 0;
			//All Accuracy values
			vector<double> lv_PartitionAccuracies(
					(unsigned int) partitionCount);
			//int array stored index if random
			int *fv_Index = new int[fv_FeatureVectorIndex.size()];

			// randomize the vector if necessary
			bool randomize = isRandomize;
			if (randomize) {
				std::random_shuffle(fv_FeatureVectorIndex.begin(),
						fv_FeatureVectorIndex.end());
				for (int i = 0; i < fv_length; i++) {
					//Copy index with random
					*(fv_Index + i) = fv_FeatureVectorIndex[i];
					//Mapping label with index
					*(fv_FeatureVectorLabel + i) = *(fv_FeatureLabel
							+ fv_FeatureVectorIndex[i]);
				}

			} else {
				for (int i = 0; i < fv_length; i++) {
					*(fv_Index + i) = *(fv_FeatureVectorIndexs + i);
					*(fv_FeatureVectorLabel + i) = *(fv_FeatureLabel + i);
				}
			}

			if (partitionCount > 2) {
				for (unsigned long long i = 0; i < partitionCount; i++) {
					unsigned int startIndex = (unsigned int) (((i) * fv_length)
							/ partitionCount);
					unsigned int stopIndex = (unsigned int) (((i + 1)
							* fv_length) / partitionCount);
					//Copy FeatureVector Array, FeatureVector Index, FeatureVector Label
					//Create new Double Array: FeatureVector Array
					jdoubleArray featureVectorArray = env->NewDoubleArray(
							fv_length * SIZE);
					env->SetDoubleArrayRegion(featureVectorArray, (jsize) 0,
							(jsize) (fv_length * SIZE),
							(jdouble*) fv_FeatureVectorData);
					//Create new Int Array: FeatureVector Index Array
					jintArray featureIndexArray = env->NewIntArray(fv_length);
					env->SetIntArrayRegion(featureIndexArray, 0, fv_length,
							fv_Index);
					//Copy FeatureVector Label Array
					jdoubleArray featureVectorLabelArray = env->NewDoubleArray(
							fv_length);
					env->SetDoubleArrayRegion(featureVectorLabelArray, 0,
							fv_length, fv_FeatureVectorLabel);
					//Do Classifier Training
					if (Java_lab411_eeg_p300processing_MainProcess_CalculateTraining(
							env, jc, startIndex, stopIndex, featureVectorArray,
							featureIndexArray, featureVectorLabelArray,
							fv_length, pathFile)) {
						//	LOGI("Values accuracy call here");
						//Get Accuracy
						fv_PartitionAccuracy = getAccuracy(startIndex,
								stopIndex, fv_FeatureVectorData, fv_Index,
								fv_FeatureVectorLabel);
						lv_PartitionAccuracies[(unsigned int) i] =
								fv_PartitionAccuracy;
						fv_FinalAccuracy += fv_PartitionAccuracy;

					}
					env->ReleaseDoubleArrayElements(featureVectorLabelArray,
							fv_FeatureVectorLabel, 0);
					env->ReleaseDoubleArrayElements(featureVectorArray,
							fv_FeatureVectorData, 0);
					env->ReleaseIntArrayElements(featureIndexArray, fv_Index,
							0);
					LOGG(
							"Finished with partition %d / %d  with performance : %lf \n", (int) (i+1), partitionCount, fv_PartitionAccuracy);
				}

				double fv_Mean = fv_FinalAccuracy / partitionCount;
				float fv_Deviation = 0;

				for (unsigned long long index = 0; index < partitionCount;
						index++) {
					double fv_Diff =
							lv_PartitionAccuracies[(unsigned int) index]
									- fv_Mean;
					fv_Deviation += fv_Diff * fv_Diff;
				}
				fv_Deviation = sqrt(fv_Deviation / (float) partitionCount);
				LOGG(
						"Cross-validation test accuracy is %f, sigma = %ff ", fv_Mean, fv_Deviation);

			} else {
				LOGG("Training without cross-validation");
				LOGG(
						"*** Reported training set accuracy will be optimistic ***");
			}
			LOGG("Training final classifier on the whole set...");
			//Training final classifier on the whole set
			//Create new Double Array: FeatureVector Array
			jdoubleArray featureVectorArray = env->NewDoubleArray(
					fv_length * SIZE);
			env->SetDoubleArrayRegion(featureVectorArray, (jsize) 0,
					(jsize) (fv_length * SIZE),
					(jdouble*) fv_FeatureVectorData);
			//Create new Int Array: FeatureVector Index Array
			jintArray featureIndexArray = env->NewIntArray(fv_length);
			env->SetIntArrayRegion(featureIndexArray, 0, fv_length, fv_Index);
			//Copy FeatureVector Label Array
			jdoubleArray featureVectorLabelArray = env->NewDoubleArray(
					fv_length);
			env->SetDoubleArrayRegion(featureVectorLabelArray, 0, fv_length,
					fv_FeatureVectorLabel);
			//Call classifier trainer
			Java_lab411_eeg_p300processing_MainProcess_CalculateTraining(env,
					jc, 0, 0, featureVectorArray, featureIndexArray,
					featureVectorLabelArray, fv_length, pathFile);
			fv_final = getAccuracy(0, fv_length, fv_FeatureVectorData, fv_Index,
					fv_FeatureVectorLabel);
			LOGG( "Training set accuracy is: %lf (optimistic)", fv_final);
			//Write to configuration file
			env->ReleaseDoubleArrayElements(featureVectorLabelArray,
					fv_FeatureVectorLabel, 0);
			env->ReleaseDoubleArrayElements(featureVectorArray,
					fv_FeatureVectorData, 0);
			env->ReleaseIntArrayElements(featureIndexArray, fv_Index, 0);

		}
	}
	env->ReleaseIntArrayElements(featureVectorIndex, fv_FeatureVectorIndexs, 0);
	env->ReleaseDoubleArrayElements(featureLabelData, fv_FeatureLabel, 0);
	env->ReleaseDoubleArrayElements(featureVectorData, fv_FeatureVectorData, 0);
	env->ReleaseDoubleArrayElements(featureLabelData, fv_FeatureVectorLabel, 0);
	return fv_final;
}

/*
 * Feature Aggregator Function
 * Aggregates all target and nontarget to featurevectors
 */JNIEXPORT jobjectArray JNICALL Java_lab411_eeg_p300processing_MainProcess_FeatureAggregator(
		JNIEnv *env, jclass jc, jobject target, jobject nontarget) {
	jclass cls = env->FindClass("java/util/List");
	// find id delegateMethod
	jmethodID list_size = env->GetMethodID(cls, "size", "()I");
	jmethodID list_get = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");

	// get size of List target and nontarget
	jint total_length = env->CallIntMethod(target, list_size)
			+ env->CallIntMethod(nontarget, list_size);

	//get FeatureVector class
	jclass classes = env->FindClass("lab411/eeg/p300processing/FeatureVector");
	//Get Method of FeatureVector class
	jobjectArray result = env->NewObjectArray(total_length, classes, 0);
	jmethodID constructor = env->GetMethodID(classes, "<init>", "()V");
	jmethodID setLabel = env->GetMethodID(classes, "setLabel", "(D)V");
	jmethodID setValue = env->GetMethodID(classes, "setValue", "(ID)V");

	//Copy target lists
	jobjectArray arrayElement;
	//Process target lists
	for (int i = 0; i < env->CallIntMethod(target, list_size); i++) {
		//Get object array
		arrayElement = (jobjectArray) env->CallObjectMethod(target, list_get,
				i);
		//Get length array
		int length_array = env->GetArrayLength(arrayElement);
		jdoubleArray dim_signal = (jdoubleArray) env->GetObjectArrayElement(
				arrayElement, 0);
		int length2_array = env->GetArrayLength(dim_signal);
		//Convert to 1D array
		double *data = new double[length_array * length2_array];
		for (int j = 0; j < length_array; ++j) {
			jdoubleArray oneDim = (jdoubleArray) env->GetObjectArrayElement(
					arrayElement, j);
			jdouble *element = env->GetDoubleArrayElements(oneDim, 0);

			for (int k = 0; k < length2_array; ++k) {

				data[j * length2_array + k] = element[k];
			}
			env->ReleaseDoubleArrayElements(oneDim, element, 0);
		}
		env->DeleteLocalRef(dim_signal);
		env->DeleteLocalRef(arrayElement);
		//Create new FeatureVector object
		jobject evt = env->NewObject(classes, constructor);
		//Set label: Target: 1
		env->CallVoidMethod(evt, setLabel, 1.0);
		//Copy 1D array to data
		for (int index = 0; index < length_array * length2_array; index++) {

			//	env->CallVoidMethod(evt,setData,data);
			env->CallVoidMethod(evt, setValue, index, data[index]);

		}
		//Add object to FeatureVector Array
		env->SetObjectArrayElement(result, i, evt);
		env->DeleteLocalRef(evt);
		free(data);
	}

	//Copy nontarget lists
	for (int i = 0; i < env->CallIntMethod(nontarget, list_size); i++) {
		//Get object array
		arrayElement = (jobjectArray) env->CallObjectMethod(nontarget, list_get,
				i);
		//Get length array
		int length_array = env->GetArrayLength(arrayElement);
		jdoubleArray dim_signal = (jdoubleArray) env->GetObjectArrayElement(
				arrayElement, 0);
		int length2_array = env->GetArrayLength(dim_signal);
		//Convert to 1D array
		double *data = new double[length_array * length2_array];
		for (int j = 0; j < length_array; ++j) {
			jdoubleArray oneDim = (jdoubleArray) env->GetObjectArrayElement(
					arrayElement, j);
			jdouble *element = env->GetDoubleArrayElements(oneDim, 0);

			for (int k = 0; k < length2_array; ++k) {
				data[j * length2_array + k] = element[k];
			}
			env->ReleaseDoubleArrayElements(oneDim, element, 0);

		}
		env->DeleteLocalRef(dim_signal);
		env->DeleteLocalRef(arrayElement);

		//Create new FeatureVector object
		jobject evt = env->NewObject(classes, constructor);
		//Set label: Target: 2
		env->CallVoidMethod(evt, setLabel, 2.0);
		//Copy 1D array to data
		for (int index = 0; index < length_array * length2_array; index++) {
			//	env->CallVoidMethod(evt,setData,data);
			env->CallVoidMethod(evt, setValue, index, data[index]);
		}

		//Add object to FeatureVector Array
		env->SetObjectArrayElement(result,
				i + env->CallIntMethod(target, list_size), evt);
		env->DeleteLocalRef(evt);
		free(data);
	}

	return result;

}

/*
 * Calculate Voting Classifier
 */
void VotingClassifier(double l_vScore[], int &labelRow, int &labelCol) {
	labelRow = 0;
	labelCol = 0;
	double l_f64ResultScore = -1E100;
	for (int i = 0; i < 6; i++) {

		LOGG("Values: %lf", l_vScore[i]);
		if (l_vScore[i] > l_f64ResultScore) {
			l_f64ResultScore = l_vScore[i];
			labelRow = i;
		} else {
			//	LOGG("Not found suitable label row");
		}
	}
	l_f64ResultScore = -1E100;
	for (int i = 0; i < 6; i++) {

		LOGG("Values: %lf", l_vScore[i+6]);
		if (l_vScore[i + 6] > l_f64ResultScore) {
			l_f64ResultScore = l_vScore[i + 6];
			labelCol = i;
		} else {
			//	LOGG("Not found suitable label column");
		}
	}

}

/*
 * Calculate classification value
 */
double classify(cv::Mat matrixData, cv::Mat coefficient) {
	if (((matrixData.rows + 1) != (unsigned int) coefficient.rows)) {
		LOGE("Feature Vector Size and hyper plane does not match!");
	}
	cv::Mat l_Features(1, 1, CV_64FC1);
	l_Features.row(0).at<double>(0) = 1;
	cv::Mat t = matrixData.t();
	l_Features.push_back(matrixData);

	cv::Mat l_Result = -l_Features.t() * coefficient;
	return l_Result.row(0).at<double>(0);
}

/*
 * Method Voting Classifier
 */JNIEXPORT jstring JNICALL Java_lab411_eeg_p300processing_MainProcess_VotingProcess(
		JNIEnv *env, jclass jc, jobject listDataRunning,
		jdoubleArray coefficients) {

	jclass cls = env->FindClass("java/util/List");
	// find id delegateMethod
	jmethodID list_size = env->GetMethodID(cls, "size", "()I");
	jmethodID list_get = env->GetMethodID(cls, "get", "(I)Ljava/lang/Object;");
	// get size of List Data
	jint total_length = env->CallIntMethod(listDataRunning, list_size);
	//get size of double array
	jobjectArray element = (jobjectArray) env->CallObjectMethod(listDataRunning,
			list_get, 0);
	//Get length array
	int length_arrayElement = env->GetArrayLength(element);
	//Get Length coefficients array
	int length_coeffs = env->GetArrayLength(coefficients);
	//Get coefficients array
	double *coeff = env->GetDoubleArrayElements(coefficients, 0);
	//Set Matrix coefficients
	cv::Mat coefficient(length_coeffs, 1, CV_64FC1);
	//Set value of matrix
	for (int index = 0; index < length_coeffs; index++) {
		coefficient.row(index).at<double>(0) = *(coeff + index);
	}
	double *l_vScore = new double[12];
	for (int i = 0; i < 12; i++) {
		l_vScore[i] = 0;
	}
	env->DeleteLocalRef((jobject) element);
	jdoubleArray arrayElement;
	int count = 0;
	for (int i = 0; i < total_length; i++) {
		//Get object array
		arrayElement = (jdoubleArray) env->CallObjectMethod(listDataRunning,
				list_get, i);
		double* pSignal = env->GetDoubleArrayElements(arrayElement, 0);
		int value = (int) *(pSignal + length_arrayElement - 1);
		//Classifier
		jdouble signal[length_arrayElement - 1];
		for (int index = 0; index < length_arrayElement - 1; index++) {
			signal[index] = *(pSignal + index);
		}

		cv::Mat dataElement = cv::Mat(length_arrayElement - 1, 1, CV_64FC1,
				signal).clone();
		double classificationVal = classify(dataElement, coefficient);
		//Return cv::Mat
		switch (value) {
		case 0:
			l_vScore[0] += -classificationVal;
			break;

		case 1:
			l_vScore[1] += -classificationVal;
			break;

		case 2:
			l_vScore[2] += -classificationVal;
			break;

		case 3:
			l_vScore[3] += -classificationVal;
			break;

		case 4:

			l_vScore[4] += -classificationVal;

			break;

		case 5:
			l_vScore[5] += -classificationVal;
			break;

		case 6:
			l_vScore[6] += -classificationVal;
			break;

		case 7:
			l_vScore[7] += -classificationVal;
			break;

		case 8:
			l_vScore[8] += -classificationVal;
			break;

		case 9:
			l_vScore[9] += -classificationVal;
			break;

		case 10:
			l_vScore[10] += -classificationVal;
			break;
		case 11:
			l_vScore[11] += -classificationVal;
			break;
		default:
			break;
		}
		env->ReleaseDoubleArrayElements(arrayElement, pSignal, 0);
		env->DeleteLocalRef((jobject) arrayElement);
	}
	//Call Voting Classifier
	VotingClassifier(l_vScore, labelRow, labelCol);
	delete[] l_vScore;
	env->ReleaseDoubleArrayElements(coefficients, coeff, 0);

	jstring returnCharacter = env->NewStringUTF(
			*(grid + labelRow * 6 + labelCol));
	return returnCharacter;
}

/*
 * XDawn Classifier Trainer Function
 */

JNIEXPORT jint JNICALL Java_lab411_eeg_p300processing_MainProcess_xDawnTrainer(
		JNIEnv *env, jclass jobj, jobjectArray signal, jobjectArray erpSignal,
		jintArray index, jdouble chunk, jint sizes, jstring pathFile) {
	//Copy signal array to 2D double array
	int len1_signal = env->GetArrayLength(signal);
	jdoubleArray dim_signal = (jdoubleArray) env->GetObjectArrayElement(signal,
			0);
	int len2_signal = env->GetArrayLength(dim_signal);
	double input[len1_signal][len2_signal];
	for (int i = 0; i < len1_signal; ++i) {
		jdoubleArray oneDim = (jdoubleArray) env->GetObjectArrayElement(signal,
				i);
		jdouble *element = env->GetDoubleArrayElements(oneDim, 0);
		for (int j = 0; j < len2_signal; ++j) {
			input[i][j] = element[j];
		}
	}

	//Copy erp signal to 2D double array
	int len1_erp = env->GetArrayLength(erpSignal);
	jdoubleArray dim_erp = (jdoubleArray) env->GetObjectArrayElement(erpSignal,
			0);
	int len2_erp = env->GetArrayLength(dim_erp);
	double erp[len1_erp][len2_erp];
	for (int i = 0; i < len1_erp; i++) {
		jdoubleArray temp = (jdoubleArray) env->GetObjectArrayElement(erpSignal,
				i);
		jdouble *elements = env->GetDoubleArrayElements(temp, 0);
		for (int j = 0; j < len2_erp; j++) {
			erp[i][j] = elements[j];
		}
	}

	//ChunkCount
	int chunkCount = (int) chunk;
	//Channel Count: input channels
	//Channel Count: 14
	int channelCount = 14;
	//Sample Count per chunk: 1 chunk has 8 samples (equal 250ms, frequency 32 Hz)
	int sampleCountPerChunk = 8;
	//Sample Count per ERP: 1 chunk has 19 samples (equal 600ms, frequency 32 Hz)
	int sampleCountPerERP = 19;
	//Total samples
	int totalSamples = chunkCount * sampleCountPerChunk;

	//D Matrix
	cv::Mat D_Matrix(totalSamples, sampleCountPerERP, CV_64FC1);

	//Averaged ERP Matrix
	cv::Mat averagedERPMatrix(channelCount, sampleCountPerERP, CV_64FC1, erp);

	//signal matrix
	cv::Mat signalMatrix(channelCount, totalSamples, CV_64FC1, input);

	/*
	 * Set values of D Matrix : 0/1 depends on startIndex or correspond between ERP Start Time and Signal Start Time
	 */
	int length = env->GetArrayLength(index);
	int *arr = env->GetIntArrayElements(index, 0);

	for (int i = 0; i < length; i++) {
		int erpIndex = *(arr + i);
		for (int k = 0; k < sampleCountPerERP; k++) {
			D_Matrix.at<double>(erpIndex + k, k) = 1;
		}
	}

	//Evoked potential size:
	// int size = l_vEvokedPotential.size();
	int size = sizes;
	// Set A Matrix
	cv::Mat A(14, 14, CV_64FC1);
	cv::Mat D = D_Matrix.t() * D_Matrix;
	D = D.inv(DECOMP_LU);
	A = (averagedERPMatrix * D * averagedERPMatrix.t()) * (double) (size)
			/ (double) totalSamples;

	// Set B Matrix
	cv::Mat B(14, 14, CV_64FC1);
	B = (signalMatrix * signalMatrix.t()) / (double) totalSamples;

	/*
	 * Calculate eigen and coefficients
	 */
	MatrixXd e_M;
	cv::Mat EVecsum, EValsum;

	//Calculate matrix: Ax=lBx
	cv::Mat result = B.inv() * A;

	//Calculate eigen
	cv2eigen(result, e_M);

	//Calculate eigen values and eigen vectors
	Eigen::EigenSolver<MatrixXd> es(e_M);
	eigen2cv(MatrixXd(es.eigenvectors().real()), EVecsum);
	eigen2cv(MatrixXd(es.eigenvalues().real()), EValsum);

	//Calculate itpp::norm of vector
	std::vector<double> norms;
	//Map between eigen values and eigen vector
	std::map<double, cv::Mat> lEigenVector;

	int i, j;
	for (i = 0; i < 14; i++) {
		long double sum = 0;
		//Calculate norm
		//EVecsum.col(i): values at column i
		for (j = 0; j < 14; j++) {
			double a = EVecsum.col(i).at<double>(j, 0);

			sum += pow(abs(a), 2);
		}
		//Stored at a std::vector
		norms.push_back((double) sqrt(sum));
		sum = 0;
	}

	for (i = 0; i < 14; i++) {
		//Get values norm
		double scale = norms.at(i);
		//Divide vector to norm
		for (j = 0; j < 14; j++) {
			double a = EVecsum.col(i).at<double>(j, 0);
			a /= scale;
			EVecsum.col(i).at<double>(j, 0) = a;
		}
		//Stored new values of vector by eigen values
		lEigenVector[EValsum.at<double>(0, i)] = EVecsum.col(i);
	}

	std::map<double, cv::Mat>::const_reverse_iterator it;
	//Write coefficients to file
	//Write to file
	FILE *file;
	const char* path = env->GetStringUTFChars(pathFile, 0);
	file = fopen(path, "w+");
	for (it = lEigenVector.rbegin(), i = 0; it != lEigenVector.rend() && i < 3;
			it++, i++) {

		for (j = 0; j < 14; j++) {

			const char* dt = env->GetStringUTFChars(
					env->NewStringUTF(
							to_string(it->second.at<double>(j, 0)).c_str()), 0);
			fprintf(file, "%s", dt);
			fprintf(file, "%s", "\n");
			env->ReleaseStringUTFChars(
					env->NewStringUTF(
							to_string(it->second.at<double>(j, 0)).c_str()),
					dt);
		}
	}
	env->ReleaseStringUTFChars(pathFile, path);
	fclose(file);
	return 0;
}

