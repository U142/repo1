package no.ums.pas.importer.gis;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Dimension;
import java.awt.event.ActionEvent;


class PreviewOptions extends DefaultPanel {
	public static final long serialVersionUID = 1;
	PreviewFrame m_parent;
	JCheckBox m_check_firstline_columnnames;
	JButton m_btn_fetch;
	JButton m_btn_finish;
	boolean m_b_is_alert = false;
	
	PreviewOptions(PreviewFrame parent, boolean bIsAlert) {
		super();
		m_b_is_alert = bIsAlert;
		m_parent = parent;
		init();
		setPreferredSize(new Dimension(300, 50));
	}
	public void actionPerformed(ActionEvent e) {
		if("act_first_row_has_columnnames".equals(e.getActionCommand())) {
			//m_gis.actionPerformed(new ActionEvent(new Boolean(m_check_firstline_columnnames.isSelected()), ActionEvent.ACTION_PERFORMED, "act_first_row_has_columnnames"));
			e.setSource(new Boolean(m_check_firstline_columnnames.isSelected()));
			m_parent.actionPerformed(e);
		}
		else if("act_fetch_addresses".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}
		else if("act_gis_finish".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}
		else if("act_goto_next_valid".equals(e.getActionCommand())) {
			m_btn_fetch.setEnabled(((Boolean)e.getSource()).booleanValue());
		}
	}
	public void add_controls() {
		set_gridconst(0, 0, 1, 1);
		add(m_check_firstline_columnnames, get_gridconst());
		set_gridconst(1, 0, 1, 1);
		add(m_btn_fetch, get_gridconst());
		set_gridconst(2, 0, 1, 1);
		add(m_btn_finish, get_gridconst());
	}
	public void init() {
		m_check_firstline_columnnames = new JCheckBox(PAS.l("importpreview_first_row_has_column_names"), true);
		m_check_firstline_columnnames.addActionListener(this);
		m_check_firstline_columnnames.setActionCommand("act_first_row_has_columnnames");
		m_btn_fetch = new JButton((m_b_is_alert ? PAS.l("common_preview") : PAS.l("common_wizard_next")));
		m_btn_fetch.addActionListener(this);
		m_btn_fetch.setActionCommand("act_fetch_addresses");
		m_btn_finish = new JButton(PAS.l("common_finish"));
		m_btn_finish.setActionCommand("act_gis_finish");
		m_btn_finish.addActionListener(this);
		m_btn_finish.setVisible(m_b_is_alert);
		add_controls();
	}
}