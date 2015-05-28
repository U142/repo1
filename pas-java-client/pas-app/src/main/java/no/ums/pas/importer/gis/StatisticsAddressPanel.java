package no.ums.pas.importer.gis;

import java.awt.event.ActionEvent;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.ws.GISFilterList;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.addressfilters.AddressFilterInfo;

public class StatisticsAddressPanel  extends DefaultPanel{
	
	private static final int fontsize = 12;
	private StdTextLabel m_lbl_filelines = new StdTextLabel(Localization.l("importresults_stat_lines_imported"),  250, fontsize, true);
	private StdTextLabel m_lbl_filelines1 = new StdTextLabel(Localization.l("importresults_stat_lines_summary"),  250, fontsize, true);
	private StdTextLabel m_output_data  = new StdTextLabel("", 150, fontsize, false);
	
	PreviewAddressPanel m_panel;
    int n_filelines = 0;
    public StatisticsAddressPanel()
	{
		super();
		add_controls();
	}
    @Override
	public void add_controls() {
        set_gridconst(0, 0, 1, 1);
		add(m_lbl_filelines1, get_gridconst());
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_filelines, get_gridconst());
		set_gridconst(1, get_panel(), 1, 1);
		add(m_output_data, get_gridconst());
       }
	
	@Override
	public void init() {
		
		setVisible(true);
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if("act_gis_imported".equals(e.getActionCommand())) {
			GISFilterList m_gisfilterlist = (GISFilterList) e.getSource();
			calCount(m_gisfilterlist);
	   }
    }
	
	protected void calCount(GISFilterList m_gisfilterlist){
		  resetStats();
		  n_filelines = m_gisfilterlist.size();
		  updateStats();
		 }
	
	private void resetStats()
	{
		n_filelines=0;
	}
	
	protected void updateStats()
	{
		m_output_data.setText(Integer.toString(n_filelines));	
	}
}
