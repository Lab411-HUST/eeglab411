#ifdef __EMOTION_H__
#define __EMOTION_H__

#include "Filter.h"

double* readFile(char* paths, int N);
void writeFile(char* paths, double* signal, int N)

void calcEmotion(double* _af3, double* _af4, double* _f3, double* _f4, int N, double *_valence, double *_arousalAF3, double *_arousalAF4, double *_arousalF3, double *_arousalF4);


#endif
