package no.ums.pas.importer.gis;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;

import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.PopupDialog;
import no.ums.pas.ums.tools.Utils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;


 
public class PreviewFrame extends JDialog implements ComponentListener, ActionListener {
	public static final long serialVersionUID = 1;
    //private static final Log logger = UmsLog.getLogger(PreviewFrame.class);
    PreviewPanel m_panel;
	private JButton m_btn_finish;
	PreviewOptions m_options = null;
	public PreviewPanel get_previewpanel() { return m_panel; }
	private StatisticsPanel m_statisticspanel;
	public StatisticsPanel get_statisticspanel() { return m_statisticspanel; }
	public String encoding = Localization.l("importpreview_encoding_iso_8859_15");
	public GISFile m_gis;
	private String importErrorMessage;

    public PreviewFrame(GISFile gis) {
		super(PAS.get_pas(), Localization.l("common_preview"), true);
		try {
			setAlwaysOnTop(true);
		} catch(Exception e) {
			
		}
		init_common();

		m_gis = gis;
		m_options = new PreviewOptions(this, gis.getIsAlert());
		m_panel = new PreviewPanel(gis, this);
		m_btn_finish = new JButton(Localization.l("common_finish"));
		//setSize(620, 500);
		int x = 800;
		int y = 600;
		setBounds(Utils.screendlg_upperleft(x, y).width, Utils.screendlg_upperleft(x, y).height, x, y);
		setLayout(new BorderLayout());
		getContentPane().add(m_panel, BorderLayout.CENTER);
		getContentPane().add(m_btn_finish, BorderLayout.SOUTH);
		m_btn_finish.setEnabled(false);
		m_btn_finish.setActionCommand("act_finish");
		m_btn_finish.addActionListener(this);
		actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_options_view"));
		setVisible(true);
		addComponentListener(this);
		resize();
	}
	
	// This is used for generate sending, skips the preview and only shows progressbar
	public PreviewFrame(GISList list, SendObject so) {
		try {
			init_common();
			get_statisticspanel().n_filelines = list.size();
			PreviewPanel pp = new PreviewPanel(so, false, this, encoding);
			GISRecord gisr;
			File gistemp = new File(StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + PAS.get_pas().get_parmcontroller().getHighestTemp() + ".txt");
			try
			{
				gistemp.delete();
			}
			catch(Exception e) {
                //logger.warn("Failed to delete from gistemp", e);
            }
			FileWriter f = new FileWriter(gistemp);
			PrintWriter writer = new PrintWriter(f);
			
			Iterator it = list.iterator();
			while(it.hasNext()) {
				gisr = (GISRecord)it.next();
				writer.print(gisr.get_municipal() + "\t" + gisr.get_streetid() + 
						"\t" + gisr.get_houseno() + "\t" + gisr.get_letter() + 
						"\t" + gisr.get_name1() + "\t" + gisr.get_name2() + "\r\n");
			}
			writer.flush();
			writer.close();

			pp.actionPerformed(new ActionEvent(gistemp,ActionEvent.ACTION_PERFORMED,"act_autofetch_addresses"));
			addComponentListener(this);
			resize();

			
		} catch(IOException ioe) {
			Error.getError().addError("IO Exception", "Error opening file in gis directory", ioe, Error.SEVERITY_ERROR);
		}
	}
	public PreviewFrame(SendObject so) {
		super(PAS.get_pas(), Localization.l("importpreview_file_content_tooltip"), true);
		
		init_common();
		try
		{
			get_statisticspanel().n_filelines = so.get_sendproperties().typecast_gis().get_gislist().size();
		}
		catch(Exception e)
		{
			
		}
		m_panel = new PreviewPanel(so, true, this, encoding);
		int x = 800;
		int y = 600;
		setBounds(Utils.screendlg_upperleft(x, y).width, Utils.screendlg_upperleft(x, y).height, x, y);
		
		setLayout(new BorderLayout());
		//actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_options_view"));
		getContentPane().add(m_panel, BorderLayout.CENTER);
		actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));
		setVisible(true);
		addComponentListener(this);
		resize();

	}
	public void init_common() {
		m_statisticspanel = new StatisticsPanel();		
	}
	
	public void componentResized(ComponentEvent e) {
		resize();
	}
	public void resize() {
		if(m_panel!=null)
			m_panel.revalidate();
		if(m_options!=null)
			m_options.revalidate();
		if(m_statisticspanel!=null)
			m_statisticspanel.revalidate();
		setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(350, 400));
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void actionPerformed(ActionEvent e) {
		if("act_first_row_has_columnnames".equals(e.getActionCommand())) {
			m_panel.get_previewlist().actionPerformed(e);
		}
		else if("act_fetch_addresses".equals(e.getActionCommand())) {
		//get event from PreviewOptions
            if(is_valid_toPerform_action())   {
            m_panel.actionPerformed(e);
				enableControls(false);
            }
            else { 
	            if("Property".equals(get_previewpanel().getM_import_type())){
	            	JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                        Localization.l("importpreview_please_specify_property"), Localization.l("common_warning"),
	                        JOptionPane.WARNING_MESSAGE);
	            }
	            else {
	            	//just in case, for street import type if no error message is set then set the default error message
	            	if(importErrorMessage==null)
	            		importErrorMessage = Localization.l("importpreview_please_specify");
	            	
	            	JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                        importErrorMessage, Localization.l("common_warning"),
	                        JOptionPane.WARNING_MESSAGE);
	            }
            }
        }
		else if("act_gis_finish".equals(e.getActionCommand())) {
            m_panel.actionPerformed(e);
            this.setVisible(false);
		}
		else if("act_set_statistics_view".equals(e.getActionCommand())) {
			if(m_options!=null) {
				getContentPane().remove(m_options);
				//getContentPane().remove(null);
			}
			if(m_statisticspanel!=null)
				getContentPane().add(m_statisticspanel, BorderLayout.NORTH);						
			this.doLayout();
			resize();
			repaint();

		}
		else if("act_set_options_view".equals(e.getActionCommand())) {
			if(m_statisticspanel!=null)
				getContentPane().remove(m_statisticspanel);
			if(m_options!=null)
				getContentPane().add(m_options, BorderLayout.NORTH);
			this.doLayout();
			resize();
			repaint();
		}
		else if("act_update_statistics".equals(e.getActionCommand())) {
			if(get_statisticspanel()!=null) {
				if(m_btn_finish!=null)
					m_btn_finish.setEnabled(true);
				get_statisticspanel().actionPerformed(e);
			}
		}
		else if("act_goto_next_valid".equals(e.getActionCommand())) {
			if(m_options!=null) {
				m_options.actionPerformed(e);
			}
		}
		else if("act_finish".equals(e.getActionCommand())) {
			//to allow to import more files
			if(JOptionPane.showConfirmDialog(this, Localization.l("import_more_files_are_you_sure"), Localization.l("import_more_files"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				SendObject currentSendObject = PAS.get_pas().get_sendcontroller().get_activesending();
				currentSendObject.set_import_more_flag(true);
				new ImportPolygon(currentSendObject.get_toolbar(), "act_polygon_imported", false, PAS.get_pas());			
			}
			this.setVisible(false);
			enableControls(true);
		}
		else if( "act_import_streetAddress".equals(e.getActionCommand())||
                "act_import_propertyAddress".equals(e.getActionCommand())){
        	m_panel.get_previewlist().actionPerformed(e);//pass import type change event to previewlist
//        	if(is_valid_toPerform_action())   {
//                m_panel.actionPerformed(e);
//                enableControls(false);
//            }
//            else
//                JOptionPane.showMessageDialog(PopupDialog.get_frame(), Localization.l("importpreview_please_specify"),
//                        Localization.l("common_warning"), JOptionPane.WARNING_MESSAGE);
        }
        
	}

    private boolean is_valid_toPerform_action() {
        if("Street".equals(get_previewpanel().getM_import_type()))
        {
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID) != -1 
//                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO) != -1 &&
//                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER) != -1
                    //as per new specification only municipal id and street id are mandatory
            		) {
            	if((get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER) != -1 || 
            			get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_APARTMENTID) !=-1) && 
            			get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO) == -1)
            	{
            		importErrorMessage = Localization.l("importpreview_please_specify_apartment");
            		return false;
            	}
            	else
            		return true;
            }
            else
               return false;
        }
        else if("StreetApartment".equals(get_previewpanel().getM_import_type()))
        {
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID) != -1 
                    //get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO) != -1 &&
                    //get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_APARTMENTID) != -1
                    //as per new specification only municipal id and street id are mandatory
            		)
                    {
                return true;
            }
            else
                return false;
        }
        else if("Property".equals(get_previewpanel().getM_import_type())){
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_GNR) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_BNR) != -1 ) {
                return true;
            }
            else
                return false;

        }
        return false;
    }

    protected void enableControls(final boolean b)
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				m_options.m_btn_fetch.setEnabled(b);
				m_options.m_check_firstline_columnnames.setEnabled(b);
				m_panel.setEnabled(b);		
			}
		});
	}
}