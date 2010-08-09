package no.ums.pas.plugins.centric.status;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.plugins.centric.CentricEastContent;
import no.ums.pas.plugins.centric.ws.WSCentricSend;
import no.ums.pas.plugins.centric.ws.WSCentricStatus;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.parm.CBALERTKILL;
import no.ums.ws.parm.CBSENDBASE;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.CBSTATUS;

public class CentricMessageStatus extends DefaultPanel implements ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_message = new StdTextLabel(PAS.l("main_sending_lba_message") + ":");
	public JTextArea get_txt_message() {
		return m_txt_message;
	}

	private JTextArea m_txt_message;
	private JScrollPane m_txt_previewscroll;
	
	private JTabbedPane m_tabbed_operators = new JTabbedPane();
	public JTabbedPane get_tpane() { return m_tabbed_operators; }
	private JButton m_btn_kill = new JButton("Kill");
	private JButton m_btn_new_message = new JButton(PAS.l("main_tas_panel_new_message"));
	private JButton m_btn_resend = new JButton("Resend");
	private JButton m_btn_send_to_address_book = new JButton("Send to Address book");
	private CentricMessages m_parent;
	public CentricMessages get_parent() { return m_parent; }
	
	private long m_l_refno;
	public long get_refno() { return m_l_refno; }
	
	public CentricMessageStatus(CentricMessages parent, long l_refno) { // Sende med status ting
		super();
		m_parent = parent;
		m_l_refno = l_refno;
		add_controls();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_btn_kill)) {
			if(confirmKill()) {
				CBALERTKILL kill = new CBALERTKILL();
				kill.setLRefno(m_l_refno);
				
				WSCentricSend send = new WSCentricSend(this,"act_kill_cb", kill); 
				send.start();
			}
		}
		else if(e.getSource().equals(m_btn_new_message)) {
			PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSEND_);
		}
		else if(e.getSource().equals(m_btn_send_to_address_book)) {
			
		}
		if(e.getActionCommand().equals("act_kill_cb")) {
			
		}
		if(e.getActionCommand().equals("act_get_cb_status")) {
			CBPROJECTSTATUSRESPONSE res = (CBPROJECTSTATUSRESPONSE)e.getSource();
			CBSTATUS cbs;
			for(int i=0;i<res.getStatuslist().getCBSTATUS().size();++i) {
				cbs = res.getStatuslist().getCBSTATUS().get(i);
				for(int j=0;j<m_tabbed_operators.getComponentCount();++j) {
					CentricOperatorStatus cos = (CentricOperatorStatus)m_tabbed_operators.getComponentAt(j);
					//if(cos.get_operator == cbs.getLOperator()){
						
						//cos.get_lbl_completed().setText(cbs.getHistcell().getULBAHISTCELL().get(cbs.getHistcell().getULBAHISTCELL().size()-1).
					//}
						
				}
			}
		}
	}

	private boolean confirmKill() {
		JFrame frame = get_frame();
		int answer;
		answer = JOptionPane.showConfirmDialog(frame, PAS.l("common_are_you_sure"), null, JOptionPane.YES_NO_OPTION);
		frame.dispose();
		if(answer == JOptionPane.YES_OPTION)
			return true;
		else
			return false;
	}
	
	private JFrame get_frame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(0,0);
		p.setLocation(p.x,p.y+PAS.get_pas().getHeight()/3);
		frame.setLocation(p);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		return frame;
	}
	
	@Override
	public void add_controls() {
		m_btn_kill.addActionListener(this);
		m_btn_new_message.addActionListener(this);
		m_btn_resend.addActionListener(this);
		m_btn_send_to_address_book.addActionListener(this);
		m_btn_send_to_address_book.setEnabled(false);
		
		m_txt_message = new JTextArea("",5,20);
		m_txt_message.setWrapStyleWord(true);
		m_txt_message.setLineWrap(true);
		m_txt_message.setEnabled(false);
		
		m_txt_previewscroll = new JScrollPane(m_txt_message);
		m_txt_previewscroll.setPreferredSize(new Dimension(50,50));
		
		//m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_txt_message, m_gridconst);
		
		
		set_gridconst(0, inc_panels(), 4, 1);
		add(m_tabbed_operators, m_gridconst);
				
		//m_tabbed_operators.add("KPN",new CentricOperatorStatus(this, false,1));
		//m_tabbed_operators.add("Vodafone",new CentricOperatorStatus(this, false,3));
		//m_tabbed_operators.add("T-Mobile",new CentricOperatorStatus(this, false,2));
		m_tabbed_operators.add(PAS.l("common_total"), new CentricOperatorStatus(this, true,-1));
		m_tabbed_operators.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-30, m_parent.getPreferredSize().height/3));	
		
		
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_btn_kill, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_new_message, m_gridconst);
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_send_to_address_book, m_gridconst);
		
		
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height/2));
		
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		super.componentResized(e);
		m_tabbed_operators.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-30, m_parent.getPreferredSize().height/3));	
		setPreferredSize(new Dimension(m_parent.getSize().width-10, m_parent.getSize().height/2));
	}

	@Override
	public void init() {
		setVisible(true);
	}
	
	
	
}
