package no.ums.pas.core.mainui.address_search;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class AddressSearchPanel extends DefaultPanel
{

    private static final Log log = UmsLog.getLogger(AddressSearchPanel.class);

	public AddressSearchPanel()
	{
		super();
	}
	SearchPanelVals m_searchvals;
	SearchPanelResultsAddrSearch m_searchresults;

	@Override
	public void actionPerformed(ActionEvent e) {			
	}

	@Override
	public void add_controls() {			
		setLayout(new BorderLayout());
		add(m_searchvals, BorderLayout.NORTH);
		add(m_searchresults, BorderLayout.CENTER);
	}

	@Override
	public void init() {
		try
		{
			m_searchvals = PAS.pasplugin.getAddressSearch().onCreateSearchPanelVals(this);
			m_searchresults = PAS.pasplugin.getAddressSearch().onCreateSearchPanelResultsAddrSearch(this, PAS.get_pas().get_pasactionlistener());
			add_controls();
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}		
	}
	public void event_search()
	{
		m_searchresults.start_search();
	}
	public SearchPanelVals get_searchpanelvals() { return m_searchvals; }
	public SearchPanelResultsAddrSearch get_searchpanelresults() { return m_searchresults; }

	@Override
	public int getWantedHeight() {
		return 300;
	}		
	
}
