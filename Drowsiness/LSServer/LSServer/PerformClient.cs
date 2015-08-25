using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Net.Sockets;

namespace LSServer
{
    class PerformClient
    {
        TcpClient client;
        NetworkStream stream;
        public PerformClient(TcpClient c)
        {
            client = c;
            stream = c.GetStream();
        }
        public void send()
        {
            byte[] data;
            data = new byte[100];
            MainForm.run = true;
            Performance pp = new Performance();
            Int64 total_mem = pp.GetTotalMemory();
            data = Encoding.ASCII.GetBytes(total_mem.ToString() + "xx");
            stream.Write(data, 0, data.Length);
            StringBuilder toClient = new StringBuilder();
            while (MainForm.run)
            {
                Thread.Sleep(1000);
                int[] cpu_processors = pp.getCPU();
                int cpu_total = pp.getTotalCPU();
                int ram_process = pp.getMemoryProcess();
                Int64 availableMemory = pp.GetPhysicalAvailableMemory();
                toClient.Append(availableMemory.ToString() + "x");
                toClient.Append(ram_process.ToString() + "x");
                toClient.Append(cpu_total.ToString() + "x");
                for (int i = 0; i < cpu_processors.Length; i++)
                {
                    toClient.Append(cpu_processors[i].ToString() + "z");
                }
                toClient.Remove(toClient.Length - 1, 1);
                toClient.Append("x");
                data = Encoding.ASCII.GetBytes(toClient.ToString());
                stream.Write(data, 0, data.Length);
                toClient.Clear();
            }
        }
    }
}
