using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Threading;
using System.Diagnostics;
using com.ums.UmsCommon;

namespace pas_cb_server
{
    public static class Log
    {
        /*
         *  severity:
         *      0: message
         *      1: warning
         *      2: error
         *      9: console only
         *
         */

        private const string szLogDTM = "yyyy'-'MM'-'dd HH':'mm':'ss";
        private static bool bsyslog = false;
        private static bool blogfile = false;
        private static string szlogfilename = "";
        private static Queue<string> qlog = new Queue<string>();
        public static void InitLog(string app, string sysloghost, int syslogport, bool syslog, string logfilepath, string logfilename, bool logfile)
        {
            try
            {
                blogfile = logfile;
                if (blogfile)
                {
                    szlogfilename = logfilepath + logfilename;
                    WriteLog(
                        "Using logfile, starting file log thread",
                        9);
                    new Thread(new ThreadStart(FlushLog)).Start();
                    Interlocked.Increment(ref Settings.threads);
                }
                else
                {
                    WriteLog(
                        "Not using logfile",
                        9);
                }

                if (app != "" && sysloghost != "" && syslogport > 0 && syslog)
                {
                    if (UCommon.Initialize(app, sysloghost, syslogport))
                    {
                        ULog.setLogTo((long)ULog.ULOGTO.SYSLOG);
                        bsyslog = syslog;
                        WriteLog(
                            String.Format("Syslog initialized ({0}) ({1}) ({2})", app, sysloghost, syslogport.ToString()),
                            9);
                    }
                    else
                    {
                        WriteLog(
                            String.Format("Syslog fail to initialized ({0}) ({1}) ({2})", app, sysloghost, syslogport.ToString()),
                            2);
                    }
                }
                else
                {
                    WriteLog(
                        "Not using syslog",
                        9);
                }
            }
            catch (Exception e)
            {
                WriteLog(
                    "Could not initialize syslog: " + e.Message,
                    "Could not initialize syslog: " + e.ToString(),
                    2);
            }
        }

        public static void UnInitLog()
        {
            if (bsyslog)
                UCommon.UnInitialize();
        }

        public static void WriteLog(string logtext, int severity)
        {
            WriteLog(logtext, logtext, severity);
        }

        public static void WriteLog(string consoletext, string logtext, int severity)
        {
            string szConsoleLine;
            string szLogLine;

            DateTime dtmRetDate = DateTime.Now;
            szConsoleLine = dtmRetDate.ToString(szLogDTM) + " " + consoletext;
            szLogLine = dtmRetDate.ToString(szLogDTM) + " " + logtext;

            if (bsyslog) // write to syslog if enabled
                WriteSysLog(consoletext, severity);

            if (blogfile && severity != 9) // add to logfile queue if enabled, but don't log severity 9
                qlog.Enqueue(szLogLine);

            Trace.WriteLine(szConsoleLine);
        }

        private static void WriteSysLog(string logtext, int severity)
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
            }
        }

        public static void FlushLog()
        {
            //Flushes the log queue to a file
            while (CBServer.running || qlog.Count > 0)
            {
                if (qlog.Count > 0)
                {
                    string szLogFilename;
                    DateTime dtmRetDate = DateTime.Now;

                    szLogFilename = szlogfilename + "_" + dtmRetDate.ToString("yyyyMMdd") + ".log";

                    try
                    {
                        TextWriter fLogfile = File.AppendText(szLogFilename);
                        fLogfile.WriteLine(qlog.Dequeue());
                        fLogfile.Close();
                    }
                    catch (Exception e)
                    {
                        WriteLog("ERROR -- Failed to write log til file", 9);
                        WriteLog("Exception: " + e, 9);
                    }
                }

                if (qlog.Count == 0)
                    Thread.Sleep(1000);
            }

            WriteLog("Stopped file log thread", 9); // wont be in log files
            Interlocked.Decrement(ref Settings.threads);
        }

    }
    
    public static class DebugLog
    {
        public static void dump(string sz_text, Operator op, string sz_method, int l_refno)
        {
            string sz_filename = String.Format("{0}log-{1}-{2}.txt"
                , Settings.sz_dumppath
                , l_refno
                , op.sz_operatorname);

            string sz_seperator = "--------------------------------------";
            string sz_header = String.Format("{0}\r\n# {1} - {2}\r\n{0}\r\n"
                , sz_seperator
                , sz_method
                , DateTime.Now);

            File.AppendAllText(sz_filename, sz_header);
            File.AppendAllText(sz_filename, sz_text + "\r\n\r\n");

            if (!Settings.live) // don't spam the log with dump messages while running in live mode
                Log.WriteLog(String.Format("{0} (op={1}) {2} DUMPED", l_refno, op.sz_operatorname, sz_method), 0);
        }
    }
}
