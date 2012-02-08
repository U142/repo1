package no.ums.pas.pluginbase;

import no.ums.map.tiled.TileData;
import no.ums.map.tiled.TileLookup;
import no.ums.pas.PAS;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.logon.LogonDialog;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mail.Smtp;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.address_search.*;
import no.ums.pas.core.menus.MainMenu;
import no.ums.pas.core.menus.MainSelectMenu;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.ws.WSDeleteProject.IDeleteProject;
import no.ums.pas.core.ws.WSDeleteStatus.IDeleteStatus;
import no.ums.pas.core.ws.WSPowerup;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.MapLoader;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendOptionToolbar;
import no.ums.ws.pas.UGabSearchResultList;
import org.geotools.data.ows.Layer;

import javax.swing.*;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface PasScriptingInterface {

    Comparator<PasScriptingInterface> COMPARATOR = new Comparator<PasScriptingInterface>() {
        @Override
        public int compare(PasScriptingInterface o1, PasScriptingInterface o2) {
            return o1.getPriority() - o2.getPriority();
        }
    };



    public enum OperatingSystem {
        WIN,
        MAC,
        UNIX,
    }

    /**
     * @author Morten H. Helvig
     * Abstract class for searching addresses
     */
    interface AddressSearch {
        /**
         * 
         * @param spr
         * @return list of valid addresses according to search
         * @throws Exception
         */
        UGabSearchResultList onExecSearch(SearchPanelVals spr) throws Exception;
		
        /**
         * 
         * @param results Use the list retrieved from onExecSearch
         * @param list Populate this list
         * @return true if ok
         * @throws Exception
         * @param sz_address
         * @param sz_no
         * @param sz_postarea
         * @param sz_postarea
         * @param sz_postarea
         * @param sz_country
         */
        
        UGabSearchResultList onExecSearch(String sz_address, String sz_no, String sz_postno, String sz_postarea, String sz_region, AddressSearchCountry sz_country) throws Exception;
		
        /**
         * 
         * @param results Use the list retrieved from onExecSearch
         * @param list Populate this list
         * @return true if ok
         * @throws Exception
         */
        
        
        boolean onPopulateList(UGabSearchResultList results, SearchPanelResults.TableList list) throws Exception;
		
        /**
         * 
         * @param panel The frame where the overridden SearchPanelVals will be added
         * @return a new overridden version of SearchPanelVals
         * @throws Exception
         */
        
        SearchPanelVals onCreateSearchPanelVals(AddressSearchPanel panel) throws Exception;
		
        /**
         * 
         * @param panel the frame where the overridden SearchPanelVals will be added
         * @param callback Where to the list will notify on clicks
         * @return a new overridden version of SearchPanelResultsAddrSearch (list)
         * @throws Exception
         */
        SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(AddressSearchPanel panel, ActionListener callback) throws Exception;
		
    }

    /**
     * Returns the priority for this plugin. Higher priority plugins will be selected over lower priority plugins.
     *
     * Standard plugin will have value {@link Integer#MIN_VALUE}
     * @return plugin priority in loading.B
     */
    int getPriority();

    /**
     * Starts the plugin.
     */
    void startPlugin();

    AddressSearch getAddressSearch();

    AddressSearchCtrl getAddressSearchGui();

    boolean onAfterPowerUp(LogonDialog dlg, WSPowerup ws);

    /**
     * Function is executed right before logon
     * @return
     */
    boolean onBeforeLogon();
	
    /**
     * Function is executed right after logon
     * @return
     */
    boolean onAfterLogon();
	
    /**
     * Function is executed right before main window is set visible
     */
    boolean onShowMainWindow();
	
    /**
     * Function is executed when user asks for new sending. The SendOptionToolbar object may be altered here
     * @param toolbar SendOptionToolbar to be altered before added
     * @return
     */
    boolean onAddSendOptionToolbar(SendOptionToolbar toolbar);
	
    /**
     * Function to add buttons below the JMenu
     * @param menu MainMenu to be altered
     * @return
     */
    boolean onAddMainMenuButtons(MainMenu menu);

    /**
     * Function to alter buttons
     * @param menu
     * @param btnGroup
     * @return
     */
    boolean onMainMenuButtonClicked(MainMenu menu, ButtonGroup btnGroup);

    /**
     * Function to add menu items to JMenu
     * @param menu
     * @return
     */
    boolean onAddMainSelectMenu(MainSelectMenu.MainMenuBar menu);
	
    /**
     * Function to start PARM
     * @return
     */
    boolean onStartParm();
	
    /**
     * Function to close PARM
     * @return
     */
    boolean onCloseParm();
	
    /**
     * Function to refresh PARM
     * @return
     */
    boolean onRefreshParm();
	
    /**
     * Function to add components to main window. Is executed right before onShowMainWindow()
     * @param p pointer to main PAS object to get the main visual objects. New visual objects may also be added.
     * @return
     */
    boolean onAddPASComponents(PAS p);
	
    /**
     * Function to determine the geographic location of the first map loaded. 
     * @param nav pointer to Navigation object for handling geographic calculations
     * @param ui pointer to UserInfo object where geographic hints are stored.
     * @return
     */
    boolean onSetInitialMapBounds(Navigation nav, UserInfo ui);
	
    /**
     * Function is executed when the user selects a new department
     * @param pas pointer to the main PAS object to alter whatever
     * @return
     */
    boolean onDepartmentChanged(PAS pas);
	
    /**
     * 
     * Function to change main window title. Is called after logon and when user changed department (onDepartmentChanged)
     * @param pas pointer to the main PAS object
     * @param s string containing hint of app title
     * @param userinfo UserInfo struct holding information that may be used to generate an app-title
     * @return
     */
    boolean onSetAppTitle(PAS pas, String s, final UserInfo userinfo);
	
    /**
     * Function to load a custom security-manager. Loaded right after plugin is loaded.
     * Defaults to load security manager null. If customized, plugins will have runtime restrictions.
     * @return
     */
    boolean onLoadSecurityManager();
	
	
    /**
     * Create a panel that will be added by onAddInfoTab
     * @return an instance InfoPanel
     */
    InfoPanel onCreateInfoPanel();
	
    /**
     * Function to add the created InfoPanel (onCreateInfoPanel) to the system's tabbed pane, or anywhere else
     * @param tab pane created by PAS
     * @param panel the infopanel that's created by onCreateInfoTab()
     * @return
     */
    boolean onAddInfoTab(JTabbedPane tab, InfoPanel panel);
	
    /**
     * Function to set a LAF before any window is opened
     * @param classloader
     * @return an instance of a LAF
     */
    LookAndFeel onSetInitialLookAndFeel(ClassLoader classloader);
	
    /**
     * Function to set a user specific LAF. This happens after logon.
     * @param settings
     * @return
     */
    boolean onSetUserLookAndFeel(Settings settings, final UserInfo userinfo);
	
    /**
     * Function executed if user is altering default LAF.
     * @param settings Update settings struct with new LAF info to store at logoff
     * @return true if ok
     */
    boolean onUserChangedLookAndFeel(Settings settings);

    /**
     * Function to load the application's window icon
     * @return instance of an ImageIcon
     */
    ImageIcon onLoadAppIcon();
	
    boolean onBeforeLoadMap(Settings settings);
    
    boolean onAfterLoadMap(Settings settings, Navigation nav, MapFrame frame);
	
    boolean onWmsLayerListLoaded(List<Layer> layers, List<String> check);
    
    /**
     * Executes when a cell is not loaded yet.
     * @param g
     * @param lookup
     * @param tile
     * @return
     */
    void onMapCellNotLoaded(Graphics g, TileLookup tileLookup, TileData tileData);
    
    /**
     * Cell failed to load image
     * @param g
     * @param tileLookup
     * @param tileData
     */
    void onMapCellError(Graphics g, TileLookup tileLookup, TileData tileData);
	
    /**
     * 
     * Function that will be called from the Error object when user click to send
     * a list of error messages.
     * @param concat_errorlist String containing all errors generated
     * @param account MailAccount object that specifies the senders credentials
     * @param callback where to callback after error reporting is done. Defaults to Error object
     * @return list of recipient addresses that received the message
     */
    List<String> onSendErrorMessages(String concat_errorlist, MailAccount account, Smtp.smtp_callback callback);
	
    /**
     * Function for parsing and handling Soap Exceptions (e.g. Session timeout)
     * @param e The Soap exception caught
     * @return true if handling of the exception is ok
     */
    boolean onSoapFaultException(UserInfo info, SOAPFaultException e);

    /**
     * Function called when logon procedure has succeeded
     * @param ui
     * @return
     */
    boolean onSessionRenewed(UserInfo ui);
	
    /**
     * Function to start a new thread for polling system messages
     * @param callback To notify when each download has completed
     * @param n_interval_msec Msec interval between polling
     * @return
     */
    boolean onStartSystemMessageThread(ActionListener callback, int n_interval_msec);
	
    /**
     * @return Number of seconds between system messages poll
     */
    int getSystemMessagesPollInterval();
    
    /**
     * Function that's called on interval from SystemMessageThread
     * @param callback To notify and send a list of system messages to the specified actionlistener
     * @return
     */
    boolean onExecAskForSystemMessage(ActionListener callback);

    /**
     * Function called if user clicks on Help-About menu
     * @return
     */
    boolean onHelpAbout();
	
    /**
     * Function called if user has selected to (de)activate training mode
     * @param b true if Training Mode should be activated
     * @return
     */
    boolean onTrainingMode(boolean b);

    /**
     * Define which controls are to be added
     * @param p The LogonPanel containing predefined controls
     * @return
     */
    boolean onLogonAddControls(LogonDialog.LogonPanel p);
	
    /**
     * Final adjustments to the LogonDialog before it's shown
     * @param dlg
     * @return
     */
    boolean onCustomizeLogonDlg(LogonDialog dlg);
	
    /**
     * After paint is called from JMenuBar
     * @param bar pointer to the bar
     * @param g Graphics context for the JMenuBar
     * @return
     */
    boolean onPaintMenuBarExtras(JMenuBar bar, Graphics g);

    /**
     * After paint is called from JMenuBar
     * @param menu pointer to the main menu
     * @param g Graphics context for the DefaultPanel
     * @return
     */
    boolean onPaintMainMenuExtras(DefaultPanel menu, Graphics g);
    /**
     * After map navigation we need to recalc coors to pix
     * @param nav current Navigation class
     * @param p Used for access of Variables to recalc
     * @return
     */
    boolean onMapCalcNewCoords(Navigation nav, PAS p);
	
    /**
     * When the map needs repaint it calls this function
     * @param nav current Navigation class
     * @param g Graphics context to paint on
     * @param p Used for access of Variables to paint
     * @return
     */
    boolean onMapDrawLayers(Navigation nav, Graphics g, PAS p);
	
    /**
     * Call to load map with bounds of all shapes to paint
     * @return
     */
    boolean onMapGotoShapesToPaint();
	
    boolean onMapGotoShape(ShapeStruct s);
	
    boolean onMapGotoNavigation(NavStruct n);
	
    boolean onMapKeyTyped(KeyEvent e);

    boolean onMapKeyPressed(KeyEvent e);

    boolean onMapKeyReleased(KeyEvent e);

    boolean onMapLoadFailed(MapLoader loader);
	
    void addShapeToPaint(ShapeStruct s);
	
    void removeShapeToPaint(ShapeStruct s);
    
    boolean removeShapeToPaint(long id);
	
    void clearShapesToPaint();
	
    Hashtable<String, ShapeStruct> getShapesToPaint(); 
	
    boolean onFrameResize(JFrame f, ComponentEvent e);
	
    Dimension getDefaultScreenSize(Settings s);
	
    Dimension getMinimumScreenSize(Settings s);
	
    /**
     * Get default Locale for this site(e.g. no_NO)
     * @param s
     * @return
     */
    String getDefaultLocale(Settings s);
	
    /**
     * Get users preferred language
     * @param l
     * @param s
     * @return
     */
    String getUserLocale(LogonInfo l, Settings s);
	
	
    EastContent onCreateEastContent();
	
    boolean onShowContactinformation();
	
    boolean onOpenAddressBook();
	
    boolean onOpenProject(Project project, long nFromNewRefno);
	
    boolean onCloseProject();
    
    boolean onStopStatusUpdates();

    int onInvokeProject();

    void onLocaleChanged(Locale from, Locale to);
	
    StatusController onCreateStatusController();
	
    /**
     * 
     * @param e EastContent that's the source of the click
     * @param pane The affected tabbed pane
     * @return
     */
    boolean onEastContentTabClicked(EastContent e, JTabbedPane pane);
	
    /**
     * Called if user clicked create/open project. Create a JDialog based on base type no.ums.pas.core.project.ProjectDlg
     * @param parent parent frame
     * @param callback will call back if a new project is created
     * @param cmd_save action command to call back for new project
     * @param b_newsending if a new sending is to be attached to this project
     * @return a new instance of ProjectDlg
     */
    ProjectDlg onCreateOpenProjectDlg(JFrame parent, ActionListener callback, String cmd_save, boolean b_newsending);
	
    /**
     * Function called when a sending is locked (no more painting allowed before final send confirmation)
     * @param toolbar SendOptionToolbar containing the shape of the sending
     * @return true if sending was locked successfully
     */
    boolean onLockSending(SendOptionToolbar toolbar, boolean bLock);
		
    boolean onDownloadHouses(final HouseController controller);
	
    boolean onSetDefaultPanMode(Settings s);

    Dimension getMinMapDimensions();
	
    /**
     * 
     * @return a value >0.0 <0.5
     */
    float getMapZoomSpeed();
    
    boolean onDownloadDocumentation(UserInfo userinfo);
    
    boolean onDeleteStatus(long refno, IDeleteStatus callback);
    
    boolean onDeleteProject(long projectpk, IDeleteProject callback);
}
