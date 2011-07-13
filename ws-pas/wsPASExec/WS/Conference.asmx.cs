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
using System.Xml;
using System.Xml.Serialization;

using libums2_csharp;
using wsPASExec.VB;
using com.ums.UmsCommon;

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
        public int scheduleConference([XmlElement(IsNullable=false)] ACCOUNT account, Participant[] participants, string conferenceName, long scheduleddatetime)
        {
            try
            {
                // Should this return refno with item numbers?
                wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
                int l_refno = conf.scheduleConference(account, participants, conferenceName, scheduleddatetime);
                
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " successfully scheduled a conference with (l_refno=" + l_refno + ")", USysLog.UFACILITY.syslog, USysLog.ULEVEL.info);

                return l_refno;
            }
            catch (Exception e)
            {
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " scheduleConference failed " + e.Message + " Stack: " + e.StackTrace, USysLog.UFACILITY.syslog, USysLog.ULEVEL.err);
                throw e;
            }
        }

        [WebMethod(Description = "This method is used for starting a conference imidiately")]
        public int startConferenceNow(ACCOUNT account, Participant[] participant, string conferenceName)
        {
            try
            {
                int l_refno = scheduleConference(account, participant, conferenceName, 0);
                
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " successfully started a conference with (l_refno=" + l_refno + ")", USysLog.UFACILITY.syslog, USysLog.ULEVEL.info);
                
                return l_refno;

            }
            catch (Exception e)
            {
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " startConferenceNow failed " + e.Message + " Stack: " + e.StackTrace, USysLog.UFACILITY.syslog, USysLog.ULEVEL.err);
                throw e;
            }
        }

        [WebMethod(Description = "This method is currently not in use")]
        public string cancelConference(ACCOUNT account, int referenceNumber)
        {
            try
            {
                wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
                string message = conf.cancelConference(account, referenceNumber);
                
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " successfully cancelled conference with l_refno=" + referenceNumber, USysLog.UFACILITY.syslog, USysLog.ULEVEL.info);

                return message;
            }
            catch (Exception e)
            {
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " cancelConference failed " + e.Message + " Stack: " + e.StackTrace, USysLog.UFACILITY.syslog, USysLog.ULEVEL.err);
                throw e;
            }
        }

        [WebMethod(Description = "This method returns the status of a conference, shows the participants with their statuses")]
        public List<Status> getConferenceStatus(ACCOUNT account, int referenceNumber)
        {
            try
            {
                // Should this return refno with item numbers?
                // Actually there could be more than one here?
                wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
                List<Status> list = conf.getConference(account, referenceNumber);

                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " successfully returned conference status with l_refno=" + referenceNumber, USysLog.UFACILITY.syslog, USysLog.ULEVEL.info);

                return list;
            }
            catch (Exception e)
            {
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " getConferenceStatus failed " + e.Message + " Stack: " + e.StackTrace, USysLog.UFACILITY.syslog, USysLog.ULEVEL.err);
                throw e;
            }
        }

        [WebMethod(Description = "This method is used for redialing participants, use getConferenceStatus to get itemNumber for a participant")]
        public string redialParticipant(ACCOUNT account, int referenceNumber, int itemNumber)
        {
            try
            {
                wsPASExec.VB.Conference conf = new wsPASExec.VB.Conference();
                string message = conf.redialParticipant(account, referenceNumber, itemNumber);

                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " successfully redialed l_refno " + referenceNumber + " l_item " + itemNumber, USysLog.UFACILITY.syslog, USysLog.ULEVEL.info);

                return message;
            }
            catch (Exception e)
            {
                UCommon.appname = "soap/conference-1.1";
                USysLog.send(Environment.MachineName + " soap/conference-1.1: client " + account.Company + "/" + account.Department + " from " + HttpContext.Current.Request.UserHostAddress + " redialParticipant failed " + e.Message + " Stack: " + e.StackTrace, USysLog.UFACILITY.syslog, USysLog.ULEVEL.err);
                throw e;
            }
        }
    }
}
