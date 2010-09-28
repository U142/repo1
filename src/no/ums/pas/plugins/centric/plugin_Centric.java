package no.ums.pas.plugins.centric;

import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.maps.defines.PLMNShape;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.plugins.centric.send.CentricProjectDlg;
import no.ums.pas.plugins.centric.status.CentricStatus;
import no.ums.pas.plugins.centric.status.CentricStatusController;

import javax.swing.*;
import javax.xml.ws.soap.SOAPFaultException;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.*;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import no.ums.pas.*;

import java.awt.*;

import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import no.ums.pas.send.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.versioning.versioning;
import no.ums.pas.core.variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.GeneralPanel;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.*;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.themes.UMSTheme;
import no.ums.pas.core.themes.UMSTheme.THEMETYPE;
import no.ums.pas.core.ws.WSPowerup;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.WMSLayerSelectorPanel;
import no.ums.pas.maps.defines.*;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.pas.ArrayOfUBBNEWS;
import no.ums.ws.pas.UBBNEWS;
import no.ums.ws.pas.USYSTEMMESSAGES;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;


public class plugin_Centric extends PAS_Scripting
{
	WMSLayerSelectorPanel wms_layer_selector = new WMSLayerSelectorPanel();

	int CURRENT_TAB = CentricEastContent.PANEL_CENTRICSEND_;

	public plugin_Centric()
	{
		super();
		System.out.println("plugin_Centric loaded");
	}
	
	@Override
	protected void setSubPluginNames() {
		System.out.println("***Using Plugins (plugin_Centric)***");
		System.out.println((this.plugin_AddressSearch = "no.ums.pas.plugins.centric.CentricAddressSearch"));
	}

	@Override
	public boolean onBeforeLogon()
	{
		super.onBeforeLogon();
		boolean b = new DisclaimerDialog().isConfirmed();
		if(!b)
		{
			System.out.println("User denied Disclaimer");
			System.exit(0);
		}
		System.out.println("User accepted Disclaimer");
		return b;
	}
	
	
	@Override
	public boolean onAfterLogon()
	{
		super.onAfterLogon();
		try
		{
			//SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.SaharaSkin");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public boolean onShowMainWindow()
	{
		super.onShowMainWindow();
		/*new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						Thread.sleep(10000);
						System.out.println("TRALLALA");
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}.start();*/
		return true;
	}
	
	private JButton menu_btn_draw_polygon;
	private JButton menu_btn_draw_ellipse;
	private JButton menu_btn_draw_plmn;
	private JButton menu_btn_import;
	
	@Override
	public boolean onAddMainMenuButtons(MainMenu menu)
	{
		menu.set_gridconst(menu.inc_xpanels(), 0, 15, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_selectmenu().get_bar(), menu.m_gridconst);

		menu.reset_xpanels();
		
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_pan(), menu.m_gridconst);
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_zoom(), menu.m_gridconst);
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_search(), menu.m_gridconst);
		menu.get_btn_search().setEnabled(false); //IDDIATTS
		
		JButton btn_goto_restriction = new JButton(PAS.l("common_navigate_home"));
		btn_goto_restriction.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		btn_goto_restriction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				PAS.get_pas().actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).getFullBBox(),ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			}
		});
		
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(btn_goto_restriction, menu.m_gridconst);

		menu.add_spacing(DefaultPanel.DIR_HORIZONTAL, 30);
		
		menu_btn_draw_polygon = new JButton(PAS.l("main_sending_type_polygon"));
		menu_btn_draw_polygon.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		menu_btn_draw_polygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				variables.MAPPANE.set_active_shape(new PolygonStruct(ShapeStruct.DETAILMODE.SHOW_POLYGON_FULL, 100000.0));
				variables.MAPPANE.set_mode(MapFrame.MAP_MODE_SENDING_POLY);
				PAS.get_pas().repaint();
			}
		});
		
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu_btn_draw_polygon, menu.m_gridconst);

		menu_btn_draw_ellipse = new JButton(PAS.l("main_sending_type_ellipse"));
		menu_btn_draw_ellipse.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		menu_btn_draw_ellipse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				variables.MAPPANE.set_active_shape(new PolygonStruct(ShapeStruct.DETAILMODE.SHOW_POLYGON_FULL, 100000.0));
				variables.MAPPANE.set_mode(MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON);
				PAS.get_pas().repaint();
			}
		});
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu_btn_draw_ellipse, menu.m_gridconst);

		menu_btn_draw_plmn = new JButton(PAS.l("main_sending_type_national"));
		menu_btn_draw_plmn.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		menu_btn_draw_plmn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				variables.MAPPANE.set_mode(MapFrame.MAP_MODE_PAN);
				variables.MAPPANE.set_active_shape(new PLMNShape());
				PAS.get_pas().repaint();
			}
		});
		menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu_btn_draw_plmn, menu.m_gridconst);
		enableSendButtons(true);
		
		/*menu_btn_import = new JButton(PAS.l("common_import"));
		menu_btn_import.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
		menu_btn_import.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource().equals(menu_btn_import))
				{
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							ImportPolygon i = new ImportPolygon(null, "act_polygon_imported", false, null);
						}
					});
				}
				else if(e.getActionCommand().equals("act_polygon_imported"))
				{
				
				}
				else if(e.getActionCommand().equals("act_set_shape"))
				{
					
				}
			}			
		});
		//menu.set_gridconst(6, 1, 1, 1, GridBagConstraints.NORTHWEST);
		//menu.add(menu_btn_import, menu.m_gridconst);*/
		
		return true;
	}
	
	@Override
	public boolean onAddMainSelectMenu(MainMenuBar menu)
	{
		super.onAddMainSelectMenu(menu);
		menu.remove(menu.get_menu_navigate());
		menu.remove(menu.get_dept());
		menu.remove(menu.get_menu_layout());
		menu.remove(menu.get_parm());
		menu.remove(menu.get_status());
		menu.remove(menu.get_menu_config());
		menu.remove(menu.get_view());
		menu.get_menu_file().remove(menu.get_item_new_sending());
		menu.get_menu_file().remove(menu.get_item_file_print_map());
		menu.get_menu_file().remove(menu.get_item_file_save_map());
		menu.get_menu_file().remove(menu.get_item_fileimport());
		menu.get_status().remove(menu.get_item_status_export());
		menu.get_status().remove(menu.get_item_status_updates());
		menu.get_view().remove(menu.get_item_view_showhouses());
		
		//menu.get_item_view_showhouses().setSelected(false);
		
		//menu.get_item_address_book().setMaximumSize(new Dimension(menu.get_item_address_book().getPreferredSize().width, menu.get_item_address_book().getMaximumSize().height));
		//menu.get_item_training_mode().setMaximumSize(new Dimension(menu.get_item_training_mode().getPreferredSize().width, menu.get_item_training_mode().getMaximumSize().height));
		JMenu menu_addressbook, menu_trainingmode;
		menu.add((menu_addressbook = new JMenu(PAS.l("common_address_book"))));
		menu.add((menu_trainingmode= new JMenu(PAS.l("mainmenu_trainingmode"))));
		
		menu_addressbook.add(menu.get_item_address_book());
		menu.get_item_address_book().setEnabled(false);
		menu.get_item_view_showhouses().setSelected(false);
		menu_trainingmode.add(menu.get_item_training_mode());
		
		
		menu.add(menu.get_menu_help());
		

		//menu.set_show_houses_invoke(false);
		return true;
	}
	
	@Override 
	public boolean onAddSendOptionToolbar(SendOptionToolbar toolbar)
	{
		//CentricSendOptionToolbar ctoolbar = new CentricSendOptionToolbar(new SendObject(PAS.get_pas(),PAS.get_pas().get_pasactionlistener()),PAS.get_pas().get_pasactionlistener(),toolbar.get_sendingid());
		toolbar.show_buttons(
				SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_|
				SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_|
				SendOptionToolbar.BTN_OPEN_|
				SendOptionToolbar.TXT_RECIPIENTTYPES_|
				SendOptionToolbar.BTN_ADRTYPES_NOFAX_, 
				true);
		return super.onAddSendOptionToolbar(toolbar);
	}
	
	
	
	@Override
	protected boolean onHandleSystemMessages(USYSTEMMESSAGES sysmsg) {
		final List<UBBNEWS> news = sysmsg.getNews().getNewslist().getUBBNEWS();
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				for(int i=0; i < news.size(); i++)
				{
					UBBNEWS bbnews = news.get(i);
					//systemmessagepanel.list.getDefaultModel().addOnTop(bbnews);
					systemmessagepanel.list.getDefaultModel().handleMessage(bbnews);
				}
				systemmessagepanel.list.getDefaultModel().sort();
			}
		});
		return true;				
	}



	class SystemMessagesPanel extends DefaultPanel implements ComponentListener{
		
		class UMSListModel extends DefaultListModel
		{
			public void sort()
			{
				Object [] list = this.toArray();
				if(list.length<=1)
					return;
				UBBNEWS tmp;
				for(int i=0; i < list.length; i++)
				{
					UBBNEWS b1 = (UBBNEWS)list[i];
					for(int j=i+1; j < list.length; j++)
					{
						UBBNEWS b2 = (UBBNEWS)list[j];
						//if(b1.getLTimestampDb()<b2.getLTimestampDb())
						boolean b_doswitch = false;
						if(b1.getLIncidentStart()<b2.getLIncidentStart())
							b_doswitch = true;
						else if(b1.getLIncidentStart()==b2.getLIncidentStart())
						{
							if(b1.getLTimestampDb()<b2.getLTimestampDb())
								b_doswitch = true;
						}
						if(b_doswitch)
						{
							tmp = b1;
							list[i] = b2;
							list[j] = tmp;
							i=i-1;
							break;
						}
					}
				}
				for(int i=0; i < list.length; i++)
				{
					UBBNEWS bbn = (UBBNEWS)list[i];
					this.setElementAt(bbn, i);
					//System.out.println("setElementAt " + i + " " + bbn.getLNewspk());
					recordset.put(genHashKey(bbn), bbn);
				}
			}
			Hashtable<Long, Object> recordset = new Hashtable<Long, Object>();
			
			public void handleMessage(UBBNEWS b)
			{
				if(b.getFActive()>=1) //insert/update
				{
					//System.out.println("newspk="+b.getLNewspk());
					if(recordset.containsKey(genHashKey(b)))
					{
						update(b);
					}
					else
					{
						add(0, b);//addOnTop(b);
					}
				}
				else //remove
				{
					if(recordset.containsKey(genHashKey(b)))
					{
						remove(b);
					}
				}
			}
			protected long genHashKey(UBBNEWS b)
			{
				return b.getLNewspk();
			}
			protected void addOnTop(Object arg1) {
				
				UBBNEWS news = (UBBNEWS)arg1;
				if(news.getFActive()>=1)
					this.add(0, arg1);
			}
			
			protected void remove(UBBNEWS b)
			{
				UBBNEWS original = (UBBNEWS)recordset.get(genHashKey(b));
				int index = super.indexOf(original);
				if(index>=0)
				{
					super.remove(index);
					if(recordset.containsKey(genHashKey(b)))
					{
						recordset.remove(genHashKey(b));
						System.out.println("newspk " + b.getLNewspk() + " removed");
					}
				}
			}
			protected void update(UBBNEWS b)
			{
				UBBNEWS original = (UBBNEWS)recordset.get(genHashKey(b));
				int index = super.indexOf(original);
				if(index>=0)
				{
					super.set(index, b);
					System.out.println("newspk " + b.getLNewspk() + " updated");
					recordset.put(genHashKey(b), b);
				}
			}
			
			@Override
			public void add(int arg0, Object arg1) {
				Long key = ((UBBNEWS)arg1).getLNewspk();
				/*if(recordset.containsKey(key))
				{
					UBBNEWS original = (UBBNEWS)recordset.get(key);
					int n = super.indexOf(original);
					if(n!=-1)
					{
						UBBNEWS news = (UBBNEWS)arg1;
						if(news.getFActive()>=1)
						{
							super.set(n, news);
							recordset.put(((UBBNEWS)arg1).getLNewspk(), arg1);
							System.out.println("newspk " + original.getLNewspk() + " updated");
						}
						else
						{
							//to be deleted
							super.remove(n);
							recordset.remove(original.getLNewspk());
							System.out.println("newspk " + original.getLNewspk() + " deleted");
						}
					}
					else
					{
						System.out.println("news " + original + " not found in list");
					}
				}
				else*/
				{
					recordset.put(genHashKey((UBBNEWS)arg1), arg1);
					//list.getDefaultModel().add(arg0, arg1);
					super.add(arg0, arg1);
					System.out.println("newspk " + ((UBBNEWS)arg1).getLNewspk() + " inserted");
				}
			}

			/*@Override
			public void addElement(Object arg0) {
				//super.addElement(arg0);
				add(0, arg0);
			}*/
			
		}
		class MessageList extends JList
		{
			MessageListRenderer renderer = new MessageListRenderer();
			MessageList()
			{
				super(new UMSListModel());
				setCellRenderer(renderer);
				setVisibleRowCount(1);
				//setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
			}
			UMSListModel getDefaultModel() { return (UMSListModel)this.getModel(); }
			class MessageListRenderer extends DefaultPanel implements ListCellRenderer
			{
				protected JLabel lbl_renderer;
				MessageListRenderer()
				{
					super();
					lbl_renderer = new JLabel("");
				}
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					int width = MessageList.this.getWidth();
					
					int scrollwidth = 0;
					if(scrollpane.getVerticalScrollBar().isVisible())
						scrollwidth = scrollpane.getVerticalScrollBar().getWidth();
					width -= scrollwidth;
					if(value.getClass().equals(String[].class))
					{
						
						String [] vals = (String[])value;
						lbl_renderer.setText(vals[0] + "    " + vals[1]);
						//cols[0].setText(vals[0]);
						//cols[1].setText(vals[1]);
					}
					else if(value.getClass().equals(UBBNEWS.class))
					{
						UBBNEWS news = (UBBNEWS)value;
						//lbl_renderer.setText(no.ums.pas.ums.tools.TextFormat.format_datetime(news.getLTimestampDb()) + "    " + news.getNewstext().getSzNews());
						String text_to_write = news.getNewstext().getSzNews();
						int text_width = lbl_renderer.getFontMetrics(lbl_renderer.getFont()).stringWidth(text_to_write);
						//if(text_width>=width)
						//	text_to_write = text_to_write.substring(0, Math.min(text_to_write.length()-1, 70));
						lbl_renderer.setText(text_to_write);
						lbl_renderer.setPreferredSize(new Dimension(width, getHeight()));
					}
					else
					{
						lbl_renderer.setText("NA");
						//cols[0].setText("None");
						//cols[1].setText("None");
					}
					return lbl_renderer;
					//return super.getListCellRendererComponent(list, value, index, isSelected,
					//		cellHasFocus);
				}
				@Override
				public void actionPerformed(ActionEvent e) {					
				}
				@Override
				public void add_controls() {
				}
				@Override
				public void init() {
				}
				
			}
			@Override
			public String getToolTipText(MouseEvent arg0) {
				Point p = arg0.getPoint();
				int location = locationToIndex(p);
				if(location>=0)
				{
					UBBNEWS b = (UBBNEWS)list.getDefaultModel().getElementAt(location);
					String html = "<html><table width=300>";
					//html += "<tr><td colspan=1><b>" + PAS.l("common_updated") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLTimestampDb()) + "</td></tr>";
					html += "<tr><td colspan=1><b>" + PAS.l("common_start") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLIncidentStart()) + "</td></tr>";
					html += "<tr><td colspan=1><b>" + PAS.l("common_end") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLIncidentEnd()) + "</td></tr>";
					html += "<tr><td colspan=2 style=\"word-wrap: break-word\">" + b.getNewstext().getSzNews() + "</td></tr>";
					html += "</html>";
					return html;
				}
				return "";
			}
		}
		int n_current_height;
		int n_max = 100;
		int n_min = 25;
		boolean expanded = false;
		MessageList list;
		JScrollPane scrollpane;
		JButton btn_expand = new JButton("v");
		SystemMessagesPanel()
		{
			super();
			n_current_height = n_min;
			list = new MessageList();
			list.setEnabled(false);
			scrollpane = new JScrollPane(list);
			scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			Font f = new Font(UIManager.getString("Common.Fontface"), Font.PLAIN, 14);
			list.setFont(f);
			int height = list.getFontMetrics(f).getHeight();
			n_current_height = height;
			n_min = n_current_height;
			
			btn_expand.addActionListener(this);
			addComponentListener(this);
			add_controls();
		}
		@Override
		public int getWantedHeight() {
			return n_current_height;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btn_expand))
			{
				//expand button clicked
				expanded = !expanded;
				if(expanded)
					n_current_height = n_max;
				else
				{
					n_current_height = n_min;
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						SystemMessagesPanel.this.setPreferredSize(new Dimension(getWidth(), n_current_height));
						revalidate();
					}
				});				
			}
		}

		@Override
		public void add_controls() {
			get_gridconst().fill = GridBagConstraints.BOTH;
			set_gridconst(0, 0, 1, 1);
			add(scrollpane, m_gridconst);
			get_gridconst().fill = GridBagConstraints.HORIZONTAL;
			set_gridconst(1, 0, 1, 1);
			add(btn_expand, m_gridconst);
		}

		@Override
		public void init() {
			
		}
		@Override
		public void componentResized(ComponentEvent e) {
			
			int set_height = n_current_height;
			n_min=22;
			list.setFixedCellHeight(n_min);
			int w = getWidth();
			int btn_size = getWantedHeight();
			scrollpane.setPreferredSize(new Dimension(getWidth()-n_min, getHeight()));
			btn_expand.setPreferredSize(new Dimension(n_min, getHeight()));
			int scroll_width = 0;
			int scroll_height = 0;
			if(this.scrollpane.getVerticalScrollBar().isVisible())
			{
				scroll_width = this.scrollpane.getVerticalScrollBar().getWidth();
			}
			if(this.scrollpane.getHorizontalScrollBar().isVisible())
			{
				scroll_height = this.scrollpane.getHorizontalScrollBar().getHeight();
				//set_height = n_current_height+scroll_height;
			}
			scroll_width-=5;
			SystemMessagesPanel.this.setPreferredSize(new Dimension(getWidth(), set_height));
			scrollpane.revalidate();
			revalidate();

			super.componentResized(e);
		}
	}
	
	class UserInfoPane extends DefaultPanel implements ComponentListener
	{
		@Override
		public int getWantedHeight() {
			return 25;
		}

		StdTextLabel lbl_userinfo = new StdTextLabel("");
		UserInfoPane()
		{
			super();
			setLayout(new BorderLayout());
			//setPreferredSize(new Dimension(10,getWantedHeight()));
			//setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
			lbl_userinfo.setBackground(Color.white);
			lbl_userinfo.setVerticalTextPosition(JLabel.CENTER);
			//lbl_userinfo.setHorizontalAlignment(SwingConstants.LEFT);
			addComponentListener(this);
			add_controls();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
		
		public void updateUserInfo(UserInfo ui)
		{
			String str = " " + ui.get_realname();
			/*switch(ui.get_departments().size())
			{
			case 1:
				str+=PAS.l("logon_rights_regional_user") + " - ";//" - $Regional user - ";
				break;
			default:
				str+=PAS.l("logon_rights_regional_superuser") + " - "; //" - $Regional Super User - ";
				break;
			}*/
			str += " - ";
			int member_of_dept = ui.get_departments().size();
			switch(ui.get_current_department().get_userprofile().get_send())
			{
			case 1: //regional or regional super user
				if(member_of_dept==1)
					str+=PAS.l("logon_rights_regional_user") + " - ";//" - $Regional user - ";
				else
					str+=PAS.l("logon_rights_regional_superuser") + " - "; //" - $Regional Super User - ";					
				for(int i = 0; i < ui.get_departments().size(); i++)
					str += " \"" + ((DeptInfo)ui.get_departments().get(i)).get_deptid() + "\"";
				break;
			case 2: //national user
				str+=PAS.l("logon_rights_national_user"); //" - $Regional Super User - ";					
				break;				
			}
			
			lbl_userinfo.setText(str);
		}

		@Override
		public void add_controls() {
			//set_gridconst(0, 0, 1, 1);
			//add(lbl_userinfo, m_gridconst);
			add(lbl_userinfo);
		}

		@Override
		public void init() {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			int w = getWidth();
			int h = getHeight();
			//if(w<=0 || h<=0)
			//	return;
			//if(w>5000 || h>5000)
			//	return;
			int x = w;
			lbl_userinfo.setPreferredSize(new Dimension(w, getWantedHeight()));
			lbl_userinfo.revalidate();
			super.componentResized(e);
		}
		
	}
	
	class Northpane extends DefaultPanel implements ComponentListener
	{
		Northpane()
		{
			super();
			addComponentListener(this);
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void add_controls() {
		}

		@Override
		public void init() {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			int w = getWidth();
			int h = getHeight();
			/*for(int i=0; i < getComponentCount(); i++)
			{
				getComponent(i).setPreferredSize(new Dimension(w,h/2));
			}*/
			PAS.get_pas().get_mainmenu().setPreferredSize(new Dimension(w, h));
		}
	}
	
	class CenterPane extends DefaultPanel implements ComponentListener
	{
		CenterPane()
		{
			super();
			setLayout(new BorderLayout());
			addComponentListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void add_controls() {
		}

		@Override
		public void init() {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			//super.componentResized(e);
			DefaultPanel p = (DefaultPanel)e.getComponent();
			int w = p.getWidth();
			int h = p.getHeight();
			int sysmsg_height = systemmessagepanel.getWantedHeight();
			int userinfo_height = userinfopane.getWantedHeight();
			PAS.get_pas().get_mappane().set_dimension(new Dimension(w, h-sysmsg_height-userinfo_height));
			systemmessagepanel.setPreferredSize(new Dimension(w, sysmsg_height));
			userinfopane.setPreferredSize(new Dimension(w, userinfo_height));
			//PAS.get_pas().get_mappane().revalidate();
			systemmessagepanel.revalidate();
			userinfopane.revalidate();
			//PAS.get_pas().applyResize();
		}
		
	}
	
	
	SystemMessagesPanel systemmessagepanel = new SystemMessagesPanel();
	Northpane northpane = new Northpane();
	CenterPane centerpane = new CenterPane();
	UserInfoPane userinfopane = new UserInfoPane();
	
	@Override
	public boolean onAddPASComponents(final PAS p)
	{
		
		System.out.println("onAddPASComponents");
		
		/*centerpane.set_gridconst(0, centerpane.inc_panels(), 1, 1, GridBagConstraints.CENTER);
		centerpane.add(systemmessagepanel, centerpane.get_gridconst());
		
		centerpane.set_gridconst(0, centerpane.inc_panels(), 1, 1, GridBagConstraints.CENTER);
		centerpane.add(PAS.get_pas().get_mappane(), centerpane.get_gridconst());
		
		centerpane.set_gridconst(0, centerpane.inc_panels(), 1, 1, GridBagConstraints.CENTER);
		centerpane.add(userinfopane, centerpane.get_gridconst());*/
		
		centerpane.add(systemmessagepanel, BorderLayout.NORTH);
		centerpane.add(PAS.get_pas().get_mappane(), BorderLayout.CENTER);
		centerpane.add(userinfopane, BorderLayout.SOUTH);
		
		p.getContentPane().add(centerpane, BorderLayout.CENTER);
		//p.getContentPane().add(p.get_mappane(), BorderLayout.CENTER);
		
		
		//p.add(p.get_mainmenu(), BorderLayout.NORTH);
		northpane.set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
		northpane.add(p.get_mainmenu(), northpane.get_gridconst());
		//northpane.set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTH);
		//northpane.add(systemmessagepanel, northpane.get_gridconst());
		p.getContentPane().add(northpane, BorderLayout.NORTH);
		
		p.getContentPane().add(p.get_southcontent(), BorderLayout.SOUTH);
		p.getContentPane().add(p.get_eastcontent(), BorderLayout.EAST);

		
		//p.get_mappane().add(wms_layer_selector, BorderLayout.WEST);
		//wms_layer_selector.setVisible(false);
		
		return true;
	}
	

	@Override
	public boolean onFrameResize(JFrame f, ComponentEvent e) {
		//northpane.setPreferredSize(new Dimension(f.getWidth(), 52));
		//centerpane.setPreferredSize(new Dimension(f.getWidth(), f.getHeight()));
		return super.onFrameResize(f, e);
	}

	@Override
	public boolean onSetInitialMapBounds(Navigation nav, UserInfo ui)
	{
		nav.setNavigation(ui.get_departments().get_combined_restriction_shape().get(0).getFullBBox());
		return true;
	}
	
	@Override 
	public boolean onStartParm()
	{
		//return super.onStartParm();
		System.out.println("onStartParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onCloseParm()
	{
		//return super.onCloseParm();
		System.out.println("onCloseParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		//return super.onRefreshParm();
		System.out.println("onRefreshParm - PARM is invalid in this plugin");
		return false;
	}
	
	@Override
	public boolean onDepartmentChanged(PAS pas)
	{
		super.onDepartmentChanged(pas);
		userinfopane.updateUserInfo(pas.get_userinfo());
		//PAS.get_pas().setAppTitle("UMS/Centric - " + pas.get_userinfo().get_current_department().get_deptid());
		return true;
	}
	
	@Override
	public boolean onSetAppTitle(PAS pas, String s, UserInfo userinfo)
	{
		boolean trainingmode = IsInTrainingMode(userinfo);
		System.out.println("onSetAppTitle");
		String maintitle = PAS.l("common_app_title");
		CentricStatusController sc = (CentricStatusController)PAS.get_pas().get_statuscontroller();
		if(sc!=null)
		{
			CentricStatus status = sc.getOpenedStatus();
			if(status!=null)
			{
				CBPROJECTSTATUSRESPONSE event = status.getResultSet();
				if(event!=null)
				{
					String projectname = event.getProject().getSzProjectname();
					maintitle += " - " + projectname;
				}
			}
		}
		pas.setMainTitle(maintitle);
				//pas.get_userinfo().get_current_department().get_deptid());
				//+(trainingmode ? "  [" + PAS.l("mainmenu_trainingmode").toUpperCase() + "] " : " ") + s);
		pas.setTitle(pas.getMainTitle());
		return true;
	}

	@Override
	public InfoPanel onCreateInfoPanel() {
		//CentricSendOptionToolbar ctoolbar = new CentricSendOptionToolbar();
		//ctoolbar.doInit();
		//return ctoolbar;
		
		InfoPanel panel = new CentricInfoPanel();
		panel.doInit();
		return panel;
	}

	@Override
	public ImageIcon onLoadAppIcon() {
		//return super.onLoadAppIcon();
		return no.ums.pas.ums.tools.ImageLoader.load_icon("no/ums/pas/plugins/centric/", "centiccLogo16.png", getClass().getClassLoader());
		/*try
		{
			//return new ImageIcon(getClass().getClassLoader().getResource("no/ums/pas/plugins/centric/alert-icon.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}*/
	}

	@Override
	public LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader) {
		try
		{
			Class<LookAndFeel> cl = null;
			switch(operating_system)
			{
			case MAC:
				cl = (Class<LookAndFeel>)classloader.loadClass("javax.swing.plaf.mac.MacLookAndFeel");
				break;
			case UNIX:
				cl = (Class<LookAndFeel>)classloader.loadClass("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				break;
			case WIN:
				cl = (Class<LookAndFeel>)classloader.loadClass("no.ums.pas.pluginbase.defaults.DefaultWindowsLookAndFeel"); //"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				if(uidefaults_initial!=null)
				{
					ArrayList<Object> defaults = new ArrayList<Object>();
					Enumeration<Object> keys = uidefaults_initial.keys();
					while(keys.hasMoreElements()) {
						Object key = keys.nextElement();
						defaults.add(key);
						defaults.add(uidefaults_initial.get(key));
					}
					UIManager.getDefaults().putDefaults(defaults.toArray());
				}
				//cl = (Class<LookAndFeel>)classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
				break;
			}
			LookAndFeel laf = (LookAndFeel)cl.newInstance();
			UIManager.setLookAndFeel(laf);
			JDialog.setDefaultLookAndFeelDecorated(false);
			JFrame.setDefaultLookAndFeelDecorated(false);
			if(PAS.get_pas()!=null)
				SwingUtilities.updateComponentTreeUI(PAS.get_pas());
			return laf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{//default to crossplatform LAF

			System.out.println("Loading cross platform LAF");
			Class cl = classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
			LookAndFeel laf = (LookAndFeel)cl.newInstance();
			UIManager.setLookAndFeel(laf);
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);	
			SwingUtilities.updateComponentTreeUI(PAS.get_pas());

			return laf;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean onSetUserLookAndFeel(Settings settings, final UserInfo userinfo) {
		try
		{
			onGetInitialUIDefaults();
			/*if(IsInTrainingMode(userinfo))
			{
				ClassLoader classloader = settings.getClass().getClassLoader();
				Class cl = classloader.loadClass("no.ums.pas.plugins.centric.TrainingLookAndFeel");
				LookAndFeel laf = (LookAndFeel)cl.newInstance();
				UIManager.setLookAndFeel(laf);
				SwingUtilities.updateComponentTreeUI(PAS.get_pas());
			}
			else
			{
				onSetInitialLookAndFeel(this.getClass().getClassLoader());
			}*/
			onSetInitialLookAndFeel(this.getClass().getClassLoader());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	

	@Override
	public boolean onUserChangedLookAndFeel(Settings settings) {
		return false;
	}

	@Override
	public boolean onBeforeLoadMap(Settings settings) {
		/*if(settings.getMapServer()==MAPSERVER.WMS)
		{
			wms_layer_selector.setVisible(true);
		}
		else
			wms_layer_selector.setVisible(false);*/
		return true;
	}

	@Override
	public boolean onWmsLayerListLoaded(List<Layer> layers, ArrayList<String> check) {
		//wms_layer_selector.populate(layers, check);
		return true;
	}
	

	@Override
	public boolean onSoapFaultException(UserInfo info, SOAPFaultException e) {
		return super.onSoapFaultException(info, e);
	}

	@Override
	protected boolean onSessionTimedOutException(UserInfo info) {
		return super.onSessionTimedOutException(info);
	}

	@Override
	public boolean onHelpAbout() {
		
		String content = PAS.l("common_aboutbox_content");
		content+="\n\n";
		content+="\nImplementation version " + versioning.IMPLEMENTATION_VERSION;
		content+="\nSpecification version " + versioning.SPECIFICATION_VERSION;
		//content+="\n" + versioning.BUILT_DATE;
		JOptionPane.showMessageDialog(PAS.get_pas(), content, PAS.l("common_aboutbox_heading"), JOptionPane.INFORMATION_MESSAGE);
		return super.onHelpAbout();
	}

	@Override
	public boolean onTrainingMode(boolean b) {
		if(b)
		{
			onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
		}
		else
		{
			onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
		}
		try
		{
			CentricVariables.centric_send.trainingModeChanged();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		PAS.get_pas().repaint();
		//onSetUserLookAndFeel(PAS.get_pas().get_settings(), PAS.get_pas().get_userinfo());
		return super.onTrainingMode(b);
	}

	
	
	@Override
	public boolean onAfterPowerUp(LogonDialog dlg, WSPowerup ws) {
		if(ws.getResult()==WSRESULTCODE.OK)
			dlg.set_errortext(PAS.l("logon_ws_active"), false);
		else
			dlg.set_errortext(PAS.l("logon_ws_inactive"));
		try
		{
			dlg.setMaxLogonTries(ws.getResponse().getLMaxLogontries());
		}
		catch(Exception e)
		{}
		return true;
	}

	@Override
	public boolean onLogonAddControls(LogonPanel p) {
		int verticalspacing = 10;
		p.m_gridconst.fill = GridBagConstraints.HORIZONTAL;
		p.m_gridconst.anchor = GridBagConstraints.CENTER;
		

		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblCompId(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getCompId(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);

		p.set_gridconst(3, p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblUserId(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getUserId(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);

		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getLblPasswd(), p.m_gridconst);
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getPasswd(), p.m_gridconst);

		p.add_spacing(p.DIR_VERTICAL, verticalspacing);
		
		
		p.set_gridconst(3,p.inc_panels(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(p.getBtnSubmit(), p.m_gridconst);			

		JButton btn_cancel = new JButton(PAS.l("common_cancel"));
		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		p.set_gridconst(5,p.get_panel(),1,1, GridBagConstraints.CENTER); //x,y,sizex,sizey
		p.add(btn_cancel, p.m_gridconst);			
		
		
		p.set_gridconst(0,p.inc_panels(),7,1, GridBagConstraints.CENTER);
		p.add(p.getLblError(), p.m_gridconst);
		
		
		
		//p.set_gridconst(0,p.inc_panels(),7,1);
		//p.add(p.getNSList(), p.m_gridconst);
		return true;

	}

	@Override
	public boolean onCustomizeLogonDlg(LogonDialog dlg) {
		dlg.setSize(new Dimension(300,200));
		dlg.get_logonpanel().getLblCompId().setVisible(false);
		dlg.get_logonpanel().getCompId().setVisible(false);
		dlg.get_logonpanel().getCompId().setEditable(false);
		dlg.get_logonpanel().getBtnSubmit().setText(PAS.l("common_ok"));

		/*dlg.get_logonpanel().get_gridconst().anchor = GridBagConstraints.CENTER;
		dlg.get_logonpanel().add_spacing(DefaultPanel.DIR_VERTICAL, 10);
		dlg.get_logonpanel().set_gridconst(0, dlg.get_logonpanel().inc_panels(), 7, 1);
		try
		{
			ImageIcon img = new ImageIcon(this.getClass().getResource("icons/logo.jpg")); //logo.png"));
			JLabel lbl = new JLabel(img);

			dlg.get_logonpanel().add(lbl, dlg.get_logonpanel().get_gridconst());
			dlg.get_logonpanel().revalidate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/

		/*dlg.get_logonpanel().getNSList().setVisible(false);
		dlg.get_logonpanel().getCompId().setEditable(false);
		dlg.get_logonpanel().getCompId().setText("UMS");
		dlg.get_logonpanel().getLblLanguage().setVisible(false);
		dlg.get_logonpanel().getLanguageCombo().setVisible(false);
		dlg.get_logonpanel().getLblUserId().setPreferredSize(new Dimension(150, 30));*/
		return super.onCustomizeLogonDlg(dlg);
	}

	@Override
	public boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g) {
		//MARK LIVE/TRAINING MODE
		g.setFont(UIManager.getFont("InternalFrame.titleFont"));

		String str = PAS.l("common_live").toUpperCase();
		if(IsInTrainingMode(PAS.get_pas().get_userinfo()))
			str = PAS.l("mainmenu_trainingmode").toUpperCase();
		int strwidth = g.getFontMetrics().stringWidth(str);
		int x = bar.getWidth()/2 - strwidth/2;
		int y = bar.getHeight()/2-9;
		int w = strwidth;
		int h = bar.getHeight()/2+5;
		g.setColor(new Color(230, 100, 100, 250));
		g.fillRoundRect(x-5, y, w+10, h, 2, 2);
		g.setColor(Color.black);
		g.drawRoundRect(x-5, y, w+10, h, 2, 2);
		g.drawString(str, x, h);
		
		//HELPDESK
		g.setColor(Color.black);
		str = PAS.l("common_helpdesk_contact");
		strwidth = g.getFontMetrics().stringWidth(str); 
		x = bar.getWidth() - strwidth - 20;
		w = strwidth;
		//g.drawRoundRect(x-5, y, w+10, h, 2, 2);
		g.drawString(str, x, h);
		return super.onPaintMenuBarExtras(bar, g);
	}

	@Override
	public boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel) {
		boolean ret = true;
		//ret = super.onAddInfoTab(tab, panel);
		CentricSendOptionToolbar send = new CentricSendOptionToolbar();
		CentricVariables.centric_send = send;
		//((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(send);
		tab.addTab(PAS.l("mainmenu_file_newsending"), null, send, PAS.l("main_parmtab_popup_generate_sending"));
		return ret;
	}

	@Override
	public boolean onMapCalcNewCoords(Navigation nav, PAS p) {
		//return super.onMapCalcNewCoords(nav, p);
		p.get_statuscontroller().calcHouseCoords();
		if(p.get_statuscontroller().get_sendinglist()!=null) {
			for(int i=0; i < p.get_statuscontroller().get_sendinglist().size(); i++) {
				try {
					if(p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape()!=null)
						p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape().calc_coortopix(nav);
				} catch(Exception e) {
					
				}
			}
		}
		try
		{
			DeptArray depts = p.get_userinfo().get_departments();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).CalcCoorRestrictionShapes();
			}
			List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).calc_coortopix(p.get_navigation());
			}
			//get_pas().get_userinfo().get_current_department().CalcCoorRestrictionShapes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			p.get_mappane().get_active_shape().calc_coortopix(PAS.get_pas().get_navigation());
		}
		catch(Exception e)
		{
			
		}
		/*for(int i=0; i < getShapesToPaint().size(); i++)
		{
			if(getShapesToPaint().get(i)!=null)
				getShapesToPaint().get(i).calc_coortopix(nav);
		}*/
		Enumeration<ShapeStruct> en = getShapesToPaint().elements();
		while(en.hasMoreElements())
		{
			en.nextElement().calc_coortopix(nav);
		}


		return true;
	}

	@Override
	public boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p) {
		try
		{
			
			DeptArray depts = p.get_userinfo().get_departments();
			//depts.ClearCombinedRestrictionShapelist();
			//depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
			//depts.test();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).drawRestrictionShapes(g, nav);
			}
			List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).draw(g, nav, false, true, false, null, true, true, 2, false);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//only paint status shapes if in status tab
		if(CentricEastContent.CURRENT_PANEL==CentricEastContent.PANEL_CENTRICSTATUS_)
		{
			try
			{
				Enumeration<ShapeStruct> en = getShapesToPaint().elements();
				while(en.hasMoreElements())
				{
					en.nextElement().draw(g, nav, false, true, false, null, true, true, 2, true);
				}			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		//only paint send shapes if in send tab
		if(CentricEastContent.CURRENT_PANEL==CentricEastContent.PANEL_CENTRICSEND_)
		{
			try {
				boolean b_finalized = !p.get_mappane().get_active_shape().isEditable();
				boolean b_editmode = PAS.get_pas().get_mappane().isInPaintMode();
				p.get_mappane().get_active_shape().draw(g, nav, false, b_finalized, b_editmode, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, false);
			} catch(Exception e) { }
		}
		try {
			p.get_mappane().draw_pinpoint(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }

		return true;
	}

	
	
	@Override
	public boolean onMapGotoShape(ShapeStruct s) {
		return super.onMapGotoShape(s);
	}

	@Override
	public boolean onMapGotoShapesToPaint() {
		return super.onMapGotoShapesToPaint();
	}

	@Override
	public boolean onMapKeyPressed(KeyEvent e) {
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_T:
			//PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon().ellipseToRestrictionlines(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).typecast_polygon());
			variables.MAPPANE.get_active_shape().typecast_polygon().ellipseToRestrictionlines(variables.USERINFO.get_departments().get_combined_restriction_shape().get(0).typecast_polygon());
			break;
		}
		return true;

	}

	@Override
	public boolean onMapKeyReleased(KeyEvent e) {
		return super.onMapKeyReleased(e);
	}

	@Override
	public boolean onMapKeyTyped(KeyEvent e) {
		return super.onMapKeyTyped(e);
	}

	@Override
	public Dimension getDefaultScreenSize(Settings s) {
		return new Dimension(1024,700);
	}

	@Override
	public String getDefaultLocale(Settings s) {
		return "nl_NL";
		//return "en_GB";
	}

	@Override
	public String getUserLocale(LogonInfo l, Settings s) {
		return "nl_NL";
		//return "en_GB";
	}

	@Override
	public EastContent onCreateEastContent() {
		return new CentricEastContent();
	}

	@Override
	public boolean onOpenAddressBook() {
		return super.onOpenAddressBook();
	}

	@Override
	public boolean onShowContactinformation() {
		//show contact information
		JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("common_helpdesk_contact"), PAS.l("common_contact_information"), JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	@Override
	public boolean onCloseProject() {
		try {
			((CentricStatusController)PAS.get_pas().get_statuscontroller()).StopUpdates();
			PAS.pasplugin.clearShapesToPaint();
			PAS.get_pas().kickRepaint();
			((CentricEastContent)PAS.get_pas().get_eastcontent()).remove_tab(CentricEastContent.PANEL_CENTRICSTATUS_);
			((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricstatus(null);
			
			((CentricSendOptionToolbar)((CentricEastContent)PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).set_projectpk(0, "");
			//((CentricSendOptionToolbar)((CentricEastContent)PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).set_centricController(null);
			((CentricSendOptionToolbar)((CentricEastContent)PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).get_reset().doClick();
			PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_item_close_project().setEnabled(false);
			onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
			onSetInitialMapBounds(variables.NAVIGATION, PAS.get_pas().get_userinfo());
			PAS.get_pas().get_mappane().load_map(true);
			return true;
		}catch(Exception e) { return false; }
	}

	@Override
	public boolean onOpenProject(Project project, long nFromNewRefno) {
		try {
			// Does the same thing as after sending a message
			CentricSendOptionToolbar csend = CentricVariables.centric_send;
			//csend.set_projectpk(Long.parseLong(project.get_projectpk()), project.get_projectname());
			
			//CentricStatusController m_centricstatuscontroller = csend.get_statuscontroller();
			
			CBSENDINGRESPONSE res = new CBSENDINGRESPONSE(); // Just to use the same
			res.setLProjectpk(Long.parseLong(project.get_projectpk()));
			
			((CentricStatusController)PAS.get_pas().get_statuscontroller()).OpenStatus(Long.parseLong(project.get_projectpk()),csend,nFromNewRefno);
			/*if(m_centricstatuscontroller == null)
				m_centricstatuscontroller = new CentricStatusController(Long.parseLong(project.get_projectpk()),csend);
			else
				m_centricstatuscontroller.set_cbsendingresponse(res);*/
			PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().get_item_close_project().setEnabled(true);
			
			
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
			return false; 
		}
	}
	
	@Override
	public int onInvokeProject() {
		try {
			int answer = 0;
			if(((CentricEastContent)PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSTATUS_) != null) {
				// Inform and close open status
				answer = confirmClosing();
				if(answer == JOptionPane.YES_OPTION) 
					if(onCloseProject())
						return answer;
			}
			return answer;
		} catch(Exception e) { return 0; }
	}
	
	private int confirmClosing() {
		JFrame frame = get_frame();
		int answer;
		answer = JOptionPane.showConfirmDialog(frame, PAS.l("project_cb_ask_new_close_event"), null, JOptionPane.YES_NO_OPTION);
		frame.dispose();
		return answer;
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

	@Override
	public StatusController onCreateStatusController() {
		return new CentricStatusController();
	}

	@Override
	public boolean onEastContentTabClicked(EastContent e, JTabbedPane pane) {
		switch(CentricEastContent.CURRENT_PANEL)
		{
		case CentricEastContent.PANEL_CENTRICSEND_:
			if(variables.MAPPANE.get_active_shape()!=null)
				variables.MAPPANE.get_active_shape().setEditable(true);
			variables.MAPPANE.set_prev_paintmode();
			enableSendButtons(true);
			break;
		case CentricEastContent.PANEL_CENTRICSTATUS_:
			if(variables.MAPPANE.get_active_shape()!=null)
				variables.MAPPANE.get_active_shape().setEditable(false);
			variables.MAPPANE.set_mode(MapFrame.MAP_MODE_PAN);
			enableSendButtons(false);
			break;
		}
		PAS.get_pas().kickRepaint();
		return super.onEastContentTabClicked(e, pane);
	}
	
	private void enableSendButtons(boolean b)
	{
		menu_btn_draw_ellipse.setVisible(b);
		menu_btn_draw_polygon.setVisible(b);
		
		int send = variables.USERINFO.get_current_department().get_userprofile().get_send();
		menu_btn_draw_plmn.setVisible(b && send>=2);
		//menu_btn_import.setVisible(b);
	}

	@Override
	public ProjectDlg onCreateOpenProjectDlg(JFrame parent,
			ActionListener callback, String cmdSave, boolean bNewsending) {
		return new CentricProjectDlg(parent, callback);
	}

	@Override
	public boolean onLockSending(SendOptionToolbar toolbar, boolean bLock) {
		CentricVariables.centric_send.lockSending(bLock);
		if(bLock)
			variables.MAPPANE.set_mode(MapFrame.MAP_MODE_PAN);
		else
			variables.MAPPANE.set_prev_paintmode();
		enableSendButtons(!bLock);
		PAS.get_pas().kickRepaint();
		return bLock;
	}

	@Override
	public boolean onDownloadHouses(HouseController controller) {
		return false;
	}

	@Override
	public boolean onSetDefaultPanMode(Settings s) {
		s.setPanByDrag(true);
		return super.onSetDefaultPanMode(s);
	}
	
	
}