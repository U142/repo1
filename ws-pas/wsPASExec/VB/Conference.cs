using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Data.Odbc;
using System.IO;
using System.Web.Services.Protocols;
using System.Xml;
using System.Text.RegularExpressions;
using System.Collections.Generic;

using libums2_csharp;
using com.ums.UmsCommon;


namespace wsPASExec.VB
{   
    public struct Status
    {
        public int ItemNumber;
        public string PhoneNumber;
        public string StatusMessage;
        public int StatusCode;
        public SendingStatusCode SendingStatus;
    }

    public enum SendingStatusCode
    {
        ReadingQueueVoiceBroadcast=1,
        ReadingVoiceBroadcast=2,
        WritingVoiceBroadcast=3,
        ReadingQueueBackbone=4,
        ReadingBackbone=5,
        SendingQueueBackbone=6,
        Finished=7
    }

    public struct Participant
    {
        public String PhoneNumber;
        public String PinCode;
        public String Name;
    }

    public class Conference
    {
        private OdbcCommand cmd;
        private OdbcConnection conn;
        private OdbcTransaction tran;
        private int l_deptpk;
        private int l_deptpri;
        private int l_comppk;
        private int l_refno;
        
        private void openConnection()
        {
            //conn = new OdbcConnection(String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd));
            conn = new OdbcConnection(String.Format("DSN={0};UID={1};PWD={2};", ConfigurationSettings.AppSettings["sz_db_dsn_aoba"], ConfigurationSettings.AppSettings["sz_db_uid"], ConfigurationSettings.AppSettings["sz_db_pwd"]));
            conn.Open();
            cmd = new OdbcCommand();
            cmd.Connection = conn;
        }

        public int scheduleConference(ACCOUNT acc, Participant[] participants, string sz_confname, long l_scheduleddatetime)
        {
            string sz_fields;
            string sz_sepused;
            int l_namepos;
            int l_phonepos;
            int l_lastantsep;
            string sz_date;
            string sz_time;
            string sz_scheddate = "";
            string sz_schedtime = "";
            l_comppk = -1;
            l_deptpk = -1;
            l_deptpri = -1;
            int b_usenofax;
            int b_removedup;
            int l_group;
            int l_type;
            int f_dynacall;
            int l_addresstypes;
            int l_userpk;
            int[] arr_groups;
            //string sz_eat_path = UCommon.UPATHS.sz_path_voice;
            string sz_eat_path = ConfigurationSettings.AppSettings["sz_path_voice"];
            checkDateTime(ref sz_scheddate, ref sz_schedtime, l_scheduleddatetime);

            if (participants.Length < 2)
                throw raiseException("", "http://ums.no/ws/vb/", "Need at least 2 participants to start a conference", "scheduleConference", FaultCode.Client);

            openConnection();

            if (!checkLogon(acc, ref l_deptpk, ref l_deptpri, ref l_comppk))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Conference.cs scheduleConference(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "scheduleConference:checkLogon", FaultCode.Client);

            l_refno = getRefno();
                if (l_refno == -1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Conference.cs scheduleConference(): Error getting refno", "conference:getRefno", FaultCode.Server);

                try
                {
                    tran = conn.BeginTransaction();
                    cmd.Transaction = tran;
                    // Insert voice to MDVSENDINGFO
                    cmd.CommandType = CommandType.Text;
                    //cmd.CommandText = "INSERT INTO MDVSENDINGINFO(sz_fields, sz_sepused, l_namepos, l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_removedup, l_group, sz_groups, l_type, f_dynacall, l_addresstypes, l_userpk) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    cmd.CommandText = "INSERT INTO MDVSENDINGINFO(l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, l_scheddate, l_schedtime, sz_sendingname, " +
                        "l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_removedup, l_group, l_type, f_dynacall, l_addresstypes) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    //cmd.Parameters.Add("@sz_fields", OdbcType.Text, 16).Value = sz_fields; // ""
                    //cmd.Parameters.Add("@sz_sepused", OdbcType.VarChar, 10).Value = sz_sepused; // ""
                    //cmd.Parameters.Add("@l_namepos", OdbcType.Int).Value = l_namepos; // null
                    cmd.Parameters.Add("@l_addresspos", OdbcType.Int).Value = 0;//l_phonepos; // 0
                    cmd.Parameters.Add("@l_lastantsep", OdbcType.Int).Value = 0; // l_lastantsep; // 0
                    cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                    cmd.Parameters.Add("@l_createdate", OdbcType.Int).Value = int.Parse(DateTime.Now.ToString("yyyyMMdd"));
                    cmd.Parameters.Add("@l_createtime", OdbcType.Int).Value = int.Parse(DateTime.Now.ToString("HHmm"));
                    cmd.Parameters.Add("@l_scheddate", OdbcType.Int).Value = sz_scheddate;
                    cmd.Parameters.Add("@l_schedtime", OdbcType.Int).Value = sz_schedtime;
                    cmd.Parameters.Add("@sz_sendingname", OdbcType.VarChar, 255).Value = sz_confname;
                    cmd.Parameters.Add("@l_sendingstatus", OdbcType.Int).Value = 1;
                    cmd.Parameters.Add("@l_companypk", OdbcType.Int).Value = l_comppk;
                    cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                    cmd.Parameters.Add("@l_nofax", OdbcType.Int).Value = 0; // b_usenofax; // 0
                    cmd.Parameters.Add("@l_removedup", OdbcType.Int).Value = 1;// b_removedup; // 1
                    cmd.Parameters.Add("@l_group", OdbcType.Int).Value = 1; //l_group; // 1
                    //cmd.Parameters.Add("@sz_groups", OdbcType.VarChar,50).Value = "";
                    cmd.Parameters.Add("@l_type", OdbcType.Int).Value = 1; // l_type; // 1
                    cmd.Parameters.Add("@f_dynacall", OdbcType.TinyInt).Value = 1; // f_dynacall; // 1
                    cmd.Parameters.Add("@l_addresstypes", OdbcType.Int).Value = 0; // l_addresstypes; // Er 0
                    //cmd.Parameters.Add("@l_userpk", OdbcType.Decimal).Value = l_userpk; // Må ha
                    cmd.ExecuteNonQuery();

                    cmd.CommandType = CommandType.Text;
                    cmd.CommandText = "INSERT INTO MDVHIST(l_refno, l_item, sz_adrinfo) VALUES(?,?,?)";

                    for (int i = 0; i < participants.Length; ++i)
                    {
                        cmd.Parameters.Clear();
                        cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                        cmd.Parameters.Add("@l_item", OdbcType.Int).Value = i + 1;
                        cmd.Parameters.Add("@sz_adrinfo", OdbcType.VarChar, 255).Value = participants[i].PhoneNumber + " " + participants[i].Name;
                        cmd.ExecuteNonQuery();
                    }

                    tran.Commit();
                    tran.Dispose();
                    cmd.Dispose();

                    StreamWriter sw = File.CreateText(sz_eat_path + "d" + l_refno + ".tmp");
                    sw.WriteLine("/MDV");
                    sw.WriteLine("/MPC");
                    sw.WriteLine("/Company=" + acc.Company.ToUpper());
                    sw.WriteLine("/Department=" + acc.Department.ToUpper());
                    sw.WriteLine("/Pri=" + l_deptpri); // + param.Priority); // How is this defined?
                    sw.WriteLine("/SchedDate=" + sz_scheddate);//DateTime.Now.ToString("yyyyMMdd"));
                    sw.WriteLine("/SchedTime=" + sz_schedtime);//DateTime.Now.ToString("HHmm"));
                    sw.WriteLine("/Item=1"); // Hvordan bestemmes denne?
                    sw.WriteLine("/Name=" + sz_confname);
                    for (int i = 0; i < participants.Length; ++i)
                        sw.WriteLine("/CONF NA " + participants[i].PhoneNumber); //CONF || SIMU
                    //sw.WriteLine("/SIMU NA " + commaSeparate(cleanNumber(to[i].PhoneNumber))); //CONF || SIMU
                    sw.Close();

                    File.Move(sz_eat_path + "d" + l_refno + ".tmp", sz_eat_path + "d" + l_refno + ".adr");
                }
                catch (SoapException se)
                {
                    throw se;
                }
                catch (Exception e)
                {
                    if (tran != null)
                        tran.Rollback();
                    System.Diagnostics.EventLog.WriteEntry("Conference.cs", "Conference.cs scheduleConference(): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Conference.cs scheduleConference(): " + e.Message, "conference:scheduleConference", FaultCode.Server);
                }
                finally
                {
                    conn.Close();
                }
            return l_refno;

        }
        public List<Status> getConference(ACCOUNT acc, int l_refno)
        {
            List<Status> statuses = new List<Status>();
            openConnection();

            if (!checkLogon(acc, ref l_deptpk, ref l_deptpri, ref l_comppk))
                throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Conference.cs getConference(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "getConference:checkLogon", FaultCode.Client);

            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_ca_getstatusws ?";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                OdbcDataReader dr = cmd.ExecuteReader();

                Status status;
                while (dr.Read())
                {
                    status = new Status();
                    status.ItemNumber = dr.GetInt32(0);
                    status.PhoneNumber = dr.GetString(1);
                    status.StatusCode = dr.GetInt32(2);
                    status.StatusMessage = dr.GetString(3);
                    status.SendingStatus = (SendingStatusCode)dr.GetInt32(4);
                    statuses.Add(status);
                }
                dr.Close();
            }
            catch (SoapException se)
            {
                throw se;
            }
            catch (Exception e)
            {
                raiseException("", "http://ums.no/ws/vb/", "Conference.cs getConference()", "getConference()", FaultCode.Client);
            }
            return statuses;
        }

        public String redialParticipant(ACCOUNT acc, int l_refno, int l_item)
        {
            openConnection();

            if (!checkLogon(acc, ref l_deptpk, ref l_deptpri, ref l_comppk))
                throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Conference.cs redialParticipant(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "redialParticipant:checkLogon", FaultCode.Client);
            try
            {
                // Må sjekke om konferansen kjører
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "SELECT * FROM BBQ WHERE l_refno=? AND l_item=?";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("@l_item", OdbcType.Int).Value = l_item;
                OdbcDataReader dr = cmd.ExecuteReader();
                if (!dr.Read())
                {
                    dr.Close();
                    throw raiseException("", "http://ums.no/ws/vb/", "The conference has finished, unable to redial", "Conference.redialParticipant()", FaultCode.Client);
                }
                dr.Close();

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_caSetManualRetry ?, ?";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.Parameters.Add("@l_item", OdbcType.Int).Value = l_item;
                cmd.ExecuteNonQuery();
                cmd.Dispose();
            }
            catch (SoapException se)
            {
                throw se;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Conference.cs", "Conference.cs redialParticipant(): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("", "http://ums.no/ws/vb/", e.Message, "redialParticipant:checkLogon", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
            return "OK";
        }

        public string cancelConference(ACCOUNT acc, int l_refno)
        {
            openConnection(); 
            
            if (!checkLogon(acc, ref l_deptpk, ref l_deptpri, ref l_comppk))
                throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Conference.cs redialParticipant(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "redialParticipant:checkLogon", FaultCode.Client);
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "SELECT l_refno FROM BBCANCEL WHERE l_refno=?";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                OdbcDataReader dr = cmd.ExecuteReader();

                if (dr.HasRows)
                {
                    dr.Close();
                    cmd.Parameters.Clear();
                    cmd.CommandType = CommandType.Text;
                    cmd.CommandText = "DELETE FROM BBCANCEL WHERE l_refno=?";
                    cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                    cmd.ExecuteNonQuery();
                }
                if (dr != null && !dr.IsClosed)
                    dr.Close();

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "INSERT INTO BBCANCEL(l_refno,l_item) VALUES(?,-1)";
                cmd.Parameters.Add("@l_refno", OdbcType.Int).Value = l_refno;
                cmd.ExecuteNonQuery();
                return "Successfully cancelled conference with refno: " + l_refno;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Conference.cs", "Conference.cs cancelConference(): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("", "http://ums.no/ws/vb/", e.Message, "A problem occurred while cancelling conference. Please check that the reference number is correct", FaultCode.Client);
            }
            finally
            {
                conn.Close();
            }
        }

        private string commaSeparate(string[] participants)
        {
            string tmp = "";
            for (int i = 0; i < participants.Length; ++i)
            {
                tmp += participants[i];
                if (i + 1 < participants.Length)
                    tmp += ",";
            }
            return tmp;
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

        private void checkDateTime(ref string sz_date, ref string sz_time, long datetime) {
            
            // Date test
            if (datetime <= 0)
                datetime = Convert.ToInt64(DateTime.Now.ToString("yyyyMMddHHmm"));
            try
            {
                sz_date = Convert.ToString(datetime).Substring(0, 8);
                sz_time = Convert.ToString(datetime).Substring(8, 4);
            }
            catch (Exception e)
            {
                throw raiseException("uri", "http://ums.no/ws/vb/", "Conference.cs checkDateTime(): There was a problem with the schedule date, the format should be yyyyMMddHHmm. Your input: " + datetime, "Conference.checkDateTime()", FaultCode.Client);
            }
        }

        private int getRefno()
        {
            try
            {
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_refno_out";

                OdbcDataReader dr = cmd.ExecuteReader();
                int l_refno = -1;
                while (dr.Read())
                    l_refno = dr.GetInt32(0);

                dr.Close();
                return l_refno;
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("libums2-csharp.Voice.cs", "Error on getRefno(): " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                return -1;
            }
        }

        private bool checkLogon(ACCOUNT acc, ref int l_deptpk, ref int l_deptpri, ref int l_comppk)
        {
            bool ret = false;

            try
            {
                cmd.Parameters.Clear();
                cmd.CommandText = "SELECT BD.l_deptpk, BD.l_deptpri, BD.l_comppk " +
                                    "FROM BBCOMPANY BC, BBDEPARTMENT BD " +
                                   "WHERE BC.l_comppk=BD.l_comppk " +
                                     "AND BC.sz_compid=? " +
                                     "AND BD.sz_deptid=? " +
                                     "AND BD.sz_password=? " +
                                     "AND BD.f_conference=1";

                cmd.CommandType = CommandType.Text;
                cmd.Parameters.Add("@sz_compid", OdbcType.VarChar, 30).Value = acc.Company.ToUpper();
                cmd.Parameters.Add("@sz_deptid", OdbcType.VarChar, 30).Value = acc.Department.ToUpper();
                cmd.Parameters.Add("@sz_password", OdbcType.VarChar, 20).Value = acc.Password;
                OdbcDataReader dr = cmd.ExecuteReader();

                if (dr.Read())
                {
                    l_deptpk = dr.GetInt32(0);
                    l_deptpri = dr.GetInt32(1);
                    l_comppk = dr.GetInt32(2);
                    ret = true;
                }

                dr.Close();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Infosentral.cs", "Error on checkLogon: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", e.Message, "checkLogon", FaultCode.Server);
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
            SoapException soapEx = new SoapException(errorMessage, faultCodeLocation);
            //Raise the exception  back to the caller
            return soapEx;
        }
    }
}
