using System;
using System.Collections.Generic;
using System.Linq;
using com.ums.UmsCommon;

namespace ums.ws.tcs
{
    public class SoapException : System.Web.Services.Protocols.SoapException
    {
        public SoapException(string message, System.Xml.XmlQualifiedName code)
        {
            // write the exception to the syslog
            Log.WriteSysLog(message, 3, 1);
            throw new System.Web.Services.Protocols.SoapException(message, code);
        }
    }

    public static class Log
    {
        private static int lsyslogtype = 0;

        public static void InitLog(string app, string sysloghost, int syslogport, int syslogtype)
        {
            try
            {
                if (app != "" && sysloghost != "" && syslogport > 0 && syslogtype > 0)
                {
                    if (UCommon.Initialize(app, sysloghost, syslogport))
                    {
                        lsyslogtype = syslogtype;
                    }
                }
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
        }

        public static void WriteSysLog(string logtext, int severity, int type)
        {
            if ((lsyslogtype & type) == type)
            {
                switch (severity)
                {
                    case 0: // message
                        ULog.write(logtext);
                        break;
                    case 1: // warning
                        ULog.warning("warning: " + logtext);
                        break;
                    case 2: // error
                        ULog.error("error: " + logtext);
                        break;
                    case 3: // exception
                        ULog.error("exception: " + logtext);
                        break;
                }
            }
        }

        public static void WriteSysLog(string clientstring, string logtext, int severity, int type)
        {
            WriteSysLog(clientstring + " " + logtext, severity, type);
        }
    }
}
