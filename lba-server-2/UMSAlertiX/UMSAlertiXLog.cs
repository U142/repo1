using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Threading;
using System.IO;

namespace UMSAlertiX
{
    public class UMSAlertiXLog
    {

        private const int lWriteConsole = 7; // lwriteconsole & ltype (1 = commands/help, 2 = standard log messages, 4 = misc info like timestamps)
        private const string szLogDTM = "yyyy'-'MM'-'dd HH':'mm':'ss";

        Queue<string> oQueue;
        public void SetQueue(ref Queue<string> obj)
        {
            oQueue = obj;
        }

        UMSAlertiXController oController;
        public void SetController(ref UMSAlertiXController obj)
        {
            oController = obj;
        }

        public void WriteLog(string szLogText)
        {
            string szLogLine;

            DateTime dtmRetDate = DateTime.Now;
            szLogLine = dtmRetDate.ToString(szLogDTM) + " " + szLogText;
            WriteConsole(szLogLine, 2);

            oQueue.Enqueue(szLogLine);
        }

        public void WriteLog(string szLogText, string szConsoleText) // overloaded function to enable less output in window than in logfile
        {
            string szLogLine;
            string szConsoleLine;

            DateTime dtmRetDate = DateTime.Now;
            szLogLine = dtmRetDate.ToString(szLogDTM) + " " + szLogText;
            szConsoleLine = dtmRetDate.ToString(szLogDTM) + " " + szConsoleText;
            WriteConsole(szConsoleLine, 2);

            oQueue.Enqueue(szLogLine);
        }

        public void WriteConsole(string szText, int lType) // type -> 1 = commands, 2 = log, 4 = misc
        {
            //DateTime dtmRetDate = DateTime.Now;
            if ((lWriteConsole & lType) == lType)
            {
                Console.WriteLine(szText);
            }
        }

        public void FlushLog()
        {
            //Flushes the log queue to a file
            while (oController.running || oQueue.Count > 0)
            {
                if (oQueue.Count > 0)
                {
                    string szLogFilename;
                    DateTime dtmRetDate = DateTime.Now;

                    szLogFilename = "lba_" + dtmRetDate.ToString("yyyyMMdd") + ".log";

                    try
                    {
                        TextWriter fLogfile = File.AppendText(szLogFilename);
                        fLogfile.WriteLine(oQueue.Dequeue());
                        fLogfile.Close();
                    }
                    catch (Exception e)
                    {
                        WriteConsole("ERROR -- Failed to write log til file", 4);
                        WriteConsole("Exception: " + e, 4);
                    }
                }
                Thread.Sleep(100);
            }
            oController.threads--;
            WriteConsole("# Stopped Log thread", 1); // wont be in log files
        }
    
    }
}
