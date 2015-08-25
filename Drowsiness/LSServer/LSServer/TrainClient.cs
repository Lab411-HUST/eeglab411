using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
namespace LSServer
{
    class TrainClient
    {
        TcpClient client;
        public static NetworkStream stream;
        public TrainClient(TcpClient c)
        {
            client = c;
            stream = client.GetStream();
        }
        public void send()
        {
            MyVector[] vec = Features.getFeatures("train_features.txt", 7, 2);
            Net ne = new Net(7, 10, 2);
            Net.initializeWeights(ref ne);
            Net.setLearningParameters(ref ne, 0.01, 0.7);
            Net.trainNetworks(ref ne, vec, 10000);
            Net.saveNetworks(ne);
            byte[] data = Encoding.ASCII.GetBytes("Training complete!");
            stream.Write(data, 0, data.Length);
        }
    }
}
