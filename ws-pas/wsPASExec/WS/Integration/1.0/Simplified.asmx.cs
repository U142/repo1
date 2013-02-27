using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.pas.integration;
using System.Xml.Serialization;

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
        [XmlInclude(typeof(Phone))]

        [WebMethod]
        public AlertResponse StartAlert(Account account,
                                        String Title,
                                        String Message, 
                                        Boolean StartImmediately, 
                                        DateTime DateTime, 
                                        Int32 Repeats, 
                                        Int32 Frequency, 
                                        Boolean Exercise, 
                                        List<StreetAddress> StreetAddresses,
                                        List<PropertyAddress> PropertyAddresses, 
                                        List<AlertObject> Alert)
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
        public DefaultResponse StopAlert(Account Account, AlertId ProjectPk)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<LogSummary> GetAlertLog(Account Account, AlertId AlertId)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<LogLineDetailed> GetAlertObjectLog(Account Account, AlertId AlertId, int StatusCodeFilter, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public List<LogLineNotFound> GetAlertNumberNotFoundLog(Account Account, AlertId AlertId, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Search for a specific recipient, either by name or number
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="SearchText">Search by name or number</param>
        /// <param name="StartIndex">Start at index</param>
        /// <param name="PageSize">Number of rows</param>
        [WebMethod]
        public List<LogObject> GetObjectLog(Account Account, String SearchText, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Search for a list of endpoints (phonenumbers, email etc) based on a list of AlertTargets.
        /// The alert targets used are StreetAddress or PropertyAddress
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="AlertTargets"></param>
        /// <returns></returns>
        [WebMethod]
        public List<AlertTargetEndpoints> GetEndpoints(Account Account, List<AlertTarget> AlertTargets)
        {
            throw new NotImplementedException();
        }

#region Templates
        [WebMethod]
        public void SaveTemplate(Account Account, TemplateId TemplateId, String Title, String MessageText)
        {
            throw new NotImplementedException();
        }

        [WebMethod]
        public void DeleteTemplate(Account Account, TemplateId TemplateId)
        {
            throw new NotImplementedException();
        }
    }
#endregion Templates
}
