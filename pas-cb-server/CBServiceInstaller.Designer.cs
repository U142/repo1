namespace pas_cb_server
{
    partial class CBServiceInstaller
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

        #region Component Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.cb_serviceInstaller = new System.ServiceProcess.ServiceInstaller();
            this.cb_serviceProcessInstaller = new System.ServiceProcess.ServiceProcessInstaller();
            // 
            // cb_serviceInstaller
            // 
            this.cb_serviceInstaller.DisplayName = "Cell Broadcast Service";
            this.cb_serviceInstaller.ServiceName = "CBService";
            // 
            // cb_serviceProcessInstaller
            // 
            this.cb_serviceProcessInstaller.Account = System.ServiceProcess.ServiceAccount.LocalSystem;
            this.cb_serviceProcessInstaller.Password = null;
            this.cb_serviceProcessInstaller.Username = null;
            // 
            // CBServiceInstaller
            // 
            this.Installers.AddRange(new System.Configuration.Install.Installer[] {
            this.cb_serviceInstaller,
            this.cb_serviceProcessInstaller});

        }

        #endregion

        private System.ServiceProcess.ServiceInstaller cb_serviceInstaller;
        private System.ServiceProcess.ServiceProcessInstaller cb_serviceProcessInstaller;
    }
}