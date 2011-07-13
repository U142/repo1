package no.ums.pas.core.popupmenus;


import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.ums.tools.PrintCtrl;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PUStatusList extends PUMenu {
	public static final long serialVersionUID = 1;
	public PUStatusList.PUActionListener m_actionlistener;
	public PUStatusList(PAS pas, String sz_name)
	{
		super(pas, sz_name);
		m_actionlistener = new PUStatusList.PUActionListener();
        JMenuItem item_showall = new JMenuItem(Localization.l("main_status_popup_list_all_recipients"));
        JMenuItem item_showvisible = new JMenuItem(Localization.l("main_status_popup_list_only_visible"));
        JMenuItem item_print = new JMenuItem(Localization.l("common_print"));
		item_showall.setActionCommand("act_status_showall");
		item_showvisible.setActionCommand("act_status_showvisible");
		item_print.setActionCommand("act_status_print");
		add(item_showall);
		add(item_showvisible);
		addSeparator();
		add(item_print);
		item_showall.addActionListener(m_actionlistener);
		item_showvisible.addActionListener(m_actionlistener);
		item_print.addActionListener(m_actionlistener);
		set_layout();
	}
	class PUActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			if("act_status_showall".equals(e.getActionCommand())) {
				if(get_id()!=null)
					get_pas().get_statuscontroller().search_houses(((Integer)get_id()).intValue(), true, new MapPointLL()); /*l_status, all*/
			} else if("act_status_showvisible".equals(e.getActionCommand())) {
				if(get_id()!=null)
					get_pas().get_statuscontroller().search_houses(((Integer)get_id()).intValue(), false, new MapPointLL()); /*l_status, visible*/
			} else if("act_status_print".equals(e.getActionCommand())) {
				if(get_id()!=null) {
					get_pas().get_statuscontroller().search_houses(((Integer)get_id()).intValue(), true, new MapPointLL()); /*l_status, all*/
					System.out.print(PAS.get_pas().get_inhabitantframe().get_panel().get_inhabitantframe());
					PrintCtrl.printComponent(PAS.get_pas().get_inhabitantframe().get_panel(), null);
					//PrinterJob job = prin
				}
			}
		}
	}
}