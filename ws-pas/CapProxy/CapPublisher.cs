using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.pas.cap.CapServiceReference;

namespace com.ums.pas.cap
{
    public class CapProxy
    {
        public void PublishCap(alert capAlert)
        {
            try
            {
                //publish file through CapService
                CapServiceClient client = new CapServiceClient("WSHttpBinding_ICapService");
                client.Open();
                client.PublishCap(capAlert);
                client.Close();
            }
            finally
            {
            }
        }
    }
}
