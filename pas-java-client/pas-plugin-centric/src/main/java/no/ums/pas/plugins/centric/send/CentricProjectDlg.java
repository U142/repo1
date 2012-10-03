package no.ums.pas.plugins.centric.send;

import no.ums.pas.core.project.ProjectDlg;

import javax.swing.*;
import javax.swing.table.TableColumn;

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
		// Removes the delete column, shouldn't be available in NLAlert
		TableColumn deleteColumn = m_projectpanel.get_projectlist().get_table().getColumnModel().getColumn(DELETE_COLUMN);
		if(deleteColumn != null) {
			m_projectpanel.get_projectlist().get_table().removeColumn(deleteColumn);
		}
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
			set_gridconst(0, inc_panels(), 10, 1);
			add(m_project_list, get_gridconst());
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_btn_open, get_gridconst());
			set_gridconst(2, get_panel(), 2, 1);
			add(m_btn_cancel, get_gridconst());
		}
		
	}
	
}