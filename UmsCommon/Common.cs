﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;
using System.Security.AccessControl;
using System.Collections;
using System.Security.Cryptography;
using System.Xml;
using System.Xml.Serialization;

namespace com.ums.UmsCommon
{

    public class PASVERSION
    {
        public String full;
        public int major;
        public int minor;
        public int build;
        public int revision;
    }

    public class ONETIMEKEY
    {
        /*public static ONETIMEKEY newInstance() 
        {
            ONETIMEKEY key = new ONETIMEKEY();
            //register the key on server
            AppKeyStore.Add(key);
            return key;
        }*/
        public ONETIMEKEY()
        {
            expires = DateTime.Now + new TimeSpan(0, 0, 180);
            guid = Guid.NewGuid().ToString();
        }
        public DateTime expires;
        public String guid;

    }

    public enum MDVSENDINGINFO_GROUP
    {
        ADDRESSLIST = 0,
        GROUPSENDING = 1,
        MAP_POLYGON = 2,
        MAP_GEMINI_GNOBNO = 3,
        MAP_GEMINI_STREETID = 4,
        MAP_ELLIPSE = 8,
        MAP_MUNICIPAL = 9,
        MAP_LBA_VOICE = 11,
        MAP_CB_NATIONAL = 16,
        MAP_POLYGONAL_ELLIPSE = 32,
    }

    public enum MDVSENDINGINFO_TYPE
    {
        CB_SENDING = 7,
        CB_TESTSENDING = 70,
    }

    public enum BBUSER_BLOCK_REASONS
    {
        NONE = 0,
        REACHED_RETRY_LIMIT = 1,
        BLOCKED_BY_ADMIN = 2,
    }

    public struct UPOWERUP_RESPONSE
    {
        public int l_max_logontries;
    }


    /**
     * Value used in Backbone log procedures/tables (e.g sp_log_BBMESSAGES => log_BBMESSAGES)
     */
    public enum UDbOperation
    {
        INSERT = 0,
        UPDATE = 1,
        DELETE = 2,
    }
    public enum ProgressJobType
    {
        GEMINI_IMPORT_STREETID = 1,
        GEMINI_IMPORT_GNRBNR = 2,
        STATUS_LIST = 3,
        STATUS_ITEMS = 4,
        PARM_UPDATE = 5,
        HOUSE_DOWNLOAD = 6,
        TAS_UPDATE = 7,
        PARM_SEND = 8,


    }

    public class PercentResult
    {
        public int n_percent;
        public int n_totalrecords;
        public int n_currentrecord;
    }

    public class SessionStore
    {

    }

    public enum LBA_SENDINGTYPES
    {
        TAS = 5,
        LBAS = 4,
        CELLBROADCAST = 7,
    }

    /*public enum RESTRICTION_TYPE
    {
        PAUSERRESTRICTION = 8,
        PADEPARTMENTRESTRICTION = 16,
    }*/

    public class OneTimeKeyStore
    {
        public static SetOneTimeKeyDelegate newDelegate() { return new SetOneTimeKeyDelegate(SetKey); }
        public delegate void SetOneTimeKeyDelegate();

        public static void SetKey()
        {

        }
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

    public class AppKeyStore
    {
        private static int maxCapacity = 100;//UCommon.USETTINGS.l_onetimekey_capacity;
        //private static List<ONETIMEKEY> _table = new List<ONETIMEKEY>(1);
        private static LinkedList<ONETIMEKEY> _table = new LinkedList<ONETIMEKEY>();

        public static string getNextKey()
        {
            ONETIMEKEY key = new ONETIMEKEY();
            Add(key);
            return key.guid;
        }

        public static bool isKeyValid(string guid)
        {
            foreach(ONETIMEKEY key in _table)
            {
                if (key.guid.Equals(guid))
                {
                    _table.Remove(key);
                    return (DateTime.Now < (key.expires));
                }
            }
            return false;
        }

        /**
         * Called if someone asks for a one time key
         */
        private static void Add(ONETIMEKEY key)
        {
            lock (_table)
            {
                _table.AddLast(key);
                while (maxCapacity>0 && _table.Count > maxCapacity)
                {
                    _table.RemoveFirst();
                }
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

    public enum PASHAPETYPES : int
    {
        PAALERT = 1,
        PAEVENT = 2,
        PAOBJECT = 4,
        PAUSERRESTRICTION = 8,
        PADEPARTMENTRESTRICTION = 16,
        PASENDING = 32,
    }

    public static class Helpers
    {
        public static string CreateMD5Hash(string input)
        {
            // Use input string to calculate MD5 hash
            MD5 md5 = System.Security.Cryptography.MD5.Create();
            byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(input);
            byte[] hashBytes = md5.ComputeHash(inputBytes);

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashBytes.Length; i++)
            {
                sb.Append(hashBytes[i].ToString("X2"));
                // To force the hex string to lower-case letters instead of
                // upper-case, use he following line instead:
                // sb.Append(hashBytes[i].ToString("x2")); 
            }
            return sb.ToString();
        }

        public static string CreateSHA512Hash(string input)
        {
            SHA512 sha = SHA512.Create();
            byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(input);
            byte[] hashBytes = sha.ComputeHash(inputBytes);

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashBytes.Length; i++)
            {
                sb.Append(hashBytes[i].ToString("X2"));
                // To force the hex string to lower-case letters instead of
                // upper-case, use he following line instead:
                // sb.Append(hashBytes[i].ToString("x2")); 
            }
            return sb.ToString();
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
        SENDTO_TAS_SMS              = 1 << 20,
        SENDTO_USE_NOFAX_COMPANY    = 1 << 27,
        SENDTO_USE_NOFAX_DEPARTMENT = 1 << 28, //reserved for future use
        SENDTO_USE_NOFAX_GLOBAL     = 1 << 29, //should always be off
        ONLY_HEAD_OF_HOUSEHOLD      = 1 << 30,

    }
    public enum SENDCHANNEL
    {
        VOICE = 1,
        SMS = 2,
        EMAIL = 3,
        LBA = 4,
        TAS = 5,
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
            catch (Exception)
            {
                throw;
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
            public static bool b_enable_adrdb;
            public static bool b_enable_nslookup;
            public static int l_folkereg_num_adrtables;
            public static String sz_url_weather_forecast;
            public static int l_gisimport_chunksize;
            public static int l_gisimport_db_timeout;
            public static int l_max_logontries;
            public static Boolean b_write_messagelib_to_file;
            public static int l_onetimekey_capacity;
            public static int l_onetimekey_valid_secs;
        }

        public struct UPATHS
        {
            public static String sz_path_bcp;
            public static String sz_path_backbone;
            public static String sz_path_predefined_areas;
            public static String sz_path_temp;
            public static String sz_path_mapsendings;
            public static String sz_path_lba;
            public static String sz_path_cb;
            public static String sz_path_tas;
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
                catch (Exception)
                {
                    throw;
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
                    throw;
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
        //public static UDATETIME UGetDateTimeNow() { return new UDATETIME(UGetDateNow(), UGetTimeNow()); }
        public static String UGetDateNowLiteralUTC() { return String.Format("{0:dd}.{0:MM}.{0:yyyy}", DateTime.UtcNow); }
        public static String UGetTimeNowLiteralUTC() { return String.Format("{0:HH}:{0:mm}", DateTime.UtcNow); }

        public static DateTime UConvertLongToDateTime(long n)
        {
            try
            {
                String sz = n.ToString();
                if (sz.Length == 14)
                {
                    DateTime dt = new DateTime(
                        int.Parse(sz.Substring(0, 4)),
                        int.Parse(sz.Substring(4, 2)),
                        int.Parse(sz.Substring(6, 2)),
                        int.Parse(sz.Substring(8, 2)),
                        int.Parse(sz.Substring(10, 2)),
                        int.Parse(sz.Substring(12, 2)));
                    return dt;
                }
                return new DateTime();

            }
            catch(Exception)
            {
                return new DateTime();
            }
        }

        public static long UConvertDateTimeToLong(ref DateTime d)
        {
            return long.Parse(d.ToString("yyyyMMddHHmmss"));
        }

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

    public class UDATETIME
    {
        public String sz_date, sz_time;
        protected DateTime dt;
        public DateTime getDT() { return dt; }
        public long getTimeDiffSec(UDATETIME old)
        {
            long seconds = 0;
            TimeSpan ts = dt - old.getDT();
            return (long)ts.TotalSeconds;
        }
        protected void createDt()
        {
            try
            {
                if (sz_date.Length == 8 && sz_time.Length == 6)
                {
                    int year = int.Parse(sz_date.Substring(0, 4));
                    int month = int.Parse(sz_date.Substring(4, 2));
                    int day = int.Parse(sz_date.Substring(6, 2));
                    int hour = int.Parse(sz_time.Substring(0, 2));
                    int minute = int.Parse(sz_time.Substring(2, 2));
                    int second = int.Parse(sz_time.Substring(4, 2));
                    dt = new DateTime(year, month, day, hour, minute, second);
                }
                else if (sz_date.Length == 8 && sz_time.Length == 4)
                {
                    int year = int.Parse(sz_date.Substring(0, 4));
                    int month = int.Parse(sz_date.Substring(4, 2));
                    int day = int.Parse(sz_date.Substring(6, 2));
                    int hour = int.Parse(sz_time.Substring(0, 2));
                    int minute = int.Parse(sz_time.Substring(2, 2));
                    int second = 0;
                    dt = new DateTime(year, month, day, hour, minute, second);
                }
                else if (sz_date.Length == 8 && sz_time.Length > 0) //assume we use backbone time-style (time = 0..2359). HHMM
                {
                    int year = int.Parse(sz_date.Substring(0, 4));
                    int month = int.Parse(sz_date.Substring(4, 2));
                    int day = int.Parse(sz_date.Substring(6, 2));
                    int hour = 0;
                    int minute = 0;
                    if (sz_time.Length == 3) //we have hours. HHMM are found in previous else if, search for HMM, MM and M
                    {
                        hour = int.Parse(sz_time.Substring(0, 1));
                        minute = int.Parse(sz_time.Substring(1));
                    }
                    else
                    {
                        minute = int.Parse(sz_time);
                    }
                    int second = 0;
                    dt = new DateTime(year, month, day, hour, minute, second);
                }
            }
            catch (Exception)
            {
                throw;
            }
        }
        public UDATETIME(Int64 datetime)
        {
            try
            {
                genDate(datetime);
            }
            catch (Exception)
            {
                throw;
            }
        }
        protected void genDate(Int64 datetime)
        {
            try
            {
                String str = datetime.ToString();
                if (datetime == 0)
                {
                    str = DateTime.Now.ToString("yyyyMMddHHmmss");
                    datetime = Int64.Parse(str);
                }
                else if (str.Length < 8)
                    throw new UMalformedDateTimeException();
                sz_date = str.Substring(0, 8);
                sz_time = str.Substring(8);
                if (sz_time.Length == 0)
                    sz_time = "0";
                createDt();
            }
            catch (Exception)
            {
                throw;
            }
        }
        public UDATETIME(String date, String time)// : this(Int64.Parse(date+time))/*yyyymmdd hhmmss*/
        {
            try
            {
                time = time.PadLeft(6, '0');
                genDate(Int64.Parse(date + time));
                //sz_date = date;
                //sz_time = time;
                createDt();
            }
            catch (Exception)
            {
                throw;
            }
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
                throw;
            }
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


    public class ULOGONINFO
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
        public String sessionid;
        public String onetimekey;
    }

    public class USYSTEMMESSAGES
    {
        public long l_timestamp;
        public UBBNEWSLIST news;

    }

    public enum UBBNEWSLIST_FILTER
    {
        ACTIVE = 1,
        IN_BETWEEN_START_END = 2,

    }
    public class UBBNEWSLIST
    {
        public long l_timestamp_db;
        public List<UBBNEWS> newslist = new List<UBBNEWS>();
    }
    public class UBBNEWS
    {
        public long l_newspk;
        public long l_created;
        public int l_validms;
        public long l_type;
        public long l_incident_start;
        public long l_incident_end;
        public int f_active;
        public int l_deptpk;
        public int l_severity;
        public int l_operator;
        public int l_errorcode;
        public long l_userpk;
        public long l_timestamp_db;
        public String sz_userid;
        public UBBNEWSTEXT newstext;
        public String sz_operatorname;
    }
    public class UBBNEWSTEXT
    {
        public long l_langpk;
        public String sz_news;
    }

    public class UBBUSER
    {
        public long l_userpk;
        public String sz_userid;
        public String sz_password;
        public String sz_name;
        public String sz_surname;
        public int l_comppk;
        public int l_deptpk;
        public long l_profilepk;
        public String sz_bdate;
        public int f_disabled;
        public int l_logontries;
        public int f_nopasswd_change;
        public int l_ivrlogon;
        public int l_language;
        public String sz_adrlistserie;
        public int l_adrlist_sort;
        public int l_adrlist_search;
        public String sz_statusserie;
        public int l_status_sort;
        public int l_status_search;
        public int f_keepalive;
        public int l_recprpage;
        public int l_audiosetup;
        public int l_status_owned_groups;
        public String sz_email;
        public String sz_paspassword;
        public int l_allow_nondept_resend;
        public String sz_timezone;
        public int f_blocklist_default;
        public int f_session_expires_sec;
        public String sz_hash_paspwd;
        public int[] l_deptpklist;
        public long l_disabled_timestamp;
        public String sz_organization;
        public BBUSER_BLOCK_REASONS l_disabled_reasoncode;
    }

    public class BBPROJECT
    {
        [XmlAttribute("l_projectpk")] public String sz_projectpk;
        [XmlAttribute("sz_projectname")] public String sz_name;
        [XmlIgnore]
        public String sz_created;
        [XmlIgnore]
        public String sz_updated;

        [XmlAttribute("l_deptpk")]
        public int n_deptpk;

        [XmlAttribute("l_userpk")]
        public long n_userpk;

        [XmlAttribute("l_finished")]
        public int l_finished;

        /*sending specifics*/
        [XmlIgnore]
        public int n_sendingcount; //sendings attached to project
        [XmlIgnore]
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
        public String sz_fields = "";
        public String sz_sepused = "";
        public long l_namepos;
        public long l_addresspos;
        public long l_lastantsep;
        //public long l_refno;
        public String l_createdate = "";
        public String l_createtime = "";
        public String l_scheddate = "";
        public String l_schedtime = "";
        public String sz_sendingname = "";
        public long l_sendingstatus;
        public long l_companypk;
        public long l_deptpk;
        public long l_nofax;
        public long l_removedup;
        public long l_group;
        public String sz_groups = "";
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
        public String sz_actionprofilename; //actionprofile used in sending (voice)
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


    public enum ULBAFILTER_STAT_TIMEUNIT
    {
        PER_HOUR,
        PER_DAY,
        PER_WEEK,
        PER_MONTH,
        PER_YEAR,
    }
    public enum ULBAFILTER_STAT_FUNCTION
    {
        STAT_AVERAGE,
        STAT_MAX,
        STAT_MIN,
    }
    public class ULBASTATISTICS_FILTER
    {
        public List<ULBACOUNTRY> countries;
        public List<int> years_to_compare; //alternative to from/to date
        //public int from_mmdd;
        //public int to_mmdd;
        public long from_date;
        public long to_date;
        public ULBAFILTER_STAT_TIMEUNIT group_timeunit;
        public ULBAFILTER_STAT_FUNCTION stat_function;
        public int rowcount;
    }

    public class ULBACOUNTRYSTATISTICS : ULBACOUNTRY
    {
        public List<UTOURISTCOUNT> statistics;
        public override int CompareTo(object obj)
        {
            if (obj == null)
                return 0;
            ULBACOUNTRYSTATISTICS t = (ULBACOUNTRYSTATISTICS)obj;
            return (int)(statistics[0].l_lastupdate - t.statistics[0].l_lastupdate);
            //return (int)(operators[0].l_lastupdate - t.operators[0].l_lastupdate);
        } 

    }

    public class ULBACOUNTRY:IComparable
    {
        public int l_cc;
        public String sz_iso;
        public String sz_name;
        public UMapPoint weightpoint;
        public UMapPoint weightpoint_screen;
        public UMapBounds bounds;
        public int l_continentpk;
        public int l_iso_numeric;
        public int n_touristcount;
        public long n_oldestupdate; //oldest of all operators
        public long n_newestupdate; //newest of all operators
        /*public int l_operator;
        public String sz_operator;*/
        public List<UTOURISTCOUNT> operators;
        public virtual int CompareTo(object obj)
        {
            if (obj == null)
                return 0;
            ULBACOUNTRY t = (ULBACOUNTRY)obj;
            return (int)(n_newestupdate - t.n_newestupdate);
            //return (int)(operators[0].l_lastupdate - t.operators[0].l_lastupdate);
        } 

    }

    public class UTOURISTCOUNT:IComparable
    {
        public long l_lastupdate;
        public int l_operator;
        public String sz_operator;
        public int l_touristcount;
        // sorting in ascending order  
        public int CompareTo(object obj)
        {
            if (obj == null)
                return 0;
            UTOURISTCOUNT t = (UTOURISTCOUNT)obj;
            return (l_lastupdate > t.l_lastupdate ? 1 : -1);
        } 
    }

    public class ULBACONTINENT
    {
        public int l_continentpk;
        public String sz_short;
        public String sz_name;
        public UMapPoint weightpoint;
        public UMapPoint weightpoint_screen;
        public UMapBounds bounds;
        public List<ULBACOUNTRY> countries;
        public int n_touristcount;
        public long n_lastupdate;
    }


    /*
     * Values to be used in a sending
     */
    public struct BBDYNARESCHED
    {
        public BBDYNARESCHED(BBRESCHEDPROFILE p, long n_scheddate)
        {
            l_retries = p.l_retries;
            l_interval = p.l_interval;
            //l_canceldate = p.l_canceldate; //This should be converted to NOW + p.l_canceldate days
            //compute canceldate

            DateTime now = DateTime.UtcNow.ToLocalTime();
            if (n_scheddate.ToString().Length==8) //fix - if scheddate is set, add days to this to determine canceldate
            {
                int year = Int32.Parse(n_scheddate.ToString().Substring(0, 4));
                int month = Int32.Parse(n_scheddate.ToString().Substring(4,2));
                int day = Int32.Parse(n_scheddate.ToString().Substring(6,2));
                now = new DateTime(year, month, day);
            }

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

    public class USMSINSTATS
    {
        public int l_refno;
        public int l_answercode;
        public String sz_description;
        public int l_count;
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
        public int l_operator;
    }

    public class ULBAHISTCELL
    {
        public int l_operator;
        public String sz_operator;
        public int l_2gtotal;
        public int l_2gok;
        public int l_3gtotal;
        public int l_3gok;
        public int l_4gtotal;
        public int l_4gok;
        public long l_timestamp;
        public float l_successpercentage;
    }
    public class ULBASEND_TS
    {
        public int l_status;
        public long l_ts;
        public int l_operator;
    }

    public enum ULBAOPERATORSTATE : long
    {
        ERROR = 0,
        KILLING = 1,
        INITIALIZING = 2,
        ACTIVE = 3,
        FINISHED = 4,
    }

    public enum ULBASTATUSCODES : long
    {
        LBASTATUS_GENERAL_ERROR = -2,

        LBASTATUS_INITED = 100,
        LBASTATUS_COULD_NOT_PUBLISH_LBA_FILE = 41100,
        LBASTATUS_SENT_TO_LBA = 199,
        LBASTATUS_PARSING_LBAS = 200,
        LBASTATUS_PARSING_LBAS_FAILED_TO_SEND = 290,

        LBASTATUS_PREPARING_CELLVISION = 300,
        LBASTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 305,
        LBASTATUS_PREPARED_CELLVISION = 310,
        LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 311,
        LBASTATUS_CONFIRMED_BY_USER = 320,
        LBASTATUS_CANCELLED_BY_USER = 330,
        LBASTATUS_SENDING = 340,

        TASSTATUS_PREPARING_CELLVISION = 400,
        TASSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 405,
        TASSTATUS_PREPARED_CELLVISION = 410,
        TASSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 411,
        TASSTATUS_CONFIRMED_BY_USER = 420,
        TASSTATUS_CANCELLED_BY_USER = 430,
        TASSTATUS_SENDING = 440,

        CBSTATUS_PREPARING_CELLVISION = 500,
        CBSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 505,
        CBSTATUS_PREPARED_CELLVISION = 510,
        CBSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 511,
        CBSTATUS_CONFIRMED_BY_USER = 520,
        CBSTATUS_CANCELLED_BY_USER = 530,
        CBSTATUS_SENDING = 540,
        CBSTATUS_PAUSED = 590,

        LBASTATUS_CANCEL_IN_PROGRESS = 800,
        LBASTATUS_FINISHED = 1000,
        LBASTATUS_CANCELLED = 2000,
        LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM = 2001,
        LBASTATUS_CANCELLED_AFTER_LOOKUP = 2002,

        LBASTATUS_EXCEPTION_EXECUTE_AREAALERT = 42001,
        LBASTATUS_EXCEPTION_PREPARE_AREAALERT = 42002,
        LBASTATUS_EXCEPTION_EXECUTE_CUSTOMALERT = 42003,
        LBASTATUS_EXCEPTION_PREPARE_CUSTOMALERT = 42004,
        LBASTATUS_EXCEPTION_EXECUTE_PREPARED_ALERT = 42005,
        LBASTATUS_EXCEPTION_CANCEL_PREPARED_ALERT = 42006,
        TASSTATUS_EXCEPTION_EXECUTE_INT_ALERT = 42007,
        TASSTATUS_EXCEPTION_PREPARE_INT_ALERT = 42008,
        LBASTATUS_FAILED_EXECUTE_AREAALERT = 42011,
        LBASTATUS_FAILED_PREPARE_AREAALERT = 42012,
        LBASTATUS_FAILED_EXECUTE_CUSTOMALERT = 42013,
        LBASTATUS_FAILED_PREPARE_CUSTOMALERT = 42014,
        LBASTATUS_FAILED_EXECUTE_PREPARED_ALERT = 42015,
        LBASTATUS_FAILED_CANCEL_PREPARED_ALERT = 42016,
        TASSTATUS_FAILED_EXECUTE_INT_ALERT = 42017,
        TASSTATUS_FAILED_PREPARE_INT_ALERT = 42018,
        CELLVISION_JOB_STATUS_ERROR = 42201,

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
        public int l_operator;
        public String sz_operator;
        public String sz_jobid;
        public String sz_areaid;
        public List<ULBAHISTCC> histcc = new List<ULBAHISTCC>(); //list of country codes hist
        public List<ULBAHISTCELL> histcell = new List<ULBAHISTCELL>(); //list of cell hist
        public List<ULBASEND_TS> send_ts = new List<ULBASEND_TS>(); //list of status timestamps
        public List<LBALanguage> languages = new List<LBALanguage>(); //list of languages and countrycodes
    }
    public class LBALanguage
    {
        public long l_textpk;
        public void setTextPk(long l_textpk)
        {
            this.l_textpk = l_textpk;
        }
        public String sz_name;
        public String sz_cb_oadc;
        public String sz_otoa;
        public String sz_text;
        public List<LBACCode> m_ccodes = new List<LBACCode>();
        public LBALanguage()
        {
        }
        public LBALanguage(String sz_name, String sz_cb_oadc, String sz_otoa, String sz_text)
        {
            this.sz_name = sz_name;
            this.sz_text = sz_text;
            this.sz_cb_oadc = sz_cb_oadc;
            this.sz_otoa = sz_otoa;
        }
        public void AddCCode(String ccode) { m_ccodes.Add(new LBACCode(ccode)); }

        public String getName() { return sz_name; }
        public String getCBOadc() { return sz_cb_oadc; }
        public String getOtoa() { return sz_otoa; }
        public String getText() { return sz_text; }
        public int getCCodeCount() { return m_ccodes.Count; }
        public LBACCode getCCode(int i) { return (LBACCode)m_ccodes[i]; }

    }
    public class LBACCode
    {
        public String ccode;
        public LBACCode(String s)
        {
            ccode = s;
        }
        public LBACCode()
        {
        }
        public String getCCode() { return ccode; }
    }

    public class UTTS_DB_PARAMS
    {
        public String sz_speaker;
        public String sz_modename;
        public String sz_manufacturer;
    }

    public enum UBBMODULEDEF
    {
        CALL = 11,
        INTRO = 12,
        LOGON = 13,
        SUBSTANCE = 14,
        DIALOGUE = 15,
        REPORT = 16,
        CONFIRM = 17,
        ENDING = 18,
        HANGUP = 19,
        ERROR = 21,
        EXECUTE = 22,
    }

    public class UBBMESSAGE
    {
        public long n_messagepk;
        public int n_deptpk;
        public UBBMODULEDEF n_type;
        public String sz_name;
        public String sz_description;
        public long n_langpk;
        public String sz_number;
        public int f_template;
        public String sz_filename;
        public int n_ivrcode;
        public long n_parentpk;
        public int n_depth;
        public long n_timestamp;
        public long n_categorypk;
        public String sz_message;
        public byte[] audiostream;
        public bool b_valid;
        public List<UCCMessage> ccmessage;
    }
    
    public class UCCMessage
    {
        public String sz_message;
        public int l_cc;
    }

    public class UBBMESSAGELIST
    {
        public List<UBBMESSAGE> list;
        public List<UBBMESSAGE> deleted;
        public long n_servertimestamp;
    }
    public class UBBMESSAGELISTFILTER
    {
        public long n_timefilter;
    }

    public class ULBAMESSAGE // Used for NLALERT
    {
        public long n_messagepk;
        public int n_deptpk;
        public String sz_name;
        public String sz_description;
        public long n_langpk;
        public long n_parentpk;
        public int n_depth;
        public long n_timestamp;
        public String sz_message;
    }

    public class ULBAMESSAGELIST // Used for NLALERT
    {
        public List<ULBAMESSAGE> list;
        public List<ULBAMESSAGE> deleted;
        public long n_servertimestamp;
    }

    public class ULBADURATION // Used for NLALERT
    {
        public long n_durationpk;
        public int n_duration;
        public int n_interval;
        public int n_repetition;
        public int n_deptpk;
    }

    public class ULBAPARAMETER // Used for NLALERT
    {
        public long l_parameterspk;
	    public int l_incorrect;
	    public int l_autologoff;
	    public String sz_adminemail;
	    public int l_channelno;
	    public int l_test_channelno;
	    public int l_heartbeat;
	    public int l_duration;
	    public int l_interval;
	    public int l_repetition;
	    public int l_deptpk;
        public int l_comppk;
    }

    public class UPROJECT_FINISHED_RESPONSE : BBPROJECT
    {
        
    }

    public enum UDATAFILTER
    {
        NONE = 0,
        BY_COMPANY = 1,
        BY_DEPARTMENT = 2,
        BY_USER = 4,
        BY_LIVE = 8,
        BY_SIMULATION = 16,
    }

}
