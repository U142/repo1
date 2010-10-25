package no.ums.pas.plugins.centric.status;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.maps.defines.PLMNShape;
import no.ums.pas.plugins.centric.CentricEastContent;
import no.ums.pas.plugins.centric.CentricVariables;
import no.ums.pas.plugins.centric.status.CentricOperatorStatus.OPERATOR_STATE;
import no.ums.pas.plugins.centric.ws.WSCentricSend;
import no.ums.pas.plugins.centric.ws.WSCentricStatus;
import no.ums.pas.status.LBASEND;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.parm.CBALERTKILL;
import no.ums.ws.parm.CBSENDBASE;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.CBSTATUS;
import no.ums.ws.pas.status.ULBASENDING;
import no.ums.ws.pas.status.UPLMN;

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
	private JButton m_btn_kill = new JButton(PAS.l("common_kill_sending"));
	private JButton m_btn_new_message = new JButton(PAS.l("main_tas_panel_new_message"));
	private JButton m_btn_resend = new JButton(PAS.l("main_status_resend"));
	private JButton m_btn_send_to_address_book = new JButton(PAS.l("main_sending_send_notification"));
	private CentricMessages m_parent;
	public CentricMessages get_parent() { return m_parent; }
	protected Hashtable<Integer, CentricOperatorStatus> hash_operators = new Hashtable<Integer, CentricOperatorStatus>(); //operatorpk as key
	protected CentricOperatorStatus total_statuspane = null;
	
	protected static Dimension MINIMUM_SIZE = new Dimension(400,400);
	public static Dimension GETMINIMUMSIZE()
	{
		return MINIMUM_SIZE;
	}
	
	protected boolean checkResendRights()
	{
		Class c = lastcbstatus.getShape().getClass();
		if(c.equals(UPLMN.class))
		{
			if(variables.USERINFO.get_current_department().get_userprofile().get_send()<2)
			{
				return false;
			}
		}
		return true;
	}
	
	protected boolean containsOperator(int pk)
	{
		if(hash_operators.containsKey(pk))
			return true;
		return false;
	}
	protected void putOperator(int pk, CentricOperatorStatus operator)
	{
		hash_operators.put(pk, operator);
	}
	
	public Hashtable<Integer, CentricOperatorStatus> getOperators()
	{
		return hash_operators;
	}
	//private long m_l_refno;
	public long get_refno() { 
		return lastcbstatus.getLRefno(); 
	}
	private CBSTATUS lastcbstatus = null;
	
	public CentricMessageStatus(CentricMessages parent, CBSTATUS cbstatus) { // Sende med status ting
		super();
		m_parent = parent;
		//m_l_refno = l_refno;
		lastcbstatus = cbstatus;
		
		add_controls();
		addComponentListener(this);
	}
	
	public void UpdateStatus(CBSTATUS cbstatus, long project_timestamp)
	{
		lastcbstatus = cbstatus;
		
		this.get_txt_message().setText(lastcbstatus.getMdv().getSzMessagetext());
		setName(lastcbstatus.getSzSendingname());
		List<ULBASENDING> arr_operators = cbstatus.getOperators().getULBASENDING();
		
		for(int oper=0; oper < arr_operators.size(); oper++)
		{
			ULBASENDING operator = arr_operators.get(oper);
			CentricOperatorStatus currentoperator = null;
			if(containsOperator(operator.getLOperator()))
			{
				currentoperator = hash_operators.get(operator.getLOperator());
			}
			else
			{
				currentoperator = new CentricOperatorStatus(this, false, operator);
				final String szOperator = operator.getSzOperator();
				final CentricOperatorStatus final_cos = currentoperator;
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						get_tpane().add(szOperator, final_cos);						
					}
				});
				putOperator(operator.getLOperator(), currentoperator);
				//hash_operators.put(operator.getLOperator(), currentoperator);
			}
			if(currentoperator!=null)
				currentoperator.UpdateStatus(cbstatus, operator, project_timestamp);
			
			//update label
			try
			{
				final JTabbedPane final_tp = get_tpane();
				final CentricOperatorStatus final_cms = currentoperator;
				final String szOperatorName = currentoperator.m_operator.getSzOperator();
				final String final_lbl = "<html>" + currentoperator.getStatusAbb() + " <font color=black>" + szOperatorName + "</font></html>";
				final String final_tooltip = currentoperator.getStatusTooltip();
				SwingUtilities.invokeLater(new Runnable() {					
					@Override
					public void run() {
						int n = final_tp.indexOfComponent(final_cms);
						if(n>=0)
						{
							final_tp.setTitleAt(n, final_lbl);
							final_tp.setToolTipTextAt(n, final_tooltip);
						}
						else
							System.out.println("Component " + final_cms + " not found");						
					}
				});
			}
			catch(Exception e)
			{
				
			}	
			
			/*if(operator.getLStatus()<1000) // All statuses under 1000 are still active
				active.put(new Long(cbs.getLRefno()), new Long(cbs.getLRefno()));
			
			for(int i=0;i<m_tabbed_operators.getComponentCount();++i) {
				ms = (CentricMessageStatus)m_tabbed_operators.getComponentAt(i);
				
				// Does the message already exist?
				if(ms.get_refno() == cbs.getLRefno()) {
					ms.get_txt_message().setText(cbs.getMdv().getSzMessagetext());
					ms.setName(cbs.getSzSendingname());
					
					if(operator.getLStatus() >= 540)  // Active
						tp.setTitleAt(i,"A " + cbs.getSzSendingname());
					if(operator.getLStatus() == 1000) // Finished
						tp.setTitleAt(i,"F " + cbs.getSzSendingname());

					CentricOperatorStatus cos;
					
					
					boolean operator_found = false;
					for(int k=0;k<ms.get_tpane().getComponentCount();++k) {
						//cos.get_lbl_channel().setText(histcell.g);
						cos = (CentricOperatorStatus)ms.get_tpane().getComponentAt(k);
						if(cos.get_operator() == operator.getLOperator()) {
							setOperatorValues(cbp, cbs, operator, cos);
							operator_found = true;
						}
					}
					if(!operator_found) {
						cos = new CentricOperatorStatus(ms, false, operator.getLOperator());
						setOperatorValues(cbp, cbs, operator, cos);
						ms.get_tpane().add(operator.getSzOperator(), cos);
					}
					
					found = true;
				}
			}*/
		}
		updateTotal(project_timestamp);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_btn_kill)) {
			if(confirmKill()) {
				m_btn_kill.setEnabled(false);
				CBALERTKILL kill = new CBALERTKILL();
				try
				{
					kill.setLProjectpk(new Long(m_parent.get_parent().getResultSet().getProject().getLProjectpk()));
					kill.setLDeptpk(variables.USERINFO.get_current_department().get_deptpk());
				}
				catch(Exception err)
				{
					err.printStackTrace();
				}
				//kill.setLProjectpk()
				//kill.setLProjectpk(lastcbstatus.get)
				kill.setLRefno(get_refno());
				
				WSCentricSend send = new WSCentricSend(this,"act_cb_killed", kill); 
				send.start();
			}
		}
		else if(e.getSource().equals(m_btn_new_message)) {
			if(checkResendRights())
			{
				PAS.get_pas().get_eastcontent().flip_to(CentricEastContent.PANEL_CENTRICSEND_);
				CentricVariables.centric_send.fromTemplate(lastcbstatus);
			}
		}
		else if(e.getSource().equals(m_btn_send_to_address_book)) {
			
		}
		if(e.getActionCommand().equals("act_cb_killed")) {
			CBSENDINGRESPONSE response = (CBSENDINGRESPONSE)e.getSource();
			switch((int)response.getLCode())
			{
			case 0: //ok
				break;
			case -1: //error writing file / updating status
				//m_btn_kill.setEnabled(true);
				checkEnableKillButton();
				break;
			case -2: //already killed
				break;
			}
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
		answer = JOptionPane.showConfirmDialog(frame, PAS.l("common_are_you_sure"), PAS.l("common_kill_sending") + " " + get_refno(), JOptionPane.YES_NO_OPTION);
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
		m_btn_new_message.setEnabled(checkResendRights());
		m_btn_resend.addActionListener(this);
		m_btn_send_to_address_book.addActionListener(this);
		m_btn_send_to_address_book.setEnabled(false);
		
		m_txt_message = new JTextArea("",5,20);
		m_txt_message.setWrapStyleWord(true);
		m_txt_message.setLineWrap(true);
		m_txt_message.setEnabled(false);
		
		m_txt_previewscroll = new JScrollPane(m_txt_message);
		m_txt_previewscroll.setPreferredSize(new Dimension(330,100));
		
		//m_gridconst.fill = GridBagConstraints.BOTH;
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 3, 1);
		add(m_txt_previewscroll, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 20);
		
		set_gridconst(0, inc_panels(), 4, 1);
		add(m_tabbed_operators, m_gridconst);
				
		//m_tabbed_operators.add("KPN",new CentricOperatorStatus(this, false,1));
		//m_tabbed_operators.add("Vodafone",new CentricOperatorStatus(this, false,3));
		//m_tabbed_operators.add("T-Mobile",new CentricOperatorStatus(this, false,2));
		ULBASENDING operator =new ULBASENDING();
		operator.setLOperator(-1); //mark operatorpk as -1 for total pane
		operator.setLStatus(LBASEND.LBASTATUS_DUMMY_OPERATOR);
		total_statuspane = new CentricOperatorStatus(this, true,operator);
		m_tabbed_operators.add(PAS.l("common_total"), total_statuspane);
		m_tabbed_operators.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-30, m_parent.getPreferredSize().height/2));	
		putOperator(operator.getLOperator(), total_statuspane);
		
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_btn_kill, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_btn_new_message, m_gridconst);
		set_gridconst(3, get_panel(), 1, 1);
		add(m_btn_send_to_address_book, m_gridconst);
		
		
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height));
		
		this.revalidate();
		repaint();
		init();
	}
	
	public OPERATOR_STATE getOperatorStatus()
	{
		OPERATOR_STATE worst_status = OPERATOR_STATE.DUMMY_OPERATOR;
		Enumeration<CentricOperatorStatus> en = hash_operators.elements();
		while(en.hasMoreElements())
		{
			CentricOperatorStatus op = en.nextElement();
			if(op.getOperatorStatus().ordinal() < worst_status.ordinal())
			{
				worst_status = op.getOperatorStatus();
			}
		}
		return worst_status;
	}
	
	public boolean getAtLeastOneOperatorCanBeKilled()
	{
		if(PAS.TRAINING_MODE)
			return false;
		boolean ret = false;
		Enumeration<CentricOperatorStatus> en = hash_operators.elements();
		while(en.hasMoreElements())
		{
			CentricOperatorStatus op = en.nextElement();
			switch(op.getOperatorStatus())
			{
				case ACTIVE:
				case INITIALIZING:
					ret = true;
					break;
				case ERROR:
				case FINISHED:
				case KILLING:
				case DUMMY_OPERATOR:
					break;
			}
			if(ret)
				break;
			
		}
		return ret;
	}
	
	public boolean getAllOperatorsFinished()
	{
		boolean ret = true;
		Enumeration<CentricOperatorStatus> en = hash_operators.elements();
		while(en.hasMoreElements())
		{
			CentricOperatorStatus op = en.nextElement();
			switch(op.getOperatorStatus())
			{
			case FINISHED:
			case DUMMY_OPERATOR:
				break;
			default:
				ret = false;
			}
			if(!ret)
				break;
		}		
		return ret;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
			return;
		super.componentResized(e);
		setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_tabbed_operators.setPreferredSize(new Dimension(getWidth()-30, getHeight()/2));
		m_txt_previewscroll.setPreferredSize(new Dimension(getWidth()-30-m_lbl_message.getPreferredSize().width, 100));
		MINIMUM_SIZE = new Dimension(getWidth(), getHeight());
		//m_tabbed_operators.setPreferredSize(new Dimension(m_parent.getPreferredSize().width-30, m_parent.getPreferredSize().height/2));	
		//setPreferredSize(new Dimension(m_parent.getPreferredSize().width-10, m_parent.getPreferredSize().height));
	}

	@Override
	public void init() {
		setVisible(true);
	}
	
	private void updateTotal(long db_timestamp) 
	{
	
		CentricOperatorStatus cos;
	
		int total=0;
		int unknown=0;
		int total_ok=0;
		float total_percent=0;
		long start = Long.parseLong("99999999999999");
		long timestamp = 0;
		int channel=0;

		Enumeration<CentricOperatorStatus> operators = getOperators().elements();
		while(operators.hasMoreElements())
		{

			CentricOperatorStatus operator = operators.nextElement();
			switch(operator.m_operator.getLOperator())
			{
				case -1: //the totals tab pane
					break;
				default: //operator tab pane
					total += operator.total<0?0:operator.total;
					unknown += operator.unknown<0?0:operator.unknown;
					total_ok += operator.total_ok<0?0:operator.total_ok;
					total_percent += operator.percent;
					if(operator.start<start) //use oldest start timestamp
						start = operator.start;
					if(operator.timestamp > timestamp)
						timestamp = operator.timestamp; //use newest timestamp
					
					channel = operator.channel;						
					break;
			}
		}
		switch(getOperatorStatus()) //get worst status. If the message is still alive, use now as timestamp
		{
			case ACTIVE:
			case INITIALIZING:
			case KILLING:
				timestamp = db_timestamp;
				break;
			case FINISHED:
			case ERROR:
			case DUMMY_OPERATOR:
				//use the latest timestamp from operators
				break;
		}
		total_statuspane.total = total;
		total_statuspane.unknown = unknown;
		total_statuspane.total_ok = total_ok;
		int num_operators = getOperators().size()-1;
		total_percent = (num_operators>=1 ? total_percent/num_operators : 0);
		//total_percent = (total_percent/(ms.get_tpane().getComponentCount()-1));
		total_statuspane.percent = total_percent;
		total_statuspane.get_lbl_channel().setText(String.valueOf(channel));
		total_statuspane.get_lbl_completed().setText(String.valueOf(total_ok<0?"N/A":total_ok));
		total_statuspane.get_lbl_unknown().setText(String.valueOf(total<0?"N/A":unknown));
		total_statuspane.get_lbl_total().setText(String.valueOf(total<0?"N/A":total));
		total_statuspane.get_lbl_percent().setText(String.valueOf(total_percent));
		// Start
		total_statuspane.get_lbl_start().setText(no.ums.pas.plugins.centric.tools.TextFormat.format_datetime(String.valueOf(start)));
		// Duration
		total_statuspane.get_lbl_duration().setText(String.valueOf(TextFormat.datetime_diff_minutes(start,timestamp) + " " + PAS.l("common_minutes_maybe")));
		
		/*switch(getOperatorStatus())
		{
		case KILLING:
		case FINISHED:
		case ERROR:
		//case INITIALIZING:
			m_btn_kill.setEnabled(false);
			break;
		default:
			m_btn_kill.setEnabled(true);
			break;
		}*/
		checkEnableKillButton();
	}
	
	public void checkEnableKillButton()
	{
		m_btn_kill.setEnabled(getAtLeastOneOperatorCanBeKilled());
		if(PAS.TRAINING_MODE)
			m_btn_kill.setToolTipText(PAS.l("mainmenu_trainingmode"));
		else
			m_btn_kill.setToolTipText(null);
		
	}
	
}
