package no.ums.pas.ums.tools;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.io.File;

public class FilePicker extends JFileChooser {
	public static final long serialVersionUID = 1;
	public static final int MODE_OPEN_ = 1;
	public static final int MODE_SAVE_ = 2;
	private String [][] m_sz_filter;
	private Component m_parent;
	private int m_mode;
	//private String m_sz_lookin = null;
	private String m_sz_selectedfiletype;
	private String m_sz_extension;
	//private String m_sz_selectedfileext;
	
	public FilePicker(Component parent, String sz_path, String sz_title, String [][] sz_filter, int MODE) {
		super(sz_path);
		m_parent	= parent;
		m_sz_filter = sz_filter;
		m_mode		= MODE;
		/*try {
			UIManager.setLookAndFeel(laf);
		} catch(Exception e) {
			Error.getError().addError("FilePicker","Exception in FilePicker",e,1);
		}*/
		init();
	}
	public String getFileType() {
		return m_sz_selectedfiletype;
	}
	public String getFileExtension() {
		return m_sz_extension;
	}
	private void init() {
		// Prevents 2 filepickers from beeing opened when project is already loaded
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = -1;
		if(m_mode == MODE_OPEN_) {
			
			this.setDialogType(JFileChooser.OPEN_DIALOG);
			setFileFilter(new Filter(m_sz_filter));
			returnVal = this.showOpenDialog(m_parent);
		}
		else if(m_mode == MODE_SAVE_) {
			this.setDialogType(JFileChooser.SAVE_DIALOG);
			this.setAcceptAllFileFilterUsed(false);
			for(int i=0; i < m_sz_filter.length; i++) {
				this.addChoosableFileFilter(new SingleFilter(m_sz_filter[i]));
			}
			returnVal = this.showSaveDialog(m_parent);
			// Prevents 2 filepickers from beeing opened when project is already loaded
			if(getSelectedFile() != null) {
					if(!getSelectedFile().getPath().endsWith(((SingleFilter)getFileFilter()).getExtension()))
				setSelectedFile(new File(getSelectedFile().getPath() + ((SingleFilter)getFileFilter()).getExtension()));
			m_sz_selectedfiletype = ((SingleFilter)getFileFilter()).getType();
			m_sz_extension = ((SingleFilter)getFileFilter()).getExtension().replace(".", "");
			}
		}
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	
	    }

	}
	
	class SingleFilter extends FileFilter {
		private String[] m_sz_filters;
		private String m_sz_description = "";
		private String m_sz_extension = "";
		private String m_sz_type = "";
		public String getExtension() { return m_sz_extension; }
		public String getType() { return m_sz_type; }

		SingleFilter(String[] sz_filters) {
			m_sz_filters = sz_filters;
			m_sz_description = m_sz_filters[0] + "(" + m_sz_filters[1] + ")";
			m_sz_extension = m_sz_filters[1];
			m_sz_type = m_sz_filters[0];
		}
		public boolean accept(File f) {
			//always show directories
			if(f.isDirectory())
				return true;
			if(f.getPath().endsWith(m_sz_filters[1])) {
				return true;
			} /*else {
				f = new File(f.getPath() + m_sz_filters[1]);
				return true;
			}*/
			return false;
		}
		public String getDescription() {
			return m_sz_description;
		}
	}
	
	class Filter extends FileFilter {
		private String [][] m_sz_filters;
		private String m_sz_description = "";
		Filter(String [][] sz_filters) {
			m_sz_filters = sz_filters;
			for(int i=0; i < m_sz_filters.length; i++) {
				m_sz_description += m_sz_filters[i][0] + "(" + m_sz_filters[i][1] + "); ";
			}
		}
		public boolean accept(File f) {
			if(f.isDirectory())
				return true;
			for(int i=0; i < m_sz_filters.length; i++) {
				if(f.getPath().endsWith(m_sz_filters[i][1]))
					return true;
			}
			return false;
			//return true;
		}
		public String getDescription() {
			return m_sz_description;
		}
	}
	
	
}