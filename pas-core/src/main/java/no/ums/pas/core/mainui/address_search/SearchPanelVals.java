package no.ums.pas.core.mainui.address_search;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SearchPanelVals extends DefaultPanel implements KeyListener {
	public static final long serialVersionUID = 1;

	protected StdTextLabel	m_txt_address;
	protected StdTextLabel	m_txt_postno;
	protected StdTextLabel	m_txt_region;
	protected StdTextLabel	m_txt_country;
	
	protected StdTextArea	m_val_address;
	protected StdTextArea	m_val_number;
	protected StdTextArea	m_val_postno;
	protected StdTextArea	m_val_postarea;
	protected StdTextArea	m_val_region;
	protected JComboBox	m_val_country;
	protected JButton		m_btn_search;
	protected AddressSearchPanel	m_searchpanel;
	
	protected String[] m_sz_country = { "Norway", "Sweden", "Denmark" };
		
	public SearchPanelVals(AddressSearchPanel frm)
	{
		super();
		m_searchpanel   = frm;
		m_gridconst.insets = new Insets(2,2,2,2);
		
		//setForeground(Color.black);

		prepare_controls();
		add_controls();
		init();
		setVisible(true);
		this.addKeyListener(this);
		m_val_address.addKeyListener(this);
		m_val_number.addKeyListener(this);
		m_val_postno.addKeyListener(this);
		m_val_postarea.addKeyListener(this);
		m_val_region.addKeyListener(this);
		m_val_country.addKeyListener(this);
		enableSearchButton(true);
	}
	public String get_address() { return m_val_address.getText(); }
	public String get_number() { return m_val_number.getText(); }
	public String get_postno() { return m_val_postno.getText(); }
	public String get_postarea() { return m_val_postarea.getText(); }
	public String get_region() { return m_val_region.getText(); }
	public String get_country() { return m_val_country.getSelectedItem().toString(); }
	
	public void actionPerformed(ActionEvent e) {
		if ("act_search".equals(e.getActionCommand()))
		{
			search_started();
			m_searchpanel.event_search();	
		}
	}
	public void set_focus()
	{
		m_val_address.grabFocus();
		m_val_address.selectAll();
	}
	public void search_started()
	{
		enableSearchButton(false);
	}
	public void search_stopped()
	{
		enableSearchButton(true);
	}
	protected boolean canSearch()
	{
		return m_btn_search.isEnabled();
	}
	
	protected void enableSearchButton(boolean b)
	{
		//check if criterias are met
		if(get_address().length()<=0 &&
			get_postno().length()<=0 &&
			get_postarea().length()<=0 &&
			get_region().length()<=0)
			b	= false;
		
		m_btn_search.setEnabled(b);
	}
	
	void prepare_controls()
	{
		m_txt_address		= new StdTextLabel(PAS.l("adrsearch_dlg_address_and_house"));
		m_txt_postno		= new StdTextLabel(PAS.l("adrsearch_dlg_postcode_place"));
		m_txt_region		= new StdTextLabel(PAS.l("adrsearch_dlg_region"));
		m_txt_country		= new StdTextLabel(PAS.l("adrsearch_dlg_country"));
		m_txt_address.setFocusable(false);
		m_txt_postno.setFocusable(false);
		m_txt_region.setFocusable(false);
		m_txt_country.setFocusable(false);
		
		m_val_address		= new StdTextArea("", false);
		m_val_number		= new StdTextArea("", false);
		m_val_postno		= new StdTextArea("", false);
		m_val_postarea		= new StdTextArea("", false);
		m_val_region		= new StdTextArea("", false);
		m_val_country		= new JComboBox(m_sz_country);

		
        m_txt_address.setPreferredSize(new Dimension(150, 15));
        m_txt_postno.setPreferredSize(new Dimension(150, 15));
        m_txt_region.setPreferredSize(new Dimension(150, 15));
        m_txt_country.setPreferredSize(new Dimension(100, 15));

        m_val_number.setPreferredSize(new Dimension(50, 15));
        m_val_postno.setPreferredSize(new Dimension(50, 15));
        /*m_val_address.setPreferredSize(new Dimension(100, 15));
        m_val_number.setPreferredSize(new Dimension(30, 15));
        m_val_postno.setPreferredSize(new Dimension(50, 15));
        m_val_postarea.setPreferredSize(new Dimension(80, 15));
        m_val_region.setPreferredSize(new Dimension(134, 15));
        m_val_country.setPreferredSize(new Dimension(134, 15));*/

        m_btn_search = new JButton(PAS.l("common_search"));
        m_btn_search.setVerticalTextPosition(AbstractButton.CENTER);
        m_btn_search.setHorizontalTextPosition(AbstractButton.LEFT);
        m_btn_search.setMnemonic('s');
        m_btn_search.setActionCommand(ENABLE);
        m_btn_search.setPreferredSize(new Dimension(100, 20));		        
		
	}
	public void add_controls()
	{
		set_gridconst(0,0,1,1, GridBagConstraints.WEST); //x,y,sizex,sizey
		add(m_txt_address, m_gridconst);
		set_gridconst(1,0,2,1, GridBagConstraints.WEST);
		add(m_val_address, m_gridconst);
		set_gridconst(3,0,1,1, GridBagConstraints.WEST);
		add(m_val_number, m_gridconst);
		
		set_gridconst(0,1,1,1, GridBagConstraints.WEST);
		add(m_txt_postno, m_gridconst);
		set_gridconst(1,1,1,1, GridBagConstraints.WEST);
		add(m_val_postno, m_gridconst);
		set_gridconst(2,1,2,1, GridBagConstraints.WEST);
		add(m_val_postarea, m_gridconst);
		
		set_gridconst(0,2,1,1, GridBagConstraints.WEST);
		add(m_txt_region, m_gridconst);
		set_gridconst(1,2,3,1, GridBagConstraints.WEST);
		add(m_val_region, m_gridconst);
		
		set_gridconst(0,3,1,1, GridBagConstraints.WEST);
		add(m_txt_country, m_gridconst);
		set_gridconst(1,3,3,1, GridBagConstraints.WEST);
		add(m_val_country, m_gridconst);		
			
		set_gridconst(0,4,4,1, GridBagConstraints.WEST);
		add(m_btn_search, m_gridconst);
	}
	
	public void init()
	{
		m_btn_search.setActionCommand("act_search");
		m_btn_search.addActionListener(this);
	}
	void set_default_cc() {
		if(PAS.get_pas().get_userinfo().get_default_dept()!=null) {
			int n_stdcc = new Integer(PAS.get_pas().get_userinfo().get_default_dept().get_stdcc()).intValue();
			switch(n_stdcc) {
				case 45:
					m_val_country.setSelectedIndex(2);
					break;
				case 46:
					m_val_country.setSelectedIndex(1);
					break;
				case 47:
					m_val_country.setSelectedIndex(0);
					break;
				default:
					m_val_country.setSelectedIndex(0);
					break;
			}
		}
	}
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				if(canSearch())
					actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_search"));
				break;
		}
		enableSearchButton(true);
	}
	public void keyReleased(KeyEvent e) {
		enableSearchButton(true);
		
	}
	public void keyTyped(KeyEvent e) { 

	}	
}