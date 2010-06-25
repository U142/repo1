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
    class CBParser
    {
        static int lRetVal = Constant.OK;

        public static void CheckFilesThread()
        {
            while (CBServer.running)
            {
                try
                {
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
                        if (fFile.LastAccessTime.AddSeconds(Settings.l_retryinterval).CompareTo(DateTime.Now) <= 0)
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
            Interlocked.Decrement(ref Settings.threads);
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
                                case "NewAlertPLMN":
                                    if (!Settings.SetUserValues(oDoc.SelectSingleNode("cb").Attributes, oUser))
                                        return Constant.FAILED;
                                    hRet = CreateAlertPLMN(oDoc.SelectSingleNode("cb"), oUser);
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
            AlertInfo oAlert;

            GetAlert(xmlCB, oUser, Operation.NEWAREA, out oAlert);

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
        private static Hashtable CreateAlertPLMN(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert;

            GetAlert(xmlCB, oUser, Operation.NEWAREA, out oAlert);

            // create an alert for each operator
            foreach (Operator op in oUser.operators)
            {
                switch (op.l_type)
                {
                    case 1: // AlertiX (not supported)
                        ret.Add(op.l_operator, Constant.FAILED);
                        break;
                    case 2: // one2many
                        ret.Add(op.l_operator, CB_one2many.CreateAlertPLMN(oAlert, op));
                        break;
                    case 3: // tmobile
                        ret.Add(op.l_operator, CB_tmobile.CreateAlertPLMN(oAlert, op));
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

            GetAlert(xmlCB, oUser, Operation.UPDATE, out oAlert);

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

            GetAlert(xmlCB, oUser, Operation.KILL, out oAlert);

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
        private static int GetAlert(XmlNode xmlCB, Settings oUser, Operation type, out AlertInfo oAlert)
        {
            oAlert = new AlertInfo();
            oAlert.alert_message = new AlertMessage();
            oAlert.alert_polygon = new List<PolyPoint>();

            NumberFormatInfo coorformat = new NumberFormatInfo();
            coorformat.NumberDecimalSeparator = ".";
            coorformat.NumberGroupSeparator = "";

            try
            {
                oAlert.l_projectpk = int.Parse(xmlCB.Attributes.GetNamedItem("l_projectpk").Value);
                oAlert.l_refno = int.Parse(xmlCB.Attributes.GetNamedItem("l_refno").Value);
                oAlert.l_sched_utc = int.Parse(xmlCB.Attributes.GetNamedItem("l_sched_utc").Value);

                if (type == Operation.KILL)
                    return Constant.OK; // return if kill, don't need more information

                oAlert.l_validity = int.Parse(xmlCB.Attributes.GetNamedItem("l_validity").Value);

                XmlAttributeCollection xml_message = xmlCB.SelectSingleNode("textmessages").SelectSingleNode("message").Attributes;
                oAlert.alert_message.l_channel = int.Parse(xml_message.GetNamedItem("l_channel").Value);
                oAlert.alert_message.sz_text = xml_message.GetNamedItem("sz_text").Value;

                if (type == Operation.NEWAREA) // get poly for new area messages
                {
                    foreach (XmlNode xml_pt in xmlCB.SelectSingleNode("alertpolygon").ChildNodes)
                    {
                        PolyPoint pt = new PolyPoint();
                        pt.x = float.Parse(xml_pt.Attributes.GetNamedItem("xcord").Value, coorformat);
                        pt.y = float.Parse(xml_pt.Attributes.GetNamedItem("ycord").Value, coorformat);

                        oAlert.alert_polygon.Add(pt);
                    }
                }
            }
            catch(Exception e)
            {
                Log.WriteLog(String.Format("Incorrect XML format: {0}", e.Message), 2);
            }

            return Constant.OK;
        }

        private enum Operation
        {
            NEWAREA,
            NEWPLNM,
            UPDATE,
            KILL
        }
    }

    public class AlertInfo
    {
        // info needed to start a cb sending
        public int l_projectpk;
        public int l_refno;

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
