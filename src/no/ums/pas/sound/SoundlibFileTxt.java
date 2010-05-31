package no.ums.pas.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import no.ums.pas.*;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.ums.errorhandling.Error;

public class SoundlibFileTxt extends SoundlibFile {
	private int m_n_langpk = -1;
	private String m_sz_text;
	public int get_langpk() { return m_n_langpk; }
	
	public SoundlibFileTxt(String [] values) {
		super(values, "txt");
		set_template(true);
		m_n_langpk = new Integer(values[4]).intValue();
	}
	public SoundlibFileTxt() {
		super();
	}
	public String get_text() {
		return m_sz_text;
	}
	protected boolean read(String sz_dest) {
		String sz_text = "", sz_temp = "";
		//m_sz_downloadfile = sz_dest;
		if(get_messagepk().equals("-1")) {
			m_sz_text = "";
			return true;
		}
		File f = new File(StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_) + sz_dest);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			while(1==1) {
				sz_temp = br.readLine();
				if(sz_temp==null)
					break;
				sz_text += sz_temp;
			}
			br.close();
		} catch (FileNotFoundException ex) {
			PAS.get_pas().add_event("File not found (" + f.getPath() + ") " + ex.getMessage(), ex);
			Error.getError().addError(PAS.l("common_error"),"Exception in read",ex,1);
			return false;
		} catch (IOException ex) {
			PAS.get_pas().add_event("IOException on (" + f.getPath() + ") " + ex.getMessage(), ex);
			Error.getError().addError(PAS.l("common_error"),"Exception in read",ex,1);
			return false;
		}
		m_sz_text = sz_text;
		return true;
	}		

}