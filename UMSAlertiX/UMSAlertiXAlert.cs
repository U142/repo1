//#define FORCE_AREA
#define FORCE_SIMULATE
#define WHITELISTS

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Globalization;
using System.Net;
using System.Data.Odbc;
using UMSAlertiX.AlertiXAlertApi;
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

        public int SendArea(ref XmlDocument oDoc) // return: 0=success, -1=retry, -2=failed
        {
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            ExecuteMode execMode = ExecuteMode.SIMULATE;
            Msisdn[] arrMsisdn = null;
            WhiteListName[] arrWhiteList = null;
            AdditionalSubscribers cAddSubscribers = new AdditionalSubscribers();
            WhiteLists cWhiteLists = new WhiteLists();

            int lRefNo;
            string szUpdateSQL;
            int lRequestType = 0;
            int lValidity = oController.message_validity;

            int lReturn = 0;
            int lRetval = 0;
            if (oDoc.SelectSingleNode("LBA") != null)
            {

                XmlNode oTextMessages;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    GetAlertMsg(ref oTextMessages, ref msgAlert, lValidity);
                }
                else
                {
                    // error, can't send empty messages
                    return Constant.ERR_NOMSG;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null)
                {
                    arrMsisdn = new Msisdn[oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        arrMsisdn[i] = new Msisdn();
                        arrMsisdn[i].value = oNode.Attributes.GetNamedItem("msisdn").Value;

                        i++;
                    }

                    cAddSubscribers.subscribers = arrMsisdn;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null)
                {
                    arrWhiteList = new WhiteListName[oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        arrWhiteList[i] = new WhiteListName();
                        arrWhiteList[i].value = oNode.Attributes.GetNamedItem("name").Value;

                        i++;
                    }

                    cWhiteLists.whiteLists = arrWhiteList;
                }

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    // error, need refno
                    return Constant.ERR_NOREFNO;
                }

                szAreaName.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid").Value;
                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send area)");

                szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);

            }
            else
            {
                //missing LBA tag
                return Constant.ERR_NOTAG_LBA;
            }

            AlertApi aAlert = new AlertApi();
            AlertResponse aResponse = new AlertResponse();

            aAlert.Url = oController.alertapi; //"http://lbv.netcom.no:8080/alertix/AlertApi";

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aAlert.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;


#if FORCE_AREA
            oController.log.WriteLog(lRefNo.ToString() + " Force testarea");
            szAreaName.value = "TestArea";
#endif
#if FORCE_SIMULATE
            oController.log.WriteLog(lRefNo.ToString() + " force simulate");
            execMode = ExecuteMode.SIMULATE;
#endif
#if WHITELISTS
            if (arrWhiteList == null)
            {
                oController.log.WriteLog(lRefNo.ToString() + " force whitelist (UMS)");
                arrWhiteList = new WhiteListName[1];
                arrWhiteList[0] = new WhiteListName();
                arrWhiteList[0].value = "UMS";

                cWhiteLists.whiteLists = arrWhiteList;
            }
            else
            {
                oController.log.WriteLog(lRefNo.ToString() + " can't force whitelist, whitelist(s) already specified.");
            }
#endif

            try
            {
                lRequestType = oController.GetRequestType(lRefNo);
                if (lRequestType == 0)
                {
                    oController.log.WriteLog(lRefNo.ToString() + " Executing alert (execute mode=" + execMode.ToString() + ")");
                    aResponse = aAlert.executeAreaAlert(szAreaName, msgAlert, cWhiteLists, cAddSubscribers, execMode);
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " Preparing alert (execute mode=" + execMode.ToString() + ")");
                    aResponse = aAlert.prepareAreaAlert(szAreaName, msgAlert, cWhiteLists, cAddSubscribers);
                }

                if (aResponse.successful)
                {
                    szUpdateSQL = "UPDATE LBASEND SET l_status=300, l_response=" + aResponse.code.ToString() + ", sz_jobid='" + aResponse.jobId.value + "' WHERE l_refno=" + lRefNo.ToString();
                    lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                    oController.log.WriteLog(lRefNo.ToString() + " Delivered (res=" + aResponse.code.ToString() + ") (job=" + aResponse.jobId.value + ")");
                }
                else if (aResponse.codeSpecified)
                {
                    if(lRequestType == 0)
                        lReturn = UpdateTries(lRefNo, 290, 42011, aResponse.code);
                    else
                        lReturn = UpdateTries(lRefNo, 290, 42012, aResponse.code);
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: (res=" + aResponse.code.ToString() + ") " + aResponse.message);
                }
                else
                {
                    if (lRequestType == 0)
                        lReturn = UpdateTries(lRefNo, 290, 42001, -1);
                    else
                        lReturn = UpdateTries(lRefNo, 290, 42002, -1);
                    oController.log.WriteLog(lRefNo.ToString() + " ERROR: No response received");
                }
            }
            catch (Exception e)
            {
                if (lRequestType == 0)
                    lReturn = UpdateTries(lRefNo, 290, 42001, -1);
                else
                    lReturn = UpdateTries(lRefNo, 290, 42002, -1);
                oController.log.WriteLog(lRefNo.ToString() + " ERROR: " + e.ToString(), lRefNo.ToString() + " ERROR: " + e.Message.ToString());
            }

            return lReturn;
        }

        public int SendPolygon(ref XmlDocument oDoc)
        {
            Point[] arrPoint = null;
            Msisdn[] arrMsisdn = null;
            WhiteListName[] arrWhiteList = null;
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            AdditionalSubscribers cAddSubscribers = new AdditionalSubscribers();
            WhiteLists cWhiteLists = new WhiteLists();
            ExecuteMode execMode = ExecuteMode.SIMULATE;
            UTM uCoConv = new UTM();

            int lRefNo;
            double UTMnorth, UTMeast;
            string szZone;
            int lValidity = oController.message_validity;

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                XmlNode oTextMessages;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    GetAlertMsg(ref oTextMessages, ref msgAlert, lValidity);
                }
                else
                {
                    // error, can't send empty messages
                    return Constant.ERR_NOMSG;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null)
                {
                    arrMsisdn = new Msisdn[oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        arrMsisdn[i] = new Msisdn();
                        arrMsisdn[i].value = oNode.Attributes.GetNamedItem("msisdn").Value;

                        i++;
                    }

                    cAddSubscribers.subscribers = arrMsisdn;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null)
                {
                    arrWhiteList = new WhiteListName[oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        arrWhiteList[i] = new WhiteListName();
                        arrWhiteList[i].value = oNode.Attributes.GetNamedItem("name").Value;

                        i++;
                    }

                    cWhiteLists.whiteLists = arrWhiteList;
                }

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    // error, need refno
                    return Constant.ERR_NOREFNO;
                }
                
                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send polygon) (execute mode=" + execMode.ToString() + ")");

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon") != null)
                {
                    szAreaName.value = lRefNo.ToString();
                    arrPoint = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
                    {
                        UTMeast = 0;
                        UTMnorth = 0;
                        szZone = "";

                        uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                        arrPoint[i] = new Point();
                        arrPoint[i].x = UTMeast;
                        arrPoint[i].y = UTMnorth;
                        arrPoint[i].xSpecified = true;
                        arrPoint[i].ySpecified = true;

                        i++;
                    }

                    if (!SendCustomArea(ref arrPoint, ref szAreaName, ref msgAlert, lRefNo, cWhiteLists, cAddSubscribers, execMode))
                    {
                        // unkown error
                        return Constant.ERR_GENERAL;
                    }
                }
                else
                {
                    // no alert polygon
                    return Constant.ERR_NOTAG_ALERTPOLY;
                }
            }
            else 
            {
                //missing LBA tag
                return Constant.ERR_NOTAG_LBA;
            }

            return Constant.OK;
        }

/*        public int SendEllipse_old(ref XmlDocument oDoc)
        {
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            Point[] arrPoint = new Point[36];
            UTM uCoConv = new UTM();
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            int lRefNo;
            double UTMnorth, UTMeast;
            string szZone;
            int lValidity = oController.message_validity;

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            if (oDoc.SelectSingleNode("LBA") != null)
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null)
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

            XmlNode oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
            GetAlertMsg(ref oTextMessages, ref msgAlert, lValidity);
            if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send ellipse) (execute mode=" + execMode.ToString() + ")");

            szAreaName.value = lRefNo.ToString();
            arrPoint = new Point[36];

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

                    arrPoint[i] = new Point();
                    arrPoint[i].x = UTMeast;
                    arrPoint[i].y = UTMnorth;
                    arrPoint[i].xSpecified = true;
                    arrPoint[i].ySpecified = true;
                }

                if (!SendCustomArea(ref arrPoint, ref szAreaName, ref msgAlert, lRefNo, execMode))
                {
                    return Constant.ERR_GENERAL;
                }
            }
            else
            {
                return Constant.ERR_GENERAL;
            }
            return Constant.OK;
        }*/

        public int SendEllipse(ref XmlDocument oDoc)
        {
            Point[] arrPoint = null;
            Msisdn[] arrMsisdn = null;
            WhiteListName[] arrWhiteList = null;
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            AdditionalSubscribers cAddSubscribers = new AdditionalSubscribers();
            WhiteLists cWhiteLists = new WhiteLists();
            ExecuteMode execMode = ExecuteMode.SIMULATE;
            UTM uCoConv = new UTM();

            int lRefNo;
            double UTMnorth, UTMeast;
            string szZone;
            int lValidity = oController.message_validity;

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            if (oDoc.SelectSingleNode("LBA") != null)
            {
                XmlNode oTextMessages;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity") != null) // defaults to config value if null
                    lValidity = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages") != null)
                {
                    oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
                    GetAlertMsg(ref oTextMessages, ref msgAlert, lValidity);
                }
                else
                {
                    // error, can't send empty messages
                    return Constant.ERR_NOMSG;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers") != null)
                {
                    arrMsisdn = new Msisdn[oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("additionalsubscribers").ChildNodes)
                    {
                        arrMsisdn[i] = new Msisdn();
                        arrMsisdn[i].value = oNode.Attributes.GetNamedItem("msisdn").Value;

                        i++;
                    }

                    cAddSubscribers.subscribers = arrMsisdn;
                }

                if (oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists") != null)
                {
                    arrWhiteList = new WhiteListName[oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes.Count];
                    int i = 0;

                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        arrWhiteList[i] = new WhiteListName();
                        arrWhiteList[i].value = oNode.Attributes.GetNamedItem("name").Value;

                        i++;
                    }

                    cWhiteLists.whiteLists = arrWhiteList;
                }

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;

                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno") != null)
                {
                    lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
                }
                else
                {
                    // error, need refno
                    return Constant.ERR_NOREFNO;
                }

                oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send ellipse) (execute mode=" + execMode.ToString() + ")");

                szAreaName.value = lRefNo.ToString();
                arrPoint = new Point[36];

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

                        arrPoint[i] = new Point();
                        arrPoint[i].x = UTMeast;
                        arrPoint[i].y = UTMnorth;
                        arrPoint[i].xSpecified = true;
                        arrPoint[i].ySpecified = true;
                    }

                    if (!SendCustomArea(ref arrPoint, ref szAreaName, ref msgAlert, lRefNo, cWhiteLists, cAddSubscribers, execMode))
                    {
                        return Constant.ERR_GENERAL;
                    }
                }
                else
                {
                    return Constant.ERR_GENERAL;
                }
            }
            else
            {
                //missing LBA tag
                return Constant.ERR_NOTAG_LBA;
            }

            return Constant.OK;
        }

        public int ExecutePreparedAlert(ref XmlDocument oDoc)
        {
            int lReturn = Constant.OK;
            int lRefNo;
            int lRetval;
            string szUpdateSQL;

            AlertApi aAlert = new AlertApi();
            Response aResponse = new Response();
            JobId idJob = new JobId();
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            idJob.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;
            if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (execute prepared alert) (execute mode=" + execMode.ToString() + ")");

            // exit if sending has already been cancelled or executed
            if (oController.GetRequestType(lRefNo) == 1 && oController.GetSendingStatus(lRefNo) == 340)
                return Constant.ERR_CANCEL;

            aAlert.Url = oController.alertapi; //"http://lbv.netcom.no:8080/alertix/AlertApi";

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aAlert.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

#if FORCE_SIMULATE
            execMode = ExecuteMode.SIMULATE;
#endif
            
            try
            {
                aResponse = aAlert.executePreparedAlert(idJob, execMode);
                if (aResponse.codeSpecified)
                {
                    szUpdateSQL = "UPDATE LBASEND SET l_status=340, l_response=" + aResponse.code + " WHERE l_refno=" + lRefNo.ToString();
                    lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                }
                else
                    lReturn = Constant.ERR_GENERAL;
            }
            catch (Exception e)
            {
                szUpdateSQL = "UPDATE LBASEND SET l_status=42001 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                lReturn = Constant.ERR_GENERAL;
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            return lReturn;
        }

        public int CancelPreparedAlert(ref XmlDocument oDoc)
        {
            int lReturn = Constant.OK;
            int lRefNo;
            JobId idJob = new JobId();

            idJob.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (cancel prepared alert)");

            // exit if sending has already been cancelled or executed
            if (oController.GetRequestType(lRefNo) == 1 && oController.GetSendingStatus(lRefNo) == 800)
                return Constant.ERR_CANCEL;

            lReturn = CancelPreparedAlert(ref idJob, lRefNo);

            return lReturn;
        }

        public int CancelPreparedAlert(ref JobId idJob, int lRefNo)
        {
            int lReturn = Constant.OK;
            int lRetval;
            string szUpdateSQL;

            szUpdateSQL = "UPDATE LBASEND SET l_status=800 WHERE l_refno=" + lRefNo.ToString();
            lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);

            AlertApi aAlert = new AlertApi();
            Response aResponse = new Response();

            aAlert.Url = oController.alertapi; //"http://lbv.netcom.no:8080/alertix/AlertApi";

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aAlert.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;
            try
            {
                aResponse = aAlert.cancelPreparedAlert(idJob);
                if (aResponse.codeSpecified)
                {
                    szUpdateSQL = "UPDATE LBASEND SET l_status=2000, l_response=" + aResponse.code + " WHERE l_refno=" + lRefNo.ToString();
                    lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                }
                else
                    lReturn = Constant.ERR_GENERAL;
            }
            catch (Exception e)
            {
                szUpdateSQL = "UPDATE LBASEND SET l_status=42002 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                lReturn = Constant.ERR_GENERAL;
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            return lReturn;
        }

        // alert api
        private bool SendCustomArea(ref Point[] pArea, ref AreaName szAreaName, ref AlertMsg msgAlert, int lRefNo, WhiteLists cWhiteLists, AdditionalSubscribers cAddSubscribers, ExecuteMode execMode) // + refno + execmode
        {
            AlertApi aAlert = new AlertApi();
            AlertResponse aResponse = new AlertResponse();
            Polygon oPoly = new Polygon();
            bool bReturn = true;
            string szUpdateSQL;
            int lRequestType = 0;

            oPoly.vertices = new Point[pArea.Length];
            for (int iCount = 0; iCount < pArea.Length; iCount++)
            {
                oPoly.vertices[iCount] = new Point();
                oPoly.vertices[iCount] = pArea[iCount];
            }

            szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_refno=" + lRefNo.ToString();
            int lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);

            aAlert.Url = oController.alertapi; //"http://lbv.netcom.no:8080/alertix/AlertApi";

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aAlert.Url);

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

#if FORCE_SIMULATE
            execMode = ExecuteMode.SIMULATE;
#endif

            try
            {
                UMSAlertiXArea oArea = new UMSAlertiXArea();
                oArea.SetController(ref oController);

                lRequestType = oController.GetRequestType(lRefNo);
                if (lRequestType == 0)
                {
                    if (oArea.AreaExists(szAreaName.ToString()))
                        oArea.DeleteArea(szAreaName.ToString());
                    aResponse = aAlert.executeCustomAlert(szAreaName, msgAlert, cWhiteLists,oPoly, cAddSubscribers, execMode);
                }
                else
                {
                    if (oArea.AreaExists(szAreaName.ToString()))
                        oArea.DeleteArea(szAreaName.ToString());
                    aResponse = aAlert.prepareCustomAlert(szAreaName, msgAlert, cWhiteLists, oPoly, cAddSubscribers);
                }
                if (aResponse.codeSpecified)
                {
                    szUpdateSQL = "UPDATE LBASEND SET l_status=300, l_response=" + aResponse.code + ", sz_jobid='" + aResponse.jobId.value + "' WHERE l_refno=" + lRefNo.ToString();
                    lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                }
                else
                    bReturn = false;
            }
            catch (Exception e)
            {
                szUpdateSQL = "UPDATE LBASEND SET l_retries=l_retries+1 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                bReturn = false;
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            return bReturn;
        }

        private bool GetAlertMsg(ref XmlNode oTextMessages, ref AlertMsg msgAlert)
        {
            return GetAlertMsg(ref oTextMessages, ref msgAlert, 0);
        }

        // help functions
        private bool GetAlertMsg(ref XmlNode oTextMessages, ref AlertMsg msgAlert, int lValidity)
        {
            bool bReturn = true;

            DateTime dtmExpiry = new DateTime();
            if(lValidity > 0)
                dtmExpiry = DateTime.Now.AddMinutes(lValidity);
            else
                dtmExpiry = DateTime.Now.AddMinutes(oController.message_validity);

            int iCountMsg = 0;
            int iCountCC = 0;

            msgAlert.countryMessages = new CountryTextMessage[oTextMessages.ChildNodes.Count - 1];
            msgAlert.expiryTime = dtmExpiry;
            msgAlert.expiryTimeSpecified = true;

            // loop through all the different messages/languages
            foreach (XmlNode oCountryMsg in oTextMessages.ChildNodes)
            {

                // check if current node is the default message
                if (oCountryMsg.SelectSingleNode("ccode").InnerText == "-1")
                {
                    msgAlert.defaultMessage = new TextMessage();

                    msgAlert.defaultMessage.text = oCountryMsg.Attributes.GetNamedItem("sz_text").Value;
                    msgAlert.defaultMessage.oa = oCountryMsg.Attributes.GetNamedItem("sz_cb_oadc").Value;
                }
                else
                {
                    msgAlert.countryMessages[iCountMsg] = new CountryTextMessage();
                    msgAlert.countryMessages[iCountMsg].countryCodes = new CountryCode[oCountryMsg.ChildNodes.Count];

                    msgAlert.countryMessages[iCountMsg].text = oCountryMsg.Attributes.GetNamedItem("sz_text").Value;
                    msgAlert.countryMessages[iCountMsg].oa = oCountryMsg.Attributes.GetNamedItem("sz_cb_oadc").Value;

                    iCountCC = 0; // reset cc count for each language
                    // loop through country codes for each language
                    foreach (XmlNode oCCode in oCountryMsg.ChildNodes)
                    {
                        msgAlert.countryMessages[iCountMsg].countryCodes[iCountCC] = new CountryCode();
                        msgAlert.countryMessages[iCountMsg].countryCodes[iCountCC].value = Convert.ToInt32(oCCode.InnerText);
                        msgAlert.countryMessages[iCountMsg].countryCodes[iCountCC].valueSpecified = true;
                        iCountCC++;
                    }
                    iCountMsg++;
                }

            }

            return bReturn;
        }

        private int UpdateTries(int lRefNo, int lTempStatus, int lEndStatus, int lResponse)
        {
            int lMaxTries = 4; // Is really retries so 4 will give 5 tries total
            int lRetVal = 0;
            string szQuery;

            szQuery = "sp_lba_upd_sendtries " + lRefNo.ToString() + ", " + lTempStatus.ToString() + ", " + lEndStatus.ToString() + ", " + lMaxTries.ToString() + ", " + lResponse.ToString();

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
    }
}
