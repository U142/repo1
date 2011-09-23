package no.ums.pas.importer.gis;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;


class PreviewOptions extends DefaultPanel {
	public static final long serialVersionUID = 1;
	PreviewFrame m_parent;
	JCheckBox m_check_firstline_columnnames;
	JComboBox m_cbx_encoding;
	JLabel m_lbl_encoding;
	JLabel m_lbl_requirements;
	JButton m_btn_fetch;
	JButton m_btn_finish;
	boolean m_b_is_alert = false;
	
	PreviewOptions(PreviewFrame parent, boolean bIsAlert) {
		super();
		m_b_is_alert = bIsAlert;
		m_parent = parent;
		init();
		setPreferredSize(new Dimension(300, 60));
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
			if(m_btn_fetch.isEnabled())
				m_btn_fetch.setToolTipText(null);
			else
				m_btn_fetch.setToolTipText(Localization.l("importpreview_please_specify"));
		}
		else if("act_change_encoding".equals(e.getActionCommand())) {
			m_parent.m_gis.set_encoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.encoding = (String)m_cbx_encoding.getSelectedItem().toString();
			m_parent.m_panel.get_resultpanel().setEncoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.m_gis.set_preview(m_parent);
			m_parent.m_gis.parse(m_parent.m_gis.get_file(), m_parent.m_gis.get_callback(), "act_gis_import_finished", m_parent.m_gis.getIsAlert());
			
		}
	}
	public void add_controls() {
		set_gridconst(0, 0, 1, 1);
		add(m_check_firstline_columnnames, get_gridconst());
		set_gridconst(0, 1, 1, 1);
		add(m_lbl_encoding, get_gridconst());
		set_gridconst(1, 1, 1, 1);
		add(m_cbx_encoding, get_gridconst());
		set_gridconst(1, 0, 1, 1);
		add(m_btn_fetch, get_gridconst());
		set_gridconst(0, 2, 2, 1);
		add(m_lbl_requirements, get_gridconst());
		set_gridconst(3, 0, 1, 1);
		add(m_btn_finish, get_gridconst());
	}
	public void init() {
        m_check_firstline_columnnames = new JCheckBox(Localization.l("importpreview_first_row_has_column_names"), true);
		m_check_firstline_columnnames.addActionListener(this);
		m_check_firstline_columnnames.setActionCommand("act_first_row_has_columnnames");
        m_btn_fetch = new JButton((m_b_is_alert ? Localization.l("common_preview") : Localization.l("common_wizard_next")));
		m_lbl_encoding = new JLabel(Localization.l("importpreview_encoding"));
		m_cbx_encoding = new JComboBox(new String[] { "ISO-8859-15", "UTF-8", "ISO-8859-2" });
		m_cbx_encoding.setActionCommand("act_change_encoding");
		m_cbx_encoding.addActionListener(this);
		m_btn_fetch.addActionListener(this);
		m_btn_fetch.setActionCommand("act_fetch_addresses");
        m_btn_finish = new JButton(Localization.l("common_finish"));
		m_btn_finish.setActionCommand("act_gis_finish");
		m_btn_finish.addActionListener(this);
		m_btn_finish.setVisible(m_b_is_alert);
		m_lbl_requirements = new JLabel(Localization.l("importpreview_please_specify"));
		add_controls();
	}
}