using System;
using System.Collections.Generic;
using System.Linq;

using System.Data.Odbc;
using System.Data;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;

using WAV2RAWLib;
using System.Xml.Serialization;
using System.Xml.Serialization.Advanced;
using System.Xml.Serialization.Configuration;
using System.Xml;
using System.Web;
using System.Web.Services.Protocols;
using System.Xml.Schema;
using System.Diagnostics;

namespace com.ums.VB
{
    public struct RECIPIENT
    {
        public string PhoneNumber;
        public string PinCode;

        public RECIPIENT(string PhoneNumber)
        {
            this.PhoneNumber = PhoneNumber;
            this.PinCode = "";
        }
        public RECIPIENT(string PhoneNumber, string PinCode)
        {
            this.PhoneNumber = PhoneNumber;
            this.PinCode = PinCode;
        }
    }
    public struct ItemStatus
    {
        public int StatusCode;
        public string StatusShort;
        public string StatusText;
    }
    public class STATUS
    {
        private Int64 l_refno;
        private int l_itemno;
        private string sz_status;
        private ItemStatus m_item_status;
        private int l_seconds_spent;
        private DateTime dtm_finishtime;
        private int l_sending_attempts;
        private string sz_phonenumber;
        private string sz_country_code;
        private DTMF dtmf_answer;

        public Int64 ReferenceNumber
        {
            get { return l_refno; }
            set { l_refno = value; }
        }
        public int ItemNumber
        {
            get { return l_itemno; }
            set { l_itemno = value; }
        }
        public string SendingStatus
        {
            get { return sz_status; }
            set { sz_status = value; }
        }
        public ItemStatus ItemStatus
        {
            get { return m_item_status; }
            set { m_item_status = value; }
        }
        public int SecondsSpent
        {
            get { return l_seconds_spent; }
            set { l_seconds_spent = value; }
        }
        public DateTime FinishedTime
        {
            get { return dtm_finishtime; }
            set { dtm_finishtime = value; }
        }
        public int SendingAttempts
        {
            get { return l_sending_attempts; }
            set { l_sending_attempts = value; }
        }
        public string PhoneNumber
        {
            get { return sz_phonenumber; }
            set { sz_phonenumber = value; }
        }
        public string CountryCode
        {
            get { return sz_country_code; }
            set { sz_country_code = value; }
        }
        public DTMF DTMFAnswer
        {
            get { return dtmf_answer; }
            set { dtmf_answer = value; }
        }

        public STATUS() { }
        public STATUS(Int64 l_refno, int l_itemno, string sz_status, int l_seconds_spent, DateTime dtm_finishtime,
            int l_sending_attempts, string sz_phonenumber, string sz_country_code, DTMF dtmf_answer, ref ItemStatus m_itemStatus)
        {
            this.l_refno = l_refno;
            this.l_itemno = l_itemno;
            this.sz_status = sz_status;
            this.l_seconds_spent = l_seconds_spent;
            this.dtm_finishtime = dtm_finishtime;
            this.l_sending_attempts = l_sending_attempts;
            this.sz_phonenumber = sz_phonenumber;
            this.sz_country_code = sz_country_code;
            this.dtmf_answer = dtmf_answer;
            this.ItemStatus = m_itemStatus;
        }
    }
    public enum LANGUAGE
    {
        NORWEGIAN = 6,
        BRITISH = 7,
        USENGLISH = 8,
        DUTCH = 9,
        SWEDISH = 10,
        DANISH_POUL = 11,
        DANISH_METTE = 13,
        GERMAN = 12,
        SPANISH = 14,
    }
    public enum DTMF
    { // 2001- user satus 3000-3007
        NO_ANSWER = -1,
        ZERO = 0,
        ONE = 1,
        TWO = 2,
        THREE = 3,
        FOUR = 4,
        FIVE = 5,
        SIX = 6,
        SEVEN = 7,
        EIGHT = 8,
        NINE = 9,
        HASH = 10,
        STAR = 11,

    }

    public struct SendinProfileChoice
    {
        [XmlElementAttribute("Name", typeof(string), IsNullable = false)]
        [XmlElementAttribute("Number", typeof(int), IsNullable = false)]
        [XmlChoiceIdentifierAttribute("ItemsElementName")]
        public object[] Items;
        [XmlElementAttribute(IsNullable = false)]
        [XmlIgnoreAttribute()]
        public SendingProfileAttribute[] ItemsElementName;
    }

    public enum SendingProfileAttribute
    {
        Name,
        Number,
    }
    public struct SendingProfile
    {
        public string Name;
        public Int64 Number;
    }
    public struct SendingSettings
    {
        //[XmlElement("SendingSettings",typeof(SendingSettings))]
        public string SendingName;
        public Int64 MessageCaching;
        public SendingProfile Profile;
        public SendingProfile ConfigProfile;
        public Int64 Schedule;
        public int IntroClip;
        public bool HiddenNumber;
    }
    /*
    public class PARAMETERS
    {
        private Int64 l_canceldate = (long)Convert.ToInt32((DateTime.Now.AddDays(1)).ToString("yyyyMMdd"));
        private Int64 l_canceltime = (long)Convert.ToInt16(DateTime.Now.ToString("HHmm"));
        private Int64 l_interval = 2;
        private Int64 l_pauseinterval = 2;
        private Int64 l_pausetime = 5;
        private Int64 l_retries = 3;
        private int l_pri = 8;
        private string sz_sending_name = "Sending name";
        private int l_item = 1; // This is supposed to be 1 and only 1
        private int l_wav2raw_frequency = 8000;
        private Int64 l_reschedpk = -1;
        private string sz_resched_name = "";
        private bool b_caching = false;
        private DateTime dtm_caching_expiry;

        public bool MessageCachingEnabled
        {
            set { b_caching = value; }
            get { return b_caching; }
        }

        public DateTime MessageCachingExpiry
        {
            set { dtm_caching_expiry = value; }
            get { return dtm_caching_expiry; }
        }

        public Int64 Canceldate
        {
            get { return l_canceldate; }
            set { l_canceldate = value; }
        }
        public Int64 Canceltime
        {
            get { return l_canceltime; }
            set { l_canceltime = value; }
        }
        public Int64 Interval
        {
            get { return l_interval; }
            set { l_interval = value; }
        }
        public Int64 Pauseinterval
        {
            get { return l_pauseinterval; }
            set { l_pauseinterval = value; }
        }
        public Int64 Pausetime
        {
            get { return l_pausetime; }
            set { l_pausetime = value; }
        }
        public Int64 Retries
        {
            get { return l_retries; }
            set { l_retries = value; }
        }
        public int Priority
        {
            get { return l_pri; }
            set { l_pri = value; }
        }
        public string SendingName
        {
            get { return sz_sending_name; }
            set { sz_sending_name = value; }
        }
        public int Item
        {
            get { return l_item; }
        }
        public int Wav2RawFrequency
        {
            get { return l_wav2raw_frequency; }
            set { l_wav2raw_frequency = value; }
        }
        public Int64 ReschedProfileNumber
        {
            get { return l_reschedpk; }
            set { l_reschedpk = value; }
        }
        public string ReschedProfileName
        {
            get { return sz_resched_name; }
            set { sz_resched_name = value; }
        }
    }*/
    public class VOCFILE
    {
        public VOCTYPE type;
        public String sz_tts_string;
        //public String sz_file_name; // Is used together with nothing
        public LANGUAGE l_langpk;
        public byte[] audiodata;
    }
    public enum VOCTYPE
    {
        TTS = 1,
        WAV = 2,
        RAW = 3,
    }
    /*
    [XmlSchemaProvider("ACCOUNT")]
    public class ACCOUNT:IXmlSerializable
    {
        public string Company;
        public string Department;
        public string Password;

        public static XmlQualifiedName ACCOUNT(XmlSchemaSet xs)
        {

        }

        public void WriteXml(XmlWriter writer)
        {
            writer.WriteString(Company);
            writer.WriteString(Department);
            writer.WriteString(Password);
        } //WriteXml
        public void ReadXml(XmlReader reader)
        {
            Company = reader.ReadString();
            Department = reader.ReadString();
            Password = reader.ReadString();
        } //ReadXml
        
        public XmlSchema GetSchema()
        {
            XmlSchema schema = new XmlSchema();
            schema.Id = "vb";
           
            // <xs:element name="cat" type="xs:string"/>
            XmlSchemaElement elementCompany = new XmlSchemaElement();
            schema.Items.Add(elementCompany);
            elementCompany.Name = "Company";
            elementCompany.SchemaTypeName = new XmlQualifiedName("string", "http://www.w3.org/2001/XMLSchema");
            elementCompany.MinOccurs = 1;
            elementCompany.MaxOccurs = 1;

            // <xs:element name="cat" type="xs:string"/>
            XmlSchemaElement elementDepartment = new XmlSchemaElement();
            schema.Items.Add(elementDepartment);
            elementDepartment.Name = "Department";
            elementDepartment.SchemaTypeName = new XmlQualifiedName("string", "http://www.w3.org/2001/XMLSchema");
            elementDepartment.MinOccurs = 1;
            elementDepartment.MaxOccurs = 1;

            // <xs:element name="cat" type="xs:string"/>
            XmlSchemaElement elementPassword = new XmlSchemaElement();
            schema.Items.Add(elementPassword);
            elementPassword.Name = "Password";
            elementPassword.SchemaTypeName = new XmlQualifiedName("string", "http://www.w3.org/2001/XMLSchema");
            elementPassword.MinOccurs = 1;
            elementPassword.MaxOccurs = 1;

            
            XmlSchemaElement elementAccount = new XmlSchemaElement();
            schema.Items.Add(elementAccount);
            elementAccount.Name = "ACCOUNT";
            

            XmlSchemaSequence seqAccount = new XmlSchemaSequence();
            
            XmlSchemaAll allAccount = new XmlSchemaAll();

            schema.Items.Add(allAccount);

            XmlSchemaComplexType complexType = new XmlSchemaComplexType();
            complexType.Name = "ACCOUNT";

            complexType.Parent = seqAccount;
            //allAccount.Items.Add(complexType);

            XmlSchemaElement CompanyRef = new XmlSchemaElement();
            seqAccount.Items.Add(CompanyRef);
            CompanyRef.RefName = new XmlQualifiedName("Company");

            XmlSchemaElement DepartmentRef = new XmlSchemaElement();
            seqAccount.Items.Add(DepartmentRef);
            DepartmentRef.RefName = new XmlQualifiedName("Departent");

            XmlSchemaElement PasswordRef = new XmlSchemaElement();
            seqAccount.Items.Add(PasswordRef);
            PasswordRef.RefName = new XmlQualifiedName("Password");            
            
            return null;
        } //GetSchema
 
    }*/

    public class ACCOUNT
    {
        [XmlElement(DataType = "string", IsNullable = false)]
        public string Company;
        [XmlElement(DataType = "string", IsNullable = false)]
        public string Department;
        [XmlElement(DataType = "string", IsNullable = false)]
        public string Password;
    }

    public class SendVoice
    {
        public SendVoice()
        {
        }
        public SendVoice(SendVoice p)
        {
            this.conn = p.conn;
            this.cmd = p.cmd;
        }

        private OdbcConnection conn;
        private OdbcCommand cmd;
        private OdbcTransaction transaction;
        private string sz_cstring;
        private string sz_eat_path;
        private string sz_tts_path;
        private float f_rms;
        private int l_deptpk;
        private int l_deptpri;

        public float Wav2RawRMS
        {
            set { f_rms = value; }
        }
        public string TTSServer
        {
            set { sz_tts_path = value; }
        }
        public string EatPath
        {
            set { sz_eat_path = value; }
        }

        public String ConnectionString
        {
            set { sz_cstring = value; }
        }

        public void soapExceptionTest()
        {
            try
            {
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs previewTTS(): Error getting refno", "previewTTS:getRefno", FaultCode.Client);
            }
            catch (SoapException se)
            {
                throw se;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public List<VOCFILE> previewTTS(ACCOUNT acc, VOCFILE[] message)
        {
            Int64 l_refno;
            List<VOCFILE> vocwavs = new List<VOCFILE>();

            try
            {
                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs previewTTS(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "previewTTS:checkLogon", FaultCode.Client);

                cmd.Parameters.Clear();

                // Get refno
                l_refno = getRefno();
                if (l_refno == -1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs previewTTS(): Error getting refno", "previewTTS:getRefno", FaultCode.Server);

                string[] files = generateVoice(message, l_refno);

                VOCFILE voc;
                for (int i = 0; i < files.Length; ++i)
                {
                    voc = new VOCFILE();
                    voc.type = VOCTYPE.WAV;
                    if (File.Exists(getAudioFilesPath() + files[i].Replace(".raw", ".wav")))
                    {
                        EventLog.WriteEntry("sendMergedVoice", "File found: " + getAudioFilesPath() + files[i].Replace(".raw", ".wav"));
                        voc.audiodata = File.ReadAllBytes((getAudioFilesPath() + files[i].Replace(".raw", ".wav")).Replace(".raw", ".wav"));
                    }
                    else
                    {
                        EventLog.WriteEntry("sendMergedVoice", "File not found: " + sz_tts_path + files[i].Replace(".raw", ".wav"));
                        voc.audiodata = File.ReadAllBytes(sz_tts_path + files[i].Replace(".raw", ".wav"));
                    }
                    voc.l_langpk = message[i].l_langpk;
                    vocwavs.Add(voc);
                }
                return vocwavs;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("sendMergedVoice", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs previewTTS(): Error generating TTS file", "previewTTS", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
        }

        public string cancelVoice(ACCOUNT acc, Int64 referenceNumber)
        {
            try
            {
                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs cancelVoice(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "cancelVoice:checkLogon", FaultCode.Client);

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_vws_cancel ?";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = referenceNumber;
                OdbcDataReader dr = cmd.ExecuteReader();
                string ret = "ERROR";
                while (dr.Read())
                    ret = dr[0].ToString();
                dr.Close();
                cmd.Dispose();
                return ret;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs:cancelVoice", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs cancelVoice(): " + e.Message, "cancelVoice", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }

        }

        public List<string> getAvailableSoundLibraryFiles(ACCOUNT acc, string globalwavpath)
        {
            List<string> fileList = new List<string>();

            try
            {
                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs getAvailableSoundLibraryFiles(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "getAvailableSoundLibraryFiles:checkLogon", FaultCode.Client);

                DirectoryInfo di = new DirectoryInfo(globalwavpath);
                FileInfo[] wavfiles = di.GetFiles("*.wav");
                for (int i = 0; i < wavfiles.Length; ++i)
                    fileList.Add(wavfiles[i].Name);

                cmd.Dispose();

            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs:getAvailableSoundLibraryFiles", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs getAvailableSoundLibraryFiles(): " + e.Message, "getAvailableSoundLibraryFiles", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
            return fileList;
        }

        public List<ItemStatus> getAvailableStatuses(ACCOUNT acc)
        {
            List<ItemStatus> itemStatusList = new List<ItemStatus>();
            try
            {
                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs getAvailableStatuses(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "getAvailableStatuses:checkLogon", FaultCode.Client);

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "SELECT l_status, sz_short_status, sz_status FROM BBSTATUSCODES ORDER BY l_status ASC";
                OdbcDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    ItemStatus itemstatus = new ItemStatus();
                    itemstatus.StatusCode = dr.GetInt32(0);
                    itemstatus.StatusShort = dr.GetString(1);
                    itemstatus.StatusText = dr.GetString(2);
                    itemStatusList.Add(itemstatus);
                }
                dr.Close();
                cmd.Dispose();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs:getAvailableStatuses", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs getAvailableStatuses(): " + e.Message, "getAvailableStatuses", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
            return itemStatusList;

        }

        public List<STATUS> getStatus(ACCOUNT acc, Int64 l_refno)
        {
            List<STATUS> statusList = new List<STATUS>();
            try
            {
                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                // Here I have to check the account info
                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs getStatus(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "getStatus:checkLogon", FaultCode.Client);

                cmd.Parameters.Clear();
                //cmd.CommandType = CommandType.StoredProcedure;
                //cmd.CommandText = "sp_voice_getstatus ?";
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "SELECT h.l_refno, h.l_item, s.sz_status, h.l_status, h.l_seconds, h.l_date, h.l_time, h.l_tries, h.sz_number, h.sz_ccode, s.sz_short_status " +
                  "FROM BBHIST h, BBSTATUSCODES s " +
                 "WHERE l_refno = ? " +
                   "AND h.l_status = s.l_status";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                OdbcDataReader dr = cmd.ExecuteReader();
                int year;
                int month;
                int day;
                int hour;
                int minute;
                string tmp_status = "";
                string status;
                while (dr.Read())
                {
                    string tmp_year = dr.GetInt32(5).ToString();
                    year = Int32.Parse(tmp_year.Substring(0, 4));
                    month = Int32.Parse(tmp_year.ToString().Substring(4, 2));
                    day = Int32.Parse(tmp_year.ToString().Substring(6, 2));
                    string tmp_time = dr.GetInt32(6).ToString();
                    if (tmp_time.Length < 4)
                        tmp_time = tmp_time.PadLeft(4, '0');
                    hour = Int32.Parse(tmp_time.Substring(0, 2));
                    minute = Int32.Parse(tmp_time.Substring(2, 2));

                    tmp_status = dr.GetString(2);

                    ItemStatus itemstatus = new ItemStatus();
                    itemstatus.StatusCode = Int32.Parse(dr[3].ToString());
                    itemstatus.StatusShort = dr["sz_short_status"].ToString();
                    itemstatus.StatusText = dr.GetString(2);

                    if (tmp_status.Equals(""))
                        status = "UNAVAILABLE";
                    else
                        status = "FINISHED"; // How to find INQUEUE status?

                    statusList.Add(new STATUS(Int64.Parse(dr[0].ToString()), dr.GetInt32(1), status, Int32.Parse(dr[4].ToString()),
                        new DateTime(year, month, day, hour, minute, 0), Int32.Parse(dr[7].ToString()),
                        dr.GetString(8), dr.GetString(9), DTMF.NO_ANSWER, ref itemstatus));
                }
                if (statusList.Count == 0)
                {
                    STATUS s = new STATUS();
                    s.SendingStatus = "UNAVAILABLE";
                    statusList.Add(s);
                }
                dr.Close();
                cmd.Dispose();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs:getStatus", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs getStatus(): " + e.Message, "getStatus:checkLogon", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
            return statusList;
        }
        // Cleans phone numbers
        private string cleanNumber(string number)
        {
            string tmp;
            Regex re = new Regex("<number");
            if (re.IsMatch(number)) // numbers as VB address book
            {
                NameTable nt = new NameTable();
                XmlNamespaceManager nsmngr = new XmlNamespaceManager(nt);

                XmlParserContext xpc = new XmlParserContext(null, nsmngr, null, XmlSpace.None);

                XmlReaderSettings rs = new XmlReaderSettings();
                rs.ConformanceLevel = ConformanceLevel.Fragment;
                XmlReader xmlreader = XmlReader.Create(new StringReader(number), rs, xpc);
                tmp = "";
                string attrib = "";
                while (xmlreader.Read())
                {
                    //tull = xmlreader.ReadToFollowing("number");
                    xmlreader.MoveToFirstAttribute();
                    attrib = "," + xmlreader.Value;
                    while (xmlreader.MoveToNextAttribute())
                        attrib += "," + xmlreader.Value;
                    xmlreader.MoveToContent();
                    tmp += xmlreader.ReadString();

                    tmp += attrib;
                    number = tmp; // sets number for next test
                }
            }

            tmp = number.Replace(" ", "");
            re = new Regex(",,");
            while (re.IsMatch(tmp))
                tmp = tmp.Replace(",,", ",");
            re = new Regex("[^0-9|'+'|',']|\\,$");
            Match m = re.Match(tmp);
            while (m.Index > 0)
            {
                tmp = tmp.Remove(m.Index, 1);
                m = re.Match(tmp);
            }
            return tmp;
        }

        public Int64 send(ACCOUNT acc, SendingSettings settings, RECIPIENT[] to, string from, VOCFILE[] message)
        {
            string sz_sender = from;//"23000050";
            string[] filename;
            Int64 l_refno;

            try
            {
                string date, time;
                // Date test
                if (settings.Schedule <= 0)
                    settings.Schedule = Convert.ToInt64(DateTime.Now.ToString("yyyyMMddHHmm"));
                try
                {
                    date = Convert.ToString(settings.Schedule).Substring(0, 8);
                    time = Convert.ToString(settings.Schedule).Substring(8, 4);
                }
                catch (Exception e)
                {
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): There was a problem with the schedule date, the format should be yyyyMMddHHmm. Your input: " + settings.Schedule, "send:dateformat", FaultCode.Client);
                }
                if (Convert.ToUInt64(date) < Convert.ToUInt64(DateTime.Now.ToString("yyyyMMdd")))
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): There was a problem with the schedule date, the format should be yyyyMMddHHmm. Your input: " + settings.Schedule, "send:dateformat", FaultCode.Client);

                if (settings.MessageCaching <= 0)
                    settings.MessageCaching = Convert.ToInt64(DateTime.Now.ToString("yyyyMMdd"));
                else
                {
                    int temp;
                    try
                    {
                        temp = Convert.ToInt32(Convert.ToString(settings.MessageCaching).Substring(0, 8));
                    }
                    catch (Exception e)
                    {
                        throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): There was a problem with the message caching date, the format should be yyyyMMddHHmm. Your input: " + settings.MessageCaching, "send:dateformat", FaultCode.Client);
                    }
                    int nowtemp = Convert.ToInt32(DateTime.Now.ToString("yyyyMMdd"));
                    if (temp < nowtemp)
                        throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): There was a problem with the message caching date, the format should be yyyyMMddHHmm. Your input: " + settings.MessageCaching, "send:dateformat", FaultCode.Client);
                }

                conn = new OdbcConnection(sz_cstring);
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                // I have to check the account info
                if (!checkLogon(acc, ref l_deptpk, ref l_deptpri))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Voice.cs send(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "send:checkLogon", FaultCode.Client);


                // Get refno
                l_refno = getRefno();
                if (l_refno == -1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error getting refno", "send:getRefno", FaultCode.Server);

                transaction = conn.BeginTransaction();
                cmd.Transaction = transaction;

                filename = generateVoice(message, l_refno);

                // set options
                //BBDYNARESCHED dynre = new BBDYNARESCHED();
                // This should really use l_reschedpk
                //if configprofile id
                if (settings.ConfigProfile.Number != -1 || !settings.ConfigProfile.Name.Equals(""))
                {
                    if (insertBBDYNARESCHED(acc, l_refno, settings.ConfigProfile.Number, settings.ConfigProfile.Name, l_deptpk) == -1)
                        throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error inserting dynamic reschedule from reschedpk or name", "send:insertBBDYNARESCHED", FaultCode.Client);
                }
                else // Is reschedule profile required? If so, throw exception
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error, reschedprofile required", "send:insertBBDYNARESCHED", FaultCode.Client);

                if (insertBBVALID(l_refno, settings) == -1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error inserting valid", "send:insertBBVALID", FaultCode.Client);

                if (from != null && from.Length > 0)
                    if (insertBBSENDNUM(l_refno, sz_sender) == -1)
                        throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error inserting sendnum", "send:insertBBVALID", FaultCode.Client);

                if (insertBBACTIONPROFILESEND(l_refno, settings, l_deptpk) == -1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): Error inserting action profile", "send:insertBBVALID", FaultCode.Client);

                transaction.Commit();
                cmd.Dispose();

                StreamWriter sw = File.CreateText(sz_eat_path + "d" + l_refno + ".tmp");
                sw.WriteLine("/MDV");
                sw.WriteLine("/Company=" + acc.Company.ToUpper());
                sw.WriteLine("/Department=" + acc.Department.ToUpper());
                sw.WriteLine("/Pri=" + l_deptpri); // + param.Priority); // How is this defined?
                sw.WriteLine("/SchedDate=" + date);//DateTime.Now.ToString("yyyyMMdd"));
                sw.WriteLine("/SchedTime=" + time);//DateTime.Now.ToString("HHmm"));
                sw.WriteLine("/Item=1"); // Hvordan bestemmes denne?
                sw.WriteLine("/Name=" + settings.SendingName);
                for (int i = 0; i < filename.Length; ++i)
                    sw.WriteLine("/File=v" + l_refno + "_" + i + ".raw");
                for (int i = 0; i < to.Length; ++i)
                    sw.WriteLine("/DCALL NA " + cleanNumber(to[i].PhoneNumber)); //DCALL || SIMU
                //sw.WriteLine("/SIMU NA " + to[i].PhoneNumber); //DCALL || SIMU
                sw.Close();

                File.Move(sz_eat_path + "d" + l_refno + ".tmp", sz_eat_path + "d" + l_refno + ".adr");

                return l_refno;
            }
            catch (SoapException se)
            {
                transaction.Rollback();
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", se.Message + " _ " + se.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw se;
            }
            catch (Exception e)
            {
                transaction.Rollback();
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Voice.cs send(): " + e.Message, "send", FaultCode.Client);
            }
            finally
            {
                conn.Close();
            }

        }

        /*
        private Int64 getRefno()
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.CommandText = "sp_refno_out";

            OdbcDataReader dr = cmd.ExecuteReader();
            Int64 l_refno = -1;
            while (dr.Read())
                l_refno = Int64.Parse(dr[0].ToString());

            dr.Close();
            return l_refno;
        }

        private int insertBBDYNARESCHED(Int64 l_refno, Int64 l_retries, Int64 l_interval, Int64 l_canceltime, Int64 l_canceldate,
            Int64 l_pausetime, Int64 l_pauseinterval)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                     "VALUES(?, ?, ?, ?, ?, ?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@l_retries", OdbcType.TinyInt).Value = l_retries;
            cmd.Parameters.Add("@l_interval", OdbcType.SmallInt).Value = l_interval;
            cmd.Parameters.Add("@l_canceltime", OdbcType.SmallInt).Value = l_canceltime;
            cmd.Parameters.Add("@l_canceldate", OdbcType.Int).Value = l_canceldate;
            cmd.Parameters.Add("@l_pausetime", OdbcType.SmallInt).Value = l_pausetime;
            cmd.Parameters.Add("@l_pauseinterval", OdbcType.Int).Value = l_pauseinterval;
            return cmd.ExecuteNonQuery();
        }

        private int insertBBVALID(Int64 l_refno)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBVALID(l_refno, l_valid) VALUES(?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@l_valid", OdbcType.Int).Value = l_valid;
            return cmd.ExecuteNonQuery();
        }

        private int insertBBSENDNUM(Int64 l_refno, string sz_sender)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES(?, ?)";
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_sender;
            return cmd.ExecuteNonQuery();
        }

        private int insertBBACTIONPROFILESEND(Int64 l_refno, Int64 l_actionprofilepk)
        {
            cmd.Parameters.Clear();
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = "INSERT INTO BBACTIONPROFILESEND(l_actionprofilepk, l_refno) VALUES(?, ?)";
            cmd.Parameters.Add("@l_actionprofilepk", OdbcType.Int).Value = l_actionprofilepk;
            cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
            return cmd.ExecuteNonQuery();
        }

        private bool checkLogon(ACCOUNT acc)
        {
            bool ret = false;
            String szSQL = "SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD WHERE BU.l_userpk=? AND " +
                                            "BC.l_comppk=? AND BD.l_deptpk=? AND BU.l_comppk=BC.l_comppk AND BC.l_comppk=BD.l_comppk AND " +
                                            "BU.sz_password=?";
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandText = szSQL;
                cmd.CommandType = CommandType.Text;
                cmd.Parameters.Add("@userpk", OdbcType.Int).Value = acc.l_userpk; // This should be BigInt, but that is not supported by ayanami
                cmd.Parameters.Add("@comppk", OdbcType.Int).Value = acc.l_comppk;
                cmd.Parameters.Add("@deptpk", OdbcType.Int).Value = acc.l_deptpk;
                cmd.Parameters.Add("@passwd", OdbcType.VarChar, 20).Value = acc.sz_password;
                OdbcDataReader dr = cmd.ExecuteReader();

                if (dr.Read())
                {
                    ret = true;
                }

                dr.Close();
                cmd.Parameters.Clear();

            }
            catch (Exception e)
            {
                throw e;
            }

            return ret;
        }
        */

        public Int64 getRefno()
        {
            bool connOpened = false;
            try
            {
                if (cmd == null)
                {
                    conn = new OdbcConnection(sz_cstring);
                    conn.Open();
                    cmd = new OdbcCommand();
                    cmd.Connection = conn;
                    connOpened = true;
                }

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_refno_out";

                OdbcDataReader dr = cmd.ExecuteReader();
                Int64 l_refno = -1;
                while (dr.Read())
                    l_refno = Int64.Parse(dr[0].ToString());

                dr.Close();
                return l_refno;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on getRefno(): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
            finally
            {
                if (connOpened)
                {
                    cmd.Dispose();
                    conn.Close();
                }
            }
        }

        private int insertBBDYNARESCHED(ACCOUNT acc, Int64 l_refno, Int64 l_reschedpk, string sz_name, int l_deptpk)
        {
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                if (l_reschedpk > 0)
                {
                    cmd.CommandText = "INSERT INTO BBDYNARESCHED (l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                      "SELECT l_refno=?, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval " +
                                        "FROM BBRESCHEDPROFILES " +
                                       "WHERE l_reschedpk=? AND l_deptpk=?";
                    cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                    cmd.Parameters.Add("@l_reschedpk", OdbcType.Int).Value = l_reschedpk;
                    cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                }
                else
                {
                    cmd.CommandText = "INSERT INTO BBDYNARESCHED (l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                      "SELECT l_refno=?, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval " +
                                        "FROM BBRESCHEDPROFILES " +
                                       "WHERE sz_name=? AND l_deptpk=?";
                    cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                    cmd.Parameters.Add("@sz_name", OdbcType.VarChar, 50).Value = sz_name;
                    cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                }
                return cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on insertBBDYNARESCHED(ACCOUNT acc, Int64 l_refno, Int64 l_reschedpk, string sz_name, int l_deptpk): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private int insertBBDYNARESCHED(Int64 l_refno, Int64 l_retries, Int64 l_interval, Int64 l_canceltime, Int64 l_canceldate,
            Int64 l_pausetime, Int64 l_pauseinterval)
        {
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                         "VALUES(?, ?, ?, ?, ?, ?, ?)";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("@l_retries", OdbcType.TinyInt).Value = l_retries;
                cmd.Parameters.Add("@l_interval", OdbcType.SmallInt).Value = l_interval;
                cmd.Parameters.Add("@l_canceltime", OdbcType.SmallInt).Value = l_canceltime;
                cmd.Parameters.Add("@l_canceldate", OdbcType.Int).Value = l_canceldate;
                cmd.Parameters.Add("@l_pausetime", OdbcType.SmallInt).Value = l_pausetime;
                cmd.Parameters.Add("@l_pauseinterval", OdbcType.Int).Value = l_pauseinterval;
                return cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on insertBBDYNARESCHED(Int64 l_refno, Int64 l_retries, Int64 l_interval, Int64 l_canceltime, Int64 l_canceldate, Int64 l_pausetime, Int64 l_pauseinterval): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private int insertBBVALID(Int64 l_refno, SendingSettings settings)
        {

            try
            {

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "INSERT INTO BBVALID(l_refno, l_valid) VALUES(?, ?)";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("@l_valid", OdbcType.Int).Value = Convert.ToUInt32(settings.MessageCaching.ToString().Substring(0, 8));
                return cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on insertBBVALID: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private int insertBBSENDNUM(Int64 l_refno, string sz_sender)
        {
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES(?, ?)";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_sender;
                return cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on insertBBSENDNUM: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private int insertBBACTIONPROFILESEND(Int64 l_refno, SendingSettings settings, int l_deptpk)
        {
            try
            {
                if (settings.Profile.Name != null && settings.Profile.Name.Length > 0)
                {
                    cmd.Parameters.Clear();
                    cmd.CommandType = CommandType.Text;
                    cmd.CommandText = "SELECT l_profilepk FROM BBACTIONPROFILESNAME WHERE l_deptpk = ? AND sz_name = ?";
                    cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                    cmd.Parameters.Add("@sz_name", OdbcType.VarChar, 50).Value = settings.Profile.Name;
                    OdbcDataReader dr = cmd.ExecuteReader();
                    while (dr.Read())
                        settings.Profile.Number = dr.GetInt32(0);
                }

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "INSERT INTO BBACTIONPROFILESEND(l_actionprofilepk, l_refno) VALUES(?, ?)";
                cmd.Parameters.Add("@l_actionprofilepk", OdbcType.Int).Value = settings.Profile.Number;
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                return cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on insertBBACTIONPROFILESEND: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private bool checkLogon(ACCOUNT acc, ref int l_deptpk, ref int l_deptpri)
        {
            bool ret = false;

            try
            {
                /*
                string szSQL = String.Format("SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc " +
                                    "FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BU.l_userpk={0} " +
                                     "AND BC.l_comppk={1} " +
                                     "AND BD.l_deptpk={2} " +
                                     "AND BU.l_comppk=BC.l_comppk " +
                                     "AND BC.l_comppk=BD.l_comppk " +
                                     "AND BU.sz_password={3}", acc.l_userpk, acc.l_comppk, acc.l_deptpk, acc.sz_password);
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = szSQL;
                OdbcDataReader dr = cmd.ExecuteReader();
                
                 * 
                 cmd.CommandText = "SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc " +
                                    "FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BU.l_userpk=? " +
                                     "AND BC.l_comppk=? " +
                                     "AND BD.l_deptpk=? " +
                                     "AND BU.l_comppk=BC.l_comppk " +
                                     "AND BC.l_comppk=BD.l_comppk " +
                                     "AND BU.sz_password=?";
                 
                 */

                cmd.Parameters.Clear();
                cmd.CommandText = "SELECT BD.l_deptpk, BD.l_deptpri " +
                                    "FROM BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BC.l_comppk=BD.l_comppk " +
                                     "AND BC.sz_compid=? " +
                                     "AND BD.sz_deptid=? " +
                                     "AND BD.sz_password=?";

                cmd.CommandType = CommandType.Text;
                cmd.Parameters.Add("@sz_compid", OdbcType.VarChar, 30).Value = acc.Company.ToUpper();
                cmd.Parameters.Add("@sz_deptid", OdbcType.VarChar, 30).Value = acc.Department.ToUpper();
                cmd.Parameters.Add("@sz_password", OdbcType.VarChar, 20).Value = acc.Password;
                OdbcDataReader dr = cmd.ExecuteReader();

                if (dr.Read())
                {
                    l_deptpk = dr.GetInt32(0);
                    l_deptpri = dr.GetInt32(1);
                    ret = true;
                }

                dr.Close();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on checkLogon: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw e;
            }
            return ret;
        }

        private string getAudioFilesPath()
        {
            string sz_audiofiles_path = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);
            sz_audiofiles_path = sz_audiofiles_path.Replace("file:\\", "");
            DirectoryInfo di = Directory.GetParent(sz_audiofiles_path);
            di = di.Parent;
            sz_audiofiles_path = di.FullName + "\\audiofiles\\";
            return sz_audiofiles_path;
        }

        public string[] generateVoice(VOCFILE[] message, Int64 l_refno)
        {
            string[] filenames = new string[message.Length];
            FileStream fs;
            BinaryWriter bw;
            string source, destination;
            convertClass conv;

            string sz_audiofiles_path = getAudioFilesPath();

            for (int i = 0; i < message.Length; ++i)
            {
                switch (message[i].type)
                {
                    case VOCTYPE.TTS:
                        //tts to WAV
                        //WAV to RAW
                        filenames[i] = TTS(message[i].sz_tts_string, i, message[i].l_langpk, l_refno);
                        break;
                    case VOCTYPE.WAV:
                        //convert to raw
                        fs = new FileStream(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", FileMode.Create, FileAccess.Write);
                        bw = new BinaryWriter(fs);
                        bw.Write(message[i].audiodata);
                        bw.Flush();
                        bw.Close();
                        fs.Close();

                        conv = new convertClass();
                        conv.WAV2RAW(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", sz_audiofiles_path + "v" + l_refno + "_" + i + ".raw");

                        filenames[i] = "v" + l_refno + "_" + i + ".raw";
                        source = sz_audiofiles_path + filenames[i];
                        destination = sz_eat_path + filenames[i];

                        File.Copy(source, destination);
                        break;
                    case VOCTYPE.RAW:
                        //make copy to server
                        bw = new BinaryWriter(File.Open(sz_eat_path + "v" + l_refno + "_" + i + ".raw", FileMode.Create));
                        bw.Write(message[i].audiodata);
                        bw.Flush();
                        bw.Close();
                        filenames[i] = "v" + l_refno + "_" + i + ".raw";
                        conv = new convertClass();
                        EventLog.WriteEntry("sendMergedVoice", "Converting raw to wav: " + sz_eat_path + "v" + l_refno + "_" + i + ".raw - " + "\\audiofiles\\" + "v" + l_refno + "_" + i + ".wav");
                        conv.RAW2WAV(sz_eat_path + "v" + l_refno + "_" + i + ".raw", sz_eat_path + "v" + l_refno + "_" + i + ".wav"); // This is special for MergeMessage
                        File.Move(sz_eat_path + "v" + l_refno + "_" + i + ".wav", "\\audiofiles\\" + "v" + l_refno + "_" + i + ".wav");
                        EventLog.WriteEntry("sendMergedVoice", "Wav file exists?: " + File.Exists(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav"));
                        break;
                }
            }
            return filenames;
        }

        public string TTS(string sz_message, int filenumber, LANGUAGE l_langpk, Int64 l_refno)
        {
            string szTempFileName = "v" + l_refno + "_" + filenumber + ".wav";
            string szFileName = "v" + l_refno + "_" + filenumber + ".wav";
            string szRawFileName = "v" + l_refno + "_" + filenumber + ".raw";
            string szTxtFileName = "v" + l_refno + "_" + filenumber + ".txt";
            string szLangFileName = "v" + l_refno + "_" + filenumber + ".lang";
            string ret = "";

            try
            {
                if (conn == null)
                {
                    conn = new OdbcConnection(sz_cstring);
                    conn.Open();
                    cmd = new OdbcCommand();
                    cmd.Connection = conn;
                }

                TTSLib.TTS objTXT2Wav = new TTSLib.TTS();
                cmd.Parameters.Clear();
                cmd.CommandText = "SELECT * FROM BBTTSLANG WHERE l_langpk=?";
                cmd.Parameters.Add("@l_langpk", OdbcType.Int).Value = l_langpk; // This should be BigInt, but that is not supported by ayanami
                OdbcDataReader dr = cmd.ExecuteReader();
                if (dr.HasRows)
                {
                    StreamWriter fLangFile = new StreamWriter(szLangFileName, false, Encoding.GetEncoding("iso-8859-1"));
                    fLangFile.WriteLine("$" + dr["sz_speaker"]);
                    fLangFile.WriteLine("£" + dr["sz_modename"]);
                    fLangFile.WriteLine("%" + dr["sz_manufacturer"]);
                    fLangFile.Close();
                }
                dr.Close();

                File.Move(szLangFileName, sz_tts_path + szLangFileName);

                if (File.Exists(sz_tts_path + szTempFileName))
                    File.Delete(sz_tts_path + szTempFileName);

                objTXT2Wav.Say_wavex(sz_message, sz_tts_path + szTxtFileName, 30);

                // This is waiting for TTS to finish converting wav to raw
                DateTime future = DateTime.Now.AddSeconds(10);
                bool b = true;

                while (b)
                {
                    if (File.Exists(sz_tts_path + szRawFileName))
                        if (new FileInfo(sz_tts_path + szRawFileName).Length > 0)
                            b = false;
                    if (b && 0 <= DateTime.Compare(DateTime.Now, future))
                        b = false;
                }
                EventLog.WriteEntry("sendMergedVoice", "Does the wav file exist?: " + sz_tts_path + szFileName);
                if (File.Exists(sz_tts_path + szFileName))
                {
                    EventLog.WriteEntry("sendMergedVoice", "Wav file exists: " + sz_tts_path + szFileName);
                    File.Copy(sz_tts_path + szFileName, getAudioFilesPath() + szFileName);
                }

                if (File.Exists(sz_tts_path + szRawFileName))
                    File.Move(sz_tts_path + szRawFileName, sz_eat_path + szRawFileName);

                ret = szRawFileName;

            }
            catch (Exception e)
            {
                if (File.Exists(szLangFileName))
                    File.Delete(szLangFileName);
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Failed in TTS: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
            }

            return ret;
        }
        private enum FaultCode
        {
            Client = 0,
            Server = 1
        }

        private SoapException raiseException(string uri,
                                    string webServiceNamespace,
                                    string errorMessage,
                                    string errorSource,
                                    FaultCode code)
        {
            XmlQualifiedName faultCodeLocation = null;
            //Identify the location of the FaultCode
            switch (code)
            {
                case FaultCode.Client:
                    faultCodeLocation = SoapException.ClientFaultCode;
                    break;
                case FaultCode.Server:
                    faultCodeLocation = SoapException.ServerFaultCode;
                    break;
            }
            XmlDocument xmlDoc = new XmlDocument();
            //Create the Detail node
            XmlNode rootNode = xmlDoc.CreateNode(XmlNodeType.Element,
                               SoapException.DetailElementName.Name,
                               SoapException.DetailElementName.Namespace);
            //Build specific details for the SoapException
            //Add first child of detail XML element.
            XmlNode errorNode = xmlDoc.CreateNode(XmlNodeType.Element, "Error",
                                                  webServiceNamespace);

            //Create and set the value for the ErrorMessage node
            XmlNode errorMessageNode = xmlDoc.CreateNode(XmlNodeType.Element,
                                                        "ErrorMessage",
                                                        webServiceNamespace);
            errorMessageNode.InnerText = errorMessage;
            //Create and set the value for the ErrorSource node
            XmlNode errorSourceNode =
              xmlDoc.CreateNode(XmlNodeType.Element, "ErrorSource",
                                webServiceNamespace);
            errorSourceNode.InnerText = errorSource;
            //Append the Error child element nodes to the root detail node.            
            errorNode.AppendChild(errorMessageNode);
            errorNode.AppendChild(errorSourceNode);
            //Append the Detail node to the root node
            rootNode.AppendChild(errorNode);
            //Construct the exception
            SoapException soapEx = new SoapException(errorMessage,
                                                     faultCodeLocation);
            //Raise the exception  back to the caller
            return soapEx;
        }
    }
}
