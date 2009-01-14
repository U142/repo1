﻿using System;
using System.Collections;
using com.ums.UmsCommon;
using com.ums.UmsFile;

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
        public ULocationBasedAlert lba() { return (ULocationBasedAlert)this; }
        public abstract bool WriteAddressFile(ref AdrfileWriter w);
        public abstract bool WriteAddressFileGUI(ref AdrfileGUIWriter w);
        public virtual bool WriteAddressFileLBA(ref ULOGONINFO logoninfo, UDATETIME sched, String sz_type, ref BBPROJECT project, ref PAALERT alert, long n_parentrefno, int n_function, ref AdrfileLBAWriter w)
        {
            w.close();
            //throw new NotImplementedException("WriteAddressFileLBA");
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
            String sz_name;
            String sz_cb_oadc;
            String sz_otoa;
            String sz_text;
            ArrayList m_ccodes = new ArrayList();
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
            String ccode;
            public LBACCode(String s)
            {
                ccode = s;
            }
            public String getCCode() { return ccode; }
        }
        protected bool m_b_isvalid;
        //public String sz_area;
        //public String sz_id;
        public String l_alertpk;
        protected ArrayList m_languages = new ArrayList();
        public void setValid() { m_b_isvalid = true; }
        public bool getValid() { return m_b_isvalid; }

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
            /*xmlwriter.insertStartElement("cellbroadcast");
            xmlwriter.insertAttribute("sz_area", sz_area);
            xmlwriter.insertAttribute("sz_id", sz_id);
            xmlwriter.insertAttribute("l_comppk", logoninfo.l_comppk.ToString());
            xmlwriter.insertAttribute("l_deptpk", logoninfo.l_deptpk.ToString());
            xmlwriter.insertAttribute("l_msgclass", "1");
            xmlwriter.insertAttribute("l_pri", logoninfo.l_deptpri.ToString());
            xmlwriter.insertAttribute("l_localsched", "1");
            xmlwriter.insertAttribute("l_validitytime", "259200");
            xmlwriter.insertAttribute("l_schedtime", sched.sz_date + sched.sz_time);
            xmlwriter.insertAttribute("l_priserver", logoninfo.l_priserver.ToString());
            xmlwriter.insertAttribute("l_altservers", logoninfo.l_altservers.ToString());
            xmlwriter.insertAttribute("sz_tarifclass", "");
            xmlwriter.insertAttribute("sz_stdcc", logoninfo.sz_stdcc);
            //xmlwriter.insertAttribute("f_simulation", (f_simulation ? "1" : "0"));
            xmlwriter.insertAttribute("f_simulation", (n_function == UCommon.USENDING_LIVE ? "0" : "1"));
            xmlwriter.insertAttribute("l_version", "2");
                xmlwriter.insertStartElement("transaction-id");
                xmlwriter.insertElementString(String.Format("{0}.{1}", project.sz_projectpk, n_parentrefno));
                xmlwriter.insertEndElement(); //transaction-id
                xmlwriter.insertStartElement("languages");
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
                xmlwriter.insertEndElement(); //languages

            xmlwriter.insertEndElement(); //cellbroadcast

            xmlwriter.insertEndDocument();
            xmlwriter.finalize();*/

            xmlwriter.insertStartElement("LBA");
            xmlwriter.insertAttribute("operation", "SendArea");
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk);
            xmlwriter.insertAttribute("l_refno", n_parentrefno.ToString());
            xmlwriter.insertAttribute("sz_areaid", alert.sz_areaid.ToString());
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

    public class UEllipse : UShape
    {
        public float lon, lat;
        public float x, y;
        public UEllipse()
            : base()
        {
        }
        public void setCenter(float lon, float lat)
        {
            this.lon = lon;
            this.lat = lat;
        }
        public void setExtents(float x, float y)
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
                float l_bo, r_bo, u_bo, b_bo;
                float f_epsilon = 0.001f;
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
        public void addPoint(float lon, float lat)
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
        float lon, lat;
        public float getLon() { return lon; }
        public float getLat() { return lat; }

        public UPolypoint(float lon, float lat)
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
        public String getSendingTypeText()
        {
            switch (n_sendingtype)
            {
                case 0:
                    return "Sending type not set";
                case 3:
                    return "Polygon";
                case 8:
                    return "Ellipse";
                default:
                    return "Unknown sending type";
            }
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
        public bool hasValidAreaID()
        {
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


    public struct PAS_SENDING
    {
        public long l_refno;
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
    //public 
        public bool hasLBA() {
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
                adrguiwriter = new AdrfileGUIWriter(l_refno);
                s.WriteAddressFileGUI(ref adrguiwriter);
                adrwriter = new AdrfileWriter(l_refno);
                s.WriteAddressFile(ref adrwriter);
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

}
