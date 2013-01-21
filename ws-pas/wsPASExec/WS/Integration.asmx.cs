using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Collections;
using com.ums.UmsCommon;

namespace com.ums.ws.powel
{
    /// <summary>
    /// external PARM Alert and Event execution
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/powel/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Powel : System.Web.Services.WebService
    {
        /// <summary>
        /// Endpoint is a common class for ways of receive messages.
        /// </summary>
        public abstract class Endpoint
        {
            public String address;
        }

        /// <summary>
        /// Phone number that may be capable of receiving SMS
        /// </summary>
        public class Phone : Endpoint
        {
            public bool canReceiveSms;
        }

        /// <summary>
        /// Common class of specifying a target for alerting.
        /// This may be a recipient, houses or areas.
        /// </summary>
        public abstract class AlertTarget
        {
            public Dictionary<String, String> properties;
        }

        /// <summary>
        /// Street Addresses are identifiable by specifying municipal, streetnumber, housenumber and a letter.
        /// This AlertTarget results in a list of recipients living on this address.
        /// </summary>
        public class StreetAddress : AlertTarget
        {
            public String municipalcode;
            public int streetno;
            public int houseno;
            public String letter;
        }

        /// <summary>
        /// Properties/buildings are specified by municipal, bruksnr, gårdsnr, festenr and seksjonsnr.
        /// This AlertTarget results in a list of recipients living on this property.
        /// </summary>
        public class PropertyAddress : AlertTarget
        {
            public String municipalcode;
            public int bnr;
            public int gnr;
            public int fnr;
            public int snr;
        }

        /// <summary>
        /// This AlertTarget is a person with x endpoints to be reached
        /// </summary>
        public class Recipient : AlertTarget
        {
            public List<Endpoint> endPoints;
        }

        /// <summary>
        /// Specify an ivrCode to the stored list for alerting.
        /// </summary>
        public class StoredList : AlertTarget
        {
            public int ivrCode;
        }

        /// <summary>
        /// Specify address pointer for alerting.
        /// </summary>
        public class StoredAddress : AlertTarget
        {
            public long addressPk;
        }

         
        /// <summary>
        /// Specify common for the alert, for all channels.
        /// 
        /// </summary>
        public class AlertConfiguration
        {
            public bool startImmediately;
            public DateTime scheduled;
            public bool simulationMode;
            public ADRTYPES sendTo;
        }

        public abstract class ChannelConfiguration
        {
        }

        public class VoiceConfiguration : ChannelConfiguration
        {
            public int repeats;
            public int frequency;
            public int pauseAtTime;
            public int pauseDuration;
            public int validDays;
            public bool useDefaultVoiceProfile;
            public int voiceProfile;
            public bool useHiddenOriginAddress;
            public String originAddress;
            public String messageContent;

        }
        public class SmsConfiguration : ChannelConfiguration
        {
            public bool flashSms;
            public String originAddress;
            public String messageContent;
        }

        public class Response
        {
            public int code;
            public String message;
            public long alertProjectPk;
        }

        public class Account
        {
            public String userId;
            public String companyId;
            public String password;
            public String departmentId;
        }


        [WebMethod]
        public Response powelAlert(Account account, 
            List<AlertTarget> alertTargets, 
            List<ChannelConfiguration> channelConfigurations
            )
        {
            return new Response();
        }
    }
}
