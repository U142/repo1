package no.ums.pas.plugins.centric;

import java.awt.Component;

import no.ums.pas.PAS;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.ums.errorhandling.Error;

public class CentricEastContent extends EastContent
{
	public static final int PANEL_CENTRICSTATUS_ = 9;
	
	private CentricStatus m_centricstatus = null;
	
	public void set_centricstatus(CentricStatus status) { m_centricstatus = status; }
	public CentricStatus get_centricstatus() { return m_centricstatus; }
		
	public CentricEastContent()
	{
		super(PAS.get_pas());
	}
	
	@Override
	public Component get_tab(int ID) {
		Component tab = null;
		switch(ID) {
		case PANEL_CENTRICSTATUS_:
			tab = m_centricstatus;
			break;
		}
		if(tab==null)
			tab = super.get_tab(ID);
		return tab;
	}
	
	@Override
	public synchronized void ensure_added(int n_leaf) {
		try
		{
			boolean not_found=true;
			switch(n_leaf) {
				case PANEL_CENTRICSTATUS_:
					if(find_component(m_centricstatus)==-1)
						get_tabbedpane().addTab(PAS.l("main_statustab_title"), null, m_centricstatus, PAS.l("main_statustab_title_tooltip"));
					not_found=false;
					break;
			}
			if(not_found)
				super.ensure_added(n_leaf);
		}
		catch (Exception e) {
			
		}
	}
	
	@Override
	public void flip_to(int n_leaf) {
		ensure_added(n_leaf);
		boolean not_found=true;
		try {
			switch(n_leaf) {
				case PANEL_CENTRICSTATUS_:
					if(get_centricstatus()!=null)
						get_tabbedpane().setSelectedComponent(get_centricstatus());
					not_found = false;
					break;
			}
			if(not_found)
				flip_to(n_leaf);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("EastContent","Exception in flip_to",e,1);
		}
	}
	
}