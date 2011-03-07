package no.ums.pas.sound;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.ws.vars;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.sendpanels.Sending_Files;
import no.ums.pas.sound.soundinfotypes.SoundInfo;
import no.ums.pas.sound.soundinfotypes.SoundInfoLibrary;
import no.ums.pas.sound.soundinfotypes.SoundInfoLocal;
import no.ums.pas.sound.soundinfotypes.SoundInfoTTS;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.AUDIOREQUEST;
import no.ums.ws.pas.AUDIORESPONSE;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

public class SoundFile extends Object {
	
	private int m_n_parent;
	private int m_n_item;
	private int m_n_param;
	private int m_n_action;
	private int m_n_seq;
	private int m_n_filenumber;
	private String m_sz_modulename;
	private String m_sz_name;
	private int m_n_sound_filetype;
	private ByteBuffer m_bytebuffer = null;
	private int m_n_template;
	private int m_n_messagepk;
	public int get_filenumber() { return m_n_filenumber; }
	public int get_param() { return m_n_param; }
	public String get_modulename() { return m_sz_modulename; }
	public String get_name() { return m_sz_name; }
	public int get_item() { return m_n_item; }
	public int get_action() { return m_n_action; }
	public int get_seq() { return m_n_seq; }
	public int get_template() { return m_n_template; }
	public int get_messagepk() { return m_n_messagepk; }
	public ByteBuffer get_bytebuffer() { return m_bytebuffer; }
	
	
	public SoundFile(int n_parent, int n_item, int n_param, int n_action, int n_seq, 
			  String sz_modulename, String sz_name, int n_template, int n_messagepk) {
		m_n_parent	= n_parent;
		m_n_item	= n_item;
		m_n_param	= n_param;
		m_n_action	= n_action;
		m_n_seq		= n_seq;
		m_sz_modulename = sz_modulename;
		m_sz_name	= sz_name;
		if(get_action() == 1018)
			m_n_filenumber = get_seq();
		else
			m_n_filenumber = get_param();
		m_n_template = n_template;
		m_n_messagepk = n_messagepk;
	
	}
	public SoundFile(String [] sz_values) {
		//"l_parent", "l_item", "l_param", "l_action", "l_seq", "sz_name", "sz_defname"
		this(new Integer(sz_values[0]).intValue(), new Integer(sz_values[1]).intValue(), new Integer(sz_values[2]).intValue(),
			 new Integer(sz_values[3]).intValue(), new Integer(sz_values[4]).intValue(), sz_values[5], sz_values[6], new Integer(sz_values[7]).intValue(), new Integer(sz_values[8]).intValue());
	}
	public void set_bytebuffer(ByteBuffer buf) {
		m_bytebuffer = buf;
	}
	public boolean send_wav(int n_refno, int n_filetype, SoundInfo info) {
		java.net.URL wsdl;
		
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			//wsdl = new java.net.URL("http://localhost/WS/PAS.asmx?WSDL"); 
		} catch(Exception e)
		{
			return false;
		}
		QName service = new QName("http://ums.no/ws/pas/", "pasws");

		
		AUDIOREQUEST audioreq = new AUDIOREQUEST();
		ULOGONINFO logon = new ULOGONINFO();
		UserInfo info1 = PAS.get_pas().get_userinfo();
		logon.setLComppk(info1.get_comppk());
		logon.setLDeptpk(info1.get_current_department().get_deptpk());
		logon.setLUserpk(Long.parseLong(info1.get_userpk()));
		logon.setSzCompid(info1.get_compid());
		logon.setSzDeptid(info1.get_current_department().get_deptid());
		logon.setSzPassword(info1.get_passwd());
		logon.setSessionid(info1.get_sessionid());
		audioreq.setNDeptpk(info1.get_current_department().get_deptpk());
		audioreq.setNRefno(n_refno);
		

		/*
		HttpPostForm form = null;
		try {
			form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_post_wav.asp");			
			form.setParameter("Type", "wav");
			form.setParameter("l_refno", new Integer(n_refno).toString());
			form.setParameter("l_param", new Integer(get_filenumber()).toString());
			form.setParameter("l_filetype", new Integer(n_filetype));			
		} catch(Exception e) {
			Error.getError().addError("SoundFile","Exception in send_wav",e,1);
			return false;
		}*/

		// This fixes the problem with the TTS file being "used up", can now simulate and test without error
		if(n_filetype == Sending_Files.SOUNDFILE_TYPE_TTS_) {
			SoundInfoTTS infotts = (SoundInfoTTS)info;
			SoundInfoLocal infoloc = new SoundInfoLocal(new File(StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_) + infotts.get_serverfilename()));
			n_filetype = Sending_Files.SOUNDFILE_TYPE_LOCAL_;
			info = infoloc;
		}
		
		String sz_filename;
		switch(n_filetype) {
			case Sending_Files.SOUNDFILE_TYPE_RECORD_:
				sz_filename = n_refno + "_" + get_filenumber() + ".wav";
				if(get_bytebuffer()==null) {
					PAS.get_pas().add_event("send_wav() bytebuffer = NULL", null);
					return false;
				}
                PAS.get_pas().add_event(Localization.l("common_uploading") + " - " + sz_filename, null);
				try {
					byte[] wavarray = get_bytebuffer().duplicate().array();
					audioreq.setWav(wavarray);
					audioreq.setNFiletype(n_filetype);
					audioreq.setNParam(get_filenumber());
				} catch(Exception e) {
					PAS.get_pas().add_event("ERROR: Upload failed " + e.getMessage(), e);
                    Error.getError().addError(Localization.l("common_error"),"Exception in send_wav",e,1);
					return false;
				}
				break;
			case Sending_Files.SOUNDFILE_TYPE_TTS_:
				//check serverside existence
				SoundInfoTTS infotts = (SoundInfoTTS)info;
                PAS.get_pas().add_event(Localization.l("sound_file_uploading_tts_text") + " : " + infotts.get_serverfilename(), null);
				audioreq.setNFiletype(n_filetype);
				audioreq.setSzTtsText(infotts.get_tts_text());
				audioreq.setSzFilename(infotts.get_serverfilename());
				audioreq.setNParam(get_filenumber());
				if(infotts.get_tts_text() != null && !infotts.get_tts_text().equals(""))
					audioreq.setNLangpk(infotts.get_n_langpk());
				break;
			case Sending_Files.SOUNDFILE_TYPE_LIBRARY_:
				//check serverside existence
				SoundInfoLibrary infolib = (SoundInfoLibrary)info;
                PAS.get_pas().add_event(Localization.l("sound_file_uploading_lib_file") + " : " + infolib.get_deptpk() + "/" + infolib.get_messagepk(), null);
				try {
					audioreq.setNFiletype(n_filetype);
					audioreq.setNMessagepk(Integer.parseInt(infolib.get_messagepk()));
					audioreq.setNDeptpk(info1.get_current_department().get_deptpk());
					audioreq.setNParam(get_filenumber());
					//form.setParameter("l_owner", new Integer(infolib.get_deptpk()).toString());
				} catch(Exception e) {
                    Error.getError().addError(Localization.l("common_error"),"Exception in send_wav",e,1);
				}
				break;
			case Sending_Files.SOUNDFILE_TYPE_LOCAL_:
				sz_filename = n_refno + "_" + get_filenumber() + ".wav";
				SoundInfoLocal infoloc = (SoundInfoLocal)info;
                PAS.get_pas().add_event(Localization.l("sound_file_uploading_local_file") + " : " + infoloc.get_file().getPath(), null);
				//upload
				try {
					File f = infoloc.get_file();
					FileInputStream is = new FileInputStream(f);
					
					int buffersize = -1;
					
					byte[] buffer = new byte[1000];
					ByteArrayOutputStream bo = new ByteArrayOutputStream();
					
					while((buffersize = is.read(buffer)) != -1) {
						bo.write(buffer);
					}
					
					audioreq.setNFiletype(n_filetype);
					audioreq.setNDeptpk(info1.get_current_department().get_deptpk());
					audioreq.setWav(bo.toByteArray());
					audioreq.setNParam(get_filenumber());
					//form.setParameter("file", sz_filename, is);
					//InputStream is = HttpPostForm.newInputStream(new java.io.File(infoloc.get_file().getPath()).
				} catch(Exception e) {
                    Error.getError().addError(Localization.l("common_error"),"Exception in send_wav",e,1);
				}
				break;
		}
		Pasws myService = new Pasws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
		try
		{
			AUDIORESPONSE response = myService.getPaswsSoap12().uPostAudio(logon, audioreq); 
			if(response.getNResponsecode()<0)
			{
				//ERROR occured
				PAS.get_pas().add_event("ERROR on SoundFile.send_wav() form-post " + response.getSzResponsetext(), new Exception(response.getSzResponsetext()));
                Error.getError().addError(Localization.l("common_error"),"Exception in send_wav",new Exception(response.getSzResponsetext()),1);
			}
				
		}
		catch(Exception e)
		{
            Error.getError().addError(Localization.l("sound_file_uploading_error"), e.getLocalizedMessage(), e, 1);
			return false;
		}
		/*try {
			form.post();
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR on SoundFile.send_wav() form-post " + e.getMessage(), e);
			Error.getError().addError("SoundFile","Exception in send_wav",e,1);
		}*/
		return true;
	}
}