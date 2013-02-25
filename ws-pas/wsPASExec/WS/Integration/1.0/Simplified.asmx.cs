using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.pas.integration;

namespace com.ums.ws.powel
{
    /// <summary>
    /// Summary description for Powel
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Powel : System.Web.Services.WebService
    {

        [WebMethod]
        public AlertResponse StartAlert(String UserId, String Title, String Message, Boolean StartImmediately, DateTime DateTime, 
            Int32 Repeats, Int32 Frequency, Boolean Exercise, List<StreetAddress> StreetAddresses, 
            List<PropertyAddress> PropertyAddresses, List<AlertTarget> Alert)
        {
            throw new NotImplementedException();
        }
        [WebMethod]
        public AlertResponse StartFollowUpAlert(Account account, long projectPk)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public AlertResponse StartTestAlert(Account account, List<AlertTarget> alertTargets, List<ChannelConfiguration> channelConfigurations)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public void StopAlert(Account account, long projectPk)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public 
    }
}
