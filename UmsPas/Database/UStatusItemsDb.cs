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
using com.ums.PAS.CB;

namespace com.ums.PAS.Database
{
    public class UStatusItemsDb : PASUmsDb
    {
        public UStatusItemsDb(UmsDbConnParams conn) 
            : base(conn.sz_dsn, conn.sz_uid, conn.sz_pwd, 180)
        {

        }
        public UStatusItemsDb()
            : base(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120)
        {

        }


        public UNonFinalStatusCodes GetNotFinalStatusCodes(ref UStatusItemSearchParams p)
        {
            UNonFinalStatusCodes ret = new UNonFinalStatusCodes();
            ret.n_parsing = 0;
            ret.n_queue = 0;
            ret.n_sending = 0;
            OdbcDataReader rs = null;

            try
            {
                String szSQL = String.Format("sp_bbcountforsend {0}, -1", p._l_projectpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }

        public List<UStatusCode> GetStatusCodes(ref UStatusItemSearchParams p)
        {
            OdbcDataReader rs = null;
            List<UStatusCode> ret = new List<UStatusCode>();
            try
            {
                String szSQL = String.Format("sp_bbgetstatuscodes {0}", p._l_projectpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
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
            OdbcDataReader rs = null;

            try
            {
                //String szSQL = String.Format("SELECT LS.l_status, LS.l_response, LS.l_items, LS.l_proc, LS.l_retries, LS.l_requesttype, LS.f_simulate, LS.sz_jobid, LS.sz_areaid, isnull(LS.l_operator, -1), isnull(LOP.sz_operatorname, 'Unknown Operator') " +
                //                            "FROM LBASEND LS, LBAOPERATORS LOP WHERE LS.l_refno={0} AND LS.l_operator*=LOP.l_operator", n_refno);
                String szSQL = String.Format("sp_pas_status_lbasend {0}", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
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
                    sending.l_type = rs.GetInt32(11);
                    sending.l_created_ts = rs.GetInt64(12);
                    sending.l_started_ts = rs.GetInt64(13);
                    sending.l_expires_ts = rs.GetInt64(14);
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
                //szSQL = String.Format("SELECT l_cc, l_delivered, l_expired, l_failed, l_unknown, l_submitted, l_queued, l_subscribers, isnull(l_operator, -1) " +
                //                    "FROM LBAHISTCC WHERE l_refno={0}", n_refno);
                szSQL = String.Format("sp_pas_status_lbahistcc {0}", n_refno);
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

                //szSQL = String.Format("SELECT l_status, isnull(l_ts, 0), isnull(l_operator, -1) FROM LBASEND_TS WHERE l_refno={0} ORDER BY l_operator, l_ts,l_status", n_refno);
                szSQL = String.Format("sp_pas_status_lbasend_ts {0}", n_refno);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

            return ret;
        }

        public List<USMSINSTATS> GetSmsInStats(long n_refno)
        {
            List<USMSINSTATS> ret = new List<USMSINSTATS>();
            String szSQL = String.Format("sp_pas_get_smsinstats {0}", n_refno);
            OdbcDataReader rs = null;
            try
            {
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    USMSINSTATS st = new USMSINSTATS();
                    try
                    {
                        st.l_refno = rs.GetInt32(0);
                        st.l_answercode = rs.GetInt32(1);
                        st.sz_description = rs.GetString(2);
                        st.l_count = rs.GetInt32(3);
                        ret.Add(st);
                    }
                    catch (Exception e)
                    {
                    }
                }
                rs.Close();
                return ret;
            }
            catch (Exception e2)
            {
                return ret;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }

        public List<LBALanguage> GetLBATextContent(long n_refno)
        {
            OdbcDataReader rs = null;

            try
            {
                List<LBALanguage> ret = new List<LBALanguage>();
                String szSQL = String.Format("sp_pas_get_lbatext {0}", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                long n_prev_textpk = -1;
                LBALanguage lang = null;
                while (rs.Read())
                {
                    long textpk = rs.GetInt64(0);
                    if (n_prev_textpk != textpk)
                    {
                        long pk = rs.GetInt64(0);
                        String sz_name = rs.GetString(1);
                        String sz_oadc = rs.GetString(2);
                        String sz_text = rs.GetString(3);
                        lang = new LBALanguage();
                        lang.sz_name = sz_name;
                        lang.sz_cb_oadc = sz_oadc;
                        lang.sz_text = sz_text;
                        lang.setTextPk(pk);
                        ret.Add(lang);
                    }
                    int l_cc = rs.GetInt16(4);
                    lang.AddCCode(l_cc.ToString());
                }
                rs.Close();
                return ret;
            }
            catch (Exception e)
            {
                return new List<LBALanguage>();
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }

        public bool ShapeFromDb(ref MDVSENDINGINFO mdv, ref UShape shape)
        {
            bool ret = false;
            ExecNonQuery("set textsize 10000000");
            String szSQL = String.Format("SELECT sz_xml FROM PASHAPE WHERE l_pk={0} AND l_type={1}",
                mdv.l_refno, (long)PASHAPETYPES.PASENDING);
            OdbcDataReader rs_poly = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
            if (rs_poly.Read())
            {
                shape = UShape.Deserialize(rs_poly.GetString(0));
                if (shape != null)
                    ret = true;
            }
            else
            {
                ret = false;
            }
            rs_poly.Close();
            return ret;
        }


        public ULBASENDING GetLBASending(long n_refno)
        {
            OdbcDataReader rs = null;

            ULBASENDING ret = new ULBASENDING();
            try
            {
                String szSQL = String.Format("SELECT l_status, l_response, l_items, l_proc, l_retries, l_requesttype, f_simulate, sz_jobid, sz_areaid " +
                                            "FROM LBASEND WHERE l_refno={0}", n_refno);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

            return ret;
        }

        public CB_PROJECT_STATUS_RESPONSE GetStatusItems(ref CB_PROJECT_STATUS_REQUEST req)
        {
            CB_PROJECT_STATUS_RESPONSE response = new CB_PROJECT_STATUS_RESPONSE();
            String szSQL = "";
            OdbcDataReader rs_poly = null;
            OdbcDataReader rs_mf = null;
            OdbcDataReader sendts = null;
            OdbcDataReader histrs = null;
            OdbcDataReader rs = null;
            try
            {
                szSQL = String.Format("sp_cb_get_projectinfo {0}", req.l_projectpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);

                BBPROJECT p = new BBPROJECT();
                while (rs.Read())
                {
                    p.n_deptpk = rs.GetInt32(4);
                    p.sz_name = rs.GetString(1);

                    p.sz_created = rs.GetDecimal(2).ToString();
                    p.sz_projectpk = rs.GetDecimal(0).ToString();
                    p.sz_name = rs.GetString(1);
                    p.sz_updated = rs.GetDecimal(2).ToString();
                    Type t = rs.GetFieldType(6);
                    if (!rs.IsDBNull(6))
                        p.l_finished = rs.GetByte(6);//int.Parse(rs.GetString(6));
                    else
                        p.l_finished = 0;
                }
                rs.Close();

                szSQL = String.Format("sp_cb_get_cbstatus {0}", req.l_projectpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                long l_refno;
                CB_STATUS cbs = null;
                List<CB_STATUS> statuslist = new List<CB_STATUS>();
                ULBAHISTCELL test = new ULBAHISTCELL();

                while (rs.Read())
                {
                    l_refno = rs.GetInt32(0);

                    //create a new CBS for this refno. then add operators to it
                    if (cbs == null || cbs.l_refno != l_refno)
                    {
                        cbs = new CB_STATUS();
                        cbs.f_simulation = rs.GetInt16(2);
                        cbs.l_refno = l_refno;
                        //cbs.shape = UShape.ParseFromXml(rs.GetString()); // UShape
                        cbs.l_started_ts = rs.IsDBNull(19) ? (long)0 : (long)rs.GetDecimal(19);
                        cbs.l_created_ts = rs.IsDBNull(20) ? (long)0 : (long)rs.GetDecimal(20);
                        cbs.l_last_ts = rs.IsDBNull(20) ? (long)0 : (long)rs.GetDecimal(23);
                        cbs.sz_sendingname = rs.GetString(13);
                        cbs.l_channel = rs.GetInt16(22);
                        cbs.operators = new List<ULBASENDING>();
                        statuslist.Add(cbs);
                    }

                    //OdbcDataReader mdv = ExecReader(String.Format("sp_cb_get_mdvsendinginfo {0}", l_refno), UmsDb.UREADER_AUTOCLOSE);
                    MDVSENDINGINFO sendinginfo = new MDVSENDINGINFO();
                    sendinginfo.sz_sendingname = rs.GetString(13);
                    sendinginfo.l_scheddate = rs.GetInt32(14).ToString();
                    sendinginfo.l_schedtime = rs.GetInt32(15).ToString();
                    sendinginfo.l_createdate = rs.GetInt32(16).ToString();
                    sendinginfo.l_createtime = rs.GetInt16(17).ToString();
                    sendinginfo.l_group = rs.GetInt32(18);
                    sendinginfo.sz_messagetext = rs.GetString(21);

                    ULBASENDING cb_operator = new ULBASENDING();
                    cb_operator.histcc = null;
                    cb_operator.sz_areaid = rs.GetString(10);
                    cb_operator.sz_jobid = rs.GetString(11);
                    cb_operator.sz_operator = rs.GetString(1); // "KPN";
                    cb_operator.l_cbtype = (int)LBA_SENDINGTYPES.CELLBROADCAST;
                    cb_operator.l_items = rs.GetInt32(4);
                    cb_operator.l_operator = rs.GetInt32(12);
                    cb_operator.l_proc = rs.GetInt32(5);
                    cb_operator.l_requesttype = (int)rs.GetByte(6);
                    cb_operator.l_response = rs.GetInt32(7);
                    cb_operator.l_retries = rs.GetByte(8);
                    cb_operator.l_status = rs.GetInt32(9);
                    cb_operator.languages = null; // List<LBALanguage>

                    //add operator to current refno
                    cbs.operators.Add(cb_operator);

                    cbs.mdv = sendinginfo; // MDVSENDINGINFO                    

                    //statuslist.Add(cbs);
                }
                rs.Close();



                for (int i = 0; i < statuslist.Count; ++i)
                {
                    for (int j = 0; j < statuslist[i].operators.Count; ++j)
                    {
                        List<ULBAHISTCELL> histlist = new List<ULBAHISTCELL>();

                        szSQL = String.Format("sp_cb_get_histcell {0}, {1}", statuslist[i].l_refno, statuslist[i].operators[j].l_operator);
                        histrs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);

                        ULBAHISTCELL hc;
                        while (histrs.Read())
                        {
                            hc = new ULBAHISTCELL();
                            hc.l_operator = histrs.GetInt32(1);
                            hc.l_2gtotal = histrs.GetInt32(2);
                            hc.l_2gok = histrs.GetInt32(3);
                            hc.l_3gtotal = histrs.GetInt32(4);
                            hc.l_3gok = histrs.GetInt32(5);
                            hc.l_4gtotal = histrs.GetInt32(6);
                            hc.l_4gok = histrs.GetInt32(7);
                            hc.l_timestamp = long.Parse(histrs.GetDecimal(8).ToString());
                            try
                            {
                                hc.l_successpercentage = histrs.GetFloat(9);
                            }
                            catch (Exception ex) { hc.l_successpercentage = (float)0.0; }
                            hc.sz_operator = histrs.GetString(10);
                            histlist.Add(hc);
                        }
                        histrs.Close();
                        statuslist[i].operators[j].histcell = histlist;
                    }
                }


                for (int i = 0; i < statuslist.Count; ++i)
                {
                    for (int j = 0; j < statuslist[i].operators.Count; ++j)
                    {
                        szSQL = String.Format("sp_cb_get_lbasend_ts {0}, {1}", statuslist[i].l_refno, statuslist[i].operators[j].l_operator);
                        sendts = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                        List<ULBASEND_TS> liststs = new List<ULBASEND_TS>();
                        ULBASEND_TS lbasend;
                        while (sendts.Read())
                        {
                            lbasend = new ULBASEND_TS();
                            lbasend.l_status = sendts.GetInt32(1);
                            lbasend.l_ts = sendts.GetInt64(2);
                            lbasend.l_operator = sendts.GetInt16(3);
                            liststs.Add(lbasend);
                        }
                        sendts.Close();
                        statuslist[i].operators[j].send_ts = liststs; // List<ULBASEND_TS>
                    }
                }

                for (int i = 0; i < statuslist.Count; ++i)
                {
                    CB_STATUS cb = statuslist[i];
                    szSQL = String.Format("SELECT sz_xml FROM PASHAPE WHERE l_pk={0} AND l_type={1}",
                        cb.l_refno, (long)PASHAPETYPES.PASENDING);
                    rs_poly = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    if (rs_poly.Read())
                    {

                        UShape shape = UShape.Deserialize(rs_poly.GetString(0));
                        cb.shape = shape;
                    }
                    rs_poly.Close();
                }
                for (int i = 0; i < statuslist.Count; ++i)
                {
                    CB_STATUS cb = statuslist[i];
                    szSQL = String.Format("sp_pas_get_lbamessagefields {0}", cb.l_refno);
                    rs_mf = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rs_mf.Read())
                    {
                        int l_fieldtype = Int32.Parse(rs_mf["l_fieldtype"].ToString());
                        long l_source_pk = Int64.Parse(rs_mf["l_source_pk"].ToString());
                        String sz_text = rs_mf["sz_text"].ToString();
                        CB_MESSAGE_FIELDS_BASE value = CB_MESSAGE_FIELDS_BASE.Create(l_fieldtype, l_source_pk, sz_text);
                        if (value.GetType().Equals(typeof(CB_RISK)))
                            cb.risk = (CB_RISK)value;
                        else if (value.GetType().Equals(typeof(CB_REACTION)))
                            cb.reaction = (CB_REACTION)value;
                        else if (value.GetType().Equals(typeof(CB_ORIGINATOR)))
                            cb.originator = (CB_ORIGINATOR)value;
                        else if (value.GetType().Equals(typeof(CB_MESSAGEPART)))
                            cb.messagepart = (CB_MESSAGEPART)value;
                        else if (value.GetType().Equals(typeof(CB_SENDER)))
                            cb.sender = (CB_SENDER)value;
                        else if (value.GetType().Equals(typeof(CB_MESSAGE_CONFIRMATION)))
                            cb.messageconfirmation = (CB_MESSAGE_CONFIRMATION)value;
                    }
                    rs_mf.Close();
                }

                response.l_db_timestamp = getDbClock();//long.Parse(DateTime.Now.ToString("yyyyMMddHHmmss"));
                p.n_sendingcount = statuslist.Count;
                response.project = p;
                response.statuslist = statuslist;



            }
            catch (Exception)
            {

                throw;
            }
            finally
            {
                if (rs_mf != null && !rs_mf.IsClosed)
                    rs_mf.Close();
                
                if (rs_poly != null && !rs_poly.IsClosed)
                    rs_poly.Close();
                
                if (sendts != null && !sendts.IsClosed)
                    sendts.Close();

                if (histrs != null && !histrs.IsClosed)
                    histrs.Close();

                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return response;
        }


        public List<UStatusItem> GetStatusItems(ref UStatusItemSearchParams p)
        {
            List<UStatusItem> ret = new List<UStatusItem>();
            OdbcDataReader rs = null;
            try
            {
                rs = ExecReader(String.Format("sp_bbhistitems {0}, {1}, {2}, {3}", p._l_projectpk, "-1", p._l_date_filter, p._l_time_filter), UmsDb.UREADER_AUTOCLOSE);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return ret;
        }

        public List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> GetMonthlyMessageReport(long month)
        {
            List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> ret = new List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE>();
            OdbcDataReader rs = null;
            try
            {
                String sz_sql = String.Format("sp_cb_get_messages_month {0}, {1}", month, month + 100000000);
                rs = ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    CB_MESSAGE_MONTHLY_REPORT_RESPONSE item = new CB_MESSAGE_MONTHLY_REPORT_RESPONSE();
                    item.l_refno = (long)rs.GetDecimal(0);
                    item.sz_operatorname = rs.GetString(1);
                    item.l_simulate = rs.GetInt32(2);
                    item.l_type = rs.GetInt16(3);
                    item.l_response = rs.GetInt32(4);
                    item.l_retries = rs.GetInt32(5);
                    item.l_status = rs.GetInt32(6);
                    item.l_last_ts = (long)rs.GetDecimal(7);
                    item.sz_text = rs.GetString(8);
                    item.l_addressedcells = rs.GetInt32(9);
                    if (rs.IsDBNull(10))
                        item.l_performance = -1;
                    else
                        item.l_performance = (float)(rs.GetDouble(10));
                    if (rs.IsDBNull(11))
                        item.sz_userid = "N/A";
                    else
                        item.sz_userid = rs.GetString(11);
                    item.l_group = rs.GetInt32(12);
                    ret.Add(item);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return ret;
        }

        public List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> GetOperatorPerformanceThisMonth(long month)
        {
            OdbcDataReader rs = null;
            List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE> ret = new List<CB_MESSAGE_MONTHLY_REPORT_RESPONSE>();
            try
            {
                rs = ExecReader(String.Format("sp_cb_get_operator_performance_month {0}, {1}", month, month + 100000000), UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    CB_MESSAGE_MONTHLY_REPORT_RESPONSE item = new CB_MESSAGE_MONTHLY_REPORT_RESPONSE();
                    item.l_operator = (int)rs.GetInt32(0);
                    item.sz_operatorname = rs.GetString(1);
                    if (rs.IsDBNull(2))
                        item.l_performance = 0;
                    else
                        item.l_performance = (float)rs.GetDouble(2);
                    ret.Add(item);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
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
            string[] p = str.Split('|');
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

    public class CB_MESSAGE_MONTHLY_REPORT_RESPONSE
    {
        public String sz_usertype;
        public String sz_userid;
        public long l_refno;
        public int l_operator;
        public String sz_operatorname;
        public int l_simulate;
        public int l_type;
        public int l_response;
        public int l_retries;
        public int l_status;
        public long l_last_ts;
        public String sz_text;
        public int l_addressedcells;
        public float l_performance;
        public int l_group;
    }

}
