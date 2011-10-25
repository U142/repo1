using System;
using System.Collections;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;
using System.IO;
using System.IO.Compression;
using System.Xml;
using System.Text;
using System.Data.Odbc;

using com.ums.UmsCommon;
using com.ums.PARM;
using com.ums.UmsParm;
using com.ums.UmsFile;
using com.ums.UmsDbLib;
using com.ums.ZipLib;
using System.Collections.Generic;

namespace com.ums.ws.parm
{
    /// <summary>
    /// Summary description for PARM
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/parm/admin/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    
    public class ParmAdmin : System.Web.Services.WebService
    {
        private ULOGONINFO m_logon;
        private PASUmsDb db;
        private USimpleXmlWriter outxml;
        private USimpleXmlWriter outpolyxml;

        private String sz_timestamp;
        private String sz_newtimestamp;
        private PercentResult m_res = new PercentResult();
        private PercentProgress.SetPercentDelegate m_percentdelegate;

        public class UPARESULT
        {
            public long pk;
        }
        public class UPAEVENTRESULT : UPARESULT
        {
        }

        public class UPAALERTRESTULT : UPARESULT
        {
        }
        public class UPAOBJECTRESULT : UPARESULT
        {
        }

        protected bool createOutXml()
        {
            try
            {
                String encoding = "UTF-8";
                outxml = new USimpleXmlWriter(encoding);
                outxml.insertStartDocument();
                outxml.insertStartElement("PAROOT");
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        
        public class CB_USER_REGION_RESPONSE
        {
            public List<PAOBJECT> regionlist;
            public UBBUSER user;
        }


        [WebMethod]
        public UPAEVENTRESULT ExecEventUpdate(ULOGONINFO logon, PAEVENT ev)
        {
            try
            {
                UPAEVENTRESULT ret = new UPAEVENTRESULT();
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                createOutXml();
                ret.pk = HandleEventUpdate(ev.parmop, ref logon, ref ev);
                outxml.insertEndElement(); //PAROOT
                outxml.insertEndDocument();
                outxml.finalize();
                db.close();
                return ret;

            }
            catch (Exception)
            {
                throw;
            }
        }

        [WebMethod]
        public UPAOBJECTRESULT ExecObjectUpdate(ULOGONINFO logon, PAOBJECT obj)
        {
            try
            {
                UPAOBJECTRESULT ret = new UPAOBJECTRESULT();
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                createOutXml();
                ret.pk = HandleObjectUpdate(obj.parmop, ref logon, ref obj);
                outxml.insertEndElement(); //PAROOT
                outxml.insertEndDocument();
                outxml.finalize();
                db.close();
                return ret;
            }
            catch (Exception)
            {
                throw;
            }
        }

        [WebMethod]
        public UPAOBJECTRESULT ExecPAShapeUpdate(ULOGONINFO logon, PAOBJECT obj, PASHAPETYPES type)
        {
            try
            {
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);

                UPAOBJECTRESULT ret = new UPAOBJECTRESULT();
                
                ret.pk = HandlePAObjectUpdate(obj.parmop, ref logon, ref obj, type);
                createOutXml();
                //bool changed = write_server_shape(ref obj, type);

                outxml.insertEndElement(); //PAROOT
                outxml.insertEndDocument();
                outxml.finalize();
                db.close();
                return ret;
            }
            catch (Exception)
            {
                throw;
            }
        }

        [WebMethod]
        public int Defines(UEllipse e, UPolygon p, UGeminiStreet s, ULocationBasedAlert l)
        {
            return 0;
        }


        [WebMethod]
        public UPAALERTRESTULT ExecAlertUpdate(ULOGONINFO logon, PAALERT a)
        {
            try
            {
                UPAALERTRESTULT ret = new UPAALERTRESTULT();
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                createOutXml();
                ret.pk = HandleAlertUpdate(a.parmop, ref logon, ref a);
                outxml.insertEndElement(); //PAROOT
                outxml.insertEndDocument();
                outxml.finalize();
                db.close();
                return ret;
            }
            catch (Exception)
            {
                throw;
            }
        }


        [WebMethod]
        public byte[] UpdateParm(byte[] zipfile /*zip-file that contains two files*/, ULOGONINFO logoninfo,
                                String sz_filename /*PARM detail file*/, String sz_polyfilename /*shapes to PARM detail*/)
        {
            m_percentdelegate = PercentProgress.newDelegate();
            m_percentdelegate(ref logoninfo, ProgressJobType.PARM_UPDATE, m_res);

            m_logon = logoninfo;
            String sessid;
            if (Session == null)
                sessid = Guid.NewGuid().ToString();
            else
                sessid = Session.SessionID;
            //log on
            try
            {
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                return null;
            }

            try
            {
                bool b = db.CheckLogon(ref logoninfo, true); //l_userpk, l_comppk, l_deptpk, sz_userid, sz_compid, sz_deptid, sz_password);
                if (!b)
                    return null;
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                return null;
            }

            //unzip the two files from in-file
            Stream parmStream = null;
            Stream adrStream = null;
            using (var ms = new MemoryStream(zipfile))
            {
                using (var zis = new Ionic.Zip.ZipInputStream(ms))
                {
                    Ionic.Zip.ZipEntry entry;
                    while ((entry = zis.GetNextEntry()) != null)
                    {
                        var content = new byte[entry.UncompressedSize];
                        zis.Read(content, 0, content.Length);
                        if (entry.FileName.Equals(sz_filename))
                        {
                            parmStream = new MemoryStream(content);
                        }
                        else if (entry.FileName.Equals(sz_polyfilename))
                        {
                            adrStream = new MemoryStream(content);
                        }
                    }
                }
            }

            if (parmStream == null)
            {
                throw new ArgumentException("Could not find file " + sz_filename + " in zipfile");
            }
            if (adrStream == null)
            {
                throw new ArgumentException("Could not find file " + sz_polyfilename + " in zipfile");
            }

            m_res.n_percent = 20;
            m_percentdelegate(ref logoninfo, ProgressJobType.PARM_UPDATE, m_res);


            //**************** delete the original zip file ******************
            
            pks_alert[0] = new ArrayList();
            pks_alert[1] = new ArrayList();
            pks_event[0] = new ArrayList();
            pks_event[1] = new ArrayList();
            pks_object[0] = new ArrayList();
            pks_object[1] = new ArrayList();

            //create return file
            String encoding = "UTF-8";
            /*outxml = new USimpleXmlWriter(encoding);
            outxml.insertStartDocument();
            outxml.insertStartElement("PAROOT");*/
            createOutXml();

            //sz_newtimestamp = UCommon.UGetFullDateTimeNow().ToString();
            OdbcDataReader rs = null;
            try
            {
                String szSQL = "select datepart(YY, getdate())*10000000000 + datepart(mm, getdate())*100000000 + datepart(dd, getdate()) * 1000000 + datepart(HH, getdate())*10000 + datepart(MI,getdate())*100 + datepart(SS, getdate())";
                rs = db.ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                if (rs.Read())
                {
                    sz_newtimestamp = rs.GetString(0);
                }
                else
                    sz_newtimestamp = "0";
                rs.Close();
            }
            catch (Exception)
            {
                sz_newtimestamp = "0";
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            

            //create poly file
            outpolyxml = new USimpleXmlWriter(encoding);
            outpolyxml.insertStartDocument();
            outpolyxml.insertStartElement("POLYGONS");


            //run updates
            try
            {
                ParsePARM(parmStream, ref db);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }

            //get address files


            try
            {
                WriteShapeUpdates(adrStream);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }
 
            //get updates
            try
            {
                WriteUpdates();
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }

            try //CLOSE UPDATE FILE (OUT)
            {
                outxml.insertEndElement(); //PAROOT
                outxml.insertEndDocument();

                outxml.finalize();
            }
            catch (Exception e)
            {
                String err = e.Message;
                ULog.error(e.Message);
            }

            try //CLOSE POLYUPDATE FILE (OUT)
            {
                outpolyxml.insertEndElement(); //POLYGONS
                outpolyxml.insertEndDocument();
                outpolyxml.finalize();
            }
            catch (Exception e)
            {
                String err = e.Message;
                ULog.error(e.Message);
                throw;
            }

            db.close();
            PercentProgress.DeleteJob(ref m_logon, ProgressJobType.PARM_UPDATE);

            try
            {
                return UGZipLib.getZipped(outxml.getXml().Replace("&#x1A", "?"));
            }
            catch (Exception e)
            {
                ULog.error(0, "Error returning PARM ZIP file", e.Message);
                throw;
            }
        }

        private ArrayList[] pks_alert = new ArrayList[2]; //idx 0 contains temp-pks, idx 1 contains new values
        private ArrayList[] pks_event = new ArrayList[2];
        private ArrayList[] pks_object = new ArrayList[2];


        /*get all updated shapes from predefined-areas and make a xml file for return (outpolyxml)*/
        private bool WritePolygonToFile(String l_polypk, String sz_objecttype, ref USimpleXmlWriter outfile)
        {
            String prefix = "";
            if (sz_objecttype.Equals("paobject"))
                prefix = "o";
            else if (sz_objecttype.Equals("paalert"))
                prefix = "a";
            else
                return false;
            //open shape from predefined-areas
            try
            {
                FileInfo t = new FileInfo(UCommon.UPATHS.sz_path_predefined_areas + "\\" + prefix + l_polypk + ".xml");
                if (t.Exists)
                {
                    StreamReader reader = t.OpenText();
                    string s = reader.ReadToEnd();
                    reader.Close();

                    outfile.insertText(s);
                }
            }
            catch (Exception)
            {
                return false;
            }

            return true;
        }

        /*
         * Write new shapes in predefined-areas
         * The source is sent from PAS client
         * Open XML file and parse. Then make new text-files to be put in predefined-areas
         */
        private int WriteShapeUpdates(Stream adrStream)
        {
            int counter = 0;
            XmlDocument doc = new XmlDocument();
            try
            {
                doc.Load(adrStream);
            }
            catch (Exception e)
            {
                throw;
            }
            try
            {
                XmlNode root = doc.DocumentElement;
                XmlNodeList Children = root.ChildNodes;
                for (int i = 0; i < Children.Count; i++)
                {
                    String l_pk = "-1";
                    FileMode fmode = FileMode.Create;
                    XmlNode node = Children.Item(i);
                    String sz_nodetype = node.Name.ToLower();
                    if (sz_nodetype.Equals("objectpolygon"))
                    {
                        l_pk = NodeGetObjectPk(node);
                    }
                    else if (sz_nodetype.Equals("alertpolygon"))
                    {
                        l_pk = NodeGetAlertPk(node);
                        if ( (db.GetAlertAddresstypes(l_pk) & (int)ADRTYPES.LBA_TEXT) == (int)ADRTYPES.LBA_TEXT)
                        {
                            CreateLBAFile(sz_nodetype, l_pk.Substring(1), node);
                        }
                    }
                    else if (sz_nodetype.Equals("objectellipse"))
                    {
                        l_pk = NodeGetObjectPk(node);
                    }
                    else if (sz_nodetype.Equals("alertellipse"))
                    {
                        l_pk = NodeGetAlertPk(node);
                        if ((db.GetAlertAddresstypes(l_pk) & (int)ADRTYPES.LBA_TEXT) == (int)ADRTYPES.LBA_TEXT)
                        {
                            CreateLBAFile(sz_nodetype, l_pk.Substring(1), node);
                        }
                    }
                    else if (sz_nodetype.Equals("alertstreetid"))
                    {
                        l_pk = NodeGetAlertPk(node);
                    }
                    else if (sz_nodetype.Equals("alertgbno"))
                    {
                        l_pk = NodeGetAlertPk(node);
                    }
                    else if (sz_nodetype.Equals("cellbroadcast"))
                    {
                        l_pk = NodeGetAlertPk(node);
                        //assume that a polygon/ellipse is already updated, just append the rest
                        fmode = FileMode.Append;
                    }

                    //CREATE A NEW FILE IN PREDEFINED-AREAS
                    FileInfo t = new FileInfo(UCommon.UPATHS.sz_path_predefined_areas + "\\" + l_pk + ".xml");
                    try
                    {
                        if (fmode.Equals(FileMode.Create))
                        {
                            try
                            {
                                t.Delete();
                            }
                            catch (Exception) { }
                        }
                        StreamWriter w = (fmode.Equals(FileMode.Create) ? t.CreateText() : t.AppendText());
                        char pktype = l_pk.ToCharArray()[0];
                        ParseShapeNode(ref node, ref w, sz_nodetype, l_pk, pktype);
                        w.Close();
                    }
                    catch (Exception e)
                    {
                        //Failed to create/open file
                        ULog.error("Failed to Write addressfile " + t.Name + "\n" + e.Message);
                    }

                    
                }
            }
            catch (Exception)
            {
                throw;
            }

            return counter;
        }

        /* parse the XmlNode and write to the opened StreamWriter (predefined-areas)*/
        private bool ParseShapeNode(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, char pktype)
        {
            String col_a = "0", col_r = "0", col_g = "0", col_b = "0";
            ARGB argb = null;
            //if (!nodetype.Equals("cellbroadcast"))
            if (nodetype.Equals("alertpolygon") || nodetype.Equals("alertellipse"))
            {
                col_a = node.Attributes["col_a"].Value;
                col_r = node.Attributes["col_r"].Value;
                col_g = node.Attributes["col_g"].Value;
                col_b = node.Attributes["col_b"].Value;
                argb = new ARGB(col_a, col_r, col_g, col_b);
            }
            else
            {
                argb = new ARGB("0", "0", "0", "0");
            }
            String sz_pkfield;
            switch (pktype)
            {
                case 'a':
                    sz_pkfield = "l_alertpk";
                    break;
                case 'o':
                    sz_pkfield = "l_objectpk";
                    break;
                default:
                    return false;
            }


            if (nodetype.Equals("objectpolygon"))
                parse_as_polygon(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("alertpolygon"))
                parse_as_polygon(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("objectellipse"))
                parse_as_ellipse(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("alertellipse"))
                parse_as_ellipse(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("alertstreetid"))
                parse_as_streetid(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("alertgbno"))
                parse_as_gbno(ref node, ref w, nodetype, l_pk, sz_pkfield, ref argb);
            else if (nodetype.Equals("cellbroadcast"))
                parse_as_cellbroadcast(ref node, ref w, nodetype, l_pk, sz_pkfield);

            
            return true;
        }

        protected bool write_server_shape(ref PAOBJECT obj)
        {
            
            //only polygons supported
            String szDbShapeString = "";
            String l_pk = "o" + obj.l_objectpk;
            String nodetype = "objectpolygon";
            StreamWriter w = _create_server_shape_file(l_pk, FileMode.Create);
            bool b_ret = _write_server_shape(ref obj.m_shape, l_pk, nodetype, ref w, ref szDbShapeString);
            if(w!=null)
                w.Close();
            bool bShapeChanged = false;
            db.UpdatePAShape(obj.l_objectpk, szDbShapeString, PASHAPETYPES.PAOBJECT, ref bShapeChanged); // Må finne pashapetypes
            return b_ret;
        }

        protected bool write_server_shape(ref PAOBJECT obj, PASHAPETYPES type)
        {
            /*TEMP - update department restriction*/
            String outxml = "", md5 = "";
 
            obj.m_shape.CreateXml(ref outxml, ref md5);
            bool changed = false;
            db.UpdatePAShape(obj.l_objectpk, outxml, type, ref changed);
            return changed;
        }

        protected StreamWriter _create_server_shape_file(String l_pk, FileMode fmode)
        {
            FileInfo t = new FileInfo(UCommon.UPATHS.sz_path_predefined_areas + "\\" + l_pk + ".xml");
            try
            {
                t.Delete();
                StreamWriter w = (fmode.Equals(FileMode.Create) ? t.CreateText() : t.AppendText());
                return w;
            }
            catch (Exception e) {
                ULog.error(0, "Error in ParmAdmin._create_server_shape_file", e.Message);
                return null;
            }
        }
        protected bool _delete_server_shape_file(String nodetype, String l_pk)
        {
            try
            {
                FileInfo t = new FileInfo(UCommon.UPATHS.sz_path_predefined_areas + "\\" + nodetype + l_pk + ".xml");
                return true;
            }
            catch (Exception e)
            {
                ULog.error(0, "Error in ParmAdmin._delete_server_shape_file", e.Message);
                return false;
            }
        }

        protected bool _write_server_shape(ref UPolygon poly, String l_pk, String nodetype, ref StreamWriter w, ref String szShapeString)
        {
            //CREATE A NEW FILE IN PREDEFINED-AREAS
            try
            {
                String pktype = "l_pk";
                if (l_pk.StartsWith("o"))
                    pktype = "l_objectpk";
                else if (l_pk.StartsWith("a"))
                    pktype = "l_alertpk";
                else if (l_pk.StartsWith("e"))
                    pktype = "l_eventpk";
                else
                    pktype = "l_alertpk";
                String tmp = "";
                tmp = String.Format(UCommon.UGlobalizationInfo, "<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\">",
                            nodetype, poly.col_alpha, poly.col_red, poly.col_green, poly.col_blue,
                            pktype, l_pk);
                szShapeString += tmp;
                if(w!=null)
                    w.WriteLine(tmp);
                List<UPolypoint> p = poly.m_array_polypoints;
                for (int i = 0; i < p.Count; i++)
                {
                    tmp = String.Format(UCommon.UGlobalizationInfo, "<polypoint xcord=\"{0}\" ycord=\"{1}\" />", p[i].lon, p[i].lat);
                    szShapeString += tmp;
                    if(w!=null)
                        w.WriteLine(tmp);
                    
                }
                tmp = String.Format("</{0}>", nodetype);
                szShapeString += tmp;
                if(w!=null)
                    w.WriteLine(tmp);


                //w.Close();
            }
            catch (Exception e)
            {
                //Failed to create/open file
                ULog.error("Failed to Write addressfile " + l_pk + ".xml\n" + e.Message);
                throw;
            }
            return true;
        }

        protected bool _write_server_shape(ref UEllipse ell, String l_pk, String nodetype, ref StreamWriter w, ref String szShapeString)
        {
            try
            {
                String tmp = String.Format(UCommon.UGlobalizationInfo, "<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\" centerx=\"{7}\" centery=\"{8}\" cornerx=\"{9}\" cornery=\"{10}\" />",
                                            nodetype, ell.col_alpha, ell.col_red, ell.col_green, ell.col_blue, "l_alertpk", l_pk, ell.ellipse().lon, ell.ellipse().lat, ell.ellipse().lon + ell.x, ell.ellipse().lat + ell.y);
                szShapeString += tmp;
                if(w!=null)
                    w.WriteLine(tmp);
            }
            catch (Exception e)
            {
                ULog.error("Failed to Write addressfile " + l_pk + ".xml\n" + e.Message);
                throw;
            }

            return true;
        }

        protected bool _write_server_shape(ref UGeminiStreet shape, String l_pk, String nodetype, FileMode fmode, ref String szShapeString)
        {
            try
            {
                String tmp = "";
                StreamWriter w = _create_server_shape_file(l_pk, fmode);
                String sz_pkfield = "l_alertpk";
                tmp = String.Format("<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\">",
                    nodetype, shape.col_alpha, shape.col_red, shape.col_green, shape.col_blue, sz_pkfield, l_pk);
                szShapeString += tmp;
                if(w!=null)
                    w.WriteLine(tmp);

                Hashtable tbl = new Hashtable();
                int written = 0;
                List<com.ums.PAS.Address.UGisImportResultLine> list = shape.gemini().linelist;
                for (int i = 0; i < list.Count; i++)
                {
                    String mun, stre, hou, let, nam1, nam2;
                    list[i].finalize();
                    if(!list[i].isValid())
                        continue;
                    mun = list[i].municipalid;
                    stre = list[i].streetid;
                    hou = list[i].houseno;
                    let = list[i].letter;
                    nam1 = list[i].namefilter1;
                    nam2 = list[i].namefilter2;

                    String key = mun.Trim() + "." + stre.Trim() + "." + hou.Trim() + "." + let.Trim() + "." + nam1.Trim() + "." + nam2.Trim();
                    if (tbl[key] != null && tbl[key].ToString() == "1")
                        continue;
                    tbl.Add(key, "1");
                    if (mun.Length == 0)
                        mun = " ";
                    if (stre.Length == 0)
                        stre = " ";
                    if (hou.Length == 0)
                        hou = " ";
                    if (let.Length == 0)
                        let = " ";
                    if (nam1.Length == 0)
                        nam1 = " ";
                    if (nam2.Length == 0)
                        nam2 = " ";
                    tmp = String.Format("<line municipal=\"{0}\" streetid=\"{1}\" houseno=\"{2}\" letter=\"{3}\" namefilter1=\"{4}\" namefilter2=\"{5}\" />",
                        mun, stre, hou, let, nam1, nam2);
                    szShapeString += tmp;
                    if(w!=null)
                        w.WriteLine(tmp);
                    written++;
                }
                //WRITE END LINE
                tmp = String.Format("</{0}>", nodetype);
                szShapeString += tmp;
                if(w!=null)
                    w.WriteLine(tmp);
                if(w!=null)
                    w.Close();
            }
            catch (Exception e)
            {
                ULog.error("Failed to Write addressfile " + l_pk + ".xml\n" + e.Message);
                throw;
            }

            return true;
        }

        protected bool _write_server_lbashape(ref PAALERT a, ref StreamWriter w, PARMOPERATION op, ref String szShapeString, ref bool bUseLba)
        {
            try
            {
                if (a.m_lba_shape == null)
                {
                    bUseLba = false;
                    return false;
                }
                //if ((a.n_sendingtype & UShape.SENDINGTYPE_POLYGON) == UShape.SENDINGTYPE_POLYGON ||
                //    (a.n_sendingtype & UShape.SENDINGTYPE_ELLIPSE) == UShape.SENDINGTYPE_ELLIPSE)
                {
                    if (typeof(ULocationBasedAlert).Equals(a.m_lba_shape.GetType()))
                    {
                        ULocationBasedAlert lba = (ULocationBasedAlert)a.m_lba_shape;
                        _write_lba_server_part(ref lba, ref w, "cellbroadcast", "a" + a.l_alertpk.ToString(), "l_alertpk", ref szShapeString);
                        bUseLba = true;
                        //_create_lba_file_for_server(op, ref a);
                    }
                }
                //else
                //    throw new NotImplementedException("LBA addresstype only available with polygon and ellipse");
            }
            catch (Exception)
            {
                bUseLba = false;
                throw;
            }
            return true;
        }

        protected bool write_server_shape(ref PAALERT a, PARMOPERATION op)
        {
            try
            {
                //char pktype = 'a';
                String l_pk = "a" + a.l_alertpk;
                String nodetype = "Unknown";
                String szShapeToDb = "";
                bool bShapeChanged = false;
                bool bUseLba = false;

                if (typeof(UPolygon).Equals(a.m_shape.GetType()))
                {
                    /*TEMP - update department restriction
                     * String outxml = "", md5 = "";
                    a.m_shape.CreateXml(ref outxml, ref md5);
                    bool changed = false;
                    db.UpdatePAShape(m_logon.l_deptpk, outxml, PASHAPETYPES.PADEPARTMENTRESTRICTION, ref changed);*/

                    nodetype = "alertpolygon";
                    UPolygon poly = a.m_shape.poly();
                    StreamWriter w = _create_server_shape_file(l_pk, FileMode.Create);
                    _write_server_shape(ref poly, l_pk, nodetype, ref w, ref szShapeToDb);
                    _write_server_lbashape(ref a, ref w, op, ref szShapeToDb, ref bUseLba);
                    if(w!=null)
                        w.Close();
                }
                else if (typeof(UEllipse).Equals(a.m_shape.GetType()))
                {
                    nodetype = "alertellipse";
                    UEllipse ell = a.m_shape.ellipse();
                    StreamWriter w = _create_server_shape_file(l_pk, FileMode.Create);
                    _write_server_shape(ref ell, l_pk, nodetype, ref w, ref szShapeToDb);
                    _write_server_lbashape(ref a, ref w, op, ref szShapeToDb, ref bUseLba);
                    if(w!=null)
                        w.Close();
                }
                else if (typeof(UGeminiStreet).Equals(a.m_shape.GetType()))
                {
                    nodetype = "alertstreetid";
                    UGeminiStreet street = a.m_shape.gemini();

                    _write_server_shape(ref street, l_pk, nodetype, FileMode.Create, ref szShapeToDb);
                }
                else if (typeof(UMunicipalShape).Equals(a.m_shape.GetType()))
                {
                    throw new NotImplementedException();
                }
                else
                    throw new NotImplementedException();
                db.UpdatePAShape(a.l_alertpk, szShapeToDb, PASHAPETYPES.PAALERT, ref bShapeChanged);
                if (bShapeChanged && bUseLba)
                {
                    _create_lba_file_for_server(op, ref a);
                }
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }


        private bool parse_as_polygon(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield, ref ARGB argb)
        {
            //WRITE START LINE
            w.WriteLine(String.Format("<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\">",
                        nodetype, argb._a, argb._r, argb._g, argb._b, sz_pkfield, l_pk));

            XmlNodeList points = node.ChildNodes;
            for (int i = 0; i < points.Count; i++)
            {
                XmlNode point = points.Item(i);
                String l_coorx, l_coory;
                l_coorx = point.Attributes["xcord"].Value;
                l_coory = point.Attributes["ycord"].Value;
                w.WriteLine(String.Format("<polypoint xcord=\"{0}\" ycord=\"{1}\" />", l_coorx, l_coory));
            }
            //WRITE END LINE
            w.WriteLine(String.Format("</{0}>", nodetype));
            return true;
        }
        private bool parse_as_ellipse(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield, ref ARGB argb)
        {
            String centerx, centery, cornerx, cornery;
            centerx = node.Attributes["centerx"].Value;
            centery = node.Attributes["centery"].Value;
            cornerx = node.Attributes["cornerx"].Value;
            cornery = node.Attributes["cornery"].Value;
            w.WriteLine(String.Format("<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\" centerx=\"{7}\" centery=\"{8}\" cornerx=\"{9}\" cornery=\"{10}\" />",
                        nodetype, argb._a, argb._r, argb._g, argb._b, sz_pkfield, l_pk, centerx, centery, cornerx, cornery));
            return true;
        }
        private bool parse_as_streetid(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield, ref ARGB argb)
        {
            //WRITE START LINE
            w.WriteLine(String.Format("<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\">",
                        nodetype, argb._a, argb._r, argb._g, argb._b, sz_pkfield, l_pk));
            XmlNodeList points = node.ChildNodes;
            Hashtable tbl = new Hashtable();
            int written = 0;
            for (int i = 0; i < points.Count; i++)
            {
                XmlNode point = points.Item(i);
                String mun, stre, hou, let, nam1, nam2;
                mun = point.Attributes["municipal"].Value;
                stre = point.Attributes["streetid"].Value;
                hou = point.Attributes["houseno"].Value;
                let = point.Attributes["letter"].Value;
                nam1 = point.Attributes["namefilter1"].Value;
                nam2 = point.Attributes["namefilter2"].Value;
                String key = mun.Trim() + "." + stre.Trim() + "." + hou.Trim() + "." + let.Trim() + "." + nam1.Trim() + "." + nam2.Trim();
                if (tbl[key]!=null && tbl[key].ToString() == "1")
                    continue;
                tbl.Add(key, "1");
                if (mun.Length == 0)
                    mun = " ";
                if (stre.Length == 0)
                    stre = " ";
                if (hou.Length == 0)
                    hou = " ";
                if (let.Length == 0)
                    let = " ";
                if (nam1.Length == 0)
                    nam1 = " ";
                if (nam2.Length == 0)
                    nam2 = " ";
                w.WriteLine(String.Format("<line municipal=\"{0}\" streetid=\"{1}\" houseno=\"{2}\" letter=\"{3}\" namefilter1=\"{4}\" namefilter2=\"{5}\" />",
                    mun, stre, hou, let, nam1, nam2));
                written++;
            }
            //WRITE END LINE
            w.WriteLine(String.Format("</{0}>", nodetype));

            return true;
        }
        private bool parse_as_gbno(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield, ref ARGB argb)
        {
            //WRITE START LINE
            w.WriteLine(String.Format("<{0} col_a=\"{1}\" col_r=\"{2}\" col_g=\"{3}\" col_b=\"{4}\" {5}=\"{6}\">",
                        nodetype, argb._a, argb._r, argb._g, argb._b, sz_pkfield, l_pk));
            XmlNodeList points = node.ChildNodes;
            for (int i = 0; i < points.Count; i++)
            {
                XmlNode point = points.Item(i);
                String mun, gno, bno;
                mun = point.Attributes["municipalid"].Value;
                gno = point.Attributes["gno"].Value;
                bno = point.Attributes["bno"].Value;
                
                w.WriteLine(String.Format("<line municipal=\"{0}\" gno=\"{1}\" bno=\"{2}\" />",
                            mun, gno, bno));
            }
            //WRITE END LINE
            w.WriteLine(String.Format("</{0}>", nodetype));
            return true;
        }

        private bool _write_lba_server_part(ref ULocationBasedAlert lba, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield,
                                            ref String szShapeString)
        {
            try
            {
                String tmp = "";
                tmp = String.Format("<{0} sz_area=\"{1}\" sz_id=\"{2}\" {3}=\"{4}\">",
                                nodetype, "-1", "-1", sz_pkfield, l_pk);
                szShapeString += tmp;
                w.WriteLine(tmp);

                for (int i = 0; i < lba.getLanguageCount(); i++)
                {
                    LBALanguage lang = lba.getLanguage(i);
                    tmp = String.Format("<message sz_lang=\"{0}\" sz_text=\"{1}\" sz_cb_oadc=\"{2}\">",
                                        lang.sz_name, lang.sz_text, lang.sz_cb_oadc);
                    szShapeString += tmp;
                    w.WriteLine(tmp);
                    for (int j = 0; j < lang.getCCodeCount(); j++)
                    {
                        LBACCode ccode = lang.getCCode(j);
                        tmp = String.Format("<ccode>{0}</ccode>", ccode.getCCode());
                        szShapeString += tmp;
                        w.WriteLine(tmp);
                    }
                    tmp = "</message>";
                    szShapeString += tmp;
                    w.WriteLine(tmp);
                }
                tmp = String.Format("</{0}>", nodetype);
                szShapeString += tmp;
                w.WriteLine(tmp);
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }

        private bool parse_as_cellbroadcast(ref XmlNode node, ref StreamWriter w, String nodetype, String l_pk, String sz_pkfield)
        {
            w.WriteLine(String.Format("<{0} sz_area=\"{1}\" sz_id=\"{2}\" {3}=\"{4}\">",
                            nodetype, "-1", "-1", sz_pkfield, l_pk));
            XmlNodeList list = node.ChildNodes;
            String sz_msg_lang, sz_msg_text, sz_msg_ccode, sz_msg_oadc;
            for (int i = 0; i < list.Count; i++)
            {
                XmlNode msg = list.Item(i);
                sz_msg_lang = msg.Attributes["sz_lang"].Value;
                sz_msg_text = msg.Attributes["sz_text"].Value;
                sz_msg_oadc = msg.Attributes["sz_cb_oadc"].Value;

                sz_msg_lang = sz_msg_lang.Replace("&", "&amp;");
                sz_msg_text = sz_msg_text.Replace("&", "&amp;");
                sz_msg_oadc = sz_msg_oadc.Replace("&", "&amp;");
                w.WriteLine(String.Format("<message sz_lang=\"{0}\" sz_text=\"{1}\" sz_cb_oadc=\"{2}\">",
                                    sz_msg_lang, sz_msg_text, sz_msg_oadc));
                XmlNodeList cclist = msg.ChildNodes;
                for (int c = 0; c < cclist.Count; c++)
                {
                    XmlNode cc = cclist.Item(c);
                    sz_msg_ccode = cc.FirstChild.Value;
                    w.WriteLine(String.Format("<ccode>{0}</ccode>", sz_msg_ccode));
                }
                w.WriteLine("</message>");
            }

            w.WriteLine(String.Format("</{0}>", nodetype));
            return true;
        }


        private String NodeGetAlertPk(XmlNode node)
        {
            String l_pk, sz_operation;
            l_pk = node.Attributes["l_alertpk"].Value;
            sz_operation = node.Attributes["sz_operation"].Value;
            if (sz_operation.ToLower().Equals("insert"))
                l_pk = "a" + find_pk_from_temp(l_pk, "paalert");
            return l_pk;
        }
        private String NodeGetObjectPk(XmlNode node)
        {
            String l_pk, sz_operation;
            l_pk = node.Attributes["l_objectpk"].Value;
            sz_operation = node.Attributes["sz_operation"].Value;
            if (sz_operation.ToLower().Equals("insert"))
                l_pk = "o" + find_pk_from_temp(l_pk, "paobject");
            return l_pk;
        }

        private String GenLBAFileName(String l_pk)
        {
            return UCommon.UPATHS.sz_path_lba + "\\" + "LBA_ADMIN_" + l_pk + "_" + sz_newtimestamp + ".tmp";
        }

        private bool CreateLBADeleteFile(String l_pk, String sz_areaid)
        {
            String filename = GenLBAFileName(l_pk);
            try
            {
                db.ResetLBAArea(l_pk);
            }
            catch (Exception) { }
            FileInfo file = new FileInfo(filename);
            StreamWriter w = file.CreateText();
            String sz = String.Format("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
            w.WriteLine(sz);
            //<LBA operation=""DeleteAreaID"" sz_areaid=""" & areaid & """ l_alertpk=""" & mid(alertpk,2) & """ />"
            sz = String.Format("<LBA operation=\"{0}\" sz_areaid=\"{1}\" l_alertpk=\"{2}\" />",
                "DeleteArea", sz_areaid, l_pk);
            w.WriteLine(sz);
            w.Close();
            try
            {
                file.MoveTo(filename.Replace(".tmp", ".xml"));
            }
            catch (Exception)
            {
                throw;
            }

            return true;
        }

        private bool _create_lba_file_for_server(PARMOPERATION op, ref PAALERT alert)
        {
            String l_pk = alert.l_alertpk.ToString();
            String filename = GenLBAFileName(l_pk);
            String original_areaid = "0";
            try
            {
                original_areaid = db.GetAlertAreaID(l_pk);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }

            try
            {
                db.ResetLBAArea(l_pk);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }
            FileInfo file = new FileInfo(filename);
            StreamWriter w = file.CreateText();

            _write_lba_admin_for_server(op, original_areaid, l_pk, ref alert, ref w);
            w.WriteLine("</LBA>");
            w.Close();
            try
            {
                file.MoveTo(filename.Replace(".tmp", ".xml"));
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }

        private bool CreateLBAFile(String sz_nodetype, String l_pk, XmlNode node)
        {
            String filename = GenLBAFileName(l_pk);
            String original_areaid = "0";
            try
            {
                original_areaid = db.GetAlertAreaID(l_pk);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }

            try
            {
                db.ResetLBAArea(l_pk);
            }
            catch(Exception e)
            {
                ULog.error(e.Message);
            }
            FileInfo file = new FileInfo(filename);
            StreamWriter w = file.CreateText();
            if (sz_nodetype.Equals("alertpolygon"))
                CreateLBAPolygon(original_areaid, l_pk, ref node, ref w);
            else if (sz_nodetype.Equals("alertellipse"))
                CreateLBAEllipse(original_areaid, l_pk, ref node, ref w);
            w.WriteLine("</LBA>");
            w.Close();
            try
            {
                file.MoveTo(filename.Replace(".tmp", ".xml"));
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }

        private bool _write_lba_admin_for_server(PARMOPERATION operation, String original_areaid, String l_pk, 
                                                        ref PAALERT alert, ref StreamWriter w)
        {
            try
            {
                String sz_lba_operation = "";
                String szDbShapeString = "";
                if(operation.ToString().ToLower().Equals("insert") || operation.ToString().ToLower().Equals("update"))
                {
                    if (original_areaid.Equals("-2") || original_areaid.Equals("-1")) //never been made before
                    {
                        if (typeof(UPolygon).Equals(alert.m_shape.GetType()))
                            sz_lba_operation = "InsertAreaPolygon";
                        else if (typeof(UEllipse).Equals(alert.m_shape.GetType()))
                            sz_lba_operation = "InsertAreaEllipse";
                    }
                    else
                    {
                        if (typeof(UPolygon).Equals(alert.m_shape.GetType()))
                            sz_lba_operation = "UpdateAreaPolygon";
                        else if (typeof(UEllipse).Equals(alert.m_shape.GetType()))
                            sz_lba_operation = "UpdateAreaEllipse";
                    }
                }
                else if (operation.ToString().ToLower().Equals("delete"))
                {
                    if (typeof(UPolygon).Equals(alert.m_shape.GetType()))
                        sz_lba_operation = "DeleteAreaPolygon";
                    else if (typeof(UEllipse).Equals(alert.m_shape.GetType()))
                        sz_lba_operation = "DeleteAreaEllipse";

                }

                _StartLBATag(ref w, sz_lba_operation, original_areaid);
                if(typeof(UPolygon).Equals(alert.m_shape.GetType()))
                {
                    UPolygon poly = (UPolygon)alert.m_shape;
                    _write_server_shape(ref poly, l_pk, "alertpolygon", ref w, ref szDbShapeString);
                }
                else if (typeof(UEllipse).Equals(alert.m_shape.GetType()))
                {
                    UEllipse ell = (UEllipse)alert.m_shape;
                    _write_server_shape(ref ell, l_pk, "alertellipse", ref w, ref szDbShapeString);
                }

            }
            catch(Exception)
            {
                throw;
            }
            return true;
        }
        private bool CreateLBAPolygon(String original_areaid, String l_pk, ref XmlNode node, ref StreamWriter w)
        {
            String sz_lba_operation = "", sz_operation;
            sz_operation = node.Attributes["sz_operation"].Value;
            if (sz_operation.Equals("insert") || sz_operation.Equals("update"))
            {
                if (original_areaid.Equals("-2") || original_areaid.Equals("-1")) //never been made before
                {
                    sz_lba_operation = "InsertAreaPolygon";
                }
                else
                {
                    sz_lba_operation = "UpdateAreaPolygon";
                }
            }
            else if (sz_operation.Equals("delete"))
                sz_lba_operation = "DeleteAreaPolygon";

            try
            {
                _StartLBATag(ref w, sz_lba_operation, original_areaid);
                ARGB argb = new ARGB("0", "0", "0", "0");
                parse_as_polygon(ref node, ref w, "alertpolygon", l_pk, "l_alertpk", ref argb);
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }
        private bool CreateLBAEllipse(String original_areaid, String l_pk, ref XmlNode node, ref StreamWriter w)
        {
            String sz_lba_operation = "", sz_operation;
            sz_operation = node.Attributes["sz_operation"].Value;
            if (sz_operation.Equals("insert") || sz_operation.Equals("update"))
            {
                if (original_areaid.Equals("-2") || original_areaid.Equals("-1")) //never been made before
                {
                    sz_lba_operation = "InsertAreaEllipse";
                }
                else
                {
                    sz_lba_operation = "UpdateAreaEllipse";
                }
            }
            else if (sz_operation.Equals("delete"))
                sz_lba_operation = "DeleteAreaPolygon";

            try
            {
                _StartLBATag(ref w, sz_lba_operation, original_areaid);
                ARGB argb = new ARGB("0", "0", "0", "0");
                parse_as_ellipse(ref node, ref w, "alertellipse", l_pk, "l_alertpk", ref argb);
            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }
        private bool _StartLBATag(ref StreamWriter w, String sz_lba_operation, String sz_original_areaid)
        {
            try
            {
                String sz = String.Format("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
                w.WriteLine(sz);
                sz = String.Format("<LBA operation=\"{0}\" sz_areaid=\"{1}\" l_comppk=\"{2}\" l_deptpk=\"{3}\" " +
                                "l_userpk=\"{4}\" sz_compid=\"{5}\" sz_deptid=\"{6}\" sz_userid=\"{7}\" " +
                                "sz_password=\"{8}\">",
                                sz_lba_operation, sz_original_areaid, m_logon.l_comppk, m_logon.l_deptpk,
                                m_logon.l_userpk, m_logon.sz_compid, m_logon.sz_deptid, m_logon.sz_userid,
                                m_logon.sz_password);
                w.WriteLine(sz);

            }
            catch (Exception)
            {
                throw;
            }
            return true;
        }



        private int WriteUpdates()
        {
            outxml.insertStartElement("updates");
            outxml.insertAttribute("l_timestamp", sz_newtimestamp);
            outxml.insertAttribute("l_comppk", m_logon.l_comppk.ToString());
            outxml.insertAttribute("l_maintimestamp", sz_newtimestamp);

            int counter = 0;
            counter += GetCategories(); //will append pacategory tags to outxml
            m_res.n_percent = 30;
            m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res);
            counter += GetObjects(); //will append paobject tags to outxml
            m_res.n_percent = 40;
            m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res);
            counter += GetEvents(); //will append paevent tags to outxml
            m_res.n_percent = 50;
            m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res); //detailed percentage for alerts. span=50-90%
            counter += GetAlerts(); //will append paalert tags to outxml
            m_res.n_percent = 90;
            m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res);
            if(!sz_timestamp.Equals("0")) //don't include deleted if this is a full PARM download
                counter += GetDeleted(); //will append delete tags to outxml
            m_res.n_percent = 99;
            m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res);

            outxml.insertEndElement();
            return counter;
        }

        private int GetObjects()
        {
            int counter = 0;
            /*String sz_sql = String.Format("SELECT PO.l_objectpk, isnull(PO.l_deptpk,0), isnull(PO.l_importpk, 0), isnull(PO.sz_name,' '), PO.sz_description, isnull(PO.l_categorypk,-1), isnull(PO.l_parent,-1), isnull(PO.sz_address,' '), isnull(PO.sz_postno,' '), isnull(PO.sz_place,' '), isnull(PO.sz_phone,' '), PO.sz_metadata, isnull(PO.f_isobjectfolder,0), isnull(PO.l_timestamp,0), SH.sz_xml FROM PAOBJECT PO, PASHAPE SH WHERE PO.l_timestamp>={0} AND PO.l_deptpk={1} AND PO.l_objectpk*=SH.l_pk AND SH.l_type={2}",
                                           sz_timestamp, m_logon.l_deptpk, (int)PASHAPETYPES.PAOBJECT);*/
            String sz_sql = String.Format("sp_parm_getobjects {0}, {1}", m_logon.l_deptpk, sz_timestamp);
            OdbcDataReader rs = null;
            try
            {
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                String l_objectpk, l_deptpk, l_importpk, sz_name, sz_description;
                String l_categorypk, l_parent, sz_address, sz_postno, sz_place;
                String sz_phone, sz_metadata, f_isobjectfolder, l_timestamp, sz_shape_xml;
                while (rs.Read())
                {
                    l_objectpk = rs.GetString(0);
                    l_deptpk = rs.GetString(1);
                    l_importpk = rs.GetString(2);
                    try
                    {
                        sz_name = rs.GetString(3);
                    }
                    catch (Exception)
                    {
                        sz_name = " ";
                    }
                    try
                    {
                        sz_description = rs.GetString(4);
                    }
                    catch (Exception)
                    {
                        sz_description = " ";
                    }
                    sz_name = sz_name.Replace("&", "&amp;");
                    sz_description = sz_description.Replace("&", "&amp;");
                    l_categorypk = rs.GetString(5);
                    l_parent = rs.GetString(6);
                    sz_address = rs.GetString(7);
                    sz_postno = rs.GetString(8);
                    sz_place = rs.GetString(9);
                    sz_phone = rs.GetString(10);
                    try
                    {
                        sz_metadata = rs.GetString(11);
                    }
                    catch (Exception)
                    {
                        sz_metadata = " ";
                    }
                    f_isobjectfolder = rs.GetString(12);
                    l_timestamp = rs.GetString(13);
                    try
                    {
                        sz_shape_xml = rs.GetString(14);
                    }
                    catch (Exception)
                    {
                        sz_shape_xml = "";
                    }
                   
                    
                    outxml.insertStartElement("paobject");
                    outxml.insertAttribute("l_objectpk", "o" + l_objectpk);
                    outxml.insertAttribute("l_deptpk", l_deptpk);
                    outxml.insertAttribute("l_importpk", l_importpk);
                    outxml.insertAttribute("sz_name", sz_name);
                    outxml.insertAttribute("sz_description", sz_description);
                    outxml.insertAttribute("l_categorypk", "c" + l_categorypk);
                    outxml.insertAttribute("l_parent", "o" + l_parent);
                    outxml.insertAttribute("sz_address", sz_address);
                    outxml.insertAttribute("sz_postno", sz_postno);
                    outxml.insertAttribute("sz_place", sz_place);
                    outxml.insertAttribute("sz_phone", sz_phone);
                    outxml.insertAttribute("sz_metadata", sz_metadata);
                    outxml.insertAttribute("f_isobjectfolder", f_isobjectfolder);
                    outxml.insertAttribute("l_timestamp", l_timestamp);
                    if (sz_shape_xml.Trim().Length > 0)
                        outxml.insertText(sz_shape_xml);
                    else
                        WritePolygonToFile(l_objectpk, "paobject", ref outxml);
                    outxml.insertEndElement();
                    counter++;
                    //WritePolygonToFile(l_objectpk, "paobject", ref outpolyxml);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

            return counter;
        }
        [WebMethod]
        public List<CB_USER_REGION_RESPONSE> GetUserRegion(ULOGONINFO logoninfo, List<UBBUSER> user)
        {
            // Check logon
            List<CB_USER_REGION_RESPONSE> response = new List<CB_USER_REGION_RESPONSE>();
            String sz_sql;
            UBBUSER tempuser;
            OdbcDataReader rs = null;
            try
            {
                for(int i=0;i<user.Count;++i) {
                    List<PAOBJECT> olist = new List<PAOBJECT>();
                    tempuser = new UBBUSER();
                    sz_sql = String.Format("SELECT u.l_userpk, u.sz_userid, u.sz_name, u.sz_surname, u.l_comppk, up.l_deptpk, up.l_deptpk, dept.sz_deptid " +
                                             "FROM BBUSERPROFILE_X_DEPT up, BBUSER u, BBDEPARTMENT dept " +
                                            "WHERE up.l_userpk = u.l_userpk " +
		                                      "AND dept.l_deptpk = up.l_deptpk " +
                                              "AND up.l_userpk = {0}", user[i].l_userpk);

                    db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                    rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                    
                    while (rs.Read())
                    {
                        tempuser.l_userpk = (long)rs.GetDecimal(0);
                        tempuser.sz_userid = rs.GetString(1);
                        tempuser.sz_name = rs.GetString(2);
                        if (rs.IsDBNull(3))
                            tempuser.sz_surname = "";
                        else
                            tempuser.sz_surname = rs.GetString(3);
                        tempuser.l_comppk = rs.GetInt32(4);
                        tempuser.l_deptpk = rs.GetInt32(5);
                        tempuser.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN;
                        PAOBJECT obj = new PAOBJECT();
                        obj.l_deptpk = rs.GetInt32(5);
                        obj.l_objectpk = (long)rs.GetDecimal(6);
                        obj.sz_name = rs.GetString(7);
                        olist.Add(obj);
                    }
                    rs.Close();

                    CB_USER_REGION_RESPONSE tmp = new CB_USER_REGION_RESPONSE();
                    tmp.user = tempuser;
                    tmp.regionlist = olist;
                    response.Add(tmp);
                }
                return response;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

        }

        [WebMethod]
        public List<UDEPARTMENT> GetRestrictionAreas(ULOGONINFO logoninfo)
        {
            List<UDEPARTMENT> dlist = new List<UDEPARTMENT>();
            string sz_sql = "SELECT l_deptpk, l_deptpri, sz_deptid, f_map, SH.sz_xml, isnull(SH.l_disabled_timestamp,0) as l_disabled_timestamp, isnull(SH.f_disabled, 0) as f_disabled " +
                              "FROM v_BBDEPARTMENT DEP " +
                              "LEFT OUTER JOIN PASHAPE SH ON DEP.l_deptpk = SH.l_pk " +
                             "WHERE SH.l_type = 16";
            OdbcDataReader rs = null;
            try
            {
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                db.CheckLogon(ref logoninfo, true);
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UDEPARTMENT obj = new UDEPARTMENT();
                    obj.l_deptpk = rs.GetInt32(0);
                    obj.l_deptpri = rs.GetInt16(1);
                    obj.sz_deptid = rs.GetString(2);
                    obj.f_map = rs.GetInt16(3);
                    UShape shape = UPolygon.ParseFromXml(rs.GetString(4));
                    shape.f_disabled = rs.GetInt16(6);
                    shape.l_disabled_timestamp = (long)rs.GetDecimal(5);
                    obj.restrictionShapes.Add(shape);
                    dlist.Add(obj);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return dlist;
        }

        [WebMethod]
        public List<UBBUSER> GetUsers(ULOGONINFO logoninfo)
        {
            List<UBBUSER> ulist = new List<UBBUSER>();

            String sz_sql = "SELECT * FROM BBUSER";
            OdbcDataReader rs = null;
            try
            {
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                db.CheckLogon(ref logoninfo, true);
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                while (rs.Read())
                {
                    UBBUSER obj = new UBBUSER();
                    obj.l_userpk = (long)rs.GetDecimal(0);
                    obj.sz_userid = rs.GetString(1);
                    obj.sz_name = rs.GetString(3);
                    if (rs.IsDBNull(4))
                        obj.sz_surname = "";
                    else
                        obj.sz_surname = rs.GetString(4);
                    obj.l_comppk = rs.GetInt32(5);
                    obj.l_deptpk = rs.GetInt32(6);
                    obj.l_profilepk = rs.GetInt32(7);
                    obj.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN;
                    ulist.Add(obj);
                }
                rs.Close();
                foreach(UBBUSER u in ulist) {
                    sz_sql = String.Format("SELECT l_deptpk FROM BBUSERPROFILE_X_DEPT WHERE l_userpk={0}", u.l_userpk);
                    rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                    List<int> deptlist = new List<int>();
                    while (rs.Read())
                        deptlist.Add(rs.GetInt32(0));
                    u.l_deptpklist = deptlist.ToArray();
                    rs.Close();
                }
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
                db.close();
            }

            return ulist;
        }
        [WebMethod]
        public List<PAOBJECT> GetRegions(ULOGONINFO logoninfo)
        {
            List<PAOBJECT> objList = new List<PAOBJECT>();

            String sz_sql = String.Format("SELECT PO.l_objectpk, isnull(PO.l_deptpk,0), isnull(PO.l_importpk, 0), isnull(PO.sz_name,' '), PO.sz_description, isnull(PO.l_categorypk,-1), isnull(PO.l_parent,-1), isnull(PO.sz_address,' '), isnull(PO.sz_postno,' '), isnull(PO.sz_place,' '), isnull(PO.sz_phone,' '), PO.sz_metadata, isnull(PO.f_isobjectfolder,0), isnull(PO.l_timestamp,0), SH.sz_xml FROM PAOBJECT PO LEFT JOIN PASHAPE SH ON PO.l_objectpk=SH.l_pk WHERE PO.l_deptpk={0} AND (SH.l_type={1} OR SH.l_type={2})",
                                           /*m_logon.l_deptpk*/1, (int)PASHAPETYPES.PADEPARTMENTRESTRICTION, (int)PASHAPETYPES.PAUSERRESTRICTION);
            OdbcDataReader rs = null;
            try
            {
                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                db.CheckLogon(ref logoninfo, true);
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                String l_objectpk, l_deptpk, l_importpk, sz_name, sz_description;
                String l_categorypk, l_parent, sz_address, sz_postno, sz_place;
                String sz_phone, sz_metadata, f_isobjectfolder, l_timestamp, sz_shape_xml;
                while (rs.Read())
                {
                    PAOBJECT obj = new PAOBJECT();
                    obj.l_objectpk = long.Parse(rs.GetString(0));
                    obj.l_deptpk = long.Parse(rs.GetString(1));
                    obj.l_importpk = long.Parse(rs.GetString(2));
                    try
                    {
                        sz_name = rs.GetString(3);
                    }
                    catch (Exception)
                    {
                        sz_name = " ";
                    }
                    try
                    {
                        sz_description = rs.GetString(4);
                    }
                    catch (Exception)
                    {
                        sz_description = " ";
                    }
                    obj.sz_name = sz_name.Replace("&", "&amp;");
                    obj.sz_description = sz_description.Replace("&", "&amp;");
                    obj.l_categorypk = long.Parse(rs.GetString(5));
                    obj.l_parent = long.Parse(rs.GetString(6));
                    obj.sz_address = rs.GetString(7);
                    obj.sz_postno = rs.GetString(8);
                    obj.sz_place = rs.GetString(9);
                    obj.sz_phone = rs.GetString(10);
                    try
                    {
                        sz_metadata = rs.GetString(11);
                    }
                    catch (Exception)
                    {
                        sz_metadata = " ";
                    }
                    obj.b_isobjectfolder = rs.GetString(12).Equals("1")?true:false;
                    obj.l_timestamp = long.Parse(rs.GetString(13));
                    try
                    {
                        sz_shape_xml = rs.GetString(14);
                    }
                    catch (Exception)
                    {
                        sz_shape_xml = "";
                    }
                    obj.m_shape = UPolygon.ParseFromXml(sz_shape_xml).poly();
                    objList.Add(obj);
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
                db.close();
            }

            return objList;
        }
        [WebMethod]
        public List<UBBUSER> GetAccessPermissions(long objectpk)
        {
            List<UBBUSER> list = new List<UBBUSER>();
            String sz_sql = String.Format("SELECT u.sz_userid " +
                                            "FROM BBUSERPROFILE_X_DEPT up, BBUSER u " +
                                           "WHERE u.l_userpk = up.l_userpk " +
                                             "AND up.l_deptpk = {0}", objectpk);
            OdbcDataReader rs = null;
            try
            {
                
                UBBUSER user;

                db = new PASUmsDb(ConfigurationManager.ConnectionStrings["backbone"].ConnectionString, 120);
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);

                while (rs.Read())
                {
                    user = new UBBUSER();
                    user.sz_userid = rs.GetString(0);
                    user.l_disabled_reasoncode = BBUSER_BLOCK_REASONS.BLOCKED_BY_ADMIN;
                    list.Add(user);
                }
                rs.Close();

                return list;
                
            }
            catch (Exception e) { return list; }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

        }
        private int GetAlerts()
        {
            DateTime start = DateTime.Now;
            int counter = 0;
            int n_records = 1;
            //"SELECT PA.l_alertpk, PA.l_parent, PA.sz_name, PA.sz_description, PA.l_profilepk, PA.l_schedpk, PA.sz_oadc, PA.l_validity, PA.l_addresstypes, PA.l_timestamp, isnull(PA.f_locked, 0) f_locked, PA.sz_areaid FROM PAALERT PA, PAEVENT PE, PAOBJECT PO WHERE PA.l_parent=PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PA.l_timestamp>" & l_maintimestamp & " AND PO.l_deptpk=" & Session("lDeptPk")
            /*String sz_sql = String.Format("SELECT PA.l_alertpk, isnull(PA.l_parent,-1), isnull(PA.sz_name,' '), PA.sz_description, isnull(PA.l_profilepk,0), isnull(PA.l_schedpk,0), isnull(PA.sz_oadc,' '), isnull(PA.l_validity,1), isnull(PA.l_addresstypes,0), isnull(PA.l_timestamp,0), isnull(PA.f_locked, 0) f_locked, isnull(PA.sz_areaid,'-1'), isnull(l_maxchannels, 0), isnull(l_requesttype, 0), isnull(l_expiry, 0), isnull(sz_sms_oadc,''), isnull(sz_sms_message,''), isnull(PA.l_deptpk, -1) FROM PAALERT PA, PAEVENT PE, PAOBJECT PO WHERE PA.l_parent=PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PA.l_timestamp>={0} AND PO.l_deptpk={1}",
                                            sz_timestamp, m_logon.l_deptpk);*/
            //db.ExecNonQuery("set textsize 10000000");
            //String sz_sql = String.Format("SELECT PA.l_alertpk, isnull(PA.l_parent,-1), isnull(PA.sz_name,' '), PA.sz_description, isnull(PA.l_profilepk,0), isnull(PA.l_schedpk,0), isnull(PA.sz_oadc,' '), isnull(PA.l_validity,1), isnull(PA.l_addresstypes,0), isnull(PA.l_timestamp,0), isnull(PA.f_locked, 0) f_locked, isnull(PA.sz_areaid,'-1'), isnull(l_maxchannels, 0), isnull(l_requesttype, 0), isnull(l_expiry, 0), isnull(sz_sms_oadc,''), isnull(sz_sms_message,''), isnull(PA.l_deptpk, -1), SH.sz_xml FROM PAALERT PA, PAEVENT PE, PAOBJECT PO, PASHAPE SH WHERE PA.l_parent=PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PA.l_timestamp>={0} AND PO.l_deptpk={1} AND PA.l_alertpk*=SH.l_pk",
            //                                sz_timestamp, m_logon.l_deptpk);
            String sz_sql = String.Format("sp_parm_getalerts {0}, {1}", m_logon.l_deptpk, sz_timestamp);

            //String sz_sql_count = String.Format("SELECT count(*) FROM PAALERT PA, PAEVENT PE, PAOBJECT PO WHERE PA.l_parent=PE.l_eventpk AND PE.l_parent=PO.l_objectpk AND PA.l_timestamp>={0} AND PO.l_deptpk={1}",
            //                                sz_timestamp, m_logon.l_deptpk);
            String sz_sql_count = String.Format("sp_parm_getcount {0}, {1}", m_logon.l_deptpk, sz_timestamp);
            OdbcDataReader rs = null;
            OdbcDataReader lba = null;
            try
            {
                rs = db.ExecReader(sz_sql_count, UmsDb.UREADER_KEEPOPEN);
                if (rs.Read())
                {
                    n_records = rs.GetInt32(0);
                }
                rs.Close();
            }
            catch (Exception e)
            {
            }
            try
            {
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_KEEPOPEN);
                String l_alertpk, l_parent, sz_name, sz_description, l_profilepk;
                String l_schedpk, sz_oadc, l_validity, l_addresstypes, l_timestamp;
                String f_locked, sz_areaid, l_maxchannels, l_requesttype;
                String l_expiry, sz_sms_oadc, sz_sms_message, sz_shape_xml;
                int l_deptpk = 0;

                while (rs.Read())
                {
                    l_alertpk = rs.GetString(0);
                    /*if (l_alertpk.Equals("1000000000000297"))
                    {
                        String dill = "break";
                        //db.ExecNonQuery("UPDATE PASHAPE set sz_xml='test æøåÆØÅ' WHERE l_pk=1000000000000297");
                        OdbcDataReader rs2 = db.ExecReader("SELECT sz_xml FROM PASHAPE where l_pk=1000000000000297", UmsDb.UREADER_KEEPOPEN);
                        if(rs2.Read())
                        {
                            //byte [] bytes = new byte[1000000];
                            //rs2.GetBytes(0, 0, bytes, 0, 1000000);
                            //dill = Encoding.UTF8.GetString(bytes);
                            dill = rs2.GetString(0);
                        }

                    }*/
                    l_parent = rs.GetString(1);
                    try
                    {
                        sz_name = rs.GetString(2);
                    }
                    catch (Exception)
                    {
                        sz_name = " ";
                    }
                    try
                    {
                        sz_description = rs.GetString(3);
                    }
                    catch (Exception)
                    {
                        sz_description = " ";
                    }
                    sz_name = sz_name.Replace("&", "&amp;");
                    sz_description = sz_description.Replace("&", "&amp;");

                    l_profilepk = rs.GetString(4);
                    l_schedpk = rs.GetString(5);
                    sz_oadc = rs.GetString(6);
                    l_validity = rs.GetString(7);
                    l_addresstypes = rs.GetString(8);
                    l_timestamp = rs.GetString(9);
                    f_locked = rs.GetString(10);
                    sz_areaid = rs.GetString(11);
                    l_maxchannels = rs.GetString(12);
                    l_requesttype = rs.GetString(13);
                    l_expiry = rs.GetString(14);
                    sz_sms_oadc = rs.GetString(15);
                    sz_sms_message = rs.GetString(16);
                    l_deptpk = rs.GetInt32(17);
                    if (l_deptpk <= 0)
                    {
                        l_deptpk = m_logon.l_deptpk;
                    }
                    try
                    {
                        sz_shape_xml = rs.GetString(18);
                        //new UPolygon().ParseFromXml(sz_shape_xml);
                    }
                    catch (Exception)
                    {
                        sz_shape_xml = "";
                    }
                    /*finally
                    {
                        if (rs != null && !rs.IsClosed)
                            rs.Close();
                    }*/
                    if (sz_shape_xml.Length > 0)
                    {
                        try
                        {
                            UShape sh = UShape.ParseFromXml(sz_shape_xml);
                        }
                        catch (Exception)
                        { }
                    }


                    outxml.insertStartElement("paalert");
                    outxml.insertAttribute("l_alertpk", "a" + l_alertpk);
                    outxml.insertAttribute("l_parent", "e" + l_parent);
                    outxml.insertAttribute("sz_name", sz_name);
                    outxml.insertAttribute("sz_description", sz_description);
                    outxml.insertAttribute("l_profilepk", l_profilepk);
                    outxml.insertAttribute("l_schedpk", l_schedpk);
                    outxml.insertAttribute("sz_oadc", sz_oadc);
                    outxml.insertAttribute("l_validity", l_validity);
                    outxml.insertAttribute("l_addresstypes", l_addresstypes);
                    outxml.insertAttribute("l_timestamp", l_timestamp);
                    outxml.insertAttribute("f_locked", f_locked);
                    outxml.insertAttribute("sz_areaid", sz_areaid);
                    outxml.insertAttribute("l_maxchannels", l_maxchannels);
                    outxml.insertAttribute("l_requesttype", l_requesttype);
                    outxml.insertAttribute("l_expiry", l_expiry);
                    outxml.insertAttribute("sz_sms_oadc", sz_sms_oadc);
                    outxml.insertAttribute("sz_sms_message", sz_sms_message);

                    if (!sz_areaid.Equals("-1")) //assume we're preparing LBA, status for each operator is in PAALERT_LBA
                    {
                        outxml.insertStartElement("lbaoperators");
                        //String szLbaSql = String.Format("SELECT DISTINCT isnull(PA.l_operator,-1), isnull(PA.l_status,-3), isnull(PA.l_areaid,0), isnull(OP.sz_operatorname,'Unknown Operator') FROM PAALERT_LBA PA, LBAOPERATORS OP WHERE OP.l_operator*=PA.l_operator AND PA.l_alertpk={0}", l_alertpk);
                        //String szLbaSql = String.Format("SELECT DISTINCT isnull(PA.l_operator,-1), isnull(PA.l_status,-3), isnull(PA.l_areaid,0), isnull(OP.sz_operatorname,'Unknown Operator') FROM PAALERT_LBA PA, LBAOPERATORS OP, LBAOPERATORS_X_DEPT XD WHERE OP.l_operator=XD.l_operator AND XD.l_operator=PA.l_operator AND PA.l_alertpk={0} AND XD.l_deptpk={1}", l_alertpk, l_deptpk);
                        String szLbaSql = String.Format("sp_parm_getalert_lbaop {0}, {1}", l_alertpk, l_deptpk);
                        lba = db.ExecReader(szLbaSql, UmsDb.UREADER_AUTOCLOSE);
                        while (lba.Read())
                        {
                            outxml.insertStartElement("operator");
                            outxml.insertAttribute("l_operator", lba.GetInt32(0).ToString());
                            outxml.insertAttribute("l_status", lba.GetInt32(1).ToString());
                            outxml.insertAttribute("l_areaid", lba.GetInt64(2).ToString());
                            outxml.insertAttribute("sz_operatorname", lba.GetString(3));
                            outxml.insertEndElement();
                        }
                        lba.Close();
                        outxml.insertEndElement(); //lbaoperators
                    }
                    if (sz_shape_xml.Trim().Length > 0)
                    {
                        //byte [] utf8 = new UTF8Encoding().GetBytes(sz_shape_xml.ToCharArray());
                        //sz_shape_xml = Encoding.GetEncoding("iso-8859-1").GetString(utf8);
                        outxml.insertText(sz_shape_xml);
                    }
                    else
                        WritePolygonToFile(l_alertpk, "paalert", ref outxml);
                    outxml.insertEndElement();//paalert
                    
                    counter++;
                    int percent = (int)(counter * 40.0f / n_records);
                    m_res.n_percent = (int)(50.0f) + percent;
                    m_percentdelegate(ref m_logon, ProgressJobType.PARM_UPDATE, m_res);


                    //WritePolygonToFile(l_alertpk, "paalert", ref outpolyxml);
                }
                rs.Close();
                DateTime end = DateTime.Now;
                TimeSpan diff = end - start;
                int sec = diff.Seconds;

            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
                if (lba != null && !lba.IsClosed)
                    lba.Close();
            }
            return counter;
        }

        private int GetEvents()
        {
            int counter = 0;
            //SELECT PE.l_eventpk, PE.l_parent, PE.sz_name, PE.sz_description, PE.l_categorypk, PE.l_timestamp, isnull(PE.f_epi_lon, 0.0) f_epi_lon, isnull(PE.f_epi_lat, 0.0) f_epi_lat FROM PAEVENT PE, PAOBJECT PO WHERE PE.l_parent=PO.l_objectpk AND PE.l_timestamp>" & l_maintimestamp & " AND PO.l_deptpk=" & Session("lDeptPk")
            /*String sz_sql = String.Format("SELECT PE.l_eventpk, isnull(PE.l_parent,-1), isnull(PE.sz_name,' '), PE.sz_description, isnull(PE.l_categorypk,0), isnull(PE.l_timestamp,0), isnull(PE.f_epi_lon, 0.0) f_epi_lon, isnull(PE.f_epi_lat, 0.0) f_epi_lat FROM PAEVENT PE, PAOBJECT PO WHERE PE.l_parent=PO.l_objectpk AND PE.l_timestamp>={0} AND PO.l_deptpk={1}",
                                            sz_timestamp, m_logon.l_deptpk);*/
            String sz_sql = String.Format("sp_parm_getevents {0}, {1}", m_logon.l_deptpk, sz_timestamp);
            OdbcDataReader rs = null;
            try
            {
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                String l_eventpk, l_parent, sz_name, sz_description, l_categorypk;
                String l_timestamp, f_epi_lon, f_epi_lat;
                while (rs.Read())
                {
                    l_eventpk = rs.GetString(0);
                    l_parent = rs.GetString(1);
                    sz_name = rs.GetString(2);
                    try
                    {
                        sz_description = rs.GetString(3);
                    }
                    catch (Exception)
                    {
                        sz_description = " ";
                    }
                    sz_name = sz_name.Replace("&", "&amp;");
                    sz_description = sz_description.Replace("&", "&amp;");

                    l_categorypk = rs.GetString(4);
                    l_timestamp = rs.GetString(5);
                    f_epi_lon = rs.GetString(6);
                    f_epi_lat = rs.GetString(7);

                    outxml.insertStartElement("paevent");
                    outxml.insertAttribute("l_eventpk", "e" + l_eventpk);
                    outxml.insertAttribute("l_parent", "o" + l_parent);
                    outxml.insertAttribute("sz_name", sz_name);
                    outxml.insertAttribute("sz_description", sz_description);
                    outxml.insertAttribute("l_categorypk", "c" + l_categorypk);
                    outxml.insertAttribute("l_timestamp", l_timestamp);
                    outxml.insertAttribute("f_epi_lon", f_epi_lon);
                    outxml.insertAttribute("f_epi_lat", f_epi_lat);
                    outxml.insertEndElement();
                    counter++;
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }
            return counter;
        }

        private int GetCategories()
        {
            int counter = 0;
            /*String sz_sql = String.Format("SELECT l_categorypk, isnull(sz_name,' '), sz_description, isnull(sz_fileext,' '), isnull(l_timestamp,0) FROM PACATEGORY WHERE l_timestamp>={0}",
                                        sz_timestamp);*/
            String sz_sql = String.Format("sp_parm_getcategories {0}", sz_timestamp);
            OdbcDataReader rs = null;
            try
            {
                rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                String l_categorypk, sz_name, sz_description, sz_fileext, l_timestamp;

                while (rs.Read())
                {
                    l_categorypk = rs.GetString(0);
                    sz_name = rs.GetString(1);
                    try
                    {
                        sz_description = rs.GetString(2);
                    }
                    catch (Exception)
                    {
                        sz_description = " ";
                    }
                    sz_fileext = rs.GetString(3);
                    l_timestamp = rs.GetString(4);

                    outxml.insertStartElement("pacategory");
                    outxml.insertAttribute("l_categorypk", "c" + l_categorypk);
                    outxml.insertAttribute("sz_name", sz_name);
                    outxml.insertAttribute("sz_description", sz_description);
                    outxml.insertAttribute("sz_fileext", sz_fileext);
                    outxml.insertAttribute("l_timestamp", l_timestamp);
                    outxml.insertEndElement();
                    counter++;
                }
                rs.Close();
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

            return counter;
        }

        private int GetDeleted()
        {
            int counter = 0;
            
            if (!sz_timestamp.Equals("0"))
            {
                //String sz_sql = String.Format("SELECT isnull(c_objtype, 'n'), isnull(l_objectpk, 0), isnull(sz_areaid,' ') FROM PADELETE WHERE l_comppk={0} AND l_timestamp>={1}",
                //                        m_logon.l_comppk, sz_timestamp);
                String sz_sql = String.Format("sp_parm_getdeleted {0}, {1}",
                                        m_logon.l_deptpk, sz_timestamp);
                String l_objectpk, c_objtype, sz_areaid;
                OdbcDataReader rs = null;
                try
                {
                    rs = db.ExecReader(sz_sql, UmsDb.UREADER_AUTOCLOSE);
                    while (rs.Read())
                    {
                        c_objtype = rs.GetString(0);
                        l_objectpk = rs.GetString(1);
                        sz_areaid = rs.GetString(2);
                        outxml.insertStartElement("delete");
                        outxml.insertAttribute("l_objectpk", c_objtype + l_objectpk);
                        outxml.insertEndElement();
                        
                        //CREATE DELETE FILE FOR LBA HERE
                        //if(c_objtype.Equals("a") && !sz_areaid.Equals("-2") && !sz_areaid.Equals("-1"))
                        //    CreateLBADeleteFile(l_objectpk, sz_areaid);

                    }

                    rs.Close();
                }
                catch (Exception)
                {
                    throw;
                }
                finally
                {
                    if (rs != null && !rs.IsClosed)
                        rs.Close();
                }
            }
            return counter;
        }

        private int ParsePARM(Stream parmStream, ref PASUmsDb db)
        {
            XmlDocument doc = new XmlDocument();
            try
            {
                doc.Load(parmStream);
                XmlNode node = doc.DocumentElement;
                //TOP LEVEL
                sz_timestamp = node.Attributes["l_timestamp"].Value;
                if (sz_timestamp == null)
                    sz_timestamp = "0";

                outxml.insertStartElement("responsecodes");
                //PARSE OPERATIONS
                XmlNodeList nl_op = node.ChildNodes;
                for (int i = 0; i < nl_op.Count; i++)
                {
                    try
                    {
                        XmlNode n_op = nl_op.Item(i);
                        String sz_nodename = n_op.Name.ToLower();
                        String sz_operation = n_op.Attributes["sz_operation"].Value;
                        if (sz_nodename.Equals("paobject"))
                        {
                            parseObject(ref n_op, sz_operation);
                        }
                        else if (sz_nodename.Equals("paevent"))
                        {
                            parseEvent(ref n_op, sz_operation);
                        }
                        else if (sz_nodename.Equals("paalert"))
                        {
                            parseAlert(ref n_op, sz_operation);
                        }
                    }
                    catch (Exception err)
                    {
                        ULog.error(err.Message);
                    }
                }
                outxml.insertEndElement(); //responsecodes
            }
            catch (Exception)
            {
                return -1;
            }
            return 0;
        }


        private void addPkRef(String temppk, String realpk, String nodename)
        {
            if (nodename.Equals("paobject"))
            {
                pks_object[0].Add(temppk);
                pks_object[1].Add(realpk);
            }
            else if (nodename.Equals("paevent"))
            {
                pks_event[0].Add(temppk);
                pks_event[1].Add(realpk);
            }
            else
            {
                pks_alert[0].Add(temppk);
                pks_alert[1].Add(realpk);
            }
        }

        private String find_pk_from_temp(String temppk, String nodename)
        {
            if (nodename.Equals("paobject"))
            {
                for (int n = 0; n < pks_object[0].Count; n++)
                {
                    if (temppk.Equals(pks_object[0][(n)]))
                        return pks_object[1][(n)].ToString();
                }
            }
            else if (nodename.Equals("paevent"))
            {
                for (int n = 0; n < pks_event[0].Count; n++)
                {
                    if (temppk.Equals(pks_event[0][(n)]))
                        return pks_event[1][(n)].ToString();
                }
            }
            else
            {
                for (int n = 0; n < pks_alert[0].Count; n++)
                {
                    if (temppk.Equals(pks_alert[0][(n)]))
                        return pks_alert[1][(n)].ToString();
                }
            }
            return "";
        }

        private bool parseObject(ref XmlNode node, String sz_operation)
        {
            String l_objectpk   = node.Attributes["l_objectpk"].Value;
            String l_temppk = l_objectpk;
            if (!sz_operation.ToLower().Equals("insert"))
                l_objectpk = l_objectpk.Substring(1);
            String l_deptpk = node.Attributes["l_deptpk"].Value;
            String l_importpk = node.Attributes["l_importpk"].Value;
            String sz_name = node.Attributes["sz_name"].Value.Replace("'", "''");
            String sz_description = node.Attributes["sz_description"].Value.Replace("'", "''");
            String l_categorypk = node.Attributes["l_categorypk"].Value;
            String l_parent = node.Attributes["l_parent"].Value;
            String sz_address = node.Attributes["sz_address"].Value.Replace("'", "''");
            String sz_postno = node.Attributes["sz_postno"].Value;
            String sz_place = node.Attributes["sz_place"].Value.Replace("'", "''");
            String sz_phone = node.Attributes["sz_phone"].Value.Replace("'", "''");
            String sz_metadata = node.Attributes["sz_metadata"].Value.Replace("'", "''");
            String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
            String f_isobjectfolder = node.Attributes["f_isobjectfolder"].Value;

            if (!sz_operation.ToLower().Equals("insert"))
            {
                if (l_parent.Length > 0)
                    l_parent = l_parent.Substring(1);
                else
                    l_parent = "-1";
            }
            else
            {
                if (l_parent.IndexOf('o') == 0 || l_parent.IndexOf('e') == 0 || l_parent.IndexOf('a') == 0)
                {
                    //PARENT EXISTS
                    if (l_parent.Length > 0)
                        l_parent = l_parent.Substring(1);
                }
                else
                {
                    l_parent = find_pk_from_temp(l_parent, "paobject");
                }
            }
            if (l_categorypk.Length > 0)
                l_categorypk = l_categorypk.Substring(1);

            String sz_sql;
	        //sz_sql = "sp_ins_paobject '" + sz_operation.ToLower() + "', " & l_objectpk & ", " & Session("lUserPk") & ", " & Session("lCompanyPk") & ", " & l_deptpk & ", " & l_importpk & ", '" & sz_name & "', " & l_categorypk &_
			//        ", " & l_parent & ", '" & sz_address & "', '" & sz_postno & "', '" & sz_place & "', '" & sz_phone & "', " & l_timestamp & ", " & f_isobjectfolder
            sz_sql = String.Format("sp_ins_paobject '{0}', {1}, {2}, {3}, {4}, {5}, '{6}', {7}, {8}, '{9}', '{10}', '{11}', '{12}', {13}, {14}",
                                   sz_operation.ToLower(), l_objectpk, m_logon.l_userpk, m_logon.l_comppk, m_logon.l_deptpk, l_importpk, sz_name, l_categorypk,
                                   l_parent, sz_address, sz_postno, sz_place, sz_phone, l_timestamp, f_isobjectfolder);
            db_exec(sz_sql, "paobject", sz_operation, l_objectpk, sz_description, true);

            return true;
        }

        /*new function for insert/update/delete of objects, by useing PAOBJECT class*/
        public long HandleObjectUpdate(PARMOPERATION operation, ref ULOGONINFO logon, ref PAOBJECT obj)
        {
            try
            {
                m_logon = logon;
                sz_newtimestamp = UCommon.UGetFullDateTimeNow().ToString();
                String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
                obj.l_timestamp = long.Parse(l_timestamp);
                String sz_sql;
                String sz_name = obj.sz_name != null?obj.sz_name.Replace("'", "''"):"";
                String sz_address = obj.sz_address != null?obj.sz_address.Replace("'", "''"):"";
                String sz_postno = obj.sz_postno != null?obj.sz_postno.Replace("'", "''"):"";
                String sz_place = obj.sz_place != null?obj.sz_place.Replace("'", "''"):"";
                String sz_phone = obj.sz_phone != null?obj.sz_phone.Replace("'", "''"):"";

                sz_sql = String.Format(UCommon.UGlobalizationInfo,
                    "sp_ins_paobject '{0}', {1}, {2}, {3}, {4}, {5}, '{6}', {7}, {8}, '{9}', '{10}', '{11}', '{12}', {13}, {14}",
                       operation.ToString().ToLower(), obj.l_objectpk, logon.l_userpk, logon.l_comppk, logon.l_deptpk, obj.l_importpk, sz_name, obj.l_categorypk,
                       obj.l_parent, sz_address, sz_postno, sz_place, sz_phone, l_timestamp, (obj.b_isobjectfolder ? 1 : 0));

                long n_ret = db_exec(sz_sql, "paobject", operation.ToString().ToLower(), obj.l_temppk.ToString(), obj.sz_description, false);
                obj.l_objectpk = n_ret;
                if (n_ret > 0)
                {
                    switch(operation)
                    {
                        case PARMOPERATION.insert:
                        case PARMOPERATION.update:
                            if (obj.m_shape != null)
                            {
                                if(typeof(UPolygon).Equals(obj.m_shape.GetType()))
                                {
                                    write_server_shape(ref obj);
                                }
                            }
                            break;
                        case PARMOPERATION.delete:
                            try
                            {
                                _delete_server_shape_file("o", obj.l_objectpk.ToString());
                                db.DeletePAShape(obj.l_objectpk, PASHAPETYPES.PAOBJECT);
                            }
                            catch (Exception) { }
                            break;
                    }
                }

                return n_ret;

            }
            catch (Exception)
            {
                throw;
            }

        }

        public long HandlePAObjectUpdate(PARMOPERATION operation, ref ULOGONINFO logon, ref PAOBJECT obj, PASHAPETYPES type)
        {
            OdbcDataReader rs = null;
            try
            {
                long n_ret = 0;
                m_logon = logon;
                sz_newtimestamp = UCommon.UGetFullDateTimeNow().ToString();
                String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
                obj.l_timestamp = long.Parse(l_timestamp);
                String sz_sql;
                String sz_name = obj.sz_name != null ? obj.sz_name.Replace("'", "''") : "";
                String sz_address = obj.sz_address != null ? obj.sz_address.Replace("'", "''") : "";
                String sz_postno = obj.sz_postno != null ? obj.sz_postno.Replace("'", "''") : "";
                String sz_place = obj.sz_place != null ? obj.sz_place.Replace("'", "''") : "";
                String sz_phone = obj.sz_phone != null ? obj.sz_phone.Replace("'", "''") : "";
                /*
                sz_sql = String.Format(UCommon.UGlobalizationInfo,
                    "sp_ins_paobject '{0}', {1}, {2}, {3}, {4}, {5}, '{6}', {7}, {8}, '{9}', '{10}', '{11}', '{12}', {13}, {14}",
                       operation.ToString().ToLower(), obj.l_objectpk, logon.l_userpk, logon.l_comppk, logon.l_deptpk, obj.l_importpk, sz_name, obj.l_categorypk,
                       obj.l_parent, sz_address, sz_postno, sz_place, sz_phone, l_timestamp, (obj.b_isobjectfolder ? 1 : 0));

                long n_ret = db_exec(sz_sql, "paobject", operation.ToString().ToLower(), obj.l_temppk.ToString(), obj.sz_description, false);
                */
                if (operation == PARMOPERATION.insert) // I had to put it here or else it wouldn't write the shapefile and couldn't put it in the switch
                {
                    sz_sql = String.Format("sp_cb_ins_dept_restriction {0}, {1}, {2}, '{3}', '{4}', '{5}', {6}, {7}, '{8}', {9}, {10}", logon.l_userpk, logon.l_comppk, logon.l_deptpri, obj.sz_name, logon.sz_password, obj.sz_name /*what should I put here?*/, 1, 1, logon.sz_stdcc, 1000, 0);
                    rs = db.ExecReader(sz_sql,UmsDb.UREADER_AUTOCLOSE);
                    while (rs.Read())
                        n_ret = rs.GetInt32(0);
                    rs.Close();
                }
                /*else
                    obj.l_objectpk = n_ret;
                */
                if (n_ret > 0)
                {
                    switch (operation)
                    {
                        case PARMOPERATION.insert:
                            String md5 = "";
                            String xml = "";
                            obj.m_shape.CreateXml(ref xml, ref md5);
                            sz_sql = String.Format("INSERT INTO PASHAPE (l_pk, l_type, l_timestamp, sz_md5, sz_xml) values({0}, {1}, {2}, '{3}', '{4}')", n_ret, (int)type, db.getDbClock(), md5, xml);
                            db.ExecNonQuery(sz_sql);
                            break;
                        case PARMOPERATION.update:
                            if (obj.m_shape != null)
                            {
                                if (typeof(UPolygon).Equals(obj.m_shape.GetType()))
                                {
                                    write_server_shape(ref obj, type);
                                }
                            }
                            break;
                        case PARMOPERATION.delete:
                            try
                            {
                                _delete_server_shape_file("o", obj.l_objectpk.ToString());
                                db.DeletePAShape(obj.l_objectpk, PASHAPETYPES.PAOBJECT);
                            }
                            catch (Exception) { }
                            break;
                    }
                    
                }
                
                return n_ret;

            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (rs != null && !rs.IsClosed)
                    rs.Close();
            }

        }

        /*new function for insert/update/delete of events, by using PAEVENT class*/
        public long HandleEventUpdate(PARMOPERATION operation, ref ULOGONINFO logon, ref PAEVENT ev)
        {
            try
            {
                m_logon = logon;
                sz_newtimestamp = UCommon.UGetFullDateTimeNow().ToString();
                String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
                ev.l_timestamp = long.Parse(l_timestamp);
                String sz_name = ev.sz_name.Replace("'", "''");
                String sz_description = ev.sz_description.Replace("'", "''");
                String sz_sql;
                sz_sql = String.Format(UCommon.UGlobalizationInfo, "sp_ins_paevent '{0}', {1}, {2}, {3}, {4}, '{5}', {6}, {7}, {8}, {9}, {10}",
                                        operation.ToString(), ev.l_eventpk, logon.l_userpk, logon.l_comppk,
                                        ev.l_parent, sz_name, ev.l_categorypk, ev.l_timestamp, ev.f_epi_lon, ev.f_epi_lat, logon.l_deptpk);
                return db_exec(sz_sql, "paevent", operation.ToString().ToLower(), ev.l_temppk.ToString(), sz_description, false);

            }
            catch (Exception)
            {
                throw;
            }
        }

        /*new function for insert/update/delete of alerts, by using PAALERT class*/
        public long HandleAlertUpdate(PARMOPERATION operation, ref ULOGONINFO logon, ref PAALERT a)
        {
            try
            {
                m_logon = logon;
                sz_newtimestamp = UCommon.UGetFullDateTimeNow().ToString();
                String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
                a.l_timestamp = long.Parse(l_timestamp).ToString();

                String sz_name = a.sz_name.Replace("'", "''");
                String sz_description = a.sz_description.Replace("'", "''");
                String sz_oadc = a.sz_sms_oadc.Replace("'", "''");
                String sz_message = a.sz_sms_message.Replace("'", "''");
                String sz_sql;
                sz_sql = String.Format(UCommon.UGlobalizationInfo, "sp_ins_paalert '{0}', {1}, {2}, {3}, {4}, '{5}', {6}, {7}, '{8}', {9}, {10}, {11}, {12}, {13}, {14}, {15}, '{16}', '{17}', '{18}', {19}",
                                        operation.ToString().ToLower(), a.l_alertpk, logon.l_userpk, logon.l_comppk, a.l_parent,
                                        sz_name, a.l_profilepk, a.l_schedpk, a.sz_oadc, a.l_validity, a.l_addresstypes, a.l_timestamp,
                                        a.f_locked, a.n_maxchannels, a.n_requesttype, a.n_expiry, sz_oadc, sz_message, "-1", logon.l_deptpk);
                long n_ret = db_exec(sz_sql, "paalert", operation.ToString().ToLower(), a.l_alertpk.ToString(), sz_description, false);
                if (n_ret > 0) //ok
                {
                    a.l_alertpk = n_ret;
                    a.l_temppk = n_ret;
                    if (operation.ToString().ToLower().Equals("delete"))
                    {
                        try
                        {
                            String sz_areaid = db.GetAlertAreaID(a.l_alertpk.ToString());
                            if (!sz_areaid.Equals("-2") && !sz_areaid.Equals("-1") && !sz_areaid.Equals("0"))
                                CreateLBADeleteFile(a.l_alertpk.ToString(), sz_areaid);
                        }
                        catch (Exception) { }
                        try
                        {
                            _delete_server_shape_file("a", a.l_alertpk.ToString());
                            db.DeletePAShape(a.l_alertpk, PASHAPETYPES.PAALERT);
                        }
                        catch (Exception) { }
                    }
                    else //if it's not a delete op, update the shape(s)
                    {
                        if (a.m_shape != null)
                        {
                            write_server_shape(ref a, operation);
                        }
                        /*if (a.m_lba_shape != null && (a.l_addresstypes & (long)ADRTYPES.LBA_TEXT) == (long)ADRTYPES.LBA_TEXT)
                        {
                            _write_server_lbashape(ref a);
                        }*/
                    }
                }
                return n_ret;
            }
            catch (Exception)
            {
                throw;
            }
        }

        private bool parseEvent(ref XmlNode node, String sz_operation)
        {
            String l_eventpk = node.Attributes["l_eventpk"].Value;
            String l_temppk = l_eventpk;
            if (!sz_operation.ToLower().Equals("insert"))
                l_eventpk = l_eventpk.Substring(1);
            String l_objectpk = l_temppk;
            String l_parent = node.Attributes["l_parent"].Value;
            String sz_name = node.Attributes["sz_name"].Value.Replace("'", "''");
            String sz_description = node.Attributes["sz_description"].Value.Replace("'", "''");
            String l_categorypk = node.Attributes["l_categorypk"].Value;
            String f_epi_lon = node.Attributes["f_epi_lon"].Value;
            String f_epi_lat = node.Attributes["f_epi_lat"].Value;
            if (f_epi_lat.Length == 0)
                f_epi_lat = "0";
            if (f_epi_lon.Length == 0)
                f_epi_lon = "0";
            String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
            if (!sz_operation.ToLower().Equals("insert"))
            {
                if (l_parent.Length > 0)
                    l_parent = l_parent.Substring(1);
                else
                    l_parent = "-1";
            }
            else
            {
                if (l_parent.IndexOf('o') == 0 || l_parent.IndexOf('e') == 0 || l_parent.IndexOf('a') == 0)
                {
                    if (l_parent.Length > 0)
                        l_parent = l_parent.Substring(1);
                }
                else
                {
                    l_parent = find_pk_from_temp(l_parent, "paevent");
                }
            }
            if (l_categorypk.Length > 0)
                l_categorypk = l_categorypk.Substring(1);

            //	sz_sql = "sp_ins_paevent '" & lcase(sz_status) & "', " & l_eventpk & ", " & Session("lUserPk") & ", " 
            //& Session("lCompanyPk") & ", " & l_parent & ", '" & sz_name & "', " & l_categorypk & ", " & l_timestamp & 
            //", " & f_epi_lon & ", " & f_epi_lat

            String sz_sql;
            sz_sql = String.Format(UCommon.UGlobalizationInfo, "sp_ins_paevent '{0}', {1}, {2}, {3}, {4}, '{5}', {6}, {7}, {8}, {9}, {10}",
                                    sz_operation.ToLower(), l_eventpk, m_logon.l_userpk, m_logon.l_comppk,
                                    l_parent, sz_name, l_categorypk, l_timestamp, f_epi_lon, f_epi_lat, m_logon.l_deptpk);

            db_exec(sz_sql, "paevent", sz_operation, l_eventpk, sz_description, true);

            return true;
        }
        private bool parseAlert(ref XmlNode node, String sz_operation)
        {
            String l_alertpk = node.Attributes["l_alertpk"].Value;
            String l_temppk = l_alertpk;
            if (!sz_operation.ToLower().Equals("insert"))
                l_alertpk = l_alertpk.Substring(1);
            String l_objectpk = l_temppk;
            String l_parent = node.Attributes["l_parent"].Value;
            String sz_name = node.Attributes["sz_name"].Value.Replace("'", "''");
            String sz_description = node.Attributes["sz_description"].Value.Replace("'", "''");
            String l_profilepk = node.Attributes["l_profilepk"].Value;
            String l_schedpk = node.Attributes["l_schedpk"].Value;
            String sz_oadc = node.Attributes["sz_oadc"].Value;
            String l_validity = node.Attributes["l_validity"].Value;
            String f_locked = node.Attributes["f_locked"].Value;
            String l_addresstypes = node.Attributes["l_addresstypes"].Value;
            String l_maxchannels = node.Attributes["l_maxchannels"].Value;
            String l_requesttype = node.Attributes["l_requesttype"].Value;
            String l_expiry = node.Attributes["l_expiry"].Value;
            String sz_sms_oadc = node.Attributes["sz_sms_oadc"].Value;
            String sz_sms_message = node.Attributes["sz_sms_message"].Value;

            String l_timestamp = UCommon.UGetFullDateTimeNow().ToString();
            if (f_locked.Length==0)
                f_locked = "0";
            if (l_validity.Length==0)
                l_validity="1";

            if (!sz_operation.ToLower().Equals("insert"))
            {
                if (l_parent.Length > 0)
                    l_parent = l_parent.Substring(1);
                else
                    l_parent = "-1";
            }
            else
            {
                if (l_parent.IndexOf('o') == 0 || l_parent.IndexOf('e') == 0 || l_parent.IndexOf('a') == 0)
                {
                    if (l_parent.Length > 0)
                        l_parent = l_parent.Substring(1);
                }
                else
                {
                    l_parent = find_pk_from_temp(l_parent, "paevent");
                }
            }
	//sz_sql = "sp_ins_paalert '" & lcase(sz_status) & "', " & l_alertpk & ", " & Session("lUserPk") & ", " & 
            //Session("lCompanyPk") & ", " & l_parent & ", '" & sz_name & "', " & l_profilepk & ", " & l_schedpk &_
	//		", '" & sz_oadc & "', " & l_validity & ", " & l_addresstypes & ", " & l_timestamp & ", " & f_locked

            if (sz_operation.Equals("delete"))
            {
                try
                {
                    String sz_areaid = db.GetAlertAreaID(l_alertpk);
                    if (!sz_areaid.Equals("-2") && !sz_areaid.Equals("-1") && !sz_areaid.Equals("0"))
                        CreateLBADeleteFile(l_alertpk, sz_areaid);
                }
                catch (Exception) { }
            }


            String sz_sql;
            sz_sql = String.Format("sp_ins_paalert '{0}', {1}, {2}, {3}, {4}, '{5}', {6}, {7}, '{8}', {9}, {10}, {11}, {12}, {13}, {14}, {15}, '{16}', '{17}', '{18}', {19}",
                                    sz_operation.ToLower(), l_alertpk, m_logon.l_userpk, m_logon.l_comppk, l_parent,
                                    sz_name, l_profilepk, l_schedpk, sz_oadc, l_validity, l_addresstypes, l_timestamp,
                                    f_locked, l_maxchannels, l_requesttype, l_expiry, sz_sms_oadc, sz_sms_message, "-1", m_logon.l_deptpk);
            try
            {
                db_exec(sz_sql, "paalert", sz_operation, l_alertpk, sz_description, true);
            }
            catch (Exception)
            {
                ULog.error(sz_sql);
            }
            return true;
        }

        private long db_exec(String sql, String sz_nodename, String sz_operation, String temppk, String sz_description,
            bool b_add_to_pkref)
        {
            String realpk = "-1";
            OdbcDataReader rs = null;
            if (sql.Length > 0)
            {
                try
                {
                    rs = db.ExecReader(sql, UmsDb.UREADER_AUTOCLOSE);
                    if (rs.Read())
                    {
                        realpk = rs.GetString(0);
                    }
                    rs.Close();
                    if (sz_operation.Equals("insert"))
                    {
                        if(b_add_to_pkref)
                            addPkRef(temppk, realpk, sz_nodename);
                        setDescription(sz_nodename, realpk, sz_description);
                    }
                    else if (sz_operation.Equals("update"))
                    {
                        //realpk = temppk;
                        setDescription(sz_nodename, realpk, sz_description);
                    }
                    else if (sz_operation.Equals("delete"))
                    {

                    }
                    addStatusText(sz_nodename, sz_operation, realpk, temppk, "S_OK", "");

                    try
                    {
                        if (realpk.Length > 0)
                            return long.Parse(realpk);
                        else
                            return -1;
                    }
                    catch (Exception e)
                    {
                        return -1;
                    }
                }
                catch (Exception e)
                {
                    throw new UDbQueryException(e.Message);
                }
                finally
                {
                    if (rs != null && !rs.IsClosed)
                        rs.Close();
                }
            }
            return -2;
        }

        bool setDescription(String sz_nodename, String l_objpk, String sz_desc)
        {
            //sz_sql = "UPDATE " & uCase(sz_nodetype) & " SET sz_description='" & sz_desc & "' WHERE " & pkid & "=" & l_objpk
            String sz_table = sz_nodename.ToUpper();
            String sz_pkid = "";
            if (sz_table.Equals("PAALERT"))
                sz_pkid = "l_alertpk";
            else if (sz_table.Equals("PAEVENT"))
                sz_pkid = "l_eventpk";
            else if (sz_table.Equals("PAOBJECT"))
                sz_pkid = "l_objectpk";

            String sz_sql = String.Format("UPDATE {0} SET sz_description='{1}' WHERE {2}={3}",
                                          sz_table, sz_desc.Replace("'","''"), sz_pkid, l_objpk);
            try
            {
                db.ExecNonQuery(sz_sql);
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        void addStatusText(String sz_nodename, String sz_operation, String l_pk, String l_temppk, String status, String statustext)
        {
            try
            {
                String sz_prefix = "u";
                if (sz_nodename.Equals("paobject"))
                    sz_prefix = "o";
                else if (sz_nodename.Equals("paevent"))
                    sz_prefix = "e";
                else if (sz_nodename.Equals("paalert"))
                    sz_prefix = "a";
                else if (sz_nodename.Equals("pacategory"))
                    sz_prefix = "c";
                outxml.insertStartElement(sz_nodename);
                outxml.insertAttribute("sz_operation", sz_operation);
                outxml.insertAttribute("l_objectpk", sz_prefix + l_pk);
                outxml.insertAttribute("l_temppk", (l_pk.Equals(l_temppk) ? sz_prefix : "") + l_temppk);
                outxml.insertAttribute("sz_status", status);
                outxml.insertAttribute("l_timestamp", UCommon.UGetFullDateTimeNow().ToString());
                outxml.insertEndElement();
            }
            catch (Exception e)
            {
            }

        }

    }
    
}
