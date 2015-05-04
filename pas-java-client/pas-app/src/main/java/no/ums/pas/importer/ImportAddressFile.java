package no.ums.pas.importer;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.gis.GISFile;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.FilePicker;

public class ImportAddressFile implements ActionListener {
	private static final Log log = UmsLog.getLogger(ImportAddressFile.class);
	
	
	public static final String MIME_TYPE_TXT_  = ".txt";
	
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
		{ Localization.l("FileChooser.fileTypeGemini"), MIME_TYPE_TXT_ },
	};
	
	private File m_addressfile;
	private String m_sz_error;
	public String get_error() { return m_sz_error; }
	protected void set_error(String s) { m_sz_error = s; }
	private ActionListener m_callback;
	private String m_action;
	boolean m_b_isalert = false;
	public ActionListener get_callback(){ return m_callback; }
	
	
	public ImportAddressFile(ActionListener callback, String action, boolean bIsAlert, Component parent) {
		log.info("#######ImportAddressFile ################");	
		FilePicker picker = new FilePicker(parent,
				StorageController.StorageElements.get_path(StorageController.PATH_HOME_),
              Localization.l("common_open_file"), FILE_FILTERS_, FilePicker.MODE_OPEN_){
              protected JDialog createDialog(Component parent) throws HeadlessException {
              JDialog dialog = super.createDialog(parent);
              dialog.setAlwaysOnTop(true);
              dialog.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(400, 200));
              return dialog;
             }
            };

                m_addressfile = picker.getSelectedFile();
                m_callback = callback;
                m_b_isalert = bIsAlert; //has an impact on gis-import

                m_action = action;

                if(m_addressfile!=null) {
        			PAS.get_pas().add_event("Opening import file " + m_addressfile.getPath(), null);
        			parse_file(m_addressfile);
        			if(bIsAlert)
        				callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_enable_next"));
        		}
		
	}
	
	
	public void parse_file(File f) {
		
		
		if(f.getPath().endsWith(MIME_TYPE_TXT_)){
			try{
				read_as_txt(f);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error reading selected file as GIS-format");
				Error.getError().addError("ImportFIle","Exception in parse_file",e,1);
		   }
	  }   
		
	      else {
			javax.swing.JOptionPane.showMessageDialog(null, "Unknown file extension", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		   }
     
	}
	private boolean read_as_txt(File f) {
		if(!f.canRead()) {
			return false;
		}
	     parse_txt(f);
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void parse_txt(File f) {
		GISFile gis = new GISFile();
		gis.setFilterPreview(true);
		m_action = "act_gis_import_finished";
		gis.parse(f, m_callback, m_action, this.m_b_isalert);
	}
	
}
