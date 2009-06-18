using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;
using System.Security.AccessControl;
using System.Collections;

namespace com.ums.UmsCommon
{
    public enum ProgressJobType
    {
        GEMINI_IMPORT_STREETID = 1,
        GEMINI_IMPORT_GNRBNR = 2,
        STATUS_LIST = 3,
        STATUS_ITEMS = 4,
        PARM_UPDATE = 5,
        HOUSE_DOWNLOAD = 6,


    }

    public class PercentResult
    {
        public int n_percent;
        public int n_totalrecords;
        public int n_currentrecord;
    }

    public class PercentProgress
    {
        
        public static SetPercentDelegate newDelegate() { return new SetPercentDelegate(SetJob); }
        public delegate void SetPercentDelegate(ref ULOGONINFO l, ProgressJobType jobType, PercentResult val);
        /*
         * inserts and updates a job with new value
         */
        public static void SetJob(ref ULOGONINFO l, ProgressJobType jobType, PercentResult val)
        {
            TempDataStore.SetRecords(jobType + "_" + l.l_comppk + "_" + l.l_deptpk + "_" + l.l_userpk + "_" + l.jobid, val);
        }
        /*
         * delete an active job
         */
        public static void DeleteJob(ref ULOGONINFO l, ProgressJobType jobType)
        {
            TempDataStore.Remove(jobType + "_" + l.l_comppk + "_" + l.l_deptpk + "_" + l.l_userpk + "_" + l.jobid);
        }
        public static PercentResult GetProgress(ref ULOGONINFO l, ProgressJobType jobType)
        {
            return (PercentResult)TempDataStore.GetRecords(jobType + "_" + l.l_comppk + "_" + l.l_deptpk + "_" + l.l_userpk + "_" + l.jobid);
        }
        public static PercentResult [] GetJobs()
        {
            try
            {
                Hashtable h = TempDataStore.getJobs();
                PercentResult[] ret = new PercentResult[h.Count];
                for (int i = 0; i < h.Count; i++)
                {
                    ret[i] = (PercentResult)h[i];
                }
                return ret;
            }
            catch (Exception)
            {
                return null;
            }
        }
    }
    /*
     * Session data for progress. Used for polling percentage of completion of different tasks
     */
    public class TempDataStore
    {
        private static Hashtable _table = new Hashtable();
        static TempDataStore() { }
        public static int getJobCount() { return _table.Count; }
        public static PercentResult getJob(int n) {
            try
            {
                return (PercentResult)_table[n];
            }
            catch(Exception)
            {
                return null;
            }
        }
        public static Hashtable getJobs()
        {
            try
            {
                return _table;
            }
            catch (Exception)
            {
                return null;
            }
        }
        public static object GetRecords(string key)
        {
            lock (_table.SyncRoot)
            {
                return _table[key];
            }
        }
        public static void SetRecords(string key, object value)
        {
            lock (_table.SyncRoot)
            {
                if (_table[key] != null)
                    _table[key] = value;
                else
                    _table.Add(key, value);
            }
        }
        public static void Remove(string key)
        {
            lock (_table.SyncRoot)
            {
                _table.Remove(key);
            }
        }
        public static void ClearAll()
        {
            lock (_table.SyncRoot)
            {
                _table.Clear();
            }
        }
    }
    public class UMapBounds
    {
        public double l_bo = 10.3f;
        public double r_bo = 11.5f;
        public double u_bo = 59.9f;
        public double b_bo = 58.9f;
        public void reset()
        {
            l_bo = 360;
            r_bo = -360;
            u_bo = -360;
            b_bo = 360;
        }
    }

    public class UMapPoint
    {
        public double lon;
        public double lat;
    }
    public class UEllipseDef
    {
        public UMapPoint center;
        public UMapPoint radius;
    }
    public class UMunicipalDef
    {
        public String sz_municipalid;
        public String sz_municipalname;
    }
    public class UGisRecord
    {
        public long id; //kon_dmid from adr-db
    }

    public enum ADRTYPES
    {
        NOPHONE_PRIVATE             = 1 << 0,
        NOPHONE_COMPANY             = 1 << 1,
        FIXED_PRIVATE               = 1 << 2,
        FIXED_COMPANY               = 1 << 3,
        MOBILE_PRIVATE              = 1 << 4,
        MOBILE_COMPANY              = 1 << 5,
        MOVED_PRIVATE               = 1 << 6,
        MOVED_COMPANY               = 1 << 7,
        LBA_TEXT                    = 1 << 8,
        LBA_VOICE                   = 1 << 9,
        SMS_PRIVATE                 = 1 << 10,
        SMS_COMPANY                 = 1 << 11,
        SMS_PRIVATE_ALT_FIXED       = 1 << 12,
        SMS_COMPANY_ALT_FIXED       = 1 << 13,
        FIXED_PRIVATE_ALT_SMS       = 1 << 14,
        FIXED_COMPANY_ALT_SMS       = 1 << 15,
        MOBILE_PRIVATE_AND_FIXED    = 1 << 16,
        MOBILE_COMPANY_AND_FIXED    = 1 << 17,
        FIXED_PRIVATE_AND_MOBILE    = 1 << 18,
        FIXED_COMPANY_AND_MOBILE    = 1 << 19,
        SENDTO_USE_NOFAX_COMPANY    = 1 << 27,
        SENDTO_USE_NOFAX_DEPARTMENT = 1 << 28, //reserved for future use
        SENDTO_USE_NOFAX_GLOBAL     = 1 << 29, //should always be off
        ONLY_HEAD_OF_HOUSEHOLD      = 1 << 30,

    }
    public class ARGB
    {
        public String _a, _r, _g, _b;
        public ARGB(String a, String r, String g, String b)
        {
            _a = a;
            _r = r;
            _g = g;
            _b = b;
        }
    }

    public static class UCommon
    {
        public static double UEARTH_CIRCUMFERENCE = 30.92 * 3600.0;
        public static double UDEG2RAD(double deg)
        {
            return Math.PI * deg / 180.0;
        }
        public const int USENDING_LIVE         = 0;
        public const int USENDING_SIMULATION = 1;
        public const int USENDING_TEST = 2;
        public static String USENDINGTYPE(int n)
        {
            switch (n)
            {
                case UCommon.USENDING_LIVE:
                    return "live";
                case UCommon.USENDING_SIMULATION:
                    return "simulation";
                case UCommon.USENDING_TEST:
                    return "test";
            }
            return "Unknown Send Function";
        }
        public static String USENDINGTYPE_SENT(int n)
        {
            switch (n)
            {
                case UCommon.USENDING_LIVE:
                    return "Sent";
                case UCommon.USENDING_SIMULATION:
                    return "Simulated";
                case UCommon.USENDING_TEST:
                    return "Tested";
            }
            return USENDINGTYPE(n);
        }


        public static System.Globalization.NumberFormatInfo UGlobalizationInfo = new System.Globalization.NumberFormatInfo();
        public static String appname; //global name of application. Used in event logging
        public static bool Initialize(String app, String sysloghost, int syslogport)
        {
            try
            {
                UGlobalizationInfo.NumberDecimalSeparator = ".";
                appname = app;
                UPATHS.CheckParameters();
                USysLog.init(sysloghost, syslogport);
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public static void UnInitialize()
        {
            try
            {
                USysLog.uninit();
            }
            catch (Exception) { }
        }

        public struct UVOICE
        {
            public static String sz_send_number;
            public static float f_rms;
            public static int l_tts_timeout;
        }

        public struct USETTINGS
        {
            public static int l_folkereg_num_adrtables;
            public static String sz_url_weather_forecast;
        }

        public struct UPATHS
        {
            public static String sz_path_bcp;
            public static String sz_path_backbone;
            public static String sz_path_predefined_areas;
            public static String sz_path_temp;
            public static String sz_path_mapsendings;
            public static String sz_path_lba;
            public static String sz_path_parmtemp;
            public static String sz_path_parmzipped;
            public static String sz_path_ttsserver;
            public static String sz_path_audiofiles;
            public static String sz_path_voice;
            public static String sz_path_bbmessages;
            public static String sz_path_predefined_messages;
            public static String sz_path_global_wav_dir;
            public static String sz_path_infosent;
            public static String sz_url_nslookup; //MaxMind NS account

            public static void AddDirectorySecurity(string FileName, string Account, FileSystemRights Rights, AccessControlType ControlType)
            {
                try
                {
                    DirectoryInfo dInfo = new DirectoryInfo(FileName);
                    DirectorySecurity dSecurity = dInfo.GetAccessControl();
                    dSecurity.AddAccessRule(new FileSystemAccessRule(Account, Rights, ControlType));
                    dInfo.SetAccessControl(dSecurity);
                }
                catch (Exception e)
                {
                    throw e;
                }
            }

            public static bool CheckParameters()
            {
                /*try
                {
                    AddDirectorySecurity(sz_path_bcp, @"administrator", FileSystemRights.ReadData, AccessControlType.Allow);
                }
                catch (Exception e)
                {
                    ULog.error(e.Message);
                    throw e;
                }*/
                try
                {
                    //Directory.SetAccessControl(sz_path_bcp, System.Security.AccessControl.DirectorySecurity

                    //PathExists(sz_path_bcp);
                    //PathExists(sz_path_backbone);
                    //PathExists(sz_path_predefined_areas);
                    //PathExists(sz_path_temp);
                    //PathExists(sz_path_mapsendings);
                    //PathExists(sz_path_lba);
                    return true;
                }
                catch (UFileNotFoundException e)
                {
                    ULog.error(e.Message);
                    throw e;
                }
                catch (Exception e)
                {
                    ULog.error(e.Message);
                    throw e;
                }
            }

        }
        public static bool PathExists(String path)
        {
            if (Directory.Exists(path))
                return true;
            throw new UFileNotFoundException(String.Format("Path does not exist. {0}", path));
        }
        public struct UBBDATABASE
        {
            public static String sz_dsn;
            public static String sz_dsn_aoba;
            public static String sz_uid;
            public static String sz_pwd;
            public static String sz_adrdb_dsnbase;
            public static String sz_adrdb_uid;
            public static String sz_adrdb_pwd;
        }
        public static String UGetDateNow() { return String.Format("{0:yyyy}{0:MM}{0:dd}", DateTime.Now.ToLocalTime()); }
        public static String UGetTimeNow() { return String.Format("{0:HH}{0:mm}", DateTime.UtcNow.ToLocalTime()); }
        public static String UGetFullTimeNow() { return String.Format("{0:HH}{0:mm}{0:ss}", DateTime.UtcNow.ToLocalTime()); }

        public static UDATETIME UGetFullDateTimeNow() { return new UDATETIME(UGetDateNow(), UGetFullTimeNow()); }
        public static UDATETIME UGetDateTimeNow() { return new UDATETIME(UGetDateNow(), UGetTimeNow()); }
        public static String UGetDateNowLiteralUTC() { return String.Format("{0:dd}.{0:MM}.{0:yyyy}", DateTime.UtcNow); }
        public static String UGetTimeNowLiteralUTC() { return String.Format("{0:HH}:{0:mm}", DateTime.UtcNow); }


        /// <summary>
        /// Converts an ellipse to a polygon
        /// </summary>
        /// <param name="centerx"></param>
        /// <param name="centery"></param>
        /// <param name="cornerx"></param>
        /// <param name="cornery"></param>
        /// <param name="steps">Number of points in the polygon, min=4, max=50</param>
        /// <param name="angle">Use 0 for a standard ellipse</param>
        /// <param name="polygon">Reference to array of doubles predefined as polygon[steps,2]</param>
        public static bool ConvertEllipseToPolygon(double centerx, double centery, double cornerx, double cornery, int steps, int angle, ref double[,] polygon)
        {
            int curStep = 0;

            if (steps < 4 || steps > 50)
                return false;
            else if (polygon.GetUpperBound(0) != steps - 1 || polygon.GetUpperBound(1) != 1)
                return false;

            double a = Math.Abs(cornerx - centerx);
            double b = Math.Abs(cornery - centery);

            double beta = -angle * (Math.PI / 180);
            double sinbeta = Math.Sin(beta);
            double cosbeta = Math.Cos(beta);

            for (int i = 0; i < 360; i += 360 / steps)
            {
                double alpha = i * (Math.PI / 180);
                double sinalpha = Math.Sin(alpha);
                double cosalpha = Math.Cos(alpha);

                polygon[curStep, 0] = centerx + (a * cosalpha * cosbeta - b * sinalpha * sinbeta);
                polygon[curStep, 1] = centery + (a * cosalpha * sinbeta + b * sinalpha * cosbeta);

                curStep++;
            }

            return true;
        }    
    }

    public struct UDATETIME
    {
        public String sz_date, sz_time;
        public UDATETIME(String date, String time) /*yyyymmdd hhmmss*/
        {
            sz_date = date;
            sz_time = time;
        }
        public override String ToString()
        {
            if (sz_date.Equals("-1") || sz_time.Equals("-1"))
                return "-1";
            return sz_date + sz_time;
        }
        public long getDateTime()
        {
            try
            {
                if (sz_date.Length == 8 && (sz_time.Length == 6 || sz_time.Length == 4))
                {
                    if (sz_time.Length == 6)
                        return long.Parse(sz_date + sz_time);
                    else
                        return long.Parse(sz_date + sz_time + "00");
                }
                else
                    return 0;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }

    public class UPASLOGON
    {
        public bool f_granted;
        public Int64 l_userpk;
        public int l_comppk;
        public String sz_userid;
        public String sz_compid;
        public String sz_name;
        public String sz_surname;
        public List<UNSLOOKUP> nslookups = new List<UNSLOOKUP>();
        public List<UDEPARTMENT> departments = new List<UDEPARTMENT>();

    }

    public class UDEPARTMENT
    {
        public int l_deptpk;
        public String sz_deptid;
        public String sz_stdcc;
        public float lbo, rbo, ubo, bbo;
        public bool f_default;
        public int l_deptpri;
        public int l_maxalloc;
        public String sz_userprofilename;
        public String sz_userprofiledesc;
        public int l_status;
        public int l_newsending;
        public int l_parm;
        public int l_fleetcontrol;
        public int l_lba;
        public int l_houseeditor;
        public long l_addresstypes;
        public String sz_defaultnumber;
        public int f_map;
        public int l_pas; //0=no access, 1=access to norway db, 2=access to folkereg db
        public List<UMunicipalDef> municipals = new List<UMunicipalDef>();
        public void AddMunicipal(String sz_id, String sz_name)
        {
            UMunicipalDef d = new UMunicipalDef();
            d.sz_municipalid = sz_id;
            d.sz_municipalname = sz_name;
            municipals.Add(d);
        }
    }

    public class UNSLOOKUP
    {
        public String sz_domain;
        public String sz_ip;
        public long l_lastdatetime;
        public String sz_location;
        public bool f_success;
    }

    public struct ULOGONINFO
    {
        public Int64 l_userpk;
        public int l_comppk;
        public int l_deptpk;
        public String sz_deptid;
        public String sz_userid;
        public String sz_compid;
        public String sz_password;

        //Retrieved from database after logon
        public int l_deptpri;
        public int l_priserver;
        public int l_altservers;
        public String sz_stdcc;
        public String jobid;
    }


    public class BBPROJECT
    {
        public String sz_projectpk;
        public String sz_name;
        public String sz_created;
        public String sz_updated;
        public int n_deptpk;

        /*sending specifics*/
        public int n_sendingcount; //sendings attached to project
        public List<MDVSENDINGINFO> mdvsendinginfo = new List<MDVSENDINGINFO>(); //collection of mdvsendinginfo
    }

    /*
     * Profile registrered for sending/alert
     */
    public struct BBRESCHEDPROFILE
    {
        public String l_reschedpk;
        public long l_deptpk;
        public long l_retries;
        public long l_interval;
        public long l_canceltime;
        public long l_canceldate;
        public long l_pausetime;
        public long l_pauseinterval;
        public String sz_name;

        public void setReschedPk(string s) { l_reschedpk = s; }
        public void setDeptPk(int i) { l_deptpk = i; }
        public void setRetries(int i) { l_retries = i; }
        public void setInterval(int i) { l_interval = i; }
        public void setCanceltime(int i) { l_canceltime = i; }
        public void setCancelDate(int i) { l_canceldate = i; }
        public void setPausetime(int i) { l_pausetime = i; }
        public void setPauseInterval(int i) { l_pauseinterval = i; }
        public void setName(string s) { sz_name = s; }
    }

    /*
     * Values to be used in a sending
     */
    public class MDVSENDINGINFO
    {
        public long l_refno;
        public String sz_fields;
        public String sz_sepused;
        public long l_namepos;
        public long l_addresspos;
        public long l_lastantsep;
        //public long l_refno;
        public String l_createdate;
        public String l_createtime;
        public String l_scheddate;
        public String l_schedtime;
        public String sz_sendingname;
        public long l_sendingstatus;
        public long l_companypk;
        public long l_deptpk;
        public long l_nofax;
        public long l_removedup;
        public long l_group;
        public String sz_groups;
        public long l_type;
        public long f_dynacall; //1 = normal voice, 2 = simulation
        public long l_addresstypes; 
        public Int64 l_userpk;
        public long l_profilepk;
        public int l_maxchannels; //initial max (voice)channels for a sending. When modified this will be returned in the l_maxalloc field


        /*extra sending variables*/
        public long l_queuestatus;
        public long l_totitem;
        public long l_processed;
        public long l_altjmp;
        public long l_alloc;
        public long l_maxalloc;
        public String sz_oadc;
        public long l_qreftype; //60 is voice simulation from BBQREF
        public int l_linktype;
        public int l_resendrefno; //if it's a resend
        public String sz_messagetext; //for sms messages
    }
    public struct BBVALID
    {
        public long l_valid;
    }
    public struct BBSENDNUM
    {
        public String sz_number;
    }
    public struct BBACTIONPROFILESEND
    {
        public long l_actionprofilepk;
    }


    /*
     * Values to be used in a sending
     */
    public struct BBDYNARESCHED
    {
        public BBDYNARESCHED(BBRESCHEDPROFILE p)
        {
            l_retries = p.l_retries;
            l_interval = p.l_interval;
            //l_canceldate = p.l_canceldate; //This should be converted to NOW + p.l_canceldate days
            //compute canceldate
            DateTime now = DateTime.UtcNow.ToLocalTime();
            String sz_canceldate = "-1";
            if (p.l_canceldate > 0 || (p.l_canceldate==0 && p.l_canceltime>0))
            {
                now = now.AddDays(p.l_canceldate);
                sz_canceldate = String.Format("{0:yyyy}{0:MM}{0:dd}", now);
            }
            try
            {
                l_canceldate = Int64.Parse(sz_canceldate);
            }
            catch (Exception e)
            {
                ULog.write(e.Message);
                l_canceldate = 0;
            }
            l_canceltime = p.l_canceltime;
            l_pausetime = p.l_pausetime;
            l_pauseinterval = p.l_pauseinterval;
        }
        public long l_retries;
        public long l_interval;
        public long l_canceltime;
        public long l_canceldate;
        public long l_pausetime;
        public long l_pauseinterval;

        public void setRetries(long s) { l_retries = s; }
        public void setInterval(long s) { l_interval = s; }
        public void setCancelTime(long s) { l_canceltime = s; }
        public void setCancelDate(long s) { l_canceldate = s; }
        public void setPauseTime(long s) { l_pausetime = s; }
        public void setPauseInterval(long s) { l_pauseinterval = s; }


    }

    public class ULBAHISTCC
    {
        public int l_cc;
        public int l_delivered;
        public int l_expired;
        public int l_failed;
        public int l_unknown;
        public int l_submitted;
        public int l_queued;
        public int l_subscribers;
    }
    public class ULBASEND_TS
    {
        public int l_status;
        public long l_ts;
    }
    public class ULBAHISTCELL
    {

    }

    public class ULBASENDING
    {
        public long l_refno;
        public int l_cbtype;
        public int l_status;
        public int l_response;
        public int l_items;
        public int l_proc;
        public int l_retries;
        public int l_requesttype;
        public int f_simulation;
        public String sz_jobid;
        public String sz_areaid;
        public List<ULBAHISTCC> histcc = new List<ULBAHISTCC>(); //list of country codes hist
        public List<ULBAHISTCELL> histcell = new List<ULBAHISTCELL>(); //list of cell hist
        public List<ULBASEND_TS> send_ts = new List<ULBASEND_TS>(); //list of status timestamps
        
    }
    public class UTTS_DB_PARAMS
    {
        public String sz_speaker;
        public String sz_modename;
        public String sz_manufacturer;
    }



}
