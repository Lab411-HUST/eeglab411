/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class samsung_lab411_svm_LibSVM */

#ifndef _Included_samsung_lab411_svm_LibSVM
#define _Included_samsung_lab411_svm_LibSVM
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     samsung_lab411_svm_LibSVM
 * Method:    trainClassifierNative
 * Signature: (Ljava/lang/String;IIFILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_samsung_lab411_svm_LibSVM_trainClassifierNative
  (JNIEnv *, jclass, jstring, jint, jint, jfloat, jint, jstring);

/*
 * Class:     samsung_lab411_svm_LibSVM
 * Method:    doClassificationNative
 * Signature: (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_samsung_lab411_svm_LibSVM_doClassificationNative
  (JNIEnv *, jclass, jint, jstring, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
