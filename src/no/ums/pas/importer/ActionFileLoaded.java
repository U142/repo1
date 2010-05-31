package no.ums.pas.importer;

import java.awt.event.ActionEvent;

public class ActionFileLoaded extends ActionEvent {
	public static final long serialVersionUID = 1;
	private String m_sz_filetype;
	public String get_filetype() { return m_sz_filetype; }
	public ActionFileLoaded(Object obj, int n_cmd, String sz_command, String sz_filetype) {
		super(obj, n_cmd, sz_command);
		m_sz_filetype = sz_filetype;
	}
}