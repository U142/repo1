using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsFile;
using com.ums.PARM.AlertExec;
using com.ums.PAS.Address;
using com.ums.PAS.Database;
using System.Xml;
using com.ums.UmsCommon.Audio;
using System.Collections.Generic;


namespace com.ums.ws.parm
{
    /// <summary>
    /// external PARM Alert and Event execution
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/parm/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class parmws : System.Web.Services.WebService
    {

        [WebMethod]
        public PAEVENT[] GetEventListTest()
        {
            /*ULOGONINFO logoninfo = new ULOGONINFO();
            logoninfo.sz_userid = "MH";
            logoninfo.sz_compid = "UMS";
            logoninfo.sz_password = "mh123,1";
            logoninfo.sz_deptid = "TEST";
            return GetEventList(logoninfo);*/
            return null;

        }

        [WebMethod]
        public PAEVENT[] GetEventList(com.ums.UmsCommon.ULOGONINFO logoninfo)
        {
            try
            {
                PASUmsDb db = new PASUmsDb();
                try
                {
                    if (db.CheckLogonLiteral(ref logoninfo))
                    {
                        List<PAEVENT> events = new List<PAEVENT>();
                        db.GetEventAlertStructure(ref logoninfo, ref events);

                        return events.ToArray();
                    }
                    else
                        throw new ULogonFailedException();
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        protected void _init()
        {
            /*UCommon.UPATHS.sz_path_backbone = "d:\\ums\\aep\\eat\\";
            UCommon.UPATHS.sz_path_bcp = "d:\\ums\\bcp\\eat\\";
            UCommon.UPATHS.sz_path_predefined_areas = "D:\\ums\\wwwroot\\vb4\\predefined-areas\\";
            UCommon.UPATHS.sz_path_temp = "D:\\ums\\temp\\";
            UCommon.UPATHS.sz_path_mapsendings = "D:\\ums\\mapsendings\\";
            UCommon.UPATHS.sz_path_lba = "D:\\ums\\lba\\";

            UCommon.UBBDATABASE.sz_dsn = "backbone_ibuki";
            UCommon.UBBDATABASE.sz_uid = "sa";
            UCommon.UBBDATABASE.sz_pwd = "diginform";
            */
            //UCommon.Initialize("Web Service - wsPASExec");
        }

        protected int ValidateFunction(String s)
        {
            if (s.Equals("live"))
                return UCommon.USENDING_LIVE;
            else if (s.Equals("simulate"))
                return UCommon.USENDING_SIMULATION;
            else if (s.Equals("test"))
                return UCommon.USENDING_TEST;
            return -1;
        }

        [WebMethod]
        public ExecResponse ExecTasSending(UTASSENDING tas)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(tas);
            response.parseFromXml(ref doc, "l_alertpk");
            return response;
        }

        [WebMethod]
        public ExecResponse ExecPolygonSending(UPOLYGONSENDING poly)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(poly);
            response.parseFromXml(ref doc, "l_alertpk"); 
            return response;
        }

        [WebMethod]
        public ExecResponse ExecEllipseSending(UELLIPSESENDING ellipse)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(ellipse);
            response.parseFromXml(ref doc, "l_alertpk");
            return response;
        }
        [WebMethod]
        public ExecResponse ExecGisSending(UGISSENDING gis)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(gis);
            response.parseFromXml(ref doc, "l_alertpk");
            return response;
        }
        [WebMethod]
        public ExecResponse ExecMunicipalSending(UMUNICIPALSENDING municipal)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(municipal);
            response.parseFromXml(ref doc, "l_alertpk");
            return response;
        }
        [WebMethod]
        public ExecResponse ExecTestSending(UTESTSENDING send)
        {
            //for(int i = 0;i<send.numbers.Count;++i)
              //  send.numbers[i] = "NA," + send.numbers[i];
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecMapSending(send);
            response.parseFromXml(ref doc, "l_alertpk");
            return response;
        }



        [WebMethod]
        public UMapBounds GetMapBoundsFromSending(ULOGONINFO logon, UMAPSENDING s)
        {
            try
            {
                //UAdrDb db = new UAdrDb(logon.sz_stdcc, 120);
                if (typeof(UMUNICIPALSENDING).Equals(s.GetType()))
                {
                    UMUNICIPALSENDING m = (UMUNICIPALSENDING)s;
                    UAdrDb db = new UAdrDb(logon.sz_stdcc, 60, logon.l_deptpk);
                    return db.GetMunicipalBounds(ref m);
                }

            }
            catch (Exception e)
            {
                throw e;
            }
            throw new NotSupportedException();
        }

        [WebMethod]
        public UAdrCount GetAdrCountPolygon(ULOGONINFO logon, UPOLYGONSENDING s)
        {
            return GetAdrCount(logon, s);
        }
        [WebMethod]
        public UAdrCount GetAdrCountEllipse(ULOGONINFO logon, UELLIPSESENDING s)
        {
            return GetAdrCount(logon, s);
        }
        [WebMethod]
        public UAdrCount GetAdrCountGis(ULOGONINFO logon, UGIS s)
        {
            //return GetAdrCount(logon, s);
            throw new NotSupportedException();
        }
        [WebMethod]
        public UAdrCount GetAdrCountMunicipal(ULOGONINFO logon, UMUNICIPALSENDING s)
        {
            return GetAdrCount(logon, s);
        }
        [WebMethod]
        public UAdrCount GetAdrCountTestsending(ULOGONINFO logon, UTESTSENDING s)
        {
            return GetAdrCount(logon, s);
        }

        [WebMethod]
        public UAdrCount GetAdrCount(ULOGONINFO logon, UMAPSENDING mapsending)
        {
            try
            {
                if ((mapsending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_AND_MOBILE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_AND_MOBILE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY_AND_FIXED) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE_AND_FIXED) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOVED_COMPANY) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.MOVED_PRIVATE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.NOPHONE_COMPANY) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.NOPHONE_PRIVATE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE) > 0 ||
                   (mapsending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0)
                {
                    return new UAdrDb(logon.sz_stdcc, 120, logon.l_deptpk).GetAddressCount(ref logon, ref mapsending);
                }
                else
                {
                    return new UAdrCount();
                }

            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * Resending tas or lba
         */
        [WebMethod]
        public ExecResponse TASResend(int refno, int lbaoperator, ULOGONINFO logoninfo) //
        {
            ExecResponse response = new ExecResponse();
            USimpleXmlWriter xml = new USimpleXmlWriter("iso-8859-1");
            xml.insertStartDocument();
            xml.insertComment("Results for ExecEvent");
            xml.insertStartElement(String.Format("results"));
            //xml.insertAttribute("l_eventpk", l_eventpk.ToString());
            //xml.insertAttribute("f_simulation", f_simulation.ToString());
            // This is now done in prepare_single_operator_resend
            /*
            xml.insertAttribute("l_projectpk", sending.n_projectpk.ToString());
            xml.insertAttribute("l_refno", sending.n_refno.ToString());
            xml.insertAttribute("sz_function", sending.sz_function);
            */
            ParmGenerateSending parm = new ParmGenerateSending();
            if (parm.Initialize(ref logoninfo, ref xml))
            {
                parm.prepare_single_operator_resend(refno, logoninfo, lbaoperator);
            }
            cleanup(ref parm);
            xml.insertEndElement(); //event
            xml.insertEndDocument();
            xml.finalize();
            
            XmlDocument xmldoc = xml.GetXmlDocument();
            response.parseFromXml(ref xmldoc, "l_alertpk");
            return response;
        }

        /*
         * Common function for ExecPolygonSending, ExecEllipseSending and ExecGixSending
         */
        protected XmlDocument ExecMapSending(UMAPSENDING sending)
        {
            ExecResponse response = new ExecResponse();
            USimpleXmlWriter xml = new USimpleXmlWriter("iso-8859-1");
            xml.insertStartDocument();
            xml.insertComment("Results for ExecEvent");
            xml.insertStartElement(String.Format("results"));
            //xml.insertAttribute("l_eventpk", l_eventpk.ToString());
            //xml.insertAttribute("f_simulation", f_simulation.ToString());
            xml.insertAttribute("l_projectpk", sending.n_projectpk.ToString());
            xml.insertAttribute("l_refno", sending.n_refno.ToString());
            xml.insertAttribute("sz_function", sending.sz_function);
            ParmGenerateSending parm = new ParmGenerateSending();
            if (parm.Initialize(ref sending.logoninfo, ref xml))
            {
                int function = ValidateFunction(sending.sz_function);
                if (function == -1)
                {
                    parm.setAlertInfo(false, "0", 0, sending.n_refno, "", "Invalid function specified [" + sending.sz_function + "]", "ExecMapSending()", ParmGenerateSending.SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    //parm.SendAlert(l_alertpk, function, sz_scheddate, sz_schedtime);
                    parm.SendMapsending(ref sending, function);
                }
            }
            cleanup(ref parm);
            xml.insertEndElement(); //event
            xml.insertEndDocument();
            xml.finalize();
            return xml.GetXmlDocument();

        }

        [WebMethod]
        public ExecResponse ExecAlertV2(Int64 l_alertpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecAlert(l_alertpk, l_comppk, l_deptpk, l_userpk, sz_compid, sz_userid,
                                        sz_deptid, sz_password, sz_function, sz_scheddate, sz_schedtime, null, "");
            response.parseFromXml(ref doc, "l_alertpk");

            return response;
        }

        [WebMethod]
        public ExecResponse ExecAlertV3(Int64 l_alertpk, ULOGONINFO logon, String sz_function, String sz_scheddate, String sz_schedtime)
        {
            try
            {
                PercentProgress.SetPercentDelegate percentdelegate = PercentProgress.newDelegate();
                percentdelegate(ref logon, ProgressJobType.PARM_SEND, new PercentResult());

                ExecResponse response = new ExecResponse();
                XmlDocument doc = ExecAlert(l_alertpk, logon.l_comppk, logon.l_deptpk, logon.l_userpk, logon.sz_compid,
                    logon.sz_userid, logon.sz_deptid, logon.sz_password, sz_function, sz_scheddate, sz_schedtime, percentdelegate, logon.jobid);
                response.parseFromXml(ref doc, "l_alertpk");
                return response;
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                PercentProgress.DeleteJob(ref logon, ProgressJobType.PARM_SEND);
            }
        }

        [WebMethod]
        public ExecResponse ExecEventV3(Int64 l_eventpk, String sz_compid, String sz_userid, String sz_deptid,
                                        String sz_password, String sz_function, String sz_scheddate,
                                        String sz_schedtime)
        {
            ExecResponse response = new ExecResponse();

            XmlDocument doc = ExecEvent(l_eventpk, 0, 0, 0, sz_compid, sz_userid, sz_deptid,
                                        sz_password, sz_function, sz_scheddate, sz_schedtime, null, "");
            response.parseFromXml(ref doc, "l_eventpk");
            return response;
        }

        [WebMethod]
        public ExecResponse ExecEventV4(Int64 l_eventpk, ULOGONINFO logon, String sz_function, String sz_scheddate,
                                        String sz_schedtime)
        {
            try
            {
                ExecResponse response = new ExecResponse();
                PercentProgress.SetPercentDelegate percentdelegate = PercentProgress.newDelegate();
                percentdelegate(ref logon, ProgressJobType.PARM_SEND, new PercentResult());

                XmlDocument doc = ExecEvent(l_eventpk, 0, 0, 0, logon.sz_compid, logon.sz_userid, logon.sz_deptid,
                                            logon.sz_password, sz_function, sz_scheddate, sz_schedtime, percentdelegate, logon.jobid);
                response.parseFromXml(ref doc, "l_eventpk");
                return response;
            }
            catch (Exception e)
            {
                throw e;

            }
            finally
            {
                PercentProgress.DeleteJob(ref logon, ProgressJobType.PARM_SEND);
            }

        }

        [WebMethod]
        public ExecResponse ExecEventV2(Int64 l_eventpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecEvent(l_eventpk, l_comppk, l_deptpk, l_userpk,
                                        sz_compid, sz_userid, sz_deptid, sz_password,
                                        sz_function, sz_scheddate, sz_schedtime, null, "");
            response.parseFromXml(ref doc, "l_eventpk");

            return response;
        }

        [WebMethod]
        public UConfirmJobResponse ConfirmJob_2_0(ULOGONINFO logon, int l_refno, bool b_confirm)
        {
            //check if jobid is correct to l_refno in LBASEND
            //check if l_status still is = 5 i sendinginfo (ready for confirmation)
            //write confirm file to LBA server
            UConfirmJobResponse ret = new UConfirmJobResponse();
            ret.l_refno = l_refno;
            //ret.sz_jobid = sz_jobid;
            try
            {
                PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 60);
                if (!db.CheckLogon(ref logon))
                {
                    //logon failed
                    ret.resultcode = -1;
                    ret.resulttext = "Logon failed";

                }
                else
                {
                    List<ULBASENDING> sendings = new List<ULBASENDING>();
                    if (db.GetLbaSendsByRefno(l_refno, ref sendings))
                    {
                        for (int i = 0; i < sendings.Count; i++)
                        {
                            String sz_jobid = sendings[i].sz_jobid;
                            if (!db.VerifyJobIDAndRefno_2_0(l_refno, sz_jobid, sendings[i].l_operator))
                            {
                                ret.resultcode = -1;
                                ret.resulttext = "Could not verify refno and jobid";
                            }
                            else
                            {
                                bool b_simulation = db.GetIsSimulation(l_refno);
                                USimpleXmlWriter xmlwriter = new USimpleXmlWriter("iso-8859-1");
                                xmlwriter.insertStartDocument();
                                xmlwriter.insertStartElement("LBA");
                                xmlwriter.insertAttribute("operation", (b_confirm ? "ConfirmJob" : "CancelJob"));
                                xmlwriter.insertAttribute("l_refno", l_refno.ToString());
                                xmlwriter.insertAttribute("sz_jobid", sz_jobid);
                                xmlwriter.insertAttribute("f_simulation", (b_simulation ? "1" : "0"));
                                xmlwriter.insertAttribute("l_operator", sendings[i].l_operator.ToString());
                                xmlwriter.insertAttribute("l_deptpk", logon.l_deptpk.ToString());
                                xmlwriter.insertAttribute("l_comppk", logon.l_comppk.ToString());
                                xmlwriter.insertAttribute("l_userpk", logon.l_userpk.ToString());
                                xmlwriter.insertAttribute("sz_compid", logon.sz_compid);
                                xmlwriter.insertAttribute("sz_deptid", logon.sz_deptid);
                                xmlwriter.insertAttribute("sz_userid", logon.sz_userid);

                                xmlwriter.insertEndElement();
                                xmlwriter.insertEndDocument();
                                xmlwriter.finalize();
                                ConfirmLBAWriter lba = new ConfirmLBAWriter(l_refno, sz_jobid);
                                lba.writeline(xmlwriter.getXml());
                                lba.close();

                                db.SetLBAStatus(l_refno, (b_confirm ? 320 : 330), "310, 311", sendings[i].l_operator); //320 = Confirmed by user, 330 = cancelled by user, but only if status is 310
                                if (!lba.publish())
                                {
                                    ret.resultcode = -1;
                                    ret.resulttext = "Failed to publish LBA Confirmation file";
                                }
                                else
                                {
                                    ret.resultcode = (b_confirm ? 0 : 1000);
                                    ret.resulttext = (b_confirm ? "CONFIRMED" : "CANCELLED");
                                }

                            }
                        }
                    }
                }
                db.close();
            }
            catch (Exception e)
            {
                ret.resultcode = -1;
                ret.resulttext = e.Message;
            }
            return ret;

        }

        [WebMethod]
        public UConfirmJobResponse ConfirmJob(ULOGONINFO logon, int l_refno, String sz_jobid, bool b_confirm)
        {
            //check if jobid is correct to l_refno in LBASEND
            //check if l_status still is = 5 i sendinginfo (ready for confirmation)
            //write confirm file to LBA server
            UConfirmJobResponse ret = new UConfirmJobResponse();
            ret.l_refno = l_refno;
            ret.sz_jobid = sz_jobid;
            try
            {
                PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 60);
                if (!db.CheckLogon(ref logon))
                {
                    //logon failed
                    ret.resultcode = -1;
                    ret.resulttext = "Logon failed";

                }
                else
                {
                    if (!db.VerifyJobIDAndRefno(l_refno, sz_jobid))
                    {
                        ret.resultcode = -1;
                        ret.resulttext = "Could not verify refno and jobid";
                    }
                    else
                    {
                        bool b_simulation = db.GetIsSimulation(l_refno);
                        USimpleXmlWriter xmlwriter = new USimpleXmlWriter("iso-8859-1");
                        xmlwriter.insertStartDocument();
                        xmlwriter.insertStartElement("LBA");
                        xmlwriter.insertAttribute("operation", (b_confirm ? "ConfirmJob" : "CancelJob"));
                        xmlwriter.insertAttribute("l_refno", l_refno.ToString());
                        xmlwriter.insertAttribute("sz_jobid", sz_jobid);
                        xmlwriter.insertAttribute("f_simulation", (b_simulation ? "1" : "0"));
                        xmlwriter.insertAttribute("l_deptpk", logon.l_deptpk.ToString());
                        xmlwriter.insertAttribute("l_comppk", logon.l_comppk.ToString());
                        xmlwriter.insertAttribute("l_userpk", logon.l_userpk.ToString());
                        xmlwriter.insertAttribute("sz_compid", logon.sz_compid);
                        xmlwriter.insertAttribute("sz_deptid", logon.sz_deptid);
                        xmlwriter.insertAttribute("sz_userid", logon.sz_userid);


                        //select operators to be included. Only include operators with status=310 (awaiting confirmation)
                        xmlwriter.insertStartElement("operators");

                        List<ULBASENDING> operators = db.GetLBAOperatorsReadyForConfirmCancel(l_refno);
                        for (int i = 0; i < operators.Count; i++)
                        {
                            xmlwriter.insertStartElement("operator");
                            xmlwriter.insertAttribute("id", operators[i].l_operator.ToString());
                            xmlwriter.insertAttribute("sz_jobid", operators[i].sz_jobid.ToString());
                        }

                        xmlwriter.insertEndElement(); //operators

                        xmlwriter.insertEndElement(); //end LBA
                        xmlwriter.insertEndDocument();
                        xmlwriter.finalize();
                        ConfirmLBAWriter lba = new ConfirmLBAWriter(l_refno, sz_jobid);
                        lba.writeline(xmlwriter.getXml());
                        lba.close();

                        db.SetLBAStatus(l_refno, (b_confirm ? 320 : 330), "310, 311", -1); //320 = Confirmed by user, 330 = cancelled by user, but only if status is 310
                        if (!lba.publish())
                        {
                            ret.resultcode = -1;
                            ret.resulttext = "Failed to publish LBA Confirmation file";
                            throw new ULBACouldNotPublishConfirmCancelFile();
                        }
                        else
                        {
                            ret.resultcode = (b_confirm ? 0 : 1000);
                            ret.resulttext = (b_confirm ? "CONFIRMED" : "CANCELLED");
                        }

                    }
                }
                db.close();
            }
            catch (Exception e)
            {
                //ret.resultcode = -1;
                //ret.resulttext = e.Message;
                throw e;
            }
            return ret;

        }

        /*[WebMethod]
        public XmlDocument ExecAlert(Int64 l_alertpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
        {
            try
            {
                return ExecAlert(l_alertpk, l_comppk, l_deptpk, l_userpk, sz_compid, sz_userid, sz_deptid, sz_password,
                    sz_function, sz_scheddate, sz_schedtime, null);
            }
            catch (Exception e)
            {
                throw e;
            }
        }*/

        //[WebMethod]
        public XmlDocument ExecAlert(Int64 l_alertpk, int l_comppk, int l_deptpk, Int64 l_userpk, 
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate, String jobid)
        {
            sz_function = sz_function.ToLower();
           
            ULOGONINFO logoninfo = new ULOGONINFO();
            logoninfo.l_comppk = l_comppk;
            logoninfo.l_deptpk = l_deptpk;
            logoninfo.l_userpk = l_userpk;
            logoninfo.sz_compid = sz_compid;
            logoninfo.sz_deptid = sz_deptid;
            logoninfo.sz_password = sz_password;
            logoninfo.sz_userid = sz_userid;
            logoninfo.jobid = jobid;

            USimpleXmlWriter xml = new USimpleXmlWriter("iso-8859-1");
            ParmGenerateSending parm = new ParmGenerateSending();
            xml.insertStartDocument();
            xml.insertComment("Results for ExecAlert");
            xml.insertStartElement(String.Format("results"));
            xml.insertAttribute("l_alertpk", l_alertpk.ToString());
            //xml.insertAttribute("f_simulation", f_simulation.ToString());
            xml.insertAttribute("sz_function", sz_function);
            if (parm.Initialize(ref logoninfo, ref xml))
            {
                int function = ValidateFunction(sz_function);
                if (function==-1)
                {
                    parm.setAlertInfo(false, "0", 0, l_alertpk, "", "Invalid function specified [" + sz_function + "]", "", ParmGenerateSending.SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    parm.SendAlert(l_alertpk, function, sz_scheddate, sz_schedtime, percentDelegate);
                }
            }
            cleanup(ref parm);


            xml.insertEndElement(); //alert
            xml.insertEndDocument();
            xml.finalize();
            return xml.GetXmlDocument();
        }


        //[WebMethod]
        public XmlDocument ExecEvent(Int64 l_eventpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate, String jobid)
        {
            sz_function = sz_function.ToLower();
            //_init();


            ULOGONINFO logoninfo = new ULOGONINFO();
            logoninfo.l_comppk = l_comppk;
            logoninfo.l_deptpk = l_deptpk;
            logoninfo.l_userpk = l_userpk;
            logoninfo.sz_compid = sz_compid;
            logoninfo.sz_deptid = sz_deptid;
            logoninfo.sz_password = sz_password;
            logoninfo.sz_userid = sz_userid;
            logoninfo.jobid = jobid;

            USimpleXmlWriter xml = new USimpleXmlWriter("iso-8859-1");
            xml.insertStartDocument();
            xml.insertComment("Results for ExecEvent");
            xml.insertStartElement(String.Format("results"));
            xml.insertAttribute("l_eventpk", l_eventpk.ToString());
            //xml.insertAttribute("f_simulation", f_simulation.ToString());
            xml.insertAttribute("sz_function", sz_function);

            
            ParmGenerateSending parm = new ParmGenerateSending();
            if (parm.Initialize(ref logoninfo, ref xml))
            {
                int function = ValidateFunction(sz_function);
                if (function == -1)
                {
                    parm.setAlertInfo(false, "0", 0, 0, "", "Invalid function specified [" + sz_function + "]", "", ParmGenerateSending.SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    parm.SendEvent(l_eventpk, function, sz_scheddate, sz_schedtime, null);
                }
            }
            cleanup(ref parm);
            xml.insertEndElement(); //event
            xml.insertEndDocument();
            xml.finalize();
            return xml.GetXmlDocument();
        }

        public void cleanup(ref ParmGenerateSending parm)
        {
            try
            {
                parm.DeInitialize();
            }
            catch (Exception e)
            {
                parm.setAlertInfo(false, "0", 0, 0, "", "Error closing Database resources", e.Message, ParmGenerateSending.SYSLOG.ALERTINFO_SYSLOG_WARNING);
            }
        }
    }

}
