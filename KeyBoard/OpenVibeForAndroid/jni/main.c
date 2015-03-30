#include <jni.h>
#include "include/hid.h"

hid_device *dev_headset;


JNIEXPORT jint JNICALL Java_lab411_eeg_emotiv_LibEmotiv_OpenDevice(JNIEnv* env, jobject thiz, jstring path){
	const char *path_dev = (*env)->GetStringUTFChars(env, path, 0);
	//LOGI("%s",path_dev);
	dev_headset = hid_open(path_dev);
	if(dev_headset != NULL){
		return 1;
	}else{
		return 0;
	}
}

JNIEXPORT jintArray JNICALL Java_lab411_eeg_emotiv_LibEmotiv_ReadRawData(JNIEnv* env, jobject thiz){
	unsigned char data[32];
	int bytes_read=hid_read(dev_headset, data, 32);
	jintArray raw_data = (*env)->NewIntArray(env,bytes_read);
	 jint *ndata = (*env)->GetIntArrayElements(env, raw_data, NULL);
	 int i=0;
	 for(i=0;i<bytes_read;i++){
		 ndata[i]=data[i];
	 }
	 (*env)->ReleaseIntArrayElements(env, raw_data, ndata, NULL);
	return raw_data;
}
