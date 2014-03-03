package no.ums.pas.importer.gis;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

    JLabel m_lbl_importOption;
    ButtonGroup m_group_importOption;
    JRadioButton m_rb_streetAddress;
    JRadioButton m_rb_propertyAddress;

    String importType="Street";

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    PreviewOptions(PreviewFrame parent, boolean bIsAlert) {
		super();
		m_b_is_alert = bIsAlert;
		m_parent = parent;
        init();
        setPreferredSize(new Dimension(300,90));
	}
	public void actionPerformed(ActionEvent e) {
		if("act_first_row_has_columnnames".equals(e.getActionCommand())) {
			//m_gis.actionPerformed(new ActionEvent(new Boolean(m_check_firstline_columnnames.isSelected()), ActionEvent.ACTION_PERFORMED, "act_first_row_has_columnnames"));
			e.setSource(new Boolean(m_check_firstline_columnnames.isSelected()));
			m_parent.actionPerformed(e);
		}
        //next button
		else if("act_fetch_addresses".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}
		else if("act_gis_finish".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}
		else if("act_goto_next_valid".equals(e.getActionCommand())) {
			m_btn_fetch.setEnabled(((Boolean) e.getSource()).booleanValue());
			if(m_btn_fetch.isEnabled())
				m_btn_fetch.setToolTipText(null);
			else
			{
				if("Street".equals(this.getImportType()))
						m_btn_fetch.setToolTipText(Localization.l("importpreview_please_specify"));
				else if("Property".equals(this.getImportType()))
						m_btn_fetch.setToolTipText(Localization.l("importpreview_please_specify_property"));
			}
		}
		else if("act_change_encoding".equals(e.getActionCommand())) {
			m_parent.m_gis.set_encoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.encoding = (String)m_cbx_encoding.getSelectedItem().toString();
			m_parent.m_panel.get_resultpanel().setEncoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.m_panel.setEncoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.m_gis.set_preview(m_parent);
			m_parent.m_gis.parse(m_parent.m_gis.get_file(), m_parent.m_gis.get_callback(), "act_gis_import_finished", m_parent.m_gis.getIsAlert());

		}
        else if("act_import_streetAddress".equals(e.getActionCommand())) {
            m_lbl_requirements.setText(Localization.l("importpreview_please_specify"));
        	m_parent.m_panel.setM_import_type("Street");
            setImportType("Street");
            m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_goto_next_valid"));
            m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_streetAddress"));//pass this event to parent frame
        }
        else if("act_import_propertyAddress".equals(e.getActionCommand())){
        	m_lbl_requirements.setText(Localization.l("importpreview_please_specify_property"));
        	m_parent.m_panel.setM_import_type("Property");
            setImportType("Property");
            m_parent.actionPerformed(new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_goto_next_valid"));
            m_parent.m_panel.setM_resultpanel(m_parent.m_panel.create_property_result_panel());
            m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_propertyAddress"));//pass this event to parent frame
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

		set_gridconst(3, 0, 1, 1);
		add(m_btn_finish, get_gridconst());


         //Requirements Label
        set_gridconst(0, 3, 2, 1);
        add(m_lbl_requirements, get_gridconst());

        set_gridconst(0, 2, 1, 1);
        add(m_lbl_importOption, get_gridconst());
        set_gridconst(1, 2, 1, 1);
        add(m_rb_streetAddress, get_gridconst());
        set_gridconst(2, 2, 1, 1);
        add(m_rb_propertyAddress, get_gridconst());
        set_gridconst(3, 2, 1, 1);
        m_rb_streetAddress.setSelected(true);
	}
	public void init() {
        m_check_firstline_columnnames = new JCheckBox(Localization.l("importpreview_first_row_has_column_names"), true);
		m_check_firstline_columnnames.addActionListener(this);
		m_check_firstline_columnnames.setActionCommand("act_first_row_has_columnnames");
        m_btn_fetch = new JButton((m_b_is_alert ? Localization.l("common_preview") : Localization.l("common_wizard_next")));
		m_lbl_encoding = new JLabel(Localization.l("importpreview_encoding"));
		m_cbx_encoding = new JComboBox(new String[] {
				Localization.l("importpreview_encoding_iso_8859_15"),
				Localization.l("importpreview_encoding_utf_8"),
				Localization.l("importpreview_encoding_iso_8859_2"),
				Localization.l("importpreview_encoding_iso_8859_1"),
				Localization.l("importpreview_encoding_cp1252"),
				Localization.l("importpreview_encoding_utf_16be") });//sachin change
		m_cbx_encoding.setActionCommand("act_change_encoding");
		m_cbx_encoding.addActionListener(this);
		m_btn_fetch.addActionListener(this);
		m_btn_fetch.setActionCommand("act_fetch_addresses");
        m_btn_finish = new JButton(Localization.l("common_finish"));
		m_btn_finish.setActionCommand("act_gis_finish");
		m_btn_finish.addActionListener(this);
		m_btn_finish.setVisible(m_b_is_alert);
		m_lbl_requirements = new JLabel(Localization.l("importpreview_please_specify"));

        //Option for either importing the street adress or property address
        m_lbl_importOption = new JLabel(Localization.l("import_option"));
        m_group_importOption =  new ButtonGroup();
        m_rb_streetAddress = new JRadioButton(Localization.l("import_street_address"));
        m_rb_streetAddress.setActionCommand("act_import_streetAddress");
        m_rb_propertyAddress = new JRadioButton(Localization.l("import_property_address"));
        m_rb_propertyAddress.setActionCommand("act_import_propertyAddress");
        m_rb_streetAddress.addActionListener(this);
        m_rb_propertyAddress.addActionListener(this);
        m_group_importOption.add(m_rb_streetAddress);
        m_group_importOption.add(m_rb_propertyAddress);
		add_controls();
	}


}