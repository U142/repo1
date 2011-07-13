package no.ums.pas.core.mainui;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.maps.defines.MapPoint;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;


public abstract class GeneralPanel extends DefaultPanel {
	boolean b_inited = false;
	/*public GeneralPanel(PAS pas, Dimension size) {
		super();
		setPreferredSize(size);
		doInit();
	}*/
	public GeneralPanel() {
		super();
	}
	public GeneralPanel(Dimension size) {
		super();
		setPreferredSize(size);
	}
	public void doInit()
	{
		init_controls();
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				if(!b_inited)
				{
					set_layout();
					add_controls();	
					b_inited = true;
				}
			}
		});
	}
	protected abstract void set_layout();
	protected abstract void do_layout();
	protected abstract void init_controls();
	
	public void add_controls() {
		do_layout();
		init();
	}
	public void init() {
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if("act_mousemove".equals(e.getActionCommand())) {
			MapPoint p = (MapPoint)e.getSource();
			update_ui(p);
		}
	}
	protected void update_ui(MapPoint p) {
		//log.debug("GeneralPanel.update_ui()");
	}

}