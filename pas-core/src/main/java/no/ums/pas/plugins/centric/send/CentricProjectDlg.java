package no.ums.pas.plugins.centric.send;

import no.ums.pas.core.project.ProjectDlg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CentricProjectDlg extends ProjectDlg
{
	public CentricProjectDlg(JFrame parent, ActionListener callback)
	{
		super(parent, callback, "act_project_saved", true);

	}

	@Override
	protected void createPanel() {
		m_projectpanel = new CentricProjectPanel();
        m_projectpanel.start();
	}
	
	protected class CentricProjectPanel extends ProjectPanel
	{
		public CentricProjectPanel()
		{
			super(true);
		}

		@Override
		public void add_controls() {
			m_gridconst.ipadx = 5;
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_lbl_errormsg, m_gridconst);
			m_lbl_errormsg.setForeground(Color.RED);
			/*set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_projectname, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_projectname, m_gridconst);
			set_gridconst(3, get_panel(), 1, 1, GridBagConstraints.EAST);
			add(m_btn_save, m_gridconst);
			set_gridconst(4, get_panel(), 1, 1, GridBagConstraints.EAST);
			add(m_btn_cancel, m_gridconst);*/
			set_gridconst(0, inc_panels(), 10, 1);
			add(m_project_list, get_gridconst());
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_btn_open, get_gridconst());
			set_gridconst(2, get_panel(), 2, 1);
			add(m_btn_cancel, get_gridconst());
		}
		
	}
	
}