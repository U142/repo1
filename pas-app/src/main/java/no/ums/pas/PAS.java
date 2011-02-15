package no.ums.pas;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.log.swing.LogFrame;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.core.Variables;
import no.ums.pas.core.controllers.GPSController;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.*;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.mainui.*;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.core.themes.UMSTheme;
import no.ums.pas.core.themes.UMSTheme.THEMETYPE;
import no.ums.pas.core.ws.WSLogoff;
import no.ums.pas.core.ws.WSSaveUI;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.localization.UIParamLoader;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.MapitemProperties;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.xml.XmlReader;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.pluginbase.AbstractPasScriptingInterface;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.sound.SoundRecorder;
import no.ums.pas.status.LBASEND;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Timeout;
import no.ums.pas.ums.tools.UMSSecurity;
import no.ums.pas.ums.tools.UMSSecurity.UMSPermission;
import no.ums.ws.pas.UPASUISETTINGS;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.button.ButtonShaperChangeListener;
import org.jvnet.substance.painter.GradientPainterChangeListener;
import org.jvnet.substance.skin.SkinChangeListener;
import org.jvnet.substance.theme.SubstanceComplexTheme;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.theme.ThemeChangeListener;
import org.jvnet.substance.watermark.SubstanceNullWatermark;
import org.jvnet.substance.watermark.WatermarkChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

//substance 3.3

/*import contrib.com.jgoodies.looks.common.FontPolicies;
import contrib.com.jgoodies.looks.common.FontPolicy;
import contrib.com.jgoodies.looks.common.FontSet;
*/
//Substance 5.2
//import org.jvnet.substance.api.SubstanceColorSchemeBundle;






/*class ThemeLoader
{
	class PluginClassLoader extends URLClassLoader {
		  public PluginClassLoader(URL[] urls, ClassLoader parent) {
		    super(urls, parent);
		  }

		  public Class<?> loadClass(String name)
		      throws ClassNotFoundException {
		    Class loadedClass = findLoadedClass(name);
		    if (loadedClass == null) {
		      try {
		        loadedClass = findClass(name);
		      } catch (ClassNotFoundException e) {
		        // Swallow exception
		        //does not exist locally
		      }

		      if (loadedClass == null) {
		        loadedClass = super.loadClass(name);
		      }
		    }
		    return loadedClass;
		  }

		}
	public void load(String s)
	{
		try
		{
			URL [] urls = new URL [] { 
					new URL("https://secure.ums2.no/vb4utv/UMSTheme.java") 
			};
			PluginClassLoader cl = new PluginClassLoader(urls, PAS.class.getClassLoader());
			Object o = cl.loadClass("com.ums.UMSTheme").newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
}*/

public class PAS extends JFrame implements ComponentListener, WindowListener, SkinChangeListener 
								/*substance 3.3*/ ,ThemeChangeListener, WatermarkChangeListener, //BorderPainterChangeListener,
											GradientPainterChangeListener, ButtonShaperChangeListener {

	public static final long serialVersionUID = 1;

    private static final Log log = UmsLog.getLogger(PAS.class);

	private String m_sz_maintitle;
	public String getMainTitle()
	{
		return m_sz_maintitle;
	}
	public void setMainTitle(String s)
	{
		m_sz_maintitle = s;
	}
	
	private static PAS g_pas = null;
	public static PAS get_pas() { return g_pas; }
	
	
	public static int icon_version = 2;
	MapFrame m_mappane;
	//MapLayeredPane m_maplayeredpane;
	MainMenu m_mainmenu;
	Navigation m_navigation = null;
	PASDraw m_drawthread = null;
	HTTPReq m_httpreq;
	
	SouthContent m_southcontent;
	EastContent m_eastcontent;
	
	private String PAS_SITENAME;
	private String PAS_OVERRIDE_USERID = null;
	private String PAS_OVERRIDE_COMPID = null;
	private String PAS_WS_SITE;;
	private String PAS_CODEBASE;
	public String ADDRESSSEARCH_URL = "";
	public String VB4_URL = "";
	StatusController m_statuscontroller = null;
	GPSController m_gpscontroller = null;
	HouseController m_housecontroller = null;
	InhabitantFrame m_inhabitantframe = null;
	GPSFrame m_gpsframe = null;
	PAS m_pas;
	public Dimension get_mapsize() { return get_mappane().get_dimension(); }
	public SouthContent get_southcontent() { return m_southcontent; }
	public EastContent get_eastcontent() { return m_eastcontent; }
	Dimension screenSize;
	public Dimension get_screensize() { return screenSize; }
	private static ImageIcon m_appicon = null;
	public ImageIcon get_appicon() { return m_appicon; }
	private Project m_current_project = null;
	private HouseEditorDlg m_houseeditor = null;
	private ParmController m_parmcontroller = null;
	public ParmController get_parmcontroller() { return m_parmcontroller; }	
	private int m_n_eastwidth = 550;
	public void setEastWidth(int n) { m_n_eastwidth = n; }
	private int m_n_southheight = 0;//300
	public int get_eastwidth() { return m_n_eastwidth; }
	public int get_southheight() { return m_n_southheight; }
	private MapitemProperties m_mapproperties = null;
	public MapitemProperties get_mapproperties() { return m_mapproperties; }
	private UserInfo m_userinfo;
	public UserInfo get_userinfo() { return m_userinfo; }
	private RightsManagement m_rightsmanagament;
	public RightsManagement get_rightsmanagement() { return m_rightsmanagament; }
	private Settings m_settings;
	public Settings get_settings() { return m_settings; }
	private Frame applet_frame;
	public Frame get_applet_frame() { return applet_frame; }
	public SendController get_sendcontroller() { return m_sendcontroller; }
	private SendController m_sendcontroller;
	private StorageController m_storagecontroller = new StorageController();
	public StorageController get_storagecontroller() { return m_storagecontroller; }
	public Project get_current_project() { return m_current_project; }
	protected void set_current_project(Project p) { m_current_project = p; }
	private boolean m_keep_sendings = false;
	public boolean get_keep_sendings() { return m_keep_sendings; }
	public void set_keep_sendings(boolean value) { m_keep_sendings = value; }
	private PASActions m_pasactionlistener = new PASActions();
	public ActionListener get_pasactionlistener() { return m_pasactionlistener; }
	Dimension dim_map;
	private int m_n_repaints = 0;
	private static boolean m_b_parm_open = false;
	public static boolean TRAINING_MODE = false;
	
	
	public static void setParmOpen(boolean b)
	{
		m_b_parm_open = b;
	}
	public static boolean isParmOpen()
	{
		return m_b_parm_open;
	}

	private LookAndFeel m_lookandfeel;
	public LookAndFeel get_lookandfeel() { return m_lookandfeel; }
	//public LookAndFeel get_lookandfeel() { return UIManager.getCrossPlatformLookAndFeelClassName(); }
	//public LookAndFeel get_lookandfeel() { return UIManager.getLookAndFeel(); }
	
	public Navigation get_navigation() { return m_navigation; }
	public Draw get_drawthread() { return m_drawthread; }
	public MapFrame get_mappane() { return m_mappane; }
	//public MapLayeredPane get_maplayeredpane() { return m_maplayeredpane; }
	public MainMenu get_mainmenu() { return m_mainmenu; }
	public HTTPReq get_httpreq() { return m_httpreq; }
	public String get_sitename() { return PAS_SITENAME; }
	public String get_pasws() { return PAS_WS_SITE; }
	public String get_codebase() { return PAS_CODEBASE; }

    public StatusController get_statuscontroller() { return m_statuscontroller; }
	public GPSController get_gpscontroller() { return m_gpscontroller; }
	public HouseController get_housecontroller() { return m_housecontroller; }
	public InhabitantFrame get_inhabitantframe() { return m_inhabitantframe; }
	public GPSFrame get_gpsframe() { return m_gpsframe; }
	public String get_maintitle() { return m_sz_maintitle; }
	public void set_rightsmanagement(RightsManagement rm) { this.m_rightsmanagament = rm; }
	public void set_settings(Settings s) { m_settings = s; }
	public Container get_contentpane() { return this.getContentPane(); }
	public boolean m_b_creategui_finished = false;
	protected String [] m_sz_program_args;

	public static Locale locale;
	public static ResourceBundle lang;
	public static ResourceBundle defaultLang = null;
	public static boolean DEBUGMODE = false;
	public static int g_n_parmversion = 2;
	public static final String sz_mark_language_words = "*";
	public static UMSTheme active_theme;
	public String OVERRIDE_WMS_SITE = null;
	
	public static final AtomicBoolean APP_EXIT = new AtomicBoolean(false);
	
	/*public void initSubstance()
	{
		try
		{
			//SubstanceLookAndFeel.setCurrentTheme(SubstanceTheme.getTheme(m_settings.getThemeClassName()));
			//ClassLoader loader = new ClassLoader(); //getSystemResource(m_settings.getThemeClassName());
			//SubstanceTheme activeTheme = new SubstanceMixTheme(m_settings.getThemeClassName());
			        //new SubstancePurpleTheme(),
			        //new SubstanceBarbyPinkTheme()).saturate(0.1);
			//SubstanceLookAndFeel.setCurrentTheme(activeTheme);

			//Substance 3.3
			////
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						//UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");
						boolean b;
						//b = SubstanceLookAndFeel.setSkin(m_settings.getSkinClassName());
						b = SubstanceLookAndFeel.setCurrentTheme(m_settings.getThemeClassName());
					
						//themeChanged();
						
						//active_theme = (UMSTheme)SubstanceLookAndFeel.getTheme();
						//active_theme = new UMSTheme(THEMETYPE.SIMPLE);
						//theme = theme.saturate(0.1, true);
						//theme = theme.tint(0.15);
						//theme = theme.hueShift(0.2);
						//theme = theme.shade(0.1);
						
						
						//b = SubstanceLookAndFeel.setCurrentTheme(active_theme);
						
						
						
						b = SubstanceLookAndFeel.setCurrentButtonShaper(m_settings.getButtonShaperClassname());
						if(m_settings.getGradientClassname()!=null && m_settings.getGradientClassname().length() > 0)
							b = SubstanceLookAndFeel.setCurrentGradientPainter(m_settings.getGradientClassname());
						else
							b = SubstanceLookAndFeel.setCurrentGradientPainter("org.jvnet.substance.painter.GlassGradientPainter");
						if(m_settings.getTitlePainterClassname()!=null && m_settings.getTitlePainterClassname().length() > 0)
							b = SubstanceLookAndFeel.setCurrentTitlePainter(m_settings.getTitlePainterClassname());
						else
							b = SubstanceLookAndFeel.setCurrentTitlePainter("org.jvnet.substance.title.Glass3DTitlePainter");
						b = SubstanceLookAndFeel.setCurrentWatermark(m_settings.getWatermarkClassName());
						
					}
					catch(Exception e)
					{
						
					}

				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}*/
	
	String sz_current_user = "";
	String sz_current_comp = "";
	public void LoadVisualSettings(ActionListener a, String user, String comp, boolean threaded)
	{
		//no need to load same userinfo
		/*if(user.equals(sz_current_user) && comp.equals(sz_current_comp))
			return;
		sz_current_comp = comp;
		sz_current_user = user;
		WSGetVisualSettings v = new WSGetVisualSettings(a, user, comp);
		if(threaded)
			v.start();
		else
			v.runNonThreaded();*/
	}
	
	public void setVisualSettings(UPASUISETTINGS ui)
	{
		m_settings.setButtonShaperClassName(ui.getSzButtonshaperClass());
		m_settings.setFleetcontrol(ui.isBAutostartFleetcontrol());
		m_settings.setGisDownloadDetailThreshold(ui.getLGisMaxForDetails());
		m_settings.setGradientClassname(ui.getSzGradientClass());
		m_settings.setLanguage(ui.getSzLanguageid().trim());
		m_settings.setLbaRefresh(ui.getLLbaUpdatePercent());
		m_settings.setMapServer((ui.getLMapserver()==0 ? MAPSERVER.DEFAULT : MAPSERVER.WMS));
		m_settings.setPanByDrag((ui.getLDragMode()==1 ? true : false));
		m_settings.setParm(ui.isBAutostartParm());
		m_settings.setSelectedWmsFormat(ui.getSzWmsFormat());
		m_settings.setWmsUsername(ui.getSzWmsUsername());
		m_settings.setWmsPassword(ui.getSzWmsPassword());
		m_settings.setWmsEpsg(ui.getSzWmsEpsg());
		pasplugin.onSetDefaultPanMode(m_settings);

		try
		{
			m_settings.setSelectedWmsLayers(ui.getSzWmsLayers());
		}
		catch(Exception e)
		{
			
		}
		m_settings.setSkinClassName(ui.getSzSkinClass());
		m_settings.setThemeClassName(ui.getSzThemeClass());
		m_settings.setTitlePainterClassname(ui.getSzTitleClass());
		m_settings.setWatermarkClassName(ui.getSzWatermarkClass());
		m_settings.setWindowFullscreen(ui.isBWindowFullscreen());
		//m_settings.setWindowHeight(ui.getLWinHeight());
		//m_settings.setWindowWidth(ui.getLWinWidth());
		m_settings.setWmsSite(ui.getSzWmsSite());
		//m_settings.setXpos(ui.getLWinposX());
		//m_settings.setYpos(ui.getLWinposY());
		
	}
	
	/*public class UMSFontSet implements FontSet
	{

		@Override
		public FontUIResource getControlFont() {
			return null;
		}

		@Override
		public FontUIResource getMenuFont() {
			return null;
		}

		@Override
		public FontUIResource getMessageFont() {
			return null;
		}

		@Override
		public FontUIResource getSmallFont() {
			return null;
		}

		@Override
		public FontUIResource getTitleFont() {
			return null;
		}

		@Override
		public FontUIResource getWindowTitleFont() {
			return null;
		}

		
	}
	public static UMSFontSet fontdelegate;
	public static UMSFontSet f() { return fontdelegate; }
*/
	
	public static Error langErrors = new Error(false);
	public static String l(String s) {
		try
		{
			if(DEBUGMODE)
				return sz_mark_language_words + lang.getString(s) + sz_mark_language_words;
			else
				return lang.getString(s);
		}
		catch(Exception e)
		{
            log.error("Glossary for %s / %s is missing", locale.getDisplayCountry(), locale.getDisplayLanguage());
            String defaultWord = "";
			if(defaultLang==null)
			{
				try
				{
					defaultLang = ResourceBundle.getBundle("localization/lang", new Locale("en", "EN"));
                    defaultWord = defaultLang.getString(s);
				}
				catch(Exception err)
				{
					defaultLang = null;
				}
			}
			if(langErrors!=null && langErrors.getError(0)!=null)
				langErrors.getError(0).appendBodyFiltered("\n"+s + " = " + defaultWord + "\n");
                log.info("Default for %s is %s", s, defaultWord);
			return "[NO STRING]";
		}
	}
	public static void setLocale(String country, String language)
	{
		try
		{
			locale = new Locale(country, language);
			lang = ResourceBundle.getBundle("no/ums/pas/localization/lang", locale);
			Locale.setDefault(locale);
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_warning"), "Could not set default locale", e, Error.SEVERITY_WARNING);
		}
	}
	public static void setLocale(String str)
	{
		try
		{
			str = str.trim();
			int underscoreat = str.indexOf("_");
			Locale temp = new Locale(str.substring(0, underscoreat), str.substring(underscoreat+1));
			boolean b_changed = false;
			if(temp!=null && locale!=null && !temp.getLanguage().equals(locale.getLanguage()))
				b_changed = true;
			locale = temp;
			lang = ResourceBundle.getBundle("no/ums/pas/localization/lang", locale);
			Locale.setDefault(locale);
			if(b_changed)
				PAS.pasplugin.onLocaleChanged(locale, temp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Substance 3.3
	/*public void setSubstanceTheme(SubstanceTheme substance) {
		//SubstanceLookAndFeel.setCurrentSkin(substance);
		try {
			SubstanceLookAndFeel.setCurrentTheme(substance);
			UIManager.setLookAndFeel(get_lookandfeel());//new SubstanceBusinessBlackSteelLookAndFeel()); //get_lookandfeel());
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"), "Exception in setSubstanceTheme", e, Error.SEVERITY_WARNING);
		}
		updateUI();
	}*/
	
	//Substance 5.2
	/*public void setSubstanceTheme(SubstanceColorSchemeBundle cols) {
		
	}*/
	
	/*public void setSubstanceTheme(String sz_name) {
		//Substance 3.3
		SubstanceLookAndFeel.setCurrentTheme(sz_name);
		
		//Substance 5.2
		//SubstanceLookAndFeel.setSkin(sz_name);
		
		try {
			UIManager.setLookAndFeel(get_lookandfeel());
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"), "Exception in setSubstanceTheme", e, Error.SEVERITY_WARNING);
		}
		updateUI(); 
	}
	public void setSubstanceSkin(final String sz_skinname)
	{
		SwingUtilities.invokeLater(new Runnable() { 
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(sz_skinname);
				}
				catch(Exception e)
				{
					
				}
				updateUI();
			}
		});
	}*/
	private void updateUI() {
		SwingUtilities.updateComponentTreeUI(getRootPane());
		repaint();
	}
	
	public PAS() {
		super();
		//this("https://secure.ums.no/vb4/", "", "", "", false, null, null, null, null);
		//init();
		m_pas = this;
		g_pas = this;
	}

	public void setDebug(boolean b) { DEBUGMODE = b; }
	public void setForceWMSSite(String s) { OVERRIDE_WMS_SITE = s; }
	public void setSiteName(String s) { PAS_SITENAME = s; }
	public void setPasWsSite(String s) {
		if(s.length()==0)
		{
			PAS_WS_SITE = PAS_SITENAME + "/ExecAlert/WS/";
		}
		else
			PAS_WS_SITE = s;
	}
	public void setCodeBase(String s) { PAS_CODEBASE = s; }
	public void setOverrideCompId(String s) { PAS_OVERRIDE_COMPID = s; }
	public void setOverrideUserId(String s) { PAS_OVERRIDE_USERID = s; }
	public void setAddressSeachUrl(String s) { ADDRESSSEARCH_URL = s; }
	public void setVB4Url(String s) { VB4_URL = s; }
	public void setProgramArguments(String [] a) { m_sz_program_args = a; }
	public String getVB4Url() { return VB4_URL; }
	
	public PAS(String sz_sitename, String sz_userid, String sz_compid, String sz_pasws, 
			boolean b_debug, String sz_codebase, String sz_plugin, 
			String sz_force_wms_site, String [] args)
	{
		super();
		DEBUGMODE = b_debug;
		OVERRIDE_WMS_SITE = sz_force_wms_site;
		try
		{
			//setLocale("en", "EN");
		}
		catch(Exception e)
		{
			
		}
		//this.setTitle(PAS.l("common_app_title"));

		//ImageLoader.setClassLoader(getClass().getClassLoader());
		m_pas = this;
		g_pas = this;
		PAS_SITENAME = sz_sitename;
		if(sz_pasws.length()==0)
		{
			PAS_WS_SITE = PAS_SITENAME + "/ExecAlert/WS/";
		}
		else
			PAS_WS_SITE = sz_pasws;
		PAS_CODEBASE = sz_codebase;
		System.out.println("Using WS " + PAS_WS_SITE);
		
		try
		{
			//m_lookandfeel = new SubstanceOfficeBlue2007LookAndFeel();//SubstanceLookAndFeel();
			//UIManager.setLookAndFeel(m_lookandfeel);
		}
		catch(Exception e)
		{
			
		}
		//m_lookandfeel = new SubstanceBusinessBlackSteelLookAndFeel();
		PAS_OVERRIDE_COMPID = sz_compid;
		PAS_OVERRIDE_USERID = sz_userid;

		//LBASEND.CreateLbaStatusHash();
		
		
		m_sz_program_args = args;
		//init();
	}
	
	public void parseAdditionalParameters(String [] params)
	{
		for(int i=0; i < params.length; i++)
		{
			String str = params[i];
			String val;
			if(str.length()>=3) //expect "-fXarg"
			{
				val = str.substring(3);
				if(str.startsWith("-f"))
				{
					SendObject obj = PAS.get_pas().get_sendcontroller().create_new_sending();
					new ImportPolygon(obj.get_toolbar(), "act_polygon_imported", new File(val));
				}
			}
		}

	}
	
	public void print_map()
	{
		get_drawthread().set_suspended(true);
		get_mappane().print_map();
		get_drawthread().set_suspended(false);
	}
	public void save_map() {
		get_drawthread().set_suspended(true);
		try {
			get_mappane().save_map(get_drawthread().get_buff_image());
		} catch(Exception e) {
			
		} finally {
			get_drawthread().set_suspended(false);
		}
	}
	

	public void init() 	{
		ToolTipManager.sharedInstance().setInitialDelay(10);
		ToolTipManager.sharedInstance().setReshowDelay(10);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                powerUp();
            }
        });
	}
		
	
		
	
	public void checkLoadParm() {
		/*if(m_settings.parm() && get_rightsmanagement().read_parm()) // og har rettighetene i orden
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"act_start_parm"));*/
		if(m_settings.fleetcontrol() && get_rightsmanagement().read_fleetcontrol()) // og har rettighetene i orden
			m_mainmenu.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"act_gps_open"));	

		
	}
	

	public static PasScriptingInterface pasplugin;
	
	private void powerUp()
	{
		
	
		
		
		try
		{
			
			new Thread("PAS powerUp thread") {
	    		public void run()
	    		{
	    			
	    			/*try
	    			{
	    				if(sz_script_class!=null)
	    				{
	    					pasplugin = PluginLoader.loadPlugin(get_codebase(), sz_script_class, no.ums.pas.pluginbase.PluginLoader.FILETYPE.JAR);
	    				}
	    			}
	    			catch(Exception e)
	    			{
	    				pasplugin = null;
	    				Error.getError().addError("Failed to load plugin", "Could not load the plugin \"" + sz_script_class + "\"", e, Error.SEVERITY_ERROR);
	    				e.printStackTrace();
	    			}
	    			if(pasplugin==null)
	    			{
	    				pasplugin = new PAS_Scripting(); //go default
	    			}	*/
	    			pasplugin.onLoadSecurityManager();
	    			createGUI();
	    			
	    		}
	    	}.start();

		}
		catch(Exception e)
		{
			
		}
    	m_b_creategui_finished = true;
	}
	
	private void createGUI() {
		//m_appicon = ImageLoader.load_icon("UMSKule_icon_16x16.gif");
		//m_appicon = ImageLoader.load_icon("pas_appicon_16.png");
		m_appicon = pasplugin.onLoadAppIcon();
		
		//m_lookandfeel = pasplugin.onSetInitialLookAndFeel(getRootPane());
		
		try {
			//UIManager.setLookAndFeel(m_lookandfeel);
			
			
			/*SubstanceLookAndFeel.setFontPolicy(contrib.com.jgoodies.looks.common.FontPolicies.getLooks1xPlasticPolicy());
			final FontSet set = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);
			SubstanceLookAndFeel.setFontPolicy(new FontPolicy() {

				@Override
				public FontSet getFontSet(String arg0, UIDefaults arg1) {
					return new org.jvnet.substance.fonts.ScaledFontSet(set, 1.0f);
		                      //substanceCoreFontSet, newValue);
				}
				
			});*/
			//PropertiesFileParamReader reader = new PropertiesFileParamReader("C:\\Program Files\\UMS Population Alert System\\lookandfeel.config");

			

		} catch(Exception e) {
			System.out.println(e.getMessage());
			Error.getError().addError(PAS.l("common_error"), "Error setting Look and Feel", e, Error.SEVERITY_ERROR);
		}
		try
		{
			String defaultlang = pasplugin.getDefaultLocale(m_settings);
			if(defaultlang.length() > 0)
				setLocale(defaultlang);
		}
		catch(Exception e)
		{
			
		}

		try
		{
			//Substance 3.3
			////
				SubstanceLookAndFeel.registerSkinChangeListener(this);
				SubstanceLookAndFeel.registerThemeChangeListener(this);
				SubstanceLookAndFeel.registerWatermarkChangeListener(this);
				//SubstanceLookAndFeel.registerBorderPainterChangeListener(this);
				SubstanceLookAndFeel.registerButtonShaperChangeListener(this);
				SubstanceLookAndFeel.registerGradientPainterChangeListener(this);
				
				UIParamLoader.loadServerUIParams();
				UIParamLoader.loadUIParams();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

		pasplugin.onBeforeLogon();
		
		
		String sz_userid, sz_compid, sz_passwd;
		sz_userid = PAS_OVERRIDE_USERID; //this.getParameter("sz_userid");
		sz_compid = PAS_OVERRIDE_COMPID; //this.getParameter("sz_compid");
		sz_passwd = ""; //this.getParameter("sz_passwd");
		if(PAS_SITENAME==null)
			PAS_SITENAME = "https://secure.ums.no/vb4/";
		this.addComponentListener(this);
		this.addWindowListener(this);
		
		String sz_home = null;//this.getParameter("home_path");
		String sz_storage_tempwav = "voc\\";
		String sz_storage_status = "status\\";
		String sz_storage_fleetcontrol = "fleetcontrol\\";
		String sz_storage_gisimport = "gis\\";
		String sz_storage_usersettings = "";
		String sz_storage_parm = "PARM\\";
			//sz_home = "C:\\Program Files\\UMS Population Alert System\\";
		String os_name = System.getProperty("os.name");
		boolean b_windows = false;
		try
		{
			if(os_name.toLowerCase().indexOf("win")>=0)
				b_windows = true;
		}
		catch(Exception e)
		{
			
		}
		
		boolean bpathok = false;
		String PASPath = File.separator + "UMS Population Alert System" + File.separator;
		if(!bpathok && b_windows)
		{
			sz_home = "C:\\Program Files\\UMS Population Alert System\\";
			bpathok = tryPath(sz_home);
		}
		if(!bpathok && b_windows)
		{
			sz_home = "C:\\UMS Population Alert System\\";
			bpathok = tryPath(sz_home);
		}
		if(!bpathok)
		{
			sz_home = System.getProperty("user.home") + PASPath;
			System.out.println("user.home=" + sz_home);
			bpathok = tryPath(sz_home);			
		}
		if(!bpathok)
		{
			sz_home = System.getenv("TEMP") + PASPath;
			System.out.println("temp=" + sz_home);
			bpathok = tryPath(sz_home);
		}
		if(!bpathok)
		{
			sz_home = System.getProperty("java.io.tmpdir") + PASPath;
			System.out.println("java.io.tmpdir=" + sz_home);
			bpathok = tryPath(sz_home);
		}
		if(bpathok)
			System.out.println("HOMEPATH=" + sz_home);
		else
			System.out.println("HOMEPATH: Error, no path found for writing");
		//vars.init(m_sz_sitename + "/ExecAlert/WS/");
		vars.init(this.get_pasws());
		//check home path
		
		
		//m_sz_maintitle = PAS.l("common_app_title") + " - " + m_sz_sitename;
		//this.setTitle(m_sz_maintitle); //+ m_sz_sitename);
		String sz_screenX, sz_screenY;
		int scr_x = 1280; int scr_y = 1024;
		Dimension dim_screen;
		sz_screenX = null;//this.getParameter("screenX");
		sz_screenY = null;//this.getParameter("screenY");
		if(sz_screenX==null || sz_screenY==null) {
			dim_screen = new Dimension(scr_x, scr_y);
		} else {
			scr_x = new Integer(sz_screenX).intValue();
			scr_y = new Integer(sz_screenY).intValue();
			dim_screen = new Dimension(scr_x, scr_y);
		}		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dim_screen = screenSize;
		scr_x = screenSize.width-10;
		scr_y = screenSize.height-100;
		dim_map = new Dimension(scr_x, scr_y);
		try
		{
			//SwingUtilities.invokeLater(new Runnable() {
			//	public void run()
				{
					m_mapproperties = new MapitemProperties();
					m_sendcontroller = new SendController(PAS.get_pas());
					Variables.setSendController(m_sendcontroller);
				
					m_drawthread = new PASDraw(PAS.get_pas(), PAS.get_pas(), Thread.NORM_PRIORITY, dim_map.width, dim_map.height);
					Variables.setDraw(m_drawthread);
					m_navigation = new Navigation(PAS.get_pas().get_pasactionlistener(), dim_map.width, dim_map.height);
					Variables.setNavigation(m_navigation);
					//m_httpreq = new HTTPReq(get_sitename(), get_navigation());
					
				}
			//});
		} catch(Exception e)
		{
			
		}

		
		final UMSSecurity security = new UMSSecurity();
		//security.add_check(UMSPermission.PERMISSION_FILE_, sz_home, "write"); //read,write,delete
		security.add_check(UMSPermission.PERMISSION_AUDIO_, null, null);
		security.check_permissions();
		/*try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					security.check_permissions();
				}
			});
		} catch(Exception e) {
			//Error.getError().addError("PAS", "Error checking security permissions", e, Error.SEVERITY_ERROR);
		}*/
		
		String site = PAS.get_pas().get_sitename();
		int start = -1;

		start = site.indexOf("https");
		if(start==-1) {
			start = site.indexOf("http");
			site = site.substring(7,site.length()-1);
		} else
			site = site.substring(8,site.length()-1);
		
		m_storagecontroller.create_storageelements(sz_home, sz_storage_tempwav, sz_storage_status, sz_storage_fleetcontrol,
												sz_storage_usersettings, sz_storage_gisimport, sz_storage_parm + site + "\\");
		SoundRecorder.setVocTempPath(StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_));
		
		//VISUAL SETTINGS MOVED TO WEBSERVICES
		/*m_settings = new XmlReader().loadVisualsSettings(m_settings);
		if(m_settings!=null)
		{
			//SubstanceLookAndFeel.setCurrentTheme(m_settings.getThemeClassName());
			try
			{
				//SubstanceLookAndFeel.setCurrentTheme(SubstanceTheme.getTheme(m_settings.getThemeClassName()));
				//ClassLoader loader = new ClassLoader(); //getSystemResource(m_settings.getThemeClassName());
				//SubstanceTheme activeTheme = new SubstanceMixTheme(m_settings.getThemeClassName());
				        //new SubstancePurpleTheme(),
				        //new SubstanceBarbyPinkTheme()).saturate(0.1);
				//SubstanceLookAndFeel.setCurrentTheme(activeTheme);

				//Substance 3.3
				////
					boolean b;
					b = SubstanceLookAndFeel.setSkin(m_settings.getSkinClassName());
					b = SubstanceLookAndFeel.setCurrentTheme(m_settings.getThemeClassName());
					b = SubstanceLookAndFeel.setCurrentButtonShaper(m_settings.getButtonShaperClassname());
					if(m_settings.getGradientClassname()!=null && m_settings.getGradientClassname().length() > 0)
						b = SubstanceLookAndFeel.setCurrentGradientPainter(m_settings.getGradientClassname());
					else
						b = SubstanceLookAndFeel.setCurrentGradientPainter("org.jvnet.substance.painter.GlassGradientPainter");
					if(m_settings.getTitlePainterClassname()!=null && m_settings.getTitlePainterClassname().length() > 0)
						b = SubstanceLookAndFeel.setCurrentTitlePainter(m_settings.getTitlePainterClassname());
					else
						b = SubstanceLookAndFeel.setCurrentTitlePainter("org.jvnet.substance.title.Glass3DTitlePainter");
					//b = SubstanceLookAndFeel.setcur
					b = SubstanceLookAndFeel.setCurrentWatermark(m_settings.getWatermarkClassName());
					//b = SubstanceLookAndFeel.setCurrentTitlePainter(m_settings.getTitlePainterClassname());
				////
					 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		m_settings = new XmlReader().loadMapSetup(m_settings);
		*/
		try
		{
			this.setIconImage(get_appicon().getImage());
		}
		catch(Exception e)
		{
			
		}
		LogonInfo info = null;
		if(sz_userid!=null && sz_compid!=null && sz_passwd!=null) {
			if(sz_userid.length()>0 && sz_compid.length()>0 && sz_passwd.length()>0)
			{
				info = new LogonInfo(sz_userid, sz_compid, sz_passwd, m_settings.getLanguage());
			}
		}

		XmlReader xmlreader = new XmlReader();
		//Kun default logon settings skal lastes her. resten kommer fra Logon WS
		try {
			m_settings = xmlreader.loadLogonSettings(m_settings);
			Variables.setSettings(m_settings);
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"), "Could not load default logon information", e, Error.SEVERITY_WARNING);
		}

		if(m_settings!=null)
		{
			if(m_settings.getUsername().length()>0 && m_settings.getCompany().length() > 0)
			{
				try
				{
					//LoadVisualSettings(get_pasactionlistener(), m_settings.getUsername(), m_settings.getCompany(), false);
					//initSubstance();
				}
				catch(Exception e)
				{
				}
				info = new LogonInfo(m_settings.getUsername(),m_settings.getCompany());
			}
		}
		
		try
		{
			//String defaultlang = m_settings.getLanguage();
			String defaultlang = pasplugin.getDefaultLocale(m_settings);
			if(defaultlang.length() > 0)
				setLocale(defaultlang);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		boolean b_save_language = false;
		boolean b_logon_saved_remote = false;
		try
		{
			//SwingUtilities.invokeAndWait(new Runnable() {
			//	public void run()
				{
					if(PAS_OVERRIDE_COMPID!=null)
					{
						m_settings.setCompany(PAS_OVERRIDE_COMPID);
					}
					if(PAS_OVERRIDE_USERID!=null)
					{
						m_settings.setUsername(PAS_OVERRIDE_USERID);
					}
					Logon logon = new Logon(get_pas(), new LogonInfo(m_settings.getUsername(),m_settings.getCompany()), m_settings.getLanguage(), false);
					if(!logon.isLoggedOn() || logon.get_userinfo()==null) {
                        LogFrame.remove();
                        for (Frame frame : Frame.getFrames()) {
                            System.out.println(frame);
                            System.out.println(frame.isValid());
                            System.out.println(frame.isVisible());
                            frame.dispose();
                            System.out.println(frame.isValid());
                        }
						return; //System.exit(0);
					}

					m_userinfo = new UserInfo(logon.get_userinfo());
					Variables.setUserInfo(m_userinfo);
					m_userinfo.set_sitename(PAS_SITENAME);
					m_settings.setUsername(m_userinfo.get_userid().toUpperCase());
					m_settings.setCompany(m_userinfo.get_compid().toUpperCase());
					UPASUISETTINGS ui = logon.get_uisettings();
					if(!ui.isInitialized())
					{
						ui.setSzLanguageid(pasplugin.getDefaultLocale(m_settings));
					}
					//if(ui.isInitialized())
					{
						if(OVERRIDE_WMS_SITE!=null)
						{
							if(OVERRIDE_WMS_SITE.toLowerCase().equals("default"))
							{
								ui.setLMapserver(0);
								
							}
							else
							{
								String [] arr = OVERRIDE_WMS_SITE.split(";");
								if(arr!=null && arr.length>=3)
								{
									ui.setLMapserver(1);
									ui.setSzWmsSite(arr[0]);
									ui.setSzWmsFormat(arr[1]);
									ui.setSzWmsLayers(arr[2]);
									if(arr.length>=4)
										ui.setSzWmsEpsg(arr[3]);
									else
										ui.setSzWmsEpsg("4326"); //default to lon/lat WGS84
									if(arr.length>=5)
										ui.setSzWmsUsername(arr[4]);
									else
										ui.setSzWmsUsername("");
									if(arr.length>=6)
										ui.setSzWmsPassword(arr[5]);
									else
										ui.setSzWmsPassword("");
									/*m_settings.setWmsSite(ui.getSzWmsSite());
									m_settings.setWmsUsername(ui.getSzWmsUsername());
									m_settings.setSelectedWmsLayers(ui.getSzWmsLayers());
									m_settings.setSelectedWmsFormat(ui.getSzWmsFormat());
									m_settings.setWmsPassword(ui.getSzWmsPassword());*/
								}
							}
						}
						setVisualSettings(ui);
						try {
							//new MailCtrl("ums.no", "mail.ums.no", 25, "mh@ums.no", "mh@ums.no", this, "PAS Error", "dilldall");
							MailAccount account = new MailAccount();
							get_userinfo().set_mailaccount(account);
						} catch(Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
							Error.getError().addError(PAS.l("common_warning"), "Unable to find your default e-mail settings in registry.", 0, Error.SEVERITY_INFORMATION);
						}
						try
						{
							MailAccount account = get_userinfo().get_mailaccount();
							account.set_accountname(ui.getSzEmailName().trim());
							account.set_displayname(ui.getSzEmailName().trim());
							account.set_mailaddress(ui.getSzEmail().trim());
							account.set_mailserver(ui.getSzEmailserver().trim());
							account.set_port(ui.getLMailport());
						}
						catch(Exception e)
						{
							
						}
						//String lang = ui.getSzLanguageid();
						b_logon_saved_remote = true;
						
					}
					//else
					//String lang = logon.getLogonInfo().get_language();
					String lang = pasplugin.getUserLocale(logon.getLogonInfo(), m_settings);
					if(lang.length()>0)
					{
						if(!lang.equals(defaultLang))
						{
							setLocale(lang);
							m_settings.setLanguage(lang);
							b_save_language = true;
						}
					}

				}
			//});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Could not create GUI\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
			System.exit(0);
		}

		try {
			m_settings = xmlreader.loadScreenSize(m_settings);
			
		} catch(Exception e) {
			//Error.getError().addError("Error loading screen settings", "Could not load last known screen settings", e, Error.SEVERITY_WARNING);
		}

		if(!b_logon_saved_remote) //load local stuff instead
		{
			try {
				m_settings = xmlreader.loadAutostartSettings(m_settings);
			} catch(Exception e) {
				Error.getError().addError(PAS.l("common_error"), "Could not load user-defined settings", e, Error.SEVERITY_WARNING);
			}
			try
			{
				// Her hentes instillingene fra xmlfilen settings.ini
				// Henter nå innstillinger fra WS i logon
				xmlreader.loadSettings();
			}
			catch(Exception e)
			{
				
			}
		}
		try
		{
			//initSubstance();
			//pasplugin.onSetUserLookAndFeel(m_settings, m_userinfo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		LBASEND.CreateLbaStatusHash();
		//setAppTitle("");
		pasplugin.onSetAppTitle(this, "", get_userinfo());
		afterLogon();
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run()
				{
					m_mainmenu.init();
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "act_dept_changed"));
					//m_pasactionlistener.deptChanged();
					if(PAS.get_pas().get_eastcontent().get_infopanel()!=null)
						PAS.get_pas().get_eastcontent().get_infopanel().actionPerformed(new ActionEvent(PAS.get_pas().get_userinfo(), ActionEvent.ACTION_PERFORMED, "act_update_accountinfo"));
				}
			});
		}
		catch(Exception e)
		{
			
		}

		parseAdditionalParameters(m_sz_program_args);
		if(b_save_language)
		{
			//this.actionPerformed(new ActionEvent(m_settings,ActionEvent.ACTION_PERFORMED,"act_save_settingsobject"));
			//System.out.println("Saving Language settings");
		}
		pasplugin.onShowMainWindow();
		pasplugin.onStartSystemMessageThread(this.get_pasactionlistener(), 5000);
	}
	/*public void setAppTitle(String s)
	{
		if(s.length()==0)
			s = PAS.l("common_app_title");
		m_sz_maintitle = s + " - " + m_sz_sitename;
		this.setTitle(m_sz_maintitle + "        " + PAS.l("projectdlg_project")+ " - " + PAS.l("projectdlg_no_project")); //+ m_sz_sitename);		
	}*/
	
	protected void afterLogon()
	{
		System.out.println("Logged on");
		pasplugin.onAfterLogon();

		this.setTitle(m_sz_maintitle + " (" + String.format(PAS.l("common_logged_on_as_format"), m_userinfo.get_realname()) + ")");
		
		m_rightsmanagament = new RightsManagement(m_userinfo.get_default_dept().get_userprofile());
		
		
		
		
		try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					try
					{
						m_mappane = new MapFrame(dim_map.width, dim_map.height, m_drawthread, m_navigation, null, true);
						Variables.setMapFrame(m_mappane);
						m_mappane.addActionListener(get_pasactionlistener());
						m_drawthread.setMapImage(get_mappane().get_mapimage());
						//m_drawthread.setMapOverlay(get_mappane().get_mapoverlay());
						m_drawthread.set_mappane(get_mappane());
	
						m_gpscontroller = new GPSController();
						m_housecontroller = new HouseController();
						m_statuscontroller = PAS.pasplugin.onCreateStatusController(); //new StatusController();
						Variables.setStatusController(PAS.get_pas().m_statuscontroller);
						m_mainmenu = new MainMenu(PAS.get_pas());
						m_inhabitantframe = new InhabitantFrame(PAS.get_pas());
						m_gpsframe = new GPSFrame(get_gpscontroller());
						m_southcontent = new SouthContent(PAS.get_pas());
						m_eastcontent = pasplugin.onCreateEastContent();//new EastContent(PAS.get_pas());
						get_mappane().set_mode(MapFrame.MAP_MODE_PAN);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

				}
			});
		} catch(Exception e) {
			System.out.println(e.getMessage());
			printStackTrace(e.getStackTrace());
			Error.getError().addError(PAS.l("common_error"), "Error creating EastContent", e, Error.SEVERITY_ERROR);
		}
		
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				
			public void run()
			{
				try
				{
					pasplugin.onAddPASComponents(PAS.this);
					pasplugin.onSetInitialMapBounds(get_navigation(), get_userinfo());
					m_mappane.initialize();
					try {
						kickRepaint();
					} catch(Exception e) {
						add_event("ERROR: Could not initialize mappane " + e.getMessage(), e);
						Error.getError().addError(PAS.l("common_error"), "Error initializing mappane", e, Error.SEVERITY_ERROR);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			});
			
		}
		catch(Exception e)
		{
			
		}
		
		
		//m_drawthread.start();
		/*try
		{
			new Installer().save_jnlp();
		}
		catch(Exception e)
		{
			
		}*/

		

		
		
		
		//get_eastcontent().get_infopanel().actionPerformed(new ActionEvent(m_userinfo, ActionEvent.ACTION_PERFORMED, "act_update_accountinfo"));
		/*try
		{
			if(get_eastcontent().get_infopanel()!=null)
				get_eastcontent().get_infopanel().update_accountinfo(m_userinfo);
		}
		catch(Exception e)
		{
			
		}*/
		try {
			//Dimension dim_pos = new Dimension(m_settings.getWindowWidth(), m_settings.getWindowHeight());
			//Dimension dim_size = new Dimension(m_settings.getWindowWidth(), m_settings.getWindowHeight());
			if(m_settings.getWindowHeight() > 0 && m_settings.getWindowWidth() > 0)
			//if(checkWindowBoundsToScreen(dim_pos, dim_size))
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						setSize(new Dimension(m_settings.getWindowWidth(), m_settings.getWindowHeight()));
						m_b_hasinitedsize = true;
					}
				});
			}
			else
			{
				this.setSize(pasplugin.getDefaultScreenSize(m_settings)); //-50);
				this.setLocationRelativeTo(null);
				m_settings.setXpos(0);
				m_settings.setYpos(0);
				
				//this.setExtendedState(Frame.MAXIMIZED_BOTH);
			}
			this.setMinimumSize(pasplugin.getMinimumScreenSize(m_settings));
			if(m_settings.isWindowFullscreen())
				this.setExtendedState(Frame.MAXIMIZED_BOTH);
			else
			{
				setLocation(m_settings.getXpos(), m_settings.getYpos());						
			}
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"), "Error setting GUI size", e, Error.SEVERITY_ERROR);
		}

		/*UIManager.put(LafWidget.TABBED_PANE_PREVIEW_PAINTER,
				 org.jvnet.lafwidget.tabbed.TabPreviewPainter.class);*/
		
		try
		{
			//SubstanceLookAndFeel.setCurrentWatermark(new org.jvnet.substance.watermark.SubstanceMagneticFieldWatermark());
			//Substance 3.3
			//SubstanceLookAndFeel.permanentlyShowHeapStatusPanel(getRootPane());
			
		}
		catch(Exception e)
		{
			
		}
		try
		{
			SwingUtilities.invokeLater(new Runnable(){
				public void run()
				{
					validate();
					//SubstanceLookAndFeel.setCurrentButtonShaper(new org.jvnet.substance.button.StandardButtonShaper());
					setVisible(true);
					
				}
			});
		}
		catch(Exception e)
		{
			
		}
		//start Garbage collector thread
		new Thread("Memory monitor") {
			public void run()
			{
				while(1==1)
				{
					try
					{
						Thread.sleep(60000);
                        long freeMem = Runtime.getRuntime().freeMemory();
						System.out.println("free memory=" + freeMem/1024 + "KB");
					}
					catch(Exception e)
					{
						System.out.println("Garbage Collector failed");
					}

				}
			}
		}.start();
	}
	
	protected boolean checkWindowBoundsToScreen(Dimension pos, Dimension size)
	{
		try
		{
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			if(pos.width < 0 || pos.width > d.width)
				return false;
			if(pos.height < 0 || pos.height > d.height)
				return false;
			if(size.width > d.width)
				return false;
			if(size.height > d.height)
				return false;
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
		
	public void componentHidden(ComponentEvent e) {
		//close app
	}
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) { 
		ActionEvent close = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_exit_application");
		actionPerformed(close);				
	}
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void componentMoved(ComponentEvent e) { }

	private int n_previous_mapwidth = 0;
	private int n_previous_mapheight = 0;
	private static Boolean m_b_firstmap = Boolean.TRUE;
	public static Boolean firstMapLoaded() { return !m_b_firstmap; }

    public void waitForFirstMap()
	{
		try
		{
			if(!firstMapLoaded())
			{
			/*	synchronized(m_b_firstmap) {
					m_b_firstmap.wait(20000);
				}*/
				Timeout to = new Timeout(20, 200);
				while(!to.timer_exceeded())
				{
					if(firstMapLoaded())
						break;
					to.inc_timer();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("waitForFirstMap exited");
	}
	
	private boolean m_b_hasinitedsize = false; //size read from config
	public boolean HasInitedSize() { return m_b_hasinitedsize; }
	
	protected int RESIZE_DELAY = 1000;
	protected Timer resizeWaitingTimer = null;
	
	public void componentResized(ComponentEvent e) {
		//this.setResizable(true);
		int w = getWidth();
		int h = getHeight();
		System.out.println("Size = " + w + " " + h);
		pasplugin.onFrameResize(this, e);
		if(resizeWaitingTimer==null && !m_b_firstmap) //start resizing
		{
			this.resizeWaitingTimer = new Timer(RESIZE_DELAY, get_pasactionlistener());
			this.resizeWaitingTimer.start();
		}
		else if(!m_b_firstmap)
			this.resizeWaitingTimer.restart(); //still resizing
		else if(m_b_firstmap) //done resizing
		{
			//applyResize(false);
			//moved to componentShown
		}
		applyResize(false);
		
	}
	boolean b_gui_initialized = false;
	public void componentShown(ComponentEvent e) {
		String s = "test";
		b_gui_initialized = true;
		applyResize(true);
	}

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduled;

    public void redrawMap(int delay) {
        if (scheduled != null) {
            scheduled.cancel(true);
        }
        scheduled = exec.schedule(new Runnable() {
            @Override
            public void run() {
                scheduled = null;
                // TODO: Update map
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

	public void applyResize(boolean b_from_timer)
	{

		System.out.println("Resizing " + getWidth() + ", " + getHeight());
		if(getWidth()<=0 || getHeight()<=0)
		{
			return;
		}
		if(!m_b_creategui_finished)
			return;
		//get_eastcontent().setPreferredSize(new Dimension(this.get_eastwidth(), getHeight()));
		//get_eastcontent().revalidate();
		//get_mappane().revalidate();
		boolean b_need_new_map = false;
		try
		{
			if(!HasInitedSize())
			{
			}
			else
			{
				if(!isVisible())
					return;		
			}
			int n_mapheight = get_mappane().getHeight();
			int n_mapwidth = get_mappane().getWidth();
			if(n_mapheight>2000 || n_mapwidth>2000)
			{
				System.out.println("Mappane size too large");
				return;
			}
			if((get_mappane().getWidth()!=n_previous_mapwidth || get_mappane().getHeight() != n_previous_mapheight) && (get_mappane().getWidth() > 0 && get_mappane().getHeight()>0)) {
				b_need_new_map = true;
				n_previous_mapwidth = get_mappane().getWidth();
				n_previous_mapheight = get_mappane().getHeight();
			}
		}
		catch(Exception err)
		{
			return;
		}
		try
		{
			dim_map = new Dimension(get_mappane().getWidth(), get_mappane().getHeight());
			Dimension dim_map2 = new Dimension(get_mappane().getWidth(), get_mappane().getHeight());
			System.out.println("  MapSize = " + dim_map.width + ", " + dim_map.height);
			get_navigation().set_dimension(dim_map);
			get_mappane().set_dimension(dim_map2);
			get_eastcontent().revalidate();
			//get_mappane().revalidate();
			get_drawthread().resize(dim_map);
			get_mappane().revalidate();
			
			int w = getWidth();
			//get_mainmenu().setSize(new Dimension(getWidth(), 70));
			get_mainmenu().setPreferredSize(new Dimension(getWidth()-20, get_mainmenu().getWantedHeight()));
			get_mainmenu().revalidate();
			if(b_from_timer) {
				System.out.println("New mapsize = " + dim_map.toString());
				//actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_loadmap"));
				get_mappane().load_map(true);//!m_b_firstmap);
				if(get_eastcontent() != null)
					get_eastcontent().actionPerformed(new ActionEvent(Variables.getNavigation(), ActionEvent.ACTION_PERFORMED, "act_maploaded"));
				//get_navigation().reloadMap();
			}
			if(m_b_firstmap && b_from_timer) {
				//checkLoadParm();
				m_b_firstmap = false;
				m_b_hasinitedsize = true;
			}
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		//kickRepaint();
		
	}
	
	public void load_status()
	{
		get_statuscontroller().open_statuslistframe();
	}
	public synchronized void kickRepaint()
	{
		//if(get_drawthread().m_b_needrepaint)
		//	return;
		try
		{
			if(get_drawthread()!=null)
				get_drawthread().setRepaint(get_mappane().get_mapimage());
		}
		catch(Exception e)
		{
			
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				//get_mappane().repaint(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
				//get_mappane().paintImmediately(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
				//System.out.println("!!!!!EXECUTING KICKREPAINT!!!!!");
				get_mappane().repaint();
				get_mappane().validate();
			}
		});
		/*if(m_n_repaints % 20 == 0) {
			this.repaint();
			m_n_repaints = 0;
		}
		else {
			get_mappane().repaint();
		}*/
		m_n_repaints ++;
	}
	public synchronized void kickRepaint(Rectangle r)
	{
		if(r!=null)
		{
			get_drawthread().setRepaint(get_mappane().get_mapimage());
			get_mappane().repaint(r.x, r.y, r.width, r.height);
		}
	}
	public synchronized void kickRepaint(int x, int y, int width, int height)
	{
		get_drawthread().setRepaint(get_mappane().get_mapimage());
		get_mappane().repaint(x, y, width, height);
	}
	
	protected class RepaintCycler extends Thread {
		private int m_n_ms = 100; //msec pr repaint
		private boolean b_stop = false;
		public void setStop() { b_stop = true; }
		public RepaintCycler(int n_ms_interval) {
			super("Repaint Cycler thread");
		}
		public void run() {
			while(!b_stop) {
				try {
					sleep(m_n_ms);
				} catch(Exception e) {
					
				}
				kickRepaint();
			}
		}
	}
	
	private RepaintCycler m_repaint_thread = null;
	public void execRepaintCycler(boolean b) {
		
		if(b) {
			if(m_repaint_thread!=null)
				return;
			m_repaint_thread = new RepaintCycler(50);
			m_repaint_thread.start();
		}
		else {
			if(m_repaint_thread!=null)
				m_repaint_thread.setStop();
		}
	}
	public void download_houses() {
		boolean b_width_exceeded = false;
		if(get_navigation().get_mapwidthmeters().intValue() > get_housecontroller().get_max_meters_width()) {
			b_width_exceeded = true;
		}
		if(!get_mainmenu().get_selectmenu().get_bar().get_show_houses()) {
			actionPerformed(new ActionEvent(HouseController.HOUSE_DOWNLOAD_DISABLED_, ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
		} else if(b_width_exceeded) {
			actionPerformed(new ActionEvent(HouseController.HOUSE_DOWNLOAD_NO_, ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
		}
		if(get_mainmenu().get_selectmenu().get_bar().get_show_houses() && !b_width_exceeded) {
			actionPerformed(new ActionEvent(HouseController.HOUSE_DOWNLOAD_IN_PROGRESS_, ActionEvent.ACTION_PERFORMED, "act_download_houses_report"));
			pasplugin.onDownloadHouses(get_housecontroller());
			//get_housecontroller().start_download(true);
		}

		get_drawthread().set_neednewcoors(true);
		PAS.get_pas().kickRepaint();
	}
	public void printStackTrace(StackTraceElement [] ste) {
        for (StackTraceElement aSte : ste) {
            add_event(aSte.getFileName() + " " + aSte.getClassName() + "." + aSte.getMethodName() + " line:" + aSte.getLineNumber());
        }
	}
	public synchronized void actionPerformed(ActionEvent e) {
		if(e.getSource()!=null)
			get_pasactionlistener().actionPerformed(e);
	}
		
	public void destroy()
	{
		m_drawthread.stop_thread();
	}	
	public void update(Graphics g)
	{
		paint(g);
	}

	public void activateProject(final Project p) {
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					if(m_current_project!=null) {
						if(m_current_project.equals(p))
							return;
					}
					m_current_project = p;
					setTitle(m_sz_maintitle + "        " + PAS.l("projectdlg_project")+ " - <" + m_current_project.get_projectname() + ">");
					switch(get_userinfo().get_current_department().get_pas_rights())
					{
					case 4: //dont enable sendpane on TAS
						break;
					default:
						get_eastcontent().flip_to(EastContent.PANEL_SENDING_);
						get_eastcontent().ensure_added(EastContent.PANEL_SENDING_);
						get_eastcontent().set_tabtext(EastContent.PANEL_SENDING_, PAS.l("projectdlg_project") + " - " + get_current_project().get_projectname());
						break;
					}
					get_sendcontroller().setActiveProject(p);
				}
			});
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void close_parm(final boolean b_appexit) {
		if(get_parmcontroller()!=null) {
			final LoadingFrame progress = new LoadingFrame(PAS.l("main_parm_closing_parm"), null);
			try
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						progress.set_totalitems(0, PAS.l("main_parm_closing_parm"));
						progress.start_and_show();
					}
				});
			}
			catch(Exception e)
			{
				
			}
			
			(new File(ParmConstants.cleanExit)).delete();
			new Thread("PARM Exit thread")
			{
				public void run()
				{
					/*if(!b_appexit) { //also remove parm tab
						try
						{
							get_eastcontent().setIndexZero();
							get_eastcontent().remove_tab(EastContent.PANEL_PARM_);
						}
						catch(Exception e)
						{
							
						}
					}*/
					try
					{
						new XmlWriter().writeTreeToFile(get_parmcontroller().getTreeCtrl().get_treegui().getTree(),get_parmcontroller().getTreeCtrl().get_treegui().getTreeModel());
					}
					catch(Exception e)
					{
						
					}
					System.out.println("Closing PARM");		
					try
					{
						get_parmcontroller().endSession(true);
					}
					catch(Exception er) { }
					// Hvis programmet avslutter før dette blir gjort vet det at tempfilene skal slettes
					// og henter alt fra databasen igjen.
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							progress.stop_and_hide();
						}
					});
					if(!b_appexit) { //also remove parm tab
						try
						{
							get_eastcontent().setIndexZero();
							get_eastcontent().remove_tab(EastContent.PANEL_PARM_);
						}
						catch(Exception e)
						{
							
						}
					}

					m_parmcontroller = null;
					get_pasactionlistener().actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"act_set_parm_closed"));
				}
			}.start();

		
		}
	}
	
	public int invoke_project(boolean bNewSending) {
		System.out.println(PAS.l("project_ask_new_project"));
		
		int answer = PAS.pasplugin.onInvokeProject();
		if(answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION || answer == JOptionPane.CANCEL_OPTION)
			return ProjectDlg.ACT_PROJECTDLG_CANCEL;
		else {
			if(this.m_current_project!=null) {
				Object[] options;
				if(get_userinfo().get_current_department().get_pas_rights() == 4)
					options = new Object[] { PAS.l("common_yes") };
				else
					options = new Object[] { PAS.l("common_discard_sendings"), PAS.l("common_keep_sendings") };
	
				Object input = JOptionPane.showInputDialog(PAS.get_pas(), PAS.l("project_ask_close_current_project") +" <" + m_current_project.get_projectname() + ">", PAS.l("project_ask_new_project"), JOptionPane.INFORMATION_MESSAGE, null, options, options[0]); 
				if(input != null) {
					if(input.equals(PAS.l("common_keep_sendings"))) {
						m_keep_sendings = true;
						System.out.println("m_keep_sendings=" + m_keep_sendings);
					}
					else
						m_keep_sendings = false;
				}
				else
					return ProjectDlg.ACT_PROJECTDLG_CANCEL;
			}
			
			if(get_eastcontent().get_taspanel() != null && this.m_current_project != null) {
				PAS.get_pas().close_active_project(true, true);
				while(get_current_project() != null)
				{
					try
					{
						Thread.sleep(20);
					}
					catch(Exception e)
					{
						
					}
				}
			}
				
		}
		ProjectDlg dlg = PAS.pasplugin.onCreateOpenProjectDlg(this, get_pasactionlistener(), "act_project_saved", bNewSending);
		//new ProjectDlg(this, get_pasactionlistener(), "act_project_saved", bNewSending);
		dlg.setVisible(true);
		return dlg.getSelectedAction();
	}
	
	public boolean keep_using_current_tas() {
		if(JOptionPane.showConfirmDialog(PAS.get_pas(), PAS.l("project_ask_continue_current_project"),PAS.l("main_tas_panel_new_message"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			return true;
		else {
			PAS.get_pas().close_active_project(true, true);
			//ProjectDlg dlg = new ProjectDlg(this, PAS.get_pas().get_pasactionlistener(), "act_project_saved", true);
			//dlg.setVisible(true);
			while(get_current_project() != null)
				;
			return false;
		}
	}
	
	public void close_active_project(boolean b_wait_for_close, boolean b_close_all_gui) {
		try
		{
			Variables.setStatusController(m_statuscontroller);
			Variables.getStatusController().setClosed();

			get_mappane().resetAllOverlays();
			WaitForStatusThread thread = null;
			System.out.println("Close project");
			get_mainmenu().get_selectmenu().enableStatusExport(false);
			boolean b_confirmed_close = true;

			if(m_sendcontroller.get_sendings().size() > 0 && m_sendcontroller.get_activesending().get_sendproperties().get_projectpk() != PAS.get_pas().get_current_project().get_projectpk()) {
				if(JOptionPane.showConfirmDialog(PAS.get_pas(), String.format(PAS.l("project_close_warning"), (m_current_project!=null ? m_current_project.get_projectname() : "No project")), PAS.l("project_close"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					thread = new WaitForStatusThread(b_close_all_gui);
				}
				else
					b_confirmed_close = false;
			}
			else
				thread = new WaitForStatusThread(b_close_all_gui);
			if(thread!=null)
				thread.start();
			if(thread!=null && b_wait_for_close)
			{
				thread.waitToFinish();
			}
			if(b_close_all_gui && b_confirmed_close)
				PAS.pasplugin.onCloseProject();
			PAS.get_pas().get_sendcontroller().reset_send_id(); // Resets the send id, alerts in a new project should now start from beginning
			//Variables.getStatusController().set_autoupdate(false);
			Variables.setStatusController(null);
			m_statuscontroller = PAS.pasplugin.onCreateStatusController();
			Variables.setStatusController(PAS.get_pas().m_statuscontroller);
		}
		catch(Exception e)
		{
			
		}
	}
	
	class WaitForStatusThread extends Thread {
		boolean b_running = false;
		boolean b_close_all = false;
		WaitForStatusThread(boolean close_all) {
			super("WaitForStatus Thread");
			b_close_all = close_all;
			b_running = true;
		}
		public void waitToFinish()
		{
			final Timeout time = new Timeout(10, 50);
			while(b_running && !time.timer_exceeded())
			{
				try
				{	
					Thread.sleep(time.get_msec_interval());	 
					time.inc_timer();
				} catch(Exception e) { }
			}
		}
		public void run() {
			b_running = true;
			final Timeout time = new Timeout(10, 50);
			try
			{
				//SwingUtilities.invokeLater(new Runnable() {
				//	public void run()
					{
						/*LoadingFrame loader = new LoadingFrame("Waiting for current statusupdate to finish", get_mappane());
						loader.setSize(new Dimension(300, 30));
						loader.set_totalitems(100, "Waiting for current statusupdate to finish");
						loader.set_currentitem(0);
			
						loader.start_and_show();*/
						get_mappane().SetIsLoading(true, PAS.l("main_status_closing"));
						while(get_statuscontroller().get_updates_in_progress() && !time.timer_exceeded()) {
							try { Thread.sleep(time.get_msec_interval()); } catch(InterruptedException e ) { }
							time.inc_timer();
							//loader.set_currentitem(0, new Integer(time.get_remaining_seconds()).toString() + "s");
						}
						//loader.stop_and_hide();
						
						
						if(b_close_all)
						{
							get_sendcontroller().resetActiveProject();
							get_eastcontent().setIndexZero();
							get_eastcontent().remove_tab(EastContent.PANEL_SENDING_);
							get_eastcontent().remove_tab(EastContent.PANEL_STATUS_LIST);
							m_statuscontroller = PAS.pasplugin.onCreateStatusController();
							Variables.setStatusController(PAS.get_pas().m_statuscontroller);
							Variables.getStatusController().set_autoupdate(false);
							//setTitle(m_sz_maintitle  + "        " + PAS.l("projectdlg_project")+ " - " + PAS.l("projectdlg_no_project"));
							PAS.pasplugin.onSetAppTitle(PAS.this, "", get_userinfo());
							m_current_project = null;
						}
						get_mappane().SetIsLoading(false, "");
						b_running = false;
					}
				//});
			}
			catch(Exception e)
			{
				b_running = false;
			}
		}
	}
	
	public void init_parmcontroller() {
		m_parmcontroller = new ParmController(get_sitename(), get_userinfo());
        m_parmcontroller.start();
	}
	
	synchronized public void add_event(String sz_text, Exception err) { 
		//get_southcontent().get_eventpanel().add_row(sz_text);
		System.out.println(sz_text);
		if(err!=null)
			printStackTrace(err.getStackTrace());
	}
	private void add_event(String sz_text) {
		System.out.println(sz_text);
	}
	public void exit_application() {
		PAS.APP_EXIT.set(true);
		close_parm(true);
		XmlWriter xmlwriter = new XmlWriter();
		try
		{
			xmlwriter.saveScreenSize(PAS.get_pas());
		}catch (Exception e) {};
		try
		{
			Installer.cleanup();
		} catch(Exception e){};
		try
		{
			new WSSaveUI(get_pasactionlistener()).runNonThreaded();
		}
		catch(Exception e)
		{
			
		}
		try
		{
			new WSLogoff(get_pasactionlistener(), m_userinfo).runNonThreaded();
		}
		catch(Exception e)
		{
			
		}
		try
		{
			new XmlWriter().saveSettings();
		}
		catch(Exception e)
		{
			
		}
		System.exit(0);
	}
	
	/*class cScheme implements ColorScheme
	{

		@Override
		public Color getDarkColor() {
			//return null;
			return Color.black;
		}

		@Override
		public Color getExtraLightColor() {
			return Color.white;
		}

		@Override
		public Color getForegroundColor() {
			//return null;
			return Color.white;
		}

		@Override
		public Color getLightColor() {
			return Color.LIGHT_GRAY;
		}

		@Override
		public Color getMidColor() {
			return Color.GRAY;
		}

		@Override
		public Color getUltraDarkColor() {
			return Color.DARK_GRAY;
		}

		@Override
		public Color getUltraLightColor() {
			return Color.white;
		}
		
	}*/
	
	@Override
	public void skinChanged() {
		//setSubstanceChanges();
		String active = SubstanceLookAndFeel.getTheme().getActiveTheme().getClass().getName();
		String disabled = SubstanceLookAndFeel.getTheme().getDisabledTheme().getClass().getName();
		String def = SubstanceLookAndFeel.getTheme().getDefaultTheme().getClass().getName();
		String watermark = SubstanceLookAndFeel.getTheme().getWatermarkTheme().getClass().getName();
		System.out.println(active);
		System.out.println(disabled);
		System.out.println(def);
		System.out.println(watermark);
	}
	//Substance 3.3
	////
		@Override
		public void themeChanged() {
			//setSubstanceChanges();
			String szname = SubstanceLookAndFeel.getCurrentThemeName();
			m_settings.setThemeClassName(szname);
			if(SubstanceLookAndFeel.getTheme().getClass().equals(UMSTheme.class))
			{
				System.out.println("UMSTheme");
			}
			else
			{
				SubstanceTheme theme = SubstanceLookAndFeel.getTheme();
				if(theme.getClass().equals(SubstanceComplexTheme.class))
				{
					PAS.active_theme = new UMSTheme(THEMETYPE.COMPLEX);
					PAS.active_theme.setActiveColorScheme(theme.getDefaultTheme().getColorScheme());
					PAS.active_theme.setDisabledColorScheme(theme.getDisabledTheme().getDisabledColorScheme()); //SubstanceLookAndFeel.getDisabledColorScheme());
					//PAS.active_theme.setTitlePaneTheme(theme.getDefaultTitlePaneTheme());
					//PAS.active_theme.setHighlightBackgroundTheme(theme.getHighlightBackgroundTheme());
					PAS.active_theme.setDisabledTheme(theme.getDisabledTheme());
					PAS.active_theme.setActiveTitlePaneTheme(theme.getActiveTitlePaneTheme());
					//PAS.active_theme.setSecondTheme(theme.getSecondTheme());
					//PAS.active_theme.setFirstTheme(theme.getFirstTheme());
					//PAS.active_theme.setWatermarkTheme(theme.getWatermarkTheme());
					PAS.active_theme.setDefaultTheme(theme.getDefaultTheme());
					PAS.active_theme.setActiveTheme(theme.getActiveTheme());
					SubstanceLookAndFeel.setCurrentWatermark(new SubstanceNullWatermark());
				}
				else
				{
					PAS.active_theme = new UMSTheme(THEMETYPE.SIMPLE);
					PAS.active_theme.setActiveColorScheme(theme.getColorScheme());
					PAS.active_theme.setDisabledColorScheme(theme.getDisabledColorScheme()); //SubstanceLookAndFeel.getDisabledColorScheme());
					//PAS.active_theme.setHighlightBackgroundTheme(PAS.active_theme);
				}
				//PAS.active_theme.setNonActivePainter(theme.getNonActivePainter());
				//PAS.active_theme.setWatermarkStampColor(theme.getWatermarkStampColor());
				
				//PAS.active_theme.setActiveColorScheme(SubstanceLookAndFeel.getTheme().getColorScheme());
				//PAS.active_theme.setDisabledColorScheme(SubstanceLookAndFeel.getTheme().getDisabledColorScheme());
				

				SubstanceLookAndFeel.setCurrentTheme(PAS.active_theme);
			}
		}
		@Override
		public void watermarkChanged() {
			//setSubstanceChanges();
		}
		@Override
		public void buttonShaperChanged() {
			//setSubstanceChanges();
		}
		@Override
		public void gradientPainterChanged() {
			//setSubstanceChanges();		
		}
		public boolean tryPath(String s)
		{
			try
			{
				File f = new File(s + "writetest.txt");
				f.delete();
				if(f.createNewFile())
				{
				}
				return true;
			}
			catch(Exception err)
			{

			}
			return false;
		}

}

