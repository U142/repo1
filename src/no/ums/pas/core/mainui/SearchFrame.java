package no.ums.pas.core.mainui;

import java.awt.*;
import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.ums.errorhandling.Error;




public class SearchFrame extends JFrame {
	public static final long serialVersionUID = 1;

	SearchPanelVals m_searchvals;
	SearchPanelResultsAddrSearch m_searchresults;
	protected boolean m_b_isactivated = false;
	protected boolean isActivated() { return m_b_isactivated; }
	//PAS m_pas;
	
	public SearchFrame()
	{
		super(PAS.l("adrsearch_dlg_title"));
		this.setIconImage(PAS.get_pas().getIconImage());
		//m_searchvals = new SearchPanelVals(this);
		//m_searchresults = new SearchPanelResultsAddrSearch(this, sz_columns, n_width, new Dimension(800, 200), PAS.get_pas().get_pasactionlistener());
		try
		{
			m_searchvals = PAS.pasplugin.ADDRESS_SEARCH.onCreateSearchPanelVals(this);
			m_searchresults = PAS.pasplugin.ADDRESS_SEARCH.onCreateSearchPanelResultsAddrSearch(this, PAS.get_pas().get_pasactionlistener());
		}
		catch(Exception e)
		{
			
		}		
		initialize();
	}
	//PAS get_pas() { return m_pas; }
	public SearchPanelVals get_searchpanelvals() { return m_searchvals; }
	
	void initialize()
	{
		getContentPane().add(m_searchvals, BorderLayout.NORTH);
		getContentPane().add(m_searchresults, BorderLayout.CENTER);

	}
	void event_search()
	{
		m_searchresults.start_search();
	}
	public void activate()
	{
		
		if(isActivated())
		{
			setVisible(true);
			return;
		}
		get_searchpanelvals().set_focus();
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
		m_searchvals.set_default_cc();
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