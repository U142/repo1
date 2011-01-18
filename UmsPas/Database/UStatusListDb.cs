using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
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
        
        public UStatusListResults GetStatusList(ref ULOGONINFO logon, UDATAFILTER filter_by)
        {
            OdbcDataReader rsType = null;
            OdbcDataReader rsDepts = null;
            OdbcDataReader rs = null;
            try
            {
                if (!CheckLogon(ref logon, true))
                {
                    throw new ULogonFailedException();
                }
            }
            catch (Exception e)
            {
                throw e;
            }

            //deptfilter
            // WHERE AND dept.l_deptpk=BUXD.l_deptpk AND BUXD.l_userpk=logon.l_userpk
            String szDeptList = "";
            String szDeptListSQL = String.Format("SELECT DISTINCT isnull(BD.l_deptpk,0) FROM BBDEPARTMENT BD, BBUSERPROFILE_X_DEPT BUXD, BBUSERPROFILE BUP WHERE BD.l_deptpk=BUXD.l_deptpk AND BUXD.l_userpk={0} AND BUXD.l_profilepk=BUP.l_profilepk AND BUP.l_status>=1", logon.l_userpk);
            rsDepts = ExecReader(szDeptListSQL, UmsDb.UREADER_KEEPOPEN);
            while (rsDepts.Read())
            {
                String l_deptpk = rsDepts.GetString(0);
                if (szDeptList.Length > 0)
                    szDeptList += ",";
                szDeptList += l_deptpk;
            }
            rsDepts.Close();
            if (szDeptList.Length == 0)
            {
                throw new UNoStatusReadRightsException("No Department/Userprofile link provides status read");
            }

            int n_pas_type = 1;

            String szDeptType = String.Format("SELECT DM.l_pas FROM BBDEPARTMENTMODS DM, BBUSERPROFILE_X_DEPT BUXD, BBUSERPROFILE BUP WHERE DM.l_deptpk={0} AND DM.l_deptpk=BUXD.l_deptpk AND BUXD.l_userpk={1} AND BUXD.l_profilepk=BUP.l_profilepk AND BUP.l_status>=1", logon.l_deptpk, logon.l_userpk);
            rsType = ExecReader(szDeptType, UmsDb.UREADER_KEEPOPEN);
            if (rsType.Read())
            {
                n_pas_type = rsType.GetInt32(0);
            }
            rsType.Close();


            UStatusListResults res = new UStatusListResults();
            String szSQL = "";
            switch (n_pas_type)
            {
                case 4:
                    /*szSQL = String.Format("SELECT distinct l_sendingtype=5, sum(isnull(head.l_items, 0)) l_totitem, " +
                     "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                     "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(head.l_status,1) l_sendingstatus, " +
                     //"isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), max(isnull(head.l_status,1)) l_sendingstatus, " +
                     "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                     "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                     "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                     "FROM MDVSENDINGINFO info, LBASEND head, " +
                     "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE info.l_type=5 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno=head.l_refno " +
                     "AND info.l_group=5 AND dept.l_deptpk={0} " +
                     //"GROUP BY head.l_refno, info.l_refno, dept.l_deptpk, projx.l_refno, proj.l_projectpk "+
                     //"GROUP BY info.l_deptpk, dept.l_deptpk, dept.sz_deptid, proj.l_projectpk, projx.l_projectpk, projx.l_refno, info.l_refno, head.l_status " +
                     "GROUP BY head.l_refno, info.l_refno, dept.l_deptpk, projx.l_refno, proj.l_projectpk, info.l_createdate, info.l_createtime,info.sz_sendingname, info.sz_groups, info.l_group, info.l_type, dept.sz_deptid, proj.sz_name,proj.l_createtimestamp, proj.l_updatetimestamp " +
                     "ORDER BY info.l_refno DESC",
                     logon.l_deptpk);*/
                    szSQL = String.Format(
                        "SELECT * FROM v_StatusListTAS " +
                        "WHERE l_deptpk={0} "+
                        "ORDER BY l_refno DESC",
                        logon.l_deptpk);
                    break;
                default:
                    /*szSQL = String.Format(
                    "SELECT isnull(head.l_type, 0) l_sendingtype, isnull(head.l_items, -1) l_totitem, " +
                    "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                    "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(info.l_sendingstatus,1) l_sendingstatus, " +
                    "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                    "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                    "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                    "FROM MDVSENDINGINFO info, BBQREF head, " +
                    "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE info.l_type=1 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno " +
                    "AND info.l_group>=2 AND dept.l_deptpk in ({0}) " +
                    "UNION " +
                    "SELECT l_sendingtype=2, isnull(head.l_items, -1) l_totitem, " +
                    "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                    "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(info.l_sendingstatus,1) l_sendingstatus, " +
                    "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                    "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                    "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                    "FROM MDVSENDINGINFO info, SMSQREF head, " +
                    "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE info.l_type=2 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno " +
                    "AND info.l_group>=2 AND dept.l_deptpk in ({0}) " +
                    "UNION " +
                    "SELECT l_sendingtype=4, sum(isnull(head.l_items, 0)) l_totitem, " +
                    "l_altjmp=0, isnull(info.l_refno, -1) l_refno, isnull(info.l_createdate, -1) l_createdate, " +
                    "isnull(info.l_createtime, -1) l_createtime, isnull(info.sz_sendingname,' '), isnull(head.l_status,1) l_sendingstatus, " +
                    "isnull(info.sz_groups,' '), isnull(info.l_group, -1) l_group, isnull(info.l_type, -1) l_type, " +
                    "isnull(dept.l_deptpk, -1), dept.sz_deptid, isnull(proj.l_projectpk, -1), isnull(proj.sz_name, ' '), " +
                    "isnull(proj.l_createtimestamp, -1), isnull(proj.l_updatetimestamp, -1) " +
                    "FROM MDVSENDINGINFO info, LBASEND head, " +
                    "BBDEPARTMENT dept, BBPROJECT_X_REFNO projx, BBPROJECT proj WHERE info.l_type=4 AND info.l_refno=projx.l_refno AND projx.l_projectpk=proj.l_projectpk AND info.l_deptpk=dept.l_deptpk AND info.l_refno*=head.l_refno " +
                    "AND info.l_group>=2 AND dept.l_deptpk in ({0}) "+
                     "GROUP BY info.l_deptpk, dept.l_deptpk, dept.sz_deptid, proj.l_projectpk, projx.l_projectpk, projx.l_refno, info.l_refno, head.l_status " +
                     "ORDER BY info.l_refno DESC",
                     szDeptList);*/
                    String sz_filter = "";

                    szSQL = String.Format(
                        "SELECT * FROM v_StatusListVoice " +
                        "WHERE l_deptpk in ({0}) " +
                        "UNION " +
                        "SELECT * FROM v_StatusListSms " +
                        "WHERE l_deptpk in ({0}) " +
                        "UNION " +
                        "SELECT * FROM v_StatusListLBA " +
                        "WHERE l_deptpk in ({0}) " +
                        "UNION " +
                        "SELECT * FROM v_StatusListCB " +
                        "WHERE l_deptpk in ({0}) ", szDeptList);
                    
                    if((filter_by & UDATAFILTER.BY_LIVE)==UDATAFILTER.BY_LIVE && (filter_by & UDATAFILTER.BY_SIMULATION)==UDATAFILTER.BY_SIMULATION)
                    {
                        sz_filter += "";
                    }
                    else if((filter_by & UDATAFILTER.BY_LIVE)==UDATAFILTER.BY_LIVE)
                    {
                        sz_filter += "AND isnull(f_simulate,0)=0 ";
                    }
                    else if((filter_by & UDATAFILTER.BY_SIMULATION)==UDATAFILTER.BY_SIMULATION)
                    {
                        sz_filter += "AND isnull(f_simulate,0)=1 ";
                    }
                    szSQL += sz_filter;
                        //"WHERE l_deptpk in (SELECT l_deptpk from BBDEPARTMENT WHERE l_comppk={0}) "+
                    szSQL += "ORDER BY l_refno DESC";
                        //logon.l_comppk);
                        //szDeptList);

                    break;
            }
            //ORIGINALLY: info.l_group in (2,3,4,8,9)
            rs = null;
            try
            {
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
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
                        item.n_updatetimestamp = (long)rs.GetDecimal(16);
                        item.n_finished = rs.GetInt16(17);

                        res.addLine(ref item);
                    }
                    catch (Exception e)
                    {
                        ULog.error(0, szSQL, e.Message);
                    }
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
                if (rsDepts != null && !rsDepts.IsClosed)
                    rsDepts.Close();
                if (rsType != null && !rsType.IsClosed)
                    rsType.Close();
            }
            res.finalize();
            return res;
        }
    }
}
