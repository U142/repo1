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
	public void setProjectpk(long l_projectpk) { m_projectpk = l_projectpk; }
	private boolean ready = true;
	public boolean isReady() { return ready; }
	private Timer m_timer = null;
	
	
	
	public CentricStatusController()
	{
		super();
	}
	
	public boolean StopUpdates()
	{
		setProjectpk(0);
		if(m_timer!=null)
		{
			m_timer.stop();
			System.out.println("Status updates stopped");
		}
		return true;
	}
	
	public boolean OpenStatus(CBSENDINGRESPONSE sendresponse, CentricSendOptionToolbar centricsend) {
		set_cbsendingresponse(sendresponse);
		m_projectpk = sendresponse.getLProjectpk();	
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		// update status ting med CBSendingresponse?
		runTimer();
		return true;
	}
	
	public boolean OpenStatus(long l_projectpk, CentricSendOptionToolbar centricsend) {
		CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
		res.setLProjectpk(l_projectpk);
		set_cbsendingresponse(res);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		
		m_projectpk = l_projectpk;
		runTimer();
		return true;
	}
	
	
	private CentricStatusController(ActionEvent sendfinished, CentricSendOptionToolbar centricsend) {
		super();
		set_cbsendingresponse((CBSENDINGRESPONSE)sendfinished.getSource());
		m_projectpk = ((CBSENDINGRESPONSE)sendfinished.getSource()).getLProjectpk();	
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		// update status ting med CBSendingresponse?
		runTimer();

	}
	
	private CentricStatusController(long projectpk, CentricSendOptionToolbar centricsend) {
		super();
		CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
		res.setLProjectpk(projectpk);
		set_cbsendingresponse(res);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		
		m_projectpk = projectpk;
		runTimer();
	}
	
	private void runTimer() {
		int delay = 5000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(m_centricstatus.isReady()) {
					CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
					res.setLProjectpk(m_projectpk);
					
					m_centricstatus.set_cbsendingresponse(res);
					System.out.println("CentricStatusControl executed...");
				}
				else
				{
					System.out.println("CentricStatusControl busy...");
				}
			}
		};
		m_timer = new Timer(delay, taskPerformer);
		m_timer.start();
	}
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res) {
		if(m_centricstatus == null)
			m_centricstatus = new CentricStatus(res);
		else
			m_centricstatus.set_cbsendingresponse(res);
	}
}
