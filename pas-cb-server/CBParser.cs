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
                if(!CBServer.parserpaused)
                    try
                    {
                        string[] fileEntries = null;
                        fileEntries = Directory.GetFiles(Settings.sz_parsepath + "eat\\", "CB_*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
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

                        fileEntries = Directory.GetFiles(Settings.sz_parsepath + "retry\\", "CB_*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
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
                                    Log.WriteLog(String.Format("{0} failed, will retry.", fileName), 1);
                                    File.SetLastAccessTime(fileName, DateTime.Now);
                                    //File.Move(fileName, fileName.Replace("\\retry\\", "\\retry\\"));
                                }
                                else // lRetVal -2 (Constant.FAILED)
                                {
                                    Thread.Sleep(500);
                                    Log.WriteLog(String.Format("{0} failed.", fileName), 2);
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
                    XmlNode xmlCB = oDoc.SelectSingleNode("cb");
                    if (xmlCB != null)
                    {
                        if (xmlCB.Attributes.GetNamedItem("operation") != null)
                        {
                            if (!Settings.SetUserValues(xmlCB.Attributes, oUser))
                                return Constant.FAILED;

                            switch (xmlCB.Attributes.GetNamedItem("operation").Value)
                            {
                                case "NewAlertPolygon":
                                    hRet = CreateAlert(xmlCB, oUser, Operation.NEWAREA);
                                    break;
                                case "NewAlertPLMN":
                                    hRet = CreateAlert(xmlCB, oUser, Operation.NEWPLMN);
                                    break;
                                case "NewAlertTest":
                                    hRet = CreateAlert(xmlCB, oUser, Operation.NEWPLMN_TEST);
                                    break;
                                case "NewAlertHeartbeat":
                                    hRet = CreateAlert(xmlCB, oUser, Operation.NEWPLMN_HEARTBEAT);
                                    break;
                                case "UpdateAlert":
                                    hRet = UpdateAlert(xmlCB, oUser);
                                    break;
                                case "KillAlert":
                                    hRet = KillAlert(xmlCB, oUser);
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
                            else if (hRet.ContainsValue(Constant.FAILED)) // if none has retry, but at least one has FAILED, return FAILED
                                lReturn = Constant.FAILED;

                            // if neither these, assume OK (default)
                        }
                        else
                        {
                            Log.WriteLog("ERROR: Missing operation attribute in CB tag", 2);
                            lReturn = Constant.FAILED;
                        }
                    }
                    else
                    {
                        Log.WriteLog("ERROR: Missing CB tag in XML file", 2);
                        lReturn = Constant.FAILED;
                    }
                    oReader.Close();
                }
            }
            catch (Exception e)
            {
                oReader.Close();
                Log.WriteLog(
                    "ERROR: Exception while reading XML " + e.Message, 
                    "ERROR: Exception while reading XML " + e.ToString(), 
                    0);
                lReturn = Constant.FAILED;
            }
            return lReturn;
        }

        private static Hashtable CreateAlert(XmlNode xmlCB, Settings oUser, Operation operation)
        {
            Hashtable ret = new Hashtable();
            LBAParameter lbaparam = new LBAParameter();
            AlertInfo oAlert;

            int l_getalert = ParseAlertXml(xmlCB, oUser, operation, out oAlert);
            if (l_getalert != Constant.OK)
            {
                ret.Add(0, l_getalert);
                return ret;
            }

            // Set values from LBAParam
            oAlert.l_repetitioninterval = lbaparam.l_repetitioninterval;

            // create an alert for each operator
            foreach (Operator op in oUser.operators)
            {
                // check for refno in LBASEND
                if (Database.VerifyRefno(oAlert.l_refno, op.l_operator))
                {
                    // check if message has been submitted for this operator
                    string sz_jobid = Database.GetJobID(op, oAlert.l_refno);
                    if (sz_jobid == null)
                    {
                        Log.WriteLog(String.Format("{0} (op={1}) failed checking if broadcast was already submitted, aborting", oAlert.l_refno, op.sz_operatorname, sz_jobid), 0);
                        ret.Add(op.l_operator, Database.UpdateTries(oAlert.l_refno, Constant.FAILEDRETRY, Constant.FAILED, 0, op.l_operator, LBATYPE.CB));
                    }
                    else if (sz_jobid != "")
                    {
                        Log.WriteLog(String.Format("{0} (op={1}) broadcast already submitted (ref={2})", oAlert.l_refno, op.sz_operatorname, sz_jobid), 0);
                        ret.Add(op.l_operator, Constant.OK);
                    }
                    else
                    {
                        // insert LBAHISTCELL if sending isn't heartbeat sending (no status for heartbeat messages
                        if(operation != Operation.NEWPLMN_HEARTBEAT)
                            Database.InsertHistCell(oAlert.l_refno, op.l_operator);
                        switch (op.l_type)
                        {
                            case 1: // AlertiX (not supported)
                                ret.Add(op.l_operator, Constant.FAILED);
                                break;
                            case 2: // one2many
                                ret.Add(op.l_operator, CB_one2many.CreateAlert(oAlert, op, operation));
                                break;
                            case 3: // tmobile
                                ret.Add(op.l_operator, CB_tmobile.CreateAlert(oAlert, op, operation));
                                break;
                            default:
                                ret.Add(op.l_operator, Constant.FAILED);
                                break;
                        }
                    }
                }
                else
                {
                    Log.WriteLog(
                        String.Format("{0} (op={1}) no record found in LBASEND", oAlert.l_refno, op.sz_operatorname)
                        , 2);
                    ret.Add(op.l_operator, Constant.FAILED);
                }
            }
            return ret;
        }
        private static Hashtable UpdateAlert(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert = new AlertInfo();

            //GetAlert(xmlCB, oUser, Operation.UPDATE, out oAlert);
            int l_getalert = ParseAlertXml(xmlCB, oUser, Operation.UPDATE, out oAlert);
            if (l_getalert != Constant.OK)
            {
                ret.Add(0, l_getalert);
                return ret;
            }

            // update a given alert at each operator
            foreach (Operator op in oUser.operators)
            {
                // check for refno in LBASEND
                if (Database.VerifyRefno(oAlert.l_refno, op.l_operator))
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
                else
                {
                    Log.WriteLog(
                        String.Format("{0} (op={1}) no record found in LBASEND", oAlert.l_refno, op.sz_operatorname)
                        , 2);
                    ret.Add(op.l_operator, Constant.FAILED);
                }
            }
            return ret;
        }
        private static Hashtable KillAlert(XmlNode xmlCB, Settings oUser)
        {
            Hashtable ret = new Hashtable();
            AlertInfo oAlert = new AlertInfo();

            // GetAlert(xmlCB, oUser, Operation.KILL, out oAlert);
            int l_getalert = ParseAlertXml(xmlCB, oUser, Operation.KILL, out oAlert);
            if (l_getalert != Constant.OK)
            {
                ret.Add(0, l_getalert);
                return ret;
            }

            // got info about user and alert, log to paslog
            Database.WritePasLog(oUser, oAlert, Operation.KILL);

            // kill a given alert at each operator
            foreach (Operator op in oUser.operators)
            {
                // get jobid and check if message has expired
                bool expired = false;
                string sz_jobid = Database.GetJobID(op, oAlert.l_refno);

                if (Database.GetSendingStatus(op, oAlert.l_refno, out expired) == Constant.CANCELLING)
                {
                    // message has expired, stop trying to cancel and set to finished instead
                    if (expired)
                    {
                        Database.SetSendingStatus(op, oAlert.l_refno, Constant.FINISHED);
                        ret.Add(op.l_operator, Constant.FAILED);
                    }
                }
                else if (sz_jobid == "")
                {
                    Log.WriteLog(String.Format("{0} (op={1}) (KillAlert) FAILED (could not find JobID, ignoring)", oAlert.l_refno, op.sz_operatorname), 1);
                    ret.Add(op.l_operator, Constant.OK);
                }
                else
                {
                    switch (op.l_type)
                    {
                        case 1: // AlertiX (not supported)
                            ret.Add(op.l_operator, Constant.FAILED);
                            break;
                        case 2: // one2many
                            ret.Add(op.l_operator, CB_one2many.KillAlert(oAlert, op, sz_jobid));
                            break;
                        case 3: // tmobile
                            ret.Add(op.l_operator, CB_tmobile.KillAlert(oAlert, op, sz_jobid));
                            break;
                        default:
                            ret.Add(op.l_operator, Constant.FAILED);
                            break;
                    }
                }
            }
            return ret;
        }
        private static int ParseAlertXml(XmlNode xmlCB, Settings oUser, Operation type, out AlertInfo oAlert)
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
                oAlert.l_sched_utc = long.Parse(xmlCB.Attributes.GetNamedItem("l_sched_utc").Value);


                if (type == Operation.KILL)
                    return Constant.OK; // return if kill, don't need more information

                oAlert.l_validity = int.Parse(xmlCB.Attributes.GetNamedItem("l_validity").Value);

                XmlAttributeCollection xml_message = xmlCB.SelectSingleNode("textmessages").SelectSingleNode("message").Attributes;
                oAlert.alert_message.l_channel = int.Parse(xml_message.GetNamedItem("l_channel").Value);
                oAlert.alert_message.sz_text = Tools.CleanMessage(xml_message.GetNamedItem("sz_text").Value);

                if (type == Operation.NEWAREA) // get poly for new area messages
                {
                    foreach (XmlNode xml_pt in xmlCB.SelectSingleNode("alertpolygon").ChildNodes)
                    {
                        if (xml_pt.Name == "polypoint")
                        {
                            PolyPoint pt = new PolyPoint();
                            pt.x = float.Parse(xml_pt.Attributes.GetNamedItem("lon").Value, coorformat);
                            pt.y = float.Parse(xml_pt.Attributes.GetNamedItem("lat").Value, coorformat);

                            oAlert.alert_polygon.Add(pt);
                        }
                        else
                        {
                            Log.WriteLog(String.Format("Warning, unrecognized element in alertpolygon: {0}", xml_pt.Name), 1);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                Log.WriteLog(
                    String.Format("Incorrect XML format: {0}", e.Message),
                    String.Format("Incorrect XML format: {0}", e), 
                    2);
                return Constant.FAILED;
            }

            return Constant.OK;
        }
    }

    public class AlertInfo
    {
        // info needed to start a cb sending
        public int l_projectpk;
        public int l_refno;
        public int l_repetitioninterval;

        public long l_sched_utc;
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
    public enum Operation
    {
        NEWAREA,
        NEWPLMN,
        NEWPLMN_TEST,
        NEWPLMN_HEARTBEAT,
        UPDATE,
        KILL
    }
}
