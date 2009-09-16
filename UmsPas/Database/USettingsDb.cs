using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.Data.Odbc;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsFile;


namespace com.ums.PAS.Database
{
    public class USettingsDb : PASUmsDb
    {
        public USettingsDb(UmsDbConnParams conn)
            : base(conn.sz_dsn, conn.sz_uid, conn.sz_pwd, 60)
        {

        }

        public bool WriteSoundLibToFile(ref USimpleXmlWriter xml, ref ULOGONINFO l)
        {
            try
            {
                xml.insertStartElement("SOUNDLIB");
                String szSQL = String.Format("SELECT l_shared=0, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, isnull(MD.l_defpk, 0) l_defpk, MD.sz_name moduledef, isnull(BM.f_template, 0) f_template FROM BBMESSAGES BM, MDVMODULEDEF MD WHERE BM.l_type=(MD.l_defpk+10) AND BM.f_template=1 AND BM.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_shared=1, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, isnull(MD.l_defpk, 0) l_defpk, MD.sz_name moduledef, isnull(BM.f_template, 0) f_template FROM BBMESSAGES BM, MDVMODULEDEF MD, BBMESSAGES_X_DEPT XD WHERE BM.l_type=(MD.l_defpk+10) AND BM.f_template=1 AND BM.l_messagepk=XD.l_messagepk AND XD.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_shared=0, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, isnull(MD.l_defpk, 0) l_defpk, MD.sz_name moduledef, isnull(BM.f_template, 0) f_template FROM BBMESSAGES BM, MDVMODULEDEF MD WHERE BM.l_type=(MD.l_defpk+10) AND BM.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_shared=1, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, isnull(MD.l_defpk, 0) l_defpk, MD.sz_name moduledef, isnull(BM.f_template, 0) f_template FROM BBMESSAGES BM, MDVMODULEDEF MD, BBMESSAGES_X_DEPT XD WHERE BM.l_type=(MD.l_defpk+10) AND BM.l_messagepk=XD.l_messagepk AND XD.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_shared=0, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, l_defpk=0, moduledef='', f_template=2 FROM BBMESSAGES BM WHERE BM.f_template=1 AND BM.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_shared=1, BM.l_deptpk, isnull(BM.l_type, 0) l_type, UPPER(BM.sz_name), BM.sz_name, BM.l_messagepk, isnull(BM.l_langpk, 0) l_langpk, l_defpk=0, moduledef='', f_template=2 FROM BBMESSAGES BM, BBMESSAGES_X_DEPT XD WHERE BM.f_template=1 AND BM.l_messagepk=XD.l_messagepk AND XD.l_deptpk={0} " +
                                            "ORDER BY f_template, UPPER(BM.sz_name)",
                                            l.l_deptpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    xml.insertStartElement("SERVERFILE");
                    xml.insertAttribute("sh", rs["l_shared"].ToString());
                    xml.insertAttribute("deptpk", rs["l_deptpk"].ToString());
                    xml.insertAttribute("name", rs["sz_name"].ToString());
                    xml.insertAttribute("pk", rs["l_messagepk"].ToString());
                    xml.insertAttribute("l_langpk", rs["l_langpk"].ToString());
                    xml.insertAttribute("def", rs["moduledef"].ToString());
                    xml.insertAttribute("template", rs["f_template"].ToString());
                    xml.insertEndElement(); //SERVERFILE
                }
                xml.insertEndElement(); //SOUNDLIB
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }

        public bool WriteTTSLangToFile(ref USimpleXmlWriter xml, ref ULOGONINFO l)
        {
            try
            {
                xml.insertStartElement("TTSLANG");
                String szSQL = String.Format("SELECT tts.l_langpk, tts.sz_name FROM BBTTSLANG tts, BBTTSLANG_X_DEPT xd WHERE xd.l_deptpk={0} AND xd.l_langpk=tts.l_langpk",
                                            l.l_deptpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    xml.insertStartElement("TTS");
                    xml.insertAttribute("l_langpk", rs["l_langpk"].ToString());
                    xml.insertAttribute("sz_name", rs["sz_name"].ToString());
                    xml.insertEndElement();
                }
                xml.insertEndElement(); //TTSLANG
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }

        public bool WriteSchedProfilesToFile(ref USimpleXmlWriter xml, ref ULOGONINFO l)
        {
            try
            {
                xml.insertStartElement("SCHEDPROFILES");
                String szSQL = String.Format("SELECT l_sharing=0, UPPER(BR.sz_name), BR.sz_name, BR.l_deptpk l_owner_deptpk, BD.l_deptpk, UPPER(BD.sz_deptid), BD.sz_deptid, BR.l_reschedpk, BR.l_retries, BR.l_interval, BR.l_canceltime, BR.l_canceldate, BR.l_pausetime, BR.l_pauseinterval FROM BBRESCHEDPROFILES BR, BBDEPARTMENT BD " +
                                            "WHERE BR.l_deptpk=BD.l_deptpk AND BR.l_deptpk={0} " +
                                            "UNION " +
                                            "SELECT l_sharing=1, UPPER(BR.sz_name), BR.sz_name, BR.l_deptpk l_owner_deptpk, BD.l_deptpk, UPPER(BD.sz_deptid), BD.sz_deptid, BR.l_reschedpk, BR.l_retries, BR.l_interval, BR.l_canceltime, BR.l_canceldate, BR.l_pausetime, BR.l_pauseinterval FROM BBRESCHEDPROFILES BR, BBRESCHEDPROFILES_X_DEPT BX, BBDEPARTMENT BD " +
                                            "WHERE BR.l_reschedpk=BX.l_reschedpk AND BX.l_deptpk=BD.l_deptpk AND BX.l_deptpk={0} " +
                                            "ORDER BY UPPER(BR.sz_name)",
                                            l.l_deptpk);

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    xml.insertStartElement("SCHEDPROFILE");
                    xml.insertAttribute("l_reschedpk", rs["l_reschedpk"].ToString());
                    xml.insertAttribute("sz_name", rs["sz_name"].ToString());
                    xml.insertAttribute("l_deptpk", rs["l_deptpk"].ToString());
                    xml.insertAttribute("sz_deptid", rs["sz_deptid"].ToString());
                    xml.insertAttribute("l_retries", rs["l_retries"].ToString());
                    xml.insertAttribute("l_interval", rs["l_interval"].ToString());
                    xml.insertAttribute("l_canceltime", rs["l_canceltime"].ToString());
                    xml.insertAttribute("l_canceldate", rs["l_canceldate"].ToString());
                    xml.insertAttribute("l_pausetime", rs["l_pausetime"].ToString());
                    xml.insertAttribute("l_pauseinterval", rs["l_pauseinterval"].ToString());
                    xml.insertAttribute("sharing", rs["l_sharing"].ToString());
                    xml.insertEndElement();
                }

                xml.insertEndElement(); //SCHEDPROFILES
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }

        public bool WriteFromNumbersToFile(ref USimpleXmlWriter xml, ref ULOGONINFO l)
        {
            try
            {
                xml.insertStartElement("FROMNUMBERS");
                String szSQL = String.Format("SELECT sz_number, isnull(l_deptpk, -1), isnull(sz_descr,'') FROM BBDEPTNUMBERS WHERE l_deptpk={0} OR l_deptpk=-1 ORDER BY sz_number DESC",
                    l.l_deptpk);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    xml.insertStartElement("OADC");
                    xml.insertAttribute("sz_number", rs.GetString(0));
                    xml.insertAttribute("l_deptpk", rs.GetInt32(1).ToString());
                    xml.insertAttribute("sz_descr", rs.GetString(2));
                    xml.insertEndElement(); //OADC
                }
                xml.insertEndElement(); //FROMNUMBERS
                rs.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }

        public bool WriteBBProfilesToFile(ref USimpleXmlWriter xml, ref ULOGONINFO l)
        {
            try
            {
                xml.insertStartElement("MSGPROFILES");

                String szSQL = String.Format("SELECT UPPER(AP.sz_name), sharing=0, AP.l_profilepk, AP.sz_name, isnull(AP.l_deptpk, -1), isnull(AP.l_reschedpk, -1), isnull(BO.l_parent, 0) l_parent, isnull(BO.l_item, 0), isnull(BO.l_param, 0), isnull(BO.l_action, 0), isnull(BO.l_seq, 0), isnull(MN.sz_name, 'Not specified') sz_modulename, isnull(MD.sz_name, 'Not specified') defname, MD.l_defpk FROM BBACTIONPROFILESNAME AP, BBACTIONPROFILESOUT BO, BBACTIONPROFILESOUT_X_MOD BX, MDVMODULE MOD, MDVMODULENAME MN, MDVMODULEDEF MD " +
                                        "WHERE AP.l_deptpk={0} AND AP.l_profilepk*=BO.l_profilepk {1} " +
                                        "UNION " +
                                        "SELECT UPPER(AP.sz_name), sharing=1, AP.l_profilepk, AP.sz_name, isnull(AP.l_deptpk, -1), isnull(AP.l_reschedpk, -1), isnull(BO.l_parent, 0) l_parent, isnull(BO.l_item, 0), isnull(BO.l_param, 0), isnull(BO.l_action, 0), isnull(BO.l_seq, 0), isnull(MN.sz_name, 'Not specified') sz_modulename, isnull(MD.sz_name, 'Not specified') defname, MD.l_defpk FROM BBACTIONPROFILESNAME AP, BBACTIONPROFILES_X_DEPT XD, BBACTIONPROFILESOUT BO, BBACTIONPROFILESOUT_X_MOD BX, MDVMODULE MOD, MDVMODULENAME MN, MDVMODULEDEF MD " +
                                        "WHERE AP.l_profilepk=XD.l_profilepk AND XD.l_deptpk={0} AND XD.l_profilepk*=BO.l_profilepk {1} " +
                                        "ORDER BY UPPER(AP.sz_name), AP.l_profilepk, MD.l_defpk",
                                        //ARG0
                                        l.l_deptpk,
                                        //ARG1
                                        "AND (BO.l_action=1 OR BO.l_action=20 OR BO.l_action=1018) AND (len(BO.sz_param)<=1 OR BO.sz_param IS NULL OR BO.sz_param=' ') " +
                                        "AND BO.l_profilepk*=BX.l_profilepk AND BO.l_parent*=BX.l_parent AND BX.l_modulepk*=MOD.l_modulepk AND (MOD.l_action=1 OR MOD.l_action=20 OR MOD.l_action=1018) " +
                                        "AND MOD.l_modulepk*=MN.l_modulepk AND MN.l_defpk*=MD.l_defpk");

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                int n_last_profilepk = -1;
                while (rs.Read())
                {
                    int n_profilepk = rs.GetInt32(2);
                    if (n_last_profilepk != n_profilepk)
                    {
                        if (n_last_profilepk > -1)
                            xml.insertEndElement(); //PROFILE
                        xml.insertStartElement("PROFILE");
                        xml.insertAttribute("l_profilepk", n_profilepk.ToString());
                        xml.insertAttribute("sz_name", rs.GetString(3));
                        xml.insertAttribute("l_deptpk", rs.GetInt32(4).ToString());
                        xml.insertAttribute("l_reschedpk", rs.GetInt64(5).ToString());
                        xml.insertAttribute("sharing", rs.GetInt32(1).ToString());
                    }
                    int n_parent = rs.GetInt32(6);
                    //String n_parent = rs.GetString(6);
                    if(n_parent>0)
                    {
                        xml.insertStartElement("FILE");
                        xml.insertAttribute("l_parent", n_parent.ToString());
                        xml.insertAttribute("l_item", rs.GetInt32(7).ToString());
                        xml.insertAttribute("l_param", rs.GetInt32(8).ToString());
                        xml.insertAttribute("l_action", rs.GetInt32(9).ToString());
                        xml.insertAttribute("l_seq", rs.GetInt32(10).ToString());
                        xml.insertAttribute("sz_name", rs.GetString(11).ToString());
                        xml.insertAttribute("sz_defname", rs.GetString(12).ToString());
                        xml.insertEndElement(); //FILE
                    }
                    n_last_profilepk = n_profilepk;

                }
                xml.insertEndElement(); //MSGPROFILES
                rs.Close();
                
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }
    }
}
