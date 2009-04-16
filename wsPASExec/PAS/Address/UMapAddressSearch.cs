﻿using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
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

            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc);
            UAddressList list = db.GetAddresslist(m_search);
            db.close();
            return list;
        }
    }

    public class UGisImportLookup : IAddressSearch
    {
        protected UGisImportParamsByStreetId m_search;
        protected ULOGONINFO m_logon;
        public UGisImportLookup(ref UGisImportParamsByStreetId search, ref ULOGONINFO logon)
        {
            m_search = search;
            m_logon = logon;
        }
        public IAddressResults Find()
        {
            UGisImportResultsByStreetId ret = new UGisImportResultsByStreetId();
            UmsDbConnParams conn = new UmsDbConnParams();
            conn.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase;
            conn.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
            conn.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;
            UAdrDb db = new UAdrDb(conn, m_logon.sz_stdcc);
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
                while (1 == 1)
                {
                    x = db.GetGisImport(ref filelines, next, chunksize, ref next);
                    for (int add = prev; add < prev+(x==chunksize ? x : x-1); add++)
                    {
                        UGisImportResultLine tmp = filelines[add];
                        ret.addLine(ref tmp);
                        tmp.list.finalize();
                    }
                    prev = next;
                    if (next >= filelines.Count)
                        break;
                }
                ret.finalize();
                db.close();
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
