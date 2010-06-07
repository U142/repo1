package no.ums.pas.entrypoint;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import no.ums.pas.*;
import no.ums.pas.pluginbase.PAS_Scripting;
import no.ums.pas.pluginbase.PluginLoader;
import no.ums.pas.ums.errorhandling.Error;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.jvnet.substance.utils.SubstanceTitlePane;
import org.jvnet.substance.utils.SubstanceTitlePane.SubstanceMenuBar;


public class ExecApp {
	public static PAS m_pas;
	public static void main(String[] args) {
		Object connect_timeout = System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", "20000") ;
		Object read_timeout = System.getProperties().setProperty("sun.net.client.defaultReadTimeout", "3600000" ) ;
		connect_timeout = System.getProperties().get("sun.net.client.defaultConnectTimeout");
		read_timeout = System.getProperties().get("sun.net.client.defaultReadTimeout");
		String sz_sitename = "http://vb4utv/";
		String sz_userid = "";
		String sz_compid = "";
		String sz_import = "";
		String sz_pasws = "";
		String sz_plugin = null;
		boolean debug = false;
		String sz_codebase = null;
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
		
		//Substance 3.3
		//SubstanceTitlePane.setCanHaveHeapStatusPanel(true);
		
		System.out.println("Using site: " + sz_sitename);
		
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
		final String f_sitename = sz_sitename;
		final String f_userid = sz_userid;
		final String f_compid = sz_compid;
		final String f_pasws = sz_pasws;
		final boolean f_debug = debug;
		final String f_codebase = sz_codebase;
		final String f_plugin = sz_plugin;
		final String [] f_args = args;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				loadPlugin(f_codebase, f_plugin);
				PAS.pasplugin.onSetInitialLookAndFeel(this.getClass().getClassLoader());
				m_pas = new PAS(f_sitename, f_userid, f_compid, f_pasws, f_debug, f_codebase, f_plugin, f_args);
			}
		});
/*		m_pas.init();
		m_pas.setBounds(0, 0, 1280, 1000);
		m_pas.setVisible(true);
		m_pas.setState(javax.swing.JFrame.MAXIMIZED_BOTH);*/
	}
	
	private static void loadPlugin(String sz_codebase, String sz_plugin)
	{
		try
		{
			if(sz_plugin!=null)
			{
				PAS.pasplugin = PluginLoader.loadPlugin(sz_codebase, sz_plugin, no.ums.pas.pluginbase.PluginLoader.FILETYPE.JAR);
			}
		}
		catch(Exception e)
		{
			PAS.pasplugin = null;
			Error.getError().addError("Failed to load plugin", "Could not load the plugin \"" + sz_plugin + "\"", e, Error.SEVERITY_ERROR);
			e.printStackTrace();
		}
		if(PAS.pasplugin==null)
		{
			PAS.pasplugin = new PAS_Scripting(); //go default
		}			
	}
}