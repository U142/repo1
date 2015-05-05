using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.Data.Odbc;
using com.ums.PAS.Address;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Collections.Generic;
using com.ums.UmsParm;


namespace com.ums.PAS.Database
{
    public class UAdrDb : UmsDb
    {
        public int m_n_pastype; //0=no db rights, 1=normal address, 2=folkereg address
        public int m_n_deptpk; //using deptpk
        public bool b_prepared = false;
        /*
         * Connect to "vb_adr_" + sz_stdcc depending on which database to access
         */
        public UAdrDb(String sz_stdcc, int timeout, int n_deptpk) 
            : base(timeout)
            //: base(conn.sz_dsn + sz_stdcc + (n_pastype==2 ? "_reg" : ""), conn.sz_uid, conn.sz_pwd, timeout)
        {
            m_n_deptpk = n_deptpk;
            try
            {
                Connect(sz_stdcc, n_deptpk);
            }
            catch (Exception)
            {
                throw;
            }
        }

        public UAdrDb(String sz_stdcc, int timeout)
        {
            try
            {
                Connect(sz_stdcc, -1);
            }
            catch (Exception)
            {
                throw;
            }
        }

        /*used for address databases only*/
        public bool Connect(string sz_stdcc, int n_deptpk)
        {
            if (!UCommon.USETTINGS.b_enable_adrdb)
            {
                throw new UServerDeniedAddressDatabaseException();
            }
            try
            {
                String dsn;

                PASUmsDb db = new PASUmsDb();
                m_n_pastype = db.GetPasType(n_deptpk);
                if (m_n_pastype <= 0)
                    throw new ULogonFailedException();

                // use deptpk or CC?
                if (db.UsesCustomDb(n_deptpk))
                    dsn = "custom_" + n_deptpk;
                else
                    dsn = "address_" + sz_stdcc;

                if (m_n_pastype == 2)
                    dsn += "_reg";
                sz_constring = ConfigurationManager.ConnectionStrings[dsn].ConnectionString;
                db.close();
                return init();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public UMapBounds GetMunicipalBounds(UMunicipalShape mun)
        {
            return GetMunicipalBounds(mun);
        }

        public UMapBounds GetMunicipalBounds(ref UMUNICIPALSENDING mun)
        {
            var mapBounds = GetMunicipalBounds(mun.municipals);
            mun.mapbounds = mapBounds;
            return mapBounds;
        }

        public UMapBounds GetMunicipalBounds(List<UMunicipalDef> municipals)
        {
            UMapBounds b = new UMapBounds();
            if (municipals == null)
                return b;
            if (municipals.Count <= 0)
                return b;
            String szMunicipal = "";
            for (int i = 0; i < municipals.Count; i++)
            {
                if (i > 0)
                    szMunicipal += ", ";
                szMunicipal += municipals[i].sz_municipalid;
            }
            /*String szSQL = String.Format("SELECT MIN(lon), MAX(lon), MIN(lat), MAX(lat) FROM " +
                                        "ADR_KONSUM WHERE KOMMUNENR IN ({0}) AND not lon=0 AND not lat=0",
                                        szMunicipal);*/
            /*String szSQL = String.Format("SELECT AVG(lon), AVG(lat) FROM ADR_KONSUM "+
                                        "WHERE KOMMUNENR IN ({0}) AND not lon=0 AND not lat=0",
                                        szMunicipal);*/
            String szSQL = String.Format("SELECT KOMMUNENR, STDEV(ALL LON) AS Expr1, STDEV(ALL lat), AVG(lon), AVG(lat) " +
                                        "FROM ADR_KONSUM " +
                                        "WHERE KOMMUNENR IN ({0}) AND NOT lon = 0 AND NOT lat = 0 " +
                                        "GROUP BY KOMMUNENR",
                                        szMunicipal);
            OdbcDataReader rs = null;
            try
            {
                rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                /*List<double> list_std_lon = new List<double>();
                List<double> list_std_lat = new List<double>();
                List<double> list_avg_lon = new List<double>();
                List<double> list_avg_lat = new List<double>();*/
                double min_lon = 360, min_lat = 360, max_lon = -360, max_lat = -360;
                while (rs.Read())
                {
                    double stddev_lon = rs.GetFloat(1);
                    double stddev_lat = rs.GetFloat(2);
                    double avg_lon = rs.GetFloat(3);
                    double avg_lat = rs.GetFloat(4);
                    double lon_min = avg_lon - stddev_lon;
                    double lon_max = avg_lon + stddev_lon;
                    double lat_min = avg_lat - stddev_lat;
                    double lat_max = avg_lat + stddev_lat;

                    if (lon_min < min_lon)
                        min_lon = lon_min;
                    if (lon_max > max_lon)
                        max_lon = lon_max;
                    if (lat_min < min_lat)
                        min_lat = lat_min;
                    if (lat_max > max_lat)
                        max_lat = lat_max;

                    /*list_std_lon.Add(stddev_lon);
                    list_std_lat.Add(stddev_lat);
                    list_avg_lon.Add(avg_lon);
                    list_avg_lat.Add(avg_lat);*/
                }
                b.l_bo = min_lat;
                b.r_bo = max_lat;
                b.b_bo = min_lon;
                b.u_bo = max_lon;
                /*for (int i = 0; i < list_avg_lat.Count; i++)
                {
                    double 
                }*/
                //b.l_bo = avg_lat - Math.Abs(stddev_lat);
                //b.r_bo = avg_lat + Math.Abs(stddev_lat);
                //b.b_bo = avg_lon - Math.Abs(stddev_lon);
                //b.u_bo = avg_lon + Math.Abs(stddev_lon);
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
            return b;
        }

        public List<UMunicipalDef> GetMunicipalsByDept(int n_deptpk)
        {
            List<UMunicipalDef> list = new List<UMunicipalDef>();
            /*String szSQL = String.Format("SELECT DXM.l_deptpk, DXM.l_municipalid, MU.sz_name "+
                            "FROM DEPARTMENT_X_MUNICIPAL DXM, MUNICIPAL MU WHERE DXM.l_deptpk={0} AND DXM.l_municipalid*=MU.l_municipalid",
                            n_deptpk);*/
            String szSQL = String.Format(
                "SELECT DXM.l_deptpk, DXM.l_municipalid, MU.sz_name " +
                "FROM "+
                "DEPARTMENT_X_MUNICIPAL DXM LEFT OUTER JOIN MUNICIPAL MU ON DXM.l_municipalid=MU.l_municipalid " +
                "WHERE " +
                "DXM.l_deptpk={0} " +
                "ORDER BY MU.sz_name",
                n_deptpk);
            OdbcDataReader rs = null;
            try
            {
                rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                while (rs.Read())
                {
                    UMunicipalDef md = new UMunicipalDef();
                    md.sz_municipalid = rs.GetInt32(1).ToString();
                    if (rs.IsDBNull(2))
                        md.sz_municipalname = "Unknown Municipal Name";
                    else
                        md.sz_municipalname = rs.GetString(2);
                    list.Add(md);
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

        public UAdrCount GetAddressCount(ref ULOGONINFO l, ref UMAPSENDING sending)
        {
            try
            {
                
                USendDb logoncheck = new USendDb();
                if (!logoncheck.CheckLogon(ref l, true))
                {
                    throw new ULogonFailedException();
                }

                long n_adrtypes = sending.n_addresstypes;

                if (typeof(UPOLYGONSENDING) == sending.GetType())
                {
                    UPOLYGONSENDING s = (UPOLYGONSENDING)sending;
                    UMapBounds bound = s._calcbounds();
                    UMapPoint[] points = s.polygonpoints;

                    return _PolyCountV2(ref bound, ref s, n_adrtypes);
                }
                else if (typeof(UELLIPSESENDING) == sending.GetType())
                {
                    UELLIPSESENDING s = (UELLIPSESENDING)sending;
                    UEllipseDef ell = s.ellipse;
                    return _EllipseCount(ref ell, n_adrtypes);
                }
                else if (typeof(UGIS) == sending.GetType())
                {
                }
                else if (typeof(UTESTSENDING) == sending.GetType())
                {
                    UTESTSENDING s = (UTESTSENDING)sending;
                    UAdrCount a = new UAdrCount();
                    a.n_private_fixed = s.numbers.Count;
                    return a;
                    //return s.numbers.Count;
                }
                else if (typeof(UMUNICIPALSENDING) == sending.GetType())
                {
                    UMUNICIPALSENDING s = (UMUNICIPALSENDING)sending;
                    List<UMunicipalDef> m = s.municipals;
                    return _MunicipalCount(ref m, n_adrtypes);
                }
                throw new NotImplementedException();
            }
            catch (Exception)
            {
                throw;
            }
        }

        protected void _AddToAdrcount(ref UAdrCount c, ref UAdrcountCandidate adr, long adrtypes)
        {
            if ((adrtypes & (long)ADRTYPES.FIXED_PRIVATE) > 0 && adr.hasfixed && adr.bedrift == 0)
                c.n_private_fixed += adr.add;
            if ((adrtypes & (long)ADRTYPES.FIXED_COMPANY) > 0 && adr.hasfixed && adr.bedrift==1)
                c.n_company_fixed += adr.add;
            if ((adrtypes & (long)ADRTYPES.MOBILE_PRIVATE) > 0 && adr.hasmobile && adr.bedrift == 0)
                c.n_private_mobile += adr.add;
            if ((adrtypes & (long)ADRTYPES.MOBILE_COMPANY) > 0 && adr.hasmobile && adr.bedrift == 1)
                c.n_company_mobile += adr.add;
            if ((adrtypes & (long)ADRTYPES.SMS_PRIVATE) > 0 && adr.hasmobile && adr.bedrift == 0)
                c.n_private_sms += adr.add;
            if ((adrtypes & (long)ADRTYPES.SMS_COMPANY) > 0 && adr.hasmobile && adr.bedrift == 1)
                c.n_company_sms += adr.add;
            if ((adrtypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0 && adr.bedrift == 0)
            {
                if (adr.hasmobile)
                    c.n_private_sms += adr.add;
                else if (adr.hasfixed)
                    c.n_private_fixed += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 && adr.bedrift == 1)
            {
                if (adr.hasmobile)
                    c.n_company_sms += adr.add;
                else if (adr.hasfixed)
                    c.n_company_fixed += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 && adr.bedrift == 0)
            {
                if (adr.hasfixed)
                    c.n_private_fixed += adr.add;
                else if(adr.hasmobile)
                    c.n_private_sms += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 && adr.bedrift == 1)
            {
                if(adr.hasfixed)
                    c.n_company_fixed += adr.add;
                else if(adr.hasmobile)
                    c.n_company_sms += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.FIXED_PRIVATE_AND_MOBILE) > 0 && adr.bedrift == 0)
            {
                if (adr.hasfixed)
                    c.n_private_fixed += adr.add;
                if (adr.hasmobile)
                    c.n_private_mobile += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.FIXED_COMPANY_AND_MOBILE) > 0 && adr.bedrift == 1)
            {
                if (adr.hasfixed)
                    c.n_company_fixed += adr.add;
                if (adr.hasmobile)
                    c.n_company_mobile += adr.add;
            }
            if((adrtypes & (long)ADRTYPES.MOBILE_PRIVATE_AND_FIXED) > 0 && adr.bedrift == 0)
            {
                if (adr.hasfixed)
                    c.n_private_fixed += adr.add;
                if (adr.hasmobile)
                    c.n_private_mobile += adr.add;
            }
            if ((adrtypes & (long)ADRTYPES.MOBILE_COMPANY_AND_FIXED) > 0 && adr.bedrift == 1)
            {
                if (adr.hasfixed)
                    c.n_company_fixed += adr.add;
                if (adr.hasmobile)
                    c.n_company_mobile += adr.add;
            }
            if (!adr.hasfixed && !adr.hasmobile && (adrtypes & (long)ADRTYPES.NOPHONE_COMPANY) > 0 && adr.bedrift == 1)
                c.n_company_nonumber += adr.add;

            if(!adr.hasfixed && !adr.hasmobile && (adrtypes & (long)ADRTYPES.NOPHONE_PRIVATE) > 0 && adr.bedrift == 0)
                c.n_private_nonumber += adr.add;
        }

        protected UAdrCount _MunicipalCount(ref List<UMunicipalDef> m, long adrtypes)
        {
            UAdrCount count = new UAdrCount();
            OdbcDataReader rs = null;
            try
            {
                bool bVulnerableCitizensOnly = (adrtypes & (long)ADRTYPES.ONLY_VULNERABLE_CITIZENS) > 0;
                String szSQL = "";
                bool bfirst = true;
                for(int i=0; i < m.Count; i++)
                {
                    if (!bfirst)
                        szSQL += " UNION ";
                    if (m[i].sz_municipalid.Length > 0)
                    {
                        if (m_n_pastype == 1)
                        {
                            szSQL += String.Format("SELECT isnull(BEDRIFT,0), isnull(f_hasfixed,0), isnull(f_hasmobile,0), count(KON_DMID) n_count " +
                                                    "FROM ADR_KONSUM WHERE KOMMUNENR={0}{1}",
                                                    m[i].sz_municipalid,
                                                    GetRecipientFilter(adrtypes));
                        }
                        else if (m_n_pastype == 2)
                        {
                            szSQL += String.Format("SELECT isnull(BEDRIFT,0), isnull(f_hasfixed,0), isnull(f_hasmobile,0), count(KON_DMID) n_count " +
                                                    "FROM ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX WHERE AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={1} AND AK.KOMMUNENR={0}{2}{3}",
                                                    m[i].sz_municipalid, m_n_deptpk, (bVulnerableCitizensOnly ? " AND AK.f_hasdisabled=1" : ""), GetRecipientFilter(adrtypes));
                        }
                        bfirst = false;
                    }
                    if (!bfirst)
                        szSQL += " GROUP BY BEDRIFT, f_hasfixed, f_hasmobile";
                }
                rs = ExecReader(szSQL, UREADER_KEEPOPEN);
                
                UAdrcountCandidate c = new UAdrcountCandidate();
                while (rs.Read())
                {
                    c.bedrift = rs.GetInt32(0);
                    c.hasfixed = (rs.GetInt32(1) == 1 ? true : false);
                    c.hasmobile = (rs.GetInt32(2) == 1 ? true : false);
                    c.add = rs.GetInt32(3);
                    _AddToAdrcount(ref count, ref c, adrtypes);
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
            return count;
        }

        private static string GetRecipientFilter(long adrtypes)
        {
            string recipientFilter = "";
            if ((adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_RESIDENT) > 0 && (adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_HOME) > 0 && (adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_VACATION) > 0)
            {
                // all recipients, no filters needed
            }
            else if ((adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_RESIDENT) > 0 || (adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_HOME) > 0 || (adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_VACATION) > 0)
            {
                bool comma = false;
                recipientFilter += " AND boligtype in (";
                if ((adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_RESIDENT) > 0)
                {
                    if (comma) recipientFilter += ","; else comma = true;
                    recipientFilter += "'R'";
                }
                if ((adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_HOME) > 0)
                {
                    if (comma) recipientFilter += ","; else comma = true;
                    recipientFilter += "'B'";
                }
                if ((adrtypes & (long)ADRTYPES.RECIPTYPE_PRIVATE_OWNER_VACATION) > 0)
                {
                    if (comma) recipientFilter += ","; else comma = true;
                    recipientFilter += "'F'";
                }
                recipientFilter += ")";
            }

            return recipientFilter;
        }

        protected UAdrCount _EllipseCount(ref UEllipseDef e, long adrtypes)
        {
            UAdrCount count = new UAdrCount();
            OdbcDataReader rs = null;
            try
            {
                bool bVulnerableCitizensOnly = (adrtypes & (long)ADRTYPES.ONLY_VULNERABLE_CITIZENS) > 0;
                string szSQL = "";

                if (m_n_pastype == 1)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "sp_getellipseadr {0}, {1}, {2}, {3}{4}",
                                            e.center.lon, e.center.lat,
                                            e.radius.lon, e.radius.lat,
                                            (m_n_pastype == 2 ? String.Format(",{0},'{1}'", m_n_deptpk, (bVulnerableCitizensOnly ? "1" : "")) : ""));
                }
                else if (m_n_pastype == 2)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, @"select 
                                                isnull(BEDRIFT,0), isnull(f_hasfixed,0), isnull(f_hasmobile,0), count(KON_DMID) n_count 
                                            FROM 
                                                ADR_KONSUM, DEPARTMENT_X_MUNICIPAL DX 
                                            WHERE
                                                LAT >= {0} AND 
                                                LAT <= {1} AND 
                                                LON >= {2} AND 
                                                LON <= {3} AND
                                                ( ( (lat-{4})*(lat-{4}) / ({5}*{5}) ) + ( (lon-{6}) * (lon-{6}) / ({7}*{7}) ) < 1 ) AND
                                                ADR_KONSUM.KOMMUNENR=DX.l_municipalid AND 
                                                DX.l_deptpk={8} 
                                                {9} 
                                                {10}
                                            GROUP BY 
                                                BEDRIFT, f_hasfixed, f_hasmobile"
                        , e.center.lon - e.radius.lon
                        , e.center.lon + e.radius.lon
                        , e.center.lat - e.radius.lat
                        , e.center.lat + e.radius.lat
                        , e.center.lon
                        , e.radius.lon
                        , e.center.lat
                        , e.radius.lat
                        , m_n_deptpk
                        , (bVulnerableCitizensOnly ? "AND ISNULL(f_hasdisabled,0) IN (1)" : "")
                        , GetRecipientFilter(adrtypes));
                }
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                UAdrcountCandidate c = new UAdrcountCandidate();
                while (rs.Read())
                {
                    c.bedrift = rs.GetInt16(0);
                    object o1 = rs.GetValue(1);
                    object o2 = rs.GetValue(2);
                    if (rs.IsDBNull(1))
                        c.hasfixed = false;
                    else
                        c.hasfixed = (rs.GetByte(1).Equals(1) ? true : false);
                    if (rs.IsDBNull(2))
                        c.hasmobile = false;
                    else
                        c.hasmobile = (rs.GetByte(2).Equals(1) ? true : false);
                    if (rs.IsDBNull(3))
                        c.add = 0;
                    else
                        c.add = rs.GetInt32(3);
                    _AddToAdrcount(ref count, ref c, adrtypes);
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
            return count;
        }


        protected UAdrCount _PolyCount(ref UMapBounds b, ref UPOLYGONSENDING p, long adrtypes)
        {
            int n_maxadr_polycount = 500000;
            UAdrCount count = new UAdrCount();
            OdbcDataReader rs = null;
            try
            {
                String szSQL = "";
                bool bVulnerableCitizensOnly = (adrtypes & (long)ADRTYPES.ONLY_VULNERABLE_CITIZENS) > 0;
                bool bOnlyHeadOfHousehold = (adrtypes & (long)ADRTYPES.ONLY_HEAD_OF_HOUSEHOLD) > 0;
                if (m_n_pastype == 1)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, l_familyno=0, l_personcode=0 FROM ADR_KONSUM " +
                                        "WHERE LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) ORDER BY BEDRIFT, f_hasfixed, f_hasmobile",
                                             n_maxadr_polycount,
                                             b.l_bo, b.r_bo, b.b_bo, b.u_bo);
                }
                else if (m_n_pastype == 2)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, ISNULL(l_familyno,0), ISNULL(l_personcode,0) FROM ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX " +
                                                            "WHERE AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={5} " +
                                                            "AND LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) AND ISNULL(f_hasdisabled,0) in ({6}) {7}",
                                            n_maxadr_polycount,
                                            b.l_bo, b.r_bo, b.b_bo, b.u_bo, m_n_deptpk, bVulnerableCitizensOnly ? "1" : "0,1",
                                            GetRecipientFilter(adrtypes));
                }
                                                                
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                UMapPoint cpoint = new UMapPoint();
                UAdrcountCandidate c = new UAdrcountCandidate();
                //long nPrevFamilyno = 0;
                while (rs.Read())
                {
                    c.lon = rs.GetDouble(1);
                    c.lat = rs.GetDouble(0);
                    c.bedrift = rs.GetInt32(2);
                    c.hasfixed = (rs.GetByte(3) == 1 ? true : false);
                    c.hasmobile = (rs.GetByte(4) == 1 ? true : false);

                    /*
                     * if we're sending to only head of household, only include the first person in the familylist
                     * This may be inaccurate - if first person have no numbers registered, no numbers will be counted, but the next persons numbers may be included in the sending
                     */
                    /*if (bOnlyHeadOfHousehold)
                    {
                        long nFamilyNo = rs.GetInt64(5);
                        int nPersonCode = rs.GetInt32(6);
                        if (nFamilyNo > 0 && nFamilyNo == nPrevFamilyno)
                        {
                            continue;
                        }
                        nPrevFamilyno = nFamilyNo;
                    }*/
                    cpoint.lat = c.lat;
                    cpoint.lon = c.lon;
                    if (p._point_inside(ref cpoint))
                    {
                        //add this address to count
                        _AddToAdrcount(ref count, ref c, adrtypes);
                    }
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
            return count;
        }
      /* method with filters support*/
        protected UAdrCount _PolyCountV2(ref UMapBounds b, ref UPOLYGONSENDING p, long adrtypes)
        {
            if (sz_constring.ToLower().Contains("sweden"))
            {
                return _polyCountForSweden(ref b, ref p, adrtypes);
            }
            else 
            {
                return _PolyCountOtherThanSweden(ref b, ref p, adrtypes);
            }
           
        }

        private UAdrCount _polyCountForSweden(ref UMapBounds b, ref UPOLYGONSENDING p, long adrtypes)
        {
            var xx = from x in p.filters
                     select x.filterId;

            string FilterIds = String.Join(",", new List<int>(xx.ToArray()).ConvertAll(i => i.ToString()).ToArray());


            int n_maxadr_polycount = 500000;
            UAdrCount count = new UAdrCount();
            OdbcDataReader rs = null;
            try
            {
                String szSQL = "";
                bool bVulnerableCitizensOnly = (adrtypes & (long)ADRTYPES.ONLY_VULNERABLE_CITIZENS) > 0;
                bool bOnlyHeadOfHousehold = (adrtypes & (long)ADRTYPES.ONLY_HEAD_OF_HOUSEHOLD) > 0;
                if (m_n_pastype == 1)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, l_familyno=0, l_personcode=0 FROM"
    + " ADR_KONSUM AK INNER JOIN Address_Filters AF ON"
    + " AF.FilterId IN ({5})"
    + " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
        + " AF.FilterId = AAF.FilterId"
        + " AND AK.KOMMUNENR = AAF.municipalId"
        + " AND AK.GATEKODE = AAF.StreetId"
        + " AND AK.HUSNR = isnull(AAF.HouseNo, AK.HUSNR)"
        + " AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
        + " AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
        + " WHERE LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) ORDER BY BEDRIFT, f_hasfixed, f_hasmobile",
                                             n_maxadr_polycount,
                                             b.l_bo, b.r_bo, b.b_bo, b.u_bo, FilterIds);
                }
                else if (m_n_pastype == 2)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, ISNULL(l_familyno,0), ISNULL(l_personcode,0)"
        + " FROM ADR_KONSUM AK INNER JOIN DEPARTMENT_X_MUNICIPAL DX ON"
        + " AK.KOMMUNENR=DX.l_municipalid"
        + " INNER JOIN Address_Filters AF ON"
        + " AF.FilterId IN ({8})"
        + " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
        + " AF.FilterId = AAF.FilterId"
        + " AND AK.KOMMUNENR = AAF.municipalId AND AK.GATEKODE = AAF.StreetId"
        + " AND AK.HUSNR = isnull(AAF.HouseNo, AK.HUSNR)"
        + " AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
        + " AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
        + " WHERE DX.l_deptpk={5} AND LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) AND ISNULL(f_hasdisabled,0) in ({6}) {7}",
                                            n_maxadr_polycount,
                                            b.l_bo, b.r_bo, b.b_bo, b.u_bo, m_n_deptpk, bVulnerableCitizensOnly ? "1" : "0,1",
                                            GetRecipientFilter(adrtypes), FilterIds);
                }

                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                UMapPoint cpoint = new UMapPoint();
                UAdrcountCandidate c = new UAdrcountCandidate();
                //long nPrevFamilyno = 0;
                while (rs.Read())
                {
                    c.lon = rs.GetDouble(1);
                    c.lat = rs.GetDouble(0);
                    c.bedrift = rs.GetInt32(2);
                    c.hasfixed = (rs.GetByte(3) == 1 ? true : false);
                    c.hasmobile = (rs.GetByte(4) == 1 ? true : false);

                    /*
                     * if we're sending to only head of household, only include the first person in the familylist
                     * This may be inaccurate - if first person have no numbers registered, no numbers will be counted, but the next persons numbers may be included in the sending
                     */
                    /*if (bOnlyHeadOfHousehold)
                    {
                        long nFamilyNo = rs.GetInt64(5);
                        int nPersonCode = rs.GetInt32(6);
                        if (nFamilyNo > 0 && nFamilyNo == nPrevFamilyno)
                        {
                            continue;
                        }
                        nPrevFamilyno = nFamilyNo;
                    }*/
                    cpoint.lat = c.lat;
                    cpoint.lon = c.lon;
                    if (p._point_inside(ref cpoint))
                    {
                        //add this address to count
                        _AddToAdrcount(ref count, ref c, adrtypes);
                    }
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
            return count;
       
        }
        private UAdrCount _PolyCountOtherThanSweden(ref UMapBounds b, ref UPOLYGONSENDING p, long adrtypes) 
        {
             var xx = from x in p.filters
                     select x.filterId;
            
            string FilterIds = String.Join(",", new List<int>(xx.ToArray()).ConvertAll(i => i.ToString()).ToArray());


            int n_maxadr_polycount = 500000;
            UAdrCount count = new UAdrCount();
            OdbcDataReader rs = null;
            try
            {
                String szSQL = "";
                bool bVulnerableCitizensOnly = (adrtypes & (long)ADRTYPES.ONLY_VULNERABLE_CITIZENS) > 0;
                bool bOnlyHeadOfHousehold = (adrtypes & (long)ADRTYPES.ONLY_HEAD_OF_HOUSEHOLD) > 0;
                if (m_n_pastype == 1)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, l_familyno=0, l_personcode=0 FROM"
        + " ADR_KONSUM AK INNER JOIN Address_Filters AF ON"
        + " AF.FilterId IN ({5})"
        + " INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
        + " AF.FilterId = AAF.FilterId"
        + " AND AK.KOMMUNENR = AAF.municipalId"
        + " AND AK.GATEKODE = AAF.StreetId"
        + " AND AK.HUSNR = isnull(AAF.HouseNo, AK.HUSNR)"
        + " AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
        + " AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
        + " AND AK.BNR = CASE WHEN (AAF.Bno=0) THEN AK.BNR ELSE AAF.Bno END"
        + " AND AK.FNR = CASE WHEN (AAF.Fno=0) THEN AK.FNR ELSE AAF.Fno END"
        + " AND AK.SNR = CASE WHEN (AAF.Sno=0) THEN AK.SNR ELSE AAF.Sno END"
        + " AND AK.UNR = CASE WHEN (AAF.Uno=0) THEN AK.UNR ELSE AAF.Uno END"
        + " WHERE LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) ORDER BY BEDRIFT, f_hasfixed, f_hasmobile",
                                             n_maxadr_polycount,
                                             b.l_bo, b.r_bo, b.b_bo, b.u_bo,FilterIds);
                }
                else if (m_n_pastype == 2)
                {
                    szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT TOP {0} LON, LAT, BEDRIFT, ISNULL(f_hasfixed,0) f_hasfixed, ISNULL(f_hasmobile,0) f_hasmobile, ISNULL(l_familyno,0), ISNULL(l_personcode,0)"
        +" FROM ADR_KONSUM AK INNER JOIN DEPARTMENT_X_MUNICIPAL DX ON"
        +" AK.KOMMUNENR=DX.l_municipalid"
        +" INNER JOIN Address_Filters AF ON"
        +" AF.FilterId IN ({8})" 
        +" INNER JOIN ADDRESSASSOCIATEDwithFILTER AAF ON"
		+" AF.FilterId = AAF.FilterId"
		+" AND AK.KOMMUNENR = AAF.municipalId AND AK.GATEKODE = AAF.StreetId"
		+" AND AK.HUSNR = isnull(AAF.HouseNo, AK.HUSNR)"
	    +" AND AK.OPPGANG = isnull(AAF.Sz_HouseLetter, AK.OPPGANG)"
        +" AND AK.sz_apartmentid = isnull(AAF.Sz_ApartmentId, AK.sz_apartmentid)"
        + " AND AK.BNR = CASE WHEN (AAF.Bno=0) THEN AK.BNR ELSE AAF.Bno END"
        + " AND AK.FNR = CASE WHEN (AAF.Fno=0) THEN AK.FNR ELSE AAF.Fno END"
        + " AND AK.SNR = CASE WHEN (AAF.Sno=0) THEN AK.SNR ELSE AAF.Sno END"
        + " AND AK.UNR = CASE WHEN (AAF.Uno=0) THEN AK.UNR ELSE AAF.Uno END"                                              
        +" WHERE DX.l_deptpk={5} AND LAT>={1} AND LAT<={2} AND LON>={3} AND LON<={4} AND BEDRIFT IN (0,1) AND ISNULL(f_hasdisabled,0) in ({6}) {7}",
                                            n_maxadr_polycount,
                                            b.l_bo, b.r_bo, b.b_bo, b.u_bo, m_n_deptpk, bVulnerableCitizensOnly ? "1" : "0,1",
                                            GetRecipientFilter(adrtypes),FilterIds);
                }

                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                UMapPoint cpoint = new UMapPoint();
                UAdrcountCandidate c = new UAdrcountCandidate();
                //long nPrevFamilyno = 0;
                while (rs.Read())
                {
                    c.lon = rs.GetDouble(1);
                    c.lat = rs.GetDouble(0);
                    c.bedrift = rs.GetInt32(2);
                    c.hasfixed = (rs.GetByte(3) == 1 ? true : false);
                    c.hasmobile = (rs.GetByte(4) == 1 ? true : false);

                    /*
                     * if we're sending to only head of household, only include the first person in the familylist
                     * This may be inaccurate - if first person have no numbers registered, no numbers will be counted, but the next persons numbers may be included in the sending
                     */
                    /*if (bOnlyHeadOfHousehold)
                    {
                        long nFamilyNo = rs.GetInt64(5);
                        int nPersonCode = rs.GetInt32(6);
                        if (nFamilyNo > 0 && nFamilyNo == nPrevFamilyno)
                        {
                            continue;
                        }
                        nPrevFamilyno = nFamilyNo;
                    }*/
                    cpoint.lat = c.lat;
                    cpoint.lon = c.lon;
                    if (p._point_inside(ref cpoint))
                    {
                        //add this address to count
                        _AddToAdrcount(ref count, ref c, adrtypes);
                    }
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
            return count;
        }
        public UAddressList GetAddresslistByQuality(ref ULOGONINFO logon, ref UMapAddressParamsByQuality param, PercentProgress.SetPercentDelegate percentCallback)
        {
            PercentResult percent = new PercentResult();
            percent.n_currentrecord = 0;
            percent.n_percent = 10;
            percent.n_totalrecords = 0;
            UAddressList list = new UAddressList();
            OdbcDataReader rs = null;
            try
            {
                String xycodes = "";
                for (int i = 0; i < param.arr_xycodes.Count; i++)
                {
                    if (i > 0)
                        xycodes += ",";
                    xycodes += param.arr_xycodes[i].ToString();
                }
                String szSQL = "";
                if(m_n_pastype==1)
                    szSQL = String.Format("sp_getadr_byquality '{0}', '{1}'", param.sz_postno, xycodes);
                else if(m_n_pastype==2)
                    szSQL = String.Format("sp_getadr_byquality '{0}', '{1}', {2}", param.sz_postno, xycodes, logon.l_deptpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                percent.n_totalrecords = rs.RecordsAffected;
                int counter = 0;
                while (rs.Read())
                {
                    UAddress adr = new UAddress();
                    readAddressFromDbByFieldnames(ref adr, ref rs, false);
                    list.addLine(adr);
                    counter++;
                    percent.n_currentrecord = counter;
                    percent.n_percent = (int)(percent.n_currentrecord * 100.0 / percent.n_totalrecords);
                    percentCallback(ref logon, ProgressJobType.HOUSE_DOWNLOAD, percent);
                }
                list.finalize();
                rs.Close();
                return list;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
                percent.n_percent = 100;
                percentCallback(ref logon, ProgressJobType.HOUSE_DOWNLOAD, percent);
            }
        }




        public int GetGisImport(ref List<UGisImportResultLine> p, int startat, int max, ref int next, ref int skiplines, bool only_coors)
        {
            try
            {
                String szSQL = "";
                int i = 0;
                int n_validcount = 0;
                for (i = startat; i < p.Count; i++)
                {
                    if (p[i].isValid())
                    {
                        if (n_validcount > 0) //there are more sql before this one, add union
                            szSQL += " UNION ";
                        if (only_coors) // large import file, select only limited imformatio
                        {
                            if (p[i].propertyField) // property import
                            {
                                if (m_n_pastype == 1)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(BEDRIFT,0) BEDRIFT, isnull(f_hasfixed,0) f_hasfixed, isnull(f_hasmobile,0) f_hasmobile, arr_indexnumber={3}, f_hasdisabled=0 " +
                                                                                        "FROM ADR_KONSUM WHERE KOMMUNENR={0} AND GNR={1} AND BNR={2} AND BEDRIFT<3", // IN (0,1)",
                                                                                        p[i].municipalid, p[i].gnr, p[i].bnr, p[i].n_linenumber - skiplines);
                                else if (m_n_pastype == 2)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(BEDRIFT,0) BEDRIFT, isnull(f_hasfixed,0) f_hasfixed, isnull(f_hasmobile,0) f_hasmobile, arr_indexnumber={3}, isnull(f_hasdisabled,0) f_hasdisabled " +
                                                                                        "FROM ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX WHERE KOMMUNENR={0} AND GNR={1} AND BNR={2} AND BEDRIFT<3 AND AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={4}", //BEDRIFT IN (0,1)
                                                                                        p[i].municipalid, p[i].gnr, p[i].bnr, p[i].n_linenumber - skiplines, m_n_deptpk);

                                if (p[i].fnr != null && p[i].fnr.Trim().Length > 0 && !p[i].fnr.Trim().Equals("0")) // ignore if fnr is empty or 0
                                {
                                    szSQL += " AND FNR=" + p[i].fnr.Trim();
                                }
                                if (p[i].snr != null && p[i].snr.Trim().Length > 0 && !p[i].snr.Trim().Equals("0")) // ignore if snr is empty or 0
                                {
                                    szSQL += " AND SNR=" + p[i].snr.Trim();
                                }
                                if (p[i].apartmentid != null && p[i].apartmentid.Trim().Length > 0 && !p[i].apartmentid.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND sz_apartmentid='" + p[i].apartmentid.Trim() + "'";
                                }
                            }
                            else // street import
                            {
                                if (m_n_pastype == 1)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(BEDRIFT,0) BEDRIFT, isnull(f_hasfixed,0) f_hasfixed, isnull(f_hasmobile,0) f_hasmobile, arr_indexnumber={2}, f_hasdisabled=0 " +
                                                                                        "FROM ADR_KONSUM WHERE KOMMUNENR={0} AND GATEKODE={1} AND BEDRIFT<3", // IN (0,1)",
                                                                                        p[i].municipalid, p[i].streetid, p[i].n_linenumber - skiplines);
                                else if (m_n_pastype == 2)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(BEDRIFT,0) BEDRIFT, isnull(f_hasfixed,0) f_hasfixed, isnull(f_hasmobile,0) f_hasmobile, arr_indexnumber={2}, isnull(f_hasdisabled,0) f_hasdisabled " +
                                                                                        "FROM ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX WHERE KOMMUNENR={0} AND GATEKODE={1} AND BEDRIFT<3 AND AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={3}", //BEDRIFT IN (0,1)
                                                                                        p[i].municipalid, p[i].streetid, p[i].n_linenumber - skiplines, m_n_deptpk);

                                if (p[i].houseno != null && p[i].houseno.Trim().Length > 0 && !p[i].houseno.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND HUSNR=" + p[i].houseno;
                                }
                                if (p[i].letter != null && p[i].letter.Trim().Length > 0 && !p[i].letter.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND OPPGANG='" + p[i].letter.Trim() + "'";
                                }
                                if (p[i].apartmentid != null && p[i].apartmentid.Trim().Length > 0 && !p[i].apartmentid.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND sz_apartmentid='" + p[i].apartmentid.Trim() + "'";
                                }
                            }
                        }
                        else // not so many lines, select everything
                        {
                            if (p[i].propertyField) // property import
                            {
                                if (m_n_pastype == 1)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber={3}, f_hasdisabled=0 FROM " +
                                                             "ADR_KONSUM WHERE KOMMUNENR={0} AND GNR={1} AND BNR={2} AND BEDRIFT<3", // IN (0,1)",
                                                                                        p[i].municipalid, p[i].gnr, p[i].bnr, p[i].n_linenumber - skiplines);
                                else if (m_n_pastype == 2)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber={3}, f_hasdisabled=0 FROM " +
                                                             "ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX  WHERE KOMMUNENR={0} AND GNR={1} AND BNR={2} AND BEDRIFT<3 AND AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={4}", //BEDRIFT IN (0,1)
                                                                                        p[i].municipalid, p[i].gnr, p[i].bnr, p[i].n_linenumber - skiplines, m_n_deptpk);

                                if (p[i].fnr != null && p[i].fnr.Trim().Length > 0 && !p[i].fnr.Trim().Equals("0")) // ignore if fnr is empty or 0
                                {
                                    szSQL += " AND FNR=" + p[i].fnr.Trim();
                                }
                                if (p[i].snr != null && p[i].snr.Trim().Length > 0 && !p[i].snr.Trim().Equals("0")) // ignore if snr is empty or 0
                                {
                                    szSQL += " AND SNR=" + p[i].snr.Trim();
                                }
                                if (p[i].apartmentid != null && p[i].apartmentid.Trim().Length > 0 && !p[i].apartmentid.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND sz_apartmentid='" + p[i].apartmentid.Trim() + "'";
                                }
                            }
                            else // street import
                            {
                                if (m_n_pastype == 1)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber={2}, f_hasdisabled=0 FROM " +
                                                             "ADR_KONSUM WHERE KOMMUNENR={0} AND GATEKODE={1} AND BEDRIFT IN (0,1)",
                                                             p[i].municipalid, p[i].streetid, p[i].n_linenumber - skiplines);
                                else if (m_n_pastype == 2)
                                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber={2}, isnull(f_hasdisabled,0) f_hasdisabled FROM " +
                                                             "ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX WHERE KOMMUNENR={0} AND GATEKODE={1} AND BEDRIFT IN (0,1) AND AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={3}",

                                     p[i].municipalid, p[i].streetid, p[i].n_linenumber - skiplines, m_n_deptpk);

                                if (p[i].houseno != null && p[i].houseno.Trim().Length > 0 && !p[i].houseno.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND HUSNR=" + p[i].houseno;
                                }
                                if (p[i].letter != null && p[i].letter.Trim().Length > 0 && !p[i].letter.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND OPPGANG='" + p[i].letter.Trim() + "'";
                                }
                                if (p[i].apartmentid != null && p[i].apartmentid.Trim().Length > 0 && !p[i].apartmentid.Trim().Equals("0")) // ignore if empty or 0
                                {
                                    szSQL += " AND sz_apartmentid='" + p[i].apartmentid.Trim() + "'";
                                }

                            }
                        }
                        n_validcount++;
                        if (n_validcount >= max)
                            break;
                    }
                }
                if (n_validcount > 0)
                {
                     OdbcDataReader rs = null;
                    try
                    {
                        //m_cmd = new OdbcCommand(szSQL, conn);
                        //rs = m_cmd.ExecuteReader();
                        rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN, !b_prepared);
                        b_prepared = true;
                        while (rs.Read())
                        {
                            if (only_coors)
                            {
                                UAddressBasics adr = new UAddressBasics();
                                adr = new UAddressBasics();
                                readAddressFromDb(ref adr, ref rs);
                                if (adr.arrayindex >= 0)
                                {
                                    p[adr.arrayindex].list.addLine(ref adr);
                                    //p[adr.arrayindex].list.finalize();
                                }
                            }
                            else
                            {
                                UAddress adr = new UAddress();
                                adr = new UAddress();
                                readAddressFromDb(ref adr, ref rs, only_coors);
                                if (adr.arrayindex >= 0)
                                {
                                    p[adr.arrayindex].list.addLine(adr);
                                    //p[adr.arrayindex].list.finalize();
                                }
                            }

                        }
                        rs.Close();
                    }
                    catch (Exception)
                    {
                        next = startat;
                        throw;
                    }
                    finally
                    {
                        if (rs != null && !rs.IsClosed)
                            rs.Close();
                    }
                }
                /*if (i >= p.Count)
                {
                    return -1;
                }*/
                //return (i+1); //return next
                next = (i + 1);
                return i - startat + 1;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public UAddressList FindAddressesByDistance(UMapDistanceParams param)
        {
            /*
             * delta_y = abs(meters)/EARTH_CIRCUMFERENCE
             * delta_x = abs(meters)*cos(param.lat)/EARTH_CIRCUMFERENCE
             * candidates = LON>=param.lat - delta_y, LON<=param.lat + delta_y, LAT>=param.lon - delta_x, LAT<=param.lon + delta_x
             */
            double delta_y = Math.Abs(param.distance) / UCommon.UEARTH_CIRCUMFERENCE;
            double delta_x = Math.Abs(param.distance) / Math.Cos(UCommon.UDEG2RAD(param.lat)) / UCommon.UEARTH_CIRCUMFERENCE;
            UMapAddressParams adr = new UMapAddressParams();
            adr.l_bo = (float)(param.lon - delta_x);
            adr.r_bo = (float)(param.lon + delta_x);
            adr.u_bo = (float)(param.lat + delta_y);
            adr.b_bo = (float)(param.lat - delta_y);
            return GetAddresslist(adr);
        }
        public UAddressList GetAddresslist(UMapAddressParams param)
        {
            UAddressList list = new UAddressList();
/*
sprintf(szSQL,  "SELECT isnull(KON_DMID, 0) KON_DMID, NAVN, ADRESSE, isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, POSTNR, POSTSTED, isnull(KOMMUNENR, 0) KOMMUNENR, FØDTÅR, TELEFON, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, MOBIL, isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, f_hasfixed, f_hasmobile FROM "
    "ADR_KONSUM WHERE %s AND BEDRIFT IN (0,1)", sz_filter1);
*/
            String szSQL = "";
            if (double.IsNaN(param.b_bo) ||
                double.IsNaN(param.u_bo) ||
                double.IsNaN(param.l_bo) ||
                double.IsNaN(param.r_bo))
            {
                return list;
            }
            if (param.b_bo > 90 || param.u_bo < -90 ||
                param.l_bo > 180 && param.r_bo <= -180)
            {
                return list;
            }

            // Ger recipient filter (always include private residents even though they aren't flagged in the db)
            PASUmsDb db = new PASUmsDb();
            string recipientFilter = GetRecipientFilter(db.GetDepartmentAddresstypes(m_n_deptpk) | (int)ADRTYPES.RECIPTYPE_PRIVATE_RESIDENT);

            if (m_n_pastype == 1)
            {
                szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber=0, f_hasdisabled=0 FROM " +
                                             "ADR_KONSUM WHERE LON>{0} AND LON<={1} AND LAT>={2} AND LAT<{3} AND BEDRIFT IN (0,1)",
                                             param.b_bo, param.u_bo, param.l_bo, param.r_bo);
            }
            else if (m_n_pastype == 2)
            {
                szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber=0, isnull(f_hasdisabled,0) f_hasdisabled FROM " +
                                             "ADR_KONSUM AK, DEPARTMENT_X_MUNICIPAL DX WHERE LON>{0} AND LON<={1} AND LAT>={2} AND LAT<{3} AND BEDRIFT IN (0,1) AND AK.KOMMUNENR=DX.l_municipalid AND DX.l_deptpk={4}{5}",
                                             param.b_bo, param.u_bo, param.l_bo, param.r_bo, m_n_deptpk, recipientFilter);
            }
            OdbcDataReader rs = null;
            try
            {
                rs = base.ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UAddress adr = new UAddress();
                    UAddressBasics _adr = (UAddressBasics)adr;
                    readAddressFromDb(ref adr, ref rs, false);
                    list.addLine(adr);
                }

                list.finalize();
                rs.Close();

                return list;
            }
            catch(Exception e)
            {
                ULog.error(0, "Error in retrieving addresses", e.Message);
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
                
        }
        public bool readAddressFromDbByFieldnames(ref UAddress adr, ref OdbcDataReader rs, bool only_coors)
        {
            try
            {
                adr.kondmid = rs["KON_DMID"].ToString();
            }
            catch (Exception)
            {
                adr.kondmid = "0";
            }
            try
            {
                adr.lon = float.Parse(rs["LON"].ToString());
            }
            catch (Exception)
            {
                adr.lon = 0;
            }
            try
            {
                adr.lat = float.Parse(rs["LAT"].ToString());
            }
            catch (Exception)
            {
                adr.lat = 0;
            }
            if (only_coors)
            {
                try
                {
                    adr.bedrift = Int32.Parse(rs["BEDRIFT"].ToString());
                }
                catch (Exception)
                {
                    adr.bedrift = 0;
                }
                try
                {
                    adr.hasfixed = Int32.Parse(rs["f_hasfixed"].ToString());
                }
                catch (Exception)
                {
                    adr.hasfixed = 0;
                }
                try
                {
                    adr.hasmobile = Int32.Parse(rs["f_hasmobile"].ToString());
                }
                catch (Exception)
                {
                    adr.hasmobile = 0;
                }
                try
                {
                    adr.arrayindex = Int32.Parse(rs["arrayindex"].ToString());
                }
                catch (Exception)
                {
                    adr.arrayindex = -1;
                }
                return true;
            }
            try
            {
                adr.name = rs["NAVN"].ToString();
            }
            catch (Exception)
            {
                adr.name = "";
            }
            try
            {
                adr.address = rs["ADRESSE"].ToString();
            }
            catch (Exception)
            {
                adr.address = "";
            }
            try
            {
                adr.houseno = Int32.Parse(rs["HUSNR"].ToString());
            }
            catch (Exception)
            {
                adr.houseno = 0;
            }
            try
            {
                adr.letter = rs["OPPGANG"].ToString();
            }
            catch (Exception)
            {
                adr.letter = "";
            }
            try
            {
                adr.postno = rs["POSTNR"].ToString();
            }
            catch (Exception)
            {
                adr.postno = "";
            }
            try
            {
                adr.postarea = rs["POSTSTED"].ToString();
            }
            catch (Exception)
            {
                adr.postarea = "";
            }
            try
            {
                adr.region = Int32.Parse(rs["KOMMUNENR"].ToString());
            }
            catch (Exception)
            {
                adr.region = 0;
            }
            try
            {
                adr.bday = rs["FØDTÅR"].ToString();
            }
            catch (Exception)
            {
                adr.bday = "0";
            }
            //adr.bday = 0;
            try
            {
                adr.number = rs["TELEFON"].ToString();
            }
            catch (Exception)
            {
                adr.number = "";
            }
            try
            {
                adr.gno = Int32.Parse(rs["GNR"].ToString());
            }
            catch (Exception)
            {
                adr.gno = 0;
            }
            try
            {
                adr.bno = Int32.Parse(rs["BNR"].ToString());
            }
            catch (Exception)
            {
                adr.bno = 0;
            }
            try
            {
                adr.bedrift = Int32.Parse(rs["BEDRIFT"].ToString());
            }
            catch (Exception)
            {
                adr.bedrift = 0;
            }
            try
            {
                adr.importid = Int32.Parse(rs["l_importid"].ToString());
            }
            catch (Exception)
            {
                adr.importid = -1;
            }
            try
            {
                adr.mobile = rs["MOBIL"].ToString();
            }
            catch (Exception)
            {
                adr.mobile = "";
            }
            try
            {
                adr.streetid = Int32.Parse(rs["GATEKODE"].ToString());
            }
            catch (Exception)
            {
                adr.streetid = 0;
            }
            try
            {
                adr.xycode = rs["QUALITY"].ToString();
            }
            catch (Exception)
            {
                adr.xycode = "a";
            }
            try
            {
                adr.hasfixed = Int32.Parse(rs["f_hasfixed"].ToString());
            }
            catch (Exception)
            {
                adr.hasfixed = 0;
            }
            try
            {
                adr.hasmobile = Int32.Parse(rs["f_hasmobile"].ToString());
            }
            catch (Exception)
            {
                adr.hasmobile = 0;
            }
            try
            {
                adr.arrayindex = Int32.Parse(rs["arrayindex"].ToString());
            }
            catch (Exception)
            {
                adr.arrayindex = -1;
            }
            return true;
    
        }

        protected bool readAddressFromDb(ref UAddressBasics adr, ref OdbcDataReader rs)
        {
            try
            {
                adr.kondmid = rs.GetString(0);
            }
            catch (Exception)
            {
                adr.kondmid = "0";
            }
            try
            {
                adr.lon = rs.GetDouble(1);
            }
            catch (Exception)
            {
                adr.lon = 0;
            }
            try
            {
                adr.lat = rs.GetDouble(2);
            }
            catch (Exception)
            {
                adr.lat = 0;
            }
            try
            {
                adr.bedrift = rs.GetInt32(3);
            }
            catch (Exception)
            {
                adr.bedrift = 0;
            }
            try
            {
                adr.hasfixed = rs.GetInt32(4);
            }
            catch (Exception)
            {
                adr.hasfixed = 0;
            }
            try
            {
                adr.hasmobile = rs.GetInt32(5);
            }
            catch (Exception)
            {
                adr.hasmobile = 0;
            }
            try
            {
                adr.arrayindex = rs.GetInt32(6);
            }
            catch (Exception)
            {
                adr.arrayindex = -1;
            }
            try
            {
                adr.hasdisabled = rs.GetInt32(7);
            }
            catch (Exception)
            {
                adr.hasdisabled = 0;
            }
            return true;

        }

        public bool readAddressFromDb(ref UAddress adr, ref OdbcDataReader rs, bool only_coors)
        {
            try
            {
                adr.kondmid = rs.GetString(0);
            }
            catch (Exception)
            {
                adr.kondmid = "0";
            }
            try
            {
                adr.lon = rs.GetDouble(1);
            }
            catch (Exception)
            {
                adr.lon = 0;
            }
            try
            {
                adr.lat = rs.GetDouble(2);
            }
            catch (Exception)
            {
                adr.lat = 0;
            }
            if (only_coors)
            {
                try
                {
                    adr.bedrift = rs.GetInt32(3);
                }
                catch (Exception)
                {
                    adr.bedrift = 0;
                }
                try
                {
                    adr.hasfixed = rs.GetInt32(4);
                }
                catch (Exception)
                {
                }
                try
                {
                    adr.hasmobile = rs.GetInt32(5);
                }
                catch (Exception)
                {
                }
                try
                {
                    adr.arrayindex = rs.GetInt32(6);
                }
                catch (Exception)
                {
                    adr.arrayindex = -1;
                }
                return true;
            }
            try
            {
                adr.name = rs.GetString(3);
            }
            catch (Exception)
            {
                adr.name = "";
            }
            try
            {
                adr.address = rs.GetString(4);
            }
            catch (Exception)
            {
                adr.address = "";
            }
            try
            {
                adr.houseno = rs.GetInt32(5);
            }
            catch (Exception)
            {
                adr.houseno = 0;
            }
            try
            {
                adr.letter = rs.GetString(6);
            }
            catch (Exception)
            {
                adr.letter = "";
            }
            try
            {
                adr.postno = rs.GetString(7);
            }
            catch (Exception)
            {
                adr.postno = "";
            }
            try
            {
                adr.postarea = rs.GetString(8);
            }
            catch (Exception)
            {
                adr.postarea = "";
            }
            try
            {
                adr.region = rs.GetInt32(9);
                adr.municipalid = adr.region.ToString();
            }
            catch (Exception)
            {
                adr.region = 0;
            }
            try
            {
                adr.bday = rs.GetString(10);
            }
            catch (Exception)
            {
                adr.bday = "0";
            }
            //adr.bday = 0;
            try
            {
                adr.number = rs.GetString(11);
            }
            catch (Exception)
            {
                adr.number = "";
            }
            try
            {
                adr.gno = rs.GetInt32(12);
            }
            catch (Exception)
            {
                adr.gno = 0;
            }
            try
            {
                adr.bno = rs.GetInt32(13);
            }
            catch (Exception)
            {
                adr.bno = 0;
            }
            try
            {
                adr.bedrift = rs.GetInt32(14);
            }
            catch (Exception)
            {
                adr.bedrift = 0;
            }
            try
            {
                adr.importid = rs.GetInt32(15);
            }
            catch (Exception)
            {
                adr.importid = -1;
            }
            try
            {
                adr.mobile = rs.GetString(16);
            }
            catch (Exception)
            {
                adr.mobile = "";
            }
            try
            {
                adr.streetid = rs.GetInt32(17);
            }
            catch (Exception)
            {
                adr.streetid = 0;
            }
            try
            {
                adr.xycode = rs.GetString(18);
            }
            catch (Exception)
            {
                adr.xycode = "a";
            }
            try
            {
                adr.hasfixed = rs.GetInt16(19);
            }
            catch (Exception)
            {
                adr.hasfixed = 0;
            }
            try
            {
                adr.hasmobile = rs.GetInt16(20);
            }
            catch (Exception)
            {
                adr.hasmobile = 0;
            }
            try
            {
                adr.arrayindex = rs.GetInt32(21);
            }
            catch (Exception)
            {
                adr.arrayindex = -1;
            }
            try
            {
                adr.hasdisabled = rs.GetInt32(22);
            }
            catch (Exception)
            {
                adr.hasdisabled = 0;
            }
            return true;
        }

        public bool InsertInhabitant(ref UAddress adr, ref ULOGONINFO l)
        {
            OdbcDataReader rs = null;
            try
            {
                
                OdbcCommand cmd = new OdbcCommand();
                cmd.Connection = conn;
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.CommandText = "{ CALL sp_ins_adr (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                cmd.Parameters.Add("@sz_name", OdbcType.VarChar, 50).Value = adr.name;
                cmd.Parameters.Add("@sz_phone", OdbcType.VarChar, 30).Value = adr.number;
                cmd.Parameters.Add("@sz_mobile", OdbcType.VarChar, 30).Value = adr.mobile;
                cmd.Parameters.Add("@sz_birthday", OdbcType.VarChar, 20).Value = adr.bday;
                cmd.Parameters.Add("@sz_address", OdbcType.VarChar, 50).Value = adr.address;
                if (adr.houseno == -1)
                    cmd.Parameters.Add("@sz_house", OdbcType.Int).Value = DBNull.Value;
                else
                    cmd.Parameters.Add("@sz_house", OdbcType.Int).Value = adr.houseno;
                cmd.Parameters.Add("@sz_letter", OdbcType.VarChar, 5).Value = adr.letter;
                cmd.Parameters.Add("@sz_postno", OdbcType.VarChar, 10).Value = adr.postno;
                cmd.Parameters.Add("@sz_place", OdbcType.VarChar, 50).Value = adr.postarea;
                cmd.Parameters.Add("@sz_gnr", OdbcType.Int).Value = adr.gno;
                cmd.Parameters.Add("@sz_bnr", OdbcType.Int).Value = adr.bno;
                cmd.Parameters.Add("@sz_municipalid", OdbcType.VarChar, 30).Value = adr.municipalid;
                cmd.Parameters.Add("@sz_streetid", OdbcType.Int).Value = adr.streetid;
                cmd.Parameters.Add("@f_lon", OdbcType.Double).Value = adr.lat;
                cmd.Parameters.Add("@f_lat", OdbcType.Double).Value = adr.lon;
                cmd.Parameters.Add("@sz_bedrift", OdbcType.Int).Value = adr.bedrift;
                cmd.Parameters.Add("@sz_importid", OdbcType.Int).Value = l.l_deptpk;

                rs = cmd.ExecuteReader();

                /* Had to use command to accomodate null value in houseno
                String szSQL = String.Format(UCommon.UGlobalizationInfo, "sp_ins_adr '{0}', '{1}', '{2}', '{3}', '{4}', {5}, '{6}', '{7}', " +
                   "'{8}', {9}, {10}, {11}, {12}, {13}, {14}, {15}, {16}",
                   adr.name, adr.number, adr.mobile, adr.bday, adr.address, adr.houseno,
                   adr.letter, adr.postno, adr.postarea, adr.gno, adr.bno, adr.municipalid,
                   adr.streetid, adr.lat, adr.lon, adr.bedrift, l.l_deptpk);

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);*/
                if (rs.Read())
                {
                    adr.kondmid = rs.GetString(0);
                    rs.Close();
                    cmd.Dispose();
                    conn.Close();
                    return true;
                }
                rs.Close();
                cmd.Dispose();
                conn.Close();
                return false;
            }
            catch (Exception e)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }
        public bool UpdateInhabitant(ref UAddress adr, ref ULOGONINFO l)
        {
            OdbcDataReader rs = null;

            String szSQL = String.Empty;
            String szSQLError = this.sz_constring;
            try
            {
                szSQL = String.Format(UCommon.UGlobalizationInfo, "sp_copy_adr {0}, {1}, {2}, {3}",
                                l.l_deptpk, adr.lat, adr.lon, adr.kondmid);
                rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    //adr.kondmid = rs.GetString(0);
                    Int64 kondmid = rs.GetInt64(0);
                    if (kondmid <= 0)
                    {
                        String szError = "Unable to make a copy of address with id="+adr.kondmid;
                        ULog.error(0, szError, szSQLError + " " + szSQL);
                        throw new UDbQueryException(szError);
                    }
                    adr.kondmid = kondmid.ToString();
                    rs.Close();
                }
                rs.Close();
                szSQL = String.Format(UCommon.UGlobalizationInfo, "sp_update_adr {0}, '{1}', '{2}', '{3}', '{4}', '{5}', {6}, '{7}', '{8}', '{9}', {10}, {11}, {12}, {13}",
                               adr.kondmid, adr.name, adr.mobile, adr.number, adr.bday, adr.address, adr.houseno, adr.letter, adr.postno, adr.postarea, adr.municipalid, adr.gno, adr.bno, adr.streetid);
                ExecNonQuery(szSQL);
                return true;
                //adr.kondmid = "-1";
                //return false;
                throw new UDbNoDataException("Error while moving inhabitant");
            }
            catch (Exception e)
            {
                String szError = "Unable to make a copy and update address with id=" + adr.kondmid;
                ULog.error(0, szError, szSQLError + " " + szSQL);
                throw new UDbQueryException(szError);
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
        }
        public bool DeleteInhabitant(ref UAddress adr, ref ULOGONINFO l)
        {
            OdbcDataReader rs = null;
            try
            {
                String szSQL = "";
                if (m_n_pastype == 1)
                {
                    szSQL = String.Format("DELETE FROM ADR_KONSUM WHERE KON_DMID={0} AND l_importid={1}",
                        adr.kondmid, adr.importid);
                }
                else if (m_n_pastype == 2)
                {
                    szSQL = String.Format("DELETE FROM ADR_EDITED WHERE KON_DMID={0} AND l_importid={1}",
                        adr.kondmid, adr.importid);
                }
                else
                    throw new UDbNoDataException("No access");
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.RecordsAffected > 0)
                {
                    rs.Close();
                    return true;
                }
                rs.Close();

                throw new UDbNoDataException("Record not found");
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
