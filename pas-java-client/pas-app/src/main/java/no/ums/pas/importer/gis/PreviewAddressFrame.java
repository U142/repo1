package no.ums.pas.importer.gis;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import no.ums.pas.PAS;
import no.ums.pas.area.FilterController;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.ImportAddressFile;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.PopupDialog;
import no.ums.pas.ums.tools.Utils;


 
public class PreviewAddressFrame extends JDialog implements ComponentListener, ActionListener {
	public static final long serialVersionUID = 1;
    PreviewAddressPanel m_panel;
    PreviewAddressFinish m_finish_panel;
    PreviewAddressFrame m_parent;
	private JButton m_btn_finish;
	PreviewAddressOptions m_options = null;
	public PreviewAddressPanel get_previewpanel() { return m_panel; }
	private StatisticsAddressPanel m_statisticspanel;
    public StatisticsAddressPanel get_statisticspanel() { return m_statisticspanel; }
	//
	
	public String encoding = Localization.l("importpreview_encoding_iso_8859_15");
	//try start
	public  String address= Localization.l("import_addr_street");
	//try end
	public GISFile m_gis;
	private String importErrorMessage;
	private String addressTypeSelected="";
	
	public void setFinishBtnEnable(boolean enabled) {
		finishEnabled = enabled;
		m_btn_finish.setEnabled(finishEnabled);
		m_finish_panel.m_btn_finish.setEnabled(finishEnabled);
	}

	private boolean finishEnabled = false;

	public boolean isFinishEnabled() {
		return finishEnabled;
	}

	public void setFinishEnabled(boolean finishEnabled) {
		this.finishEnabled = finishEnabled;
	}

    public String getAddressTypeSelected() {
		return addressTypeSelected;
	}

	public void setAddressTypeSelected(String addressTypeSelected) {
		this.addressTypeSelected = addressTypeSelected;
	}

	public PreviewAddressFrame(GISFile gis) {
		super(PAS.get_pas(), Localization.l("common_import"), true);
		try {
			setAlwaysOnTop(true);
		} catch(Exception e) {
			
		}
		init_common();

		m_gis = gis;
		m_options = new PreviewAddressOptions(this, gis.getIsAlert());
		m_panel = new PreviewAddressPanel(gis, this);
		m_panel.setM_parent(this);
		m_btn_finish = new JButton(Localization.l("common_finish"));
		m_finish_panel=new PreviewAddressFinish(this,gis.getIsAlert());
		//setSize(620, 500);
		int x = 1000;
		int y = 800;
		setBounds(Utils.screendlg_upperleft(x, y).width, Utils.screendlg_upperleft(x, y).height, x, y);
		setLayout(new BorderLayout());
		//m_panel.setBorder(new LineBorder(Color.GREEN));
		getContentPane().add(new JButton("north"),BorderLayout.NORTH);
		getContentPane().add(m_panel, BorderLayout.CENTER);
		getContentPane().add(m_finish_panel, BorderLayout.SOUTH);
		m_btn_finish.setEnabled(false);
		m_btn_finish.setActionCommand("act_finish");
		m_btn_finish.addActionListener(this);
		actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_options_view"));
		setVisible(true);
		addComponentListener(this);
		resize();
	}
	
	// This is used for generate sending, skips the preview and only shows progressbar
	public PreviewAddressFrame(GISList list, SendObject so) {
		try {
			init_common();
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
			
			boolean isPropertyImport = false;
			Iterator it = list.iterator();
			while(it.hasNext()) {
				gisr = (GISRecord)it.next();

				if(gisr instanceof GISRecordProperty)
				{
					GISRecordProperty propertyRecord = (GISRecordProperty)gisr;
					writer.print(propertyRecord.get_municipal() + "\t" + propertyRecord.getM_sz_gnr() +
							"\t" + propertyRecord.getM_sz_bnr() + "\t" + propertyRecord.getM_sz_fnr() +
							"\t" + propertyRecord.getM_sz_snr() +
							"\t" + gisr.get_name1() + "\t" + gisr.get_name2() + "\r\n");
					if(!isPropertyImport)
						isPropertyImport=true;
				}
				else
				{
					writer.print(gisr.get_municipal() + "\t" + gisr.get_streetid() +
						"\t" + gisr.get_houseno() + "\t" + gisr.get_letter() + 
						"\t" + gisr.get_name1() + "\t" + gisr.get_name2() + "\r\n");
				}
			}
			writer.flush();
			writer.close();

			if(isPropertyImport)
				pp.setM_import_type("Property");

			pp.actionPerformed(new ActionEvent(gistemp,ActionEvent.ACTION_PERFORMED,"act_autofetch_addresses"));
			addComponentListener(this);
			resize();

			
		} catch(IOException ioe) {
			Error.getError().addError("IO Exception", "Error opening file in gis directory", ioe, Error.SEVERITY_ERROR);
		}
	}
	public PreviewAddressFrame(SendObject so) {
		super(PAS.get_pas(), Localization.l("importpreview_file_content_tooltip"), true);
		
		init_common();
		try
		{
			get_statisticspanel().n_filelines = so.get_sendproperties().typecast_gis().get_gislist().size();
		}
		catch(Exception e)
		{
			
		}
		m_panel = new PreviewAddressPanel(so, true, this, encoding);
		int x = 1000;
		int y = 800;
		setBounds(Utils.screendlg_upperleft(x, y).width, Utils.screendlg_upperleft(x, y).height, x, y);
		
		setLayout(new BorderLayout());
//		m_panel.setBorder(new LineBorder(Color.BLUE));
        getContentPane().add(m_panel, BorderLayout.CENTER);
        actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));
		setVisible(true);
		addComponentListener(this);
		resize();

	}
	public void init_common() {
		m_statisticspanel = new StatisticsAddressPanel();		
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
            	m_panel.setAddressType(getAddressTypeSelected());
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
	                
	                if("import_addr_street".equals(getAddressTypeSelected()))
	                   {
	                 JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                            Localization.l("filter.import.addr.street"), Localization.l("common_warning"),
	                            JOptionPane.WARNING_MESSAGE);
	                   }
	                   else if("import_addr_CUNorway".equals(getAddressTypeSelected())){
	                    JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                            Localization.l("filter.import.CUNorway"), Localization.l("common_warning"),
	                            JOptionPane.WARNING_MESSAGE);
	                   }
	                   else if("import_addr_CUSweden".equals(getAddressTypeSelected())){
	                    JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                            Localization.l("filter.import.CUSweden"), Localization.l("common_warning"),
	                            JOptionPane.WARNING_MESSAGE);
	                   }
	                   else if("import_addr_VABanken".equals(getAddressTypeSelected())){
	                    JOptionPane.showMessageDialog(PopupDialog.get_frame(),
	                            Localization.l("filter.import.VABanken"), Localization.l("common_warning"),
	                            JOptionPane.WARNING_MESSAGE);
	                   }
	                
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
              }
			   if(m_statisticspanel!=null)
			    m_statisticspanel.actionPerformed(new ActionEvent(PAS.get_pas().getPredefinedFilterController().getFilterCtrl().getCurrentFilter().getGisFilterList(), ActionEvent.ACTION_PERFORMED, "act_gis_imported"));
//			   m_statisticspanel.setBorder(new LineBorder(Color.RED));
			    getContentPane().add(m_statisticspanel, BorderLayout.NORTH);      
			   this.doLayout();
			   resize();
			   repaint();

			  }
		else if("act_set_options_view".equals(e.getActionCommand())) {
			
			if(m_statisticspanel!=null)
				getContentPane().remove(m_statisticspanel);
			if(m_options!=null)
			{
				//m_options.setBorder(new LineBorder(Color.BLUE));
				//m_options.setPreferredSize(new Dimension(300, 400));
				getContentPane().add(m_options, BorderLayout.NORTH);
			}
			this.doLayout();
			resize();
			repaint();
		}
		else if("act_update_statistics".equals(e.getActionCommand())) {
                m_btn_finish.setEnabled(true);
				get_statisticspanel().actionPerformed(e);
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
				boolean isShapeFromFilter = false;
				try
				{
					FilterController filterController = PAS.get_pas().getPredefinedFilterController().getFilterCtrl();
                   if(filterController.isLock())
					{
						filterController.setImportMore(true);
						new ImportAddressFile(filterController, "act_address_file_imported", false, PAS.get_pas());
						isShapeFromFilter = true;
					}
				}
				catch (Exception err) {
					err.printStackTrace();
				}

				if(!isShapeFromFilter) {
					SendObject currentSendObject = PAS.get_pas().get_sendcontroller().get_activesending();
					currentSendObject.set_import_more_flag(true);
					new ImportAddressFile(currentSendObject.get_toolbar(), "act_address_file_imported", false, PAS.get_pas());
				}
			}
			this.setVisible(false);
			enableControls(true);
			PAS.get_pas().getPredefinedFilterController().getFilterCtrl().getGui().enableSave(true);
		}
		else if( "act_import_streetAddress".equals(e.getActionCommand())||
                "act_import_propertyAddress".equals(e.getActionCommand())){
        	m_panel.get_previewlist().actionPerformed(e);//pass import type change event to previewlist
             }
		else if( "act_import_CUN".equals(e.getActionCommand())){
			m_panel.get_previewlist().actionPerformed(e);
		}
		else if( "act_import_CUS".equals(e.getActionCommand())){
			m_panel.get_previewlist().actionPerformed(e);
		}
		else if( "act_import_VBS".equals(e.getActionCommand())){
			m_panel.get_previewlist().actionPerformed(e);
		}
        
	}

    private boolean is_valid_toPerform_action() {
   
    	if("import_addr_street".equals(getAddressTypeSelected()))
        {
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_STREETID) != -1 
                  ) {
            	if((get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_LETTER) != -1 || 
            			get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_APARTMENTID) !=-1) && 
            			get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_HOUSENO) == -1)
            	{
            		importErrorMessage = Localization.l("import_Street_details");
            		return false;
            	}
            	else
            		return true;
            }
            else
               return false;
        }
        else if("import_addr_CUNorway".equals(getAddressTypeSelected())){
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_GNR) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_BNR) != -1 ) {
                return true;
            }
            else
                return false;

        }
        else if("import_addr_CUSweden".equals(getAddressTypeSelected())){
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_Fastighetsnyckel) != -1 ) {
                return true;
            }
            else
                return false;

        }
        else if("import_addr_VABanken".equals(getAddressTypeSelected())){
            if(get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_MUNICIPALID) != -1 &&
                    get_previewpanel().get_previewlist().get_column_bytype(PreviewList.ComboField.FIELDID_VB) != -1 ) {
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
				m_options.m_btn_import.setEnabled(b);
				m_options.m_check_firstline_columnnames.setEnabled(b);
				m_panel.setEnabled(b);
                m_finish_panel.m_btn_import.setEnabled(b);
            }
		});
	}

	public PreviewAddressFrame getM_parent() {
		return m_parent;
	}

	public void setM_parent(PreviewAddressFrame m_parent) {
		this.m_parent = m_parent;
	}
}