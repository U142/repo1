using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Net;
using System.Threading;
using umsalertix.alertix.statistics;

namespace umsalertix
{
    class AXStatistics
    {
        public static void CountryCodeStatistics()
        {
            while (Program.running)
            {
                GetCountryDistribution();

                // sleep for l_statistics_timer minutes, but in 1 sec intervals to detect exit
                for (int i = 0; i < 60 * Settings.l_statistics_timer; i++)
                {
                    if (!Program.running)
                        break;

                    Thread.Sleep(1000);
                }
            }
            Log.WriteLog("Stopped parser thread", 9);
        }

        public static int GetSbscriberCountByCC(int CountryCode, Settings oUser, Operator op, int l_requestpk)
        {
            int l_return = Constant.OK;
            int l_retval = 0;
            string sz_updatesql = "";

            StatisticsApi oAlert = new StatisticsApi();
            SubscriberCountResponse oResponse = new SubscriberCountResponse();

            CountryCodes oCountryCodes = new CountryCodes();
            oCountryCodes.countryCodes = new CountryCode[1];
            oCountryCodes.countryCodes[0] = new CountryCode();
            oCountryCodes.countryCodes[0].value = CountryCode;
            oCountryCodes.countryCodes[0].valueSpecified = true;

            try
            {
                oAlert.Url = op.sz_url + "StatisticsApi";

                NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                Uri uri = new Uri(oAlert.Url);

                ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                oAlert.Credentials = objAuth;
                oAlert.PreAuthenticate = true;
                
                oResponse = oAlert.getSubscriberCountByCc(oCountryCodes);

                if (oResponse.successful)
                {
                    Log.WriteLog(String.Format("Cnt {0} (op={1}) getCountByCC OK (cc={2}, sub={3})", l_requestpk, op.sz_operatorname, CountryCode, oResponse.count), 0);
                    Database.Execute(
                        String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                            CountryCode,
                            op.l_cc_from,
                            op.sz_iso_from,
                            op.l_operator,
                            oResponse.count,
                            l_requestpk));
                    sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=2000, l_response={0}, sz_jobid='' WHERE l_requestpk={1} AND l_operator={2}", oResponse.code, l_requestpk, op.l_operator);
                    l_retval = Database.Execute(sz_updatesql);
                }
                else if (oResponse.codeSpecified)
                {
                    Log.WriteLog(String.Format("Cnt {0} {3}/{4}/{5} (op={1}) getCountByCC FAIL (res={2})", l_requestpk, op.sz_operatorname, oResponse.code, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                    //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response={0} WHERE l_requestpk={1} AND l_operator={2}", oResponse.code, l_requestpk, op.l_operator);
                    //l_retval = Database.Execute(sz_updatesql);
                    l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_countByCC, oResponse.code, op.l_operator, LBATYPE.TAS);

                    //l_return = Constant.RETRY;
                }
                else
                {
                    Log.WriteLog(String.Format("Cnt {0} {2}/{3}/{4} (op={1}) getCountByCC FAIL", l_requestpk, op.sz_operatorname, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid), 2);
                    //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                    //l_retval = Database.Execute(sz_updatesql);
                    l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_countByCC, -1, op.l_operator, LBATYPE.TAS);

                    //l_return = Constant.RETRY;
                }

            }
            catch (Exception e)
            {
                Log.WriteLog(
                    String.Format("Cnt {0} {3}/{4}/{5} (op={1}) getCountByCC FAIL (exception={2})", l_requestpk, op.sz_operatorname, e.Message, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                    String.Format("Cnt {0} {3}/{4}/{5} (op={1}) getCountByCC FAIL (exception={2})", l_requestpk, op.sz_operatorname, e, oUser.sz_compid, oUser.sz_deptid, oUser.sz_userid),
                    2);
                //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                //l_retval = Database.Execute(sz_updatesql);
                l_return = Database.UpdateTries(l_requestpk, 290, Constant.EXC_countByCC, -1, op.l_operator, LBATYPE.TAS);

                //l_return = Constant.RETRY;
            }

            return l_return;
        }

        public static int GetCountryDistribution()
        {
            int l_requestpk = Database.GetRequestPK();
            int l_return = Constant.OK;
            int l_retval = 0;
            string sz_updatesql = "";

            StatisticsApi oAlert = new StatisticsApi();
            CountryDistributionByCcResponse oResponse = new CountryDistributionByCcResponse();

            foreach (Operator op in Operator.GetOperators())
            {
                if (op.b_statisticsapi)
                {
                    try
                    {
                        oAlert.Url = op.sz_url + "StatisticsApi";

                        NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                        Uri uri = new Uri(oAlert.Url);

                        ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                        oAlert.Credentials = objAuth;
                        oAlert.PreAuthenticate = true;

                        oResponse = oAlert.getCountryDistributionByCc();

                        if (oResponse.successful)
                        {
                            int ccs = 0;
                            int subs = 0;
                            l_retval = Database.Execute(String.Format(@"sp_tas_insert_request {0}, {1}, 0, 0"
                                , l_requestpk
                                , op.l_operator));

                            HashSet<int> allCC = Database.GetCountryCodes();

                            foreach (CountByCc cccount in oResponse.countryDistributionByCc)
                            {
                                if (cccount.countryCodeSpecified && cccount.countSpecified)
                                {
                                    sz_updatesql = String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                        cccount.countryCode,
                                        op.l_cc_from,
                                        op.sz_iso_from,
                                        op.l_operator,
                                        cccount.count,
                                        l_requestpk);

                                    l_retval = Database.Execute(sz_updatesql);
                                    ccs++;
                                    subs += cccount.count;

                                    allCC.Remove(cccount.countryCode);
                                    //Log.WriteLog(String.Format("{0} returned {1}", sz_updatesql, l_retval),-1); // temp logging just to file..
                                }
                            }

                            foreach (int cc in allCC)
                            {
                                sz_updatesql = String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                        cc,
                                        op.l_cc_from,
                                        op.sz_iso_from,
                                        op.l_operator,
                                        0,
                                        l_requestpk);
                                l_retval = Database.Execute(sz_updatesql);
                                //Log.WriteLog(String.Format("{0} returned {1}", sz_updatesql, l_retval), -1); // temp logging just to file..
                            }

                            Log.WriteLog(String.Format("Sts {0} (op={1}) getCountryDistribution OK ({3} subscribers in {2} countries)", l_requestpk, op.sz_operatorname, ccs, subs), 0);
                            sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=2000, l_response={0}, sz_jobid='' WHERE l_requestpk={1} AND l_operator={2}", oResponse.code, l_requestpk, op.l_operator);
                            l_retval = Database.Execute(sz_updatesql);
                        }
                        else if (oResponse.codeSpecified)
                        {
                            Log.WriteLog(String.Format("Sts {0} {3}/{4}/{5} (op={1}) getCountryDistribution FAIL (res={2})", l_requestpk, op.sz_operatorname, oResponse.code), 2);
                            //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response={0} WHERE l_requestpk={1} AND l_operator={2}", oResponse.code, l_requestpk, op.l_operator);
                            //l_retval = Database.Execute(sz_updatesql);
                            l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_countryDistributionByCc, oResponse.code, op.l_operator, LBATYPE.TAS);

                            //l_return = Constant.RETRY;
                        }
                        else
                        {
                            Log.WriteLog(String.Format("Sts {0} {2}/{3}/{4} (op={1}) getCountryDistribution FAIL", l_requestpk, op.sz_operatorname), 2);
                            //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                            //l_retval = Database.Execute(sz_updatesql);
                            l_return = Database.UpdateTries(l_requestpk, 290, Constant.ERR_countryDistributionByCc, -1, op.l_operator, LBATYPE.TAS);

                            //l_return = Constant.RETRY;
                        }

                    }
                    catch (Exception e)
                    {
                        Log.WriteLog(
                            String.Format("Sts {0} SYSTEM (op={1}) getCountryDistribution FAIL (exception={2})", l_requestpk, op.sz_operatorname, e.Message),
                            String.Format("Sts {0} SYSTEM (op={1}) getCountryDistribution FAIL (exception={2})", l_requestpk, op.sz_operatorname, e),
                            2);
                        //sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=290, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", l_requestpk, op.l_operator);
                        //l_retval = Database.Execute(sz_updatesql);
                        l_return = Database.UpdateTries(l_requestpk, 290, Constant.EXC_countryDistributionByCc, -1, op.l_operator, LBATYPE.TAS);

                        //l_return = Constant.RETRY;
                    }
                }
            }

            return l_return;
        }
    }
}
