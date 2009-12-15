using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Data.Odbc;
using com.ums.UmsParm;
using com.ums.PAS.TAS;


namespace com.ums.PAS.Database
{
    public class UTasDb : PASUmsDb
    {
        public UTasDb()
            : base()
        {

        }

        public int insertCountRequest(ref UTasCountShape s, ref ULOGONINFO logon, ref List<int> retoperators)
        {
            try
            {
                if (s.countries.Count <= 0)
                    throw new UNoCountryCodeSpecifiedException();

                int n_requestpk = -1;
                OdbcDataReader rsReq = ExecReader("sp_tas_requestpk", UmsDb.UREADER_KEEPOPEN);
                if (rsReq.Read())
                    n_requestpk = rsReq.GetInt32(0);
                rsReq.Close();
                List<int> operators = GetOperatorsForSend(-1, logon.l_deptpk);
                retoperators = operators;
                if(operators.Count<=0)
                    throw new UNoAccessOperatorsException();
                for(int i=0; i < operators.Count; i++)
                {
                    String szSQL = String.Format("sp_tas_insert_request {0}, {1}, {2}, {3}",
                                            n_requestpk, operators[i], logon.l_userpk, logon.l_deptpk);
                    ExecNonQuery(szSQL);
                }
                for (int i = 0; i < s.countries.Count; i++)
                {
                    String szSQL = String.Format("sp_tas_insert_request_country {0}, {1}, '{2}'",
                                        n_requestpk, s.countries[i].l_cc, s.countries[i].sz_iso);
                    ExecNonQuery(szSQL);
                }
                return n_requestpk;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        protected long _getInitTimestamp(long n, long now)
        {
            if (n.ToString().Length < 14)
            {
                long n_tmp = now;
                //create a new date, 30 minutes earlier
                DateTime date = UCommon.UConvertLongToDateTime(n_tmp);
                date = date.AddMinutes(-n);
                n = UCommon.UConvertDateTimeToLong(ref date);
                return n;
            }
            else
                return n;
        }



        public bool GetTasSendings(ref List<UTASREQUESTRESULTS> res, ref ULOGONINFO logon, long timefilter, long dbtime)
        {
            try
            {
                timefilter = _getInitTimestamp(timefilter, dbtime);
                String sql = String.Format(
                    "select " +
                    "SI.l_refno, OP.l_operator, OP.sz_operatorname, LS.sz_jobid, LS.l_response, LS.l_status, " +
                    "TS.l_ts, SI.l_userpk, SI.l_deptpk, BU.sz_userid, (BU.sz_name + ' ' + BU.sz_surname) sz_username, " +
                    "LS.l_retries, LS.l_requesttype, LS.f_simulate, SI.sz_sendingname " +
                    "FROM " +
                    "MDVSENDINGINFO SI, LBASEND LS, LBASEND_TS TS, LBAOPERATORS OP, BBUSER BU " +
                    "WHERE " +
                    "SI.l_refno=LS.l_refno AND LS.l_refno=TS.l_refno AND LS.l_operator=TS.l_operator AND TS.l_operator=OP.l_operator AND SI.l_deptpk={0} " +
                    "AND SI.l_userpk=BU.l_userpk AND TS.l_ts>={1} " +
                    "ORDER BY TS.l_ts, TS.l_refno, TS.l_operator",
                    logon.l_deptpk, timefilter);
                OdbcDataReader rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
                int n_prev_operator = -1;
                int n_prev_request = -1;
                int n_new_operator = -1;
                int n_new_request = -1;
                UTASREQUESTRESULTS tmp = null;
                List<ULBACOUNTRY> ccs = null;
                while (rs.Read())
                {
                    n_new_request = rs.GetInt32(0);
                    n_new_operator = rs.GetInt32(1);

                    //this is an old result, just add the country
                    if (n_new_request == n_prev_request && n_new_operator == n_prev_operator)
                    {
                        /*ULBACOUNTRY country = new ULBACOUNTRY();
                        country.l_cc = rs.GetInt32(11);
                        country.sz_iso = rs.GetString(12);
                        if(ccs != null)
                           ccs.Add(country);*/
                        tmp.n_timestamp = rs.GetInt64(6);
                        tmp.n_status = rs.GetInt32(5);
                    }
                    //this is a new result
                    else
                    {
                        tmp = new UTASREQUESTRESULTS();
                        tmp.type = ENUM_TASREQUESTRESULTTYPE.SENDING;
                        tmp.n_requestpk = rs.GetInt32(0);
                        tmp.n_operator = rs.GetInt32(1);
                        tmp.sz_operatorname = rs.GetString(2);
                        tmp.sz_jobid = rs.GetString(3);
                        tmp.n_response = rs.GetInt32(4);
                        tmp.n_status = rs.GetInt32(5);
                        tmp.n_timestamp = rs.GetInt64(6);
                        tmp.n_userpk = rs.GetInt64(7);
                        tmp.n_deptpk = rs.GetInt32(8);
                        tmp.sz_userid = rs.GetString(9);
                        tmp.sz_username = rs.GetString(10);
                        tmp.n_retries = rs.GetInt32(11);
                        tmp.n_requesttype = rs.GetInt32(12);
                        tmp.n_simulation = rs.GetInt32(13);
                        tmp.sz_sendingname = rs.GetString(14);
                        tmp.n_refno = tmp.n_requestpk;
                        
                        ccs = new List<ULBACOUNTRY>();

                        String sqlCC = String.Format("SELECT l_cc_to, isnull(sz_iso_to,'') FROM LBASEND_COUNTRIES WHERE l_refno={0}", tmp.n_refno);
                        OdbcDataReader rsCC = ExecReader(sqlCC, UmsDb.UREADER_KEEPOPEN);
                        while (rsCC.Read())
                        {
                            ULBACOUNTRY country = new ULBACOUNTRY();
                            country.l_cc = rsCC.GetInt32(0);
                            country.sz_iso = rsCC.GetString(1).Trim();
                            ccs.Add(country);
                        }
                        rsCC.Close();
                        tmp.list = ccs;
                        res.Add(tmp);

                        n_prev_request = tmp.n_requestpk;
                        n_prev_operator = tmp.n_operator;
                        
                    }
                }
                rs.Close();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * timefilter = yyyymmddhhmmss
         * if timefilter's length is less than 14, it's a fresh query. Use timefilter to download timefilter minutes old records.
         */
        public bool GetTasRequestResults(ref List<UTASREQUESTRESULTS> res, ref ULOGONINFO logon, long timefilter, long dbtime)
        {
            try
            {
                timefilter = _getInitTimestamp(timefilter, dbtime);


                String sql = String.Format("SELECT " +
                    "REQ.l_requestpk, REQ.l_operator, OP.sz_operatorname, REQ.sz_jobid, REQ.l_response, REQ.l_status, " +
                    "REQ.l_timestamp, REQ.l_userpk, REQ.l_deptpk, BU.sz_userid, (BU.sz_name + ' ' + BU.sz_surname) sz_username, " +
                    "COU.l_cc_to, COU.sz_iso_to " +
                    "FROM " +
                    "LBATOURISTCOUNTREQ REQ, LBAOPERATORS OP, LBATOURISTCOUNTREQ_COUNTRIES COU, BBUSER BU " +
                    "WHERE " +
                    "REQ.l_operator=OP.l_operator AND REQ.l_requestpk=COU.l_requestpk AND REQ.l_userpk=BU.l_userpk AND " +
                    "REQ.l_timestamp>={0} " +
                    "ORDER BY REQ.l_requestpk, REQ.l_operator",
                    timefilter);
                OdbcDataReader rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
                int n_prev_operator = -1;
                int n_prev_request = -1;
                int n_new_operator = -1;
                int n_new_request = -1;
                UTASREQUESTRESULTS tmp = null;
                List<ULBACOUNTRY> ccs = null;
                while (rs.Read())
                {
                    n_new_request = rs.GetInt32(0);
                    n_new_operator = rs.GetInt32(1);

                    //this is an old result, just add the country
                    if (n_new_request == n_prev_request && n_new_operator == n_prev_operator)
                    {
                        ULBACOUNTRY country = new ULBACOUNTRY();
                        country.l_cc = rs.GetInt32(11);
                        country.sz_iso = rs.GetString(12).Trim();
                        if(ccs != null)
                           ccs.Add(country);
                    }
                    //this is a new result
                    else
                    {
                        tmp = new UTASREQUESTRESULTS();
                        tmp.type = ENUM_TASREQUESTRESULTTYPE.COUNTREQUEST;
                        tmp.n_requestpk = rs.GetInt32(0);
                        tmp.n_operator = rs.GetInt32(1);
                        tmp.sz_operatorname = rs.GetString(2);
                        tmp.sz_jobid = rs.GetString(3);
                        tmp.n_response = rs.GetInt32(4);
                        tmp.n_status = rs.GetInt32(5);
                        tmp.n_timestamp = rs.GetInt64(6);
                        tmp.n_userpk = rs.GetInt64(7);
                        tmp.n_deptpk = rs.GetInt32(8);
                        tmp.sz_userid = rs.GetString(9);
                        tmp.sz_username = rs.GetString(10);
                        ccs = new List<ULBACOUNTRY>();
                        ULBACOUNTRY country = new ULBACOUNTRY();
                        country.l_cc = rs.GetInt32(11);
                        country.sz_iso = rs.GetString(12).Trim();
                        ccs.Add(country);
                        tmp.list = ccs;
                        res.Add(tmp);

                        n_prev_request = tmp.n_requestpk;
                        n_prev_operator = tmp.n_operator;
                        
                    }
                }
                rs.Close();
                
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public List<ULBACONTINENT> GetContinentsAndCountries(String from_country, long timefilter)
        {
            if (from_country==null || from_country.Length <= 0)
                throw new UNoCountryCodeSpecifiedException();
            List<ULBACONTINENT> list = new List<ULBACONTINENT>();
            try
            {
                String sql = String.Format("SELECT l_continentpk, sz_short, sz_name, f_weight_lat, f_weight_lon, f_dimension_lat, f_dimension_lon FROM LBACONTINENTS");
                OdbcDataReader rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    ULBACONTINENT cont = new ULBACONTINENT();
                    cont.l_continentpk  = rs.GetInt32(0);
                    cont.sz_short = rs.GetString(1);
                    cont.sz_name = rs.GetString(2);
                    cont.weightpoint = new UMapPoint();
                    cont.weightpoint.lat = rs.GetDouble(3);
                    cont.weightpoint.lon = rs.GetDouble(4);
                    cont.bounds = new UMapBounds();
                    cont.bounds.l_bo = cont.weightpoint.lon - 45.0;
                    cont.bounds.r_bo = cont.weightpoint.lon + 45.0;
                    cont.bounds.u_bo = cont.weightpoint.lat + 45.0;
                    cont.bounds.b_bo = cont.weightpoint.lat - 45.0;

                    cont.countries = GetCountriesByContinent(cont.l_continentpk, from_country, timefilter);
                    if(cont.countries.Count>0)
                        list.Add(cont);
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
               
            return list;
        }
        public List<ULBACOUNTRY> GetCountriesByContinent(int n, String from_country, long timefilter)
        {
            List<ULBACOUNTRY> list = new List<ULBACOUNTRY>();
            try
            {
                //String sql = String.Format("SELECT LC.l_cc, LC.sz_iso, LC.sz_name, LC.f_weight_lat, LC.f_weight_lon, LC.f_dimension_lat, LC.f_dimension_lon, LC.l_continentpk, LC.l_isonumeric FROM LBACOUNTRIES LC WHERE LC.l_continentpk={0} AND LC.l_isonumeric>0", n);
                /*String sql = String.Format("SELECT " +
                    "LC.l_cc, LC.sz_iso, LC.sz_name, LC.f_weight_lat, LC.f_weight_lon, LC.f_dimension_lat, LC.f_dimension_lon, LC.l_continentpk, LC.l_isonumeric, isnull(TC.l_timestamp,-1), isnull(OP.l_operator,0), isnull(OP.sz_operatorname,''), isnull(TC.l_count,0) " +
                    "FROM " +
                    "LBACOUNTRIES LC, LBATOURISTCOUNT TC, LBAOPERATORS OP " +
                    "WHERE " +
                    "LC.l_continentpk={0} AND LC.l_isonumeric>0 AND LC.l_cc*=TC.l_cc_to AND TC.l_cc_from={1} AND TC.l_operator*=OP.l_operator "+
                    "ORDER BY LC.sz_iso, OP.l_operator",
                    n, from_country);
                */
                //timefilter = -1;
                String join = "=";
                if (timefilter == -1) //first download
                    join = "*=";
                String sql = String.Format("SELECT " +
                    "LC.l_cc, LC.sz_iso, LC.sz_name, LC.f_weight_lat, LC.f_weight_lon, LC.f_dimension_lat, LC.f_dimension_lon, LC.l_continentpk, LC.l_isonumeric, isnull(TC.l_timestamp,0), isnull(OP.l_operator,0), isnull(OP.sz_operatorname,''), isnull(TC.l_count,0) " +
                    "FROM " +
                    "LBACOUNTRIES LC, LBATOURISTCOUNT TC, LBAOPERATORS OP " +
                    "WHERE " +
                    "LC.l_cc>0 AND LC.l_continentpk={0} AND LC.l_isonumeric>0 AND LC.l_cc{3}TC.l_cc_to AND TC.l_cc_from={1} AND TC.l_operator*=OP.l_operator AND TC.l_timestamp>={2}" +
                    "ORDER BY LC.l_cc, LC.sz_iso, OP.l_operator",
                    n, from_country, timefilter, join);


                OdbcDataReader rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
                ULBACOUNTRY last = null;
                String prevcountryname = "";
                while (rs.Read())
                {
                    ULBACOUNTRY c = new ULBACOUNTRY();
                    UTOURISTCOUNT t = new UTOURISTCOUNT();
                    c.l_cc = rs.GetInt32(0);
                    c.sz_iso = rs.GetString(1).Trim();
                    c.sz_name = rs.GetString(2);
                    c.weightpoint = new UMapPoint();
                    c.weightpoint.lat = rs.GetDouble(3);
                    c.weightpoint.lon = rs.GetDouble(4);
                    c.bounds = new UMapBounds();
                    c.bounds.l_bo = c.weightpoint.lon - 12.0;
                    c.bounds.r_bo = c.weightpoint.lon + 12.0;
                    c.bounds.u_bo = c.weightpoint.lat + 12.0;
                    c.bounds.b_bo = c.weightpoint.lat - 12.0;
                    c.l_continentpk = rs.GetInt32(7);
                    c.l_iso_numeric = rs.GetInt32(8);
                    c.n_oldestupdate = -1;
                    t.l_lastupdate = rs.GetInt64(9);
                    t.l_operator = rs.GetInt32(10);
                    t.sz_operator = rs.GetString(11);
                    t.l_touristcount = rs.GetInt32(12);
                    if (last!=null && last.sz_iso == c.sz_iso)
                    {
                        //no need to add, sum count
                        if (t.l_lastupdate > 0)
                        {
                            last.operators.Add(t);
                            if (t.l_lastupdate <= last.n_oldestupdate)
                                last.n_oldestupdate = t.l_lastupdate;
                            if (t.l_lastupdate >= last.n_newestupdate)
                                last.n_newestupdate = t.l_lastupdate;
                        }
                        last.n_touristcount += t.l_touristcount;
                    }
                    else if (last != null && last.l_cc == c.l_cc)
                    {
                        if (!prevcountryname.Equals(c.sz_name))
                        {
                            last.sz_name += ", " + c.sz_name;
                            prevcountryname = c.sz_name;
                        }
                    }
                    /*if (last != null && last.l_cc == c.l_cc) //same CC, merge them
                    {
                        if(last.sz_iso != c.sz_iso)
                            last.sz_name += ", " + c.sz_name;
                        //no need to add, sum count
                        if (t.l_lastupdate > 0)
                        {
                            if (last.operators==null)
                                last.operators = new List<UTOURISTCOUNT>();
                            last.operators.Add(t);
                            if (t.l_lastupdate <= last.n_oldestupdate)
                                last.n_oldestupdate = t.l_lastupdate;
                        }
                        last.n_touristcount += t.l_touristcount;
                    }*/
                    else
                    { //initialize and add country
                        c.operators = new List<UTOURISTCOUNT>();
                        if (t.l_lastupdate > 0)
                        {
                            c.operators.Add(t);
                            c.n_oldestupdate = t.l_lastupdate;
                            c.n_newestupdate = t.l_lastupdate;
                        }
                        c.n_touristcount = t.l_touristcount;
                        list.Add(c);
                        last = c;
                    }


                    //c.n_lastupdate = rs.GetInt64(9);
                    //c.n_touristcount = rs.GetInt32(10);
                }
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return list;
        }
    }
}
