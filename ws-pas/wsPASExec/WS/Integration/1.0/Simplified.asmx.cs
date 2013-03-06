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
        [XmlInclude(typeof(Recipient))]

        /// <summary>
        /// Alert based on street addresses and/or property addresses and/or list of alert objects (name/number).
        /// </summary>
        /// <param name="account">The account</param>
        /// <param name="Title">The alert title</param>
        /// <param name="SmsMessage">The message content for SMS</param>
        /// <param name="VoiceMessage">The message content for Voice Text-to-speech</param>
        /// <param name="StartImmediately">True if start now, overrides StartDateTime</param>
        /// <param name="StartDateTime">Depends on StartImmediately=false</param>
        /// <param name="Repeats">Number of repeats (only voice)</param>
        /// <param name="Frequency">Frequency in minutes between repeats</param>
        /// <param name="Exercise">If true, it's a simulated alert</param>
        /// <param name="AlertTargets">AlertTargets may consist of overridden classes as StreetAddress, PropertyAddress, OwnerAddress and AlertObject</param>
        /// <returns></returns>
        [WebMethod(Description = "<b>Alert based on street addresses and/or property addresses and/or list of alert objects (name/number).</b><br>AlertTargets may consist of overridden classes as StreetAddress, PropertyAddress, OwnerAddress and AlertObject")]
        [XmlInclude(typeof(StreetAddress))]
        [XmlInclude(typeof(PropertyAddress))]
        [XmlInclude(typeof(OwnerAddress))]
        [XmlInclude(typeof(AlertObject))]
        public AlertResponse StartAlert(Account account,
                                        String Title,
                                        String SmsMessage,
                                        String VoiceMessage,
                                        Boolean StartImmediately, 
                                        DateTime DateTime, 
                                        Int32 Repeats, 
                                        Int32 Frequency, 
                                        Boolean Exercise, 
                                        List<AlertTarget> AlertTargets)
        {
            AlertConfiguration alertConfiguration = new AlertConfiguration();
            alertConfiguration.AlertName = Title;
            alertConfiguration.Scheduled = DateTime;
            alertConfiguration.SendToAllChannels = true;
            alertConfiguration.SimulationMode = Exercise;
            alertConfiguration.StartImmediately = StartImmediately;
            List<ChannelConfiguration> channelConfigurations = new List<ChannelConfiguration>();

            //TODO - Get default voice origin number.
            channelConfigurations.Add(ChannelConfigurationFactory.newVoiceConfiguration(Repeats,
                                                                                        Frequency,
                                                                                        2200,
                                                                                        60 * 10,
                                                                                        7,
                                                                                        false,
                                                                                        1,
                                                                                        true,
                                                                                        "23500801",
                                                                                        VoiceMessage));
            //TODO - Get default SMS oadc
            channelConfigurations.Add(ChannelConfigurationFactory.newSmsConfiguration("Default",
                                                                                        SmsMessage,
                                                                                        false));


            return new Integration().StartAlert(account, alertConfiguration, channelConfigurations, AlertTargets);
        }

        /// <summary>
        /// Send new alert to same receivers as a previous sent alert.
        /// Message content may differ in SMS and Text-to-speech, may specify both indipendently.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="Title">The alert title</param>
        /// <param name="SmsMessage">The message content for SMS</param>
        /// <param name="VoiceMessage">The message content for Voice Text-to-speech</param>
        /// <param name="StartImmediately">True if start now, overrides StartDateTime</param>
        /// <param name="StartDateTime">Depends on StartImmediately=false</param>
        /// <param name="Repeats">Number of repeats (only voice)</param>
        /// <param name="Frequency">Frequency in minutes between repeats</param>
        /// <returns>An alert response</returns>
        [WebMethod(Description=@"<b>Send new alert to same receivers as a previous sent alert.</b><br>Message content may differ in SMS and Text-to-speech, may specify both indipendently.")]
        public AlertResponse StartFollowUpAlert(Account Account, String Title, String SmsMessage, String VoiceMessage, Boolean StartImmediately, DateTime StartDateTime,
            Int32 Repeats, Int32 Frequency)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Sends a test alert to a specified phone number.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="Message">The message content</param>
        /// <param name="PhoneNumber">The phone number for testing</param>
        /// <param name="SendTo">Send via specified channels</param>
        /// <returns></returns>
        [WebMethod(Description=@"<b>Sends a test alert to a specified phone number.</b>")]
        public AlertResponse StartTestAlert(Account Account, String Message, String PhoneNumber, SendChannel SendTo)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Abort alerting
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">The alert id to stop</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Abort alerting</b>")]
        public DefaultResponse StopAlert(Account Account, AlertId AlertId)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get array of previously sent alerts.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="StartIndex">Start at</param>
        /// <param name="PageSize">Number of rows</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get array of previously sent alerts.</b>")]
        public List<AlertSummary> GetAlerts(Account Account, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get status of a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">The alert id</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get status of a previously sent alert.</b>")]
        public List<LogSummary> GetAlertLog(Account Account, AlertId AlertId)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get object log for a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">The alert id</param>
        /// <param name="StatusCodeFilter">Status code for filtering</param>
        /// <param name="StartIndex">Start at</param>
        /// <param name="PageSize">Number of rows</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get object log for a previously sent alert.</b>")]
        public List<LogLineDetailed> GetAlertObjectLog(Account Account, AlertId AlertId, int StatusCodeFilter, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get log of addresses where no telephone number was found for a previously sent alert.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="AlertId">Alert ID</param>
        /// <param name="StartIndex">Start at</param>
        /// <param name="PageSize">Number of rows</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get log of addresses where no telephone number was found for a previously sent alert.</b>")]
        public List<LogLineNotFound> GetAlertNumberNotFoundLog(Account Account, AlertId AlertId, int StartIndex, int PageSize)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Search for a specific recipient in any alert, either by person name, org name or phone number
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="SearchText">Search by name or number</param>
        /// <param name="StartIndex">Start at index</param>
        /// <param name="PageSize">Number of rows</param>
        [WebMethod(Description = @"<b>Search for a specific recipient in any alert, either by person name, org name or phone number</b>")]
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
        [WebMethod(Description = @"<b>Search for a list of endpoints (phonenumbers, email etc) based on a list of AlertTargets.</b><br>The alert targets used are StreetAddress, PropertyAddress or OwnerAddress")]
        public List<AlertTargetEndpoints> GetEndpoints(Account Account, List<AlertTarget> AlertTargets)
        {
            throw new NotImplementedException();
        }

#region Templates
        /// <summary>
        /// Save a template
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="MessageTemplate"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Save a template</b>")]
        public MessageTemplateResponse SaveTemplate(Account Account, MessageTemplate MessageTemplate)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Delete a Message template
        /// </summary>
        /// <param name="Account"></param>
        /// <param name="TemplateId"></param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Delete a Message template</b>")]
        public MessageTemplateResponse DeleteTemplate(Account Account, MessageTemplateId TemplateId)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get a list of all message templates available for Account
        /// </summary>
        /// <param name="Account">The account</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get a list of all message templates available for Account</b>")]
        public List<MessageTemplateListItem> GetTemplates(Account Account)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Get a template from id.
        /// </summary>
        /// <param name="Account">The account</param>
        /// <param name="TemplateId">Template ID</param>
        /// <returns></returns>
        [WebMethod(Description = @"<b>Get a template from id.</b>")]
        public List<MessageTemplate> GetTemplate(Account Account, MessageTemplateId TemplateId)
        {
            throw new NotImplementedException();
        }
    
    }
#endregion Templates
}
