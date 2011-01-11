package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.status.StatusListObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OpenStatusFrame extends JFrame implements ActionListener {
	public static final long serialVersionUID = 1;
	private SearchPanelStatusList m_statuspanel;
	
	public StatusController get_controller() { return PAS.get_pas().get_statuscontroller(); }
	public SearchPanelStatusList get_statuspanel() { return m_statuspanel; }
	
	public OpenStatusFrame()
	{
		super(PAS.l("mainmenu_status_open"));
		//m_statuscontroller = controller;
		this.setIconImage(PAS.get_pas().getIconImage());
		Dimension dim = new Dimension(700, 400);
		setBounds(new Rectangle(PAS.get_pas().get_mappane().getLocationOnScreen().x + PAS.get_pas().get_mappane().get_dimension().width/2 - dim.width/2, PAS.get_pas().get_mappane().getLocationOnScreen().y + PAS.get_pas().get_mappane().get_dimension().height/2 - dim.height/2, dim.width, dim.height));
		getContentPane().setLayout(new BorderLayout ());
//String[] sz_itemattr = { "l_refno", "l_sendingtype", "l_totitem", "l_altjmp", "l_createdate", "l_createtime",
// "sz_sendingname", "l_sendingstatus", "l_group", "l_type", "l_deptpk", "sz_deptid" };
		String sz_columns[] = { PAS.l("projectdlg_projectname"), PAS.l("common_owner"), PAS.l("common_refno"), PAS.l("common_channel"), PAS.l("common_items"), PAS.l("common_type"), PAS.l("common_created"), PAS.l("common_time"), PAS.l("common_sendingname"), PAS.l("common_sendingstatus") };
		int n_width[] = { 100, 50, 50, 50, 75, 75, 100, 75, 200, 100 };
		m_statuspanel = new SearchPanelStatusList(PAS.get_pas(), this, sz_columns, n_width);
		
		init();
	}
	
	public void init()
	{
		getContentPane().add(m_statuspanel, BorderLayout.CENTER);
	}
	
	public void open()
	{
		setVisible(true);
		get_statuspanel().clear();
		m_statuspanel.setLoading(true);
		get_controller().retrieve_statuslist(this);
		//get_controller().retrieve_statusitems(this);
	}
	public void actionPerformed(ActionEvent e) {
		if("act_download_finished".equals(e.getActionCommand())) {
			try
			{
				ArrayList<StatusListObject> arr_statuslist = (ArrayList<StatusListObject>)e.getSource();
				m_statuspanel.fill(arr_statuslist);
			}
			catch(Exception err) {}
			m_statuspanel.setLoading(false);
			
		}
	}
	public void onDownloadFinishedStatusList()
	{
		
	}
	public synchronized void onDownloadFinishedStatusItems()
	{
/*		get_controller().set_nav_init(get_controller().get_xmlstatusitems().get_statusitemsthread().get_nav_init());
		get_controller().set_polygon(get_controller().get_xmlstatusitems().get_statusitemsthread().get_polygon());
		get_controller().set_statusitems(get_controller().get_xmlstatusitems().get_statusitemsthread().get_statusitems(), get_controller().get_xmlstatusitems().get_statusitemsthread().get_statuscodes());*/
		setVisible(false);
		//get_controller().get_pas().get_eastcontent().flip_to(EastContent.PANEL_STATUS_LIST);
	}

}