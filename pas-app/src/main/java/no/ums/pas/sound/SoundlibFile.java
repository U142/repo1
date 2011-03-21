package no.ums.pas.sound;

import no.ums.pas.Installer;
import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;


public abstract class SoundlibFile extends Object implements ActionListener, Comparable<SoundlibFile>{
	//'l_sh, deptpk, name, pk, def, langpk, template
	private int m_n_listpriority = 0;
	private boolean m_b_shared;
	private int m_n_deptpk;
	private int m_n_type;
	private String m_sz_name;
	private String m_sz_messagepk = "-1";
	//private int m_n_moduledef;
	private String m_sz_moduledef;
	private boolean m_b_template;
	protected void set_template(boolean b) { m_b_template = b; }
	public boolean get_template() { return m_b_template; }
	protected String m_sz_downloadfile;
	//public String get_downloadfile() { return m_sz_downloadfile; }
	protected boolean m_b_file_downloaded = false;
	public boolean isFileDownloaded() { return m_b_file_downloaded; }
	protected void setFileDownloaded(boolean b) { m_b_file_downloaded = b; }
	//public abstract boolean load_file();
	public String get_messagepk() { return m_sz_messagepk; }
	public String get_name() { return m_sz_name; }
	public String get_moduledef() { return m_sz_moduledef; }
	public int get_type() { return m_n_type; }
	public void setListPriority(int n) { m_n_listpriority = n; }
	public int getListPriority() { return m_n_listpriority; }
	protected abstract boolean read(String sz_dest);
	private String m_sz_sourcefile;
	private String m_sz_localfile;
	private String m_sz_fileext;
	public String get_localfile() { return m_sz_localfile; }
	private ActionListener m_callback = null;
	private String m_sz_action_command;
	private SoundRecorderPanel m_soundrecordpanel;
	public int get_deptpk() { return m_n_deptpk; }
		
	public SoundlibFile(String [] values, String sz_fileext) {
		this(new Boolean(values[0]).booleanValue(), Integer.valueOf(values[1]),
				values[2], values[3], values[4], Integer.valueOf(values[7]), sz_fileext);
	}
	public SoundlibFile(boolean b_shared, int n_deptpk, String sz_name, String sz_messagepk, String sz_moduledef, int n_type, String sz_fileext) {
		m_b_shared	= b_shared;
		m_n_deptpk	= n_deptpk;
		m_sz_name	= sz_name;
		m_sz_messagepk= sz_messagepk;
		m_sz_moduledef= sz_moduledef;
		m_sz_fileext = sz_fileext;
		m_n_type = n_type;
		m_sz_sourcefile	= /*PAS.get_pas().get_sitename()*/ PAS.get_pas().getVB4Url() + "/bbmessages/" + m_n_deptpk + "/" + this.get_messagepk() + "." + sz_fileext;
		m_sz_localfile  = this.get_messagepk() + "." + sz_fileext;
	}
	
	/** use this constructor for dynamic audio files in status, specify urlpath as e.g. /audiofiles/ */
	public SoundlibFile(String sz_urlpath, boolean b_shared, int n_deptpk, String sz_name, String sz_refno_fileno, String sz_moduledef, String sz_fileext) {
		m_b_shared	= b_shared;
		m_n_deptpk	= n_deptpk;
		m_sz_name	= sz_name;
		m_sz_messagepk= sz_refno_fileno;
		m_sz_moduledef= sz_moduledef;
		m_sz_fileext = sz_fileext;
		m_n_type = -1;
		//m_sz_sourcefile	= PAS.get_pas().get_sitename() + "bbmessages/" + m_n_deptpk + "/" + this.get_messagepk() + "." + sz_fileext;
		m_sz_sourcefile = /*PAS.get_pas().get_sitename()*/ PAS.get_pas().getVB4Url() + "/" + sz_urlpath + "/" + sz_refno_fileno + "." + sz_fileext;
		m_sz_localfile  = this.get_messagepk() + "." + sz_fileext;
	}
	
	public SoundlibFile() {
		
	}
	public String toString() {
		return get_name();
	}
	public boolean load_file(ActionListener callback, String sz_action_command) {
		m_callback = callback;
		m_sz_action_command = sz_action_command;
		if(!isFileDownloaded() && get_messagepk() != "-1") {
			String sz_local = StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_) + m_sz_localfile;
			if((callback!=null ? new Installer().download_and_save(m_sz_sourcefile, sz_local, true, this, "act_download_finished", this) : new Installer().download_and_save(m_sz_sourcefile, sz_local, true))) {
				return true;
			}
		} else if(get_messagepk().equals("-1") || isFileDownloaded()) {
			actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_download_finished"));
			return true;
		}
		return isFileDownloaded();
	}
	public void actionPerformed(ActionEvent e) {
		if("act_download_finished".equals(e.getActionCommand())) {
			read(m_sz_localfile);
			setFileDownloaded(true);
			try {
				if(m_soundrecordpanel == null)
					m_callback.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, m_sz_action_command));
			} catch(Exception err) {
				PAS.get_pas().add_event("Exception on SoundlibFile.actionPerformed " + err.getMessage(), err);
                Error.getError().addError(Localization.l("common_error"),"Exception in actionPerformed",err,1);
			}
		}
	}
	
	public static class CompareMessagePk implements Comparator<SoundlibFile>
	{
		@Override
		public int compare(SoundlibFile o1, SoundlibFile o2) {
			return (int)(Long.valueOf(o2.get_messagepk())-Long.valueOf(o1.get_messagepk()));
		}		
	}

	@Override
	public int compareTo(SoundlibFile o) {
		if(o.getListPriority() != this.getListPriority())
			return o.getListPriority() - this.getListPriority();
		if(o.getListPriority()!=0)
		{
			return (int)(Long.valueOf(o.get_messagepk())-Long.valueOf(get_messagepk()));			
		}
		return get_name().toUpperCase().compareTo(o.get_name().toUpperCase());
	}

}



