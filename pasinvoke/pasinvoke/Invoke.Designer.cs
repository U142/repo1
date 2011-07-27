namespace pasinvoke
{
    partial class Invoke
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Invoke));
            this.btn_send = new System.Windows.Forms.Button();
            this.txt_password = new System.Windows.Forms.TextBox();
            this.lbl_password = new System.Windows.Forms.Label();
            this.lbl_status = new System.Windows.Forms.Label();
            this.lbl_user = new System.Windows.Forms.Label();
            this.txt_user = new System.Windows.Forms.TextBox();
            this.lbl_company = new System.Windows.Forms.Label();
            this.txt_company = new System.Windows.Forms.TextBox();
            this.lbl_department = new System.Windows.Forms.Label();
            this.txt_department = new System.Windows.Forms.TextBox();
            this.lbl_version_number = new System.Windows.Forms.Label();
            this.lbl_version = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // btn_send
            // 
            this.btn_send.Location = new System.Drawing.Point(191, 116);
            this.btn_send.Name = "btn_send";
            this.btn_send.Size = new System.Drawing.Size(75, 23);
            this.btn_send.TabIndex = 1;
            this.btn_send.Text = "Send";
            this.btn_send.UseVisualStyleBackColor = true;
            this.btn_send.Click += new System.EventHandler(this.button1_Click);
            this.btn_send.KeyDown += new System.Windows.Forms.KeyEventHandler(this.textBox1_TextChanged);
            // 
            // txt_password
            // 
            this.txt_password.Location = new System.Drawing.Point(81, 90);
            this.txt_password.Name = "txt_password";
            this.txt_password.Size = new System.Drawing.Size(185, 20);
            this.txt_password.TabIndex = 0;
            this.txt_password.UseSystemPasswordChar = true;
            this.txt_password.KeyDown += new System.Windows.Forms.KeyEventHandler(this.textBox1_TextChanged);
            // 
            // lbl_password
            // 
            this.lbl_password.AutoSize = true;
            this.lbl_password.Location = new System.Drawing.Point(17, 93);
            this.lbl_password.Name = "lbl_password";
            this.lbl_password.Size = new System.Drawing.Size(56, 13);
            this.lbl_password.TabIndex = 2;
            this.lbl_password.Text = "Password:";
            // 
            // lbl_status
            // 
            this.lbl_status.AutoSize = true;
            this.lbl_status.Location = new System.Drawing.Point(16, 182);
            this.lbl_status.Name = "lbl_status";
            this.lbl_status.Size = new System.Drawing.Size(0, 13);
            this.lbl_status.TabIndex = 3;
            // 
            // lbl_user
            // 
            this.lbl_user.AutoSize = true;
            this.lbl_user.Location = new System.Drawing.Point(17, 15);
            this.lbl_user.Name = "lbl_user";
            this.lbl_user.Size = new System.Drawing.Size(58, 13);
            this.lbl_user.TabIndex = 4;
            this.lbl_user.Text = "Username:";
            // 
            // txt_user
            // 
            this.txt_user.Enabled = false;
            this.txt_user.Location = new System.Drawing.Point(81, 12);
            this.txt_user.Name = "txt_user";
            this.txt_user.Size = new System.Drawing.Size(185, 20);
            this.txt_user.TabIndex = 5;
            // 
            // lbl_company
            // 
            this.lbl_company.AutoSize = true;
            this.lbl_company.Location = new System.Drawing.Point(16, 41);
            this.lbl_company.Name = "lbl_company";
            this.lbl_company.Size = new System.Drawing.Size(54, 13);
            this.lbl_company.TabIndex = 6;
            this.lbl_company.Text = "Company:";
            // 
            // txt_company
            // 
            this.txt_company.Enabled = false;
            this.txt_company.Location = new System.Drawing.Point(81, 38);
            this.txt_company.Name = "txt_company";
            this.txt_company.Size = new System.Drawing.Size(185, 20);
            this.txt_company.TabIndex = 7;
            // 
            // lbl_department
            // 
            this.lbl_department.AutoSize = true;
            this.lbl_department.Location = new System.Drawing.Point(16, 67);
            this.lbl_department.Name = "lbl_department";
            this.lbl_department.Size = new System.Drawing.Size(65, 13);
            this.lbl_department.TabIndex = 8;
            this.lbl_department.Text = "Department:";
            // 
            // txt_department
            // 
            this.txt_department.Enabled = false;
            this.txt_department.Location = new System.Drawing.Point(81, 64);
            this.txt_department.Name = "txt_department";
            this.txt_department.Size = new System.Drawing.Size(185, 20);
            this.txt_department.TabIndex = 9;
            // 
            // lbl_version_number
            // 
            this.lbl_version_number.AutoSize = true;
            this.lbl_version_number.Location = new System.Drawing.Point(78, 148);
            this.lbl_version_number.Name = "lbl_version_number";
            this.lbl_version_number.Size = new System.Drawing.Size(35, 13);
            this.lbl_version_number.TabIndex = 10;
            this.lbl_version_number.Text = "label1";
            // 
            // lbl_version
            // 
            this.lbl_version.AutoSize = true;
            this.lbl_version.Location = new System.Drawing.Point(17, 148);
            this.lbl_version.Name = "lbl_version";
            this.lbl_version.Size = new System.Drawing.Size(45, 13);
            this.lbl_version.TabIndex = 11;
            this.lbl_version.Text = "Version:";
            // 
            // Invoke
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(278, 204);
            this.Controls.Add(this.lbl_version);
            this.Controls.Add(this.lbl_version_number);
            this.Controls.Add(this.btn_send);
            this.Controls.Add(this.txt_department);
            this.Controls.Add(this.lbl_department);
            this.Controls.Add(this.txt_company);
            this.Controls.Add(this.lbl_company);
            this.Controls.Add(this.txt_user);
            this.Controls.Add(this.lbl_user);
            this.Controls.Add(this.lbl_status);
            this.Controls.Add(this.txt_password);
            this.Controls.Add(this.lbl_password);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Invoke";
            this.Text = "Send";
            this.Load += new System.EventHandler(this.InvokeDialog_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btn_send;
        private System.Windows.Forms.TextBox txt_password;
        private System.Windows.Forms.Label lbl_password;
        private System.Windows.Forms.Label lbl_status;
        private System.Windows.Forms.Label lbl_user;
        private System.Windows.Forms.TextBox txt_user;
        private System.Windows.Forms.Label lbl_company;
        private System.Windows.Forms.TextBox txt_company;
        private System.Windows.Forms.Label lbl_department;
        private System.Windows.Forms.TextBox txt_department;
        private System.Windows.Forms.Label lbl_version_number;
        private System.Windows.Forms.Label lbl_version;
    }
}