using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using com.ums.UmsCommon;
using com.ums.PAS.Database;

namespace com.ums.PAS.TAS
{

    public class UTas
    {
        UTasDb db;
        ULOGONINFO logon;
        public UTas(ref ULOGONINFO logon)
        {
            this.logon = logon;
            try
            {
                db = new UTasDb();
                if (!db.CheckLogon(ref logon))
                    throw new ULogonFailedException();
            }
            catch (Exception e)
            {
                throw e;
            }

        }
        public List<ULBACONTINENT> GetContinents()
        {
            List<ULBACONTINENT> list = new List<ULBACONTINENT>();

            return list;
        }
        public List<ULBACOUNTRY> GetCountries()
        {
            List<ULBACOUNTRY> list = new List<ULBACOUNTRY>();
            return list;
        }
        public List<ULBACONTINENT> GetContinentsAndCountries(long timefilter)
        {
            try
            {
                List<ULBACONTINENT> list = db.GetContinentsAndCountries(logon.sz_stdcc, timefilter);
                db.close();
                return list;
            }
            catch (Exception e)
            {
                throw e;
            }

        }
    }
}
