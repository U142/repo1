using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.pas.integration;

namespace com.ums.ws.integration
{
    /// <summary>
    /// Summary description for Powel
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/integration/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Simplified : System.Web.Services.WebService
    {
        [WebMethod]
        public AlertResponse Test()
        {
            return AlertResponseFactory.Ok(new AlertId(), "12345");
        }

        [WebMethod]
        public AlertResponse StartAlert(Account account, String Title, String Message, Boolean StartImmediately, DateTime DateTime, 
            Int32 Repeats, Int32 Frequency, Boolean Exercise, List<StreetAddress> StreetAddresses, 
            List<PropertyAddress> PropertyAddresses, List<AlertTarget> Alert)
        {
            throw new NotImplementedException();
        }
        [WebMethod]
        public AlertResponse StartFollowUpAlert(Account Account, String Title, String Message, Boolean StartImmediately, DateTime StartDateTime,
            Int32 Repeats, Int32 Frequency)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public AlertResponse StartTestAlert(Account Account, String Message, String PhoneNumber, SENDCHANNEL SendTo)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public void StopAlert(Account Account, long ProjectPk)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }
    }
}
