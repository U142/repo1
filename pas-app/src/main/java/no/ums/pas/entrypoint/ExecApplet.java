package no.ums.pas.entrypoint;

import no.ums.pas.PAS;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class ExecApplet extends JApplet implements Runnable {
	public static final long serialVersionUID = 1;
	private PAS m_pas;
	public ExecApplet() {
		//m_pas = new PAS();
	}
	public void init() {
		//m_pas.init();
		JFrame.setDefaultLookAndFeelDecorated(true);
		/*Substance 3.3
		SubstanceTitlePane.setCanHaveHeapStatusPanel(true);
		*/
		String sz_sitename = "https://secure.ums2.no/vb4utv/";
		String sz_userid = "";
		String sz_compid = "";
		System.out.println("Using site: " + sz_sitename);
		m_pas = new PAS(sz_sitename, sz_userid, sz_compid, "", false, null, null, null, null);
		try {
			m_pas.setVisible(true);
			m_pas.setBounds(0,0,1280,1024);
		}catch(Exception e) {
			
		}
	}
	public void run()
	{
		
	}
}