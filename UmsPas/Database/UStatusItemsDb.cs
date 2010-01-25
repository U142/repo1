using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.Collections;
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
            : base(conn.sz_dsn, conn.sz_uid, conn.sz_pwd, 180)
        {

        }

        public UNonFinalStatusCodes GetNotFinalStatusCodes(ref UStatusItemSearchParams p)
        {
            UNonFinalStatusCodes ret = new UNonFinalStatusCodes();
            ret.n_parsing = 0;
            ret.n_queue = 0;
            ret.n_sending = 0;
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
                            ret.n_parsing += rs.GetInt32(1);
                            break;
                        case -1001:
                            ret.n_queue += rs.GetInt32(1);
                            break;
                        case -1002:
                            ret.n_sending += rs.GetInt32(1);
                            break;
                    }
                }
                rs.Close();
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
                String szSQL = String.Format("sp_bbgetstatuscodes {0}", p._l_projectpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UStatusCode code = new UStatusCode();
                    code.n_status = rs.GetInt32(0);
                    code.sz_status = rs.GetString(1);
                    code.b_userdef_text = (rs.GetString(2).Equals("__notuserdefined__") ? false : true);
                    ret.Add(code);
                }
                rs.Close();
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

        public List<ULBASENDING> GetLBASending_2_0(long n_refno)
        {
            List<ULBASENDING> ret = new List<ULBASENDING>();
            Hashtable operatorlink = new Hashtable();

            
            try
            {
                String szSQL = String.Format("SELECT LS.l_status, LS.l_response, LS.l_items, LS.l_proc, LS.l_retries, LS.l_requesttype, LS.f_simulate, LS.sz_jobid, LS.sz_areaid, isnull(LS.l_operator, -1), isnull(LOP.sz_operatorname, 'Unknown Operator') " +
                                            "FROM LBASEND LS, LBAOPERATORS LOP WHERE LS.l_refno={0} AND LS.l_operator*=LOP.l_operator", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                int n_index = 0;
                while (rs.Read())
                {
                    ULBASENDING sending = new ULBASENDING();
                    sending.l_refno = n_refno;
                    sending.l_status = rs.GetInt32(0);
                    sending.l_response = rs.GetInt32(1);
                    sending.l_items = rs.GetInt32(2);
                    sending.l_proc = rs.GetInt32(3);
                    sending.l_retries = rs.GetInt32(4);
                    sending.l_requesttype = rs.GetInt32(5);
                    sending.f_simulation = rs.GetInt32(6);
                    sending.sz_jobid = rs.GetString(7);
                    sending.sz_areaid = rs.GetString(8);
                    sending.l_operator = rs.GetInt32(9);
                    sending.sz_operator = rs.GetString(10);
                    operatorlink.Add(sending.l_operator, n_index); //add operator as key and an index to the List as value
                    ret.Add(sending);
                    n_index++;
                }
                if (ret.Count <= 0)
                {
                    rs.Close();
                    return null;
                }
                rs.Close();
                szSQL = String.Format("SELECT l_cc, l_delivered, l_expired, l_failed, l_unknown, l_submitted, l_queued, l_subscribers, isnull(l_operator, -1) " +
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
                    cc.l_operator = rs.GetInt32(8);
                    if (cc.l_operator > 0)
                    {
                        ULBASENDING lba = ret[(Int32)operatorlink[cc.l_operator]];
                        lba.histcc.Add(cc);
                    }
                    //ret.histcc.Add(cc);
                }
                rs.Close();

                szSQL = String.Format("SELECT l_status, isnull(l_ts, 0), isnull(l_operator, -1) FROM LBASEND_TS WHERE l_refno={0} ORDER BY l_operator, l_ts,l_status", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    ULBASEND_TS ts = new ULBASEND_TS();
                    try
                    {
                        ts.l_status = rs.GetInt32(0);
                        ts.l_ts = rs.GetInt64(1);
                        ts.l_operator = rs.GetInt32(2);
                        if (ts.l_operator > 0)
                        {
                            ULBASENDING lba = ret[(Int32)operatorlink[ts.l_operator]];
                            lba.send_ts.Add(ts);
                        }
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

        public List<LBALanguage> GetLBATextContent(long n_refno)
        {
            try
            {
                List<LBALanguage> ret = new List<LBALanguage>();
                String szSQL = String.Format("sp_pas_get_lbatext {0}", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                long n_prev_textpk = -1;
                LBALanguage lang = null;
                while(rs.Read())
                {
                    long textpk = rs.GetInt64(0);
                    if (n_prev_textpk != textpk)
                    {
                        String sz_name = rs.GetString(1);
                        String sz_oadc = rs.GetString(2);
                        String sz_text = rs.GetString(3);
                        lang = new LBALanguage();
                        lang.sz_name = sz_name;
                        lang.sz_cb_oadc = sz_oadc;
                        lang.sz_text = sz_text;
                        ret.Add(lang);
                    }
                    int l_cc = rs.GetInt16(4);
                    lang.AddCCode(l_cc.ToString());
                }
                rs.Close();
                return ret;
            }
            catch(Exception e)
            {
                return new List<LBALanguage>();
            }
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
                else
                {
                    rs.Close();
                    return null;
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
            string [] p = str.Split('|');
            if (p.Length >= 1)
                sz_adrname = p[0];
            else
                sz_adrname = "N/A";
            if (p.Length >= 2)
                sz_postaddr = p[1];
            else
                sz_postaddr = "N/A";
            if (p.Length >= 3)
                sz_postno = p[2];
            else
                sz_postno = "N/A";
            if (p.Length >= 4)
                sz_postarea = p[3];
            else
                sz_postarea = "N/A";

            /*int s = 0;
            int e = str.IndexOf("|");
            sz_adrname = str.Substring(s, e);
            s = e + 1;
            e = str.IndexOf("|", s);
            sz_postaddr = str.Substring(s, e-s);
            s = e + 1;
            e = str.IndexOf("|", s);
            sz_postno = str.Substring(s, e-s);
            s = e + 1;
            sz_postarea = str.Substring(s);*/
        }

    }
}
