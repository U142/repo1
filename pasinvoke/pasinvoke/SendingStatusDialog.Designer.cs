namespace pasinvoke
{
    partial class SendingStatusDialog
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SendingStatusDialog));
            this.dgv_status = new System.Windows.Forms.DataGridView();
            this.lbl_project = new System.Windows.Forms.Label();
            this.lbl_eventpk = new System.Windows.Forms.Label();
            this.lbl_projectpk = new System.Windows.Forms.Label();
            this.lbl_eventinfo = new System.Windows.Forms.Label();
            this.backgroundWorker1 = new System.ComponentModel.BackgroundWorker();
            this.progressBar1 = new System.Windows.Forms.ProgressBar();
            this.lbl_info = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.dgv_status)).BeginInit();
            this.SuspendLayout();
            // 
            // dgv_status
            // 
            this.dgv_status.AllowUserToAddRows = false;
            this.dgv_status.AllowUserToDeleteRows = false;
            this.dgv_status.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgv_status.Location = new System.Drawing.Point(12, 64);
            this.dgv_status.Name = "dgv_status";
            this.dgv_status.ReadOnly = true;
            this.dgv_status.Size = new System.Drawing.Size(818, 192);
            this.dgv_status.TabIndex = 0;
            // 
            // lbl_project
            // 
            this.lbl_project.AutoSize = true;
            this.lbl_project.Location = new System.Drawing.Point(9, 9);
            this.lbl_project.Name = "lbl_project";
            this.lbl_project.Size = new System.Drawing.Size(43, 13);
            this.lbl_project.TabIndex = 1;
            this.lbl_project.Text = "Project:";
            // 
            // lbl_eventpk
            // 
            this.lbl_eventpk.AutoSize = true;
            this.lbl_eventpk.Location = new System.Drawing.Point(9, 32);
            this.lbl_eventpk.Name = "lbl_eventpk";
            this.lbl_eventpk.Size = new System.Drawing.Size(41, 13);
            this.lbl_eventpk.TabIndex = 2;
            this.lbl_eventpk.Text = "Event: ";
            // 
            // lbl_projectpk
            // 
            this.lbl_projectpk.AutoSize = true;
            this.lbl_projectpk.Location = new System.Drawing.Point(76, 9);
            this.lbl_projectpk.Name = "lbl_projectpk";
            this.lbl_projectpk.Size = new System.Drawing.Size(0, 13);
            this.lbl_projectpk.TabIndex = 3;
            // 
            // lbl_eventinfo
            // 
            this.lbl_eventinfo.AutoSize = true;
            this.lbl_eventinfo.Location = new System.Drawing.Point(76, 32);
            this.lbl_eventinfo.Name = "lbl_eventinfo";
            this.lbl_eventinfo.Size = new System.Drawing.Size(0, 13);
            this.lbl_eventinfo.TabIndex = 4;
            // 
            // backgroundWorker1
            // 
            this.backgroundWorker1.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorker1_DoWork);
            this.backgroundWorker1.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorker1_RunWorkerCompleted);
            this.backgroundWorker1.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.backgroundWorker1_ProgressChanged);
            // 
            // progressBar1
            // 
            this.progressBar1.Location = new System.Drawing.Point(12, 263);
            this.progressBar1.Name = "progressBar1";
            this.progressBar1.Size = new System.Drawing.Size(818, 23);
            this.progressBar1.TabIndex = 5;
            // 
            // lbl_info
            // 
            this.lbl_info.AutoSize = true;
            this.lbl_info.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lbl_info.Location = new System.Drawing.Point(488, 9);
            this.lbl_info.Name = "lbl_info";
            this.lbl_info.Size = new System.Drawing.Size(0, 20);
            this.lbl_info.TabIndex = 6;
            // 
            // SendingStatusDialog
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(842, 298);
            this.Controls.Add(this.lbl_info);
            this.Controls.Add(this.progressBar1);
            this.Controls.Add(this.lbl_eventinfo);
            this.Controls.Add(this.lbl_projectpk);
            this.Controls.Add(this.lbl_eventpk);
            this.Controls.Add(this.lbl_project);
            this.Controls.Add(this.dgv_status);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "SendingStatusDialog";
            this.Text = "UMS PAS status";
            this.Load += new System.EventHandler(this.SendingStatusDialog_Load);
            this.Shown += new System.EventHandler(this.SendingStatusDialog_Shown);
            ((System.ComponentModel.ISupportInitialize)(this.dgv_status)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView dgv_status;
        private System.Windows.Forms.Label lbl_project;
        private System.Windows.Forms.Label lbl_eventpk;
        private System.Windows.Forms.Label lbl_projectpk;
        private System.Windows.Forms.Label lbl_eventinfo;
        private System.ComponentModel.BackgroundWorker backgroundWorker1;
        private System.Windows.Forms.ProgressBar progressBar1;
        private System.Windows.Forms.Label lbl_info;


    }
}