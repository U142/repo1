package no.ums.pas.core.mainui;

//import Core.WebData.*;
//import Core.WebData.XMLSaveGAB.SaveGABResult;

import no.ums.pas.PAS;
import no.ums.pas.core.controllers.Controller;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.ws.WSHouseByQuality;
import no.ums.pas.core.ws.WSHouseEditor;
import no.ums.pas.core.ws.WSHouseEditor.SaveGABResult;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.ArrayOfString;
import no.ums.ws.pas.HOUSEEDITOROPERATION;
import no.ums.ws.pas.UMapAddressParamsByQuality;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;



public class HouseMovePanel extends DefaultPanel implements ComponentListener, KeyListener {
	public static final long serialVersionUID = 1;
	private HouseEditorDlg m_dlg;
	private StdTextLabel m_lbl_heading = new StdTextLabel(PAS.l("main_houseeditortab_search_then_assign"), 400, 14, true);
	private StdTextLabel m_lbl_postno = new StdTextLabel(PAS.l("common_adr_postno") + ":", 70);
	private StdTextArea m_txt_postno = new StdTextArea("", false, 70);
	private JButton m_btn_search = new JButton(PAS.l("common_search"));
	private JCheckBox m_chk_private = new JCheckBox(PAS.l("common_adr_private"), true);
	private JCheckBox m_chk_company = new JCheckBox(PAS.l("common_adr_company"), true);
	private JCheckBox m_chk_mobile	= new JCheckBox(PAS.l("common_adr_mobile"), true);
	private JButton m_btn_export = new JButton(PAS.l("common_export"));
	private HouseInhabitantsList m_list;
	//private XMLHouses m_xml;
	//protected XMLHouses get_xml() { return m_xml; }
	private WSHouseByQuality m_xml;
	protected WSHouseByQuality get_xml() { return m_xml; }
	protected ArrayList<Inhabitant> m_inhabitants = null;
	private Inhabitant m_current_inhabitant = null;
	private boolean m_b_ready_for_coorinput = false;
	public boolean get_ready_for_coorinput() { return m_b_ready_for_coorinput; }
	private MapPoint m_current_position = null;
	
	public HouseMovePanel(HouseEditorDlg dlg) {
		super();
		m_dlg = dlg;
		m_list = new HouseInhabitantsList(new String [] { PAS.l("common_adr_quality"), "", PAS.l("common_adr_name"), PAS.l("common_adr_address"), PAS.l("common_adr_postno"), PAS.l("common_adr_postplace"), PAS.l("common_adr_phone"), PAS.l("common_adr_mobile") }, new int [] { 32, 16, 100, 100, 50, 100, 75, 75 } );
		add_controls();
		//m_txt_postno.grabFocus();
		m_txt_postno.addKeyListener(this);
		addComponentListener(this);
		m_chk_private.setActionCommand("act_show_private");
		m_chk_company.setActionCommand("act_show_company");
		m_chk_mobile.setActionCommand("act_show_mobile");
		m_btn_export.setActionCommand("act_export");
		m_chk_private.addActionListener(this);
		m_chk_company.addActionListener(this);
		m_chk_mobile.addActionListener(this);
		m_btn_export.addActionListener(this);
	}
	
	public void set_focus() {
		m_txt_postno.grabFocus();
		m_txt_postno.selectAll();
	}
	
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) {
		//System.out.println("COMPONENT RESIZED");
		//m_lbl_heading.setPreferredSize(new Dimension(getWidth(), m_lbl_heading.getHeight()));
		//m_list.setPreferredSize(new Dimension(getWidth(), m_list.getHeight()));
		//m_lbl_heading.setPreferredSize(new Dimension(getWidth()-100, getHeight()));
		
		//m_list.setPreferredSize(new Dimension(getWidth()-100, getHeight()-400));
		int height = getHeight();
		m_list.setPreferredSize(new Dimension(getWidth(), height-400));

	}
	public void keyTyped(KeyEvent e) { 
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ENTER)
			m_btn_search.doClick();
		
	}
	public void keyReleased(KeyEvent e) {
			
	}
	
	protected boolean checkInteger(String s) {
		try {
			int n_postno = Integer.parseInt(s);
			return true;
		} catch(Exception e) {
			System.out.println(PAS.l("error_not_a_number"));
			e.printStackTrace();
			Error.getError().addError("HouseMovePanel","Exception in checkInteger",e,1);
		}
		return false;		
	}
	protected boolean checkInteger(StdTextArea a) {
		return checkInteger(a.getText());
	}
	protected void lock(boolean b) {
		m_list.setEnabled(!b);
		m_list.m_tbl.setEnabled(!b);
		m_txt_postno.setEnabled(!b);
		m_btn_search.setEnabled(!b);
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_search_postno".equals(e.getActionCommand())) {
			String sz_postno = m_txt_postno.getText();
			if(!checkInteger(sz_postno)) {
				JOptionPane.showMessageDialog(this, PAS.l("error_not_a_number_info"), PAS.l("error_heading"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			String sz_quality = "E,F,G,H,I";
			m_list.setLoading(true);
			try {
				/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_gethouses_zipped.asp");
				form.setParameter("sz_filter1",  "byquality");
				form.setParameter("sz_filter2", sz_postno);
				form.setParameter("sz_filter3", sz_quality);*/
				//m_xml = new XMLHouses(Thread.MAX_PRIORITY, PAS.get_pas(), "PAS_gethouses_zipped.asp?l_companypk=" + PAS.get_pas().get_userinfo().get_comppk() + "&sz_filter1=byquality&sz_filter2=" + sz_postno + "&sz_filter3=" + sz_quality, null, new HTTPReq(PAS.get_pas().get_sitename())/*m_http_req*/, this);
				UMapAddressParamsByQuality params = new UMapAddressParamsByQuality();
				params.setSzPostno(sz_postno);
				ArrayOfString xycodes = new ArrayOfString();
				xycodes.getString().add("E");
				xycodes.getString().add("F");
				xycodes.getString().add("G");
				xycodes.getString().add("H");
				xycodes.getString().add("I");
				params.setArrXycodes(xycodes);
				m_xml = new WSHouseByQuality(this, "act_download_finished", params);
				lock(true);
				//m_xml = new XMLHouses(form, null, this);
				//get_xml().start();
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("HouseMovePanel","Exception in actionPerformed: act_search_postno",err,1);
				lock(false);
			}
		}
		else if("act_download_finished".equals(e.getActionCommand())) {
			lock(false);
			m_inhabitants = (ArrayList<Inhabitant>)get_xml().get_items().clone();
			m_list.start_search();
			m_list.setLoading(false);
		}
		else if("act_ready_for_coor_assignment".equals(e.getActionCommand())) {
			//change mapmode to COOR_ASSIGNMENT
			m_b_ready_for_coorinput = true;
			m_current_inhabitant = (Inhabitant)e.getSource();
			//e.setSource(e.);
			m_dlg.actionPerformed(e);
		}
		else if("act_coor_assignment".equals(e.getActionCommand())) {
			//save
			m_current_position  = (MapPoint)e.getSource();
			/*String sz_operation = "copy";
			String sz_kondmid	= m_current_inhabitant.get_kondmid();
			String sz_deptpk	= new Integer(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()).toString();
				
			try {
				HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_gab_save.asp");
				form.setParameter("sz_operation", sz_operation);
				form.setParameter("sz_kondmid", sz_kondmid);
				form.setParameter("l_deptpk", sz_deptpk);
				form.setParameter("lon", new Double(m_current_position.get_lon()).toString());
				form.setParameter("lat", new Double(m_current_position.get_lat()).toString());
				new XMLSaveGAB(null, form, PAS.get_pas().get_sitename(), this, "act_coor_assignment_complete", null).start();
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("HouseMovePanel","Exception in actionPerformed: act_coor_assignment",err,1);
			}*/
			Inhabitant inhab = new Inhabitant();
			inhab.set_kondmid(m_current_inhabitant.get_kondmid());
			inhab.set_deptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			inhab.set_lon(m_current_position.get_lon());
			inhab.set_lat(m_current_position.get_lat());
			new WSHouseEditor(this, "act_coor_assignment_complete", inhab, HOUSEEDITOROPERATION.UPDATE);
		}
		else if("act_coor_assignment_complete".equals(e.getActionCommand())) {
			m_b_ready_for_coorinput = false;
			SaveGABResult gab = (SaveGABResult)e.getSource();
			if(gab.is_success()) {
				Inhabitant i = m_current_inhabitant;
				m_list.delete_row(i);
				i.set_kondmid(gab.get_kondmid());
				i.set_lon(m_current_position.get_lon());
				i.set_lat(m_current_position.get_lat());
				m_dlg.actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_save_complete"));
			}
			else
				JOptionPane.showMessageDialog(this, "Failed to copy record to new location, with error " + gab.get_errortext(), "Error", JOptionPane.WARNING_MESSAGE);
		}
		else if("act_show_private".equals(e.getActionCommand())) {
			JCheckBox c = (JCheckBox)e.getSource();
			m_list.add_show_adrtype(Inhabitant.INHABITANT_PRIVATE, c.isSelected()); //SendController.SENDTO_FIXED_PRIVATE, c.isSelected());
		}
		else if("act_show_company".equals(e.getActionCommand())) {
			JCheckBox c = (JCheckBox)e.getSource();
			m_list.add_show_adrtype(Inhabitant.INHABITANT_COMPANY, c.isSelected()); //SendController.SENDTO_FIXED_COMPANY, c.isSelected());
		}
		else if("act_show_mobile".equals(e.getActionCommand())) {
			JCheckBox c = (JCheckBox)e.getSource();
			m_list.add_show_adrtype(SendController.SENDTO_MOBILE_COMPANY | SendController.SENDTO_MOBILE_PRIVATE, c.isSelected());
		}
		else if("act_export".equals(e.getActionCommand())) {
			m_list.export(true);
		}
	}
	public void set_point(MapPoint p) {
		if(get_ready_for_coorinput()) {
			//m_current_position = p;
			actionPerformed(new ActionEvent(p, ActionEvent.ACTION_PERFORMED, "act_coor_assignment"));
		}
	}
	public void add_controls() {
		m_btn_search.setPreferredSize(new Dimension(80, 16));
		m_btn_search.setActionCommand("act_search_postno");
		m_btn_search.addActionListener(this);
		
		set_gridconst(0, inc_panels(), 6, 1, java.awt.GridBagConstraints.CENTER);
		add(m_lbl_heading, m_gridconst);
		super.add_spacing(DefaultPanel.DIR_VERTICAL, 20);
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_postno, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_postno, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
		add(m_btn_search, m_gridconst);
		set_gridconst(3, get_panel(), 1, 1);
		add(m_chk_private, m_gridconst);
		set_gridconst(4, get_panel(), 1, 1);
		add(m_chk_company, m_gridconst);
		//set_gridconst(5, get_panel(), 1, 1);
		//add(m_chk_mobile, m_gridconst);
		set_gridconst(0, inc_panels(), 6, 1);
		add(m_list, m_gridconst);
		set_gridconst(0, inc_panels(), 6, 1);
		add(m_btn_export, m_gridconst);
		
		init();
	}
	public void init() {
		setVisible(true);
	}
	
	public class HouseInhabitantsList extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		//private ImageIcon m_icon_blank = null;
		//private ImageIcon m_icon_delete = null;
		private ImageIcon m_icon_private = null;
		private ImageIcon m_icon_company = null;
		public static final int m_n_col_object = 2;
		private int m_n_include_adrtypes = Controller.ADR_TYPES_SHOW_ALL_;
		
		public void add_show_adrtype(int ID, boolean b) {
			if(b)
				m_n_include_adrtypes |= ID;
			else
				m_n_include_adrtypes &= ~ID;
			start_search();
		}
		//private int m_n_col_delete = 8;
		
		public HouseInhabitantsList(String [] sz_columns, int [] n_width)  {
			super(sz_columns, n_width, null, new Dimension(300, 480), ListSelectionModel.SINGLE_SELECTION);
			//m_icon_delete = ImageLoader.load_icon("no.gif");
			//m_icon_blank = ImageLoader.load_icon("lock.gif");
			m_icon_private= ImageLoader.load_icon("inhab_private.png");
			m_icon_company= ImageLoader.load_icon("inhab_company.png");
		}
		public void insert_row(Inhabitant inhab) {
			Object icon = null;
			Object inhabtype = null;
			try {
				//if(inhab.get_deptpk() > 0) icon = m_icon_delete; else icon = m_icon_blank;
				/*if((inhab.get_adrtype() & SendController.SENDTO_FIXED_COMPANY) == SendController.SENDTO_FIXED_COMPANY ||
					(inhab.get_adrtype() & SendController.SENDTO_MOBILE_COMPANY) == SendController.SENDTO_MOBILE_COMPANY ||
					(inhab.get_adrtype() & SendController.SENDTO_NOPHONE_COMPANY) == SendController.SENDTO_NOPHONE_COMPANY)*/
				if(inhab.get_inhabitanttype()==Inhabitant.INHABITANT_COMPANY)
					inhabtype = m_icon_company;
				else
					inhabtype = m_icon_private;
				Object data [] = new Object[] { String.valueOf(inhab.get_quality()), inhabtype, inhab, inhab.get_postaddr(), inhab.get_postno(), inhab.get_postarea(), inhab.get_number(), inhab.get_mobile() };
				super.insert_row(data, 0);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseMovePanel","Exception in insert_row",e,1);
			}
		}
		public void delete_row(Object o) {
			try {
				//super.remove(find(o, m_n_col_object));
				delete_row(o, m_n_col_object, Inhabitant.class);
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseMovePanel","Exception in delete_row",e,1);
			}
		}
		public boolean is_cell_editable(int row, int col) {
			return false;
		}
		public void onMouseLClick(int row, int col, Object [] obj, Point p) {
			/*try {
				Inhabitant i = (Inhabitant)obj[m_n_col_object];
				if(col==m_n_col_delete && i.get_deptpk() == PAS.get_pas().get_userinfo().get_default_deptpk()) {
					actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_delete_inhabitant"));
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}*/
			onMouseLDblClick(row, col, obj, p);
		}
		public void onMouseLDblClick(int row, int col, Object [] obj, Point p) {
			try {
				Inhabitant i = (Inhabitant)obj[m_n_col_object];
				//if(col!=m_n_col_delete) {
					//fill_form(i);
					actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_ready_for_coor_assignment"));
				//}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HouseMovePanel","Exception in onMouseLDblClick",e,1);
			}
		}
		public void onMouseRClick(int row, int col, Object [] obj, Point p) {
			
		}
		public void onMouseRDblClick(int row, int col, Object [] obj, Point p) {
			
		}
		public void start_search() {
			super.clear();
			for(int i=0; i < m_inhabitants.size(); i++) {
				try {
					Inhabitant inhab = (Inhabitant)m_inhabitants.get(i);
					//if((inhab.get_adrtype() & this.m_n_include_adrtypes) == inhab.get_adrtype())
					if(inhab.get_inhabitanttype()==Inhabitant.INHABITANT_PRIVATE && m_chk_private.isSelected() ||
						inhab.get_inhabitanttype()==Inhabitant.INHABITANT_COMPANY && m_chk_company.isSelected())
						insert_row(inhab);
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("HouseMovePanel","Exception in start_search",e,1);
				}
			}
		}
		public void valuesChanged() {
			
		}
	}
}