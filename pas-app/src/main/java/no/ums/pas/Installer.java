package no.ums.pas;

/*import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;*/

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.ums.errorhandling.Error;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

//import com.roxes.win32.LnkFile;
//import com.roxes.win32.Win32;


class JNLP {
	public JNLP() {
		
	}
}

class Downloader extends Thread {
    
    private static final Log log = UmsLog.getLogger(Downloader.class);

	private String m_sz_sourcefile, m_sz_destfile;
	private boolean m_b_overwrite;
	private boolean m_b_success = false;
	public boolean get_success() { return m_b_success; }
	private boolean m_b_finished = false;
	public boolean isFinished() { return m_b_finished; }
	private String m_sz_action_command;
	private ActionListener m_callback = null;
	private Object m_callback_object = null;
	private boolean m_b_fileexists = false;
	public boolean file_exists() { return m_b_fileexists; }

	public void run() {
		m_b_success = download_and_save();
		if(m_callback!=null)
			onDownloadFinished();
		m_b_finished = true;
	}
	private void onDownloadFinished() {
		try {
			m_callback.actionPerformed(new ActionEvent(m_callback_object, ActionEvent.ACTION_PERFORMED, m_sz_action_command));
		} catch(Exception e) {
			Error.getError().addError("Installer","Exception in onDownloadFinished",e,1);
			PAS.get_pas().add_event("callback failed Downloader.onDownloadFinished() " + e.getMessage(), e);
		}
	}
	Downloader(String sz_sourcefile, String sz_destfile, boolean b_overwrite) {
		super("Installer thread");
		m_sz_sourcefile = sz_sourcefile;
		m_sz_destfile   = sz_destfile;
		m_b_overwrite   = b_overwrite;
	}	
	Downloader(String sz_sourcefile, String sz_destfile, boolean b_overwrite,
				ActionListener callback, String sz_action_command, Object callback_object) {
		this(sz_sourcefile, sz_destfile, b_overwrite);
		m_callback = callback;
		m_sz_action_command = sz_action_command;
		m_callback_object = callback_object;
	}
	public boolean download_and_save() {
		PAS.get_pas().add_event("Downloading " + m_sz_sourcefile + " to " + m_sz_destfile, null);

		try {
			URL url = new URL(m_sz_sourcefile);
			String sz_local_file = m_sz_destfile;//StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "PAS.jnlp";
			//file version check
			File f = new File(sz_local_file);
			if(f.exists()) {
				if(m_b_overwrite)
					f.delete();
				else
					return m_b_fileexists = true;
;
				//f.delete();
				m_b_fileexists = true;
			} else {
				//m_b_firsticon = true;
			}
			InputStream is = url.openStream();
			BufferedInputStream inn = new BufferedInputStream(is);

			//BufferedWriter ut = null;
			FileOutputStream fos = null;
			try {
				//ut = new BufferedWriter(new FileWriter(f));
				fos = new FileOutputStream(f);
			} catch(Exception e) {
				log.warn(e.getMessage(), e);
				return false;
			}
			int c = 0;
			while ((c = inn.read()) != -1) {//ut.write(c);
				fos.write(c);
			}
			inn.close(); fos.close();				
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR download_and_save(): " + e.getMessage(), e);
			Error.getError().addError("Installer","Exception in download_and_save",e,1);
			return false;
		}
		return true;
	}	
}

public class Installer {
	private String m_sz_local_lnkfile;
	private String m_sz_local_icofile;
	private boolean m_b_firsticon = false;
	Downloader dl;

	public Installer() {
	}
	
	public void save_jnlp() {
		try {
			m_sz_local_lnkfile = StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "PAS.jnlp";
			m_sz_local_icofile = StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "pas.ico";
			//download_and_save(PAS.get_pas().get_sitename() + "PASapp.jnlp", m_sz_local_lnkfile, true);
			//download_and_save(PAS.get_pas().get_sitename() + "images/pas.ico", m_sz_local_icofile, false);
			//m_b_fisticon = !dl.file_exists();
			//if(!dl.file_exists()) {
			//	ask_shortcut();
			//}
		} catch(Exception e) {
			Error.getError().addError("Installer","Exception in save_jnlp",e,1);
			PAS.get_pas().add_event("Error: " + e.getMessage(), e);
		}
	}
		
	private String m_sz_sourcefile, m_sz_destfile;
	private boolean m_b_overwrite;
	
	public boolean download_and_save(String sz_sourcefile, String sz_destfile, boolean b_overwrite) {
		//return new Downloader(sz_sourcefile, sz_destfile, b_overwrite).download_and_save();
		dl = new Downloader(sz_sourcefile, sz_destfile, b_overwrite);
		try {
			dl.start();
		} catch(Exception e) {
			Error.getError().addError("Installer","Exception in download_and_save",e,1);
			return false;
		}
		while(!dl.isFinished()) {
			try {
				Thread.sleep(50);
			} catch(Exception e) {Error.getError().addError("Installer","Exception in download_and_save",e,1);}
		}
		return dl.get_success();
	}
	public boolean download_and_save(String sz_sourcefile, String sz_destfile, boolean b_overwrite,
												ActionListener callback, String sz_action_command,
												Object callback_object) {
		dl = new Downloader(sz_sourcefile, sz_destfile, b_overwrite, callback, 
							sz_action_command, callback_object);
		try {
			dl.start();
		} catch(Exception e) {
			Error.getError().addError("Installer","Exception in download_and_save",e,1);
			return false;
		}
		return true;
		//return dl.get_success();
	}
	

	public void ask_shortcut() {
		if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Do you want a shortcut to be placed on the desktop?", "Installer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			create_shortcut();
	}
	public void create_shortcut() {
		/*try
		{
			String sz_desktop_path;
			File f = Win32.getSpecialDirectory(Win32.SPECIALDIRECTORY_PERSONAL_DESKTOP);
			sz_desktop_path = f.getAbsolutePath() + "\\";
			PAS.get_pas().add_event("Users desktop path = " + sz_desktop_path, null);
			LnkFile lnk = new LnkFile(sz_desktop_path, "");
			lnk.setDescription("Start PAS");
			lnk.setPath(m_sz_local_lnkfile);
			lnk.setName("Start UMS Population Alert System");
			lnk.setWorkingDirectory(StorageController.StorageElements.get_path(StorageController.PATH_HOME_));
			lnk.setIconLocation(m_sz_local_icofile);
			lnk.setIconIndex(0);
			lnk.save();		
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"),"Unable to create desktop icon.",e,1);
		}*/
	}
	public static synchronized void cleanup() {
		File f = new File(StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_));
		if(f.listFiles().length > 0) {
			//if(JOptionPane.showConfirmDialog(PAS.get_pas(), "Do you want to delete the " + f.listFiles().length + " temporary soundfile(s) from \n" + StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				File files[] = f.listFiles();
				for(int i=0; i < files.length; i++) {
					try {
						files[i].delete();
					} catch(Exception e) {
						Error.getError().addError("Installer","Exception in cleanup",e,1);
						JOptionPane.showMessageDialog(PAS.get_pas(), "Error: " + files[i].getPath() + " could not be deleted (" + e.getMessage() + ")");
					}
				}
				//JOptionPane.showMessageDialog(PAS.get_pas(), f.listFiles()[0].getPath());
			//}
		}
	}

}