using System;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using umssettings;
using CookComputing.XmlRpc;

namespace pas_cb_server
{
    class CBServer
    {
        public static bool running = true;

        static void Main(string[] args)
        {
            // Add custom handler for ctrl+c
            Console.CancelKeyPress += new ConsoleCancelEventHandler(exit);

            // Get Config values and initialize
            try
            {
                // ParsePath and DBConn are required
                Settings.sz_parsepath = Settings.GetString("ParsePath");
                Settings.sz_dbconn = String.Format("DSN={0};UID={1};PWD={2};", Settings.GetString("DSN"), Settings.GetString("UID"), Settings.GetString("PWD"));

                // Init log values default is both syslog and log files
                Log.InitLog(Settings.GetValue("SyslogApp", "umsalertix"), Settings.GetValue("SyslogServer", "makoto.umscom.com"), Settings.GetValue("SyslogPort", 514), Settings.GetValue("Syslog", true), Settings.GetValue("LogFileName", "umsalertix"), Settings.GetValue("LogFile", true));

                // Start threads
                Log.WriteLog("Starting parser thread", 9);
                new Thread(new ThreadStart(Parser.CheckFiles)).Start();

                AlertInfo ai = new AlertInfo();
                Operator op = new Operator();
                CB_one2many.CreateAlert(ai, op);
            }
            catch (Exception e)
            {
                Trace.WriteLine(e.Message);
                return;
            }

            while (running)
            {
                Thread.Sleep(10);
            };

            Console.CancelKeyPress -= new ConsoleCancelEventHandler(exit);
        }

        protected static void exit(object sender, ConsoleCancelEventArgs args)
        {
            Trace.WriteLine("Stopping...\nPress ctrl+c again to force exit.");
            args.Cancel = true;
            running = false;
        }
    }
}
