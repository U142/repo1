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

            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc, 60);
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

        public UGisImportLookup(ref UGisImportParamsByStreetId search, ref ULOGONINFO logon, PercentProgress.SetPercentDelegate percentCallback)
        {
            m_search = search;
            m_logon = logon;
            m_callback = percentCallback;
        }
        public IAddressResults Find()
        {
            UGisImportResultsByStreetId ret = new UGisImportResultsByStreetId();
            UmsDbConnParams conn = new UmsDbConnParams();
            conn.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase;
            conn.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
            conn.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;
            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc, 60);
            /*parse file and get results from db
             populate UGisImportResultLine with house info, the inhabitant results will be returned
             * in UGisImportResultLine.list
             */
            GisImportFile import = new GisImportFile(ref m_search);
            List<UGisImportResultLine> filelines = import.Parse();
            //open file and have it populated into search criterias
            try
            {
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
                int chunksize = 255;
                float max = filelines.Count;
                PercentResult result = new PercentResult();
                result.n_totalrecords = filelines.Count;
                result.n_currentrecord = 0;
                result.n_percent = 0;
                m_callback(ref m_logon, ProgressJobType.GEMINI_IMPORT_STREETID, result);
                while (1 == 1)
                {
                    try
                    {
                        x = db.GetGisImport(ref filelines, next, chunksize, ref next, ref m_search.SKIPLINES);
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                    for (int add = prev; add < prev+(x==chunksize ? x : x-1); add++)
                    {
                        UGisImportResultLine tmp = filelines[add];
                        ret.addLine(ref tmp);
                        tmp.list.finalize();
                    }
                    try
                    {
                        result.n_currentrecord = next;
                        if (max > 0)
                            result.n_percent = (int)((next * 100.0) / max);
                        else
                            result.n_percent = 100;
                        m_callback(ref m_logon, ProgressJobType.GEMINI_IMPORT_STREETID, result);
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
                    m_callback(ref m_logon, ProgressJobType.GEMINI_IMPORT_STREETID, result);
                }
                catch (Exception) { }
                return ret;
            }
            catch (Exception e)
            {
                ULog.warning(e.Message);
                throw e;
            }
        }
    }

}
