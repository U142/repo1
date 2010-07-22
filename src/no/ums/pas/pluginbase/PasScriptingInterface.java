package no.ums.pas.pluginbase;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.xml.ws.soap.SOAPFaultException;

import org.geotools.data.ows.Layer;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.logon.LogonDialog;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.LogonDialog.LogonPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.address_search.AddressSearchPanel;
import no.ums.pas.core.mainui.address_search.SearchFrame;
import no.ums.pas.core.mainui.address_search.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.address_search.SearchPanelVals;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu;
import no.ums.pas.core.menus.MainSelectMenu.MainMenuBar;
import no.ums.pas.core.ws.WSThread.WSRESULTCODE;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.ws.pas.UBBNEWSLIST;
import no.ums.ws.pas.UGabSearchResultList;
import no.ums.ws.pas.USYSTEMMESSAGES;


/**
 * @author Morten H. Helvig
 * Abstract class for changing PAS behaviour
 */
public abstract class PasScriptingInterface
{
	
	public enum OPERATING_SYSTEM
	{
		WIN,
		MAC,
		UNIX,
	};
	
	protected OPERATING_SYSTEM operating_system;
	protected UIDefaults uidefaults_initial;
	
	
	/**
	 * String containing plugin for AddressSearch
	 */
	protected String plugin_AddressSearch;

		
	/**
	 * @author Morten H. Helvig
	 * Abstract class for searching addresses
	 */
	public static abstract class AddressSearch
	{
		/**
		 * 
		 * @param spr
		 * @return list of valid addresses according to search
		 * @throws Exception
		 */
		public abstract UGabSearchResultList onExecSearch(SearchPanelVals spr) throws Exception;
		
		/**
		 * 
		 * @param results Use the list retrieved from onExecSearch
		 * @param list Populate this list
		 * @return true if ok
		 * @throws Exception
		 */
		public abstract boolean onPopulateList(UGabSearchResultList results, TableList list) throws Exception;
		
		/**
		 * 
		 * @param frame The frame where the overridden SearchPanelVals will be added
		 * @return a new overridden version of SearchPanelVals
		 * @throws Exception
		 */
		public abstract SearchPanelVals onCreateSearchPanelVals(AddressSearchPanel panel) throws Exception;
		
		/**
		 * 
		 * @param frame the frame where the overridden SearchPanelVals will be added
		 * @param callback Where to the list will notify on clicks
		 * @return a new overridden version of SearchPanelResultsAddrSearch (list)
		 * @throws Exception
		 */
		public abstract SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(AddressSearchPanel panel, ActionListener callback) throws Exception;
		
	}

	/**
	 * Function to init all SubPlugins. The plugin-classes to be loaded are defined in the abstract function setSubPluginNames.
	 * If setSubPluginNames is not overridden, all default plugins will be loaded.
	 * @return true if plugins are loaded ok
	 */
	private boolean initSubPlugins() {
		try
		{
			Class<PasScriptingInterface.AddressSearch> class_address_search = (Class<PasScriptingInterface.AddressSearch>)Class.forName(plugin_AddressSearch, true, this.getClass().getClassLoader());
			ADDRESS_SEARCH = class_address_search.newInstance();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Holds the plugin for address search
	 */
	public static PasScriptingInterface.AddressSearch ADDRESS_SEARCH = null;
	
	/**
	 * Main constructor. Will execute setSubPluginNames and load classes dynamically
	 */
	public PasScriptingInterface()
	{
		System.out.println("PasScriptingInterface");
		setSubPluginNames();
		initSubPlugins();
		_OSLookup();
	}
	
	/**
	 * Sets the default plugin-class-names. If other plugins are to be used, this function should be overridden
	 */
	protected void setSubPluginNames()
	{
		System.out.println("***Using Plugins***");
		System.out.println((this.plugin_AddressSearch = "no.ums.pas.pluginbase.defaults.DefaultAddressSearch"));
	}
	
	private void _OSLookup()
	{
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win")>=0)
			operating_system = OPERATING_SYSTEM.WIN;
		else if(os.indexOf("mac")>=0)
			operating_system = OPERATING_SYSTEM.MAC;
		else if(os.indexOf("nix")>=0)
			operating_system = OPERATING_SYSTEM.UNIX;
		else
			operating_system = OPERATING_SYSTEM.WIN;
		System.out.println("Operating System: "+os);
	}
	
	public abstract boolean onAfterPowerUp(LogonDialog dlg, WSRESULTCODE ws);

	/**
	 * Function is executed right before logon
	 * @return
	 */
	public abstract boolean onBeforeLogon();
	
	/**
	 * Function is executed right after logon
	 * @return
	 */
	public abstract boolean onAfterLogon();
	
	/**
	 * Function is executed right before main window is set visible
	 */
	public abstract boolean onShowMainWindow();
	
	@Deprecated
	public abstract boolean onTest();
	
	/**
	 * Function is executed when user asks for new sending. The SendOptionToolbar object may be altered here
	 * @param toolbar SendOptionToolbar to be altered before added
	 * @return
	 */
	public abstract boolean onAddSendOptionToolbar(SendOptionToolbar toolbar);
	
	/**
	 * Function to add buttons below the JMenu
	 * @param menu MainMenu to be altered
	 * @return
	 */
	public abstract boolean onAddMainMenuButtons(MainMenu menu);
	
	/**
	 * Function to add menu items to JMenu
	 * @param menu
	 * @return
	 */
	public abstract boolean onAddMainSelectMenu(MainMenuBar menu);
	
	/**
	 * Function to start PARM
	 * @return
	 */
	public abstract boolean onStartParm();
	
	/**
	 * Function to close PARM
	 * @return
	 */
	public abstract boolean onCloseParm();
	
	/**
	 * Function to refresh PARM
	 * @return
	 */
	public abstract boolean onRefreshParm();
	
	/**
	 * Function to add components to main window. Is executed right before onShowMainWindow()
	 * @param p pointer to main PAS object to get the main visual objects. New visual objects may also be added.
	 * @return
	 */
	public abstract boolean onAddPASComponents(PAS p);
	
	/**
	 * Function to determine the geographic location of the first map loaded. 
	 * @param nav pointer to Navigation object for handling geographic calculations
	 * @param ui pointer to UserInfo object where geographic hints are stored.
	 * @return
	 */
	public abstract boolean onSetInitialMapBounds(Navigation nav, UserInfo ui);
	
	/**
	 * Function is executed when the user selects a new department
	 * @param pas pointer to the main PAS object to alter whatever
	 * @return
	 */
	public abstract boolean onDepartmentChanged(PAS pas);
	
	/**
	 * 
	 * Function to change main window title. Is called after logon and when user changed department (onDepartmentChanged)
	 * @param pas pointer to the main PAS object
	 * @param s string containing hint of app title
	 * @param userinfo UserInfo struct holding information that may be used to generate an app-title
	 * @return
	 */
	public abstract boolean onSetAppTitle(PAS pas, String s, final UserInfo userinfo);
	
	/**
	 * Function to load a custom security-manager. Loaded right after plugin is loaded.
	 * Defaults to load security manager null. If customized, plugins will have runtime restrictions.
	 * @return
	 */
	public boolean onLoadSecurityManager()
	{
		System.out.println("onLoadSecurityManager");
		try
		{
			System.setSecurityManager(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * Create a panel that will be added by onAddInfoTab
	 * @return an instance InfoPanel
	 */
	public abstract InfoPanel onCreateInfoPanel();
	
	/**
	 * Function to add the created InfoPanel (onCreateInfoPanel) to the system's tabbed pane, or anywhere else
	 * @param tab pane created by PAS
	 * @param panel the infopanel that's created by onCreateInfoTab()
	 * @return
	 */
	public abstract boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel);
	
	/**
	 * Function to set a LAF before any window is opened
	 * @param classloader
	 * @return an instance of a LAF
	 */
	public abstract LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader);
	
	/**
	 * Function to set a user specific LAF. This happens after logon.
	 * @param settings
	 * @return
	 */
	public abstract boolean onSetUserLookAndFeel(Settings settings, final UserInfo userinfo);
	
	/**
	 * Function executed if user is altering default LAF.
	 * @param settings Update settings struct with new LAF info to store at logoff
	 * @return true if ok
	 */
	public abstract boolean onUserChangedLookAndFeel(Settings settings);
	
	/**
	 * Define all UIDefaults when program starts. If UI changes (e.g. training mode), one may revert to default
	 * @return
	 */
	protected abstract boolean onGetInitialUIDefaults();
		
	/**
	 * Function to load the application's window icon
	 * @return instance of an ImageIcon
	 */
	public abstract ImageIcon onLoadAppIcon();
	
	public abstract boolean onBeforeLoadMap(Settings settings);
	
	public abstract boolean onWmsLayerListLoaded(List<Layer> layers, ArrayList<String> check);
	
	/**
	 * 
	 * Function that will be called from the Error object when user click to send
	 * a list of error messages.
	 * @param concat_errorlist String containing all errors generated
	 * @param account MailAccount object that specifies the senders credentials
	 * @param callback where to callback after error reporting is done. Defaults to Error object
	 * @return list of recipient addresses that received the message
	 */
	public abstract List<String> onSendErrorMessages(String concat_errorlist, MailAccount account, ActionListener callback);
	
	/**
	 * Function for parsing and handling Soap Exceptions (e.g. Session timeout)
	 * @param e The Soap exception caught
	 * @return true if handling of the exception is ok
	 */
	public abstract boolean onSoapFaultException(UserInfo info, SOAPFaultException e);
	
	/**
	 * Function called when the webservice session has expired.
	 * Called from onSoapFaultException, if details show that server threw
	 * a com.ums.UmsCommon.USessionExpiredException
	 * @return
	 */
	protected abstract boolean onSessionTimedOutException(UserInfo info);
	
	/**
	 * Function called when logon procedure has succeeded
	 * @param ui
	 * @return
	 */
	public abstract boolean onSessionRenewed(UserInfo ui);
	
	/**
	 * Function to start a new thread for polling system messages
	 * @param callback To notify when each download has completed
	 * @param n_interval_msec Msec interval between polling
	 * @return
	 */
	public abstract boolean onStartSystemMessageThread(ActionListener callback, int n_interval_msec);
	
	/**
	 * Function that's called on interval from SystemMessageThread
	 * @param callback To notify and send a list of system messages to the specified actionlistener
	 * @return
	 */
	protected abstract boolean onExecAskForSystemMessage(ActionListener callback);
	
	/**
	 * Function that's called after a new list of System Messages are downloaded
	 * @param system message object. List of news/messages from system/operators
	 * @return
	 */
	protected abstract boolean onHandleSystemMessages(USYSTEMMESSAGES sysmsg);
	
	/**
	 * Function called if user clicks on Help-About menu
	 * @return
	 */
	public abstract boolean onHelpAbout();
	
	/**
	 * Function called if user has selected to (de)activate training mode
	 * @param b true if Training Mode should be activated
	 * @return
	 */
	public abstract boolean onTrainingMode(boolean b);
	
	/**
	 * Function to determine if user has enabled TrainingMode.
	 * Default - if PAS.TRAINING_MODE==true
	 * @param userinfo May be used to determine if user is in TrainingMode
	 * @return If in training mode or not
	 */
	protected abstract boolean IsInTrainingMode(final UserInfo userinfo);
	
	/**
	 * Define which controls are to be added
	 * @param p The LogonPanel containing predefined controls
	 * @return
	 */
	public abstract boolean onLogonAddControls(LogonPanel p);
	
	/**
	 * Final adjustments to the LogonDialog before it's shown
	 * @param dlg
	 * @return
	 */
	public abstract boolean onCustomizeLogonDlg(LogonDialog dlg);
	
	/**
	 * After paint is called from JMenuBar
	 * @param bar pointer to the bar
	 * @param g Graphics context for the JMenuBar
	 * @return
	 */
	public abstract boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g);

	/**
	 * After map navigation we need to recalc coors to pix
	 * @param nav current Navigation class
	 * @param p Used for access of variables to recalc
	 * @return
	 */
	public abstract boolean onMapCalcNewCoords(Navigation nav, PAS p);
	
	/**
	 * When the map needs repaint it calls this function
	 * @param nav current Navigation class
	 * @param g Graphics context to paint on
	 * @param p Used for access of variables to paint
	 * @return
	 */
	public abstract boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p);
	
	public abstract boolean onFrameResize(JFrame f, ComponentEvent e);
	
	/**
	 * Get default Locale for this site(e.g. no_NO)
	 * @param s
	 * @return
	 */
	public abstract String getDefaultLocale(Settings s);
	
	/**
	 * Get users preferred language
	 * @param l
	 * @param s
	 * @return
	 */
	public abstract String getUserLocale(LogonInfo l, Settings s);
	
	
	public abstract EastContent onCreateEastContent();
}

