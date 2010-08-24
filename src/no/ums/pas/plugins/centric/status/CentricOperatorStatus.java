package no.ums.pas.plugins.centric.status;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;

import javax.swing.JTabbedPane;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.ums.tools.StdTextLabel;

public class CentricOperatorStatus extends DefaultPanel implements ComponentListener {

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
	
	private int m_operator;
	public int get_operator() { return m_operator; }
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
	
	public CentricOperatorStatus(CentricMessageStatus parent, boolean is_total, int operator) {
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

}

