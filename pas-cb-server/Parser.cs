using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Collections;
using System.Threading;
using System.Globalization;

namespace pas_cb_server
{
    class Parser
    {
        static int lRetVal = Constant.OK;

        public static void CheckFiles()
        {
            while (CBServer.running)
            {
                try
                {
                    int lRetryInterval = Settings.GetValue("RetryInterval", 60); // defaults to 60 seconds
                    string[] fileEntries = null;

                    fileEntries = Directory.GetFiles(Settings.sz_parsepath + "eat\\", "*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
                    foreach (string fileName in fileEntries)
                    {
                        if (!CBServer.running) break;
                        lRetVal = ParseXMLFile(fileName);
                        if (lRetVal == Constant.OK)
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\done\\"));
                        }
                        else if (lRetVal == Constant.RETRY)
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\retry\\"));
                        }
                        else // lRetVal -2 (Constant.FAILED)
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\failed\\"));
                        }
                    }

                    fileEntries = Directory.GetFiles(Settings.sz_parsepath + "retry\\", "*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
                    foreach (string fileName in fileEntries)
                    {
                        if (!CBServer.running) break;
                        FileInfo fFile = new FileInfo(fileName);
                        if (fFile.LastAccessTime.AddSeconds(lRetryInterval).CompareTo(DateTime.Now) <= 0)
                        {
                            lRetVal = ParseXMLFile(fileName);
                            if (lRetVal == Constant.OK)
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\done\\"));
                            }
                            else if (lRetVal == Constant.RETRY)
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\retry\\"));
                            }
                            else // lRetVal -2 (Constant.FAILED)
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\failed\\"));
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.WriteLog("Exception during file poll: " + e.Message, 2);
                }
                Thread.Sleep(1000);
            }
            Log.WriteLog("Stopped parser thread", 9);
        }

        private static int ParseXMLFile(string sz_filename)
        {
            int lReturn = Constant.OK;
            Hashtable hRet = new Hashtable();

            XmlTextReader oReader = new XmlTextReader(sz_filename);
            XmlDocument oDoc = new XmlDocument();
            Settings oUser = new Settings();

            try
            {
                if (oReader.Read())
                {
                    oDoc.Load(oReader);
                    if (oDoc.SelectSingleNode("cb") != null)
                    {
                        if (oDoc.SelectSingleNode("cb").Attributes.GetNamedItem("operation") != null)
                        {
                            switch (oDoc.SelectSingleNode("cb").Attributes.GetNamedItem("operation").Value)
                            {
                                case "NewAlertPolygon":
                                    if (!Settings.SetUserValues(oDoc.SelectSingleNode("cb").Attributes, oUser))
                                        return Constant.FAILED;
                                    hRet = CreateAlert(oDoc.SelectSingleNode("cb"), oUser);
                                    break;
                                case "UpdateAlert":
                                    if (!Settings.SetUserValues(oDoc.SelectSingleNode("cb").Attributes, oUser))
                                        return Constant.FAILED;
                                    hRet = UpdateAlert(oDoc.SelectSingleNode("cb"), oUser);
                                    break;
                                case "KillAlert":
                                    if (!Settings.SetUserValues(oDoc.SelectSingleNode("cb").Attributes, oUser))
                                        return Constant.FAILED;
                                    hRet = KillAlert(oDoc.SelectSingleNode("cb"), oUser);
                                    break;
                                default:
                                    Log.WriteLog("ERROR: Operation not recognized", 2);
                                    lReturn = Constant.FAILED;
                                    break;
                            }
                            // look through Hashtable hRet and check status(es) for operator(s)
                            // retry operators that failed?
                            if (hRet.ContainsValue(Constant.RETRY)) // if at least one operator has RETRY, return RETRY
                                lReturn = Constant.RETRY;
                            else if(hRet.ContainsValue(Constant.FAILED)) // if none has retry, but at least one has FAILED, return FAILED
                                lReturn = Constant.FAILED;
                            
                            // if neither these, assume OK (default)
                        }
                    }
                    oReader.Close();
                }
            }
            catch (Exception e)
            {
                oReader.Close();
                Log.WriteLog("ERROR: Exception while reading XML " + e.Message, "ERROR: Exception while reading XML " + e.ToString(), 0);
                lReturn = Constant.FAILED;
            }
            return lReturn;
        }

        private static Hashtable CreateAlert(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert = new AlertInfo();

            // create an alert for each operator
            foreach (Operator op in oUser.operators)
            {
                switch (op.l_type)
                {
                    case 1: // AlertiX (not supported)
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                    case 2: // one2many
                        ret.Add(op.l_operator, CB_one2many.CreateAlert(oAlert, op));
                        break;
                    case 3: // tmobile
                        ret.Add(op.l_operator, CB_tmobile.CreateAlert(oAlert, op));
                        break;
                    default:
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                }
            }
            return ret;
        }
        private static Hashtable UpdateAlert(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert = new AlertInfo();

            // update a given alert at each operator
            foreach (Operator op in oUser.operators)
            {
                switch (op.l_type)
                {
                    case 1: // AlertiX (not supported)
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                    case 2: // one2many
                        ret.Add(op.l_operator, CB_one2many.UpdateAlert(oAlert, op));
                        break;
                    case 3: // tmobile
                        ret.Add(op.l_operator, CB_tmobile.UpdateAlert(oAlert, op));
                        break;
                    default:
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                }
            }
            return ret;
        }
        private static Hashtable KillAlert(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert = new AlertInfo();

            // kill a given alert at each operator
            foreach (Operator op in oUser.operators)
            {
                switch (op.l_type)
                {
                    case 1: // AlertiX (not supported)
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                    case 2: // one2many
                        ret.Add(op.l_operator, CB_one2many.KillAlert(oAlert, op));
                        break;
                    case 3: // tmobile
                        ret.Add(op.l_operator, CB_tmobile.KillAlert(oAlert, op));
                        break;
                    default:
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                }
            }
            return ret;
        }
        private static int GetAlertStatus()
        {
            int ret = 0;
            // get alert status
            // all alerts for all operators?
            return ret;
        }
    }

    public class AlertInfo
    {
        // info needed to start a cb sending
        public int l_projectpk;
        public int l_refno;
        public int l_comppk;
        public int l_deptpk;
        public int l_userpk;
        public string sz_password;

        public int l_sched_utc;
        public int l_validity;

        public AlertMessage alert_message;
        public List<PolyPoint> alert_polygon;
    }

    public class AlertMessage
    {
        public int l_channel;
        public string sz_text;
    }

    public class PolyPoint
    {
        public float x; // longitude
        public float y; // latitude
    }
}
