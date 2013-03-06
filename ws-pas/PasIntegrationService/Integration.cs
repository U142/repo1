using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using Apache.NMS;
using Apache.NMS.Util;
using com.ums.UmsCommon;

namespace com.ums.pas.integration
{
    public partial class Integration : ServiceBase
    {
        IntegrationMq integrationMq = new IntegrationMq();

        public Integration()
        {
            ULog.setLogTo((long)ULog.ULOGTO.EVENTLOG);
            UCommon.Initialize(String.Format("soap/pas/integration/{0}", "1.0"), "makoto.umscom.com", 514);
            InitializeComponent();
        }


        protected override void OnStart(string[] args)
        {
            StartActiveMq();
        }

        public void StartActiveMq()
        {
            ULog.write("Starting up PAS Integration Service");
            integrationMq.startUpQueue();
        }

        protected override void OnStop()
        {
            StopQueue();
        }

        protected void StopQueue()
        {
            integrationMq.KeepRunning.GetAndSet(false);
        }

        public void StopManually(Delegate Callback)
        {
            StopQueue();
        }
    }
}
