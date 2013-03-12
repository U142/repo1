using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Runtime.InteropServices;
using System.Threading;
using com.ums.UmsCommon;
using System.Threading.Tasks;
using log4net;
using log4net.Config;

namespace com.ums.pas.integration
{
    static class IntegrationService
    {
        private static ILog log = LogManager.GetLogger(typeof(IntegrationService));
        [DllImport("kernel32.dll",
            EntryPoint = "GetStdHandle",
            SetLastError = true,
            CharSet = CharSet.Auto,
            CallingConvention = CallingConvention.StdCall)]
        private static extern IntPtr GetStdHandle(int nStdHandle);
        [DllImport("kernel32.dll",
            EntryPoint = "AllocConsole",
            SetLastError = true,
            CharSet = CharSet.Auto,
            CallingConvention = CallingConvention.StdCall)]
        private static extern int AllocConsole();
        private const int STD_OUTPUT_HANDLE = -11;
        private const int MY_CODE_PAGE = 437; 

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            if (!Environment.UserInteractive)
            {
                ServiceBase[] ServicesToRun;
                ServicesToRun = new ServiceBase[] 
			    { 
				    new Integration() 
			    };
                ServiceBase.Run(ServicesToRun);
            }
            else
            {
                AllocConsole();
                XmlConfigurator.Configure();
                //BasicConfigurator.Configure();
                
                log.Info("Starting in UserInteractive Mode");
                Integration integration = new Integration();
                Thread thread = new Thread(() => integration.StartActiveMq()); //new ThreadStart(integration.StartActiveMq));
                thread.Start();
                while (true)
                {
                    String lineString = Console.ReadLine();
                    if (lineString.ToUpper().Equals("Q"))
                    {
                        break;
                    }
                }
                log.Info("Stopping service, Waiting for thread to finish...");
                integration.Stop();
                thread.Join();


            }
        }

      
    }
}
