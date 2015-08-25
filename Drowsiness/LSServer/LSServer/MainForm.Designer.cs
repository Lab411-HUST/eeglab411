namespace LSServer
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.tb_device = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.btn_stop = new System.Windows.Forms.Button();
            this.btn_start = new System.Windows.Forms.Button();
            this.tb_port = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.tb_ip = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.tb_hostname = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.rtb_hd = new System.Windows.Forms.RichTextBox();
            this.SuspendLayout();
            // 
            // tb_device
            // 
            this.tb_device.BackColor = System.Drawing.Color.White;
            this.tb_device.Enabled = false;
            this.tb_device.Location = new System.Drawing.Point(124, 132);
            this.tb_device.Name = "tb_device";
            this.tb_device.Size = new System.Drawing.Size(158, 26);
            this.tb_device.TabIndex = 39;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(12, 139);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(70, 20);
            this.label4.TabIndex = 38;
            this.label4.Text = "DEVICE";
            // 
            // btn_stop
            // 
            this.btn_stop.BackColor = System.Drawing.Color.DarkGray;
            this.btn_stop.Location = new System.Drawing.Point(177, 197);
            this.btn_stop.Name = "btn_stop";
            this.btn_stop.Size = new System.Drawing.Size(105, 45);
            this.btn_stop.TabIndex = 37;
            this.btn_stop.Text = "STOP";
            this.btn_stop.UseVisualStyleBackColor = false;
            this.btn_stop.Click += new System.EventHandler(this.btn_stop_Click);
            // 
            // btn_start
            // 
            this.btn_start.BackColor = System.Drawing.Color.SeaGreen;
            this.btn_start.Location = new System.Drawing.Point(16, 197);
            this.btn_start.Name = "btn_start";
            this.btn_start.Size = new System.Drawing.Size(105, 45);
            this.btn_start.TabIndex = 36;
            this.btn_start.Text = "START";
            this.btn_start.UseVisualStyleBackColor = false;
            this.btn_start.Click += new System.EventHandler(this.btn_start_Click);
            // 
            // tb_port
            // 
            this.tb_port.BackColor = System.Drawing.Color.White;
            this.tb_port.Enabled = false;
            this.tb_port.Location = new System.Drawing.Point(124, 91);
            this.tb_port.Name = "tb_port";
            this.tb_port.Size = new System.Drawing.Size(158, 26);
            this.tb_port.TabIndex = 35;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(12, 98);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(102, 20);
            this.label3.TabIndex = 34;
            this.label3.Text = "Server PORT";
            // 
            // tb_ip
            // 
            this.tb_ip.BackColor = System.Drawing.Color.White;
            this.tb_ip.Enabled = false;
            this.tb_ip.Location = new System.Drawing.Point(124, 52);
            this.tb_ip.Name = "tb_ip";
            this.tb_ip.Size = new System.Drawing.Size(158, 26);
            this.tb_ip.TabIndex = 33;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 59);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(74, 20);
            this.label2.TabIndex = 32;
            this.label2.Text = "Server IP";
            // 
            // tb_hostname
            // 
            this.tb_hostname.BackColor = System.Drawing.Color.White;
            this.tb_hostname.Enabled = false;
            this.tb_hostname.Location = new System.Drawing.Point(124, 14);
            this.tb_hostname.Name = "tb_hostname";
            this.tb_hostname.Size = new System.Drawing.Size(158, 26);
            this.tb_hostname.TabIndex = 31;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 21);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(99, 20);
            this.label1.TabIndex = 30;
            this.label1.Text = "HOSTNAME";
            // 
            // rtb_hd
            // 
            this.rtb_hd.Enabled = false;
            this.rtb_hd.Location = new System.Drawing.Point(313, 14);
            this.rtb_hd.Name = "rtb_hd";
            this.rtb_hd.Size = new System.Drawing.Size(262, 228);
            this.rtb_hd.TabIndex = 40;
            this.rtb_hd.Text = "";
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(590, 257);
            this.Controls.Add(this.rtb_hd);
            this.Controls.Add(this.tb_device);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.btn_stop);
            this.Controls.Add(this.btn_start);
            this.Controls.Add(this.tb_port);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.tb_ip);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.tb_hostname);
            this.Controls.Add(this.label1);
            this.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Name = "MainForm";
            this.Text = "MainForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tb_device;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Button btn_stop;
        private System.Windows.Forms.Button btn_start;
        private System.Windows.Forms.TextBox tb_port;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox tb_ip;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox tb_hostname;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.RichTextBox rtb_hd;
    }
}