using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Globalization;
using System.IO;
namespace LSServer
{
    class DetectClient
    {
        TcpClient client;
        public static NetworkStream stream;
        public DetectClient(TcpClient c)
        {
            client = c;
            stream = client.GetStream();
        }
        public void send()
        {
            Net ne = new Net(7, 10, 2);
            Net.loadNet(ref ne);
            List<double> list = new List<double>();
            byte[] data;
            while (true)
            {
                data = new byte[1024];
                string s1 = Encoding.ASCII.GetString(data, 0, client.GetStream().Read(data, 0, data.Length));
                string[] s2 = s1.Split('x');
                string[] s3 = s2[0].Trim().Split('\n');
                for (int i = 0; i < s3.Length; i++)
                {
                    list.Add(Double.Parse(s3[i], CultureInfo.InvariantCulture));
                }
                if (list.Count == 2000)
                {
                    double[] signal = list.ToArray();
                    string stage = Net.detect(ref ne, signal);
                    if (stage.Equals("nothing"))
                    {
                        list.RemoveRange(0, 50);
                    }
                    else
                    {
                        data = Encoding.ASCII.GetBytes(stage);
                        stream.Write(data, 0, data.Length);
                        list.Clear();
                    }
                }
                
            }
        }
    }
}
