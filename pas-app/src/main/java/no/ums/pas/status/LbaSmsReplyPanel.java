package no.ums.pas.status;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.USMSINSTATS;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;



public class LbaSmsReplyPanel extends DefaultPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int m_l_status = 3; 
	private JButton btn_new_message;
	private JButton btn_response_details;
	private StdTextLabel lbl_refno;
	private StdTextLabel lbl_status;
	private StdTextLabel lbl_count;
	private ArrayList<StdTextLabel> refno;
	private ArrayList<StdTextLabel> status;
	private ArrayList<StdTextLabel> count;
	private StatusSending m_parent;
	
	public LbaSmsReplyPanel(StatusSending parent) { // Her kan jeg få inn array av LBASmsInStats
		super();
		setPreferredSize(new Dimension(300, 200));
		setBorder(BorderFactory.createRaisedBevelBorder());
		
		refno = new ArrayList<StdTextLabel>();
		status = new ArrayList<StdTextLabel>();
		count = new ArrayList<StdTextLabel>();
		
		lbl_refno = new StdTextLabel(PAS.l("common_refno"));
		lbl_status = new StdTextLabel(PAS.l("mainmenu_status"));
		lbl_count = new StdTextLabel(PAS.l("main_tas_panel_response_count"));
		
		btn_new_message = new JButton(PAS.l("main_tas_panel_new_message"));
		btn_new_message.addActionListener(this);
		btn_new_message.setActionCommand("act_resend");
		
		btn_response_details = new JButton(PAS.l("main_tas_panel_response_details"));
		btn_response_details.addActionListener(this);
		btn_response_details.setActionCommand("act_open_response_details");
		btn_response_details.setVisible(false);
		
		add_controls();
		m_parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if("act_resend".equals(e.getActionCommand())) {
			PAS.get_pas().get_sendcontroller().actionPerformed(new ActionEvent(m_parent, ActionEvent.ACTION_PERFORMED, "act_resend"));
		}
		
	}
	
	public void update_responses(List<USMSINSTATS> usmsinstats) {
		/*for(int i=0;i<count.size();++i)
			count.get(i).setText("0");
		*/
		// Reset responses
		for(int i=0; i<m_l_status;++i) {
			refno.get(i).setText("");
			status.get(i).setText("");
			count.get(i).setText("");
		}
		for(int j=0; j<usmsinstats.size();j++) {
			refno.get(usmsinstats.get(j).getLAnswercode()).setText(String.valueOf(usmsinstats.get(j).getLRefno()));
			status.get(usmsinstats.get(j).getLAnswercode()).setText(usmsinstats.get(j).getSzDescription());
			count.get(usmsinstats.get(j).getLAnswercode()).setText(String.valueOf(usmsinstats.get(j).getLCount()));
		}
		if(usmsinstats.size()==0) {
			btn_new_message.setEnabled(false);
			btn_response_details.setEnabled(false);
		}
		else {
			btn_new_message.setEnabled(true);
			btn_response_details.setEnabled(true);
		}
		
		/*
		for(int i=0;i<count.size();++i)
			if(Integer.parseInt(count.get(i).getText())==0)
				count.get(i).setText("");
				*/
	}

	@Override
	public void add_controls() {
		m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_refno, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(lbl_status, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
		add(lbl_count, m_gridconst);
		add_spacing(DIR_VERTICAL, 10);
		for(int i=0;i<m_l_status;++i) {
			// Her kommer lbaSmsinstats
			set_gridconst(0, inc_panels(), 1, 1);
			refno.add(new StdTextLabel(""));
			add(refno.get(i), m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			status.add(new StdTextLabel(""));
			add(status.get(i), m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			count.add(new StdTextLabel(""));
			add(count.get(i), m_gridconst);
		}
		add_spacing(DIR_VERTICAL, 20);
		set_gridconst(0, inc_panels(), 1, 1);
		add(btn_new_message, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
		add(btn_response_details, m_gridconst);
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}

}

