using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace LSServer
{
    class Net
    {
        const double LEARNING_RATE = 0.01;
        const double MOMENTUM = 0.7;

        public int nInput, nHidden, nOutput;

        //neurons
        public double[] inputNeurons;
        public double[] hiddenNeurons;
        public double[] outputNeurons;

        //weights
        public double[][] wInputHidden;
        public double[][] wHiddenOutput;

        //change to weights
        public double[][] deltaInputHidden;
        public double[][] deltaHiddenOutput;

        //error gradients
        public double[] hiddenErrorGradients;
        public double[] outputErrorGradients;

        //learning parameters
        public double learningRate;
        public double momentum;

        public Net(int nI, int nH, int nO)
        {
            nInput = nI;
            nHidden = nH;
            nOutput = nO;

            inputNeurons = new double[nInput];
            for (int i = 0; i < nInput; i++)
            {
                inputNeurons[i] = 0;
            }
            hiddenNeurons = new double[nHidden];
            for (int i = 0; i < nHidden; i++)
            {
                hiddenNeurons[i] = 0;
            }
            outputNeurons = new double[nOutput];
            for (int i = 0; i < nOutput; i++)
            {
                outputNeurons[i] = 0;
            }
            wInputHidden = new double[nInput][];
            for (int i = 0; i < nInput; i++)
            {
                wInputHidden[i] = new double[nHidden];
                for (int j = 0; j < nHidden; j++)
                {
                    wInputHidden[i][j] = 0;
                }
            }
            wHiddenOutput = new double[nHidden][];
            for (int i = 0; i < nHidden; i++)
            {
                wHiddenOutput[i] = new double[nOutput];
                for (int j = 0; j < nOutput; j++)
                {
                    wHiddenOutput[i][j] = 0;
                }
            }

            deltaInputHidden = new double[nInput][];
            for (int i = 0; i < nInput; i++)
            {
                deltaInputHidden[i] = new double[nHidden];
                for (int j = 0; j < nHidden; j++)
                {
                    deltaInputHidden[i][j] = 0;
                }
            }
            deltaHiddenOutput = new double[nHidden][];
            for (int i = 0; i < nHidden; i++)
            {
                deltaHiddenOutput[i] = new double[nOutput];
                for (int j = 0; j < nOutput; j++)
                {
                    deltaHiddenOutput[i][j] = 0;
                }
            }

            hiddenErrorGradients = new double[nHidden];
            for (int i = 0; i < nHidden; i++)
            {
                hiddenErrorGradients[i] = 0;
            }
            outputErrorGradients = new double[nOutput];
            for (int i = 0; i < nOutput; i++)
            {
                outputErrorGradients[i] = 0;
            }
            learningRate = LEARNING_RATE;
            momentum = MOMENTUM;
        }
        public static void initializeWeights(ref Net ne)
        {
            Random random = new Random();
            for (int i = 0; i < ne.nInput; i++)
            {
                for (int j = 0; j < ne.nHidden; j++)
                {
                    ne.wInputHidden[i][j] = random.NextDouble()*0.4 - 0.2;
                }
            }
            for (int i = 0; i < ne.nHidden; i++)
            {
                for (int j = 0; j < ne.nOutput; j++)
                {
                    ne.wHiddenOutput[i][j] = random.NextDouble() * 0.4 - 0.2;
                }
            }
        }
        public static void resetWeights(ref Net ne)
        {
            initializeWeights(ref ne);
        }
        public static void setLearningParameters(ref Net ne, double lr, double m)
        {
            ne.learningRate = lr;
            ne.momentum = m;
        }
        public static double activationFunction(double x)
        {
            return (double)(1.0 / (1 + Math.Exp(-x)));
        }
        public static void feedForward(ref Net ne, double[] inputs)
        {
            for (int i = 0; i < ne.nInput; i++)
            {
                ne.inputNeurons[i] = inputs[i];
            }
            for (int i = 0; i < ne.nHidden; i++)
            {
                ne.hiddenNeurons[i] = 0;
                for (int j = 0; j < ne.nInput; j++)
                {
                    ne.hiddenNeurons[i] += ne.inputNeurons[j] * ne.wInputHidden[j][i];
                }
                ne.hiddenNeurons[i] = activationFunction(ne.hiddenNeurons[i]);
            }
            for (int i = 0; i < ne.nOutput; i++)
            {
                ne.outputNeurons[i] = 0;
                for (int j = 0; j < ne.nHidden; j++)
                {
                    ne.outputNeurons[i] += ne.hiddenNeurons[j] * ne.wHiddenOutput[j][i];
                }
                ne.outputNeurons[i] = activationFunction(ne.outputNeurons[i]);
            }
        }
        public static void backpropagate(ref Net ne, double[] desiredValues)
        {
            double sum;
            for (int i = 0; i < ne.nOutput; i++)
            {
                ne.outputErrorGradients[i] = ne.outputNeurons[i] * (1 - ne.outputNeurons[i]) * (desiredValues[i] - ne.outputNeurons[i]);
                for (int j = 0; j < ne.nHidden; j++)
                {
                    ne.deltaHiddenOutput[j][i] = ne.learningRate * ne.hiddenNeurons[j] * ne.outputErrorGradients[i] + ne.momentum * ne.deltaHiddenOutput[j][i];
                }
            }
            for (int i = 0; i < ne.nHidden; i++)
            {
                sum = 0;
                for (int j = 0; j < ne.nOutput; j++)
                {
                    sum += ne.outputErrorGradients[j] * ne.wHiddenOutput[i][j];
                }
                ne.hiddenErrorGradients[i] = ne.hiddenNeurons[i] * (1 - ne.hiddenNeurons[i]) * sum;
                for (int j = 0; j < ne.nInput; j++)
                {
                    ne.deltaInputHidden[j][i] = ne.learningRate * ne.inputNeurons[j] * ne.hiddenErrorGradients[i] + ne.momentum * ne.deltaInputHidden[j][i];
                }
            }
        }
        public static void updateWeights(ref Net ne)
        {
            for (int i = 0; i < ne.nInput; i++)
            {
                for (int j = 0; j < ne.nHidden; j++)
                {
                    ne.wInputHidden[i][j] += ne.deltaInputHidden[i][j];
                }
            }
            for (int i = 0; i < ne.nHidden; i++)
            {
                for (int j = 0; j < ne.nOutput; j++)
                {
                    ne.wHiddenOutput[i][j] += ne.deltaHiddenOutput[i][j];
                }
            }
        }
        public static void disorder(ref int[] input, Random random)
        {
            int a1, a2;
            int tmp;
            for (int i = 0; i < 2 * input.Length; i++)
            {
                a1 = random.Next(0, input.Length);
                do
                {
                    a2 = random.Next(0, input.Length);
                } while (a1 == a2);
                tmp = input[a1];
                input[a1] = input[a2];
                input[a2] = tmp;
            }
        }
        public static void trainNetworks(ref Net ne, MyVector[] vec, int loop)
        {
            int[] irea = new int[vec.Length];
            for (int i = 0; i < vec.Length; i++)
            {
                irea[i] = i;
            }
            Random random = new Random();

            for (int i = 0; i < loop; i++)
            {
                disorder(ref irea, random);
                for (int j = 0; j < vec.Length; j++)
                {
                    feedForward(ref ne, vec[irea[j]].pattern);
                    backpropagate(ref ne, vec[irea[j]].target);
                    updateWeights(ref ne);
                }
            }
        }
        public static void getRoundedOutputValue(ref Net ne)
        {
            for (int i = 0; i < ne.nOutput; i++)
            {
                if (ne.outputNeurons[i] < 0.1)
                    ne.outputNeurons[i] = 0.0;
                else if (ne.outputNeurons[i] > 0.9)
                    ne.outputNeurons[i] = 1.0;
                else
                    ne.outputNeurons[i] = -1.0;
            }
        }
        public static bool compare(double[] a, double[] b)
        {
            if (a.Length != b.Length)
                return false;
            else
            {
                for (int i = 0; i < a.Length; i++)
                {
                    if (a[i] != b[i])
                        return false;
                }
                return true;
            }
        }
        public static double accuracy(ref Net ne, MyVector[] vec)
        {
            double incorrectResults = 0;
            for (int i = 0; i < vec.Length; i++)
            {
                feedForward(ref ne, vec[i].pattern);
                getRoundedOutputValue(ref ne);
                if (compare(ne.outputNeurons, vec[i].target))
                    incorrectResults++;
            }
            return (double)incorrectResults / vec.Length;
        }
        public static string detect(ref Net ne, double[] input)
        {
            double[] w = new double[2];
            w[0] = 0;
            w[1] = 0;
            double[] s1 = new double[2];
            s1[0] = 0;
            s1[1] = 1;
            double[] s2 = new double[2];
            s2[0] = 1;
            s2[1] = 0;
            double[] s3 = new double[2];
            s3[0] = 1;
            s3[1] = 1;
            double[] extract = Features.Extraction(input);
            feedForward(ref ne, extract);
            getRoundedOutputValue(ref ne);
            if (compare(ne.outputNeurons, w))
                return "wake";
            else if (compare(ne.outputNeurons, s1))
                return "stage1";
            else if (compare(ne.outputNeurons, s2))
                return "stage2";
            else if (compare(ne.outputNeurons, s3))
                return "stage3";
            else
                return "nothing";
        }
        public static void saveNetworks(Net ne)
        {
            string path = Program.DATA_DIRECTORY + "networks.txt";
            StringBuilder str = new StringBuilder();
            str.Append("nInput\tnHidden\tnOutput\n");
            str.Append(ne.nInput.ToString() + "\t" + ne.nHidden.ToString() + "\t" + ne.nOutput.ToString() + "\n");
            str.Append("Weights Input Hidden\n");
            for (int i = 0; i < ne.nInput; i++)
            {
                for (int j = 0; j < ne.nHidden; j++)
                {
                    str.Append(ne.wInputHidden[i][j].ToString() + "\t");
                }
                str.Append("\n");
            }
            str.Append("Weights Hidden Output\n");
            for (int i = 0; i < ne.nHidden; i++)
            {
                for (int j = 0; j < ne.nOutput; j++)
                {
                    str.Append(ne.wHiddenOutput[i][j].ToString() + "\t");
                }
                str.Append("\n");
            }
            str.Append("LearningRate\tMomentum\n");
            str.Append(ne.learningRate.ToString() + "\t" + ne.momentum.ToString());

            File.WriteAllText(@path, str.ToString());
            str.Clear();
        }
        public static void loadNet(ref Net ne)
        {
            string path = Program.DATA_DIRECTORY + "networks.txt";
            string[] lines = File.ReadAllLines(@path);
            string[] tmp = lines[1].Split('\t');
            ne.nInput = Convert.ToInt32(tmp[0]);
            ne.nHidden = Convert.ToInt32(tmp[1]);
            ne.nOutput = Convert.ToInt32(tmp[2]);
            for (int i = 0; i < ne.nInput; i++)
            {
                string[] tmp1 = lines[3 + i].Split('\t');
                for (int j = 0; j < ne.nHidden; j++)
                {
                    ne.wInputHidden[i][j] = Convert.ToDouble(tmp1[j]);
                }
            }
            for (int i = 0; i < ne.nHidden; i++)
            {
                string[] tmp1 = lines[4 + ne.nInput + i].Split('\t');
                for (int j = 0; j < ne.nOutput; j++)
                {
                    ne.wHiddenOutput[i][j] = Convert.ToDouble(tmp1[j]);
                }
            }
            string[] tmp2 = lines[5 + ne.nInput + ne.nHidden].Split('\t');
            ne.learningRate = Convert.ToDouble(tmp2[0]);
            ne.momentum = Convert.ToDouble(tmp2[1]);
        }
    }
}
