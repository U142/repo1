using System;
using System.Net;
using System.Xml;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.IO;
using umsalertix.alertix.internationalalert;

namespace umsalertix
{
    class AXInternational
    {
        public static int SendInternational(XmlDocument oDoc, Settings oUser)
        {
            int l_return = Constant.OK;

            InternationalAlertApi oAlert = new InternationalAlertApi();
            AlertResponse oResponse = new AlertResponse();

            CountryCodes oCountryCodes = GetCcodes(oDoc);
            InternationalAlertMsg oMsg = GetAlertMsg(oDoc);
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            if (Settings.sz_executemode == "NORMAL") // use from file
            {
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            }
            else if (Settings.sz_executemode == "SILENT") // use SILENT if file says LIVE
            {
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.SILENT;
            }
            else if (Settings.sz_executemode == "SIMULATE") // force SIMULATE
                execMode = ExecuteMode.SIMULATE;

            int l_refno = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            int l_requesttype = Database.GetRequestType(l_refno);

            string sz_updatesql = String.Format("UPDATE LBASEND SET l_status=200 WHERE l_status IN (199,290) AND l_refno={0}", l_refno);
            string sz_requestcc = "";
            string sz_requesttype = "";

            int l_retval = Database.Execute(sz_updatesql);

            foreach (CountryCode cc in oCountryCodes.countryCodes)
            {
                if (sz_requestcc != "")
                    sz_requestcc += ",";
                sz_requestcc += cc.value;
            }
            if (l_requesttype == 0)
            {
                sz_requesttype = "execute";
            }
            else
            {
                sz_requesttype = "prepare";
            }
            Log.WriteLog(String.Format("Snd {0} {4}/{5}/{6} Initiating {1} (cc={2}) (mode={3})", l_refno, sz_requesttype, sz_requestcc, execMode, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);

            foreach (Operator op in oUser.operators)
            {
                string sz_whitelists = "";
                try
                {
                        // check if request is sent to operator already
                        string szJobID = Database.GetJobIDSend(l_refno, op.l_operator);
                        if (szJobID != "")
                        {
                            //Log.WriteLog(l_requestpk.ToString() + " (" + op.sz_operatorname + ") already submitted with jobid: " + szJobID);
                            Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) already submitted.", l_refno, op.sz_operatorname, szJobID), 0);
                        }
                        else
                        {
                            oAlert.Url = op.sz_url + "InternationalAlertApi";

                            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                            Uri uri = new Uri(oAlert.Url);

                            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                            oAlert.Credentials = objAuth;
                            oAlert.PreAuthenticate = true;

                            WhiteLists oWhiteLists = new WhiteLists();
                            HashSet<string> wl_static = WhiteListConfig.GetWhiteLists(oUser.sz_compid, oUser.sz_deptid, op.l_operator);
                            HashSet<string> wl_tcs = GetTcsWhiteLists(oDoc, oUser);
                            wl_tcs.ExceptWith(wl_static); // remove duplicates

                            List<string> h_lists = new List<string>();

                            h_lists.AddRange(wl_static);
                            h_lists.AddRange(wl_tcs);

                            if (h_lists.Count > 0)
                            {
                                sz_whitelists = "";
                                WhiteListName[] oWhiteListName = new WhiteListName[h_lists.Count];
                                for (int i = 0; i < h_lists.Count; i++)
                                {
                                    oWhiteListName[i] = new WhiteListName();
                                    oWhiteListName[i].value = h_lists.ElementAt(i);

                                    if (sz_whitelists != "")
                                        sz_whitelists += ",";
                                    sz_whitelists += h_lists.ElementAt(i);
                                }
                                sz_whitelists = " (wl=" + sz_whitelists + ") ";
                                oWhiteLists.whiteLists = oWhiteListName;
                            }
                            if (sz_whitelists == "")
                                sz_whitelists = " ";

                            if (l_requesttype == 0)
                            {
                                oResponse = oAlert.executeAlert(oCountryCodes, oMsg, oWhiteLists, null, execMode);
                            }
                            else
                            {
                                oResponse = oAlert.prepareAlert(oCountryCodes, oMsg, oWhiteLists, null);
                            }

                            if (oResponse.successful)
                            {
                                Log.WriteLog(String.Format("Snd {0} (job={3}) (op={1}) {2}Alert OK{4}", l_refno, op.sz_operatorname, sz_requesttype, oResponse.jobId.value, sz_whitelists), 0);
                                sz_updatesql = String.Format("UPDATE LBASEND SET l_status=400, l_response={0}, sz_jobid='{1}' WHERE l_refno={2} AND l_operator={3}", oResponse.code, oResponse.jobId.value, l_refno, op.l_operator);
                                l_retval = Database.Execute(sz_updatesql);
                            }
                            else if (oResponse.codeSpecified)
                            {
                                Log.WriteLog(String.Format("Snd {0} {5}/{6}/{7} (op={1}) {2}Alert FAIL{4}(res={3})", l_refno, op.sz_operatorname, sz_requesttype, oResponse.code, sz_whitelists, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                l_return = Database.UpdateTries(l_refno, 290, l_requesttype == 0 ? Constant.ERR_executeIntAlert : Constant.ERR_prepareIntAlert, oResponse.code, op.l_operator, LBATYPE.LBAS);
                            }
                            else
                            {
                                Log.WriteLog(String.Format("Snd {0} {4}/{5}/{6} (op={1}) {2}Alert FAIL{3}", l_refno, op.sz_operatorname, sz_requesttype, sz_whitelists, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                l_return = Database.UpdateTries(l_refno, 290, l_requesttype == 0 ? Constant.ERR_executeIntAlert : Constant.ERR_prepareIntAlert, -1, op.l_operator, LBATYPE.LBAS);
                            }
                        }
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("Snd {0} {5}/{6}/{7} (op={1}) {2}Alert FAIL{4}(exception={3})", l_refno, op.sz_operatorname, sz_requesttype, e.Message, sz_whitelists, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        String.Format("Snd {0} {5}/{6}/{7} (op={1}) {2}Alert FAIL{4}(exception={3})", l_refno, op.sz_operatorname, sz_requesttype, e, sz_whitelists, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        2);
                    l_return = Database.UpdateTries(l_refno, 290, l_requesttype == 0 ? Constant.EXC_executeIntAlert : Constant.EXC_prepareIntAlert, -1, op.l_operator, LBATYPE.LBAS);
                }
            }

            return l_return;
        }

        public static int CancelPreparedAlert(XmlDocument oDoc, Settings oUser)
        {
            int l_return = Constant.OK;
            int l_retval;
            int l_refno;
            int l_operator;
            string sz_updatesql;
            JobId jobid = new JobId();
            Operator op;

            jobid.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;

            l_refno = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            l_operator = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_operator").Value);
            op = Operator.GetOperator(l_operator);

            sz_updatesql = String.Format("UPDATE LBASEND SET l_status=800, l_response=0 WHERE l_refno={0} AND l_operator={1}", l_refno, l_operator);
            l_retval = Database.Execute(sz_updatesql);

            InternationalAlertApi aAlert = new InternationalAlertApi();
            Response aResponse = new Response();
            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);

            Uri uri = new Uri(aAlert.Url);
            aAlert.Timeout = 300000;

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

            try
            {
                aResponse = aAlert.cancelPreparedAlert(jobid);
                if (aResponse.successful)
                {
                    sz_updatesql = String.Format("UPDATE LBASEND SET l_status=2000, l_response={0} WHERE l_refno={1} AND l_operator={2}", aResponse.code, l_refno, op.l_operator);
                    l_retval = Database.Execute(sz_updatesql);
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) cancelAlert OK", l_refno, op.sz_operatorname, jobid.value), 0);
                }
                else if (aResponse.codeSpecified)
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) cancelAlert FAIL (res={3})", l_refno, op.sz_operatorname, jobid.value, aResponse.code), 2);
                    l_return = Database.UpdateTries(l_refno, 290, Constant.ERR_cancelPreparedAlert, aResponse.code, op.l_operator, LBATYPE.LBAS);
                }
                else
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) cancelAlert FAIL", l_refno, op.sz_operatorname, jobid.value), 2);
                    l_return = Database.UpdateTries(l_refno, 290, Constant.ERR_cancelPreparedAlert, -1, op.l_operator, LBATYPE.LBAS);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Snd {0} (job={3}) (op={1}) cancelAlert FAIL (exception={2})", l_refno, op.sz_operatorname, e.Message, jobid.value),
                    String.Format("Snd {0} (job={3}) (op={1}) cancelAlert FAIL (exception={2})", l_refno, op.sz_operatorname, e, jobid.value),
                    2);
                l_return = Database.UpdateTries(l_refno, 290, Constant.EXC_cancelPreparedAlert, -1, op.l_operator, LBATYPE.LBAS);
            }

            return l_return;
        }

        public static int ConfirmPreparedAlert(XmlDocument oDoc, Settings oUser)
        {
            int l_return = Constant.OK;
            int l_retval;
            int l_refno;
            int l_operator;
            string sz_updatesql;
            JobId jobid = new JobId();
            Operator op;
            ExecuteMode execMode = ExecuteMode.SIMULATE;

            jobid.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_jobid").Value;

            if (Settings.sz_executemode == "NORMAL") // use from file
            {
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.LIVE;
            }
            else if (Settings.sz_executemode == "SILENT") // use SILENT if file says LIVE
            {
                if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation") != null) // defaults to SIMULATE if null
                    if (Convert.ToInt16(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("f_simulation").Value) == 0) execMode = ExecuteMode.SILENT;
            }
            else if (Settings.sz_executemode == "SIMULATE") // force SIMULATE
                execMode = ExecuteMode.SIMULATE;

            l_refno = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_refno").Value);
            l_operator = Convert.ToInt32(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_operator").Value);
            op = Operator.GetOperator(l_operator);

            InternationalAlertApi aAlert = new InternationalAlertApi();
            Response aResponse = new Response();
            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);

            Uri uri = new Uri(aAlert.Url);
            aAlert.Timeout = 300000;

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

            try
            {
                aResponse = aAlert.executePreparedAlert(jobid, execMode);
                if (aResponse.successful)
                {
                    sz_updatesql = String.Format("UPDATE LBASEND SET l_status=440, l_response={0} WHERE l_refno={1} AND l_operator={2}", aResponse.code, l_refno, op.l_operator);
                    l_retval = Database.Execute(sz_updatesql);
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) confirmAlert OK", l_refno, op.sz_operatorname, jobid.value), 0);
                }
                else if (aResponse.codeSpecified)
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) confirmAlert FAIL (res={3})", l_refno, op.sz_operatorname, jobid.value, aResponse.code), 2);
                    l_return = Database.UpdateTries(l_refno, 290, Constant.ERR_cancelPreparedAlert, aResponse.code, op.l_operator, LBATYPE.LBAS);
                }
                else
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) confirmAlert FAIL", l_refno, op.sz_operatorname, jobid.value), 2);
                    l_return = Database.UpdateTries(l_refno, 290, Constant.ERR_cancelPreparedAlert, -1, op.l_operator, LBATYPE.LBAS);
                }
            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Snd {0} (job={3}) (op={1}) confirmAlert FAIL (exception={2})", l_refno, op.sz_operatorname, e.Message, jobid.value),
                    String.Format("Snd {0} (job={3}) (op={1}) confirmAlert FAIL (exception={2})", l_refno, op.sz_operatorname, e, jobid.value),
                    2);
                l_return = Database.UpdateTries(l_refno, 290, Constant.EXC_cancelPreparedAlert, -1, op.l_operator, LBATYPE.LBAS);
            }

            return l_return;
        }

        public static int CountInternational(XmlDocument oDoc, Settings oUser)
        {
            int l_return = Constant.OK;

            InternationalAlertApi oAlert = new InternationalAlertApi();
            AlertResponse oResponse = new AlertResponse();

            CountryCodes oCountryCodes = GetCcodes(oDoc);
            InternationalAlertMsg oMsg = GetAlertMsg(oDoc);

            int l_requestpk = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_requestpk").Value);
            string sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=200 WHERE l_status IN (199,290) AND l_requestpk={0}", l_requestpk);
            string sz_requestcc = "";
            
            int l_retval = Database.Execute(sz_updatesql);

            foreach (CountryCode cc in oCountryCodes.countryCodes)
            {
                if (sz_requestcc != "")
                    sz_requestcc += ",";
                sz_requestcc += cc.value;
            }
            Log.WriteLog(String.Format("Cnt {0} {2}/{3}/{4} Initiating request (cc={1})", l_requestpk, sz_requestcc, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);

            foreach (Operator op in oUser.operators)
            {
                if (op.b_statisticsapi)
                {
                    foreach (CountryCode ccode in oCountryCodes.countryCodes)
                    {
                        l_retval = AXStatistics.GetSbscriberCountByCC((int)ccode.value, oUser, op, l_requestpk);
                    }
                }
                else
                {
                    try // always do prepareAlert on Count
                    {
                        // check if request is sent to operator already
                        string szJobID = Database.GetJobIDCount(l_requestpk, op.l_operator);
                        if (szJobID != "")
                        {
                            //Log.WriteLog(l_requestpk.ToString() + " (" + op.sz_operatorname + ") already submitted with jobid: " + szJobID);
                            Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) already submitted.", l_requestpk, op.sz_operatorname, szJobID), 0);
                        }
                        else
                        {
                            oAlert.Url = op.sz_url + "InternationalAlertApi";

                            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                            Uri uri = new Uri(oAlert.Url);

                            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                            oAlert.Credentials = objAuth;
                            oAlert.PreAuthenticate = true;

                            oResponse = oAlert.prepareAlert(oCountryCodes, oMsg, null, null);

                            if (oResponse.successful)
                            {
                                Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) prepareAlert OK", l_requestpk, op.sz_operatorname, oResponse.jobId.value), 0);
                                sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=400, l_response={0}, sz_jobid='{1}' WHERE l_requestpk={2} AND l_operator={3}", oResponse.code, oResponse.jobId.value, l_requestpk, op.l_operator);
                                l_retval = Database.Execute(sz_updatesql);
                            }
                            else if (oResponse.codeSpecified)
                            {
                                Log.WriteLog(String.Format("Cnt {0} {3}/{4}/{5} (op={1}) prepareAlert FAIL (res={2})", l_requestpk, op.sz_operatorname, oResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response={0} WHERE l_requestpk={1} AND l_operator={2}", oResponse.code, l_requestpk, op.l_operator);
                                //l_retval = Database.Execute(sz_updatesql);
                                l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_prepareIntAlert, oResponse.code, op.l_operator, LBATYPE.TAS);

                                //l_return = Constant.RETRY;
                            }
                            else
                            {
                                Log.WriteLog(String.Format("Cnt {0} {2}/{3}/{4} (op={1}) prepareAlert FAIL", l_requestpk, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                                //l_retval = Database.Execute(sz_updatesql);
                                l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_prepareIntAlert, -1, op.l_operator, LBATYPE.TAS);

                                //l_return = Constant.RETRY;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.WriteLog(
                            String.Format("Cnt {0} {3}/{4}/{5} (op={1}) prepareAlert FAIL (exception={2})", l_requestpk, op.sz_operatorname, e.Message, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                            String.Format("Cnt {0} {3}/{4}/{5} (op={1}) prepareAlert FAIL (exception={2})", l_requestpk, op.sz_operatorname, e, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                            2);
                        //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                        //l_retval = Database.Execute(sz_updatesql);
                        l_return = Database.UpdateTries(l_requestpk, 290, Constant.EXC_prepareIntAlert, -1, op.l_operator, LBATYPE.TAS);

                        //l_return = Constant.RETRY;
                    }
                }
            }
            return l_return;
        }

        public static int CancelCountInternational(string sz_jobid, int l_requestpk, int l_operator)
        {
            int l_return = Constant.OK;
            int l_retval;
            string sz_updatesql;
            JobId jobid = new JobId();
            Operator op = Operator.GetOperator(l_operator);

            jobid.value = sz_jobid;

            sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=800, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
            l_retval = Database.Execute(sz_updatesql);

            InternationalAlertApi aAlert = new InternationalAlertApi();
            Response aResponse = new Response();

            aAlert.Url = op.sz_url + "InternationalAlertApi";

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aAlert.Url);
            aAlert.Timeout = 300000;

            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
            aAlert.Credentials = objAuth;
            aAlert.PreAuthenticate = true;

            try
            {
                aResponse = aAlert.cancelPreparedAlert(jobid);
                if (aResponse.successful)
                {
                    sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=2000, l_response={0} WHERE l_requestpk={1} AND l_operator={2}", aResponse.code, l_requestpk, op.l_operator);
                    l_retval = Database.Execute(sz_updatesql);
                    Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) cancelAlert OK", l_requestpk, op.sz_operatorname, jobid.value), 0);
                }
                else if (aResponse.codeSpecified)
                {
                    //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response={0} WHERE l_requestpk={1} AND l_operator={2}", aResponse.code, l_requestpk, op.l_operator);
                    //l_retval = Database.Execute(sz_updatesql);
                    Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) cancelAlert FAIL (res={3})", l_requestpk, op.sz_operatorname, jobid.value, aResponse.code), 2);
                    l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_cancelPreparedAlert, aResponse.code, op.l_operator, LBATYPE.TAS);

                    //l_return = Constant.RETRY;
                }
                else
                {
                    //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                    //l_retval = Database.Execute(sz_updatesql);
                    Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) cancelAlert FAIL", l_requestpk, op.sz_operatorname, jobid.value), 2);
                    l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_cancelPreparedAlert, -1, op.l_operator, LBATYPE.TAS);

                    //l_return = Constant.RETRY;
                }
            }
            catch (Exception e)
            {
                //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                //l_retval = Database.Execute(sz_updatesql);
                Log.WriteLog(
                    String.Format("Cnt {0} (job={3}) (op={1}) cancelAlert FAIL (exception={2})", l_requestpk, op.sz_operatorname, e.Message, jobid.value), 
                    String.Format("Cnt {0} (job={3}) (op={1}) cancelAlert FAIL (exception={2})", l_requestpk, op.sz_operatorname, e, jobid.value), 
                    2);
                l_return = Database.UpdateTries(l_requestpk, 290, Constant.EXC_cancelPreparedAlert, -1, op.l_operator, LBATYPE.TAS);

                //l_return = Constant.RETRY;
            }

            return l_return;
        }

        private static CountryCodes GetCcodes(XmlDocument oDoc)
        {
            CountryCode[] arrcc = null;
            CountryCodes ccodes = new CountryCodes();

            HashSet<int> hlcc = new HashSet<int>();
            foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages").SelectSingleNode("message").ChildNodes)
            {
                if (oNode.Name == "ccode")
                    hlcc.Add(int.Parse(oNode.InnerText));
            }
            arrcc = new CountryCode[hlcc.Count()];
            int i = 0;
            foreach (int ccode in hlcc)
            {
                arrcc[i] = new CountryCode();
                arrcc[i].value = ccode;
                arrcc[i].valueSpecified = true;
                i++;
            }
            hlcc.Clear();
            ccodes.countryCodes = arrcc;

            return ccodes;
        }

        private static InternationalAlertMsg GetAlertMsg(XmlDocument oDoc)
        {
            TextMessage oMessage = new TextMessage();
            DateTime dtmExpiry = new DateTime();
            InternationalAlertMsg oMsg = new InternationalAlertMsg();

            int lValidity = int.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_validity").Value);

            if (lValidity > 0)
                dtmExpiry = DateTime.Now.AddMinutes(lValidity);
            else
                dtmExpiry = DateTime.Now.AddMinutes(Settings.l_validity);

            oMessage.text = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages").SelectSingleNode("message").Attributes.GetNamedItem("sz_text").Value;
            oMessage.oa = oDoc.SelectSingleNode("LBA").SelectSingleNode("textmessages").SelectSingleNode("message").Attributes.GetNamedItem("sz_cb_oadc").Value;
            if (oMessage.oa == "NULL")
                oMessage.oa = "";

            oMsg.message = oMessage;
            oMsg.expiryTime = dtmExpiry;
            oMsg.expiryTimeSpecified = true;

            return oMsg;
        }

        private static HashSet<string> GetTcsWhiteLists(XmlDocument oDoc, Settings oUser)
        {
            HashSet<string> ret = new HashSet<string>();

            if(oDoc.SelectSingleNode("LBA")!=null)
                if(oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists")!=null)
                    foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("whitelists").ChildNodes)
                    {
                        if (oNode.Name == "whitelist")
                            ret.Add(Database.GetListName(oNode.Attributes.GetNamedItem("name").Value, oUser.l_deptpk, oUser.l_comppk));
                    }

            return ret;
        }
    }
}
