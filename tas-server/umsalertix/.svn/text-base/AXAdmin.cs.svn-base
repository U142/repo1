using System;
using System.Net;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using umsalertix.alertix.admin;

namespace umsalertix
{
    class AXAdmin
    {
        public static int CreateWhitelist(XmlDocument oDoc, Settings oUser)
        {
            int ret = Constant.OK;
            decimal l_whitelistid = decimal.Parse(oDoc.SelectSingleNode("TAS").Attributes.GetNamedItem("l_listpk").Value);

            AdminApi oAdmin = new AdminApi();
            Response oResponse = new Response();

            WhiteListName listname = new WhiteListName();
            listname.value = String.Format("TCS-id{0}-c{1}-d{2}", l_whitelistid, oUser.l_comppk, oUser.l_deptpk);

            Database.SetListState(l_whitelistid, QueueState.PROCESSING);
            foreach (Operator op in oUser.operators)
            {
                try
                {
                    oAdmin.Url = op.sz_url + "AdminApi";

                    NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                    Uri uri = new Uri(oAdmin.Url);

                    ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                    oAdmin.Credentials = objAuth;
                    oAdmin.PreAuthenticate = true;

                    Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) Initiating CreateWhitelist", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);

                    oResponse = oAdmin.createWhiteList(listname);
                    if (oResponse.successful)
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) CreateWhitelist OK", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                        Database.SetListState(l_whitelistid, oResponse.code, op);
                    }
                    else if (oResponse.codeSpecified)
                    {
                        Log.WriteLog(String.Format("Adm {0} {3}/{4}/{5} (op={1}) CreateWhitelist FAIL (res={2})", l_whitelistid, op.sz_operatorname, oResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                        Database.SetListState(l_whitelistid, oResponse.code, op);
                        ret = Constant.FAILED;
                    }
                    else
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) CreateWhitelist FAIL", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                        Database.SetListState(l_whitelistid, -1, op);
                        ret = Constant.FAILED;
                    }
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) CreateWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e.Message, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) CreateWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        2);
                    Database.SetListState(l_whitelistid, -2, op);
                    ret = Constant.FAILED;
                }
            }
            if (ret == Constant.OK)
                Database.SetListState(l_whitelistid, QueueState.READY);
            else if (ret == Constant.FAILED)
                Database.SetListState(l_whitelistid, QueueState.ERROR);
            
            // do nothing for status failedretry (keep as processing)
            // initially no retry on whitelists, allow delete if error or ready

            return ret;
        }
        public static int UpdateWhitelist(XmlDocument oDoc, Settings oUser)
        {
            int ret = Constant.OK;
            decimal l_whitelistid = decimal.Parse(oDoc.SelectSingleNode("TAS").Attributes.GetNamedItem("l_listpk").Value);

            AdminApi oAdmin = new AdminApi();
            AddResponse oAddResponse = new AddResponse();
            DeleteResponse oDeleteResponse = new DeleteResponse();

            WhiteListName listname = new WhiteListName();
            listname.value = String.Format("TCS-id{0}-c{1}-d{2}", l_whitelistid, oUser.l_comppk, oUser.l_deptpk);

            Database.SetListState(l_whitelistid, QueueState.PROCESSING);

            // make lists off add and remove numbers
            List<Msisdn> list_add = new List<Msisdn>();
            List<Msisdn> list_rem = new List<Msisdn>();

            foreach (XmlNode oNode in oDoc.SelectSingleNode("TAS").SelectSingleNode("whitelist").ChildNodes)
            {
                Msisdn number = new Msisdn();
                number.value = oNode.InnerText;

                if (oNode.Name == "add")
                    list_add.Add(number);
                else if (oNode.Name == "rem")
                    list_rem.Add(number);
            }

            foreach (Operator op in oUser.operators)
            {
                try
                {
                    oAdmin.Url = op.sz_url + "AdminApi";

                    NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                    Uri uri = new Uri(oAdmin.Url);

                    ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                    oAdmin.Credentials = objAuth;
                    oAdmin.PreAuthenticate = true;

                    Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) Initiating UpdateWhitelist", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);

                    // add
                    if (list_add.Count > 0)
                    {
                        oAddResponse = oAdmin.addWhiteListSubscribers(listname, list_add.ToArray());
                        if (oAddResponse.successful)
                        {
                            Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) addSubscribers OK (sub={5})", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid, oAddResponse.addedCount), 0);
                            Database.SetListState(l_whitelistid, oAddResponse.code, op);
                        }
                        else if (oAddResponse.codeSpecified)
                        {
                            if (oAddResponse.code == 800) // does not exist, add code to handle
                            {
                                Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) addSubscribers FAIL (list not found)", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                                Database.SetListState(l_whitelistid, oAddResponse.code, op);
                                ret = Constant.FAILED;
                            }
                            else
                            {
                                Log.WriteLog(String.Format("Adm {0} {3}/{4}/{5} (op={1}) addSubscribers FAIL (res={2})", l_whitelistid, op.sz_operatorname, oAddResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                Database.SetListState(l_whitelistid, oAddResponse.code, op);
                                ret = Constant.FAILED;
                            }
                        }
                        else
                        {
                            Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) addSubscribers FAIL", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                            Database.SetListState(l_whitelistid, -1, op);
                            ret = Constant.FAILED;
                        }
                    }
                    else
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) addSubscribers OK (no numbers to add)", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                    }

                    // remove
                    if (list_rem.Count > 0)
                    {
                        oDeleteResponse = oAdmin.deleteWhiteListSubscribers(listname, list_rem.ToArray());
                        if (oDeleteResponse.successful)
                        {
                            Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) deleteSubscribers OK (sub={5})", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid, oDeleteResponse.deletedCount), 0);
                            Database.SetListState(l_whitelistid, oDeleteResponse.code, op);
                        }
                        else if (oDeleteResponse.codeSpecified)
                        {
                            if (oDeleteResponse.code == 800) // does not exist, add code to handle
                            {
                                Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) deleteSubscribers FAIL (list not found)", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                                Database.SetListState(l_whitelistid, oDeleteResponse.code, op);
                                ret = Constant.FAILED;
                            }
                            else
                            {
                                Log.WriteLog(String.Format("Adm {0} {3}/{4}/{5} (op={1}) deleteSubscribers FAIL (res={2})", l_whitelistid, op.sz_operatorname, oDeleteResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                                Database.SetListState(l_whitelistid, oDeleteResponse.code, op);
                                ret = Constant.FAILED;
                            }
                        }
                        else
                        {
                            Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) deleteSubscribers FAIL", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                            Database.SetListState(l_whitelistid, -1, op);
                            ret = Constant.FAILED;
                        }
                    }
                    else
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) deleteSubscribers OK (no numbers to remove)", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                    }
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) UpdateWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e.Message, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) UpdateWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        2);
                    Database.SetListState(l_whitelistid, -2, op);
                    ret = Constant.FAILED;
                }
            }

            if (ret == Constant.OK)
                Database.SetListState(l_whitelistid, QueueState.READY);
            else if (ret == Constant.FAILED)
                Database.SetListState(l_whitelistid, QueueState.ERROR);

            return ret;
        }
        public static int DeleteWhitelist(XmlDocument oDoc, Settings oUser)
        {
            int ret = Constant.OK;
            decimal l_whitelistid = decimal.Parse(oDoc.SelectSingleNode("TAS").Attributes.GetNamedItem("l_listpk").Value);

            AdminApi oAdmin = new AdminApi();
            Response oResponse = new Response();

            WhiteListName listname = new WhiteListName();
            listname.value = String.Format("TCS-id{0}-c{1}-d{2}", l_whitelistid, oUser.l_comppk, oUser.l_deptpk);

            Database.SetListState(l_whitelistid, QueueState.PROCESSING);
            foreach (Operator op in oUser.operators)
            {
                try
                {
                    oAdmin.Url = op.sz_url + "AdminApi";

                    NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                    Uri uri = new Uri(oAdmin.Url);

                    ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                    oAdmin.Credentials = objAuth;
                    oAdmin.PreAuthenticate = true;

                    Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) Initiating DeleteWhitelist", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);

                    oResponse = oAdmin.deleteWhiteList(listname);
                    if (oResponse.successful)
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) DeleteWhitelist OK", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                        Database.SetListState(l_whitelistid, oResponse.code, op);
                    }
                    else if (oResponse.codeSpecified)
                    {
                        if (oResponse.code == 800) // does not exist => don't treat as error
                        {
                            Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) DeleteWhitelist OK (list not found)", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 0);
                            Database.SetListState(l_whitelistid, oResponse.code, op);
                        }
                        else
                        {
                            Log.WriteLog(String.Format("Adm {0} {3}/{4}/{5} (op={1}) DeleteWhitelist FAIL (res={2})", l_whitelistid, op.sz_operatorname, oResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                            Database.SetListState(l_whitelistid, oResponse.code, op);
                            ret = Constant.FAILED;
                        }
                    }
                    else
                    {
                        Log.WriteLog(String.Format("Adm {0} {2}/{3}/{4} (op={1}) DeleteWhitelist FAIL", l_whitelistid, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                        Database.SetListState(l_whitelistid, -1, op);
                        ret = Constant.FAILED;
                    }
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) DeleteWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e.Message, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        String.Format("Adm {0} {3}/{4}/{5} (op={1}) DeleteWhitelist FAIL (exception={2})", l_whitelistid, op.sz_operatorname, e, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                        2);
                    Database.SetListState(l_whitelistid, -2, op);
                    ret = Constant.FAILED;
                }
            }
            if (ret == Constant.OK)
                Database.DeleteWhitelist(l_whitelistid);
            else if (ret == Constant.FAILED)
                Database.SetListState(l_whitelistid, QueueState.ERROR);

            // do nothing for status failedretry (keep as processing)
            // initially no retry on whitelists, allow delete if error or ready

            return ret;
        }
    }

    public enum QueueState
    {
        ERROR = -1,
        READY = 0,
        QUEUED = 1,
        PROCESSING = 2
    }
}
