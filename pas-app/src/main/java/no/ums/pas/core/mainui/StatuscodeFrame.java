package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.status.StatusSending;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class StatuscodeFrame extends JPanel implements ComponentListener{ //JFrame
	public static final long serialVersionUID = 1;

	//private StatusController m_statuscontroller;
	public OpenStatuscodes m_statuspanel;

	
	public StatusController get_controller() { return PAS.get_pas().get_statuscontroller(); } //m_statuscontroller; }
	public OpenStatuscodes get_panel() { return m_statuspanel; }
	public Dimension get_dim() { return m_dim; }
	private Dimension m_dim;
	private JLabel m_label;
	public JLabel get_label() { return m_label; }
	protected StatusSending m_filter = null;
	
	public StatuscodeFrame()
	{
		super();
		//m_statuscontroller = controller;
		m_dim = new Dimension(PAS.get_pas().get_eastwidth()-30, 300);
		//setBounds(0,0,m_dim.width, m_dim.height);
		String sz_columns[] = { PAS.l("main_status_code"), PAS.l("mainmenu_status"), PAS.l("common_items"), PAS.l("common_show"), PAS.l("common_animate"), PAS.l("common_color") };
		boolean b_editable[] = { false, false, false, true, true, false };
		int n_width[] = { 20, 150, 30, 30, 30, 50 };
		m_statuspanel = new OpenStatuscodes(PAS.get_pas(), this, sz_columns, n_width, b_editable, m_dim);
		m_label = new JLabel(PAS.l("main_status_statuscodes"));
		addComponentListener(this);
		
		init();
	}
	public void clear()
	{
		get_panel().clear();
		get_panel().m_tbl.removeAll();
	}
	
	/*filter statuscodes by sending*/
	public void setFilter(StatusSending s)
	{
		m_filter = s;
		fill();
	}
	
	public void fill()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				//setBounds(new Rectangle(PAS.get_pas().get_mappane().getLocationOnScreen().x + PAS.get_pas().get_mappane().get_dimension().width, PAS.get_pas().get_mappane().getLocationOnScreen().y, m_dim.width, m_dim.height));
				set_visible(true);
				//get_controller().get_pas().add_event("Filling statuscodes");
				try
				{
					get_panel().start_search(m_filter);
				}
				catch(Exception e)
				{
					
				}
			}
		});
	}
	
	public void init()
	{
	}
	public void set_visible(boolean b)
	{
		setVisible(b);
		get_panel().setVisible(b);
		get_label().setVisible(b);
	}	
	
	public void open()
	{
	}
	public void onDownloadFinishedStatusList()
	{
		
	}
	public synchronized void onDownloadFinishedStatusItems()
	{
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { 
		//setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_statuspanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_statuspanel.setSize(new Dimension(getWidth(), getHeight()));
		
	}
	public void componentShown(ComponentEvent e) { }
}	
	