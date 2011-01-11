package no.ums.pas.gps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.webdata.*;
import no.ums.pas.maps.defines.*;

/* load all icons from iconlib
 * 1) load xml with icon-information
 * 2) load icons based on xml info
 * 3) add_icons
 */
public class MapObjectPicturepane extends DefaultPanel {
	public static final long serialVersionUID = 1;
	
	private MapObjectReg m_reg;
	private ButtonGroup m_btn_group = new ButtonGroup();
	private IconObject m_selected_icon = null;
	private LabelButton m_selected_label = null;
	private boolean m_b_isloaded = false;
	private void setLoaded() { m_b_isloaded = true; }
	public boolean isLoaded() { return m_b_isloaded; }
	public IconObject get_selected_icon() { return m_selected_icon; }
	ArrayList<IconObject> m_arr_icons;
	public MapObjectPicturepane(MapObjectReg reg) {
		super();
		m_reg = reg;
		//m_gridconst.ipadx = 1;
		//m_gridconst.ipady = 1;
		//setLayout(new GridBagLayout());
		init();
	}

	public void actionPerformed(ActionEvent e) {
		if("act_iconload_finished".equals(e.getActionCommand())) {
			m_arr_icons = (ArrayList<IconObject>)e.getSource();
			load_icons();
			add_controls();
		}
		else if("act_icon_select".equals(e.getActionCommand())) {
			if(m_selected_label!=null)
			{
				m_selected_label.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			m_selected_icon = ((LabelButton)e.getSource()).get_iconobject();
			m_selected_label = (LabelButton)e.getSource();
			m_selected_label.setBorder(BorderFactory.createLineBorder(Color.RED));
			m_reg.m_obj.set_icon(m_selected_icon.get_icon());
			m_reg.m_infopanel.m_icon.setIcon(m_selected_icon.get_icon());
			
			PAS.get_pas().kickRepaint();
		}
	}
	
	class LabelButton extends JRadioButton {
		public static final long serialVersionUID = 1;
		
		IconObject m_obj;
		ImageIcon m_icon_resized;
		public IconObject get_iconobject() { return m_obj; }
		LabelButton(IconObject obj) {
			super();
			m_obj = obj;
			m_icon_resized = new ImageIcon(obj.get_icon().getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
			super.setIcon(m_icon_resized);
			super.setHorizontalAlignment(SwingConstants.CENTER);
			super.setVerticalAlignment(SwingConstants.CENTER);
			super.setToolTipText(m_obj.get_name());
			super.setPreferredSize(new Dimension(45, 45));
			
		}
	}
	
	public void add_controls() {
		int max_x = 10;
		int x = 0, y = 0;
		IconObject obj;
		LabelButton label;
		setVisible(false);
		while(1==1) {
			for(x=0; x < max_x; x++) {
				if((y * max_x + x) >=( m_arr_icons.size()-1))
					break;
				obj = (IconObject)m_arr_icons.get(y * max_x + x);
				set_gridconst(x, y, 1, 1, GridBagConstraints.CENTER);
				label = new LabelButton(obj);

				if(obj.get_picturepk().equals(m_reg.get_mapobject().get_picturepk())) {
					m_selected_icon = obj;
					m_selected_label = label;
				}
				
				m_btn_group.add(label);
				label.addActionListener(this);
				label.setBorderPainted(true);
				label.setActionCommand("act_icon_select");
				label.setBorder(BorderFactory.createLineBorder(Color.black));
				
				add(label, m_gridconst);
				doLayout();
			}
			if((y * max_x + x - 1) > m_arr_icons.size())
				break;
			y++;
		}
		setLoaded();
		setVisible(true);
		if(m_selected_label!=null)
			m_selected_label.doClick();
	}
	public void init() {
		XMLIcons iconloader = new XMLIcons(PAS.get_pas(), "PAS_get_iconlist.asp?l_companypk=" + PAS.get_pas().get_userinfo().get_comppk(), this, PAS.get_pas().get_httpreq());
		iconloader.start();
		//setVisible(true);		
	}
	private void load_icons() {
		IconObject obj;
		for(int i=0; i < m_arr_icons.size(); i++) {
			obj = (IconObject)m_arr_icons.get(i);
			obj.set_icon(no.ums.pas.ums.tools.ImageLoader.load_icon(PAS.get_pas().get_sitename(), "", obj.get_filename()));
		}
	}
	/*
	 * load external resources
	 */
/*	private static ImageIcon load_icon(String sz_dir, String sz_filename) {
		URL url_icon = null;
		ImageIcon icon = null;
		String sz_path;
		if(sz_dir.length()==0)
			sz_path = "/images/map/user_objects/";
		else
			sz_path = sz_dir;
		try { 
			url_icon = new URL(PAS.get_pas().get_sitename() + sz_path + sz_filename);
			icon = new ImageIcon(url_icon);
		} catch(MalformedURLException e) {
			
		}
		return icon;
	}*/
	/*
	 * load resources from downloaded JAR
	 */
/*	public static ImageIcon load_icon(String sz_filename) {
		ClassLoader cl = PAS.get_pas().getClass().getClassLoader();
		return new ImageIcon(cl.getResource("PASIcons/" + sz_filename));
	}*/
}