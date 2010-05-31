package no.ums.pas.entrypoint;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import no.ums.pas.*;

import org.jvnet.substance.SubstanceLookAndFeel;
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		//Substance 3.3
		SubstanceTitlePane.setCanHaveHeapStatusPanel(true);
		
		System.out.println("Using site: " + sz_sitename);
		m_pas = new PAS(sz_sitename, sz_userid, sz_compid, sz_pasws, debug, sz_plugin, args);
/*		m_pas.init();
		m_pas.setBounds(0, 0, 1280, 1000);
		m_pas.setVisible(true);
		m_pas.setState(javax.swing.JFrame.MAXIMIZED_BOTH);*/
	}
}