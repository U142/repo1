using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
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
using System.IO.Compression;



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

            if (!m_db.CheckLogon(ref m_logon, true))
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
            outxml.insertComment("UMS Population Alert Status file");
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

                //WRITE map data
                /*only do this if refno is not in refno_filter*/
                bool b_writeshapes = isRefnoInFilter(ref mdv);
                /*if (m_search._l_refno_filter != null)
                {
                    for (int x = 0; x < m_search._l_refno_filter.Length; x++)
                    {
                        if (mdv.l_refno == m_search._l_refno_filter[x])
                        {
                            b_writeshapes = false;
                            break;
                        }
                    }
                }*/

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
                outxml.insertAttribute("l_linktype", mdv.l_linktype.ToString());
                outxml.insertAttribute("l_resendrefno", mdv.l_resendrefno.ToString());
                outxml.insertAttribute("sz_messagetext", mdv.sz_messagetext.ToString());
                outxml.insertAttribute("sz_actionprofilename", mdv.sz_actionprofilename.ToString());
                try
                {
                    if (b_writeshapes)
                        outxml.insertAttribute("l_num_dynfiles", m_db.getNumDynfilesInProfile(mdv.l_profilepk).ToString());
                    else
                        outxml.insertAttribute("l_num_dynfiles", "-2");
                }
                catch (Exception e)
                {
                    outxml.insertAttribute("l_num_dynfiles", "-3");
                }
                outxml.insertAttribute("b_marked_as_cancelled", mdv.b_marked_as_cancelled.ToString());

                List<USMSINSTATS> smsinstats = m_db.GetSmsInStats(mdv.l_refno);
                outxml.insertStartElement("SMSINSTATS");
                for (int st = 0; st < smsinstats.Count; st++)
                {
                    USMSINSTATS stats = smsinstats[st];
                    outxml.insertStartElement("ANSWER");
                    outxml.insertAttribute("l_refno", stats.l_refno.ToString());
                    outxml.insertAttribute("l_answercode", stats.l_answercode.ToString());
                    outxml.insertAttribute("sz_description", stats.sz_description);
                    outxml.insertAttribute("l_count", stats.l_count.ToString());
                    outxml.insertEndElement(); //ANSWERS
                }
                outxml.insertEndElement(); //SMSINSTATS


                UBoundingRect bounding = null;
                if (b_writeshapes)
                {
                    //GET map data
                    UShape shape = null;
                    try
                    {
                        if (m_db.ShapeFromDb(ref mdv, ref shape))
                        {

                        }
                        //check validity
                        if (shape == null)
                        {
                            ParseMapFile(ref mdv, ref bounding, ref shape);
                        }
                        else //grab bounds
                        {
                            if (shape.GetType().Equals(typeof(UGIS)) && shape.gis().m_bounds!=null)
                            {
                                bounding = new UBoundingRect();
                                bounding._left = shape.gis().m_bounds.l_bo;
                                bounding._right = shape.gis().m_bounds.r_bo;
                                bounding._top = shape.gis().m_bounds.u_bo;
                                bounding._bottom = shape.gis().m_bounds.b_bo;
                            }
                            else if (shape.GetType().Equals(typeof(UMunicipalShape)) && shape.municipal().m_bounds!=null)
                            {
                                bounding = new UBoundingRect();
                                bounding._left = shape.municipal().m_bounds.l_bo;
                                bounding._right = shape.municipal().m_bounds.r_bo;
                                bounding._top = shape.municipal().m_bounds.u_bo;
                                bounding._bottom = shape.municipal().m_bounds.b_bo;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        ULog.error(mdv.l_refno, "Error deserializing statusshape from PASHAPE", e.Message);
                    }
                    try
                    {
                        if (shape == null)
                        {
                            ParseMapFile(ref mdv, ref bounding, ref shape);
                        }
                    }
                    catch (Exception)
                    {
                    }
                    if (bounding == null)
                        bounding = new UBoundingRect();

                    if (shape != null)
                    {
                        switch (mdv.l_group)
                        {
                            case 3: //polygon
                                {
                                    for (int i = 0; i < shape.poly().getSize(); i++)
                                    {
                                        try
                                        {
                                            outxml.insertStartElement("POLYPOINT");
                                            outxml.insertAttribute("lat", shape.poly().getPoint(i).getLat().ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertAttribute("lon", shape.poly().getPoint(i).getLon().ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertEndElement();
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                    }
                                }
                                break;
                            case 4: //gemini
                                {
                                    outxml.insertStartElement("BOUNDS");
                                    try
                                    {
                                        outxml.insertAttribute("lbo", shape.gis().m_bounds.l_bo.ToString(UCommon.UGlobalizationInfo)); //bounding._left.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("rbo", shape.gis().m_bounds.r_bo.ToString(UCommon.UGlobalizationInfo)); //bounding._right.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("ubo", shape.gis().m_bounds.u_bo.ToString(UCommon.UGlobalizationInfo)); //bounding._top.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("bbo", shape.gis().m_bounds.b_bo.ToString(UCommon.UGlobalizationInfo)); //bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                    }
                                    catch (Exception e)
                                    {
                                        outxml.insertAttribute("lbo", "-9999"); //bounding._left.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("rbo", "-9999"); //bounding._right.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("ubo", "-9999"); //bounding._top.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("bbo", "-9999"); //bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                    }
                                    outxml.insertEndElement();
                                }
                                break;
                            case 5: //TAS country
                                {
                                    try
                                    {
                                        if (bounding != null)
                                        {
                                            outxml.insertStartElement("BOUNDS");
                                            outxml.insertAttribute("lbo", bounding._left.ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertAttribute("rbo", bounding._right.ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertAttribute("ubo", bounding._top.ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertAttribute("bbo", bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                            outxml.insertEndElement();
                                        }
                                        if (shape != null)
                                        {
                                            outxml.insertStartElement("TASCOUNTRIES");
                                            for (int i = 0; i < shape.tas().countries.Count; i++)
                                            {
                                                outxml.insertStartElement("COUNTRY");
                                                outxml.insertAttribute("iso", shape.tas().countries[i].sz_iso);
                                                outxml.insertAttribute("iso-n", shape.tas().countries[i].l_iso_numeric.ToString());
                                                outxml.insertAttribute("cc", shape.tas().countries[i].l_cc.ToString());
                                                outxml.insertAttribute("name", shape.tas().countries[i].sz_name);
                                                outxml.insertEndElement();
                                            }
                                            outxml.insertEndElement();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                }
                                break;
                            case 8: //ellipse
                                {
                                    try
                                    {
                                        outxml.insertStartElement("ELLIPSE");
                                        outxml.insertAttribute("f_centerx", shape.ellipse().lon.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("f_centery", shape.ellipse().lat.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("f_radiusx", shape.ellipse().x.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("f_radiusy", shape.ellipse().y.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement(); //ELLIPSE
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                }
                                break;
                            case 9: //municipal
                                {
                                    if (bounding != null)
                                    {
                                        if (shape != null)
                                        {
                                            outxml.insertStartElement("MUNICIPALLIST");
                                            for (int i = 0; i < shape.municipal().GetMunicipals().Count; i++)
                                            {
                                                outxml.insertStartElement("MUNICIPAL");
                                                outxml.insertAttribute("id", shape.municipal().GetMunicipals()[i].sz_municipalid);
                                                outxml.insertAttribute("name", shape.municipal().GetMunicipals()[i].sz_municipalname);
                                                outxml.insertEndElement();
                                            }
                                            outxml.insertEndElement();
                                        }

                                        outxml.insertStartElement("BOUNDS");
                                        outxml.insertAttribute("lbo", bounding._left.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("rbo", bounding._right.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("ubo", bounding._top.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("bbo", bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement();
                                        /*outxml.insertStartElement("POLYPOINT");
                                        outxml.insertAttribute("lat", bounding._left.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("lon", bounding._top.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement();
                                        outxml.insertStartElement("POLYPOINT");
                                        outxml.insertAttribute("lat", bounding._right.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("lon", bounding._top.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement();
                                        outxml.insertStartElement("POLYPOINT");
                                        outxml.insertAttribute("lat", bounding._right.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("lon", bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement();
                                        outxml.insertStartElement("POLYPOINT");
                                        outxml.insertAttribute("lat", bounding._left.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertAttribute("lon", bounding._bottom.ToString(UCommon.UGlobalizationInfo));
                                        outxml.insertEndElement();*/

                                    }
                                }
                                break;
                        }
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
            catch (Exception e)
            {
                String err = e.Message;
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
                if(
                    (mdv.l_addresstypes & (int)ADRTYPES.LBA_TEXT)==(int)ADRTYPES.LBA_TEXT ||
                    (mdv.l_addresstypes & (int)ADRTYPES.LBA_VOICE)==(int)ADRTYPES.LBA_VOICE ||
                    (mdv.l_addresstypes & (int)ADRTYPES.SENDTO_TAS_SMS)==(int)ADRTYPES.SENDTO_TAS_SMS
                  )
                {
                    bool b_writeshapes = isRefnoInFilter(ref mdv);


                    //ULBASENDING lbasending = m_db.GetLBASending(mdv.l_refno);

                    List<ULBASENDING> lbasendings = m_db.GetLBASending_2_0(mdv.l_refno);
                    if (lbasendings != null)
                    {
                        for (int lbaoperator = 0; lbaoperator < lbasendings.Count; lbaoperator++)
                        {
                            ULBASENDING lbasending = lbasendings[lbaoperator];
                            if (lbasending != null)
                            {
                                outxml.insertStartElement("LBASEND");
                                try
                                {
                                    outxml.insertAttribute("l_refno", mdv.l_refno.ToString());
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
                                    outxml.insertAttribute("l_operator", lbasending.l_operator.ToString());
                                    outxml.insertAttribute("sz_operator", lbasending.sz_operator);
                                    outxml.insertAttribute("l_type", lbasending.l_type.ToString());
                                    outxml.insertAttribute("l_created_ts", lbasending.l_created_ts.ToString());
                                    outxml.insertAttribute("l_started_ts", lbasending.l_started_ts.ToString());
                                    outxml.insertAttribute("l_expires_ts", lbasending.l_expires_ts.ToString());
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
                                        outxml.insertAttribute("l_operator", cc.l_operator.ToString());
                                        outxml.insertEndElement(); //STATUS
                                    }
                                    outxml.insertStartElement("LBASEND_TS");
                                    for (int c = 0; c < lbasending.send_ts.Count; c++)
                                    {
                                        ULBASEND_TS ts = lbasending.send_ts[c];
                                        outxml.insertStartElement("TS");
                                        outxml.insertAttribute("l_status", ts.l_status.ToString());
                                        outxml.insertAttribute("l_ts", ts.l_ts.ToString());
                                        outxml.insertAttribute("l_operator", ts.l_operator.ToString());
                                        outxml.insertEndElement(); //TS
                                    }
                                    outxml.insertEndElement(); //LBASEND_TS

                                    try
                                    {
                                        if (b_writeshapes)
                                        {
                                            List<LBALanguage> lbatext = m_db.GetLBATextContent(mdv.l_refno);
                                            outxml.insertStartElement("LBALANGUAGES");
                                            for (int lang = 0; lang < lbatext.Count; lang++)
                                            {
                                                outxml.insertStartElement("LBALANGUAGE");
                                                outxml.insertAttribute("textpk", lbatext[lang].l_textpk.ToString());
                                                outxml.insertAttribute("name", lbatext[lang].getName());
                                                outxml.insertAttribute("oadc", lbatext[lang].getCBOadc());
                                                outxml.insertAttribute("text", lbatext[lang].getText());
                                                /*outxml.insertStartElement("CCODES");
                                                for (int langcc = 0; langcc < lbatext[lang].getCCodeCount(); langcc++)
                                                {
                                                    outxml.insertStartElement("country");
                                                    outxml.insertAttribute("cc", lbatext[lang].getCCode(langcc).getCCode());
                                                    outxml.insertEndElement(); //CC
                                                }
                                                outxml.insertEndElement(); //CCODES*/
                                                String cclist = "";
                                                for (int langcc = 0; langcc < lbatext[lang].getCCodeCount(); langcc++)
                                                {
                                                    if (langcc > 0)
                                                        cclist += ",";
                                                    cclist += lbatext[lang].getCCode(langcc).getCCode();
                                                }
                                                outxml.insertAttribute("ccodelist", cclist);

                                                outxml.insertEndElement();//LBALANGAUGE
                                            }
                                            outxml.insertEndElement(); //LBALANGUAGES
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                    }

                                }
                                catch (Exception e)
                                {
                                    ULog.error(0, "Error fetching LBA status for refno " + mdv.l_refno, e.Message);
                                }

                                outxml.insertEndElement(); //LBASEND

                            }
                        }



                    }
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

            /*UZipLib zip = new UZipLib(UCommon.UPATHS.sz_path_parmtemp, "statuszip." + Guid.NewGuid() + ".zip");
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
            }*/
            UGZipLib zip = new UGZipLib(UCommon.UPATHS.sz_path_parmtemp, "statuszip." + Guid.NewGuid() + ".zip");
            try
            {
                String xml = outxml.getXml();
                return zip.getZipped(outxml.getXml());
            }
            catch (Exception e)
            {
                ULog.error(0, "Error writing Status ZIP", e.Message);
                throw;
            }

            //return outxml.getXml();
            //XmlDocument xml = new XmlDocument();
            //xml.LoadXml(Encoding.UTF8.(outxml.getXml().ToCharArray()));



            //return null;
        }

        protected bool isRefnoInFilter(ref MDVSENDINGINFO mdv)
        {
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
            return b_writeshapes;
        }


        private bool ParseMapFile(ref MDVSENDINGINFO mdv, ref UBoundingRect rect, ref UShape shape)
        {
            UFile filename = new UFile(UCommon.UPATHS.sz_path_mapsendings, String.Format("{0}.adr", mdv.l_refno));
            TextReader reader = null;
            try
            {
                reader = new StreamReader(File.OpenRead(filename.full()));
            }
            catch(Exception)
            {
                throw;
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
                catch (Exception)
                {
                    throw;
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
                        catch (Exception)
                        {
                            throw;
                        }
                    }
                    break;
                case 4: //expect gemini
                    shape = null;
                    break;
                case 5: //expect TAS country
                    {//4 first lines are bounds (start of function), then a list of countries follows
                        try
                        {
                            shape = new UTasShape();
                            bool b_continue = true;
                            while(b_continue)
                            {
                                String str;
                                str = reader.ReadLine();
                                if (str == null)
                                    break;
                                String [] country = str.Split(';');
                                ULBACOUNTRY lba = new ULBACOUNTRY();

                                if (country != null && country.Length >= 3)
                                {
                                    lba.sz_iso = country[0];
                                    lba.l_iso_numeric = Int32.Parse(country[1]);
                                    lba.l_cc = Int32.Parse(country[2]);
                                    lba.sz_name = country[3];
                                    shape.tas().addCountry(lba);
                                }
                            }
                        }
                        catch (Exception)
                        {
                            throw;
                        }
                        break;
                    }
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
                        catch (Exception)
                        {
                            throw;
                        }
                    }
                    break;
                case 9: //expect Municipal
                    shape = new UMunicipalShape();
                    try
                    {
                        bool b_continue = true;
                        String str = "";
                        while (b_continue)
                        {
                            try
                            {
                                str = reader.ReadLine();
                            }
                            catch (Exception e)
                            {
                                break;
                            }
                            if (str == null)
                                break;
                            if(str.Trim().Length == 0)
                                continue;
                            String[] p = str.Split('\t');
                            if (p.Length >= 2)
                            {
                                UMunicipalDef def = new UMunicipalDef();
                                def.sz_municipalid = p[0];
                                def.sz_municipalname = p[1];
                                shape.municipal().addRecord(def);
                            }
                        }
                    }
                    catch (Exception)
                    {
                        throw;
                    }
                    break;
            }
            reader.Close();
            return true;
        }
    }

}
