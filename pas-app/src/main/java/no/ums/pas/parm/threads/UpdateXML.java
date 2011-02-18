package no.ums.pas.parm.threads;

import no.ums.pas.PAS;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.server.ServerCon;
import no.ums.pas.parm.xml.XmlReader;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.ums.errorhandling.Error;

import java.io.*;
import java.util.Collection;



public class UpdateXML extends Thread{
	private String commando;
	private String filename, polyFileName;
	private MainController main;
	private ServerCon con;
	private XmlReader xmlReader;
	private XmlWriter xmlWriter;
	private int hTempPk;
	private boolean stop = false;
	private String path = "tempxml";
	private String polyPath = "polyxml";
	private InputStream sendXmlStream, responseXmlStream;
	private boolean hasExited = false;
	public boolean hasExited() { return hasExited; }
	private Object waitObject = null;
	public void setWaitForObject(Object o) { waitObject = o; }
	boolean m_b_inprogress = false;
	public synchronized boolean inProgress() { return m_b_inprogress; }
	
	public UpdateXML(MainController main, String sessionID){
		super("PARM UpdateXML Thread");
		this.commando = "";
		this.filename = sessionID.concat(path);
		this.polyFileName = sessionID.concat(polyPath);
		this.main = main;
		this.con = null;
		this.sendXmlStream = null;
		this.responseXmlStream = null;
		this.xmlReader = null;
		this.xmlWriter = null;
		
	}
	public void saveProject(){
		this.interrupt();
	}
	public void endSession(){
		//If you set the commando to Stop the thread will stop!
		this.stop = true;
		this.interrupt();
	}
	public void readXML()throws ParmException{
		this.xmlReader = new XmlReader(main);
		this.xmlWriter = new XmlWriter();
		this.hTempPk = this.main.getHighestTemp();
		String tempfile = filename + this.hTempPk+".xml";
		String tempPoly = polyFileName + this.hTempPk + ".xml";
		String filename = this.filename + this.hTempPk+".zip";
		System.out.println(filename);
		this.path = xmlWriter.writeTempXml(this.xmlReader.readTempXml(xmlWriter),ParmConstants.tempxmlLocation+filename);
		System.out.println(path);
		this.getSendXmlStream();
		
		this.con = new ServerCon();
		this.responseXmlStream = con.update(this.sendXmlStream,tempfile, tempPoly, filename, main.userinfo);
		this.con = null;// det går bra til her
		if(responseXmlStream!=null)
		{
			Collection <Object>liste = this.xmlReader.readResponseXml(this.responseXmlStream);
		
			this.closeStreams();
			if(!this.stop)
				this.main.refreshTree(liste);
		}
		else
		{
			//an error occured serverside, no data was retrieved
		//	Error.getError().addError("PARM Error", "No data was retrieved from the server", 0, 1);
		}
		try
		{
			this.xmlWriter.deleteZip(path);
		}
		catch(Exception e)
		{
			
		}
		
		this.xmlWriter = null;
		this.responseXmlStream = null;
		this.sendXmlStream = null;
		
		this.xmlReader = null;
		
	}
	private void getSendXmlStream()throws ParmException{
		try{
			this.sendXmlStream = new BufferedInputStream(new FileInputStream(path));
		}catch(FileNotFoundException fnfe){
			Error.getError().addError("UpdateXML","FileNotFoundException in getSendXmlStream",fnfe,1);
			throw new ParmException("Could not find " + path);
		}
	}
	private void closeStreams() throws ParmException{
		try{
			if(this.responseXmlStream!=null)
				this.responseXmlStream.close();
			if(this.sendXmlStream!=null)
				this.sendXmlStream.close();
		}catch(IOException ioe){
			Error.getError().addError("UpdateXML","IOException in closeStreams",ioe,1);
			throw new ParmException("Could not close streams");
		}
	}
	public void run(){
		while(!(commando.compareTo("Stop")==0)){
			try{
				if(this.stop==true){
					/*LoadingFrame loadingframe = new LoadingFrame("Closing PARM", null);
					LoadingPanel loadingpanel = new LoadingPanel("Closing PARM", new java.awt.Dimension(100, 16));
					try {
						loadingpanel.set_totalitems(0, "Closing PARM");
						loadingpanel.start_progress(0, "Closing PARM");
						loadingframe.getContentPane().add(loadingpanel, java.awt.BorderLayout.NORTH);
						loadingframe.setVisible(true);
					} catch(Exception e) { }*/
					m_b_inprogress = true;
					try
					{
						PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_dept().setEnabled(false);
					}
					finally
					{
						
					}
					readXML();
					try
					{
						PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_dept().setEnabled(true);
					}
					finally
					{
						
					}
					/*try {
						loadingpanel.set_currentitem(1);
						loadingframe.setVisible(false);
					} catch(Exception e) { }*/
					this.commando="Stop";
					break;
				}
				else{
					try
					{
						PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_dept().setEnabled(false);
						
					}
					finally
					{
						
					}
					readXML();
					try
					{
						PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_dept().setEnabled(true);
					}
					finally
					{
						
					}

					if(waitObject!=null)
						synchronized(waitObject) {
							waitObject.notify();
						}
					try{
						m_b_inprogress = false;
						Thread.sleep(ParmConstants.updateSequence);
					} catch(InterruptedException e) {
						System.out.println("Sleep interrupted");
						//Error.getError().addError("UpdateXML","Exception in run",e,1);
						//break;
					}
					m_b_inprogress = true;
				}
			}catch(ParmException pe){
				System.out.println(pe.getMessage());
				Error.getError().addError("UpdateXML","ParmException in run",pe,1);
			}catch(Exception e){
				//this.commando = "stop";
				//this.suspend();
				m_b_inprogress = false;
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("UpdateXML","Exception in run",e,1);
			}
		}
		m_b_inprogress = false;
		hasExited = true;
	}
}
