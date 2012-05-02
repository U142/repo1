﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Data.Odbc;
using com.ums.UmsParm;
using com.ums.PAS.TAS;
using System.Configuration;


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
            OdbcDataReader rsReq = null;
            try
            {
                if (s.countries.Count <= 0)
                    throw new UNoCountryCodeSpecifiedException();

                int n_requestpk = -1;
                rsReq = ExecReader("sp_tas_requestpk", UmsDb.UREADER_KEEPOPEN);
                if (rsReq.Read())
                    n_requestpk = rsReq.GetInt32(0);
                rsReq.Close();
                List<int> operators = GetOperatorsForSend(-1, logon.l_deptpk);
                retoperators = operators;
                if (operators.Count <= 0)
                    throw new UNoAccessOperatorsException();
                for (int i = 0; i < operators.Count; i++)
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rsReq != null && !rsReq.IsClosed)
                    rsReq.Close();
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
            OdbcDataReader rsCC = null;
            OdbcDataReader rs = null;
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
                rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
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


                        using (OdbcConnection con2 = new OdbcConnection(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString))
                        {
                            con2.Open();
                            String sqlCC = String.Format("SELECT l_cc_to, isnull(sz_iso_to,'') FROM LBASEND_COUNTRIES WHERE l_refno={0}", tmp.n_refno);
                            OdbcCommand cmd2 = new OdbcCommand(sqlCC, con2);
                            rsCC = cmd2.ExecuteReader();
                            while (rsCC.Read())
                            {
                                ULBACOUNTRY country = new ULBACOUNTRY();
                                country.l_cc = rsCC.GetInt32(0);
                                country.sz_iso = rsCC.GetString(1).Trim();
                                ccs.Add(country);
                            }
                            cmd2.Dispose();
                            con2.Close();
                            tmp.list = ccs;
                            res.Add(tmp);
                        }

                        n_prev_request = tmp.n_requestpk;
                        n_prev_operator = tmp.n_operator;
                        
                    }
                }
                rs.Close();
                return true;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rsCC != null && !rsCC.IsClosed)
                    rsCC.Close();
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }

        /*
         * timefilter = yyyymmddhhmmss
         * if timefilter's length is less than 14, it's a fresh query. Use timefilter to download timefilter minutes old records.
         */
        public bool GetTasRequestResults(ref List<UTASREQUESTRESULTS> res, ref ULOGONINFO logon, long timefilter, long dbtime)
        {
            OdbcDataReader rs = null;

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
                rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
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
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }

        public List<ULBACONTINENT> GetContinentsAndCountries(String from_country, long timefilter)
        {
            if (from_country==null || from_country.Length <= 0)
                throw new UNoCountryCodeSpecifiedException();
            List<ULBACONTINENT> list = new List<ULBACONTINENT>();
            OdbcDataReader rs = null;

            try
            {
                String sql = String.Format("SELECT l_continentpk, sz_short, sz_name, f_weight_lat, f_weight_lon, f_dimension_lat, f_dimension_lon FROM LBACONTINENTS");
                rs = ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    ULBACONTINENT cont = new ULBACONTINENT();
                    cont.l_continentpk = rs.GetInt32(0);
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
                    if (cont.countries.Count > 0)
                        list.Add(cont);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
               
            return list;
        }
        public List<ULBACOUNTRY> GetCountriesByContinent(int n, String from_country, long timefilter)
        {
            List<ULBACOUNTRY> list = new List<ULBACOUNTRY>();
            OdbcDataReader rs = null;

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

                String join = "INNER JOIN";
                if (timefilter == -1) //first download
                    join = "LEFT OUTER JOIN";
                String sql = String.Format("SELECT " +
	                "LC.l_cc, LC.sz_iso, LC.sz_name, LC.f_weight_lat, LC.f_weight_lon, LC.f_dimension_lat, LC.f_dimension_lon, " +
	                "LC.l_continentpk, LC.l_isonumeric, isnull(TC.l_timestamp,0), isnull(OP.l_operator,0), isnull(OP.sz_operatorname,''), isnull(TC.l_count,0) " +
                    "FROM " +
	                "LBACOUNTRIES LC " +
	                "{3} LBATOURISTCOUNT TC ON LC.l_cc=TC.l_cc_to " +
	                "LEFT OUTER JOIN LBAOPERATORS OP ON TC.l_operator=OP.l_operator " +
                    "WHERE " +
	                "LC.l_cc>0 AND " +
	                "LC.l_continentpk={0} AND " +
	                "LC.l_isonumeric>0 AND " +
	                "TC.l_cc_from={1} AND " +
	                "TC.l_timestamp>={2} AND NOT LC.l_cc={1} " +
                    "ORDER BY " +
	                "LC.l_cc, LC.sz_iso, OP.l_operator", 
                    n, from_country, timefilter, join);

                PASUmsDb db2 = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                rs = db2.ExecReader(sql, UmsDb.UREADER_KEEPOPEN);
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
                    if (last != null && last.sz_iso == c.sz_iso)
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
                db2.close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return list;
        }
        public List<UTASRESPONSENUMBER> GetResponseNumbers(ref ULOGONINFO logon)
        {
            List<UTASRESPONSENUMBER> list = new List<UTASRESPONSENUMBER>();
            OdbcDataReader rs = null;
            try
            {
                UTASRESPONSENUMBER responsenumber;
                string szSQL = String.Format("SELECT sz_replynumber, l_activerefno, l_timestamp FROM LBASMSIN_REPLYNUMBERS WHERE (l_comppk={0} AND l_deptpk={1}) OR (l_comppk={2} AND l_deptpk=0)", logon.l_comppk, logon.l_deptpk, logon.l_comppk);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    responsenumber = new UTASRESPONSENUMBER();
                    responsenumber.sz_responsenumber = rs.GetString(0);
                    if (rs.IsDBNull(1))
                        responsenumber.n_refno = 0;
                    else
                        responsenumber.n_refno = rs.GetInt32(1);
                    if (rs.IsDBNull(2))
                        responsenumber.n_timestamp = 0;
                    else
                        responsenumber.n_timestamp = (long)rs.GetDecimal(2);
                    list.Add(responsenumber);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return list;
        }

        public List<ULBACOUNTRYSTATISTICS> GetStatistics_Countries_Per_Timeunit(ref ULBASTATISTICS_FILTER filter)
        {
            OdbcDataReader rs = null;

            try
            {
                List<ULBACOUNTRYSTATISTICS> ret = new List<ULBACOUNTRYSTATISTICS>();

                String from_ts = "";
                String to_ts = "";
                long number_of_date = 8;
                if (filter.group_timeunit.Equals(ULBAFILTER_STAT_TIMEUNIT.PER_HOUR))
                    number_of_date = 10;
                else if (filter.group_timeunit.Equals(ULBAFILTER_STAT_TIMEUNIT.PER_DAY))
                    number_of_date = 8;
                else if (filter.group_timeunit.Equals(ULBAFILTER_STAT_TIMEUNIT.PER_MONTH))
                    number_of_date = 6;
                else if (filter.group_timeunit.Equals(ULBAFILTER_STAT_TIMEUNIT.PER_YEAR))
                    number_of_date = 4;

                String szSQL = "";

                bool b_inc_rowcount = false;
                if (filter.rowcount > 0)
                    b_inc_rowcount = true;


                String statfunction = "avg";
                switch (filter.stat_function)
                {
                    case ULBAFILTER_STAT_FUNCTION.STAT_AVERAGE:
                        statfunction = "AVG";
                        break;
                    case ULBAFILTER_STAT_FUNCTION.STAT_MAX:
                        statfunction = "MAX";
                        break;
                    case ULBAFILTER_STAT_FUNCTION.STAT_MIN:
                        statfunction = "MIN";
                        break;
                }

                String tempSQL = "";
                if (!b_inc_rowcount)
                {
                    for (int countries = 0; countries < filter.countries.Count; countries++)
                    {
                        int cc_to = filter.countries[countries].l_cc;
                        from_ts = filter.from_date.ToString();
                        to_ts = filter.to_date.ToString();
                        tempSQL = String.Format(
                            "select TC.l_cc_to, TC.l_operator, substring(convert(varchar(18), TCH.l_timestamp),1,{1}), {5}(TCH.l_count), LO.sz_operatorname " +
                            "FROM " +
                            "LBATOURISTCOUNT TC, LBATOURISTCOUNTHIST TCH, LBAOPERATORS LO " +
                            "WHERE " +
                            "TC.l_countpk=TCH.l_countpk AND " +
                            "TC.l_cc_to={0} AND " +
                            "TCH.l_timestamp>={2} AND TCH.l_timestamp<={3} " +
                            "AND TC.l_operator = LO.l_operator " +
                            "GROUP BY TC.l_cc_to, TC.l_operator, substring(convert(varchar(18), TCH.l_timestamp),1,{1}), LO.sz_operatorname ",
                            cc_to, number_of_date, from_ts, to_ts, filter.countries[countries].sz_name, statfunction);
                        szSQL += (countries > 0 ? " UNION " : "");
                        szSQL += tempSQL;
                    }
                    szSQL += String.Format("" +
                             "ORDER BY substring(convert(varchar(18), TCH.l_timestamp),1,{0})",
                             number_of_date);
                    rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                    while (rs.Read())
                    {
                        ULBACOUNTRYSTATISTICS stats = new ULBACOUNTRYSTATISTICS();
                        stats.l_cc = rs.GetInt32(0);
                        stats.statistics = new List<UTOURISTCOUNT>();

                        UTOURISTCOUNT touristcount = new UTOURISTCOUNT();
                        touristcount.l_operator = rs.GetInt32(1);
                        touristcount.l_lastupdate = rs.GetInt64(2);
                        touristcount.l_touristcount = rs.GetInt32(3);
                        touristcount.sz_operator = rs.GetString(4);
                        stats.statistics.Add(touristcount);
                        ret.Add(stats);
                    }

                    rs.Close();
                }
                else
                {
                    for (int countries = 0; countries < filter.countries.Count; countries++)
                    {
                        int cc_to = filter.countries[countries].l_cc;
                        from_ts = filter.from_date.ToString();
                        to_ts = filter.to_date.ToString();

                        tempSQL = String.Format("sp_tas_max_pr_timeunit_cc {0}, {1}, {2}, {3}, {4}",
                                                cc_to, filter.rowcount, number_of_date, filter.from_date, filter.to_date);
                        szSQL = tempSQL;
                        rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                        while (rs.Read())
                        {
                            ULBACOUNTRYSTATISTICS stats = new ULBACOUNTRYSTATISTICS();
                            stats.l_cc = rs.GetInt32(0);
                            stats.statistics = new List<UTOURISTCOUNT>();

                            UTOURISTCOUNT touristcount = new UTOURISTCOUNT();
                            touristcount.l_operator = rs.GetInt32(1);
                            touristcount.l_lastupdate = rs.GetInt64(2);
                            touristcount.l_touristcount = rs.GetInt32(3);
                            stats.statistics.Add(touristcount);
                            ret.Add(stats);
                        }

                        rs.Close();
                    }
                    ret.Sort();
                }


                return ret;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }
    }
}
