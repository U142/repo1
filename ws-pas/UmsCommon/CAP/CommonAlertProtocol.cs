using System;
using System.Xml;
using System.Xml.Serialization;
using System.Collections.Generic;

namespace com.ums.UmsCommon.CAP
{

    public enum CAP_MSGTYPE
    {
        Alert,
        Update,
        Cancel,
        Ack,
        Error,
    }
    public enum CAP_SCOPE
    {
        Public,
        Restricted,
        Private,
    }
    public enum CAP_STATUS
    {
        Actual,
        Exercise,
        System,
        Test,
        Draft,
    }
    public enum CAP_CATEGORY
    {
        Geo,
        Met,
        Safety,
        Security,
        Rescue,
        Fire,
        Health,
        Evn,
        Transport,
        Infra,
        CBRNE,
        Other,
    }
    public enum CAP_RESPONSETYPE
    {
        Shelter,
        Evacuate,
        Prepare,
        Execute,
        Avoid,
        Monitor,
        Assess,
        AllClear,
        None,
    }
    public enum CAP_URGENCY
    {
        Immediate,
        Expected,
        Future,
        Past,
        Unknown,
    }
    public enum CAP_SEVERITY
    {
        Extreme,
        Severe,
        Moderate,
        Minor,
        Unknown,
    }
    public enum CAP_CERTAINTY
    {
        Observed,
        Likely,
        Possible,
        Unlikely,
        Unknown,
    }
    public class CAP_PARAMETER
    {
        [XmlAttribute("valueName")]
        public string valueName;

        [XmlAttribute("value")]
        public String value;
    }
    public class CAP_PARAMETERLIST
    {
        public List<CAP_PARAMETER> parameters;
    }
    [XmlRoot("alert")]
    public class CAP_BASE
    {
        public String identifier;
        public String sender;
        public DateTime sent;
        public CAP_STATUS status;
        public CAP_MSGTYPE msgType;
        public String source;
        public CAP_SCOPE scope;
        public String restriction;
        public List<String> addresses; //double-quote whitespace
        public List<String> code;
        public String note;
        public List<String> references; //whitespace
        public List<String> incidents; //double-quote whitespace

        [XmlElement("info")]
        CAP_INFO info;
    }

    public class CAP_INFO
    {
        public String language;
        public CAP_CATEGORY category;

        [XmlAttribute("event")]
        public String capevent;

        public CAP_RESPONSETYPE responseType;
        public CAP_URGENCY urgency;
        public CAP_SEVERITY severity;
        public CAP_CERTAINTY certainty;
        public String audience;
        //eventCode
        public DateTime effective;
        public DateTime onset;
        public DateTime expires;
        public String senderName;
        public String headline;
        public String description;
        public String instruction;
        public String web;
        public String contact;

        [XmlElement("parameter")]
        public CAP_PARAMETERLIST parameter;

    }
}