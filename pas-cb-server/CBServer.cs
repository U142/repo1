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
        static void Main(string[] args)
        {
            // Add custom handler for ctrl+c
            Console.CancelKeyPress += new ConsoleCancelEventHandler(exit);

            // Get Config values and initialize
            try
            {
                // Get settings
                Settings.init();

                // Write startup info
                Log.WriteLog(String.Format("Debug mode: {0}", Settings.debug), 9);
                Log.WriteLog(String.Format("Dump path: {0}", Settings.sz_dumppath), 9);
                Log.WriteLog(String.Format("Parse path: {0}", Settings.sz_parsepath), 9);

                if (Settings.l_statuspollinterval > 0)
                    Log.WriteLog(String.Format("Status poll interval is {0} seconds", Settings.l_statuspollinterval), 9);
                else
                    Log.WriteLog(String.Format("Status poll interval is disabled"), 9);

                // Set CPU affinity
                if (Settings.l_cpuaffinity > 0)
                    System.Diagnostics.Process.GetCurrentProcess().ProcessorAffinity = (System.IntPtr)Settings.l_cpuaffinity;

                // Start threads
                Settings.running = true; // has to be set before threads start

                Log.WriteLog("Starting keyreader thread", 9);
                new Thread(new ThreadStart(Tools.KeyReaderThread)).Start();

                Log.WriteLog("Starting parser thread", 9);
                new Thread(new ThreadStart(CBParser.CheckFilesThread)).Start();

                if (Settings.l_statuspollinterval > 0)
                {
                    Log.WriteLog("Starting status thread", 9);
                    new Thread(new ThreadStart(CBStatus.CheckStatusThread)).Start();
                }
            }
            catch (Exception e)
            {
                Trace.WriteLine(e.Message);
                return;
            }

            while (Settings.running)
            {
                Thread.Sleep(100);
            };

            Console.CancelKeyPress -= new ConsoleCancelEventHandler(exit);
        }

        protected static void exit(object sender, ConsoleCancelEventArgs args)
        {
            Trace.WriteLine("Stopping...\nPress ctrl+c again to force exit.");
            args.Cancel = true;
            Settings.running = false;
        }
    }
}
