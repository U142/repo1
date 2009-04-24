using System;
using System.Collections;
using com.ums.UmsCommon;
using com.ums.UmsFile;
using System.Collections.Generic;
using System.Text;

namespace com.ums.UmsParm
{
    public abstract class UShape
    {
        public static int SENDINGTYPE_POLYGON = 3;
        public static int SENDINGTYPE_ELLIPSE = 8;
        public static int SENDINGTYPE_GIS = 4;


        public UPolygon poly() { return (UPolygon)this; }
        public UEllipse ellipse() { return (UEllipse)this; }
        public UGIS gis() { return (UGIS)this; }
        public UResend resend() { return (UResend)this; }
        public ULocationBasedAlert lba() { return (ULocationBasedAlert)this; }
        public abstract bool WriteAddressFile(ref AdrfileWriter w);
        public abstract bool WriteAddressFileGUI(ref AdrfileGUIWriter w);
        public virtual bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, String sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            w.close();
            //throw new NotImplementedException("WriteAddressFileLBA");
            return true;
        }
        public bool WriteAddressResendFile(long copy_refno)
        {
            return true;
        }
        public bool WriteAddressResendFileGUI(long copy_refno)
        {
            return true;
        }
    }

    public class ULocationBasedAlert : UShape
    {
        public ULocationBasedAlert()
            : base()
        {
            m_b_isvalid = false;
        }
        public class LBALanguage
        {
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
        protected bool m_b_isvalid;
        //public String sz_area;
        //public String sz_id;
        public String l_alertpk;
        public List<LBALanguage> m_languages = new List<LBALanguage>();
        public void setValid() { m_b_isvalid = true; }
        public bool getValid() { return m_b_isvalid; }
        public long n_expiry_minutes;
        public int n_requesttype; //send right away or wait for confirmation
        public int getRequestType() { return n_requesttype; }

        public LBALanguage addLanguage(String sz_name, String sz_cb_oadc, String sz_otoa, String sz_text)
        {
            LBALanguage lang = new LBALanguage(sz_name, sz_cb_oadc, sz_otoa, sz_text);
            m_languages.Add(lang);
            return lang;
        }
        public int getLanguageCount() { return m_languages.Count; }
        public LBALanguage getLanguage(int i) { return (LBALanguage)m_languages[i]; }

        /*public override bool WriteAddressFileBCP(ref AdrfileWriter w)
        {
            throw new NotImplementedException();
        }*/
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, String sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            //create xml and save it to AdrfileLBAWriter
            USimpleXmlWriter xmlwriter = new USimpleXmlWriter(new UTF8Encoding(false));
            xmlwriter.insertStartDocument();
            xmlwriter.insertStartElement("LBA");
            xmlwriter.insertAttribute("operation", "SendArea");
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk);
            xmlwriter.insertAttribute("l_refno", n_parentrefno.ToString());
            if (alert.hasValidAreaID())
                xmlwriter.insertAttribute("sz_areaid", alert.sz_areaid.ToString());
            else
                xmlwriter.insertAttribute("sz_areaid", "AdHoc");
            xmlwriter.insertAttribute("l_sched_utc", "0");
            xmlwriter.insertAttribute("l_comppk", logoninfo.l_comppk.ToString());
            xmlwriter.insertAttribute("l_deptpk", logoninfo.l_deptpk.ToString());
            xmlwriter.insertAttribute("l_userpk", logoninfo.l_userpk.ToString());
            xmlwriter.insertAttribute("l_deptpri", logoninfo.l_deptpri.ToString());
            xmlwriter.insertAttribute("sz_password", logoninfo.sz_password);
            xmlwriter.insertAttribute("f_simulation", (n_function == UCommon.USENDING_LIVE ? "0" : "1"));
            xmlwriter.insertAttribute("l_version", "3");
                xmlwriter.insertStartElement("textmessages");
                for (int i = 0; i < getLanguageCount(); i++)
                {
                    xmlwriter.insertStartElement("message");
                    xmlwriter.insertAttribute("sz_name", getLanguage(i).getName());
                    xmlwriter.insertAttribute("sz_text", getLanguage(i).getText());
                    xmlwriter.insertAttribute("sz_cb_oadc", getLanguage(i).getCBOadc());
                    xmlwriter.insertAttribute("l_otoa", getLanguage(i).getOtoa());
                    for (int c = 0; c < getLanguage(i).getCCodeCount(); c++)
                    {
                        xmlwriter.insertStartElement("ccode");
                        xmlwriter.insertText(getLanguage(i).getCCode(c).getCCode());
                        xmlwriter.insertEndElement(); //ccode
                    }
                    xmlwriter.insertEndElement(); //message
                }
                xmlwriter.insertEndElement(); //textmessages
            xmlwriter.insertEndElement(); //LBA
            xmlwriter.insertEndDocument();
            xmlwriter.finalize();

            //String xml = Encoding.UTF8.GetBytes(xmlwriter.getXml());
            w.write(xmlwriter.getXml());
            //w.write(xmlwriter.getXml2());
            //w.write(xmlwriter.getXml(Encoding.UTF8));
            w.close();
            return true;
        }
    }

    public class UBoundingRect : UShape
    {
        public Double _left, _right, _top, _bottom;
        public UBoundingRect() : this(0.0, 0.0, 0.0, 0.0)
        {
            
        }
        public UBoundingRect(Double left, Double right, Double top, Double bottom)
        {
            _left = left;
            _right = right;
            _top = top;
            _bottom = bottom;
        }
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            throw new NotImplementedException();
        }
    }

    public class UGIS : UShape
    {
        protected List<UGisRecord> m_gis = new List<UGisRecord>();
        public UGIS()
            : base()
        {
        }

        public bool addRecord(UGisRecord r)
        {
            m_gis.Add(r);
            return true;
        }

        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            //throw new NotImplementedException();
            try
            {
                for (int i = 0; i < m_gis.Count; i++)
                {
                    w.writeline("/KONDMID=" + m_gis[i].id);
                    w.writeline("/ADRID=");
                }
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, string sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            //throw new NotImplementedException();
            try
            {
                try
                {
                    double l_bo, r_bo, u_bo, b_bo;
                    double f_epsilon = 0.001f;
                    l_bo = 0.0f-f_epsilon;
                    r_bo = 0.0f+f_epsilon;
                    u_bo = 0.0f+f_epsilon;
                    b_bo = 0.0f-f_epsilon;
                    /*l_bo = lon - x - f_epsilon;
                    r_bo = lon + x + f_epsilon;
                    u_bo = lat + y + f_epsilon;
                    b_bo = lat - y - f_epsilon;*/
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", l_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", u_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", r_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", b_bo));
                }
                catch (Exception e)
                {
                    ULog.error(w.getRefno(), "UEllipse::WriteAddressFileGUI", e.Message);
                    throw new UFileWriteException(e.Message);
                }
                finally
                {
                    w.close();
                }
                return true;

            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }

    public class UResend : UShape
    {
        public String sz_header;
        public List<long> resend_status = new List<long>();
        public long resend_refno;

        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            try
            {
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/Resend={0}", sz_header));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/Refno={0}", resend_refno));
                for(int i = 0; i < resend_status.Count; i++)
                {
                    //SMS Specific - resend of sms is based on l_dst=[0,1,2]
                    if (resend_status[i] >= 8000 && resend_status[i] <= 8002)
                        resend_status[i] = resend_status[i] - 8000;

                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "/Status={0}", resend_status[i]));
                }
            }
            catch (Exception e)
            {
                ULog.error(w.getRefno(), "UResend::WriteAddressFile", e.Message);
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            return true;
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            try
            {
                w.publish();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, string sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            throw new NotImplementedException();
        }

    }

    public class UResendVoice : UResend
    {
        public UResendVoice()
            : base()
        {
            sz_header = "MS";
        }
    }
    public class UResendSMS : UResend
    {
        public UResendSMS()
            : base()
        {
            sz_header = "SM";
        }
    }

    public class UEllipse : UShape
    {
        public double lon, lat;
        public double x, y;
        public UEllipse()
            : base()
        {
        }
        public void setCenter(double lon, double lat)
        {
            this.lon = lon;
            this.lat = lat;
        }
        public void setExtents(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        override public bool WriteAddressFile(ref AdrfileWriter w)
        {
            //throw new NotImplementedException("Ellipse sending not yet implemented");
            try
            {
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/coor={0}", lon));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/coor={0}", lat));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/coor={0}", x));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "/coor={0}", y));
            }
            catch(Exception e)
            {
                ULog.error(w.getRefno(), "UEllipse::WriteAddressFile", e.Message);
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            return true;
        }
        override public bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            //throw new NotImplementedException("Ellipse sending not yet implemented");
            try
            {
                double l_bo, r_bo, u_bo, b_bo;
                double f_epsilon = 0.001f;
                l_bo = lon - x - f_epsilon;
                r_bo = lon + x + f_epsilon;
                u_bo = lat + y + f_epsilon;
                b_bo = lat - y - f_epsilon;
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", l_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", u_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", r_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", b_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", lon));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", lat));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", x));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", y));
            }
            catch (Exception e)
            {
                ULog.error(w.getRefno(), "UEllipse::WriteAddressFileGUI", e.Message);
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            return true;
        }
    }

    public class UPolygon : UShape
    {
        protected ArrayList m_array_polypoints;
        public ArrayList getPolygon() { return m_array_polypoints; }
        public long getSize() { return m_array_polypoints.Count; }
        public UPolypoint getPoint(int i) { return (UPolypoint)m_array_polypoints[i]; }

        public UPolygon()
            : base()
        {
            m_array_polypoints = new ArrayList();
        }
        public void addPoint(double lon, double lat)
        {
            m_array_polypoints.Add(new UPolypoint(lon, lat));
        }
        override public bool WriteAddressFile(ref AdrfileWriter w)
        {
            if (getSize() < 3)
            {
                ULog.error(w.getRefno(), "UPolygon::WriteAddressFile. The UPolygon contains not enough points to write (min 3)");
                w.close();
                return false;
            }
            try
            {
                String write;
                for (int i = 0; i < getSize(); i++)
                {
                    write = String.Format(UCommon.UGlobalizationInfo, "/coor={0}", getPoint(i).getLon());
                    w.writeline(write);
                    write = String.Format(UCommon.UGlobalizationInfo, "/coor={0}", getPoint(i).getLat());
                    w.writeline(write);
                }
            }
            catch (Exception e)
            {
                ULog.error(w.getRefno(), "UPolygon::WriteAddressFileBCP", e.Message);
                //w.close();
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            //w.close();
            return true;
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            if (getSize() < 3)
            {
                ULog.error(w.getRefno(), "UPolygon::WriteAddressFileGUI. The UPolygon contains not enough points to write (min 3)");
                w.close();
                return false;
            }
            try
            {
                String lbo, rbo, ubo, bbo;
                lbo = ""; rbo = ""; ubo = ""; bbo = "";
                w.writeline(lbo);
                w.writeline(ubo);
                w.writeline(rbo);
                w.writeline(bbo);

                for (int i = 0; i < getSize(); i++)
                {
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", getPoint(i).getLon()));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", getPoint(i).getLat()));
                }
            }
            catch (Exception e)
            {
                ULog.error(w.getRefno(), "UPolygon::WriteAddressFileGUI", e.Message);
                //w.close();
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            return true;
        }
    }

    public class UPolypoint
    {
        double lon, lat;
        public double getLon() { return lon; }
        public double getLat() { return lat; }

        public UPolypoint(double lon, double lat)
        {
            this.lon = lon;
            this.lat = lat;
        }
    }
    public class URGBA
    {
        public int a, r, g, b;
        public URGBA(int a, int r, int g, int b)
        {
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        }

    }


    public struct PAALERT
    {
        public static String getSendingTypeText(int n_sendingtype)
        {
            switch (n_sendingtype)
            {
                case 0:
                    return "Sending type not set";
                case 3:
                    return "Polygon";
                case 4:
                    return "GIS import";
                case 7:
                    return "Test message";
                case 8:
                    return "Ellipse";
                default:
                    return "Unknown sending type";
            }
        }
        public String getSendingTypeText()
        {
            /*switch (n_sendingtype)
            {
                case 0:
                    return "Sending type not set";
                case 3:
                    return "Polygon";
                case 8:
                    return "Ellipse";
                default:
                    return "Unknown sending type";
            }*/
            return getSendingTypeText(n_sendingtype);
        }
        public Int64 l_alertpk;
        public String l_parent;
        public String sz_name;
        public String sz_description;
        public long l_profilepk;
        public String l_schedpk;
        public String sz_oadc;
        public long l_validity;
        public long l_addresstypes;
        public String l_timestamp;
        public long f_locked;
        public String sz_areaid;
        BBDYNARESCHED m_dynaresched;
        BBRESCHEDPROFILE m_reschedprofile;
        public int n_function; //live / simulation / test
        public UShape m_shape;
        public UShape m_lba_shape;
        public int n_sendingtype;
        public int n_maxchannels;
        public int n_requesttype;
        public int n_nofax;

        public int getSendingType() { return n_sendingtype; }

        public void setAlertPk(Int64 s) { l_alertpk = s; }
        public void setParent(string s) { l_parent = s; }
        public void setName(string s) { sz_name = s; }
        public void setDescription(string s) { sz_description = s; }
        public void setProfilePk(long s) { l_profilepk = s; }
        public void setSchedPk(string s) { l_schedpk = s; }
        public void setOadc(string s) { sz_oadc = s; }
        public void setValidity(long s) { l_validity = s; }
        public void setAddressTypes(long s) { l_addresstypes = s; }
        public void setTimestamp(string s) { l_timestamp = s; }
        public void setLocked(long s) { f_locked = s; }
        public void setAreaID(String s) { sz_areaid = s; }
        public void setFunction(int n) { n_function = n; }
        public void setShape(UShape s) { m_shape = s; }
        public void setLBAShape(UShape s) { m_lba_shape = s; }
        public void setSendingType(int n) { n_sendingtype = n; }
        public void setMaxChannels(int n) { n_maxchannels = n; }
        public void setRequestType(int n) { n_requesttype = n; }
        public void setNofax(int n) { n_nofax = n; }
        public bool hasValidAreaID()
        {
            if (sz_areaid == null)
                return false;
            if(sz_areaid.Equals("-2") || sz_areaid.Equals("-1") || sz_areaid.Equals("0") || sz_areaid.Equals(""))
            {
                return false;
            }
            return true;
        }
        public void setReschedProfile(BBRESCHEDPROFILE p)
        {
            m_reschedprofile = p;
            m_dynaresched = new BBDYNARESCHED(p);
        }
    }

    public class SMS_SENDING : PAS_SENDING 
    {
        //new override public char sendingtype = 's'; //sms
        public String sz_smsmessage;
        public String sz_smsoadc;
        public int n_expirytime_minutes;

        public SMS_SENDING()
        {
            sendingtype = 's';
        }

        public override bool hasLBA()
        {
            return false;
        }
        public void setSmsMessage(String s)
        {
            sz_smsmessage = s;
        }
        public void setSmsOadc(String s)
        {
            sz_smsoadc = s;
        }
        public void setExpiryTimeMinutes(int n)
        {
            n_expirytime_minutes = n;
        }
    }

    public class PAS_SENDING
    {
        public char getSendingType() { return sendingtype; }
        public char sendingtype = 'v'; //voice
        public long l_refno;
        public long l_resend_refno;
        public bool b_resend;
        public BBSENDNUM m_sendnum;
        public BBRESCHEDPROFILE m_reschedprofile;
        public MDVSENDINGINFO m_sendinginfo;
        public BBVALID m_valid;
        public BBACTIONPROFILESEND m_profile;
        public BBDYNARESCHED m_dynaresched;
        public UShape m_shape;
        public UShape m_lba_shape;
        public AdrfileWriter adrwriter;
        public AdrfileLBAWriter adrlbawriter;
        public AdrfileGUIWriter adrguiwriter;
        public BBPROJECT m_project;
        protected bool b_simulation;
        public bool getSimulation() { return b_simulation; }
        public void setSimulation(bool b) { b_simulation = b; }
    //public 

        public bool createShape(ref UMAPSENDING s)
        {
            if (s.b_resend)
            {
                //make a copy of the old sending
                UShape shape = null;

                if (getSendingType() == 'v') //voice
                    shape = new UResendVoice();
                else if (getSendingType() == 's') //sms
                    shape = new UResendSMS();
                else
                    return false;
                shape.resend().resend_refno = s.n_resend_refno;
                for (int i = 0; i < s.resend_statuscodes.Length; i++)
                {
                    shape.resend().resend_status.Add(s.resend_statuscodes[i]);
                }
                setShape(ref shape);
            }
            else if (typeof(UPOLYGONSENDING) == s.GetType())
            {
                UPOLYGONSENDING polygon = (UPOLYGONSENDING)s;
                s.setGroup(3);
                UShape shape = new UPolygon();
                for (int i = 0; i < polygon.polygonpoints.Length; i++)
                {
                    UMapPoint point = (UMapPoint)polygon.polygonpoints.GetValue(i);
                    shape.poly().addPoint(point.lon, point.lat);
                }
                setShape(ref shape);
                s.n_sendingtype = UShape.SENDINGTYPE_POLYGON;
            }
            else if (typeof(UELLIPSESENDING) == s.GetType())
            {
                UELLIPSESENDING ellipse = (UELLIPSESENDING)s;
                s.setGroup(8);
                UShape shape = new UEllipse();
                shape.ellipse().setCenter(ellipse.ellipse.center.lon, ellipse.ellipse.center.lat);
                shape.ellipse().setExtents(ellipse.ellipse.radius.lon, ellipse.ellipse.radius.lat);
                setShape(ref shape);
                s.n_sendingtype = UShape.SENDINGTYPE_ELLIPSE;

            }
            else if (typeof(UGISSENDING) == s.GetType())
            {
                UGISSENDING gis = (UGISSENDING)s;
                s.setGroup(4);
                UShape shape = new UGIS();
                for (int i = 0; i < gis.gis.Length; i++)
                {
                    UGisRecord rec = gis.gis[i];
                    shape.gis().addRecord(rec);
                }
                setShape(ref shape);
                s.n_sendingtype = UShape.SENDINGTYPE_GIS;
            }
            
            return true;
        }
        public virtual bool hasLBA() {
            bool b = false;
            try
            {
                b = m_lba_shape.lba().getValid();
            }
            catch (Exception)
            {
            }
            return b;
        }

        public void setRefno(long refno, ref BBPROJECT p)
        {
            l_refno = refno;
            m_project = p;
        }
        public void setSendNum(ref BBSENDNUM r)
        {
            m_sendnum = r;
        }

        /*DYNARESCHED will be automatically generated*/
        public void setReschedProfile(ref BBRESCHEDPROFILE r)
        {
            m_reschedprofile = r;
            m_dynaresched = new BBDYNARESCHED(r);

        }
        public void setSendingInfo(ref MDVSENDINGINFO r)
        {
            m_sendinginfo = r;
        }
        public void setValid(ref BBVALID r)
        {
            m_valid = r;
        }
        public void setActionProfile(ref BBACTIONPROFILESEND r)
        {
            m_profile = r;
        }
        public void setDynaResched(ref BBDYNARESCHED r)
        {
            m_dynaresched = r;
        }
        public bool setShape(ref UShape s)
        {
            m_shape = s;
            //create addresslist using UmsFile.AdrfileWriter
            if (l_refno < 0)
            {
                ULog.error("PAS_SENDING failed to create adrfile because l_refno was not set");
                return false;
            }
            try
            {
                if (b_resend)
                {
                    adrguiwriter = new AdrfileGUIWriter(l_refno, l_resend_refno);
                }
                else
                {
                    adrguiwriter = new AdrfileGUIWriter(l_refno);
                    s.WriteAddressFileGUI(ref adrguiwriter);
                }
                adrwriter = new AdrfileWriter(l_refno, getSendingType(), b_resend);
                s.WriteAddressFile(ref adrwriter);
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool setLBAShape(ref ULOGONINFO logoninfo, ref ULocationBasedAlert s, int n_function)
        {
            m_lba_shape = s;
            if (l_refno < 0)
            {
                ULog.error("PAS_SENDING failed to create LBA adrfile because l_refno was not set");
                return false;
            }
            try
            {
                m_lba_shape.lba().setValid();
                adrlbawriter = new AdrfileLBAWriter(m_project.sz_projectpk, l_refno);
                PAALERT nullalert = new PAALERT();
                s.WriteAddressFileLBA(ref logoninfo, new UDATETIME(m_sendinginfo.l_scheddate, m_sendinginfo.l_schedtime), "sms", ref m_project, ref nullalert, l_refno, n_function, ref adrlbawriter);
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool setLBAShape(ref ULOGONINFO logoninfo,  ref PAALERT alert, ref UShape s, int n_function)
        {
            m_lba_shape = s;
            //create addresslist using UmsFile.AdrfileWriter
            if (l_refno < 0)
            {
                ULog.error("PAS_SENDING failed to create LBA adrfile because l_refno was not set");
                return false;
            }
            try
            {
                adrlbawriter = new AdrfileLBAWriter(m_project.sz_projectpk, l_refno);
                s.WriteAddressFileLBA(ref logoninfo, new UDATETIME(m_sendinginfo.l_scheddate, m_sendinginfo.l_schedtime), "sms", ref m_project, ref alert, l_refno, n_function, ref adrlbawriter);
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool publishGUIAdrFile()
        {
            //return adrguiwriter.publish();
            try
            {
                /*if (b_resend)
                {
                    m_shape.WriteAddressResendFileGUI(l_resend_refno);
                }
                else
                {
                    adrguiwriter.publish();
                }*/
                adrguiwriter.publish();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool publishAdrFile()
        {
            try
            {
                adrwriter.publish();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool publishLBAFile()
        {
            //If the file is empty, just cleanup and exit
            if (!hasLBA())
            {
                try
                {
                    lbacleanup();
                    return false;
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
            //try to publish the addressfile to LBA path
            try
            {
                adrlbawriter.publish();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public bool lbacleanup()
        {
            try
            {
                adrlbawriter.close();
            }
            catch (Exception)
            {
            }
            try
            {
                adrlbawriter.delete();
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }


    public class UMAPSENDING
    {
        public String sz_sendingname;
        public long n_profilepk;
        public long n_scheddate;
        public long n_schedtime;
        public BBSENDNUM oadc;
        public int n_validity;
        public long n_reschedpk;
        public int n_sendingtype;
        public long n_projectpk;
        public long n_refno;
        public int n_dynvoc;
        public int n_retries;
        public int n_interval;
        public int n_canceltime;
        public int n_canceldate;
        public int n_pausetime;
        public int n_pauseinterval;
        public long n_addresstypes;
        public ULOGONINFO logoninfo;
        public UMapBounds mapbounds;
        public String sz_function;
        public String sz_lba_oadc;
        public ULocationBasedAlert m_lba;
        public String sz_sms_oadc;
        public String sz_sms_message;
        public int n_sms_expirytime_minutes;
        public bool b_resend;
        public long n_resend_refno;
        public long[] resend_statuscodes;
        public int n_send_channels; //0=all, 1=voice, 2=sms
        /*public String sz_sms_message;
        public String sz_sms_oadc;
        public int n_sms_expirytime_minutes;*/
        public int n_maxchannels;
        protected int n_group;
        protected int n_function;
        

        public int getGroup()
        {
            return n_group;
        }
        public int getFunction()
        {
            return n_function;
        }
        public void setGroup(int n)
        {
            n_group = n;
        }
        public void setFunction(int n)
        {
            n_function = n;
        }
        public bool doSendVoice()
        {
            if (n_send_channels == 0 || n_send_channels == 1)
                return true;
            return false;
        }
        public bool doSendSMS()
        {
            if (n_send_channels == 0 || n_send_channels == 2)
                return true;
            return false;
        }
    }
    public class UPOLYGONSENDING : UMAPSENDING
    {
        public UMapPoint[] polygonpoints;
    }
    public class UELLIPSESENDING : UMAPSENDING
    {
        public UEllipseDef ellipse;
    }
    public class UGISSENDING : UMAPSENDING
    {
        public UGisRecord[] gis;
    }
}
