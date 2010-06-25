using System;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.ServiceProcess;
using umssettings;
using CookComputing.XmlRpc;

namespace pas_cb_server
{
    class CBServer
    {
        public static bool running = true;

        static void Main(string[] args)
        {            
            try
            {
                // check for interactive (console) or service mode
                if (Environment.UserInteractive)
                {
                    // Add custom handler for ctrl+c
                    Console.CancelKeyPress += new ConsoleCancelEventHandler(exit);

                    CBService serv = new CBService();
                    serv.start(args);

                    while (CBServer.running)
                    {
                        Thread.Sleep(100);
                    };

                    serv.stop();

                    Console.CancelKeyPress -= new ConsoleCancelEventHandler(exit);
                }
                else
                {
                    ServiceBase[] ServicesToRun;
                    ServicesToRun = new ServiceBase[] 
		            { 
			            new CBService() 
		            };
                    ServiceBase.Run(ServicesToRun);
                }
            }
            catch (Exception e)
            {
                Trace.WriteLine(e.Message);
                CBServer.running = false;
                return;
            }
        }

        protected static void exit(object sender, ConsoleCancelEventArgs args)
        {
            Log.WriteLog(String.Format("Shutting down ({0} threads) (interactive mode)", Interlocked.Read(ref Settings.threads)), 0);
            Trace.WriteLine("\nStopping...\nPress ctrl+c again to force exit.");
            args.Cancel = true;
            CBServer.running = false;
        }
    }
}
