package no.ums.pas.importer.gis;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class PreviewAddressOptions extends DefaultPanel {
	public static final long serialVersionUID = 1;
	PreviewAddressFrame m_parent;
	JCheckBox m_check_firstline_columnnames;
	JComboBox m_cbx_encoding, m_cbx_address;
	JLabel m_lbl_encoding, m_lbl_msg, m_lbl_msg2, m_lbl_msg3;
	JLabel m_lbl_address_param, m_lbl_street;
	JButton m_btn_import, m_btn_cancel, m_test, m_test1, m_test2;
	//JButton m_btn_finish;
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

    PreviewAddressOptions(PreviewAddressFrame parent, boolean bIsAlert) {
		super();
		m_b_is_alert = bIsAlert;
		m_parent = parent;
        init();
        setPreferredSize(new Dimension(300,150));
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
		/*else if("act_gis_finish".equals(e.getActionCommand())) {
			m_parent.actionPerformed(e);
		}*/
		else if("frame_dispose".equals(e.getActionCommand())) {
			m_parent.dispose();
		}
		else if("act_goto_next_valid".equals(e.getActionCommand())) {
			m_btn_import.setEnabled(((Boolean) e.getSource()).booleanValue());
			if(m_btn_import.isEnabled())
				m_btn_import.setToolTipText(null);
			else
			{
				if("Street".equals(this.getImportType()))
						m_btn_import.setToolTipText(Localization.l("importpreview_please_specify"));
				else if("Property".equals(this.getImportType()))
						m_btn_import.setToolTipText(Localization.l("importpreview_please_specify_property"));
			}
		}
		else if("act_change_encoding".equals(e.getActionCommand())) {
			m_parent.m_gis.set_encoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.encoding = (String)m_cbx_encoding.getSelectedItem().toString();
			m_parent.m_panel.get_resultpanel().setEncoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.m_panel.setEncoding((String)m_cbx_encoding.getSelectedItem().toString());
			m_parent.m_gis.set_preview_addr(m_parent);
			m_parent.m_gis.parse(m_parent.m_gis.get_file(), m_parent.m_gis.get_callback(), "act_gis_import_finished", m_parent.m_gis.getIsAlert());

		}
       else if("act_change_address_type".equals(e.getActionCommand())) {
    	 
            if(m_cbx_address.getSelectedItem().toString().equals(Localization.l("import_addr_street"))){
          	  m_lbl_street.setText((Localization.l("import_addr_street_b")));
              m_lbl_address_param.setText(Localization.l("import_Street_details"));
          	   m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_streetAddress"));//pass this event to parent frame
          	   m_parent.setAddressTypeSelected("import_addr_street");
            }
            else if(m_cbx_address.getSelectedItem().toString().equals(Localization.l("import_addr_CUNorway"))){
          	  
          	  m_lbl_street.setText((Localization.l("import_addr_CUNorway_b")));
          	   m_lbl_address_param.setText(Localization.l("import_cun_details"));
          	  m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_CUN"));//pass this event to parent frame
          	  m_parent.setAddressTypeSelected("import_addr_CUNorway");
            }
            else if(m_cbx_address.getSelectedItem().toString().equals(Localization.l("import_addr_CUSweden"))){
          	  m_lbl_street.setText((Localization.l("import_addr_CUSweden_b")));
          	 m_lbl_address_param.setText(Localization.l("import_cus_details"));
          	  m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_CUS"));//pass this event to parent frame
          	 m_parent.setAddressTypeSelected("import_addr_CUSweden");
            }
            else if(m_cbx_address.getSelectedItem().toString().equals(Localization.l("import_addr_VABanken"))){
          	  m_lbl_street.setText((Localization.l("import_addr_VABanken")));
          	  m_lbl_address_param.setText(Localization.l("import_vbn_details"));
          	  m_parent.actionPerformed (new ActionEvent(new Boolean(true), ActionEvent.ACTION_PERFORMED, "act_import_VBS"));//pass this event to parent frame
          	  m_parent.setAddressTypeSelected("import_addr_VABanken");
            }
            else{
          	  m_lbl_street.setText((Localization.l("import_addr_street_b")));
            }
        }
       
	}
	
	public void add_controls() {
		
		get_gridconst().insets=new Insets(3, 3, 3, 3);
		
		set_gridconst(0, 0, 6, 1);
		
		add(m_lbl_msg, get_gridconst());
		
		set_gridconst(0, 3, 1, 1);
		add(m_lbl_msg2, get_gridconst());
		
		set_gridconst(2, 3, 1, 1);
		add(m_lbl_street, get_gridconst());
		
		set_gridconst(0, 4, 6, 6);
		add(m_lbl_address_param, get_gridconst());
		
	    //lable address type
		set_gridconstLast(5, 3, 1, 1);
		add(m_lbl_msg3, get_gridconst());
		 //dropdowns
		set_gridconstLast(6, 3, 1, 1);
		add(m_cbx_address, get_gridconst());
		
		set_gridconstLast(6, 4, 1, 1);
		add(m_cbx_encoding, get_gridconst());
		
		set_gridconstSouthWest(0, 18, 2, 1);
		add(m_check_firstline_columnnames, get_gridconst());
		
		set_gridconstSouth(10, 10, 1, 1);
		add(m_btn_import, get_gridconst());
		
		set_gridconstSouth(9, 10, 1, 1);
		add(m_btn_cancel, get_gridconst());

}
	
	public void init() {
        
		m_check_firstline_columnnames = new JCheckBox(Localization.l("importpreview_first_row_has_column_names"), true);
		m_check_firstline_columnnames.addActionListener(this);
		m_check_firstline_columnnames.setActionCommand("act_first_row_has_columnnames");
		
		
		
		m_lbl_msg = new JLabel(Localization.l("import_first_row_msg"));
		
       
		m_lbl_msg2 = new JLabel(Localization.l("import_second_row_msg"));
		m_lbl_msg3 = new JLabel(Localization.l("import_second_row_second_column"));
		m_lbl_street = new JLabel(Localization.l("import_addr_street_b"));
		m_lbl_address_param = new JLabel(Localization.l("import_Street_details"));
		
		m_cbx_address = new JComboBox(new String[] {
				Localization.l("import_addr_street"),
				Localization.l("import_addr_CUNorway"),
				Localization.l("import_addr_CUSweden"),
				Localization.l("import_addr_VABanken"),
				 });
		m_parent.setAddressTypeSelected("import_addr_street");
		m_cbx_address.setActionCommand("act_change_address_type");
		m_cbx_address.addActionListener(this);
		//m_cbx_encoding.setActionCommand("act_change_encoding");
		//m_cbx_encoding.addActionListener(this);
		
		
		m_cbx_encoding = new JComboBox(new String[] {
				Localization.l("importpreview_encoding_iso_8859_15"),
				Localization.l("importpreview_encoding_utf_8"),
				Localization.l("importpreview_encoding_iso_8859_2"),
				Localization.l("importpreview_encoding_iso_8859_1"),
				Localization.l("importpreview_encoding_cp1252"),
				Localization.l("importpreview_encoding_utf_16be") });//sachin change
		m_cbx_encoding.setActionCommand("act_change_encoding");
		m_cbx_encoding.addActionListener(this);
		
		 m_btn_import = new JButton((m_b_is_alert ? Localization.l("common_preview") : Localization.l("common_wizard_import")));
		 m_btn_import.addActionListener(this);
		 m_btn_import.setActionCommand("act_fetch_addresses");
		 
		 m_btn_cancel = new JButton("Cancel");
		 m_btn_cancel.addActionListener(this);
		 m_btn_cancel.setActionCommand("frame_dispose");
		 
    
        m_group_importOption =  new ButtonGroup();

		add_controls();
	}


}