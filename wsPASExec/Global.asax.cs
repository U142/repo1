using System;
using System.Collections;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.SessionState;
using System.Xml.Linq;
using com.ums.UmsCommon;


namespace com.ums.wsPASExec
{
    public class Global : System.Web.HttpApplication
    {

        protected void Application_Start(object sender, EventArgs e)
        {
            UCommon.UPATHS.sz_path_backbone = ConfigurationSettings.AppSettings["sz_path_backbone"]; //"d:\\ums\\aep\\eat\\";
            UCommon.UPATHS.sz_path_bcp = ConfigurationSettings.AppSettings["sz_path_bcp"]; //"d:\\ums\\bcp\\eat\\";
            UCommon.UPATHS.sz_path_predefined_areas = ConfigurationSettings.AppSettings["sz_path_predefined_areas"]; //"D:\\ums\\wwwroot\\vb4\\predefined-areas\\";
            UCommon.UPATHS.sz_path_temp = ConfigurationSettings.AppSettings["sz_path_temp"]; //"D:\\ums\\temp\\";
            UCommon.UPATHS.sz_path_mapsendings = ConfigurationSettings.AppSettings["sz_path_mapsendings"]; //"D:\\ums\\mapsendings\\";
            UCommon.UPATHS.sz_path_lba = ConfigurationSettings.AppSettings["sz_path_lba"]; //"D:\\ums\\lba\\";
            UCommon.UPATHS.sz_path_parmtemp = ConfigurationSettings.AppSettings["sz_parm_path_temp"];
            UCommon.UPATHS.sz_path_parmzipped = ConfigurationSettings.AppSettings["sz_parm_path_zipped"];
            UCommon.UPATHS.sz_path_ttsserver = ConfigurationSettings.AppSettings["sz_path_tts_server"];
            UCommon.UPATHS.sz_path_audiofiles = ConfigurationSettings.AppSettings["sz_path_audiofiles"];
            UCommon.UPATHS.sz_path_voice = ConfigurationSettings.AppSettings["sz_path_voice"];
            UCommon.UPATHS.sz_path_bbmessages = ConfigurationSettings.AppSettings["sz_path_bbmessages"];
            UCommon.UPATHS.sz_path_predefined_messages = ConfigurationSettings.AppSettings["sz_path_predefined_messages"];
            UCommon.UPATHS.sz_path_global_wav_dir = ConfigurationSettings.AppSettings["sz_path_global_wav_dir"];
            UCommon.UPATHS.sz_path_infosent = ConfigurationSettings.AppSettings["sz_path_infosent"];
            UCommon.UPATHS.sz_url_nslookup = ConfigurationSettings.AppSettings["sz_url_nslookup"];

            UCommon.UBBDATABASE.sz_dsn = ConfigurationSettings.AppSettings["sz_db_dsn"]; //"backbone_ibuki";
            UCommon.UBBDATABASE.sz_dsn_aoba = ConfigurationSettings.AppSettings["sz_db_dsn_aoba"]; //"aoba";
            UCommon.UBBDATABASE.sz_uid = ConfigurationSettings.AppSettings["sz_db_uid"];//"sa";
            UCommon.UBBDATABASE.sz_pwd = ConfigurationSettings.AppSettings["sz_db_pwd"]; //"diginform";
            UCommon.UBBDATABASE.sz_adrdb_dsnbase = ConfigurationSettings.AppSettings["sz_adrdb_dsnbase"];
            UCommon.UBBDATABASE.sz_adrdb_uid = ConfigurationSettings.AppSettings["sz_adrdb_uid"];
            UCommon.UBBDATABASE.sz_adrdb_pwd = ConfigurationSettings.AppSettings["sz_adrdb_pwd"];

            UCommon.UVOICE.sz_send_number = ConfigurationSettings.AppSettings["sz_send_number"];
            UCommon.UVOICE.l_tts_timeout = int.Parse(ConfigurationSettings.AppSettings["l_tts_timeout_secs"]);
            UCommon.UVOICE.f_rms = float.Parse(ConfigurationSettings.AppSettings["f_rms"]);

            UCommon.USETTINGS.l_folkereg_num_adrtables = int.Parse(ConfigurationSettings.AppSettings["l_folkereg_num_adrtables"]);

            String sysloghost = ConfigurationSettings.AppSettings["sysloghost"];
            String port = ConfigurationSettings.AppSettings["syslogport"];
            int syslogport = int.Parse(port);

            try
            {
                UCommon.Initialize("soap/pas/1.0", sysloghost, syslogport);
            }
            catch (Exception ex)
            {
                throw new Exception(String.Format("Could not initialize {0}. {1}", UCommon.appname, ex.Message));
            }
        }

        protected void Session_Start(object sender, EventArgs e)
        {
        }

        protected void Application_BeginRequest(object sender, EventArgs e)
        {
            String s = sender.ToString();
        }

        protected void Application_AuthenticateRequest(object sender, EventArgs e)
        {

        }

        protected void Application_Error(object sender, EventArgs e)
        {
        }

        protected void Session_End(object sender, EventArgs e)
        {
            //TempDataStore.Remove(Session.SessionID);
        }

        protected void Application_End(object sender, EventArgs e)
        {
            UCommon.UnInitialize();
            TempDataStore.ClearAll();
        }
    }
}