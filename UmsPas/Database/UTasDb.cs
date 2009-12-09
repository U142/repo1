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

        public int insertCountRequest(ref UTasCountShape s, ref ULOGONINFO logon)
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
                    String szSQL = String.Format("sp_tas_insert_request_country {0}, {1}, {2}",
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
                    "LC.l_cc>0 AND LC.l_continentpk={0} AND LC.l_isonumeric>0 AND LC.l_cc{3}TC.l_cc_to AND TC.l_cc_from={1} AND TC.l_operator*=OP.l_operator AND TC.l_timestamp>{2}" +
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
                    c.sz_iso = rs.GetString(1);
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
