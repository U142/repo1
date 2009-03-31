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
using System.Xml;
using com.ums.UmsCommon.Audio;


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
                                        sz_deptid, sz_password, sz_function, sz_scheddate, sz_schedtime);
            response.parseFromXml(ref doc, "l_alertpk");

            return response;
        }

        [WebMethod]
        public ExecResponse ExecEventV2(Int64 l_eventpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
        {
            ExecResponse response = new ExecResponse();
            XmlDocument doc = ExecEvent(l_eventpk, l_comppk, l_deptpk, l_userpk,
                                        sz_compid, sz_userid, sz_deptid, sz_password,
                                        sz_function, sz_scheddate, sz_schedtime);
            response.parseFromXml(ref doc, "l_eventpk");

            return response;
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
                PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
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
                        xmlwriter.insertEndElement();
                        xmlwriter.insertEndDocument();
                        xmlwriter.finalize();
                        ConfirmLBAWriter lba = new ConfirmLBAWriter(l_refno, sz_jobid);
                        lba.writeline(xmlwriter.getXml());
                        lba.close();

                        db.SetLBAStatus(l_refno, (b_confirm ? 320 : 330), 310); //320 = Confirmed by user, 330 = cancelled by user, but only if status is 310
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
        public XmlDocument ExecAlert(Int64 l_alertpk, int l_comppk, int l_deptpk, Int64 l_userpk, 
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
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
                    parm.SendAlert(l_alertpk, function, sz_scheddate, sz_schedtime);
                }
            }
            cleanup(ref parm);


            xml.insertEndElement(); //alert
            xml.insertEndDocument();
            xml.finalize();
            return xml.GetXmlDocument();
        }


        [WebMethod]
        public XmlDocument ExecEvent(Int64 l_eventpk, int l_comppk, int l_deptpk, Int64 l_userpk,
                                String sz_compid, String sz_userid, String sz_deptid, String sz_password,
                                String sz_function, String sz_scheddate, String sz_schedtime)
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
                    parm.SendEvent(l_eventpk, function, sz_scheddate, sz_schedtime);
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
