using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LSServer
{
    class MyVector
    {
        public double[] pattern;
        public double[] target;
        public MyVector()
        {
        }
        public MyVector(int nI, int nO)
        {
            pattern = new double[nI];
            target = new double[nO];
        }
    }
}
