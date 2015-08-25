using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Reflection;
using System.Globalization;
namespace LSServer
{
    class Features
    {
        public static int getLengthDownSample(int size)
        {
            return (int)(size / 2);
        }
        public static int getLengthDWT(int size)
        {
            return getLengthDownSample(size + 3);
        }
        public static double[] downSample(double[] signal)
        {
            int n = getLengthDownSample(signal.Length);
            double[] result = new double[n];
            int j = 1;
            for (int i = 0; i < n; i++)
            {
                result[i] = signal[j];
                j = j + 2;
            }
            return result;
        }
        public static double[] conv(double[] A, double[] B)
        {
            int size = A.Length + B.Length - 1;
            double tmp;
            double[] result = new double[size];

            for (int i = 0; i < size; i++)
            {
                int k = i;
                tmp = 0;
                for (int j = 0; j < B.Length; j++)
                {
                    if (k >= 0 && k < A.Length)
                        tmp += A[k] * B[j];
                    k = k - 1;
                    result[i] = tmp;
                }
            }
            return result;
        }
        public static double[][] dwt(double[] signal)
        {
            double[] h = { -0.12940952255092145, 0.22414386804185735, 0.836516303737469, 0.48296291314469025 };
            double[] g = { -h[3], h[2], -h[1], h[0] };

            double[][] result = new double[2][];
            result[0] = downSample(conv(h, signal));
            result[1] = downSample(conv(g, signal));
            return result;
        }
        public static double meanEnergy(double[] input, int N)
        {
            double sum = 0;
            for (int i = 0; i < input.Length; i++)
            {
                sum += Math.Pow(input[i], 2);
            }
            return (double)(sum / N);
        }
        public static double standardDeviation(double[] input)
        {
            double sum = 0;
            for (int i = 0; i < input.Length; i++)
            {
                sum += input[i];
            }
            double mean = sum / input.Length;
            sum = 0;
            for (int i = 0; i < input.Length; i++)
            {
                sum += Math.Pow(input[i] - mean, 2);
            }
            return Math.Sqrt(sum / input.Length);
        }
        public static double[] Extraction(double[] signal)
        {
            double[] result = new double[7];
            double[][] coeff1 = dwt(signal);
            double[][] coeff2 = dwt(coeff1[0]);
            double[][] coeff3 = dwt(coeff1[1]);
            double[][] coeff4 = dwt(coeff2[0]);
            double[][] coeff5 = dwt(coeff2[1]);
            double[][] coeff6 = dwt(coeff3[0]);
            double[][] coeff7 = dwt(coeff3[1]);
            double[][] coeff8 = dwt(coeff4[0]);
            double[][] coeff9 = dwt(coeff4[1]);
            double[][] coeff10 = dwt(coeff5[0]);
            double[][] coeff11 = dwt(coeff5[1]);
            double[][] coeff12 = dwt(coeff6[0]);
            double[][] coeff13 = dwt(coeff6[1]);
            double[][] coeff14 = dwt(coeff7[0]);
            double[][] coeff15 = dwt(coeff7[1]);
            double[][] coeff16 = dwt(coeff8[0]);
            double[][] coeff17 = dwt(coeff8[1]);
            double[][] coeff18 = dwt(coeff9[0]);
            double[][] coeff19 = dwt(coeff9[1]);

            result[0] = meanEnergy(coeff15[1], signal.Length) + meanEnergy(coeff16[0], signal.Length) + meanEnergy(coeff16[1], signal.Length) + meanEnergy(coeff19[1], signal.Length);
            result[1] = meanEnergy(coeff11[1], signal.Length) + meanEnergy(coeff12[0], signal.Length) + meanEnergy(coeff17[0], signal.Length) + meanEnergy(coeff17[1], signal.Length) + meanEnergy(coeff18[0], signal.Length);
            result[2] = meanEnergy(coeff13[0], signal.Length) + meanEnergy(coeff18[1], signal.Length);
            result[3] = meanEnergy(coeff13[1], signal.Length) + meanEnergy(coeff14[0], signal.Length) + meanEnergy(coeff14[1], signal.Length);
            result[4] = meanEnergy(coeff8[1], signal.Length) + meanEnergy(coeff9[0], signal.Length);
            result[5] = meanEnergy(coeff3[0], signal.Length) + meanEnergy(coeff9[1], signal.Length);
            result[6] = standardDeviation(signal);
            return result;
        }
        public static MyVector[] getFeatures(string file, int nInput, int nOutput)
        {
            string path = Program.DATA_DIRECTORY + file;
            string[] lines = File.ReadAllLines(@path);
            MyVector[] result = new MyVector[lines.Length];
            for (int i = 0; i < result.Length; i++)
            {
                result[i] = new MyVector(nInput, nOutput);
                string[] tmp = lines[i].Split('\t');
                for (int j = 0; j < nInput; j++)
                {
                    result[i].pattern[j] = Convert.ToDouble(tmp[j]);
                }
                for (int j = 0; j < nOutput; j++)
                {
                    result[i].target[j] = Convert.ToDouble(tmp[nInput + j]);
                }
            }
            return result;
        }
    }
}
