using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.Xml;

using com.ums.UmsCommon;
using com.ums.UmsFile;
using System.Security.Cryptography;
using System.Xml.Serialization;
using System.IO;
using System.Xml.Schema;


namespace com.ums.UmsParm.compatability
{

    [XmlType(Namespace = "http://ums.no/ws/common/parm/compatability")]
    [XmlInclude(typeof(UPolypoint))]
    public class UPolygon : UShape
    {
        public UBoundingRect CalcBounds()
        {
            UBoundingRect rect = new UBoundingRect(180, -180, -90, 90);
            foreach (UPolypoint p in this.getPolygon())
            {
                if (p.lat > rect._top)
                    rect._top = p.lat;
                if (p.lat < rect._bottom)
                    rect._bottom = p.lat;
                if (p.lon < rect._left)
                    rect._left = p.lon;
                if (p.lon > rect._right)
                    rect._right = p.lon;
            }
            return rect;
        }

        public bool PointInside(double lon, double lat)
        {
            UMapPoint mp = new UMapPoint();
            mp.lon = lon;
            mp.lat = lat;
            return PointInside(ref mp);
        }

        public bool PointInside(ref UMapPoint p)
        {
            if (getPolygon() == null)
                return false;
            if (getPolygon().Count < 3)
                return false;
            int counter = 0;
            double p1x, p1y, p2x, p2y;
            double xinters;

            p1x = getPolygon()[0].lon;
            p1y = getPolygon()[0].lat;
            for (int i = 1; i <= getPolygon().Count; i++)
            {
                p2x = getPolygon()[i % getPolygon().Count].lon;
                p2y = getPolygon()[i % getPolygon().Count].lat;
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


        public static UmsParm.UPolygon Deserialize(String xml)
        {
            UPolygon cob = new UPolygon();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                UmsParm.UPolygon ret = new UmsParm.UPolygon();
                cob = (UPolygon)serializer.Deserialize(reader);
                ret.m_array_polypoints = cob.m_array_polypoints;
                return ret;
            }
            catch (Exception e)
            {
                String s = e.Message;
                throw;
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
        
        [XmlElement("polypoint", Form = XmlSchemaForm.Unqualified)]
        public List<UPolypoint> m_array_polypoints;
        public List<UPolypoint> getPolygon() { return m_array_polypoints; }
        public long getSize() { return m_array_polypoints.Count; }
        public UPolypoint getPoint(int i) { return (UPolypoint)m_array_polypoints[i]; }


        public UPolygon()
            : base()
        {
            m_array_polypoints = new List<UPolypoint>();
        }

        [XmlIgnore]
        Hashtable hash_points = null;

        public Hashtable getHashPoints()
        {
            return hash_points;
        }

        public void createPointHash()
        {
            hash_points = new Hashtable();
            for (int i = 0; i < m_array_polypoints.Count; ++i)
            {
                UPolypoint point = m_array_polypoints[i];
                String key = _generatePointHashKey(ref point);
                hash_points.Add(key, m_array_polypoints);
            }
        }
        protected String _generatePointHashKey(ref UPolypoint p)
        {
            return p.lon + "_" + p.lat;
        }

        public List<UPolygon> findPolysWithSharedBorder(ref List<UPolygon> in_polygons)
        {
            if (getHashPoints() == null)
                createPointHash();
            List<UPolygon> ret = new List<UPolygon>();
            //traverse list of in polygons
            for (int i = 0; i < in_polygons.Count; ++i)
            {
                UPolygon p = in_polygons[i];
                //check that point-hash is made for dest poly
                if (p.getHashPoints() == null)
                    p.createPointHash();

                //traverse polypoints in destination polygon
                IDictionaryEnumerator arr_in_points = p.getHashPoints().GetEnumerator();
                while (arr_in_points.MoveNext())
                {
                    String in_key = (String)arr_in_points.Key;
                    if (getHashPoints().ContainsKey(in_key))
                    {
                        //we have a match on at least one polypoint
                        ret.Add(p);
                        break;
                    }
                }

            }
            return ret;
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
            catch (Exception)
            {
                throw;
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



    [XmlType(Namespace = "http://ums.no/ws/common/parm/compatability")]
    public class UMunicipalDef
    {
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public String sz_municipalid;
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public String sz_municipalname;
    }

    [XmlType(Namespace = "http://ums.no/ws/common/parm/compatability")]
    [XmlInclude(typeof(UMapBounds))]
    [XmlInclude(typeof(UmsParm.compatability.UMunicipalDef))]
    public class UMunicipalShape : UShape
    {
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public List<UMunicipalDef> m_municipals = new List<UMunicipalDef>();


        
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public UMapBounds m_bounds = null;


        public List<UMunicipalDef> GetMunicipals() { return m_municipals; }
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
                    if (m_municipals[i].sz_municipalid.Length > 0)
                        w.writeline("/MUNICIPALID=" + m_municipals[i].sz_municipalid);
                }
            }
            catch (Exception)
            {
                throw;
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
                    for (int i = 0; i < m_municipals.Count; i++)
                    {
                        w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}\t{1}", m_municipals[i].sz_municipalid, m_municipals[i].sz_municipalname));
                    }

                }
                //w.writeline(String.Format(UCommon.UGlobalizationInfo, "{0}", l_bo));
            }
            catch (Exception)
            {
                throw;
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

        public static UmsParm.UMunicipalShape Deserialize(String xml)
        {
            UmsParm.compatability.UMunicipalShape cob = new UmsParm.compatability.UMunicipalShape();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (UmsParm.compatability.UMunicipalShape)serializer.Deserialize(reader);
                UmsParm.UMunicipalShape ret = new UmsParm.UMunicipalShape();
                //ret.m_municipals = cob.m_municipals;
                ret.m_bounds = cob.m_bounds;
                return ret;
            }
            catch (Exception)
            {
                throw;
            }
        }

    }

    [XmlType(Namespace = "http://ums.no/ws/common/compatability")]
    public class UGisRecord
    {
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public long id; //kon_dmid from adr-db
    }


    [XmlType(Namespace = "http://ums.no/ws/common/parm/compatability")]
    [XmlInclude(typeof(UGisRecord))]
    [XmlInclude(typeof(UMapBounds))]
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


        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public List<UGisRecord> m_gis = new List<UGisRecord>();

        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public UMapBounds m_bounds = null;

        
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
            catch (Exception)
            {
                throw;
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
                    l_bo = m_bounds.l_bo - f_epsilon;
                    r_bo = m_bounds.r_bo + f_epsilon;
                    u_bo = m_bounds.u_bo + f_epsilon;
                    b_bo = m_bounds.b_bo - f_epsilon;
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
            catch (Exception)
            {
                throw;
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

        public static UmsParm.UGIS Deserialize(String xml)
        {
            UmsParm.compatability.UGIS cob = new UmsParm.compatability.UGIS();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (UmsParm.compatability.UGIS)serializer.Deserialize(reader);
                UmsParm.UGIS ret = new UmsParm.UGIS();
                ret.m_gis = null;
                ret.m_bounds = cob.m_bounds;
                return ret;
            }
            catch (Exception)
            {
                throw;
            }
        }
    }


    [XmlType(Namespace = "http://ums.no/ws/common/parm/compatability")]
    public class UEllipse : UShape
    {
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public double lon;
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public double lat;
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public double x;
        [XmlElement(Form = XmlSchemaForm.Unqualified)]
        public double y;
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
            catch (Exception e)
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
            catch (Exception)
            {
                throw;
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
        public static UmsParm.UEllipse Deserialize(String xml)
        {
            UmsParm.compatability.UEllipse cob = new UmsParm.compatability.UEllipse();
            StringReader read = new StringReader(xml);
            XmlSerializer serializer = new XmlSerializer(cob.GetType());
            XmlReader reader = new XmlTextReader(read);
            try
            {
                cob = (UmsParm.compatability.UEllipse)serializer.Deserialize(reader);
                UmsParm.UEllipse ret = new UmsParm.UEllipse();
                ret.x = cob.x;
                ret.y = cob.y;
                ret.lon = cob.lon;
                ret.lat = cob.lat;
                return ret;
            }
            catch (Exception e)
            {
                String sz = e.Message;
                throw;
            }
        }
    }
}
