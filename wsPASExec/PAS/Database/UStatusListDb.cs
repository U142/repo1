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

using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.PAS.Status;
using System.Data.Odbc;
using com.ums.UmsParm;


namespace com.ums.PAS.Database
{
    public class UStatusListDb : PASUmsDb
    {
        public UStatusListDb(UmsDbConnParams conn) 
            : base(conn.sz_dsn, conn.sz_uid, conn.sz_pwd, 60)
        {

        }
        /*
	sprintf(szSQL, "SELECT isnull(head.l_type,0) l_sendingtype, isnull(head.l_items, -1) l_totitem, "
					"l_altjmp=0, isnull(info.l_refno, -1) l_refno, "
					"isnull(info.l_createdate, -1) l_createdate, isnull(info.l_createtime, -1) l_createtime, "
					"info.sz_sendingname, isnull(info.l_sendingstatus, -1) l_sendingstatus, info.sz_groups, "
					"isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, isnull(dept.l_deptpk, -1), "
					"dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), "
					"isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) "
					"FROM MDVSENDINGINFO info, BBQREF head, "
					"BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE %s AND info.l_refno%s=projx.l_refno AND projx.l_projectpk%s=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno "
					"AND info.l_group in (2,3,4,8) ORDER BY info.l_refno DESC",
					sz_filter1, sz_filter_outerjoin, sz_filter_outerjoin);*/
        
        public UStatusListResults GetStatusList(ref ULOGONINFO logon)
        {
            if (!CheckLogon(ref logon))
            {
                throw new ULogonFailedException();
            }

            UStatusListResults res = new UStatusListResults();
            String szSQL = String.Format(
                "SELECT isnull(head.l_type, 0) l_sendingtype, isnull(head.l_items, -1) l_totitem, " +
                "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(info.l_sendingstatus,1) l_sendingstatus, " +
                "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                "FROM MDVSENDINGINFO info, BBQREF head, " +
                "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE dept.l_comppk={0} AND info.l_type=1 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno " +
                "AND info.l_group in (2,3,4,8,9) " +
                "UNION " +
                "SELECT l_sendingtype=2, isnull(head.l_items, -1) l_totitem, " +
                "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(info.l_sendingstatus,1) l_sendingstatus, " +
                "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                "FROM MDVSENDINGINFO info, SMSQREF head, " +
                "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE dept.l_comppk={0} AND info.l_type=2 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno " +
                "AND info.l_group in (2,3,4,8,9) " +
                "ORDER BY info.l_refno DESC",
                logon.l_comppk);
            try
            {
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    try
                    {
                        UStatusListItem item = new UStatusListItem();
                        item.n_sendingtype = rs.GetInt32(0);
                        item.n_totitem = rs.GetInt32(1);
                        item.n_altjmp = rs.GetInt32(2);
                        item.n_refno = rs.GetInt32(3);
                        item.n_createdate = rs.GetInt32(4);
                        item.n_createtime = rs.GetInt32(5);
                        item.sz_sendingname = rs.GetString(6);
                        item.n_sendingstatus = rs.GetInt32(7);
                        item.sz_groups = rs.GetString(8);
                        item.n_group = rs.GetInt32(9);
                        item.n_type = rs.GetInt32(10);
                        item.n_deptpk = rs.GetInt32(11);
                        item.sz_deptid = rs.GetString(12);
                        item.n_projectpk = rs.GetInt64(13);
                        item.sz_projectname = rs.GetString(14);
                        item.n_createtimestamp = rs.GetInt64(15);
                        item.n_updatetimestamp = rs.GetInt64(16);

                        res.addLine(ref item);
                    }
                    catch (Exception e)
                    {
                        ULog.error(0, szSQL, e.Message);
                    }
                }
            }
            catch (Exception e)
            {
                throw e;
            }
            res.finalize();
            return res;
        }
    }
}
