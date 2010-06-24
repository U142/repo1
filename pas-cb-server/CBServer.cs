using System;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
//using System.ServiceProcess;
using umssettings;
using CookComputing.XmlRpc;

namespace pas_cb_server
{
    class CBServer// : ServiceBase
    {
        static void Main(string[] args)
        {
            // Add custom handler for ctrl+c
            Console.CancelKeyPress += new ConsoleCancelEventHandler(exit);
            
            // Get Config values and initialize
            try
            {
                // Get all config values
                Settings.init();

                // Start threads
                Settings.start();
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
