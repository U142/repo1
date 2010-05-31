package no.ums.pas.core.popupmenus;


import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import no.ums.pas.PAS;
import no.ums.pas.core.mainui.*;
import no.ums.pas.status.StatusListObject;


public class PUOpenStatus extends PUMenu implements ActionListener {
	public static final long serialVersionUID = 1;
	//JMenuItem item_refno;
	JMenuItem item_project;
	SearchPanelStatusList m_callback;
	boolean m_b_project = false;
	StatusListObject m_obj;
	
	public PUOpenStatus(PAS pas, String sz_name, SearchPanelStatusList callback)
	{
		super(pas, sz_name);
		//item_refno = new JMenuItem("Open status for refno");
		item_project = new JMenuItem(PAS.l("projectdlg_open_project"));
		//item_refno.setActionCommand("act_status_refno");
		item_project.setActionCommand("act_status_project");
		//add(item_refno);
		add(item_project);
		//item_refno.addActionListener(this);
		item_project.addActionListener(this);
		set_layout();
		m_callback = callback;
	}
	public void pop(Component comp, Point p, StatusListObject obj) {
		super.pop(comp, p);
		m_obj = obj;
		try {
			if(obj.get_project().get_projectpk().equals("-1")) {
				m_b_project = false;
			} else {
				m_b_project = true;
			}
			item_project.setEnabled(m_b_project);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void actionPerformed(ActionEvent e) {
		/*if("act_status_refno".equals(e.getActionCommand())) {
			m_callback.openStatus(false, "-1", m_obj.get_refno());
		}
		else */if("act_status_project".equals(e.getActionCommand())) {
			m_callback.openStatus(true, m_obj.get_project().get_projectpk(), m_obj.get_refno());
		}
	}
}