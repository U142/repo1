package no.ums.pas.entrypoint;

import no.ums.pas.PAS;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.pluginbase.PluginLoader;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.versioning.Versioning;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import java.net.URL;



public class ExecApp {
	public static PAS m_pas;
	public static void main(String[] args) {
		//Object connect_timeout = System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", "20000") ;
		//Object read_timeout = System.getProperties().setProperty("sun.net.client.defaultReadTimeout", "3600000" ) ;
		Object connect_timeout;
		Object read_timeout;
		connect_timeout = System.getProperties().get("sun.net.client.defaultConnectTimeout");
		read_timeout = System.getProperties().get("sun.net.client.defaultReadTimeout");
		System.out.println("sun.net.client.defaultConnectTimeout=" + connect_timeout);
		System.out.println("sun.net.client.defaultReadTimeout=" + read_timeout);
		String sz_sitename = "http://vb4utv/";
		String sz_userid = null;
		String sz_compid = null;
		String sz_import = "";
		String sz_pasws = "";
		String sz_plugin = null;
		String sz_force_wms = null;
		boolean debug = false;
		String sz_codebase = null;
		String sz_addresssearch_url = null;
		String[] arr_args = args;

		for(int i=0; i < args.length; i++) {
			if(args[i].charAt(0)=='-') {
				switch(args[i].charAt(1)) {
					case 's':
						sz_sitename = args[i].substring(2);
						break;
					case 'u':
						sz_userid = args[i].substring(2);
						break;
					case 'c':
						sz_compid = args[i].substring(2);
						break;
					case 'd':
						debug = true;
						break;
					case 'w':
						sz_pasws = args[i].substring(2);
						break;
					case 'p':
						sz_plugin = args[i].substring(2);
						break;
					case 'm':
						sz_force_wms = args[i].substring(2);
						break;
					case 'a':
						sz_addresssearch_url = args[i].substring(2);
						break;
					case 'f':
						if(args[i].length()>3)
						{
							System.out.println("auto import file=" + args[i].substring(3));
							sz_import = args[i].substring(3);
						}
						//JOptionPane.showInputDialog(args[i].substring(2));
						break;
				}
			}
		}
		//JFrame.setDefaultLookAndFeelDecorated(true);

		
	
	
		System.out.println("Using site: " + sz_sitename);
		System.out.println("Using WS: " + sz_pasws);
	
		try
		{
			BasicService basicService = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
			URL url = basicService.getCodeBase();
			System.out.println("Codebase="+url.toExternalForm());
			sz_codebase = url.toExternalForm();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			sz_codebase = sz_sitename;
		}
		
	
		
		m_pas = new PAS(); //(f_sitename, f_userid, f_compid, f_pasws, f_debug, f_codebase, f_plugin, f_force_wms, f_args);
		m_pas.setSiteName(sz_sitename);
		m_pas.setOverrideUserId(sz_userid);
		m_pas.setOverrideCompId(sz_compid);
		m_pas.setPasWsSite(sz_pasws); 
		m_pas.setDebug(debug);
		m_pas.setCodeBase(sz_codebase);
		m_pas.setPlugin(sz_plugin);
		m_pas.setProgramArguments(args);
		m_pas.setForceWMSSite(sz_force_wms);
		m_pas.setAddressSeachUrl(sz_addresssearch_url);
		loadPlugin(sz_codebase, sz_plugin);
		Versioning.initVersioning(PAS.pasplugin);
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				PAS.pasplugin.onSetInitialLookAndFeel(this.getClass().getClassLoader());
				m_pas.init();
			}
		});
	}
	
	
	
	
	
	private static void loadPlugin(String sz_codebase, String sz_plugin)
	{
		try
		{
			if(sz_plugin!=null && sz_plugin.length()>0)
			{
				PAS.pasplugin = PluginLoader.loadPlugin(sz_codebase, sz_plugin, no.ums.pas.pluginbase.PluginLoader.FILETYPE.JAR);
			}
		}
		catch(Exception e)
		{
			PAS.pasplugin = null;
			Error.getError().addError("Failed to load plugin", "Could not load the plugin \"" + sz_plugin + "\"", e, Error.SEVERITY_ERROR);
			JOptionPane.showMessageDialog(null, "Unable to load plugin \"" + sz_plugin + "\"" + sz_codebase, "Unable to load plugin", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
		if(PAS.pasplugin==null)
		{
			PAS.pasplugin = new PAS_Scripting(); //go default
		}			
	}
}