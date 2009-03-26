using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;

using System.Collections.Generic;

using libums2_csharp;
using wsPASExec.VB;

namespace wsPASExec.WS
{
    /// <summary>
    /// Summary description for Conference
    /// </summary>
    [WebService(Namespace = "http://no.ums/ws/vb", Description = "This is a service for scheduling and starting a telephone conference. To get an account and a password, contact the UMS Sales office on telephone +47 23501600, or email us on info@ums.no")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Conference : System.Web.Services.WebService
    {

        [WebMethod(Description="This method is used for scheduling a conference")]
        public int scheduleConference(ACCOUNT account, Participant[] participants, string conferenceName, long scheduleddatetime)
        {
            // Should this return refno with item numbers?
            wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
            return conf.scheduleConference(account, participants, conferenceName, scheduleddatetime);
        }

        [WebMethod(Description = "This method is used for starting a conference imidiately")]
        public int startConferenceNow(ACCOUNT account, Participant[] participant, string conferenceName)
        {
            return scheduleConference(account, participant, conferenceName, 0);
        }

        [WebMethod(Description = "This method is currently not in use")]
        public string cancelConference(ACCOUNT account, int referenceNumber)
        {
            return null;
        }

        [WebMethod(Description = "This method returns the status of a conference, shows the participants with their statuses")]
        public List<Status> getConferenceStatus(ACCOUNT account, int referenceNumber)
        {
            // Should this return refno with item numbers?
            // Actually there could be more than one here?
            wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
            return conf.getConference(account, referenceNumber);
        }

        [WebMethod(Description = "This method is used for redialing participants, use getConferenceStatus to get itemNumber for a participant")]
        public string redialParticipant(ACCOUNT account, int referenceNumber, int itemNumber)
        {
            wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
            return conf.redialParticipant(account, referenceNumber, itemNumber);
        }
    }
}
