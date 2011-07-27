using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using pasinvoke.no.ums2.secure;

namespace pasinvoke.src
{
    public class Send
    {
        ULOGONINFO logon;
        public ExecResponse execResponse { get; set; }

        public Send(ULOGONINFO logon)
        {
            this.logon = logon;
        }

        public void send(long eventpk)
        {
            parmws _parmws = new parmws();
            _parmws.Url = "https://secure.ums.no/pas/ws_pasqa/ws/ExternalExec.asmx";

            _parmws.ExecEventV3Completed += new ExecEventV3CompletedEventHandler(ExecEventV3Completed);
            _parmws.ExecEventV3Async(eventpk, logon, "simulate", DateTime.Now.ToString("yyyyMMdd"), DateTime.Now.ToString("HHmmss"));
                
        }

        public void ExecEventV3Completed(object sender, ExecEventV3CompletedEventArgs args)
        {
            execResponse = args.Result;
        }


    }
}
