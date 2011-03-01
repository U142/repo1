package no.ums.pas.core.mainui;


import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.ws.WSClosestGAB;
import no.ums.pas.core.ws.WSHouseEditor;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.HOUSEEDITOROPERATION;
import no.ums.ws.pas.UMapPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;


public class HouseEditorPanel extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
	class IconRadio extends JRadioButton {
		public static final long serialVersionUID = 1;
		private Object _value = null;
		public Object get_value() { return _value; }
		IconRadio(String sz_icon, String sz_text, Object value) {
			super(sz_text, ImageLoader.load_icon(sz_icon));
			_value = value;
		}
		IconRadio(String sz_text, Object value) {
			this(sz_text, value, false);
		}
		IconRadio(String sz_text, Object value, boolean selected) {
			super(sz_text, selected);
			_value = value;
		}
	}
	
	private StdTextLabel m_lbl_houseinfo= new StdTextLabel("", 300, 12, true);
	private StdTextLabel m_lbl_name		= new StdTextLabel(PAS.l("common_adr_name"), 150);
	private StdTextLabel m_lbl_phone	= new StdTextLabel(PAS.l("common_adr_phone"), 150);
	private StdTextLabel m_lbl_mobile	= new StdTextLabel(PAS.l("common_adr_mobile"), 150);
	private StdTextLabel m_lbl_address	= new StdTextLabel(PAS.l("common_adr_address"), 150);
	//private StdTextLabel m_lbl_postno	= new StdTextLabel("Postno", 100);
	private StdTextLabel m_lbl_place	= new StdTextLabel(PAS.l("common_adr_postno") + "/" + PAS.l("common_adr_postplace") ,150);
	private StdTextLabel m_lbl_gnrbnr		= new StdTextLabel(PAS.l("common_adr_gno") + "/" + PAS.l("common_adr_bno"), 150);
	private StdTextLabel m_lbl_municipal= new StdTextLabel(PAS.l("common_adr_municipal_no"), 150);
	private StdTextLabel m_lbl_streetid = new StdTextLabel(PAS.l("common_adr_streetid"), 150);
	private StdTextLabel m_lbl_lonlat	= new StdTextLabel(PAS.l("common_lon") + "/" + PAS.l("common_lat"), 150);
	private StdTextLabel m_lbl_birthday = new StdTextLabel(PAS.l("common_adr_birthday") + " (DD.MM.YYYY)", 150);
	private StdTextArea m_txt_name		= new StdTextArea("", false, 150);
	private StdTextArea m_txt_phone		= new StdTextArea("", false, 100);
	private StdTextArea m_txt_mobile	= new StdTextArea("", false, 100);
	private StdTextArea m_txt_address	= new StdTextArea("", false, 150);
	private StdTextArea m_txt_house		= new StdTextArea("", false, 30);
	private StdTextArea m_txt_letter	= new StdTextArea("", false, 20);
	private StdTextArea m_txt_postno	= new StdTextArea("", false, 50);
	private StdTextArea m_txt_place		= new StdTextArea("", false, 100);
	private StdTextArea m_txt_gnr		= new StdTextArea("", false, 50);
	private StdTextArea m_txt_bnr		= new StdTextArea("", false, 50);
	private StdTextArea m_txt_municipal = new StdTextArea("", false, 75);
	private StdTextArea m_txt_streetid	= new StdTextArea("", false, 75);
	private StdTextLabel m_txt_lonshow  = new StdTextLabel("", false, 75);
	private StdTextLabel m_txt_latshow  = new StdTextLabel("", false, 75);
	private StdTextArea m_txt_birthday = new StdTextArea("", false, 75);
	private JButton m_btn_save			= new JButton(PAS.l("common_save"));
	private HouseInhabitantsList m_inhablist = null;
	private IconRadio m_radio_private	= new IconRadio(PAS.l("common_adr_private"), 0, true);
	private IconRadio m_radio_company	= new IconRadio(PAS.l("common_adr_company"), 1);
	private ButtonGroup m_group_user	= new ButtonGroup();
	
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		//System.out.println("resize: " + getWidth() + ", " + m_inhablist.getHeight());
		//m_lbl_houseinfo.setPreferredSize(new Dimension(getWidth(), m_lbl_houseinfo.getHeight()));
		//m_inhablist.setMinimumSize(new Dimension(getWidth(), 100));
		//m_inhablist.setMaximumSize(new Dimension(getWidth(), 1000));
		int height = getHeight();
		m_inhablist.setPreferredSize(new Dimension(getWidth(), height-400));
		//m_inhablist.setSize(d);
	}	
	public void set_focus() {
		
	}
	
	public void fill_form(Inhabitant inhab) {
		if(inhab!=null) {
			try {
				m_txt_name.setText(inhab.get_adrname());
				m_txt_phone.setText(inhab.get_number());
				m_txt_mobile.setText(inhab.get_mobile());
				m_txt_address.setText(inhab.get_postaddr());
				m_txt_house.setText(inhab.get_no());
				m_txt_letter.setText(inhab.get_letter());
				m_txt_postno.setText(inhab.get_postno());
				m_txt_place.setText(inhab.get_postarea());
				m_txt_gnr.setText(Integer.toString(inhab.get_gnumber()));
				m_txt_bnr.setText(Integer.toString(inhab.get_bnumber()));
				m_txt_municipal.setText(inhab.get_region());
				m_txt_birthday.setText(inhab.get_birthday_formatted());
				m_txt_streetid.setText(Integer.toString(inhab.get_streetid()));
				m_inhabitant = inhab;
				//m_txt_streetid.setText(inhab.
				//if(inhab.get_adrtype()==Controller.ADR_TYPES_COMPANY_)
				if((inhab.get_adrtype() & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY ||
					(inhab.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY)
					m_radio_company.doClick();
				else
					m_radio_private.doClick();
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseEditorPanel","Exception in fill_form",e,1);
			}
		} else {
			m_txt_name.setText("");
			m_txt_phone.setText("");
			m_txt_mobile.setText("");
			if(m_gislookup!=null)
			{
				m_txt_address.setText(m_gislookup.get_postaddr());
				m_txt_house.setText(m_gislookup.get_no());
				m_txt_letter.setText(m_gislookup.get_letter());
				m_txt_place.setText(m_gislookup.get_postarea());
			}
			m_txt_postno.setText("");
			m_txt_gnr.setText("");
			m_txt_bnr.setText("");
			m_txt_municipal.setText("");
			m_txt_birthday.setText("");
			m_txt_streetid.setText("");
			m_radio_private.doClick();
			m_inhabitant = null;
		}
	}
	
	//HouseItem m_house;
	//protected HouseItem get_house() { return m_house; 
	protected ArrayList<HouseItem> m_house;
	protected Inhabitant m_inhabitant;
	protected Inhabitant m_gislookup;
	protected Inhabitant get_gislookup() { return m_gislookup; }
	protected Inhabitant get_inhabitant() { return m_inhabitant; }
	protected ArrayList<HouseItem> get_house() { return m_house; }
	protected MapPoint m_point;
	protected ActionListener m_callback;
	protected HouseInhabitantsList get_inhablist() { return m_inhablist; }
	
	public HouseEditorPanel(ActionListener callback, MapPoint point, HouseItem house) {
		super();
		m_point = point;
		m_callback = callback;
		m_house = new ArrayList<HouseItem>();//house;
		
		m_inhablist = new HouseInhabitantsList(new String [] { "", PAS.l("common_adr_name"), PAS.l("common_adr_address"), PAS.l("common_adr_postno"), PAS.l("common_adr_postplace"), PAS.l("common_adr_phone"), PAS.l("common_adr_mobile"), "" }, new int [] { 16, 100, 100, 50, 100, 75, 75, 16 } );
		
		m_group_user.add(m_radio_private);
		m_group_user.add(m_radio_company);
		//m_house = new HouseItem(point.get_lon(), point.get_lat());
		if(point!=null) {
			m_inhabitant = new Inhabitant(point.get_lon(), point.get_lat());
		}
		add_controls();
		//gab_lookup();
		addComponentListener(this);
		get_inhablist().start_search();		
	}
	public void reinit(MapPoint point, ArrayList<HouseItem> house) {
		m_inhabitant = new Inhabitant(point.get_lon(), point.get_lat());
		m_point = point;
		m_house = house;
		if(m_house==null)
		{
			gab_lookup();
		}
		else
		{
			init_values(m_inhabitant);
			//fill form using the first inhabitant's credentials
			if(m_house.size()>=1 && m_house.get(0).get_inhabitantcount()>0)
			{
				try
				{
					Inhabitant tmp = m_house.get(0).get_itemfromhouse(0).clone();
					tmp.set_adrname("");
					tmp.set_deptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
					tmp.set_birthday("");
					tmp.set_mobile("");
					tmp.set_number("");
					tmp.set_kondmid("");
					fill_form(tmp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}
		get_inhablist().start_search();
	}
	
	public void refresh(HouseItem house) {
		if(m_house!=null && m_house.size()>=1)
		{
			m_house = new ArrayList<HouseItem>();
			m_house.add(house);
			get_inhablist().start_search();
		}
		//m_house = null;
		//housepointer is still valid
	}
	
	protected void gab_lookup() {
	
		//String sz_url = "PAS_gablookup.asp?lon=" + get_inhabitant().get_lon() + "&lat=" + get_inhabitant().get_lat();
		//new XMLClosestGAB(PAS.get_pas().get_sitename(), sz_url, this, "act_gablookup_complete").start();
		try
		{
			UMapPoint p = new UMapPoint();
			p.setLat(get_inhabitant().get_lat());
			p.setLon(get_inhabitant().get_lon());
			new WSClosestGAB(this, "act_gablookup_complete", p);
		}
		catch(Exception e)
		{
			
		}
	}
	protected void init_values(Inhabitant item) {
		m_inhabitant = item;
		m_txt_lonshow.setText(new Double(get_inhabitant().get_lon()).toString());
		m_txt_latshow.setText(new Double(get_inhabitant().get_lat()).toString());
		m_txt_address.setText(get_inhabitant().get_postaddr());
		m_txt_house.setText(get_inhabitant().get_no());
		m_txt_letter.setText(get_inhabitant().get_letter());
		m_txt_place.setText(get_inhabitant().get_postarea());
		String sz_houseinfo = PAS.l("main_houseeditortab_newhouse");
		if(get_house()!=null && get_house().size()>=1)
		{
			int total_inhab = 0;
			for(HouseItem h : get_house())
			{
				total_inhab += h.get_inhabitantcount();
			}
			sz_houseinfo = PAS.l("main_houseeditortab_house_with") + " " + total_inhab + " " + PAS.l("common_adr_inhabitants");
		}
		m_lbl_houseinfo.setText(sz_houseinfo);
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_gablookup_complete".equals(e.getActionCommand())) {
			Inhabitant item = (Inhabitant)e.getSource();
			m_inhabitant = item;
			m_gislookup = item;
			init_values(item);
			fill_form(item);
		}
		else if("act_save".equals(e.getActionCommand())) {
			save();
		}
		else if("act_save_complete".equals(e.getActionCommand())) {
			WSHouseEditor.SaveGABResult result = (WSHouseEditor.SaveGABResult)e.getSource();
			String sz_kondmid = result.get_kondmid();
			if(result.is_success()) {
				try {
					//PAS.get_pas().get_housecontroller().start_download(false);
					get_inhabitant().set_kondmid(sz_kondmid);
					e.setSource(get_inhabitant());
					fill_form(null);
					m_callback.actionPerformed(e);
				} catch(Exception err) {
					System.out.println(err.getMessage());
					err.printStackTrace();
					Error.getError().addError("HouseEditorPanel","Exception in actionPerformed: act_save_complete",err,1);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Could not insert address, with error: " + result.get_errortext(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if("act_delete_inhabitant".equals(e.getActionCommand())) {
			Inhabitant i = (Inhabitant)e.getSource();
			m_inhabitant = i;
			if(JOptionPane.showConfirmDialog(this, PAS.l("common_delete_are_you_sure") + " " + i.get_adrname() + " ?", PAS.l("main_houseeditortab_delete_heading"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
				delete_inhabitant(i);
			}
		}
		else if("act_delete_inhabitant_complete".equals(e.getActionCommand())) {
			WSHouseEditor.SaveGABResult result = (WSHouseEditor.SaveGABResult)e.getSource();
			
			if(result.is_success()) {
				//JOptionPane.showMessageDialog(this, "Inhabitant was successfully deleted", "Inhabitant deleted", JOptionPane.INFORMATION_MESSAGE);
				//PAS.get_pas().get_housecontroller().start_download(false);
				e.setSource(get_inhabitant());
				m_callback.actionPerformed(e);
			}
			else
				JOptionPane.showMessageDialog(this, "Could not delete inhabitant with error: " + result.get_errortext(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void delete_inhabitant(Inhabitant i) {
		try {
			new WSHouseEditor(this, "act_delete_inhabitant_complete", m_inhabitant, HOUSEEDITOROPERATION.DELETE);
			/*String sz_operation = "delete";
			String sz_kondmid	= i.get_kondmid();*/

			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_gab_save.asp");
			form.setParameter("sz_operation", sz_operation);
			form.setParameter("sz_kondmid", sz_kondmid);
			form.setParameter("l_deptpk", sz_deptpk);
			new XMLSaveGAB(null, form, PAS.get_pas().get_sitename(), this, "act_delete_inhabitant_complete", null).start();*/
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("HouseEditorPanel","Exception in delete_inhabitant",e,1);
		}
	}
	
	protected boolean save() {
		try {
			if(m_txt_municipal.getText().length() == 0 || m_txt_municipal.getText().matches("[^0-9]"))
			{
				showErrorDialog(PAS.l("main_houseeditortab_municipal_required"));
				return false;
			}
			if(m_point == null) {
				showErrorDialog(PAS.l("main_houseeditortab_point_not_selected"));
				return false;
			}
			String sz_operation = "insert";
			String sz_kondmid = "-1";
			if(m_inhabitant != null) {
				sz_operation = "update";
				sz_kondmid = m_inhabitant.get_kondmid();
			}
			else {
				if(m_house != null && m_house.size()>=1)
					m_inhabitant = new Inhabitant(m_house.get(0).get_lon(), m_house.get(0).get_lat());
				else
					m_inhabitant = new Inhabitant(m_point.get_lon(), m_point.get_lat());	
			}
				
			

			//String sz_deptpk	= new Integer(PAS.get_pas().get_userinfo().get_default_deptpk()).toString();
			
			
			String sz_name = m_txt_name.getText();
			String sz_phone = m_txt_phone.getText();
			String sz_mobile = m_txt_mobile.getText();
			String sz_house = m_txt_house.getText();
			String sz_letter = m_txt_letter.getText();
			String sz_address = m_txt_address.getText(); //+ " " + sz_house + sz_letter;
			String sz_postno= m_txt_postno.getText();
			String sz_place = m_txt_place.getText();
			String sz_gnr = m_txt_gnr.getText();
			String sz_bnr = m_txt_bnr.getText();
			String sz_municipalid = m_txt_municipal.getText();
			String sz_birthday = m_txt_birthday.getText();
			//String sz_bedrift = ((Integer)((IconRadio)m_group_user.getSelection()).get_value()).toString();
            int n_adrtype;
			if(m_radio_private.isSelected()) {
                n_adrtype = Inhabitant.INHABITANT_PRIVATE;//SendController.SENDTO_FIXED_COMPANY;
			}
			else {
                n_adrtype = Inhabitant.INHABITANT_COMPANY; //SendController.SENDTO_FIXED_PRIVATE;
			}
			if(sz_gnr.length()==0)
				sz_gnr = "0";
			if(sz_bnr.length()==0)
				sz_bnr = "0";
			
			m_inhabitant.set_kondmid(sz_kondmid);
			m_inhabitant.set_adrname(sz_name);
			m_inhabitant.set_number(sz_phone);
			m_inhabitant.set_mobile(sz_mobile);
			m_inhabitant.set_no(sz_house);
			m_inhabitant.set_letter(sz_letter);
			m_inhabitant.set_postaddr(sz_address + " " + sz_house + (sz_letter.length()>0 ? sz_letter : ""));
			m_inhabitant.set_postno(sz_postno);
			m_inhabitant.set_postarea(sz_place);
			m_inhabitant.set_gnumber(new Integer(sz_gnr).intValue());
			m_inhabitant.set_bnumber(new Integer(sz_bnr).intValue());
			m_inhabitant.set_deptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			m_inhabitant.set_adrtype(n_adrtype);
			m_inhabitant.set_birthday_formatted(sz_birthday);
			m_inhabitant.set_region(sz_municipalid);
			
			sz_birthday = m_inhabitant.get_birthday();
			System.out.println(sz_birthday);

			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_gab_save.asp");
			form.setParameter("sz_operation", sz_operation);
			form.setParameter("sz_kondmid", sz_kondmid);
			form.setParameter("sz_name", sz_name);
			form.setParameter("sz_birthday", sz_birthday);
			form.setParameter("sz_phone", sz_phone);
			form.setParameter("sz_address", sz_address);
			form.setParameter("sz_house", sz_house);
			form.setParameter("sz_letter", sz_letter);
			form.setParameter("sz_postno", sz_postno);
			form.setParameter("sz_place", sz_place);
			form.setParameter("sz_gnr", sz_gnr);
			form.setParameter("sz_bnr", sz_bnr);
			form.setParameter("sz_municipalid", sz_municipalid);
			form.setParameter("sz_streetid", sz_streetid);
			form.setParameter("lon", lon.toString());
			form.setParameter("lat", lat.toString());
			form.setParameter("sz_bedrift", sz_bedrift);
			form.setParameter("sz_mobile", sz_mobile);
			
			new XMLSaveGAB(null, form, PAS.get_pas().get_sitename(), this, "act_save_complete", null).start();
			*/
			if(m_inhabitant.get_kondmid().equals(""))
				new WSHouseEditor(this, "act_save_complete", m_inhabitant, HOUSEEDITOROPERATION.INSERT);
			else
				new WSHouseEditor(this, "act_save_complete", m_inhabitant, HOUSEEDITOROPERATION.UPDATE);
			
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("HouseEditorPanel","Exception in save",e,1);
		}
		return false;
	}
	
	public void init() {
		m_btn_save.setActionCommand("act_save");
		m_btn_save.addActionListener(this);
		setVisible(true);
	}
	public void add_controls() {
		set_gridconst(0, get_panel(), 10, 1);
		add(m_lbl_houseinfo, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_radio_private, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_radio_company, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_name, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_name, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_phone, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_phone, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_mobile, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_mobile, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_birthday, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_birthday, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_address, m_gridconst);
		set_gridconst(5, get_panel(), 4, 1);
		add(m_txt_address, m_gridconst);
		set_gridconst(9, get_panel(), 1, 1);
		add(m_txt_house, m_gridconst);
		set_gridconst(10, get_panel(), 1, 1);
		add(m_txt_letter, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_place, m_gridconst);
		set_gridconst(5, get_panel(), 1, 1);
		add(m_txt_postno, m_gridconst);
		set_gridconst(6, get_panel(), 2, 1);
		add(m_txt_place, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_gnrbnr, m_gridconst);
		set_gridconst(5, get_panel(), 1, 1);
		add(m_txt_gnr, m_gridconst);
		set_gridconst(6, get_panel(), 1, 1);
		add(m_txt_bnr, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_municipal, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_municipal, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_streetid, m_gridconst);
		set_gridconst(5, get_panel(), 5, 1);
		add(m_txt_streetid, m_gridconst);
		
		inc_panels();
		set_gridconst(0, get_panel(), 5, 1);
		add(m_lbl_lonlat, m_gridconst);
		set_gridconst(5, get_panel(), 2, 1);
		add(m_txt_lonshow, m_gridconst);
		set_gridconst(7, get_panel(), 2, 1);
		add(m_txt_latshow, m_gridconst);

		inc_panels();
		set_gridconst(8, get_panel(), 2, 1);
		add(m_btn_save, m_gridconst);

		inc_panels();
		set_gridconst(0, get_panel(), 11, 1);
		add(m_inhablist, m_gridconst);
		
		
		init();
	}
	
	private void showErrorDialog(String errormsg) {
		JFrame frame = get_frame();
		JOptionPane.showMessageDialog(frame, errormsg, PAS.l("common_warning"), JOptionPane.WARNING_MESSAGE);
		frame.dispose();
	}
	
	private JFrame get_frame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		return frame;
	}
	
	public class HouseInhabitantsList extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		private ImageIcon m_icon_blank = null;
		private ImageIcon m_icon_delete = null;
		private ImageIcon m_icon_private = null;
		private ImageIcon m_icon_company = null;
		private int m_n_col_object = 1;
		private int m_n_col_delete = 7;
		
		public HouseInhabitantsList(String [] sz_columns, int [] n_width)  {
			super(sz_columns, n_width, null, new Dimension(400, 500), ListSelectionModel.SINGLE_SELECTION);
			m_icon_delete = ImageLoader.load_icon("delete_16.png");
			m_icon_blank = ImageLoader.load_icon("lock_16.png");
			m_icon_private= ImageLoader.load_icon("inhab_private.png");
			m_icon_company= ImageLoader.load_icon("inhab_company.png");
		}
		public void insert_row(Inhabitant inhab) {
			this.m_tbl.setRowHeight(24);
			Object icon = null;
			Object inhabtype = null;
			if(inhab==null)
				return;
			try {
				if(inhab.get_deptpk() > 0 && inhab.get_deptpk()==Variables.getUserInfo().get_current_department().get_deptpk()) 
					icon = m_icon_delete; 
				else 
					icon = m_icon_blank;
				//if((inhab.get_adrtype() & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY ||
				//	(inhab.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY)
				if((inhab.get_inhabitanttype() & Inhabitant.INHABITANT_COMPANY) == Inhabitant.INHABITANT_COMPANY)
					inhabtype = m_icon_company;
				else
					inhabtype = m_icon_private;
				Object data [] = new Object[] { inhabtype, inhab, inhab.get_postaddr(), inhab.get_postno(), inhab.get_postarea(), inhab.get_number(), inhab.get_mobile(), icon };
				super.insert_row(data, 0);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseEditorPanel","Exception in insert_row",e,1);
			}
		}
		public boolean is_cell_editable(int row, int col) {
			return false;
		}
		public void onMouseLClick(int row, int col, Object [] obj, Point p) {
			try {
				Inhabitant i = (Inhabitant)obj[m_n_col_object];
				if(col==m_n_col_delete && i.get_deptpk() == PAS.get_pas().get_userinfo().get_default_deptpk()) {
					actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_delete_inhabitant"));
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseEditorPanel","Exception in onMouseClick",e,1);
			}
		}
		public void onMouseLDblClick(int row, int col, Object [] obj, Point p) {
			try {
				Inhabitant i = (Inhabitant)obj[m_n_col_object];
				if(col!=m_n_col_delete) {
					fill_form(i);
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();	
				Error.getError().addError("HouseEditorPanel","Exception in onMouseLDblClick",e,1);
			}
		}
		public void onMouseRClick(int row, int col, Object [] obj, Point p) {
			
		}
		public void onMouseRDblClick(int row, int col, Object [] obj, Point p) {
			
		}
		public void start_search() {
			super.clear();
			//HouseItem h = get_house();
			if(get_house()==null)
				return;
			for(HouseItem h : get_house())
			{
				try {
					if(h!=null) {
						Inhabitant inhab;
						for(int i=0; i < h.get_inhabitants().size(); i++) {
							insert_row(h.get_itemfromhouse(i));
						}
					}
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("HouseEditorPanel","Exception in start_search",e,1);
				}
			}
		}
		public void valuesChanged() {
			
		}
	}
}