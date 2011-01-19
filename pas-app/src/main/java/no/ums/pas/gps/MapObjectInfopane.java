package no.ums.pas.gps;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.maps.defines.MapObjectVars;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MapObjectInfopane extends DefaultPanel {
	public static final long serialVersionUID = 1;
	public static final int MODE_STATIC_ = 0;
	public static final int MODE_DYNAMIC_ = 1;
	private MapObjectReg m_reg;
	JLabel m_icon;
	JRadioButton m_radio_static = new JRadioButton("Static");
	JRadioButton m_radio_dynamic= new JRadioButton("Dynamic (GPS)", true);
	ButtonGroup m_btn_group = new ButtonGroup();
	StdTextLabel m_lbl_name		= new StdTextLabel("Name:", 150);
	StdTextArea m_txt_name		= new StdTextArea("", false, 200);
	StdTextLabel m_lbl_gsmno		= new StdTextLabel("GSM No.:", 150);
	StdTextArea m_txt_gsmno		= new StdTextArea("", false, 150);
	StdTextLabel m_lbl_unitpk	= new StdTextLabel("Unit no:", 150);
	StdTextArea m_txt_unitpk	= new StdTextArea("", false, 150);
	StdTextLabel m_lbl_gsmno2	= new StdTextLabel("GSM No.(comm): ", 150);
	StdTextArea m_txt_gsmno2	= new StdTextArea("", false, 150);
	StdTextLabel m_lbl_gpsmanufacturer = new StdTextLabel("GPS Type: ", 150);
	JComboBox m_combo_gpsmanufacturer = new JComboBox(MapObjectVars.GpsUnitManufacturers.getNames());
	StdTextLabel m_lbl_gpsusage = new StdTextLabel("Carrier: ", 150);
	JComboBox m_combo_gpsusage = new JComboBox(MapObjectVars.GpsUnitUsertype.getNames());
	StdTextLabel m_lbl_location = new StdTextLabel("Location:", 150);
	StdTextLabel m_txt_location	= new StdTextLabel("", 300);
	
	public MapObjectInfopane(MapObjectReg reg) {
		super();
		m_reg = reg;
		if(!m_reg.get_mapobject().isNew())
			m_icon = new JLabel(m_reg.get_mapobject().get_icon());
		else
			m_icon = new JLabel(ImageLoader.load_icon("mime-type-capplet.png"));
		m_btn_group.add(m_radio_static);
		m_btn_group.add(m_radio_dynamic);
		m_combo_gpsmanufacturer.setPreferredSize(new Dimension(100, 15));
		m_combo_gpsusage.setPreferredSize(new Dimension(100, 15));
		add_controls();
		m_combo_gpsmanufacturer.setSelectedIndex(m_reg.get_mapobject().get_manufacturer());
		m_combo_gpsusage.setSelectedIndex(m_reg.get_mapobject().get_usertype());
		m_txt_location.setText(m_reg.get_mapobject().get_street() + " " + m_reg.get_mapobject().get_region());
	}
	public void actionPerformed(ActionEvent e) {
		if("act_static".equals(e.getActionCommand())) {
			set_mode(MODE_STATIC_);
		} else if("act_dynamic".equals(e.getActionCommand())) {
			set_mode(MODE_DYNAMIC_);
		}
	}
	public void update_data(boolean b_to_vars) {
		//get_pas().add_event("manuf-id: " + m_combo_gpsmanufacturer.getSelectedIndex());
		if(b_to_vars) {
			try {
				m_reg.get_mapobject().set_name(m_txt_name.getText());
				m_reg.get_mapobject().set_gsmno(m_txt_gsmno.getText());
				m_reg.get_mapobject().set_unitpk(m_txt_unitpk.getText());
				m_reg.get_mapobject().set_gsmno2(m_txt_gsmno2.getText());
				m_reg.get_mapobject().set_manufacturer(MapObjectVars.GpsUnitManufacturers.values()[m_combo_gpsmanufacturer.getSelectedIndex()].getId());
				m_reg.get_mapobject().set_usertype(MapObjectVars.GpsUnitUsertype.values()[m_combo_gpsusage.getSelectedIndex()].getId());
				m_reg.get_mapobject().set_picturepk(m_reg.m_picturepanel.get_selected_icon().get_picturepk());
			} catch(Exception e) {
				PAS.get_pas().add_event("update_data(true) failed " + e.getMessage(), e);
			}
		} else {
			
		}
	}

	public void add_controls() {
		set_gridconst(0, 0, 4, 1, GridBagConstraints.NORTH);
		add(m_icon, m_gridconst);
		set_gridconst(0, 1, 2, 1, GridBagConstraints.WEST);
		add(m_radio_static, m_gridconst);
		set_gridconst(2, 1, 2, 1, GridBagConstraints.WEST);
		add(m_radio_dynamic, m_gridconst);
		set_gridconst(0, 2, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_name, m_gridconst);
		set_gridconst(2, 2, 2, 1, GridBagConstraints.WEST);
		add(m_txt_name, m_gridconst);
		set_gridconst(0, 3, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_gsmno, m_gridconst);
		set_gridconst(2, 3, 2, 1, GridBagConstraints.WEST);
		add(m_txt_gsmno, m_gridconst);
		set_gridconst(0, 4, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_unitpk, m_gridconst);
		set_gridconst(2, 4, 2, 1, GridBagConstraints.WEST);
		add(m_txt_unitpk, m_gridconst);
		set_gridconst(0, 5, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_gsmno2, m_gridconst);
		set_gridconst(2, 5, 2, 1, GridBagConstraints.WEST);
		add(m_txt_gsmno2, m_gridconst);
		set_gridconst(0, 6, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_gpsmanufacturer, m_gridconst);
		set_gridconst(2, 6, 2, 1, GridBagConstraints.WEST);
		add(m_combo_gpsmanufacturer, m_gridconst);
		set_gridconst(0, 7, 2, 1, GridBagConstraints.WEST);
		add(m_lbl_gpsusage, m_gridconst);
		set_gridconst(2, 7, 2, 1, GridBagConstraints.WEST);
		add(m_combo_gpsusage, m_gridconst);
		if(!m_reg.get_mapobject().isNew()) {
			set_gridconst(0, 8, 2, 1, GridBagConstraints.WEST);
			add(m_lbl_location, m_gridconst);
			set_gridconst(2, 8, 2, 1, GridBagConstraints.WEST);
			add(m_txt_location, m_gridconst);
		}

		init();
	}
	public void init() {
		m_radio_static.addActionListener(this);
		m_radio_dynamic.addActionListener(this);
		m_radio_static.setActionCommand("act_static");
		m_radio_dynamic.setActionCommand("act_dynamic");
		populate_controls();

		doLayout();
		setVisible(true);
	}	
	public void populate_controls() {

		if(m_reg.get_mapobject().isNew())
			return;
		if(m_reg.get_mapobject().get_dynamic()) 
			m_radio_dynamic.doClick(); 
		else 
			m_radio_static.doClick();
		m_radio_dynamic.setEnabled(false);
		m_radio_static.setEnabled(false);
		m_txt_name.setText(m_reg.get_mapobject().get_name());
		m_txt_gsmno.setText(m_reg.get_mapobject().get_gsmno());
		m_txt_unitpk.setText(m_reg.get_mapobject().get_unitpk());
		
	}
	public void set_mode(int n_mode) {
		boolean b_show = true;
		if(n_mode==MODE_STATIC_) {
			b_show = false;
		} else if(n_mode==MODE_DYNAMIC_) {
		}
		m_lbl_gsmno.setVisible(b_show);
		m_txt_gsmno.setVisible(b_show);
		m_lbl_unitpk.setVisible(b_show);
		m_txt_unitpk.setVisible(b_show);
		m_lbl_gsmno2.setVisible(b_show);
		m_txt_gsmno2.setVisible(b_show);
		m_lbl_gpsmanufacturer.setVisible(b_show);
		m_combo_gpsmanufacturer.setVisible(b_show);
		m_lbl_gpsusage.setVisible(b_show);
		m_combo_gpsusage.setVisible(b_show);
		doLayout();
		m_reg.get_mapobject().set_dynamic(b_show);
		//m_reg.set_mode(n_mode);
	}
}