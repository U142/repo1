using System;
using System.IO;
using System.Collections.Generic;
using com.ums.UmsCommon;
using com.ums.PAS.Address;


public class GisImportFile
{
    protected UGisImportParamsByStreetId m_params;
    public GisImportFile(ref UGisImportParamsByStreetId p)
    {
        m_params = p;
    }
    public List<UGisImportResultLine> Parse()
    {
        try
        {
            List<UGisImportResultLine> ret = new List<UGisImportResultLine>();
            MemoryStream ms = new MemoryStream(m_params.FILE);
            StreamReader sr = new StreamReader(ms);
            int lineno = -1;
            while (!sr.EndOfStream)
            {
                lineno++;
                if (lineno < m_params.SKIPLINES)
                    continue;
                string str = sr.ReadLine();
                UGisImportResultLine gisline = new UGisImportResultLine();
                string[] arr = str.Split(m_params.SEPARATOR.ToCharArray());
                //get municipal
                if (m_params.COL_MUNICIPAL < arr.Length)
                    gisline.municipalid = arr[m_params.COL_MUNICIPAL];
                if (m_params.COL_STREETID < arr.Length)
                    gisline.streetid = arr[m_params.COL_STREETID];
                if (m_params.COL_HOUSENO < arr.Length)
                    gisline.houseno = arr[m_params.COL_HOUSENO];
                if (m_params.COL_LETTER < arr.Length)
                    gisline.letter = arr[m_params.COL_LETTER];
                if (m_params.COL_NAMEFILTER1 < arr.Length && m_params.COL_NAMEFILTER1 >= 0)
                    gisline.namefilter1 = arr[m_params.COL_NAMEFILTER1];
                else
                    gisline.namefilter1 = "";
                if (m_params.COL_NAMEFILTER2 < arr.Length && m_params.COL_NAMEFILTER2 >= 0)
                    gisline.namefilter2 = arr[m_params.COL_NAMEFILTER2];
                else
                    gisline.namefilter2 = "";
                gisline.n_linenumber = (lineno + 1);

                gisline.finalize();
                ret.Add(gisline);
            }
           
            return ret;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

}