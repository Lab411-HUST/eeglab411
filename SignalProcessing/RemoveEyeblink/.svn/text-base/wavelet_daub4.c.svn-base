#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "eegdata_conf.h"
#include "wavelet_daub4.h"

double c[4] = {
     0.4829629131445341, 
     0.8365163037378079, 
     0.2241438680420133, 
    -0.1294095225512603 };

static double *read_data_from_file(char *pFilePath, int Sample)
  {
    int i; 
    double *u;
    FILE *fl;
    char Path[512];
    
    strcpy(Path, pFilePath);
   
    fl = fopen(Path, "r");
    u = malloc ( Sample * sizeof ( double ) );
    
    for (i=0; i<Sample; i++) {
	fscanf(fl, "%lf ", &(u[i]));	
    }
    fclose(fl);
    return u;	
  }

void WaveletTransform(double *Data, int Sample, int CompIndex, double **Data_WT){
  FILE *OutputFile;
  int i;
  char path[512];

  Data_WT = daub4_transform(Sample, Data);

  /*
  if(CompIndex==0)
    OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA_DWT", "wb");
  else
    OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA_DWT", "ab");
  
  fprintf(OutputFile, "**********************Comp %i\n", CompIndex);  
  for(i=0; i<Sample; i++)
    fprintf(OutputFile, "%f\n", Data_WT[i]);  
  fclose(OutputFile);  */
}

double* WaveletTransformInverse(double *Data, int Sample, int CompIndex, int Level){
   double *Data_DWT;
   double *Data_IDWT;
   FILE *OutputFile;
   int i;
   int a, b;

   Data_DWT = daub4_transform(Sample, Data);
	
   a = Sample;
   i = Level;
   for(i; i>=0; i--)
      a = a / 2;
   b= a*2;
   
   for(i=0; i<a; i++)
      Data_DWT[i] = 0;
   for(i=b; i < Sample; i++)
      Data_DWT[i] = 0;

  Data_IDWT = daub4_transform_inverse(Sample, Data_DWT);
   
  /*
  if(CompIndex==CHANNEL_NUMBER_MAX)
    OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataFp2_IDWT", "wb");
  else if(CompIndex==0){
    OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA_IDWT", "wb");
    fprintf(OutputFile, "**********************Comp %i\n", CompIndex);  
  }else{
    OutputFile = fopen("/storage/sdcard0/NickGun/EEG/Liec-len-xuong/DataICA_IDWT", "ab");
    fprintf(OutputFile, "**********************Comp %i\n", CompIndex);  
  }
  
  for(i = 0; i<Sample; i++)
    fprintf(OutputFile, "%f\n", Data_IDWT[i]);
  fclose(OutputFile);*/
  
  return Data_IDWT;
}

double *daub4_transform ( int n, double x[] )
/******************************************************************************/
/*
  Purpose:
    DAUB4_TRANSFORM computes the DAUB4 transform of a vector.
    
  Parameters:
    Input, int N, the dimension of the vector.
    N must be a power of 2 and at least 4.
    Input, double X[N], the vector to be transformed. 
    Output, double DAUB4_TRANSFORM[N], the transformed vector.
*/
{
  int i, j;
  int j0, j1, j2, j3;
  int m;
  double *y;
  double *z;

  y = r8vec_copy_new ( n, x );
  z = ( double * ) malloc ( n * sizeof ( double ) );
  for ( i = 0; i < n; i++ )
  {
    z[i] = 0.0;
  }
  m = n;

  while ( 4 <= m )
  {
    i = 0;

    for ( j = 0; j < m - 1; j = j + 2 )
    {
      j0 = i4_wrap ( j,     0, m - 1 );
      j1 = i4_wrap ( j + 1, 0, m - 1 );
      j2 = i4_wrap ( j + 2, 0, m - 1 );
      j3 = i4_wrap ( j + 3, 0, m - 1 );
      
      z[i]     = c[0] * y[j0] + c[1] * y[j1] 
               + c[2] * y[j2] + c[3] * y[j3];
      z[i+m/2] = c[3] * y[j0] - c[2] * y[j1] 
               + c[1] * y[j2] - c[0] * y[j3];

      i = i + 1;
    }
    
    for ( i = 0; i < m; i++ )
    {
      y[i] = z[i];
    }

    m = m / 2;
  }

  free ( z );
  
  return y;
}

double *daub4_transform_inverse ( int n, double y[] )
/******************************************************************************/
/*
  Purpose:
    DAUB4_TRANSFORM_INVERSE inverts the DAUB4 transform of a vector.
    
  Parameters:
    Input, int N, the dimension of the vector.
    N must be a power of 2 and at least 4.
    Input, double Y[N], the transformed vector. 
    Output, double DAUB4_TRANSFORM_INVERSE[N], the original vector.
*/
{
  int i, j;
  int i0, i1, i2, i3;
  int m;
  double *x;
  double *z;

  x = r8vec_copy_new ( n, y );
  z = ( double * ) malloc ( n * sizeof ( double ) );
  for ( i = 0; i < n; i++ )
  {
    z[i] = 0.0;
  }

  m = 4;

  while ( m <= n )
  {
    j = 0;
    for ( i = 0; i < m / 2; i++ )
    { 
      i0 = i4_wrap ( i - 1,          0,     m / 2 - 1 );
      i2 = i4_wrap ( i,              0,     m / 2 - 1 );
      i1 = i4_wrap ( i + m / 2 - 1,  m / 2, m - 1 );
      i3 = i4_wrap ( i + m / 2,      m / 2, m - 1 );
      
      z[j]   = c[2] * x[i0] + c[1] * x[i1] 
             + c[0] * x[i2] + c[3] * x[i3];
      z[j+1] = c[3] * x[i0] - c[0] * x[i1] 
             + c[1] * x[i2] - c[2] * x[i3];

      j = j + 2;
    }
    for ( i = 0; i < m; i++ )
    {
      x[i] = z[i];
    }
    m = m * 2;
  }

  free ( z );
  return x;
}

double *r8vec_copy_new ( int n, double a1[] )
/******************************************************************************/
/*
  Purpose:
    R8VEC_COPY_NEW copies an R8VEC.
    
  Parameters:
    Input, int N, the number of entries in the vectors.
    Input, double A1[N], the vector to be copied.
    Output, double R8VEC_COPY_NEW[N], the copy of A1.
*/
{
  double *a2;
  int i;

  a2 = ( double * ) malloc ( n * sizeof ( double ) );

  for ( i = 0; i < n; i++ )
  {
    a2[i] = a1[i];
  }
  return a2;
}



int i4_max ( int i1, int i2 )

/******************************************************************************/
/*
  Purpose:
    I4_MAX returns the maximum of two I4's.
    
  Parameters:
    Input, int I1, I2, are two integers to be compared.
    Output, int I4_MAX, the larger of I1 and I2.
*/
{
  int value;

  if ( i2 < i1 )
  {
    value = i1;
  }
  else
  {
    value = i2;
  }
  return value;
}
/******************************************************************************/

int i4_min ( int i1, int i2 )

/******************************************************************************/
/*
  Purpose:
    I4_MIN returns the smaller of two I4's.
    
  Parameters:
    Input, int I1, I2, two integers to be compared.
    Output, int I4_MIN, the smaller of I1 and I2.
*/
{
  int value;

  if ( i1 < i2 )
  {
    value = i1;
  }
  else
  {
    value = i2;
  }
  return value;
}
/******************************************************************************/

int i4_modp ( int i, int j )

/******************************************************************************/
/*
  Purpose:
    I4_MODP returns the nonnegative remainder of I4 division.

  Discussion:
    If
      NREM = I4_MODP ( I, J )
      NMULT = ( I - NREM ) / J
    then
      I = J * NMULT + NREM
    where NREM is always nonnegative.

    The MOD function computes a result with the same sign as the
    quantity being divided.  Thus, suppose you had an angle A,
    and you wanted to ensure that it was between 0 and 360.
    Then mod(A,360) would do, if A was positive, but if A
    was negative, your result would be between -360 and 0.

    On the other hand, I4_MODP(A,360) is between 0 and 360, always.

  Example:
        I         J     MOD  I4_MODP   I4_MODP Factorization

      107        50       7       7    107 =  2 *  50 + 7
      107       -50       7       7    107 = -2 * -50 + 7
     -107        50      -7      43   -107 = -3 *  50 + 43
     -107       -50      -7      43   -107 =  3 * -50 + 43

  Parameters:
    Input, int I, the number to be divided.
    Input, int J, the number that divides I.
    Output, int I4_MODP, the nonnegative remainder when I is
    divided by J.
*/
{
  int value;

  if ( j == 0 )
  {
    fprintf ( stderr, "\n" );
    fprintf ( stderr, "I4_MODP - Fatal error!\n" );
    fprintf ( stderr, "  I4_MODP ( I, J ) called with J = %d\n", j );
    exit ( 1 );
  }

  value = i % j;

  if ( value < 0 )
  {
    value = value + abs ( j );
  }

  return value;
}

int i4_wrap ( int ival, int ilo, int ihi )
/******************************************************************************/
/*
  Purpose:
    I4_WRAP forces an I4 to lie between given limits by wrapping.

  Example:
    ILO = 4, IHI = 8
    
    I   Value
    -2     8
    -1     4
     0     5
     1     6
     2     7
     3     8
     4     4
     5     5
     6     6
     7     7
     8     8
     9     4
    10     5
    11     6
    12     7
    13     8
    14     4

  Licensing:
    This code is distributed under the GNU LGPL license.
  Parameters:
    Input, int IVAL, an integer value.
    Input, int ILO, IHI, the desired bounds for the integer value.
    Output, int I4_WRAP, a "wrapped" version of IVAL.
*/
{
  int jhi;
  int jlo;
  int value;
  int wide;

  jlo = i4_min ( ilo, ihi );
  jhi = i4_max ( ilo, ihi );

  wide = jhi + 1 - jlo;

  if ( wide == 1 )
  {
    value = jlo;
  }
  else
  {
    value = jlo + i4_modp ( ival - jlo, wide );
  }
  return value;
}
