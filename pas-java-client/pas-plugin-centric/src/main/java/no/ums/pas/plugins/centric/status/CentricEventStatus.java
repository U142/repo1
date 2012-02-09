package no.ums.pas.plugins.centric.status;

import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.cb.CBSENDINGRESPONSE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class CentricEventStatus extends DefaultPanel implements ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_status = new StdTextLabel(Localization.l("main_statustab_title"),300);
    //private StdTextLabel m_lbl_refno = new StdTextLabel(PAS.l("common_refno") + ": ",300);
	private StdTextLabel m_lbl_sent = new StdTextLabel(Localization.l("main_status_messages_sent") + ":",300);
    private StdTextLabel m_lbl_active = new StdTextLabel(Localization.l("main_status_messages_active") + ":",300);
    private StdTextArea m_sent = new StdTextArea("",100);
	private StdTextArea m_active = new StdTextArea("",100);
	
	private JButton m_btn_finish = new JButton(Localization.l("main_status_finish_project"));

    public StdTextArea get_sent() { return m_sent; }
	public StdTextArea get_active() { return m_active; }
	
	public CentricEventStatus(CBSENDINGRESPONSE res, CentricStatus status) { // Sende med status ting
		super();
		add_controls();
		//m_lbl_refno.setText(m_lbl_refno.getText() + res.getLRefno());
		m_btn_finish.setEnabled(false);
    }

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		super.componentResized(e);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_status, m_gridconst);

		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_sent, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_sent, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_active, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_active, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_btn_finish, m_gridconst);
		setPreferredSize(new Dimension(300,600));
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}
}