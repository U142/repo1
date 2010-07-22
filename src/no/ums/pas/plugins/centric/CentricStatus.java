package no.ums.pas.plugins.centric;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.parm.CBSENDINGRESPONSE;

public class CentricStatus extends DefaultPanel implements ComponentListener{

	private JTabbedPane m_message_tabbed;
	private StdTextLabel m_lbl_status = new StdTextLabel("Status info ...", 150,100,true);
	private JButton m_btn_kill = new JButton("Kill");
	private JButton m_btn_update = new JButton("Update");
	private JButton m_btn_resend = new JButton("Resend");
	private JButton m_btn_send_to_address_book = new JButton("Send to Address book");
	private CBSENDINGRESPONSE res;
	
	public CentricStatus(CBSENDINGRESPONSE res) {
		super();
		add_controls();
		this.res = res; 
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_btn_kill)) {
			
		}
		else if(e.getSource().equals(m_btn_update)) {
			
		}
		else if(e.getSource().equals(m_btn_resend)) {
			
		}
		else if(e.getSource().equals(m_btn_send_to_address_book)) {
			
		}
	}

	@Override
	public void add_controls() {
		m_message_tabbed = new JTabbedPane();
		m_btn_kill.addActionListener(this);
		m_btn_update.addActionListener(this);
		m_btn_resend.addActionListener(this);
		m_btn_send_to_address_book.addActionListener(this);
		
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_message_tabbed, m_gridconst);
		add(m_lbl_status, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
				
		set_gridconst(0, inc_panels(), 1, 1); //5
		m_btn_kill.setPreferredSize(new Dimension(30,20));
		add(m_btn_kill, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(1, get_panel(), 1, 1);
		m_btn_update.setPreferredSize(new Dimension(50,20));
		add(m_btn_update, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(2, get_panel(), 1, 1);
		m_btn_resend.setPreferredSize(new Dimension(50,20));
		add(m_btn_resend, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(3, get_panel(), 1, 1);
		m_btn_send_to_address_book.setPreferredSize(new Dimension(150,20));
		add(m_btn_send_to_address_book, m_gridconst);
				
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}
	

}

