using System;
using System.Configuration;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using System.Data.Odbc;
using System.Net;
using System.IO;
using System.Text;
using System.Data;
using System.Data.SqlClient;

namespace com.ums.PAS.Database
{
    public class ULogon : UmsDb
    {
        public ULogon()
            : base()
        {

        }

        public bool SaveUiSettings(ref ULOGONINFO l, ref UPASUISETTINGS ui)
        {
            OdbcDataReader rs = null;
            try
            {
                base.CheckLogon(ref l, true);
                String wms_layers = ui.sz_wms_layers;

                bool b_ret = false;
                //base.CheckLogon(ref l);
                String szSQL = String.Format("sp_pas_ins_ui {0}, '{1}', {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, " +
                                            "{11}, {12}, {13}, '{14}', '{15}', '{16}', '{17}', '{18}', '{19}', " +
                                            "{20}, '{21}', '{22}', '{23}', {24}, '{25}', '{26}', '{27}', {28}, {29}, '{30}', '{31}'",
                                            l.l_userpk,
                                            ui.sz_languageid,
                                            ui.f_mapinit_lbo,
                                            ui.f_mapinit_rbo,
                                            ui.f_mapinit_ubo,
                                            ui.f_mapinit_bbo,
                                            (ui.b_autostart_fleetcontrol ? 1 : 0),
                                            (ui.b_autostart_parm ? 1 : 0),
                                            (ui.b_window_fullscreen ? 1 : 0),
                                            ui.l_winpos_x,
                                            ui.l_winpos_y,
                                            ui.l_win_width,
                                            ui.l_win_height,
                                            ui.l_gis_max_for_details,
                                            ui.sz_skin_class,
                                            ui.sz_theme_class,
                                            ui.sz_watermark_class,
                                            ui.sz_buttonshaper_class,
                                            ui.sz_gradient_class,
                                            ui.sz_title_class,
                                            ui.l_mapserver,
                                            ui.sz_wms_site,
                                            wms_layers,
                                            ui.sz_wms_format,
                                            ui.l_drag_mode,
                                            ui.sz_email_name,
                                            ui.sz_email,
                                            ui.sz_emailserver,
                                            ui.l_mailport,
                                            ui.l_lba_update_percent,
                                            ui.sz_wms_username,
                                            ui.sz_wms_password);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    if (rs.GetInt32(0) >= 1)
                        b_ret = true;
                }
                rs.Close();

                return b_ret;
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

        protected bool SaveNsLookup(long n_userpk, ref UNSLOOKUP ns)
        {
            OdbcDataReader rs = null;
            try
            {
                String szSQL = String.Format("SELECT l_userpk, sz_location FROM BBUSER_NSLOOKUP WHERE l_userpk={0} AND "+
                                            "sz_domain='{1}'",
                                            n_userpk, ns.sz_domain);

                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    rs.Close();
                    szSQL = String.Format("UPDATE BBUSER_NSLOOKUP SET l_lastdatetime={0}, sz_ip='{1}', f_success={2}, " +
                                          "sz_location='{3}' WHERE l_userpk={4} AND sz_domain='{5}' ",
                                          ns.l_lastdatetime, ns.sz_ip, (ns.f_success ? 1 : 0), ns.sz_location,
                                          n_userpk, ns.sz_domain);
                    ExecNonQuery(szSQL);
                }
                else
                {
                    rs.Close();
                    szSQL = String.Format("INSERT INTO BBUSER_NSLOOKUP(l_userpk, sz_domain, sz_ip, l_lastdatetime, f_success, sz_location) " +
                                        "VALUES({0}, '{1}', '{2}', {3}, {4}, '{5}')",
                                        n_userpk, ns.sz_domain, ns.sz_ip, ns.l_lastdatetime, (ns.f_success ? 1 : 0), ns.sz_location);
                    ExecNonQuery(szSQL);
                }
                return true;
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

        protected bool NsLookup(ref UNSLOOKUP ns)
        {
            
            try
            {
                //Returns:ISO 3166 Two-letter Country Code, Region Code, City, Postal Code, Latitude, Longitude, Metropolitan Code, Area Code, ISP, Organization, Error code
                String remoteIP = HttpContext.Current.Request.UserHostAddress;
                String remoteHost = HttpContext.Current.Request.UserHostName;
                //NO,09,Sandnes,,68.583298,14.883300,0,0,"Ventelo Norge AS","BLUECOM-STATIC-IP-CUSTOMERS"

                ns.sz_ip = remoteIP;
                ns.sz_domain = remoteHost;
                ns.sz_location = "";
                try
                {
                    ns.l_lastdatetime = UCommon.UGetFullDateTimeNow().getDateTime();
                }
                catch(Exception)
                {
                    ns.l_lastdatetime = 0;
                }
                if (remoteIP.Length > 0)
                {
                    //remoteIP = "81.191.35.194";
                    //String szUrl = String.Format("{0}&i={1}", UCommon.UPATHS.sz_url_nslookup, remoteIP);
                    String szUrl = String.Format("{0}&i={1}", ConfigurationSettings.AppSettings["sz_url_nslookup"], remoteIP);
                    HttpWebRequest req = (HttpWebRequest)WebRequest.Create(szUrl);
                    HttpWebResponse response = (HttpWebResponse)req.GetResponse();
                    StreamReader sr = new StreamReader(response.GetResponseStream());
                    String output = sr.ReadToEnd();
                    String [] arr = output.Split(',');
                    try
                    {
                        sr.Close();
                        response.Close();
                    }
                    catch (Exception)
                    {
                    }


                    for (int i = 0; i < arr.Length; i++)
                        arr[i] = arr[i].Replace("\"", "");
                    
                    if (arr.Length >= 3)
                    {
                        if (arr[2].Length > 0)
                            ns.sz_location = arr[2];
                        if (arr[0].Length > 0)
                            ns.sz_location += " " + arr[0] + "\n";
                        if (ns.sz_location.Length == 0 && arr.Length >= 11)
                            ns.sz_location = arr[10];
                            
                        //ns.sz_location = arr[2] + " " + arr[0] + "\n";
                    }
                    if (arr.Length >= 10)
                    {
                        String provider = arr[8];
                        String providertext = arr[9];
                        if(provider.Length > 0)
                            ns.sz_location += provider;
                        if (!provider.Equals(providertext) && providertext.Length > 0)
                            ns.sz_location += " - " + providertext;
                    }
                    ns.sz_location = ns.sz_location.Trim();
                    if (ns.sz_location.Length >= 99)
                    {
                        ns.sz_location = ns.sz_location.Substring(0, 98);
                    }
                }
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public UPASUISETTINGS LoadLanguageAndVisualsBeforeLogon(ref ULOGONINFO l)
        {
            OdbcDataReader rs = null;
            try
            {
                UPASUISETTINGS ret = new UPASUISETTINGS();
                String szSQL = String.Format("SELECT BU.l_userpk FROM BBUSER BU, BBCOMPANY BC WHERE BU.sz_userid='{0}' AND BU.l_comppk=BC.l_comppk AND BC.sz_compid='{1}'",
                                    l.sz_userid, l.sz_compid);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                long n_userpk = 0;
                if (rs.Read())
                    n_userpk = rs.GetInt64(0);
                rs.Close();

                return LoadLanguageAndVisuals(n_userpk, true);
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

        protected UPASUISETTINGS LoadLanguageAndVisuals(long l_userpk, bool b_visualsonly)
        {
            OdbcDataReader rs = null;
            try
            {
                UPASUISETTINGS ret = new UPASUISETTINGS();
                ret.initialized = false;
                String szSQL;
                szSQL = String.Format("sp_pas_get_ui {0}", l_userpk);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    ret.initialized = true;
                    ret.sz_languageid = rs.GetString(0);
                    if (!b_visualsonly)
                    {
                        ret.f_mapinit_lbo = rs.GetDouble(1);
                        ret.f_mapinit_rbo = rs.GetDouble(2);
                        ret.f_mapinit_ubo = rs.GetDouble(3);
                        ret.f_mapinit_bbo = rs.GetDouble(4);
                        ret.b_autostart_fleetcontrol = (rs.GetInt32(5) >= 1 ? true : false);
                        ret.b_autostart_parm = (rs.GetInt32(6) >= 1 ? true : false);
                        ret.b_window_fullscreen = (rs.GetInt32(7) >= 1 ? true : false);
                        ret.l_winpos_x = rs.GetInt32(8);
                        ret.l_winpos_y = rs.GetInt32(9);
                        ret.l_win_width = rs.GetInt32(10);
                        ret.l_win_height = rs.GetInt32(11);
                        ret.l_gis_max_for_details = rs.GetInt32(12);
                    }
                    ret.sz_skin_class = rs.GetString(13);
                    ret.sz_theme_class = rs.GetString(14);
                    ret.sz_watermark_class = rs.GetString(15);
                    ret.sz_buttonshaper_class = rs.GetString(16);
                    ret.sz_gradient_class = rs.GetString(17);
                    ret.sz_title_class = rs.GetString(18);
                    if (!b_visualsonly)
                    {
                        ret.l_mapserver = rs.GetInt32(19);
                        ret.sz_wms_site = rs.GetString(20);
                        ret.sz_wms_layers = rs.GetString(21);
                        ret.sz_wms_format = rs.GetString(22);
                        ret.l_drag_mode = rs.GetInt32(23);
                        ret.sz_email_name = rs.GetString(24);
                        ret.sz_email = rs.GetString(25);
                        ret.sz_emailserver = rs.GetString(26);
                        ret.l_mailport = rs.GetInt32(27);
                        ret.l_lba_update_percent = rs.GetInt32(28);
                        ret.sz_wms_username = rs.GetString(29);
                        ret.sz_wms_password = rs.GetString(30);
                    }
                }
                rs.Close();

                return ret;
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

        public bool Logoff(ref ULOGONINFO l)
        {
            try
            {
                return RemoveSession(ref l);
            }
            catch (Exception)
            {
                throw;
            }
        }


        public UPASLOGON Logon(ref ULOGONINFO l)
        {
            UPASLOGON ret = new UPASLOGON();
            String szSQL;
            OdbcDataReader rs = null;

            try
            {
                bool b_default_dept_set = false;
                l.sz_compid = l.sz_compid.ToUpper();
                l.sz_userid = l.sz_userid.ToUpper();
                l.sz_compid = l.sz_compid.Replace("'", "''");
                l.sz_userid = l.sz_userid.Replace("'", "''");
                l.sz_password = l.sz_password.Replace("'", "''");
                byte[] utf8pass = Encoding.UTF8.GetBytes(l.sz_password);
                byte[] asciipass = Encoding.ASCII.GetBytes(l.sz_password);
                String pass = Encoding.GetEncoding("iso-8859-1").GetString(utf8pass);

                OdbcCommand cmdLogon = conn.CreateCommand();
                cmdLogon.CommandType = CommandType.StoredProcedure;
                cmdLogon.CommandText = "{ CALL sp_pas_logon (?,?,?,?) }";
                cmdLogon.Parameters.Add("@sz_userid", OdbcType.VarChar, 50).Value = l.sz_userid;
                cmdLogon.Parameters.Add("@sz_paspassword", OdbcType.Char, 128).Value = l.sz_password;
                cmdLogon.Parameters.Add("@sz_compid", OdbcType.VarChar, 50).Value = l.sz_compid;
                cmdLogon.Parameters.Add("@l_max_tries", OdbcType.Int).Value = 0;//0 will get max from db. UCommon.USETTINGS.l_max_logontries;
                rs = cmdLogon.ExecuteReader();

                if (!rs.HasRows)  //logon failed
                {
                    rs.Close();
                    ret.f_granted = false;
                    ret.l_comppk = 0;
                    ret.sessionid = "-1";
                    try
                    {
                        //find the userpk to log a failed logon
                        szSQL = String.Format("SELECT BU.l_userpk FROM BBUSER BU, BBCOMPANY BC WHERE BU.sz_userid='{0}' AND BU.l_comppk=BC.l_comppk AND BC.sz_compid='{1}'",
                                                l.sz_userid, l.sz_compid);
                        rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                        if (rs.Read())
                        {
                            ret.l_userpk = rs.GetInt64(0);
                        }
                        else
                            ret.l_userpk = 0;
                        rs.Close();
                    }
                    catch (Exception)
                    {
                        ret.l_userpk = 0;
                        throw;
                    }
                }
                else //logon ok
                {
                    object block_reason = rs["l_blocked_reasoncode"];
                    BBUSER_BLOCK_REASONS reason = (BBUSER_BLOCK_REASONS)Enum.Parse(typeof(BBUSER_BLOCK_REASONS), block_reason.ToString());
                    switch (reason)
                    {
                        case BBUSER_BLOCK_REASONS.NONE:
                            ret.f_granted = true;
                            ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                            break;
                        case BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN:
                        case BBUSER_BLOCK_REASONS.REACHED_RETRY_LIMIT:
                            ret.f_granted = false;
                            ret.reason = reason;
                            break;
                    }
                }
                UNSLOOKUP nsl = new UNSLOOKUP();
                nsl.l_lastdatetime = UCommon.UGetFullDateTimeNow().getDateTime();
                nsl.sz_ip = HttpContext.Current.Request.UserHostAddress;
                nsl.sz_domain = HttpContext.Current.Request.UserHostName;
                nsl.sz_location = "Unknown";
                nsl.f_success = ret.f_granted;

                if (ret.l_userpk > 0 && !ret.f_granted)
                {
                    if (UCommon.USETTINGS.b_enable_nslookup)
                    {
                        NsLookup(ref nsl);
                        SaveNsLookup(ret.l_userpk, ref nsl);
                    }
                    ULog.error(String.Format("{5} - Error in logon credentials (wrong password): {0}/{1} \nRequest from (domain/ip/location): {2}/{3}/{4}\nUsing password: {6}", l.sz_userid.ToUpper(), l.sz_compid.ToUpper(), nsl.sz_domain, nsl.sz_ip, nsl.sz_location, DateTime.Now.ToLocalTime(), (l.sz_password.Length < 128 ? l.sz_password : "[Hashed]")));
                }
                else if (ret.l_userpk <= 0)
                {
                    if (UCommon.USETTINGS.b_enable_nslookup)
                        NsLookup(ref nsl);
                    ULog.error(String.Format("{5} - Error in logon credentials (user/comp combination not found): {0}/{1} \nRequest from (domain/ip/location): {2}/{3}/{4} \nUsing password: {6}", l.sz_userid.ToUpper(), l.sz_compid.ToUpper(), nsl.sz_domain, nsl.sz_ip, nsl.sz_location, DateTime.Now.ToLocalTime(), (l.sz_password.Length < 128 ? l.sz_password : "[Hashed]")));
                }

                if (!ret.f_granted)
                {
                    ret.l_userpk = 0;
                    return ret;
                }


                if (rs.Read()) //logon succeeded
                {
                    ret.f_granted = true;
                    ret.sz_name = rs["sz_name"].ToString();
                    ret.sz_surname = rs["sz_surname"].ToString();
                    ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                    ret.l_comppk = Int32.Parse(rs["l_comppk"].ToString());
                    ret.sz_userid = rs["sz_userid"].ToString();
                    ret.sz_compid = rs["sz_compid"].ToString();
                    ret.l_language = Int32.Parse(rs["l_language"].ToString());
                    ret.sessionid = l.sessionid;
                    ret.sz_organization = rs["sz_organization"].ToString();
                    ret.default_oadc.l_type = int.Parse(rs["l_oadc_type"].ToString());
                    ret.default_oadc.sz_value = rs["sz_oadc_value"].ToString();
                    do //parse departments
                    {
                        UDEPARTMENT dept = new UDEPARTMENT(); //CREATE NEW DEPARTMENT

                        try
                        {
                            String sz_nav = rs["l_mapinit"].ToString().Replace(",", ".");
                            String[] bounds = sz_nav.Split('|');
                            if (bounds.Length >= 4)
                            {
                                dept.lbo = float.Parse(bounds[0], UCommon.UGlobalizationInfo);
                                dept.ubo = float.Parse(bounds[1], UCommon.UGlobalizationInfo);
                                dept.rbo = float.Parse(bounds[2], UCommon.UGlobalizationInfo);
                                dept.bbo = float.Parse(bounds[3], UCommon.UGlobalizationInfo);
                            }
                            else
                            {
                                dept.lbo = 3.0f;
                                dept.rbo = 31.0f;
                                dept.ubo = 71.0f;
                                dept.bbo = 57.0f;

                            }
                        }
                        catch (Exception)
                        {
                            dept.lbo = 3.0f;
                            dept.rbo = 31.0f;
                            dept.ubo = 71.0f;
                            dept.bbo = 57.0f;
                        }
                        Int32 l_deptpk, l_default_deptpk;
                        long l_profilepk, l_default_profilepk;
                        Int32 l_dept_parm, l_dept_fleetcontrol, l_dept_houseeditor, l_dept_pas_send, l_pas_send;

                        l_deptpk = Int32.Parse(rs["l_deptpk"].ToString());
                        l_default_deptpk = Int32.Parse(rs["l_default_deptpk"].ToString());
                        l_profilepk = long.Parse(rs["l_profilepk"].ToString());
                        l_default_profilepk = long.Parse(rs["l_default_profilepk"].ToString());
                        if (l_deptpk == l_default_deptpk && l_profilepk == l_default_profilepk)
                        {
                            dept.f_default = true;
                            b_default_dept_set = true;
                        }
                        else
                            dept.f_default = false;

                        dept.l_deptpk = l_deptpk;
                        dept.sz_deptid = rs["sz_deptid"].ToString();
                        dept.sz_stdcc = rs["sz_stdcc"].ToString();
                        dept.l_deptpri = Int32.Parse(rs["l_deptpri"].ToString());
                        dept.l_maxalloc = Int32.Parse(rs["l_maxalloc"].ToString());
                        dept.sz_userprofilename = rs["sz_userprofilename"].ToString();
                        dept.sz_userprofiledesc = rs["sz_userprofiledesc"].ToString();
                        dept.l_status = Int32.Parse(rs["l_status"].ToString());
                        dept.l_newsending = Int32.Parse(rs["l_newsending"].ToString());
                        dept.l_parm = Int32.Parse(rs["l_parm"].ToString());
                        dept.l_fleetcontrol = Int32.Parse(rs["l_fleetcontrol"].ToString());
                        l_dept_parm = Int32.Parse(rs["l_dept_parm"].ToString());
                        l_dept_fleetcontrol = Int32.Parse(rs["l_dept_fleetcontrol"].ToString());
                        l_dept_houseeditor = Int32.Parse(rs["l_dept_houseeditor"].ToString());
                        dept.l_houseeditor = Int32.Parse(rs["l_houseeditor"].ToString());
                        l_dept_pas_send = Int32.Parse(rs["l_dept_pas_send"].ToString());
                        l_pas_send = Int32.Parse(rs["l_pas_send"].ToString());
                        dept.l_addresstypes = long.Parse(rs["l_addresstypes"].ToString());
                        dept.sz_defaultnumber = rs["sz_defaultnumber"].ToString();
                        dept.f_map = Int32.Parse(rs["f_map"].ToString());
                        dept.f_sms = Int32.Parse(rs["f_sms"].ToString());
                        dept.l_pas = Int32.Parse(rs["l_dept_pas"].ToString());
                        dept.l_langpk = Int32.Parse(rs["l_langpk"].ToString());
                        String xml = rs["sz_restriction_shape"].ToString();
                        UShape restrictionshape = UShape.ParseFromXml(xml);
                        dept.AddRestrictionShape(ref restrictionshape);

                        if (l_dept_houseeditor <= 0)
                            dept.l_houseeditor = 0;
                        if (l_dept_pas_send <= 0)
                            dept.l_newsending = 0;
                        if ((l_pas_send & 1) == 1 && (l_dept_pas_send & 1) == 0)
                            l_pas_send &= ~1;
                        //l_pas_send -= 1;
                        if ((l_pas_send & 2) == 2 && (l_dept_pas_send & 2) == 0)
                            l_pas_send &= ~2;
                        //l_pas_send -= 2;
                        dept.l_newsending = l_pas_send;

                        if (l_dept_parm <= 0)
                            dept.l_parm = 0;
                        if (l_dept_fleetcontrol <= 0)
                            dept.l_fleetcontrol = 0;

                        //check the folkereg adr database of this department if the user has access to municipals
                        if (UCommon.USETTINGS.b_enable_adrdb)
                        {
                            try
                            {
                                UmsDbConnParams p = new UmsDbConnParams();
                                p.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase + dept.sz_stdcc; // +"_reg";
                                p.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
                                p.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;
                                UAdrDb adr = new UAdrDb(dept.sz_stdcc, 60, dept.l_deptpk);
                                dept.municipals = adr.GetMunicipalsByDept(dept.l_deptpk);
                                adr.close();
                            }
                            catch (Exception)
                            {

                            }
                        }
                        //if(dept.f_map>0)
                        ret.departments.Add(dept); //ADD DEPARTMENT TO LIST
                    } while (rs.Read());

                    if (!b_default_dept_set)
                    {
                        if (ret.departments.Count > 0)
                            ret.departments[0].f_default = true; //set a department to default
                    }

                    rs.Close();

                    //Create a new session
                    //start session
                    try
                    {
                        String szSessionSql = String.Format("sp_pas_startsession {0}, '{1}', '{2}'",
                                                ret.l_userpk, l.sessionid, l.sz_password);
                        ExecNonQuery(szSessionSql);
                    }
                    catch (Exception)
                    {
                        throw;
                    }


                    if (UCommon.USETTINGS.b_enable_nslookup)
                    {
                        //READ NS TABLE
                        szSQL = String.Format("SELECT * FROM BBUSER_NSLOOKUP WHERE l_userpk={0} ORDER BY l_lastdatetime DESC",
                                               ret.l_userpk);
                        rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                        while (rs.Read())
                        {
                            UNSLOOKUP ns = new UNSLOOKUP();
                            ns.sz_domain = rs["sz_domain"].ToString();
                            ns.sz_ip = rs["sz_ip"].ToString();
                            ns.l_lastdatetime = long.Parse(rs["l_lastdatetime"].ToString());
                            ns.sz_location = rs["sz_location"].ToString();
                            ns.f_success = bool.Parse((rs["f_success"].ToString().Equals("1") ? "true" : "false"));
                            ret.nslookups.Add(ns);
                        }
                        rs.Close();
                    }

                    //READ UI SETTINGS
                    ret.uisettings = LoadLanguageAndVisuals(ret.l_userpk, false);

                } //end of department read
                /*else //no records, logon failed
                {
                    ret.f_granted = false;
                    ret.l_comppk = 0;
                    try
                    {
                        //find the userpk to log a failed logon
                        szSQL = String.Format("SELECT BU.l_userpk FROM BBUSER BU, BBCOMPANY BC WHERE BU.sz_userid='{0}' AND BU.l_comppk=BC.l_comppk AND BC.sz_compid='{1}'",
                                                l.sz_userid, l.sz_compid);
                        rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                        if (rs.Read())
                        {
                            ret.l_userpk = rs.GetInt64(0);
                            if (ret.l_userpk > 0)
                            {
                                UNSLOOKUP ns = new UNSLOOKUP();
                                NsLookup(ref ns);
                                ns.f_success = ret.f_granted;
                                SaveNsLookup(ret.l_userpk, ref ns);
                            }
                        }
                        else
                            ret.l_userpk = 0;
                    }
                    catch (Exception)
                    {
                        ret.l_userpk = 0;
                    }
                }*/


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

            return ret;
        }

        public UPASLOGON CBAdminLogon(ref ULOGONINFO l) // Fort å gale, akkurat likt bare endret SP
        {
            UPASLOGON ret = new UPASLOGON();
            String szSQL;
            OdbcDataReader rs = null;

            try
            {
                bool b_default_dept_set = false;
                l.sz_compid = l.sz_compid.ToUpper();
                l.sz_userid = l.sz_userid.ToUpper();
                l.sz_compid = l.sz_compid.Replace("'", "''");
                l.sz_userid = l.sz_userid.Replace("'", "''");
                l.sz_password = l.sz_password.Replace("'", "''");
                byte[] utf8pass = Encoding.UTF8.GetBytes(l.sz_password);
                byte[] asciipass = Encoding.ASCII.GetBytes(l.sz_password);
                String pass = Encoding.GetEncoding("iso-8859-1").GetString(utf8pass);
                //pass = "mh123,1µ°Õ";
                //l.sz_password = l.sz_password.ToUpper();
                //Get userinfo

                szSQL = String.Format("sp_cb_pasadmin_logon '{0}', '{1}', '{2}'",
                    l.sz_userid,
                    l.sz_password,
                    l.sz_compid);
                rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);


                if (!rs.HasRows)  //logon failed
                {
                    ret.f_granted = false;
                    ret.l_comppk = 0;
                    ret.sessionid = "-1";
                    try
                    {
                        //find the userpk to log a failed logon
                        szSQL = String.Format("SELECT BU.l_userpk FROM BBUSER BU, BBCOMPANY BC WHERE BU.sz_userid='{0}' AND BU.l_comppk=BC.l_comppk AND BC.sz_compid='{1}'",
                                                l.sz_userid, l.sz_compid);
                        rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
                        if (rs.Read())
                        {
                            ret.l_userpk = rs.GetInt64(0);
                        }
                        else
                            ret.l_userpk = 0;
                        rs.Close();
                    }
                    catch (Exception)
                    {
                        ret.l_userpk = 0;
                        throw;
                    }
                }
                else //logon ok
                {
                    ret.f_granted = true;
                    ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                }
                if (ret.l_userpk > 0 && UCommon.USETTINGS.b_enable_nslookup)
                {
                    UNSLOOKUP ns = new UNSLOOKUP();
                    NsLookup(ref ns);
                    ns.f_success = ret.f_granted;
                    SaveNsLookup(ret.l_userpk, ref ns);
                }

                if (!ret.f_granted)
                {
                    ret.l_userpk = 0;
                    return ret;
                }

                int reasoncode = 0;
                if (rs.Read()) //logon succeeded
                {
                    reasoncode = int.Parse(rs["l_blocked_reasoncode"].ToString());
                    if (reasoncode == 0)
                    {
                        ret.f_granted = true;
                        ret.sz_name = rs["sz_name"].ToString();
                        ret.sz_surname = rs["sz_surname"].ToString();
                        ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                        ret.l_comppk = Int32.Parse(rs["l_comppk"].ToString());
                        ret.sz_userid = rs["sz_userid"].ToString();
                        ret.sz_compid = rs["sz_compid"].ToString();
                        ret.l_language = Int32.Parse(rs["l_language"].ToString());
                        ret.sessionid = l.sessionid;
                        do //parse departments
                        {
                            UDEPARTMENT dept = new UDEPARTMENT(); //CREATE NEW DEPARTMENT

                            try
                            {
                                String sz_nav = rs["l_mapinit"].ToString().Replace(",", ".");
                                String[] bounds = sz_nav.Split('|');
                                if (bounds.Length >= 4)
                                {
                                    dept.lbo = float.Parse(bounds[0], UCommon.UGlobalizationInfo);
                                    dept.ubo = float.Parse(bounds[1], UCommon.UGlobalizationInfo);
                                    dept.rbo = float.Parse(bounds[2], UCommon.UGlobalizationInfo);
                                    dept.bbo = float.Parse(bounds[3], UCommon.UGlobalizationInfo);
                                }
                                else
                                {
                                    dept.lbo = 3.0f;
                                    dept.rbo = 31.0f;
                                    dept.ubo = 71.0f;
                                    dept.bbo = 57.0f;

                                }
                            }
                            catch (Exception)
                            {
                                dept.lbo = 3.0f;
                                dept.rbo = 31.0f;
                                dept.ubo = 71.0f;
                                dept.bbo = 57.0f;
                            }
                            Int32 l_deptpk, l_default_deptpk;
                            long l_profilepk, l_default_profilepk;
                            Int32 l_dept_parm, l_dept_fleetcontrol, l_dept_houseeditor, l_dept_pas_send, l_pas_send;

                            l_deptpk = Int32.Parse(rs["l_deptpk"].ToString());
                            l_default_deptpk = Int32.Parse(rs["l_default_deptpk"].ToString());
                            l_profilepk = long.Parse(rs["l_profilepk"].ToString());
                            l_default_profilepk = long.Parse(rs["l_default_profilepk"].ToString());
                            if (l_deptpk == l_default_deptpk && l_profilepk == l_default_profilepk)
                            {
                                dept.f_default = true;
                                b_default_dept_set = true;
                            }
                            else
                                dept.f_default = false;

                            dept.l_deptpk = l_deptpk;
                            dept.sz_deptid = rs["sz_deptid"].ToString();
                            dept.sz_stdcc = rs["sz_stdcc"].ToString();
                            dept.l_deptpri = Int32.Parse(rs["l_deptpri"].ToString());
                            dept.l_maxalloc = Int32.Parse(rs["l_maxalloc"].ToString());
                            dept.sz_userprofilename = rs["sz_userprofilename"].ToString();
                            dept.sz_userprofiledesc = rs["sz_userprofiledesc"].ToString();
                            dept.l_status = Int32.Parse(rs["l_status"].ToString());
                            dept.l_newsending = Int32.Parse(rs["l_newsending"].ToString());
                            dept.l_parm = Int32.Parse(rs["l_parm"].ToString());
                            dept.l_fleetcontrol = Int32.Parse(rs["l_fleetcontrol"].ToString());
                            l_dept_parm = Int32.Parse(rs["l_dept_parm"].ToString());
                            l_dept_fleetcontrol = Int32.Parse(rs["l_dept_fleetcontrol"].ToString());
                            l_dept_houseeditor = Int32.Parse(rs["l_dept_houseeditor"].ToString());
                            dept.l_houseeditor = Int32.Parse(rs["l_houseeditor"].ToString());
                            l_dept_pas_send = Int32.Parse(rs["l_dept_pas_send"].ToString());
                            l_pas_send = Int32.Parse(rs["l_pas_send"].ToString());
                            dept.l_addresstypes = long.Parse(rs["l_addresstypes"].ToString());
                            dept.sz_defaultnumber = rs["sz_defaultnumber"].ToString();
                            dept.f_map = Int32.Parse(rs["f_map"].ToString());
                            dept.l_pas = Int32.Parse(rs["l_dept_pas"].ToString());
                            String xml = rs["sz_restriction_shape"].ToString();
                            UShape restrictionshape = UShape.ParseFromXml(xml);
                            dept.AddRestrictionShape(ref restrictionshape);

                            if (l_dept_houseeditor <= 0)
                                dept.l_houseeditor = 0;
                            if (l_dept_pas_send <= 0)
                                dept.l_newsending = 0;
                            if ((l_pas_send & 1) == 1 && (l_dept_pas_send & 1) == 0)
                                l_pas_send -= 1;
                            if ((l_pas_send & 2) == 2 && (l_dept_pas_send & 2) == 0)
                                l_pas_send -= 2;
                            dept.l_newsending = l_pas_send;

                            if (l_dept_parm <= 0)
                                dept.l_parm = 0;
                            if (l_dept_fleetcontrol <= 0)
                                dept.l_fleetcontrol = 0;

                            //check the folkereg adr database of this department if the user has access to municipals
                            if (UCommon.USETTINGS.b_enable_adrdb)
                            {
                                try
                                {
                                    UmsDbConnParams p = new UmsDbConnParams();
                                    p.sz_dsn = UCommon.UBBDATABASE.sz_adrdb_dsnbase + dept.sz_stdcc; // +"_reg";
                                    p.sz_uid = UCommon.UBBDATABASE.sz_adrdb_uid;
                                    p.sz_pwd = UCommon.UBBDATABASE.sz_adrdb_pwd;
                                    UAdrDb adr = new UAdrDb(dept.sz_stdcc, 60, dept.l_deptpk);
                                    dept.municipals = adr.GetMunicipalsByDept(dept.l_deptpk);
                                    adr.close();
                                }
                                catch (Exception)
                                {

                                }
                            }
                            //if(dept.f_map>0)
                            ret.departments.Add(dept); //ADD DEPARTMENT TO LIST
                        } while (rs.Read());

                        if (!b_default_dept_set)
                        {
                            if (ret.departments.Count > 0)
                                ret.departments[0].f_default = true; //set a department to default
                        }

                        rs.Close();

                        //Create a new session
                        //start session
                        try
                        {
                            String szSessionSql = String.Format("sp_pas_startsession {0}, '{1}', '{2}'",
                                                    ret.l_userpk, l.sessionid, l.sz_password);
                            ExecNonQuery(szSessionSql);
                        }
                        catch (Exception)
                        {
                            throw;
                        }


                        if (UCommon.USETTINGS.b_enable_nslookup)
                        {
                            //READ NS TABLE
                            szSQL = String.Format("SELECT * FROM BBUSER_NSLOOKUP WHERE l_userpk={0} ORDER BY l_lastdatetime DESC",
                                                   ret.l_userpk);
                            rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                            while (rs.Read())
                            {
                                UNSLOOKUP ns = new UNSLOOKUP();
                                ns.sz_domain = rs["sz_domain"].ToString();
                                ns.sz_ip = rs["sz_ip"].ToString();
                                ns.l_lastdatetime = long.Parse(rs["l_lastdatetime"].ToString());
                                ns.sz_location = rs["sz_location"].ToString();
                                ns.f_success = bool.Parse((rs["f_success"].ToString().Equals("1") ? "true" : "false"));
                                ret.nslookups.Add(ns);
                            }
                            rs.Close();
                        }

                        //READ UI SETTINGS
                        ret.uisettings = LoadLanguageAndVisuals(ret.l_userpk, false);

                    } //end of department read
                    else
                    {
                        rs.Close();
                        throw new Exception("Logon failed: " + Enum.GetName(typeof(BBUSER_BLOCK_REASONS), reasoncode));
                    }
                    /*else //no records, logon failed
                    {
                        ret.f_granted = false;
                        ret.l_comppk = 0;
                        try
                        {
                            //find the userpk to log a failed logon
                            szSQL = String.Format("SELECT BU.l_userpk FROM BBUSER BU, BBCOMPANY BC WHERE BU.sz_userid='{0}' AND BU.l_comppk=BC.l_comppk AND BC.sz_compid='{1}'",
                                                    l.sz_userid, l.sz_compid);
                            rs = ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                            if (rs.Read())
                            {
                                ret.l_userpk = rs.GetInt64(0);
                                if (ret.l_userpk > 0)
                                {
                                    UNSLOOKUP ns = new UNSLOOKUP();
                                    NsLookup(ref ns);
                                    ns.f_success = ret.f_granted;
                                    SaveNsLookup(ret.l_userpk, ref ns);
                                }
                            }
                            else
                                ret.l_userpk = 0;
                        }
                        catch (Exception)
                        {
                            ret.l_userpk = 0;
                        }
                    }*/

                }

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


            return ret;
        }

    }
}
