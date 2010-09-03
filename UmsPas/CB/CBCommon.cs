using System;
using System.Xml;
using System.Xml.Serialization;
using System.Collections.Generic;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using System.IO;
using System.Text;

namespace com.ums.PAS.CB
{
    public enum CB_OPERATION
    {
        NewAlertPolygon,
        NewAlertPLMN,
        UpdateAlert,
        KillAlert,
    }

    public class CB_MESSAGE_FIELDS
    {
        public long l_db_timestamp;
        public List<CB_RISK> risk_list = new List<CB_RISK>();
        public List<CB_REACTION> reaction_list = new List<CB_REACTION>();
        public List<CB_ORIGINATOR> originator_list = new List<CB_ORIGINATOR>();
    }

    public enum ULBAMESSAGEFIELDTYPE : int
    {
        RISK = 1,
        REACTION = 2,
        ORIGINATOR = 3,
        MESSAGEPART = 4,
        SENDER = 5,
    }

    public class CB_MESSAGE_FIELDS_BASE
    {
        [XmlAttribute("l_pk")]
        public long l_pk;
        [XmlAttribute("sz_name")]
        public String sz_name;
        [XmlIgnore]
        public long l_timestamp;
        [XmlIgnore]
        public ULBAMESSAGEFIELDTYPE type;

        public static CB_MESSAGE_FIELDS_BASE Create(int l_fieldtype, long l_source_pk, String sz_text)
        {
            ULBAMESSAGEFIELDTYPE type = (ULBAMESSAGEFIELDTYPE)Enum.Parse(typeof(ULBAMESSAGEFIELDTYPE), l_fieldtype.ToString());
            switch (type)
            {
                case ULBAMESSAGEFIELDTYPE.MESSAGEPART:
                    return new CB_MESSAGEPART(l_source_pk, sz_text);
                case ULBAMESSAGEFIELDTYPE.ORIGINATOR:
                    return new CB_ORIGINATOR(l_source_pk, sz_text);
                case ULBAMESSAGEFIELDTYPE.REACTION:
                    return new CB_REACTION(l_source_pk, sz_text);
                case ULBAMESSAGEFIELDTYPE.RISK:
                    return new CB_RISK(l_source_pk, sz_text);
                case ULBAMESSAGEFIELDTYPE.SENDER:
                    return new CB_SENDER(l_source_pk, sz_text);
                default:
                    return new CB_MESSAGE_FIELDS_BASE();
            }
        }
    }

    public class CB_RISK : CB_MESSAGE_FIELDS_BASE
    {
        public CB_RISK() : this(-1, "Not set")
        {
        }
        public CB_RISK(long pk, String s)
        {
            l_pk = pk;
            sz_name = s;
            type = ULBAMESSAGEFIELDTYPE.RISK;
        }

    }
    public class CB_REACTION : CB_MESSAGE_FIELDS_BASE
    {
        public CB_REACTION() : this(-1, "Not set")
        {
        }
        public CB_REACTION(long pk, String s)
        {
            l_pk = pk;
            sz_name = s;
            type = ULBAMESSAGEFIELDTYPE.REACTION;
        }
    }
    public class CB_ORIGINATOR : CB_MESSAGE_FIELDS_BASE
    {
        public CB_ORIGINATOR() : this(-1, "Not set")
        {
        }
        public CB_ORIGINATOR(long pk, String s)
        {
            l_pk = pk;
            sz_name = s;
            type = ULBAMESSAGEFIELDTYPE.ORIGINATOR;
        }
    }
    public class CB_SENDER : CB_MESSAGE_FIELDS_BASE
    {
        public CB_SENDER()
            : this(-1, "Not set")
        {
        }
        public CB_SENDER(long pk, String s)
        {
            l_pk = pk;
            sz_name = s;
            type = ULBAMESSAGEFIELDTYPE.SENDER;
        }
    }

    public class CB_MESSAGEPART : CB_MESSAGE_FIELDS_BASE
    {
        public CB_MESSAGEPART() : this(-1, "")
        {
        }
        public CB_MESSAGEPART(long pk, String s)
        {
            l_pk = pk;
            sz_name = s;
            type = ULBAMESSAGEFIELDTYPE.MESSAGEPART;
        }
    }

    public class CB_MESSAGE
    {
        [XmlAttribute("l_channel")]
        public int l_cbchannel;
        [XmlAttribute("sz_text")]
        public String sz_text;
    }

    public class CB_MESSAGELIST
    {
        [XmlElement("message")]
        public CB_MESSAGE[] Items
        {
            get
            {
                CB_MESSAGE[] items = list.ToArray();
                return items;
            }
            set
            {
                if (value == null)
                    return;
                CB_MESSAGE[] items = (CB_MESSAGE[])value;
                list.Clear();
                foreach (CB_MESSAGE item in items)
                    list.Add(item);
            }
        }
        [XmlIgnore]
        public List<CB_MESSAGE> list = new List<CB_MESSAGE>();
    }





    [XmlRoot("cb")]
    public abstract class CB_OPERATION_BASE
    {

        //get serializer based on the abstract CreateSerializer method. Write to file
        public bool Serialize(String Path)
        {
            String path_temp = UCommon.UPATHS.sz_path_temp + "\\";
            String path_release = Path + "\\";
            String base_file = "CB_" + this.operation.ToString() + "_" + this.l_projectpk + "_" + this.l_refno; 
            String tmp_file = path_temp + base_file + ".tmp";
            String xml_file = path_release + base_file + ".xml";
            try
            {
                XmlSerializer s = CreateSerializer();
                XmlSerializerNamespaces xmlnsEmpty = new XmlSerializerNamespaces();
                xmlnsEmpty.Add("", "");

                TextWriter w = new StreamWriter(tmp_file, false, Encoding.UTF8);
                s.Serialize(w, this, xmlnsEmpty);
                w.Close();
            }
            catch (Exception e)
            {
                throw new Exception("Error serializing CB-file " + tmp_file + "\n\nCause:\n" + e.Message);
            }
            try
            {
                File.Move(tmp_file, xml_file);
                return true;
            }
            catch (Exception e)
            {
                throw new Exception("Error publishing CB-file " + xml_file + "\n\nCause:\n" + e.Message);
            }
        }
        protected abstract XmlSerializer CreateSerializer();

        [XmlIgnore]
        public String sz_name;

        [XmlAttribute("operation")]
        public CB_OPERATION operation;

        [XmlAttribute("l_refno")]
        public long l_refno;
        public void setRefno(long refno)
        {
            l_refno = refno;
        }

        [XmlAttribute("l_projectpk")]
        public long l_projectpk = 0;

        [XmlAttribute("sz_projectname")]
        public String sz_projectname = "";

        
        [XmlIgnore]
        public DateTime datetime = new DateTime();
        
        [XmlElement("risk")]
        public CB_RISK risk = new CB_RISK();

        [XmlElement("reaction")]
        public CB_REACTION reaction = new CB_REACTION();

        [XmlElement("originator")]
        public CB_ORIGINATOR originator = new CB_ORIGINATOR();


        [XmlAttribute("l_comppk")]
        public int l_comppk;
        public void setComppk(int comppk)
        {
            l_comppk = comppk;
        }

        [XmlAttribute("l_deptpk")]
        public int l_deptpk;
        public void setDeptpk(int deptpk)
        {
            l_deptpk = deptpk;
        }

        [XmlAttribute("l_userpk")]
        public long l_userpk;
        public void setUserpk(long userpk)
        {
            l_userpk = userpk;
        }
    }

    [XmlRoot("cb")]
    public abstract class CB_SEND_BASE : CB_OPERATION_BASE
    {
        public CB_SEND_BASE()
        {
            risk = new CB_RISK();
            reaction = new CB_REACTION();
            originator = new CB_ORIGINATOR();
            messagepart = new CB_MESSAGEPART();
            sender = new CB_SENDER();
        }
        [XmlAttribute("l_sched_utc")]
        public long l_sched_utc;

        [XmlElement("messagepart")]
        public CB_MESSAGEPART messagepart;

        [XmlElement("textmessages")]
        public CB_MESSAGELIST textmessages = null;

        [XmlElement("risk")]
        public CB_RISK risk;

        [XmlElement("reaction")]
        public CB_REACTION reaction;

        [XmlElement("originator")]
        public CB_ORIGINATOR originator;

        [XmlElement("sender")]
        public CB_SENDER sender;


        [XmlAttribute("mdvgroup")]
        public MDVSENDINGINFO_GROUP mdvgroup;

        [XmlAttribute("l_validity")]
        public int l_validity;

        [XmlAttribute("l_parent_refno")]
        public long l_parent_refno; //link for resend in BBPROJECT_X_REFNO


    }


    [XmlRoot("cb")]
    public class CB_ALERT_POLYGON : CB_SEND_BASE // Used for new Polyalert or update Polyalert
    {
        public CB_ALERT_POLYGON()
        {
            textmessages = new CB_MESSAGELIST(); // initialize
            this.operation = CB_OPERATION.NewAlertPolygon;
        }
        [XmlElement("alertpolygon")]
        public UPolygon shape;

        protected override XmlSerializer CreateSerializer()
        {
            return new XmlSerializer(typeof(CB_ALERT_POLYGON));
        }
        public static CB_ALERT_POLYGON Deserialize(String file, Encoding encoding)
        {
            String s = File.ReadAllText(file, encoding);
            return Deserialize(s);
        }
        public static CB_ALERT_POLYGON Deserialize(String xml)
        {
            CB_ALERT_POLYGON cob = new CB_ALERT_POLYGON();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (CB_ALERT_POLYGON)serializer.Deserialize(reader);
                return cob;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }

    [XmlRoot("cb")]
    public class CB_ALERT_PLMN : CB_SEND_BASE //Used for Nation wide CB
    {
        public CB_ALERT_PLMN()
        {
            textmessages = new CB_MESSAGELIST();
            this.operation = CB_OPERATION.NewAlertPLMN;
        }

        [XmlElement("alertplmn")]
        public UPLMN shape;

        protected override XmlSerializer CreateSerializer()
        {
            return new XmlSerializer(typeof(CB_ALERT_PLMN));
        }

    }

    [XmlRoot("cb")]
    public class CB_ALERT_UPDATE : CB_SEND_BASE //Update an existing CB-alert
    {
        public CB_ALERT_UPDATE()
        {
            textmessages = new CB_MESSAGELIST();
            this.operation = CB_OPERATION.UpdateAlert;
        }
        protected override XmlSerializer CreateSerializer()
        {
            return new XmlSerializer(typeof(CB_ALERT_UPDATE));
        }
    }

    [XmlRoot("cb")]
    public class CB_ALERT_KILL : CB_OPERATION_BASE //Kill a CB-alert
    {
        public CB_ALERT_KILL()
        {
            this.operation = CB_OPERATION.KillAlert;
        }
        [XmlAttribute("l_sched_utc")]
        public long l_sched_utc;
        protected override XmlSerializer CreateSerializer()
        {
            return new XmlSerializer(typeof(CB_ALERT_KILL));
        }
    }

    public class CB_SENDING_RESPONSE
    {
        public CB_OPERATION operation;
        public long l_projectpk;
        public long l_timestamp;
        public long l_refno;
        public long l_code; //0=success

    }

    public class CB_PROJECT_STATUS_REQUEST
    {
        public ULOGONINFO logon;
        public long l_projectpk;
        public long l_timefilter;
    }

    public class CB_PROJECT_STATUS_RESPONSE
    {
        public long l_db_timestamp;
        public BBPROJECT project;
        public List<CB_STATUS> statuslist;
    }

    public class CB_STATUS
    {
        public String sz_sendingname;
        public MDVSENDINGINFO mdv;
        public CB_MESSAGEPART messagepart;
        public CB_RISK risk;
        public CB_REACTION reaction;
        public CB_ORIGINATOR originator;
        public CB_SENDER sender;

        public UShape shape;
        public long l_refno;
        public long l_combined_status;
        public long l_created_ts;
        public long l_started_ts;
        public long l_last_ts;
        public int l_channel;
        public int f_simulation;
        public List<ULBASENDING> operators;
    }
}