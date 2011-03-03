package no.ums.pas.entrypoint;

import no.ums.log.swing.LogFrame;
import no.ums.log.swing.LogRecordCollector;
import no.ums.pas.PAS;
import no.ums.pas.pluginbase.DefaultPasScripting;
import no.ums.pas.pluginbase.PasScriptingInterface;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.SwingUtilities;
import java.net.URL;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;


public class
        ExecApp {
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
        String sz_force_wms = null;
		boolean debug = false;
		String sz_codebase = null;
		String sz_addresssearch_url = null;
		String sz_vb4_url = null;
        String codebaseFolder = "";
		String[] arr_args = args;

        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                    case 's':
                        sz_sitename = arg.substring(2);
                        break;
                    case 'u':
                        sz_userid = arg.substring(2);
                        break;
                    case 'c':
                        sz_compid = arg.substring(2);
                        break;
                    case 'd':
                        debug = true;
                        break;
                    case 'w':
                        sz_pasws = arg.substring(2);
                        break;
                    case 'm':
                        sz_force_wms = arg.substring(2);
                        break;
                    case 'a':
                        sz_addresssearch_url = arg.substring(2);
                        break;
					case 'v':
						sz_vb4_url = arg.substring(2);
						break;
                    case 'x':
                        codebaseFolder = arg.substring(2);
                        break;
                    case 'f':
                        if (arg.length() > 3) {
                            System.out.println("auto import file=" + arg.substring(3));
                            sz_import = arg.substring(3);
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
            sz_codebase = url.toExternalForm() + codebaseFolder;
			System.out.println("Codebase="+sz_codebase);
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
		m_pas.setProgramArguments(args);
		m_pas.setForceWMSSite(sz_force_wms);
		m_pas.setAddressSeachUrl(sz_addresssearch_url);
		m_pas.setVB4Url(sz_vb4_url);
		PAS.pasplugin = loadPlugin();

        // Install logging handler and frame
        LogRecordCollector.install();
        LogFrame.install();

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				PAS.pasplugin.onSetInitialLookAndFeel(this.getClass().getClassLoader());
				m_pas.init();
			}
		});
	}
	
	
	
	
	
	public static PasScriptingInterface loadPlugin() {
        SortedSet<PasScriptingInterface> plugins = new TreeSet<PasScriptingInterface>(PasScriptingInterface.COMPARATOR);
        plugins.add(new DefaultPasScripting());
        for (PasScriptingInterface plugin : ServiceLoader.load(PasScriptingInterface.class)) {
            plugins.add(plugin);
        }
        PasScriptingInterface plugin = plugins.first();
        plugin.startPlugin();
        return plugin;
	}
}