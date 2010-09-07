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
	
	public CentricStatus getOpenedStatus() { 
		if(m_centricstatus!=null)
		{
			if(m_centricstatus.isClosed())
				return null;
			return m_centricstatus;
		}
		else
			return null;
	}
	private CentricStatus m_centricstatus;
	private long m_projectpk;
	public void setProjectpk(long l_projectpk) { m_projectpk = l_projectpk; }
	public long getProjectpk() { return m_projectpk; }
	private boolean ready = true;
	public boolean isReady() { return ready; }
	private CentricStatusTimer m_timer = null;
	public boolean isStopped()
	{
		return (getProjectpk()<=0 ? true : false);
	}
	
	
	public CentricStatusController()
	{
		super();
	}
	
	public boolean StopUpdates()
	{
		setProjectpk(0);
		if(m_timer!=null)
		{
			m_centricstatus.setClosed();
			m_timer.stop();
			System.out.println("Status updates stopped");
		}
		return true;
	}
	
	/*public boolean OpenStatus(CBSENDINGRESPONSE sendresponse, CentricSendOptionToolbar centricsend) {
		set_cbsendingresponse(sendresponse);
		m_projectpk = sendresponse.getLProjectpk();	
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		// update status ting med CBSendingresponse?
		runTimer();
		return true;
	}*/
	
	public boolean OpenStatus(long l_projectpk, CentricSendOptionToolbar centricsend) {
		CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
		res.setLProjectpk(l_projectpk);
		set_cbsendingresponse(res, false);
		if(getOpenedStatus()==null)
			m_centricstatus = new CentricStatus(res);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(m_centricstatus);
		((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(centricsend);
		PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSTATUS_);
		
		m_projectpk = l_projectpk;
		runTimer();
		return true;
	}
	
	
	ActionListener taskPerformer = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(m_centricstatus.isReady() && getProjectpk()>0) {
				CBSENDINGRESPONSE res = new CBSENDINGRESPONSE();
				res.setLProjectpk(m_projectpk);
				
				m_centricstatus.set_cbsendingresponse(res);
				m_centricstatus.getCBStatus(res);
				System.out.println("CentricStatusControl updates - timer="+m_timer.toString());
			}
			else
			{
				System.out.println("CentricStatusControl busy...");
			}
			m_timer.setDelay(m_timer.getRecurringDelay());
		}
	};
	protected class CentricStatusTimer extends Timer
	{
		public ActionListener getTaskPerformer() { return taskPerformer; }
		public int getRecurringDelay() { return recurring_delay; }
		int recurring_delay = 10000;
		public CentricStatusTimer(int initial_delay, int recurring_delay)
		{
			super(initial_delay, taskPerformer);
			this.recurring_delay = recurring_delay;
		}
	}
	public void forceQuickUpdate()
	{
		if(m_timer!=null)
		{
			m_timer.setDelay(1);
		}
	}

	
	private void runTimer() {
		if(getProjectpk()>0)
		{
			if(m_timer==null || !m_timer.isRunning())
			{
				int initial_delay = 1; //milliseconds
				int recurring_delay = 10000;
				m_timer = new CentricStatusTimer(initial_delay, recurring_delay);
				m_timer.start();
			}
		}
	}
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res, boolean b_isnewsending) {
		if(m_centricstatus == null)
			m_centricstatus = new CentricStatus(res);
		else
			m_centricstatus.set_cbsendingresponse(res);
		if(b_isnewsending)
			forceQuickUpdate();
		runTimer();
	}
}
