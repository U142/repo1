package no.ums.pas.plugins.centric.status;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.ws.WSGetStatusItems;
import no.ums.pas.plugins.centric.CentricEastContent;
import no.ums.pas.plugins.centric.ws.WSCentricStatus;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.parm.CBORIGINATOR;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.CBSTATUS;
import no.ums.ws.pas.status.ULOGONINFO;

public class CentricStatus extends DefaultPanel implements ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane m_status_tabbed;
	public JTabbedPane getTabbedPane() { return m_status_tabbed; }
	
	private CentricEventStatus m_event;
	private CentricMessages m_messages;
	
	public CentricMessages get_messages() { return m_messages; }
	public CentricEventStatus get_event() { return m_event; }
	
	private CBPROJECTSTATUSREQUEST cbsreq;
	private CBPROJECTSTATUSRESPONSE cbsres;
	
	private boolean ready = true;
	public boolean isReady() { return ready; }
	
	@Override
	public void componentResized(ComponentEvent e) {
		//super.componentResized(e);
		if(getWidth()<=0||getHeight()<=0) {
			
		}
		setPreferredSize(new Dimension(PAS.get_pas().get_eastcontent().getWidth()-10,PAS.get_pas().get_eastcontent().getHeight()-20));
		m_status_tabbed.setPreferredSize(new Dimension(getPreferredSize().width-10,getPreferredSize().height-10));
		m_event.componentResized(e);
		//m_messages.componentResized(e);
	}

	private CBSENDINGRESPONSE res;
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res) { 
		this.res = res;
		// update infoting
		getCBStatus(res);
	}
	
	private void getCBStatus(CBSENDINGRESPONSE res) {
		ImageIcon icon = ImageLoader.load_icon("remembermilk_orange.gif");
		for(int i=0;i<m_status_tabbed.getComponentCount();++i)
			m_status_tabbed.setIconAt(i, icon);
		
		ULOGONINFO l = new ULOGONINFO();
		l.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		l.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		l.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		l.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		l.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
		l.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		
		cbsreq = new CBPROJECTSTATUSREQUEST();
		cbsreq.setLProjectpk(res.getLProjectpk());
		cbsreq.setLogon(l);
		
		try {
			ready = false;
			WSCentricStatus getStatus = new WSCentricStatus(this,"act_status_downloaded",cbsreq,this);
			getStatus.start();
		} catch(Exception ex) {
			ready = true;
		}
	}
	
	public CentricStatus(CBSENDINGRESPONSE res) {
		super();
		this.res = res;
		add_controls();
		getCBStatus(res);
		
	}

	@Override
	public void add_controls() {
		//DefaultPanel.ENABLE_GRID_DEBUG = true;
		m_status_tabbed = new JTabbedPane();
		
		
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_status_tabbed, m_gridconst);
		
		setPreferredSize(new Dimension(PAS.get_pas().get_eastcontent().getWidth()-10,PAS.get_pas().get_eastcontent().getHeight()-50));
		m_status_tabbed.setPreferredSize(new Dimension(getPreferredSize().width-10,getPreferredSize().height-10));
		
		m_event = new CentricEventStatus(res, this);
		m_status_tabbed.addTab("Event Name", m_event);
		
		
		m_messages = new CentricMessages(this);
		m_status_tabbed.addTab(PAS.l("main_sending_lba_heading_messages"), m_messages);
		
		/*
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(m_txt_message, m_gridconst);
		
		set_gridconst(0, inc_panels(), 2, 1);
		add(m_message_tabbed, m_gridconst);
		
		set_gridconst(0, inc_panels(), 2, 1);
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
		*/
		
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
		if(e.getActionCommand().equals("act_status_downloaded")){
			for(int i=0;i<m_status_tabbed.getComponentCount();++i)
				m_status_tabbed.setIconAt(i, null);
			ready = true;
		}
	}
	

}




