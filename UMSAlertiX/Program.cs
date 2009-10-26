﻿using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

namespace UMSAlertiX
{
    class Program
    {
        static void Main(string[] args)
        {
            bool bError = false;
            UMSAlertiXController oController = new UMSAlertiXController();
            UMSAlertiXParser oParser = new UMSAlertiXParser();
            UMSAlertiXStatus oStatus = new UMSAlertiXStatus();
            UMSAlertiXLog oLog = new UMSAlertiXLog();
            UMSAlertiXConfig oConfig = new UMSAlertiXConfig();

            Queue<string> qLog = new Queue<string>();

            oParser.SetController(ref oController);
            oStatus.SetController(ref oController);
            oLog.SetController(ref oController);
            oLog.SetQueue(ref qLog);

            oController.log = oLog;
            oController.dsn = "DSN=" + oConfig.GetConfigValue("DBDSN") + ";UID=" + oConfig.GetConfigValue("DBUID") + ";PWD=" + oConfig.GetConfigValue("DBPWD") + ";";
            oController.statusapi = oConfig.GetConfigValue("WSStatusAPI");
            oController.alertapi = oConfig.GetConfigValue("WSAlertAPI");
            oController.areaapi = oConfig.GetConfigValue("WSAreaAPI");
            oController.parsepath = oConfig.GetConfigValue("ParsePath");
            if(oConfig.GetConfigValue("MessageValidity")!="") oController.message_validity = Convert.ToInt32(oConfig.GetConfigValue("MessageValidity")); // default validity
            if(oConfig.GetConfigValue("CPUAffinity")!="") oController.affinity = Convert.ToInt32(oConfig.GetConfigValue("CPUAffinity"));

            //oController.InitOperators();

            UMSAlertiXWebServer oWmsProxy = new UMSAlertiXWebServer(oConfig.GetConfigValue("WebServerPrefix"));
            oController.webserver = oWmsProxy;
            oWmsProxy.SetController(ref oController);
            
            System.Diagnostics.Process.GetCurrentProcess().ProcessorAffinity = (System.IntPtr)oController.affinity;
            Console.BackgroundColor = ConsoleColor.DarkBlue;
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.Clear();

            // parse command line arguments
            foreach (string s in args)
            {
                switch (s)
                {
                    case "-?":
                    case "-h":
                        Console.WriteLine("");
                        Console.WriteLine("List of commands:");
                        Console.WriteLine("");
                        Console.WriteLine("  -h, -?\tthis page");
                        break;
                    default:
                        break;
                }
            }

            // abort if error detected
            if (bError)
            {
                Console.WriteLine("Invalid parameter. -h for help");
                return;
            }

            Console.WriteLine("-- Starting up --", 1);

            // start watcher thread
            Console.WriteLine("# Starting Watcher thread", 1);
            Thread trKey = new Thread(new ThreadStart(oController.GetKey));
            trKey.Start();
            oController.threads++;

            // start log thread
            Console.WriteLine("# Starting Log Status thread", 1);
            Thread trLog = new Thread(new ThreadStart(oLog.FlushLog));
            trLog.Start();
            oController.threads++;

            // start parser thread
            Console.WriteLine("# Starting Parser thread", 1);
            Thread trParser = new Thread(new ThreadStart(oParser.CheckFiles));
            trParser.Start();
            oController.threads++;

            // start job status thread
            Console.WriteLine("# Starting Job Status thread", 1);
            Thread trJobStatus = new Thread(new ThreadStart(oStatus.UpdateJobStatus));
            trJobStatus.Start();
            oController.threads++;

            // start cc status thread
            Console.WriteLine("# Starting CC Status thread", 1);
            Thread trCCStatus = new Thread(new ThreadStart(oStatus.UpdateCCStatus));
            trCCStatus.Start();
            oController.threads++;

            // start webserver thread
            Console.WriteLine("# Starting Webserver thread with filter: " + oConfig.GetConfigValue("WebServerPrefix"), 1);
            Thread trWebServer = new Thread(new ThreadStart(oWmsProxy.Start));
            trWebServer.Start();
            oController.threads++;

            while (oController.threads > 0)
            {
                Thread.Sleep(100);
            }
            Thread.Sleep(1000);
        }
    }
}
