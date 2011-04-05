package no.ums.pas.status;

import no.ums.pas.ums.tools.TextFormat;

import java.util.ArrayList;

/*
 * contains details for one CB part-refno (one country code)
 * will always contain between 1-3 statuslines (l_dst=0 message delivered, l_dst=1 will try later, l_dst=2 cannot be delivered)
 */
class StatusCellBroadcastItem extends Object {
	private String _sz_projectpk;
	//private int _n_refno;
	private int _n_dst;
	private String _sz_ccode;
	private boolean _f_simulation;
	private int _n_recipients;
	private String _sz_oadc;
	private int _n_pri;
	private int _n_expecteditems;
	private int _n_items;
	private int _n_proc;
	private int _n_sendingstatus;
	
	public String get_projectpk() { return _sz_projectpk; }
	//public int get_refno() { return _n_refno; }
	public int get_dst() { return _n_dst; }
	public String get_ccode() { return _sz_ccode; }
	public boolean get_simulation() { return _f_simulation; }
	public int get_count_recipients() { return _n_recipients; }
	public String get_oadc() { return _sz_oadc; }
	public int get_pri() { return _n_pri; }
	public int get_expecteditems() { return _n_expecteditems; }
	public int get_items() { return _n_items; }
	public int get_proc() { return _n_proc; }
	public int get_sendingstatus() { return _n_sendingstatus; }
	public String get_sendingstatustext() { return TextFormat.get_statustext_from_code(get_sendingstatus(), 0, false); }
	
	StatusCellBroadcastItem(String sz_projectpk, int n_dst, String sz_ccode, boolean f_simulation, int n_recipients,
								String sz_oadc, int n_pri, int n_expecteditems, int n_items, int n_proc, int n_sendingstatus) {
		_sz_projectpk = sz_projectpk;
		//_n_refno = n_refno;
		_n_dst = n_dst;
		_sz_ccode = sz_ccode;
		_f_simulation = f_simulation;
		_n_recipients = n_recipients;
		_sz_oadc = sz_oadc;
		_n_pri = n_pri;
		_n_expecteditems = n_expecteditems;
		_n_items = n_items;
		_n_proc = n_proc;
		_n_sendingstatus = n_sendingstatus;
	}
}

/*
 * contains one cell broadcast sending (one parent refno). 
 * Each sending may contain multiple StatusCellBroadcastItem, one for each country code sent to.
 * For every statusupdate (Core.XMLGetStatusItems) a new StatusCellBroadcast object will be generated and replace current
 */
public class StatusCellBroadcast extends Object {
	private int m_n_parent_refno; //sending that initiated the CB sending
	public int get_parent_refno() { return m_n_parent_refno; }
	private ArrayList<Object> m_arr_items = null;
	public int get_size() { return m_arr_items.size(); } //number of statuslines
	public StatusCellBroadcastItem get_item(int n) { return (StatusCellBroadcastItem)m_arr_items.get(n); }
	private void addItem(StatusCellBroadcastItem o) { m_arr_items.add(o); }
	
	/*
		 * l_dst=-2 -- Ready for send
		 * l_dst=-1 -- Awaiting GSM status
		 * l_dst=0  -- Message delivered
		 * l_dst=1	-- Could not be delivered, retrying
		 * l_dst=2	-- Could not be delivered, aborted
	 */
	private int m_n_dst_2 = 0;
	private int m_n_dst_1 = 0;
	private int m_n_dst0  = 0;
	private int m_n_dst1  = 0;
	private int m_n_dst2  = 0;
	private ArrayList<Object> m_arr_cc;
	private boolean m_b_issimulation = false;
	private String m_sz_oadc;
	private int m_n_pri = -1;
	private int m_n_expecteditems = -1;
	private int m_n_items = -1;
	private int m_n_proc = -1;
	private int m_n_sendingstatus = 1;
	
	public int getCountReadyForSend() { return m_n_dst_2; }
	public int getCountAwaitingGSMStatus() { return m_n_dst_1; }
	public int getCountDelivered() { return m_n_dst0; }
	public int getCountFailedRetrying() { return m_n_dst1; }
	public int getCountFailedAborted() { return m_n_dst2; }
	public String getInvolvedCC(int n) { return m_arr_cc.get(n).toString(); }
	public int getInvolvedCCSize() { return m_arr_cc.size(); }
	public boolean getIsSimulation() { return m_b_issimulation; }
	public String getOadc() { return m_sz_oadc; }
	public int getPri() { return m_n_pri; }
	public int getExpectedItems() { return m_n_expecteditems; }
	protected int _getSendingStatus() { return m_n_sendingstatus; }
	public int getItems() { return m_n_items; }
	public int getProcessed() { return m_n_proc; }
	public String getSendingStatus() { return TextFormat.get_statustext_from_code(_getSendingStatus(), 0, false); }

	public StatusCellBroadcast(String [] sz) {
		this(sz[0]);
	}
	public StatusCellBroadcast(String sz_parentrefno) {
		super();
		m_arr_items = new ArrayList<Object>();
		m_arr_cc = new ArrayList<Object>();
		init(sz_parentrefno);
		
	}
	public void init(String sz_parentrefno) {
		m_n_parent_refno = new Integer(sz_parentrefno).intValue();
	}
	public void addStatusItem(String [] sz) {
		addStatusItem(sz[0], new Integer(sz[1]).intValue(), sz[2], 
						new Boolean((sz[3].equals("1") ? true : false)).booleanValue(), new Integer(sz[4]).intValue(), sz[5],
						new Integer(sz[6]).intValue(), new Integer(sz[7]).intValue(), new Integer(sz[8]).intValue(), 
						new Integer(sz[9]).intValue(), new Integer(sz[10]).intValue());
	}
	protected void addStatusItem(String sz_projectpk, int n_dst, String sz_ccode, boolean f_simulation, int n_recipients, String sz_oadc, int n_pri, int n_expecteditems,
									int n_items, int n_proc, int n_sendingstatus) {
		StatusCellBroadcastItem o = new StatusCellBroadcastItem(sz_projectpk, n_dst, sz_ccode, f_simulation, n_recipients, sz_oadc, n_pri, n_expecteditems,
																	n_items, n_proc, n_sendingstatus);
		addItem(o);
	}
	
	public void recalcStatistics() {
		m_n_dst_2 = 0;
		m_n_dst_1 = 0;
		m_n_dst0  = 0;
		m_n_dst1  = 0;
		m_n_dst2  = 0;
		m_b_issimulation = false;
		m_n_expecteditems = 0;
		m_sz_oadc = "";
		m_n_pri = -1;
		m_n_proc = 0;
		m_n_items = 0;
		m_n_sendingstatus = 99;
		m_arr_cc.clear();
		
		for(int i=0; i < m_arr_items.size(); i++) 
		{
			StatusCellBroadcastItem cb = (StatusCellBroadcastItem)m_arr_items.get(i);
			switch(cb.get_dst()) {
				case -2:
					m_n_dst_2 += cb.get_count_recipients();
					break;
				case -1:
					m_n_dst_1 += cb.get_count_recipients();
					break;
				case 0:
					m_n_dst0  += cb.get_count_recipients();
					break;
				case 1:
					m_n_dst1  += cb.get_count_recipients();
					break;
				case 2:
					m_n_dst2  += cb.get_count_recipients();
					break;
			}
			m_b_issimulation = cb.get_simulation();
			if(cb.get_ccode().startsWith("00")) {
				if(!m_arr_cc.contains(cb.get_ccode()))
					m_arr_cc.add(cb.get_ccode());
			}
			if(!m_sz_oadc.contains(cb.get_oadc()))
				if(m_sz_oadc.length()>0)
					m_sz_oadc += ", " + cb.get_oadc();
				else
					m_sz_oadc = cb.get_oadc();
			
			if(m_sz_oadc.length()==0)
				m_sz_oadc = "NA";
			
			m_n_pri = cb.get_pri();
			m_n_expecteditems = cb.get_expecteditems();
			m_n_proc = cb.get_proc();
			m_n_items = cb.get_items();
			if(cb.get_sendingstatus() <= m_n_sendingstatus)
				m_n_sendingstatus = cb.get_sendingstatus();
		}
	}
}
	