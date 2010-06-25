using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading;

namespace pas_cb_server
{
    partial class CBService : ServiceBase
    {
        public CBService()
        {
            InitializeComponent();
        }

        public void start(string[] args)
        {
            OnStart(args);
        }

        public void stop()
        {
            OnStop();
        }

        protected override void OnStart(string[] args)
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
            Log.WriteLog("Starting parser thread", 9);
            new Thread(new ThreadStart(CBParser.CheckFilesThread)).Start();
            Interlocked.Increment(ref Settings.threads);

            if (Environment.UserInteractive)
            {
                Log.WriteLog("Starting keyreader thread", 9);
                new Thread(new ThreadStart(Tools.KeyReaderThread)).Start();
                Interlocked.Increment(ref Settings.threads);
            }

            if (Settings.l_statuspollinterval > 0)
            {
                Log.WriteLog("Starting status thread", 9);
                new Thread(new ThreadStart(CBStatus.CheckStatusThread)).Start();
                Interlocked.Increment(ref Settings.threads);
            }

            // log startup mode
            if (Environment.UserInteractive)
                Log.WriteLog(String.Format("Started up ({0} threads) (interactive mode)", Interlocked.Read(ref Settings.threads)), 0);
            else
                Log.WriteLog(String.Format("Started up ({0} threads) (service mode)", Interlocked.Read(ref Settings.threads)), 0);
        }

        protected override void OnStop()
        {
            int exitcounter = 0;
            
            if (!Environment.UserInteractive)
                Log.WriteLog(String.Format("Shutting down ({0} threads) (service mode)", Interlocked.Read(ref Settings.threads)), 0);

            if(CBServer.running)
                CBServer.running = false;

            while(Interlocked.Read(ref Settings.threads)>0)
            {
                if (++exitcounter > 30)
                    break;

                Thread.Sleep(1000);
            }
        }
    }
}
