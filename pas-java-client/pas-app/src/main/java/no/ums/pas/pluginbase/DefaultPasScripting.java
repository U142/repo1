package no.ums.pas.pluginbase;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.TileData;
import no.ums.map.tiled.TileLookup;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.Variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.Logon;
import no.ums.pas.core.logon.LogonDialog;
import no.ums.pas.core.logon.Logon.Holder;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.UserInfo.SESSION_INACTIVE_REASON;
import no.ums.pas.core.mail.Smtp;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.address_search.AddressSearchCtrl;
import no.ums.pas.core.menus.FileMenuActions;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu.MainMenuBar;
import no.ums.pas.core.menus.NavigateActions;
import no.ums.pas.core.menus.OtherActions;
import no.ums.pas.core.menus.StatusActions;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.ws.WSDeleteProject;
import no.ums.pas.core.ws.WSDeleteProject.IDeleteProject;
import no.ums.pas.core.ws.WSDeleteStatus;
import no.ums.pas.core.ws.WSGetSystemMessages;
import no.ums.pas.core.ws.WSPowerup;
import no.ums.pas.core.ws.vars;
import no.ums.pas.core.ws.WSDeleteStatus.IDeleteStatus;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.localization.Localization;
import no.ums.pas.localization.UIParamLoader;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.MapLoader;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.pluginbase.defaults.DefaultAddressSearch;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.pas.send.SendPropertiesGIS;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Timeout;
import no.ums.pas.versioning.VersionInfo;
import no.ums.ws.common.PASVERSION;
import no.ums.ws.common.USYSTEMMESSAGES;
import no.ums.ws.pas.Pasws;

import org.geotools.data.ows.Layer;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class DefaultPasScripting extends AbstractPasScriptingInterface
{
    private static final Log log = UmsLog.getLogger(DefaultPasScripting.class);

    private final AddressSearch addressSearch = new DefaultAddressSearch();
    private final AddressSearchCtrl addressSearchGui = new AddressSearchCtrl();

    @Override
	public void onMapCellNotLoaded(Graphics g, TileLookup tileLookup,
			TileData tileData) {
	}

	@Override
	public void onMapCellError(Graphics g, TileLookup tileLookup,
			TileData tileData) {
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setColor(new Color(0, 0, 0, 128));
    	g2d.drawRect(tileData.getX(), tileData.getY(), tileData.getWidth()-1, tileData.getHeight()-1);

		Point center = new Point(tileData.getX() + tileData.getWidth()/2, tileData.getY() + tileData.getHeight()/2);
    	Image img = ImageFetcher.getImage("unknown_24.png");
    	g.drawImage(img, center.x - img.getWidth(null)/2, center.y - img.getHeight(null)/2, null);
	}

	@Override
    public void startPlugin() {
        super.startPlugin();
        log.debug("PAS_Scripting loaded");
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public AddressSearch getAddressSearch() {
    	return addressSearch;
    }
    
    

    @Override
	public AddressSearchCtrl getAddressSearchGui() {
    	return addressSearchGui;
	}

	@Override
	public boolean onAfterPowerUp(LogonDialog dlg, WSPowerup ws) {
		if(ws.getResult()==WSRESULTCODE.OK) {
			dlg.setExtendedTitleLangID("logon_ws_active");
        }
		else {
			dlg.setExtendedTitleLangID("logon_ws_inactive");
        }
		try {
			dlg.setMaxLogonTries(ws.getResponse().getLMaxLogontries());
		} catch(Exception e) {
            log.warn("Failed to set max logon tries", e);
        }
		return true;
	}

	@Override
	public boolean onBeforeLogon() {
		return true;
	}
	
	@Override
	public boolean onAfterLogon()
	{
		return true;
	}
	@Override
	public boolean onShowMainWindow()
	{
		return true;
	}
	@Override 
	public boolean onAddSendOptionToolbar(SendOptionToolbar toolbar)
	{
		return true;
	}

	
	
	@Override
	public boolean onMainMenuButtonClicked(MainMenu menu, ButtonGroup btnGroup) {
		menu.change_buttoncolor(menu.get_btn_pan(), false);
		menu.change_buttoncolor(menu.get_btn_zoom(), false);
		menu.change_buttoncolor(menu.get_btn_houseeditor(), false);
		menu.change_buttoncolor(menu.get_btn_showhousedetails(), false);
		
		// For native GUI
		menu.get_btn_pan().setSelected(false);
		menu.get_btn_zoom().setSelected(false);
		menu.get_btn_houseeditor().setSelected(false);
		menu.get_btn_showhousedetails().setSelected(false);
		
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
			case HOUSESELECT:
				menu.change_buttoncolor(menu.get_btn_showhousedetails(), true);
				menu.get_btn_showhousedetails().setSelected(true);
				break;
			case HOUSEEDITOR:
				menu.change_buttoncolor(menu.get_btn_houseeditor(), true);
				menu.get_btn_houseeditor().setSelected(true);
				break;
		}
		return true;
	}

	@Override
	public boolean onAddMainMenuButtons(MainMenu menu)
	{
		menu.set_gridconst(0, 0, 15, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_selectmenu().get_bar(), menu.m_gridconst);

		menu.set_gridconst(0, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_pan(), menu.m_gridconst);
		menu.set_gridconst(1, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_zoom(), menu.m_gridconst);
		menu.set_gridconst(2, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_navigate_home(), menu.m_gridconst);
		menu.set_gridconst(3, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_search(), menu.m_gridconst);
		menu.set_gridconst(4, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_houseeditor(), menu.m_gridconst);
		menu.set_gridconst(5, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_showhousedetails(), menu.m_gridconst);
		menu.get_selectmenu().get_bar().showHouseSelect(false);
		menu.set_gridconst(6, 1, 1, 1, GridBagConstraints.NORTHWEST);
		menu.add(menu.get_btn_zoom_to_world(), menu.m_gridconst);
		
		//menu.set_gridconst(6, 1, 1, 1, GridBagConstraints.NORTHWEST);
		//menu.add(menu.get_combo_mapsite(), menu.m_gridconst);
		return true;
	}

	@Override
	public boolean onAddMainSelectMenu(MainMenuBar menu) {
        final JMenu file = menu.add(new JMenu(Localization.l("mainmenu_file")));
        file.add(FileMenuActions.NEW_SENDING);
        file.add(FileMenuActions.OPEN_PROJECT);
        file.add(FileMenuActions.CLOSE_PROJECT);
        file.addSeparator();
        file.add(FileMenuActions.FILE_IMPORT);
        file.add(FileMenuActions.PRINT_MAP);
        file.add(FileMenuActions.SAVE_MAP);
        file.addSeparator();
        file.add(FileMenuActions.UPDATE_PASSWORD);
        file.addSeparator();
        file.add(FileMenuActions.EXIT);

        final JMenu navigate = menu.add(new JMenu(Localization.l("mainmenu_navigation")));
        navigate.add(NavigateActions.PAN);
        navigate.add(NavigateActions.ZOOM);
        navigate.add(NavigateActions.SEARCH);
        navigate.add(NavigateActions.MAP_GOTO_HOME);

        final JMenu view = menu.add(new JMenu(Localization.l("mainmenu_view")));
        view.add(ViewOptions.TOGGLE_POLYGON.asChecked());
        //view.add(ViewOptions.TOGGLE_STATUSCODES.asChecked());
        view.add(ViewOptions.TOGGLE_HOUSES.asChecked());
        view.add(ViewOptions.TOGGLE_SEARCHPOINTS.asChecked());

        final JMenu settings = menu.add(new JMenu(Localization.l("mainmenu_settings")));
        settings.add(OtherActions.SHOW_SETTINGS);
        //settings.add(OtherActions.SHOW_MESSAGELIB);
        //if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4) {
        //    settings.add(OtherActions.SHOW_MESSAGELIB);
        //}
        settings.addSeparator();
        settings.add(OtherActions.SET_DEPARTMENT_MAPBOUNDS);

        final JMenu status = menu.add(new JMenu(Localization.l("mainmenu_status")));
        status.add(StatusActions.OPEN);
        status.add(StatusActions.EXPORT);

        final JMenu statusUpdates = (JMenu) status.add(new JMenu(Localization.l("mainmenu_status_updates")));
        ButtonGroup group = new ButtonGroup();
        group.add(statusUpdates.add(StatusActions.MANUAL_UPDATE.asRadio()));
        group.add(statusUpdates.add(StatusActions.AUTOMATIC_UPDATE.asRadio()));

        group = new ButtonGroup();
        statusUpdates.addSeparator();
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.SECONDS, 5).asRadio()));
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.SECONDS, 10).asRadio()));
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.SECONDS, 20).asRadio()));
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.SECONDS, 30).asRadio()));
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.MINUTES, 1).asRadio()));
        group.add(statusUpdates.add(new StatusActions.UpdateInterval(TimeUnit.MINUTES, 5).asRadio()));

        
        final JMenu parm = menu.add(new JMenu(Localization.l("mainmenu_parm")));
        parm.add(OtherActions.PARM_START);
        parm.add(OtherActions.PARM_REFRESH);
        parm.add(OtherActions.PARM_CLOSE);

		menu.add(menu.get_dept());

        final JMenu help = menu.add(new JMenu(Localization.l("mainmenu_help")));
        help.add(OtherActions.HELP_ABOUT);
        help.add(OtherActions.SHOW_CONTACT_INFO);
        help.add(OtherActions.DOWNLOAD_DOCUMENTATION);
        help.add(OtherActions.HELP_SHOWLOG);
		return true;
	}
	
	@Override
	public boolean onAddPASComponents(PAS p)
	{
		p.add(p.get_mappane(), BorderLayout.CENTER);
		p.add(p.get_mainmenu(), BorderLayout.NORTH);
		p.add(p.get_southcontent(), BorderLayout.SOUTH);
		p.add(p.get_eastcontent(), BorderLayout.EAST);	

		return true;
	}
	
	@Override
	public boolean onSetInitialMapBounds(Navigation nav, UserInfo ui)
	{
		switch(ui.get_current_department().get_pas_rights())
		{
		case 4: //TAS
			ui.set_nav_init(Navigation.NAV_WORLD);
			break;
		}
		nav.setNavigation(ui.get_nav_init());

		return true;
	}
	
	@Override
    public boolean onStartParm() {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    long start = System.currentTimeMillis();
                    PAS.get_pas().waitForFirstMap();

                    log.debug("Waited %d seconds for map to load", (System.currentTimeMillis() - start) / 1000);
                    log.debug(String.format(Locale.ENGLISH, "Waited %d seconds for map to load", (System.currentTimeMillis() - start) / 1000));
                    if (PAS.get_pas().get_parmcontroller() != null) {
                        return null;
                    }
                } catch (Exception err) {
                    log.warn("Failed to start parm", err);
                }
                return null;
            }

            @Override
            protected void done() {
            	if(Variables.getUserInfo().get_current_department().get_userprofile().get_parm_rights()>=1)
            	{
            		log.debug("Starting PARM");
	                PAS.get_pas().init_parmcontroller();
	                PAS.get_pas().get_parmcontroller().setExpandedNodes();
	                PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_PARM_);
	                OtherActions.PARM_START.setEnabled(false);
	                OtherActions.PARM_CLOSE.setEnabled(true);
            	}
            	else
            	{
            		log.debug("No PARM righs");
            	}
            }
        }.execute();
        return true;
    }
	
	@Override
	public boolean onCloseParm()
	{
		PAS.get_pas().close_parm(false);
        OtherActions.PARM_START.setEnabled(Variables.getUserInfo().get_current_department().get_userprofile().get_parm_rights()>=1);
        OtherActions.PARM_CLOSE.setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onRefreshParm()
	{
		PAS.get_pas().get_parmcontroller().getUpdateXML().saveProject();
		return true;
	}
	@Override
	public boolean onSetAppTitle(PAS pas, String s, final UserInfo userinfo)
	{
		if(s.length()==0) {
            s = Localization.l("common_app_title");
        }
		pas.setMainTitle(s + " - " + pas.get_sitename());
        pas.setTitle(pas.getMainTitle() + "        " + Localization.l("projectdlg_project") + " - " + Localization.l("projectdlg_no_project")); //+ m_sz_sitename);
		return true;
	}
	@Override
	public boolean onDepartmentChanged(PAS pas)
	{
		boolean b_activate_parm = Variables.getUserInfo().get_current_department().get_userprofile().get_parm_rights()>=1;
		OtherActions.PARM_CLOSE.setEnabled(b_activate_parm);
		OtherActions.PARM_REFRESH.setEnabled(b_activate_parm);
		OtherActions.PARM_START.setEnabled(b_activate_parm);
		if(PAS.isParmOpen()) {
            OtherActions.PARM_CLOSE.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "act_close_parm"));
        }
		if(pas.get_rightsmanagement().read_parm() && pas.get_userinfo().get_current_department().get_pas_rights() != 4) {
			Timeout t = new Timeout(30, 200);
			while(PAS.isParmOpen() && !t.timer_exceeded())
			{
				try
				{
					Thread.sleep(t.get_msec_interval());
					t.inc_timer();
				}
				catch(Exception e)
				{
					
				}
			}
            if(!PAS.isParmOpen() && pas.get_settings().parm()) {
				PAS.setParmOpen(PAS.pasplugin.onStartParm());
			}
		}

        final boolean enableSending = pas.get_rightsmanagement().cansend() || pas.get_rightsmanagement().cansimulate();
        FileMenuActions.NEW_SENDING.setEnabled(enableSending);
        FileMenuActions.OPEN_PROJECT.setEnabled(enableSending);
        FileMenuActions.CLOSE_PROJECT.setEnabled(PAS.get_pas().get_current_project()!=null);
        
		if(!enableSending) {
            pas.askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
				
				@Override
				public void Complete(boolean bStatusClosed) {
					
				}
			});
		}
		switch(pas.get_userinfo().get_current_department().get_pas_rights())
		{
		case 0:
			//none
			System.exit(0);
			break;
		case 1:
		case 2:
			pas.get_mainmenu().setTASMode(false);
			//pas.setAppTitle("");
			onSetAppTitle(pas, "", PAS.get_pas().get_userinfo());
			pas.get_eastcontent().remove_tab(EastContent.PANEL_TAS_);
			pas.get_mainmenu().enable_mapsite(true);
			break;
		case 4: //TAS
			pas.get_mainmenu().setTASMode(true);
			//pas.setAppTitle("UMS - " + PAS.l("main_tas_appname"));
            onSetAppTitle(pas, "UMS - " + Localization.l("main_tas_appname"), PAS.get_pas().get_userinfo());
			pas.get_eastcontent().InitTAS();
			pas.get_eastcontent().flip_to(EastContent.PANEL_TAS_);
			pas.get_mainmenu().enable_mapsite(false);
			break;
		}


		StatusActions.OPEN.setEnabled(pas.get_rightsmanagement().status());

		pas.get_mainmenu().setHouseeditorEnabled(pas.get_rightsmanagement().houseeditor() >= 1);
		return true;
	}

	

	@Override
	public InfoPanel onCreateInfoPanel() {
		InfoPanel panel = new InfoPanel();
		panel.doInit();
		return panel;
	}


	@Override
	public boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel) {
        tab.addTab(Localization.l("main_infotab_title"), null,
				panel,
				//sp,
                Localization.l("main_infotab_title_tooltip"));
		return true;
	}
	



	@Override
	public LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader) {
		try
		{
			/*JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);	
			SubstanceOfficeBlue2007LookAndFeel laf = new SubstanceOfficeBlue2007LookAndFeel();
			UIManager.setLookAndFeel(laf);*/
			boolean bSystemLafOk = false;
			try
			{
				String laf = UIManager.getSystemLookAndFeelClassName();
				log.debug("Using LAF=" + laf);
				LookAndFeel oLaf = (LookAndFeel)classloader.loadClass(laf).newInstance();
				UIManager.setLookAndFeel(oLaf);
				//SwingUtilities.updateComponentTreeUI(this);
				bSystemLafOk = true;
				return oLaf;
			}
			catch(Exception e)
			{
			}
			if(!bSystemLafOk)
			{
				try
				{
					String laf = UIManager.getCrossPlatformLookAndFeelClassName();
					log.debug("Using LAF=" + laf);
					LookAndFeel oLaf = (LookAndFeel)classloader.loadClass(laf).newInstance();
					UIManager.setLookAndFeel(oLaf);
					//SwingUtilities.updateComponentTreeUI(this);
					bSystemLafOk = true;
					return oLaf;
				}
				catch(Exception e)
				{
					
				}
			}					
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return null;

	}
	
	

	@Override
	protected boolean onGetInitialUIDefaults() {
		if(uidefaults_initial==null)
			uidefaults_initial = (UIDefaults)UIManager.getDefaults().clone();
		return true;
	}


	@Override
	public boolean onSetUserLookAndFeel(final Settings settings, final UserInfo userinfo) {
		try
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						SubstanceLookAndFeel.setCurrentTheme(settings.getThemeClassName());
						SubstanceLookAndFeel.setCurrentButtonShaper(settings.getButtonShaperClassname());
						if(settings.getGradientClassname()!=null && settings.getGradientClassname().length() > 0) {
                            SubstanceLookAndFeel.setCurrentGradientPainter(settings.getGradientClassname());
                        }
						else {
                            SubstanceLookAndFeel.setCurrentGradientPainter("org.jvnet.substance.painter.GlassGradientPainter");
                        }
						if(settings.getTitlePainterClassname()!=null && settings.getTitlePainterClassname().length() > 0) {
                            SubstanceLookAndFeel.setCurrentTitlePainter(settings.getTitlePainterClassname());
                        }
						else {
                            SubstanceLookAndFeel.setCurrentTitlePainter("org.jvnet.substance.title.Glass3DTitlePainter");
                        }
						SubstanceLookAndFeel.setCurrentWatermark(settings.getWatermarkClassName());
						
					}
					catch(Exception e)
					{
						log.warn("Failed to start substance look and feel", e);
					}

				}
			});
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return true;
	}

	
	

	@Override
	public boolean onUserChangedLookAndFeel(Settings settings) {
		try
		{
			
			//String szname = UIManager.getLookAndFeel().getLayoutStyle().getClass().getName();
			//m_settings.setThemeClassName(szname);
			String szname = UIManager.getLookAndFeel().getClass().getName();
			
			szname = SubstanceLookAndFeel.getCurrentWatermark().getClass().getName();
			settings.setWatermarkClassName(szname);

			szname = SubstanceLookAndFeel.getCurrentButtonShaper().getClass().getName();
			settings.setButtonShaperClassName(szname);
			
			szname = SubstanceLookAndFeel.getCurrentGradientPainter().getClass().getName();
			settings.setGradientClassname(szname);
			
			szname = SubstanceLookAndFeel.getCurrentTitlePainter().getClass().getName();
			settings.setTitlePainterClassname(szname);
			//SubstanceLookAndFeel.getCurrentDecorationPainter().getClass().getName();
			//m_settings.setTitlePainterClassname(szname);
			
			szname = SubstanceLookAndFeel.getTheme().getClass().getName();
			settings.setThemeClassName(szname);
		
			return true;
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			return false;
		}
	}







	@Override
	public ImageIcon onLoadAppIcon() {
		try
		{
			return no.ums.pas.ums.tools.ImageLoader.load_icon("pas_appicon_16.png");
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			return null;
		}
	}


	@Override
	public boolean onBeforeLoadMap(Settings settings) {
		return true;
	}

	@Override
	public boolean onAfterLoadMap(Settings settings, Navigation nav,
			MapFrame frame) {
		if(Variables.getStatusController()!=null)
			Variables.getStatusController().refresh_search_houses();
		PAS.signalFirstMapLoaded();
		return true;
	}

	@Override
	public boolean onWmsLayerListLoaded(List<Layer> layers, List<String> check) {
		return false;
	}

	@Override
	public List<String> onSendErrorMessages(String concat_errorlist, MailAccount account, Smtp.smtp_callback callback) {
		List<String> arr_adr = new ArrayList<String>();
		arr_adr.add("mh@ums.no");
		//arr_adr.add("sa@ums.no");		
		//new MailCtrl(account.get_helo(),account.get_mailserver(),account.get_port(),account.get_displayname(),account.get_mailaddress(),arr_adr, callback,"PAS error", concat_errorlist);
        final Smtp smtp = new Smtp(account.get_helo(), account.get_mailserver(), account.get_mailaddress(), arr_adr, "PAS error report", concat_errorlist, callback);
        PasApplication.getInstance().getExecutor().submit(smtp);
		return arr_adr;
	}

	@Override
	public boolean onSoapFaultException(UserInfo info, SOAPFaultException e) {
		int idx1 = e.getLocalizedMessage().indexOf(">")+2;
		int idx2 = e.getLocalizedMessage().indexOf(":", idx1);
		if(idx1>=0 && idx2>idx1)
		{
			String sz_class = e.getLocalizedMessage().substring(idx1, idx2);
			if(sz_class.equals("com.ums.UmsCommon.USessionExpiredException"))
			{
				info.set_session_active(false);
				info.set_session_inactive_reason(SESSION_INACTIVE_REASON.EXPIRED);
				return onSessionTimedOutException(info);
			}
			else if(sz_class.equals("com.ums.UmsCommon.ULogonFailedException"))
			{
				info.set_session_active(false);
				info.set_session_inactive_reason(SESSION_INACTIVE_REASON.EXPIRED);
				return onSessionTimedOutException(info);				
			}
			else if(sz_class.equals("com.ums.UmsCommon.USessionDeletedException"))
			{
				info.set_session_active(false);
				info.set_session_inactive_reason(SESSION_INACTIVE_REASON.DELETED);
				return onSessionTimedOutException(info);
			}
			else if(sz_class.equals("com.ums.UmsCommon.UNoAccessOperatorsException"))
			{
				//JOptionPane.showMessageDialog(null, "No operators are active at the moment.\nAborting sending...", PAS.l("common_error"), JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(null, Localization.l("main_sending_lba_error_no_operators"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
				return true;
			}
			else if(sz_class.equals("com.ums.UmsCommon.UEmptySMSMessageException"))
			{
                JOptionPane.showMessageDialog(null, Localization.l("main_sending_warning_empty_sms"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			}
			else if(sz_class.equals("com.ums.UmsCommon.UEmptySMSOadcException"))
			{
                JOptionPane.showMessageDialog(null, Localization.l("main_sending_warning_empty_sms_oadc"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			}
			else if(sz_class.equals("com.ums.UmsCommon.URefnoException"))
			{
                JOptionPane.showMessageDialog(null, Localization.l("main_sending_error_retrieving_refno"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			}
			else
			{
                JOptionPane.showMessageDialog(null, "An unexpected error occured\n\n" + e.getMessage(), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}


    /**
     * Function called when the webservice session has expired.
     * Called from onSoapFaultException, if details show that server threw
     * a com.ums.UmsCommon.USessionExpiredException
     * @return
     */
	protected boolean onSessionTimedOutException(UserInfo info) {
		try
		{
			PAS.get_pas().setEnabled(false);
			//PAS.get_pas().setVisible(false);
			info.set_session_active(false);
			if(!PAS.APP_EXIT.get())
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						try
						{
							//ClassLoader classloader = PAS.get_pas().getClass().getClassLoader();
							//Class cl = classloader.loadClass("no.ums.pas.pluginbase.defaults.DisabledLookAndFeel");
							//LookAndFeel laf = (LookAndFeel)cl.newInstance();
							//UIManager.setLookAndFeel(laf);
							//SwingUtilities.updateComponentTreeUI(PAS.get_pas());
							onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
						}
						catch(Exception err)
						{
							
						}
					}
				});
				switch(info.get_session_inactive_reason())
				{
				case DELETED:
                    JOptionPane.showMessageDialog(null, Localization.l("logon_error_user_blocked"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
					break;
				case EXPIRED:
                    JOptionPane.showMessageDialog(null, Localization.l("logon_error_user_session_timeout"), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
					break;
				}
				/*Logon logon = new Logon(new LogonInfo(PAS.get_pas().get_settings().getUsername(),
						PAS.get_pas().get_settings().getCompany()), 
						PAS.get_pas().get_settings().getLanguage(),
						true);*/
				Logon logon = Holder.getInstance(new LogonInfo(PAS.get_pas().get_settings().getUsername(),
						PAS.get_pas().get_settings().getCompany()), 
						PAS.get_pas().get_settings().getLanguage(),
						true);
				logon.startLogonProcedure();
				if(!logon.isLoggedOn())
					System.exit(0);
	
				if(logon.get_userinfo()==null) {
					System.exit(0);
				}
				PAS.get_pas().get_userinfo().set_sessionid(logon.get_userinfo().get_sessionid());
				PAS.pasplugin.onSessionRenewed(PAS.get_pas().get_userinfo());
			}
	
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return true;
	}


	@Override
	public boolean onSessionRenewed(final UserInfo ui) {
		ui.set_session_active(true);
		//onUserChangedLookAndFeel(PAS.get_pas().get_settings());
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				PAS.get_pas().setEnabled(true);
				//PAS.get_pas().setVisible(true);
				//onSetInitialLookAndFeel(ui.getClass().getClassLoader());
				//onSetUserLookAndFeel(PAS.get_pas().get_settings(), ui);
				onSetAppTitle(PAS.get_pas(), "", ui);
				PAS.get_pas().toFront();
			}
		});
		return true;
	}


	@Override
	public int getSystemMessagesPollInterval() {
		return 60;
	}

	@Override
	public boolean onStartSystemMessageThread(final ActionListener callback, final int n_interval_msec) {
		new Thread("PAS System Messages") {
			public void run()
			{
				while(!PAS.APP_EXIT.get())
				{
					try
					{
						onExecAskForSystemMessage(callback);
						Thread.sleep(n_interval_msec);
					}
					catch(Exception e)
					{
						log.warn(e.getMessage(), e);
					}
				}
			}
		}.start();
		return true;
	}

	WSGetSystemMessages ws_getsystemmessages;
	@Override
	public boolean onExecAskForSystemMessage(ActionListener callback) {
		if(ws_getsystemmessages==null)
			ws_getsystemmessages = new WSGetSystemMessages(callback);
		ws_getsystemmessages.runNonThreaded();
		onHandleSystemMessages(ws_getsystemmessages.getSystemMessages());
		return true;
	}
	
	

	@Override
	protected boolean onHandleSystemMessages(USYSTEMMESSAGES sysmsg) {
		return true;
	}

	@Override
	public boolean onHelpAbout() {
        final StringWriter aboutContent = new StringWriter();
        final PrintWriter about = new PrintWriter(aboutContent);
        new SwingWorker() {
            PASVERSION wsVersion;
			@Override
			protected Object doInBackground() throws Exception {
				//URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
				//QName service = new QName("http://ums.no/ws/pas/", "pasws");
				//wsVersion = new Pasws(wsdl, service).getPaswsSoap12().getVersionNumber();
		        return Boolean.TRUE;
			}

			@Override
			protected void done() {
		        about.println(Localization.l("common_aboutbox_content"));
		        about.println();
		        about.println();
//		        about.printf("Implementation version: %s\n", VersionInfo.getInstance().IMPLEMENTATION_VERSION);
//		        about.printf("Specification version: %s\n", VersionInfo.getInstance().SPECIFICATION_VERSION);
		        about.printf("%s %s\n", Localization.l("mainmenu_help_about_version"), VersionInfo.INSTANCE.buildVersion);
//		        about.printf("Build number: %s\n", VersionInfo.INSTANCE.buildNumber);
//		        about.printf("Build user: %s\n", VersionInfo.getInstance().buildUser);
		        about.printf("%s %s (b%s)\n", Localization.l("mainmenu_help_about_revision"), VersionInfo.INSTANCE.revisionNumber, VersionInfo.INSTANCE.buildNumber);
		        //if(wsVersion!=null)
		        //	about.printf("Web Service Version: %d.%d.%d\n", wsVersion.getMajor(), wsVersion.getMinor(), wsVersion.getBuild());
		        //else
		        //	about.print("Web Service Version: Unknown / Offline");
		        //timestampformat = yyyymmdd-hhmm
		        String ts = (VersionInfo.INSTANCE.buildTimestamp.length()==13 ? VersionInfo.INSTANCE.buildTimestamp : null);
		        if(ts!=null)
		        {     	
			        Calendar calendar = Calendar.getInstance();
			        calendar.set(Integer.parseInt(ts.substring(0, 4)),
			        			Integer.parseInt(ts.substring(4, 6))-1, //month is 0-based
			        			Integer.parseInt(ts.substring(6, 8)), 
			        			Integer.parseInt(ts.substring(9, 11)),
			        			Integer.parseInt(ts.substring(11, 13)));
			        about.printf("%s %s\n", Localization.l("mainmenu_help_about_created"), DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(calendar.getTime()));
		        }
			    about.close();
		        JOptionPane.showMessageDialog(PAS.get_pas(), aboutContent.toString(), Localization.l("common_aboutbox_heading"), JOptionPane.INFORMATION_MESSAGE);
				super.done();
			}
        	
		}.execute();
        
		return true;
	}

	@Override
	public boolean onTrainingMode(boolean b) {
		log.debug("TrainingMode=" + b);
		return true;
	}
	
	/**
	 * Function to determine if a user has activated training mode
	 * @return true if user is in training mode
	 */
	@Override
	protected boolean IsInTrainingMode() {
		return PAS.TRAINING_MODE;
	}


	@Override
	public boolean onLogonAddControls(LogonPanel p) {
		p.add_spacing(DefaultPanel.DIR_HORIZONTAL, 100);
		p.add_controls();
		return true;
	}

	@Override
	public boolean onCustomizeLogonDlg(LogonDialog dlg) {
		log.debug("onCustomizeLogonDlg");
		dlg.setPreferredSize(new Dimension(400, 350));
		return true;
	}

	@Override
	public boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g) {
		return true;
	}

	@Override
	public boolean onMapCalcNewCoords(Navigation nav, PAS p) {
		p.get_statuscontroller().calcHouseCoords();
		p.get_housecontroller().calcHouseCoords();
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
			for(int i=0; i < p.get_sendcontroller().get_sendings().size(); i++)
			{
				try
				{
					p.get_sendcontroller().get_sendings().get(i).get_sendproperties().calc_coortopix();
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		catch(Exception e)
		{
			
		}
		Enumeration<ShapeStruct> en = getShapesToPaint().elements();
		while(en.hasMoreElements())
		{
			en.nextElement().calc_coortopix(nav);
		}			

		p.get_gpscontroller().calcGpsCoords();	
		if(p.get_parmcontroller()!=null)
			p.get_parmcontroller().calc_coortopix();
		if(p.get_eastcontent().get_taspanel()!=null)
			p.get_eastcontent().get_taspanel().calc_coortopix();
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
			log.warn(e.getMessage(), e);
		}
		try
		{
			p.get_mappane().get_active_shape().calc_coortopix(PAS.get_pas().get_navigation());
		}
		catch(Exception e)
		{
			
		}
		return true;
	}

	@Override
	public boolean onMapKeyPressed(KeyEvent e) {
		return false;
	}

	@Override
	public boolean onMapKeyReleased(KeyEvent e) {
		return false;
	}

	@Override
	public boolean onMapKeyTyped(KeyEvent e) {
		return false;
	}

	protected Hashtable<String, ShapeStruct> shapes_to_paint = new Hashtable<String, ShapeStruct>();
	public void addShapeToPaint(ShapeStruct s)
	{
		if(s!=null)
		{
			if(!shapes_to_paint.containsKey("s" + s.shapeID))
			{
				shapes_to_paint.put("s" + s.shapeID, s);
			}
		}
	}

	public boolean removeShapeToPaint(long id)
	{
        String idString = String.valueOf(id);
        if(shapes_to_paint.containsKey(idString))
		{
			shapes_to_paint.remove(idString);
			return true;
		}
		return false;
	}
	
	@Override
	public void removeShapeToPaint(ShapeStruct s) {
		if(s==null)
			return;
		String key = "s" + s.shapeID;
		if(shapes_to_paint.containsKey(key))
		{
			shapes_to_paint.remove(key);
		}
	}

	public void clearShapesToPaint()
	{
		shapes_to_paint.clear();
	}
	
	@Override
	public Hashtable<String, ShapeStruct> getShapesToPaint() {
		return shapes_to_paint;
	}


	@Override
	public boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p) {
		try
		{
			
			DeptArray depts = p.get_userinfo().get_departments();
			//depts.ClearCombinedRestrictionShapelist();
			//depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
			//depts.test();
            for (Object dept : depts) {
                ((DeptInfo) dept).drawRestrictionShapes(g, nav);
            }
			List<ShapeStruct> list = p.get_userinfo().get_departments().get_combined_restriction_shape();
            for (ShapeStruct aList : list) {
                aList.draw(g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), false, true, false, null, true, true, 2, false);
            }

		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		if(p.get_parmcontroller()!=null && 
				(EastContent.CURRENT_PANEL==EastContent.PANEL_PARM_ ||
				EastContent.CURRENT_PANEL==EastContent.PANEL_INFO_))
		{
			p.get_parmcontroller().drawLayers(g);
		}
		
		/*try {
			if(EastContent.CURRENT_PANEL==EastContent.PANEL_SENDING_ ||
				EastContent.CURRENT_PANEL==EastContent.PANEL_INFO_)
			{
				p.get_sendcontroller().draw_polygons(g, PAS.get_pas().get_mappane().get_current_mousepos());
			}
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		*/
		if(ViewOptions.TOGGLE_HOUSES.isSelected())
		{
			p.get_housecontroller().calcHouseCoords();
			p.get_housecontroller().drawItems(g);
		}

		if(EastContent.CURRENT_PANEL==EastContent.PANEL_SENDING_ ||
			EastContent.CURRENT_PANEL==EastContent.PANEL_INFO_)
		{
			for(SendObject so : p.get_sendcontroller().get_sendings())
			{
				//if(!so.get_sendproperties().get_shapestruct().equals(p.get_mappane().get_active_shape()))
				{
					boolean bEdit = p.get_mappane().isInPaintMode() && so.get_sendproperties().get_shapestruct().equals(p.get_mappane().get_active_shape());
					if(so.get_sendproperties() instanceof SendPropertiesGIS)
					{
						so.get_sendproperties().calc_coortopix();
						so.draw(g, Variables.getMapFrame().get_current_mousepos());
					}
					else
					{
						so.get_sendproperties().get_shapestruct().draw(
								g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), !bEdit, !bEdit, bEdit, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, true, bEdit);
					}
				}
			}
		}
		try {
			if(EastContent.CURRENT_PANEL==EastContent.PANEL_PARM_ ||
				EastContent.CURRENT_PANEL==EastContent.PANEL_INFO_)
			{
				//if(p.get_parmcontroller().get_shape().equals(p.get_mappane().get_active_shape()))
				{
					//p.get_mappane().get_active_shape().draw(g, nav, false, false, true, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 1, false);
				}
			}
		} catch(Exception e) { }

		Enumeration<ShapeStruct> en = getShapesToPaint().elements();
		while(en.hasMoreElements())
		{
			en.nextElement().draw(g, p.get_mappane().getMapModel(), p.get_mappane().getZoomLookup(), true, true, false, null, true, true, 1, false);
		}			

		try {
			p.get_mappane().draw_pinpoint(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_mappane().draw_adredit(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(p.get_mappane().get_mode()==MapFrame.MapMode.HOUSEEDITOR) {
				switch(p.get_mappane().get_submode()) {
					case MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR:
					case MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR:
						p.get_mappane().draw_moveinhab_text(g);
						break;
				}
						
			}
		} catch(Exception e) { }
		try {
			if(EastContent.CURRENT_PANEL==EastContent.PANEL_STATUS_LIST ||
				EastContent.CURRENT_PANEL==EastContent.PANEL_INFO_)
			{
				p.get_statuscontroller().drawItems(g);
			}
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_gpscontroller().drawItems(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			p.get_housecontroller().draw_details(g);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(p.get_eastcontent().get_taspanel()!=null)
			{
				p.get_eastcontent().get_taspanel().calc_coortopix();
				p.get_eastcontent().get_taspanel().drawItems((Graphics2D)g);
				p.get_eastcontent().get_taspanel().drawLog((Graphics2D)g);
			}
		} catch(Exception e) { 
			log.warn(e.getMessage(), e);
		}
		return true;
	}
	
	
	@Override
	public boolean onMapGotoShapesToPaint() {
		if(shapes_to_paint.size()==0)
			return false;
		NavStruct nav = CommonFunc.calc_bounds(shapes_to_paint.values().toArray());
		if(nav!=null)
		{
			nav = CommonFunc.navPadding(nav, 0.01f);
			onMapGotoNavigation(nav);
		}
		return true;
	}

	
	@Override
	public boolean onMapGotoShape(ShapeStruct s) {
		if(s==null)
			return false;
		NavStruct nav = s.calc_bounds();
		if(nav!=null)
		{
			nav = CommonFunc.navPadding(nav, 0.01f);
			onMapGotoNavigation(nav);
		}
		return true;
	}


	@Override
	public boolean onMapGotoNavigation(NavStruct n) {
		//PAS.get_pas().actionPerformed(new ActionEvent(n, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
		Variables.getNavigation().gotoMap(n);
		return true;
	}







	@Override
	public boolean onMapLoadFailed(MapLoader loader) {
		Variables.getDraw().setFirstMap(false);
		return true;
	}







	@Override
	public boolean onFrameResize(JFrame f, ComponentEvent e) {
		return true;
	}


	@Override
	public Dimension getDefaultScreenSize(Settings s) {
		return new Dimension(1024,700);
	}

	@Override
	public Dimension getMinimumScreenSize(Settings s) {
		return getDefaultScreenSize(s);
	}


	@Override
	public String getDefaultLocale(Settings s) {
		return (s==null || s.getLanguage().isEmpty() ? "en_GB" : s.getLanguage());
	}
	
	@Override
	public String getUserLocale(LogonInfo l, Settings s) {
		return l.get_language();
	}


	@Override
	public void onLocaleChanged(Locale from, Locale to) {
		try
		{
			log.debug("Language changed from " + from.getDisplayLanguage() + " to " + to.getDisplayLanguage());
			UIParamLoader.loadServerUIParams();
		}
		catch(FileNotFoundException e)
		{
			log.warn(e.getMessage(), e);
		}
	}







	@Override
	public EastContent onCreateEastContent() {
		return new EastContent(PAS.get_pas());
	}



	@Override
	public StatusController onCreateStatusController() {
		return new StatusController();
	}







	@Override
	public boolean onOpenAddressBook() {
		return false;
	}


	@Override
	public boolean onShowContactinformation() {
        JOptionPane.showMessageDialog(PAS.get_pas(), Localization.l("common_helpdesk_contact"), Localization.l("common_contact_information"), JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	@Override
	public boolean onCloseProject() {
		FileMenuActions.CLOSE_PROJECT.setEnabled(false);
		//PAS.get_pas().get_mappane().set_active_shape(null);
		PAS.get_pas().get_sendcontroller().remove_all_sendings();
		PAS.get_pas().get_mainmenu().get_selectmenu().get_bar().showHouseSelect(false);
		return true;
	}

	@Override
	public boolean onStopStatusUpdates() {
		return false;
	}

	@Override
	public boolean onOpenProject(Project project, long nFromNewRefno) {
		FileMenuActions.CLOSE_PROJECT.setEnabled(true);
		if(project.get_num_sendings()>0)
			PAS.get_pas().actionPerformed(new ActionEvent(project, ActionEvent.ACTION_PERFORMED, "act_project_activate"));
		return true;
	}

	public int onInvokeProject() {
		return PAS.get_pas().askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
			@Override
			public void Complete(boolean bStatusClosed) {
				
			}
		});
	}


	@Override
	public boolean onEastContentTabClicked(EastContent e, JTabbedPane pane) {
		log.debug("Tab: " + pane.getTitleAt(pane.getSelectedIndex()));
		PAS.get_pas().kickRepaint();
		return true;
	}

	@Override
	public ProjectDlg onCreateOpenProjectDlg(JFrame parent,
			ActionListener callback, String cmdSave, boolean bNewsending) {
		return new ProjectDlg(parent, callback, cmdSave, bNewsending);
	}

	@Override
	public boolean onLockSending(SendOptionToolbar toolbar, boolean bLock) {
		toolbar.lock_sending(bLock);
		return true;
	}

	@Override
	public boolean onDownloadHouses(final HouseController controller) {
        PasApplication.getInstance().getExecutor().submit(new Runnable() {
            @Override
            public void run() {
				PAS.get_pas().actionPerformed(new ActionEvent(HouseController.HOUSE_DOWNLOAD_IN_PROGRESS_, ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
				controller.start_download(true);
			}
		});
		return true;
	}


	@Override
	public boolean onSetDefaultPanMode(Settings s) {
		
		return s.getPanByDrag();
	}


	@Override
	public Dimension getMinMapDimensions() {
		return new Dimension(100,100);
	}


	@Override
	public float getMapZoomSpeed() {
		return 0.25f;
	}

	@Override
	public boolean onDownloadDocumentation(UserInfo userinfo) {
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append(PAS.get_pas().getVB4Url());
			sb.append("/uploadedfiles/");
			sb.append("0_NO-UMS-PAS.pdf");
			Desktop.getDesktop().browse(URI.create(sb.toString()));
		}catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean onDeleteStatus(final long refno, final IDeleteStatus callback) {
		new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				new WSDeleteStatus(refno, callback).runNonThreaded();
				return Boolean.TRUE;
			}
			
		}.execute();
		return true;
	}

	@Override
	public boolean onDeleteProject(final long projectpk, final IDeleteProject callback) {
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				new WSDeleteProject(projectpk, callback).runNonThreaded();
				return Boolean.TRUE;
			}			
		}.execute();
		return true;
	}

	
	

}