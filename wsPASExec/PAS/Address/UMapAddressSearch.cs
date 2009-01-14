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
using com.ums.PAS.Database;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;

namespace com.ums.PAS.Address
{
    /*
     * Main class for searching database
     * will generate a list of addresses in UAddressList format
     */
    public class UMapAddressSearch : IAddressSearch
    {
        protected UMapAddressParams m_search;
        protected ULOGONINFO m_logon;

        public UMapAddressSearch(ref UMapAddressParams search, ref ULOGONINFO logon)
        {
            m_search = search;
            m_logon = logon;
        }

        public IAddressResults Find()
        {
            UmsDbConnParams conn = new UmsDbConnParams();
            conn.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase;
            conn.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
            conn.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;

            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc);
            UAddressList list = db.GetAddresslist(m_search);
            db.close();
            return list;
            //return list;
        }
    }

}
