package no.ums.pas.importer;

import java.io.*;
import java.util.ArrayList;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.HeadlessException;
import java.awt.event.*;

import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.core.variables;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.esri.ShapeImporter;
import no.ums.pas.importer.gis.*;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.sendpanels.SendWindow;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.FilePicker;

/*
.HODE
..TEGNSETT ANSI
..TRANSPAR
...KOORDSYS 22  
...ENHET 0.01
...ORIGO-NØ 0 0
..OMRÅDE
...MIN-NØ 6883943 388410
...MAX-NØ 6885939 391058
..SOSI-VERSJON  3.4
..SOSI-NIVÅ 4
..EIER "Møre og Romsdal Fylke"
..PRODUSENT "Møre og Romsdal Fylke"
!
!
.KURVE 1:
..OBJTYPE FlomArealGrense
..LTEMA 3280
..DIGDATO 20060117
..KVALITET 82
..INFORMASJON "Digitalisert på skjerm etter plott"
..NØ
688593817 38871428 ...KP 1
..NØ
688587301 38879340
688533312 38931135
688500001 38941175
688469550 38963847
688460042 38989645
688438101 39042703
688411760 39105672
688404689 39099137
688399471 39090042
688394418 39070827
688409643 39027011
688431984 38978608
688448074 38956999
688466292 38934991
688490361 38893835
688526464 38876215
688532980 38862785
688547807 38847958
688562301 38841176
688569282 38843902
688579123 38856867
688593817 38871428 ...KP 1
.FLATE 2:
..OBJTYPE FlomAreal
..FTEMA 3280
..DIGDATO 20060117
..KVALITET 82
..INFORMASJON "Digitalisert på skjerm etter plott"
..REF :1
..NØ
688494117 38918778
.SLUTT

	 */




abstract class PolygonParser extends FileParser {
	PolygonParser(File f, ActionListener callback) {
		super(f, callback, "act_polygon_imported_eof");
	}
	public abstract boolean parse();
	public abstract boolean create_values();
}






public class ImportPolygon implements ActionListener {
	public static final String MIME_TYPE_SOSI_ = ".sos";
	public static final String MIME_TYPE_ISO_  = ".iso";
	public static final String MIME_TYPE_GIS_  = ".gis";
	public static final String MIME_TYPE_TXT_  = ".txt";
	public static final String MIME_TYPE_SHP_  = ".shp";
	public static final String MIME_TYPE_DBF_  = ".dbf";
	public static final String MIME_TYPE_PRJ_  = ".prj";
	
	public static final String[][] FILE_FILTERS_ = new String[][] {
			{ "SOSI files", MIME_TYPE_SOSI_ }, 
			{ "ISO files", MIME_TYPE_ISO_ },
			{ "GIS files", MIME_TYPE_GIS_ },
			{ "Gemini files", MIME_TYPE_TXT_ },
			{ "ESRI Shape files", MIME_TYPE_SHP_ },
			{ "ESRI DBF files", MIME_TYPE_DBF_ },
			{ "ESRI Projection files", MIME_TYPE_PRJ_ },
			
	};
	
	private File m_polygonfile;
	private String m_sz_error;
	public String get_error() { return m_sz_error; }
	protected void set_error(String s) { m_sz_error = s; }
	private ActionListener m_callback;
	private String m_action;
	boolean m_b_isalert = false;
	
	public ImportPolygon(ActionListener callback, String action, boolean bIsAlert, Component parent) {
		
		
		FilePicker picker = new FilePicker(parent, 
								StorageController.StorageElements.get_path(StorageController.PATH_HOME_), 
								PAS.l("common_open_file"), FILE_FILTERS_, FilePicker.MODE_OPEN_){
			protected JDialog createDialog(Component parent) throws HeadlessException {
				JDialog dialog = super.createDialog(parent);
				dialog.setAlwaysOnTop(true);
				dialog.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(400, 200));
				return dialog;
			}
		};
		
		m_polygonfile = picker.getSelectedFile();
		m_callback = callback;
		m_b_isalert = bIsAlert; //has an impact on gis-import
		
		m_action = action;
		
		//if(m_callback==null)
		//	m_callback = (ActionListener)PAS.get_pas().get_sendcontroller().create_new_sending().get_toolbar();
		if(m_polygonfile!=null) {
			PAS.get_pas().add_event("Opening import file " + m_polygonfile.getPath(), null);
			parse_file(m_polygonfile);
			if(bIsAlert)
				callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_enable_next"));
		}
	}
	
	public ImportPolygon(ActionListener callback, String action, File polygonFile) {
		m_polygonfile = polygonFile;
		m_callback = callback;
		
		m_action = action;
		
		//if(m_callback==null)
		//	m_callback = (ActionListener)PAS.get_pas().get_sendcontroller().create_new_sending().get_toolbar();
		if(m_polygonfile!=null) {
			PAS.get_pas().add_event("Opening import file " + m_polygonfile.getPath(), null);
			parse_file(m_polygonfile);
		}
	}
	
	public void parse_file(File f) {
		if(f.getPath().endsWith(MIME_TYPE_SOSI_)) {
			try {
				read_as_sosi(f);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error reading selected file as SOSI-format");
				Error.getError().addError("ImportPolygon","Exception in parse_file",e,1);
			}
		} else if(f.getPath().endsWith(MIME_TYPE_ISO_)) {
			try {
				read_as_iso(f);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error reading selected file as ISO-format");
				Error.getError().addError("ImportPolygon","Exception in parse_file",e,1);
			}
		} else if(f.getPath().endsWith(MIME_TYPE_GIS_) || f.getPath().endsWith(MIME_TYPE_TXT_)) {
			try {
				read_as_gis(f);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error reading selected file as GIS-format");
				Error.getError().addError("ImportPolygon","Exception in parse_file",e,1);
			}
		} else if(f.getPath().endsWith(MIME_TYPE_SHP_) ||
				f.getPath().endsWith(MIME_TYPE_DBF_) ||
				f.getPath().endsWith(MIME_TYPE_PRJ_)) {
			try
			{
				read_as_shape(f);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error reading selected file as ESRI Shape-format");
				Error.getError().addError("ImportPolygon","Exception in parse_file",e,1);
				
			}
		} else {
			javax.swing.JOptionPane.showMessageDialog(null, "Unknown file extension", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}
	private boolean read_as_iso(File f) {
		return false;
	}
	private boolean read_as_sosi(File f) {
		if(!f.canRead()) {
			PAS.get_pas().add_event("Cannot read file " + f.getPath(), null);
			return false;
		}
		parse_sosi(f);
		return true;
	}
	private boolean read_as_shape(File f) {
		if(!f.canRead()) {
			PAS.get_pas().add_event("Cannot read file " + f.getPath(), null);
			return false;			
		}
		parse_shape(f);
		return true;
	}
	private boolean read_as_gis(File f) {
		if(!f.canRead()) {
			return false;
		}
		parse_gis(f);
		return true;
	}
	protected void set_actionlistener(ActionListener a) {
		m_callback = a;
	}
	
	private SosiFile parse_sosi(File f) {
		try {
			SosiFile sosi = new SosiFile();
			sosi.parse(f, this, m_action);
			return sosi;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("ImportPolygon","Exception in parse_sosi",e,1);
		}
		return null;
	}
	private ShapeImporter parse_shape(File f) {
		try
		{
			ShapeImporter shape = new ShapeImporter(f, this, m_action);
			//shape.parse();
			return shape;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("ImportPolygon","Exception in parse_shape",e,1);			
		}
		return null;
	}
	private void parse_gis(File f) {
		GISFile gis = new GISFile();
		m_action = "act_gis_import_finished";
		gis.parse(f, m_callback, m_action, this.m_b_isalert);
	}
	protected void reportSosiFile() {
		
	}
	private ArrayList <SendObject>m_sendings_found = new ArrayList<SendObject>();
	private ArrayList <ShapeStruct>m_shapes_found = new ArrayList<ShapeStruct>();
	public void actionPerformed(ActionEvent e) {
		if("act_sosi_parsing_complete".equals(e.getActionCommand())) {
			//SosiFile file = (SosiFile)e.getSource();
			//zoom into view
			if(m_callback!=null)
				m_callback.actionPerformed(new ActionEvent(m_sendings_found, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
				//m_callback.actionPerformed(e);
			else { //import from menu
				ArrayList<SendObject> sendings_found = m_sendings_found;
				for(int i=0; i < sendings_found.size(); i++) {
					SendObject obj = sendings_found.get(i);
					PAS.get_pas().actionPerformed(new ActionEvent(m_sendings_found.get(i), ActionEvent.ACTION_PERFORMED, "act_add_sending"));
				}				
			}
		}
		else if("act_shape_parsing_complete".equals(e.getActionCommand()))
		{
			try
			{
				ShapeImporter imp = (ShapeImporter)e.getSource();
				for(int i=0; i < imp.polylist.size(); i++)
				{
					PolygonStruct p = (PolygonStruct)imp.polylist.get(i);
					//SendObject obj = new SendObject("Imported polygon", SendProperties.SENDING_TYPE_POLYGON_, i, PAS.get_pas().get_sendcontroller(), variables.NAVIGATION);
					//obj.get_sendproperties().set_shapestruct(p);
					m_shapes_found.add(p);
					//m_sendobj.get_sendproperties().set_sendingname(get_flater().get_current_flate().get_name(), getFlateInformation(n_flate));
					//SendObject obj = new SendObject()
					//ArrayList<SendObject> sendings_found = m_sendings_found;
				}
				//if(m_callback!=null)
				//	m_callback.actionPerformed(new ActionEvent(m_shapes_found.toArray(), ActionEvent.ACTION_PERFORMED, "act_sosi_parsing_complete"));
				if(m_callback!=null)
				{
					for(int i=0; i < m_shapes_found.size(); i++)
					{
						SendObject obj = new SendObject("Imported polygon", SendProperties.SENDING_TYPE_POLYGON_, i, PAS.get_pas().get_sendcontroller(), variables.NAVIGATION);
						obj.get_sendproperties().set_shapestruct(m_shapes_found.get(i));
						obj.get_sendproperties().set_sendingname("Imported polygon " + i, "");
						m_sendings_found.add(obj);
					}
				
					m_callback.actionPerformed(new ActionEvent(m_sendings_found, ActionEvent.ACTION_PERFORMED, "act_sosi_parsing_complete"));
					return;
				}

				String [] sz_columns = { PAS.l("common_id"), PAS.l("common_name") };
				int [] n_width = { 50, 250 };
				boolean [] b_edit = { false, false };
				Dimension d = new Dimension(400, 500);
				ShapeStruct [] ss = new ShapeStruct[m_shapes_found.size()];
				if(m_shapes_found.size()>5)
				{
					for(int i=0; i < m_shapes_found.size(); i++)
					{
						ss[i] = m_shapes_found.get(i);
						//PAS.pasplugin.addShapeToPaint(ss[i]);
					}
					new SubsetSelect(sz_columns, n_width, b_edit, d, m_callback, ss);
				}
				else
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							
							for(int i=0; i < m_shapes_found.size(); i++)
							{
								if(i==0)
								{
									m_callback.actionPerformed(new ActionEvent(m_shapes_found.get(i), ActionEvent.ACTION_PERFORMED, "act_set_shape"));
									PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().set_sendingname("Imported polygon " + i, "");
								}
								else
								{
									SendObject obj = new SendObject("Imported polygon", SendProperties.SENDING_TYPE_POLYGON_, i, PAS.get_pas().get_sendcontroller(), variables.NAVIGATION);
									obj.get_sendproperties().set_shapestruct(m_shapes_found.get(i));
									obj.get_sendproperties().set_sendingname("Imported polygon " + i, "");
									//PAS.get_pas().actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
									PAS.get_pas().get_sendcontroller().add_sending(obj);
								}
			
							}
							PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
							PAS.get_pas().get_sendcontroller().get_activesending().get_toolbar().setActive();
							PAS.get_pas().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_activate_drawmode"));
						}
					});
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
	
		}
		else if("act_importsending_found".equals(e.getActionCommand())) {
			System.out.println("Sending found");
			m_sendings_found.add((SendObject)e.getSource());
		}
	}

}

