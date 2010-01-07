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

        public UBBMESSAGE InsertMessage(ref UBBMESSAGE msg)
        {
            try
            {
                String szSQL = String.Format("sp_ins_messages_tree {0}, {1}, '{2}', '{3}', {4}, {5}, '{6}', {7}, {8}, {9}, {10}",
                                    msg.n_deptpk,
                                    msg.n_type,
                                    msg.sz_name,
                                    msg.sz_description,
                                    msg.n_langpk,
                                    msg.f_template,
                                    msg.sz_filename,
                                    msg.n_ivrcode,
                                    msg.n_categorypk,
                                    msg.n_parentpk,
                                    msg.n_messagepk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                    msg.n_messagepk = rs.GetInt64(0);
                else
                    msg.n_messagepk = -1;

                rs.Close();

                UFile file = new UFile(UCommon.UPATHS.sz_path_bbmessages + "\\" + msg.n_deptpk + "\\", msg.n_messagepk.ToString() + ".txt");
                StreamWriter sw = new StreamWriter(file.full(), false, Encoding.GetEncoding("iso-8859-1"));
                sw.Write(msg.sz_message);
                sw.Close();               
                return msg;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public UBBMESSAGELIST GetMessageList(ref ULOGONINFO logon, UBBMESSAGELISTFILTER filter)
        {
            try
            {
                
                UBBMESSAGELIST ret = new UBBMESSAGELIST();
                ret.n_servertimestamp = base.getDbClock();
                ret.list = new List<UBBMESSAGE>();
                String szSQL = String.Format("SELECT l_deptpk, isnull(l_type,0), sz_name, sz_description, l_messagepk, isnull(l_langpk,0), isnull(sz_number,''), isnull(f_template,0), isnull(sz_filename,''), isnull(l_ivrcode,-1), isnull(l_parentpk,-1), isnull(l_depth,0), isnull(l_timestamp,0), isnull(l_categorypk,-1) " +
                                            "FROM BBMESSAGES " +
                                            "WHERE l_deptpk={0} AND l_timestamp>={1} " +
                                            "ORDER BY l_depth",
                                            logon.l_deptpk, filter.n_timefilter);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    UBBMESSAGE msg = new UBBMESSAGE();
                    msg.n_deptpk = rs.GetInt32(0);
                    msg.n_type = (UBBMODULEDEF)rs.GetInt32(1);
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
                    msg.n_categorypk = rs.GetInt64(13);
                    ret.list.Add(msg);
                }
                rs.Close();
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }

        }
    }
}
