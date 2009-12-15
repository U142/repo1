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
        public long n_timestamp;
        public List<int> operators;
    }

    public enum ENUM_TASREQUESTRESULTTYPE
    {
        COUNTREQUEST = 1,
        SENDING = 2,
    }

    public class UTASREQUESTRESULTS : UTASREQUEST
    {
        public ENUM_TASREQUESTRESULTTYPE type;
        public int n_operator;
        public String sz_operatorname;
        public String sz_jobid;
        public int n_response;
        public int n_status;
        public long n_userpk;
        public int n_deptpk;
        public String sz_userid;
        public String sz_username;

        /*if SENDING*/
        public int n_refno;
        public int n_retries;
        public int n_requesttype;
        public int n_simulation;
        public String sz_sendingname;
    }


    public class UTASUPDATES
    {
        public List<ULBACONTINENT> continents;
        public List<UTASREQUESTRESULTS> request_updates;
        public long n_serverclock;
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
        public UTASUPDATES GetContinentsAndCountries(long timefilter_count, long timefilter_requestlog)
        {
            try
            {
                long serverclock = db.getDbTime(); //first, get db-timestamp before getting data
                UTASUPDATES updates = new UTASUPDATES();
                List<ULBACONTINENT> list = db.GetContinentsAndCountries(logon.sz_stdcc, timefilter_count);
                updates.continents = list;
                List<UTASREQUESTRESULTS> requests = new List<UTASREQUESTRESULTS>();
                bool b = db.GetTasRequestResults(ref requests, ref logon, timefilter_requestlog, serverclock);
                b = db.GetTasSendings(ref requests, ref logon, timefilter_requestlog, serverclock);
                updates.request_updates = requests;
                updates.n_serverclock = serverclock;

                db.close();
                return updates;
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
                List<int> operators = new List<int>();
                int n_requestpk = db.insertCountRequest(ref shape, ref logon, ref operators);
                UTASREQUEST req = new UTASREQUEST();
                req.n_requestpk = n_requestpk;
                req.operators = operators;
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
