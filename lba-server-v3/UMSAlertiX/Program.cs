using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Configuration;

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

            Queue<string> qLog = new Queue<string>();

            oParser.SetController(ref oController);
            oStatus.SetController(ref oController);
            oLog.SetController(ref oController);
            oLog.SetQueue(ref qLog);

            oController.log = oLog;
            oController.dsn = ConfigurationManager.ConnectionStrings["backbone"].ConnectionString;
            oController.statusapi = ConfigurationManager.AppSettings["WSStatusAPI"];
            oController.alertapi = ConfigurationManager.AppSettings["WSAlertAPI"];
            oController.areaapi = ConfigurationManager.AppSettings["WSAreaAPI"];
            oController.parsepath = ConfigurationManager.AppSettings["ParsePath"];
            oController.CopyFrom = ConfigurationManager.AppSettings["CopyFrom"];
            oController.CopyTo = new List<string>();
            foreach (string path in ConfigurationManager.AppSettings["CopyTo"].Split(','))
                oController.CopyTo.Add(path);

            if(ConfigurationManager.AppSettings["MessageValidity"]!="") oController.message_validity = Convert.ToInt32(ConfigurationManager.AppSettings["MessageValidity"]); // default validity

            if (ConfigurationManager.AppSettings["IgnoreCertErrors"].Equals("true", StringComparison.OrdinalIgnoreCase))
            {
                Console.WriteLine("Ignoring signature errors");
                System.Net.ServicePointManager.ServerCertificateValidationCallback += (sender, certificate, chain, sslPolicyErrors) => true;
            }

            UMSAlertiXWebServer oWmsProxy = new UMSAlertiXWebServer(ConfigurationManager.AppSettings["WebServerPrefix"]);
            oController.webserver = oWmsProxy;
            oWmsProxy.SetController(ref oController);
            
            Console.BackgroundColor = ConsoleColor.DarkBlue;
            Console.ForegroundColor = ConsoleColor.White;
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
            Console.WriteLine("# Starting Webserver thread with filter: " + ConfigurationManager.AppSettings["WebServerPrefix"], 1);
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
