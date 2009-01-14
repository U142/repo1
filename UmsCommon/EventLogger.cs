using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;



namespace com.ums.UmsCommon
{
    public static class USysLog
    {
        public static bool _inited = false;
        public enum UFACILITY
        {
            kern = 0,
            user = 1,
            mail = 2,
            daemon = 3,
            auth = 4,
            syslog = 5,
            lpr = 6,
            news = 7,
            uucp = 8,
            cron = 9,
            authpriv = 10,
            ftp = 11,
            local0 = 16,
            local1 = 17,
            local2 = 18,
            local3 = 19,
            local4 = 20,
            local5 = 21,
            local6 = 22,
            local7 = 23,
        };
        public enum ULEVEL
        {
            emerg = 0,
            alert = 1,
            crit = 2,
            err = 3,
            warning = 4,
            notice = 5,
            info = 6,
            debug = 7,
        };

        static Socket sock = null;
        public static void init(string host, int port)
        {
            String ip_resolved = "";
            try
            {
                sock = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
                //IPHostEntry ipHostInfo = Dns.Resolve(host);
                IPHostEntry ipHostInfo = Dns.GetHostEntry(host);
                IPAddress ipAddress = ipHostInfo.AddressList[0];
                ip_resolved = ipAddress.ToString();
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);
                //IPAddress ipAddress = new IPAddress("195.119.0.180");
                sock.Connect(remoteEP);
                //sock.Bind(remoteEP);
                _inited = true;
            }
            catch (Exception) 
            {
                _inited = false;
                ULog.error(String.Format("Unable to connect to syslog {0}:{1} ({2})", host, port, ip_resolved));
            }

        }
        public static void uninit()
        {
            sock.Close();
            _inited = false;
        }

        public static void send(string s, UFACILITY facility, ULEVEL level)
        {
            if (!_inited)
                return;
            string data = string.Format("<{0}>{1}", (level + ((int)facility<<3)), s);
            UTF8Encoding enc = new UTF8Encoding();
            try
            {
                sock.Send(enc.GetBytes(data));
            }
            catch (Exception) { }
        }

    }

    public static class ULog
    {
        public static void write(long l_refno, String s)
        {
            String o = String.Format("Refno {0}\n{1}", l_refno, s);
            write(o);
        }
        public static void error(long l_refno, String s)
        {
            String o = String.Format("Refno {0}\n{1}", l_refno, s);
            error(o);
        }
        public static void warning(long l_refno, String s)
        {
            String o = String.Format("Refno {0}\n{1}", l_refno, s);
            warning(o);
        }
        public static void warning(long l_refno, String s, String e)
        {
            String o = String.Format("Refno {0}\n{1}\n{2}", l_refno, s, e);
            warning(o);
        }
        public static void error(long l_refno, String s, String e)
        {
            String o = String.Format("Refno {0}\n{1}\n{2}", l_refno, s, e);
            error(o);
        }

        public static void write(String s)
        {
            _insert(s, EventLogEntryType.Information);
        }
        public static void error(String s)
        {
            _insert(s, EventLogEntryType.Error);
        }
        public static void warning(String s)
        {
            _insert(s, EventLogEntryType.Warning);
        }
        private static void _insert(String s, EventLogEntryType type)
        {
            String sz_stack = "\r\n";
            try
            {
                StackTrace st = new StackTrace(0, true);
                foreach (System.Diagnostics.StackFrame frame in st.GetFrames())
                {
                    sz_stack += frame.GetFileName() + " (line: " + frame.GetFileLineNumber() + ")\n" + frame.GetMethod() + "\r\n\r\n";
                }
                

            }
            catch (Exception)
            {
                sz_stack = "No stack trace available";
            }
            try
            {
                EventLog log = new EventLog();
                log.Source = UCommon.appname;
                log.WriteEntry(s + "\r\n" + sz_stack, type);
            }
            catch (Exception)
            {
            }
            if (!USysLog._inited)
                return;
            try
            {
                USysLog.ULEVEL syslogtype;
                switch (type)
                {
                    case EventLogEntryType.Error:
                        syslogtype = USysLog.ULEVEL.crit;
                        break;
                    case EventLogEntryType.Information:
                        syslogtype = USysLog.ULEVEL.info;
                        break;
                    case EventLogEntryType.Warning:
                        syslogtype = USysLog.ULEVEL.warning;
                        break;
                    default:
                        syslogtype = USysLog.ULEVEL.warning;
                        break;

                }
                USysLog.send(UCommon.appname + ": " + s, USysLog.UFACILITY.syslog, syslogtype);
            }
            catch (Exception)
            {

            }
        }
    }
}
