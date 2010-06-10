package no.ums.pas.status;

import javax.swing.*;
import java.awt.*;

import java.awt.Frame;
import javax.swing.filechooser.*;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.mainui.*;
import no.ums.pas.core.project.*;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.tools.*;

import java.io.*;
import java.util.*;


public class StatusExport
{
	Project m_p;
	ArrayList<StatusListObject> m_sa;
	StatusListObject m_s;
	StatusItemList m_statusitems;
	StatusCodeList m_statuscodes;
	StatusItemObject m_item;
	
	public StatusExport(Project p)
	{
		m_p = p;
		m_sa = p.get_status_sendings();
	}
	
	/*
	 * pop up GUI for settings
	 */
	public void show(JFrame opener, LookAndFeel laf, StatusItemList l, StatusCodeList s)
	{
		m_statusitems = l;
		m_statuscodes = s;
		new StatusExportGUI().init(opener, laf);
	}
	
	/*
	 * supports txt and html
	 */
	protected boolean export(File f, String sz_filetype)
	{
		FileWriter w = null;
		try
		{
			w = new FileWriter(f);
			if(sz_filetype.equalsIgnoreCase("HTML file"))
			{
				return export_as_html(w);
			}
			else if(sz_filetype.equalsIgnoreCase("CSV file"))
			{
				return export_as_csv(w);
			}
			else
			{
				return export_as_txt(w);
			}
		}
		catch(Exception e) 
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("Error", "Error exporting status", e, 1);
		}
		finally
		{
			try
			{
				w.close();
				//Runtime.getRuntime().exec("iexplore.exe " + "\"" + f.getPath() + "\"");
				java.awt.Desktop.getDesktop().browse(f.toURI()); //new java.net.URL("file://" + f.toURI()()).toURI());
			}
			catch(Exception err) {
				no.ums.pas.ums.errorhandling.Error.getError().addError("Error opening file", "Could not open statusfile", err, 1);
			}
		}
		
		return false;
	}
	protected boolean export_as_txt(FileWriter w)
	{
		return _export_separator(w, "	");
	}
	protected boolean export_as_csv(FileWriter w)
	{
		return _export_separator(w, ",");
	}
	
	protected boolean _export_separator(FileWriter w, String separator)
	{
		StatusItemObject.SetExportFields(StatusItemObject.ADR_EXPORT_FIELD_REFNO |
				 StatusItemObject.ADR_EXPORT_FIELD_ITEM |
				 StatusItemObject.ADR_EXPORT_FIELD_ADRNAME | 
				 //StatusItemObject.ADR_EXPORT_FIELD_BDATE |
				 StatusItemObject.ADR_EXPORT_FIELD_POSTADDR | 
				 //StatusItemObject.ADR_EXPORT_FIELD_HOUSE | 
				 StatusItemObject.ADR_EXPORT_FIELD_POSTNO | 
				 StatusItemObject.ADR_EXPORT_FIELD_STATUS |
				 StatusItemObject.ADR_EXPORT_FIELD_STATUSNAME | 
				 StatusItemObject.ADR_EXPORT_FIELD_AREA | 
				 StatusItemObject.ADR_EXPORT_FIELD_DATE | 
				 StatusItemObject.ADR_EXPORT_FIELD_TIME | 
				 StatusItemObject.ADR_EXPORT_FIELD_LAT | 
				 StatusItemObject.ADR_EXPORT_FIELD_LON |
				 StatusItemObject.ADR_EXPORT_FIELD_PHONE,
				 separator, false);
	
		try
		{
			Iterator it = m_statusitems.iterator();
			w.write(StatusItemObject.EXPORT_HEADING);
			ComboRow cr;
			while(it.hasNext())
			{
				// This is required for exporting based on filter settings
				if(PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedIndex() == 0) {
					m_item = (StatusItemObject)it.next();
					w.write(m_item.exportString(m_statuscodes));
				}
				else {
					m_item = (StatusItemObject)it.next();
					cr = (ComboRow)PAS.get_pas().get_eastcontent().get_statuspanel().get_combo_filter().getSelectedItem();
					if(m_item.get_refno() == ((StatusSending)cr.getId()).get_refno())
						w.write(m_item.exportString(m_statuscodes));
				}
			}
		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("Error", "Error writing status to file", e, 1);
		}
		finally
		{
		}
		return true;
	}
	
	protected boolean export_as_html(FileWriter w)
	{
		StatusItemObject.SetExportFields(StatusItemObject.ADR_EXPORT_FIELD_REFNO |
				 StatusItemObject.ADR_EXPORT_FIELD_ITEM |
				 StatusItemObject.ADR_EXPORT_FIELD_ADRNAME | 
				 //StatusItemObject.ADR_EXPORT_FIELD_BDATE |
				 StatusItemObject.ADR_EXPORT_FIELD_POSTADDR | 
				 //StatusItemObject.ADR_EXPORT_FIELD_HOUSE | 
				 StatusItemObject.ADR_EXPORT_FIELD_POSTNO | 
				 StatusItemObject.ADR_EXPORT_FIELD_STATUS |
				 StatusItemObject.ADR_EXPORT_FIELD_STATUSNAME | 
				 StatusItemObject.ADR_EXPORT_FIELD_AREA | 
				 StatusItemObject.ADR_EXPORT_FIELD_DATE | 
				 StatusItemObject.ADR_EXPORT_FIELD_TIME | 
				 StatusItemObject.ADR_EXPORT_FIELD_LAT | 
				 StatusItemObject.ADR_EXPORT_FIELD_LON |
				 StatusItemObject.ADR_EXPORT_FIELD_PHONE,
				 ",", true);
		try
		{
			Iterator it = m_statusitems.iterator();
			w.write("<HTML>");
			w.write("<TABLE>");
			w.write("<tr>");
			w.write(StatusItemObject.EXPORT_HEADING);
			w.write("</tr>");
			while(it.hasNext())
			{
				m_item = (StatusItemObject)it.next();
				w.write(m_item.exportStringHtml(m_statuscodes));
			}			
			w.write("</TABLE>");
			w.write("</HTML>");
		}
		catch(Exception e)
		{
			
		}
		return true;
	}
	


	/*
	 * Show project info and specify file for export
	 */
	protected class StatusExportGUI //extends JDialog
	{
		/*protected LightPanel m_panel = new LightPanel();
		private StdTextLabel m_lbl_time;
		private StdTextLabel m_lbl_sendings;
		*/
		public static final String MIME_TYPE_CSV_  = ".csv";
		public static final String MIME_TYPE_HTML_ = ".html";
		public static final String MIME_TYPE_TXT_ = ".xls";
		public final String[][] FILE_FILTERS_ = new String[][] {
			{ "CSV file", MIME_TYPE_CSV_ },
			{ "HTML file", MIME_TYPE_HTML_ },
			{ "Tab separated text file (for Excel)", MIME_TYPE_TXT_ }
		};	
		
		public StatusExportGUI()
		{
			//super(opener, "Status Export", ModalityType.DOCUMENT_MODAL);
		}
		public void init(JFrame opener, LookAndFeel laf)
		{
			//m_lbl_time = new StdTextLabel("Last Update", true);
			//m_lbl_sendings = new StdTextLabel("", true);
			FilePicker picker = new FilePicker(opener, 
					StorageController.StorageElements.get_path(StorageController.PATH_HOME_), 
					"Open file", FILE_FILTERS_, FilePicker.MODE_SAVE_);	
			//picker.getSelectedFile();
			if(picker!= null && picker.getSelectedFile() != null)
				export(picker.getSelectedFile(), picker.getFileType());
		}
	}
}







