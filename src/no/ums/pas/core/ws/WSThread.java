package no.ums.pas.core.ws;

import java.awt.event.*;
import java.util.UUID;

public abstract class WSThread extends Thread
{
	public static String GenJobId()
	{
		return UUID.randomUUID().toString();
	}
	ActionListener m_callback;
	String sz_cb_cmd;
	public void set_callbackcmd(String sz)
	{
		sz_cb_cmd = sz;
	}
	public WSThread(ActionListener callback)
	{
		m_callback = callback;
	}
	public abstract void run();
	public abstract void OnDownloadFinished();
	
	public void runNonThreaded()
	{
		run();
	}
}