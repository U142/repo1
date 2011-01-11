package no.ums.pas.core.popupmenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

import no.ums.pas.PAS;

public class PUGPSList extends PUMenu {
	public static final long serialVersionUID = 1;
	PUGPSList.PUActionListener m_actionlistener;
	public PUActionListener get_actionlistener() { return m_actionlistener; }
	public PUGPSList(PAS pas, String sz_name)
	{
		super(pas, sz_name);
		m_actionlistener = new PUGPSList.PUActionListener();
		JMenuItem item_find = new JMenuItem("Find");
		JMenuItem item_edit = new JMenuItem("Edit");
		JMenuItem item_getcoor = new JMenuItem("Get current position");
		JMenuItem item_send_voice = new JMenuItem("Send Voice Message");
		JMenuItem item_send_sms = new JMenuItem("Send SMS");
		JMenuItem item_query_battery = new JMenuItem("Query battery");
		JMenuItem item_shutdown = new JMenuItem("Shutdown");
		//SliderMenuItem item_epsilon = new SliderMenuItem(pas, "Epsilon");
		item_find.setActionCommand("act_find");
		item_edit.setActionCommand("act_edit");
		item_getcoor.setActionCommand("act_current_position");
		item_send_voice.setActionCommand("act_send_voice");
		item_send_sms.setActionCommand("act_send_sms");
		item_query_battery.setActionCommand("act_query_battery");
		item_shutdown.setActionCommand("act_shutdown");
		add(item_find);
		add(item_edit);
		add(item_getcoor);
		add(item_send_voice);
		add(item_send_sms);
		add(item_query_battery);
		add(item_shutdown);
		//add(item_epsilon);
		item_find.addActionListener(m_actionlistener);
		item_edit.addActionListener(m_actionlistener);
		item_getcoor.addActionListener(m_actionlistener);
		item_send_voice.addActionListener(m_actionlistener);
		item_send_sms.addActionListener(m_actionlistener);
		item_query_battery.addActionListener(m_actionlistener);
		item_shutdown.addActionListener(m_actionlistener);
		
		//Sjekker hvilke rettigheter brukeren har
		if(pas.get_rightsmanagement().read_fleetcontrol())
			item_find.setEnabled(true);
		
		if(pas.get_rightsmanagement().write_fleetcontrol()) {
			item_edit.setEnabled(true);
			item_getcoor.setEnabled(true);
			item_send_sms.setEnabled(true);
			item_send_voice.setEnabled(true);
			item_query_battery.setEnabled(true);
			item_shutdown.setEnabled(true);
		}
		else {
			item_edit.setEnabled(false);
			item_getcoor.setEnabled(false);
			item_send_sms.setEnabled(false);
			item_send_voice.setEnabled(false);
			item_query_battery.setEnabled(false);
			item_shutdown.setEnabled(false);
		}
		
		set_layout();
	}
	
	public class PUActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			if("act_find".equals(e.getActionCommand())) {
				if(get_id()!=null)
				{
					get_pas().add_event("goto MapObject" + get_id(), null);
					get_pas().get_gpscontroller().goto_object(((String)get_id()), 1000);
				}
				else
					get_pas().add_event("PUGPSList: get_id()==null", null);
			}
			else if("act_edit".equals(e.getActionCommand())) {
				get_pas().get_gpscontroller().reg_mapobj(get_pas().get_gpscontroller().get_mapobjects().find((String)get_id()));
			}
			else if("act_send_voice".equals(e.getActionCommand())) {
				
			}
			else if("act_send_sms".equals(e.getActionCommand())) {
				
			}
			else if("act_current_position".equals(e.getActionCommand())) {
				get_pas().get_gpscontroller().get_current_position(((String)get_id()));
			}
			else if("act_query_battery".equals(e.getActionCommand())) {
				get_pas().get_gpscontroller().gps_query_battery((String)get_id());
			}
			else if("act_shutdown".equals(e.getActionCommand())) {
				get_pas().get_gpscontroller().gps_shutdown((String)get_id());
			}
		}
	}
	
}