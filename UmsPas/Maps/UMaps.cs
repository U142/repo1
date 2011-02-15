using System;
using System.Collections;
using com.ums.UmsCommon;
using com.ums.UmsFile;
using System.IO;
using System.Net;

using com.ums.UmsCommon.CoorConvert;
using com.ums.UmsDbLib;
using System.Data.Odbc;

namespace com.ums.PAS.Maps
{

    public class UPASMap
    {
        private MemoryStream memimage;
        public UMapInfo mapinfo;
        public byte [] image; //= new byte[1000000];
        public void createMemoryStream(Stream stream)
        {
            memimage = new MemoryStream();
            byte[] temp = new byte[10240];
            int n_total = 0;
            while (true)
            {
                int n_read = stream.Read(temp, 0, temp.Length);
                if (n_read <= 0)
                    break;
                memimage.Write(temp, 0, n_read);
                n_total += n_read;
                
            }

            stream.Close();

            image = memimage.ToArray();
            memimage.Close();
        }
        public void setMapInfo(UMapInfo info)
        {
            mapinfo = info;
        }
    }

    public class UMapInfoLayer : UMapInfo
    {
        public String sz_userid;
        public String sz_password;
    }

    public enum UGSMLAYERS
    {
        GSM900 = 1,
        GSM1800 = 2,
        ALL = 3,
        UMTS = 4,
    }

    public class UMapInfoLayerCellVision : UMapInfoLayer
    {
        public String sz_jobid;
        public String layers;
        public String sz_request;
        public String version;
        public String styles;
        public String SRS = "4326";
    }


    public class UMapInfo : UMapBounds
    {
        public int width = 1024;
        public int height = 768;
        public String portrayal = "Mow1";
        public String mapstatus;
        public String maptime;
    }

    public abstract class UGetMaps
    {
        protected ULOGONINFO m_logoninfo;
        protected UMapInfo m_mapinfo;
        protected String userid = "UMS";
        protected String password = "MSG";
        public UGetMaps(ULOGONINFO logoninfo, UMapInfo request)
        {
            m_logoninfo = logoninfo;
            m_mapinfo = request;
        }
        public abstract UPASMap getMap();
    }

    public class UMapOverlay : UGetMaps
    {
        public static double MAGIC_NUMBER=6356752.3142;
        public static double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
        public static double WGS84_ECCENTRICITY = 0.0818191913108718138;

        public static double DEG2RAD = 0.0174532922519943;
        public static double PI = 3.14159267;

        public UMapOverlay(ref ULOGONINFO logoninfo, ref UMapInfoLayerCellVision request)
            : base(logoninfo, request)
        {

        }


        public double dd2MercMetersLat(double p_lat) {
	        var lat_rad = p_lat * DEG2RAD;
	        return WGS84_SEMI_MAJOR_AXIS * Math.Log(Math.Tan((lat_rad + PI / 2) / 2) * Math.Pow( ((1 - WGS84_ECCENTRICITY * Math.Sin(lat_rad)) / (1 + WGS84_ECCENTRICITY * Math.Sin(lat_rad))), (WGS84_ECCENTRICITY/2)));
        }
        public double dd2MercMetersLng(double p_lng) {
	        return WGS84_SEMI_MAJOR_AXIS * (p_lng*DEG2RAD);
        }

        public void LLtoGoogle(double lon, double lat, ref double mx, ref double my)
        {
            double max = 20037508.342789244;

            mx = lon * max / 180.0;
            my = Math.Log(Math.Tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);

            my = my * max / 180.0;

        }

        public override UPASMap getMap()
        {
            UMapInfoLayerCellVision cv = (UMapInfoLayerCellVision)m_mapinfo;
            
            /*UTM utm = new UTM();
            double lb_x = 0, lb_y = 0, ur_x = 0, ur_y = 0;
            String UTMZone = "";
            utm.LL2UTM(23, cv.b_bo, cv.l_bo, 32, ref lb_y, ref lb_x, ref UTMZone);
            utm.LL2UTM(23, cv.u_bo, cv.r_bo, 32, ref ur_y, ref ur_x, ref UTMZone);
            String lBbox = String.Format(UCommon.UGlobalizationInfo, "{0},{1},{2},{3}", dd2MercMetersLng(cv.l_bo), dd2MercMetersLat(cv.u_bo), dd2MercMetersLng(cv.r_bo), dd2MercMetersLat(cv.b_bo));

            double lower_left_google_x = 0, lower_left_google_y= 0, upper_right_google_x = 0, upper_right_google_y = 0;
            LLtoGoogle(cv.l_bo, cv.b_bo, ref lower_left_google_x, ref lower_left_google_y);
            LLtoGoogle(cv.r_bo, cv.u_bo, ref upper_right_google_x, ref upper_right_google_y);

            String mercatorlBbox = String.Format(UCommon.UGlobalizationInfo, "{0},{1},{2},{3}", 
                dd2MercMetersLng(cv.l_bo), dd2MercMetersLat(cv.b_bo), dd2MercMetersLng(cv.r_bo), dd2MercMetersLat(cv.u_bo));
            */
            /*
             * 32632 utm32
             * 32633 utm33
             * 4326 wgs84
             * 900913 google
             * 54004 mercator
             */


             

            /*sz_request = String.Format(UCommon.UGlobalizationInfo,
                                            "http://dnweb12.dirnat.no/wmsconnector/com.esri.wms.Esrimap?" +
                                            "WMTVER=1.1.1&FORMAT=PNG&BGCOLOR=0x000000&TRANSPARENT=TRUE&" +
                                            "STYLES=default&LAYERS=Kulturlandskap&SRS=EPSG:4326&" +
                                            "BBOX={0},{1},{2},{3}&WIDTH={4}&HEIGHT={5}" +
                                            "&REQUEST=map&ServiceName=WMS_NB_Kulturlandskap",
                                            cv.l_bo, cv.b_bo, cv.r_bo, cv.u_bo,
                                            Math.Round(cv.width / Math.Cos(((cv.u_bo + cv.b_bo) / 2) * DEG2RAD)), cv.height);*/


            String sz_operator = "";
            String sz_wms_url = "";
            String sz_wms_user = "";
            String sz_wms_password = "";
            OdbcDataReader rs = null;

            try
            {
                UmsDb db = new UmsDb();
                if (cv.sz_jobid.Equals("-1") || cv.sz_jobid.Trim().Equals(""))
                    throw new ULbaCellCoverageJobNotPreparedException(sz_operator);
                String sz_sql = String.Format("SELECT isnull(LO.sz_operatorname, ''), isnull(LO.sz_wms_url,''), isnull(LO.sz_wms_user,''), isnull(LO.sz_wms_password,'') FROM LBASEND LS, LBAOPERATORS LO WHERE LS.sz_jobid='{0}' AND LS.l_operator=LO.l_operator", cv.sz_jobid);
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    sz_operator = rs.GetString(0).Trim();
                    sz_wms_url = rs.GetString(1).Trim();
                    sz_wms_user = rs.GetString(2).Trim();
                    sz_wms_password = rs.GetString(3).Trim();
                    if (sz_wms_url.Length == 0)
                    {
                        rs.Close();
                        throw new ULbaCellCoverageWmsServerNotRegisteredException(sz_operator);
                    }
                }
                else
                {
                    rs.Close();
                    throw new ULbaCellCoverageJobIdNotFoundException(cv.sz_jobid);
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


            String sz_request = "";
            try
            {
                //LON LAT
                sz_request = String.Format(UCommon.UGlobalizationInfo,
                                                  //"http://lbv.netcom.no:8081/alertix/alertix.asp?" +
                                                  sz_wms_url + 
                                                  "?" +
                                                  //"VERSION={0}&REQUEST={1}&LAYERS={2}_{3}&STYLES={4}&" +
                                                  "VERSION={0}&REQUEST={1}&LAYERS={2}&STYLES={4}&" +
                                                  "SRS=EPSG:{5}&BBOX={6},{7},{8},{9}&WIDTH={10}&" +
                                                  "HEIGHT={11}&FORMAT=image/png",
                                                  cv.version, cv.sz_request, cv.sz_jobid, cv.layers,
                                                  "", cv.SRS, cv.l_bo, cv.b_bo, cv.r_bo, cv.u_bo,
                                                  Math.Round(cv.width / Math.Cos(((cv.u_bo + cv.b_bo) / 2) * DEG2RAD)), cv.height);
                //cv.width, Math.Round(cv.height * Math.Cos(((cv.u_bo + cv.b_bo) / 2) * DEG2RAD)));

                HttpWebRequest web = (HttpWebRequest)HttpWebRequest.Create(sz_request);
                //web.Credentials = new NetworkCredential("jone", "jone");
                HttpWebResponse response = (HttpWebResponse)web.GetResponse();
                UPASMap map = new UPASMap();
                if (response.ContentLength < 50)
                {
                    byte [] error = new byte[response.ContentLength];
                    response.GetResponseStream().Read(error, 0, (int)response.ContentLength);
                    String sz_error = System.Text.Encoding.GetEncoding(response.CharacterSet).GetString(error);
                    //throw new UMapLoadFailedException(new Exception(sz_error), m_mapinfo.l_bo + " | " + m_mapinfo.r_bo + " | " + m_mapinfo.u_bo + " | " + m_mapinfo.b_bo);
                    throw new UMapOverlayFailedException(sz_error, sz_operator, cv.sz_jobid);
                }
                map.createMemoryStream(response.GetResponseStream());
                map.setMapInfo(m_mapinfo);

                map.mapinfo.l_bo = m_mapinfo.l_bo;
                map.mapinfo.r_bo = m_mapinfo.r_bo;
                map.mapinfo.u_bo = m_mapinfo.u_bo;
                map.mapinfo.b_bo = m_mapinfo.b_bo;
                map.mapinfo.width = m_mapinfo.width;
                map.mapinfo.height = m_mapinfo.height;
                return map;
            }
            catch (Exception e)
            {
                ULog.error(0, "Error retrieving WMS layer", e.Message);
                //throw new UMapLoadFailedException(e, m_mapinfo.l_bo + " | " + m_mapinfo.r_bo + " | " + m_mapinfo.u_bo + " | " + m_mapinfo.b_bo);
                throw;
            }

        }
    }

    public class UFleximap : UGetMaps
    {
        public UFleximap(ref ULOGONINFO logoninfo, ref UMapInfo request)
            : base(logoninfo, request)
        {
            //sUrl = "http://api.fleximap.com/servlet/FlexiMap?OID=" & sz_compid & "_" & sz_deptid & "&UID=" & sz_userid & "&UPA=" & sz_passwd & "
            //&OP=drawarea&LBO=" & Replace(lBo,",",".") & "&RBO=" & Replace(rBo,",",".") & "&TBO=" & Replace(uBo,",",".") & "&BBO=" & Replace(bBo,",",".") & 
            //"&IW=" & lMapWidth & "&IH=" & lMapHeight & "&IT=Image&FileFormat=1&PL=" & szPortrayal & ";M"
        }
        public override UPASMap getMap()
        {
            String sz_request = String.Format(UCommon.UGlobalizationInfo,
                                "https://api.fleximap.com/servlet/FlexiMap?" +
                                "OID={0}_{1}" +
                                "&UID={2}" + 
                                "&UPA={3}" + 
                                "&OP=drawarea&" +
                                "&LBO={4}" + 
                                "&RBO={5}" + 
                                "&TBO={6}" + 
                                "&BBO={7}" + 
                                "&IW={8}" + 
                                "&IH={9}" + 
                                "&IT=0" +
                                "&IF=3" + //0 = GIF, 1 = JPG, 3 = PNG
                                "&IP=1" + //1=true color
                                "&PL={10}" + 
                                ";SYM",
                                //";M",
                                m_logoninfo.sz_compid, m_logoninfo.sz_deptid,
                                userid, password, m_mapinfo.l_bo, m_mapinfo.r_bo,
                                m_mapinfo.u_bo, m_mapinfo.b_bo,
                                m_mapinfo.width, m_mapinfo.height,
                                m_mapinfo.portrayal);


            HttpWebRequest web = null;
            HttpWebResponse response = null;

            try
            {
                web = (HttpWebRequest)HttpWebRequest.Create(sz_request);
            }
            catch (Exception e) { throw new Exception("Error creating http request in UMaps", e); }
            try
            {
                response = (HttpWebResponse)web.GetResponse();
            }
            catch (Exception e) { throw new Exception("Error getting http response in UMaps\n sz_request=" + sz_request, e); }
            
            UPASMap map = new UPASMap();
            try
            {
                String s_lbo, s_rbo, s_ubo, s_bbo;

                s_lbo = response.GetResponseHeader("TIME_LEFTBOUNDARY");
                s_rbo = response.GetResponseHeader("TIME_RIGHTBOUNDARY");
                s_ubo = response.GetResponseHeader("TIME_UPPERBOUNDARY");
                s_bbo = response.GetResponseHeader("TIME_LOWERBOUNDARY");

                m_mapinfo.l_bo = float.Parse(s_lbo, UCommon.UGlobalizationInfo);
                m_mapinfo.r_bo = float.Parse(s_rbo, UCommon.UGlobalizationInfo);
                m_mapinfo.u_bo = float.Parse(s_ubo, UCommon.UGlobalizationInfo);
                m_mapinfo.b_bo = float.Parse(s_bbo, UCommon.UGlobalizationInfo);
            }
            catch (Exception e)
            {
                throw new UMapLoadFailedException(e, m_mapinfo.l_bo + " | " + m_mapinfo.r_bo + " | " + m_mapinfo.u_bo + " | " + m_mapinfo.b_bo);
            }
            m_mapinfo.mapstatus = response.GetResponseHeader("MAPSTATUS");
            m_mapinfo.maptime = response.GetResponseHeader("MAPTIME");

            map.createMemoryStream(response.GetResponseStream());
            map.setMapInfo(m_mapinfo);
            return map;
        }
    }

}