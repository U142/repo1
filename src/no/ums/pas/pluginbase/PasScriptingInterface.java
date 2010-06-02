package no.ums.pas.pluginbase;

import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.SearchFrame;
import no.ums.pas.core.mainui.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.SearchPanelVals;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu;
import no.ums.pas.core.menus.MainSelectMenu.MainMenuBar;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.ws.pas.UGabSearchResultList;


/**
 * @author Morten H. Helvig
 * Abstract class for changing PAS behaviour
 */
public abstract class PasScriptingInterface
{
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
		 * @param results - Use the list retrieved from onExecSearch
		 * @param list - Populate this list
		 * @return true if ok
		 * @throws Exception
		 */
		public abstract boolean onPopulateList(UGabSearchResultList results, TableList list) throws Exception;
		
		/**
		 * 
		 * @param frame - The frame where the overridden SearchPanelVals will be added
		 * @return - a new overridden version of SearchPanelVals
		 * @throws Exception
		 */
		public abstract SearchPanelVals onCreateSearchPanelVals(SearchFrame frame) throws Exception;
		
		/**
		 * 
		 * @param frame - the frame where the overridden SearchPanelVals will be added
		 * @param callback - Where to the list will notify on clicks
		 * @return - a new overridden version of SearchPanelResultsAddrSearch (list)
		 * @throws Exception
		 */
		public abstract SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(SearchFrame frame, ActionListener callback) throws Exception;
		
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
	}
	
	/**
	 * Sets the default plugin-class-names. If other plugins are to be used, this function should be overridden
	 */
	protected void setSubPluginNames()
	{
		System.out.println("***Using Plugins***");
		System.out.println((this.plugin_AddressSearch = "no.ums.pas.pluginbase.defaults.DefaultAddressSearch"));
	}

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
	 * @param toolbar - SendOptionToolbar to be altered before added
	 * @return
	 */
	public abstract boolean onAddSendOptionToolbar(SendOptionToolbar toolbar);
	
	/**
	 * Function to add buttons below the JMenu
	 * @param menu - MainMenu to be altered
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
	 * @param p - pointer to main PAS object to get the main visual objects. New visual objects may also be added.
	 * @return
	 */
	public abstract boolean onAddPASComponents(PAS p);
	
	/**
	 * Function to determine the geographic location of the first map loaded. 
	 * @param nav - pointer to Navigation object for handling geographic calculations
	 * @param ui - pointer to UserInfo object where geographic hints are stored.
	 * @return
	 */
	public abstract boolean onSetInitialMapBounds(Navigation nav, UserInfo ui);
	
	/**
	 * Function is executed when the user selects a new department
	 * @param pas - pointer to the main PAS object to alter whatever
	 * @return
	 */
	public abstract boolean onDepartmentChanged(PAS pas);
	
	/**
	 * Function to change main window title. Is called after logon and when user changed department (onDepartmentChanged)
	 * @param pas - pointer to the main PAS object
	 * @param s - string containing hint of app title
	 * @return
	 */
	public abstract boolean onSetAppTitle(PAS pas, String s);
	
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
	 * 
	 * @param tab pane created by PAS
	 * @param panel the infopanel that's created by onCreateInfoTab()
	 * @return
	 */
	public abstract boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel);
}