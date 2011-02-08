package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.importer.gis.GISList;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.ProgressJobType;
import no.ums.ws.pas.UGisImportParamsByStreetId;
import no.ums.ws.pas.UGisImportResultsByStreetId;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;



public class WSGisImport extends WSThread
{
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
	public WSGisImport(ActionListener callback, String sz_cmd, LoadingPanel loader)
	{
		this(callback, sz_cmd);
		this.loader = loader;
	}
	public void setColSet(GisColumnsetStreetid colset)
	{
		m_colset = colset;
	}
	
	
	@Override
	public void call() throws Exception {
		no.ums.ws.pas.ObjectFactory of = new no.ums.ws.pas.ObjectFactory();
		no.ums.ws.pas.ULOGONINFO logon = of.createULOGONINFO();
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

			
			//URL wsdl = new URL(PAS.get_pas().get_sitename() + "/ExecAlert/WS/PasStatus.asmx?WSDL");
			UGisImportParamsByStreetId search = of.createUGisImportParamsByStreetId();
			search.setCOLHOUSENO(m_colset.COL_HOUSENO);
			search.setCOLLETTER(m_colset.COL_LETTER);
			search.setCOLMUNICIPAL(m_colset.COL_MUNICIPAL);
			search.setCOLNAMEFILTER1(m_colset.COL_NAMEFILTER1);
			search.setCOLNAMEFILTER2(m_colset.COL_NAMEFILTER2);
			search.setCOLSTREETID(m_colset.COL_STREETID);
			search.setSEPARATOR(m_colset.SEPARATOR);
			search.setSKIPLINES(m_colset.SKIPLINES);
			search.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
			byte [] bytes = no.ums.pas.ums.tools.IO.ConvertInputStreamtoByteArray(new FileInputStream(m_colset.FILE));
			search.setFILE(bytes);
			//URL wsdl = new URL("http://localhost/ws/Pas.asmx?WSDL");
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			UGisImportResultsByStreetId res = new Pasws(wsdl, service).getPaswsSoap12().getGisByStreetId(logon, search);
			m_gislist = new GISList(); //init array as l_totitem big
			m_gislist.fill(res);
		}
		catch(Exception e)
		{
			//no.ums.pas.ums.errorhandling.Error.getError().addError("Error fetching GIS import", "WSGisImport::run()", e, 1);
			e.printStackTrace();
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






