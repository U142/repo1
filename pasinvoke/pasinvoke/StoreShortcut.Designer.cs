namespace pasinvoke
{
    partial class StoreShortcut
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(StoreShortcut));
            this.cbx_events = new System.Windows.Forms.ComboBox();
            this.lbl_selectevent = new System.Windows.Forms.Label();
            this.btn_saveshortcut = new System.Windows.Forms.Button();
            this.lbl_user = new System.Windows.Forms.Label();
            this.lbl_company = new System.Windows.Forms.Label();
            this.lbl_dept = new System.Windows.Forms.Label();
            this.lbl_password = new System.Windows.Forms.Label();
            this.txt_user = new System.Windows.Forms.TextBox();
            this.txt_department = new System.Windows.Forms.TextBox();
            this.txt_password = new System.Windows.Forms.TextBox();
            this.txt_company = new System.Windows.Forms.TextBox();
            this.btn_getevents = new System.Windows.Forms.Button();
            this.lbl_message = new System.Windows.Forms.Label();
            this.btn_send = new System.Windows.Forms.Button();
            this.progressBar1 = new System.Windows.Forms.ProgressBar();
            this.backgroundWorker1 = new System.ComponentModel.BackgroundWorker();
            this.lbl_version = new System.Windows.Forms.Label();
            this.lbl_version_number = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // cbx_events
            // 
            this.cbx_events.Enabled = false;
            this.cbx_events.FormattingEnabled = true;
            this.cbx_events.Location = new System.Drawing.Point(82, 145);
            this.cbx_events.Name = "cbx_events";
            this.cbx_events.Size = new System.Drawing.Size(174, 21);
            this.cbx_events.TabIndex = 6;
            this.cbx_events.SelectedIndexChanged += new System.EventHandler(this.cbx_events_SelectedIndexChanged);
            // 
            // lbl_selectevent
            // 
            this.lbl_selectevent.AutoSize = true;
            this.lbl_selectevent.Location = new System.Drawing.Point(8, 148);
            this.lbl_selectevent.Name = "lbl_selectevent";
            this.lbl_selectevent.Size = new System.Drawing.Size(71, 13);
            this.lbl_selectevent.TabIndex = 2;
            this.lbl_selectevent.Text = "Select Event:";
            // 
            // btn_saveshortcut
            // 
            this.btn_saveshortcut.Enabled = false;
            this.btn_saveshortcut.Location = new System.Drawing.Point(162, 172);
            this.btn_saveshortcut.Name = "btn_saveshortcut";
            this.btn_saveshortcut.Size = new System.Drawing.Size(94, 23);
            this.btn_saveshortcut.TabIndex = 7;
            this.btn_saveshortcut.Text = "Save shortcut";
            this.btn_saveshortcut.UseVisualStyleBackColor = true;
            this.btn_saveshortcut.Click += new System.EventHandler(this.btn_saveshortcut_Click);
            // 
            // lbl_user
            // 
            this.lbl_user.AutoSize = true;
            this.lbl_user.Location = new System.Drawing.Point(8, 9);
            this.lbl_user.Name = "lbl_user";
            this.lbl_user.Size = new System.Drawing.Size(58, 13);
            this.lbl_user.TabIndex = 4;
            this.lbl_user.Text = "Username:";
            // 
            // lbl_company
            // 
            this.lbl_company.AutoSize = true;
            this.lbl_company.Location = new System.Drawing.Point(8, 35);
            this.lbl_company.Name = "lbl_company";
            this.lbl_company.Size = new System.Drawing.Size(54, 13);
            this.lbl_company.TabIndex = 5;
            this.lbl_company.Text = "Company:";
            // 
            // lbl_dept
            // 
            this.lbl_dept.AutoSize = true;
            this.lbl_dept.Location = new System.Drawing.Point(8, 61);
            this.lbl_dept.Name = "lbl_dept";
            this.lbl_dept.Size = new System.Drawing.Size(76, 13);
            this.lbl_dept.TabIndex = 6;
            this.lbl_dept.Text = "Department id:";
            // 
            // lbl_password
            // 
            this.lbl_password.AutoSize = true;
            this.lbl_password.Location = new System.Drawing.Point(8, 87);
            this.lbl_password.Name = "lbl_password";
            this.lbl_password.Size = new System.Drawing.Size(56, 13);
            this.lbl_password.TabIndex = 7;
            this.lbl_password.Text = "Password:";
            // 
            // txt_user
            // 
            this.txt_user.Location = new System.Drawing.Point(82, 6);
            this.txt_user.Name = "txt_user";
            this.txt_user.Size = new System.Drawing.Size(174, 20);
            this.txt_user.TabIndex = 1;
            // 
            // txt_department
            // 
            this.txt_department.Location = new System.Drawing.Point(82, 58);
            this.txt_department.Name = "txt_department";
            this.txt_department.Size = new System.Drawing.Size(174, 20);
            this.txt_department.TabIndex = 3;
            // 
            // txt_password
            // 
            this.txt_password.Location = new System.Drawing.Point(82, 84);
            this.txt_password.Name = "txt_password";
            this.txt_password.Size = new System.Drawing.Size(174, 20);
            this.txt_password.TabIndex = 4;
            this.txt_password.UseSystemPasswordChar = true;
            this.txt_password.TextChanged += new System.EventHandler(this.txt_password_TextChanged);
            // 
            // txt_company
            // 
            this.txt_company.Location = new System.Drawing.Point(82, 32);
            this.txt_company.Name = "txt_company";
            this.txt_company.Size = new System.Drawing.Size(174, 20);
            this.txt_company.TabIndex = 2;
            // 
            // btn_getevents
            // 
            this.btn_getevents.Location = new System.Drawing.Point(181, 111);
            this.btn_getevents.Name = "btn_getevents";
            this.btn_getevents.Size = new System.Drawing.Size(75, 23);
            this.btn_getevents.TabIndex = 5;
            this.btn_getevents.Text = "Get Events";
            this.btn_getevents.UseVisualStyleBackColor = true;
            this.btn_getevents.Click += new System.EventHandler(this.button1_Click);
            // 
            // lbl_message
            // 
            this.lbl_message.AutoSize = true;
            this.lbl_message.Location = new System.Drawing.Point(8, 234);
            this.lbl_message.Name = "lbl_message";
            this.lbl_message.Size = new System.Drawing.Size(0, 13);
            this.lbl_message.TabIndex = 13;
            // 
            // btn_send
            // 
            this.btn_send.Enabled = false;
            this.btn_send.Location = new System.Drawing.Point(82, 173);
            this.btn_send.Name = "btn_send";
            this.btn_send.Size = new System.Drawing.Size(75, 23);
            this.btn_send.TabIndex = 14;
            this.btn_send.Text = "Send";
            this.btn_send.UseVisualStyleBackColor = true;
            this.btn_send.Click += new System.EventHandler(this.btn_send_Click);
            // 
            // progressBar1
            // 
            this.progressBar1.Location = new System.Drawing.Point(11, 110);
            this.progressBar1.Name = "progressBar1";
            this.progressBar1.Size = new System.Drawing.Size(164, 23);
            this.progressBar1.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
            this.progressBar1.TabIndex = 15;
            // 
            // backgroundWorker1
            // 
            this.backgroundWorker1.WorkerReportsProgress = true;
            this.backgroundWorker1.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorker1_DoWork);
            this.backgroundWorker1.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorker1_RunWorkerCompleted);
            this.backgroundWorker1.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.backgroundWorker1_ProgressChanged);
            // 
            // lbl_version
            // 
            this.lbl_version.AutoSize = true;
            this.lbl_version.Location = new System.Drawing.Point(8, 207);
            this.lbl_version.Name = "lbl_version";
            this.lbl_version.Size = new System.Drawing.Size(45, 13);
            this.lbl_version.TabIndex = 16;
            this.lbl_version.Text = "Version:";
            // 
            // lbl_version_number
            // 
            this.lbl_version_number.AutoSize = true;
            this.lbl_version_number.Location = new System.Drawing.Point(79, 207);
            this.lbl_version_number.Name = "lbl_version_number";
            this.lbl_version_number.Size = new System.Drawing.Size(35, 13);
            this.lbl_version_number.TabIndex = 17;
            this.lbl_version_number.Text = "label1";
            // 
            // StoreShortcut
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(278, 256);
            this.Controls.Add(this.lbl_version_number);
            this.Controls.Add(this.lbl_version);
            this.Controls.Add(this.progressBar1);
            this.Controls.Add(this.btn_send);
            this.Controls.Add(this.lbl_message);
            this.Controls.Add(this.btn_getevents);
            this.Controls.Add(this.txt_company);
            this.Controls.Add(this.txt_password);
            this.Controls.Add(this.txt_department);
            this.Controls.Add(this.txt_user);
            this.Controls.Add(this.lbl_password);
            this.Controls.Add(this.lbl_dept);
            this.Controls.Add(this.lbl_company);
            this.Controls.Add(this.lbl_user);
            this.Controls.Add(this.cbx_events);
            this.Controls.Add(this.btn_saveshortcut);
            this.Controls.Add(this.lbl_selectevent);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "StoreShortcut";
            this.Text = "UMS PAS Invoke";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ComboBox cbx_events;
        private System.Windows.Forms.Label lbl_selectevent;
        private System.Windows.Forms.Button btn_saveshortcut;
        private System.Windows.Forms.Label lbl_user;
        private System.Windows.Forms.Label lbl_company;
        private System.Windows.Forms.Label lbl_dept;
        private System.Windows.Forms.Label lbl_password;
        private System.Windows.Forms.TextBox txt_user;
        private System.Windows.Forms.TextBox txt_department;
        private System.Windows.Forms.TextBox txt_password;
        private System.Windows.Forms.TextBox txt_company;
        private System.Windows.Forms.Button btn_getevents;
        private System.Windows.Forms.Label lbl_message;
        private System.Windows.Forms.Button btn_send;
        private System.Windows.Forms.ProgressBar progressBar1;
        private System.ComponentModel.BackgroundWorker backgroundWorker1;
        private System.Windows.Forms.Label lbl_version;
        private System.Windows.Forms.Label lbl_version_number;
    }
}

