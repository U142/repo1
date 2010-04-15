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
                base.CheckLogon(ref logon);
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
                                            "WHERE l_deptpk={0} AND isnull(l_timestamp,0)>={1} AND f_template=1 " +
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
                    msg.n_timestamp = rs.GetInt64(12);
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
                    msg.ccmessage = new List<UCCMessage>();
                    szSQL = String.Format("SELECT l_cc, sz_text " +
                                            "FROM BBMESSAGE_SMSCONTENT " +
                                            "WHERE l_messagepk={0} " +
                                            "ORDER BY l_cc",
                                            msg.n_messagepk);
                    OdbcDataReader rsCC = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rsCC.Read())
                    {
                        UCCMessage ccm = new UCCMessage();
                        ccm.l_cc = rsCC.GetInt32(0);
                        ccm.sz_message = rsCC.GetString(1);
                        msg.ccmessage.Add(ccm);
                    }
                    rsCC.Close();
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
    }
}
