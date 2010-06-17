using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Odbc;
using com.ums.UmsCommon;
using System.Web.Services.Protocols;
using System.Xml;

namespace com.ums.UmsDbLib
{
    public class UmsDbConnParams
    {
        public String sz_dsn;
        public String sz_uid;
        public String sz_pwd;
    }

    public class UmsDb
    {
        public static int UREADER_KEEPOPEN = 1;
        public static int UREADER_AUTOCLOSE = 2;

        private String sz_last_error;
        protected OdbcConnection conn;
        protected bool m_b_dbconn;
        protected string sz_constring;
        protected OdbcTransaction m_odbc_transaction;
        protected bool m_b_transaction_in_progress = false;
        protected OdbcCommand m_cmd;
        protected OdbcDataReader m_reader;
        protected int timeout = 60;

        protected void setLastError(string s) { 
            sz_last_error = s;
            ULog.error(s);
        }

        public String getLastError() { return sz_last_error; }

        public void BeginTransaction()
        {
            if (m_b_dbconn)
            {
                try
                {
                    m_odbc_transaction = conn.BeginTransaction();
                    m_b_transaction_in_progress = true;
                    return;
                }
                catch (Exception)
                {
                    throw new UDbConnectionException();
                }
            }
            throw new UDbConnectionException();
        }
        public void RollbackTransaction()
        {
            try
            {
                m_odbc_transaction.Rollback();
                m_b_transaction_in_progress = false;
            }
            catch (Exception)
            {
                throw new UDbConnectionException();
            }
        }
        public void CommitTransaction()
        {
            try
            {
                m_odbc_transaction.Commit();
                m_b_transaction_in_progress = false;
            }
            catch (Exception)
            {
                throw new UDbConnectionException();
            }

        }


        public UmsDb(string sz_dsn, string sz_uid, string sz_password, int timeout)
        {
            sz_constring = String.Format("DSN={0}; UID={1}; PWD={2}", sz_dsn, sz_uid, sz_password);
            this.timeout = timeout;
            try
            {
                init();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public UmsDb() : this(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 120)
        {
            
        }
        /*no auto connect*/
        public UmsDb(int n_timeout)
        {
            this.timeout = n_timeout;
        }


        public bool CheckLogonLiteral(ref ULOGONINFO info)
        {
            bool b_ret = false;
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            String szSQL = String.Format("SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc, BU.l_userpk, BD.l_deptpk, BC.l_comppk FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD WHERE BU.sz_userid='{0}' AND " +
                                            "BC.sz_compid='{1}' AND BD.sz_deptid='{2}' AND BU.l_comppk=BC.l_comppk AND BC.l_comppk=BD.l_comppk AND " +
                                            "BU.sz_hash_paspwd='{3}'",
                                            info.sz_userid, info.sz_compid, info.sz_deptid, info.sz_password);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    Int64 l_fromdb = rs.GetInt64(0);
                    //if (l_fromdb == info.l_userpk)
                    {
                        info.l_deptpri = rs.GetInt32(1);
                        info.sz_stdcc = rs.GetString(2);
                        info.l_userpk = rs.GetInt64(3);
                        info.l_deptpk = rs.GetInt32(4);
                        info.l_comppk = rs.GetInt32(5);
                        info.l_priserver = 2;
                        info.l_altservers = 1;
                        b_ret = true;
                    }
                }
                rs.Close();
            }
            catch (SoapException e)
            {
                throw e;
            }
            catch (USessionDoesNotExsistException e)
            {
                throw e;
            }
            catch (USessionExpiredException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                throw new UDbQueryException("CheckLogon");
            }
            checkSessionIntegrity(ref info);
            /*finally
            {
                CloseRecordSet();
            }*/
            if (!b_ret)
            {
                setLastError(String.Format("Error in logon credentials for userid/compid {0}/{1}", info.sz_userid, info.sz_compid));
                throw new ULogonFailedException();
            }
            return b_ret;
        }

        public bool CheckLogon(ref ULOGONINFO info)
        {
            bool b_ret = false;
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            //info.sz_password = Encoding.ASCII.GetString(info.sz_password.ToCharArray());
            String szSQL = String.Format("SELECT BU.l_userpk, BD.l_deptpri, BD.sz_stdcc FROM BBUSER BU, BBCOMPANY BC, BBDEPARTMENT BD WHERE BU.l_userpk={0} AND " +
                                            "BC.l_comppk={1} AND BD.l_deptpk={2} AND BU.l_comppk=BC.l_comppk AND BC.l_comppk=BD.l_comppk AND " +
                                            "BU.sz_hash_paspwd='{3}'",
                                            info.l_userpk, info.l_comppk, info.l_deptpk, 
                                            info.sz_password);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    Int64 l_fromdb = rs.GetInt64(0);
                    if (l_fromdb == info.l_userpk)
                    {
                        info.l_deptpri = rs.GetInt32(1);
                        info.sz_stdcc = rs.GetString(2);
                        info.l_priserver = 2;
                        info.l_altservers = 1;
                        b_ret = true;
                    }
                }
                rs.Close();
                checkSessionIntegrity(ref info);

                szSQL = String.Format("sp_pas_updatesession {0}, '{1}', '{2}'",
                                    info.l_userpk, info.sessionid, info.sz_password);
                ExecNonQuery(szSQL);

            }
            catch (SoapException e)
            {
                throw e;
            }
            catch (USessionDoesNotExsistException e)
            {
                throw e;
            }
            catch (USessionExpiredException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                //throw new UDbQueryException("CheckLogon");
                throw e;
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            if (!b_ret)
            {
                setLastError(String.Format("Error in logon credentials for userid/compid {0}/{1}", info.sz_userid, info.sz_compid));
                throw new ULogonFailedException();
            }
            return b_ret;
        }

        protected bool checkSessionIntegrity(ref ULOGONINFO info)
        {
            if (info.l_userpk <= 0)
                throw new ULogonFailedException();
            if (info.sessionid.Length <= 0)
                throw new ULogonFailedException();
            if (info.sz_password.Length <= 0)
                throw new ULogonFailedException();
            //check session integrity
            String szSQL = String.Format("sp_pas_getsession {0}, '{1}', '{2}'",
                               info.l_userpk, info.sessionid, info.sz_password);
            OdbcDataReader rs = ExecReader(szSQL, UREADER_KEEPOPEN);
            Int64 now = 0;
            Int64 last = 0;
            String snow = "0";
            String slast = "0";
            Decimal dnow = 0;
            Decimal dlast = 0;
            int timeout = 0;
            if (rs.Read())
            {
                if (!rs.IsDBNull(0))
                    dnow = rs.GetDecimal(0);
                if (!rs.IsDBNull(1))
                    dlast = rs.GetDecimal(1);
                now = Int64.Parse(dnow.ToString());
                last = Int64.Parse(dlast.ToString());
                if (!rs.IsDBNull(2))
                    timeout = rs.GetInt32(2);
                else
                    timeout = 1;
            }
            rs.Close();
            if (last <= 0)
            {
                throw new SoapException("Session no longer exist", SoapException.ServerFaultCode, new USessionExpiredException(0));
            }
            //throw new USessionDoesNotExsistException();

            UDATETIME dt_now = new UDATETIME(now);
            UDATETIME dt_last = new UDATETIME(last);
            long seconds = dt_now.getTimeDiffSec(dt_last);
            if (Math.Abs(seconds) > timeout && timeout > 0)
            {
                RemoveSession(ref info);
                throw new SoapException("Session Expired", SoapException.ServerFaultCode, new USessionExpiredException(seconds));
            }
            //USessionExpiredException(seconds, new Exception("Session expired"));

            return true;
        }

        protected bool RemoveSession(ref ULOGONINFO l)
        {
            try
            {
                //delete logon record from DB
                String szSQL = String.Format("sp_pas_logoff {0}, '{1}', '{2}'", l.l_userpk, l.sessionid, l.sz_password);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    rs.Close();
                    return true;
                }
                else
                {
                    rs.Close();
                    throw new USessionDoesNotExsistException();
                }
            }
            catch (Exception e)
            {
                throw e;
            }

        }

        public long getDbClock()
        {
            long ret = 0;
            try
            {
                String szSQL = String.Format("sp_getdatetime");
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    ret = rs.GetInt64(0);
                }
                rs.Close();

            }
            catch (Exception e)
            {
            }
            return ret;

        }

        /*initialize database connection*/
        protected bool init()
        {
            m_b_dbconn = false;
            conn = new OdbcConnection(sz_constring);
            //conn.ConnectionTimeout = timeout;
            try
            {
                conn.Open();
                m_b_dbconn = true;
            }
            catch (Exception e)
            {
                String constr = "";
                if (sz_constring.Length > 12)
                    constr = sz_constring.Substring(0, sz_constring.Length - 9);
                else
                    constr = sz_constring;
                setLastError("constr="+ constr + "\n" + e.Message + "\n" + e.StackTrace);
                throw e;
            }
            return m_b_dbconn;
        }
        public void close()
        {
            try
            {
                m_reader.Close();
            }
            catch (Exception)
            {
            }
            try
            {
                m_cmd.Dispose();
            }
            catch (Exception)
            {
            }
            try
            {
                conn.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public long newRefno()
        {
            int n_ret = -1;
            String szSQL = String.Format("sp_refno_out");
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    n_ret = rs.GetInt32(0);
                }
                rs.Close();
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                throw e;
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            return n_ret;
        }
        public bool newProject(String sz_projectname /*in*/, ref ULOGONINFO l /*in*/, ref BBPROJECT p /*out*/)
        {
            bool b_ret = false;
            String szSQL = String.Format("sp_project '{0}', 0, '{1}', {2}, {3}", 
                                        "insert", sz_projectname, UCommon.UGetDateNow() + UCommon.UGetTimeNow(), l.l_deptpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    p.sz_projectpk = rs.GetString(0);
                    p.sz_name = rs.GetString(1);
                    p.sz_created = rs.GetString(2);
                    p.sz_updated = rs.GetString(3);
                    b_ret = true;
                }
                else
                    ULog.error(0, szSQL, String.Format("Could not create project for {0}/{1}/{2}", l.sz_userid, l.sz_deptid, l.sz_compid));
                rs.Close();
            }
            catch (Exception e)
            {
                ULog.error(0, szSQL, e.Message);
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            return b_ret;
        }
        /*
         *  Link a sending (refno) to a project
         */
        public bool linkRefnoToProject(ref BBPROJECT p /*in*/, long n_refno /*in*/, long n_type /*in*/, long n_parent_refno /*in*/)
        {
            /*
             * n_type (linktype)
             * 0 = Voice sending
             * 9 = Test sending (list of phone numbers to test SMS text and voice profiles)
             */
            String szSQL = String.Format("sp_ins_projectsending {0}, {1}, {2}, {3}",
                                         p.sz_projectpk, n_refno, n_type, n_parent_refno);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, szSQL, e.Message);
            }
            return false;
        }

        public int getNumDynfilesInProfile(long l_profilepk)
        {
            int n_ret = -1;
            String szSQL = String.Format("sp_get_numdynfiles_in_profile {0}", l_profilepk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    n_ret = rs.GetInt32(0);
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw new UDbQueryException(e.Message);
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            if (n_ret < 0)
            {
                throw new UDbNoDataException("Could not find specified profile (l_profilepk=" + l_profilepk + ")");
            }
            return n_ret;
        }

        public OdbcCommand CreateCommand(String s, int OPENMODE)
        {
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            try
            {
                if (m_reader != null)
                    m_reader.Close(); //close the old reader
            }
            catch (Exception)
            {
            }
            try
            {
                if (m_cmd != null)
                    m_cmd.Dispose();
            }
            catch (Exception)
            {
            }
            m_cmd = conn.CreateCommand(); // new OdbcCommand(s, conn, m_odbc_transaction);
            
            m_cmd.CommandTimeout = timeout;
            m_cmd.CommandText = s;
            return m_cmd;
        }

        public void AddCommandParameter(String paramname, OdbcType type)
        {
            m_cmd.Parameters.Add(paramname, type);
        }

        public void SetCommandValue(String paramname, Object value)
        {
            m_cmd.Parameters[paramname].Value = value;
        }

        public OdbcDataReader ExecCommandReader()
        {
            try
            {
                return m_cmd.ExecuteReader();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public int ExecCommand()
        {
            try
            {
                return m_cmd.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public OdbcDataReader ExecReader(String s, int OPENMODE)
        {
            return ExecReader(s, OPENMODE, false);
        }

        /*
         * using KEEPOPEN the caller is responsible for Closing the reader 
         */
        public OdbcDataReader ExecReader(String s, int OPENMODE, bool prepare) //KEEPOPEN or AUTOCLOSE
        {
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            try
            {
                if(m_reader!=null)
                    m_reader.Close(); //close the old reader
            }
            catch (Exception)
            {
            }
            try
            {
                if(m_cmd!=null)
                    m_cmd.Dispose();
            }
            catch (Exception)
            {
            }

            try
            {
                if (m_b_transaction_in_progress)
                {
                    m_cmd = new OdbcCommand(s, conn, m_odbc_transaction);
                }
                else
                    m_cmd = new OdbcCommand(s, conn);
                m_cmd.CommandTimeout = timeout;
                if (prepare)
                    m_cmd.Prepare();
            }
            catch (Exception e)
            {
                throw e;
            }
            OdbcDataReader tempreader;
            tempreader = m_cmd.ExecuteReader();
            if (OPENMODE == UREADER_AUTOCLOSE)
                m_reader = tempreader;//m_cmd.ExecuteReader());
            else
                m_reader = null;
            /*else
            {
                return tempreader; // m_cmd.ExecuteReader();
            }*/
            try
            {
                m_cmd.Dispose();
            }
            catch (Exception) { }

            return tempreader;
        }

        /*
         * May be executed while a reader is open
         */
        public bool ExecNonQuery(String s)
        {
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            try
            {
                m_cmd.Dispose(); //close the old command
            }
            catch (Exception)
            {
            }
            try
            {
                if (m_b_transaction_in_progress)
                {
                    m_cmd = new OdbcCommand(s, conn, m_odbc_transaction);
                }
                else
                    m_cmd = new OdbcCommand(s, conn);
                m_cmd.ExecuteNonQuery();
                m_cmd.Dispose();
            }
            catch (Exception e)
            {
                throw e;
            }
    
            return true;
        }
    }


}
