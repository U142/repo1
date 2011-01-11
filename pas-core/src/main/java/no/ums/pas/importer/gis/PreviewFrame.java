package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Utils;

import javax.swing.*;
import java.awt.*;
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
    private static final Log logger = UmsLog.getLogger(PreviewFrame.class);
    PreviewPanel m_panel;
	private JButton m_btn_finish;
	PreviewOptions m_options = null;
	public PreviewPanel get_previewpanel() { return m_panel; }
	private StatisticsPanel m_statisticspanel;
	public StatisticsPanel get_statisticspanel() { return m_statisticspanel; }
	
	
	public PreviewFrame(GISFile gis) {
		super(PAS.get_pas(), "Import preview", true);
		try {
			setAlwaysOnTop(true);
			//super.setIconImage(PAS.get_pas().getIconImage());
		} catch(Exception e) {
			
		}
		init_common();
		//get_statisticspanel().n_filelines = gis.
		//setModal(true);
		m_options = new PreviewOptions(this, gis.getIsAlert());
		m_panel = new PreviewPanel(gis, this);
		m_btn_finish = new JButton("Finish");
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
			PreviewPanel pp = new PreviewPanel(so, false, this);
			GISRecord gisr;
			File gistemp = new File(StorageController.StorageElements.get_path(StorageController.PATH_GISIMPORT_) + PAS.get_pas().get_parmcontroller().getHighestTemp() + ".txt");
			try
			{
				gistemp.delete();
			}
			catch(Exception e) {
                logger.warn("Failed to delete from gistemp", e);
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
			//actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_set_statistics_view"));

			pp.actionPerformed(new ActionEvent(gistemp,ActionEvent.ACTION_PERFORMED,"act_autofetch_addresses"));
			addComponentListener(this);
			resize();

//			GISFile gisfile = new GISFile();
//			gisfile.parse(gistemp, callback, action);
//			m_panel = new PreviewPanel(gisfile);
//			m_options = new PreviewOptions(this);
//			m_options.m_btn_fetch.doClick();
			
		} catch(IOException ioe) {
			Error.getError().addError("IO Exception", "Error opening file in gis directory", ioe, Error.SEVERITY_ERROR);
		}
	}
	public PreviewFrame(SendObject so) {
		super(PAS.get_pas(), "Results", true);
		
		init_common();
		try
		{
			get_statisticspanel().n_filelines = so.get_sendproperties().typecast_gis().get_gislist().size();
		}
		catch(Exception e)
		{
			
		}
		m_panel = new PreviewPanel(so, true, this);
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
		else if("act_fetch_addresses".equals(e.getActionCommand())) { //get event from PreviewOptions
			m_panel.actionPerformed(e);
			enableControls(false);
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
			this.setVisible(false);
			enableControls(true);
		}
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