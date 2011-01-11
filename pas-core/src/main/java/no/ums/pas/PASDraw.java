package no.ums.pas;

import java.awt.*;


public class PASDraw extends Draw {
	PAS m_pas;
	UpdateThread m_checkforupdates_thread;
	protected PAS get_pas() { return m_pas; }
	
	public PASDraw(PAS pas, Component component, int l_pri, int x, int y) {
		super(component, l_pri, x, y);
		m_pas = pas;
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	
	protected void calc_new_coors() {
		try
		{
			PAS.pasplugin.onMapCalcNewCoords(PAS.get_pas().get_navigation(), get_pas());
			super.calc_new_coors();
		}
		catch(Exception e)
		{
			
		}
	}
	protected void draw_layers() {
		if(PAS.get_pas()!=null) // Because of MapApplet for admin interface
			PAS.pasplugin.onMapDrawLayers(PAS.get_pas().get_navigation(), m_gfx_buffer, PAS.get_pas());
		super.draw_layers();
	}
	protected void map_repaint() {
		//get_pas().get_mappane().repaint();
		//super.map_repaint();
	}
	protected synchronized void checkforupdates() {
		try {
			if(m_checkforupdates_thread.isAlive())
				m_checkforupdates_thread.interrupt();
		} catch(Exception e) { }
		if(get_pas().get_gpscontroller()!=null)
			get_pas().get_gpscontroller().check_needupdate();
		if(get_pas().get_statuscontroller()!=null)
			get_pas().get_statuscontroller().check_needupdate();
		super.checkforupdates();
		//if(m_checkforupdates_thread!=null)
		//	if(m_checkforupdates_thread.isAlive())
		//		return;
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	
	class UpdateThread extends Thread implements Runnable {
		int m_n_msec;
		UpdateThread(int ms) {
			super("PASDraw.Update Thread");
			m_n_msec = ms;
			start();
		}
		
		public void run() {
			try {
				Thread.sleep(m_n_msec);
			} catch(InterruptedException e) {
				//no longer need to wait for autoupdate. this thread will restart when updates are checked
				return;
			}
			checkforupdates();
		}
	}
	
}