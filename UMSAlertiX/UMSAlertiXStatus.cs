using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Threading;
using System.Net;
using System.Data.Odbc;
using UMSAlertiX.AlertiXStatusApi;

namespace UMSAlertiX
{
    public class UMSAlertiXStatus
    {
        UMSAlertiXController oController;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public void GetAllJobs()
        {
            StatusApi aStatus = new StatusApi();
            AllJobsResponse aResponse = new AllJobsResponse();
            JobId idJob = new JobId();

            NetworkCredential objNetCredentials = new NetworkCredential("jone", "jone");
            Uri uri = new Uri(aStatus.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aStatus.Url = oController.statusapi;
            aStatus.Credentials = objAuth;
            aStatus.PreAuthenticate = true;

            aResponse = aStatus.getAllJobs();
        }

        public void UpdateJobStatus() // checks status of jobs
        {
            string szJobQuery = "SELECT s.l_refno, s.sz_jobid, s.l_operator, s.l_status FROM LBASEND s WHERE s.l_status=300 OR s.l_status=305 OR (s.l_status=310 AND s.l_items=0)";

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            StatusApi aStatus = new StatusApi();
            JobStatusResponse aJobResponse = new JobStatusResponse();
            JobId idJob = new JobId();

            int lRetVal;
            int lRefNo;
            int lOperator;
            int lStatus;

            dbConn.Open();
            while (oController.running)
            {
                rsJobs = cmdJobs.ExecuteReader();
                while (rsJobs.Read() && oController.running)
                {
                    if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2))|| rsJobs.IsDBNull(3)) // required values missing from LBASEND
                    {
                        lRefNo = rsJobs.GetInt32(0);
                        idJob.value = rsJobs.GetString(1);
                        lOperator = rsJobs.GetInt32(2);
                        lStatus = rsJobs.GetInt32(3);
                        //Console.WriteLine("Checking refno={0} job={1} operator={2} status={3}", lRefNo.ToString(), idJob.value, lOperator.ToString(), lStatus.ToString());

                        Operator op = oController.GetOperator(lOperator);

                        aStatus.Url = op.sz_url + oController.statusapi; // "http://lbv.netcom.no:8080/alertix/StatusApi";

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
                            oController.log.WriteLog(e.ToString(), e.Message);
                        }

                        if (aJobResponse.codeSpecified)// && aJobResponse.jobStatusSpecified)
                        {
                            if (aJobResponse.code == 200)
                            {
                                switch (aJobResponse.jobStatus.Value.ToString())
                                {
                                    case "NO_SUBSCRIBERS":
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status NO_SUBSCRIBERS");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case "PROCESSING_SUBSCRIBERS":
                                        if (lStatus == 300)
                                        {
                                            //UpdateCCStatus(idJob, lRefNo, lOperator);
                                            oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status PROCESSING_SUBSCRIBERS (" + aJobResponse.subscriberCount.ToString() + " items)");
                                            lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=305 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        }
                                        break;
                                    case "PREPARED":
                                        if (oController.GetRequestType(lRefNo) == 2)
                                        {
                                            oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status PREPARED (" + aJobResponse.subscriberCount.ToString() + " items) (request type 2, performing autocancel)");
                                            UMSAlertiXAlert oAlert = new UMSAlertiXAlert();
                                            UMSAlertiX.AlertiXAlertApi.JobId idCancelJob = new UMSAlertiX.AlertiXAlertApi.JobId();

                                            idCancelJob.value = idJob.value;
                                            oAlert.CancelPreparedAlert(ref idCancelJob, lRefNo, lOperator);
                                        }
                                        else
                                        {
                                            oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status PREPARED (" + aJobResponse.subscriberCount.ToString() + " items)");
                                            lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=310 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        }
                                        //UpdateCCStatus(idJob, lRefNo, lOperator);
                                        break;
                                    case "SUBMITTING_STARTED":
                                    case "SUBMITTING":
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status SENDING (" + aJobResponse.subscriberCount.ToString() + " items)");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + aJobResponse.subscriberCount.ToString() + ", l_proc=0, l_status=340 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case "ERROR":
                                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status ERROR");
                                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=42003 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                        break;
                                    case "PREPARING":
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else
                            {
                                oController.log.WriteLog(aJobResponse.code.ToString() + " - " + idJob.value + " - " + aJobResponse.message);
                            }
                        }
                        else
                        {
                            Console.WriteLine("jobResponse.code not specified");
                        }
                    }
                }
                rsJobs.Close();
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!oController.running)
                        break;
                }
            }
            dbConn.Close();
            oController.threads--;
            Console.WriteLine("# Stopped Job Status thread", 1);
        }

        public void UpdateCCStatus() // updates jobs with country code status
        {
            string szJobQuery = "SELECT l_refno, sz_jobid, l_operator FROM LBASEND where l_status=340 or l_status=310";

            OdbcConnection dbConn = new OdbcConnection(oController.dsn);
            OdbcCommand cmdJobs = new OdbcCommand(szJobQuery, dbConn);
            OdbcDataReader rsJobs;

            JobId idJob = new JobId();

            int lRefNo;
            int lOperator;
            dbConn.Open();

            while (oController.running)
            {
                rsJobs = cmdJobs.ExecuteReader();
                while (rsJobs.Read() && oController.running)
                {
                    if (!(rsJobs.IsDBNull(0) || rsJobs.IsDBNull(1) || rsJobs.IsDBNull(2)))
                    {
                        lRefNo = rsJobs.GetInt32(0);
                        idJob.value = rsJobs.GetString(1);
                        lOperator = rsJobs.GetInt32(2);
                        //Console.WriteLine("Checking status for refno={0} jobid={1} operator={2}", lRefNo.ToString(), idJob.value, lOperator.ToString());

                        UpdateCCStatus(idJob, lRefNo, lOperator);
                    }
                }
                rsJobs.Close();
                for (int i = 0; i < 5; i++)
                {
                    Thread.Sleep(1000);
                    if (!oController.running)
                        break;
                }
            }
            dbConn.Close();
            oController.threads--;
            Console.WriteLine("# Stopped CC Status thread", 1);
        }

        private bool UpdateCCStatus(JobId idJob, int lRefNo, int lOperator)
        {
            StatusApi aStatus = new StatusApi();
            CountryCodeAlertStatusResponse aStatusResponse = new CountryCodeAlertStatusResponse();
            Operator op = oController.GetOperator(lOperator);

//            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            aStatus.Url = op.sz_url + oController.statusapi; // "http://lbv.netcom.no:8080/alertix/StatusApi";

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aStatus.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aStatus.Credentials = objAuth;
            aStatus.PreAuthenticate = true;

            bool bReturn = true;

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
                oController.GetSendingProc(lRefNo, lOperator, ref lItems, ref lPrevProc, ref lStatus);
                aStatusResponse = aStatus.getAlertStatusByCountryCode(idJob);
            }
            catch (Exception e)
            {
                oController.log.WriteLog(e.ToString(), e.Message.ToString());
            }

            if (aStatusResponse == null)
            {
                // getAlertStatusByCountryCode didn't fail, but returned NULL, update database with something
                oController.log.WriteLog("jobid: " + idJob.value + " statusResponse is NULL (status=" + lStatus.ToString() + ")");
            }
            else if (aStatusResponse.countryStatusCounts != null)
            {
                foreach (CountryCodeAlertStatus ccStatus in aStatusResponse.countryStatusCounts)
                {
                    cc = ccStatus.countryCode;

                    ccDelivered = ccStatus.deliveredSuccessfully;
                    ccExpired = ccStatus.deliveryExpired;
                    ccFailed = ccStatus.deliveryFailed + ccStatus.submissionFailed;
                    ccUnknown = ccStatus.deliveryUnknown;

                    ccSubmitted = ccStatus.submitted;
                    ccQueued = ccStatus.queued;

                    ccSubscribers = ccStatus.subscribersCount;
                    lCountSub += ccSubscribers;
                    lProc += ccDelivered + ccExpired + ccFailed;

                    lRetVal = oController.ExecDB("sp_upd_status_lba_cc " + lRefNo.ToString() + ", " + lOperator.ToString() + ", " + cc.ToString() + ", " + ccDelivered.ToString() + ", " + ccExpired.ToString() + ", " + ccFailed.ToString() + ", " + ccUnknown.ToString() + ", " + ccSubmitted.ToString() + ", " + ccQueued.ToString() + ", " + ccSubscribers.ToString(), oController.dsn);
                }

                if (lProc == lItems && lStatus == 340 && lItems>0) // done sending
                {
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status done (" + lItems.ToString() + " recipients)");
                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + lItems.ToString() + ", l_proc=" + lProc.ToString() + ", l_status=1000 WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                }
                else if (lItems == lCountSub && lStatus == 310 && lItems>0) // got all ccode statuses from prepared sending (skip those with 0 items)
                {
                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Got all cc for prepared sending (" + lItems.ToString() + " recipients)");
                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=311 WHERE l_refno=" + lRefNo.ToString() + " AND l_status=310 AND l_operator=" + lOperator.ToString(), oController.dsn);
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
                            oController.log.WriteLog(e.ToString(), e.Message);
                        }
                        if (aJobResponse.codeSpecified)// && aJobResponse.jobStatusSpecified)
                        {
                            if (aJobResponse.code == 200)
                            {
                                lItems = aJobResponse.subscriberCount;
                                if (lItems > 0)
                                {
                                    oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status adjusting items (" + lItems.ToString() + " subscribers)");
                                    lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=" + lItems.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                                }
                            }
                            else
                            {
                                oController.log.WriteLog(aJobResponse.code.ToString() + " - " + idJob.value + " - " + aJobResponse.message);
                            }
                        }
                        else
                        {
                            Console.WriteLine("jobResponse.code not specified");
                        }
                    }

                    if (lPrevProc < lProc && lProc > 0)
                    {
                        oController.log.WriteLog(lRefNo.ToString() + " (" + op.sz_operatorname + ") Status update (" + lProc.ToString() + " of " + lItems.ToString() + " delivered)");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_proc=" + lProc.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString(), oController.dsn);
                    }
                }
            }
            else if (aStatusResponse.codeSpecified && (lStatus == 310 || lStatus == 340)) // if status is not 310 or 340 the message has most likely been cancelled by the user, ignore it
            {
                switch (aStatusResponse.code)
                {
                    case 803:   // AX_JOB_EXPIRED
                    case 202:   // AX_NO_SUBSCRIBERS
                        // set to completed
                        oController.log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_items=0, l_proc=0, l_status=1000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'", oController.dsn);
                        break;
                    default:
                        // set to cancelled
                        oController.log.WriteLog("jobid: " + idJob.value + " error (" + aStatusResponse.code.ToString() + ") (" + aStatusResponse.message + ")");
                        lRetVal = oController.ExecDB("UPDATE LBASEND set l_status=2000, l_response=" + aStatusResponse.code.ToString() + " WHERE l_refno=" + lRefNo.ToString() + " AND l_operator=" + lOperator.ToString() + " AND sz_jobid='" + idJob.value + "'", oController.dsn);
                        break;
                }
            }
            else
            {
                oController.log.WriteLog("jobid: " + idJob.value + " no action taken (status=" + lStatus.ToString() + ")");
            }
            return bReturn;
        }
    }
}
