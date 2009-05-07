using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Data.Odbc;
using System.Net;
using System.IO;

namespace com.ums.PAS.Database
{
    public class ULogon : UmsDb
    {
        public ULogon()
            : base()
        {

        }

        protected bool SaveNsLookup(long n_userpk, ref UNSLOOKUP ns)
        {
            try
            {
                String szSQL = String.Format("SELECT l_userpk, sz_location FROM BBUSER_NSLOOKUP WHERE l_userpk={0} AND "+
                                            "sz_domain='{1}'",
                                            n_userpk, ns.sz_domain);

                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);
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
            catch (Exception e)
            {
                throw e;
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
                    String szUrl = String.Format("{0}&i={1}", UCommon.UPATHS.sz_url_nslookup, remoteIP);
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
            catch (Exception e)
            {
                throw e;
            }
        }

        public UPASLOGON Logon(ref ULOGONINFO l)
        {
            UPASLOGON ret = new UPASLOGON();
            String szSQL;

            try
            {
                bool b_default_dept_set = false;
                l.sz_compid = l.sz_compid.ToUpper();
                l.sz_userid = l.sz_userid.ToUpper();
                l.sz_password = l.sz_password.ToUpper();
                //Get userinfo
                szSQL = String.Format("SELECT BU.l_userpk, BU.sz_name, BU.sz_surname, BU.l_deptpk l_default_deptpk, " +
                                    "isnull(BU.l_profilepk,0) l_default_profilepk, isnull(BUXD.l_profilepk,-1) l_profilepk, " +
                                    "BD.l_deptpk, BU.l_comppk, BU.sz_userid, BD.sz_deptid, BC.sz_compid, " +
                                    "isnull(BD.l_mapinit, '') l_mapinit, isnull(BD.sz_stdcc, '0047') sz_stdcc, BD.l_deptpri, " +
                                    "isnull(BD.l_maxalloc, 180) l_maxalloc, BUP.sz_name sz_userprofilename, " +
                                    "BUP.sz_description sz_userprofiledesc, isnull(BUP.l_status, 0) l_status, " +
                                    "isnull(l_newsending, 0) l_newsending, isnull(BUP.l_parm, 0) l_parm, " +
                                    "isnull(BUP.l_fleetcontrol, 0) l_fleetcontrol, BD.l_pas l_dept_pas, BD.l_parm l_dept_parm, " +
                                    "BD.l_fleetcontrol l_dept_fleetcontrol, isnull(BD.l_houseeditor, 0) l_dept_houseeditor, " +
                                    "isnull(BUP.l_houseeditor, 0) l_houseeditor, isnull(BD.l_pas_send, 0) l_dept_pas_send, " +
                                    "isnull(BUP.l_pas_send, 0) l_pas_send, BD.l_addresstypes, BD.sz_defaultnumber " +
                                    "FROM BBUSER BU, BBCOMPANY BC, v_BBDEPARTMENT BD, BBUSERPROFILE_X_DEPT BUXD, BBUSERPROFILE BUP " +
                                    "WHERE UPPER(BU.sz_userid)='{0}' AND UPPER(BU.sz_paspassword)='{1}' AND BU.l_comppk=BC.l_comppk AND " +
                                    "UPPER(BC.sz_compid)='{2}' AND BUXD.l_userpk=BU.l_userpk AND BUXD.l_deptpk=BD.l_deptpk AND " +
                                    "BUXD.l_userpk=BU.l_userpk AND BUP.l_profilepk=BUXD.l_profilepk AND BD.l_pas>=1",
                                    l.sz_userid, l.sz_password, l.sz_compid);
                OdbcDataReader rs = ExecReader(szSQL, UmsDb.UREADER_KEEPOPEN);


                if (!rs.HasRows)  //logon failed
                {
                    ret.f_granted = false;
                    ret.l_comppk = 0;
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
                    catch (Exception e)
                    {
                        ret.l_userpk = 0;
                        throw e;
                    }
                }
                else //logon ok
                {
                    ret.f_granted = true;
                    ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                }
                if (ret.l_userpk > 0)
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


                if (rs.Read()) //logon succeeded
                {
                    ret.f_granted = true;
                    ret.sz_name = rs["sz_name"].ToString();
                    ret.sz_surname = rs["sz_surname"].ToString();
                    ret.l_userpk = long.Parse(rs["l_userpk"].ToString());
                    ret.l_comppk = Int32.Parse(rs["l_comppk"].ToString());
                    ret.sz_userid = rs["sz_userid"].ToString();
                    ret.sz_compid = rs["sz_compid"].ToString();
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
                        }
                        catch (Exception)
                        {
                            dept.lbo = 0.0f;
                            dept.rbo = 0.0f;
                            dept.ubo = 0.0f;
                            dept.bbo = 0.0f;
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



                        ret.departments.Add(dept); //ADD DEPARTMENT TO LIST
                    } while (rs.Read());

                    if (!b_default_dept_set)
                    {
                        if (ret.departments.Count > 0)
                            ret.departments[0].f_default = true; //set a department to default
                    }

                    rs.Close();

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
            catch (Exception e)
            {
                throw e;
            }

            return ret;
        }

    }
}
