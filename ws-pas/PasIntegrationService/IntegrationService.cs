using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;

namespace com.ums.pas.integration
{
    static class IntegrationService
    {
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
                new Integration().StartActiveMq();
            }
        }
    }
}
