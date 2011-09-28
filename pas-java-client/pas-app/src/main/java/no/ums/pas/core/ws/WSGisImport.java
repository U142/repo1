package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.importer.gis.GISList;
import no.ums.ws.common.ProgressJobType;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.ArrayOfUGisImportLine;
import no.ums.ws.pas.ArrayOfUGisImportResultLine;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGisImportLine;
import no.ums.ws.pas.UGisImportList;
import no.ums.ws.pas.UGisImportParamsByStreetId;
import no.ums.ws.pas.UGisImportResultLine;
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
import java.util.List;



public class WSGisImport extends WSThread
{

    private static final Log log = UmsLog.getLogger(WSGisImport.class);
    private String m_encoding;

	protected class GisColumnset
	{
		public int COL_MUNICIPAL;
		public int COL_NAMEFILTER1;
		public int COL_NAMEFILTER2;
		public int SKIPLINES;
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
		public int COL_STREETID;
		public int COL_HOUSENO;
		public int COL_LETTER;
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
		return new WSGisImport.GisColumnsetStreetid(municipal, streetid, houseno, letter, namefilter1, namefilter2, skiplines, sep, f);
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
		//WSFillLogoninfo.fill((no.ums.ws.pas.status.ULOGONINFO)logon, PAS.get_pas().get_userinfo());
		
		
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
			
			UGisImportList search = new UGisImportList();
			ArrayOfUGisImportLine importlines = new ArrayOfUGisImportLine();
			search.setList(importlines);
			search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
			search.setSKIPLINES(m_colset.SKIPLINES);
			final BufferedReader br = new BufferedReader(new StringReader(content.toString()));
			String temp;
			int line=0;
			UGisImportLine resline;
			while((temp = br.readLine()) != null) {
				if(line==0 && search.getSKIPLINES() == 1) {
					// isje jørr någe
				}
				else {
					resline = new UGisImportLine();
					String[] ting = temp.split(m_colset.SEPARATOR);
					resline.setMunicipalid(ting[m_colset.COL_MUNICIPAL]); // getC
					resline.setStreetid(ting[m_colset.COL_STREETID]);
					resline.setHouseno(ting[m_colset.COL_HOUSENO]);
					resline.setLetter(ting.length-1>=m_colset.COL_LETTER?ting[m_colset.COL_LETTER]:"");
					resline.setNamefilter1(ting.length-1>=m_colset.COL_NAMEFILTER1?ting[m_colset.COL_NAMEFILTER1]:"");
					resline.setNamefilter2(ting.length-1>=m_colset.COL_NAMEFILTER2?ting[m_colset.COL_NAMEFILTER2]:"");
					importlines.getUGisImportLine().add(resline);
				}
				line++;
			}	
			
			
			/*
			 * PasApplication.getInstance().getPaswsSoap().getGisByStreetIdAsync(logon, search, new AsyncHandler<GetGisByStreetIdResponse>() {
				
				@Override
				public void handleResponse(Response<GetGisByStreetIdResponse> res) {
					m_callback.actionPerformed(new ActionEvent(m_gislist, ActionEvent.ACTION_PERFORMED, sz_cb_cmd));
					m_gislist = new GISList(); //init array as l_totitem big
					try {
						m_gislist.fill(res.get().getGetGisByStreetIdResult());
					} catch (InterruptedException e) {
						progress.setFinishedText("Aborted");
					} catch (ExecutionException e) {
						log.warn(e.getMessage(), e);
						throw e;
					}
				}
			});
			 */
			
			//URL wsdl = new URL("http://localhost/ws/Pas.asmx?WSDL");
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			UGisImportResultsByStreetId res = new Pasws(wsdl, service).getPaswsSoap12().getGisByStreetIdV2(logon, search);
			m_gislist = new GISList(); //init array as l_totitem big
			m_gislist.fill(res);
		}
		catch(Exception e)
		{
			//no.ums.pas.ums.errorhandling.Error.getError().addError("Error fetching GIS import", "WSGisImport::run()", e, 1);
			log.warn(e.getMessage(), e);
			throw e;
		}
		finally
		{
			progress.SetFinished();
			//onDownloadFinished();
			
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
}






