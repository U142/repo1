using System;
using System.Data.Odbc;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Collections;
using System.Collections.Generic;

namespace com.ums.UmsParm
{
    public class PASUmsDb : UmsDb
    {
        public PASUmsDb(string sz_dsn, string sz_uid, string sz_password, int timeout)
            : base(sz_dsn, sz_uid, sz_password, timeout)
        {
        }
        public PASUmsDb()
            : base()
        {

        }

        public int GetPasType(int n_deptpk)
        {
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            String szSQL = String.Format("SELECT l_pas FROM BBDEPARTMENTMODS WHERE l_deptpk={0}", n_deptpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if(rs.Read())
                {
                    return rs.GetInt32(0);
                }
                else
                    return -1;
            }
            catch(Exception e)
            {
                setLastError(e.Message);
                throw new UDbQueryException(szSQL);
            }
        }

        /*
         * Check l_alertpk, external execution and owned by
         * check alert profile (no dynamic voc!)
         * check alert addressfile existence
         */
        public bool VerifyAlert(Int64 l_alertpk, ref ULOGONINFO l)
        {
            bool b_ret = false;
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            String szSQL = String.Format("SELECT PA.l_alertpk FROM PAALERT PA, PAEVENT PE, PAOBJECT PO WHERE PA.l_alertpk={0} AND " +
                                            "PA.l_parent=PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PO.l_deptpk={1}",
                                            l_alertpk, l.l_deptpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    b_ret = true;
                }
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                throw new UDbQueryException(szSQL);
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            if(!b_ret)
                throw new UDbNoDataException("Could not find specified Alert (l_alertpk=" + l_alertpk + ")");
            return b_ret;
        }
        public bool VerifyProfile(long l_profilepk, bool b_must_have_no_dynfiles)
        {
            if (!m_b_dbconn)
                throw new UDbConnectionException();
            int n_num_audiofiles = base.getNumDynfilesInProfile(l_profilepk);
            if (n_num_audiofiles > 0 && b_must_have_no_dynfiles)
            {
                throw new UProfileNotSupportedException(l_profilepk, "Max dynamic audio files allowed = 0");
            }
            return true;
        }

        /*
         * in : l_eventpk
         * out: ArrayList of PAALERT objects
         * returns: bool
         * throws DbException
         *  retrieve all alerts from a eventpk.
         */
        public bool GetAlertsFromEvent(Int64 l_eventpk, int n_function, ref List<PAALERT> alerts)//ref ArrayList alerts)
        {
            String szSQL = String.Format("SELECT l_alertpk FROM PAALERT WHERE l_parent={0}", l_eventpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    PAALERT pa = new PAALERT();
                    pa.l_alertpk = rs.GetInt64(0);
                    FillAlert(rs.GetInt64(0), n_function, ref pa);
                    alerts.Add(pa);
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
            return true;
        }

        public bool GetEventAlertStructure(ref ULOGONINFO l, ref List<PAEVENT> list)
        {
            bool b_ret = false;
            /*String szSQL = String.Format("SELECT PA.l_alertpk, PA.l_parent, PE.l_eventpk, PE.l_parent FROM PAALERT PA, PAEVENT PE, PAOBJECT PO "+
                                        "WHERE PA.l_parent=*PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PO.l_deptpk={0} "+
                                        "ORDER BY PE.l_eventpk, PA.l_alertpk",
                                        l.l_deptpk);*/
            String szSQL = String.Format("SELECT isnull(PE.l_eventpk,-1), isnull(PE.l_parent,-1), isnull(PE.sz_name,'N/A'), PE.sz_description, " +
                                        "isnull(PE.l_categorypk,-1), isnull(PE.l_timestamp,-1), isnull(PE.f_epi_lon,0), isnull(PE.f_epi_lat,0) " +
                                        "FROM PAEVENT PE, PAOBJECT PO WHERE PE.l_parent=PO.l_objectpk AND PO.l_deptpk={0}",
                                        l.l_deptpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                //PAEVENT current_event = null;
                while (rs.Read())
                {
                    PAEVENT ev = new PAEVENT();
                    ev.l_eventpk = rs.GetInt64(0);
                    ev.l_parent = rs.GetInt64(1);
                    ev.sz_name = rs.GetString(2);
                    if (rs.IsDBNull(3))
                        ev.sz_description = " ";
                    else
                        ev.sz_description = rs.GetString(3);
                    ev.l_categorypk = rs.GetInt64(4);
                    ev.l_timestamp = rs.GetInt64(5);
                    ev.f_epi_lon = rs.GetFloat(6);
                    ev.f_epi_lat = rs.GetFloat(7);


                    if (GetAlertsFromEvent(ev.l_eventpk, 0, ref ev.alerts))
                    {

                    }

                    /*long n_alertpk = rs.GetInt64(0);
                    long n_alertparent = rs.GetInt64(1);
                    long n_eventparent = rs.GetInt64(3);
                    long n_eventpk = rs.GetInt64(2);
                    if (current_event == null || current_event.l_eventpk != n_eventpk)
                    {
                        current_event = new PAEVENT();
                    }

                    PAALERT prev_alert = null;*/


                    list.Add(ev);
                }
                rs.Close();
                b_ret = true;
            }
            catch (Exception e)
            {
                throw e;
            }
            return b_ret;
        }

        public bool FillEvent(Int64 l_eventpk, int n_function, ref PAEVENT pe)
        {
            bool b_ret = false;
            pe.l_eventpk = l_eventpk;
            String szSQL = String.Format("SELECT l_eventpk, l_parent, sz_name, sz_description, l_categorypk, l_timestamp, f_epi_lon, f_epi_lat " +
                                        "FROM PAEVENT WHERE l_eventpk={0}", l_eventpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    pe.l_parent = rs.GetInt64(1);
                    pe.sz_name = rs.GetString(2);
                    pe.sz_description = rs.GetString(3);
                    pe.l_categorypk = rs.GetInt64(4);
                    pe.l_timestamp = rs.GetInt64(5);
                    pe.f_epi_lon = rs.GetFloat(6);
                    pe.f_epi_lat = rs.GetFloat(7);
                    b_ret = true;
                }
                rs.Close();
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                pe.l_eventpk = 0;
            }
            return b_ret;

        }

        /*  in: alertpk, reference to PAALERT struct
         *  out: UmsCommon.PAALERT struct
         *  returns: boolean
         */
        public bool FillAlert(Int64 l_alertpk, int n_function, ref PAALERT pa)
        {
            bool b_ret = false;
            pa.l_alertpk = l_alertpk;
            String szSQL = String.Format("SELECT l_alertpk, isnull(l_parent,-1), sz_name, sz_description, isnull(l_profilepk, -1), " +
                                            "isnull(l_schedpk,-1), sz_oadc, isnull(l_validity,1), isnull(l_addresstypes,0), isnull(l_timestamp,0), isnull(f_locked,0), isnull(sz_areaid,''), "+
                                            "isnull(l_maxchannels, 0), isnull(l_requesttype, 0), isnull(sz_sms_oadc, ''), isnull(sz_sms_message,''), isnull(l_expiry, 60) l_expiry " +
                                            "FROM PAALERT WHERE l_alertpk={0}", l_alertpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    pa.setAlertPk(rs.GetInt64(0));
                    pa.setParent(rs.GetString(1));
                    pa.setName(rs.GetString(2));
                    if (rs.IsDBNull(3))
                        pa.setDescription("");
                    else
                        pa.setDescription(rs.GetString(3));
                    pa.setProfilePk(rs.GetInt32(4));
                    pa.setSchedPk(rs.GetString(5));
                    pa.setOadc(rs.GetString(6));
                    pa.setValidity(rs.GetInt32(7));
                    pa.setAddressTypes(rs.GetInt32(8));
                    pa.setTimestamp(rs.GetString(9));
                    pa.setLocked(rs.GetInt32(10));
                    pa.setAreaID(rs.GetString(11));
                    pa.setMaxChannels(rs.GetInt32(12));
                    pa.setRequestType(rs.GetInt32(13));
                    pa.setFunction(n_function);
                    pa.setSmsOadc(rs.GetString(14));
                    pa.setSmsMessage(rs.GetString(15));
                    pa.setExpiry(rs.GetInt32(16));
                    b_ret = true;
                }
                rs.Close();
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                pa.setAlertPk(0);
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            return b_ret;
        }

        /*
         * Fill BBRESCHEDPROFILE struct based on reschedpk
         */
        public bool FillReschedProfile(string sz_reschedpk, ref BBRESCHEDPROFILE re)
        {
            bool b_ret = false;
            String szSQL = String.Format("SELECT l_reschedpk, l_deptpk, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval, sz_name " +
                                            "FROM BBRESCHEDPROFILES WHERE l_reschedpk={0}", sz_reschedpk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    re.setReschedPk(rs.GetString(0));
                    re.setDeptPk(rs.GetInt32(1));
                    re.setRetries(rs.GetInt32(2));
                    re.setInterval(rs.GetInt32(3));
                    re.setCanceltime(rs.GetInt32(4));
                    re.setCancelDate(rs.GetInt32(5));
                    re.setPausetime(rs.GetInt32(6));
                    re.setPauseInterval(rs.GetInt32(7));
                    re.setName(rs.GetString(8));
                    b_ret = true;
                }
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                re.setReschedPk("-1");
            }
            /*finally
            {
                CloseRecordSet();
            }*/
            return b_ret;
        }

        public bool FillValid(int n_valid, ref BBVALID v)
        {
            v.l_valid = n_valid;
            return true;
        }

        /*
         * Get Valid from alert. This is days before callback is disabled
         */
        public bool FillValid(ref PAALERT a, ref BBVALID v)
        {
            try
            {
                v.l_valid = a.l_validity;
                return true;
            }
            catch (Exception e)
            {
                setLastError(e.Message);
            }
            return false;
        }

        /*
         * Get sending number from alert
         */
        public bool FillSendNum(ref PAALERT a, ref BBSENDNUM s)
        {
            s.sz_number = a.sz_oadc;
            return true;
        }

        public bool FillSendNum(String sz_number, ref BBSENDNUM s)
        {
            s.sz_number = sz_number;
            return true;
        }

        /*
         * Get project info by projectpk
         */
        public bool GetProjectInfo(Int64 l_projectpk, ref BBPROJECT p)
        {
            try
            {
                String szSQL = String.Format("SELECT BP.l_projectpk, BP.sz_name, BP.l_createtimestamp, BP.l_updatetimestamp, " +
                                        "BP.l_deptpk, count(BXR.l_refno) n_count " +
                                        "FROM BBPROJECT BP, BBPROJECT_X_REFNO BXR WHERE BP.l_projectpk={0} "+
                                        "AND BP.l_projectpk=BXR.l_projectpk "+
                                        "GROUP BY BP.l_projectpk, BP.sz_name, BP.l_createtimestamp, "+
                                        "BP.l_updatetimestamp, BP.l_deptpk", l_projectpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    p.sz_projectpk = l_projectpk.ToString();
                    p.sz_name = rs.GetString(1);
                    p.sz_created = rs.GetString(2);
                    p.sz_updated = rs.GetString(3);
                    p.n_deptpk = rs.GetInt32(4);
                    p.n_sendingcount = rs.GetInt32(5);

                    //rs.Close();
                }
                rs.Close();
                return false;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         *  fill BBPROJECT.sendinginfo array based on project's pk
         */
        public bool GetSendingInfosByProject(ref BBPROJECT project)
        {
            try
            {
                String szSQL = String.Format("SELECT l_refno, isnull(l_type, 0), isnull(l_parentrefno,0) FROM BBPROJECT_X_REFNO WHERE l_projectpk={0} AND l_type in (0,2)", project.sz_projectpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    int n_refno = rs.GetInt32(0);
                    int n_linktype = rs.GetInt32(1);
                    int n_parentrefno = rs.GetInt32(2);
                    MDVSENDINGINFO mdv = new MDVSENDINGINFO();
                    
                    try
                    {
                        if (GetSendingInfo(n_refno, ref mdv))
                        {
                            mdv.l_linktype = n_linktype;
                            mdv.l_resendrefno = n_parentrefno;
                            project.mdvsendinginfo.Add(mdv);
                        }
                    }
                    catch (Exception)
                    {

                    }

                }
                rs.Close();
                return true;

                
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * fill sendinginfo struct by refno
         */
        public bool GetSendingInfo(long l_refno, ref MDVSENDINGINFO m)
        {
            try
            {
                OdbcDataReader rs = ExecReader(String.Format("sp_sendinginfo {0}", l_refno), UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    m.l_refno = rs.GetInt32(0);
                    m.l_createdate = rs.GetString(1);
                    m.l_createtime = rs.GetString(2);
                    m.l_scheddate = rs.GetString(3);
                    m.l_schedtime = rs.GetString(4);
                    m.sz_sendingname = rs.GetString(5);
                    m.l_sendingstatus = rs.GetInt32(6);
                    if (m.l_sendingstatus == 0)
                        m.l_sendingstatus = 1;
                    m.l_companypk = rs.GetInt32(7);
                    m.l_deptpk = rs.GetInt32(8);
                    m.l_group = rs.GetInt32(9);
                    m.l_type = rs.GetInt32(10);
                    m.l_addresstypes = rs.GetInt32(11);
                    m.l_profilepk = rs.GetInt32(12);
                    try
                    {
                        m.l_queuestatus = rs.GetInt32(13);
                    }
                    catch (Exception) { m.l_queuestatus = 0; }
                    try
                    {
                        m.l_totitem = rs.GetInt32(14);
                    }
                    catch (Exception) { m.l_totitem = 0; }
                    try
                    {
                        m.l_processed = rs.GetInt32(15);
                    }
                    catch (Exception) { m.l_processed = 0; }
                    try
                    {
                        m.l_altjmp = rs.GetInt32(16);
                    }
                    catch (Exception) { m.l_altjmp = 0; }
                    try
                    {
                        m.l_alloc = rs.GetInt32(17);
                    }
                    catch (Exception) { m.l_alloc = 0; }
                    try
                    {
                        m.l_maxalloc = rs.GetInt32(18);
                    }
                    catch (Exception) { m.l_maxalloc = 0; }
                    try
                    {
                        m.sz_oadc = rs.GetString(19);
                    }
                    catch (Exception) { m.sz_oadc = "N/A"; }
                    try
                    {
                        m.l_qreftype = rs.GetInt32(20);
                    }
                    catch (Exception)
                    {
                        m.l_qreftype = 0;
                    }
                    try
                    {
                        m.f_dynacall = rs.GetInt32(21);
                    }
                    catch (Exception)
                    {
                        m.f_dynacall = 1;
                    }
                    try
                    {
                        m.l_nofax = rs.GetInt32(22);
                    }
                    catch (Exception)
                    {
                        m.l_nofax = 0;
                    }
                    try
                    {
                        m.sz_messagetext = rs.GetString(23);
                    }
                    catch (Exception)
                    {
                        m.sz_messagetext = "N/A";
                    }

                    return true;
                }
                rs.Close();
                return false;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * Fill sendinginfo struct
         */
        public bool FillSendingInfo(ref ULOGONINFO l, ref PAALERT a, ref MDVSENDINGINFO m, UDATETIME schedule, String sz_sendingname)
        {
            m.sz_fields = "";
            m.sz_sepused = "";
            m.l_namepos = 0;
            m.l_addresspos = 0;
            m.l_lastantsep = 0;
            m.l_createdate = UCommon.UGetDateNow(); //String.Format("{0:yyyy}{0:MM}{0:dd}", DateTime.UtcNow.ToLocalTime());
            m.l_createtime = UCommon.UGetTimeNow(); //String.Format("{0:HH}{0:mm}", DateTime.UtcNow.ToLocalTime());
            m.l_scheddate = schedule.sz_date;
            m.l_schedtime = schedule.sz_time;
            m.sz_sendingname = sz_sendingname;
            m.l_sendingstatus = 1;
            m.l_companypk = l.l_comppk;
            m.l_deptpk = l.l_deptpk;
            m.l_nofax = ((a.l_addresstypes & (long)ADRTYPES.SENDTO_USE_NOFAX_COMPANY) == (long)ADRTYPES.SENDTO_USE_NOFAX_COMPANY ? 1 : 0);
            m.l_removedup = 1;
            m.l_maxchannels = a.n_maxchannels;
            //m.l_nofax = a.n_nofax;
           

            m.l_group = a.getSendingType(); //type dependent, 3 = polygon, 8 = ellipse
            if (m.l_group != UShape.SENDINGTYPE_POLYGON && m.l_group != UShape.SENDINGTYPE_ELLIPSE &&
                m.l_group != UShape.SENDINGTYPE_GIS)
                throw new USendingTypeNotSupportedException(String.Format("Sending type {0} not supported", m.l_group));

            m.sz_groups = "";
            m.l_type = 1; //voice
            m.f_dynacall = (a.n_function==UCommon.USENDING_LIVE ? 1 : 2);
            m.l_addresstypes = a.l_addresstypes;
            m.l_userpk = l.l_userpk;

            return true;
        }
        public bool FillSendingInfo(ref ULOGONINFO l, ref UMAPSENDING s, ref MDVSENDINGINFO m, UDATETIME schedule)
        {
            if (s.b_resend)
            {
                try
                {
                    GetSendingInfo(s.n_resend_refno, ref m);
                }
                catch (Exception e)
                {
                    throw e;
                }
                /*String resendtype = "";
                switch (s.n_send_channels)
                {
                    case 1:
                        resendtype = "Voice";
                        break;
                    case 2:
                        resendtype = "SMS";
                        break;
                }
                m.sz_sendingname = "[RESEND " + resendtype + "] " + s.sz_sendingname;*/
            }
            else
            {
                m.l_namepos = 0;
                m.l_addresspos = 0;
                m.l_lastantsep = 0;
                m.sz_sendingname = s.sz_sendingname;
                m.l_companypk = l.l_comppk;
                m.l_deptpk = l.l_deptpk;
                m.l_group = s.getGroup();
                //m.l_group = a.getSendingType(); //type dependent, 3 = polygon, 8 = ellipse, 4 = GIS
                m.l_type = 1; //voice
                m.l_addresstypes = s.n_addresstypes;
            }
            //COMMON
            m.sz_fields = "";
            m.sz_sepused = "";
            m.l_createdate = UCommon.UGetDateNow(); //String.Format("{0:yyyy}{0:MM}{0:dd}", DateTime.UtcNow.ToLocalTime());
            m.l_createtime = UCommon.UGetTimeNow(); //String.Format("{0:HH}{0:mm}", DateTime.UtcNow.ToLocalTime());
            m.l_scheddate = schedule.sz_date;
            m.l_schedtime = schedule.sz_time;
            m.l_removedup = 1;
            m.l_maxchannels = s.n_maxchannels;
            m.sz_groups = "";
            m.l_userpk = l.l_userpk;
            m.f_dynacall = (s.getFunction() == UCommon.USENDING_LIVE ? 1 : 2);
            m.l_nofax = ((s.n_addresstypes & (long)ADRTYPES.SENDTO_USE_NOFAX_COMPANY) == (long)ADRTYPES.SENDTO_USE_NOFAX_COMPANY ? 1 : 0);
            m.l_sendingstatus = 1;

            if (m.l_group != UShape.SENDINGTYPE_POLYGON && m.l_group != UShape.SENDINGTYPE_ELLIPSE &&
                m.l_group != UShape.SENDINGTYPE_GIS && m.l_group != UShape.SENDINGTYPE_TESTSENDING &&
                m.l_group != UShape.SENDINGTYPE_MUNICIPAL)
                throw new USendingTypeNotSupportedException(String.Format("Sending type {0} not supported", m.l_group));

            return true;
        }
        public bool FillActionProfile(ref PAALERT a, ref BBACTIONPROFILESEND s)
        {
            s.l_actionprofilepk = a.l_profilepk;
            return true;
        }

        public bool FillActionProfile(long n_profilepk, ref BBACTIONPROFILESEND s)
        {
            s.l_actionprofilepk = n_profilepk;
            return true;
        }

        public bool InsertBBVALID(ref PAS_SENDING s)
        {
            return InsertBBVALID(ref s.l_refno, ref s.m_valid);
        }
        
        public bool InsertBBVALID(ref long l_refno, ref BBVALID valid)
        {
            String szSQL = String.Format("INSERT INTO BBVALID(l_refno, l_valid) VALUES({0}, {1})", l_refno, valid.l_valid);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
        }
        
        public bool InsertBBSENDNUM(ref PAS_SENDING s)
        {
            return InsertBBSENDNUM(ref s.l_refno, ref s.m_sendnum);
        }

        public bool InsertBBSENDNUM(ref long l_refno, ref BBSENDNUM sn)
        {
            String szSQL = String.Format("INSERT INTO BBSENDNUM(l_refno, sz_number) VALUES({0}, '{1}')", l_refno, sn.sz_number);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
        }

        public bool InsertMDVSENDINGINFO(ref PAS_SENDING s)
        {
            String szSQL = String.Format("INSERT INTO MDVSENDINGINFO(sz_fields, sz_sepused, l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, " +
                                  "l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_group, " +
                                  "l_removedup, l_type, f_dynacall, l_addresstypes, l_userpk, l_maxchannels) " +
                                  "VALUES('{0}', '{1}', {2}, {3}, {4}, {5}, {6}, {7}, {8}, '{9}', {10}, {11}, {12}, {13}, " +
                                  "{14}, {15}, {16}, {17}, {18}, {19}, {20})",
                            s.m_sendinginfo.sz_fields,
                            s.m_sendinginfo.sz_sepused,
                            s.m_sendinginfo.l_addresspos,
                            s.m_sendinginfo.l_lastantsep,
                            s.l_refno, 
                            s.m_sendinginfo.l_createdate,
                            s.m_sendinginfo.l_createtime,
                            s.m_sendinginfo.l_scheddate, 
                            s.m_sendinginfo.l_schedtime, 
                            s.m_sendinginfo.sz_sendingname,
                            s.m_sendinginfo.l_sendingstatus, 
                            s.m_sendinginfo.l_companypk,
                            s.m_sendinginfo.l_deptpk,
                            s.m_sendinginfo.l_nofax,
                            s.m_sendinginfo.l_group, 
                            s.m_sendinginfo.l_removedup, 
                            s.m_sendinginfo.l_type, 
                            s.m_sendinginfo.f_dynacall,
                            s.m_sendinginfo.l_addresstypes,
                            s.m_sendinginfo.l_userpk,
                            s.m_sendinginfo.l_maxchannels);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(s.l_refno, szSQL, e.Message);
                throw e;
            }
        }

        public bool InsertBBACTIONPROFILE(ref long l_refno, ref BBACTIONPROFILESEND ap)
        {
            String szSQL = String.Format("INSERT INTO BBACTIONPROFILESEND(l_actionprofilepk, l_refno) VALUES({0}, {1})",
                                         ap.l_actionprofilepk, l_refno);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
        }

        public bool InsertBBACTIONPROFILE(ref PAS_SENDING s)
        {
            try
            {
                return InsertBBACTIONPROFILE(ref s.l_refno, ref s.m_profile);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool InsertBBDYNARESCHED(ref long l_refno, ref BBDYNARESCHED dyna)
        {
            String szSQL = String.Format("INSERT INTO BBDYNARESCHED(l_refno, l_retries, l_interval, l_canceltime, l_canceldate, l_pausetime, l_pauseinterval) " +
                                         "VALUES({0}, {1}, {2}, {3}, {4}, {5}, {6})",
                                         l_refno, dyna.l_retries, dyna.l_interval, dyna.l_canceltime,
                                         dyna.l_canceldate, dyna.l_pausetime, dyna.l_pauseinterval);
            try
            {
                ExecNonQuery(szSQL);
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
            return true;
        }
        public bool InsertBBDYNARESCHED(ref PAS_SENDING s)
        {
            try
            {
                return InsertBBDYNARESCHED(ref s.l_refno, ref s.m_dynaresched);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool SP_smsqref_bcp(ref SMS_SENDING s, ref ULOGONINFO logon)
        {
            String sql = "";
            try
            {
                int n_oadctype = 1;
                int n_msgclass = 1;
                int n_localsched = 1;
                //int n_priserver = logon.l_priserver;
                //int n_altserver = logon.l_altservers;
                int n_fromapplication = 13;
                String sz_tarifclass = "";
                String sz_stdcc = "";
                String n_scheddatetime = new UDATETIME(s.m_sendinginfo.l_scheddate.ToString(), s.m_sendinginfo.l_schedtime.ToString() + "00").ToString();
                if (n_scheddatetime.Equals("-1"))
                    n_scheddatetime = s.m_sendinginfo.l_createdate + s.m_sendinginfo.l_createtime;
                int n_priserver = 0, n_altserver = 0;
                try
                {
                    sql = String.Format("SELECT l_serverid FROM SMSSERVERS_X_DEPT WHERE l_deptpk={0} ORDER BY l_pri",
                                        s.m_sendinginfo.l_deptpk);
                    OdbcDataReader rs = ExecReader(sql, UmsDb.UREADER_AUTOCLOSE);
                    if (rs.Read())
                    {
                        n_priserver = rs.GetInt32(0);
                        if (rs.Read())
                        {
                            n_altserver = rs.GetInt32(0);
                        }
                    }
                    else
                        n_priserver = 2;
                    rs.Close();
                }
                catch (Exception e)
                {
                    n_priserver = 2;
                }
 


                sql = String.Format("sp_sms_ins_smsqref_bcp {0}, {1}, {2}, {3}, {4}, {5}," +
                                    "{6}, {7}, {8}, {9}, {10}, {11}, '{12}', '{13}', '{14}', {15}," +
                                    "{16}, {17}, '{18}', {19}, {20}, '{21}', {22}, {23}, {24}, {25}," +
                                    "{26}, {27}, {28}, '{29}', {30}, {31}",
                                            0, //0 projectpk
                                            s.l_refno, //1
                                            s.m_sendinginfo.l_companypk, //2
                                            s.m_sendinginfo.l_deptpk, //3
                                            n_oadctype, //4
                                            n_msgclass, //5
                                            logon.l_deptpri, //6
                                            n_localsched, //7
                                            s.n_expirytime_minutes, //8
                                            n_scheddatetime, //9
                    //s.m_sendinginfo.l_scheddate, //9
                    //s.m_sendinginfo.l_schedtime, //9
                                            n_priserver, //10
                                            n_altserver, //11
                                            sz_tarifclass.Replace("'", "''"), //12
                                            s.sz_smsoadc.Replace("'", "''"), //13
                                            s.m_sendinginfo.sz_sendingname.Replace("'", "''"), //14
                                            (s.getSimulation() ? 1 : 0), //15
                                            0,//parent refno //16
                                            0,//expected items //17
                                            s.sz_smsmessage.Replace("'", "''"), //18
                                            n_fromapplication, //19
                                            s.m_sendinginfo.l_group, //20
                                            s.m_sendinginfo.sz_sepused.Replace("'", "''"), //21
                                            s.m_sendinginfo.l_lastantsep, //22
                                            s.m_sendinginfo.l_addresspos, //23
                                            s.m_sendinginfo.l_createdate, //24
                                            s.m_sendinginfo.l_createtime, //25
                                            s.m_sendinginfo.l_userpk, //26
                                            s.m_sendinginfo.l_nofax, //27
                                            s.m_sendinginfo.l_removedup, //28
                                            sz_stdcc.Replace("'", "''"), //29
                                            s.m_sendinginfo.l_addresstypes, //30
                                            s.m_sendinginfo.f_dynacall); //31
                return ExecNonQuery(sql);
            }
            catch (Exception e)
            {
                ULog.error(s.l_refno, sql, e.Message);
                throw e;
            }
        }

        public bool Send(ref SMS_SENDING s, ref ULOGONINFO logon)
        {
            try
            {
                //PAS_SENDING p = (PAS_SENDING)s;
                PAS_SENDING p = s;
                InsertBBVALID(ref p);
                //InsertMDVSENDINGINFO(ref p);
                SP_smsqref_bcp(ref s, ref logon);
                
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public bool Send(ref PAS_SENDING s)
        {
            try
            {
                InsertBBVALID(ref s);
                InsertBBSENDNUM(ref s);
                InsertMDVSENDINGINFO(ref s);
                InsertBBACTIONPROFILE(ref s);
                InsertBBDYNARESCHED(ref s);
                return true;
            }
            catch (Exception e)
            {
                //cleanup
                throw e;
            }

        }

        public List<int> GetOperatorsForSend(long l_alertpk, long l_deptpk)
        {
            List<int> operators = new List<int>();
            try
            {
                String szSQL = "";
                int n_operator = 0;
                int n_status = -2;
                if (l_alertpk < 0) //ad-hoc sending, select all operators
                    szSQL = String.Format("SELECT isnull(OP.l_operator,-1), l_status=0 FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT XD WHERE XD.l_deptpk={0} AND XD.l_operator=OP.l_operator", l_deptpk);
                else //sending from alert, select only prepared operators
                    szSQL = String.Format("select DISTINCT isnull(PA.l_operator,-1), isnull(PA.l_status,-2) from PAALERT_LBA PA, LBAOPERATORS OP WHERE PA.l_alertpk={0} and PA.l_operator=OP.l_operator", l_alertpk);

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    n_operator = rs.GetInt32(0);
                    n_status = rs.GetInt32(1);
                    if(n_operator > 0 && n_status == 0)
                        operators.Add(n_operator);
                }
                rs.Close();
            }
            catch (Exception)
            {
            }
            return operators;
        }

        public bool InsertLBARecord_2_0(long l_alertpk, long l_refno, int l_status, int l_response, int l_items,
                                    int l_proc, int l_retries, int l_requesttype,
                                    String sz_jobid, String sz_areaid, int n_function/*live or simulate*/, ref List<Int32> operatorfilter, long l_deptpk) //sending.l_refno, 3, -1, -1, -1, 0, 1, '', pa.sz_areaid)
        {
            //select all operators from db, then insert one record pr operator
            String szSQL = "";
            try
            {
                /*int n_operator = 0;
                int n_status = -2;
                if (l_alertpk < 0) //ad-hoc sending, select all operators
                    szSQL = "SELECT isnull(l_operator,-1), l_status=0 FROM LBAOPERATORS";
                else //sending from alert, select only prepared operators
                    szSQL = String.Format("select DISTINCT isnull(PA.l_operator,-1), isnull(PA.l_status,-2) from PAALERT_LBA PA, LBAOPERATORS OP WHERE PA.l_alertpk={0} and PA.l_operator=OP.l_operator", l_alertpk);

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    n_operator = rs.GetInt32(0);
                    n_status = rs.GetInt32(1);
                    if (n_operator > 0 && l_status == 0)
                    {*/
                List<int> operators = GetOperatorsForSend(l_alertpk, l_deptpk);
                for (int i = 0; i < operators.Count; i++)
                {
                    szSQL = String.Format("INSERT INTO LBASEND(l_refno, l_status, l_response, l_items, l_proc, l_retries, " +
                                             "l_requesttype, sz_jobid, sz_areaid, f_simulate, l_operator) VALUES({0}, {1}, {2}, {3}, {4}, {5}, " +
                                             "{6}, '{7}', '{8}', {9}, {10})",
                                             l_refno, l_status, l_response, l_items, l_proc, l_retries, l_requesttype,
                                             sz_jobid, sz_areaid, n_function, operators[i]);
                    ExecNonQuery(szSQL);
                }
                    /*}
                }
                rs.Close();*/
                return true;
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
        }

        public bool InsertLBARecord(long l_refno, int l_status, int l_response, int l_items,
                                    int l_proc, int l_retries, int l_requesttype,
                                    String sz_jobid, String sz_areaid, int n_function/*live or simulate*/) //sending.l_refno, 3, -1, -1, -1, 0, 1, '', pa.sz_areaid)
        {
            String szSQL = String.Format("INSERT INTO LBASEND(l_refno, l_status, l_response, l_items, l_proc, l_retries, " +
                                         "l_requesttype, sz_jobid, sz_areaid, f_simulate) VALUES({0}, {1}, {2}, {3}, {4}, {5}, " +
                                         "{6}, '{7}', '{8}', {9})",
                                         l_refno, l_status, l_response, l_items, l_proc, l_retries, l_requesttype,
                                         sz_jobid, sz_areaid, n_function);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(l_refno, szSQL, e.Message);
                throw e;
            }
        }
        public bool ResetLBAArea(String l_alertpk)
        {
            String szSQL = String.Format("UPDATE PAALERT SET sz_areaid='0' WHERE l_alertpk={0}", l_alertpk);
            try
            {
                ExecNonQuery(szSQL);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(0, szSQL, e.Message);
                throw e;
            }
        }

        public int GetAlertAddresstypes(String l_alertpk)
        {
            int n_ret = 0;
           String szSQL = String.Format("SELECT l_addresstypes FROM PAALERT WHERE l_alertpk={0}", l_alertpk.Substring(1));
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                    n_ret = rs.GetInt32(0);
                rs.Close();
                return n_ret;
            }
            catch (Exception e)
            {
                ULog.error(0, szSQL, e.Message);
                throw e;
            }
        }
        public String GetAlertAreaID(String l_pk)
        {
            String ret = "";
            String szSQL = String.Format("SELECT isnull(sz_areaid,'-1') FROM PAALERT WHERE l_alertpk={0}", l_pk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    ret = rs.GetString(0);
                }
                rs.Close();
                return ret;
            }
            catch (Exception e)
            {
                ULog.error(0, szSQL, e.Message);
                throw e;
            }
        }

        public bool GetLbaSendsByRefno(int n_refno, ref List<ULBASENDING> list)
        {
            bool b = false;
            try
            {
                String szSQL = String.Format("SELECT l_refno, l_status, l_response, l_items, l_proc, l_retries, l_requesttype, sz_jobid, sz_areaid, f_simulate, l_operator FROM LBASEND WHERE l_refno={0}", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    ULBASENDING s = new ULBASENDING();
                    s.l_refno = rs.GetInt32(0);
                    s.l_status = rs.GetInt32(1);
                    s.l_response = rs.GetInt32(2);
                    s.l_items = rs.GetInt32(3);
                    s.l_proc = rs.GetInt32(4);
                    s.l_retries = rs.GetInt32(5);
                    s.l_requesttype = rs.GetInt32(6);
                    s.sz_jobid = rs.GetString(7);
                    s.sz_areaid = rs.GetString(8);
                    s.f_simulation = rs.GetInt32(9);
                    s.l_operator = rs.GetInt32(10);
                    list.Add(s);
                }
                rs.Close();
                if (list.Count > 0)
                    b = true;
                return b;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public bool VerifyJobIDAndRefno_2_0(int n_refno, String sz_jobid, int l_operator)
        {
            bool b = false;
            try
            {
                String szSQL = String.Format("SELECT sz_jobid FROM LBASEND WHERE l_refno={0} AND l_operator={1} AND l_status=310", n_refno, l_operator);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    String job = rs.GetString(0);
                    if (job.Equals(sz_jobid))
                        b = true;
                }
                rs.Close();
                return b;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool VerifyJobIDAndRefno(int n_refno, String sz_jobid)
        {
            bool b = false;
            try
            {
                String szSQL = String.Format("SELECT sz_jobid FROM LBASEND WHERE l_refno={0}", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    String job = rs.GetString(0);
                    if (job.Equals(sz_jobid))
                        b = true;
                }
                rs.Close();
                return b;
            }
            catch (Exception e)
            {
                throw e;
            }
        }


        public bool SetLBAStatus(long n_refno, int n_status)
        {
            try
            {
                String szSQL = String.Format("UPDATE LBASEND SET l_status={0} WHERE l_refno={1}", n_status, n_refno);
                if (ExecNonQuery(szSQL))
                {
                    return true;
                }
                return false;
            }
            catch (Exception e)
            {
                throw e;
            }

        }

        public bool SetLBAStatus(long n_refno, int n_status, int where_status_is, int n_operator)
        {
            try
            {
                String szSQL = String.Format("UPDATE LBASEND SET l_status={0} WHERE l_refno={1} AND l_status={2} AND l_operator={3}", n_status, n_refno, where_status_is, n_operator);
                if (ExecNonQuery(szSQL))
                {
                    return true;
                }
                return false;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public List<ULBASENDING> GetLBAOperatorsReadyForConfirmCancel(int l_refno)
        {
            try
            {
                List<ULBASENDING> operators = new List<ULBASENDING>();
                String szSQL = String.Format("SELECT LS.l_operator, LS.sz_jobid, LS.sz_operatorname FROM LBASEND LS, LBAOPERATORS OP WHERE LS.l_status=310 AND LS.l_refno={0} AND LS.l_operator=OP.l_operator", l_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    int op = rs.GetInt32(0);
                    String job = rs.GetString(1);
                    String sz_operator = rs.GetString(2);
                    if (op >= 1)
                    {
                        ULBASENDING lba = new ULBASENDING();
                        lba.l_operator = op;
                        lba.sz_jobid = job;
                        lba.sz_operator = sz_operator;
                        operators.Add(lba);
                    }
                }
                rs.Close();
                if (operators.Count <= 0)
                    throw new ULBANoOperatorsReadyForConfirmCancel();
                return operators;
            }
            catch (Exception e)
            {
                throw e;
            }

        }
        public bool GetIsSimulation(int n_refno)
        {
            try
            {
                bool b = true;
                String szSQL = String.Format("SELECT f_simulate FROM LBASEND WHERE l_refno={0}", n_refno);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    int n = rs.GetInt32(0);
                    if (n == 0)
                        b = false;
                    else
                        b = true;
                }
                rs.Close();
                return b;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool GetTTSParams(int n_langpk, ref UTTS_DB_PARAMS p)
        {
            bool b_ret = false;
            try
            {
                String szSQL = String.Format("SELECT sz_speaker, sz_modename, sz_manufacturer FROM BBTTSLANG WHERE l_langpk={0}", n_langpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    p.sz_speaker = rs.GetString(0);
                    p.sz_modename = rs.GetString(1);
                    p.sz_manufacturer = rs.GetString(2);
                    b_ret = true;
                }
                rs.Close();
                return b_ret;

            }
            catch(Exception e)
            {
                ULog.error(e.Message);
                return false;
            }
        }
    }
}