package no.ums.pas.core.mainui.address_search;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.errorhandling.Error;




public class SearchFrame extends JFrame implements ComponentListener {
	public static final long serialVersionUID = 1;

	protected boolean m_b_isactivated = false;
	protected boolean isActivated() { return m_b_isactivated; }
	protected AddressSearchPanel m_panel = new AddressSearchPanel();
	public AddressSearchPanel get_panel() { return m_panel; }
	
	
	public SearchFrame()
	{
		super(PAS.l("adrsearch_dlg_title"));
		m_panel.init();
		this.setIconImage(PAS.get_pas().getIconImage());
		initialize();
		/*try
		{
			m_searchvals = PAS.pasplugin.ADDRESS_SEARCH.onCreateSearchPanelVals(this);
			m_searchresults = PAS.pasplugin.ADDRESS_SEARCH.onCreateSearchPanelResultsAddrSearch(this, PAS.get_pas().get_pasactionlistener());
		}
		catch(Exception e)
		{
			
		}		
		initialize();*/
	}
	//PAS get_pas() { return m_pas; }
	
	
	
	void initialize()
	{
		getContentPane().add(m_panel, BorderLayout.CENTER);
		this.addComponentListener(this);

	}
	@Override
	public void componentHidden(ComponentEvent e) {		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	@Override
	public void componentResized(ComponentEvent e) {
		/*if(getWidth()<=0 || getHeight()<=0)
			return;
		get_panel().setPreferredSize(new Dimension(getWidth(), getHeight()));
		get_panel().get_searchpanelresults().setPreferredSize(new Dimension(getWidth()-20, getHeight()/2));*/
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		
	}



	public void activate()
	{
		
		if(isActivated())
		{
			setVisible(true);
			return;
		}
		get_panel().get_searchpanelvals().set_focus();
		int n_width = 460;
		int n_height = 300;
		setBounds(new Rectangle(PAS.get_pas().get_mappane().getLocationOnScreen().x, PAS.get_pas().get_mappane().getLocationOnScreen().y, n_width, n_height));
		try {
			setAlwaysOnTop(true);
			setVisible(true);
			m_b_isactivated = true;
		}
		catch(SecurityException e) {
			PAS.get_pas().add_event("setAlwaysOnTop() : Security exception", e);
			Error.getError().addError(PAS.l("common_error"),"SearchFrame Exception in activate",e,1);
		}

	}
	public void set_default_cc() {
		get_panel().get_searchpanelvals().set_default_cc();
	}
	public void initUI() {
		try {
			//UIManager.setLookAndFeel(PAS.get_pas().get_lookandfeel());
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"),"SearchFrame Exception in initUI",e,1);
		}
		SwingUtilities.updateComponentTreeUI(this);
	}

}