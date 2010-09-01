using System;
using System.Collections;
using com.ums.UmsCommon;
using com.ums.UmsFile;
using System.Collections.Generic;
using System.Text;
using com.ums.PAS.Database;
using System.Xml;
using System.Text;
using System.Security.Cryptography;
using System.Xml.Serialization;
using System.IO;


namespace com.ums.UmsParm
{
    //public static bool operator !=(SENDINGTYPES s, long x) { return x != (long)s; }

    /*public static class USENDINGTYPES
    {
        public enum types
        {
            SENDINGTYPE_POLYGON = 3,
            SENDINGTYPE_ELLIPSE = 8,
            SENDINGTYPE_GIS = 4,
            SENDINGTYPE_MUNICIPAL = 9,
            SENDINGTYPE_TESTSENDING = 0,
        }
        public static bool operator !=(USENDINGTYPES a, USENDINGTYPES b)
        {
            return (a!=b);
        }
        public static bool operator ==(USENDINGTYPES a, USENDINGTYPES b)
        {
            return a == b;
        }
    }*/

    public enum PARMOPERATION
    {
        insert,
        update,
        delete,
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
        public Int32 l_language;
        public List<UNSLOOKUP> nslookups = new List<UNSLOOKUP>();
        public List<UDEPARTMENT> departments = new List<UDEPARTMENT>();
        public UPASUISETTINGS uisettings;
        public String sessionid;
        public BBUSER_BLOCK_REASONS reason = BBUSER_BLOCK_REASONS.NONE;
        public String sz_organization;
    }

    public class UPASUISETTINGS
    {
        public bool initialized = false;
        public String sz_languageid;
        public double f_mapinit_lbo;
        public double f_mapinit_rbo;
        public double f_mapinit_ubo;
        public double f_mapinit_bbo;
        public bool b_autostart_fleetcontrol;
        public bool b_autostart_parm;
        public bool b_window_fullscreen;
        public int l_winpos_x;
        public int l_winpos_y;
        public int l_win_width;
        public int l_win_height;
        public int l_gis_max_for_details;
        public String sz_skin_class;
        public String sz_theme_class;
        public String sz_watermark_class;
        public String sz_buttonshaper_class;
        public String sz_gradient_class;
        public String sz_title_class;
        public int l_mapserver;
        public String sz_wms_site;
        public String sz_wms_layers;
        public String sz_wms_format;
        public String sz_wms_username;
        public String sz_wms_password;
        public int l_drag_mode;

        public String sz_email_name;
        public String sz_email;
        public String sz_emailserver;
        public int l_mailport;
        public int l_lba_update_percent;
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
        public UPolygon typedef1;
        public UEllipse typedef2;
        public UBoundingRect typedef3;
        public void AddMunicipal(String sz_id, String sz_name)
        {
            UMunicipalDef d = new UMunicipalDef();
            d.sz_municipalid = sz_id;
            d.sz_municipalname = sz_name;
            municipals.Add(d);
        }
        public List<UShape> restrictionShapes = new List<UShape>();
        public void AddRestrictionShape(ref UShape s)
        {
            restrictionShapes.Add(s);
        }
    }


    public abstract class UShape
    {
        //public static USENDINGTYPES operator !=(USENDINGTYPES s, long n) { return new USENDINGTYPES(); }
        /*public enum USENDINGTYPES : int
        {
            
            SENDINGTYPE_POLYGON = 3,
            SENDINGTYPE_ELLIPSE = 8,
            SENDINGTYPE_GIS = 4,
            SENDINGTYPE_MUNICIPAL = 9,
            SENDINGTYPE_TESTSENDING = 0,

        };*/
        
        public static int SENDINGTYPE_POLYGON = 3;
        public static int SENDINGTYPE_ELLIPSE = 8;
        public static int SENDINGTYPE_GIS = 4;
        public static int SENDINGTYPE_TAS = 5;
        public static int SENDINGTYPE_MUNICIPAL = 9;
        public static int SENDINGTYPE_TESTSENDING = 0;//imported list

        //[XmlIgnore]
        [XmlAttribute("col_red")]
        public float col_red;
        [XmlAttribute("col_green")]
        public float col_green;
        [XmlAttribute("col_blue")]
        public float col_blue;
        [XmlAttribute("col_alpha")]
        public float col_alpha;
        [XmlAttribute("f_disabled")]
        public int f_disabled;
        [XmlAttribute("l_disabled_timestamp")]
        public long l_disabled_timestamp;
        public UPolygon poly() { return (UPolygon)this; }
        public UEllipse ellipse() { return (UEllipse)this; }
        public UGIS gis() { return (UGIS)this; }
        public UGeminiStreet gemini() { return (UGeminiStreet)this; }
        public UMunicipalShape municipal() { return (UMunicipalShape)this; }
        public UTasShape tas() { return (UTasShape)this; }
        public UTestSending test() { return (UTestSending)this; }
        public UResend resend() { return (UResend)this; }
        public ULocationBasedAlert lba() { return (ULocationBasedAlert)this; }
        public abstract bool WriteAddressFile(ref AdrfileWriter w);
        public abstract bool WriteAddressFileGUI(ref AdrfileGUIWriter w);
        protected abstract bool ParseFromXml(ref XmlDocument d);
        protected abstract String CreateXml(ref USimpleXmlWriter d);

        public static UShape Deserialize(String xml)
        {
            if (xml.IndexOf("UPolygon") >= 0)
            {
                return UPolygon.Deserialize(xml);
            }
            else if (xml.IndexOf("UPLMN") >= 0)
                return UPLMN.Deserialize(xml);
            return null;
        }

        public bool CreateXml(ref String outxml, ref String md5)
        {
            try
            {
                USimpleXmlWriter w = new USimpleXmlWriter(Encoding.UTF8);
                w.insertComment("UMS -- Created XML from UShape object");
                outxml = CreateXml(ref w);
                md5 = UmsCommon.Helpers.CreateMD5Hash(outxml);
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * Get shape string from DB, then parse it based on the node type
         */
        public static UShape ParseFromXml(String s)
        {
            try
            {
                if (s.Trim().Length <= 0) //the whole world
                    return new UBoundingRect(-180.0, 180.0, 90.0, -90.0);
                UShape retshape = null;
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(s);
                {
                    XmlNodeList nl;
                    nl = doc.GetElementsByTagName("polygon");
                    if (nl.Count > 0)
                    {
                        retshape = new UPolygon();
                        retshape.ParseFromXml(ref doc);
                        return retshape;
                    }
                    nl = doc.GetElementsByTagName("ellipse");
                    if (nl.Count > 0)
                    {
                        retshape = new UEllipse();
                        retshape.ParseFromXml(ref doc);
                        return retshape;
                    }
                    throw new UXmlShapeException("XML String does not contain a valid shape");

                }
            }
            catch (Exception e)
            {
                throw new UXmlShapeException(e.Message);
            }
        }
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
        protected UShape sourceshape; //either polygon or ellipse
        public void setSourceShape(ref UShape shape) { sourceshape = shape; }

        public bool Validate()
        {
            if (m_languages != null)
            {
                if (m_languages.Count > 0)
                    setValid();
            }
            return false;
        }

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
            bool b_adhoc = false;
            USimpleXmlWriter xmlwriter = new USimpleXmlWriter(new UTF8Encoding(false));
            xmlwriter.insertStartDocument();
            xmlwriter.insertStartElement("LBA");
            if (typeof(PAALERT).Equals(alert.GetType()))
            {
                xmlwriter.insertAttribute("operation", "SendArea");
            }
            else if (typeof(PANULLALERT).Equals(alert.GetType()))
            {
                if (typeof(UTasShape).Equals(sourceshape.GetType())) //TAS
                    xmlwriter.insertAttribute("operation", "SendInternational");
                else if (typeof(UTasCountShape).Equals(sourceshape.GetType())) //TAS Count
                {
                    xmlwriter.insertAttribute("operation", "CountInternational");
                    xmlwriter.insertAttribute("l_requestpk", n_parentrefno.ToString());
                }
                else if (typeof(UPolygon).Equals(sourceshape.GetType()))
                    xmlwriter.insertAttribute("operation", "SendPolygon");
                else if (typeof(UEllipse).Equals(sourceshape.GetType()))
                    xmlwriter.insertAttribute("operation", "SendEllipse");
                else
                    throw new USendingTypeNotSupportedException("Sending type " + sourceshape.GetType().ToString() + " not supported for Location Based Alert");
                b_adhoc = true;
            }
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk.ToString());
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
            xmlwriter.insertAttribute("sz_password", logoninfo.sz_password.ToString());
            xmlwriter.insertAttribute("f_simulation", (n_function == UCommon.USENDING_LIVE ? "0" : "1"));
            xmlwriter.insertAttribute("l_version", "3");
            xmlwriter.insertAttribute("l_validity", alert.n_expiry.ToString()); //If TAS then jalla jalla ((UTASSENDING)sourceshape).n_sms_expirytime_minutes;
            xmlwriter.insertAttribute("l_requesttype", alert.n_requesttype.ToString());
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
                if (b_adhoc)
                {
                    if (sourceshape == null)
                    {
                        //TAS has no shape
                    }
                    else if (typeof(UTasShape).Equals(sourceshape.GetType()))
                    {
                    }
                    else if (typeof(UTasCountShape).Equals(sourceshape.GetType()))
                    {
                    }
                    else if (typeof(UPolygon).Equals(sourceshape.GetType()))
                    {
                        xmlwriter.insertStartElement("alertpolygon");
                        xmlwriter.insertAttribute("col_a", "0");
                        xmlwriter.insertAttribute("col_b", "0");
                        xmlwriter.insertAttribute("col_r", "0");
                        xmlwriter.insertAttribute("col_g", "0");
                        xmlwriter.insertAttribute("l_alertpk", "AdHoc");
                        for (int i = 0; i < sourceshape.poly().getSize(); i++)
                        {
                            xmlwriter.insertStartElement("polypoint");
                            xmlwriter.insertAttribute("xcord", sourceshape.poly().getPoint(i).getLon().ToString(UCommon.UGlobalizationInfo));
                            xmlwriter.insertAttribute("ycord", sourceshape.poly().getPoint(i).getLat().ToString(UCommon.UGlobalizationInfo));
                            xmlwriter.insertEndElement();
                        }
                        xmlwriter.insertEndElement();
                    }
                    else if (typeof(UEllipse).Equals(sourceshape.GetType()))
                    {
                        xmlwriter.insertStartElement("alertellipse");
                        xmlwriter.insertAttribute("col_a", "0");
                        xmlwriter.insertAttribute("col_b", "0");
                        xmlwriter.insertAttribute("col_r", "0");
                        xmlwriter.insertAttribute("col_g", "0");
                        xmlwriter.insertAttribute("l_alertpk", "AdHoc");
                        xmlwriter.insertAttribute("centerx", sourceshape.ellipse().lon.ToString(UCommon.UGlobalizationInfo));
                        xmlwriter.insertAttribute("centery", sourceshape.ellipse().lat.ToString(UCommon.UGlobalizationInfo));
                        xmlwriter.insertAttribute("cornerx", (sourceshape.ellipse().lon + sourceshape.ellipse().x).ToString(UCommon.UGlobalizationInfo));
                        xmlwriter.insertAttribute("cornery", (sourceshape.ellipse().lat + sourceshape.ellipse().y).ToString(UCommon.UGlobalizationInfo));
                        xmlwriter.insertEndElement();
                    }
                }
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
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
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
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }

    }

    public class UTestSending : UShape
    {
        protected List<string> m_numbers = new List<string>();
        public UTestSending()
            : base()
        {
        }
        public bool addRecord(String number)
        {
            m_numbers.Add(number);
            return true;
        }
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            try
            {
                for (int i = 0; i < m_numbers.Count; i++)
                {
                    if (m_numbers[i].Length > 5)
                        w.writeline(m_numbers[i]);
                }
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                w.close();
            }
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            //throw new NotImplementedException();
            return true;
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, string sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            throw new NotImplementedException();
        }
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }

    }

    public class UMunicipalShape : UShape
    {
        public List<UMunicipalDef> m_municipals = new List<UMunicipalDef>();
        public List<UMunicipalDef> GetMunicipals() { return m_municipals; }
        protected UMapBounds m_bounds = null;
        public void SetBounds(UMapBounds b)
        {
            m_bounds = b;
        }


        public UMunicipalShape()
            : base()
        {
        }
        public bool addRecord(UMunicipalDef r)
        {
            m_municipals.Add(r);
            return true;
        }
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            try
            {
                for (int i = 0; i < m_municipals.Count; i++)
                {
                    if(m_municipals[i].sz_municipalid.Length>0)
                        w.writeline("/MUNICIPALID=" + m_municipals[i].sz_municipalid);
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
            try
            {
                if (m_bounds != null)
                {
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", m_bounds.l_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", m_bounds.u_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", m_bounds.r_bo));
                    w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", m_bounds.b_bo));
                    for(int i = 0; i < m_municipals.Count; i++)
                    {
                        w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}\t{1}", m_municipals[i].sz_municipalid, m_municipals[i].sz_municipalname));
                    }
                    
                }
                //w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", l_bo));
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                w.close();
            }
            return true;
        }
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }

    }

    public class UGeminiStreet : UShape
    {
        public List<com.ums.PAS.Address.UGisImportResultLine> linelist;
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, string sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            return base.WriteAddressFileLBA(ref logoninfo, sched, sz_type, ref project, ref alert, n_parentrefno, n_function, ref w);
        }
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }

    }

    public class UGIS : UShape
    {
        public int GetInabitantCount()
        {
            if (m_gis != null)
                return m_gis.Count;
            return -1;
        }
        public int GetLineCount()
        {
            return m_n_linecount;
        }
        protected int m_n_linecount = 0;
        public void SetLineCount(int n)
        {
            m_n_linecount = n;
        }
        public List<UGisRecord> m_gis = new List<UGisRecord>();
        protected UMapBounds m_bounds = null;
        public void SetBounds(UMapBounds b)
        {
            m_bounds = b;
        }

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
            try
            {
                try
                {
                    double l_bo, r_bo, u_bo, b_bo;
                    double f_epsilon = 0.0001f;
                    if (m_bounds == null)
                        m_bounds = new UMapBounds();
                    l_bo = m_bounds.l_bo-f_epsilon;
                    r_bo = m_bounds.r_bo+f_epsilon;
                    u_bo = m_bounds.u_bo+f_epsilon;
                    b_bo = m_bounds.b_bo-f_epsilon;
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
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
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

                    if (sz_header == "TR")
                    {
                        //SMS Specific - resend of sms is based on l_dst=[0,1,2] for TAS
                        if (resend_status[i] >= 1000 && resend_status[i] <= 1002)
                            resend_status[i] = resend_status[i] - 1000;
                        w.writeline(String.Format(UCommon.UGlobalizationInfo, "/Answercode={0}", resend_status[i]));
                    }
                    else
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
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
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

    public class UResendTAS : UResend
    {
        public UResendTAS()
            : base()
        {
            sz_header = "TR";
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
        protected override string CreateXml(ref USimpleXmlWriter w)
        {
            try
            {
                w.insertStartDocument();
                    w.insertStartElement("ellipse");
                        w.insertAttribute("col_a", col_alpha.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("col_r", col_red.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("col_g", col_green.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("col_b", col_blue.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("centerx", lon.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("centery", lat.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("cornerx", x.ToString(UCommon.UGlobalizationInfo));
                        w.insertAttribute("cornery", y.ToString(UCommon.UGlobalizationInfo));
                    w.insertEndElement();
                w.insertEndDocument();
                w.finalize();
                return w.getXml2();
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            try
            {
                XmlNodeList tags = doc.GetElementsByTagName("ellipse");
                if (tags.Count >= 1)
                {
                    XmlNode node = tags.Item(0);
                    String centerx = node.Attributes["centerx"].Value;
                    String centery = node.Attributes["centery"].Value;
                    String cornerx = node.Attributes["cornerx"].Value;
                    String cornery = node.Attributes["cornery"].Value;
                    String col_r = node.Attributes["col_r"].Value;
                    String col_g = node.Attributes["col_g"].Value;
                    String col_b = node.Attributes["col_b"].Value;
                    String col_a = node.Attributes["col_a"].Value;
                    this.col_alpha = Int32.Parse(col_a);
                    this.col_red = Int32.Parse(col_r);
                    this.col_green = Int32.Parse(col_g);
                    this.col_blue = Int32.Parse(col_b);
                    this.setCenter(Double.Parse(centerx, UCommon.UGlobalizationInfo), Double.Parse(centery, UCommon.UGlobalizationInfo));
                    this.setExtents(Double.Parse(cornerx, UCommon.UGlobalizationInfo), Double.Parse(cornery, UCommon.UGlobalizationInfo));

                }
                return true;
            }
            catch (Exception e)
            {
                ULog.warning("Error parsing Xml string");
                return false;
            }
        }
    }

    public class UTasCountShape : UTasShape
    {
    }

    public class UTasShape : UShape
    {
        public List<ULBACOUNTRY> countries;
        protected UMapBounds bounds;
        public UTASSENDING sending;

        public UTasShape()
            : base()
        {
            countries = new List<ULBACOUNTRY>();
        }
        public void addCountry(ULBACOUNTRY c)
        {
            countries.Add(c);
        }
        public void setBounds(UMapBounds b)
        {
            bounds = b;
        }
        public void setSending(ref UTASSENDING s)
        {
            sending = s;
        }
        public override bool WriteAddressFile(ref AdrfileWriter w)
        {
            throw new NotImplementedException();
        }
        public override bool WriteAddressFileGUI(ref AdrfileGUIWriter w)
        {
            try
            {
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", bounds.l_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", bounds.u_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", bounds.r_bo));
                w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", bounds.b_bo));
                for(int i=0; i < countries.Count; i++)
                {
                    w.writeline(countries[i].sz_iso + ";" + countries[i].l_iso_numeric.ToString() + ";" + countries[i].l_cc + ";" + countries[i].sz_name);
                }
            }
            catch (Exception e)
            {
                ULog.error(w.getRefno(), "UTasShape::WriteAddressFileGUI", e.Message);
                throw new UFileWriteException(e.Message);
            }
            finally
            {
                w.close();
            }
            return true;
        }
        public override bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, string sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            //return base.WriteAddressFileLBA(ref logoninfo, sched, sz_type, ref project, ref alert, n_parentrefno, n_function, ref w);
            ULocationBasedAlert loc = new ULocationBasedAlert();
            UShape _this = this;
            loc.setSourceShape(ref _this);
            loc.l_alertpk = "-1";
            loc.m_languages = new List<ULocationBasedAlert.LBALanguage>();
            ULocationBasedAlert.LBALanguage lbalang = new ULocationBasedAlert.LBALanguage();
            if (alert.n_requesttype == 2) //only a count request, we don't have any details
            {
                lbalang.sz_cb_oadc = "NULL";
                lbalang.sz_text = "NOTEXT";
                lbalang.sz_name = "Default";
                lbalang.sz_otoa = "0";
            }
            else
            {
                lbalang.sz_cb_oadc = sending.sz_sms_oadc;
                lbalang.sz_name = "Default";
                lbalang.sz_otoa = "0";
                lbalang.sz_text = sending.sz_sms_message;
            }
            lbalang.m_ccodes = new List<ULocationBasedAlert.LBACCode>();
            for(int i=0; i < countries.Count; i++)
            {
                ULocationBasedAlert.LBACCode ccode = new ULocationBasedAlert.LBACCode();
                ccode.ccode = countries[i].l_cc.ToString();
                lbalang.m_ccodes.Add(ccode);
            }
            loc.m_languages.Add(lbalang);
            loc.WriteAddressFileLBA(ref logoninfo, sched, sz_type, ref project, ref alert, n_parentrefno, n_function, ref w);
            return true;
        }
        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            throw new NotImplementedException();
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }

    }

    public class UPLMN : UShape
    {
        public static UPLMN Deserialize(String xml)
        {
            UPLMN cob = new UPLMN();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (UPLMN)serializer.Deserialize(reader);
                return cob;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public String Serialize()
        {
            XmlSerializer s = new XmlSerializer(typeof(UPLMN));
            XmlSerializerNamespaces xmlnsEmpty = new XmlSerializerNamespaces();
            xmlnsEmpty.Add("", "");
            MemoryStream m = new MemoryStream();
            XmlTextWriter xmlWriter = new XmlTextWriter(m, Encoding.UTF8);
            s.Serialize(xmlWriter, this, xmlnsEmpty);
            String xml;
            xml = Encoding.UTF8.GetString(m.GetBuffer());
            xml = xml.Substring(xml.IndexOf(Convert.ToChar(60)));
            xml = xml.Substring(0, (xml.LastIndexOf(Convert.ToChar(62)) + 1));
            return xml;
        }
        protected override string CreateXml(ref USimpleXmlWriter d)
        {
            throw new NotImplementedException();
        }
        protected override bool ParseFromXml(ref XmlDocument d)
        {
            throw new NotImplementedException();
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

    public class UPolygon : UShape
    {
        public static UPolygon Deserialize(String xml)
        {
            UPolygon cob = new UPolygon();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (UPolygon)serializer.Deserialize(reader);
                return cob;
            }
            catch (Exception e)
            {
                throw e;
            }
        }


        public String Serialize()
        {
            XmlSerializer s = new XmlSerializer(typeof(UPolygon));
            XmlSerializerNamespaces xmlnsEmpty = new XmlSerializerNamespaces();
            xmlnsEmpty.Add("", "");
            MemoryStream m = new MemoryStream();
            XmlTextWriter xmlWriter = new XmlTextWriter(m, Encoding.UTF8);
            s.Serialize(xmlWriter, this, xmlnsEmpty);
            String xml;
            xml = Encoding.UTF8.GetString(m.GetBuffer());
            xml = xml.Substring(xml.IndexOf(Convert.ToChar(60)));
            xml = xml.Substring(0, (xml.LastIndexOf(Convert.ToChar(62)) + 1));
            return xml;
        }

        [XmlElement("polypoint")] 
        public List<UPolypoint> m_array_polypoints;
        public List<UPolypoint> getPolygon() { return m_array_polypoints; }
        public long getSize() { return m_array_polypoints.Count; }
        public UPolypoint getPoint(int i) { return (UPolypoint)m_array_polypoints[i]; }
        

        public UPolygon()
            : base()
        {
            m_array_polypoints = new List<UPolypoint>();
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
        protected override string CreateXml(ref USimpleXmlWriter w)
        {
            try
            {
                w.insertStartElement("polygon");
                w.insertAttribute("col_a", col_alpha.ToString(UCommon.UGlobalizationInfo));
                w.insertAttribute("col_r", col_red.ToString(UCommon.UGlobalizationInfo));
                w.insertAttribute("col_g", col_green.ToString(UCommon.UGlobalizationInfo));
                w.insertAttribute("col_b", col_blue.ToString(UCommon.UGlobalizationInfo));
                for (int i = 0; i < m_array_polypoints.Count; i++)
                {
                    w.insertStartElement("pp");
                    w.insertAttribute("lon", getPoint(i).lon.ToString(UCommon.UGlobalizationInfo));
                    w.insertAttribute("lat", getPoint(i).lat.ToString(UCommon.UGlobalizationInfo));
                    w.insertEndElement();
                }
                w.insertEndElement(); //polygon
                w.finalize();
                return w.getXml2();
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        protected override bool ParseFromXml(ref XmlDocument doc)
        {
            try
            {
                XmlNodeList headnode = doc.GetElementsByTagName("polygon");
                if (headnode.Count >= 1)
                {
                    XmlNode node = headnode.Item(0);
                    String col_a = node.Attributes["col_a"].Value;
                    String col_r = node.Attributes["col_r"].Value;
                    String col_g = node.Attributes["col_g"].Value;
                    String col_b = node.Attributes["col_b"].Value;
                    this.col_alpha = Int32.Parse(col_a);
                    this.col_red = Int32.Parse(col_r);
                    this.col_green = Int32.Parse(col_g);
                    this.col_blue = Int32.Parse(col_b);
                }
                XmlNodeList points = doc.GetElementsByTagName("pp");
                for (int i = 0; i < points.Count; i++)
                {
                    XmlNode node = points.Item(i);
                    String x = node.Attributes["lon"].Value;
                    String y = node.Attributes["lat"].Value;
                    m_array_polypoints.Add(new UPolypoint(Double.Parse(x, UCommon.UGlobalizationInfo), Double.Parse(y, UCommon.UGlobalizationInfo)));
                }
                return true;
            }
            catch (Exception e)
            {
                ULog.warning("Error parsing Xml string");
                return false;
            }
        }
    }

    public class UPolypoint
    {
        [XmlAttribute("lon")] public double lon;
        [XmlAttribute("lat")] public double lat;

        public double getLon() { return lon; }
        public double getLat() { return lat; }

        public UPolypoint(double lon, double lat)
        {
            this.lon = lon;
            this.lat = lat;
        }
        public UPolypoint()
        {
            this.lat = 0.0;
            this.lon = 0.0;
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

    public class PAOBJECT
    {
        public PARMOPERATION parmop;
        public long l_temppk;
        public long l_objectpk;
        public long l_deptpk;
        public long l_importpk;
        public String sz_name;
        public String sz_description;
        public long l_categorypk;
        public long l_parent;
        public String sz_address;
        public String sz_postno;
        public String sz_place;
        public String sz_phone;
        public String sz_metadata;
        public bool b_isobjectfolder;
        public long l_timestamp;
        public com.ums.UmsParm.UPolygon m_shape; //must be of type: polygon
    }

    public class PAEVENT
    {
        public PARMOPERATION parmop;
        public long l_temppk;
        public long l_eventpk;
        public long l_parent;
        public String sz_name;
        public String sz_description;
        public long l_categorypk;
        public long l_timestamp;
        public float f_epi_lon;
        public float f_epi_lat;
        public List<PAALERT> alerts = new List<PAALERT>();
        public override string ToString()
        {
            return sz_name;
        }
    }

    public class PANULLALERT : PAALERT
    {
    }

    public class PAALERT
    {
        public static String getSendingTypeText(int n_sendingtype)
        {
            switch (n_sendingtype)
            {
                case 0:
                    return "Test sending";
                case 3:
                    return "Polygon";
                case 4:
                    return "GIS import";
                case 7:
                    return "Test message";
                case 8:
                    return "Ellipse";
                case 9:
                    return "Municipal";
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
        public PARMOPERATION parmop;
        public long l_temppk;
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
        public int n_expiry;
        //public int n_nofax;
        public String sz_sms_oadc;
        public String sz_sms_message;

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
        //public void setNofax(int n) { n_nofax = n; }
        public void setSmsOadc(String s) { sz_sms_oadc = s; }
        public void setSmsMessage(String s) { sz_sms_message = s; }
        public void setExpiry(int n) { n_expiry = n; }
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
        public void setReschedProfile(BBRESCHEDPROFILE p, long n_scheddate)
        {
            m_reschedprofile = p;
            m_dynaresched = new BBDYNARESCHED(p, n_scheddate);
        }
    }



    public class TAS_SENDING : SMS_SENDING
    {
        private bool m_b_allow_response;
        private string m_sz_response_number;

        public TAS_SENDING()
        {
            sendingtype = 's';
        }
        public override bool hasLBA()
        {
            return true;
        }
        public override bool setLBAShape(ref ULOGONINFO logoninfo, ref ULocationBasedAlert s, int n_function)
        {
            adrlbawriter = new AdrfileLBAWriter(m_project.sz_projectpk, l_refno, true, SENDCHANNEL.TAS);
            //s = new ULocationBasedAlert();
            PAALERT alert = new PANULLALERT();
            alert.n_expiry = (int)s.n_expiry_minutes; // Dette må til for at validity skal bli riktig
            //UTasShape tas = new UTasShape();
            m_shape.WriteAddressFileLBA(ref logoninfo, new UDATETIME(m_sendinginfo.l_scheddate, m_sendinginfo.l_schedtime), "sms", ref m_project, ref alert, l_refno, n_function, ref adrlbawriter);
            return true;
        }
        public bool AllowResponse {
            set { m_b_allow_response = value; }
            get { return m_b_allow_response; }
        }
        public string ResponseNumber
        {
            set { m_sz_response_number = value; }
            get { return m_sz_response_number; }
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
    
    public class TAS_RESEND : SMS_SENDING
    {
        public long[] l_resend_status;
        
        public TAS_RESEND(long l_refno)
        {
            sendingtype = 's';
        }

        public void setLResend_status(long[] resendStatus)
        {
            l_resend_status = resendStatus;
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

                if (typeof(UTASSENDING) == s.GetType()) // tas sms
                    shape = new UResendTAS();
                else if (getSendingType() == 'v') //voice
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
                setShape(ref shape, s.logoninfo.l_deptpri);
            }
            else if (typeof(UPOLYGONSENDING) == s.GetType())
            {
                UPOLYGONSENDING polygon = (UPOLYGONSENDING)s;
                s.setGroup(UShape.SENDINGTYPE_POLYGON);
                UShape shape = new UPolygon();
                for (int i = 0; i < polygon.polygonpoints.Length; i++)
                {
                    UMapPoint point = (UMapPoint)polygon.polygonpoints.GetValue(i);
                    shape.poly().addPoint(point.lon, point.lat);
                }
                setShape(ref shape, s.logoninfo.l_deptpri);
                s.n_sendingtype = UShape.SENDINGTYPE_POLYGON;
            }
            else if (typeof(UTASSENDING) == s.GetType())
            {
                UTASSENDING tas = (UTASSENDING)s;
                s.setGroup(UShape.SENDINGTYPE_TAS);
                UShape shape = new UTasShape();
                for (int i = 0; i < tas.countrylist.Count; i++)
                {
                    shape.tas().addCountry(tas.countrylist[i]);
                }
                shape.tas().setBounds(tas.mapbounds);
                shape.tas().setSending(ref tas);
                setShape(ref shape, s.logoninfo.l_deptpri);
                s.n_sendingtype = UShape.SENDINGTYPE_TAS;
            }
            else if (typeof(UELLIPSESENDING) == s.GetType())
            {
                UELLIPSESENDING ellipse = (UELLIPSESENDING)s;
                s.setGroup(8);
                UShape shape = new UEllipse();
                shape.ellipse().setCenter(ellipse.ellipse.center.lon, ellipse.ellipse.center.lat);
                shape.ellipse().setExtents(ellipse.ellipse.radius.lon, ellipse.ellipse.radius.lat);
                setShape(ref shape, s.logoninfo.l_deptpri);
                s.n_sendingtype = UShape.SENDINGTYPE_ELLIPSE;

            }
            else if (typeof(UGISSENDING) == s.GetType())
            {
                UGISSENDING gis = (UGISSENDING)s;
                s.setGroup(UShape.SENDINGTYPE_GIS);
                UShape shape = new UGIS();
                for (int i = 0; i < gis.gis.Length; i++)
                {
                    UGisRecord rec = gis.gis[i];
                    shape.gis().addRecord(rec);
                }
                shape.gis().SetBounds(gis.mapbounds);
                setShape(ref shape, s.logoninfo.l_deptpri);
                s.n_sendingtype = UShape.SENDINGTYPE_GIS;
            }
            else if (typeof(UMUNICIPALSENDING) == s.GetType())
            {
                UMUNICIPALSENDING mun = (UMUNICIPALSENDING)s;
                s.setGroup(UShape.SENDINGTYPE_MUNICIPAL);
                UShape shape = new UMunicipalShape();
                for (int i = 0; i < mun.municipals.Count; i++)
                {
                    UMunicipalDef def = mun.municipals[i];
                    shape.municipal().addRecord(def);
                }
                try
                {
                    UAdrDb db = new UAdrDb(s.logoninfo.sz_stdcc, 60, s.logoninfo.l_deptpk);
                    db.GetMunicipalBounds(ref mun);
                }
                catch (Exception e)
                {
                }
                shape.municipal().SetBounds(mun.mapbounds);

                setShape(ref shape, s.logoninfo.l_deptpri);
                s.n_sendingtype = UShape.SENDINGTYPE_MUNICIPAL;

            }
            else if (typeof(UTESTSENDING) == s.GetType())
            {
                UTESTSENDING t = (UTESTSENDING)s;
                t.setGroup(UShape.SENDINGTYPE_TESTSENDING);
                UShape shape = new UTestSending();

                for (int i = 0; i < t.numbers.Count; i++)
                {
                    shape.test().addRecord(t.numbers[i]);
                }
                setShape(ref shape, s.logoninfo.l_deptpri);
                t.n_sendingtype = UShape.SENDINGTYPE_TESTSENDING;
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
        public void setReschedProfile(ref BBRESCHEDPROFILE r, long n_scheddate)
        {
            m_reschedprofile = r;
            m_dynaresched = new BBDYNARESCHED(r, n_scheddate);

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
        public bool setShape(ref UShape s, int n_priority)
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
                bool b_write_addressfile = true;
                if (typeof(UTasShape).Equals(s.GetType()))
                    b_write_addressfile = false;
                if (b_write_addressfile)
                {
                    adrwriter = new AdrfileWriter(l_refno, getSendingType(), b_resend, n_priority);
                    s.WriteAddressFile(ref adrwriter);
                }
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public virtual bool setLBAShape(ref ULOGONINFO logoninfo, ref ULocationBasedAlert s, int n_function)
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
                adrlbawriter = new AdrfileLBAWriter(m_project.sz_projectpk, l_refno, true, SENDCHANNEL.LBA);
                PAALERT nullalert = new PANULLALERT();
                nullalert.n_expiry = (int)s.n_expiry_minutes;
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
                adrlbawriter = new AdrfileLBAWriter(m_project.sz_projectpk, l_refno, true, SENDCHANNEL.LBA);
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
        public bool publishLBAResendFile()
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
                adrlbawriter.publishResend();
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
        public UMapBounds _calcbounds()
        {
            UMapBounds bounds = new UMapBounds();
            if (polygonpoints != null && polygonpoints.Length>0)
            {
                bounds.l_bo = polygonpoints[0].lon;
                bounds.r_bo = polygonpoints[0].lon;
                bounds.u_bo = polygonpoints[0].lat;
                bounds.b_bo = polygonpoints[0].lat;
                for (int i = 1; i < polygonpoints.Length; i++)
                {
                    if (polygonpoints[i].lon < bounds.l_bo)
                        bounds.l_bo = polygonpoints[i].lon;
                    if (polygonpoints[i].lon > bounds.r_bo)
                        bounds.r_bo = polygonpoints[i].lon;
                    if (polygonpoints[i].lat < bounds.b_bo)
                        bounds.b_bo = polygonpoints[i].lat;
                    if (polygonpoints[i].lat > bounds.u_bo)
                        bounds.u_bo = polygonpoints[i].lat;

                }
            }

            mapbounds = bounds;
            return mapbounds;
        }
        protected int counter;
        protected double p1x, p1y, p2x, p2y;
        protected double xinters;
        public bool _point_inside(ref UMapPoint p)
        {
            if (polygonpoints == null)
                return false;
            if (polygonpoints.Length < 3)
                return false;
            counter = 0;
            
            p1x = polygonpoints[0].lon;
            p1y = polygonpoints[0].lat;
            for (int i = 1; i <= polygonpoints.Length; i++)
            {
                p2x = polygonpoints[i % polygonpoints.Length].lon;
                p2y = polygonpoints[i % polygonpoints.Length].lat;
                if (p.lat > Math.Min(p1y, p2y))
                {
                    if (p.lat <= Math.Max(p1y, p2y))
                    {
                        if (p.lon <= Math.Max(p1x, p2x))
                        {
                            if (p1y != p2y)
                            {
                                xinters = (p.lat - p1y) * (p2x - p1x) / (p2y - p1y) + p1x;
                                if (p1x == p2x || p.lon <= xinters)
                                    counter++;
                            }
                        }
                    }
                }
                p1x = p2x;
                p1y = p2y;
            }
            if (counter % 2 == 0)
                return false;
            return true;

        }

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
    public class UTESTSENDING : UMAPSENDING
    {
        public List<string> numbers;
    }
    public class UMUNICIPALSENDING : UMAPSENDING
    {
        public List<UMunicipalDef> municipals;
    }
    public class UTASSENDING : UMAPSENDING
    {
        public List<ULBACOUNTRY> countrylist;
        public int n_requesttype; //for LBA/TAS call
        public bool b_allow_response;
        public String sz_response_number;
    }
}
