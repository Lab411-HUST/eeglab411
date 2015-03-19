#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "svm.h"
#include "svm-predict.h"
#define LOG_TAG "PREDICT"
int print_null(const char *s,...) {return 0;}

static int (*info)(const char *fmt,...) = &printf;
struct svm_node *x;
int max_nr_attr = 64;

struct svm_model* model;
int predict_probability=0;

static char *line = NULL;
static int max_line_len;

static char* readline(FILE *input)
{
	int len;
	if(fgets(line,max_line_len,input) == NULL)
		return NULL;

	while(strrchr(line,'\n') == NULL)
	{
		max_line_len *= 2;
		line = (char *) realloc(line,max_line_len);
		len = (int) strlen(line);
		if(fgets(line+len,max_line_len-len,input) == NULL)
			break;
	}
	return line;
}

void exit_input_error_predict(int line_num)
{
	exit(1);
}

int predict(FILE *input , FILE *output)
{
	int correct =0;
	int total = 0;
	int error = 0;
	double sump = 0, sumt = 0, sumpt = 0, sumpp = 0, sumtt = 0;
	int svm_type=svm_get_svm_type(model);
	int nr_class=svm_get_nr_class(model);
	double *prob_estimate = NULL;
	int j;
	double target_label, predict_label,idVal;

	if(predict_probability)
	{
		if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
		{}
		else
		{

			int *labels=(int *) malloc(nr_class*sizeof(int));
			svm_get_labels(model,labels);

			fprintf(output,"labels");
			for(j=0;j<nr_class;j++)
				fprintf(output," %d",labels[j]);
			fprintf(output,"\n");
			free(labels);
		}
	}
	max_line_len = 1024;
	line = (char *)malloc(max_line_len*sizeof(char));

	while(readline(input) != NULL)
	{

		int i =0;
		char *idx, *val, *label, *endptr;
		int inst_max_index = -1;
		label = strtok(line, " \t\n");
		if(label == NULL)
		{
			exit_input_error_predict(total+1);
		}
		target_label = strtod(label, &endptr);
		//if(*endptr =='\0')
		if(endptr == label|| *endptr !='\0')
		{
			//LOGD("endptr = %s", endptr);
			//LOGD("endptr == label|| *endpt!=\0");
			//predict_label = svm_predict(model,x);
			//fprintf(output,"%g\n",predict_label);
			exit_input_error_predict(total +1);
		}
		while(1)
		{
			if(i >= max_nr_attr-1)
			{
				max_nr_attr *=2;
				x =(struct svm_node *) realloc(x, max_nr_attr*sizeof(struct svm_node));

			}
			//LOGD("line = %s ",line);
			//idVal = strtod(line,&endptr);
			//LOGD("endptr = %s", endptr);
			idx = strtok(NULL,":");
			val = strtok(NULL," \t");
			if(val == NULL)
			{
				break;
			}
			errno = 0;
			x[i].index = (int) strtol(idx,&endptr,10);
			//if(endptr ==idx ||errno !=0 || *endptr =='\0'||x[i].index <=inst_max_index)
			if(endptr ==idx ||errno !=0 || *endptr !='\0'||x[i].index <=inst_max_index)
			{
				exit_input_error_predict(total +1);
			}

			else
			{
				inst_max_index = x[i].index;
			}
			errno = 0;
			x[i].value = strtod(val, &endptr);
			//if(endptr == val || errno !=0||(*endptr =='\0'&& !isspace(*endptr)))
			if(endptr == val || errno !=0||(*endptr !='\0'&& !isspace(*endptr)))
			{
				exit_input_error_predict(total +1);
			}
			++i;
		}
		x[i].index = -1;
		if(predict_probability &&(svm_type == C_SVC || svm_type == NU_SVC))
		{
			predict_label = svm_predict_probability(model, x, prob_estimate);
			fprintf(output,"%g", predict_label);
			for (j = 0; j < nr_class; j++)
			{
				fprintf(output,"%g",prob_estimate[j]);
			}
			fprintf(output,"\n");
		}
		else
		{
			predict_label = svm_predict(model,x);
			fprintf(output,"%g\n",predict_label);
		}
		if(predict_label == target_label)
		{
			++correct;
		}
		error +=(predict_label - target_label)*(predict_label - target_label);
		sump += predict_label;
		sumt +=target_label;
		sumpp += predict_label*predict_label;
		sumtt += target_label*target_label;
		sumpt += predict_label * target_label;
		++total;
	}
	if(svm_type == NU_SVR|| svm_type == EPSILON_SVR)
	{
	}
	else
	{
		if(predict_probability)
			free(prob_estimate);
	}
       /* for (int i = 0; i < rowNum; i++)
        {
            double target_label, predict_label=0;
            x = (struct svm_node *) realloc(x,(colNum+1)*sizeof(struct svm_node));

            for (int j = 0; j < colNum; j++)
            {
                x[j].index = indices[i][j];
                x[j].value = values[i][j];
            }
            x[colNum].index = -1;

            // Probability prediction 
            if (isProb && (svm_type==C_SVC || svm_type==NU_SVC))
            {
                    predict_label = svm_predict_probability(model,x,prob_estimates);
					labels[0]=predict_label;

            }
            else { labels[i] = svm_predict(model,x); }
	} // For

        return 0;
}*/
	return predict_label;

}
void exit_with_help_for_predict()
{
	exit(1);
}

int svmpredict(int argc, char **argv)
{
	for(int j = 0 ;j< argc;j++)
	{
		/*//int m = sizeof(argv);
		LOGD("m = %d", m);
		for (int k = 0; k<m;k++)
			LOGD("argv:%c ",argv[j][k]);
*/
	}

	FILE *input, *output;
		int i;
		// parse options
		for(i=1;i<argc;i++)
		{
			//LOGD("argv: %c",argv[i][0]);
			//LOGD("argv: %c",argv[i-1][1]);
			if(argv[i][0] != '-') break;
			++i;
			switch(argv[i-1][1])
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					//LOGD("predict_probability : %d",predict_probability);
					break;
				case 'q':
					info = &print_null;
					i--;
					break;
				default:
					fprintf(stderr,"Unknown option: -%c\n", argv[i-1][1]);
					exit_with_help_for_predict();
			}
		}
		if(i>=argc-2)
		{
		exit_with_help_for_predict();
		}
		input = fopen(argv[i],"r");
		if(input == NULL)
		{

			fprintf(stderr,"can't open input file %s\n",argv[i]);
			exit(1);
		}

		output = fopen(argv[i+2],"w");
		if(output == NULL)
		{
			fprintf(stderr,"can't open output file %s\n",argv[i+2]);
			exit(1);
		}

		if((model=svm_load_model(argv[i+1]))==0)
		{
			fprintf(stderr,"can't open model file %s\n",argv[i+1]);
			exit(1);
		}

		x = (struct svm_node *) malloc(max_nr_attr*sizeof(struct svm_node));
		if(predict_probability)
		{
			if(svm_check_probability_model(model)==0)
			{
				fprintf(stderr,"Model does not support probabiliy estimates\n");
				exit(1);
			}
		}
		else
		{
			if(svm_check_probability_model(model)!=0)
				info("Model supports probability estimates, but disabled in prediction.\n");
		}
		//LOGD("predict:(%s, %s)",input,output);
		int result = predict(input,output);
		svm_free_and_destroy_model(&model);
		free(x);
		free(line);
		fclose(input);
		fclose(output);
		return result;
}
