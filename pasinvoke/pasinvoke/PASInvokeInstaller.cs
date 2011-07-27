using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;
using System.Linq;


namespace pasinvoke
{
    [RunInstaller(true)]
    public partial class PASInvokeInstaller : Installer
    {
        public PASInvokeInstaller()
        {
            InitializeComponent();
        }
    }
}
