using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;
using System.Xml.Serialization;

namespace com.ums.pas.integration
{

    #region Alert
    [Serializable]
    [Flags]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum AdrTypes
    {
        NOPHONE_PRIVATE = 1 << 0,
        NOPHONE_COMPANY = 1 << 1,
        FIXED_PRIVATE = 1 << 2,
        FIXED_COMPANY = 1 << 3,
        MOBILE_PRIVATE = 1 << 4,
        MOBILE_COMPANY = 1 << 5,
        MOVED_PRIVATE = 1 << 6,
        MOVED_COMPANY = 1 << 7,
        LBA_TEXT = 1 << 8,
        LBA_VOICE = 1 << 9,
        SMS_PRIVATE = 1 << 10,
        SMS_COMPANY = 1 << 11,
        SMS_PRIVATE_ALT_FIXED = 1 << 12,
        SMS_COMPANY_ALT_FIXED = 1 << 13,
        FIXED_PRIVATE_ALT_SMS = 1 << 14,
        FIXED_COMPANY_ALT_SMS = 1 << 15,
        MOBILE_PRIVATE_AND_FIXED = 1 << 16,
        MOBILE_COMPANY_AND_FIXED = 1 << 17,
        FIXED_PRIVATE_AND_MOBILE = 1 << 18,
        FIXED_COMPANY_AND_MOBILE = 1 << 19,
        SENDTO_TAS_SMS = 1 << 20,
        ONLY_VULNERABLE_CITIZENS = 1 << 21,
        SENDTO_USE_NOFAX_COMPANY = 1 << 27,
        SENDTO_USE_NOFAX_DEPARTMENT = 1 << 28, //reserved for future use
        SENDTO_USE_NOFAX_GLOBAL = 1 << 29, //should always be off
        ONLY_HEAD_OF_HOUSEHOLD = 1 << 30,

    }

    /// <summary>
    /// Specifies to which channel to send
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum SendChannel
    {
        VOICE = 1,
        SMS = 2,
        EMAIL = 3,
        LBA = 4,
        TAS = 5,
    }

    /// <summary>
    /// Delivery status of each item in an alert
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum ItemDeliveryStatus
    {
        NOT_PROCESSED = -1,
        DELIVERED = 0,
        IN_PROGRESS = 1,
        FAILED = 2,
    }

    /// <summary>
    /// Overall status for an alert.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum AlertOverallStatus
    {
        NOT_PROCESSED = -1,
        IN_PROGRESS = 1,
        FINISHED = 0,
        FAILED = 2,
        SCHEDULED = 3,
    }

    /// <summary>
    /// Endpoint is a common class for ways of receive messages.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    [XmlInclude(typeof(Phone))]
    public abstract class Endpoint
    {
        private String _address;

        public String Address
        {
            get { return _address; }
            set { _address = value; }
        }
    }

    /// <summary>
    /// Phone number that may be capable of receiving SMS
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Phone : Endpoint
    {
        private bool _canReceiveSms;

        public bool CanReceiveSms
        {
            get { return _canReceiveSms; }
            set { _canReceiveSms = value; }
        }
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class DataItem
    {
        public string Key;
        public string Value;
        public DataItem()
        {
        }
        public DataItem(string key, string value)
        {
            Key = key;
            Value = value;
        }
    }

    /// <summary>
    /// Enumeration of standard attribute-keys to be set on an alert target.
    /// You may set other attribute-keys /values as well, but these are standardized
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum AlertTargetAttributeKey
    {
        NAME,
        SURNAME,
        EXTID,
        MUNICIPALID,
        STREETID,
        HOUSENO,
        LETTER,

    }

    /// <summary>
    /// Common class of specifying a target for alerting.
    /// This may be a recipient, houses or areas.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    [XmlInclude(typeof(AlertObject))]
    [XmlInclude(typeof(StreetAddress))]
    [XmlInclude(typeof(PropertyAddress))]
    [XmlInclude(typeof(OwnerAddress))]
    public abstract class AlertTarget
    {


        private List<DataItem> _attributes;

        public List<DataItem> Attributes
        {
            get { return _attributes; }
            set { _attributes = value; }
        }
    }


    public class AlertTargetFactory
    {
        public static StreetAddress newStreetAddress(StreetAddress s)
        {
            return new StreetAddress(s);
        }
        public static StreetAddress newStreetAddress(String municipalCode, int streetNo, int houseNo, String letter, String oppgang)
        {
            return new StreetAddress(municipalCode, streetNo, houseNo, letter, oppgang);
        }
        public static PropertyAddress newPropertyAddress(PropertyAddress p)
        {
            return new PropertyAddress(p);
        }
        public static PropertyAddress newPropertyAddress(String municipalCode, int gnr, int bnr, int fnr, int snr)
        {
            return new PropertyAddress(municipalCode, gnr, bnr, fnr, snr);
        }
        public static StoredAddress newStoredAddress(StoredAddress s)
        {
            return new StoredAddress();
        }
        public static StoredList newStoredList(StoredList s)
        {
            return new StoredList();
        }
        public static AlertObject newAlertObject(String Name, String ExternalId, String PhoneNumber, Boolean CanReceiveSms)
        {
            return new AlertObject(Name, ExternalId, PhoneNumber, CanReceiveSms);
        }

    }


    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertObject : AlertTarget
    {

        private List<Endpoint> _endpoints = new List<Endpoint>();

        public List<Endpoint> Endpoints
        {
            get { return _endpoints; }
            set { _endpoints = value; }
        }


        private String _name;

        public String Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private String _externalId;

        public String ExternalId
        {
            get { return _externalId; }
            set { _externalId = value; }
        }


        public AlertObject()
        {
        }
        public AlertObject(String Name, String ExternalId, Phone Phone) 
            : this(Name, ExternalId, Phone.Address, Phone.CanReceiveSms)
        {
            this.Name = Name;
            this.ExternalId = ExternalId;
            Endpoints.Add(Phone);
            //this.Phone = Phone;
        }
        public AlertObject(String Name, String ExternalId, String PhoneNumber, Boolean CanReceiveSms)
        {
            this.Name = Name;
            this.ExternalId = ExternalId;
            Endpoints.Add(new Phone()
            {
                Address = PhoneNumber,
                CanReceiveSms = CanReceiveSms,
            });
        }
    }


    /// <summary>
    /// Street Addresses are identifiable by specifying municipal, streetnumber, housenumber and a letter.
    /// This AlertTarget results in a list of recipients living on this address.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class StreetAddress : AlertTarget
    {
        public StreetAddress()
        {
        }
        public StreetAddress(StreetAddress s)
            : this(s.MunicipalCode, s.StreetNo, s.HouseNo, s.Letter, s.Oppgang)
        {
            this.Attributes = s.Attributes;
        }
        public StreetAddress(String municipalCode, int streetNo, int houseNo, String letter, String oppgang)
        {
            this.MunicipalCode = municipalCode;
            this.StreetNo = streetNo;
            this.HouseNo = houseNo;
            this.Letter = letter;
            this.Oppgang = oppgang;
        }
        private String _municipalCode;

        public String MunicipalCode
        {
            get { return _municipalCode; }
            set { _municipalCode = value; }
        }
        private int _streetNo;

        public int StreetNo
        {
            get { return _streetNo; }
            set { _streetNo = value; }
        }
        private int _houseNo;

        public int HouseNo
        {
            get { return _houseNo; }
            set { _houseNo = value; }
        }
        private String _letter;

        public String Letter
        {
            get { return _letter == null ? "" : _letter; }
            set { _letter = value; }
        }

        private String _oppgang;

        public String Oppgang
        {
            get { return _oppgang; }
            set { _oppgang = value; }
        }


    }

    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertTargetEndpoints
    {
        private AlertTarget _alertTarget;

        public AlertTarget AlertTarget
        {
            get { return _alertTarget; }
            set { _alertTarget = value; }
        }

        private List<Endpoint> _endPoints;

        public List<Endpoint> EndPoints
        {
            get { return _endPoints; }
            set { _endPoints = value; }
        }


    }


    /// <summary>
    /// Properties/buildings are specified by municipal, bruksnr, gårdsnr, festenr and seksjonsnr.
    /// This AlertTarget results in a list of recipients living on this property.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class PropertyAddress : AlertTarget
    {
        public PropertyAddress()
        {
        }
        public PropertyAddress(PropertyAddress p)
            : this(p.MunicipalCode, p.Gnr, p.Bnr, p.Fnr, p.Snr)
        {
            this.Attributes = p.Attributes;
        }
        public PropertyAddress(String municipalCode, int gnr, int bnr, int fnr, int snr)
        {
            this.MunicipalCode = municipalCode;
            this.Gnr = gnr;
            this.Bnr = bnr;
            this.Fnr = fnr;
            this.Snr = snr;
        }
        private String _municipalCode;

        public String MunicipalCode
        {
            get { return _municipalCode; }
            set { _municipalCode = value; }
        }

        private int _gnr;

        public int Gnr
        {
            get { return _gnr; }
            set { _gnr = value; }
        }
        private int _bnr;

        public int Bnr
        {
            get { return _bnr; }
            set { _bnr = value; }
        }
        private int _fnr;

        public int Fnr
        {
            get { return _fnr; }
            set { _fnr = value; }
        }
        private int _snr;

        public int Snr
        {
            get { return _snr; }
            set { _snr = value; }
        }

    }

    /// <summary>
    /// Norway specific way of describing ownership category
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum NorwayEierKategoriKode
    {
        IKKE_DEFINERT = ' ',
        AKSJESELSKAP = 'A',
        BORETTSLAG = 'B',
        ANSVARLIG_SELSKAP = 'D',
        ENKELTPERSON = 'E',
        FYLKESKOMMUNEN = 'F',
        ANNEN_EIENDOM = 'G',
        KOMMUNEN = 'K',
        LEGAT = 'L',
        BRUKSRETTHAVER = 'R',
        STATEN = 'S',
        UTENLANDSK = 'W',
        ANNEN_EIERTYPE = 'X',
    }

    /// <summary>
    /// Norway specific way of describing a property owner's personal status
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum NorwayEierStatusKode
    {
        IKKE_DEFINERT = ' ',
        ANNULERT = 'A',
        BOSATT_I_NORGE = 'B',
        DOD = 'D',
        FORSVUNNET = 'F',
        IKKE_BOSATT = 'I',
        AKTIV_PERSON_MED_DNR = 'K',
        FODSELSREGISTRERT = 'R',
        SAVNET_PERSON_MED_DNR = 'S',
        UTVANDRET = 'U',
        UKJENT_PERSON = 'X',
    }

    /// <summary>
    /// Norway specific way of describing the instance that owns property
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public enum NorwayEierIdKode
    {
        FYSISK_PERSON = 'F',
        JURIDISK_PERSON = 'S',
        ANNEN_PERSON = 'N',
        /// <summary>
        /// Used if no rights/access of using social security number
        /// </summary>
        UNIKID = 'X',
    }


    /// <summary>
    /// Used for specifying an owner of a property.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class OwnerAddress : AlertTarget
    {
        private NorwayEierKategoriKode _eierKategoriKode;
        private NorwayEierStatusKode _eierStatusKode;
        private NorwayEierIdKode _eierIdKode;

        private int _eierId;
        private String _navn;
        private int _postnr;
        private String _adresselinje1;
        private String _adresselinje2;
        private String _adresselinje3;

        public NorwayEierIdKode EierIdKode
        {
            get { return _eierIdKode; }
            set { _eierIdKode = value; }
        }

        public String Adresselinje3
        {
            get { return _adresselinje3; }
            set { _adresselinje3 = value; }
        }


        public String Adresselinje2
        {
            get { return _adresselinje2; }
            set { _adresselinje2 = value; }
        }


        public String Adresselinje1
        {
            get { return _adresselinje1; }
            set { _adresselinje1 = value; }
        }


        public int Postnr
        {
            get { return _postnr; }
            set { _postnr = value; }
        }


        public String Navn
        {
            get { return _navn; }
            set { _navn = value; }
        }


        public int EierId
        {
            get { return _eierId; }
            set { _eierId = value; }
        }


        public NorwayEierStatusKode EierStatusKode
        {
            get { return _eierStatusKode; }
            set { _eierStatusKode = value; }
        }



        public NorwayEierKategoriKode EierKategoriKode
        {
            get { return _eierKategoriKode; }
            set { _eierKategoriKode = value; }
        }

    }

/*    /// <summary>
    /// This AlertTarget is a person with x endpoints to be reached
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Recipient : AlertTarget
    {
        private List<Endpoint> _endPoints;

        public List<Endpoint> EndPoints
        {
            get { return _endPoints; }
            set { _endPoints = value; }
        }

    }*/

    /// <summary>
    /// Specify an ivrCode to the stored list for alerting.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class StoredList : AlertTarget
    {
        private int _ivrCode;

        public int IvrCode
        {
            get { return _ivrCode; }
            set { _ivrCode = value; }
        }

    }

    /// <summary>
    /// Specify address pointer for alerting.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class StoredAddress : AlertTarget
    {
        private long _addressPk;

        public long AddressPk
        {
            get { return _addressPk; }
            set { _addressPk = value; }
        }

    }


    /// <summary>
    /// Specify common for the alert, for all channels.
    /// Should be singleton for each alert.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertConfiguration
    {
        private String _alertName;

        public String AlertName
        {
            get { return _alertName; }
            set { _alertName = value; }
        }


        private bool _startImmediately;

        public bool StartImmediately
        {
            get { return _startImmediately; }
            set { _startImmediately = value; }
        }
        private DateTime _scheduled;

        public DateTime Scheduled
        {
            get { return _scheduled; }
            set { _scheduled = value; }
        }

        private bool _simulationMode;

        public bool SimulationMode
        {
            get { return _simulationMode; }
            set { _simulationMode = value; }
        }

        private bool _sendToAllChannels;

        public bool SendToAllChannels
        {
            get { return _sendToAllChannels; }
            set { _sendToAllChannels = value; }
        }

    }



    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    [XmlInclude(typeof(VoiceConfiguration))]
    [XmlInclude(typeof(SmsConfiguration))]
    public abstract class ChannelConfiguration
    {
        private String _baseMessageContent;

        public String BaseMessageContent
        {
            get { return _baseMessageContent; }
            set { _baseMessageContent = value; }
        }
    }

    public class ChannelConfigurationFactory
    {
        /// <summary>
        /// Create a configuration for voice.
        /// If originAddress is empty and not useHiddenOriginAddress, the
        /// system should select a default number based on department settings.
        /// </summary>
        /// <param name="repeats"></param>
        /// <param name="frequencyMinutes"></param>
        /// <param name="pauseAtTime"></param>
        /// <param name="pauseDurationMinutes"></param>
        /// <param name="validDays"></param>
        /// <param name="useDefaultVoiceProfile"></param>
        /// <param name="voiceProfilePk"></param>
        /// <param name="useHiddenOriginAddress"></param>
        /// <param name="originAddress"></param>
        /// <param name="messageContent"></param>
        /// <returns></returns>
        public static VoiceConfiguration newVoiceConfiguration(int repeats, int frequencyMinutes, int pauseAtTime,
                                        int pauseDurationMinutes, int validDays, bool useDefaultVoiceProfile,
                                        int voiceProfilePk, bool useHiddenOriginAddress,
                                        String originAddress, String messageContent)
        {
            VoiceConfiguration voiceConfiguration = new VoiceConfiguration();
            voiceConfiguration.Repeats = repeats;
            voiceConfiguration.FrequencyMinutes = frequencyMinutes;
            voiceConfiguration.PauseAtTime = pauseAtTime;
            voiceConfiguration.PauseDurationMinutes = pauseDurationMinutes;
            voiceConfiguration.ValidDays = validDays;
            voiceConfiguration.UseDefaultVoiceProfile = useDefaultVoiceProfile;
            voiceConfiguration.VoiceProfilePk = voiceProfilePk;
            voiceConfiguration.UseHiddenOriginAddress = useHiddenOriginAddress;
            voiceConfiguration.OriginAddress = originAddress;
            voiceConfiguration.BaseMessageContent = messageContent;
            return voiceConfiguration;
        }
        public static SmsConfiguration newSmsConfiguration(String originAddress, String messageContent, bool flashSms)
        {
            SmsConfiguration smsConfiguration = new SmsConfiguration();
            smsConfiguration.OriginAddress = originAddress;
            smsConfiguration.BaseMessageContent = messageContent;
            smsConfiguration.FlashSms = flashSms;
            return smsConfiguration;
        }
    }

    /// <summary>
    /// Class for storing properties related to a voice alert
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class VoiceConfiguration : ChannelConfiguration
    {
        private int _repeats;

        public int Repeats
        {
            get { return _repeats; }
            set { _repeats = value; }
        }

        private int _frequencyMinutes;

        public int FrequencyMinutes
        {
            get { return _frequencyMinutes; }
            set { _frequencyMinutes = value; }
        }

        private int _pauseAtTime;

        public int PauseAtTime
        {
            get { return _pauseAtTime; }
            set { _pauseAtTime = value; }
        }

        private int _pauseDurationMinutes;

        public int PauseDurationMinutes
        {
            get { return _pauseDurationMinutes; }
            set { _pauseDurationMinutes = value; }
        }

        private int _validDays;

        public int ValidDays
        {
            get { return _validDays; }
            set { _validDays = value; }
        }

        private bool _useDefaultVoiceProfile;

        public bool UseDefaultVoiceProfile
        {
            get { return _useDefaultVoiceProfile; }
            set { _useDefaultVoiceProfile = value; }
        }

        private int _voiceProfilePk;

        public int VoiceProfilePk
        {
            get { return _voiceProfilePk; }
            set { _voiceProfilePk = value; }
        }

        private bool _useHiddenOriginAddress;

        public bool UseHiddenOriginAddress
        {
            get { return _useHiddenOriginAddress; }
            set { _useHiddenOriginAddress = value; }
        }

        private String _originAddress;

        public String OriginAddress
        {
            get { return _originAddress; }
            set { _originAddress = value; }
        }

    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class SmsConfiguration : ChannelConfiguration
    {
        public bool FlashSms = false;
        public String OriginAddress;
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertId
    {
        public AlertId()
        {
        }
        public AlertId(long projectPk)
        {
            this.Id = projectPk;
        }
        private long _id;

        public long Id
        {
            get { return _id; }
            set { _id = value; }
        }

    }

    /// <summary>
    /// Contains summary of a previously sent alert.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertSummary
    {
        private AlertId _alertId;

        public AlertId AlertId
        {
            get { return _alertId; }
            set { _alertId = value; }
        }
        private String _title;

        public String Title
        {
            get { return _title; }
            set { _title = value; }
        }

        private AlertOverallStatus _progressStatus;

        public AlertOverallStatus ProgressStatus
        {
            get { return _progressStatus; }
            set { _progressStatus = value; }
        }
        

        private String _status;

        public String Status
        {
            get { return _status; }
            set { _status = value; }
        }
        private DateTime _startDateTime;

        public DateTime StartDateTime
        {
            get { return _startDateTime; }
            set { _startDateTime = value; }
        }
        private Boolean _exercise;

        public Boolean Exercise
        {
            get { return _exercise; }
            set { _exercise = value; }
        }
    }

    #endregion Alert

    #region Log
    /// <summary>
    /// Log/Status summary of a specific alert.
    /// Extension of the AlertSummary. Also containing overall status/log details.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class LogSummary : AlertSummary
    {
        private int _voiceAnswered;

        public int VoiceAnswered
        {
            get { return _voiceAnswered; }
            set { _voiceAnswered = value; }
        }
        private int _voiceUnanswered;

        public int VoiceUnanswered
        {
            get { return _voiceUnanswered; }
            set { _voiceUnanswered = value; }
        }
        private int _voiceConfirmed;

        public int VoiceConfirmed
        {
            get { return _voiceConfirmed; }
            set { _voiceConfirmed = value; }
        }
        private int _voiceTotal;

        public int VoiceTotal
        {
            get { return _voiceTotal; }
            set { _voiceTotal = value; }
        }
        private int _smsSent;

        public int SmsSent
        {
            get { return _smsSent; }
            set { _smsSent = value; }
        }
        private int _smsReceived;

        public int SmsReceived
        {
            get { return _smsReceived; }
            set { _smsReceived = value; }
        }
        private int _smsTotal;

        public int SmsTotal
        {
            get { return _smsTotal; }
            set { _smsTotal = value; }
        }
        private List<LogLine> _errors;

        public List<LogLine> Errors
        {
            get { return _errors; }
            set { _errors = value; }
        }

        
    }


    /// <summary>
    /// Class containing information about an AlertTarget that didn't result in an Endpoint (Phone)
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class LogLineNotFound
    {
        private String _name;

        /// <summary>
        /// The name of person or organization
        /// </summary>
        public String Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private String _externalId;

        public String ExternalId
        {
            get { return _externalId; }
            set { _externalId = value; }
        }


        private AlertTarget _requestedAlertTarget;

        /// <summary>
        /// The requested alert target that didn't result in an Endpoint (phone number)
        /// </summary>
        public AlertTarget RequestedAlertTarget
        {
            get { return _requestedAlertTarget; }
            set { _requestedAlertTarget = value; }
        }

    }

    /// <summary>
    /// Detailed Log/status per recipient.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class LogLineDetailed
    {
        private String _name;

        public String Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private Endpoint _endpoint;

        public Endpoint Endpoint
        {
            get { return _endpoint; }
            set { _endpoint = value; }
        }

        private DateTime _dateTime;

        public DateTime DateTime
        {
            get { return _dateTime; }
            set { _dateTime = value; }
        }

        private int _status;

        public int Status
        {
            get { return _status; }
            set { _status = value; }
        }
        private String _externalId;

        public String ExternalId
        {
            get { return _externalId; }
            set { _externalId = value; }
        }

        private AlertObject _alertObject;

        public AlertObject AlertObject
        {
            get { return _alertObject; }
            set { _alertObject = value; }
        }

                
    }

    /// <summary>
    /// Single object from Log, also containing alert information.
    /// Used when searching for specific persons and numbers
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class LogObject
    {
        private String _name;

        public String Name
        {
            get { return _name; }
            set { _name = value; }
        }
        private String _externalId;

        public String ExternalId
        {
            get { return _externalId; }
            set { _externalId = value; }
        }
        private String _phoneNumber;

        public String PhoneNumber
        {
            get { return _phoneNumber; }
            set { _phoneNumber = value; }
        }
        private DateTime _dateTime;

        public DateTime DateTime
        {
            get { return _dateTime; }
            set { _dateTime = value; }
        }
        private int _statusCode;

        public int StatusCode
        {
            get { return _statusCode; }
            set { _statusCode = value; }
        }

        private AlertId _alertId;

        public AlertId AlertId
        {
            get { return _alertId; }
            set { _alertId = value; }
        }

        private String _alertTitle;

        public String AlertTitle
        {
            get { return _alertTitle; }
            set { _alertTitle = value; }
        }

        private String _alertMessage;

        public String AlertMessage
        {
            get { return _alertMessage; }
            set { _alertMessage = value; }
        }

        private List<AlertTarget> _alertTargets;

        public List<AlertTarget> AlertTargets
        {
            get { return _alertTargets; }
            set { _alertTargets = value; }
        }

    }

    /// <summary>
    /// Log line specifies an Endpoint and it's status.
    /// It does not specify who owns the Endpoint (Recipient)
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class LogLine
    {
        private Endpoint _endpoint;

        public Endpoint EndPoint
        {
            get { return _endpoint; }
            set { _endpoint = value; }
        }

        private int _statusCode;

        public int StatusCode
        {
            get { return _statusCode; }
            set { _statusCode = value; }
        }
        private String _statusText;

        public String StatusText
        {
            get { return _statusText; }
            set { _statusText = value; }
        }

    }

    #endregion Log


    #region Responses
    /// <summary>
    /// Base class for responses.
    /// </summary>
    /// 
    /// <typeparam name="T">Defined when overriding for fluent interface of common setters</typeparam>
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class GenericResponse<T> where T : GenericResponse<T>
    {
        private int _code;

        public int Code
        {
            get { return _code; }
            set { _code = value; }
        }
        private String _message;

        public String Message
        {
            get { return _message; }
            set { _message = value; }
        }

        public T SetCode(int code)
        {
            this.Code = code;
            return (T) this;
        }
        public T SetMessage(String message)
        {
            this.Message = message;
            return (T) this;
        }

    }

    /// <summary>
    /// Response with code and text, default for put/post operations.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class DefaultResponse : GenericResponse<DefaultResponse>
    {

    }


    /// <summary>
    /// Response object with code and text for return when generating alerts.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertResponse : GenericResponse<AlertResponse>
    {
        public String Description;
        public AlertId AlertId;
        public String QueueId;
        public AlertResponse SetQueueId(String queueId)
        {
            this.QueueId = queueId;
            return this;
        }
        public AlertResponse SetAlertId(AlertId alertId)
        {
            this.AlertId = alertId;
            return this;
        }
        public AlertResponse SetDescription(String description)
        {
            this.Description = description;
            return this;
        }
    }



    /// <summary>
    /// Factory for creating return values for Alert.
    /// Generates AlertResponse objects
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertResponseFactory
    {
        public static AlertResponse Ok(AlertId projectPk, String queueId)
        {
            return new AlertResponse()
                .SetCode(0)
                .SetMessage("OK")
                .SetAlertId(projectPk)
                .SetQueueId(queueId);
        }

        public static AlertResponse Failed(int code, String description, params object[] args)
        {
            return Failed(code, String.Format(description, args));
        }
        public static AlertResponse Failed(int code, String description)
        {
            return new AlertResponse()
                .SetCode(code)
                .SetMessage("FAILED")
                .SetDescription(description)
                .SetAlertId(new AlertId(-1));
        }
    }
    #endregion Responses

    #region Div

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AccountDetails
    {
        public int Comppk { get; set; }
        public int Deptpk { get; set; }
        public int DeptPri { get; set; }
        public int PrimarySmsServer { get; set; }
        public int SecondarySmsServer { get; set; }
        public String StdCc { get; set; }
        public int MaxVoiceChannels { get; set; }
        public List<String> AvailableVoiceNumbers { get; set; }
        public int DefaultTtsLang { get; set; }
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class Account
    {
        private String _companyId;

        [XmlElement(IsNullable = false)]
        public String CompanyId
        {
            get { return _companyId; }
            set { _companyId = value; }
        }
        private String  _password;

        [XmlElement(IsNullable = false)]
        public String  Password
        {
            get { return _password; }
            set { _password = value; }
        }
        private String _departmentId;

        [XmlElement(IsNullable = false)]
        public String DepartmentId
        {
            get { return _departmentId; }
            set { _departmentId = value; }
        }
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public static class StringHelpers
    {
        /// <summary>
        /// Extension of ToString that will list all properties within a class with format "prop: value newline"
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public static string ToStringExtension(this object obj)
        {
            StringBuilder sb = new StringBuilder();
            foreach (FieldInfo property in obj.GetType().GetFields())
            {
                try
                {
                    sb.Append(property.Name);
                    sb.Append(": ");
                    {
                        sb.Append(property.GetValue(obj).ToString());
                    }

                }
                catch (Exception e)
                {
                    sb.Append("Unable to append property.GetValue(obj)");
                    sb.Append(System.Environment.NewLine);
                    sb.Append(e);
                }
                sb.Append(System.Environment.NewLine);
            }

            return sb.ToString();
        }
    }

    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class TtsLanguage
    {
        private int _langId;

        public int LangId
        {
            get { return _langId; }
            set { _langId = value; }
        }
        private String _name;

        public String Name
        {
            get { return _name; }
            set { _name = value; }
        }

    }

    #endregion Div


    #region ActiveMq
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class AlertMqPayload
    {
        public AlertId AlertId;
        public Account Account = new Account();
        public AccountDetails AccountDetails = new AccountDetails();
        public AlertConfiguration AlertConfiguration = new AlertConfiguration();
        public List<AlertTarget> AlertTargets = new List<AlertTarget>();
        public List<ChannelConfiguration> ChannelConfigurations = new List<ChannelConfiguration>();
    }
    #endregion

    #region Templates


    /// <summary>
    /// Id for a MessageTemplate
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class MessageTemplateId
    {
        private long _id;

        public long Id
        {
            get { return _id; }
            set { _id = value; }
        }
    }


    /// <summary>
    /// Message template item for listing, containing id and title.
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class MessageTemplateListItem
    {
        private MessageTemplateId _templateId;

        public MessageTemplateId TemplateId
        {
            get { return _templateId; }
            set { _templateId = value; }
        }
        private String _title;

        public String Title
        {
            get { return _title; }
            set { _title = value; }
        }

    }

    /// <summary>
    /// Message template, title and a content.
    /// Override the list-version MessageTemplateListItem
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class MessageTemplate : MessageTemplateListItem
    {
        private String _messageText;

        public String MessageText
        {
            get { return _messageText; }
            set { _messageText = value; }
        }

        
    }

    /// <summary>
    /// Response object when creating or altering a MessageTemplate
    /// </summary>
    [Serializable]
    [XmlType(Namespace = "http://ums.no/ws/integration")]
    public class MessageTemplateResponse : GenericResponse<MessageTemplateResponse>
    {
        private MessageTemplateId _templateId;

        public MessageTemplateId TemplateId
        {
            get { return _templateId; }
            set { _templateId = value; }
        }

    }

    #endregion Templates
}
