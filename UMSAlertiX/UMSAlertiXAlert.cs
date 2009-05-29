#define FORCE_AREA
#define FORCE_SIMULATE
//#define WHITELISTS

using System;
using System.Collections.Generic;
//using System.Linq;
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

            int lRefNo;
            string szUpdateSQL;
            int lRequestType = 0;

            int lReturn = 0;
            XmlNode oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");

            szAreaName.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid").Value;
            GetAlertMsg(ref oTextMessages, ref msgAlert);
            if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send area)");

            szUpdateSQL = "UPDATE LBASEND SET l_status=200 WHERE l_refno=" + lRefNo.ToString();
            int lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);

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
#if WHITELISTS
            oController.log.WriteLog(lRefNo.ToString() + " Force whitelist");
            WhiteLists oWhiteLists = new WhiteLists();
            oWhiteLists.whiteLists = new WhiteListName[1];
            oWhiteLists.whiteLists[0] = new WhiteListName();
            oWhiteLists.whiteLists[0].value = "StorfjordenKommune";
#else
            WhiteLists oWhiteLists = null;
#endif
#if FORCE_SIMULATE
            oController.log.WriteLog(lRefNo.ToString() + " force simulate");
            execMode = ExecuteMode.SIMULATE;
#endif

            try
            {
                lRequestType = oController.GetRequestType(lRefNo);
                if (lRequestType == 0)
                {
                    oController.log.WriteLog(lRefNo.ToString() + " Executing alert (execute mode=" + execMode.ToString() + ")");
                    aResponse = aAlert.executeAreaAlert(szAreaName, msgAlert, oWhiteLists, execMode);
                }
                else
                {
                    oController.log.WriteLog(lRefNo.ToString() + " Preparing alert (execute mode=" + execMode.ToString() + ")");
                    aResponse = aAlert.prepareAreaAlert(szAreaName, msgAlert, oWhiteLists);
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
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            Point[] arrPoint = null;
            UTM uCoConv = new UTM();
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            int lRefNo;
            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            XmlNode oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
            GetAlertMsg(ref oTextMessages, ref msgAlert);
            if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (send polygon) (execute mode=" + execMode.ToString() + ")");

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

            if (!SendCustomArea(ref arrPoint, ref szAreaName, ref msgAlert, lRefNo, execMode))
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int SendEllipse(ref XmlDocument oDoc)
        {
            AreaName szAreaName = new AreaName();
            AlertMsg msgAlert = new AlertMsg();
            Point[] arrPoint = new Point[36];
            UTM uCoConv = new UTM();
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            int lRefNo;
            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            XmlNode oTextMessages = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages");
            GetAlertMsg(ref oTextMessages, ref msgAlert);
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
                    lReturn = -2;
                }
            }
            else
            {
                lReturn = -2;
            }
            return lReturn;
        }

        public int ExecutePreparedAlert(ref XmlDocument oDoc)
        {
            int lReturn = 0;
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
                return -1;

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
                    lReturn = -2;
            }
            catch (Exception e)
            {
                szUpdateSQL = "UPDATE LBASEND SET l_status=42001 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                lReturn = -2;
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            return lReturn;
        }

        public int CancelPreparedAlert(ref XmlDocument oDoc)
        {
            int lReturn = 0;
            int lRefNo;
            JobId idJob = new JobId();

            idJob.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;
            lRefNo = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            oController.log.WriteLog(lRefNo.ToString() + " Started parsing (cancel prepared alert)");

            // exit if sending has already been cancelled or executed
            if (oController.GetRequestType(lRefNo) == 1 && oController.GetSendingStatus(lRefNo) == 800)
                return -1;

            lReturn = CancelPreparedAlert(ref idJob, lRefNo);

            return lReturn;
        }

        public int CancelPreparedAlert(ref JobId idJob, int lRefNo)
        {
            int lReturn = 0;
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
                    lReturn = -2;
            }
            catch (Exception e)
            {
                szUpdateSQL = "UPDATE LBASEND SET l_status=42002 WHERE l_refno=" + lRefNo.ToString();
                lRetval = oController.ExecDB(szUpdateSQL, oController.dsn);
                lReturn = -2;
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            return lReturn;
        }

        // alert api
        private bool SendCustomArea(ref Point[] pArea, ref AreaName szAreaName, ref AlertMsg msgAlert, int lRefNo, ExecuteMode execMode) // + refno + execmode
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

#if WHITELISTS
            WhiteLists oWhiteLists = new WhiteLists();
            oWhiteLists.whiteLists = new WhiteListName[1];
            oWhiteLists.whiteLists[0] = new WhiteListName();
            oWhiteLists.whiteLists[0].value = "StorfjordenKommune";
#else
            WhiteLists oWhiteLists = null;
#endif
#if FORCE_SIMULATE
            execMode = ExecuteMode.SIMULATE;
#endif

            try
            {
                lRequestType = oController.GetRequestType(lRefNo);
                if (lRequestType == 0)
                {
                    aResponse = aAlert.executeCustomAlert(szAreaName, msgAlert, oWhiteLists, oPoly, execMode);
                }
                else
                {
                    aResponse = aAlert.prepareCustomAlert(szAreaName, msgAlert, oWhiteLists, oPoly);
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

        // help functions
        private bool GetAlertMsg(ref XmlNode oTextMessages, ref AlertMsg msgAlert)
        {
            bool bReturn = true;

            DateTime dtmExpiry = new DateTime();
            //dtmExpiry = DateTime.Now.AddHours(12);
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
