package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.importer.gis.GISList;
import no.ums.ws.common.ProgressJobType;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.ArrayOfUGisImportApartmentLine;
import no.ums.ws.pas.ArrayOfUGisImportLine;
import no.ums.ws.pas.ArrayOfUGisImportPropertyLine;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGisImportApartmentLine;
import no.ums.ws.pas.UGisImportApartmentList;
import no.ums.ws.pas.UGisImportLine;
import no.ums.ws.pas.UGisImportList;
import no.ums.ws.pas.UGisImportPropertyLine;
import no.ums.ws.pas.UGisImportPropertyList;
import no.ums.ws.pas.UGisImportResultsByStreetId;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

/**
 * Work to be done for mocking data
 */

public class WSGisImport extends WSThread
{

    private static final Log log = UmsLog.getLogger(WSGisImport.class);
    private String m_encoding;
    private String m_importType;
    private boolean isImportFilter= false;
    protected class GisColumnset
    {
        public int COL_MUNICIPAL;
        public int COL_NAMEFILTER1;
        public int COL_NAMEFILTER2;
        public int SKIPLINES;
        public int COL_STREETID;
        public int COL_HOUSENO;
        public int COL_LETTER;
        public int COL_APARTMENTID;
        public int COL_GNR;
        public int COL_BNR;
        public int COL_FNR;
        public int COL_SNR;

        public String SEPARATOR;
        public File FILE;
    }
    public class GisColumnsetStreetid extends GisColumnset
    {
        public GisColumnsetStreetid()
        {
        }
        public GisColumnsetStreetid(int municipal, int streetid, int houseno, int letter, int namefilter1, int namefilter2, int skiplines, String sep, File f)
        {
            COL_MUNICIPAL = municipal;
            COL_STREETID = streetid;
            COL_HOUSENO = houseno;
            COL_LETTER = letter;
            COL_NAMEFILTER1 = namefilter1;
            COL_NAMEFILTER2 = namefilter2;
            SKIPLINES = skiplines;
            SEPARATOR = sep;
            FILE = f;
        }
        public GisColumnsetStreetid(int municipal, int streetid, int houseno, int letter,int apartmentID, int namefilter1, int namefilter2, int skiplines, String sep, File f)
        {
            this(municipal,streetid,houseno,letter,namefilter1,namefilter2,skiplines,sep,f);
            COL_APARTMENTID = apartmentID;

        }

    }

    public class GisColumnsetPropertyid extends GisColumnsetStreetid
    {
        public GisColumnsetPropertyid()
        {
        }

        public GisColumnsetPropertyid(int municipal, int gnr, int bnr, int fnr, int snr,int apartmentID, int namefilter1, int namefilter2, int skiplines, String sep, File f) {
            COL_MUNICIPAL=municipal;
            COL_GNR=gnr;
            COL_BNR=bnr;
            COL_FNR = fnr;
            COL_SNR = snr;
            COL_APARTMENTID = apartmentID;
            COL_NAMEFILTER1 = namefilter1;
            COL_NAMEFILTER2 = namefilter2;
            SKIPLINES = skiplines;
            SEPARATOR = sep;
            FILE = f;
        }
    }

    public class GisColumnsetGnoBno extends GisColumnset
    {
        public int COL_GNR;
        public int COL_BNR;
    }



    public GisColumnsetStreetid newGisColumnsetStreetid()
    {
        return new WSGisImport.GisColumnsetStreetid();
    }
    public GisColumnsetStreetid newGisColumnsetStreetid(int municipal, int streetid, int houseno, int letter, int namefilter1, int namefilter2, int skiplines, String sep, File f)
    {
        return new WSGisImport.GisColumnsetStreetid(municipal, streetid, houseno, letter,namefilter1, namefilter2, skiplines, sep, f);
    }
    public GisColumnsetStreetid newGisColumnsetStreetid(int municipal, int streetid, int houseno, int letter,int apartmentId, int namefilter1, int namefilter2, int skiplines, String sep, File f)
    {
        return new WSGisImport.GisColumnsetStreetid(municipal, streetid, houseno, letter, apartmentId,namefilter1, namefilter2, skiplines, sep, f);
    }
    public GisColumnsetPropertyid newGisColumnsetPropertyId(int municipal, int gnr, int bnr, int fnr, int snr,int apartmentId,int namefilter1, int namefilter2, int skiplines, String sep, File f)
    {
        return new WSGisImport.GisColumnsetPropertyid(municipal, gnr,bnr,fnr,snr,apartmentId, namefilter1, namefilter2, skiplines, sep, f);
    }
    protected GisColumnsetStreetid m_colset;
    protected GISList m_gislist;
    protected LoadingPanel loader;
    public WSGisImport(ActionListener callback, String sz_cmd)
    {
        super(callback);
        set_callbackcmd(sz_cmd);
        m_gislist = new GISList();
    }
    public WSGisImport(ActionListener callback, String sz_cmd, LoadingPanel loader, String encoding)
    {
        this(callback, sz_cmd);
        this.loader = loader;
        this.m_encoding = encoding;
    }
    public WSGisImport(ActionListener callback, String sz_cmd, LoadingPanel loader, String encoding,String import_type)
    {
        this(callback, sz_cmd);
        this.loader = loader;
        this.m_encoding = encoding;
        this.m_importType= import_type;
    }
    public void setColSet(GisColumnsetStreetid colset)
    {
        m_colset = colset;
    }


    @Override
    public void call() throws Exception {
        ULOGONINFO logon = new ULOGONINFO();
        UserInfo u = PAS.get_pas().get_userinfo();
        logon.setLComppk(u.get_comppk());
        logon.setLDeptpk(u.get_current_department().get_deptpk());
        logon.setLUserpk(new Long(u.get_userpk()));
        logon.setSzCompid(u.get_compid());
        logon.setSzDeptid(u.get_current_department().get_deptid());
        logon.setSzPassword(u.get_passwd());
        logon.setSzStdcc(u.get_current_department().get_stdcc());
        logon.setLDeptpk(u.get_current_department().get_deptpk());
        logon.setSessionid(u.get_sessionid());
        logon.setJobid(WSThread.GenJobId());

        WSProgressPoller progress = new WSProgressPoller(loader, ProgressJobType.GEMINI_IMPORT_STREETID, logon, "GIS Import", "Finished", false);
        try
        {
            progress.start();

            final FileInputStream fis = new FileInputStream(m_colset.FILE);
            final Reader rdr = new InputStreamReader(fis, m_encoding);
            final StringBuffer content = new StringBuffer();
            char[] buffer = new char[4096];
            int read = 0;
            while ((read = rdr.read(buffer)) > 0) {
                content.append(buffer, 0, read);
            }

            final BufferedReader br = new BufferedReader(new StringReader(content.toString()));
            String temp;
            int line=0;

            URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
            QName service = new QName("http://ums.no/ws/pas/", "pasws");
            UGisImportResultsByStreetId res = null;

            if("Street".equals(m_importType)){
                ArrayOfUGisImportLine importlines = new ArrayOfUGisImportLine();
                UGisImportList search = new UGisImportList();
                search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0); //editor has already made a new file
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colset.SEPARATOR);
                    UGisImportLine resline = new UGisImportLine();
                    if(ting.length>0)
                    {
                    	resline.setMunicipalid(ting[m_colset.COL_MUNICIPAL]);
                        resline.setStreetid(ting[m_colset.COL_STREETID]);
                        resline.setHouseno(ting.length-1>=m_colset.COL_HOUSENO?ting[m_colset.COL_HOUSENO]:"");
                        resline.setLetter(ting.length-1>=m_colset.COL_LETTER?ting[m_colset.COL_LETTER]:"");
                        resline.setApartmentid(ting.length-1>=m_colset.COL_APARTMENTID?ting[m_colset.COL_APARTMENTID]:"");
                        resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                        resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        importlines.getUGisImportLine().add(resline);
                    }
                    line++;
                }
                if(isImportFilter()){
                }
                else{
                res = new Pasws(wsdl, service).getPaswsSoap12().getGisByStreetIdV2(logon, search);
                m_gislist = new GISList(); //init array as l_totitem big
                m_gislist.fill(res);
                }
            }   else if("StreetApartment".equalsIgnoreCase(m_importType)){
                ArrayOfUGisImportApartmentLine importlines = new ArrayOfUGisImportApartmentLine();
                UGisImportApartmentList search = new UGisImportApartmentList();
                search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0); //editor has already made a new file
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colset.SEPARATOR);
                    UGisImportApartmentLine resline = new UGisImportApartmentLine();
                    if(ting.length>0)
                    {
                        resline.setMunicipalid(ting[m_colset.COL_MUNICIPAL]);
                        resline.setStreetid(ting[m_colset.COL_STREETID]);
                        resline.setHouseno(ting[m_colset.COL_HOUSENO]);
                        resline.setApartmentid(ting[m_colset.COL_APARTMENTID]);
                        resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                        resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        importlines.getUGisImportApartmentLine().add(resline);
                    }
                    line++;
                }
                res = new Pasws(wsdl, service).getPaswsSoap12().getGisByApartmentIdV3(logon, search);
                m_gislist = new GISList(); //init array as l_totitem big
                m_gislist.fill(res);
            } else{
                ArrayOfUGisImportPropertyLine importlines = new ArrayOfUGisImportPropertyLine();
                UGisImportPropertyList search = new UGisImportPropertyList();
                search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0); //editor has already made a new file
                while((temp = br.readLine()) != null) {
                	String[] ting = temp.split(m_colset.SEPARATOR);
                    UGisImportPropertyLine resline = new UGisImportPropertyLine();
                    if(ting.length>0)
                    {
                        resline.setMunicipalid(ting[m_colset.COL_MUNICIPAL]);
                        resline.setGnr(ting[m_colset.COL_GNR]);
                        resline.setBnr(ting[m_colset.COL_BNR]);
                        resline.setFnr(ting.length-1>=m_colset.COL_FNR?ting[m_colset.COL_FNR]:"");
                        resline.setSnr(ting.length-1>=m_colset.COL_SNR?ting[m_colset.COL_SNR]:"");
                        resline.setApartmentid(ting.length-1>=m_colset.COL_APARTMENTID?ting[m_colset.COL_APARTMENTID]:"");
                        resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                        resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        importlines.getUGisImportPropertyLine().add(resline);
                    }
                    line++;
                }
                res = new Pasws(wsdl, service).getPaswsSoap12().getGisByPropertyIdV3(logon, search);
                m_gislist = new GISList(); //init array as l_totitem big
                m_gislist.fillProperty(res);
            }

        }
        catch(Exception e)
        {
            log.warn(e.getMessage(), e);
            throw e;
        }
        finally
        {
            progress.SetFinished();

        }

    }

    @Override
    protected String getErrorMessage() {
        return "Error fetching GIS import";
    }
    @Override
    public void onDownloadFinished() {
        m_callback.actionPerformed(new ActionEvent(m_gislist, ActionEvent.ACTION_PERFORMED, sz_cb_cmd));
    }
	public boolean isImportFilter() {
		return isImportFilter;
	}
	public void setImportFilter(boolean isImportFilter) {
		this.isImportFilter = isImportFilter;
	}
}






