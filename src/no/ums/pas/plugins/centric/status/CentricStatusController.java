package no.ums.pas.plugins.centric.status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import javax.swing.SwingUtilities;

import no.ums.pas.PAS;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.plugins.centric.CentricAddressSearch;
import no.ums.pas.plugins.centric.CentricEastContent;
import no.ums.pas.plugins.centric.CentricSendOptionToolbar;
import no.ums.pas.plugins.centric.ws.WSCentricStatus;
import no.ums.ws.parm.CBSENDINGRESPONSE;

public class CentricStatusController extends StatusController { 
	
	private CentricStatus m_centricstatus;
	private long m_projectpk;
	private boolean ready = true;
	public boolean isReady() { return ready; }
	
	public CentricStatusController(ActionEvent sendfinished, CentricSendOptionToolbar centricsend) {
		
		set_cbsendingresponse((CBSENDINGRESPONSE)sendfinished.getSource());
		m_projectpk = ((CBSENDINGRESPONSE)sendfinished.getSource()).getLProjectpk();	
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		// update status ting med CBSendingresponse?
		
		int delay = 5000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(m_centricstatus.isReady()) {
					CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
					res.setLProjectpk(m_projectpk);
					
					m_centricstatus.set_cbsendingresponse(res);
				}
			}
		};
		new Timer(delay, taskPerformer).start();
	}
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res) {
		if(m_centricstatus == null)
			m_centricstatus = new CentricStatus(res);
		else
			m_centricstatus.set_cbsendingresponse(res);
	}
}
