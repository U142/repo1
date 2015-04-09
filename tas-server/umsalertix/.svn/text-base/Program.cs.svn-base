using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace umsalertix
{
    class Program
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
                Settings.l_statistics_timer = Settings.GetValue("StatisticsTimer", 5); // 5 minutes poll interval for statistics
                Settings.l_validity = Settings.GetValue("MessageValidity", 60);
                Settings.sz_executemode = Settings.GetValue("ExecuteMode", ""); // default is normal (empty);
                Settings.sz_parsepath = Settings.GetString("ParsePath");
                Settings.sz_dbconn = String.Format("DSN={0};UID={1};PWD={2};", Settings.GetString("DSN"), Settings.GetString("UID"), Settings.GetString("PWD"));
                
                // use DSN/UID/PWD for wl if wl is missing
                Settings.sz_dbconn_whitelist = String.Format("DSN={0};UID={1};PWD={2};", Settings.GetValue("DSN_wl", Settings.GetString("DSN")), Settings.GetValue("UID_wl", Settings.GetString("UID")), Settings.GetValue("PWD_wl", Settings.GetString("PWD")));

                // Which threads to run
                Settings.tr_parser = Settings.GetValue("ThreadParser", true);
                Settings.tr_statistics = Settings.GetValue("ThreadStatistics", false);
                Settings.tr_stat_cccount = Settings.GetValue("ThreadStatusCountryCodeCount", true);
                Settings.tr_stat_ccsend = Settings.GetValue("ThreadStatusCountryCodeSend", true);
                Settings.tr_stat_jobcount = Settings.GetValue("ThreadJobCount", true);
                Settings.tr_stat_jobsend = Settings.GetValue("ThreadJobSend", true);

                // Init log values default is both syslog and log files
                Log.InitLog(Settings.GetValue("SyslogApp", "umsalertix"), Settings.GetValue("SyslogServer", "makoto.umscom.com"), Settings.GetValue("SyslogPort", 514), Settings.GetValue("Syslog",true), Settings.GetValue("LogFileName","umsalertix"), Settings.GetValue("LogFile",true));
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return;
            }

            if (Settings.sz_executemode != "")
                Log.WriteLog("Forcing ExecuteMode=" + Settings.sz_executemode, 9);

            // all good, continue starting stuff
            Log.WriteLog(String.Format("Parser thread: {0}", Settings.tr_parser), 9);
            if (Settings.tr_parser)
                new Thread(new ThreadStart(Parser.CheckFiles)).Start();

            Log.WriteLog(String.Format("CountryCodeStatistics thread (count): {0}", Settings.tr_statistics), 9);
            if (Settings.tr_statistics)
                new Thread(new ThreadStart(AXStatistics.CountryCodeStatistics)).Start();

            Log.WriteLog(String.Format("CountryCodeAlertStatus thread (count): {0}", Settings.tr_stat_cccount), 9);
            if (Settings.tr_stat_cccount)
                new Thread(new ThreadStart(AXStatus.UpdateCountCC)).Start();

            Log.WriteLog(String.Format("CountryCodeAlertStatus thread (send): {0}", Settings.tr_stat_ccsend), 9);
            if (Settings.tr_stat_ccsend)
                new Thread(new ThreadStart(AXStatus.UpdateSendCC)).Start();

            Log.WriteLog(String.Format("JobStatus thread (count): {0}", Settings.tr_stat_jobcount), 9);
            if (Settings.tr_stat_jobcount)
                new Thread(new ThreadStart(AXStatus.UpdateJobStatusCount)).Start();

            Log.WriteLog(String.Format("JobStatus thread (send): {0}", Settings.tr_stat_jobsend), 9);
            if (Settings.tr_stat_jobsend)
                new Thread(new ThreadStart(AXStatus.UpdateJobStatusSend)).Start();

            while (running) {
                Thread.Sleep(10);
            };

            Console.CancelKeyPress -= new ConsoleCancelEventHandler(exit);
        }

        protected static void exit(object sender, ConsoleCancelEventArgs args)
        {
            Console.WriteLine("Stopping...\nPress ctrl+c again to force exit.");
            args.Cancel = true;
            running = false;
        }

    }
}
