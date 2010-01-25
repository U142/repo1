using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Collections;
using com.ums.UmsCommon;
using com.ums.UmsFile;
using System.Xml;
using com.ums.PAS.Address;

namespace com.ums.UmsParm
{
    public class UXmlAlert : UXml
    {
        protected ULOGONINFO m_logon;
        public void SetLogonInfo(ref ULOGONINFO logon) { m_logon = logon; }
        protected String l_alertpk;
        protected URGBA m_rgba;
        public URGBA getRGBA() { return m_rgba; }
        protected UShape m_shape;
        protected UShape m_lba_shape;
        public UShape getShape() { return m_shape; }
        public UShape getLBAShape() { return m_lba_shape; }
        protected int n_sendingtype;
        public int getSendingType() { return n_sendingtype; }
        protected PercentProgress.SetPercentDelegate percentDelegate = null;
        public void setPercentDelegate(PercentProgress.SetPercentDelegate percent)
        {
            percentDelegate = percent;
        
        }
        protected ProgressJobType jobType = ProgressJobType.GEMINI_IMPORT_STREETID;
        public void setJobType(ProgressJobType jobType)
        {
            this.jobType = jobType;
        }

        public UXmlAlert(string path, string file) : base(path, file)
        {
            n_sendingtype = -1;
        }
        public bool load(string s_localid)
        {
            try
            {
                createLocalVersion(s_localid); //we need to make a new xml-file first
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw new UFileCopyException(e.Message);
            }
            try
            {
                parse();
                return true;
            }
            catch (Exception e)
            {
                throw new UXmlParseException(e.Message);
            }
        }

        public override bool DeleteOperation()
        {
            try
            {
                return base.DeleteOperation();                
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*
         * Create a sending file in temp path based on the content from original predefined file
         */
        protected bool createLocalVersion(String s_localid)
        {
            UFile dest = new UFile(UCommon.UPATHS.sz_path_temp, String.Format("temp-{0}.xml", s_localid));
            try
            {
                //File.Copy(full(), dest.full());
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw new UFileCopyException(e.Message);
            }

            //this = dest;

            TextWriter tw = null;
            try
            {
                tw = File.CreateText(dest.full()); //local destination file
            }
            catch (Exception e)
            {
                throw e;
            }
            try
            {
                //TextReader tr = File.OpenText(full()); //remote source file
                //StreamReader tr = File.OpenText(full());
                TextReader tr = new StreamReader(full(), new UTF8Encoding(false)); //Encoding.GetEncoding("iso-8859-1")); //new FileStream(full(),FileMode.Open),Encoding.GetEncoding("iso-8859-1"));
                String file_content = tr.ReadToEnd();
                tw.WriteLine("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
                tw.WriteLine("<sending>");
                tw.Write(file_content);
                tw.WriteLine("</sending>");
                tw.Flush();
                //tw.Close();
                tr.Close();

                //make new local file references and ignore the original file
            }
            catch (Exception e)
            {
                ULog.error(0, "l_alertpk=" + l_alertpk, e.Message);
                tw.Close();
                dest.DeleteOperation();
                throw e;
            }
            finally
            {
                sz_filename = dest.file();
                sz_path = dest.path();
            }
            tw.Close();
            return true;
        }


        protected bool parse()
        {
            try
            {
                read();
            }
            catch(Exception e)
            {
                throw e;
            }

            XmlElement el_sending = doc.DocumentElement; //GetElementById("sending");
            try
            {
                XmlNode el_subnode;
                for (int i = 0; i < el_sending.ChildNodes.Count; i++)
                {
                    el_subnode = el_sending.ChildNodes[i];
                    if (el_subnode.Name.Equals("alertpolygon"))
                    {
                        parsePolygon(el_subnode);
                    }
                    else if (el_subnode.Name.Equals("alertstreetid"))
                    {
                        parseStreetid(el_subnode);
                    }
                    else if (el_subnode.Name.Equals("alertellipse"))
                    {
                        parseEllipse(el_subnode);
                        //not implemented
                        //ULog.error("Error - alertellipse not yet implemented");
                        //throw new USendingTypeNotSupportedException("Error - shape [alertellipse] not yet implemented");
                    }
                    else if (el_subnode.Name.Equals("cellbroadcast"))
                    {
                        parseLBA(el_subnode);
                    }
                    else
                    {
                        throw new USendingTypeNotSupportedException(String.Format("Error - shape type not valid [{0}]", el_subnode.Name));
                    }
                }
                
            }
            catch (Exception e)
            {
                ULog.error(0, full(), e.Message);
                throw e;
            }
            return true;
        }

        protected bool parseStreetid(XmlNode node)
        {
            m_shape = new UGIS();
            String col_a, col_b, col_r, col_g, l_alertpk;
            try
            {
                col_a = node.Attributes["col_a"].Value;
                col_b = node.Attributes["col_b"].Value;
                col_r = node.Attributes["col_r"].Value;
                col_g = node.Attributes["col_g"].Value;
                l_alertpk = node.Attributes["l_alertpk"].Value;
                XmlNodeList nl = node.SelectNodes("line");
                String municipal, streetid, houseno, letter, namefilter1, namefilter2;
                List<UGisImportResultLine> filelines = new List<UGisImportResultLine>();
                for (int i = 0; i < nl.Count; i++)
                {
                    XmlNode n = nl.Item(i);
                    try
                    {
                        municipal = n.Attributes["municipal"].Value;
                        streetid = n.Attributes["streetid"].Value;
                        houseno = n.Attributes["houseno"].Value;
                        letter = n.Attributes["letter"].Value;
                        namefilter1 = n.Attributes["namefilter1"].Value;
                        namefilter2 = n.Attributes["namefilter2"].Value;
                        UGisImportResultLine res = new UGisImportResultLine();
                        res.n_linenumber = (i + 1);
                        res.municipalid = municipal;
                        res.streetid = streetid;
                        res.houseno = houseno;
                        res.letter = letter;
                        res.namefilter1 = namefilter1;
                        res.namefilter2 = namefilter2;
                        res.b_isvalid = true;
                        res.finalize();
                        filelines.Add(res);
                        /*UGisRecord r = new UGisRecord();
                        r.id = "";
                        m_shape.gis().addRecord(r);*/
                    }
                    catch (Exception e)
                    {

                    }
                }
                try
                {
                    m_shape.gis().SetLineCount(filelines.Count);
                    UGisImportParamsByStreetId search = new UGisImportParamsByStreetId();
                    search.SKIPLINES = 0;
                    bool adhoc_percentdelegate = false;
                    if (percentDelegate == null)
                    {
                        adhoc_percentdelegate = true;
                        percentDelegate = PercentProgress.newDelegate();
                        percentDelegate(ref m_logon, jobType, new PercentResult());
                    }
                    UGisImportResultsByStreetId res = (UGisImportResultsByStreetId)new UGisImportLookup(ref search, ref m_logon, percentDelegate, jobType).SearchDatabase(ref filelines, true);
                    UMapBounds bounds = new UMapBounds();
                    bounds.reset();
                    bool setbounds = false;
                    if (res != null && res.list!=null)
                    {
                        for (int line = 0; line < res.list.Count; line++)
                        {
                            if (res.list[line].list != null && res.list[line].list.list_basics != null)
                            {
                                for(int inhab = 0; inhab < res.list[line].list.list_basics.Count; inhab++)
                                {
                                    UAddressBasics adr = res.list[line].list.list_basics[inhab];
                                    UGisRecord gisrecord = new UGisRecord();
                                    if (adr.kondmid.Length > 0)
                                    {
                                        gisrecord.id = long.Parse(adr.kondmid);
                                        m_shape.gis().addRecord(gisrecord);
                                        
                                        if (adr.lon != 0 && adr.lat != 0)
                                        {
                                            if (adr.lat < bounds.l_bo)
                                                bounds.l_bo = adr.lat;
                                            if (adr.lat > bounds.r_bo)
                                                bounds.r_bo = adr.lat;
                                            if (adr.lon < bounds.b_bo)
                                                bounds.b_bo = adr.lon;
                                            if (adr.lon > bounds.u_bo)
                                                bounds.u_bo = adr.lon;
                                            setbounds = true;
                                        }
                                    }
                                }

                            }
                        }
                        res.finalize();
                    }
                    if(setbounds)
                        m_shape.gis().SetBounds(bounds);
                    if(adhoc_percentdelegate)
                        PercentProgress.DeleteJob(ref m_logon, jobType);

                }
                catch (Exception e)
                {

                }
            }
            catch (Exception e)
            {
                String s = "parseStreetid() Error parsing alert file " + full();
                ULog.error(0, s, e.Message);
                throw new UParseStreetidException(s + ". " + e.Message);
            }
            n_sendingtype = UShape.SENDINGTYPE_GIS;
            return true;
        }

        protected bool parseEllipse(XmlNode node)
        {
            m_shape = new UEllipse();
            String col_a, col_b, col_r, col_g, l_alertpk, center_x, center_y, corner_x, corner_y;
            try
            {
                col_a = node.Attributes["col_a"].Value;
                col_b = node.Attributes["col_b"].Value;
                col_r = node.Attributes["col_r"].Value;
                col_g = node.Attributes["col_g"].Value;
                l_alertpk = node.Attributes["l_alertpk"].Value;
                center_x = node.Attributes["centerx"].Value;
                center_y = node.Attributes["centery"].Value;
                corner_x = node.Attributes["cornerx"].Value;
                corner_y = node.Attributes["cornery"].Value;
                float f_center_x, f_center_y, f_corner_x, f_corner_y, f_extents_x, f_extents_y;
                f_center_x = float.Parse(center_x, UCommon.UGlobalizationInfo);
                f_center_y = float.Parse(center_y, UCommon.UGlobalizationInfo);
                f_corner_x = float.Parse(corner_x, UCommon.UGlobalizationInfo);
                f_corner_y = float.Parse(corner_y, UCommon.UGlobalizationInfo);
                f_extents_x = Math.Abs(f_corner_x - f_center_x);
                f_extents_y = Math.Abs(f_corner_y - f_center_y);

                m_shape.ellipse().setCenter(f_center_x, f_center_y);
                m_shape.ellipse().setExtents(f_extents_x, f_extents_y);
            }
            catch (Exception e)
            {
                String s = "parseEllipse() Error parsing alert file " + full();
                ULog.error(0, s, e.Message);
                throw new UParseEllipseException(s + ". " + e.Message);
            }
            n_sendingtype = UShape.SENDINGTYPE_ELLIPSE;
            return true;

        }

        protected bool parsePolygon(XmlNode node)
        {
            m_shape = new UPolygon();
            XmlNodeList nl = node.SelectNodes("polypoint");
            String lon, lat;
            for (int i = 0; i < nl.Count; i++)
            {
                XmlNode n = nl.Item(i);
                try
                {
                    lon = n.Attributes["xcord"].Value;
                    lat = n.Attributes["ycord"].Value;
                    m_shape.poly().addPoint(float.Parse(lon, UCommon.UGlobalizationInfo), float.Parse(lat, UCommon.UGlobalizationInfo));
                }
                catch (Exception e)
                {
                    String s = "parsePolygon() Error parsing alert file " + full();
                    ULog.error(0, s, e.Message);
                    throw new UParsePolygonException(s + "\n" + e.Message);
                }

            }
            n_sendingtype = UShape.SENDINGTYPE_POLYGON;
            return true;
        }


        protected bool parseLBA(XmlNode node)
        {
            m_lba_shape = new ULocationBasedAlert();
            try
            {
                //m_lba_shape.lba().sz_area   = node.Attributes["sz_area"].Value;
                //m_lba_shape.lba().sz_id     = node.Attributes["sz_id"].Value;
                m_lba_shape.lba().l_alertpk = node.Attributes["l_alertpk"].Value;
                XmlNodeList lbalist = node.SelectNodes("message");
                int n_langcount = 0;
                for (int i = 0; i < lbalist.Count; i++)
                {
                    XmlNode lba = lbalist.Item(i);
                    LBALanguage lang;
                    try
                    {
                        lang = m_lba_shape.lba().addLanguage(lba.Attributes["sz_lang"].Value, lba.Attributes["sz_cb_oadc"].Value, "1", lba.Attributes["sz_text"].Value);
                        n_langcount++;
                    }
                    catch (Exception e)
                    {
                        ULog.error(0, "parseLBA() Error parsing message nodes", e.Message);
                        throw e;
                    }
                    XmlNodeList ccodelist = lba.SelectNodes("ccode");
                    for (int c = 0; c < ccodelist.Count; c++)
                    {
                        XmlNode ccode = ccodelist.Item(c);
                        try
                        {
                            lang.AddCCode(ccode.FirstChild.Value);
                        }
                        catch (Exception e)
                        {
                            ULog.error(0, "parseLBA() Error parsing ccode nodes", e.Message);
                            throw e;
                        }
                    }

                }
                if (n_langcount > 0)
                    m_lba_shape.lba().setValid();
            }
            catch (Exception e)
            {
                ULog.error(0, "parseLBA() Error parsing Location Based Alert " + full(), e.Message);
                throw e;
            }
            return true;
        }
    }
}
