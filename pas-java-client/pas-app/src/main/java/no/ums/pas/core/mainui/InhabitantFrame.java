package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;



public class InhabitantFrame extends JPanel implements ComponentListener { //JFrame
	public static final long serialVersionUID = 1;

	private PAS m_pas;
	public Dimension get_dim() { return m_dim; }
	private Dimension m_dim;
	private PAS get_pas() { return m_pas; }
	public InhabitantResults m_inhabitantpanel;
	public InhabitantResults get_panel() { return m_inhabitantpanel; }
	private JLabel m_label;
	public JLabel get_label() { return m_label; }
	
	public InhabitantFrame(PAS pas)
	{
		super();
		m_pas = pas;
		m_dim = new Dimension(get_pas().get_eastwidth()-10, 200);
		//setBounds(0,0,m_dim.width, m_dim.height);
		setSize(m_dim.width, m_dim.height);

		String sz_columns[] = { Localization.l("common_item"), 
								Localization.l("mainmenu_status"), 
								Localization.l("common_name"), 
								Localization.l("common_number"), 
								Localization.l("common_adr_address"), 
								Localization.l("common_adr_postno"), 
								Localization.l("common_adr_postplace"), 
								Localization.l("common_time") 
							};
		boolean b_editable[] = { false, false, false, false, false, false, false, false };
		int n_width[] = { 1, 70, 130, 70, 70, 30, 30, 30 };
		m_inhabitantpanel = new InhabitantResults(get_pas(), this, sz_columns, n_width, b_editable, m_dim);
		m_label = new JLabel("Inhabitants");
		m_inhabitantpanel.sort(0);
		m_inhabitantpanel.m_tbl.setRowHeight(24);
		addComponentListener(this);
		init();
	}
	public void clear()
	{
		get_panel().clear();
	}
	
	public void fill()
	{
		//setBounds(new Rectangle(get_pas().get_mappane().getLocationOnScreen().x + get_pas().get_mappane().get_dimension().width, get_pas().get_mappane().getLocationOnScreen().y, m_dim.width, m_dim.height));
		set_visible(true);
		//get_pas().add_event("Searching for inhabitants...");
		get_panel().start_search();
	}
	public void set_visible(boolean b)
	{
		setVisible(b);
		get_panel().setVisible(b);
		get_label().setVisible(b);
	}		
	
	public void init()
	{
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
		if(getWidth()<=0 || getHeight()<=0)
		{
			return;
		}
		m_inhabitantpanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_inhabitantpanel.setSize(new Dimension(getWidth(), getHeight()));
		
	}
	public void componentShown(ComponentEvent e) { }
}	