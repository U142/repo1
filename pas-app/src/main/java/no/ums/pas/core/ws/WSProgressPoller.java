package no.ums.pas.core.ws;

import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.PercentResult;
import no.ums.ws.common.ProgressJobType;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.Component;
import java.net.URL;
import java.util.Calendar;

/**
 * This class will contact PAS WebService continuously until it's defined as finished.
 */
public class WSProgressPoller extends Thread
{
	protected ULOGONINFO logon;
	protected Component loader;
	protected ProgressJobType jobType;
	protected boolean b_run = true;
	protected int updateInterval_ms = 2000;
	protected String sz_loadertext;
	protected Calendar startdate;
	protected boolean b_only_percent = false;
	protected String sz_finished_text;
	public void setFinishedText(String s) { sz_finished_text = s; }
	public void setShowOnlyPercent(boolean b) { b_only_percent = b; }
	protected boolean b_use_timer = false;
	
	public void SetFinished() { 
		b_run = false;
		if(loader!=null)
		{
			if(loader.getClass().equals(LoadingPanel.class))
			{
				LoadingPanel tmp = (LoadingPanel)loader;
				tmp.set_currentitem(100);
				try
				{
					Thread.sleep(500);
				}
				catch(Exception e)
				{
					
				}
				tmp.set_totalitems(100, sz_finished_text);		
			}
			else if(loader.getClass().equals(LoadingFrame.class))
			{
				LoadingFrame tmp = (LoadingFrame)loader;
				tmp.set_currentitem(100);
				try
				{
					Thread.sleep(500);
				}
				catch(Exception e)
				{
					
				}
				tmp.set_totalitems(100, sz_finished_text);
			}
			else if(loader.getClass().equals(StdTextLabel.class))
			{
				StdTextLabel tmp = (StdTextLabel)loader;
				tmp.setText("100%");
			}
		}		
	}
	public void SetUpdateInterval(int ms) { updateInterval_ms = ms; }
	
	
	public WSProgressPoller(Component loader, ProgressJobType jobType, ULOGONINFO logon, String sz_loadertext, String sz_finishedtext, boolean b_use_timer)
	{
		super("WSProgressPoller thread");
		this.loader = loader;
		this.jobType = jobType;
		this.logon = logon;
		this.sz_loadertext = sz_loadertext;
		this.sz_finished_text = sz_finishedtext; 
		logon.setJobid(logon.getJobid());
		this.b_use_timer = b_use_timer;
		initLoader(100, sz_loadertext, b_use_timer);
	}
	
	public void initLoader(int n_max, String text, boolean b_use_timer)
	{
		if(loader!=null)
		{
			if(loader.getClass().equals(LoadingPanel.class))
			{
				LoadingPanel tmp = (LoadingPanel)loader;
				tmp.set_totalitems(n_max, text);
				tmp.set_currentitem(0);
				tmp.initTimer(b_use_timer);
			}
			else if(loader.getClass().equals(LoadingFrame.class))
			{
				LoadingFrame tmp = (LoadingFrame)loader;
				tmp.set_totalitems(n_max, text);
				tmp.set_currentitem(0);
			}
		}
	}
	
	public void run()
	{
		startdate = Calendar.getInstance();
		URL wsdl = null;
		QName service = null;
		try
		{
			wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
			//wsdl = new URL("http://localhost/ws/PAS.asmx?WSDL");
			service = new QName("http://ums.no/ws/pas/", "pasws");
		}
		catch(Exception e)
		{
			SetFinished(); //abort
		}
		PercentResult res = new PercentResult();
		while(b_run)
		{
			try
			{
				
				res = new Pasws(wsdl, service).getPaswsSoap12().getProgress(logon, jobType);
				int percent = res.getNPercent();//new Integer(sz_value).intValue();
				if(loader!=null)
				{
					if(loader.getClass().equals(LoadingPanel.class))
					{
						LoadingPanel tmp = (LoadingPanel)loader;
						if(b_only_percent)
							tmp.set_totalitems(100, sz_loadertext);
						else
							tmp.set_totalitems(100, sz_loadertext + " ("+res.getNCurrentrecord() + " / " + res.getNTotalrecords() + ")");
						tmp.set_currentitem(percent);
						if(!b_use_timer)
							tmp.doTimeCalc();
					}
					else if(loader.getClass().equals(LoadingFrame.class))
					{
						LoadingFrame tmp = (LoadingFrame)loader;
						if(b_only_percent)
							tmp.set_totalitems(100, sz_loadertext);
						else
							tmp.set_totalitems(100, sz_loadertext + " ("+res.getNCurrentrecord() + " / " + res.getNTotalrecords() + ")");
						if(b_only_percent)
							tmp.set_currentitem(percent, sz_loadertext);
						else
							tmp.set_currentitem(percent, sz_loadertext + " ("+res.getNCurrentrecord() + " / " + res.getNTotalrecords() + ")");
					}
					else if(loader.getClass().equals(StdTextLabel.class))
					{
						StdTextLabel tmp = (StdTextLabel)loader;
						tmp.setText(percent + "%");
					}
				}
				Thread.sleep(updateInterval_ms);
			}
			catch(Exception e)
			{
				try
				{
					Thread.sleep(updateInterval_ms);
				}
				catch(Exception err)
				{
					SetFinished(); //abort
				}
				//log.warn(e.getMessage(), e);
			}
		}
	}
	
}