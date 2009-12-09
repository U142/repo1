using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using com.ums.UmsCommon;
using com.ums.PAS.Database;
using com.ums.PAS.Address;
using com.ums.UmsParm;
using com.ums.UmsFile;

namespace com.ums.PAS.TAS
{

    public class UTASREQUEST
    {
        public int n_requestpk;
        public bool b_success;
        public List<ULBACOUNTRY> list;
    }

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
        public UTASREQUEST PerformAdrCountByCountry(ref List<ULBACOUNTRY> c, ref ULOGONINFO logon)
        {
            try
            {
                UTasCountShape shape = new UTasCountShape();
                int i = 0;
                for (i = 0; i < c.Count; i++)
                {
                    shape.addCountry(c[i]);
                }
                if (i == 0)
                    throw new UNoCountryCodeSpecifiedException();
                BBPROJECT proj = new BBPROJECT();
                proj.sz_projectpk = Guid.NewGuid().ToString();
                PAALERT alert = new PANULLALERT();
                alert.n_requesttype = 2;
                //do DB-stuff
                int n_requestpk = db.insertCountRequest(ref shape, ref logon);
                UTASREQUEST req = new UTASREQUEST();
                req.n_requestpk = n_requestpk;
                AdrfileLBAWriter w = new AdrfileLBAWriter(proj.sz_projectpk, n_requestpk, true, SENDCHANNEL.TAS);
                shape.WriteAddressFileLBA(ref logon, new UDATETIME("0", "0"), "sms", ref proj, ref alert, n_requestpk, 0, ref w);
                
                
                w.publish();
                req.b_success = true;
                req.list = c;
                    
                return req;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }
}
