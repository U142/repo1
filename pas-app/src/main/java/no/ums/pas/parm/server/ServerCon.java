package no.ums.pas.parm.server;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.HttpPostForm;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.WSProgressPoller;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.pas.localization.Localization;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.ProgressJobType;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.parm.admin.ObjectFactory;
import no.ums.ws.parm.admin.ParmAdmin;
import no.ums.ws.parm.admin.UpdateParm;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//import no.ums.ws.pas.*;

public class ServerCon {
	private HttpPostForm server;
	private InputStream streamReturn;
	
	public ServerCon(){
		
	}
	public InputStream update(InputStream in, String xmlFilename, String polyFileName, String zipFilename, UserInfo info)throws ParmException{
		boolean useWS = true;
		
		if(useWS)
		{
			try
			{
				ObjectFactory factory = new ObjectFactory();
				UpdateParm update = factory.createUpdateParm();
				ULOGONINFO logon = new ULOGONINFO();
				logon.setSzCompid(info.get_compid());
				logon.setSzUserid(info.get_userid());
				logon.setSzDeptid(info.get_current_department().get_deptid());
				logon.setSzPassword(info.get_passwd());
				logon.setLComppk(info.get_comppk());
				logon.setLDeptpk(info.get_current_department().get_deptpk());
				logon.setLUserpk(new Long(info.get_userpk()));
				logon.setSessionid(info.get_sessionid());
				update.setLogoninfo(logon);
				update.setSzFilename(xmlFilename);
				update.setSzPolyfilename(polyFileName);
	
				byte [] bytes = no.ums.pas.ums.tools.IO.ConvertInputStreamtoByteArray(in);
				update.setZipfile(bytes);
				ULOGONINFO tmp = new ULOGONINFO();
				tmp.setLComppk(logon.getLComppk());
				tmp.setLDeptpk(logon.getLDeptpk());
				tmp.setLUserpk(logon.getLUserpk());
				tmp.setJobid(WSThread.GenJobId());
				
				WSProgressPoller progress = null;
				try
				{
                    progress = new WSProgressPoller(/*PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().lbl_initializing*/PAS.get_pas().get_parmcontroller().getTreeCtrl().get_treegui().loader, ProgressJobType.PARM_UPDATE, tmp, Localization.l("main_parmtab_loading_parm"), Localization.l("main_parmtab_populating_tree"), false);
					progress.setShowOnlyPercent(true);
					progress.start();
				}
				catch(Exception err)
				{}
				logon.setJobid(tmp.getJobid());
				URL wsdl = new URL(vars.WSDL_PARMADMIN); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
				//URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
				QName service = new QName("http://ums.no/ws/parm/admin/", "ParmAdmin");
				//byte [] ret = new ParmAdmin(wsdl, service).getParmAdminSoap12().updateParm(bytes, logon, xmlFilename, polyFileName);
				InputStream is = new ByteArrayInputStream(new ParmAdmin(wsdl, service).getParmAdminSoap12().updateParm(bytes, logon, xmlFilename, polyFileName));
				try
				{
					progress.SetFinished();					
				}
				catch(Exception e) { }
				return is;
				//File f = new File("d:\\slettes\\test.zip");
				//FileWriter w = new FileWriter(f, false);
				//w.write(ret.toString());
				//w.close();
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Error.getError().addError("Error in Update PARM", "Error in request to server", e, 1);
			}
		}
		else
		{
		
			//Creates connection with webserver
			createConnection();
			//Set parameter wich is the xml file name inside the zip file
			setParameter(ParmConstants.fileName,xmlFilename);
			setParameter(ParmConstants.polyFileName, polyFileName);
			setParameter("xuid", info.get_userid());
			setParameter("xcid", info.get_compid());
			setParameter("xpwd", info.get_passwd());
			try {
				setParameter("xdep", Integer.toString(info.get_current_department().get_deptpk()));
				setParameter("xupk", info.get_userpk());
				setParameter("xcpk", Integer.toString(info.get_comppk()));
			} catch(Exception e) {
				
			}
			//Set parameter that tells the webserver what name the zip file has
	
			setParameter("file",zipFilename,in);
			//Sends the file
			send();
		}
		
		
			
		return this.streamReturn;
	}
	private void createConnection()throws ParmException{
		try{
			server = new HttpPostForm(ParmConstants.aspUrl);
		}catch(IOException ioe){
			Error.getError().addError("ServerCon","IOException in createConnection",ioe,1);
			throw new ParmException("Could not establish connection with webserver!");
		}
	}
	private void setParameter(String name, String value)throws ParmException{
		try{
			server.setParameter(name,value);
		}catch(IOException ioe){
			Error.getError().addError("ServerCon","IOException in setParameter",ioe,1);
			throw new ParmException("Could not set the parameter!");
		}
	}
	private void setParameter(String name, String value, InputStream in)throws ParmException{
		try{
			server.setParameter(name,value,in);
		}catch(IOException ioe){
			Error.getError().addError("ServerCon","IOException in createConnection",ioe,1);
			throw new ParmException("Could not set the parameter");
		}
	}
	private void send()throws ParmException{
		try{
			this.streamReturn = server.post();
		}catch(IOException ioe){
			Error.getError().addError("ServerCon","IOException in send",ioe,1);
			throw new ParmException("Could not send the file, lost connection with webserver");
		}
	}
}
