package no.ums.pas.core.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.send.BBProfile;
import no.ums.pas.send.BBSchedProfile;
import no.ums.pas.send.OADC;
import no.ums.pas.send.TTSLang;
import no.ums.pas.sound.SoundFile;
import no.ums.pas.sound.SoundlibFile;
import no.ums.pas.sound.SoundlibFileTxt;
import no.ums.pas.sound.SoundlibFileWav;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;


public class WSSendSettings extends WSThread
{

    private static final Log log = UmsLog.getLogger(WSSendSettings.class);

	ArrayList<BBProfile> m_profiles;
	public ArrayList<BBProfile> get_profiles() { return m_profiles; }
	ArrayList<OADC> m_oadcnumbers;
	public ArrayList<OADC> get_oadcnumbers() { return m_oadcnumbers; }
	ArrayList<BBSchedProfile> m_schedprofiles;
	public ArrayList<BBSchedProfile> get_schedprofiles() { return m_schedprofiles; }
	ArrayList<TTSLang> m_ttslang;
	public ArrayList<TTSLang> get_ttslang() { return m_ttslang; }
	ArrayList<SoundlibFile> m_soundlib;
	public ArrayList<SoundlibFile> get_soundlib() { return m_soundlib; }
	ArrayList<SoundlibFile> m_txtlib;
	public ArrayList<SoundlibFile> get_txtlib() { return m_txtlib; }
	ArrayList<SoundlibFile> m_smstemplatelib;
	public ArrayList<SoundlibFile> get_smstemplatelib() { return m_smstemplatelib; }

	public WSSendSettings(ActionListener callback)
	{
		super(callback);
	}
	@Override
	public void call() throws Exception
	{
		ULOGONINFO logon = new ULOGONINFO();
		//WSFillLogoninfo.fill(logon, PAS.get_pas().get_userinfo());
		logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		logon.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		try
		{
			URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/Pas.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			log.debug("Sending request");
			ByteArrayInputStream zip_data = new ByteArrayInputStream(new Pasws(wsdl, service).getPaswsSoap12().getSendSettings(logon));
			log.debug("Got response");
			ZipInputStream zis = new ZipInputStream(zip_data);
			log.debug("Unziping response");
			zis.getNextEntry();
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(zis);
			log.debug("Building document");
			parseDoc(doc);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}

	}
	
	@Override
	protected String getErrorMessage() {
		return "Error loading Send Settings";
	}

	@Override
	public void onDownloadFinished()
	{
		try
		{
			m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_settings_loaded"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void parseDoc(Document doc) {
		if(doc==null)
			return;
		m_profiles = new ArrayList<BBProfile>();
		m_oadcnumbers = new ArrayList<OADC>();
		m_schedprofiles = new ArrayList<BBSchedProfile>();
		m_ttslang = new ArrayList<TTSLang>();
		m_soundlib = new ArrayList<SoundlibFile>();
		m_txtlib = new ArrayList<SoundlibFile>();
		m_smstemplatelib = new ArrayList<SoundlibFile>();
		
		// Test
		//new XmlWriter().writeXMLFile(doc, StorageElements.get_path(StorageController.PATH_HOME_) + "profiles.zip");
		
		//get profiles
		NodeList parent = doc.getElementsByTagName("PROFILE");
		if(parent == null)
			return;
		NodeList list_items = parent;
		Node node_item;
		NamedNodeMap nnm_items;
		//<PROFILE l_profilepk="213" sz_name="aka - beep detection" l_deptpk="1" l_reschedpk="1000000000000004" sharing="0">
		String[] sz_itemattr;
		String[] sz_values;
		BBProfile bbprofile;
		for(int n_items=0; n_items < list_items.getLength(); n_items++) { //<PROFILE>
			sz_itemattr = new String [] { "l_profilepk", "sz_name", "l_deptpk", "l_reschedpk", "sharing" };
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null) {
				log.debug("ERROR: XMLSendSettings.nnm_items==null <PROFILE> (" + n_items + ") " + node_item.getNodeName());
				continue;
			}
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				} catch(Exception e) {
					sz_values[n_attr] = new String("0");
					Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				}
			}
			try {
				bbprofile = new BBProfile(sz_values);
				get_profiles().add(bbprofile);
			} catch(Exception e) { 
				//log.debug(e.getMessage());
				//e.printStackTrace();
				Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				continue;
			}
			//<FILE l_parent="4" l_item="0" l_param="1" l_action="1" l_seq="0" sz_name="std substans" sz_defname="Substans" /> 
			NodeList list_files = node_item.getChildNodes(); //<FILE>
			if(list_files==null)
				continue;
			if(list_files.getLength()==0)
				continue;
			for(int n_files=0; n_files < list_files.getLength(); n_files++) {
				sz_itemattr = new String [] { "l_parent", "l_item", "l_param", "l_action", "l_seq", "sz_name", "sz_defname", "f_template", "l_messagepk" };
				node_item = list_files.item(n_files);
				nnm_items = node_item.getAttributes();
				if(nnm_items==null) {
					//log.debug("ERROR: XMLSendSettings.nnm_items==null <FILE> (profile=" + bbprofile.get_profilepk() + ")");
					continue;
				}
				sz_values = new String[sz_itemattr.length];
				for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
					try {
						sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
					} catch(Exception e) {
						sz_values[n_attr] = new String("0");
						//Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
					}
				}
				try {
					bbprofile.add_soundfile(new SoundFile(sz_values));
				} catch(Exception e) {
					//log.debug(e.getMessage());
					//e.printStackTrace();
					Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
					continue;
				}
			}
			
		}

		//get oadc numbers
		parent = doc.getElementsByTagName("OADC");
		list_items = parent;
		OADC oadc;
		for(int n_items=0; n_items < list_items.getLength(); n_items++) { //<PROFILE>
			sz_itemattr = new String [] { "sz_number", "l_deptpk", "sz_descr" };
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null) {
				//log.debug("ERROR: XMLSendSettings.nnm_items==null <OADC> (" + n_items + ") " + node_item.getNodeName());
				continue;
			}
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				} catch(Exception e) {
					sz_values[n_attr] = new String("0");
					Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				}
			}
			try {
				oadc = new OADC(sz_values);
				get_oadcnumbers().add(oadc);
			} catch(Exception e) { 
				//log.debug(e.getMessage());
				//e.printStackTrace();
				Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				continue;
			}
		}
		
		//get sched profiles
		parent = doc.getElementsByTagName("SCHEDPROFILE");
		list_items = parent;
		BBSchedProfile sched;
		for(int n_items=0; n_items < list_items.getLength(); n_items++) { //<PROFILE>
			sz_itemattr = new String [] { "l_reschedpk", "sz_name", "l_deptpk", "sz_deptid", "l_retries", "l_interval", "l_canceltime", "l_canceldate", "l_pausetime", "l_pauseinterval", "sharing" };
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null) {
				//log.debug("ERROR: XMLSendSettings.nnm_items==null <OADC> (" + n_items + ") " + node_item.getNodeName());
				continue;
			}
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				} catch(Exception e) {
					sz_values[n_attr] = new String("0");
					//Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				}
			}
			try {
				sched = new BBSchedProfile(sz_values);
				get_schedprofiles().add(sched);
			} catch(Exception e) { 
				//PAS.get_pas().add_event("ERROR: XMLSendSettings.add(oadc) (" + n_items + ")" + e.getMessage(), e);
				Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				continue;
			}
		}
		
		parent = doc.getElementsByTagName("TTS");
		list_items = parent;
		TTSLang lang;
		for(int n_items=0; n_items < list_items.getLength(); n_items++) {
			sz_itemattr = new String [] { "l_langpk", "sz_name" };
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null) {
				//log.debug("ERROR: XMLSendSettings.nnm_items==null <TTSLANG> (" + n_items + ") " + node_item.getNodeName());
				continue;
			}
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				} catch(Exception e) {
					sz_values[n_attr] = new String("0");
					//Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				}
			}
			try {
				lang = new TTSLang(sz_values);
				get_ttslang().add(lang);
			} catch(Exception e) { 
				//log.debug(e.getMessage());
				//e.printStackTrace();
				Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				continue;
			}			
		}
		
		//l_sh, deptpk, name, pk, def, langpk, template
		//Node soundlib;
		//soundlib = doc.getElementsByTagName("SOUNDLIB").item(0);
		
		//parent = soundlib.getChildNodes();//doc.getElementsByTagName("SERVERFILE");
		parent = doc.getElementsByTagName("SERVERFILE");
		list_items = parent;
		SoundlibFile file;
		//get_txtlib().add(new SoundlibFileTxt());
		sz_itemattr = new String [] { "sh", "deptpk", "name", "pk", "langpk", "def", "template", "type" };
		for(int n_items=0; n_items < list_items.getLength(); n_items++) {
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null) {
				log.debug("ERROR: XMLSendSettings.nnm_items==null <SERVERFILE> (" + n_items + ") " + node_item.getNodeName());
				continue;
			}
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++) {
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				} catch(Exception e) {
					sz_values[n_attr] = new String("0");
					//Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				}
			}
			try {
				int n_template = new Integer(sz_values[6]).intValue(); //new Boolean((new Integer(sz_values[6]).intValue() == 1 ? true : false)).booleanValue();
				if(n_template==1) {
					file = new SoundlibFileTxt(sz_values);
					get_txtlib().add(file);
				}
				else if(n_template==0){
					file = new SoundlibFileWav(sz_values);
					get_soundlib().add(file);
				}
				else if(n_template==2){
					file = new SoundlibFileTxt(sz_values);
					get_smstemplatelib().add(file);
				}
			} catch(Exception e) { 
				//log.debug(e.getMessage());
				//e.printStackTrace();
				Error.getError().addError("XMLSendSettings","Exception in parseDoc",e,1);
				continue;
			}			
		}

		
		//onDownloadFinished();
	}
	
}