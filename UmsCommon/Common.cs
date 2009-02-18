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
    public enum ADRTYPES
    {
        NOPHONE_PRIVATE = 1,
        NOPHONE_COMPANY = 2,
        FIXED_PRIVATE = 4,
        FIXED_COMPANY = 8,
        MOBILE_PRIVATE = 16,
        MOBILE_COMPANY = 32,
        MOVED_PRIVATE = 64,
        MOVED_COMPANY = 128,
        LBA_TEXT = 256,
        LBA_VOICE = 512,
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
            public static String sz_path_global_wav_dir;

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
            return sz_date + sz_time;
        }
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
            now = now.AddDays(p.l_canceldate);
            String sz_canceldate = String.Format("{0:yyyy}{0:MM}{0:dd}", now);
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
