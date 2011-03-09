package no.ums.pas.core.logon;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import org.geotools.data.ows.Layer;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;


public class SettingsGUI extends JFrame implements ActionListener {
	public static final long serialVersionUID = 1;

	Container c = getContentPane();
	
	private Settings settings;
	private SettingsLayout m_settings_layout;
	
	private StdTextLabel m_lbl_error;
	
	private StdTextLabel m_lbl_username;
	private StdTextArea m_txt_username;
	
	private StdTextLabel m_lbl_company;
	private StdTextArea m_txt_company;

	private StdTextLabel m_lbl_load_on_start;
	
	private StdTextLabel m_lbl_start_parm;
	private JCheckBox m_chk_start_parm;
	
	private StdTextLabel m_lbl_mapsite;
	private JRadioButton m_btn_default;
	private JRadioButton m_btn_wms;
	public JRadioButton getWmsRadioBtn() { return m_btn_wms; }
	private ButtonGroup m_group_mapsite;
	private StdTextArea m_txt_wms_site;
	private JButton m_btn_openwms;
	private JScrollPane m_wms_scroll;
	private WmsLayerList m_wms_list;
	private JComboBox m_combo_wmsformat;
	private StdTextLabel m_lbl_wms_username;
	private StdTextLabel m_lbl_wms_password;
	private StdTextArea m_txt_wms_username;
	private JPasswordField m_txt_wms_password;
	
	protected WmsLayerTree m_wms_tree;
	public WmsLayerTree getWmsLayerTree() { return m_wms_tree; }

	
	private JRadioButton m_btn_pan_by_click;
	private JRadioButton m_btn_pan_by_drag;
	private ButtonGroup m_group_pan;
	
	//private StdTextLabel m_lbl_start_fleetcontrol;
	//private JCheckBox m_chk_start_fleetcontrol;
	
	private StdTextLabel m_lbl_mail_displayname;
	private StdTextArea m_txt_mail_displayname;
	
	private StdTextLabel m_lbl_mail_outgoing;
	private StdTextArea m_txt_mail_outgoing;
	
	private StdTextLabel m_lbl_mail_address;
	private StdTextArea m_txt_mail_address;
	
	private StdTextLabel m_lbl_lba_refresh;
	private StdTextArea m_txt_lba_refresh;
	
	private JButton m_btn_save;
	private JButton m_btn_cancel;
	
	public SettingsGUI() {
		super("NONE");
	}
	
	public SettingsGUI(PAS parent) {
		super(Localization.l("mainmenu_settings"));
		try
		{
			this.setIconImage(PAS.get_pas().getIconImage());
		}
		catch(Exception e)
		{
			
		}
		//new SettingsGUI2(this).dlg.setVisible(true);
		try
		{
			this.setAlwaysOnTop(true);
		}
		catch(Exception e)
		{
			
		}

		m_settings_layout = new SettingsLayout(parent);
		
		m_lbl_error = new StdTextLabel("", 300);
		m_lbl_error.setForeground(Color.RED);
		
		m_lbl_username = new StdTextLabel(Localization.l("logon_userid"),100, 12 ,true);
		m_txt_username = new StdTextArea("", false, 175);
		
		m_lbl_company = new StdTextLabel(Localization.l("logon_company"),100,12,true);
		m_txt_company = new StdTextArea("", false, 175);
		
		m_lbl_load_on_start = new StdTextLabel(PAS.l("main_pas_settings_loadonstart_heading"),100,12,true);
		
		m_lbl_wms_username = new StdTextLabel(PAS.l("main_pas_settings_mapsite_wms_username"), 100, 10, true);
		m_lbl_wms_password = new StdTextLabel(PAS.l("main_pas_settings_mapsite_wms_password"), 100, 10, true);
		m_txt_wms_username = new StdTextArea("", false, 100);
		m_txt_wms_password = new JPasswordField("");
		Dimension d = new Dimension(100, 15);
		m_txt_wms_password.setPreferredSize(d);
		m_txt_wms_username.setPreferredSize(d);
		
		m_lbl_start_parm = new StdTextLabel(Localization.l("mainmenu_parm"),100);
		m_chk_start_parm = new JCheckBox();
		
		m_lbl_mapsite = new StdTextLabel(PAS.l("main_pas_settings_map_heading"), 100,12,true);
		m_btn_default = new JRadioButton(PAS.l("main_pas_settings_mapsite_default"));
		m_btn_wms = new JRadioButton(PAS.l("main_pas_settings_mapsite_wms"));
		m_txt_wms_site = new StdTextArea("", false, 350);
		m_group_mapsite = new ButtonGroup();
		m_combo_wmsformat = new JComboBox();
		m_group_mapsite.add(m_btn_default);
		m_group_mapsite.add(m_btn_wms);
		m_btn_default.addActionListener(this);
		m_btn_wms.addActionListener(this);
		m_btn_openwms = new JButton(Localization.l("common_open"));
		m_btn_openwms.addActionListener(this);
		
		//m_model = new DefaultTreeModel(null);
		m_wms_tree = new WmsLayerTree(new DefaultTreeModel(null));
		
		
		
		m_btn_pan_by_click = new JRadioButton(PAS.l("main_pas_settings_pan_by_click"));
		m_btn_pan_by_drag = new JRadioButton(PAS.l("main_pas_settings_pan_by_drag"));
		//m_btn_pan_by_click.addActionListener(this);
		//m_btn_pan_by_drag.addActionListener(this);
		m_group_pan = new ButtonGroup();
		m_group_pan.add(m_btn_pan_by_click);
		m_group_pan.add(m_btn_pan_by_drag);
		
		
		m_lbl_mail_displayname = new StdTextLabel(PAS.l("main_pas_settings_email_displayname"),185,12,true);
		m_txt_mail_displayname = new StdTextArea("", false, 175);
		
		m_lbl_mail_address = new StdTextLabel(PAS.l("main_pas_settings_email_address"),185,12,true);
		m_txt_mail_address = new StdTextArea("", false, 175);
		
		m_lbl_mail_outgoing = new StdTextLabel(PAS.l("main_pas_settings_email_server"),185,12,true);
		m_txt_mail_outgoing = new StdTextArea("", false, 175);
		
		m_lbl_lba_refresh = new StdTextLabel(PAS.l("main_pas_settings_auto_lba_update"), 185,12,true);
		m_txt_lba_refresh = new StdTextArea("", false, 175);
		
		m_btn_cancel = new JButton(Localization.l("common_cancel"));
		m_btn_save = new JButton(Localization.l("common_save"));
		
		m_btn_cancel.addActionListener(this);
		m_btn_save.addActionListener(this);
		
		m_settings_layout.add_controls();
		c.add(m_settings_layout);
		
		setSize(470,680);
	  Dimension dim = getToolkit().getScreenSize();
	  super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(470, 680));
	  //super.setLocationRelativeTo(null);

		setVisible(true);
		
	}
	class SettingsLayout extends DefaultPanel {
		public static final long serialVersionUID = 1;

		public SettingsLayout(PAS pas) {
			super();
		}

		public void add_controls() {
			int n_width = 10;
			_add(m_lbl_error, 0, get_panel(), n_width, 1);
			_add(m_lbl_username, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_username, n_width/2, get_panel(), n_width/2, 1);
			_add(m_lbl_company, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_company, n_width/2, get_panel(), n_width/2, 1);
			_add(m_lbl_load_on_start, 0, inc_panels(), n_width/2, 1);
			inc_panels();
			_add(m_lbl_start_parm, 0, inc_panels(), n_width/2, 1);
			//_add(m_lbl_start_fleetcontrol, n_width/2, get_panel(), n_width/2, 1);
			_add(m_chk_start_parm, 0, inc_panels(), n_width/2, 1);
			//_add(m_chk_start_fleetcontrol, n_width/2, get_panel(), n_width/2, 1);
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
				m_lbl_load_on_start.setVisible(false);
				m_lbl_start_parm.setVisible(false);
				m_chk_start_parm.setVisible(false);
			}
			
			add_spacing(DIR_VERTICAL, 10);
			_add(m_lbl_mapsite, 0, inc_panels(), n_width/2, 1);
			_add(m_btn_default, 0, inc_panels(), n_width/2, 1);
			_add(m_btn_wms, n_width/2, get_panel(), n_width/2, 1);
			_add(m_txt_wms_site, 0, inc_panels(), n_width, 1);
			_add(m_btn_openwms, n_width, get_panel(), 1, 1);
			
			_add(m_lbl_wms_username, 0, inc_panels(), 3, 1);
			_add(m_txt_wms_username, 3, get_panel(), 3, 1);
			_add(m_lbl_wms_password, 0, inc_panels(), 1, 1);
			_add(m_txt_wms_password, 3, get_panel(), 3, 1);
			
			add_spacing(DIR_VERTICAL, 10);
			_add(m_combo_wmsformat, 0, inc_panels(), n_width, 1);
			//_add(m_wms_list, 0, inc_panels(), n_width, 1);
			add_spacing(DIR_VERTICAL, 10);
			
			JScrollPane scroll = new JScrollPane(m_wms_tree);
			scroll.setPreferredSize(new Dimension(350, 200));
			_add(scroll, 0, inc_panels(), n_width, 1);
			add_spacing(DIR_VERTICAL, 10);
			
			_add(m_btn_pan_by_click, 0, inc_panels(), n_width/2, 1);
			_add(m_btn_pan_by_drag, n_width/2, get_panel(), n_width/2, 1);
			add_spacing(DIR_VERTICAL, 10);
			
			_add(m_lbl_mail_displayname, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_mail_displayname, n_width/2, get_panel(), n_width/2, 1);
			_add(m_lbl_mail_address, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_mail_address, n_width/2, get_panel(), n_width/2, 1);
			_add(m_lbl_mail_outgoing, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_mail_outgoing, n_width/2, get_panel(), n_width/2, 1);
			add_spacing(DIR_VERTICAL,10);
			_add(m_lbl_lba_refresh, 0, inc_panels(), n_width/2, 1);
			_add(m_txt_lba_refresh, n_width/2, get_panel(), n_width/2, 1);
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
				m_lbl_lba_refresh.setVisible(false);
				m_txt_lba_refresh.setVisible(false);
			}
			add_spacing(DIR_VERTICAL, 10);
			_add(m_btn_cancel, 0, inc_panels(), n_width/2, 1);
			_add(m_btn_save, n_width/2, get_panel(), n_width/2, 1);
			init();
		}
		
		protected final void _add(Component c, int x, int y, int w, int h) {
			set_gridconst(x, y, w, h, GridBagConstraints.WEST);
			add(c, m_gridconst);
		}

		public void init() {
			this.doLayout();
		}

		public void actionPerformed(ActionEvent e) {
			
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_btn_save) {
			boolean valid = true;
			String errormsg = "";
			//Validate
			if(m_txt_mail_address.getText().length() > 1 & valid) {
				if(!m_txt_mail_address.getText().contains("@"))
					valid = false;
				if(!m_txt_mail_address.getText().contains("."))
					valid = false;
				if(!valid)
					errormsg = "E-mail address not valid";
			}
			if(m_txt_mail_outgoing.getText().length() > 1 & valid) {
				int periods = 0;
				for(int i=0;i<m_txt_mail_outgoing.getText().length();i++) {
					i = m_txt_mail_outgoing.getText().indexOf(".",i);
					if(i > -1)
						periods++;
					else
						break;
				}
				if(periods < 2) {
					valid = false;
					errormsg = "Outgoing servername not valid";
				}
			}
			if(m_txt_mail_address.getText().length() > 0 && m_txt_mail_outgoing.getText().length() > 0 & valid) {
				if(m_txt_mail_displayname.getText().length() == 0) {
					valid = false;
					errormsg = "Please enter display name";
				}
			}
			try {
				Integer.parseInt(m_txt_lba_refresh.getText());
				if(Integer.parseInt(m_txt_lba_refresh.getText())>100 || Integer.parseInt(m_txt_lba_refresh.getText()) < 0)
				{
					valid= false;
					errormsg = "LBA refresh has to be between 0 and 100";
					m_txt_lba_refresh.requestFocus();
				}
			}catch (NumberFormatException nfe) {
				valid= false;
				errormsg = "LBA refresh is not a number";
				m_txt_lba_refresh.requestFocus();
			}
			if(valid) {
				MAPSERVER mapserver = MAPSERVER.DEFAULT;
				if(m_btn_wms.isSelected())
					mapserver = MAPSERVER.WMS;
				ArrayList<String> selected_layers = null;
				String selected_format = "image/png";
				if(mapserver==MAPSERVER.WMS && m_wms_tree.getModel().getRoot()!=null) // m_wms_list.m_tbl_list.getRowCount() > 0)
				{
					/*selected_layers = new java.util.ArrayList<String>();
					for(int i=0; i < m_wms_list.m_tbl_list.getRowCount(); i++)
					{
						Boolean b = (Boolean)m_wms_list.m_tbl_list.getValueAt(i, 0);
						if(b)
						{
							selected_layers.add(((Layer)m_wms_list.m_tbl_list.getValueAt(i, 2)).getName());
						}
					}*/
					selected_layers = m_wms_tree.getSelectedLayers();
				}
				else //keep the old ones
				{
					selected_layers = PAS.get_pas().get_settings().getSelectedWmsLayers();
				}
				if(mapserver==MAPSERVER.WMS && m_combo_wmsformat.getItemCount() > 0)
				{
					selected_format = m_combo_wmsformat.getSelectedItem().toString();
					if(selected_format.length()<=0)
					{
						Error.getError().addError("Select an image format", "", new Exception("Select desired image format for downloading maps"), Error.SEVERITY_WARNING);
						return;
					}
					
				}
				else
				{
					selected_format = PAS.get_pas().get_settings().getSelectedWmsFormat();
				}
				String sz_language = PAS.get_pas().get_settings().getLanguage();
				
				String s = String.copyValueOf(m_txt_wms_password.getPassword());
				settings = new Settings(m_txt_username.getText(), m_txt_company.getText(),
						m_chk_start_parm.isSelected(),/*m_chk_start_fleetcontrol.isSelected()*/false, Integer.parseInt(m_txt_lba_refresh.getText()),
						mapserver, m_txt_wms_site.getText(), selected_layers, selected_format, (m_btn_pan_by_drag.isSelected() ? true : false), sz_language,
						m_txt_wms_username.getText(), s);
				MailAccount mail = PAS.get_pas().get_userinfo().get_mailaccount();
				mail.set_displayname(m_txt_mail_displayname.getText());
				mail.set_mailaddress(m_txt_mail_address.getText());
				mail.set_mailserver(m_txt_mail_outgoing.getText());
				ActionEvent ae = new ActionEvent(settings,ActionEvent.ACTION_PERFORMED,"act_save_settingsobject");
				PAS.get_pas().actionPerformed(ae);
				//PAS.get_pas().get_mappane().load_map();
				Variables.getNavigation().reloadMap();
				this.dispose();
			} else
				m_lbl_error.setText(errormsg);
		}
		else if(e.getSource() == m_btn_wms)
		{
			m_txt_wms_site.setEnabled(true);
			m_combo_wmsformat.setEnabled(true);
			m_txt_wms_username.setEnabled(true);
			m_txt_wms_password.setEnabled(true);
			m_btn_openwms.setEnabled(true);
			if(m_txt_wms_site.getText().length()==0)
			{
				m_txt_wms_site.setText("http://");
			}
		}
		else if(e.getSource() == m_btn_default)
		{
			m_txt_wms_site.setEnabled(false);			
			m_combo_wmsformat.setEnabled(false);
			m_txt_wms_username.setEnabled(false);
			m_txt_wms_password.setEnabled(false);
			m_btn_openwms.setEnabled(false);
		}
		else if(e.getSource() == m_btn_openwms)
		{
			try
			{
				new SwingWorker<DefaultTreeModel, List<Layer>>() {
					List<Layer> layers;
					Settings s = PAS.get_pas().get_settings();
					
					@Override
					protected DefaultTreeModel doInBackground() throws Exception {
						String current_url = PAS.get_pas().get_mappane().getMapLoader().getWmsUrl();
						String new_url = getM_txt_wms_site().getText();
						boolean b_new_url = false;
						if(!current_url.equals(new_url))
							b_new_url = true;
						PAS.get_pas().get_mappane().getMapLoader().testWmsUrl(new_url, m_txt_wms_username.getText(), m_txt_wms_password.getPassword());
						layers = PAS.get_pas().get_mappane().getMapLoader().getCapabilitiesTest().getLayerList();
						
						m_combo_wmsformat.removeAllItems();
						List<String> formats = PAS.get_pas().get_mappane().getMapLoader().m_wms_formats;
						int select_index = 0;
						for(int i=0; i < formats.size(); i++)
						{
							m_combo_wmsformat.addItem(formats.get(i));
							if(s.getSelectedWmsFormat().equals(formats.get(i)))
								select_index = i;
						}
						m_combo_wmsformat.setSelectedIndex(select_index);
						
						m_wms_tree.populate(layers, s.getSelectedWmsLayers(), b_new_url, null);
						return m_wms_tree.getModel();
					}

					@Override
					protected void done() {
						super.done();
						PAS.pasplugin.onWmsLayerListLoaded(layers, s.getSelectedWmsLayers());
					}
					
				}.execute();
				//m_wms_list.clear();

			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
		else {
			this.dispose();
		}
	}
	/*public JCheckBox getM_chk_start_fleetcontrol() {
		return m_chk_start_fleetcontrol;
	}*/
	public JCheckBox getM_chk_start_parm() {
		return m_chk_start_parm;
	}
	public StdTextArea getM_txt_company() {
		return m_txt_company;
	}
	public StdTextArea getM_txt_username() {
		return m_txt_username;
	}
	public StdTextArea getM_txt_mail_address() {
		return m_txt_mail_address;
	}
	public StdTextArea getM_txt_mail_displayname() {
		return m_txt_mail_displayname;
	}
	public StdTextArea getM_txt_mail_outgoing() {
		return m_txt_mail_outgoing;
	}
	public StdTextArea getM_txt_lba_refresh() {
		return m_txt_lba_refresh;
	}
	public JRadioButton getM_btn_wms() { 
		return m_btn_wms;
	}
	public JRadioButton getM_btn_default() {
		return m_btn_default;
	}
	public StdTextArea getM_txt_wms_site() {
		return m_txt_wms_site;
	}
	public JRadioButton getM_btn_pan_by_click() {
		 return m_btn_pan_by_click;
	}
	public JRadioButton getM_btn_pan_by_drag() {
		return m_btn_pan_by_drag;
	}
	
	public void setMapServer(MAPSERVER m) {
		
		switch(m)
		{
		case WMS:
			m_btn_wms.doClick();
			break;
		case DEFAULT:
		default:
			m_btn_default.doClick();
			break;
		}
	}
	public void setWmsUser(String s)
	{
		m_txt_wms_username.setText(s);
	}
	public void setWmsPassword(String s)
	{
		m_txt_wms_password.setText(s);
	}
	public void setWmsSite(String s) {
		m_txt_wms_site.setText(s);
		if(s.length()>0 && !s.equals("http://"))
		{
			actionPerformed(new ActionEvent(m_btn_openwms, ActionEvent.ACTION_PERFORMED, ""));
		}			

	}
	

	
	public class WmsLayerList extends SearchPanelResults
	{
		public WmsLayerList(String [] cols, int [] width, boolean [] editable, Dimension dim)
		{
			super(cols, width, editable, dim);
		}

		@Override
		public boolean is_cell_editable(int row, int col) {
			return super.is_cell_editable(col);
		}

		@Override
		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseLDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseRDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void start_search() {
			
		}

		@Override
		protected void valuesChanged() {
			
		}
		
	}

}
