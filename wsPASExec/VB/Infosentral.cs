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

using libums2_csharp;
using com.ums.UmsCommon;

namespace com.ums.VB
{
    public class Infosentral
    {
        
        private OdbcConnection conn;
        private OdbcCommand cmd;
        private int l_deptpk;
        private long l_messagepk;
        private long l_refno;
        private string sz_path_sound;

        private void openConnection()
        {
            conn = new OdbcConnection(String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd));
            conn.Open();
            cmd = new OdbcCommand();
            cmd.Connection = conn;
        }

        public long storeMessage(ACCOUNT acc, string sz_name, VOCFILE message)
        {
            try
            {
                sz_path_sound = UCommon.UPATHS.sz_path_predefined_messages;

                openConnection();

                if (!checkLogon(acc, ref l_deptpk))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Infosentral.cs storeMessage(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "storeMessage:checkLogon", FaultCode.Client);

                cmd.Parameters.Clear();

                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_get_bbmessagepk";
                OdbcDataReader dr = cmd.ExecuteReader();
                while(dr.Read())
                    l_messagepk = long.Parse(dr[0].ToString());

                dr.Close();

                if (message.type == VOCTYPE.TTS)
                {
                    string sz_filename = sz_path_sound + l_deptpk + "\\" + l_messagepk + ".txt";
                    StreamWriter sw = File.CreateText(sz_filename);
                    sw.Write(message.sz_tts_string);
                    sw.Close();
                }

    			cmd.CommandText = "INSERT INTO BBMESSAGES(l_deptpk, l_type, sz_name, sz_description, l_messagepk, l_langpk, sz_filename) VALUES(?,?,?,?,?,?,?)";
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                cmd.Parameters.Add("@l_type", OdbcType.SmallInt).Value = 3;
                cmd.Parameters.Add("@sz_name", OdbcType.VarChar, 50).Value = sz_name;
                cmd.Parameters.Add("@sz_description", OdbcType.VarChar, 255).Value = "";
                cmd.Parameters.Add("@l_messagepk", OdbcType.Decimal).Value = l_messagepk;
                cmd.Parameters.Add("@l_langpk", OdbcType.Decimal).Value = message.l_langpk;
                cmd.Parameters.Add("@sz_filename", OdbcType.VarChar, 255).Value = "";
                if(cmd.ExecuteNonQuery()<1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs storeMessage(): Error inserting into BBMESSAGES", "storeMessage", FaultCode.Client);
                    
                // This is used to get a unique file name
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_refno_out";
                dr = cmd.ExecuteReader();
                while(dr.Read())
                    l_refno = Int64.Parse(dr[0].ToString());

                dr.Close();
                
                SendVoice voice = new SendVoice();
                
                voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
                voice.EatPath = UCommon.UPATHS.sz_path_voice;
                voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
                voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
                
                string[] tmpfilename = voice.generateVoice(new VOCFILE[] { message }, l_refno);
                
                bool rawexists = false;
                DateTime future = DateTime.Now.AddSeconds(60);
                while (!rawexists || (future < DateTime.Now && !rawexists))
                {
                    if (File.Exists(UCommon.UPATHS.sz_path_voice + tmpfilename[0])) // Its like this because generateVoice moves this file here
                        rawexists = true;
                }
                if (!rawexists)
                    raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs storeMessage(): Rawfile was not created", "storeMessage", FaultCode.Server);
                
                File.Move(UCommon.UPATHS.sz_path_ttsserver + tmpfilename[0].Substring(0, tmpfilename[0].Length - 3) + "wav", sz_path_sound + l_deptpk + "\\" + l_messagepk + ".wav");
                File.Move(UCommon.UPATHS.sz_path_voice + tmpfilename[0], sz_path_sound + l_deptpk + "\\" + l_messagepk + ".raw");

            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Infosentral.cs", "Error storing new message: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs storeMessage(): " + e.Message, "storeMessage", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
            return l_messagepk;
        }
        public void test()
        {
            l_deptpk = 1;
            l_messagepk = 30304;
            string tmpfilename = "v399494_0.raw";
            string ting = UCommon.UPATHS.sz_path_ttsserver + tmpfilename.Substring(0,tmpfilename.Length-3) + "wav";
            string tang = UCommon.UPATHS.sz_path_predefined_messages + l_deptpk + "\\" + l_messagepk + ".wav";
            string ling = UCommon.UPATHS.sz_path_ttsserver + tmpfilename;
            string lung = UCommon.UPATHS.sz_path_predefined_messages + l_deptpk + "\\" + l_messagepk + ".raw";
        }

        public void attatchMessage(ACCOUNT acc, string sz_number, long l_messagepk)
        {
            string sz_messagepath = UCommon.UPATHS.sz_path_predefined_messages;

            try
            {
                openConnection();

                if (!checkLogon(acc, ref l_deptpk))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Infosentral.cs attatchMessage(): Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "attachMessage:checkLogon", FaultCode.Client);

                // Does the user have privileges to the number and message that is about to be used?
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "SELECT DISTINCT * FROM BBDEPTNUMBERS dn, BBMESSAGES m WHERE dn.l_deptpk=m.l_deptpk AND m.l_deptpk=? AND dn.sz_number=? AND m.l_messagepk=?";
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_number;
                cmd.Parameters.Add("@l_messagepk", OdbcType.Int).Value = l_messagepk;
                OdbcDataReader dr = cmd.ExecuteReader();
                if (!dr.Read())
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs attatchMessage(): The user does not have permission to use this number or message", "attachMessage", FaultCode.Client);
                    
                dr.Close();

                string source = sz_messagepath + l_deptpk + "\\" + l_messagepk + ".raw";
                string destination = "";
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_is_getpath ?";
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_number;
                dr = cmd.ExecuteReader();

                if (dr.Read())
                {
                    destination = dr.GetString(0);
                    string temp = destination.Substring(2);
                    destination = UCommon.UPATHS.sz_path_infosent + temp;
                    File.Copy(source, destination, true);
                }
                else
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs attatchMessage(): Error finding message sound file", "attachMessage", FaultCode.Server);
                    
                dr.Close();

                // Finally remove previous attachment and set new
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "UPDATE BBMESSAGES SET sz_number=NULL WHERE sz_number=? AND l_deptpk=?";
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_number;
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                if(cmd.ExecuteNonQuery()<1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs attatchMessage(): Error updating BBMESSAGES remove previous attachment", "attachMessage", FaultCode.Client);

                //if 1 = yey
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "UPDATE BBMESSAGES SET sz_number=? WHERE l_messagepk=? AND l_deptpk=?";
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_number;
                cmd.Parameters.Add("@l_messagepk", OdbcType.Int).Value = l_messagepk;
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                if (cmd.ExecuteNonQuery() < 1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs attatchMessage(): Error updating BBMESSAGES adding new attachment", "attachMessage", FaultCode.Client);
                
                cmd.Parameters.Clear();
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Infosentral.cs", "Error attaching message: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs attatchMessage(): " + e.Message, "attachMessage", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
        }

        public void setRedirectNumber(ACCOUNT acc, string sz_backbonenumber, string sz_dtmf, string sz_redirectnumber) {
            try
            {
                conn = new OdbcConnection(String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd));
                conn.Open();
                cmd = new OdbcCommand();
                cmd.Connection = conn;

                if (!checkLogon(acc, ref l_deptpk))
                    throw raiseException("uri", "http://ums.no/ws/vb/", String.Format("Error in logon credentials for userpk/comppk {0}/{1}", acc.Company, acc.Department), "checkLogon", FaultCode.Client);

                // Does the user have privilages to this number?
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = "SELECT * FROM BBDEPTNUMBERS WHERE l_deptpk=? AND sz_number=?";
                cmd.Parameters.Add("@l_deptpk", OdbcType.Int).Value = l_deptpk;
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_backbonenumber;
                OdbcDataReader dr = cmd.ExecuteReader();
                if (!dr.Read())
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs setRedirectNumber(): The user does not have permission to use this number", "setRedirectNumber", FaultCode.Client);

                dr.Close();

                // Is it a redirect number?

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_infos_isredirectnum ?";
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_redirectnumber;
                dr = cmd.ExecuteReader();
                if(!dr.Read())
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs setRedirectNumber(): The redirect number is not valid", "setRedirectNumber", FaultCode.Client);
                dr.Close();

                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "sp_set_redirnum ?, ?, ?";
                cmd.Parameters.Add("@sz_number", OdbcType.VarChar, 20).Value = sz_backbonenumber;
                cmd.Parameters.Add("@c_dtmf", OdbcType.Char, 1).Value = sz_dtmf;
                cmd.Parameters.Add("@sz_redir", OdbcType.VarChar, 20).Value = sz_redirectnumber;
                int ra = cmd.ExecuteNonQuery();
                if (ra < 1)
                    throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs setRedirectNumber(): There was a problem with the DTMF", "setRedirectNumber", FaultCode.Client);
            }
            catch (Exception e)
            {
                System.Diagnostics.EventLog.WriteEntry("Infosentral.cs", "Error setting redirect number: " + e.Message + " _ " + e.StackTrace, System.Diagnostics.EventLogEntryType.Error);
                throw raiseException("uri", "http://ums.no/ws/vb/", "Infosentral.cs setRedirectNumber(): " + e.Message, "setRedirectNumber", FaultCode.Server);
            }
            finally
            {
                conn.Close();
            }
        }

        private bool checkLogon(ACCOUNT acc, ref int l_deptpk)
        {
            bool ret = false;

            try
            {
                cmd.Parameters.Clear();
                cmd.CommandText = "SELECT BD.l_deptpk " +
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

        public void testSoapException()
        {
            throw raiseException("uri", "http://ums.no/ws/vb/", "hahaha, lååål", "checkLogon", FaultCode.Server);
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
                                                     faultCodeLocation, uri,
                                                     rootNode);
            //Raise the exception  back to the caller
            return soapEx;
        }
    }
}
