#include <jni.h>
#include "lab411_eeg_filedata_FileData.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_lab411_eeg_filedata_FileData_writeFiledata
  (JNIEnv *env , jclass ojb,jstring pathFile ,jstring data)
{
	FILE *f;
	const char* path = env->GetStringUTFChars(pathFile,0);
	const char* dt = env->GetStringUTFChars(data,0);
	f = fopen(path,"wt");
	fprintf(f,"%s",dt);
	env->ReleaseStringUTFChars(pathFile,path);
	env->ReleaseStringUTFChars(data,dt);
	fclose (f);
	}

JNIEXPORT jint JNICALL Java_lab411_eeg_filedata_FileData_readDataFileOutput
  (JNIEnv *env, jclass ojb, jstring pathfile)
{
	int result  = 0;
	FILE *f;
	const char* path = env->GetStringUTFChars(pathfile,0);
	f = fopen(path,"r");
	fscanf(f,"%d",&result);
	env->ReleaseStringUTFChars(pathfile,path);
	fclose(f);
	return result;
	}
