package no.ums.pas.core.defines.tree;

import no.ums.pas.ums.tools.Timeout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class TreeUpdater
{
	public static final String LOADING_START = "act_start_loading";
	public static final String LOADING_FINISHED = "act_loading_finished";
	
	ActionListener m_callback;
	int updateinterval;
	public TreeUpdater(ActionListener callback, int updateinterval)
	{
		m_callback = callback;
		this.updateinterval = updateinterval;
	}
	
	public void notifyDownloadDone()
	{
        downloadReady.set(true);
	}
		
	
	/** Indicates if the TAS app should be running, only uninit() can cause it to stop*/
	protected boolean b_run = false;
    /** Signalling object for notifying download finished*/
    private final AtomicBoolean downloadReady = new AtomicBoolean(true);

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
                while(true)
				{
					try
					{
                        if (downloadReady.compareAndSet(true, false)) {
                            m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, LOADING_START));
                            downloadReady.wait(TimeUnit.MINUTES.toMillis(2));
                            timeout = new Timeout(120, 100);
                            while(!downloadReady.get() && !timeout.timer_exceeded())
                            {
                                Thread.sleep(timeout.get_msec_interval());
                                timeout.inc_timer();
                            }
                            m_callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, LOADING_FINISHED));
                        }
					}
					catch(InterruptedException interrupt) {
                        // Stop the download thread.
                        break;
					}
					catch(Exception e) {
                        // Ignored exception, download is aborted
					} finally {
                        downloadReady.set(true);
                    }
					if(only_once) {
                       break;
                    } else {
                        try {
                            Thread.sleep(10*1000);
                        } catch (InterruptedException e) {
                            // Stop the download thread.
                            break;
                        }
                    }
				}
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