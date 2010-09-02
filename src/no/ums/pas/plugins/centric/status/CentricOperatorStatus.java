package no.ums.pas.plugins.centric.status;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JTabbedPane;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.pas.status.CBSTATUS;
import no.ums.ws.pas.status.ULBAHISTCELL;
import no.ums.ws.pas.status.ULBASENDING;

public class CentricOperatorStatus extends DefaultPanel implements ComponentListener {

	public enum OPERATOR_STATUS
	{
		INITIALIZING,
		ACTIVE,
		FINISHED,
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StdTextLabel m_lbl_hdr_completed = new StdTextLabel(PAS.l("common_completed"));
	private StdTextLabel m_lbl_hdr_total = new StdTextLabel(PAS.l("common_total"));
	private StdTextLabel m_lbl_hdr_percent = new StdTextLabel(PAS.l("common_percent"));
	private StdTextLabel m_lbl_hdr_channel = new StdTextLabel(PAS.l("common_channel"));
	private StdTextLabel m_lbl_hdr_duration = new StdTextLabel(PAS.l("common_duration"));
	private StdTextLabel m_lbl_hdr_start = new StdTextLabel(PAS.l("common_start"));
	private StdTextLabel m_lbl_hdr_unknown = new StdTextLabel(PAS.l("main_sending_type_unknown"));
	
	private StdTextLabel m_lbl_completed = new StdTextLabel("");
	private StdTextLabel m_lbl_total = new StdTextLabel("");
	private StdTextLabel m_lbl_percent = new StdTextLabel("");
	private StdTextLabel m_lbl_channel = new StdTextLabel("991");
	private StdTextLabel m_lbl_duration = new StdTextLabel("6 hours");
	private StdTextLabel m_lbl_start = new StdTextLabel("23-06-2010 22:00",200);
	private StdTextLabel m_lbl_unknown = new StdTextLabel("");
	
	//private int m_operator;
	public ULBASENDING get_operator() { return m_operator; }
	protected ULBASENDING m_operator;
	public int get_operatorpk() { return m_operator.getLOperator(); }
	private CentricMessageStatus m_parent;
	public int total;
	public int total_ok;
	public int unknown;
	public float percent;
	public long start;
	public long timestamp;
	public int channel;
	
	
	public StdTextLabel get_lbl_completed() { return m_lbl_completed; }
	public StdTextLabel get_lbl_total() { return m_lbl_total; }
	public StdTextLabel get_lbl_percent() { return m_lbl_percent; }
	public StdTextLabel get_lbl_channel() { return m_lbl_channel; }
	public StdTextLabel get_lbl_duration() { return m_lbl_duration; }
	public StdTextLabel get_lbl_start() { return m_lbl_start; }
	public StdTextLabel get_lbl_unknown() { return m_lbl_unknown; }
	
	public CentricOperatorStatus(CentricMessageStatus parent, boolean is_total, ULBASENDING operator) {
		// Operator
		super();
		m_parent = parent;
		add_controls();
		m_operator = operator;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_start, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_start, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_duration, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_duration, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_channel, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_channel, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_total, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_total, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);	
		add(m_lbl_hdr_completed, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_completed, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_unknown, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_unknown, m_gridconst);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_hdr_percent, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(m_lbl_percent, m_gridconst);
		
		
		setPreferredSize(new Dimension(m_parent.getPreferredSize().width-30, (m_parent.getPreferredSize().height)-10));
		
		this.revalidate();
		repaint();
		init();
		
	}

	@Override
	public void init() {
		setVisible(true);		
	}

	public void UpdateStatus(CBSTATUS cbs, ULBASENDING operator, long db_timestamp)
	{
		m_operator = operator;
		List<ULBAHISTCELL> arr_cellstatus = m_operator.getHistcell().getULBAHISTCELL();
		ULBAHISTCELL histcell = null;
		if(arr_cellstatus.size()>0)
			histcell = arr_cellstatus.get(0);
		
		int tmp_total_ok = 0;
		int tmp_total_unknown = 0;
		int tmp_total = 0;
		float tmp_percent = 0;
		
		if(histcell != null) {
			tmp_total_ok = histcell.getL2Gok() + histcell.getL3Gok() + histcell.getL4Gok();
			tmp_total = histcell.getL2Gtotal() + histcell.getL3Gtotal() + histcell.getL4Gtotal();
			tmp_total_unknown = tmp_total - total_ok;
			
			total = tmp_total;
			total_ok = tmp_total_ok;
			unknown = tmp_total_unknown;
			tmp_percent = Math.round((((float)total_ok/(float)total)*100.0));//histcell.getLSuccesspercentage();
			
			
		}
		percent = tmp_percent;
		if(total<=0)
			percent=0;
		get_lbl_completed().setText(String.valueOf(total_ok<0?PAS.l("common_na"):total_ok));
		get_lbl_unknown().setText(String.valueOf(total<0?PAS.l("common_na"):tmp_total_unknown));
		get_lbl_total().setText(String.valueOf(total<0?PAS.l("common_na"):total));
		get_lbl_unknown().setText(String.valueOf(unknown<0?PAS.l("common_na"):unknown));
		get_lbl_percent().setText(String.valueOf(percent));
		// Channel
		get_lbl_channel().setText(String.valueOf(cbs.getLChannel()));
		channel = cbs.getLChannel();
		// Start
		start = cbs.getLCreatedTs();
		get_lbl_start().setText(no.ums.pas.plugins.centric.tools.TextFormat.format_datetime(String.valueOf(cbs.getLCreatedTs())));
		// Duration
		if(operator.getLStatus()>=1000) // Finished
			timestamp = cbs.getLLastTs();
		else
			timestamp = db_timestamp;
		get_lbl_duration().setText(String.valueOf(TextFormat.datetime_diff_minutes(cbs.getLCreatedTs(),db_timestamp)) + " " + PAS.l("common_minutes_maybe"));
		
		/*
		ULBAHISTCELL histcell = null;
		if(operator.getHistcell().getULBAHISTCELL().size() > 0)
			histcell = operator.getHistcell().getULBAHISTCELL().get(operator.getHistcell().getULBAHISTCELL().size()-1);
		
		int total_ok = 0;
		int total_unknown = 0;
		int total = 0;
		float percent = 0;
		
		if(histcell != null) {
			total_ok = histcell.getL2Gok() + histcell.getL3Gok() + histcell.getL4Gok();
			total = histcell.getL2Gtotal() + histcell.getL3Gtotal() + histcell.getL4Gtotal();
			total_unknown = total - total_ok;
			cos.total = total;
			cos.total_ok = total_ok;
			cos.unknown = total_unknown;
			percent = histcell.getLSuccesspercentage();
			
			
		}
		cos.percent = percent;
		cos.get_lbl_completed().setText(String.valueOf(total_ok<0?"N/A":total_ok));
		cos.get_lbl_unknown().setText(String.valueOf(total<0?"N/A":total_unknown));
		cos.get_lbl_total().setText(String.valueOf(total<0?"N/A":total));
		cos.get_lbl_percent().setText(String.valueOf(percent));
		// Channel
		cos.get_lbl_channel().setText(String.valueOf(cbs.getLChannel()));
		cos.channel = cbs.getLChannel();
		// Start
		cos.start = cbs.getLCreatedTs();
		cos.get_lbl_start().setText(no.ums.pas.plugins.centric.tools.TextFormat.format_datetime(String.valueOf(cbs.getLCreatedTs())));
		// Duration
		if(operator.getLStatus()>=1000) // Finished
			cos.timestamp = cbs.getLLastTs();
		else
			cos.timestamp = cbp.getLDbTimestamp();
		cos.get_lbl_duration().setText(String.valueOf(TextFormat.datetime_diff_minutes(cbs.getLCreatedTs(),cbp.getLDbTimestamp())) + " " + PAS.l("common_minutes_maybe"));

		 */
	}
	
	public OPERATOR_STATUS getOperatorStatus()
	{
		int status = get_operator().getLStatus();
		if(get_operator().getLStatus() == 1000)
			return OPERATOR_STATUS.FINISHED;
		else if(get_operator().getLStatus() >= 540)
			return OPERATOR_STATUS.ACTIVE;
		else
			return OPERATOR_STATUS.INITIALIZING;
	}
}

