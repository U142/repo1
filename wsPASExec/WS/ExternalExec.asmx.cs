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
using com.ums.PAS.CB;
using System.Xml;
using com.ums.UmsCommon.Audio;
using System.Collections.Generic;
using System.Xml.Serialization;
using System.Text;

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

        [XmlInclude(typeof(CB_ALERT_KILL))]
        [XmlInclude(typeof(CB_ALERT_PLMN))]
        [XmlInclude(typeof(CB_ALERT_POLYGON))]
        [XmlInclude(typeof(CB_ALERT_UPDATE))]
        [XmlInclude(typeof(ULBAOPERATORSTATE))]
        [XmlInclude(typeof(ULBASTATUSCODES))]


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

        /*[WebMethod]
        public String testPolymatch()
        {
            UPolygon poly = new UPolygon();
            poly.addPoint(0, 0);
            poly.addPoint(0, 10);
            poly.addPoint(10, 0);
            UPolygon poly2 = new UPolygon();
            poly2.addPoint(0, 0);
            poly2.addPoint(0, -10);
            poly2.addPoint(-10, 0);

            List<UPolygon> arr = new List<UPolygon>();
            arr.Add(poly2);
            poly.findPolysWithSharedBorder(ref arr);

            return "OK";
        }*/

        [WebMethod]
        public PAEVENT[] GetEventList(com.ums.UmsCommon.ULOGONINFO logoninfo)
        {
            try
            {
                PASUmsDb db = new PASUmsDb();
                try
                {
                    if (db.CheckLogonLiteral(ref logoninfo, true))
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
            else if (s.Equals("silent"))
                return UCommon.USENDING_LIVE_SILENT;
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
            response.sz_function = "test";
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

        /*[WebMethod]
        public XmlDocument ExecCBSending(ULOGONINFO logon, CB_ALERT_POLYGON sending)
        {
            XmlDocument doc = new XmlDocument();
            PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);
            db.Send(ref logon, ref sending);

            return doc;
        }*/

        /*[WebMethod]
        public bool _cb_export(CB_ALERT_KILL kill
                                ,CB_ALERT_PLMN plmn
                                ,CB_ALERT_POLYGON poly
                                ,CB_ALERT_UPDATE update)
        {
            return true;
        }*/



        /*[WebMethod]
        public CB_SENDING_RESPONSE testCBOperation()
        {
            try
            {
                ULOGONINFO logon = new ULOGONINFO();
                logon.l_userpk = 1;
                logon.l_comppk = 1;
                logon.l_deptpk = 1;
                CB_ALERT_POLYGON cb = new CB_ALERT_POLYGON();
                cb.shape = new UPolygon();
                cb.shape.addPoint(50, 10);
                cb.shape.addPoint(60, 11);
                cb.shape.addPoint(55, 10.5);
                cb.l_projectpk = 0;
                cb.sz_projectname = "Morten tester";
                cb.l_sched_utc = 20100719154600;
                cb.l_validity = 7;
                cb.originator = new CB_ORIGINATOR();
                cb.reaction = new CB_REACTION();
                cb.risk = new CB_RISK();
                cb.sz_name = "test";
                cb.sz_sender = "UMS";
                CB_MESSAGE msg = new CB_MESSAGE();
                msg.l_cbchannel = 919;
                msg.sz_text = "Extreme weersomstandigheden waarschuwing in dit gebied tot 4:15pm";
                cb.textmessages.list.Add(msg);
                CB_OPERATION_BASE send = (CB_OPERATION_BASE)cb;
                return new CBGenerateSending(ref logon, ref send).Send();
            }
            catch (Exception e)
            {
                throw e;
            }
        }*/

        /*[WebMethod]
        public bool tmpRestrictionShape()
        {
            CB_ALERT_POLYGON p = CB_ALERT_POLYGON.Deserialize("S:\\UMS\\var\\CB\\eat\\CB_NewAlertPolygon_395_100514.xml", Encoding.UTF8);
            String xml = "", md5 = "";
            p.shape.CreateXml(ref xml, ref md5);
            int n_deptpk = 4;
            UmsDb db = new UmsDb();
            String szSQL = String.Format("UPDATE PASHAPE SET sz_xml='{0}', sz_md5='{1}' WHERE l_pk={2} AND l_type=16",
                                xml, md5, n_deptpk);
            db.ExecNonQuery(szSQL);
            
            return true;
        }*/

        /**
         * Execute a CB-operation (send poly, send plnm, update, kill)
         * Fill operation-base from logon-info
         * Get a refno
         * Insert into DB
         * Serialize CB_OPERATION
         */
        [WebMethod]
        public CB_SENDING_RESPONSE ExecCBOperation(ULOGONINFO logon, CB_OPERATION_BASE cb)
        {
            try
            {
                PASUmsDb db = new PASUmsDb();
                db.CheckLogon(ref logon, true);
                db.close();

                //SEND BASE MAY BE Send Poly, Send nationwide or Update sending
                if (typeof(CB_SEND_BASE).Equals(cb.GetType().BaseType))
                {
                    return new CBGenerateSending(ref logon, ref cb).Send();
                }
                //Kill sending
                else if(typeof(CB_ALERT_KILL).Equals(cb.GetType()))
                {
                    return new CBGenerateSending(ref logon, ref cb).Send();
                }
                throw new Exception("Sending definition not found for " + cb.GetType());
            }
            catch (Exception e)
            {
                throw e;
            }
        }


        /*
         * Common function for ExecPolygonSending, ExecEllipseSending and ExecGixSending
         */
        protected XmlDocument ExecMapSending(UMAPSENDING sending)
        {
            try
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
            catch (Exception)
            {
                throw;
            }

        }

        [WebMethod]
        public ExecResponse ExecAlertV2(Int64 l_alertpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecAlert(l_alertpk, l_comppk, l_deptpk, l_userpk, sz_compid, sz_userid,
                                        sz_deptid, sz_password, sz_function, sz_scheddate, sz_schedtime, null, "", "");
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
                    logon.sz_userid, logon.sz_deptid, logon.sz_password, sz_function, sz_scheddate, sz_schedtime, percentdelegate, logon.jobid, logon.sessionid);
                response.parseFromXml(ref doc, "l_alertpk");
                return response;
            }
            catch (Exception)
            {
                throw;
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
            try
            {
                ExecResponse response = new ExecResponse();
                XmlDocument doc = ExecEvent(l_eventpk, l_comppk, l_deptpk, l_userpk,
                                            sz_compid, sz_userid, sz_deptid, sz_password,
                                            sz_function, sz_scheddate, sz_schedtime, null, "");
                response.parseFromXml(ref doc, "l_eventpk");

                return response;
            }
            catch (Exception)
            {
                throw;
            }
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
                if (!db.CheckLogon(ref logon, true))
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
                                int n_simulation = db.GetIsSimulation(l_refno);
                                USimpleXmlWriter xmlwriter = new USimpleXmlWriter("iso-8859-1");
                                xmlwriter.insertStartDocument();
                                xmlwriter.insertStartElement("LBA");
                                xmlwriter.insertAttribute("operation", (b_confirm ? "ConfirmJob" : "CancelJob"));
                                xmlwriter.insertAttribute("l_refno", l_refno.ToString());
                                xmlwriter.insertAttribute("sz_jobid", sz_jobid);
                                xmlwriter.insertAttribute("f_simulation", n_simulation.ToString());
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
                if (!db.CheckLogon(ref logon, true))
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
                        int n_simulation = db.GetIsSimulation(l_refno);
                        USimpleXmlWriter xmlwriter = new USimpleXmlWriter("iso-8859-1");
                        xmlwriter.insertStartDocument();
                        xmlwriter.insertStartElement("LBA");
                        xmlwriter.insertAttribute("operation", (b_confirm ? "ConfirmJob" : "CancelJob"));
                        xmlwriter.insertAttribute("l_refno", l_refno.ToString());
                        xmlwriter.insertAttribute("sz_jobid", sz_jobid);
                        xmlwriter.insertAttribute("f_simulation", n_simulation.ToString());//(b_simulation ? "1" : "0"));
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
                                String sz_function, String sz_scheddate, String sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate, String jobid, String sessionid)
        {
            try
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
                logoninfo.sessionid = sessionid;

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
                    if (function == -1)
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
            catch (Exception)
            {
                throw;
            }
        }


        //[WebMethod]
        public XmlDocument ExecEvent(Int64 l_eventpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate, String jobid)
        {
            try
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
            catch (Exception)
            {
                throw;
            }
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

        [WebMethod]
        public CB_MESSAGE_FIELDS getCBSendingFields(ULOGONINFO logon)
        {
            try
            {
                UmsDb db = new UmsDb();
                CB_MESSAGE_FIELDS ret = new CB_MESSAGE_FIELDS();
                ret.l_db_timestamp = db.getDbClock();
                List<CB_ORIGINATOR> originators = new List<CB_ORIGINATOR>();
                List<CB_RISK> risk = new List<CB_RISK>();
                List<CB_REACTION> reaction = new List<CB_REACTION>();

                originators.Add(new CB_ORIGINATOR(1, "Afz. Burg. Den Oudsten"));
                originators.Add(new CB_ORIGINATOR(2, "Afz. Burg. Wolfsen"));
                originators.Add(new CB_ORIGINATOR(3, "Afz. Burg. Krikke"));

                risk.Add(new CB_RISK(4, "Explosiegevaar"));
                risk.Add(new CB_RISK(5, "Gezondheidsklachten"));
                risk.Add(new CB_RISK(6, "Ernstige Wateroverlast"));

                reaction.Add(new CB_REACTION(7, "Verlaat de wijk"));
                reaction.Add(new CB_REACTION(8, "Blijf binnen"));
                reaction.Add(new CB_REACTION(9, "Verlaat het gebied"));


                ret.originator_list = originators;
                ret.risk_list = risk;
                ret.reaction_list = reaction;

                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

    }


}
