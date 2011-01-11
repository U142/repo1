package no.ums.pas.core.defines.tree;

import no.ums.pas.ums.tools.Timeout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class TreeUpdater
{
	public static String LOADING_START = "act_start_loading";
	public static String LOADING_FINISHED = "act_loading_finished";
	
	ActionListener m_callback;
	int updateinterval;
	public TreeUpdater(ActionListener callback, int updateinterval)
	{
		m_callback = callback;
		this.updateinterval = updateinterval;
	}
	
	public void notifyDownloadDone()
	{
		synchronized(download_notify)
		{
			//download_notify.notify();
			download_notify = true;
		}
	}
		
	
	/** Indicates if the TAS app should be running, only uninit() can cause it to stop*/
	protected boolean b_run = false;
	/** Indicates when the update thread has stopped*/
	protected boolean b_running = false;
	/** Last updated record, use this as filter in Download procedure*/
	protected long n_last_update = -1;
	/** Signalling object for notifying download finished*/
	protected Boolean download_notify = new Boolean(false);
	
	protected boolean b_need_to_wait = true;
	
	public void setNeedToWait(boolean b)
	{
		//b_need_to_wait = b;
		download_notify = b;
	}
	
	public boolean isRunning()
	{
		return b_running;
	}
	Timeout timeout = null;
	Thread thread = null;
	
	public void startDownloadThread(final boolean only_once)
	{
		if(b_run) //already running
			return; 
		b_run = true;
		thread = new Thread("TreeUpdater Download thread")
		{
			public void run()
			{
				b_running = true;
				while(b_run)
				{
					try
					{
						setNeedToWait(false);
						m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, LOADING_START));
						timeout = new Timeout(120, 100);
						while(!download_notify && !timeout.timer_exceeded())
						{
							Thread.sleep(timeout.get_msec_interval());
							timeout.inc_timer();
						}
						if(!b_run)
							break;
						m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, LOADING_FINISHED));
						//System.out.println("TreeUpdater Waiting " + m_callback.getClass().getName());
						if(only_once)
						{
							System.out.println("TreeUpdater only_once="+only_once);
							break;
						}
						Thread.sleep(10*1000);
					}
					catch(InterruptedException interrupt)
					{
						if(!b_run)
						{
							System.out.println("TreeUpdater Interrupted");
							break;
						}
					}
					catch(Exception e)
					{
					}
					if(only_once)
						b_run = false;
				}
				b_running = false;
				System.out.println("Tree updater exited gracefully");
			}
		};
		thread.start();
	}
	public void uninit()
	{
		b_run = false;
		try
		{
			timeout.ForceExit();
			thread.interrupt();
		}
		catch(Exception e)
		{
			
		}
	}

}