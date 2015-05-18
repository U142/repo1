

package no.ums.pas.core.ws;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.importer.gis.GISList;
import no.ums.ws.addressfilters.AddressAssociatedWithFilter;
import no.ums.ws.addressfilters.AddressFilterInfo;
import no.ums.ws.addressfilters.AddressFilters;
import no.ums.ws.addressfilters.ArrayOfAddressAssociatedWithFilter;
import no.ums.ws.common.ProgressJobType;
import no.ums.ws.common.ULOGONINFO;

/**
 * Work to be done for mocking data
 */

public class WSGisFilterImport extends WSThread
{

    private static final Log log = UmsLog.getLogger(WSGisFilterImport.class);
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
        public int COL_UNR;
        public int COL_FGKL;
        public int COL_VBID;

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

    public class GisCuNorway extends GisColumnset
    {
        public GisCuNorway()
        {
        }

        public GisCuNorway(int municipal, int gnr, int bnr, int fnr, int snr,int unr,int apartmentID,int skiplines, String sep, File f) {
            COL_MUNICIPAL=municipal;
            COL_GNR=gnr;
            COL_BNR=bnr;
            COL_FNR = fnr;
            COL_SNR = snr;
            COL_UNR = unr;
            COL_APARTMENTID = apartmentID;
           // COL_NAMEFILTER1 = namefilter1;
           // COL_NAMEFILTER2 = namefilter2;
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


     public class GisCuSweden extends  GisColumnset
     {
    	 public GisCuSweden(){
    		 
    	 }
    	 
    	 public GisCuSweden(int municipal, int Fastighetsnyckel, int houseno,int letter, int skiplines,String sep, File f){
    		 COL_MUNICIPAL= municipal;
    		 COL_FGKL = Fastighetsnyckel; 
    		 COL_HOUSENO = houseno;
    		 COL_LETTER = letter;
    		 //COL_APARTMENTID = apartmentId;
    		 SKIPLINES = skiplines;
             SEPARATOR = sep;
             FILE = f;
    		 
    	 }
    	 
    	    public GisCuSweden(int municipal, int Fastighetsnyckel, int houseno, int letter,int apartmentID,int skiplines, String sep, File f)
            {
                this(municipal,Fastighetsnyckel,houseno,letter,skiplines,sep,f);
                COL_APARTMENTID = apartmentID;

            }
    	 
     }
     
     public class GisVbBanken extends  GisColumnset
     {
    	 public GisVbBanken(){
    		 
    	 }
    	 
    	 public GisVbBanken(int municipal, int Vbanken, int houseno,int letter,int skiplines, String sep, File f){
    		 COL_MUNICIPAL= municipal;
    		 COL_VBID = Vbanken;
    		 COL_HOUSENO = houseno;
    		 COL_LETTER = letter;
    		// COL_APARTMENTID = apartmentId;

    		 SKIPLINES = skiplines;
             SEPARATOR = sep;
             FILE = f;
    		 
    	 }
    	 
    	    public GisVbBanken(int municipal, int Vbanken, int houseno, int letter,int apartmentID,int skiplines, String sep, File f)
            {
                this(municipal,Vbanken,houseno,letter,skiplines,sep,f);
                COL_APARTMENTID = apartmentID;

            }
     }
    
    
    public GisColumnsetStreetid newGisColumnsetStreetid()
    {
        return new WSGisFilterImport.GisColumnsetStreetid();
    }
   /* public GisColumnsetStreetid newGisColumnsetStreetid(int municipal, int streetid, int houseno, int letter, int namefilter1, int namefilter2, int skiplines, String sep, File f)
    {
        return new WSGisFilterImport.GisColumnsetStreetid(municipal, streetid, houseno, letter,namefilter1, namefilter2, skiplines, sep, f);
    }*/
    public GisColumnsetStreetid newGisColumnsetStreetid(int municipal, int streetid, int houseno, int letter,int apartmentId, int namefilter1, int namefilter2, int skiplines, String sep, File f)
    {
        return new WSGisFilterImport.GisColumnsetStreetid(municipal, streetid, houseno, letter, apartmentId,namefilter1, namefilter2, skiplines, sep, f);
    }
    public GisCuNorway newGisCuNorway(int municipal, int gnr, int bnr, int fnr, int snr,int unr,int apartmentId, int skiplines, String sep, File f)
    {
        return new WSGisFilterImport.GisCuNorway(municipal, gnr,bnr,fnr,snr,unr,apartmentId,skiplines, sep, f);
    }
    public GisCuSweden newGisCuSweden(int municipal, int fastighetsnyckel,int houseno,int letter, int apartmentId, int skiplines, String sep, File f)
    {
        return new WSGisFilterImport.GisCuSweden(municipal,fastighetsnyckel,houseno, letter, apartmentId, skiplines, sep, f);
    }
    public GisVbBanken newGisVbBanken(int municipal, int Vbanken,int houseno,int letter, int apartmentId, int skiplines, String sep, File f)
    {
        return new WSGisFilterImport.GisVbBanken(municipal,Vbanken, houseno, letter, apartmentId, skiplines, sep, f);
    }
    protected GisColumnsetStreetid m_colset;
    protected GisCuSweden m_colsetSwe;
    protected GisCuNorway m_colsetNor;
    protected GisVbBanken m_colsetVb;
    protected GISList m_gislist;
    protected GISFilterList m_gisfilterlist;
    protected no.ums.pas.importer.gis.GISFilterList gisFilterList;
    protected LoadingPanel loader;
    public WSGisFilterImport(ActionListener callback, String sz_cmd)
    {
        super(callback);
        set_callbackcmd(sz_cmd);
        m_gisfilterlist = new GISFilterList();
    }
    public WSGisFilterImport(ActionListener callback, String sz_cmd, LoadingPanel loader, String encoding)
    {
        this(callback, sz_cmd);
        this.loader = loader;
        this.m_encoding = encoding;
    }
    public WSGisFilterImport(ActionListener callback, String sz_cmd, LoadingPanel loader, String encoding,String import_type)
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
    
    public void setColSet(GisCuSweden colset)
    {
    	m_colsetSwe = colset;
    }

    public void setColSet(GisCuNorway colset)
    {
    	m_colsetNor = colset;
    }

    public void setColSet(GisVbBanken colset)
    {
    	m_colsetVb = colset;
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
        logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
        logon.setSzStdcc(u.get_current_department().get_stdcc());
        logon.setLDeptpk(u.get_current_department().get_deptpk());
        logon.setSessionid(u.get_sessionid());
        logon.setJobid(WSThread.GenJobId());

        
        WSProgressPoller progress = new WSProgressPoller(loader, ProgressJobType.GEMINI_IMPORT_STREETID, logon, "GIS Import", "Finished", false);
       
        try
        {
            progress.start();
             
            
            FileInputStream fis=null;    
             
             if("import_addr_street".equals(m_importType)){
            	 fis = new FileInputStream(m_colset.FILE);
             }
             else if("import_addr_CUNorway".equals(m_importType)){
            	 fis = new FileInputStream(m_colsetNor.FILE);
             }
             else if("import_addr_CUSweden".equals(m_importType)){
            	 fis = new FileInputStream(m_colsetSwe.FILE);
             }
             else if("import_addr_VABanken".equals(m_importType)){
            	 fis = new FileInputStream(m_colsetVb.FILE);
             }
             else{
                  fis=new FileInputStream(new File(""));
             }
             
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

           /* URL wsdl = new URL(vars.WSDL_ADDRESSFILTERS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
            QName service = new QName("http://ums.no/ws/addressfilters/",
					"AddressFilters");*/
           // UGisImportResultsByStreetId res = null;
            AddressFilters res=null;
            AddressFilterInfo info = new AddressFilterInfo();

            if("import_addr_street".equals(m_importType)){
               // ArrayOfUGisImportLine importlines = new ArrayOfUGisImportLine();
            	ArrayOfAddressAssociatedWithFilter importlines = new ArrayOfAddressAssociatedWithFilter();
            	
                /*UGisImportList search = new UGisImportList();
                search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0);*/ //editor has already made a new file
            	
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colset.SEPARATOR);
                   // UGisImportLine resline = new UGisImportLine();
                    AddressAssociatedWithFilter resline = new AddressAssociatedWithFilter();
                    
                    
                    if(ting.length>0)
                    {
                    	 String munId=ting[m_colset.COL_MUNICIPAL].trim();
                 	    if(munId.equals("")){
                 	    	 resline.setMunicipalId(0);
                         }else{
               	    	resline.setMunicipalId(Integer.parseInt(munId));
               	    }
                 	    
                 	   String strId=ting[m_colset.COL_STREETID].trim();
               	    if(strId.equals("")){
               	    	 resline.setStreetId(0);
                       }else{
                  	    	resline.setStreetId(Integer.parseInt(strId));
                  	    }
               	    
               	  String houseNo=ting[m_colset.COL_HOUSENO].trim();
             	    if(houseNo.equals("")){
             	    	 resline.setHouseNo(0);
                     } else{
                	    	resline.setHouseNo(Integer.parseInt(houseNo));
                	    }  
                      
                        resline.setSzHouseLetter(ting.length-1>=m_colset.COL_LETTER?(ting[m_colset.COL_LETTER]):"");
                        resline.setSzApartmentId(ting.length-1>=m_colset.COL_APARTMENTID?ting[m_colset.COL_APARTMENTID]:"");
                        
                        //resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                        //resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        importlines.getAddressAssociatedWithFilter().add(resline);
                    }
                    
                    line++;
                }
                m_gisfilterlist.fill(importlines.getAddressAssociatedWithFilter());
               // res = new AddressFilters(wsdl, service);
               // res.getAddressFiltersSoap().execUpdateAddressFilter(PARMOPERATION.INSERT, logon, info);
                //gisFilterList.add(null);

            }   else if("import_addr_CUNorway".equalsIgnoreCase(m_importType)){
                //ArrayOfUGisImportApartmentLine importlines = new ArrayOfUGisImportApartmentLine();
                ArrayOfAddressAssociatedWithFilter importlines = new ArrayOfAddressAssociatedWithFilter();
               // UGisImportApartmentList search = new UGisImportApartmentList();
               /* search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0);*/ //editor has already made a new file
                
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colsetNor.SEPARATOR);
                   // UGisImportApartmentLine resline = new UGisImportApartmentLine();
                    AddressAssociatedWithFilter resline = new AddressAssociatedWithFilter();
                    if(ting.length>0)
                    {
                    	 String munId=ting[m_colsetNor.COL_MUNICIPAL].trim();
                    	    if(munId.equals("")){
                    	    	 resline.setMunicipalId(0);
                            }
                    	    else{
                    	    	resline.setMunicipalId(Integer.parseInt(munId));
                    	    }
                       
                        String gnr=ting[m_colsetNor.COL_GNR].trim();
                        if(gnr.equals("")){
                        	resline.setGnrNumber(0);
                        }
                        else{
                	    	resline.setGnrNumber(Integer.parseInt(gnr));
                	    }
                       
                        String bnr=ting[m_colsetNor.COL_BNR].trim();
                        if(bnr.equals("")){
                        	 resline.setBnrNumber(0);
                        }
                        else{
                	    	resline.setBnrNumber(Integer.parseInt(bnr));
                	    }
                       
                        String fnr=ting[m_colsetNor.COL_FNR].trim();
                        if(fnr.equals("")){
                        	 resline.setFnrNumber(0);
                        } else{
                	    	resline.setFnrNumber(Integer.parseInt(fnr));
                	    }
                       
                       
                        String snr=ting[m_colsetNor.COL_SNR].trim();
                        if(snr.equals("")){
                        	resline.setSnrNumber(0);
                        }else{
                	    	resline.setSnrNumber(Integer.parseInt(snr));
                	    }
                        
                        String unr=ting[m_colsetNor.COL_UNR].trim();
                        if(unr.equals("")){
                        	resline.setUnrNumber(0);
                        }else{
                	    	resline.setUnrNumber(Integer.parseInt(unr));
                	    }
                        resline.setSzApartmentId(ting.length-1>=m_colsetNor.COL_APARTMENTID?ting[m_colsetNor.COL_APARTMENTID]:"");
                       // resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                       // resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        //importlines.getUGisImportApartmentLine().add(resline);
                        importlines.getAddressAssociatedWithFilter().add(resline);
                        
                    }
                    line++;
                }
                //info.setAddressForFilterlist(importlines);
                m_gisfilterlist.fill(importlines.getAddressAssociatedWithFilter());
               // res = new AddressFilters(wsdl, service);
               // res.getAddressFiltersSoap().execUpdateAddressFilter(PARMOPERATION.INSERT, logon, info);
              //  m_gislist = new GISList(); //init array as l_totitem big
              //  m_gislist.fill(res);
            }
            else if("import_addr_CUSweden".equalsIgnoreCase(m_importType)){
                //ArrayOfUGisImportApartmentLine importlines = new ArrayOfUGisImportApartmentLine();
                ArrayOfAddressAssociatedWithFilter importlines = new ArrayOfAddressAssociatedWithFilter();
               // UGisImportApartmentList search = new UGisImportApartmentList();
               /* search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0);*/ //editor has already made a new file
                
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colsetSwe.SEPARATOR);
                   // UGisImportApartmentLine resline = new UGisImportApartmentLine();
                    AddressAssociatedWithFilter resline = new AddressAssociatedWithFilter();
                    if(ting.length>0)
                    {
                    	 String munId=ting[m_colsetSwe.COL_MUNICIPAL].trim();
                  	    if(munId.equals("")){
                  	    	 resline.setMunicipalId(0);
                          }else{
                  	    	resline.setMunicipalId(Integer.parseInt(munId));
                  	    }
                  	  String fgkl=ting[m_colsetSwe.COL_FGKL].trim();
               	    if(fgkl.equals("")){
               	    	 resline.setSeCadId(0);
                       }else{
                 	    	resline.setSeCadId(Integer.parseInt(fgkl));
                 	    }
                      
               	 String houseNo=ting[m_colsetSwe.COL_HOUSENO].trim();
          	    if(houseNo.equals("")){
          	    	 resline.setHouseNo(0);
                  } else{
           	    	resline.setHouseNo(Integer.parseInt(houseNo));
           	    }               
          	           resline.setSzHouseLetter(ting.length-1>=m_colsetSwe.COL_LETTER?(ting[m_colsetSwe.COL_LETTER]):"");
                        resline.setSzApartmentId(ting.length-1>=m_colsetSwe.COL_APARTMENTID?ting[m_colsetSwe.COL_APARTMENTID]:"");
                       
                       // resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                       // resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        //importlines.getUGisImportApartmentLine().add(resline);
                        importlines.getAddressAssociatedWithFilter().add(resline);
                        
                    }
                    line++;
                }
                m_gisfilterlist.fill(importlines.getAddressAssociatedWithFilter());
               // res = new AddressFilters(wsdl, service);
                //res.getAddressFiltersSoap().execUpdateAddressFilter(PARMOPERATION.INSERT, logon, info);
                
            } 
             
            else if("import_addr_VABanken".equalsIgnoreCase(m_importType)){
                //ArrayOfUGisImportApartmentLine importlines = new ArrayOfUGisImportApartmentLine();
                ArrayOfAddressAssociatedWithFilter importlines = new ArrayOfAddressAssociatedWithFilter();
               // UGisImportApartmentList search = new UGisImportApartmentList();
               /* search.setList(importlines);
                search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                search.setSKIPLINES(0);*/ //editor has already made a new file
                
                while((temp = br.readLine()) != null) {
                    String[] ting = temp.split(m_colsetVb.SEPARATOR);
                   // UGisImportApartmentLine resline = new UGisImportApartmentLine();
                    AddressAssociatedWithFilter resline = new AddressAssociatedWithFilter();
                    if(ting.length>0)
                    {
                   	 String munId=ting[m_colsetVb.COL_MUNICIPAL].trim();
               	    if(munId.equals("")){
               	    	 resline.setMunicipalId(0);
                       }else{
                  	    	resline.setMunicipalId(Integer.parseInt(munId));
                  	    }               
                 	       
               	  String vbId=ting[m_colsetVb.COL_VBID].trim();
             	    if(vbId.equals("")){
             	    	 resline.setSeVaId(0);
                     }else{
               	    	resline.setSeVaId(Integer.parseInt(vbId));
               	    } 
                      
             	 	 String houseNo=ting[m_colsetVb.COL_HOUSENO].trim();
               	    if(houseNo.equals("")){
               	    	 resline.setHouseNo(0);
                       } else{
                  	    	resline.setHouseNo(Integer.parseInt(houseNo));
                  	    }  
               	
               	    
               	         resline.setSzHouseLetter(ting.length-1>=m_colsetVb.COL_LETTER?(ting[m_colsetVb.COL_LETTER]):"");
                        resline.setSzApartmentId(ting.length-1>=m_colsetVb.COL_APARTMENTID?ting[m_colsetVb.COL_APARTMENTID]:"");
                       
                       
                       // resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
                       // resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
                        //importlines.getUGisImportApartmentLine().add(resline);
                        importlines.getAddressAssociatedWithFilter().add(resline);
                        
                    }
                    line++;
                }
                m_gisfilterlist.fill(importlines.getAddressAssociatedWithFilter());
               /* res = new AddressFilters(wsdl, service);
                res.getAddressFiltersSoap().execUpdateAddressFilter(PARMOPERATION.INSERT, logon, info);*/
                
            }else{
            	log.warn("NO Import selected");
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
        m_callback.actionPerformed(new ActionEvent(m_gisfilterlist, ActionEvent.ACTION_PERFORMED, sz_cb_cmd));
    }
	public boolean isImportFilter() {
		return isImportFilter;
	}
	public void setImportFilter(boolean isImportFilter) {
		this.isImportFilter = isImportFilter;
	}
	public GISFilterList getM_gisfilterlist() {
		return m_gisfilterlist;
	}
	public void setM_gisfilterlist(GISFilterList m_gisfilterlist) {
		this.m_gisfilterlist = m_gisfilterlist;
	}
}






