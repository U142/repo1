using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;
using System.IO;
using com.ums.PAS.Maps;
using com.ums.PAS.Address;
using com.ums.PAS.Address.gab;
using com.ums.PAS.Database;
using com.ums.UmsCommon;
using com.ums.PAS.Status;
using com.ums.PAS.Project;
using com.ums.UmsCommon.Audio;
using com.ums.PAS.Settings;
using System.Text;
using com.ums.UmsDbLib;
using com.ums.PAS.Weather;
using com.ums.PAS.messagelib;


namespace com.ums.ws.pas
{
    /// <summary>
    /// Communication channel for PAS applications
    /// </summary>
    /// 

    [WebService(Namespace = "http://ums.no/ws/pas/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]



    public class pasws : System.Web.Services.WebService
    {
        [WebMethod]
        public UPASLOGON PasLogon(ULOGONINFO l)
        {
            try
            {
                ULogon logon = new ULogon();
                UPASLOGON ret = logon.Logon(ref l);
                logon.close();
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }

        }

        [WebMethod]
        public bool SavePasUiSettings(ULOGONINFO l, UPASUISETTINGS ui)
        {
            try
            {
                return new ULogon().SaveUiSettings(ref l, ref ui);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        /*[WebMethod]
        public UPASLOGON TestLogon()
        {
            ULOGONINFO l = new ULOGONINFO();
            l.sz_userid = "MH";
            l.sz_compid = "UMS";
            l.sz_password = "mh123,";
            return new ULogon().Logon(ref l);
        }*/

        /*
         * Will powerup the webservice if it's been shut down
         */
        [WebMethod]
        public String Powerup()
        {
            return "OK";
        }

        [WebMethod]
        public UPASMap GetMap(UMapInfo request) //UMapInfo input)
        {
            ULOGONINFO info = new ULOGONINFO();
            info.sz_compid = "UMS";
            info.sz_deptid = "TEST";
            UFleximap fleximap = new UFleximap(ref info, ref request);
            UPASMap map = null;
            try
            {
                map = fleximap.getMap();
            }
            catch (Exception e)
            {
                throw e;
            }
            return map;
        }

        [WebMethod]
        public UAddressList GetAddressListByQuality(ULOGONINFO logon, UMapAddressParamsByQuality param)
        {
            try
            {
                PercentProgress.SetPercentDelegate percentdelegate = PercentProgress.newDelegate();
                percentdelegate(ref logon, ProgressJobType.HOUSE_DOWNLOAD, new PercentResult());
                UAdrDb db = new UAdrDb(logon.sz_stdcc, 120, logon.l_deptpk);
                UAddressList list = db.GetAddresslistByQuality(ref logon, ref param, percentdelegate);
                db.close();
                return list;
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                PercentProgress.DeleteJob(ref logon, ProgressJobType.HOUSE_DOWNLOAD);
            }
        }

        [WebMethod]
        public UGisImportResultsByStreetId GetGisByStreetId(ULOGONINFO logon, UGisImportParamsByStreetId search)
        {
            try
            {
                PercentProgress.SetPercentDelegate percentdelegate = PercentProgress.newDelegate();
                percentdelegate(ref logon, ProgressJobType.GEMINI_IMPORT_STREETID, new PercentResult());
                UGisImportLookup lookup = new UGisImportLookup(ref search, ref logon, percentdelegate, ProgressJobType.GEMINI_IMPORT_STREETID);
                UGisImportResultsByStreetId res = (UGisImportResultsByStreetId)lookup.Find();
                
                return res;
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                PercentProgress.DeleteJob(ref logon, ProgressJobType.GEMINI_IMPORT_STREETID);
            }

        }

        [WebMethod]
        public PercentResult GetProgress(ULOGONINFO l, ProgressJobType jobType)
        {
            try
            {
                //String ret = TempDataStore.GetRecords(jobType + "_" + l.l_comppk + "_" + l.l_deptpk + "_" + l.l_userpk).ToString();
                //return ret;
                return PercentProgress.GetProgress(ref l, jobType);
            }
            catch (Exception)
            {
                return new PercentResult();
            }

        }

        [WebMethod]
        public String GetCurrentJobs()
        {
            try
            {
                String ret = "Results\n";
                PercentResult [] pr = PercentProgress.GetJobs();
                if (pr!=null)
                {
                    for (int i = 0; i < pr.Length; i++)
                        ret += pr[i].ToString() + "\n";

                }
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        [WebMethod]
        public byte[] GetSendSettings(ULOGONINFO l)
        {
            try
            {
                return new USendSettings(ref l).Find();
                //return null;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*[WebMethod]
        public String __GetSendSettings()
        {
            ULOGONINFO l = new ULOGONINFO();
            l.l_deptpk = 1;
            l.l_comppk = 2;
            l.l_userpk = 1;
            l.sz_password = "mh123,1";

            try
            {
                return new USendSettings(ref l).Find();
            }
            catch (Exception e)
            {
                throw e;
            }
        }*/


        /*[WebMethod]
        public void test(AsyncCallback outerCallback)
        {
            
            int retval = 50;
            Stack stack = Session["Status"] as Stack;
            stack.Push(retval);
        }*/

        [WebMethod]
        public UPASMap GetMapOverlay(UMapInfoLayerCellVision request)
        {
            ULOGONINFO info = new ULOGONINFO();
            info.sz_compid = "UMS";
            info.sz_deptid = "TEST";
            UMapOverlay overlay = new UMapOverlay(ref info, ref request);
            UPASMap map = null;
            try
            {
                map = overlay.getMap();
            }
            catch (Exception e)
            {
                map = new UPASMap();
                map.image = Encoding.UTF8.GetBytes(e.Message);
                //throw e;
            }
            return map;
        }

        [WebMethod]
        public long GetRefno(ULOGONINFO logoninfo)
        {
            UmsDbLib.UmsDb db = new UmsDbLib.UmsDb();
            long n_refno = -1;
            try
            {
                n_refno = db.newRefno();
                db.close();
            }
            catch (Exception)
            {
            }
            return n_refno;
        }

        [WebMethod]
        public UMAXALLOC SetMaxAlloc(ULOGONINFO logoninfo, UMAXALLOC maxalloc)
        {
            USendDb db = new USendDb();
            try
            {
                if (!db.CheckLogon(ref logoninfo))
                {
                    UMAXALLOC max = new UMAXALLOC();
                    max.n_maxalloc = -1; //represents failed
                    max.n_projectpk = maxalloc.n_projectpk;
                    max.n_refno = maxalloc.n_refno;
                    return max;
                }
                int n = db.setMaxAlloc(ref maxalloc);
                db.close();
            }
            catch (Exception)
            {
                maxalloc.n_maxalloc = -1;
            }
            return maxalloc;
        }

        [WebMethod]
        public UAddress HouseEditor(UAddress adr, ULOGONINFO logoninfo, UHouseEditor.HOUSEEDITOR_OPERATION operation)
        {
            try
            {
                UHouseEditor edit = new UHouseEditor(ref logoninfo, ref adr, operation);
                return adr;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        [WebMethod]
        public UAddressList GetAddressList(UMapAddressParams searchparams, ULOGONINFO logoninfo)
        {
            UMapAddressSearch search = new UMapAddressSearch(ref searchparams, ref logoninfo);
            return (UAddressList)search.Find();
        }

        [WebMethod]
        public UGabResultFromPoint GetNearestGABFromPoint(ULOGONINFO logon, UMapPoint p)
        {
            //UGabResult search = new UGabResult();
            //return search;
            try
            {
                return (UGabResultFromPoint)new UGabFromPoint(ref logon, ref p).Find();
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        /*[WebMethod]
        public UGabResultFromPoint GetNearestTest()
        {
            ULOGONINFO l = new ULOGONINFO();
            l.sz_stdcc = "0047";
            UMapPoint p = new UMapPoint();
            p.lat = 59.1;
            p.lon = 8.7;
            return (UGabResultFromPoint)new UGabFromPoint(ref l, ref p).Find();
        }*/

        [WebMethod]
        public UAddressList GetNearestInhabitantsFromPoint(UMapDistanceParams param, ULOGONINFO logoninfo)
        {
            try
            {
                UAddressList list = (UAddressList)new UInhabitantsByDistance(ref param, ref logoninfo).Find();
                return list;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        /*[WebMethod]
        public UAddressList Test()
        {
            try
            {
                UMapDistanceParams param = new UMapDistanceParams();
                param.lat = 60 + 37.0/60.0 + 44.98/3600;
                param.lon = 6 + 25.0/60.0 + 32.28/3600;
                param.distance = 2.0;
                ULOGONINFO logoninfo = new ULOGONINFO();
                logoninfo.l_comppk = 2;
                logoninfo.l_deptpk = 1;
                logoninfo.l_userpk = 1;
                logoninfo.sz_stdcc = "0047";
                UAddressList list = GetNearestInhabitantsFromPoint(param, logoninfo);
                return list;
            }
            catch (Exception e)
            {
                throw e;
            }
        }*/

        /*
         * Method - searching for a specified address in an external system (Ugland)
         */
        [WebMethod]
        public UGabSearchResultList GabSearch(UGabSearchParams searchparams, ULOGONINFO logoninfo)
        {
            UGabSearch search = new UGabSearch(ref searchparams, ref logoninfo);
            return (UGabSearchResultList)search.Find();
        }

        [WebMethod]
        public UCONVERT_TTS_RESPONSE ConvertTTS(ULOGONINFO logon, UCONVERT_TTS_REQUEST req)
        {
            return new UAudio().ConvertTTS(ref logon, ref req);
        }
        
        [WebMethod]
        public AUDIO_RESPONSE UPostAudio(ULOGONINFO logon, AUDIO_REQUEST req)
        {
            try
            {
                return new UAudio().UPostAudio(ref logon, ref req);
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        [WebMethod]
        public UPROJECT_RESPONSE UCreateProject(ULOGONINFO logon, UPROJECT_REQUEST req)
        {
            return new UProject().uproject(ref logon, ref req);
        }
        
        [WebMethod]
        public REFNO_RESPONSE URefno()
        {
            /*ULOGONINFO logon = new ULOGONINFO();
            logon.l_comppk = 2;
            logon.l_deptpk = 1;
            logon.l_userpk = 1;
            logon.sz_userid = "MH";
            logon.sz_compid = "UMS";
            logon.sz_password = "mh123,1";

            return new URefno().getRefno(ref logon);*/
            return new REFNO_RESPONSE();
        }

        [WebMethod]
        public UWeatherReportResults GetWeatherReport(ULOGONINFO l, UWeatherSearch s)
        {
            UWeatherReport report = new UWeatherReport();
            try
            {
                UmsDb db = new UmsDb();
                db.CheckLogon(ref l);
                return report.GetWeatherReport(ref s);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        [WebMethod]
        public UBBMESSAGE InsertMessageLibrary(ULOGONINFO logon, UBBMESSAGE msg)
        {
            try
            {
                UBBMESSAGE ret = new UMessageLib(ref logon).InsertMessage(ref msg);
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        [WebMethod]
        public UBBMESSAGELIST GetMessageLibrary(ULOGONINFO logon, UBBMESSAGELISTFILTER filter)
        {
            try
            {
                UBBMESSAGELIST ret = new UMessageLib(ref logon).GetMessageList(ref logon, filter);
                return ret;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*[WebMethod]
        public UWeatherReportResults GetWeatherTest()
        {
            UWeatherSearch w = new UWeatherSearch();
            w.forecasts = 1;
            w.interval = 1;
            if (w.forecasts == 1)
                w.interval = 0;
            w.lat = 55.5;
            w.lon = 5.5;
            return new UWeatherReport().GetWeatherReport(ref w);
        }*/

        /*[WebMethod]
        public UPROJECT_RESPONSE UProjectTest()
        {
            ULOGONINFO logon = new ULOGONINFO();
            logon.l_comppk = 2;
            logon.l_deptpk = 1;
            logon.l_userpk = 1;
            logon.sz_userid = "MH";
            logon.sz_compid = "UMS";
            logon.sz_password = "mh123,1";

            UPROJECT_REQUEST req = new UPROJECT_REQUEST();
            req.sz_name = "Svein test prosjektting";

            return new UProject().uproject(ref logon, ref req);
        }*/

        /*[WebMethod]
        public UPOST_AUDIO_RESPONSE UPostAudioTest()
        {
            ULOGONINFO logon = new ULOGONINFO();
            logon.l_comppk = 2;
            logon.l_deptpk = 1;
            logon.l_userpk = 1;
            logon.sz_userid = "MH";
            logon.sz_compid = "UMS";
            logon.sz_password = "mh123,1";

            UPOST_AUDIO_REQUEST req = new UPOST_AUDIO_REQUEST();
            req.n_refno = 90500;
            req.n_filetype = 4;
            req.sz_filename = "v90500_1.wav";
            req.n_param = 1;
            req.n_deptpk = 99999;
            req.n_messagepk = 90500;
            req.wav = File.ReadAllBytes(UCommon.UPATHS.sz_path_audiofiles + "PAS_1a395317-c540-4735-a89c-5072fd14ab45_1.wav");
            return new UAudio().UPostAudio(ref logon, ref req);
        }*/

        /*[WebMethod]
        public bool TestConvertTTS()
        {
            ULOGONINFO logon = new ULOGONINFO();
            logon.l_comppk = 2;
            logon.l_deptpk = 1;
            logon.l_userpk = 1;
            logon.sz_userid = "MH";
            logon.sz_compid = "UMS";
            logon.sz_password = "mh123,1";

            UCONVERT_TTS_REQUEST req = new UCONVERT_TTS_REQUEST();
            req.n_langpk = 1;
            req.n_dynfile = 1;
            req.sz_text = "Morten tester med web sørvis";
            new UAudio().ConvertTTS(ref logon, ref req);
            return true;
        }*/

        /*[WebMethod]
        public String TestGabSearch()
        {
            UGabSearchParams sp = new UGabSearchParams();
            sp.sz_no = "";
            sp.sz_uid = "UMS";
            sp.sz_pwd = "MSG";
            sp.sz_language = "NO";
            sp.n_count = 10;
            sp.n_sort = 1;
            sp.sz_address = "Åre";
            
            ULOGONINFO logon = new ULOGONINFO();
            UGabSearch search = new UGabSearch(ref sp, ref logon);
            search.Find();
            
            return "S_OK";
        }*/
    }
}
