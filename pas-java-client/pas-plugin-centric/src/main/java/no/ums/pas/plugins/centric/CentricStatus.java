package no.ums.pas.plugins.centric;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.cb.CBSENDINGRESPONSE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;

public class CentricStatus extends DefaultPanel implements ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane m_status_tabbed;


    private CBSENDINGRESPONSE res;
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res) { 
		this.res = res;
		// update infoting
	}
	
	public CentricStatus(CBSENDINGRESPONSE res) {
		super();
		this.res = res;
		res.getLProjectpk(); // checkstatus
		add_controls();
		
	}
	
	

	@Override
	public void add_controls() {
		
		m_status_tabbed = new JTabbedPane();
		
		
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_status_tabbed, m_gridconst);

        CentricEventStatus m_event = new CentricEventStatus();
        m_status_tabbed.addTab(Localization.l("projectdlg_projectname"), m_event);


        JPanel m_messages = new JPanel();
        m_status_tabbed.addTab(Localization.l("main_sending_lba_heading_messages"), m_messages);

		setPreferredSize(new Dimension(PAS.get_pas().get_eastcontent().getWidth()-50,PAS.get_pas().get_eastcontent().getHeight()-50));
		m_status_tabbed.setPreferredSize(new Dimension(getPreferredSize().width-10,getPreferredSize().height-10));
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}

class CentricEventStatus extends DefaultPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_status = new StdTextLabel("Status info ...", 150,100,true);
	
	public CentricEventStatus() { // Sende med status ting
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add_controls() {
		m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_status, m_gridconst);
		setPreferredSize(new Dimension(200,400));
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}
	
}

class CentricMessagesStatus extends DefaultPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_message = new StdTextLabel(Localization.l("main_sending_lba_message"));
    private StdTextArea m_txt_message = new StdTextArea(Localization.l("common_message_content"));
    private JTabbedPane m_tabbed_operators = new JTabbedPane();
	private JButton m_btn_kill = new JButton(Localization.l("common_kill_sending"));
    private JButton m_btn_update = new JButton(Localization.l("common_update"));
    private JButton m_btn_resend = new JButton(Localization.l("main_status_resend"));

    private JButton m_btn_send_to_address_book = new JButton(Localization.l("main_sending_send_notification"));

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
		m_btn_kill.addActionListener(this);
		m_btn_update.addActionListener(this);
		m_btn_resend.addActionListener(this);
		m_btn_send_to_address_book.addActionListener(this);
		
		m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_message, m_gridconst);

		m_tabbed_operators.add("KPN",new CentricOperatorStatus());
		m_tabbed_operators.add("Vodafone",new CentricOperatorStatus());
		m_tabbed_operators.add("T-Mobile",new CentricOperatorStatus());
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_btn_kill, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_update, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
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

class CentricOperatorStatus extends DefaultPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_hdr_completed = new StdTextLabel(Localization.l("common_completed"));
    private StdTextLabel m_lbl_hdr_total = new StdTextLabel(Localization.l("common_total"));
    private StdTextLabel m_lbl_hdr_percent = new StdTextLabel(Localization.l("common_percent"));
    private StdTextLabel m_lbl_completed = new StdTextLabel("");
	private StdTextLabel m_lbl_total = new StdTextLabel("");
	private StdTextLabel m_lbl_percent = new StdTextLabel("");
	
	public CentricOperatorStatus() {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		set_gridconst(0, inc_panels(), 1, 1);	
		add(m_lbl_hdr_completed, m_gridconst);
		
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_hdr_total, m_gridconst);
		
		set_gridconst(2, get_panel(), 1, 1);
		add(m_lbl_hdr_percent, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_completed, m_gridconst);
		
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_total, m_gridconst);
		
		set_gridconst(2, get_panel(), 1, 1);
		add(m_lbl_percent, m_gridconst);
		
		this.revalidate();
		repaint();
		init();
		
	}

	@Override
	public void init() {
		setVisible(true);		
	}

}

