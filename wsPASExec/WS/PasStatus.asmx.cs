﻿using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;
using com.ums.UmsCommon;
using com.ums.PAS.Status;
using com.ums.UmsCommon.CoorConvert;
using System.Xml;
using System.Collections.Generic;
using com.ums.PAS.CB;
using com.ums.PAS.Database;
using com.ums.UmsParm;
using System.Xml.Serialization;

namespace com.ums.ws.pas.status
{
    /// <summary>
    /// Summary description for PasStatus
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/pas/status")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]

    public class PasStatus : System.Web.Services.WebService
    {
        [XmlInclude(typeof(UPolygon))]
        [XmlInclude(typeof(UEllipse))]
        [XmlInclude(typeof(UBoundingRect))]

        [WebMethod]
        public List<USMSINSTATS> GetSmsStats(ULOGONINFO logoninfo, long n_refno)
        {
            List<USMSINSTATS> ret = new List<USMSINSTATS>();
            return ret;
        }



        /*
         * Retrieve statuslist
         */
        [WebMethod]
        public UStatusListResults GetStatusList(ULOGONINFO logoninfo)
        {
            try
            {
                UStatusListSearch sl = new UStatusListSearch(ref logoninfo);

                return (UStatusListResults)sl.Find();
            }
            catch (USessionExpiredException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        [WebMethod]
        public CB_PROJECT_STATUS_RESPONSE GetCBStatus(CB_PROJECT_STATUS_REQUEST req)
        {
            try
            {
                UStatusItemsDb s = new UStatusItemsDb();
                s.CheckLogon(ref req.logon, true);
                return s.GetStatusItems(ref req);
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        [WebMethod]
        public byte[] GetStatusItems(ULOGONINFO logoninfo, UStatusItemSearchParams search)
        {
            UStatusItemSearch itemsearch = new UStatusItemSearch(ref logoninfo, ref search);
            return itemsearch.Find();
            //generate xml document containing all statusinfo based on search parameters
            //zip it and return

        }

        [WebMethod]
        public byte[] GetStatusItemsTest()
        {
            /*ULOGONINFO logoninfo = new ULOGONINFO();
            logoninfo.l_comppk = 2;
            logoninfo.l_deptpk = 1;
            logoninfo.l_userpk = 1;
            logoninfo.sz_userid = "MH";
            logoninfo.sz_compid = "UMS";
            logoninfo.sz_password = "mh123,1æøå";
            long[] refnos = new long[1];
            refnos[0] = 91721;
            UStatusItemSearchParams search = new UStatusItemSearchParams(1000000000000938, 0, 0, 0, refnos);

            UStatusItemSearch itemsearch = new UStatusItemSearch(ref logoninfo, ref search);
            return itemsearch.Find();
            */
            return null;

        }

        [WebMethod]
        public List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> GetAllMesagesThisMonth(ULOGONINFO logoninfo, long period)
        {
            UStatusItemsDb s = new UStatusItemsDb();
            s.CheckLogon(ref logoninfo, true);
            return s.GetMonthlyMessageReport(period);
        }
        
        [WebMethod]
        public List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> GetOperatorPerformanceThisMonth(ULOGONINFO logoninfo, long period)
        {
            UStatusItemsDb s = new UStatusItemsDb();
            s.CheckLogon(ref logoninfo, true);
            return s.GetOperatorPerformanceThisMonth(period);
        }

        /*[WebMethod]
        public String TestCoor()
        {
            double northing = 0, easting = 0;
            string zone = "33V";
            new UTM().LL2UTM(23, 62.319492, 6.913175, 33, ref northing, ref easting, ref zone);
            String test = northing + " " + easting + " " + zone;
            return test;
        }*/
    }
}
