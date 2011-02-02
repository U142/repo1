using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Xml;
using System.Text;

using com.ums.UmsCommon;
using com.ums.ZipLib;
using com.ums.UmsDbLib;
using com.ums.PAS.Database;
using com.ums.UmsFile;


namespace com.ums.PAS.Settings
{
    public class USendSettings
    {
        protected ULOGONINFO m_logon;
        private USettingsDb m_db;
        private USimpleXmlWriter outxml;

        public USendSettings(ref ULOGONINFO l)
        {
            m_logon = l;
        }
        public byte [] Find()
        {
            //Connect to database
            UmsDbConnParams dbparams = new UmsDbConnParams();
            dbparams.sz_dsn = UCommon.UBBDATABASE.sz_dsn;
            dbparams.sz_uid = UCommon.UBBDATABASE.sz_uid;
            dbparams.sz_pwd = UCommon.UBBDATABASE.sz_pwd;

            try
            {
                m_db = new USettingsDb(dbparams);
            }
            catch (Exception)
            {
                throw new UDbConnectionException();
            }

            if (!m_db.CheckLogon(ref m_logon, true))
            {
                throw new ULogonFailedException();
            }


            String sz_encoding = "utf-8";
            try
            {
                outxml = new USimpleXmlWriter(sz_encoding);
            }
            catch (Exception)
            {
                throw;
            }

            outxml.insertStartDocument();
            outxml.insertComment("UMS Send Settings file");
            outxml.insertStartElement("SENDOPTIONS");

            m_db.WriteBBProfilesToFile(ref outxml, ref m_logon);
            m_db.WriteFromNumbersToFile(ref outxml, ref m_logon);
            m_db.WriteSchedProfilesToFile(ref outxml, ref m_logon);
            m_db.WriteTTSLangToFile(ref outxml, ref m_logon);
            m_db.WriteSoundLibToFile(ref outxml, ref m_logon);

            outxml.insertEndElement(); //SENDOPTIONS
            outxml.finalize();
            m_db.close();

            UZipLib zip = new UZipLib(UCommon.UPATHS.sz_path_parmtemp, "settings." + Guid.NewGuid() + ".zip");
            try
            {
                zip.AddTextAsZipFileEntry("settings", outxml.getXml(), Encoding.GetEncoding(sz_encoding));
                zip.finalize();
                return zip.ReadZipFileBytes();
            }
            catch (Exception e)
            {
                ULog.error(0, "Error writing Settings ZIP", e.Message);
                throw;
            }
        }
    }
}
