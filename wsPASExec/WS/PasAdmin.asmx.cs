using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using com.ums.UmsCommon;
using com.ums.UmsDbLib;
using com.ums.PAS.Database;
using com.ums.UmsParm;
using System.Data.Odbc;
using System.Xml.Serialization;
using System.Xml;
using System.Xml.Linq;

namespace com.ums.ws.pas.admin
{
    /// <summary>
    /// Summary description for PasAdmin
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/pas/admin")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class PasAdmin : System.Web.Services.WebService
    {
        [XmlInclude(typeof(UPolygon))]    

        [WebMethod]
        public DeactivateMessageResponse doDeactivateMessage(ULOGONINFO logon, long l_newspk)
        {
            DeactivateMessageResponse res;

            try
            {
                ULogon l = new ULogon();

                l.CheckLogon(ref logon, true);
                string sz_sql = String.Format("sp_cb_upd_activate_message {0}, {1}", l_newspk, 0);
                l.ExecNonQuery(sz_sql);
            }
            catch (Exception e)
            {
                res = new DeactivateMessageResponse();
                res.errorCode = -1;
                res.l_newspk = l_newspk;
                res.reason = e.Message;
                res.successful = false;
                return res;
            }

            res = new DeactivateMessageResponse();
            res.errorCode = 0;
            res.l_newspk = l_newspk;
            res.reason = "";
            res.successful = true;
            return res;
        }

        [WebMethod]
        public StoreUserResponse doStoreUser(ULOGONINFO logoninfo, UBBUSER user, int[] deptk)
        {
            string sz_sql = String.Format("SELECT * FROM BBUSER WHERE sz_userid='{0}'", user.sz_userid.ToUpper().Replace("'", "''"));
            StoreUserResponse res = new StoreUserResponse();
            PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);

            try
            {
                db.CheckLogon(ref logoninfo, true);
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);

                if (rs.HasRows && user.l_userpk == 0)
                {
                    res.errorCode = -1;
                    res.successful = false;
                    res.reason = "User ID already exists";
                    rs.Close();
                    return res;
                }

                rs.Close();
                long l_timestamp = db.getDbClock();
                user.l_disabled_timestamp = l_timestamp;
                sz_sql = String.Format("sp_cb_store_user {0}, '{1}', '{2}', '{3}', {4}, {5}, {6}, {7}, '{8}', {9}, {10}, '{11}'",
                user.l_userpk, user.sz_userid.ToUpper().Replace("'", "''"), user.sz_name.Replace("'", "''"), user.sz_paspassword, user.l_profilepk, user.f_disabled, user.l_deptpk, logoninfo.l_comppk, user.sz_hash_paspwd, l_timestamp, (int)BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN, user.sz_organization.Replace("'", "''"));

                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    user.l_userpk = (long)rs.GetDecimal(0);
                }
                rs.Close();

                // Delete existing regions
                sz_sql = string.Format("sp_cb_del_userregions {0}", user.l_userpk);
                db.ExecNonQuery(sz_sql);

                // Insert new regions
                user.l_deptpklist = deptk;
                for (int i = 0; i < deptk.Length; ++i)
                {
                    sz_sql = String.Format("sp_cb_ins_userregions {0}, {1}, {2}", user.l_profilepk, user.l_userpk, deptk[i]);
                    db.ExecNonQuery(sz_sql);
                }
            }
            catch (Exception e)
            {
                res.successful = false;
                res.user = user;
                res.errorCode = -1;
                res.reason = e.Message;
                return res;
            }
            finally { db.close(); }

            res.successful = true;
            res.user = user;
            return res;
        }

        [WebMethod]
        public GetUsersResponse doGetUsers(ULOGONINFO logoninfo)
        {
            List<UBBUSER> ulist = new List<UBBUSER>();
            GetUsersResponse res = new GetUsersResponse();

            String sz_sql = "SELECT * FROM BBUSER";
            PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);
            try
            {
                
                db.CheckLogon(ref logoninfo, true);
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UBBUSER obj = new UBBUSER();
                    
                    obj.l_userpk = (long)rs.GetDecimal(0);
                    obj.sz_userid = rs.GetString(1);
                    obj.sz_name = rs.GetString(3);
                    if (rs.IsDBNull(4))
                        obj.sz_surname = "";
                    else
                        obj.sz_surname = rs.GetString(4);
                    obj.l_comppk = rs.GetInt32(5);
                    obj.l_deptpk = rs.GetInt32(6);
                    obj.l_profilepk = rs.GetInt32(7);
                    obj.f_disabled = rs.GetInt16(9);
                    if(rs.IsDBNull(31))
                        obj.l_disabled_timestamp = 0;
                    else
                        obj.l_disabled_timestamp = (long)rs.GetDecimal(31);

                    if (rs.IsDBNull(32))
                        obj.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN; //doesn't matter
                    else
                    {
                        if (rs.GetInt32(32) == 1)
                            obj.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.REACHED_RETRY_LIMIT;
                        else if (rs.GetInt32(32) == 2)
                            obj.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN;
                        
                    }
                    if(rs.IsDBNull(33))
                        obj.sz_organization = "";
                    else
                        obj.sz_organization = rs.GetString(33);
                    ulist.Add(obj);
                }
                rs.Close();
                foreach (UBBUSER u in ulist)
                {
                    sz_sql = String.Format("SELECT l_deptpk FROM BBUSERPROFILE_X_DEPT WHERE l_userpk={0}", u.l_userpk);
                    rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                    List<int> deptlist = new List<int>();
                    while (rs.Read())
                        deptlist.Add(rs.GetInt32(0));
                    u.l_deptpklist = deptlist.ToArray();
                    rs.Close();
                }
            }
            catch (Exception e)
            {
                res.errorCode = -1;
                res.successful = false;
                res.user = ulist;
                res.reason = e.Message;
            }
            finally { db.close(); }

            res.successful = true;
            res.user = ulist;
            return res;
        }
        

        [WebMethod]
        public PasLogonResponse doPasLogon(ULOGONINFO logoninfo)
        {
            PasLogonResponse res;

            try
            {
                //System.Web.SessionState.HttpSessionState state = this.Session;
                logoninfo.sessionid = Guid.NewGuid().ToString();//state.SessionID.ToString();
                //String sha = Helpers.CreateSHA512Hash(l.sz_password);
                ULogon logon = new ULogon();
                UPASLOGON ret = logon.CBAdminLogon(ref logoninfo); //if ok, sessionid is stored in ret
                logon.close();
                res = new PasLogonResponse();
                res.successful = true;
                res.logon = ret;
                return res;
            }
            catch (Exception e)
            {
                res = new PasLogonResponse();
                res.errorCode = -1;
                res.successful = false;
                res.reason = e.Message;
                return res;
            }

        }

        [WebMethod]
        public SetPAShapeObsoleteResponse doSetPAShapeObsolete(ULOGONINFO logoninfo, UDEPARTMENT department, UShape shape)
        {
            SetPAShapeObsoleteResponse res;
            PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);
            try
            {
                db.CheckLogon(ref logoninfo, true);
                res = new SetPAShapeObsoleteResponse();
                res.shape = db.setPAShapeObsolete(department, shape);
                res.successful = true;
                return res;
            }
            catch (Exception e)
            {
                res = new SetPAShapeObsoleteResponse();
                res.errorCode = -1;
                res.successful = false;
                res.reason = e.Message;
                return res;
            }

        }
        
        [WebMethod]
        public GetTotalNumberOfMessagesResponse doGetTotalNumberOfMessages(ULOGONINFO logoninfo, long l_period)
        {
            GetTotalNumberOfMessagesResponse res;
            PASUmsDb db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);
            try
            {
                db.CheckLogon(ref logoninfo, true);
                res = new GetTotalNumberOfMessagesResponse();
                db.getTotalNumberOfMessages(l_period, ref res.total_events, ref res.total_regional, ref res.total_national, ref res.total_test);
                res.successful = true;
                return res;
            }
            catch (Exception e)
            {
                res = new GetTotalNumberOfMessagesResponse();
                res.errorCode = -1;
                res.successful = false;
                res.reason = e.Message;
                return res;
            }

        }
        

        [WebMethod]
        public GetRestrictionAreasResponse doGetRestrictionAreas(ULOGONINFO logoninfo, PASHAPETYPES type)
        {
            PASUmsDb db;
            GetRestrictionAreasResponse res;

            if (type.Equals(PASHAPETYPES.PAALERT) ||
                   type.Equals(PASHAPETYPES.PAEVENT) ||
                   type.Equals(PASHAPETYPES.PAOBJECT) ||
                   type.Equals(PASHAPETYPES.PASENDING))
            {
                throw new NotImplementedException();
            }

            List<UDEPARTMENT> dlist = new List<UDEPARTMENT>();
            string sz_sql = String.Format("SELECT l_deptpk, l_deptpri, sz_deptid, f_map, SH.sz_xml, isnull(SH.l_disabled_timestamp,0) as l_disabled_timestamp, isnull(SH.f_disabled, 0) as f_disabled " +
                                            "FROM v_BBDEPARTMENT DEP " +
                                            "LEFT OUTER JOIN PASHAPE SH ON DEP.l_deptpk = SH.l_pk " +
                                           "WHERE SH.l_type = {0} AND DEP.l_comppk = {1} ORDER BY DEP.l_deptpk", (int)type, logoninfo.l_comppk);
            try
            {
                db = new PASUmsDb();
                db.CheckLogon(ref logoninfo, true);
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);

                UDEPARTMENT obj = new UDEPARTMENT();
                obj.l_deptpk = -1;

                while (rs.Read())
                {
                    int tmppk = rs.GetInt32(0);
                    if (tmppk != obj.l_deptpk)
                    {
                        obj = new UDEPARTMENT();
                        obj.l_deptpk = rs.GetInt32(0);
                        obj.l_deptpri = rs.GetInt16(1);
                        obj.sz_deptid = rs.GetString(2);
                        obj.f_map = rs.GetInt16(3);
                        dlist.Add(obj);
                    }

                    UShape shape = UPolygon.ParseFromXml(rs.GetString(4));
                    shape.f_disabled = rs.GetInt16(6);
                    shape.l_disabled_timestamp = (long)rs.GetDecimal(5);
                    obj.restrictionShapes.Add(shape);
                }
                rs.Close();
                db.close();
            }
            catch (Exception e)
            {
                res = new GetRestrictionAreasResponse();
                res.successful = false;
                res.errorCode = -1;
                res.reason = e.Message;
            }

            res = new GetRestrictionAreasResponse();
            res.successful = true;
            res.restrictions = dlist;
            return res;
        }
        
        [WebMethod]
        public GetUserActivityResponse doGetUserActivity(ULOGONINFO logoninfo, long period, List<UBBUSER> users)
        {
            PASUmsDb db;
            GetUserActivityResponse res;
            List<UPASLOG> loglist = new List<UPASLOG>();

            List<UDEPARTMENT> dlist = new List<UDEPARTMENT>();
            string sz_sql_part = "";
            
            for(int i=0;i<users.Count;++i)
            {
                if(i==0)
                    sz_sql_part += "AND (";
                sz_sql_part += "l_userpk=" + users[i].l_userpk + " ";
                if (i + 1 < users.Count)
                    sz_sql_part += "OR ";
                else
                    sz_sql_part += ")";
            }
            string sz_sql = String.Format(
                            "SELECT * " +
                              "FROM PASLOG " +
                             "WHERE l_timestamp BETWEEN {0} AND {1} ", period, period + 100000000);
            sz_sql += sz_sql_part;

            try
            {
                db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120);
                db.CheckLogon(ref logoninfo, true);
                
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_KEEPOPEN);
                OdbcDataReader refno;
                UPASLOG log;
                
                while (rs.Read())
                {
                    log = new UPASLOG();
                    log.l_id = rs.GetInt64(0);
                    log.l_userpk = (long)rs.GetDecimal(1);
                    log.l_operation = rs.GetInt16(2);
                    log.l_timestamp = (long)rs.GetDecimal(3);    
                    log.sz_desc = rs.GetString(4);
                    loglist.Add(log);
                }
                rs.Close();

                foreach (UPASLOG paslog in loglist)
                {
                    if (paslog.l_operation == 104) // New sending
                    {
                        string msg_text = "";
                        string event_name = "";

                        string desc = paslog.sz_desc;
                        desc = desc.Substring(desc.IndexOf("l_refno"));
                        desc = desc.Substring(0,desc.IndexOf(','));
                        long l_refno = long.Parse(desc.Substring(desc.IndexOf('=') + 1));
                        sz_sql = String.Format("SELECT distinct l_refno, sz_text FROM LBASEND_TEXT where l_refno={0}", l_refno);
                        rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                        while (rs.Read())
                            msg_text = rs.GetString(1);
                        rs.Close();

                        desc = paslog.sz_desc;
                        desc = desc.Substring(desc.IndexOf("l_projectpk"));
                        desc = desc.Substring(0,desc.IndexOf(','));
                        long l_projectpk = long.Parse(desc.Substring(desc.IndexOf('=') + 1));
                        sz_sql = String.Format("SELECT distinct l_projectpk, sz_name FROM BBPROJECT where l_projectpk={0}", l_projectpk);
                        rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                        while (rs.Read())
                            event_name = rs.GetString(1);
                        rs.Close();

                        paslog.sz_desc = event_name + " - " + msg_text;

                    }
                    else if (paslog.l_operation == 101) // New event
                    {
                        string desc = paslog.sz_desc;
                        desc = desc.Substring(desc.IndexOf("l_projectpk"));
                        desc = desc.Substring(0, desc.IndexOf(','));
                        long l_projectpk = long.Parse(desc.Substring(desc.IndexOf('=') + 1));
                        sz_sql = String.Format("SELECT distinct l_projectpk, sz_name FROM BBPROJECT where l_projectpk={0}", l_projectpk);
                        rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                        while (rs.Read())
                            paslog.sz_desc = rs.GetString(1);
                        rs.Close();
                    }
                    else if (paslog.l_operation == 107 || paslog.l_operation == 111 || paslog.l_operation == 200) // info on user createing a sending, sending to operator or kill sending
                    {
                        string msg_text = "";
                        string event_name = "";
                        string desc = paslog.sz_desc;
                        desc = desc.Substring(desc.IndexOf("l_refno"));
                        desc = desc.Substring(0, desc.IndexOf(','));
                        long l_refno = long.Parse(desc.Substring(desc.IndexOf('=') + 1));
                        sz_sql = String.Format("SELECT distinct st.l_refno, st.sz_text, p.sz_name " +
                                                 "FROM LBASEND_TEXT st, BBPROJECT p, BBPROJECT_X_REFNO pxr " +
                                                "WHERE st.l_refno={0} " + 
                                                  "AND st.l_refno=pxr.l_refno " +
                                                  "AND p.l_projectpk = pxr.l_projectpk", l_refno);
                        rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                        while (rs.Read())
                            paslog.sz_desc = rs.GetString(2) + " - " + rs.GetString(1);
                        rs.Close();
                    }
                }
                db.close();
            }
            catch (Exception e)
            {
                res = new GetUserActivityResponse();
                res.successful = false;
                res.errorCode = -1;
                res.reason = e.Message;
                throw e;
            }

            res = new GetUserActivityResponse();
            res.successful = true;
            res.log = loglist;
            return res;
        }
        

        [WebMethod]
        public GetSingleRestricionResponse doGetSingleRestricion(ULOGONINFO logoninfo, long areaid)
        {
            PASUmsDb db;
            GetSingleRestricionResponse res;

            string sz_sql = String.Format("SELECT l_deptpk, l_deptpri, sz_deptid, f_map, SH.sz_xml, isnull(SH.l_disabled_timestamp,0) as l_disabled_timestamp, isnull(SH.f_disabled, 0) as f_disabled " +
                              "FROM v_BBDEPARTMENT DEP " +
                              "LEFT OUTER JOIN PASHAPE SH ON DEP.l_deptpk = SH.l_pk " +
                             "WHERE SH.l_type = 16 " + 
                             "AND SH.l_pk = {0}", areaid);
            try
            {
                db = new PASUmsDb();
                db.CheckLogon(ref logoninfo, true);
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                if (rs.HasRows)
                {
                    rs.Read();
                    UDEPARTMENT obj = new UDEPARTMENT();
                    obj.l_deptpk = rs.GetInt32(0);
                    obj.l_deptpri = rs.GetInt16(1);
                    obj.sz_deptid = rs.GetString(2);
                    obj.f_map = rs.GetInt16(3);
                    UShape shape = UPolygon.ParseFromXml(rs.GetString(4));
                    shape.f_disabled = rs.GetInt16(6);
                    shape.l_disabled_timestamp = (long)rs.GetDecimal(5);
                    obj.restrictionShapes.Add(shape);
                    res = new GetSingleRestricionResponse();
                    res.restriction = obj;
                    res.successful = true;
                }
                else
                {
                    res = new GetSingleRestricionResponse();
                    res.successful = false;
                    res.reason = "No restriction found";
                    res.errorCode = -1;
                }
                rs.Close();
                db.close();
            }
            catch (Exception e)
            {
                res = new GetSingleRestricionResponse();
                res.successful = false;
                res.errorCode = -1;
                res.reason = e.Message;
                return res;
            }

            return res;
        }
       
        [WebMethod]
        public CheckAccessResponse doCheckAccess(ULOGONINFO logoninfo, ACCESSPAGE accesspage)
        {
            PASUmsDb db;
            CheckAccessResponse res;

            switch (accesspage)
            {
                case ACCESSPAGE.PREDEFINEDTEXT:
                    break;
                case ACCESSPAGE.RESTRICTIONAREA:
                    break;
            }
            
            string sz_sql = "";
            
            try
            {
                db = new PASUmsDb();
                db.CheckLogon(ref logoninfo, true);
                OdbcDataReader rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                if (rs.HasRows)
                {
                    rs.Read();
                    res = new CheckAccessResponse();
                    res.granted = true;
                    res.successful = true;
                }
                else
                {
                    res = new CheckAccessResponse();
                    res.successful = false;
                    res.reason = "Function occupied by another user, please try again later";
                    res.errorCode = -1;
                }
                rs.Close();
                db.close();
            }
            catch (Exception e)
            {
                res = new CheckAccessResponse();
                res.successful = false;
                res.errorCode = -1;
                res.reason = e.Message;
                return res;
            }

            return res;
        }

        // Responses
        public class DeactivateMessageResponse : Response
        {
            // return value
            public long l_newspk;
        }

        public class StoreUserResponse : Response
        {
            // return value
            public UBBUSER user;

        }

        public class GetUsersResponse : Response
        {
            // return value
            public List<UBBUSER> user;

        }

        public class PasLogonResponse : Response
        {
            // return value
            public UPASLOGON logon;
        }

        public class SetPAShapeObsoleteResponse : Response
        {
            // return value
            public UShape shape;
        }
        public class GetTotalNumberOfMessagesResponse : Response
        {
            // return value
            public long total_events;
            public long total_regional;
            public long total_national;
            public long total_test;
        }

        public class GetRestrictionAreasResponse : Response
        {
            // return value
            public List<UDEPARTMENT> restrictions;
        }

        public class GetUserActivityResponse : Response
        {
            // return value
            public List<UPASLOG> log;
        }

        public class GetSingleRestricionResponse : Response
        {
            // return value
            public UDEPARTMENT restriction;
        }

        public class CheckAccessResponse : Response
        {
            // return value
            public bool granted;
        }
       
    }
}
