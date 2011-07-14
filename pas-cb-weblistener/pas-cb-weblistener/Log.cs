using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Diagnostics;

namespace pas_cb_weblistener
{
    public class Log
    {
        public static void WriteLog(string text, int severity) 
        { 
            WriteLog(text, text, severity); 
        }
        public static void WriteLog(string shorttext, string longtext, int severity)
        {
            try
            {
                EventLogEntryType eventtype;

                switch (severity)
                {
                    case 0:
                        eventtype = EventLogEntryType.Information;
                        break;
                    case 1:
                        eventtype = EventLogEntryType.Warning;
                        break;
                    case 2:
                        eventtype = EventLogEntryType.Error;
                        break;
                    default:
                        eventtype = EventLogEntryType.Warning;
                        break;
                }
                EventLog.WriteEntry("CBServer", longtext, eventtype);
            }
            catch
            { }
        }
    }
}
