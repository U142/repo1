using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Data.Odbc;

using com.ums.UmsCommon;
using com.ums.UmsDbLib;

namespace com.ums.PAS.Settings
{
    public class UCBSettings : UmsDb
    {
        public UCBSettings(ref ULOGONINFO logon) : base()
        {
            try
            {
                base.CheckLogon(ref logon, true);
            }
            catch (Exception)
            {
                throw new ULogonFailedException();
            }
        }

        public bool updateLBAParameter(ULOGONINFO logon, ULBAPARAMETER param)
        {

            try
            {
                String szSQL;
                szSQL = String.Format("sp_cb_upd_LBAPARAMETER {0},{1},{2},'{3}',{4},{5},{6},{7},{8},{9},{10},{11},{12},{13},{14},{15}", logon.l_userpk, param.l_incorrect, param.l_autologoff, param.sz_adminemail.Replace("'","''"),
                    param.l_channelno, param.l_test_channelno, param.l_heartbeat, param.l_duration, param.l_interval, param.l_repetition, param.l_deptpk, param.l_comppk, param.l_pagesize, param.l_maxpages, param.l_second_channelno, param.b_second_channel_active);

                return ExecNonQuery(szSQL);

            }
            catch (Exception)
            {
                throw;
            }
        }

        public ULBAPARAMETER getLBAParameter(ULOGONINFO logon)
        {
            OdbcDataReader odr = null;

            try
            {
                ULBAPARAMETER param = new ULBAPARAMETER();

                String szSQL;
                szSQL = String.Format("sp_cb_get_LBAPARAMETER {0},{1}", logon.l_deptpk, logon.l_comppk);

                odr = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);

                if (odr.HasRows)
                {
                    odr.Read();
                    param.l_incorrect = odr.GetInt32(1);
                    param.l_autologoff = odr.GetInt32(2);
                    param.sz_adminemail = odr.GetString(3);
                    param.l_channelno = odr.GetInt32(4);
                    param.l_second_channelno = odr.GetInt32(odr.GetOrdinal("l_second_channel"));
                    param.b_second_channel_active = odr.GetBoolean(odr.GetOrdinal("b_second_channel_active"));
                    param.l_test_channelno = odr.GetInt32(5);
                    param.l_heartbeat = odr.GetInt32(6);
                    param.l_duration = odr.GetInt32(7);
                    param.l_interval = odr.GetInt32(8);
                    param.l_repetition = odr.GetInt32(9);
                    param.l_pagesize = odr.GetInt32(odr.GetOrdinal("l_pagesize"));
                    param.l_maxpages = odr.GetInt32(odr.GetOrdinal("l_maxpages"));
                    param.l_deptpk = logon.l_deptpk;
                    param.l_comppk = logon.l_comppk;
                }
                //odr.Close();

                return param;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if(odr!=null && !odr.IsClosed)
                    odr.Close();
            }

        }
    }
}
