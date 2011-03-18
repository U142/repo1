package no.ums.pas.sound;

import no.ums.pas.localization.Localization;
import no.ums.pas.ums.errorhandling.Error;

import java.io.File;


public class SoundlibFileWav extends SoundlibFile {
	File m_f = null;
	public File get_file() { return m_f; }
	public SoundlibFileWav(String [] values) {
		super(values, "wav");
		set_template(false);
	}
	public SoundlibFileWav(String sz_url, boolean b_shared, int n_deptpk, String sz_name, String sz_refno_fileno, String sz_moduledef, String sz_fileext) {
		super(sz_url, b_shared, n_deptpk, sz_name, sz_refno_fileno, sz_moduledef, sz_fileext);
		set_template(false);
	}

	protected boolean read(String sz_dest) {
		try {
			m_f = new File(sz_dest);
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Exception in read",e,1);
			return false;
		}
		if(m_f.canRead())
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	
}