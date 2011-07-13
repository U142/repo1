using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.Collections;

using com.ums.UmsCommon;
using com.ums.UmsDbLib;
using com.ums.PAS.Database;

namespace com.ums.PAS.Status
{
    public class UStatusListItem
    {
        public int n_sendingtype;
        public int n_totitem;
        public int n_altjmp;
        public int n_refno;
        public int n_createdate;
        public int n_createtime;
        public String sz_sendingname;
        public int n_sendingstatus;
        public String sz_groups;
        public int n_group;
        public int n_type;
        public int n_deptpk;
        public String sz_deptid;
        public long n_projectpk;
        public String sz_projectname;
        public long n_createtimestamp;
        public long n_updatetimestamp;
    }

    /*search class*/
    public class UStatusListSearch : IStatusListSearch
    {
        ULOGONINFO m_logon;
        public UStatusListSearch(ref ULOGONINFO logoninfo)
        {
            m_logon = logoninfo;
        }
        public IStatusListResults Find()
        {
            UmsDbConnParams conn = new UmsDbConnParams();
            conn.sz_dsn = UCommon.UBBDATABASE.sz_dsn;
            conn.sz_uid = UCommon.UBBDATABASE.sz_uid;
            conn.sz_pwd = UCommon.UBBDATABASE.sz_pwd;

            UStatusListDb db = new UStatusListDb(conn);
            UStatusListResults list = db.GetStatusList(ref m_logon);
            db.close();
            return list;
        }
    }

    /*collection of statuslist items*/
    public class UStatusListResults : IStatusListResults
    {
        public UStatusListResults()
        {
        }

        protected ArrayList temp = new ArrayList();
        public UStatusListItem[] list;
        public void addLine(ref UStatusListItem adr)
        {
            temp.Add(adr);
        }
        public void finalize()
        {
            list = temp.Cast<UStatusListItem>().ToArray();
        }
    }

}
