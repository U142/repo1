package no.ums.pas.tas;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.TreeRenderer;
import no.ums.pas.core.defines.tree.TreeTable;
import no.ums.pas.core.defines.tree.TreeUpdater;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.ws.WSTas;
import no.ums.pas.core.ws.WSTasCount;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.tas.painters.LogPainter;
import no.ums.pas.tas.statistics.UMSChartFrame;
import no.ums.pas.tas.treenodes.CommonTASListItem;
import no.ums.pas.tas.treenodes.ContinentListItem;
import no.ums.pas.tas.treenodes.CountryListItem;
import no.ums.pas.tas.treenodes.RequestLogItem;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdSearchArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.UnderlineHighlightPainter;
import no.ums.ws.common.ULBACONTINENT;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.UMapPoint;
import no.ums.ws.pas.tas.*;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;


public class TasPanel extends DefaultPanel implements ComponentListener, ItemListener, ActionListener, MouseListener {

    private static final Log log = UmsLog.getLogger(TasPanel.class);

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if(selRow != -1) {
			if(e.getClickCount()==1)
			{
				
			}
			else if(e.getClickCount()==2)
			{
				Object sel = selPath.getLastPathComponent();
				if(sel.getClass().equals(CountryListItem.class))
				{
					zoomToCountry(((CountryListItem)sel).getCountry());
				}
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	List<ULBACONTINENT> temp_continents;
	List<CountryListItem> arr_countries;
	List<ContinentListItem> arr_continents;
	
	public static final Hashtable<Object, CommonTASListItem> treehash = new Hashtable<Object, CommonTASListItem>();
	public static ULBACOUNTRY getCountryFromHash(String iso)
	{
		CommonTASListItem i = (CommonTASListItem)treehash.get(iso);
		if(i!=null)
		{
			if(i.getClass().equals(CountryListItem.class))
			{
				return ((CountryListItem)i).getCountry();
			}
		}
		ULBACOUNTRY c = new ULBACOUNTRY();
		c.setSzName("Unknown");
		return c;
	}
	public static ULBACOUNTRY getCountryFromCC(int cc)
	{
		ULBACOUNTRY ret = new ULBACOUNTRY();
		ret.setSzName("Unknown");
		ret.setLCc(0);
		Enumeration<CommonTASListItem> en = treehash.elements();
		while(en.hasMoreElements())
		{
			CommonTASListItem t = en.nextElement();
			if(t.getClass().equals(CountryListItem.class))
			{
				CountryListItem c = (CountryListItem)t;
				if(c.getCountry().getLCc()==cc)
					return c.getCountry();
			}
		}
		return ret;
	}
	
	CommonTASListItem prevsel = null;
	CommonTASListItem prevhovered = null;
	long n_timefilter = 0;
	long n_timefilter_requestlog = 0;
	List<UTASREQUESTRESULTS> temp_requestlog;
	Hashtable<Object, RequestLogItem> reqreshash = new Hashtable<Object, RequestLogItem>(); //storage of requestpk_operator as unique key
	Vector<RequestLogItem> reqreshash_sorted = new Vector<RequestLogItem>();
	JButton btn_send_to_list = new JButton(ImageLoader.load_icon("outbox_32.png"));
	JButton btn_clear_list = new JButton(ImageLoader.load_icon("delete_32.png"));
	JButton btn_showhistory			= new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("history_32.png"));

	//TasSendListPanel sendinglist = new TasSendListPanel(this);
	
	//TreeUpdater m_updater_thread;
	
	/** after each download this value will change based on the clock of the database*/
	public static long SERVER_CLOCK = 0;
	
	/** mark country's adrcount as expired if the count record is older than this value*/
	public static final int TAS_ADRCOUNT_TIMESTAMP_EXPIRED_SECONDS = 60*60*24; // 60 sec * 60 min * 24 hours 60*60*4; //60 sec * 60 min * 4 hours
	/** mark records as expired if age exceeds this value*/
	public static final int TAS_REQUEST_TIMESTAMP_EXPIRED_SECONDS = 60*15; //60 sec * 15 minutes
	/** delete records from hashtable if age exceeds this value*/
	public static final int TAS_REQUEST_TIMESTAMP_EXPIRED_DELETEAFTER_SECONDS = 60 * 60; //60 sec * 60 minutes
	/** Interval to check for updates (in seconds)*/
	public static final int TAS_UPDATE_INTERVAL = 5;


	protected void startRepaintTimer()
	{
		synchronized(repaint_timer) {
			if(repaint_timer.isRunning())
				repaint_timer.stop();
			repaint_timer.start();
			repaint_times=0;
		}

	}
	Timer repaint_timer = new Timer(30, this);
	TasTree tree;
	JScrollPane pane;
	//DefaultTreeModel model;
	//DefaultMutableTreeNode top;
	//TreeColumnsRenderer cr;
	//TreeRenderer cr;
	//StdTextLabel lbl_search = new StdTextLabel(PAS.l("common_search"), true, new Dimension(100, 25));
	StdSearchArea txt_search = new StdSearchArea("", false, new Dimension(200, 25), Localization.l("common_search"));

    {
        txt_search = new StdSearchArea("", false, new Dimension(200, 25), Localization.l("common_search"));
    }

    JButton btn_show_world = new JButton();
	//JButton btn_cancel_search = new JButton(ImageLoader.load_icon("delete_16.png"));
	TasDetailView pnl_details = new TasDetailView(this);
	SendListPanel pnl_send_list = new SendListPanel();
	StdTextLabel lbl_loading = new StdTextLabel("");
	JSlider slide_detaillevel = new JSlider(1, 300);
	ActionListener m_callback;
	float f_detaillevel = 0.6f;
	JMenuItem m_mi_request_touristcount;
	
	public class TasTree extends UMSTree implements TableCellRenderer
	{
		
		@Override
		public CommonTASListItem getSelectedNode() {
			return (CommonTASListItem)selected_node;
		}
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			try
			{
				DefaultMutableTreeNode dmtn=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(prevsel!=null)
				{
					CommonTASListItem c = (CommonTASListItem)prevsel;
					c.b_selected = false;
				}
				prevsel = (CommonTASListItem)dmtn;
				CommonTASListItem ctli = (CommonTASListItem)dmtn;
				ctli.b_selected = true;
				setSelectedNode(ctli);
				if(dmtn.getUserObject().getClass().equals(ULBACOUNTRY.class))
				{
					ULBACOUNTRY c = (ULBACOUNTRY)dmtn.getUserObject();
					log.debug(c.getSzName());
					if(!Variables.getNavigation().pointVisible(c.getWeightpoint()))
					{
						zoomToCountry(c);
						//NavStruct nav = new NavStruct(c.getBounds().getLBo(), c.getBounds().getRBo(), c.getBounds().getUBo(), c.getBounds().getBBo());
						//Variables.NAVIGATION.gotoMap(nav);
					}
					pnl_details.setCountry(ctli);
					startRepaintTimer();
				} else if(dmtn.getUserObject().getClass().equals(ULBACONTINENT.class))
				{
					ULBACONTINENT c = (ULBACONTINENT)dmtn.getUserObject();
					log.debug(c.getSzName());
					//zoomToContinent(c);
					//NavStruct nav = new NavStruct(c.getBounds().getLBo(), c.getBounds().getRBo(), c.getBounds().getUBo(), c.getBounds().getBBo());
					//Variables.NAVIGATION.gotoMap(nav);
					pnl_details.setCountry(null);
					startRepaintTimer();
				}
				PAS.get_pas().kickRepaint();
			}
			catch(Exception err)
			{
				
			}
		}
		//JPopupMenu popup;
		@Override
		public int getRowHeight() {
			//return super.getRowHeight();
			return 30;
		}
		public TasTree()
		{
			//super(model);
			super(TasPanel.this, TAS_UPDATE_INTERVAL);
			//top = UMSTreeNode.newTopNode(null);
			//model = new DefaultTreeModel(top);
			//super.setModel(model);
			//addTreeSelectionListener(this);
			//popup = new JPopupMenu();
            JMenuItem mi = new JMenuItem(Localization.l("main_infotab_goto_map"));
			mi.addActionListener(TasPanel.this);
			mi.setActionCommand("act_goto_map");
            m_mi_request_touristcount = new JMenuItem(Localization.l("main_tas_panel_request_touristcount"));
			m_mi_request_touristcount.addActionListener(TasPanel.this);
			m_mi_request_touristcount.setActionCommand("act_request_touristcount");
			popup.add(mi);
			popup.add(m_mi_request_touristcount);
			popup.setOpaque(true);
	        popup.setLightWeightPopupEnabled(true);
            mi = new JMenuItem(Localization.l("main_tas_show_country_history"), ImageLoader.load_icon("history_16.png"));
	        mi.addActionListener(TasPanel.this);
	        mi.setActionCommand("act_tas_show_statistics");
	        popup.add(mi);
	        
		}
		
		int visibleRow;
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			visibleRow = row;
			return this;
		}
		@Override
		public void InitRenderer() {
            final String [] cols = new String [] {"", Localization.l("main_tas_panel_table_heading_tourists"), Localization.l("main_tas_panel_table_heading_updated")};
			final int [] width = new int [] { 250, 80, 150 };
			final boolean [] b_editable = new boolean [] { false, false, false };

			final TreeTable table = new TreeTable(cols, width, b_editable, new Dimension(300, 100))
			{
				@Override
				public void set_custom_cellrenderer(TableColumn column, final int n_col) {
					column.setCellRenderer(new DefaultTableCellRenderer() {
						public static final long serialVersionUID = 1;
					    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					        
					    	Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
					    	
					        Object o = (CommonTASListItem)m_tbl_list.getValueAt(row, 0);
					        if(selected_node!=null && selected_node.equals(o))
					        {
				        		isSelected = true;
					        }
					        if(n_col==2)
				        	{
				        		CommonTASListItem item = (CommonTASListItem)m_tbl_list.getValueAt(row, 0);				        		
				        		renderer.setForeground(item.getOutdatedColor());
				        		renderer.setBackground(Color.red);
				        	}
				        	else if(n_col==0)
				        	{
				        		CountryListItem item = (CountryListItem)m_tbl_list.getValueAt(row, 0);
				        		//return item.lbl;
				        	}
				        	else
				        	{
				        		//renderer.setForeground(Color.black);
				        	}
					        renderer.setBackground(isSelected ? DEFAULT_SELECTION_COLOR : ALTERNATING_BG_COLOR_2);
					        renderer.setForeground(isSelected ? FOREGROUND_SELECTION_COLOR : FOREGROUND_COLOR);

					        //renderer.setBackground(new Color(0,0,0,0));
					        return renderer;
					    }
					});
				}
			};
			cr = new TreeRenderer(this, table) 
			{
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus){
					//super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					Object o = node.getUserObject();
					setOpaque(true);
					if(o==null)
					{
						setFont(UIManager.getFont("Tree.font"));
						return this;
					}			
					m_tbl_list.m_tbl.setShowGrid(true);
					if(node.getClass().equals(CountryListItem.class)) {
						CountryListItem li = (CountryListItem)node;
						ULBACOUNTRY c = (ULBACOUNTRY)o;
						if(li.lbl==null)
						{
							SimpleAttributeSet attrs=new SimpleAttributeSet();
				            StyleConstants.setAlignment(attrs,StyleConstants.ALIGN_CENTER);
				            li.lbl = new JTextPane();
				            StyledDocument doc=(StyledDocument)li.lbl.getDocument();
				            try
				            {
				            	doc.insertString(0, c.getSzName(), attrs);
				            	
				            }
				            catch(Exception e)
				            {
				            	log.error(e);
				            }
							//li.lbl.setText(c.getSzName());
							li.lbl.setEnabled(true);
							li.lbl.setEditable(false);
							li.lbl.setBackground(new Color(0,0,0,Color.TRANSLUCENT));
						}
						li.lbl.setBorder(null);
						setBackground(new Color(0,0,0,0));
						int w = totalwidth; //getWidth();
						this.setPreferredSize(new Dimension(w-50, getRowHeight()));
						
						//Color ul = SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor();
						Color ul = SystemColor.controlDkShadow;
						painter = new UnderlineHighlightPainter(new Color(ul.getRed(), ul.getGreen(), ul.getBlue(), 170));

						li.lbl.getHighlighter().removeAllHighlights();
						if(m_sz_searchstring.length()>0)
						{
							int idx = c.getSzName().toUpperCase().indexOf(m_sz_searchstring);
							try
							{
								if(idx>=0)
								{
									int len = m_sz_searchstring.length();
									int end = idx+len;
									li.lbl.getHighlighter().addHighlight(idx, end, painter);
								}
									
							}
							catch(Exception e)
							{
								
							}
						}

						lbl.setText(c.getSzName());
						Object [] data = new Object [] { li, c.getNTouristcount()+"", m_tbl_list.newTableDateFormatter(c.getNOldestupdate())/*UMS.Tools.TextFormat.format_datetime(c.getNOldestupdate()) */};
						boolean [] update = new boolean[] { true, true, true };
						m_tbl_list.edit_row(data, 0, update);
						//if(sel)
						{
							//m_tbl_list.m_tbl.setBackground(sel ? m_tbl_list.DEFAULT_SELECTION_COLOR : m_tbl_list.ALTERNATING_BG_COLOR_2);
							//m_tbl_list.m_tbl.setForeground(sel ? m_tbl_list.FOREGROUND_SELECTION_COLOR : m_tbl_list.FOREGROUND_COLOR);
							//this.setForeground(new java.awt.Color(255,0,0));
						}
						m_tbl_list.m_tbl.setRowHeight(getRowHeight());

						return m_tbl_list.m_tbl;
					}
					else if(o.getClass().equals(ULBACONTINENT.class)) {
						ULBACONTINENT c = (ULBACONTINENT)o;
						setFont(UIManager.getFont("InternalFrame.titleFont"));
						sel = true;
						setBorder(null);
						//int width = colwidths[0]+30+(pane.getVerticalScrollBar().isVisible() ? -5 : 0); //pane.getVerticalScrollBar().getWidth());//261;
						int width = colwidths[0] + 15;
						int w = totalwidth;
						this.setPreferredSize(new Dimension(w, getRowHeight()));
						int remaining_width = w - getSize().width - colwidths[0] - 30 + (pane.getVerticalScrollBar().isVisible() ? pane.getVerticalScrollBar().getWidth() : 0);
						//this.setSize(new Dimension(w, 30));

						Color c1 = SystemColor.control;
						int transparency = 255;
						if(leaf)
							transparency = 127;
						this.setForeground(new Color(255-transparency, 255-transparency, 255-transparency, transparency));
						Color item_bg = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 255);
						this.setBackground(item_bg);
						this.setOpaque(true);
						this.setText("<html><tr><td width=" + (colwidths[0]+15) + ">" + c.getSzName() + "</td><td width=" + (colwidths[1]+15) + ">" + c.getNTouristcount() + "</td><td width="+ (colwidths[2]) +"></td></tr></html>");
						return this;
					}
					setFont(UIManager.getFont("Tree.font"));			
					return this;
				}
				@Override
				protected void initTable(TreeTable table)
				{
					String [] data = new String [] { " ", " ", " " };
					table.m_tbl.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
					table.insert_row(data, 0);
					table.m_tbl.setBackground(new Color(0,0,0,0));
					table.m_tbl.setOpaque(false);
					table.m_tbl.getTableHeader().setBackground(new Color(0,0,0,0));
					table.m_tbl.getTableHeader().setResizingAllowed(true);
					table.m_tbl.getTableHeader().setOpaque(false);
					table.m_tbl.setIntercellSpacing(new Dimension(0,0));
					table.m_tbl.setShowGrid(false);
					table.revalidate();
					super.initTable(table);
				}
				int colwidths [] = new int [] { 250, 80, 50 };
				@Override
				public void setPreferredSize(Dimension d)
				{
					TableColumn col1 = m_tbl_list.m_tbl.getColumnModel().getColumn(0);
					TableColumn col2 = m_tbl_list.m_tbl.getColumnModel().getColumn(1);
					TableColumn col3 = m_tbl_list.m_tbl.getColumnModel().getColumn(2);
					col1.setWidth(colwidths[0]);
					col2.setWidth(colwidths[1]);
					int w = totalwidth;//getWidth();
					col3.setWidth(w-colwidths[0]-colwidths[1]);
					col1.setResizable(false);
					col2.setResizable(false);
					col3.setResizable(false);
					//super.setPreferredSize(d);
				}
			};
		}
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if(getSelectedNode()!=null)
			{
        		CommonTASListItem c = (CommonTASListItem)getSelectedNode();
        		m_mi_request_touristcount.setEnabled(c.canRunCountRequest());
			}
		}
		@Override
		public String getToolTipText(java.awt.event.MouseEvent e)
		{
			Object tip = null;
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if(path!=null)
			{
				tip = path.getLastPathComponent();
			}
			if(tip!=null)
			{
				tip = ((DefaultMutableTreeNode)tip).getUserObject();
				String text = "<html>";
				if(tip.getClass().equals(ULBACONTINENT.class))
				{
					ULBACONTINENT c = (ULBACONTINENT)tip;
					text += "<tr>";
					text += "<td>";
					text += c.getSzName() + "(" + c.getSzShort() + ")";
					text += "</td>";
					text += "<td>";
					text += c.getNTouristcount();
					text += "</td>";
					text += "</tr>";
					//return "Continent";
				}
				else if(tip.getClass().equals(ULBACOUNTRY.class))
				{
					ULBACOUNTRY c = (ULBACOUNTRY)tip;
					text += "<tr>";
					text += "<td>";
					text += c.getSzName() + "(+" + c.getLCc() + ")";
					text += "</td>";
					text += "<td>";
					text += c.getNTouristcount();
					text += "</td>";
					text += "</tr>";
					
					//return "Country";
				}
				text+="</html>";
				return null;
			}
			return null;
		}
		
	}
	
	protected void zoomToCountry(ULBACOUNTRY c)
	{
		NavStruct nav = new NavStruct(c.getBounds().getLBo(), c.getBounds().getRBo(), c.getBounds().getUBo(), c.getBounds().getBBo());
		Variables.getNavigation().gotoMap(nav);
	}
	protected void zoomToContinent(ULBACONTINENT c)
	{
		NavStruct nav = new NavStruct(c.getBounds().getLBo(), c.getBounds().getRBo(), c.getBounds().getUBo(), c.getBounds().getBBo());
		Variables.getNavigation().gotoMap(nav);
	}

	public TasPanel(ActionListener callback)
	{
		n_timefilter = -1;
		n_timefilter_requestlog = 60; //get last 60 minutes of logging
		arr_countries = new ArrayList<CountryListItem>();
		arr_continents = new ArrayList<ContinentListItem>();
		m_callback = callback;
		//m_updater_thread = new TreeUpdater(this, TAS_UPDATE_INTERVAL);
		/*top = new DefaultMutableTreeNode(new ContinentListItem(new ULBACONTINENT()));
		model = new DefaultTreeModel(top);
		tree = new TasTree(model);*/
		tree = new TasTree();
		btn_send_to_list.addActionListener(pnl_details);
		btn_send_to_list.setActionCommand("act_send_tas_multiple");
		btn_send_to_list.setVisible(true);
		btn_send_to_list.setEnabled(false);
		btn_clear_list.addActionListener(pnl_details);
		btn_clear_list.setActionCommand("act_clear_sendlist");
		btn_clear_list.setVisible(true);
		btn_clear_list.setEnabled(false);
		btn_showhistory.addActionListener(pnl_details);
		btn_showhistory.setActionCommand("act_tas_show_statistics_multiple");
		btn_showhistory.setVisible(true);
		btn_showhistory.setEnabled(false);

        btn_send_to_list.setToolTipText(Localization.l("main_tas_send_to_selected_countries"));
        btn_clear_list.setToolTipText(Localization.l("main_tas_clear_country_list"));
        btn_showhistory.setToolTipText(Localization.l("main_tas_show_country_history"));
		//cr = new TreeColumnsRenderer(tree);
		

		
		/*tree.setCellRenderer(cr);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.setBorder(null);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(this);
		*/
		ToolTipManager.sharedInstance().registerComponent(tree);
		/*if (cr instanceof SubstanceDefaultTreeCellRenderer) {
			SubstanceDefaultTreeCellRenderer dtcr = (SubstanceDefaultTreeCellRenderer) cr;
			//Substance 3.3
			Color c = SubstanceLookAndFeel.getActiveColorScheme()
					.getUltraLightColor();
			//Substance 5.2
			//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
			
			Color ctrans = new Color(c.getRed(), c.getGreen(), c.getBlue(),
					Color.TRANSLUCENT);
			dtcr.setBackground(ctrans); // new
										// Color(255,255,255,Color.TRANSLUCENT));
			dtcr.setOpaque(false);

			// Finally, set the tree's background color
			tree.setBackground(ctrans); // new
										// Color(255,255,255,Color.TRANSLUCENT));
										// //dtcr.getBackground());//new
										// Color(255,255,255,Color.TRANSLUCENT));
			tree.setOpaque(false);
		}
		cr.setBorder(null);
		cr.setOpaque(false);
		cr.setBackground(new Color(0, 0, 0, Color.TRANSLUCENT));*/
		
		pane = new JScrollPane(tree);
		//tree.addTreeSelectionListener(this);
		tree.setBackground(new Color(0, 0, 0, Color.TRANSLUCENT));
		//lbl_search.setFont(UIManager.getFont("LargeText.font"));
		txt_search.setFont(UIManager.getFont("LargeText.font"));
		lbl_loading.setPreferredSize(new Dimension(100, 50));
		lbl_loading.setIcon(ImageLoader.load_icon("bigrotation2.gif"));
		lbl_loading.setVisible(false);
		
		btn_show_world.setIcon(no.ums.pas.ums.tools.ImageLoader.load_icon("earth_32.png"));
		btn_show_world.addActionListener(PAS.get_pas().get_pasactionlistener());
		btn_show_world.setActionCommand("act_show_world");
        btn_show_world.setToolTipText(Localization.l("common_pas_zoom_world"));
		
		slide_detaillevel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				//f_detaillevel = (float)(slide_detaillevel.getValue()/100.0f);
				log.debug(f_detaillevel);
				PAS.get_pas().kickRepaint();
			}
		});
		slide_detaillevel.setValue(60);
		
		//btn_cancel_search.addActionListener(this);
		//btn_cancel_search.setActionCommand("act_cancel_search");

		//m_gridconst.anchor = GridBagConstraints.CENTER;
		m_gridconst.anchor = GridBagConstraints.WEST;
		//set_gridconst(get_xpanel(), inc_panels(), 8, 1);
		//add(btn_show_world, m_gridconst);

		//add_spacing(DIR_HORIZONTAL, 40);
		
		//DefaultPanel.ENABLE_GRID_DEBUG = true;
		
		pnl_send_list.m_gridconst.anchor = GridBagConstraints.CENTER;
		pnl_send_list.set_gridconst(pnl_send_list.get_xpanel(), pnl_send_list.get_panel(), 1, 1);
		pnl_send_list.add(btn_send_to_list, pnl_send_list.m_gridconst);
		pnl_send_list.add_spacing(DIR_HORIZONTAL, 20);
		
		pnl_send_list.set_gridconst(pnl_send_list.inc_xpanels(), pnl_send_list.get_panel(), 1, 1);
		pnl_send_list.add(btn_clear_list, pnl_send_list.m_gridconst);
		pnl_send_list.add_spacing(DIR_HORIZONTAL, 20);
		
		pnl_send_list.set_gridconst(pnl_send_list.inc_xpanels(), pnl_send_list.get_panel(), 1, 1);
		pnl_send_list.add(btn_showhistory, pnl_send_list.m_gridconst);
		
		set_gridconst(0, inc_panels(), 8, 1);
		add(pnl_send_list, m_gridconst);
        pnl_send_list.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(Localization.l("main_tas_send_multiple_countries")));
		
		/*
		set_gridconst(inc_xpanels(), get_panel(), 1, 1);
		add(btn_send_to_list, m_gridconst);

		add_spacing(DIR_HORIZONTAL, 20);

		set_gridconst(inc_xpanels(), get_panel(), 1, 1);
		add(btn_clear_list, m_gridconst);
		
		add_spacing(DIR_HORIZONTAL, 20);
		
		set_gridconst(inc_xpanels(), get_panel(), 1, 1);
		add(btn_showhistory, m_gridconst);
		*/
		
		/*set_gridconst(0, get_panel(), 3, 1);
		m_gridconst.anchor = GridBagConstraints.CENTER;
		add(lbl_loading, m_gridconst);*/
		
		//set_gridconst(0, inc_panels(), 3, 1);
		//add(slide_detaillevel, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 20);
		//set_gridconst(0, inc_panels(), 1, 1);
		//add(lbl_search, m_gridconst);
		set_gridconst(0, inc_panels(), 8, 1);
		m_gridconst.anchor = GridBagConstraints.WEST;
		add(txt_search, m_gridconst);
		add_spacing(DIR_VERTICAL, 5);
		//m_gridconst.anchor = GridBagConstraints.WEST;
		//set_gridconst(2, get_panel(), 1, 1);
		//add(btn_cancel_search, m_gridconst);
		
		//add_spacing(DIR_VERTICAL, 10);
		set_gridconst(0, inc_panels(), 8, 1);
		add(pane, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 10);
		set_gridconst(0, inc_panels(), 8, 1);
		add(pnl_details, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 10);
		set_gridconst(0, inc_panels(), 8, 1);
		//add(sendinglist, m_gridconst);

		
		addComponentListener(this);
		//this.setPreferredSize(new Dimension(getWidth(), getHeight()));
		//Download();
		//startDownloadThread();
		//txt_search.addKeyListener(this);
		txt_search.addActionListener(this);
		
		//m_updater_thread.startDownloadThread();
		if(treehash != null && treehash.size()>0)
			treehash.clear();
			
		tree.startUpdater();
	}
	
	
	@Override
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
		{
			super.componentResized(e);
			return;
		}
		int height_of_detailview = 250;
		int height_of_searchbox = 120;
		int height_of_sendlist = 80;
		int height_of_tree = getHeight() - height_of_detailview - height_of_searchbox - height_of_sendlist;
		if(height_of_tree<=0)
			height_of_tree = 50;
		
		pane.setPreferredSize(new Dimension(getWidth(), height_of_tree));
		pnl_details.setPreferredSize(new Dimension(getWidth(), height_of_detailview));
		pnl_send_list.setPreferredSize(new Dimension(getWidth(), height_of_sendlist));
		tree.getTreeRenderer().setTotalWidth(getWidth());
		tree.getTreeRenderer().setTotalHeight(height_of_tree);
		tree.getTreeRenderer().setPreferredSize(new Dimension(getWidth(), getHeight()));
		//this.sendinglist.setPreferredSize(new Dimension(getWidth(), height_of_sendlist));
		int searchsize = txt_search.getSize().height;
		pane.revalidate();
		tree.revalidate();
		this.revalidate();
		tree.repaint();
		//log.debug("RESIZE w="+getWidth());
		//if(searchsize>0)
		//	btn_cancel_search.setPreferredSize(new Dimension(searchsize, searchsize));
		super.componentResized(e);
	}

	public boolean Download()
	{
		try
		{
			new WSTas(this, n_timefilter, n_timefilter_requestlog);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public void uninit()
	{
		//m_updater_thread.uninit();
		tree.stopUpdater();
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(repaint_timer))
		{
			//repaint_times=0;
			/*if(repaint_times<255)
				repaint_times+=1;
			else
				repaint_timer.stop();
				*/
			repaint_times = ((repaint_times + 1) % 255);
			//if(prevsel!=null && prevsel.rect!=null)
			//	PAS.get_pas().kickRepaint(prevsel.rect);
			//else 
				PAS.get_pas().kickRepaint();
		}
		else if(TreeUpdater.LOADING_START.equals(e.getActionCommand()))
		{
			m_callback.actionPerformed(e);
			Download();
		}
		else if(TreeUpdater.LOADING_FINISHED.equals(e.getActionCommand()))
		{
			m_callback.actionPerformed(e);
		}
		else if("act_download_finished".equals(e.getActionCommand()))
		{
			try
			{
				UTASUPDATES updates = (UTASUPDATES)e.getSource();
				SERVER_CLOCK = updates.getNServerclock();
				n_timefilter = SERVER_CLOCK;
				n_timefilter_requestlog = SERVER_CLOCK;
				ArrayOfULBACONTINENT arr = updates.getContinents(); //(ArrayOfULBACONTINENT)e.getSource();
				temp_continents = arr.getULBACONTINENT();
				syncUpdateTree(false, false);
				if(prevsel!=null)
				{
					if(prevsel.getClass().equals(CountryListItem.class))
					{
						CountryListItem c = (CountryListItem)prevsel;
						pnl_details.setCountry(prevsel);
					}
				}
				//PAS.get_pas().get_drawthread().set_neednewcoors(true);
				//calc_coortopix();
				PAS.get_pas().kickRepaint();
				//TreePath path = new TreePath(tree.top);
				//tree.expandPath(path);
				tree.openFolder(tree.top);
				
				temp_requestlog = updates.getRequestUpdates().getUTASREQUESTRESULTS();
				updateRequestResults();
				//Thread.sleep(30000);
				//tree.updateUI();
				//tree.validate();
			}
			catch(Exception err)
			{
				log.warn(err.getMessage(), err);
			}
			finally
			{
				/*synchronized(download_notify)
				{
					download_notify.notify();
				}*/
				//m_updater_thread.notifyDownloadDone();
				tree.signalDownloadFinished();
				//log.debug("TAS Signal download finished");
			}			
		}
		else if("act_cancel_search".equals(e.getActionCommand()))
		{
			txt_search.setText("");
			filterTree("", false);
		}
		else if(StdSearchArea.ACTION_SEARCH_UPDATED.equals(e.getActionCommand()))
		{
			String s = (String)e.getSource();
			filterTree(s, s.length()<=0);
		}
		else if(StdSearchArea.ACTION_SEARCH_CLEARED.equals(e.getActionCommand()))
		{
			filterTree((String)e.getSource(), true);
		}
		else if("act_goto_map".equals(e.getActionCommand()))
		{
			DefaultMutableTreeNode dmtn, node;
			TreePath path = tree.getSelectionPath();
	        dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
	        prevsel = (CommonTASListItem)dmtn;
	        //tree.setSelectionPath(dmtn.get);
			if(prevsel!=null)
			{
				if(prevsel.getClass().equals(ContinentListItem.class))
					zoomToContinent(((ContinentListItem) prevsel).getContinent());
				else if(prevsel.getClass().equals(CountryListItem.class))
					zoomToCountry(((CountryListItem)prevsel).getCountry());
			}
		}
		else if("act_tas_show_statistics".equals(e.getActionCommand()))
		{
			if(prevsel!=null && prevsel.getClass().equals(CountryListItem.class))
			{
				CountryListItem c = (CountryListItem)prevsel;
				List<ULBACOUNTRY> countries = new ArrayList<ULBACOUNTRY>();
				countries.add(c.getCountry());
				//TasChart chart = new ChartOverTime(countries);
				//chart.UpdateChart();
                UMSChartFrame frame = new UMSChartFrame(Localization.l("main_tas_stats_heading_over_time"), true, countries);

			}
			else if(prevsel!=null && prevsel.getClass().equals(ContinentListItem.class))
			{
				ContinentListItem c = (ContinentListItem)prevsel;
				//List<ULBACOUNTRY> countries = c.getContinent().getCountries().getULBACOUNTRY();
				//ChartByCountry chart = new ChartByCountry(countries);
				//UMSChartFrame frame = new UMSChartFrame("Statistics", chart.getChart(), true);
				//TasChart chart = new ChartOverTime(c.getContinent());
				//chart.UpdateChart();
                UMSChartFrame chart = new UMSChartFrame(Localization.l("main_tas_stats_heading_over_time"), true, c.getContinent());
				
			}
		}
		else if("act_request_touristcount".equals(e.getActionCommand()))
		{
			try
			{
				List<ULBACOUNTRY> list = new ArrayList<ULBACOUNTRY>();
				if(e.getSource().getClass().equals(JButton.class)) //from detailview
				{
					if(pnl_details.m_selected_item!=null && pnl_details.m_selected_item.getClass().equals(CountryListItem.class))
					{
						list.add(((CountryListItem)pnl_details.m_selected_item).getCountry());
						prevsel.setCountInProgress(true);
						pnl_details.setCountry(prevsel);
						//markItemAsCountInProgress(pnl_details.m_selected_country, true);
					}
				}
				else if(e.getSource().getClass().equals(List.class)) //list of countries
				{
					list = (List<ULBACOUNTRY>)e.getSource();
					prevsel.setCountInProgress(true);
					pnl_details.setCountry(prevsel);
				}
				else //from popup menu
				{
					if(prevsel!=null && prevsel.getClass().equals(CountryListItem.class))
					{
						list.add(((CountryListItem)prevsel).getCountry());
						prevsel.setCountInProgress(true);
						pnl_details.setCountry(prevsel);
						//markItemAsCountInProgress((CountryListItem)prevsel, true);
					}
					else if(prevsel!=null && prevsel.getClass().equals(ContinentListItem.class))
					{
						ContinentListItem c = (ContinentListItem)prevsel;
						List<ULBACOUNTRY> countries = c.getContinent().getCountries().getULBACOUNTRY();
						for(int i=0; i < countries.size(); i++)
						{
							list.add(countries.get(i));
						}
					}
				}
				if(list.size()>0)
				{
					log.debug("Request tourist count");
					new WSTasCount(this, list).start();
				}
			}
			catch(Exception err)
			{
				log.warn(err.getMessage(), err);
			}

		}
		else if("act_tascount_finished".equals(e.getActionCommand()))
		{
			UTASREQUEST req = (UTASREQUEST)e.getSource();
			log.debug("Count started="+req.getNRequestpk());
			try
			{
				UTASREQUESTRESULTS res = new UTASREQUESTRESULTS();
				res.setNRequestpk(req.getNRequestpk());
				//res.setNTimestamp(req.getNTimestamp());
				long n = no.ums.pas.ums.tools.Utils.get_current_datetime();
				res.setNTimestamp(n);
				res.setNStatus(1);
				
				for(int op = 0; op < req.getOperators().getInt().size(); op++)
				{
					ArrayOfULBACOUNTRY arr_countries = new ArrayOfULBACOUNTRY();
					for(int i=0; i < req.getList().getULBACOUNTRY().size(); i++)
					{
						arr_countries.getULBACOUNTRY().add(req.getList().getULBACOUNTRY().get(i));
						log.debug("  " + req.getList().getULBACOUNTRY().get(i).getSzName());
						
					}
					res.setList(arr_countries);
					RequestLogItem log = new RequestLogItem(res, tree.model);
					String key = ENUMTASREQUESTRESULTTYPE.COUNTREQUEST.name() +  res.getNRequestpk() + "_" + req.getOperators().getInt().get(op);
					synchronized(reqreshash)
					{
						if(!reqreshash.containsKey(key))
							reqreshash.put(key, log);
						synchronized(reqreshash_sorted)
						{
							reqreshash_sorted = new Vector(reqreshash.values());
							Collections.sort(reqreshash_sorted);
						}
					}
				}
				PAS.get_pas().kickRepaint();
			}
			catch(Exception err)
			{
				
			}
		}
		else if("act_add_to_sendlist".equals(e.getActionCommand()))
		{
			final CountryListItem i = (CountryListItem)e.getSource();
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					//i.setAddedToSendList(true);
					tree.model.nodeChanged(i);
					calc_coortopix();
					PAS.get_pas().kickRepaint();
					btn_send_to_list.setEnabled(pnl_details.getSendList().size()>0);
					btn_clear_list.setEnabled(pnl_details.getSendList().size()>0);
					btn_showhistory.setEnabled(pnl_details.getSendList().size()>0);
				}
			});
			//this.revalidate();
		}
		else if("act_remove_from_sendlist".equals(e.getActionCommand()))
		{
			final CountryListItem i = (CountryListItem)e.getSource();
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					//i.setAddedToSendList(false);
					tree.model.nodeChanged(i);
					calc_coortopix();
					PAS.get_pas().kickRepaint();
				}
			});
		}
		else if("act_clear_sendlist".equals(e.getActionCommand()))
		{
			btn_send_to_list.setEnabled(pnl_details.getSendList().size()>0);
			btn_clear_list.setEnabled(pnl_details.getSendList().size()>0);
			btn_showhistory.setEnabled(pnl_details.getSendList().size()>0);
			PAS.get_pas().kickRepaint();
		}
		else if("act_removed_from_sendlist".equals(e.getActionCommand()))
		{
			try
			{
				CountryListItem i = (CountryListItem)e.getSource();
				CountryListItem node = (CountryListItem)treehash.get(i.getCountry().getSzIso().trim());
				node.setAddedToSendList(false);
				this.setPreferredSize(new Dimension(getWidth(), getHeight()));
				tree.model.nodeChanged(node);
				this.pnl_details.updateCurrentCountry();
			}
			catch(Exception err)
			{
				
			}
			//this.revalidate();
		}
		else if("act_sendlist_cleared".equals(e.getActionCommand()))
		{
			//sendinglist.setVisible(false);
		}
	}
	
	protected void updateRequestResults()
	{
		if(temp_requestlog!=null)
		{
			for(int i=0; i < temp_requestlog.size(); i++)
			{
				UTASREQUESTRESULTS res = temp_requestlog.get(i);
				String key = res.getType().name() + res.getNRequestpk() + "_" + res.getNOperator();
				synchronized(reqreshash)
				{
					if(reqreshash.containsKey(key))
					{
						//update
						RequestLogItem upd = reqreshash.get(key);
						upd.Update(res);
					}
					else
					{
						RequestLogItem ins = new RequestLogItem(res, tree.model);
						synchronized(reqreshash)
						{
							reqreshash.put(key, ins);
						}
						
					}
				}
				//if(res.getNTimestamp() > n_timefilter_requestlog)
				//	n_timefilter_requestlog = res.getNTimestamp();
			}
			synchronized(reqreshash_sorted)
			{
				reqreshash_sorted = new Vector(reqreshash.values());
				Collections.sort(reqreshash_sorted);
			}
		}
	}
	protected void markItemAsCountInProgress(CommonTASListItem c, boolean b)
	{
		try
		{
			c.setCountRequestSent(b);
		}
		catch(Exception e)
		{
            Error.getError().addError(Localization.l("common_error"), "An error occured", e, Error.SEVERITY_ERROR);
		}
	}
	protected void markItemAsCountInProgress(ULBACOUNTRY c, boolean b)
	{
		try
		{
			if(pnl_details.m_selected_item != null && pnl_details.m_selected_item.getClass().equals(CountryListItem.class))
			{
				CommonTASListItem item = treehash.get(((CountryListItem)pnl_details.m_selected_item).getCountry().getSzIso().trim());
				markItemAsCountInProgress(item, b);
			}
		}
		catch(Exception e)
		{
            Error.getError().addError(Localization.l("common_error"), "An error occured", e, Error.SEVERITY_ERROR);
		}
	}
	
	protected void treeInsertDelete(List<CountryListItem> list)
	{
		Iterator<CountryListItem> it_cont = list.iterator();
		while(it_cont.hasNext())
		{
			CountryListItem c = it_cont.next();
			if(c.b_added && !c.b_visible)
			{
				try
				{
					c.parent.remove(c);
					c.b_added = false;
				}
				catch(Exception e)
				{
					
				}
				//c.b_visible = false;
			}
			else if(!c.b_added && c.b_visible)
			{
				try
				{
					c.parent.add(c);
					c.b_added = true;
				}
				catch(Exception e)
				{
					
				}
				//c.b_visible = true;
			}
		}		
	}
	
	protected void syncUpdateTree(boolean force_delete, boolean auto_arrange_folders)
	{
		synchronized(temp_continents)
		{
			updateTree(force_delete, auto_arrange_folders);
		}
	}
	
	protected void updateTree(boolean force_delete, boolean auto_arrange_folders)
	{
		Iterator<ULBACONTINENT> it_cont = temp_continents.iterator();
		while(it_cont.hasNext())
		{
			ULBACONTINENT c = it_cont.next();
			ContinentListItem contitem = new ContinentListItem(c, tree.model);
			if(!treehash.containsKey(c.getLContinentpk()))
			{
				tree.top.add(contitem);
				treehash.put(c.getLContinentpk(), contitem);
				arr_continents.add(contitem);
			}
			else
			{
				contitem = (ContinentListItem)treehash.get(c.getLContinentpk());
				contitem.setParent(tree.top);
				contitem.Update(c);
			}
			Iterator<ULBACOUNTRY> it_countries = c.getCountries().getULBACOUNTRY().iterator();
			while(it_countries.hasNext())
			{
				ULBACOUNTRY country = it_countries.next();
				CountryListItem countryitem = new CountryListItem(country, contitem, tree.model);

				boolean b_visible = true;

				if(!treehash.containsKey(country.getSzIso().trim()))
				{
					treehash.put(country.getSzIso().trim(), countryitem);
					arr_countries.add(countryitem);
					countryitem.b_visible = b_visible;
				}
				else
				{
					countryitem = (CountryListItem)treehash.get(country.getSzIso().trim());
					if(countryitem.Update(country))
					{
						if(countryitem.equals(prevsel))
							startRepaintTimer();
					}
				}
				//if(countryitem.getCountry().getNNewestupdate() > n_timefilter)
				//	n_timefilter = countryitem.getCountry().getNNewestupdate();

			}
			//Collections.sort(arr_countries);
			//treeInsertDelete(arr_countries);
			//contitem.getContinent().setNTouristcount(touristcount_pr_continent);
		}
		_filterProc(false, false, true);
	}
	
	protected void _filterProc(boolean force_delete, boolean auto_arrange_folders, boolean b_new_continentcount)
	{
		if(b_new_continentcount)
		{
			for(int i=0; i < arr_continents.size(); i++)
			{
				arr_continents.get(i).getContinent().setNTouristcount(0);
			}
		}
		for(int i=0; i < arr_countries.size(); i++)
		{
			CountryListItem countryitem = arr_countries.get(i);
			if(tree.getTreeRenderer().getSearchString().length()>0)
			{
				boolean b_visible = false;
				if(countryitem.getCountry().getSzName().toUpperCase().indexOf(tree.getTreeRenderer().getSearchString())>=0)
					b_visible = true;
				else
					b_visible = false;
				/*if(countryitem.b_visible && !b_visible)
				{
					//contitem.b_visible = false;
					countryitem.parent.b_visible = false;
					//countryitem.b_visible = false;
					countryitem.setVisible(false);
				}
				else if(!countryitem.b_visible && b_visible)
				{
					//contitem.b_visible = true;
					countryitem.parent.b_visible = true;
					//countryitem.b_visible = true;
					countryitem
				}*/
				countryitem.setVisible(b_visible);
				
			}
			else
			{
				countryitem.b_visible = true;
				countryitem.parent.b_visible = true;
			}
			countryitem.b_visible = (force_delete ? false : countryitem.b_visible);

			if(b_new_continentcount)
			{
				int tourists = countryitem.parent.getContinent().getNTouristcount();
				countryitem.parent.getContinent().setNTouristcount(tourists+countryitem.getCountry().getNTouristcount());
			}
			
			try
			{
				if(auto_arrange_folders)
				{
					TreePath path = new TreePath(countryitem.parent.getPath());
					tree.expandPath(path);
				}
			}
			catch(Exception e)
			{
				
			}

		}
		Collections.sort(arr_countries);
		treeInsertDelete(arr_countries);

	}
	
	protected void filterTree(final String s, final boolean b_collapse)
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				
				tree.getTreeRenderer().setSearchString(s.toUpperCase());
				_filterProc(true, true, false);
				_filterProc(false, true, false);
				try
				{
					if(b_collapse)
					{
						for(int i=0; i < arr_continents.size(); i++)
						{
							TreePath path = new TreePath(arr_continents.get(i).getPath());
							tree.collapsePath(path);
						}

					}
					tree.updateUI();
				}
				catch(Exception e)
				{
					
				}
			}
		});
	}


	@Override
	public void add_controls() {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
	}
	

	

	


	

	boolean b_paint_countries = true;
	boolean b_paint_continents = false;
	boolean b_paint_countrytext = false;
	int n_countrytext_fontsize = 0;
	int n_countrytext_nonsel_fontsize = 0;
	
	public void drawLog(Graphics g)
	{
		LogPainter.drawLog((Graphics2D)g, reqreshash, reqreshash_sorted, treehash);
	}
	
	public void drawItems(Graphics2D g)
	{
		if(temp_continents==null)
			return;
		Font usefont = new Font("Verdana", Font.BOLD, n_countrytext_fontsize);
		g.setFont(usefont);
		
		
		//Enumeration<DefaultMutableTreeNode> it = treehash.elements();
		if(b_paint_countries)
		{
			Iterator<CountryListItem> it = arr_countries.iterator();
			//Enumeration<CommonTASListItem> it = treehash.elements();
			while(it.hasNext())//it.hasMoreElements())
			{
				DefaultMutableTreeNode node = it.next();//it.nextElement();
					if(node.getClass().equals(CountryListItem.class))
					{
						CountryListItem cou = (CountryListItem)node;
						if(!cou.isAddedToSendList())
						{
							//Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getDarkColor();
							Color c1 = SystemColor.control;
							Color col = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), (cou.b_selected ? 128 : 128));
							//ULBACOUNTRY country = ((CountryListItem)node).getCountry();
							UMapPoint screen = cou.getCountry().getWeightpointScreen();
							if(screen!=null && cou!=prevsel)
							{
		
								if(cou.b_selected)
								{
									//if(repaint_times>5)
									//	repaint_times = 5;
		
									usefont = new Font("Verdana", Font.BOLD, n_countrytext_fontsize+5);
								}
								else
									usefont = new Font("Verdana", Font.BOLD, n_countrytext_fontsize);
									
								//g.setFont(usefont);
								String tourists = ((Integer)cou.getCountry().getNTouristcount()).toString();
								if(cou.getCountry().getNOldestupdate()<=0) {
                                    tourists = Localization.l("common_na");
                                }
								if(screen!=null)
									paintItem(g, (cou.b_selected ? true : b_paint_countrytext), cou.getCountry().getSzName(), tourists, usefont, col, screen, 10, cou.b_selected, false, cou);
								
							}
						}
					}
				}
		}
		if(b_paint_continents)
		{
			Iterator<ContinentListItem> it = arr_continents.iterator();
			while(it.hasNext())
			{
				ContinentListItem node = it.next();
				//Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getDarkColor();
				Color c1 = SystemColor.controlDkShadow;
				Color col = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 128);
				if(node.getClass().equals(ContinentListItem.class))
				{
					ULBACONTINENT continent = ((ContinentListItem)node).getContinent();
					UMapPoint screen = continent.getWeightpointScreen();
					if(screen!=null)
						paintItem(g, true, continent.getSzName(), ((Integer)continent.getNTouristcount()).toString(), usefont, col, screen, 10, ((ContinentListItem)node).b_selected, false, (ContinentListItem)node);
				}
			}
		}
		
		if(b_paint_continents || prevsel!=null)
		{
			if(prevsel!=null)
			{
				if(prevsel.getClass().equals(CountryListItem.class))
				{
					usefont = new Font("Verdana", Font.BOLD, 12);
					//g.setFont(usefont);
					CountryListItem showCountry = (CountryListItem)prevsel;
					if(!showCountry.isAddedToSendList())
					{
						Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
						c1 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 128);
						String tourists = ((Integer)showCountry.getCountry().getNTouristcount()).toString();
						if(showCountry.getCountry().getNOldestupdate()<=0) {
                            tourists = Localization.l("common_na");
                        }
						if(showCountry.getCountry().getWeightpointScreen()!=null)
							paintItem(g, true, showCountry.getCountry().getSzName(), tourists, usefont, c1, showCountry.getCountry().getWeightpointScreen(), 10, showCountry.b_selected, true, showCountry);
					}
				}
			}
		}
		for(int i=0; i < pnl_details.getSendList().size(); i++)
		{
			usefont = new Font("Verdana", Font.BOLD, n_countrytext_nonsel_fontsize);
			CountryListItem showCountry = (CountryListItem)pnl_details.getSendList().get(i);
			Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
			c1 = new Color(c1.getRed(), c1.getGreen()/10, c1.getBlue()/10, 128);
			String tourists = ((Integer)showCountry.getCountry().getNTouristcount()).toString();
			if(showCountry.getCountry().getNOldestupdate()<=0) {
                tourists = Localization.l("common_na");
            }
			if(showCountry.getCountry().getWeightpointScreen()!=null)
				paintItem(g, true, showCountry.getCountry().getSzName(), tourists, usefont, c1, showCountry.getCountry().getWeightpointScreen(), 10, showCountry.b_selected, true, showCountry);
			
		}
		/*if(b_paint_continents || prevhovered!=null)
		{
			if(prevhovered!=null)
			{
				if(prevhovered.getClass().equals(CountryListItem.class))
				{
					usefont = new Font("Verdana", Font.BOLD, 10);
					//g.setFont(usefont);
					CountryListItem showCountry = (CountryListItem)prevhovered;
					Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor();
					c1 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 128);
					paintItem(g, true, showCountry.getCountry().getSzName(), showCountry.getCountry().getNTouristcount(), usefont, c1, showCountry.getCountry().getWeightpointScreen(), 10, true, true, showCountry);
				}
			}

		}*/
	}
	int repaint_times = 0;
	protected void paintItem(Graphics2D g, boolean btext, String s, String touristcount, Font usefont, Color col, UMapPoint screen, int rounding, boolean b_focus, boolean b_animate, CommonTASListItem item)
	{
		String str_paint = (btext ? s+" - " : "")+touristcount;
		String str_paint2 = str_paint;
 		boolean b_animate_stop_request = false;
		if(b_focus && b_animate)
		{
			if(str_paint.length()<=repaint_times)
			{
				b_animate_stop_request = true;
				//	repaint_timer.stop();
			}
			else
			{
				if(!repaint_timer.isRunning())
					repaint_timer.start();
			}
			str_paint2 = str_paint.substring(0, Math.min(repaint_times, str_paint.length()));
		}
		else if(b_focus)
			repaint_timer.stop();
		Rectangle2D textsize = 	usefont.getStringBounds(str_paint, g.getFontRenderContext());
		//if(b_focus)
		//	usefont = usefont.deriveFont(Math.min(repaint_times, usefont.getSize())*1.0f);
		int padding = 3;
		Rectangle r = new Rectangle();
		r.setRect(
				screen.getLon() - textsize.getWidth()/2.0, 
				screen.getLat() - textsize.getHeight()/2.0,
				textsize.getWidth(),
				textsize.getHeight());
		Rectangle rect = new Rectangle((int)r.getX()-padding, (int)r.getY()-padding, (int)r.getWidth()+padding*2, (int)r.getHeight()+padding*2);

		if(item.getClass().equals(ContinentListItem.class) && prevsel!=null && prevsel.getClass().equals(CountryListItem.class))
		{
			//check if it's overlapping the selected country
			if(prevsel!=null && prevsel.rect!=null && prevsel.rect.intersects(rect))
			{
				//rect.x += prevsel.rect.width;
				int sign = 1;
				if(rect.y > prevsel.rect.y)
					sign = -1;
				rect.y -= prevsel.rect.height * sign;
				r.y -= prevsel.rect.height * sign;
			}
		}

		g.setColor(col);
		Stroke oldstroke = g.getStroke();
		Paint oldpaint = g.getPaint();
		if(b_focus && b_animate)
		{
			//float dash1[] = { (repaint_times % 2) + 1};
			Color _c1 = SubstanceLookAndFeel.getActiveColorScheme().getExtraLightColor();
			Color _c2 = SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor();
			Color c1 = new Color(_c1.getBlue(), _c1.getBlue(), _c1.getBlue(), 50);
			Color c2 = new Color(_c2.getRed(), _c2.getGreen(), _c2.getBlue(), 50);
			GradientPaint gp = new GradientPaint(1, 1, c1, 50, 50,
					c2, true);
			g.setPaint(gp);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f));//, dash1, 0.0f));
			int size = 100 - (repaint_times*5);
			if(size<=1 && b_animate_stop_request)
			{
				if(repaint_timer.isRunning())
					repaint_timer.stop();
			}
			Color c3 = new Color(_c2.getRed(), _c2.getGreen(), _c2.getBlue(), 200);
			g.setColor(c2);
			g.fillOval((int)screen.getLon()-size/2, (int)screen.getLat()-size/2, size, size);
			//g.setColor(c3);
			//g.drawOval((int)screen.getLon()-size/2, (int)screen.getLat()-size/2, size, size);
			//g.drawOval((int)screen.getLon()-size/8, (int)screen.getLat()-size/8, size/4, size/4);
			g.setColor(c3);
			g.fillOval((int)screen.getLon()-size/16, (int)screen.getLat()-size/16, size/8, size/8);
			//Color oldcol = g.getColor();
			//g.setColor(Color.black);
			//g.drawOval((int)screen.getLon()-size/2, (int)screen.getLat()-size/2, size, size);
			//g.setColor(oldcol);
			
			g.setPaint(oldpaint);
			g.setStroke(oldstroke);
			//g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		}
		item.rect = rect;
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, rounding, rounding);
		//Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor();
		if(b_focus && b_animate)
			g.setColor(new Color(0,0,0,Math.min(repaint_times*10, 255)));
		else
			g.setColor(new Color(0,0,0,200));
		g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, rounding, rounding);
		//g.setColor(new Color(Math.min(repaint_times*10, 255),Math.min(repaint_times*10, 255),Math.min(repaint_times*10, 255),Math.min(repaint_times*10, 255)));
		//g.drawRoundRect(rect.x-1, rect.y-1, rect.width+2, rect.height+2, rounding, rounding);

		if(b_focus)
		{
			//g.setStroke(oldstroke);
			//g.setPaint(oldpaint);
		}
		g.setColor(new Color(0,0,0,200));
		g.setFont(usefont);
		g.drawString(str_paint2, (int)r.getX(), (int)(r.getY()+r.getHeight()/2+4));
	}
	
	protected void calc_detaillevel()
	{
		b_paint_countries = true;
		b_paint_continents = false;
		b_paint_countrytext = false;
		f_detaillevel = 1.0f;
		//int mapwidth = Math.abs(Variables.NAVIGATION.get_mapwidthmeters().intValue());
		//double modifier = Math.cos(Variables.NAVIGATION.getHeaderBBO() * CoorConverter.deg2rad);
		double mapwidth = Variables.getNavigation().getDeltaLon();
		int zoom = Variables.getMapFrame().getMapModel().getZoom();
		//log.debug("MapWidth deg = " + mapwidth);
		double levels [] = new double[] { 180, 90, 50, 40, 2, 1, 0.5 }; 
		
		if(zoom<4) //20000000
		{
			b_paint_continents = true;
			b_paint_countries = false;
			n_countrytext_fontsize = 12;
			n_countrytext_nonsel_fontsize = 7;
		}
		else if(zoom<5) //10000000
		{
			n_countrytext_fontsize = 7;
			n_countrytext_nonsel_fontsize = 7;
		}
		else if(zoom<6) //6000000
		{
			n_countrytext_fontsize = 8;
			n_countrytext_nonsel_fontsize = 8;
		}
		else if(zoom<7) //2800000
		{
			n_countrytext_fontsize = 8;
			n_countrytext_nonsel_fontsize = 8;
			b_paint_countrytext = true;
		}
		else if(zoom<8) //2000000
		{
			b_paint_countrytext = true;
			n_countrytext_nonsel_fontsize = 10;
			n_countrytext_fontsize = 10;
		}
		else if(zoom<9) //1400000
		{
			b_paint_countrytext = true;
			n_countrytext_nonsel_fontsize = 11;
			n_countrytext_fontsize = 11;
		}
		else if(zoom<10) //1000000
		{
			b_paint_countrytext = true;
			n_countrytext_nonsel_fontsize = 12;
			n_countrytext_fontsize = 12;
		}
		else
		{
			n_countrytext_fontsize = 20;
			n_countrytext_nonsel_fontsize = 20;
			b_paint_countrytext = true;
		}

	}
	public void calc_coortopix()
	{
		if(temp_continents==null)
			return;
		Enumeration<CommonTASListItem> it = treehash.elements();
		while(it.hasMoreElements())
		{
			DefaultMutableTreeNode node = it.nextElement();
			UMapPoint ll = null;
			if(node.getClass().equals(CountryListItem.class))
			{
				ULBACOUNTRY country = ((CountryListItem)node).getCountry();
				ll = country.getWeightpoint();
				if(ll==null)
					continue;
				UMapPoint screen = new UMapPoint();
            	Point pointPin = Variables.getMapFrame().getZoomLookup().getPoint(new LonLat(ll.getLon(), ll.getLat()));
            	Point topLeft = Variables.getMapFrame().getZoomLookup().getPoint(Variables.getMapFrame().getMapModel().getTopLeft());
            	Point p = new Point(pointPin.x - topLeft.x, pointPin.y - topLeft.y);
            	Rectangle scrRect = new Rectangle(Variables.getMapFrame().getSize());
				if(scrRect.contains(p))
				{
					screen.setLon(p.getX());
					screen.setLat(p.getY());
				}
				else
				{
					//log.debug("Not Visible");
					screen = null;
					((CountryListItem)node).rect = null;
				}
				country.setWeightpointScreen(screen);				
			}
			else if(node.getClass().equals(ContinentListItem.class))
			{
				ULBACONTINENT cont = ((ContinentListItem)node).getContinent();
				ll = cont.getWeightpoint();
				if(ll==null)
					continue;
				UMapPoint screen = new UMapPoint();
            	Point pointPin = Variables.getMapFrame().getZoomLookup().getPoint(new LonLat(ll.getLon(), ll.getLat()));
            	Point topLeft = Variables.getMapFrame().getZoomLookup().getPoint(Variables.getMapFrame().getMapModel().getTopLeft());
            	Point p = new Point(pointPin.x - topLeft.x, pointPin.y - topLeft.y);
            	Rectangle scrRect = new Rectangle(Variables.getMapFrame().getSize());
				if(scrRect.contains(p))
				{
					screen.setLon(p.getX());
					screen.setLat(p.getY());
				}
				else
					screen = null;
				cont.setWeightpointScreen(screen);				
			}
		}	
		
		calc_detaillevel();
		if(!b_paint_countries && pnl_details.getSendList().size()<=0) //only iterate IF we're painting countries OR if some countries are added to send-list
			return;
		it = treehash.elements();
		boolean b_newround = true;
		int iterations = 0;
		while(it.hasMoreElements())
		{
			if(b_newround)
				it = treehash.elements();
			b_newround = false;
			Point boundaries;
			if(b_paint_countrytext)
				boundaries = new Point(100, 20);
			else
				boundaries = new Point(20,20);
			CommonTASListItem item1 = it.nextElement();
			if(item1.getClass().equals(ContinentListItem.class))
				continue;
			CountryListItem cli = (CountryListItem)item1;
			if(!b_paint_countries && !cli.isAddedToSendList())
				continue;
			ULBACOUNTRY c1 = ((CountryListItem)item1).getCountry();
			Enumeration<CommonTASListItem> it2 = treehash.elements();
			//Rectangle r1 = item1.rect;
			if(c1.getWeightpointScreen()==null)
				continue;
			Rectangle r1 = new Rectangle((int)c1.getWeightpointScreen().getLon() - boundaries.x,
					(int)c1.getWeightpointScreen().getLat() - boundaries.y,
					boundaries.x, 
					boundaries.y);
			if(r1!=null)
			{
				iterations++;
				if(iterations>5000)
					break;
				while(it2.hasMoreElements())
				{
					CommonTASListItem item2 = it2.nextElement();
					if(item2.getClass().equals(ContinentListItem.class))
						continue;
					if(item1.equals(item2)) //don't compare to self
						continue;
					CountryListItem cli2 = (CountryListItem)item2;
					if(!b_paint_countries && !cli2.isAddedToSendList())
						continue;
					ULBACOUNTRY c2 = ((CountryListItem)item2).getCountry();
					//Rectangle r2 = item2.rect;
					if(c2.getWeightpointScreen()==null)
						continue;
					Rectangle r2 = new Rectangle((int)c2.getWeightpointScreen().getLon() - boundaries.x,
												(int)c2.getWeightpointScreen().getLat() - boundaries.y,
												boundaries.x, 
												boundaries.y);
					if(r2!=null)
					{
						Rectangle intersect = r1.intersection(r2);
						if(!intersect.isEmpty()) //we have intersection
						{
							//move r1 half way
							//r1.move((intersect.width/2)-1, (intersect.height/2)-1);
							//r2.move((intersect.width/2)+1, (intersect.width/2)-1);
							//item1.rect.setLocation((intersect.width/2)-10, (intersect.height/2)-10);
							//item2.rect.setLocation((intersect.width/2)+10, (intersect.height/2)-10);
							//item1.rect.setLocation(new Point(item1.rect.getLocation().x - (intersect.width/2)-1, item1.rect.getLocation().y - (intersect.height/2)-1) );
							//item2.rect.setLocation(new Point(item2.rect.getLocation().x + (intersect.width/2)+1, item2.rect.getLocation().y + (intersect.width/2)-1) );
							//if(item1.getClass().equals(CountryListItem.class) && item2.getClass().equals(CountryListItem.class))
							{
								//ULBACOUNTRY c1 = ((CountryListItem)item1).getCountry();
								//ULBACOUNTRY c2 = ((CountryListItem)item2).getCountry();
								boolean c1up = true, c1left = true;
								boolean move_x = false;
								if(c1.getWeightpoint().getLon() > c2.getWeightpoint().getLon())
									c1left = false;
								if(c1.getWeightpoint().getLat() < c2.getWeightpoint().getLat())
									c1up = false;
								if(intersect.width < intersect.height)
									move_x = true;
								int movex = intersect.width;
								int movey = intersect.height;
								if(move_x)
								{
									c1.getWeightpointScreen().setLon(c1.getWeightpointScreen().getLon() + movex * (c1left ? -1 : 1));
									c2.getWeightpointScreen().setLon(c2.getWeightpointScreen().getLon() + movex * (c1left ? 1 : -1));
								}
								else
								{
									c1.getWeightpointScreen().setLat(c1.getWeightpointScreen().getLat() + movey * (c1up ? -1 : 1));
									c2.getWeightpointScreen().setLat(c2.getWeightpointScreen().getLat() + movey * (c1up ? 1 : -1));
								}

								//log.debug(c1.getSzName() + " " + c1.getWeightpointScreen().getLon() + " " + c1.getWeightpointScreen().getLat());
							}
							/*if(item2.getClass().equals(CountryListItem.class))
							{
								ULBACOUNTRY c = ((CountryListItem)item2).getCountry();
								c.getWeightpointScreen().setLon(c.getWeightpointScreen().getLon() + intersect.width);
								c.getWeightpointScreen().setLat(c.getWeightpointScreen().getLat() + intersect.height);
							}*/
							b_newround = true;
							break;
							//continue;
						}
					}
				}
				if(b_newround)
					continue;
				b_newround = false;
			}
		}
		//log.debug("Iterations=" + iterations);
		
	}

}

class SendListPanel extends DefaultPanel {

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
