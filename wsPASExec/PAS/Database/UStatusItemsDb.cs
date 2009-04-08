using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Collections.Generic;

using System.Data.Odbc;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.PAS.Status;

namespace com.ums.PAS.Database
{
    public class UStatusItemsDb : PASUmsDb
    {
        public UStatusItemsDb(UmsDbConnParams conn) 
            : base(conn.sz_dsn, conn.sz_uid, conn.sz_pwd)
        {

        }

        public UNonFinalStatusCodes GetNotFinalStatusCodes(ref UStatusItemSearchParams p)
        {
            UNonFinalStatusCodes ret = new UNonFinalStatusCodes();
            try
            {
                String szSQL = String.Format("sp_bbcountforsend {0}, -1", p._l_projectpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    int code = rs.GetInt32(0);
                    switch (code)
                    {
                        case -1000:
                            ret.n_parsing = rs.GetInt32(1);
                            break;
                        case -1001:
                            ret.n_queue = rs.GetInt32(1);
                            break;
                        case -1002:
                            ret.n_sending = rs.GetInt32(1);
                            break;
                    }
                }
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public List<UStatusCode> GetStatusCodes(ref UStatusItemSearchParams p)
        {
            List<UStatusCode> ret = new List<UStatusCode>();
            try
            {
                String szSQL = String.Format("SELECT hist.l_status, isnull(apst.sz_name, codes.sz_status) sz_status, isnull(apst.sz_name, '__notuserdefined__') userdef " +
                                "FROM BBHIST hist (INDEX hist_idx), BBSTATUSCODES codes, BBACTIONPROFILESEND ap, BBACTIONPROFILESSTATUS apst, BBPROJECT_X_REFNO pxr " +
                                "WHERE pxr.l_projectpk={0} AND hist.l_refno=pxr.l_refno AND hist.l_status=codes.l_status " +
                                "AND hist.l_refno*=ap.l_refno AND ap.l_actionprofilepk*=apst.l_profilepk " +
                                "AND hist.l_status*=apst.l_status " +
                                "GROUP BY hist.l_status, codes.sz_status, apst.sz_name " +
                                "ORDER BY userdef, l_status DESC", p._l_projectpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UStatusCode code = new UStatusCode();
                    code.n_status = rs.GetInt32(0);
                    code.sz_status = rs.GetString(1);
                    code.b_userdef_text = (rs.GetString(2).Equals("__notuserdefined__") ? false : true);
                    ret.Add(code);
                }
            }
            catch (Exception e)
            {
                throw e;
            }
            UStatusCode c = new UStatusCode();
            c.n_status = 8000;
            c.sz_status = "SMS Sent OK";
            c.b_userdef_text = true;
            ret.Add(c);
            c = new UStatusCode();
            c.n_status = 8001;
            c.sz_status = "SMS retry";
            c.b_userdef_text = true;
            ret.Add(c);
            c = new UStatusCode();
            c.n_status = 8002;
            c.sz_status = "SMS Failed";
            c.b_userdef_text = true;
            ret.Add(c);

            return ret;
        }

        public ULBASENDING GetLBASending(long n_refno)
        {
            ULBASENDING ret = new ULBASENDING();
            try
            {
                String szSQL = String.Format("SELECT l_status, l_response, l_items, l_proc, l_retries, l_requesttype, f_simulate, sz_jobid, sz_areaid " +
                                            "FROM LBASEND WHERE l_refno={0}", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    ret.l_refno = n_refno;
                    ret.l_status = rs.GetInt32(0);
                    ret.l_response = rs.GetInt32(1);
                    ret.l_items = rs.GetInt32(2);
                    ret.l_proc = rs.GetInt32(3);
                    ret.l_retries = rs.GetInt32(4);
                    ret.l_requesttype = rs.GetInt32(5);
                    ret.f_simulation = rs.GetInt32(6);
                    ret.sz_jobid = rs.GetString(7);
                    ret.sz_areaid = rs.GetString(8);
                }
                rs.Close();
                szSQL = String.Format("SELECT l_cc, l_delivered, l_expired, l_failed, l_unknown, l_submitted, l_queued, l_subscribers " +
                                    "FROM LBAHISTCC WHERE l_refno={0}", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    ULBAHISTCC cc = new ULBAHISTCC();
                    cc.l_cc = rs.GetInt32(0);
                    cc.l_delivered = rs.GetInt32(1);
                    cc.l_expired = rs.GetInt32(2);
                    cc.l_failed = rs.GetInt32(3);
                    cc.l_unknown = rs.GetInt32(4);
                    cc.l_submitted = rs.GetInt32(5);
                    cc.l_queued = rs.GetInt32(6);
                    cc.l_subscribers = rs.GetInt32(7);
                    ret.histcc.Add(cc);
                }
                rs.Close();

                szSQL = String.Format("SELECT l_status, l_ts FROM LBASEND_TS WHERE l_refno={0} ORDER BY l_ts,l_status", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    ULBASEND_TS ts = new ULBASEND_TS();
                    try
                    {
                        ts.l_status = rs.GetInt32(0);
                        ts.l_ts = rs.GetInt64(1);
                        ret.send_ts.Add(ts);
                    }
                    catch (Exception)
                    {
                    }
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }

            return ret;
        }

        public List<UStatusItem> GetStatusItems(ref UStatusItemSearchParams p)
        {
            List<UStatusItem> ret = new List<UStatusItem>();
            try
            {
                OdbcDataReader rs = ExecReader(String.Format("sp_bbhistitems {0}, {1}, {2}, {3}", p._l_projectpk, "-1", p._l_date_filter, p._l_time_filter), UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UStatusItem item = new UStatusItem();
                    item.n_item = rs.GetInt32(0);
                    item.n_adrpk = rs.GetInt64(1);
                    item.f_lon = rs.GetFloat(2);
                    item.f_lat = rs.GetFloat(3);
                    item.setAdrInfo(rs.GetString(4));
                    item.n_date = rs.GetInt32(5);
                    item.n_time = rs.GetInt32(6);
                    item.n_status = rs.GetInt32(7);
                    item.sz_number = rs.GetString(8);
                    item.n_tries = rs.GetInt32(9);
                    item.n_channel = rs.GetInt32(10);
                    item.n_pcid = rs.GetInt32(11);
                    item.n_seconds = rs.GetInt32(12);
                    item.n_changedate = rs.GetInt32(13);
                    item.n_changetime = rs.GetInt32(14);
                    item.n_refno = rs.GetInt32(15);
                    item.n_ldate = rs.GetInt32(16);
                    item.n_ltime = rs.GetInt32(17);
                    ret.Add(item);
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return ret;
        }
    }


    public class UNonFinalStatusCodes
    {
        public int n_parsing;
        public int n_queue;
        public int n_sending;
    }
    public class UStatusCode
    {
        public int n_status;
        public String sz_status;
        public bool b_userdef_text;
    }


    public class UStatusItem
    {
        public int n_refno;
        public int n_item;
        public long n_adrpk;
        public float f_lon;
        public float f_lat;
        private String sz_adrinfo;
        public String sz_adrname;
        public String sz_postaddr;
        public String sz_postno;
        public String sz_postarea;
        public int n_date;
        public int n_time;
        public int n_status;
        public String sz_number;
        public int n_tries;
        public int n_channel;
        public int n_pcid;
        public int n_seconds;
        public int n_changedate;
        public int n_changetime;
        public int n_ldate;
        public int n_ltime;
        public void setAdrInfo(String str)
        {
            sz_adrinfo = str;
            int s = 0;
            int e = str.IndexOf("|");
            sz_adrname = str.Substring(s, e);
            s = e + 1;
            e = str.IndexOf("|", s);
            sz_postaddr = str.Substring(s, e-s);
            s = e + 1;
            e = str.IndexOf("|", s);
            sz_postno = str.Substring(s, e-s);
            s = e + 1;
            sz_postarea = str.Substring(s);
        }

    }
}
