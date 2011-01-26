package no.ums.pas.pluginbase;

import no.ums.ws.pas.USYSTEMMESSAGES;

import javax.swing.*;


/**
 * @author Morten H. Helvig
 * Abstract class for changing PAS behaviour
 */
public abstract class AbstractPasScriptingInterface implements PasScriptingInterface {

	protected OperatingSystem operatingSystem;
	protected UIDefaults uidefaults_initial;
	
	
	/**
	 * String containing plugin for AddressSearch
	 */
	protected String plugin_AddressSearch;

	/**
	 * Function to init all SubPlugins. The plugin-classes to be loaded are defined in the abstract function setSubPluginNames.
	 * If setSubPluginNames is not overridden, all default plugins will be loaded.
	 * @return true if plugins are loaded ok
	 */
	private boolean initSubPlugins() {
		try
		{
			Class<AbstractPasScriptingInterface.AddressSearch> class_address_search = (Class<AbstractPasScriptingInterface.AddressSearch>)Class.forName(plugin_AddressSearch, true, this.getClass().getClassLoader());
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
	public static AbstractPasScriptingInterface.AddressSearch ADDRESS_SEARCH = null;

    @Override
    public void startPlugin() {
        System.out.println("Starting PasScriptingInterface:"+getClass().getSimpleName());
        setSubPluginNames();
        initSubPlugins();
        _OSLookup();
    }

    /**
	 * Sets the default plugin-class-names. If other plugins are to be used, this function should be overridden
	 */
	protected void setSubPluginNames() {
		System.out.println("***Using Plugins***");
		System.out.println((this.plugin_AddressSearch = "no.ums.pas.pluginbase.defaults.DefaultAddressSearch"));
	}
	
	private void _OSLookup() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win")>=0)
			operatingSystem = OperatingSystem.WIN;
		else if(os.indexOf("mac")>=0)
			operatingSystem = OperatingSystem.MAC;
		else if(os.indexOf("nix")>=0)
			operatingSystem = OperatingSystem.UNIX;
		else
			operatingSystem = OperatingSystem.WIN;
		System.out.println("Operating System: "+os);
	}
	
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
     * Function to determine if user has enabled TrainingMode.
     * Default - if PAS.TRAINING_MODE==true
     * @return If in training mode or not
     */
    protected abstract boolean IsInTrainingMode();


    /**
     * Define all UIDefaults when program starts. If UI changes (e.g. training mode), one may revert to default
     * @return
     */
    protected abstract boolean onGetInitialUIDefaults();



    /**
     * Function that's called after a new list of System Messages are downloaded
     * @param sysmsg message object. List of news/messages from system/operators
     * @return
     */
    protected abstract boolean onHandleSystemMessages(USYSTEMMESSAGES sysmsg);

}

