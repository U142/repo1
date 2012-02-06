package no.ums.pas.plugins.centric;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.component.MapController;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.Variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.mail.Smtp;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.address_search.AddressSearchCtrl;
import no.ums.pas.core.menus.*;
import no.ums.pas.core.menus.MainSelectMenu.MainMenuBar;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.ws.WSPowerup;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.WMSLayerSelectorPanel;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PLMNShape;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.pluginbase.DefaultPasScripting;
import no.ums.pas.plugins.centric.address_search.CentricAddressSearchCtrl;
import no.ums.pas.plugins.centric.send.CentricProjectDlg;
import no.ums.pas.plugins.centric.status.CentricStatus;
import no.ums.pas.plugins.centric.status.CentricStatusController;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.UBBNEWS;
import no.ums.ws.common.USYSTEMMESSAGES;
import no.ums.ws.common.cb.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.common.cb.CBSENDINGRESPONSE;
import org.geotools.data.ows.Layer;

import javax.swing.*;
import javax.swing.Timer;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class CentricPasScripting extends DefaultPasScripting {

    private static final Log log = UmsLog.getLogger(CentricPasScripting.class);

    WMSLayerSelectorPanel wms_layer_selector = new WMSLayerSelectorPanel();

    MenuTimer m_menutimer = new MenuTimer();

    class MenuTimer extends Timer implements ActionListener {
        boolean flip = false;
        Color c1 = new Color(230, 100, 100, 250);
        Color c2 = new Color(0, 0, 0, 250);

        Color col_text = c2;
        Color col_bg = c1;

        public MenuTimer() {
            super(500, null);
            this.addActionListener(this);
        }

        public void resetColors() {
            col_text = c2;
            col_bg = c1;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //flip colors
            flip = !flip;
            if (PAS.TRAINING_MODE) {
                col_text = (flip ? c1 : c2);
                col_bg = (flip ? c2 : c1);
                PAS.get_pas().get_mainmenu().repaint();
            } else {
                col_text = c2;
                col_bg = c1;
            }
        }

    }

    private final AddressSearch addressSearch = new CentricAddressSearch();
    private final AddressSearchCtrl addressSearchGui = new CentricAddressSearchCtrl();

    @Override
    public void startPlugin() {
        super.startPlugin();
        log.debug("CentricPasScripting loaded");
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public AddressSearch getAddressSearch() {
    	return this.addressSearch;
    }
    
    


	@Override
	public AddressSearchCtrl getAddressSearchGui() {
		return addressSearchGui;
	}

	@Override
    protected void setSubPluginNames() {
        log.debug("***Using Plugins (CentricPasScripting)***");
        log.debug((this.plugin_AddressSearch = "no.ums.pas.plugins.centric.CentricAddressSearch"));
    }

    @Override
    public boolean onBeforeLogon() {
        super.onBeforeLogon();
        boolean b = new DisclaimerDialog().isConfirmed();
        if (!b) {
            log.debug("User denied Disclaimer");
            System.exit(0);
        }
        log.debug("User accepted Disclaimer");
        return b;
    }


    @Override
    public boolean onAfterLogon() {
        super.onAfterLogon();
        return true;
    }

    @Override
    public boolean onShowMainWindow() {
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
                          log.debug("TRALLALA");
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
    private JMenu menu_addressbook;
    private JMenu menu_trainingmode;


    
    
    @Override
	public boolean onMainMenuButtonClicked(MainMenu menu, ButtonGroup btnGroup) {
		menu.change_buttoncolor(menu.get_btn_pan(), false);
		menu.change_buttoncolor(menu.get_btn_zoom(), false);
		menu.change_buttoncolor(menu_btn_draw_ellipse, false);
		menu.change_buttoncolor(menu_btn_draw_polygon, false);
		
		// For native GUI
		menu.get_btn_pan().setSelected(false);
		menu.get_btn_zoom().setSelected(false);
		menu_btn_draw_ellipse.setSelected(false);
		menu_btn_draw_polygon.setSelected(false);
		
    	switch(Variables.getMapFrame().get_mode())
		{
			case PAN:
				menu.get_btn_pan().setSelected(true);
			case PAN_BY_DRAG:
				menu.change_buttoncolor(menu.get_btn_pan(), true);
				menu.get_btn_pan().setSelected(true);
				break;
			case ZOOM:
				menu.change_buttoncolor(menu.get_btn_zoom(), true);
				menu.get_btn_zoom().setSelected(true);
				break;
			case SENDING_ELLIPSE:
			case SENDING_ELLIPSE_POLYGON:
				menu.change_buttoncolor(menu_btn_draw_ellipse, true);
				menu_btn_draw_ellipse.setSelected(true);
				break;
			case SENDING_POLY:
				menu.change_buttoncolor(menu_btn_draw_polygon, true);
				menu_btn_draw_polygon.setSelected(true);
				break;
		}
		return true;
    }

	@Override
    public boolean onAddMainMenuButtons(MainMenu menu) {
        menu.set_gridconst(menu.inc_xpanels(), 0, 15, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu.get_selectmenu().get_bar(), menu.m_gridconst);

        menu.reset_xpanels();

        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu.get_btn_pan(), menu.m_gridconst);
        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu.get_btn_zoom(), menu.m_gridconst);
        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu.get_btn_search(), menu.m_gridconst);
        menu.get_btn_search().setEnabled(true); //IDDIATTS

        JButton btn_goto_restriction = new JButton(Localization.l("common_navigate_home"));
        btn_goto_restriction.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
        btn_goto_restriction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PAS.get_pas().actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).getFullBBox(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
            }
        });

        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(btn_goto_restriction, menu.m_gridconst);

        menu.add_spacing(DefaultPanel.DIR_HORIZONTAL, 30);

        
        menu_btn_draw_polygon = new JButton(Localization.l("main_sending_type_polygon"));
        menu_btn_draw_polygon.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
        menu_btn_draw_polygon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Variables.getMapFrame().set_active_shape(new PolygonStruct(ShapeStruct.DETAILMODE.SHOW_POLYGON_FULL, 100000.0));
                Variables.getMapFrame().set_mode(MapFrame.MapMode.SENDING_POLY);
                PAS.get_pas().repaint();
            }
        });

        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu_btn_draw_polygon, menu.m_gridconst);

        menu_btn_draw_ellipse = new JButton(Localization.l("main_sending_type_ellipse"));
        menu_btn_draw_ellipse.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
        menu_btn_draw_ellipse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Variables.getMapFrame().set_active_shape(new PolygonStruct(ShapeStruct.DETAILMODE.SHOW_POLYGON_FULL, 100000.0));
                Variables.getMapFrame().set_mode(MapFrame.MapMode.SENDING_ELLIPSE_POLYGON);
                PAS.get_pas().repaint();
            }
        });
        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu_btn_draw_ellipse, menu.m_gridconst);

        menu_btn_draw_plmn = new JButton(Localization.l("main_sending_type_national"));
        menu_btn_draw_plmn.setPreferredSize(new Dimension(MainMenu.BTN_SIZE_WIDTH, MainMenu.BTN_SIZE_HEIGHT));
        menu_btn_draw_plmn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Variables.getMapFrame().set_mode(MapFrame.MapMode.PAN);
                Variables.getMapFrame().set_active_shape(new PLMNShape());
                PAS.get_pas().repaint();
            }
        });
        menu.set_gridconst(menu.inc_xpanels(), 1, 1, 1, GridBagConstraints.NORTHWEST);
        menu.add(menu_btn_draw_plmn, menu.m_gridconst);
        
        menu.get_btn_group_navigation().add(menu_btn_draw_polygon);
        menu.get_btn_group_navigation().add(menu_btn_draw_ellipse);

        
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
    public boolean onAddMainSelectMenu(MainMenuBar menu) {
        StatusActions.EXPORT.setEnabled(false);
        ViewOptions.TOGGLE_HOUSES.setSelected(false);

        final JMenu file = menu.add(new JMenu(Localization.l("mainmenu_file")));
        //file.add(FileMenuActions.NEW_SENDING);
        file.add(FileMenuActions.OPEN_PROJECT);
        file.add(FileMenuActions.CLOSE_PROJECT);
        file.addSeparator();
        //file.add(FileMenuActions.FILE_IMPORT);
        //file.add(FileMenuActions.PRINT_MAP);
        //file.add(FileMenuActions.SAVE_MAP);
        file.add(FileMenuActions.EXIT);

        JMenu addressBook = menu.add(new JMenu(Localization.l("common_address_book")));
        addressBook.add(menu.get_item_address_book());
        menu.get_item_address_book().setEnabled(false);
        //menu_trainingmode.add(menu.get_item_training_mode());
		menu.add((menu_trainingmode= new JMenu(Localization.l("mainmenu_trainingmode"))));
		menu_trainingmode.add(menu.get_item_training_mode());
        final JMenu help = menu.add(new JMenu(Localization.l("mainmenu_help")));
        help.add(OtherActions.HELP_ABOUT);
        //help.add(OtherActions.SHOW_CONTACT_INFO);
        return true;
    }

    @Override
	public boolean onHelpAbout() {
		return super.onHelpAbout();
	}

	@Override
    public boolean onAddSendOptionToolbar(SendOptionToolbar toolbar) {
        //CentricSendOptionToolbar ctoolbar = new CentricSendOptionToolbar(new SendObject(PAS.get_pas(),PAS.get_pas().get_pasactionlistener()),PAS.get_pas().get_pasactionlistener(),toolbar.get_sendingid());
        toolbar.show_buttons(
                SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_ |
                        SendOptionToolbar.BTN_SENDINGTYPE_ELLIPSE_ |
                        SendOptionToolbar.BTN_OPEN_ |
                        SendOptionToolbar.TXT_RECIPIENTTYPES_ |
                        SendOptionToolbar.BTN_ADRTYPES_NOFAX_,
                true);
        return super.onAddSendOptionToolbar(toolbar);
    }


    @Override
    protected boolean onHandleSystemMessages(USYSTEMMESSAGES sysmsg) {
        final List<UBBNEWS> news = sysmsg.getNews().getNewslist().getUBBNEWS();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < news.size(); i++) {
                    UBBNEWS bbnews = news.get(i);
                    //systemmessagepanel.list.getDefaultModel().addOnTop(bbnews);
                    systemmessagepanel.list.getDefaultModel().handleMessage(bbnews);
                }
                systemmessagepanel.list.getDefaultModel().sort();
            }
        });
        return true;
    }


    @Override
	public int getSystemMessagesPollInterval() {
		return 5;
	}


	class SystemMessagesPanel extends DefaultPanel implements ComponentListener {

        class UMSListModel extends DefaultListModel {
            public void sort() {
                Object[] list = this.toArray();
                if (list.length <= 1)
                    return;
                UBBNEWS tmp;
                for (int i = 0; i < list.length; i++) {
                    UBBNEWS b1 = (UBBNEWS) list[i];
                    for (int j = i + 1; j < list.length; j++) {
                        UBBNEWS b2 = (UBBNEWS) list[j];
                        //if(b1.getLTimestampDb()<b2.getLTimestampDb())
                        boolean b_doswitch = false;
                        if (b1.getLIncidentStart() < b2.getLIncidentStart())
                            b_doswitch = true;
                        else if (b1.getLIncidentStart() == b2.getLIncidentStart()) {
                            if (b1.getLTimestampDb() < b2.getLTimestampDb())
                                b_doswitch = true;
                        }
                        if (b_doswitch) {
                            tmp = b1;
                            list[i] = b2;
                            list[j] = tmp;
                            i = i - 1;
                            break;
                        }
                    }
                }
                for (int i = 0; i < list.length; i++) {
                    UBBNEWS bbn = (UBBNEWS) list[i];
                    this.setElementAt(bbn, i);
                    //log.debug("setElementAt " + i + " " + bbn.getLNewspk());
                    recordset.put(genHashKey(bbn), bbn);
                }
            }

            Hashtable<Long, Object> recordset = new Hashtable<Long, Object>();

            public void handleMessage(UBBNEWS b) {
                if (b.getFActive() >= 1) //insert/update
                {
                    //log.debug("newspk="+b.getLNewspk());
                    if (recordset.containsKey(genHashKey(b))) {
                        update(b);
                    } else {
                        add(0, b);//addOnTop(b);
                    }
                } else //remove
                {
                    if (recordset.containsKey(genHashKey(b))) {
                        remove(b);
                    }
                }
            }

            protected long genHashKey(UBBNEWS b) {
                return b.getLNewspk();
            }

            protected void addOnTop(Object arg1) {

                UBBNEWS news = (UBBNEWS) arg1;
                if (news.getFActive() >= 1)
                    this.add(0, arg1);
            }

            protected void remove(UBBNEWS b) {
                UBBNEWS original = (UBBNEWS) recordset.get(genHashKey(b));
                int index = super.indexOf(original);
                if (index >= 0) {
                    super.remove(index);
                    if (recordset.containsKey(genHashKey(b))) {
                        recordset.remove(genHashKey(b));
                        log.debug("newspk " + b.getLNewspk() + " removed");
                    }
                }
            }

            protected void update(UBBNEWS b) {
                UBBNEWS original = (UBBNEWS) recordset.get(genHashKey(b));
                int index = super.indexOf(original);
                if (index >= 0) {
                    super.set(index, b);
                    log.debug("newspk " + b.getLNewspk() + " updated");
                    recordset.put(genHashKey(b), b);
                }
            }

            @Override
            public void add(int arg0, Object arg1) {
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
                                log.debug("newspk " + original.getLNewspk() + " updated");
                            }
                            else
                            {
                                //to be deleted
                                super.remove(n);
                                recordset.remove(original.getLNewspk());
                                log.debug("newspk " + original.getLNewspk() + " deleted");
                            }
                        }
                        else
                        {
                            log.debug("news " + original + " not found in list");
                        }
                    }
                    else*/
                {
                    recordset.put(genHashKey((UBBNEWS) arg1), arg1);
                    //list.getDefaultModel().add(arg0, arg1);
                    super.add(arg0, arg1);
                    log.debug("newspk " + ((UBBNEWS) arg1).getLNewspk() + " inserted");
                }
            }

            /*@Override
               public void addElement(Object arg0) {
                   //super.addElement(arg0);
                   add(0, arg0);
               }*/

        }

        class MessageList extends JList {
            MessageListRenderer renderer = new MessageListRenderer();

            MessageList() {
                super(new UMSListModel());
                setCellRenderer(renderer);
                setVisibleRowCount(1);
                //setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
            }

            UMSListModel getDefaultModel() {
                return (UMSListModel) this.getModel();
            }

            class MessageListRenderer extends DefaultPanel implements ListCellRenderer {
                protected JLabel lbl_renderer;

                MessageListRenderer() {
                    super();
                    lbl_renderer = new JLabel("");
                }

                @Override
                public Component getListCellRendererComponent(JList list,
                                                              Object value, int index, boolean isSelected,
                                                              boolean cellHasFocus) {

                    int width = MessageList.this.getWidth();

                    int scrollwidth = 0;
                    if (scrollpane.getVerticalScrollBar().isVisible())
                        scrollwidth = scrollpane.getVerticalScrollBar().getWidth();
                    width -= scrollwidth;
                    if (value.getClass().equals(String[].class)) {

                        String[] vals = (String[]) value;
                        lbl_renderer.setText(vals[0] + "    " + vals[1]);
                        //cols[0].setText(vals[0]);
                        //cols[1].setText(vals[1]);
                    } else if (value.getClass().equals(UBBNEWS.class)) {
                        UBBNEWS news = (UBBNEWS) value;
                        //lbl_renderer.setText(no.ums.pas.ums.tools.TextFormat.format_datetime(news.getLTimestampDb()) + "    " + news.getNewstext().getSzNews());
                        String text_to_write = news.getNewstext().getSzNews();
//                        int text_width = lbl_renderer.getFontMetrics(lbl_renderer.getFont()).stringWidth(text_to_write);
                        //if(text_width>=width)
                        //	text_to_write = text_to_write.substring(0, Math.min(text_to_write.length()-1, 70));
                        lbl_renderer.setText(text_to_write);
                        lbl_renderer.setPreferredSize(new Dimension(width, getHeight()));
                    } else {
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
                if (location >= 0) {
                    UBBNEWS b = (UBBNEWS) list.getDefaultModel().getElementAt(location);
                    String html = "<html><table width=300>";
                    //html += "<tr><td colspan=1><b>" + PAS.l("common_updated") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLTimestampDb()) + "</td></tr>";
                    html += "<tr><td colspan=1><b>" + Localization.l("common_start") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLIncidentStart()) + "</td></tr>";
                    html += "<tr><td colspan=1><b>" + Localization.l("common_end") + ":</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(b.getLIncidentEnd()) + "</td></tr>";
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

        SystemMessagesPanel() {
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
            if (e.getSource().equals(btn_expand)) {
                //expand button clicked
                expanded = !expanded;
                if (expanded)
                    n_current_height = n_max;
                else {
                    n_current_height = n_min;
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
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
            n_min = 22;
            list.setFixedCellHeight(n_min);
//            int w = getWidth();
//            int btn_size = getWantedHeight();
            scrollpane.setPreferredSize(new Dimension(getWidth() - n_min, getHeight()));
            btn_expand.setPreferredSize(new Dimension(n_min, getHeight()));
//            int scroll_width = 0;
//            int scroll_height = 0;
            if (this.scrollpane.getVerticalScrollBar().isVisible()) {
//                scroll_width = this.scrollpane.getVerticalScrollBar().getWidth();
            }
            if (this.scrollpane.getHorizontalScrollBar().isVisible()) {
//                scroll_height = this.scrollpane.getHorizontalScrollBar().getHeight();
                //set_height = n_current_height+scroll_height;
            }
//            scroll_width -= 5;
            SystemMessagesPanel.this.setPreferredSize(new Dimension(getWidth(), set_height));
            scrollpane.revalidate();
            revalidate();

            super.componentResized(e);
        }
    }

    class UserInfoPane extends DefaultPanel implements ComponentListener {
        @Override
        public int getWantedHeight() {
            return 25;
        }

        StdTextLabel lbl_userinfo = new StdTextLabel("");

        UserInfoPane() {
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

        public void updateUserInfo(UserInfo ui) {
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
            switch (ui.get_current_department().get_userprofile().get_send()) {
                case 1: //regional or regional super user
                    if (member_of_dept == 1) {
                        str += Localization.l("logon_rights_regional_user") + " - ";//" - $Regional user - ";
                    }
                    else {
                        str += Localization.l("logon_rights_regional_superuser") + " - "; //" - $Regional Super User - ";
                    }
                    for (int i = 0; i < ui.get_departments().size(); i++)
                        str += " \"" + ((DeptInfo) ui.get_departments().get(i)).get_deptid() + "\"";
                    break;
                case 2: //national user
                    str += Localization.l("logon_rights_national_user"); //" - $Regional Super User - ";
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
//            int h = getHeight();
            //if(w<=0 || h<=0)
            //	return;
            //if(w>5000 || h>5000)
            //	return;
//            int x = w;
            lbl_userinfo.setPreferredSize(new Dimension(w, getWantedHeight()));
            lbl_userinfo.revalidate();
            super.componentResized(e);
        }

    }

    class Northpane extends DefaultPanel implements ComponentListener {
        Northpane() {
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

    class CenterPane extends DefaultPanel implements ComponentListener {
        CenterPane() {
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
            DefaultPanel p = (DefaultPanel) e.getComponent();
            int w = p.getWidth();
            int h = p.getHeight();
            int sysmsg_height = systemmessagepanel.getWantedHeight();
            int userinfo_height = userinfopane.getWantedHeight();
            PAS.get_pas().get_mappane().set_dimension(new Dimension(w, h - sysmsg_height - userinfo_height));
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
    public boolean onAddPASComponents(final PAS p) {

        log.debug("onAddPASComponents");

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
    public boolean onSetInitialMapBounds(Navigation nav, UserInfo ui) {
        nav.setNavigation(ui.get_departments().get_combined_restriction_shape().get(0).getFullBBox(), false);
        MapController.zoomLevel = 7;
        return true;
    }

    @Override
    public boolean onStartParm() {
        //return super.onStartParm();
        log.debug("onStartParm - PARM is invalid in this plugin");
        return false;
    }

    @Override
    public boolean onCloseParm() {
        //return super.onCloseParm();
        log.debug("onCloseParm - PARM is invalid in this plugin");
        return false;
    }

    @Override
    public boolean onRefreshParm() {
        //return super.onRefreshParm();
        log.debug("onRefreshParm - PARM is invalid in this plugin");
        return false;
    }

    @Override
    public boolean onDepartmentChanged(PAS pas) {
        super.onDepartmentChanged(pas);
        userinfopane.updateUserInfo(pas.get_userinfo());
        //PAS.get_pas().setAppTitle("UMS/Centric - " + pas.get_userinfo().get_current_department().get_deptid());
        return true;
    }

    @Override
    public boolean onSetAppTitle(PAS pas, String s, UserInfo userinfo) {
//        boolean trainingmode = IsInTrainingMode(userinfo);
        log.debug("onSetAppTitle");
        String maintitle = Localization.l("common_app_title");
        CentricStatusController sc = (CentricStatusController) PAS.get_pas().get_statuscontroller();
        if (sc != null) {
            CentricStatus status = sc.getOpenedStatus();
            if (status != null) {
                CBPROJECTSTATUSRESPONSE event = status.getResultSet();
                if (event != null) {
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
              log.warn(e.getMessage(), e);
              return null;
          }*/
    }

    @Override
    public LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader) {
        try {
            Class<LookAndFeel> cl = null;
            switch (operatingSystem) {
                case MAC:
                    cl = (Class<LookAndFeel>) classloader.loadClass("javax.swing.plaf.mac.MacLookAndFeel");
                    break;
                case UNIX:
                    cl = (Class<LookAndFeel>) classloader.loadClass("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    break;
                case WIN:
                    cl = (Class<LookAndFeel>) classloader.loadClass("no.ums.pas.pluginbase.defaults.DefaultWindowsLookAndFeel"); //"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    if (uidefaults_initial != null) {
                        ArrayList<Object> defaults = new ArrayList<Object>();
                        Enumeration<Object> keys = uidefaults_initial.keys();
                        while (keys.hasMoreElements()) {
                            Object key = keys.nextElement();
                            defaults.add(key);
                            defaults.add(uidefaults_initial.get(key));
                        }
                        UIManager.getDefaults().putDefaults(defaults.toArray());
                    }
                    //cl = (Class<LookAndFeel>)classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
            }
            LookAndFeel laf = (LookAndFeel) cl.newInstance();
            UIManager.setLookAndFeel(laf);
            JDialog.setDefaultLookAndFeelDecorated(false);
            JFrame.setDefaultLookAndFeelDecorated(false);
            if (PAS.get_pas() != null)
                SwingUtilities.updateComponentTreeUI(PAS.get_pas());
            return laf;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        try {//default to crossplatform LAF

            log.debug("Loading cross platform LAF");
            Class cl = classloader.loadClass(UIManager.getCrossPlatformLookAndFeelClassName());
            LookAndFeel laf = (LookAndFeel) cl.newInstance();
            UIManager.setLookAndFeel(laf);
            JDialog.setDefaultLookAndFeelDecorated(true);
            JFrame.setDefaultLookAndFeelDecorated(true);
            SwingUtilities.updateComponentTreeUI(PAS.get_pas());

            return laf;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean onSetUserLookAndFeel(Settings settings, final UserInfo userinfo) {
        try {
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

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
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
    public boolean onWmsLayerListLoaded(List<Layer> layers, List<String> check) {
        //wms_layer_selector.populate(layers, check);
        return true;
    }


    @Override
    public boolean onSoapFaultException(UserInfo info, SOAPFaultException e) {
        return super.onSoapFaultException(info, e);
    }

    @Override
    public boolean onTrainingMode(boolean b) {
        if (b) {
            onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
            m_menutimer.start();
        } else {
            onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
            m_menutimer.stop();
            m_menutimer.resetColors();
        }
        try {
            CentricVariables.getCentric_send().trainingModeChanged();
            Variables.getStatusController().trainingModeChanged();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        PAS.get_pas().repaint();
        //onSetUserLookAndFeel(PAS.get_pas().get_settings(), PAS.get_pas().get_userinfo());
        return super.onTrainingMode(b);
    }


    @Override
    public boolean onAfterPowerUp(LogonDialog dlg, WSPowerup ws) {
        if (ws.getResult() == WSRESULTCODE.OK) {
            dlg.set_errortext(Localization.l("logon_ws_active"), false);
        }
        else {
            dlg.set_errortext(Localization.l("logon_ws_inactive"));
        }
        try {
            dlg.setMaxLogonTries(ws.getResponse().getLMaxLogontries());
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public boolean onLogonAddControls(LogonPanel p) {
        int verticalspacing = 10;
        p.m_gridconst.fill = GridBagConstraints.HORIZONTAL;
        p.m_gridconst.anchor = GridBagConstraints.CENTER;

        p.add_spacing(DefaultPanel.DIR_HORIZONTAL, 75);

        p.set_gridconst(3, p.inc_panels(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getLblCompId(), p.m_gridconst);
        p.set_gridconst(5, p.get_panel(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getCompId(), p.m_gridconst);

        p.add_spacing(p.DIR_VERTICAL, verticalspacing);

        p.set_gridconst(3, p.inc_panels(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getLblUserId(), p.m_gridconst);
        p.set_gridconst(5, p.get_panel(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getUserId(), p.m_gridconst);

        p.add_spacing(p.DIR_VERTICAL, verticalspacing);

        p.set_gridconst(3, p.inc_panels(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getLblPasswd(), p.m_gridconst);
        p.set_gridconst(5, p.get_panel(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getPasswd(), p.m_gridconst);

        p.add_spacing(p.DIR_VERTICAL, verticalspacing);


        p.set_gridconst(3, p.inc_panels(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(p.getBtnSubmit(), p.m_gridconst);

        JButton btn_cancel = new JButton(Localization.l("common_cancel"));
        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        p.set_gridconst(5, p.get_panel(), 1, 1, GridBagConstraints.CENTER); //x,y,sizex,sizey
        p.add(btn_cancel, p.m_gridconst);


        p.set_gridconst(0, p.inc_panels(), 7, 1, GridBagConstraints.CENTER);
        p.add(p.getLblError(), p.m_gridconst);


        //p.set_gridconst(0,p.inc_panels(),7,1);
        //p.add(p.getNSList(), p.m_gridconst);
        return true;

    }

    @Override
    public boolean onCustomizeLogonDlg(LogonDialog dlg) {
        dlg.setSize(new Dimension(400, 200));
        dlg.get_logonpanel().getLblCompId().setVisible(false);
        dlg.get_logonpanel().getCompId().setVisible(false);
        dlg.get_logonpanel().getCompId().setEditable(false);
        dlg.get_logonpanel().getBtnSubmit().setText(Localization.l("common_ok"));

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
              log.warn(e.getMessage(), e);
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
        //Color c1 = new Color(230, 100, 100, 250);
        //Color c2 = new Color(0, 0, 0, 250);

        Color ctext = m_menutimer.col_text;
        Color cbg = m_menutimer.col_bg;

        g.setFont(UIManager.getFont("InternalFrame.titleFont"));

        String str = Localization.l("common_live").toUpperCase();
        if (IsInTrainingMode()) {
            str = Localization.l("mainmenu_trainingmode").toUpperCase();
        }
        int strwidth = g.getFontMetrics().stringWidth(str);
        int x = bar.getWidth() / 2 - strwidth / 2;
        int y = bar.getHeight() / 2 - 9;
        int w = strwidth;
        int h = bar.getHeight() / 2 + 5;
        g.setColor(cbg);
        g.fillRoundRect(x - 5, y, w + 10, h, 2, 2);
        g.setColor(ctext);
        g.drawRoundRect(x - 5, y, w + 10, h, 2, 2);
        g.drawString(str, x, h);

        //HELPDESK
        g.setColor(Color.black);
        str = Localization.l("common_helpdesk_contact");
        strwidth = g.getFontMetrics().stringWidth(str);
        x = bar.getWidth() - strwidth - 20;
//        w = strwidth;
        //g.drawRoundRect(x-5, y, w+10, h, 2, 2);
        g.drawString(str, x, h);
        return super.onPaintMenuBarExtras(bar, g);
    }

    @Override
    public boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel) {
        boolean ret = true;
        //ret = super.onAddInfoTab(tab, panel);
        CentricSendOptionToolbar send = new CentricSendOptionToolbar();
        CentricVariables.setCentric_send(send);
        //((CentricEastContent)PAS.get_pas().get_eastcontent()).set_centricsend(send);
        tab.addTab(Localization.l("mainmenu_file_newsending"), null, send, Localization.l("main_parmtab_popup_generate_sending"));
        return ret;
    }

    @Override
    public boolean onMapCalcNewCoords(Navigation nav, PAS p) {
        //return super.onMapCalcNewCoords(nav, p);
        p.get_statuscontroller().calcHouseCoords();
        if (p.get_statuscontroller().get_sendinglist() != null) {
            for (int i = 0; i < p.get_statuscontroller().get_sendinglist().size(); i++) {
                try {
                    if (p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape() != null)
                        p.get_statuscontroller().get_sendinglist().get_sending(i).get_shape().calc_coortopix(nav);
                } catch (Exception e) {

                }
            }
        }
        try {
            DeptArray depts = p.get_userinfo().get_departments();
            for (int i = 0; i < depts.size(); i++) {
                ((DeptInfo) depts.get(i)).CalcCoorRestrictionShapes();
            }
            List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).calc_coortopix(p.get_navigation());
            }
            //get_pas().get_userinfo().get_current_department().CalcCoorRestrictionShapes();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        try {
            p.get_mappane().get_active_shape().calc_coortopix(PAS.get_pas().get_navigation());
        } catch (Exception e) {

        }
        /*for(int i=0; i < getShapesToPaint().size(); i++)
          {
              if(getShapesToPaint().get(i)!=null)
                  getShapesToPaint().get(i).calc_coortopix(nav);
          }*/
        Enumeration<ShapeStruct> en = getShapesToPaint().elements();
        while (en.hasMoreElements()) {
            en.nextElement().calc_coortopix(nav);
        }


        return true;
    }

    @Override
    public boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p) {
        try {

            DeptArray depts = p.get_userinfo().get_departments();
            //depts.ClearCombinedRestrictionShapelist();
            //depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
            //depts.test();
            for (int i = 0; i < depts.size(); i++) {
                ((DeptInfo) depts.get(i)).drawRestrictionShapes(g, nav);
            }
            List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).draw(g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), false, true, false, null, true, true, 2, false);
            }

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        //only paint status shapes if in status tab
        if (CentricEastContent.getCurrentPanel() == CentricEastContent.PANEL_CENTRICSTATUS_) {
            try {
                Enumeration<ShapeStruct> en = getShapesToPaint().elements();
                while (en.hasMoreElements()) {
                    en.nextElement().draw(g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), false, true, false, null, true, true, 2, true);
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

        //only paint send shapes if in send tab
        if (CentricEastContent.getCurrentPanel() == CentricEastContent.PANEL_CENTRICSEND_) {
            try {
                boolean b_finalized = !p.get_mappane().get_active_shape().isEditable();
                boolean b_editmode = PAS.get_pas().get_mappane().isInPaintMode();
                p.get_mappane().get_active_shape().draw(g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), false, b_finalized, b_editmode, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, false);
            } catch (Exception e) {
            }
        }
        try {
            p.get_mappane().draw_pinpoint(g);
        } catch (Exception e) {
            Error.getError().addError("PASDraw", "Exception in draw_layers", e, 1);
        }

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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_T:
                //PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon().ellipseToRestrictionlines(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).typecast_polygon());
                Variables.getMapFrame().get_active_shape().typecast_polygon().ellipseToRestrictionlines(Variables.getUserInfo().get_departments().get_combined_restriction_shape().get(0).typecast_polygon());
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
        return new Dimension(1024, 700);
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
    public void onLocaleChanged(Locale from, Locale to) {
        //do nothing
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
        JOptionPane.showMessageDialog(PAS.get_pas(), Localization.l("common_helpdesk_contact"), Localization.l("common_contact_information"), JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    @Override
    public boolean onCloseProject() {
        try {
            ((CentricStatusController) Variables.getStatusController()).stopUpdates();
            PAS.pasplugin.clearShapesToPaint();
            PAS.get_pas().kickRepaint();
            ((CentricEastContent) PAS.get_pas().get_eastcontent()).remove_tab(CentricEastContent.PANEL_CENTRICSTATUS_);
            ((CentricEastContent) PAS.get_pas().get_eastcontent()).set_centricstatus(null);

            ((CentricSendOptionToolbar) ((CentricEastContent) PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).set_projectpk(0, "");
            //((CentricSendOptionToolbar)((CentricEastContent)PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).set_centricController(null);
            ((CentricSendOptionToolbar) ((CentricEastContent) PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSEND_)).get_reset().doClick();
            FileMenuActions.CLOSE_PROJECT.setEnabled(false);
            onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
            onSetInitialMapBounds(Variables.getNavigation(), PAS.get_pas().get_userinfo());
            PAS.get_pas().get_mappane().load_map(true);
            menu_trainingmode.setEnabled(true);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
	public boolean onStopStatusUpdates() {
        ((CentricStatusController) Variables.getStatusController()).stopUpdates();
		return super.onStopStatusUpdates();
	}

	@Override
    public boolean onOpenProject(Project project, long nFromNewRefno) {
        try {
            // Does the same thing as after sending a message
            CentricSendOptionToolbar csend = CentricVariables.getCentric_send();

            CBSENDINGRESPONSE res = new CBSENDINGRESPONSE(); // Just to use the same
            res.setLProjectpk(Long.parseLong(project.get_projectpk()));

            ((CentricStatusController) PAS.get_pas().get_statuscontroller()).openStatus(Long.parseLong(project.get_projectpk()), csend, nFromNewRefno);
            FileMenuActions.CLOSE_PROJECT.setEnabled(true);

            menu_trainingmode.setEnabled(false);


            return true;

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int onInvokeProject() {
        try {
            int answer = 0;
            if (((CentricEastContent) PAS.get_pas().get_eastcontent()).get_tab(CentricEastContent.PANEL_CENTRICSTATUS_) != null) {
                // Inform and close open status
                answer = confirmClosing();
                if (answer == JOptionPane.YES_OPTION)
                    if (onCloseProject())
                        return answer;
            }
            return answer;
        } catch (Exception e) {
            return 0;
        }
    }

    private int confirmClosing() {
        JFrame frame = get_frame();
        int answer;
        answer = JOptionPane.showConfirmDialog(frame, Localization.l("project_cb_ask_new_close_event"), null, JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return answer;
    }

    private JFrame get_frame() {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0, 0);
        p.setLocation(p.x, p.y + PAS.get_pas().getHeight() / 3);
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
        switch (CentricEastContent.getCurrentPanel()) {
            case CentricEastContent.PANEL_CENTRICSEND_:
                if (Variables.getMapFrame().get_active_shape() != null)
                    Variables.getMapFrame().get_active_shape().setEditable(true);
                Variables.getMapFrame().set_prev_paintmode();
                enableSendButtons(true);
                break;
            case CentricEastContent.PANEL_CENTRICSTATUS_:
                if (Variables.getMapFrame().get_active_shape() != null)
                    Variables.getMapFrame().get_active_shape().setEditable(false);
                Variables.getMapFrame().set_mode(MapFrame.MapMode.PAN);
                enableSendButtons(false);
                break;
        }
        PAS.get_pas().kickRepaint();
        return super.onEastContentTabClicked(e, pane);
    }

    private void enableSendButtons(boolean b) {
        menu_btn_draw_ellipse.setVisible(b);
        menu_btn_draw_polygon.setVisible(b);

        int send = Variables.getUserInfo().get_current_department().get_userprofile().get_send();
        menu_btn_draw_plmn.setVisible(b && send >= 2);
        //menu_btn_import.setVisible(b);
    }

    @Override
    public ProjectDlg onCreateOpenProjectDlg(JFrame parent,
                                             ActionListener callback, String cmdSave, boolean bNewsending) {
        return new CentricProjectDlg(parent, callback);
    }

    @Override
    public boolean onLockSending(SendOptionToolbar toolbar, boolean bLock) {
        CentricVariables.getCentric_send().lockSending(bLock);
        if (bLock)
            Variables.getMapFrame().set_mode(MapFrame.MapMode.PAN);
        else
            Variables.getMapFrame().set_prev_paintmode();
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

    @Override
    public List<String> onSendErrorMessages(String concatErrorlist,
                                            MailAccount account, Smtp.smtp_callback callback) {
        MailAccount newaccount = new MailAccount();
        newaccount.set_accountname("NL-Alert");
        newaccount.set_autodetected(false);
        newaccount.set_displayname("NL-Alert");
        newaccount.set_mailaddress("nlalert@ums.no");
        newaccount.set_mailserver("mail.ums.no");
        newaccount.set_port(25);
        List<String> arr_adr = new ArrayList<String>();
        arr_adr.add("mh@ums.no");

        final Smtp smtp = new Smtp(account.get_helo(), account.get_mailserver(), account.get_displayname(), arr_adr, "PAS error report", concatErrorlist, callback);
        PasApplication.getInstance().getExecutor().submit(smtp);
        //new MailCtrl(newaccount.get_helo(), newaccount.get_mailserver(), newaccount.get_port(), newaccount.get_displayname(), newaccount.get_mailaddress(), arr_adr, callback, "PAS error", concatErrorlist);
        return arr_adr;
    }

    @Override
    public Dimension getMinMapDimensions() {
        return new Dimension(20000, 20000);
    }
}