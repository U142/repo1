using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using com.ums.UmsCommon;
using com.ums.PAS.Database;
using com.ums.UmsDbLib;

namespace com.ums.PAS.Address
{
    public class UInhabitantsByDistance : IAddressSearch
    {
        protected UMapDistanceParams m_search;
        protected ULOGONINFO m_logon;

        public UInhabitantsByDistance(ref UMapDistanceParams search, ref ULOGONINFO logon)
        {
            m_search = search;
            m_logon = logon;
        }

        public IAddressResults Find()
        {
            try
            {
                UAdrDb db = new UAdrDb(m_logon.sz_stdcc, 60, m_logon.l_deptpk);
                UAddressList list = db.FindAddressesByDistance(m_search);
                db.close();
                return list;
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw;
            }
        }
    }
}
