package no.ums.pas.core.logon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListModel;
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

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.maps.defines.MapSite;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.SubstanceLookAndFeel;
import java.util.*;


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
		super(PAS.l("mainmenu_settings"));
		try
		{
			this.setIconImage(PAS.get_pas().getIconImage());
		}
		catch(Exception e)
		{
			
		}
		try
		{
			this.setAlwaysOnTop(true);
		}
		catch(Exception e)
		{
			
		}

		m_settings_layout = new SettingsLayout(parent);
		
		m_lbl_error = new StdTextLabel("", 200);
		m_lbl_error.setForeground(Color.RED);
		
		m_lbl_username = new StdTextLabel(PAS.l("logon_userid"),100, 12 ,true);
		m_txt_username = new StdTextArea("", false, 175);
		
		m_lbl_company = new StdTextLabel(PAS.l("logon_company"),100,12,true);
		m_txt_company = new StdTextArea("", false, 175);
		
		m_lbl_load_on_start = new StdTextLabel("Load on start:",100,12,true);
		
		m_lbl_wms_username = new StdTextLabel("WMS Username", 100, 10, true);
		m_lbl_wms_password = new StdTextLabel("WMS Password", 100, 10, true);
		m_txt_wms_username = new StdTextArea("", false, 100);
		m_txt_wms_password = new JPasswordField("");
		Dimension d = new Dimension(100, 15);
		m_txt_wms_password.setPreferredSize(d);
		m_txt_wms_username.setPreferredSize(d);
		
		m_lbl_start_parm = new StdTextLabel(PAS.l("mainmenu_parm"),100);
		m_chk_start_parm = new JCheckBox();
		
		m_lbl_mapsite = new StdTextLabel("Map Site", 100,12,true);
		m_btn_default = new JRadioButton("Default");
		m_btn_wms = new JRadioButton("WMS");
		m_txt_wms_site = new StdTextArea("", false, 320);
		m_group_mapsite = new ButtonGroup();
		m_combo_wmsformat = new JComboBox();
		m_group_mapsite.add(m_btn_default);
		m_group_mapsite.add(m_btn_wms);
		m_btn_default.addActionListener(this);
		m_btn_wms.addActionListener(this);
		m_btn_openwms = new JButton(PAS.l("common_open"));
		m_btn_openwms.addActionListener(this);
		
		//m_model = new DefaultTreeModel(null);
		m_wms_tree = new WmsLayerTree(new DefaultTreeModel(null));
		
		
		
		m_btn_pan_by_click = new JRadioButton("Pan by click");
		m_btn_pan_by_drag = new JRadioButton("Pan by drag");
		//m_btn_pan_by_click.addActionListener(this);
		//m_btn_pan_by_drag.addActionListener(this);
		m_group_pan = new ButtonGroup();
		m_group_pan.add(m_btn_pan_by_click);
		m_group_pan.add(m_btn_pan_by_drag);
		
		String [] cols = new String [] {"", "Layer", "" };
		int width [] = new int [] { 30, 270, 0 };
		boolean [] editable = new boolean [] { true, false, false };
		//m_wms_list = new WmsLayerList(cols, width, editable, new Dimension(320, 100));
		
		
		//m_lbl_start_fleetcontrol = new StdTextLabel("Fleet control",100);
		//m_chk_start_fleetcontrol = new JCheckBox();
		
		m_lbl_mail_displayname = new StdTextLabel("Displayname:",100,12,true);
		m_txt_mail_displayname = new StdTextArea("", false, 175);
		
		m_lbl_mail_address = new StdTextLabel("Mail address:",100,12,true);
		m_txt_mail_address = new StdTextArea("", false, 175);
		
		m_lbl_mail_outgoing = new StdTextLabel("Outgoing mailserver(SMTP):",160,12,true);
		m_txt_mail_outgoing = new StdTextArea("", false, 175);
		
		m_lbl_lba_refresh = new StdTextLabel("LBA map update percentage:", 170,12,true);
		m_txt_lba_refresh = new StdTextArea("", false, 175);
		
		m_btn_cancel = new JButton(PAS.l("common_cancel"));
		m_btn_save = new JButton(PAS.l("common_save"));
		
		m_btn_cancel.addActionListener(this);
		m_btn_save.addActionListener(this);
		
		m_settings_layout.add_controls();
		c.add(m_settings_layout);
		
		setSize(450,650);
	  Dimension dim = getToolkit().getScreenSize();
	  super.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(450, 600));
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
				variables.NAVIGATION.reloadMap();
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
				
				//m_wms_list.clear();
				String current_url = PAS.get_pas().get_mappane().getMapLoader().getWmsUrl();
				String new_url = getM_txt_wms_site().getText();
				boolean b_new_url = false;
				if(!current_url.equals(new_url))
					b_new_url = true;
				PAS.get_pas().get_mappane().getMapLoader().testWmsUrl(new_url, m_txt_wms_username.getText(), m_txt_wms_password.getPassword());
				List<Layer> layers = PAS.get_pas().get_mappane().getMapLoader().getCapabilitiesTest().getLayerList();
				Settings s = PAS.get_pas().get_settings();
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
				PAS.pasplugin.onWmsLayerListLoaded(layers, s.getSelectedWmsLayers());

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
			actionPerformed(new ActionEvent(m_btn_openwms, ActionEvent.ACTION_PERFORMED, ""));

	}
	
	public class WmsLayerTree extends JTree
	{
		List<Layer> m_layers;
		protected DefaultTreeModel m_model;

		public WmsLayerTree(TreeModel model)
		{
			super(model);
			m_model = (DefaultTreeModel)model;
			setCellRenderer(new LayerRenderer());
			setCellEditor(new CheckBoxNodeEditor(this));
		}
		public ArrayList<String> getSelectedLayers()
		{
			
			TreePath path = new TreePath(((DefaultMutableTreeNode)getModel().getRoot()).getPath());
			ArrayList<String> ret = new ArrayList<String>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
	        //select(node, b);
	        if (node.getChildCount() > 0) {
	            java.util.Enumeration<DefaultMutableTreeNode> e = node.children();
	            while(e.hasMoreElements()) {
	                DefaultMutableTreeNode n = e.nextElement();
	                if(n.getUserObject() instanceof LayerCheckBoxNode)
	                {
	                	LayerCheckBoxNode chk = (LayerCheckBoxNode)n.getUserObject();
	                	if(chk.selected)
	                		ret.add(chk.layer.getName());
	                }
	                //select(n, b);
	                //selectAllChildren(path.pathByAddingChild(n), b);
	            }
	        }
			return ret;
		}
	    public void selectAllChildren(TreePath path, boolean b) {
	        TreeNode node = (TreeNode)path.getLastPathComponent();
	        select(node, b);
	        if (node.getChildCount() > 0) {
	            java.util.Enumeration<TreeNode> e = node.children();
	            while(e.hasMoreElements()) {
	                TreeNode n = e.nextElement();
	                select(n, b);
	                selectAllChildren(path.pathByAddingChild(n), b);
	            }
	        }
	    }
	    private void select(TreeNode node, boolean b) {
	        DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
	        if(n.getUserObject() instanceof CheckBoxNode)
	        {
	        	CheckBoxNode chk = (CheckBoxNode)n.getUserObject();
	        	chk.selected = b;
	        }
	    }
		public void populate(List<Layer> layers, ArrayList<String> check, boolean b_new_url /*check none*/, ActionListener callback)
		{
			this.setEditable(true);
			boolean b_topnode_set = false;
			m_layers = layers;
			Hashtable<Layer, DefaultMutableTreeNode> hash = new Hashtable<Layer, DefaultMutableTreeNode>();
			DefaultMutableTreeNode node;
			DefaultMutableTreeNode topnode = null;
			Layer toplayer = null;
			//m_model.setRoot(new DefaultMutableTreeNode(layers.get(0)));
			for(int i=0; i < layers.size(); i++)
			{
				if(m_layers.get(i)!=null && m_layers.get(i).getParent()==null)
				{
					toplayer = layers.get(i);
					//LayerCheckBoxNode chk = new LayerCheckBoxNode(toplayer.getName(), false, toplayer);
					topnode = new DefaultMutableTreeNode(new DefaultMutableTreeNode(toplayer));
					m_model.setRoot(topnode);
					hash.put(layers.get(i), topnode);
					b_topnode_set = true;
					break;
				}
			}
			if(!b_topnode_set)
			{
				toplayer = new Layer("Top node");
				//LayerCheckBoxNode chk = new LayerCheckBoxNode(toplayer.getName(), false, toplayer);
				topnode = new DefaultMutableTreeNode(toplayer);
				hash.put(toplayer, topnode);
				m_model.setRoot(topnode);
			}
			
			for(int i=0; i < layers.size(); i++)
			{
				Layer currentlayer = layers.get(i);
				Layer parentlayer = layers.get(i).getParent();
				if(hash.containsKey(currentlayer))
					continue;
				DefaultMutableTreeNode parent = hash.get(parentlayer);
				if(parent==null)
					parent = topnode;

				Boolean b = new Boolean(false);
				if(b_new_url)
					b = new Boolean(true);
				else if(check.contains(layers.get(i).getName()))
					b = new Boolean(true);

				LayerCheckBoxNode chk = new LayerCheckBoxNode(currentlayer.getName(), b.booleanValue(), currentlayer, callback);
				node = new DefaultMutableTreeNode(chk);
				m_model.insertNodeInto(node, parent, 0);
				
				hash.put(currentlayer, node);
			}
			TreePath path = new TreePath(topnode.getPath());
			
			this.expandPath(path);
			//selectAllChildren(path, false);

			
		}
		
		class LayerCheckBoxNode extends CheckBoxNode {
			Layer layer;
			ActionListener callback;
			public LayerCheckBoxNode(String text, boolean selected, Layer layer, ActionListener callback)
			{
				super(text, selected);
				this.layer = layer;
				this.callback = callback;
			}
		}
		class CheckBoxNode {
			  String text;

			  boolean selected;

			  public CheckBoxNode(String text, boolean selected) {
			    this.text = text;
			    this.selected = selected;
			  }

			  public boolean isSelected() {
			    return selected;
			  }

			  public void setSelected(boolean newValue) {
			    selected = newValue;
			  }

			  public String getText() {
			    return text;
			  }

			  public void setText(String newValue) {
			    text = newValue;
			  }

			  public String toString() {
			    return getClass().getName() + "[" + text + "/" + selected + "]";
			  }
		}
		protected class LayerCheckBox extends JCheckBox
		{
			public Layer layer;
		}
		protected class LayerRenderer implements TreeCellRenderer
		{
			private LayerCheckBox checkboxrenderer = new LayerCheckBox();
			private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			public LayerCheckBox getCheckRenderer() { return checkboxrenderer; }
			public DefaultTreeCellRenderer getNormalRenderer() { return renderer; }
			Color selectionBorderColor, selectionForeground, selectionBackground,
		      textForeground, textBackground;
			
			public LayerRenderer()
			{
				selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
			    selectionForeground = UIManager.getColor("Tree.selectionForeground");
			    selectionBackground = UIManager.getColor("Tree.selectionBackground");
			    textForeground = UIManager.getColor("Tree.textForeground");
			    textBackground = UIManager.getColor("Tree.textBackground");
			}
			public Component getTreeCellRendererComponent(
					JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {
		        checkboxrenderer.setOpaque(false);
				
				Component returnValue = null;
		      if ((value != null) && (value instanceof DefaultMutableTreeNode)) 
		      {
		    	  DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) value;
		          Object userObject = ((DefaultMutableTreeNode) value)
		              .getUserObject();
		          if (userObject instanceof CheckBoxNode) 
		          {
			            LayerCheckBoxNode node = (LayerCheckBoxNode) userObject;
			            if(treenode.getChildCount()==0 && treenode.getParent().equals(tree.getModel().getRoot()) || 
			            		treenode.getChildCount()>0 && treenode.getParent().equals(tree.getModel().getRoot()))
		        	  {
			            checkboxrenderer.setText(node.getText());
			            checkboxrenderer.setSelected(node.isSelected());
			            checkboxrenderer.layer = node.layer;
					      returnValue = checkboxrenderer;
		        	  }
		        	  else
		        	  {
		        		  renderer.setText(node.getText());
		        		  returnValue = renderer;
		        	  }
		          }
		          else if (userObject instanceof DefaultMutableTreeNode)
		          {
		        	  Object userObject2 = ((DefaultMutableTreeNode) userObject)
		              .getUserObject();
		        	  if(userObject2 instanceof Layer)
		        	  {
			        	  Layer node = (Layer) userObject2;
			        	  renderer.setText(node.getTitle());
			        	  returnValue = renderer;
		        	  }
		          }
		          else
				        returnValue = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);	
		          
			      if (sel) {
			    	  returnValue.setForeground(selectionForeground);
			    	  returnValue.setBackground(selectionBackground);
			        } else {
			        	returnValue.setForeground(textForeground);
			        	returnValue.setBackground(textBackground);
			        }
	        	  if(treenode.isRoot())
	        	  {
	        		  //Substance 3.3
	        		  returnValue.setBackground(SubstanceLookAndFeel.getActiveColorScheme().getDarkColor());
	        		  
	        		  //Substance 5.2
	        		  //returnValue.setBackground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getDarkColor());
	        	  }
		          
		      }
		      else {
		        returnValue = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		      }	
		        return returnValue;
			}
		}
		
		class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

			LayerRenderer renderer = new LayerRenderer();

			  ChangeEvent changeEvent = null;

			  JTree tree;

			  public CheckBoxNodeEditor(JTree tree) {
			    this.tree = tree;
			  }

			  public Object getCellEditorValue() {
			    LayerCheckBox checkbox = renderer.getCheckRenderer();
			    LayerCheckBoxNode checkBoxNode = new LayerCheckBoxNode(checkbox.getText(),
			        checkbox.isSelected(), checkbox.layer, null);
			    return checkBoxNode;
			  }

			  public boolean isCellEditable(EventObject event) {
			    boolean returnValue = false;
			    if (event instanceof MouseEvent) {
			      MouseEvent mouseEvent = (MouseEvent) event;
			      TreePath path = tree.getPathForLocation(mouseEvent.getX(),
			          mouseEvent.getY());
			      if (path != null) {
			        Object node = path.getLastPathComponent();
			        if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
			          DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
			          Object userObject = treeNode.getUserObject();
			          //returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
			          //returnValue = ((!treeNode.isRoot()) && userObject instanceof CheckBoxNode);
			          //if((!treeNode.isRoot()) && userObject instanceof CheckBoxNode)
			        	//  return true
			          if(treeNode.isRoot())
			        	  return false;
			          if(treeNode.getChildCount()==0 && treeNode.getParent().equals(tree.getModel().getRoot()))
			        	  return true;
			          if(treeNode.getChildCount()>0 && treeNode.getParent().equals(tree.getModel().getRoot()))
			        	  return true;
			          
			        		  
			        }
			      }
			    }
			    return returnValue;
			  }

			  public Component getTreeCellEditorComponent(JTree tree, Object value,
			      boolean selected, boolean expanded, boolean leaf, int row) {

			    Component editor = renderer.getTreeCellRendererComponent(tree, value,
			        true, expanded, leaf, row, true);

			    // editor always selected / focused
			    ItemListener itemListener = new ItemListener() {
			      public void itemStateChanged(ItemEvent itemEvent) {
			        if (stopCellEditing()) {
			          fireEditingStopped();
			        }
			      }
			    };
			    if (editor instanceof JCheckBox) {
			      ((JCheckBox) editor).addItemListener(itemListener);
			    }

			    return editor;
			  }
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
