using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Data.Odbc;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsFile;

namespace com.ums.PAS.messagelib
{
    public class UMessageLib : UmsDb
    {
        public UMessageLib(ref ULOGONINFO logon)
            : base()
        {
            try
            {
                base.CheckLogon(ref logon, true);
            }
            catch (Exception e)
            {
                throw new ULogonFailedException();
            }
        }

        public UBBMESSAGE DeleteMessage(ref ULOGONINFO logon, ref UBBMESSAGE msg)
        {
            try
            {
                String szSQL;
                szSQL = String.Format("sp_log_BBMESSAGES {0}, {1}, {2}, {3}",
                                    logon.l_userpk, logon.l_comppk, (int)UDbOperation.DELETE,//(int)Enum.Parse(typeof(UDbOperation), UDbOperation.DELETE.ToString()),
                                    msg.n_messagepk);
                if (!ExecNonQuery(szSQL))
                {
                    msg.b_valid = false;
                    return msg;
                }

                szSQL = String.Format("DELETE FROM BBMESSAGES WHERE l_messagepk={0} AND l_deptpk={1}",
                                        msg.n_messagepk, msg.n_deptpk);
                if (ExecNonQuery(szSQL))
                {
                    msg.b_valid = true;
                }
                else
                {
                    msg.b_valid = false;
                    return msg;
                }
                UFile filetxt = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk.ToString() + ".txt");
                if(File.Exists(filetxt.full()))
                    File.Delete(filetxt.full());
                UFile filewav = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk.ToString() + ".wav");
                if(File.Exists(filewav.full()))
                    File.Delete(filewav.full());
                return msg;
            }
            catch (Exception e)
            {
                msg.n_messagepk = -1;
                msg.b_valid = false;
                throw e;
            }
        }

        public UBBMESSAGE InsertMessage(ref UBBMESSAGE msg)
        {
            try
            {
                String langpk = msg.n_langpk.ToString();
                if (msg.n_langpk < 0)
                    langpk = "NULL";
                String szSQL = String.Format("sp_ins_messages_tree {0}, {1}, '{2}', '{3}', {4}, {5}, '{6}', {7}, {8}, {9}, {10}",
                                    msg.n_deptpk,
                                    (int)Enum.Parse(typeof(UBBMODULEDEF), msg.n_type.ToString()),
                                    msg.sz_name,
                                    msg.sz_description,
                                    langpk,
                                    msg.f_template,
                                    msg.sz_filename,
                                    msg.n_ivrcode,
                                    msg.n_categorypk,
                                    msg.n_parentpk,
                                    msg.n_messagepk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    msg.n_messagepk = rs.GetInt64(0);
                    msg.n_timestamp = rs.GetInt64(1);
                    // Delete existing messages
                    szSQL = String.Format("sp_prep_message_smscontent {0}", msg.n_messagepk);
                    ExecNonQuery(szSQL);

                    for (int i = 0; i < msg.ccmessage.Count; ++i)
                    {
                        szSQL = String.Format("sp_ins_message_smscontent {0}, {1}, '{2}'", msg.n_messagepk, msg.ccmessage[i].l_cc, msg.ccmessage[i].sz_message);
                        ExecNonQuery(szSQL);
                    }

                }
                else
                {
                    msg.n_messagepk = -1;
                    msg.b_valid = false;
                }

                rs.Close();

                if (!Directory.Exists(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\"))
                    Directory.CreateDirectory(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\");

                UFile file_tmp = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk.ToString() + ".tmp");
                UFile file = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk.ToString() + ".txt");
                StreamWriter sw = new StreamWriter(file_tmp.full(), false, Encoding.GetEncoding("iso-8859-1"));
                sw.Write(msg.sz_message);
                sw.Close();
                file_tmp.MoveOperation(file, true);
                return msg;
            }
            catch (Exception e)
            {
                msg.n_messagepk = -1;
                msg.b_valid = false;
                throw e;
            }
        }

        /**
         * If f_template=1, it's either a WAV file if l_langpk>0 and texttemplate if l_langpk==NULL
         * 
         */
        public UBBMESSAGELIST GetMessageList(ref ULOGONINFO logon, UBBMESSAGELISTFILTER filter)
        {
            try
            {
                UBBMESSAGELIST ret = new UBBMESSAGELIST();
                ret.n_servertimestamp = base.getDbClock();
                ret.list = new List<UBBMESSAGE>();
                String szSQL = String.Format("SELECT l_deptpk, isnull(l_type,0), sz_name, sz_description, l_messagepk, isnull(l_langpk,-1), isnull(sz_number,''), isnull(f_template,0), isnull(sz_filename,''), isnull(l_ivrcode,-1), isnull(l_parentpk,-1), isnull(l_depth,0), isnull(l_timestamp,0), isnull(l_categorypk,-1) " +
                                            "FROM BBMESSAGES " +
                                            "WHERE l_deptpk IN (-1,{0}) AND isnull(l_timestamp,0)>={1} AND f_template=1 " +
                                            "ORDER BY l_depth",
                                            logon.l_deptpk, filter.n_timefilter);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    UBBMESSAGE msg = new UBBMESSAGE();
                    msg.n_deptpk = rs.GetInt32(0);
                    try
                    {
                        int type = rs.GetInt32(1);
                        if (type <= 0)
                            msg.n_type = UBBMODULEDEF.DIALOGUE;
                        else
                            msg.n_type = (UBBMODULEDEF)type;
                        
                    }
                    catch (Exception)
                    {
                        msg.n_type = UBBMODULEDEF.DIALOGUE;
                    }
                    msg.sz_name = rs.GetString(2);
                    msg.sz_description = rs.GetString(3);
                    msg.n_messagepk = rs.GetInt64(4);
                    msg.n_langpk = rs.GetInt32(5);
                    msg.sz_number = rs.GetString(6);
                    msg.f_template = rs.GetInt32(7);
                    msg.sz_filename = rs.GetString(8);
                    msg.n_ivrcode = rs.GetInt32(9);
                    msg.n_parentpk = rs.GetInt64(10);
                    msg.n_depth = rs.GetInt32(11);
                    try
                    {
                        msg.n_timestamp = rs.GetInt64(12);
                    }
                    catch (Exception e)
                    {
                        msg.n_timestamp = 0;
                    }
                    try
                    {
                        msg.n_categorypk = rs.GetInt64(13);
                    }
                    catch (Exception e)
                    {
                        msg.n_categorypk = 0;
                    }
                    msg.b_valid = false;
                    if (msg.f_template == 1 || msg.n_langpk>=0) //Text template or TTS, there should exist a txt-file
                    {
                        try
                        {
                            UFile f = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk + ".txt");
                            if (File.Exists(f.full()))
                            {
                                msg.sz_message = File.ReadAllText(f.full(), Encoding.GetEncoding("iso-8859-1"));
                                msg.b_valid = true;
                            }
                            else
                                msg.sz_message = "";
                        }
                        catch (Exception)
                        {
                            msg.sz_message = "";
                            msg.audiostream = null;
                        }
                    }
                    if (msg.f_template<=0) //WAV message, either TTS or rec/upload
                    {
                        try
                        {
                            UFile f = new UFile(UCommon.UPATHS.sz_path_predefined_messages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk + ".wav");
                            if(File.Exists(f.full()))
                            {
                                msg.audiostream = File.ReadAllBytes(f.full());
                                msg.b_valid = true;
                            }
                        }
                        catch (Exception)
                        {
                            msg.sz_message = "";
                            msg.audiostream = null;
                            msg.b_valid = false;
                        }
                    }
                    else
                    {
                        //NOT A VALID MESSAGE
                    }
                    // Gets messages for specific country codes
                    UmsDb db = new UmsDb();
                    msg.ccmessage = new List<UCCMessage>();
                    szSQL = String.Format("SELECT l_cc, sz_text " +
                                            "FROM BBMESSAGE_SMSCONTENT " +
                                            "WHERE l_messagepk={0} " +
                                            "ORDER BY l_cc",
                                            msg.n_messagepk);
                    OdbcDataReader rsCC = db.ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rsCC.Read())
                    {
                        UCCMessage ccm = new UCCMessage();
                        ccm.l_cc = rsCC.GetInt32(0);
                        ccm.sz_message = rsCC.GetString(1);
                        msg.ccmessage.Add(ccm);
                    }
                    rsCC.Close();
                    db.close();
                    ret.list.Add(msg);
                }
                rs.Close();

                //ALSO GET DELETED if this is an update
                ret.deleted = new List<UBBMESSAGE>();
                if(filter.n_timefilter>0)
                {
                    szSQL = String.Format("SELECT l_messagepk FROM log_BBMESSAGES WHERE log_l_timestamp>={0} AND l_deptpk={1}",
                                    filter.n_timefilter, logon.l_deptpk);
                    rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rs.Read())
                    {
                        UBBMESSAGE msg = new UBBMESSAGE();
                        msg.n_messagepk = rs.GetInt64(0);
                        msg.n_type = UBBMODULEDEF.DIALOGUE;
                        ret.deleted.Add(msg);
                    }
                    rs.Close();
                }

                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }

        }
        /*
         * 
         * Get messages for NLALERT
         * 
         */
        public ULBAMESSAGELIST GetLBAMessageList(ref ULOGONINFO logon, UBBMESSAGELISTFILTER filter)
        {
            try
            {
                ULBAMESSAGELIST ret = new ULBAMESSAGELIST();
                ret.n_servertimestamp = base.getDbClock();
                ret.list = new List<ULBAMESSAGE>();
                String szSQL = String.Format("SELECT l_deptpk, sz_description, sz_text, l_messagepk, isnull(l_langpk,-1), isnull(l_parentpk,-1), isnull(l_depth,0) as l_depth, isnull(l_timestamp,0) " +
                                            "FROM LBAMESSAGES " +
                                            "WHERE l_deptpk={0} AND isnull(l_timestamp,0)>={1} " +
                                            "ORDER BY l_depth, l_messagepk",
                                            logon.l_deptpk, filter.n_timefilter);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    ULBAMESSAGE msg = new ULBAMESSAGE();
                    msg.n_deptpk = rs.GetInt32(0);
                    
                    msg.sz_name = rs.GetString(1);
                    msg.sz_message = rs.GetString(2);
                    msg.n_messagepk = rs.GetInt64(3);
                    msg.n_langpk = rs.GetInt32(4);
                    msg.n_parentpk = rs.GetInt64(5);
                    msg.n_depth = rs.GetInt32(6);
                    try
                    {
                        msg.n_timestamp = rs.GetInt64(7);
                    }
                    catch (Exception e)
                    {
                        msg.n_timestamp = 0;
                    }

                    ret.list.Add(msg);
                }
                rs.Close();

                //ALSO GET DELETED if this is an update
                ret.deleted = new List<ULBAMESSAGE>();
                if (filter.n_timefilter > 0)
                {
                    szSQL = String.Format("SELECT l_messagepk FROM log_BBMESSAGES WHERE log_l_timestamp>={0} AND l_deptpk={1}",
                                    filter.n_timefilter, logon.l_deptpk);
                    rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rs.Read())
                    {
                        ULBAMESSAGE msg = new ULBAMESSAGE();
                        msg.n_messagepk = rs.GetInt64(0);
                        ret.deleted.Add(msg);
                    }
                    rs.Close();
                }

                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }

        }
        /*
         * 
         * Delete messages for NLALERT
         * 
         */
        public ULBAMESSAGE DeleteLBAMessage(ref ULOGONINFO logon, ref ULBAMESSAGE msg)
        {
            try
            {
                String szSQL;
                /*
                szSQL = String.Format("sp_log_BBMESSAGES {0}, {1}, {2}, {3}",
                                    logon.l_userpk, logon.l_comppk, (int)UDbOperation.DELETE,//(int)Enum.Parse(typeof(UDbOperation), UDbOperation.DELETE.ToString()),
                                    msg.n_messagepk);
                if (!ExecNonQuery(szSQL))
                {
                    msg.b_valid = false;
                    return msg;
                }
                */
                szSQL = String.Format("DELETE FROM LBAMESSAGES WHERE l_messagepk={0} AND l_deptpk={1}",
                                        msg.n_messagepk, msg.n_deptpk);
                if (!ExecNonQuery(szSQL))
                    msg.n_messagepk = -1;
                
                return msg;
            }
            catch (Exception e)
            {
                msg.n_messagepk = -1;
                throw e;
            }
        }
        /*
         * 
         * Delete messages for NLALERT
         * 
         */
        public ULBAMESSAGE InsertLBAMessage(ref ULOGONINFO logon, ref ULBAMESSAGE msg)
        {
            try
            {
                String langpk = msg.n_langpk.ToString();
                if (msg.n_langpk < 0)
                    langpk = "NULL";
                String szSQL = String.Format("sp_ins_lbamessages_tree {0}, '{1}', '{2}', {3}, {4}, {5}",
                                    msg.n_deptpk,
                                    msg.sz_name,
                                    msg.sz_message,
                                    langpk,
                                    msg.n_parentpk,
                                    msg.n_messagepk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    msg.n_messagepk = rs.GetInt64(0);
                    msg.n_timestamp = rs.GetInt64(1);
                    // Delete existing messages
                    //szSQL = String.Format("sp_prep_message_smscontent {0}", msg.n_messagepk);
                    //ExecNonQuery(szSQL);
                }
                else
                {
                    msg.n_messagepk = -1;
                }

                rs.Close();

                return msg;
            }
            catch (Exception e)
            {
                msg.n_messagepk = -1;
                throw e;
            }
        }
        /*
         * 
         * Insert duration for NLALERT
         * 
         */
        public ULBADURATION InsertLBADuration(ref ULOGONINFO logon, ref ULBADURATION duration)
        {
            try
            {

                String szSQL = String.Format("sp_ins_lbaduration {0}, {1}, {2}, {3}, {4}",
                                    duration.n_deptpk,
                                    duration.n_duration,
                                    duration.n_interval,
                                    duration.n_repetition,
                                    duration.n_durationpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    duration.n_durationpk = rs.GetInt64(0);
                }
                else
                {
                    duration.n_durationpk = -1;
                }

                rs.Close();

                return duration;
            }
            catch (Exception e)
            {
                duration.n_durationpk = -1;
                throw e;
            }
        }
        /*
         * 
         * get duration list for NLALERT
         * 
         */
        public List<ULBADURATION> GetLBADuration(ref ULOGONINFO logon)
        {
            try
            {

                String szSQL = String.Format("SELECT l_durationpk, l_duration, l_interval, l_repetition, l_deptpk FROM LBADURATION WHERE l_deptpk = {0}",
                                    logon.l_deptpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);

                List<ULBADURATION> dl = new List<ULBADURATION>();
                ULBADURATION d;
                while (rs.Read())
                {
                    d = new ULBADURATION();
                    d.n_durationpk = rs.GetInt64(0);
                    d.n_duration = rs.GetInt32(1);
                    d.n_interval = rs.GetInt32(2);
                    d.n_repetition = rs.GetInt32(3);
                    d.n_deptpk = rs.GetInt32(4);
                    dl.Add(d);
                }
                
                rs.Close();

                return dl;
            }
            catch (Exception e)
            {
                return new List<ULBADURATION>();
                throw e;
            }
        }
        /*
         * 
         * Delete duration for NLALERT
         * 
         */
        public ULBADURATION DeleteLBADuration(ref ULOGONINFO logon, ref ULBADURATION duration)
        {
            try
            {
                String szSQL;
                /*
                szSQL = String.Format("sp_log_BBMESSAGES {0}, {1}, {2}, {3}",
                                    logon.l_userpk, logon.l_comppk, (int)UDbOperation.DELETE,//(int)Enum.Parse(typeof(UDbOperation), UDbOperation.DELETE.ToString()),
                                    msg.n_messagepk);
                if (!ExecNonQuery(szSQL))
                {
                    msg.b_valid = false;
                    return msg;
                }
                */
                szSQL = String.Format("DELETE FROM LBADURATION WHERE l_durationpk={0} AND l_deptpk={1}",
                                        duration.n_durationpk, duration.n_deptpk);
                if (!ExecNonQuery(szSQL))
                    duration.n_durationpk = -1;

                return duration;
            }
            catch (Exception e)
            {
                duration.n_durationpk = -1;
                throw e;
            }
        }
    }
}
