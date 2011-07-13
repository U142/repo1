package no.ums.pas.core.popupmenus;

import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.status.StatusItemObject;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PUInhabitantList extends PUMenu {
	public static final long serialVersionUID = 1;
	PUInhabitantList.PUActionListener m_actionlistener;
	public PUActionListener get_actionlistener() { return m_actionlistener; }
	private StatusItemObject m_statusitem;
	public void set_statusitemobject(StatusItemObject obj) {
		m_statusitem = obj;
	}
	
	public PUInhabitantList(PAS pas, String sz_name)
	{
		super(pas, sz_name);
		m_actionlistener = new PUInhabitantList.PUActionListener();
        JMenuItem item_find = new JMenuItem(Localization.l("main_status_popup_find_on_map"));
		item_find.setActionCommand("act_find");
		add(item_find);
		item_find.addActionListener(m_actionlistener);
		set_layout();
	}
	
	public class PUActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			if("act_find".equals(e.getActionCommand())) {
				if(m_statusitem!=null)
				{
					//get_pas().add_event("goto_house ", null);
					//get_pas().get_statuscontroller().goto_house(((Integer)get_id()).intValue());
					get_pas().get_navigation().exec_adrsearch(m_statusitem.get_lon(), m_statusitem.get_lat(), 200);
					// Adds search pinpoint to better show inhabitant location
					MapPointLL center = new MapPointLL(new Double(m_statusitem.get_lon()).doubleValue(), new Double(m_statusitem.get_lat()).doubleValue());
					get_pas().actionPerformed(new ActionEvent(center, ActionEvent.ACTION_PERFORMED, "act_set_pinpoint"));
				}
				else {
				}
			}
		}
	}
}