using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.Collections.Generic;
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

            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc, 60, m_logon.l_deptpk);
            UAddressList list = db.GetAddresslist(m_search);
            db.close();
            return list;
        }
    }

    public class UGisImportLookup : IAddressSearch
    {
        protected UGisImportParamsByStreetId m_search;
        protected ULOGONINFO m_logon;
        protected PercentProgress.SetPercentDelegate m_callback;
        protected ProgressJobType jobType = ProgressJobType.GEMINI_IMPORT_STREETID;

        public UGisImportLookup(ref UGisImportParamsByStreetId search, ref ULOGONINFO logon, PercentProgress.SetPercentDelegate percentCallback, ProgressJobType jobType)
        {
            this.jobType = jobType;
            m_search = search;
            m_logon = logon;
            m_callback = percentCallback;
        }

        public UGisImportLookup(ref ULOGONINFO logon, PercentProgress.SetPercentDelegate percentCallback, ProgressJobType jobType)
        {
            this.jobType = jobType;
            m_logon = logon;
            m_callback = percentCallback;
        }
        public UGisImportResultsByStreetId SearchDatabase(ref List<UGisImportResultLine> filelines, bool only_coors)
        {
            UGisImportResultsByStreetId ret = new UGisImportResultsByStreetId();
            try
            {
                UmsDbConnParams conn = new UmsDbConnParams();
                conn.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase;
                conn.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
                conn.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;
                UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc, UCommon.USETTINGS.l_gisimport_db_timeout, m_logon.l_deptpk);

                /*
                 * OLD ONE BY ONE LINE VERSION
                for (int x = 0; x < filelines.Count; x++)
                {
                    UGisImportResultLine tmp = filelines[x];
                    if (tmp.isValid())
                    {
                        int i = db.GetGisImport(ref tmp);
                        tmp.list.finalize();
                    }

                    tmp.finalize();
                    ret.addLine(ref tmp);
                }*/
                int x = 0;
                int prev = 0;
                int next = 0;
                //UGisImportResultLine[] lines = filelines.ToArray();
                double n_tables = (double)UCommon.USETTINGS.l_folkereg_num_adrtables;
                //int chunksize = (int)Math.Floor(255.0 / n_tables);
                int chunksize = UCommon.USETTINGS.l_gisimport_chunksize;
                float max = filelines.Count;
                PercentResult result = new PercentResult();
                result.n_totalrecords = filelines.Count;
                result.n_currentrecord = 0;
                result.n_percent = 0;
                m_callback(ref m_logon, jobType, result);
                while (1 == 1)
                {
                    try
                    {
                        x = db.GetGisImport(ref filelines, next, chunksize, ref next, ref m_search.SKIPLINES, only_coors);
                    }
                    catch (Exception e)
                    {
                        ULog.error(e.Message);
                        throw;
                    }
                    //if (!only_coors)
                    {
                        for (int add = prev; add < prev + (x == chunksize ? x : x - 1); add++)
                        {
                            if (add >= filelines.Count)
                            {
                                String s = "";
                            }
                            else
                            {
                                UGisImportResultLine tmp = filelines[add];
                                ret.addLine(ref tmp);
                                tmp.list.finalize();
                            }
                        }
                    }
                    try
                    {
                        result.n_currentrecord = next;
                        if (max > 0)
                            result.n_percent = (int)((next * 100.0) / max);
                        else
                            result.n_percent = 100;
                        m_callback(ref m_logon, jobType, result);
                    }
                    catch (Exception) { }
                    prev = next;
                    if (next >= filelines.Count)
                        break;
                }
                ret.finalize();
                db.close();
                try
                {
                    result.n_currentrecord = result.n_totalrecords;
                    result.n_percent = 100;
                    m_callback(ref m_logon, jobType, result);
                }
                catch (Exception) { }
                return ret;
            }
            catch (Exception e)
            {
                ULog.warning(e.Message);
                throw;
            }
        }

        public IAddressResults Find()
        {
            //UGisImportResultsByStreetId ret = new UGisImportResultsByStreetId();
            /*parse file and get results from db
             populate UGisImportResultLine with house info, the inhabitant results will be returned
             * in UGisImportResultLine.list
             */
            try
            {
                GisImportFile import = new GisImportFile(ref m_search);
                List<UGisImportResultLine> filelines = import.Parse();
                //open file and have it populated into search criterias
                bool b_only_coors = false;
                if (filelines.Count > m_search.DETAIL_THRESHOLD_LINES)
                    b_only_coors = true;
                else
                    b_only_coors = false;
                
                return SearchDatabase(ref filelines, b_only_coors);
            }
            catch (Exception e)
            {
                throw;
            }
        }

        public IAddressResults Find(UGisImportList filelines)
        {
            //UGisImportResultsByStreetId ret = new UGisImportResultsByStreetId();
            /*parse file and get results from db
             populate UGisImportResultLine with house info, the inhabitant results will be returned
             * in UGisImportResultLine.list
             */

            m_search = new UGisImportParamsByStreetId();
            m_search.SKIPLINES = filelines.SKIP_LINES;

            List<UGisImportResultLine> resultlines = convertGisImportList(filelines);

            try
            {
                bool b_only_coors = false;
                if (filelines.list.Count > filelines.DETAIL_THRESHOLD_LINES)
                    b_only_coors = true;
                else
                    b_only_coors = false;
                
                return SearchDatabase(ref resultlines, b_only_coors);
            }
            catch (Exception e)
            {
                throw;
            }
        }

        private List<UGisImportResultLine> convertGisImportList(UGisImportList importlist)
        {
            List<UGisImportResultLine> gisImportResultList = new List<UGisImportResultLine>();
            int line = 0;
            foreach (var import in importlist.list)
            {
                UGisImportResultLine tmp = new UGisImportResultLine
                {
                    houseno = import.houseno,
                    letter = import.letter,
                    municipalid = import.municipalid,
                    namefilter1 = import.namefilter1,
                    namefilter2 = import.namefilter2,
                    streetid = import.streetid,
                    n_linenumber = line
                };
                tmp.finalize();
                gisImportResultList.Add(tmp);
                line++;
            }

            return gisImportResultList;
        }
    }

}
