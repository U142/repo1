﻿using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using com.ums.UmsCommon;
using com.ums.PAS.Database;
using com.ums.UmsDbLib;
using com.ums.UmsFile;
using com.ums.UmsParm;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using com.ums.ZipLib;



namespace com.ums.PAS.Status
{
    public class UStatusItemSearchParams
    {

        public Int64 _l_projectpk;
        public int _l_item_filter;
        public int _l_date_filter;
        public int _l_time_filter;
        public long[] _l_refno_filter; //these refno's are only for update

        public UStatusItemSearchParams()
        {
        }

        public UStatusItemSearchParams(Int64 l_projectpk, int l_item_filter, int l_date_filter, int l_timefilter, long[] l_refno_filter)
        {
            _l_projectpk = l_projectpk;
            _l_item_filter = l_item_filter;
            _l_date_filter = l_date_filter;
            _l_time_filter = l_timefilter;
            _l_refno_filter = l_refno_filter;
        }
    }





    public class UStatusItemSearch
    {
        ULOGONINFO m_logon;
        UStatusItemSearchParams m_search;
        UStatusItemsDb m_db;
        private USimpleXmlWriter outxml;


        public UStatusItemSearch(ref ULOGONINFO logoninfo, ref UStatusItemSearchParams search)
        {
            m_logon = logoninfo;
            m_search = search;


        }

        public byte[] Find()
        {
            //Connect to database
            UmsDbConnParams dbparams = new UmsDbConnParams();
            dbparams.sz_dsn = UCommon.UBBDATABASE.sz_dsn;
            dbparams.sz_uid = UCommon.UBBDATABASE.sz_uid;
            dbparams.sz_pwd = UCommon.UBBDATABASE.sz_pwd;

            try
            {
                m_db = new UStatusItemsDb(dbparams);
            }
            catch (Exception)
            {
                throw new UDbConnectionException();
            }

            if (!m_db.CheckLogon(ref m_logon))
            {
                throw new ULogonFailedException();
            }

            BBPROJECT proj = new BBPROJECT();

            //create output xml file
            String sz_encoding = "utf-8";
            try
            {
                outxml = new USimpleXmlWriter(sz_encoding);
            }
            catch (Exception)
            {
            }


            outxml.insertStartDocument();
            outxml.insertComment("UMS Status file");
            m_db.GetProjectInfo(m_search._l_projectpk, ref proj);

            //TOP LEVEL
            outxml.insertStartElement("STATUSITEMS");
            
            
            outxml.insertStartElement("SENDINGS");
            outxml.insertAttribute("l_projectpk", proj.sz_projectpk);
            outxml.insertAttribute("sz_name", proj.sz_name);
            outxml.insertAttribute("l_created", proj.sz_created);
            outxml.insertAttribute("l_updated", proj.sz_updated);
            outxml.insertAttribute("l_deptpk", proj.n_deptpk.ToString());
            outxml.insertAttribute("l_count", proj.n_sendingcount.ToString());

            //WRITE SENDING TAGS AND SHAPEINFO FOUND IN mapsendings PATH
            try
            {
                m_db.GetSendingInfosByProject(ref proj); //BBPROJECT.mdvsendinginfo array is now filled
            }
            catch (Exception)
            {
                //sql failed
            }
            for (int r = 0; r < proj.mdvsendinginfo.Count; r++)
            {
                MDVSENDINGINFO mdv = proj.mdvsendinginfo[r];
                //sending
                outxml.insertStartElement("SENDING");
                outxml.insertAttribute("sz_sendingname", mdv.sz_sendingname);
                outxml.insertAttribute("l_refno", mdv.l_refno.ToString());
                outxml.insertAttribute("l_group", mdv.l_group.ToString());
                outxml.insertAttribute("l_createdate", mdv.l_createdate);
                outxml.insertAttribute("l_createtime", mdv.l_createtime);
                outxml.insertAttribute("l_scheddate", mdv.l_scheddate);
                outxml.insertAttribute("l_schedtime", mdv.l_schedtime);
                outxml.insertAttribute("l_sendingstatus", mdv.l_sendingstatus.ToString());
                outxml.insertAttribute("l_comppk", mdv.l_companypk.ToString());
                outxml.insertAttribute("l_deptpk", mdv.l_deptpk.ToString());
                outxml.insertAttribute("l_type", mdv.l_type.ToString());
                outxml.insertAttribute("l_addresstypes", mdv.l_addresstypes.ToString());
                outxml.insertAttribute("l_profilepk", mdv.l_profilepk.ToString());
                outxml.insertAttribute("l_queuestatus", mdv.l_queuestatus.ToString());
                outxml.insertAttribute("l_totitem", mdv.l_totitem.ToString());
                outxml.insertAttribute("l_proc", mdv.l_processed.ToString());
                outxml.insertAttribute("l_altjmp", mdv.l_altjmp.ToString());
                outxml.insertAttribute("l_alloc", mdv.l_alloc.ToString());
                outxml.insertAttribute("l_maxalloc", mdv.l_maxalloc.ToString());
                outxml.insertAttribute("sz_oadc", mdv.sz_oadc);
                outxml.insertAttribute("l_qreftype", mdv.l_qreftype.ToString());
                outxml.insertAttribute("f_dynacall", mdv.f_dynacall.ToString());
                outxml.insertAttribute("l_nofax", mdv.l_nofax.ToString());


                //WRITE map data
                /*only do this if refno is not in refno_filter*/
                bool b_writeshapes = true;
                if (m_search._l_refno_filter != null)
                {
                    for (int x = 0; x < m_search._l_refno_filter.Length; x++)
                    {
                        if (mdv.l_refno == m_search._l_refno_filter[x])
                        {
                            b_writeshapes = false;
                            break;
                        }
                    }
                }

                if (b_writeshapes)
                {
                    //GET map data
                    UShape shape = null;
                    UBoundingRect bounding = null;
                    try
                    {
                        if (ParseMapFile(ref mdv, ref bounding, ref shape))
                        {
                        }
                    }
                    catch (Exception)
                    {
                    }
                    if (bounding == null)
                        bounding = new UBoundingRect();

                    switch (mdv.l_group)
                    {
                        case 3: //polygon
                            {
                                for (int i = 0; i < shape.poly().getSize(); i++)
                                {
                                    outxml.insertStartElement("POLYPOINT");
                                    outxml.insertAttribute("lat", shape.poly().getPoint(i).getLat().ToString(UCommon.UGlobalizationInfo));
                                    outxml.insertAttribute("lon", shape.poly().getPoint(i).getLon().ToString(UCommon.UGlobalizationInfo));
                                    outxml.insertEndElement();
                                }
                            }
                            break;
                        case 4: //gemini
                            {

                            }
                            break;
                        case 8: //ellipse
                            {
                                outxml.insertStartElement("ELLIPSE");
                                outxml.insertAttribute("f_centerx", shape.ellipse().lon.ToString(UCommon.UGlobalizationInfo));
                                outxml.insertAttribute("f_centery", shape.ellipse().lat.ToString(UCommon.UGlobalizationInfo));
                                outxml.insertAttribute("f_radiusx", shape.ellipse().x.ToString(UCommon.UGlobalizationInfo));
                                outxml.insertAttribute("f_radiusy", shape.ellipse().y.ToString(UCommon.UGlobalizationInfo));
                                outxml.insertEndElement(); //ELLIPSE
                            }
                            break;
                    }
                }

                outxml.insertEndElement(); //SENDING
            }
            outxml.insertEndElement(); //SENDINGS


            UNonFinalStatusCodes nonfinalstatuscodes = null;
            try
            {
                nonfinalstatuscodes = m_db.GetNotFinalStatusCodes(ref m_search);
            }
            catch (Exception)
            {
            }

            //WRITE STATUSITEMLIST
            //Write item list for all sendings
            outxml.insertStartElement("STATUSITEMLIST");
            outxml.insertAttribute("l_refno", "-1");
            outxml.insertAttribute("records", "0");
            outxml.insertAttribute("totitem", "0");
            outxml.insertAttribute("lbo", "0");//bounding._left.ToString());
            outxml.insertAttribute("rbo", "0");//bounding._right.ToString());
            outxml.insertAttribute("ubo", "0");//bounding._top.ToString());
            outxml.insertAttribute("bbo", "0");//bounding._bottom.ToString());
            outxml.insertAttribute("parsing", (nonfinalstatuscodes != null ? nonfinalstatuscodes.n_parsing.ToString() : "0"));
            outxml.insertAttribute("queue", (nonfinalstatuscodes != null ? nonfinalstatuscodes.n_queue.ToString() : "0"));
            outxml.insertAttribute("sending", (nonfinalstatuscodes != null ? nonfinalstatuscodes.n_sending.ToString() : "0"));

            try
            {
                List<UStatusItem> items = m_db.GetStatusItems(ref m_search);
                for (int i = 0; i < items.Count; i++)
                {
                    UStatusItem item = items[i];
                    outxml.insertStartElement("STATUSITEM");
                    outxml.insertAttribute("l_refno", item.n_refno.ToString());
                    outxml.insertAttribute("l_item", item.n_item.ToString());
                    outxml.insertAttribute("l_adrpk", item.n_adrpk.ToString());
                    outxml.insertAttribute("l_lon", item.f_lat.ToString(UCommon.UGlobalizationInfo));
                    outxml.insertAttribute("l_lat", item.f_lon.ToString(UCommon.UGlobalizationInfo));
                    outxml.insertAttribute("sz_adrname", item.sz_adrname);
                    outxml.insertAttribute("sz_postaddr", item.sz_postaddr);
                    outxml.insertAttribute("sz_postno", item.sz_postno);
                    outxml.insertAttribute("sz_postarea", item.sz_postarea);
                    outxml.insertAttribute("l_date", item.n_date.ToString());
                    outxml.insertAttribute("l_time", item.n_time.ToString());
                    outxml.insertAttribute("l_status", item.n_status.ToString());
                    outxml.insertAttribute("sz_number", item.sz_number);
                    outxml.insertAttribute("l_tries", item.n_tries.ToString());
                    outxml.insertAttribute("l_channel", item.n_channel.ToString());
                    outxml.insertAttribute("l_pcid", item.n_pcid.ToString());
                    outxml.insertAttribute("l_seconds", item.n_seconds.ToString());
                    outxml.insertAttribute("l_changedate", item.n_changedate.ToString());
                    outxml.insertAttribute("l_changetime", item.n_changetime.ToString());
                    outxml.insertAttribute("l_ldate", item.n_ldate.ToString());
                    outxml.insertAttribute("l_ltime", item.n_ltime.ToString());

                    outxml.insertEndElement();
                }
            }
            catch (Exception)
            {

            }

            outxml.insertEndElement(); //STATUSITEMLIST




            //WRITE STATUSCODES

            outxml.insertStartElement("STATUSCODES");
            try
            {
                List<UStatusCode> codes = m_db.GetStatusCodes(ref m_search);
                for(int i=0; i < codes.Count; i++)
                {
                    outxml.insertStartElement("CODE");
                    outxml.insertAttribute("l_status", codes[i].n_status.ToString());
                    outxml.insertAttribute("sz_name", codes[i].sz_status);
                    outxml.insertAttribute("f_userdefined", codes[i].b_userdef_text.ToString());
                    outxml.insertEndElement(); //CODE
                }
            }
            catch (Exception)
            {
            }

            outxml.insertEndElement(); //STATUSCODES



            //WRITE LBA info
            for(int lba = 0; lba < proj.mdvsendinginfo.Count; lba++)
            {
                MDVSENDINGINFO mdv = proj.mdvsendinginfo[lba];
                if((mdv.l_addresstypes & (int)ADRTYPES.LBA_TEXT)==(int)ADRTYPES.LBA_TEXT ||
                    (mdv.l_addresstypes & (int)ADRTYPES.LBA_VOICE)==(int)ADRTYPES.LBA_VOICE)
                {
                    outxml.insertStartElement("LBASEND");
                    try
                    {
                        ULBASENDING lbasending = m_db.GetLBASending(mdv.l_refno);
                        outxml.insertAttribute("l_parentrefno", mdv.l_refno.ToString());
                        outxml.insertAttribute("l_cbtype", "2");
                        outxml.insertAttribute("l_status", lbasending.l_status.ToString());
                        outxml.insertAttribute("l_response", lbasending.l_response.ToString());
                        outxml.insertAttribute("l_items", lbasending.l_items.ToString());
                        outxml.insertAttribute("l_proc", lbasending.l_proc.ToString());
                        outxml.insertAttribute("l_retries", lbasending.l_retries.ToString());
                        outxml.insertAttribute("l_requesttype", lbasending.l_requesttype.ToString());
                        outxml.insertAttribute("sz_jobid", lbasending.sz_jobid);
                        outxml.insertAttribute("sz_areaid", lbasending.sz_areaid);
                        outxml.insertAttribute("f_simulation", lbasending.f_simulation.ToString());
                        for (int st = 0; st < lbasending.histcc.Count; st++)
                        {
                            ULBAHISTCC cc = lbasending.histcc[st];
                            outxml.insertStartElement("LBAHISTCC");
                            //outxml.insertAttribute("l_projectpk", proj.sz_projectpk);
                            outxml.insertAttribute("sz_ccode", cc.l_cc.ToString());
                            outxml.insertAttribute("l_delivered", cc.l_delivered.ToString());
                            outxml.insertAttribute("l_expired", cc.l_expired.ToString());
                            outxml.insertAttribute("l_failed", cc.l_failed.ToString());
                            outxml.insertAttribute("l_unknown", cc.l_unknown.ToString());
                            outxml.insertAttribute("l_submitted", cc.l_submitted.ToString());
                            outxml.insertAttribute("l_queued", cc.l_queued.ToString());
                            outxml.insertAttribute("l_subscribers", cc.l_subscribers.ToString());
                            outxml.insertEndElement(); //STATUS
                        }
                        outxml.insertStartElement("LBASEND_TS");
                        for (int c = 0; c < lbasending.send_ts.Count; c++)
                        {
                            ULBASEND_TS ts = lbasending.send_ts[c];
                            outxml.insertStartElement("TS");
                            outxml.insertAttribute("l_status", ts.l_status.ToString());
                            outxml.insertAttribute("l_ts", ts.l_ts.ToString());
                            outxml.insertEndElement(); //TS
                        }
                        outxml.insertEndElement(); //LBASEND_TS
                    }
                    catch (Exception e)
                    {
                        ULog.error(0, "Error fetching LBA status", e.Message);
                    }

                    outxml.insertEndElement(); //LBASEND
                }
            }


            outxml.insertEndElement(); //STATUSITEMS
            outxml.insertEndDocument();

            m_db.close();
            //return new byte[1];
            outxml.finalize();


            /*try
            {
                outxml.GetXmlDocument().Save("d:\\slettes\\status.xml");
            }
            catch (Exception e)
            {
            }*/

            UZipLib zip = new UZipLib(UCommon.UPATHS.sz_path_parmtemp, "statuszip." + Guid.NewGuid() + ".zip");
            try
            {
                zip.AddTextAsZipFileEntry("status", outxml.getXml(), Encoding.GetEncoding(sz_encoding));
                zip.finalize();
                return zip.ReadZipFileBytes();
            }
            catch (Exception e)
            {
                ULog.error(0, "Error writing Status ZIP", e.Message);
                throw e;
            }
            //return outxml.getXml();
            //XmlDocument xml = new XmlDocument();
            //xml.LoadXml(Encoding.UTF8.(outxml.getXml().ToCharArray()));



            //return null;
        }

        private bool ParseMapFile(ref MDVSENDINGINFO mdv, ref UBoundingRect rect, ref UShape shape)
        {
            UFile filename = new UFile(UCommon.UPATHS.sz_path_mapsendings, String.Format("{0}.adr", mdv.l_refno));
            TextReader reader = null;
            try
            {
                reader = new StreamReader(File.OpenRead(filename.full()));
            }
            catch(Exception e)
            {
                throw e;
            }
            rect = new UBoundingRect();

            bool b_valid_bounding = true;
            //read the first 4 lines (this is the bounding area)
            for (int i = 0; i < 4; i++)
            {
                String line = "";
                try
                {
                     line = reader.ReadLine();
                }
                catch (Exception e)
                {
                    throw e;
                }
                try
                {
                    Double d = Double.Parse(line, UCommon.UGlobalizationInfo);
                    switch (i)
                    {
                        case 0:
                            rect._left = d;
                            break;
                        case 1:
                            rect._top = d;
                            break;
                        case 2:
                            rect._right = d;
                            break;
                        case 3:
                            rect._bottom = d;
                            break;
                    }
                }
                catch (Exception)
                {
                    b_valid_bounding = false; //not all four bounding coordinates were found, do not use bounding area
                }
            }
            if (!b_valid_bounding)
                rect = null;


            switch (mdv.l_group)
            {
                case 2: //square
                    break;
                case 3://expect polygon
                    {
                        shape = new UPolygon();
                        bool b_continue = true;
                        try
                        {
                            while (b_continue)
                            {
                                //read pairs of coordinates
                                /*if (reader.Peek() != -1)
                                {
                                    break;
                                }*/
                                float x, y;
                                String str;
                                try
                                {
                                    str = reader.ReadLine();
                                }
                                catch (Exception)
                                {
                                    break;
                                }
                                try
                                {
                                    x = float.Parse(str, UCommon.UGlobalizationInfo);
                                }
                                catch (Exception)
                                {
                                    break;
                                }
                                try
                                {
                                    str = reader.ReadLine();
                                }
                                catch (Exception)
                                {
                                    break;
                                }
                                try
                                {
                                    y = float.Parse(str, UCommon.UGlobalizationInfo);
                                }
                                catch (Exception)
                                {
                                    break;
                                }
                                shape.poly().addPoint(x, y);
                            }
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                    break;
                case 4: //expect gemini
                    shape = null;
                    break;
                case 8: //expect ellipse
                    {
                        shape = new UEllipse();
                        //read 4 ellipse parameters
                        try
                        {
                            String str = reader.ReadLine();
                            shape.ellipse().lon = float.Parse(str, UCommon.UGlobalizationInfo);
                            str = reader.ReadLine();
                            shape.ellipse().lat = float.Parse(str, UCommon.UGlobalizationInfo);
                            str = reader.ReadLine();
                            shape.ellipse().x = float.Parse(str, UCommon.UGlobalizationInfo);
                            str = reader.ReadLine();
                            shape.ellipse().y = float.Parse(str, UCommon.UGlobalizationInfo);
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                    break;
            }
            reader.Close();
            return true;
        }
    }

}
