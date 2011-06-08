package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Frame;



public class SendOptionDlg extends JFrame {

    private static final Log log = UmsLog.getLogger(SendOptionDlg.class);

	public static final long serialVersionUID = 1;
	private PAS m_pas;
	private PAS get_pas() { return m_pas; }
	private SendOptionToolbar m_panel;
	
	public SendOptionDlg(PAS pas, Frame owner, boolean b_modal) {
		super("Sending");
		//setNativeLookAndFeel();
		
		m_pas = pas;
		//setModal(b_modal);
		setName("Sending");
		setTitle("Send options");
		
		//setUndecorated(true);
		int n_width  = 610;
		int n_height = 230;
		setBounds((pas.get_mappane().get_dimension().width + pas.get_eastwidth())/2 - n_width/2, (pas.get_mappane().get_dimension().height + pas.get_southheight())/2 - n_height/2, n_width, n_height);
		//setSize(n_width, n_height);
		add_panel();
		pack();
		try {
			setAlwaysOnTop(true);
		} catch(Exception e) {
			//get_pas().add_event("Error: " + e.getMessage(), e);
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		setVisible(true);
	}
	void add_panel() {
		//m_panel = new SendOptionToolbar(get_pas(), get_pas().get_sendcontroller().get_sendings().size()+1);
		//getContentPane().add(m_panel, BorderLayout.CENTER);
		get_pas().add(m_panel, BorderLayout.CENTER);
	}	
}











