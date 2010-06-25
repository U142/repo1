using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;
using System.Linq;


namespace pas_cb_server
{
    [RunInstaller(true)]
    public partial class CBServiceInstaller : Installer
    {
        public CBServiceInstaller()
        {
            InitializeComponent();
        }
    }
}
