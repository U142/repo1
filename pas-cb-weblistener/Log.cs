using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace pas_cb_weblistener
{
    public class Log
    {
        public static void WriteLog(string text, int severity) 
        { 
            WriteLog(text, text, severity); 
        }
        public static void WriteLog(string shorttext, string longtext, int severity) { }
    }
}
