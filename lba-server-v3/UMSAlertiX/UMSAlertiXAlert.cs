using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Globalization;
using System.Net;
using System.Data.Odbc;
using UMSAlertiX.AlertiX3;
using com.ums.UmsCommon;
using com.ums.UmsCommon.CoorConvert;

namespace UMSAlertiX
{
    public class UMSAlertiXAlert
    {
        UMSAlertiXController oController;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public int SendArea(ref XmlDocument oDoc, ref UserValues oUser) // return: 0=ok, -1=retry, -2=failed
        {
            StoredTarget storedTarget = new StoredTarget();
            List<MessageSelector> messages = new List<MessageSelector>();
            SubmissionMode execMode = SubmissionMode.SIMULATE;
            smsDisplayMode displayMode = smsDisplayMode.NORMAL;
            UMSAlertiXArea oArea = new UMSAlertiXArea();
            oArea.SetController(ref oController);

            List<String> additionalSubscribers = new List<string>();
            List<FilterTag> filters = new List<FilterTag>();

            AlertPeriod alertPeriod = null;

            string szUpdateSQL;

            int lRefNo;
            int lRequestType = 0;
            int lValidity = oController.message_validity;

            int lReturn = Constant.OK;  // return code for method

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                XmlNode oTextMessages;

                // Check if refno is present, this is required both for sending and status updating, return immediately
                // if refno is missing (can't update status)
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    oController.log.WriteLog("ERROR: (SendArea) Missing refno");
                    return Constant.FAILED;
                }

                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (SendArea)");

                // check for other required fields that prevent sending
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid") != null)
                {
                    storedTarget.name = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid").Value;
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing area id");
                    SetStatus(lRefNo, Constant.ERR_NOATTR_AREA);
                    return Constant.FAILED;
                }

                // The rest of the fields aren't required for sending and can default if missing
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                {
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.NORMAL;
                    }
                    else if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 2)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.SILENT;
                    }
                }

                // validity is optional, but needs to run before GetAlertMsg
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (lValidity <= 0)
                    lValidity = GetAlertContentExpiry(oController.message_validity);

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    int lRetVal = GetAlertMsg(oTextMessages, messages, displayMode, lValidity);
                    if (lRetVal != 0)
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing country code for textmessage(s)");
                        SetStatus(lRefNo, lRetVal);
                        return Constant.FAILED;
                    }
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing textmessage tag");
                    SetStatus(lRefNo,Constant.ERR_NOTAG_MSG);
                    return Constant.FAILED;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null) // optional
                {
                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        additionalSubscribers.Add(oNode.Attributes.GetNamedItem("msisdn").Value);
                    }
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null) // optional
                {
                    List<String> whiteLists = new List<string>();

                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        filters.Add(new FilterTag() { filterType = FilterType.SubscriberIncludeList, value = oNode.Attributes.GetNamedItem("name").Value });
                    }
                }

                // set parsing lba status for all operators where status is either new (199) or retry (290) to
                // prevent successfull operators to be sent a second time on retries where only one operator failed
                szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_status IN (199,290) AND l_refno=" + lRefNo.ToString();
                oController.ExecDB(szUpdateSQL, oController.dsn);
            }
            else
            {
                //missing LBA tag, can't log error so just return failed immediately
                oController.log.WriteLog("ERROR: (SendArea) Missing LBA tag");
                return Constant.FAILED;
            }

            if (!oArea.CheckStoredArea(storedTarget, oUser))
            {
                oController.log.WriteLog(lRefNo.ToString() + " ERROR sending to stored target, area does not exist, and failed to create.");
                return -2;
            }

            return SendAlert(oUser.operators, 
                storedTarget, 
                messages, 
                execMode, 
                additionalSubscribers, 
                filters,
                alertPeriod,
                ref szUpdateSQL, 
                lRefNo, 
                ref lRequestType, 
                ref lReturn);
        }

        public int SendPolygon(ref XmlDocument oDoc, ref UserValues oUser)
        {
            PolygonTarget polygonTarget = new PolygonTarget();
            
            List<MessageSelector> messages = new List<MessageSelector>();
            SubmissionMode execMode = SubmissionMode.SIMULATE;
            smsDisplayMode displayMode = smsDisplayMode.NORMAL;

            List<String> additionalSubscribers = new List<string>();
            List<FilterTag> filters = new List<FilterTag>();
            AlertPeriod alertPeriod = null;
            
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            string szUpdateSQL;

            int lRefNo;
            int lValidity = oController.message_validity;
            int lRequestType = 0;
            int lReturn = Constant.OK;

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                XmlNode oTextMessages;

                // Check if refno is present, this is required both for sending and status updating, return immediately
                // if refno is missing (can't update status)
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    oController.log.WriteLog("ERROR: (SendPolygon) Missing refno");
                    return Constant.FAILED;
                }

                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (SendPolygon)");

                // check for other required fields that prevent sending
                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon") != null)
                {
                    //szAreaName.value = lRefNo.ToString();
                    polygonTarget.polygon = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
                    {
                        UTMeast = 0;
                        UTMnorth = 0;
                        szZone = "";

                        uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                        polygonTarget.polygon[i] = new Point();
                        polygonTarget.polygon[i].easting = UTMeast;
                        polygonTarget.polygon[i].northing = UTMnorth;

                        i++;
                    }
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing polygon");
                    SetStatus(lRefNo, Constant.ERR_NOTAG_POLY);
                    return Constant.FAILED;
                }

                // The rest of the fields aren't required for sending and can default if missing
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                {
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.NORMAL;
                    }
                    else if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 2)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.SILENT;
                    }
                }

                // validity is optional but needs to run before GetAlertMsg
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (lValidity <= 0)
                    lValidity = GetAlertContentExpiry(oController.message_validity);
                
                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    int lRetVal = GetAlertMsg(oTextMessages, messages, displayMode, lValidity);
                    if (lRetVal != 0)
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing country code for textmessage(s)");
                        SetStatus(lRefNo, lRetVal);
                        return Constant.FAILED;
                    }
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing textmessage tag");
                    SetStatus(lRefNo, Constant.ERR_NOTAG_MSG);
                    return Constant.FAILED;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null) // optional
                {
                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        additionalSubscribers.Add(oNode.Attributes.GetNamedItem("msisdn").Value);
                    }
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null) // optional
                {
                    List<String> whiteLists = new List<string>();

                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        filters.Add(new FilterTag() { filterType = FilterType.SubscriberIncludeList, value = oNode.Attributes.GetNamedItem("name").Value });
                    }
                }
                                
                // set parsing lba status for all operators where status is either new (199) or retry (290) to
                // prevent successfull operators to be sent a second time on retries where only one operator failed
                szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_status IN (199,290) AND l_refno=" + lRefNo.ToString();
                oController.ExecDB(szUpdateSQL, oController.dsn);
            }
            else
            {
                //missing LBA tag, can't log error so just return failed immediately
                oController.log.WriteLog("ERROR: (SendArea) Missing LBA tag");
                return Constant.FAILED;
            }

            return SendAlert(oUser.operators, 
                polygonTarget, 
                messages, 
                execMode, 
                additionalSubscribers, 
                filters,
                alertPeriod,
                ref szUpdateSQL, 
                lRefNo, 
                ref lRequestType, 
                ref lReturn);
        }

        public int SendEllipse(ref XmlDocument oDoc, ref UserValues oUser)
        {
            PolygonTarget polygonTarget = new PolygonTarget();

            List<MessageSelector> messages = new List<MessageSelector>();
            SubmissionMode execMode = SubmissionMode.SIMULATE;
            smsDisplayMode displayMode = smsDisplayMode.NORMAL;

            List<String> additionalSubscribers = new List<string>();
            List<FilterTag> filters = new List<FilterTag>();
            AlertPeriod alertPeriod = null;

            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            string szUpdateSQL;

            int lRefNo;
            int lValidity = oController.message_validity;
            int lRequestType = 0;
            int lReturn = Constant.OK;

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                XmlNode oTextMessages;

                // Check if refno is present, this is required both for sending and status updating, return immediately
                // if refno is missing (can't update status)
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    oController.log.WriteLog("ERROR: (SendPolygon) Missing refno");
                    return Constant.FAILED;
                }

                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (SendEllipse)");

                //check for other required fields that prevent sending
                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse") != null)
                {
                    polygonTarget.polygon = new Point[36];

                    int steps = 36;
                    double centerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centerx").Value, provider);
                    double centery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centery").Value, provider);
                    double cornerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornerx").Value, provider);
                    double cornery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornery").Value, provider);

                    double[,] arrPoly = new double[steps, 2];

                    if (UCommon.ConvertEllipseToPolygon(centerx, centery, cornerx, cornery, steps, 0, ref arrPoly))
                    {
                        // put array of doubles into array of points
                        for (int i = 0; i < steps; i++)
                        {
                            UTMeast = 0;
                            UTMnorth = 0;
                            szZone = "";

                            uCoConv.LL2UTM(23, arrPoly[i, 1], arrPoly[i, 0], 33, ref UTMnorth, ref UTMeast, ref szZone);

                            polygonTarget.polygon[i] = new Point();
                            polygonTarget.polygon[i].easting = UTMeast;
                            polygonTarget.polygon[i].northing = UTMnorth;
                        }

                    }
                    else
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " ERROR: Failed to convert ellipse to polygon");
                        SetStatus(lRefNo, Constant.ERR_EllipseToPoly);
                        return Constant.FAILED;
                    }
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing ellipse");
                    SetStatus(lRefNo, Constant.ERR_NOTAG_POLY);
                    return Constant.FAILED;
                }

                // Set execMode and displayMode before getting message
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                {
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.NORMAL;
                    }
                    else if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 2)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.SILENT;
                    }
                }

                // validity is optional but needs to run before GetAlertMsg
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (lValidity <= 0)
                    lValidity = GetAlertContentExpiry(oController.message_validity);

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    int lRetVal = GetAlertMsg(oTextMessages, messages, displayMode, lValidity);
                    if (lRetVal != 0)
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing country code for textmessage(s)");
                        SetStatus(lRefNo, lRetVal);
                        return Constant.FAILED;
                    }
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing textmessage tag");
                    SetStatus(lRefNo, Constant.ERR_NOTAG_MSG);
                    return Constant.FAILED;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null) // optional
                {
                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        additionalSubscribers.Add(oNode.Attributes.GetNamedItem("msisdn").Value);
                    }
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null) // optional
                {
                    List<String> whiteLists = new List<string>();

                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        filters.Add(new FilterTag() { filterType = FilterType.SubscriberIncludeList, value = oNode.Attributes.GetNamedItem("name").Value });
                    }
                }

                // set parsing lba status for all operators where status is either new (199) or retry (290) to
                // prevent successfull operators to be sent a second time on retries where only one operator failed
                szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_status IN (199,290) AND l_refno=" + lRefNo.ToString();
                oController.ExecDB(szUpdateSQL, oController.dsn);
            }
            else
            {
                //missing LBA tag, can't log error so just return failed immediately
                oController.log.WriteLog("ERROR: (SendArea) Missing LBA tag");
                return Constant.FAILED;
            }

            return SendAlert(oUser.operators,
                polygonTarget,
                messages,
                execMode,
                additionalSubscribers,
                filters,
                alertPeriod,
                ref szUpdateSQL,
                lRefNo,
                ref lRequestType,
                ref lReturn);
        }

        public int ExecutePreparedAlert(ref XmlDocument oDoc)
        {
            SnapshotTarget storedTarget = new SnapshotTarget();
            List<MessageSelector> messages = new List<MessageSelector>();
            SubmissionMode execMode = SubmissionMode.SIMULATE;
            smsDisplayMode displayMode = smsDisplayMode.NORMAL;

            List<String> additionalSubscribers = new List<string>();
            List<FilterTag> filters = new List<FilterTag>();

            AlertPeriod alertPeriod = null;
            int validity; // used to be AlertPeriod

            string szUpdateSQL;

            int lRefNo;
            int lRequestType = 0;
            int lValidity = oController.message_validity;
            int lOperator;

            int lReturn = Constant.OK;  // return code for method

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                // Check if refno is present, this is required both for sending and status updating, return immediately
                // if refno is missing (can't update status)
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    oController.log.WriteLog("ERROR: (Execute Prepared Alert) Missing refno");
                    return Constant.FAILED;
                }

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_operator") != null)
                {
                    lOperator = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_operator").Value);
                }
                else
                {
                    oController.log.WriteLog("ERROR: (Execute Prepared Alert) Missing operator");
                    return Constant.FAILED;
                }

                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (Execute Prepared Alert)");

                // check for other required fields that prevent sending
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid") != null)
                {
                    storedTarget.snapshotId = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing job id");
                    SetStatus(lRefNo, Constant.ERR_NOATTR_AREA);
                    return Constant.FAILED;
                }

                // The rest of the fields aren't required for sending and can default if missing
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                {
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.NORMAL;
                    }
                    else if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 2)
                    {
                        execMode = SubmissionMode.NORMAL;
                        displayMode = smsDisplayMode.SILENT;
                    }
                }

                // TODO: Get validity from LBASEND
                validity = GetAlertContentExpiry(lRefNo);

                // TODO: Get messages from TEXT and TEXT_CC
                int lRetVal = GetAlertMsg(lRefNo, messages, displayMode, validity);
                if (lRetVal != 0)
                {
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: Missing country code for textmessage(s)");
                    SetStatus(lRefNo, lRetVal);
                    return Constant.FAILED;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null) // optional
                {
                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        additionalSubscribers.Add(oNode.Attributes.GetNamedItem("msisdn").Value);
                    }
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null) // optional
                {
                    List<String> whiteLists = new List<string>();

                    // TODO: add errorhandling for malformated tags
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        filters.Add(new FilterTag() { filterType = FilterType.SubscriberIncludeList, value = oNode.Attributes.GetNamedItem("name").Value });
                    }
                }

                // set parsing lba status for all operators where status is either new (199) or retry (290) to
                // prevent successfull operators to be sent a second time on retries where only one operator failed
                szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_status IN (199,290) AND l_refno=" + lRefNo.ToString();
                oController.ExecDB(szUpdateSQL, oController.dsn);
            }
            else
            {
                //missing LBA tag, can't log error so just return failed immediately
                oController.log.WriteLog("ERROR: (SendArea) Missing LBA tag");
                return Constant.FAILED;
            }

            return SendAlert(new Operator[] { oController.GetOperator(lOperator) },
                storedTarget,
                messages,
                execMode,
                additionalSubscribers,
                filters,
                alertPeriod,
                ref szUpdateSQL,
                lRefNo,
                ref lRequestType,
                ref lReturn);
        }

        public int CancelPreparedAlert(ref XmlDocument oDoc)
        {
            int lReturn = Constant.OK;
            int lRefNo;
            int lOperator;
            string snapshotId;

            snapshotId = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            lOperator = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_operator").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (cancel prepared alert)");

            // exit if sending has already been cancelled or executed
            if (oController.GetRequestType(lRefNo) == 1 && oController.GetSendingStatus(lRefNo) == 800)
                return Constant.FAILED;

            lReturn = CancelPreparedAlert(snapshotId, lRefNo, lOperator);

            return lReturn;
        }
        public int CancelPreparedAlert(string snapshotId, int lRefNo, int lOperator)
        {
            int lReturn = Constant.OK;
            int lRetval;
            string szUpdateSQL;
            Operator op = oController.GetOperator(lOperator);

            szUpdateSQL = "UPDATE LBASEND SET l_status=800 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + op.l_operator.ToString();
            lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);

            AlertixWsApiService aAlert = new AlertixWsApiService();

            aAlert.Url = op.sz_url + oController.alertapi;

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aAlert.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

            AlertiX3.Response aResponse = new AlertiX3.Response();

            try
            {
                // TODO: Make sure this is the snapshot id!
                aResponse = aAlert.deleteTarget(snapshotId);// aAlert.cancelPreparedAlert(idJob);
                if (aResponse.result == Result.SUCCESS)
                {
                    szUpdateSQL = "UPDATE LBASEND SET l_status=2000, l_response=" + (int)aResponse.result + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + op.l_operator.ToString();
                    lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Cancelled (res=" + aResponse.result.ToString() + ") (job=" + snapshotId + ")");
                }
                else if (aResponse.resultSpecified)
                {
                    lReturn = UpdateTries(lRefNo, 290, Constant.ERR_cancelPreparedAlert, -1, op.l_operator);
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Cancel ERROR: (res=" + aResponse.result.ToString() + ") (job=" + snapshotId + ")");
                }
                else
                {
                    lReturn = UpdateTries(lRefNo, 290, Constant.EXC_cancelPreparedAlert, -1, op.l_operator);
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Cancel ERROR: No response received (job=" + snapshotId + ")");
                }
            }
            catch (Exception e)
            {
                lReturn = UpdateTries(lRefNo, 290, Constant.EXC_cancelPreparedAlert, -1, op.l_operator);
                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Cancel Exception: " + e.ToString(), lRefNo.ToString() + " (" + op.sz_operatorname + ") Cancel Exception: " + e.Message.ToString());
            }

            return lReturn;
        }

        // help functions
        private int SendAlert(Operator[] operators, Target target, List<MessageSelector> messages, SubmissionMode execMode, List<String> additionalSubscribers, List<FilterTag> filters, AlertPeriod alertPeriod, ref string szUpdateSQL, int lRefNo, ref int lRequestType, ref int lReturn)
        {
            AlertixWsApiService aAlert = new AlertixWsApiService();

            foreach (Operator op in operators)
            {
                try
                {
                    // check if sending has been submitted already
                    string szJobID = oController.GetJobID(lRefNo, op.l_operator);
                    if (szJobID != "")
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") already submitted with jobid: " + szJobID);
                    }
                    else
                    {
                        aAlert.Url = op.sz_url + oController.alertapi;

                        NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                        Uri uri = new Uri(aAlert.Url);

                        ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                        aAlert.Credentials = objAuth;
                        aAlert.PreAuthenticate = true;

                        lRequestType = oController.GetRequestType(lRefNo);
                        if (lRequestType == 0)
                        {
                            oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Executing alert (execute mode=" + execMode.ToString() + ")");
                            Alert aResponse = aAlert.executeAlert(target, messages.ToArray(), alertPeriod, execMode, PositioningMode.LAST, filters.ToArray(), null, Priority.DEFAULT, additionalSubscribers.ToArray());

                            if (aResponse.result == Result.SUCCESS)
                            {
                                szUpdateSQL = "UPDATE LBASEND SET l_status=300, l_started_ts=" + DateTime.Now.ToString("yyyyMMddHHmmss") + ", l_expires_ts=" + DateTime.Now.AddMinutes(alertPeriod.duration.minutes).ToString("yyyyMMddHHmmss") + ", l_response=" + (int)aResponse.result + ", sz_jobid='" + aResponse.id + "' WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + op.l_operator.ToString();
                                oController.ExecDB(szUpdateSQL, oController.dsn);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Delivered (res=" + aResponse.result.ToString() + ") (job=" + aResponse.id + ")");
                            }
                            else if (aResponse.resultSpecified)
                            {
                                lReturn = UpdateTries(lRefNo, 290, Constant.ERR_executeAreaAlert, (int)aResponse.result, op.l_operator);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: (res=" + aResponse.result.ToString() + ") " + aResponse.description);
                            }
                            else
                            {
                                lReturn = UpdateTries(lRefNo, 290, Constant.EXC_executeAreaAlert, -1, op.l_operator);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: No response received");
                            }
                        }
                        else
                        {
                            oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Preparing alert (execute mode=" + execMode.ToString() + ")");
                            Snapshot aResponse = aAlert.createSnapshot(target, null);

                            if (aResponse.result == Result.SUCCESS)
                            {
                                // TODO: Uncertain if I can use DateTime.Now
                                szUpdateSQL = "UPDATE LBASEND SET l_status=300, l_expires_ts=" + DateTime.Now.AddMinutes(alertPeriod.duration.minutes).ToString("yyyyMMddHHmmss") + ", l_response=" + (int)aResponse.result + ", sz_jobid='" + aResponse.id + "' WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + op.l_operator.ToString();
                                oController.ExecDB(szUpdateSQL, oController.dsn);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Delivered (res=" + aResponse.result.ToString() + ") (job=" + aResponse.id + ")");
                            }
                            else if (aResponse.resultSpecified)
                            {
                                lReturn = UpdateTries(lRefNo, 290, Constant.ERR_prepareAreaAlert, (int)aResponse.result, op.l_operator);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: (res=" + aResponse.result.ToString() + ") " + aResponse.description);
                            }
                            else
                            {
                                lReturn = UpdateTries(lRefNo, 290, Constant.EXC_prepareAreaAlert, -1, op.l_operator);
                                oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: No response received");
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    if (lRequestType == 0)
                        lReturn = UpdateTries(lRefNo, 290, Constant.EXC_executeAreaAlert, -1, op.l_operator);
                    else
                        lReturn = UpdateTries(lRefNo, 290, Constant.EXC_prepareAreaAlert, -1, op.l_operator);
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: " + e.ToString(), lRefNo.ToString() + " (" + op.sz_operatorname + ") ERROR: " + e.Message.ToString());
                }
            }

            return lReturn;
        }

        private int GetAlertMsg(XmlNode oTextMessages, List<MessageSelector> messages, smsDisplayMode displayMode, int validity)
        {
            try
            {
                // loop through all the different messages/languages
                foreach (XmlNode oCountryMsg in oTextMessages.ChildNodes)
                {
                    if (oCountryMsg.SelectSingleNode("ccode") != null)
                    {
                        // check if current node is the default message
                        if (oCountryMsg.SelectSingleNode("ccode").InnerText == "-1")
                        {
                            SmsContent msg = new SmsContent();

                            msg.displayMode = displayMode;
                            msg.text = oCountryMsg.Attributes.GetNamedItem("sz_text").Value;
                            msg.originator = oCountryMsg.Attributes.GetNamedItem("sz_cb_oadc").Value;
                            msg.expiryMinutes = validity;

                            messages.Add(new DefaultSelector() { content = msg });
                        }
                        else
                        {
                            SmsContent msg = new SmsContent();

                            msg.displayMode = displayMode;
                            msg.text = oCountryMsg.Attributes.GetNamedItem("sz_text").Value; ;
                            msg.originator = oCountryMsg.Attributes.GetNamedItem("sz_cb_oadc").Value;
                            msg.expiryMinutes = validity;
                            

                            List<string> ccs = new List<string>();
                            foreach (XmlNode oCCode in oCountryMsg.ChildNodes)
                            {
                                foreach(string isocc in oController.getISOFromCC(Convert.ToInt32(oCCode.InnerText)))
                                    ccs.Add(isocc);
                            }

                            messages.Add(new NationalitySelector() { content = msg, codes = ccs.ToArray() });
                        }
                    }
                    else // missing ccode tag, return 
                    {
                        return Constant.ERR_NOTAG_CCODE; //GetAlertMsg exception
                    }
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog("GetAlertMsg EXCEPTION: " + e.ToString(), "GetAlertMsg EXCEPTION: " + e.Message.ToString());
                return Constant.EXC_GetAlertMsg; //GetAlertMsg exception
            }

            // Need to sort messages so that DefaultSelector() comes last
            sortMessages(ref messages);

            return Constant.OK; //ok
        }

        private void sortMessages(ref List<MessageSelector> messages)
        {
            messages.Sort((MessageSelector a, MessageSelector b) =>
            {
                return a is DefaultSelector ? 0 : 1;
            });
        }


        private int UpdateTries(int lRefNo, int lTempStatus, int lEndStatus, int lResponse, int lOperator)
        {
            int lMaxTries = 1; // Is really retries so 4 will give 5 tries total
            int lRetVal = 0;
            string szQuery;

            szQuery = "sp_lba_upd_sendtries " + lRefNo.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + lMaxTries.ToString() + ", " + lResponse.ToString() + ", " + lOperator.ToString();

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            dbConn.Open();
            rsRequestType = cmd.ExecuteReader();

            if (rsRequestType.Read())
                if (!rsRequestType.IsDBNull(0))
                    lRetVal = rsRequestType.GetInt32(0);

            cmd.Dispose();
            dbConn.Close();

            return lRetVal;
        }

        private int SetStatus(int lRefNo, int lStatus) // update all
        {
            return SetStatus(lRefNo, lStatus, -1);
        }

        private int SetStatus(int lRefNo, int lStatus, int lOperator)
        {
            int lRetVal = 0;
            string szQuery;

            if(lOperator==-1) // update all
                szQuery = "UPDATE LBASEND SET l_status=" + lStatus.ToString() + " WHERE l_refno=" + lRefNo.ToString();
            else
                szQuery = "UPDATE LBASEND SET l_status=" + lStatus.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString();

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmd = new OdbcCommand(szQuery, dbConn);
            OdbcDataReader rsRequestType;

            dbConn.Open();
            rsRequestType = cmd.ExecuteReader();

            if (rsRequestType.Read())
                if (!rsRequestType.IsDBNull(0))
                    lRetVal = rsRequestType.GetInt32(0);

            cmd.Dispose();
            dbConn.Close();

            return lRetVal;
        }

        private int GetAlertContentExpiry(int lRefNo)
        {
            DateTime end = oController.GetExpiry(lRefNo);
            
            TimeSpan duration = end - DateTime.Now;
            
            return duration.Minutes;
        }
        
        private AlertPeriod GetAlertPeriod(int lRefNo)
        {
            AlertPeriod ret = new AlertPeriod();
            Duration duration = new Duration();

            ret.start = DateTime.Now;

            DateTime end = oController.GetExpiry(lRefNo);
            TimeSpan timeSpan = end - ret.start;
            duration.minutes = timeSpan.Minutes;
            ret.duration = duration;

            return ret;
        }

        private Duration GetAlertDuration(int minutes)
        {
            Duration duration = new Duration();

            DateTime start = DateTime.Now;
            DateTime end = DateTime.Now.AddMinutes(minutes);
            TimeSpan timeSpan = end - start;
            duration.minutes = timeSpan.Minutes;

            return duration;
        }

        private int GetAlertMsg(int lRefNo, List<MessageSelector> messages, smsDisplayMode displayMode, int validity)
        {
            List<Message> ccMessages = oController.GetMessages(lRefNo);

            foreach (Message ccMessage in ccMessages)
            {
                // check if current node is the default message
                if (ccMessage.countryCodes.Count == 1 && ccMessage.countryCodes[0] == -1)
                {
                    SmsContent msg = new SmsContent();

                    msg.displayMode = displayMode;
                    msg.text = ccMessage.messageText;
                    msg.originator = ccMessage.originator;
                    msg.expiryMinutes = validity;
                    

                    messages.Add(new DefaultSelector() { content = msg });
                }
                else
                {
                    SmsContent msg = new SmsContent();

                    msg.displayMode = displayMode;
                    msg.text = ccMessage.messageText;
                    msg.originator = ccMessage.originator;
                    msg.expiryMinutes = validity;
                    

                    List<string> ccs = new List<string>();
                    foreach (int countryCode in ccMessage.countryCodes)
                    {
                        foreach (string isocc in oController.getISOFromCC(countryCode))
                            ccs.Add(isocc);
                    }

                    messages.Add(new NationalitySelector() { content = msg, codes = ccs.ToArray() });
                }
            }

            return Constant.OK;
        }
    }
}
