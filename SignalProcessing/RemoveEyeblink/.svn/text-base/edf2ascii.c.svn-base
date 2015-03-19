/*
***************************************************************************
*
* Author: Teunis van Beelen
*
* Copyright (C) 2007, 2008, 2009, 2010 Teunis van Beelen
*
* teuniz@gmail.com
*
***************************************************************************
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation version 2 of the License.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
***************************************************************************
*
* This version of GPL is at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
*
***************************************************************************
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#if !defined(__APPLE__) && !defined(__MACH__) && !defined(__APPLE_CC__)
  #include <malloc.h>
#endif
#include <locale.h>
#include "edf2ascii.h"
#include "eegdata_conf.h"
#include "matrix.h"

void utf8_to_latin1(char *);
int eegDataSampleCount = 0; //so mau tin hieu lay duoc
char *pFile;
char *pPath;
char chr[10];

char ChanNum[19][3] = {
  "Pz", "Cz", "Fz", "P3", "C3", "F3", "Fp1", "T5", "T3", "F7", "P4", "C4", "F4", "Fp2", "T6", "T4", "F8", "O2", "O1"
};

struct edfparamblock{
         int smp_per_record;
         int smp_written;
         int dig_min;
         int dig_max;
         int offset;
         int buf_offset;
         double phys_min;
         double phys_max;
         double time_step;
         double sense;
       } *edfparam;

//mat A la ma tran chua du lieu cua 14 kenh khi doc tu file Edf ra.
//int readData(mat X)
int readData(char *pFilePath, double** XReturn, int StartSample, int StopSample)
{
  double** X;

  FILE *inputfile,
       *outputfile,
       *annotationfile;

  const char *fileName;

  int i, j, k, p, r, m, n,
      pathlen,
      fname_len,
      signals,
      datarecords,
      datarecordswritten,
      recordsize,
      recordfull,
      edf=0,
      bdf=0,
      edfplus=0,
      bdfplus=0,
      annot_ch[256],
      nr_annot_chns,
      skip,
      max,
      onset,
      duration,
      zero,
      max_tal_ln,
      samplesize;

  char path[512],
       ascii_path[512],
       *edf_hdr,
       *scratchpad,
       *cnv_buf,
       *time_in_txt,
       *duration_in_txt;

  double data_record_duration,
         elapsedtime,
         time_tmp,
         d_tmp,
         value_tmp=0.0;

  union {
          unsigned int one;
          signed int one_signed;
          unsigned short two[2];
          signed short two_signed[2];
          unsigned char four[4];
        } var;

  setlocale(LC_ALL, "C");

  X = mat_create(SAMPLE_MAX,CHANNEL_NUMBER_MAX);
  
  eegDataSampleCount = 0;
  //strcpy(path, "/storage/sdcard0/NickGun/EEG/ap-eyeblink-26.11.10.01.04.20.edf");
  //strcpy(ascii_path, "/storage/sdcard0/NickGun/EEG/ap-eyeblink-26.11.10.01.04.20.edf");
  
  strcpy(path, pFilePath);
  strcpy(ascii_path, pFilePath);

  pathlen = strlen(path);

  if(pathlen<5)
  {
    printf("Error, filename must contain at least five characters.\n");
    return(0);
  }

  scratchpad = (char *)malloc(128);
  if(scratchpad==NULL)
  {
    printf("Malloc error! (scratchpad).\n");
    return(0);
  }

  fname_len = 0;
  for(i=pathlen; i>0; i--)
  {
       if((path[i-1]=='/')||(path[i-1]=='\\'))  break;
       fname_len++;
  }
  fileName = path + pathlen - fname_len;

  for(i=0; fileName[i]!=0; i++);
  if(i==0)
  {
    printf("Error, filename must contain at least five characters.\n");
    return(0);
  }

  i -= 4;
  if((strcmp((const char *)fileName + i, ".edf")) &&
     (strcmp((const char *)fileName + i, ".EDF")) &&
     (strcmp((const char *)fileName + i, ".bdf")) &&
     (strcmp((const char *)fileName + i, ".BDF")))
  {
    printf("Error, filename extension must have the form \".edf\" or \".EDF\" or \".bdf\" or \".BDF\"\n");
    return(0);
  }

  if((!strcmp((const char *)fileName + i, ".edf")) ||
     (!strcmp((const char *)fileName + i, ".EDF")))
  {
    edf = 1;
    samplesize = 2;
  }
  else
  {
    bdf = 1;
    samplesize = 3;
  }

/***************** check header ******************************/

  inputfile = fopen(path, "rb");
  if(inputfile==NULL)
  {
    printf("Error, can not open file %s for reading\n", path);
    return(0);
  }

  if(fseek(inputfile, 0xfc, SEEK_SET))
  {
    printf("Error, reading file %s\n", path);
    fclose(inputfile);
    return(0);
  }

  if(fread(scratchpad, 4, 1, inputfile)!=1)
  {
    printf("Error, reading file %s\n", path);
    fclose(inputfile);
    return(0);
  }

  scratchpad[4] = 0;
  signals = atoi(scratchpad);
  if((signals<1)||(signals>256))
  {
    printf("Error, number of signals in header is %i\n", signals);
    fclose(inputfile);
    return(0);
  }

  edf_hdr = (char *)malloc((signals + 1) * 256);
  if(edf_hdr==NULL)
  {
    printf("Malloc error! (edf_hdr)\n");
    fclose(inputfile);
    return(0);
  }

  rewind(inputfile);
  if(fread(edf_hdr, (signals + 1) * 256, 1, inputfile)!=1)
  {
    printf("Error, reading file %s\n", path);
    fclose(inputfile);
    free(edf_hdr);
    return(0);
  }

  for(i=0; i<((signals+1)*256); i++)
  {
    if(edf_hdr[i]==',') edf_hdr[i] = '\'';  /* replace all comma's in header by single quotes because they */
  }                                         /* interfere with the comma-separated txt-files                */

  if(edf)
  {
    if(strncmp(edf_hdr, "0       ", 8))
    {
      printf("Error, EDF-header has unknown version\n");
      fclose(inputfile);
      free(edf_hdr);
      return(0);
    }
  }

  if(bdf)
  {
    if(strncmp(edf_hdr + 1, "BIOSEMI", 7) || (edf_hdr[0] != -1))
    {
      printf("Error, BDF-header has unknown version\n");
      fclose(inputfile);
      free(edf_hdr);
      return(0);
    }
  }

  strncpy(scratchpad, edf_hdr + 0xec, 8);
  scratchpad[8] = 0;
  datarecords = atoi(scratchpad);
  if(datarecords<1)
  {
    printf("Error, number of datarecords in header is %i\n", datarecords);
    fclose(inputfile);
    free(edf_hdr);
    return(0);
  }

  strncpy(scratchpad, edf_hdr + 0xf4, 8);
  scratchpad[8] = 0;
  data_record_duration = atof(scratchpad);

  nr_annot_chns = 0;

  if((strncmp(edf_hdr + 0xc0, "EDF+C     ", 10))&&(strncmp(edf_hdr + 0xc0, "EDF+D     ", 10)))
  {
    edfplus = 0;
  }
  else
  {
    edfplus = 1;
    for(i=0; i<signals; i++)
    {
      if(!(strncmp(edf_hdr + 256 + i * 16, "EDF Annotations ", 16)))
      {
        annot_ch[nr_annot_chns] = i;
        nr_annot_chns++;
        if(nr_annot_chns>255)  break;
      }
    }
    if(!nr_annot_chns)
    {
      printf("Error, file is marked as EDF+ but it has no annotationsignal.\n");
      fclose(inputfile);
      free(edf_hdr);
      return(0);
    }
  }

  if((strncmp(edf_hdr + 0xc0, "BDF+C     ", 10))&&(strncmp(edf_hdr + 0xc0, "BDF+D     ", 10)))
  {
    bdfplus = 0;
  }
  else
  {
    bdfplus = 1;
    for(i=0; i<signals; i++)
    {
      if(!(strncmp(edf_hdr + 256 + i * 16, "BDF Annotations ", 16)))
      {
        annot_ch[nr_annot_chns] = i;
        nr_annot_chns++;
        if(nr_annot_chns>255)  break;
      }
    }
    if(!nr_annot_chns)
    {
      printf("Error, file is marked as BDF+ but it has no annotationsignal.\n");
      fclose(inputfile);
      free(edf_hdr);
      return(0);
    }
  }

  edfparam = (struct edfparamblock *)malloc(signals * sizeof(struct edfparamblock));
  if(edfparam==NULL)
  {
    printf("Malloc error! (edfparam)\n");
    fclose(inputfile);
    free(edf_hdr);
    return(0);
  }

  recordsize = 0;

  for(i=0; i<signals; i++)
  {
    strncpy(scratchpad, edf_hdr + 256 + signals * 216 + i * 8, 8);
    scratchpad[8] = 0;
    edfparam[i].smp_per_record = atoi(scratchpad);
    edfparam[i].buf_offset = recordsize;
    recordsize += edfparam[i].smp_per_record;
    strncpy(scratchpad, edf_hdr + 256 + signals * 104 + i * 8, 8);
    scratchpad[8] = 0;
    edfparam[i].phys_min = atof(scratchpad);
    strncpy(scratchpad, edf_hdr + 256 + signals * 112 + i * 8, 8);
    scratchpad[8] = 0;
    edfparam[i].phys_max = atof(scratchpad);
    strncpy(scratchpad, edf_hdr + 256 + signals * 120 + i * 8, 8);
    scratchpad[8] = 0;
    edfparam[i].dig_min = atoi(scratchpad);
    strncpy(scratchpad, edf_hdr + 256 + signals * 128 + i * 8, 8);
    scratchpad[8] = 0;
    edfparam[i].dig_max = atoi(scratchpad);
    edfparam[i].time_step = data_record_duration / edfparam[i].smp_per_record;
    edfparam[i].sense = (edfparam[i].phys_max - edfparam[i].phys_min) / (edfparam[i].dig_max - edfparam[i].dig_min);
    edfparam[i].offset = edfparam[i].phys_max / edfparam[i].sense - edfparam[i].dig_max;
  }

  cnv_buf = (char *)malloc(recordsize * samplesize);
  if(cnv_buf==NULL)
  {
    printf("Malloc error! (cnv_buf)\n");
    fclose(inputfile);
    free(edf_hdr);
    free(edfparam);
    return(0);
  }

  free(scratchpad);

  max_tal_ln = 0;

  for(r=0; r<nr_annot_chns; r++)
  {
    if(max_tal_ln<edfparam[annot_ch[r]].smp_per_record * samplesize)  max_tal_ln = edfparam[annot_ch[r]].smp_per_record * samplesize;
  }

  if(max_tal_ln<128)  max_tal_ln = 128;

  scratchpad = (char *)malloc(max_tal_ln + 3);
  if(scratchpad==NULL)
  {
    printf("Malloc error! (scratchpad)\n");
    fclose(inputfile);
    free(cnv_buf);
    free(edf_hdr);
    free(edfparam);
    return(0);
  }

  duration_in_txt = (char *)malloc(max_tal_ln + 3);
  if(duration_in_txt==NULL)
  {
    printf("Malloc error! (duration_in_txt)\n");
    fclose(inputfile);
    free(scratchpad);
    free(cnv_buf);
    free(edf_hdr);
    free(edfparam);
    return(0);
  }

  time_in_txt = (char *)malloc(max_tal_ln + 3);
  if(time_in_txt==NULL)
  {
    printf("Malloc error! (time_in_txt)\n");
    fclose(inputfile);
    free(duration_in_txt);
    free(scratchpad);
    free(cnv_buf);
    free(edf_hdr);
    free(edfparam);
    return(0);
  }

/*
 * **************** write data ******************************
 */

  ascii_path[pathlen-4] = 0;
  strcat(ascii_path, "_data.txt");
  outputfile = fopen(ascii_path, "wb");
  if(outputfile==NULL)
  {
    printf("Error, can not open file %s for writing\n", ascii_path);
    fclose(inputfile);
    free(edf_hdr);
    free(edfparam);
    free(cnv_buf);
    free(time_in_txt);
    free(duration_in_txt);
    free(scratchpad);
    return(0);
  }

  fprintf(outputfile, "Time");

  for(i=0; i<(signals-nr_annot_chns); i++)
  {
    fprintf(outputfile, ",%i", i + 1);
  }

  
  if(fputc('\n', outputfile)==EOF)
  {
    printf("Error when writing to outputfile\n");
    fclose(inputfile);
    fclose(annotationfile);
    fclose(outputfile);
    free(edf_hdr);
    free(edfparam);
    free(cnv_buf);
    free(time_in_txt);
    free(duration_in_txt);
    free(scratchpad);
    return(0);
  }

  if(fseek(inputfile, (signals + 1) * 256, SEEK_SET))
  {
    printf("Error when reading inputfile\n");
    fclose(inputfile);
    fclose(annotationfile);
    fclose(outputfile);
    free(edf_hdr);
    free(edfparam);
    free(cnv_buf);
    free(time_in_txt);
    free(duration_in_txt);
    free(scratchpad);
    return(0);
  }

/***************** start data conversion ******************************/

  datarecordswritten = 0;

  for(i=0; i<datarecords; i++)
  {
    for(j=0; j<signals; j++)  edfparam[j].smp_written = 0;

    if(fread(cnv_buf, recordsize * samplesize, 1, inputfile)!=1)
    {
      printf("Error when reading inputfile during conversion\n");
      fclose(inputfile);
      fclose(annotationfile);
      fclose(outputfile);
      free(edf_hdr);
      free(edfparam);
      free(cnv_buf);
      free(time_in_txt);
      free(duration_in_txt);
      free(scratchpad);
      return(0);
    }
    if(edfplus || bdfplus)
    {
      max = edfparam[annot_ch[0]].smp_per_record * samplesize;
      p = edfparam[annot_ch[0]].buf_offset * samplesize;

/* extract time from datarecord */

      for(k=0; k<max; k++)
      {
        if(k>max_tal_ln)
        {
          printf("Error, TAL in record %i exceeds my buffer\n", datarecordswritten + 1);
          fclose(inputfile);
          fclose(annotationfile);
          fclose(outputfile);
          free(edf_hdr);
          free(edfparam);
          free(cnv_buf);
          free(time_in_txt);
          free(duration_in_txt);
          free(scratchpad);
          return(0);
        }
        scratchpad[k] = cnv_buf[p + k];
        if(scratchpad[k]==20) break;
      }
      scratchpad[k] = 0;
      elapsedtime = atof(scratchpad);
/* process annotations */

      for(r=0; r<nr_annot_chns; r++)
      {
        p = edfparam[annot_ch[r]].buf_offset * samplesize;
        max = edfparam[annot_ch[r]].smp_per_record * samplesize;
        n = 0;
        zero = 0;
        onset = 0;
        duration = 0;
        time_in_txt[0] = 0;
        duration_in_txt[0] = 0;
        scratchpad[0] = 0;

        for(k=0; k<max; k++)
        {
          if(k>max_tal_ln)
          {
            printf("Error, TAL in record %i exceeds my buffer\n", datarecordswritten + 1);
            fclose(inputfile);
            fclose(annotationfile);
            fclose(outputfile);
            free(edf_hdr);
            free(edfparam);
            free(cnv_buf);
            free(time_in_txt);
            free(duration_in_txt);
            free(scratchpad);
            return(0);
          }

          scratchpad[n] = cnv_buf[p + k];

          if(scratchpad[n]==0)
          {
            n = 0;
            onset = 0;
            duration = 0;
            time_in_txt[0] = 0;
            duration_in_txt[0] = 0;
            scratchpad[0] = 0;
            zero++;
            continue;
          }
          else  zero = 0;

          if(zero>1)  break;

          if(scratchpad[n]==20)
          {
            if(duration)
            {
              scratchpad[n] = 0;
              strcpy(duration_in_txt, scratchpad);
              n = 0;
              duration = 0;
              scratchpad[0] = 0;
              continue;
            }
            else if(onset)
                 {
                   scratchpad[n] = 0;
                   if(n)
                   {
                     utf8_to_latin1(scratchpad);
                     for(m=0; m<n; m++)
                     {
                       if(scratchpad[m] == 0)
                       {
                         break;
                       }

                       if((((unsigned char *)scratchpad)[m] < 32) || (((unsigned char *)scratchpad)[m] == ','))
                       {
                         scratchpad[m] = '.';
                       }
                     }
                     fprintf(annotationfile, "%s,%s,%s\n", time_in_txt, duration_in_txt, scratchpad);
                   }
                   n = 0;
                   duration = 0;
                   duration_in_txt[0] = 0;
                   scratchpad[0] = 0;
                   continue;
                 }
                 else
                 {
                   scratchpad[n] = 0;
                   strcpy(time_in_txt, scratchpad);
                   n = 0;
                   onset = 1;
                   duration = 0;
                   duration_in_txt[0] = 0;
                   scratchpad[0] = 0;
                   continue;
                 }
          }

          if(scratchpad[n]==21)
          {
            if(!onset)
            {
              scratchpad[n] = 0;
              strcpy(time_in_txt, scratchpad);
              onset = 1;
            }
            n = 0;
            duration = 1;
            duration_in_txt[0] = 0;
            scratchpad[0] = 0;
            continue;
          }

          if(++n>max_tal_ln)
          {
            printf("Error, TAL in record %i exceeds my buffer\n", datarecordswritten + 1);
            fclose(inputfile);
            fclose(annotationfile);
            fclose(outputfile);
            free(edf_hdr);
            free(edfparam);
            free(cnv_buf);
            free(time_in_txt);
            free(duration_in_txt);
            free(scratchpad);
            return(0);
          }
        }
      }
    }
    else elapsedtime = datarecordswritten * data_record_duration;
/* done with timekeeping and annotations, continue with the data */
  
    
    
    do
    {
      time_tmp = 10000000000.0;
      for(j=0; j<signals; j++)
      {
        if(edfplus || bdfplus)
        {
          skip = 0;

          for(p=0; p<nr_annot_chns; p++)
          {
            if(j==annot_ch[p])
            {
              skip = 1;
              break;
            }
          }

          if(skip) continue;
        }

        d_tmp = edfparam[j].smp_written * edfparam[j].time_step;
        if(d_tmp<time_tmp) time_tmp = d_tmp;
      }
 
      //fprintf(outputfile, "%f ", elapsedtime + time_tmp); //ghi thoi diem vao file
      
      //write 36 du lieu tai 1 thoi diem ung voi 36 kenh - signals = 36
      for(j=0; j<signals; j++)
      {
        if(edfplus || bdfplus)
        {
          skip = 0;

          for(p=0; p<nr_annot_chns; p++)
          {
            if(j==annot_ch[p])
            {
              skip = 1;
              break;
            }
          }

          if(skip) continue;
        }

        d_tmp = edfparam[j].smp_written * edfparam[j].time_step;

        if((d_tmp<(time_tmp+0.00000000000001))&&(d_tmp>(time_tmp-0.00000000000001))&&(edfparam[j].smp_written<edfparam[j].smp_per_record))
        {
          if(edf)
          {
            value_tmp = ((*(((signed short *)cnv_buf)+edfparam[j].buf_offset+edfparam[j].smp_written)) + edfparam[j].offset) * edfparam[j].sense;
          }

          if(bdf)
          {
            var.two[0] = *((unsigned short *)(cnv_buf + ((edfparam[j].buf_offset + edfparam[j].smp_written) * 3)));
            var.four[2] = *(cnv_buf + ((edfparam[j].buf_offset + edfparam[j].smp_written) * 3) + 2);

            if(var.four[2]&0x80)
            {
              var.four[3] = 0xff;
            }
            else
            {
              var.four[3] = 0x00;
            }

            value_tmp = (var.one_signed + edfparam[j].offset) * edfparam[j].sense;
          }
	 
	  
          fprintf(outputfile, "%f ", value_tmp);//ham nay write du lieu 36 kenh vao file data
	
	  
	  //luu du lieu vao X[][]
	  X[eegDataSampleCount][j] = value_tmp;
	    
	//printf("%f", value_tmp);
	    
          edfparam[j].smp_written++;
        }
        else fputc(',', outputfile);
      }

      if(fputc('\n', outputfile)==EOF)
      {
        printf("Error when writing to outputfile during conversion\n");
        fclose(inputfile);
        fclose(annotationfile);
        fclose(outputfile);
        free(edf_hdr);
        free(edfparam);
        free(cnv_buf);
        free(time_in_txt);
        free(duration_in_txt);
        free(scratchpad);
        return(0);
      }

      recordfull = 1;
      for(j=0; j<signals; j++)
      {
        if(edfparam[j].smp_written<edfparam[j].smp_per_record)
        {
          if(edfplus || bdfplus)
          {
            skip = 0;

            for(p=0; p<nr_annot_chns; p++)
            {
              if(j==annot_ch[p])
              {
                skip = 1;
                break;
              }
            }

            if(skip) continue;
          }

          recordfull = 0;
          break;
        }
      }
      
      eegDataSampleCount++;
    }
    while(!recordfull);

    datarecordswritten++;
  }
  fclose(outputfile);
  
  pPath = realpath(pFilePath, pFile);
  

  //doc 1 khoang du lieu trong tat ca cac kenh
  strcat(pPath, "_");
  sprintf(chr, "%d", StartSample);
  strcat(pPath, chr);
  strcat(pPath, "_");
  sprintf(chr, "%d", StopSample);
  strcat(pPath, chr);
  strcat(pPath, "_AllChan");
  outputfile = fopen(pPath, "wb");
 // for(j=0; j<signals; j++){
   // fprintf(outputfile, "***********************Channel %i:\n", j);
   // for(i=StartSample; i<StopSample; i++){
    //  XReturn[i-StartSample][j]= X[i][j];
    //  fprintf(outputfile, "%f\n", X[i][j]);
   // }
 // }

	

  for(i=StartSample; i<StopSample; i++){
    for(j=0; j<signals; j++){
      XReturn[i-StartSample][j]= X[i][j];
      fprintf(outputfile, "%f ", X[i][j]);
    }
    fprintf(outputfile, "\n", X[i][j]);
  }
  fclose(outputfile);

  
  mat_delete(X, SAMPLE_MAX, CHANNEL_NUMBER_MAX);
  
  fclose(inputfile);
  
  free(edf_hdr);
  free(edfparam);
  free(cnv_buf);
  free(time_in_txt);
  free(duration_in_txt);
  free(scratchpad);
  
  return 1;
}



void utf8_to_latin1(char *utf8_str)
{
  int i, j, len;

  unsigned char *str;


  str = (unsigned char *)utf8_str;

  len = strlen(utf8_str);

  if(!len)
  {
    return;
  }

  j = 0;

  for(i=0; i<len; i++)
  {
    if((str[i] < 32) || ((str[i] > 127) && (str[i] < 192)))
    {
      str[j++] = '.';

      continue;
    }

    if(str[i] > 223)
    {
      str[j++] = 0;

      return;  /* can only decode Latin-1 ! */
    }

    if((str[i] & 224) == 192)  /* found a two-byte sequence containing Latin-1, Greek, Cyrillic, Coptic, Armenian, Hebrew, etc. characters */
    {
      if((i + 1) == len)
      {
        str[j++] = 0;

        return;
      }

      if((str[i] & 252) != 192) /* it's not a Latin-1 character */
      {
        str[j++] = '.';

        i++;

        continue;
      }

      if((str[i + 1] & 192) != 128) /* UTF-8 violation error */
      {
        str[j++] = 0;

        return;
      }

      str[j] = str[i] << 6;
      str[j] += (str[i + 1] & 63);

      i++;
      j++;

      continue;
    }

    str[j++] = str[i];
  }

  if(j<len)
  {
    str[j] = 0;
  }
}
