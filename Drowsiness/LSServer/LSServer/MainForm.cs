using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Globalization;
namespace LSServer
{
    public partial class MainForm : Form
    {
        public string HOSTNAME, DEVICENAME;
        TcpListener server;
        TcpClient client;
        public static Boolean run;

        public MainForm()
        {
            InitializeComponent();
        }

        private void btn_start_Click(object sender, EventArgs e)
        {
            IPHostEntry ipHostInfo = Dns.Resolve(Dns.GetHostName());
            IPAddress IP = ipHostInfo.AddressList[0];
            int PORT = 9999;
            HOSTNAME = Dns.GetHostName();

            IPEndPoint iep = new IPEndPoint(IP, PORT);
            server = new TcpListener(iep);
            server.Start();
            rtb_hd.AppendText("Waiting for connection ... \n");

            tb_hostname.Text = HOSTNAME;
            tb_ip.Text = IP.ToString();
            tb_port.Text = PORT.ToString();
            btn_start.Enabled = false;

            DistributedThread d = new DistributedThread(MainServer);
            d.ProcessorAffinity = 1;
            d.Start();
        }

        private void btn_stop_Click(object sender, EventArgs e)
        {
            run = false;
            server.Stop();
            this.Close();
            Application.Exit();
        }

        private void MainServer()
        {
            run = true;
            byte[] data;
            while(run)
            {
                if (server.Pending())
                {
                    client = server.AcceptTcpClient();
                    data = new byte[100];
                    string receive = Encoding.ASCII.GetString(data, 0, client.GetStream().Read(data, 0, data.Length));
                    if (receive.Equals("device"))
                    {
                        data = new byte[100];
                        DEVICENAME = Encoding.ASCII.GetString(data, 0, client.GetStream().Read(data, 0, data.Length));
                        tb_device.Invoke((Action)delegate
                        {
                            tb_device.Text = DEVICENAME;
                        });
                        rtb_hd.Invoke((Action)delegate
                        {
                            rtb_hd.AppendText(DEVICENAME + " connected\n");
                        });
                        data = Encoding.ASCII.GetBytes(HOSTNAME);
                        client.GetStream().Write(data, 0, data.Length);
                    }
                    if (receive.Equals("perform"))
                    {
                        PerformClient p = new PerformClient(client);
                        DistributedThread d = new DistributedThread(p.send);
                        d.ProcessorAffinity = 8;
                        d.Start();
                    }
                    if (receive.Equals("train"))
                    {
                        rtb_hd.Invoke((Action)delegate
                        {
                            rtb_hd.AppendText("Training networks...\n");
                        });
                        TrainClient t = new TrainClient(client);
                        DistributedThread d = new DistributedThread(t.send);
                        d.ProcessorAffinity = 2;
                        d.Start();
                    }
                    if (receive.Equals("detect"))
                    {
                        rtb_hd.Invoke((Action)delegate
                        {
                            rtb_hd.AppendText("Detect signal...\n");
                        });
                        DetectClient dt = new DetectClient(client);
                        DistributedThread d = new DistributedThread(dt.send);
                        d.ProcessorAffinity = 2;
                        d.Start();
                    }
                }            
            }
            client.Close();
        }
    }
}
