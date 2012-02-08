package no.ums.pas.plugins.centric;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.localization.Localization;
import no.ums.pas.plugins.centric.status.CentricStatus;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

public class CentricEastContent extends EastContent
{

    private static final Log log = UmsLog.getLogger(CentricEastContent.class);

	public static final int PANEL_CENTRICSTATUS_ = 9;
	public static final int PANEL_CENTRICSEND_ = 10;
	
	private static int currentPanel = PANEL_CENTRICSEND_;
	
	private CentricStatus m_centricstatus = null;
	private CentricSendOptionToolbar m_centricsend = null;

    public static int getCurrentPanel() {
        return currentPanel;
    }

    public static void setCurrentPanel(int currentPanel) {
        CentricEastContent.currentPanel = currentPanel;
    }

    public void set_centricstatus(CentricStatus status) { m_centricstatus = status; }
	public CentricStatus get_centricstatus() { return m_centricstatus; }
		
	public void set_centricsend(CentricSendOptionToolbar send) { m_centricsend = send; }
	public CentricSendOptionToolbar get_centricsend() { return m_centricsend; }
	
	public CentricEastContent()
	{
		super(PAS.get_pas());
	}
	
	@Override
	protected void tabChanged()
	{
		if(m_tabbedpane.getSelectedComponent() instanceof CentricSendOptionToolbar)
		{
			setCurrentPanel(PANEL_CENTRICSEND_);
		}
		else if(m_tabbedpane.getSelectedComponent() instanceof CentricStatus)
		{
			setCurrentPanel(PANEL_CENTRICSTATUS_);
		}
		else {
			//check the rest
			super.tabChanged();
		}
		PAS.pasplugin.onEastContentTabClicked(CentricEastContent.this, m_tabbedpane);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		super.componentResized(e);
		if(m_centricstatus != null)
			m_centricstatus.componentResized(e);
	}
	@Override
	public Component get_tab(int ID) {
		Component tab = null;
		switch(ID) {
		case PANEL_CENTRICSTATUS_:
			tab = m_centricstatus;
			break;
		case PANEL_CENTRICSEND_:
			tab = m_centricsend;
			break;
		}
		if(tab==null)
			tab = super.get_tab(ID);
		return tab;
	}
	
	public void remove_tab(int ID) {
		switch(ID) {
			case PANEL_CENTRICSTATUS_:
				get_tabbedpane().remove(m_centricstatus);
				break;
			case PANEL_CENTRICSEND_:
				get_tabbedpane().remove(m_centricsend);
				break;
		}
	}
	
	@Override
	public synchronized void ensure_added(int n_leaf) {
		try
		{
			boolean not_found=true;
			switch(n_leaf) {
				case PANEL_CENTRICSTATUS_:
					if(find_component(m_centricstatus)==-1) {
                        get_tabbedpane().addTab(Localization.l("main_statustab_title"), null, m_centricstatus, Localization.l("main_statustab_title_tooltip"));
                    }
					not_found=false;
					break;
			}
			if(not_found)
				super.ensure_added(n_leaf);
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
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
				case PANEL_CENTRICSEND_:
					if(get_centricstatus()!=null)
						get_tabbedpane().setSelectedComponent(get_centricsend());
					not_found = false;
					break;
			}
			if(not_found)
				super.flip_to(n_leaf);
		} catch(Exception e) {
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("EastContent","Exception in flip_to",e,1);
		}
	}
	
}