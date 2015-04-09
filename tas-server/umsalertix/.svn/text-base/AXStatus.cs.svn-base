using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Data.Odbc;
using System.Threading;
using umsalertix.alertix.status;

namespace umsalertix
{
    class AXStatus
    {
        // Methods for updating Count requests
        public static void UpdateJobStatusCount()
        {
            string szJobQuery = "SELECT s.l_requestpk, s.sz_jobid, s.l_operator, s.l_status FROM LBATOURISTCOUNTREQ s WHERE s.l_status=400";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            StatusApi aStatus = new StatusApi();
            JobStatusResponse aJobResponse = new JobStatusResponse();
            JobId idJob = new JobId();

            int lRetVal;
            int lRequestPK;
            int lOperator;
            int lStatus;

            while (Program.running)
            {
                try
                {
                    dbConn.Open();
                    rsJobs = cmdJobs.ExecuteReader();
                    while (rsJobs.Read() && Program.running)
                    {
                        if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)) || rsJobs.IsDBNull(3)) // required values missing from LBASEND
                        {
                            lRequestPK = rsJobs.GetInt32(0);
                            idJob.value = rsJobs.GetString(1);
                            lOperator = rsJobs.GetInt32(2);
                            lStatus = rsJobs.GetInt32(3);

                            Operator op = Operator.GetOperator(lOperator);

                            aStatus.Url = op.sz_url + "StatusApi";

                            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                            Uri uri = new Uri(aStatus.Url);
                            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

                            aStatus.Credentials = objAuth;
                            aStatus.PreAuthenticate = true;

                            try
                            {
                                aJobResponse = aStatus.getJobStatus(idJob);
                            }
                            catch (Exception e)
                            {
                                Log.WriteLog(
                                    String.Format("Cnt {0} (job={2}) (op={1}) jobStatus FAIL (exception={3})", lRequestPK, op.sz_operatorname, idJob.value, e.Message),
                                    String.Format("Cnt {0} (job={2}) (op={1}) jobStatus FAIL (exception={3})", lRequestPK, op.sz_operatorname, idJob.value, e),
                                    2);
                            }

                            if (aJobResponse.codeSpecified)// && aJobResponse.jobStatusSpecified)
                            {
                                if (aJobResponse.code == 200)
                                {
                                    HashSet<int> h_cc = Database.GetReqCountries(lRequestPK);
                                    switch (aJobResponse.jobStatus.Value.ToString())
                                    {
                                        case "NO_SUBSCRIBERS":
                                            // update med 0 på alle cc og cancel
                                            foreach (int l_cc in h_cc)
                                            {
                                                Database.Execute(
                                                    String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                                        l_cc,
                                                        op.l_cc_from,
                                                        op.sz_iso_from,
                                                        op.l_operator,
                                                        0,
                                                        lRequestPK));
                                                Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) updateCount OK (cc={3}, sub=0)", lRequestPK, op.sz_operatorname, idJob.value, l_cc), 0);
                                            }
                                            // update DB to cancelled (since it has no subscribers)
                                            string sz_updatesql = String.Format("UPDATE LBATOURISTCOUNTREQ SET l_status=2000, l_response=0 WHERE l_requestpk={0} AND l_operator={1}", lRequestPK, op.l_operator);
                                            int l_retval = Database.Execute(sz_updatesql);
                                            Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) cancelAlert OK", lRequestPK, op.sz_operatorname, idJob.value), 0);
                                            break;
                                        case "PREPARED":
                                            if (h_cc.Count > 1)
                                            {
                                                Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) PREPARED (sub={3}) requesting individual count for {4} country codes", lRequestPK, op.sz_operatorname, idJob.value, aJobResponse.subscriberCount, h_cc.Count), 0);
                                                lRetVal = Database.Execute("UPDATE LBATOURISTCOUNTREQ set l_status=410 WHERE l_requestpk=" + lRequestPK.ToString() + " AND l_operator=" + lOperator.ToString());
                                            }
                                            else
                                            {
                                                Database.Execute(
                                                    String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                                        h_cc.First(),
                                                        op.l_cc_from,
                                                        op.sz_iso_from,
                                                        op.l_operator,
                                                        aJobResponse.subscriberCount,
                                                        lRequestPK));

                                                Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) updateCount OK (cc={3}, sub={4})", lRequestPK, op.sz_operatorname, idJob.value, h_cc.First(), aJobResponse.subscriberCount), 0);
                                                AXInternational.CancelCountInternational(idJob.value, lRequestPK, lOperator);
                                            }
                                            break;
                                        case "ERROR":
                                            string sz_requestcc = "";
                                            foreach (int l_cc in h_cc)
                                            {
                                                if (sz_requestcc != "")
                                                    sz_requestcc += ",";
                                                sz_requestcc += l_cc.ToString();
                                            }

                                            Log.WriteLog(String.Format("Cnt {0} (job={1}) (op={2}) jobStatus ERROR (cc={3})", lRequestPK, idJob.value, op.sz_operatorname, sz_requestcc), 2);
                                            lRetVal = Database.Execute("UPDATE LBATOURISTCOUNTREQ set l_status=" + Constant.ERR_JobError + " WHERE l_requestpk=" + lRequestPK.ToString() + " AND l_operator=" + lOperator.ToString());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                else
                                {
                                    Log.WriteLog(aJobResponse.code.ToString() + " - " + idJob.value + " - " + aJobResponse.message, 2);
                                }
                            }
                            else
                            {
                                Log.WriteLog("JobId: " + idJob.value + " jobResponse.code not specified", 2);
                            }
                        }
                    }
                    rsJobs.Close();
                    dbConn.Close();
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("UpdateJobStatusCount (exception={0})", e.Message),
                        String.Format("UpdateJobStatusCount (exception={0})", e),
                        2);
                }
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!Program.running)
                        break;
                }
            }
            Log.WriteLog("Stopped JobStatus thread (count)", 9);
        }
        public static void UpdateCountCC()
        {
            string szJobQuery = "SELECT l_requestpk, sz_jobid, l_operator FROM LBATOURISTCOUNTREQ where l_status=410";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            StatusApi aStatus = new StatusApi();
            //JobStatusResponse aJobResponse = new JobStatusResponse();
            CountryCodeAlertStatusResponse aStatusResponse = new CountryCodeAlertStatusResponse();
            JobStatusResponse aJobStatus = new JobStatusResponse();

            JobId idJob = new JobId();

            int lRequestPK;
            int lOperator;
            string sz_updatesql;

            while (Program.running)
            {
                try
                {
                    dbConn.Open();
                    rsJobs = cmdJobs.ExecuteReader();
                    while (rsJobs.Read() && Program.running)
                    {
                        if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)))
                        {
                            int lRetVal;
                            int ccCount;         // current CC
                            int ccTotal;    // total all CC
                            //int lITems;     // total all CC (from jobstatus)

                            lRequestPK = rsJobs.GetInt32(0);
                            idJob.value = rsJobs.GetString(1);
                            lOperator = rsJobs.GetInt32(2);

                            Operator op = Operator.GetOperator(lOperator);
                            aStatus.Url = op.sz_url + "StatusApi";

                            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                            Uri uri = new Uri(aStatus.Url);
                            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

                            aStatus.Credentials = objAuth;
                            aStatus.PreAuthenticate = true;

                            try
                            {
                                aStatusResponse = aStatus.getAlertStatusByCountryCode(idJob);
                                aJobStatus = aStatus.getJobStatus(idJob);
                            }
                            catch (Exception e)
                            {
                                Log.WriteLog(
                                    String.Format("Cnt {0} (job={2}) (op={1}) updateCount FAIL (exception={3})", lRequestPK, op.sz_operatorname, idJob.value, e.Message),
                                    String.Format("Cnt {0} (job={2}) (op={1}) updateCount FAIL (exception={3})", lRequestPK, op.sz_operatorname, idJob.value, e),
                                    2);
                            }
                            if (aStatusResponse == null)
                            {
                                // getAlertStatusByCountryCode didn't fail, but returned NULL, update database with something
                                Log.WriteLog("jobid: " + idJob.value + " statusResponse is NULL", 2);
                            }
                            else if (aStatusResponse.countryStatusCounts != null)
                            {
                                ccTotal = 0;
                                ccCount = 0;
                                HashSet<int> req_cc = Database.GetReqCountries(lRequestPK);
                                foreach (CountryCodeAlertStatus ccStatus in aStatusResponse.countryStatusCounts)
                                {
                                    ccTotal += ccStatus.subscribersCount;
                                    ccCount++;

                                    req_cc.Remove(ccStatus.countryCode);

                                    sz_updatesql = String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                        ccStatus.countryCode,
                                        op.l_cc_from,
                                        op.sz_iso_from,
                                        op.l_operator,
                                        ccStatus.subscribersCount,
                                        lRequestPK);

                                    lRetVal = Database.Execute(sz_updatesql);
                                    //Log.WriteLog("jobid: " + idJob.value + " updated CC " + ccStatus.countryCode.ToString() + " countrycodes with " + ccStatus.subscribersCount + " subscribers", 0);
                                    Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) updateCount OK (cc={3}, sub={4})", lRequestPK, op.sz_operatorname, idJob.value, ccStatus.countryCode, ccStatus.subscribersCount), 0);
                                }
                                if (aJobStatus.subscriberCount == ccTotal) // cancel if all CC's are accounted for, if not, keep polling this CC
                                {
                                    foreach (int l_cc in req_cc)
                                    {
                                        Database.Execute(
                                            String.Format("sp_upd_tas_count {0}, {1}, '{2}', {3}, {4}, {5}",
                                                l_cc,
                                                op.l_cc_from,
                                                op.sz_iso_from,
                                                op.l_operator,
                                                0,
                                                lRequestPK));
                                        //Log.WriteLog("jobid: " + idJob.value + " updated CC " + l_cc.ToString() + " countrycodes with 0 subscribers", 0);
                                        Log.WriteLog(String.Format("Cnt {0} (job={2}) (op={1}) updateCount OK (cc={3}, sub=0)", lRequestPK, op.sz_operatorname, idJob.value, l_cc), 0);
                                    }
                                    AXInternational.CancelCountInternational(idJob.value, lRequestPK, lOperator);
                                }
                            }
                            else if (aStatusResponse.codeSpecified) // if status is not 310 or 340 the message has most likely been cancelled by the user, ignore it
                            {
                                switch (aStatusResponse.code)
                                {
                                    case 803:   // AX_JOB_EXPIRED
                                    case 202:
                                        // set to completed
                                        Log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")", 0);
                                        //lRetVal = Database.Execute("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'");
                                        break;
                                    case 201: // CC status is preparing and not yet available, just skip this code
                                        break;
                                    default:
                                        // set to cancelled
                                        Log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")", 2);
                                        //lRetVal = Database.Execute("UPDATE LBASEND set l_status=2000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'");
                                        break;
                                }
                            }
                            else
                            {
                                Log.WriteLog("jobid: " + idJob.value + " no action taken", 1);
                            }
                        }
                    }
                    rsJobs.Close();
                    dbConn.Close();
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("UpdateCountCC (exception={0})", e.Message),
                        String.Format("UpdateCountCC (exception={0})", e),
                        2);
                }
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!Program.running)
                        break;
                }
            }
            Log.WriteLog("Stopped CountryCodeAlertStatus thread (count)", 9);
        }

        // Methods for updating International sendings
        // -- should work for LBAS as well
        public static void UpdateJobStatusSend()
        {
            string szJobQuery = "SELECT s.l_refno, s.sz_jobid, s.l_operator, s.l_status FROM LBASEND s WHERE s.l_status=400 OR s.l_status=405 OR (s.l_status=410 AND s.l_items=0)";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            StatusApi aStatus = new StatusApi();
            JobStatusResponse aJobResponse = new JobStatusResponse();
            JobId idJob = new JobId();

            int lRetVal;
            int lRefNo;
            int lOperator;
            int lStatus;

            while (Program.running)
            {
                try
                {
                    dbConn.Open();
                    rsJobs = cmdJobs.ExecuteReader();
                    while (rsJobs.Read() && Program.running)
                    {
                        if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)) || rsJobs.IsDBNull(3)) // required values missing from LBASEND
                        {
                            lRefNo = rsJobs.GetInt32(0);
                            idJob.value = rsJobs.GetString(1);
                            lOperator = rsJobs.GetInt32(2);
                            lStatus = rsJobs.GetInt32(3);

                            Operator op = Operator.GetOperator(lOperator);

                            aStatus.Url = op.sz_url + "StatusApi";

                            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                            Uri uri = new Uri(aStatus.Url);
                            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

                            aStatus.Credentials = objAuth;
                            aStatus.PreAuthenticate = true;

                            try
                            {
                                aJobResponse = aStatus.getJobStatus(idJob);
                            }
                            catch (Exception e)
                            {
                                Log.WriteLog(
                                    String.Format("Snd {0} (job={2}) (op={1}) jobStatus FAIL (exception={3})", lRefNo, op.sz_operatorname, idJob.value, e.Message),
                                    String.Format("Snd {0} (job={2}) (op={1}) jobStatus FAIL (exception={3})", lRefNo, op.sz_operatorname, idJob.value, e),
                                    2);
                            }

                            if (aJobResponse.codeSpecified)// && aJobResponse.jobStatusSpecified)
                            {
                                if (aJobResponse.code == 200)
                                {
                                    HashSet<int> h_cc = Database.GetSendCountries(lRefNo);
                                    switch (aJobResponse.jobStatus.Value.ToString())
                                    {
                                        case "NO_SUBSCRIBERS":
                                            Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus NO_SUBSCRIBERS", lRefNo, op.sz_operatorname, idJob.value), 0);
                                            // populate LBAHISTCC with 0 subscribers
                                            foreach (int l_cc in h_cc)
                                            {
                                                Database.Execute(
                                                    String.Format("sp_upd_status_lba_cc {0},{1},{2},0,0,0,0,0,0,0",
                                                    lRefNo,
                                                    op.l_operator,
                                                    l_cc));
                                            }
                                            lRetVal = Database.Execute("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                            break;
                                        case "PROCESSING_SUBSCRIBERS":
                                            if (lStatus == 400)
                                            {
                                                Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus PROCESSING_SUBSCRIBERS {3} subscribers in {4} country codes", lRefNo, op.sz_operatorname, idJob.value, aJobResponse.subscriberCount, h_cc.Count), 0);
                                                lRetVal = Database.Execute("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=405 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                            }
                                            break;
                                        case "PREPARED":
                                            Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus PREPARED {3} subscribers in {4} country codes", lRefNo, op.sz_operatorname, idJob.value, aJobResponse.subscriberCount, h_cc.Count), 0);
                                            lRetVal = Database.Execute("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=410 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                            break;
                                        case "SUBMITTING_STARTED":
                                        case "SUBMITTING":
                                            Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus SENDING {3} subscribers in {4} country codes", lRefNo, op.sz_operatorname, idJob.value, aJobResponse.subscriberCount, h_cc.Count), 0);
                                            lRetVal = Database.Execute("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=440 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                            break;
                                        case "ERROR":
                                            string sz_requestcc = "";
                                            foreach (int l_cc in h_cc)
                                            {
                                                if (sz_requestcc != "")
                                                    sz_requestcc += ",";
                                                sz_requestcc += l_cc.ToString();
                                            }

                                            Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus ERROR (cc={3})", lRefNo, op.sz_operatorname, idJob.value, sz_requestcc), 0);
                                            lRetVal = Database.Execute("UPDATE LBASEND set l_status=" + Constant.ERR_JobError + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                            break;
                                        case "PREPARING":
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                else
                                {
                                    Log.WriteLog(aJobResponse.code.ToString() + " - " + idJob.value + " - " + aJobResponse.message, 2);
                                }
                            }
                            else
                            {
                                Log.WriteLog("JobId: " + idJob.value + " jobResponse.code not specified", 2);
                            }
                        }
                    }
                    rsJobs.Close();
                    dbConn.Close();
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("UpdateJobStatusSend (exception={0})", e.Message),
                        String.Format("UpdateJobStatusSend (exception={0})", e),
                        2);
                }
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!Program.running)
                        break;
                }
            }
            Log.WriteLog("Stopped JobStatus thread (send)", 9);
        }
        public static void UpdateSendCC() 
        {
            string szJobQuery = "SELECT l_refno, sz_jobid, l_operator FROM LBASEND where l_status=440 or l_status=410";

            OdbcConnection dbConn = new OdbcConnection(Settings.sz_dbconn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            JobId idJob = new JobId();

            int lRefNo;
            int lOperator;

            while (Program.running)
            {
                try
                {
                    dbConn.Open();
                    rsJobs = cmdJobs.ExecuteReader();
                    while (rsJobs.Read() && Program.running)
                    {
                        if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)))
                        {
                            lRefNo = rsJobs.GetInt32(0);
                            idJob.value = rsJobs.GetString(1);
                            lOperator = rsJobs.GetInt32(2);

                            UpdateSendCC(idJob, lRefNo, lOperator);
                        }
                    }
                    rsJobs.Close();
                    dbConn.Close();
                }
                catch (Exception e)
                {
                    Log.WriteLog(
                        String.Format("UpdateSendCC (exception={0})", e.Message),
                        String.Format("UpdateSendCC (exception={0})", e),
                        2);
                }
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!Program.running)
                        break;
                }
            }
            Log.WriteLog("Stopped CountryCodeAlertStatus thread (send)", 9);
        }
    
        // Method for processing individual CC request for International sendings
        // -- should work for LBAS as well
        private static void UpdateSendCC(JobId idJob, int lRefNo, int lOperator)
        {
            StatusApi aStatus = new StatusApi();
            CountryCodeAlertStatusResponse aStatusResponse = new CountryCodeAlertStatusResponse();
            Operator op = Operator.GetOperator(lOperator);

            aStatus.Url = op.sz_url + "StatusApi";

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aStatus.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aStatus.Credentials = objAuth;
            aStatus.PreAuthenticate = true;

            int lRetVal;
            int cc;

            int ccDelivered;
            int ccExpired;
            int ccFailed;
            int ccUnknown;

            int ccSubmitted;
            int ccQueued;

            int ccSubscribers;

            int lItems = 0;
            int lProc = 0;
            int lCountSub = 0;
            int lStatus = 0;

            int lPrevProc = 0;

            try
            {
                Database.GetSendingProc(lRefNo, lOperator, ref lItems, ref lPrevProc, ref lStatus);
                aStatusResponse = aStatus.getAlertStatusByCountryCode(idJob);
            }
            catch (Exception e)
            {
                Log.WriteLog(e.Message.ToString(),e.ToString(),2);
            }

            if (aStatusResponse == null)
            {
                // getAlertStatusByCountryCode didn't fail, but returned NULL, update database with something
                Log.WriteLog("jobid: " + idJob.value + " statusResponse is NULL (status=" + lStatus.ToString() + ")",2);
            }
            else if (aStatusResponse.countryStatusCounts != null)
            {
                HashSet<int> h_cc = Database.GetSendCountries(lRefNo);
                foreach (CountryCodeAlertStatus ccStatus in aStatusResponse.countryStatusCounts)
                {
                    h_cc.Remove(ccStatus.countryCode);

                    cc = ccStatus.countryCode;

                    ccDelivered = ccStatus.deliveredSuccessfully;
                    ccExpired = ccStatus.deliveryExpired;
                    ccFailed = ccStatus.deliveryFailed + ccStatus.submissionFailed;
                    ccUnknown = ccStatus.deliveryUnknown;

                    ccSubmitted = ccStatus.submitted;
                    ccQueued = ccStatus.queued;

                    ccSubscribers = ccStatus.subscribersCount;
                    lCountSub += ccSubscribers;
                    lProc += ccDelivered + ccExpired + ccFailed + ccUnknown;

                    lRetVal = Database.Execute("sp_upd_status_lba_cc " + lRefNo.ToString() + ", " + lOperator.ToString() + ", " + cc.ToString() + ", " + ccDelivered.ToString() + ", " + ccExpired.ToString() + ", " + ccFailed.ToString() + ", " + ccUnknown.ToString() + ", " + ccSubmitted.ToString() + ", " + ccQueued.ToString() + ", " + ccSubscribers.ToString());
                }

                if (lProc == lItems && lStatus == 440 && lItems > 0) // done sending
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus DONE (sub={3})", lRefNo, op.sz_operatorname, idJob.value, lItems), 0);
                    //Log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status done (" + lItems.ToString() + " recipients)", 0);
                    lRetVal = Database.Execute("UPDATE LBASEND set l_items=" + lItems.ToString() + ", l_proc=" + lProc.ToString() + ", l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                    // populate LBAHISTCC with 0 subscribers for CC that are missing from statusresponse
                    foreach (int l_cc in h_cc)
                    {
                        Database.Execute(
                            String.Format("sp_upd_status_lba_cc {0},{1},{2},0,0,0,0,0,0,0",
                            lRefNo,
                            op.l_operator,
                            l_cc));
                    }
                }
                else if (lItems == lCountSub && lStatus == 410 && lItems > 0) // got all ccode statuses from prepared sending (skip those with 0 items)
                {
                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus PREPARED Got all cc for prepared sending (sub={3})", lRefNo, op.sz_operatorname, idJob.value, lItems), 0);
                    //Log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Got all cc for prepared sending (" + lItems.ToString() + " recipients)", 0);
                    lRetVal = Database.Execute("UPDATE LBASEND set l_status=411 WHERE l_refno=" + lRefNo.ToString() + " AND l_status=410 AND l_operator=" + lOperator.ToString());
                    // populate LBAHISTCC with 0 subscribers for CC that are missing from statusresponse
                    foreach (int l_cc in h_cc)
                    {
                        Database.Execute(
                            String.Format("sp_upd_status_lba_cc {0},{1},{2},0,0,0,0,0,0,0",
                            lRefNo,
                            op.l_operator,
                            l_cc));
                    }
                }
                else // sending in progress
                {
                    if (lItems == 0)
                    {
                        JobStatusResponse aJobResponse = new JobStatusResponse();
                        try
                        {
                            aJobResponse = aStatus.getJobStatus(idJob);
                        }
                        catch (Exception e)
                        {
                            Log.WriteLog(e.ToString(), e.Message,2);
                        }
                        if (aJobResponse.codeSpecified)// && aJobResponse.jobStatusSpecified)
                        {
                            if (aJobResponse.code == 200)
                            {
                                lItems = aJobResponse.subscriberCount;
                                if (lItems > 0)
                                {
                                    Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus SENDING Status adjusting items (sub={3})", lRefNo, op.sz_operatorname, idJob.value, lItems), 0);
                                    //Log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status adjusting items (" + lItems.ToString() + " subscribers)", 0);
                                    lRetVal = Database.Execute("UPDATE LBASEND set l_items=" + lItems.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                                }
                            }
                            else
                            {
                                Log.WriteLog(aJobResponse.code.ToString() + " - " + idJob.value + " - " + aJobResponse.message,2);
                            }
                        }
                        else
                        {
                            Console.WriteLine("jobResponse.code not specified");
                        }
                    }

                    if (lPrevProc < lProc && lProc > 0)
                    {
                        Log.WriteLog(String.Format("Snd {0} (job={2}) (op={1}) jobStatus SENDING Status update (sub={3} of {4}) processed", lRefNo, op.sz_operatorname, idJob.value, lProc, lItems), 0);
                        //Log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status update (" + lProc.ToString() + " of " + lItems.ToString() + " delivered)",0);
                        lRetVal = Database.Execute("UPDATE LBASEND set l_proc=" + lProc.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString());
                    }
                }
            }
            else if (aStatusResponse.codeSpecified && (lStatus == 410 || lStatus == 440)) // if status is not 310 or 340 the message has most likely been cancelled by the user, ignore it
            {
                switch (aStatusResponse.code)
                {
                    case 803:   // AX_JOB_EXPIRED
                    case 202:   // AX_NO_SUBSCRIBERS
                        // set to completed
                        Log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")",2);
                        lRetVal = Database.Execute("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'");
                        break;
                    case 201:   // still preparing (do nothing, but log a warning)
                        Log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")", 1);
                        break;
                    default:
                        // set to cancelled
                        Log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")",2);
                        lRetVal = Database.Execute("UPDATE LBASEND set l_status=2000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'");
                        break;
                }
            }
            else
            {
                Log.WriteLog("jobid: " + idJob.value + " no action taken (status=" + lStatus.ToString() + ")",2);
            }
        }
    }
}
