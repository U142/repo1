package no.ums.pas.status;


import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.mainui.StatusPanel;
import no.ums.pas.core.Variables;
import no.ums.pas.core.ws.WSTasResend;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MunicipalStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendProperties;
import no.ums.pas.sound.SoundPlayer;
import no.ums.pas.sound.SoundlibFileWav;
import no.ums.pas.status.LBASEND.LBAHISTCC;
import no.ums.pas.status.LBASEND.LBASEND_TS;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.OpenBrowser;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.pas.LBALanguage;
import no.ums.ws.pas.UMAXALLOC;
import no.ums.ws.pas.status.USMSINSTATS;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StatusSending extends Object {
	/*sprintf(sz_xmltemp, "<SENDING sz_sendingname=\"%s\" l_refno=\"%d\" l_group=\"%d\" l_createdate=\"%d\" l_createtime=\"%d\" "
			"l_scheddate=\"%d\" l_schedtime=\"%d\" l_sendingstatus=\"%d\" l_comppk=\"%d\" l_deptpk=\"%d\" "
			"l_type=\"%d\" l_addresstypes=\"%d\" l_profilepk=\"%d\" l_queuestatus=\"%d\" l_totitem=\"%d\" "
			"l_proc=\"%d\" l_altjmp=\"%d\" l_alloc=\"%d\" l_maxalloc=\"%d\">",
			info->_sz_sendingname, info->_n_refno, info->_n_group, info->_n_createdate, info->_n_createtime,
			info->_n_scheddate, info->_n_schedtime, info->_n_sendingstatus, info->_n_companypk,
			info->_n_deptpk, info->_n_type, info->_n_addresstypes, info->_n_profilepk, info->_n_queuestatus,
			info->_n_totitem, info->_n_processed, info->_n_altjmp, info->_n_alloc, info->_n_maxalloc);*/
	

	
	
	protected StatusSending m_this;
	protected VoicePanel VOICEPANEL;
	public StatusSending get_statussending() { return m_this; }
	private StatusSendingList m_sendinglist = null;
	//private StatusCellBroadcast m_cellbroadcast = null; //singleton. Every sending may have a child cell broadcast. This will be refreshed on every statusupdate.
	//public StatusCellBroadcast getStatusCellBroadcast() { return m_cellbroadcast; }
	private LBASEND m_lba = null; 
	private ArrayList<LBASEND> m_lba_by_operator = new ArrayList<LBASEND>();
	public void ResetLbaByOperator() {
		m_lba_by_operator.clear();
	}
	public void addLbaOperator(LBASEND lba)
	{
		m_lba_by_operator.add(lba);
	}
	
	
	public LBASEND getLBA() { return m_lba; }
	public boolean hasLBA() { return (m_lba==null ? false : true); }
	
	
	private StatusSendingUI m_uipanel;
	public StatusSendingUI get_uipanel() { return m_uipanel; }
	private JProgressBar m_lba_progress= new JProgressBar();
	private JProgressBar m_voice_progress = new JProgressBar();
	private LBATabbedPane m_lba_tabbed = new LBATabbedPane();
	private JTabbedPane m_pa_tabbed = new JTabbedPane();
	private StdTextLabel m_sendingname_label = new StdTextLabel("", 11, false);
	public StdTextLabel getTotalSendingnameLabel() { return m_sendingname_label; }
	protected int n_sending_completion_percent = 0;
	
	public JProgressBar getVoiceProgressbar() { return m_voice_progress; }
	
	/*labels for updating UI*/
	protected JLabel m_lbl_sendingname = new JLabel("");
	protected JLabel m_lbl_processed_and_total = new JLabel("");
	protected JLabel m_lbl_completion_percent = new JLabel("");
	protected int m_filter_status_by_operator = -1;
	
	protected void updateLabels()
	{
		getSendingnameLabel();
		getProcessedAndTotalLabel();
		getCompletionPercentLabel();
	}
	public JLabel getSendingnameLabel() {
		m_lbl_sendingname.setText(_sz_sendingname);
		return m_lbl_sendingname;
	}
	public JLabel getProcessedAndTotalLabel() {
		m_lbl_processed_and_total.setText(get_proc() + " / " + get_totitem());
		return m_lbl_processed_and_total;
	}
	public JLabel getCompletionPercentLabel() {
		m_lbl_completion_percent.setText(n_sending_completion_percent + "%");
		return m_lbl_completion_percent;
	}
	
	public String toString()
	{
		String ret = getTotalSendingnameLabel().getText();
		return ret;
	}
	public String getSendingname() { 
		return _sz_sendingname;
	}
	public int getSendingPercent()
	{
		return n_sending_completion_percent;
	}
	
	public void setSendingnameLabel(String sz)
	{
		String sz_newtext = "<html><font style=\"font-size:9px;\">" + _sz_sendingname + "&nbsp;&nbsp;&nbsp;<b>" + sz + "</font></b></html>"; //font-face:Arial; 
		String sz_text = m_sendingname_label.getText();
		if(sz_text.compareTo(sz_newtext)!=0)
		{
			m_sendingname_label.setText(sz_newtext);
			m_sendingname_label.revalidate();
		}
	}
	public void setSendingnameLabel()
	{
		//create % statistics for sending
		boolean b_use_voice = true;
		boolean b_use_lba = false;
		if(get_type()==4)
			b_use_voice = false;
		if(get_type()==5)
			b_use_voice = false;
		
		float n_lba_percent = 100;
		float n_voice_percent = 100;
		if(m_lba!=null && 
				(((get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT)) ||
				(((get_addresstypes() & SendController.SENDTO_TAS_SMS) == SendController.SENDTO_TAS_SMS)))
		{
			if(get_lba_items()>0)
				n_lba_percent = get_lba_processed()*100.0f / get_lba_items();
			else
			{
				if(m_lba.HasFinalStatus())
					n_lba_percent = 100;
				else
					n_lba_percent = 0;
			}
			b_use_lba = true;
		}
		
		if(get_totitem()>0) //sending has started
			n_voice_percent = get_proc() * 100.0f / get_totitem();
		else
		{
			if(get_sendingstatus()==7) //no recipients and finished, this is a sending without voice
			{
				n_voice_percent = 100;
				if(b_use_lba) //dont count voice if voice has 0 recipients and LBA is active
					b_use_voice = false;
				//b_use_voice = false;
			}
			else
				n_voice_percent = 0;
		}
		if(get_lba_sendingstatus()==LBASEND.LBASTATUS_CANCELLED || get_lba_sendingstatus()==LBASEND.LBASTATUS_FINISHED)
			n_lba_percent = 100;
		if(get_sendingstatus()==7)
			n_voice_percent = 100;
		
		float n_percent = 0;
		if(b_use_lba && b_use_voice)
			n_percent = (n_lba_percent + n_voice_percent) / 2;
		else if(b_use_lba)
			n_percent = n_lba_percent;
		else if(b_use_voice)
			n_percent = n_voice_percent;
		n_sending_completion_percent = (int)Math.round(n_percent);
		setSendingnameLabel((int)Math.round(n_percent) + "%");
	}
	
	public void SetStatusNeedsAttention(boolean b)
	{
		if(b)
			getTotalSendingnameLabel().setIcon(ImageLoader.load_icon("alert.gif"));
		else
			getTotalSendingnameLabel().setIcon(null);
	}
	
	public void set_sendinglist(StatusSendingList parent) {
		m_sendinglist = parent;
	}
	public StatusSendingList get_sendinglist() { return m_sendinglist; }
	
	public void init_ui() {
		try
		{
			/*SwingUtilities.invokeAndWait(new Runnable() {
				public void run()
				{
				*/	
					//m_lba_progress = new JProgressBar();
					//m_voice_progress = new JProgressBar();
					//m_lba_tabbed = new LBATabbedPane();				
					m_uipanel = new StatusSendingUI();
					get_uipanel().init_ui();
				/*}
			});*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/*
	 * sum all operators
	 */
	public void CalcLbaTotalsFromOperators()
	{
		LBASEND lba = new LBASEND();
		for(int i=0; i < m_lba_by_operator.size(); i++)
		{
			if(m_filter_status_by_operator != m_lba_by_operator.get(i).l_operator && m_filter_status_by_operator > -1)
				continue;
			lba.n_cancelled += m_lba_by_operator.get(i).n_cancelled;
			lba.n_delivered += m_lba_by_operator.get(i).n_delivered;
			lba.n_failed += m_lba_by_operator.get(i).n_failed;
			lba.n_items += m_lba_by_operator.get(i).n_items==-1?0:m_lba_by_operator.get(i).n_items;
			lba.n_proc += m_lba_by_operator.get(i).n_proc==-1?0:m_lba_by_operator.get(i).n_proc;
			lba.n_queued += m_lba_by_operator.get(i).n_queued;
			
			lba.n_cbtype = m_lba_by_operator.get(i).n_cbtype;
			lba.f_simulation = m_lba_by_operator.get(i).f_simulation;
			lba.l_operator = m_lba_by_operator.get(i).l_operator;
			lba.n_parentrefno = m_lba_by_operator.get(i).n_parentrefno;
			lba.n_requesttype = m_lba_by_operator.get(i).n_requesttype;
			lba.n_response = m_lba_by_operator.get(i).n_response;
			lba.n_retries = m_lba_by_operator.get(i).n_retries;
			lba.n_status = m_lba_by_operator.get(i).n_status;
			lba.sz_areaid = m_lba_by_operator.get(i).sz_areaid;
			lba.sz_jobid = m_lba_by_operator.get(i).sz_jobid;

			lba.send_ts = m_lba_by_operator.get(i).send_ts;
			lba.hist_cc = m_lba_by_operator.get(i).hist_cc;
			lba.hist_cell = m_lba_by_operator.get(i).hist_cell;

		}
		lba.hist_cc = CalcTotalsByCC();
		lba.send_ts = MergeLbaTimestamps();
		//CalcTotalsByCells();
		if(get_type()==4 || get_type()==5)
		{
			setLBA(lba);
			//pnl_icon.showLbaLanguages(true);
			//pnl_icon.showOverlayButtons(true);
		}
		else
		{
			//pnl_icon.showLbaLanguages(false);
			//pnl_icon.showOverlayButtons(false);
		}
		//MergeLbaTimestamps();
	}
	
	protected ArrayList<LBASEND_TS> MergeLbaTimestamps()
	{
		ArrayList<LBASEND_TS> tslist = new ArrayList<LBASEND_TS>(); 
		for(int i=0; i < m_lba_by_operator.size(); i++)
		{
			if(m_filter_status_by_operator != m_lba_by_operator.get(i).l_operator && m_filter_status_by_operator > -1)
				continue;			
			for(int ts=0; ts < m_lba_by_operator.get(i).send_ts.size(); ts++)
			{
				LBASEND_TS temp = m_lba_by_operator.get(i).send_ts.get(ts);
				temp.sz_operator = m_lba_by_operator.get(i).sz_operator;
				tslist.add(temp);
			}
		}
		//m_lba.send_ts = tslist;
		Collections.sort(tslist);
		return tslist;
	}
	
	/*make one total list of CC*/
	protected ArrayList<LBAHISTCC> CalcTotalsByCC()
	{
		ArrayList<LBAHISTCC> cclist = new ArrayList<LBAHISTCC>(); //make a new list
		Hashtable<Integer, LBAHISTCC> added = new Hashtable<Integer, LBAHISTCC>();
		
		for(int i=0; i < m_lba_by_operator.size(); i++)
		{
			if(m_filter_status_by_operator != m_lba_by_operator.get(i).l_operator && m_filter_status_by_operator > -1)
				continue;
			for(int cc=0; cc < m_lba_by_operator.get(i).hist_cc.size(); cc++)
			{
				LBAHISTCC cctemp = m_lba_by_operator.get(i).hist_cc.get(cc); //to be added to cclist
				Integer ccode = new Integer(cctemp.l_ccode);
				if(added.containsKey(ccode)) //already added ccode, accumulate
				{
					/*LBAHISTCC ccfound = added.get(ccode);*/
					LBAHISTCC ccfound = added.get(ccode);
					ccfound.l_delivered += cctemp.l_delivered;
					ccfound.l_expired += cctemp.l_expired;
					ccfound.l_failed += cctemp.l_failed;
					ccfound.l_queued += cctemp.l_queued;
					ccfound.l_submitted += cctemp.l_submitted;
					ccfound.l_subscribers += cctemp.l_subscribers;
					ccfound.l_unknown += cctemp.l_unknown;
					
				}
				else { //new ccode, make new
					//added.put(new Integer(cctemp.l_ccode), cctemp);
					try
					{
						LBAHISTCC newcc = (LBAHISTCC)cctemp.clone();
						cclist.add(newcc);
						added.put(new Integer(cctemp.l_ccode), newcc);
					}
					catch(Exception e)
					{ 
						
					}
				}
				
			}
		}
		/*Enumeration<LBAHISTCC> en = added.elements();
		while(en.hasMoreElements())
		{
			cclist.add(en.nextElement());
		}*/
		return cclist;
	}
	
	public synchronized void setLBA(LBASEND lba)
	{
		m_lba = lba;
		try 
		{
			m_lba.CalcStatistics();
			//m_lba.CalcStatistics();
			if(m_lba.n_items<0)
			{
				m_lba_progress.setIndeterminate(true);
				m_lba_progress.setStringPainted(false);
			}
			else if(m_lba.n_items==0)
			{
				m_lba_progress.setMaximum(1);
				m_lba_progress.setValue(1);
				m_lba_progress.setIndeterminate(false);
				m_lba_progress.setStringPainted(false);				
			}
			else
			{
				m_lba_progress.setIndeterminate(false);
				m_lba_progress.setMinimum(0);
				m_lba_progress.setMaximum(m_lba.n_items);
				m_lba_progress.setValue(m_lba.n_proc + m_lba.getCancelled());
				m_lba_progress.setStringPainted(true);
			}
			//m_lba_progress.setString(m_lba_progress.getString());
			set_lba_sendingstatus(m_lba.n_status);
			set_lba_delivered(m_lba.getDelivered());
			set_lba_queued(m_lba.getQueued());
			set_lba_failed(m_lba.getFailed());
			set_lba_expired(m_lba.getExpired());
			set_lba_processed(m_lba.n_proc);
			set_lba_items(m_lba.n_items);
			set_lba_cancelled(m_lba.n_cancelled);
			//update rows in list
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					m_lba_tabbed.UpdateData(m_lba);
					m_lba_tabbed.UpdateDataByOperator(m_lba_by_operator);
				}
			});
			try
			{
				set_lba_sendingstatusS(LBASEND.LBASTATUSTEXT(get_lba_sendingstatus()));
			}
			catch(Exception e)
			{
				set_lba_sendingstatusS(PAS.l("common_unknown_status") + " [" + get_lba_sendingstatus() + "]");
			}
			//Get time from start to stop
			String sz_time_used = " ";
			if(m_lba.send_ts != null && m_lba.send_ts.size()>0)
			{
				LBASEND.LBASEND_TS starttime = m_lba.send_ts.get(0);
				LBASEND.LBASEND_TS endtime = m_lba.send_ts.get(m_lba.send_ts.size()-1);
				if(endtime.n_status==LBASEND.LBASTATUS_CANCELLED || endtime.n_status==LBASEND.LBASTATUS_FINISHED)
				{
					try
					{
						String ts_end = endtime.n_timestamp;
						String ts_start = starttime.n_timestamp;
						java.util.Calendar end = java.util.Calendar.getInstance(); //((int)(Long.parseLong(endtime.n_timestamp) - Long.parseLong(starttime.n_timestamp)) * 1000); //0, 0, (int)(Long.parseLong(endtime.n_timestamp) - Long.parseLong(starttime.n_timestamp)));
						java.util.Calendar start = java.util.Calendar.getInstance();
						end.set(Integer.parseInt((ts_end.substring(0, 4))), Integer.parseInt(ts_end.substring(4, 6)), Integer.parseInt(ts_end.substring(6, 8)), Integer.parseInt(ts_end.substring(8, 10)), Integer.parseInt(ts_end.substring(10, 12)), Integer.parseInt(ts_end.substring(12, 14)));
						//end.add(Calendar.DATE, 5);
						start.set(Integer.parseInt((ts_start.substring(0, 4))), Integer.parseInt(ts_start.substring(4, 6)), Integer.parseInt(ts_start.substring(6, 8)), Integer.parseInt(ts_start.substring(8, 10)), Integer.parseInt(ts_start.substring(10, 12)), Integer.parseInt(ts_start.substring(12, 14)));
						long diff = end.getTimeInMillis() - start.getTimeInMillis();
						SimpleDateFormat dateFormat;
						if(diff >= 1000*60*60*24) //one day or more
							dateFormat = new SimpleDateFormat("dd'" + PAS.l("common_days_short") + " 'HH'" + PAS.l("common_hours_short") + " 'mm'" + PAS.l("common_minutes_short") + " 'ss'" + PAS.l("common_seconds_short") + "'");
						if(diff >= 1000*60*60)//one hour or more
							dateFormat = new SimpleDateFormat("HH'" + PAS.l("common_hours_short") + " 'mm'" + PAS.l("common_minutes_short") + " 'ss'" + PAS.l("common_seconds_short") + "'");
						else
							dateFormat = new SimpleDateFormat("mm'" + PAS.l("common_minutes_short") + " 'ss'" + PAS.l("common_seconds_short") + "'");
						
						sz_time_used = "    [" + PAS.l("common_completed") + " " + dateFormat.format(new java.util.Date(diff)) + "] "; 
					}
					catch(Exception timeerr)
					{
					}
				}
			}
			String sz_type = "";
			switch(get_group())
			{
			case 5:
				
				sz_type = PAS.l("main_status_traveller_alert");
				break;
			default:
				sz_type = PAS.l("main_status_locationbased_alert");
				break;
			}
			if(m_lba.f_simulation==1)
				
				pnl_cell.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + sz_type + "    [" + PAS.l("main_sending_simulated") + "]" + sz_time_used)); //JobID=" + m_lba.sz_jobid));//BorderFactory.createTitledBorder("Location Based Alert (Simulated) -- JobID " + m_lba.sz_jobid));
			else
				pnl_cell.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + sz_type + "    [" + PAS.l("main_sending_live") + "]" + sz_time_used));// JobID=" + m_lba.sz_jobid));//BorderFactory.createTitledBorder("Location Based Alert (Live sending) -- JobID " + m_lba.sz_jobid));
		
		}
		catch(Exception e)
		{
		}
		try
		{
			pnl_cell.init();
		}
		catch(Exception e)
		{
			
		}
		
	}
	

	
	public void destroy_ui() {
	}
	private ShapeStruct m_shape;
	public ShapeStruct get_shape() { return m_shape; }
	public PolygonStruct get_polygon() { return (PolygonStruct)m_shape; }
	public EllipseStruct get_ellipse() { return (EllipseStruct)m_shape; }
	public MunicipalStruct get_municipal() { return (MunicipalStruct)m_shape; }
	public void set_shape(ShapeStruct s) { 
		/*if(s.shapeID != 0)
			System.out.println("Break");
		
		if(s.shapeID == ShapeStruct.SHAPE_POLYGON)
		{
			double lon=0,lat=0;
			for(int i=0;i<((PolygonStruct)s).get_coors_lat().size();++i) {
				if(lon == ((PolygonStruct)s).get_coors_lon().get(i) && lat == ((PolygonStruct)s).get_coors_lat().get(i));
					System.out.println("Break");
				lon = ((PolygonStruct)s).get_coors_lon().get(i);
				lat = ((PolygonStruct)s).get_coors_lat().get(i);
			}
		}*/
		m_shape = s; 
		
	}
	
	private String _sz_sendingname;
	private int _n_refno;
	private int _n_group;
	private int _n_createdate;
	private int _n_createtime;
	private int _n_scheddate;
	private int _n_schedtime;
	private int _n_sendingstatus;
	private int _n_comppk;
	private int _n_deptpk;
	private int _n_type;
	private int _n_addresstypes;
	private int _n_profilepk;
	private int _n_queuestatus;
	private int _n_totitem;
	private int _n_proc;
	private int _n_altjmp;
	private int _n_alloc;
	private int _n_maxalloc;
	private String m_sz_projectpk;
	private String _sz_oadc;
	private int _n_qreftype; //type=60 is simulation
	private int _f_dynacall; //1 is live sending, 2 is simulation
	private int _n_nofax; //0 is no, 1 is yes (company nofax)
	private int _n_linktype; //0 is normal voice, 9 is test sending to single number
	private int _n_resendrefno; //if >0 then this is a resent sending
	
	private int _n_lba_sendingstatus;
	private String _sz_lba_sendingstatus;
	private int _n_lba_delivered;
	private int _n_lba_queued;
	private int _n_lba_failed;
	private int _n_lba_expired;
	private int _n_lba_processed;
	private int _n_lba_items;
	private int _n_lba_cancelled;
	private String _sz_sms_messagetext;
	private String _sz_actionprofilename;
	private int _n_num_dynfiles;
	
	private List<USMSINSTATS> _m_smsin_stats;
	public void setSmsInStats(List<USMSINSTATS> ref)
	{
		_m_smsin_stats = ref;
	}
	public List<USMSINSTATS> getSmsInStats() {
		return _m_smsin_stats;
	}
	public void addSmsInStats(USMSINSTATS ref)
	{
		if(_m_smsin_stats!=null)
			_m_smsin_stats.add(ref);
	}
	
	protected List<LBALanguage> _lba_languages;
	public List<LBALanguage> getLbaLanguages()
	{
		return _lba_languages;
	}
	protected void setLbaLanguages(List<LBALanguage> l)
	{
		_lba_languages = l;
		pnl_icon.setLbaLanguages();
	}
	
	protected IconPanel pnl_icon;
	private VoicePanel pnl_voice;
	private PAPanel pnl_pa;
	//protected ResendPanel pnl_resend;
	private CellPanel pnl_cell;
	private LbaSmsReplyPanel pnl_lbasmsreply;
	
	public String get_sendingname() { return _sz_sendingname; }
	public int get_refno() { return _n_refno; }
	public int get_group() { return _n_group; }
	public int get_createdate() { return _n_createdate; }
	public int get_createtime() { return _n_createtime; }
	public int get_scheddate() { return _n_scheddate; }
	public int get_schedtime() { return _n_schedtime; }
	public int get_sendingstatus() { return _n_sendingstatus; }
	public int get_comppk() { return _n_comppk; }
	public int get_deptpk() { return _n_deptpk; }
	public int get_type() { return _n_type; }
	public int get_addresstypes() { return _n_addresstypes; }
	public int get_profilepk() { return _n_profilepk; }
	public int get_queuestatus() { return _n_queuestatus; }
	public int get_totitem() { return _n_totitem; }
	public int get_proc() { return _n_proc; }
	public int get_altjmp() { return _n_altjmp; }
	public int get_alloc() { return _n_alloc; }
	public int get_maxalloc() { return _n_maxalloc; }
	public String get_projectpk() { return m_sz_projectpk; }
	public String get_oadc() { return _sz_oadc; }
	public int get_qreftype() { return _n_qreftype; }
	public int get_dynacall() { return _f_dynacall; }
	public int get_nofax() { return _n_nofax; }
	public int get_linktype()  { return _n_linktype; }
	public int get_resendrefno() { return _n_resendrefno; }
	public String get_actionprofilename() { return _sz_actionprofilename; }
	public int get_num_dynfiles() { return _n_num_dynfiles; }
	
	public boolean isSimulation() { return (get_dynacall()==2 ? true : false); }
	
	public void set_lba_sendingstatus(int n) { _n_lba_sendingstatus = n; }
	public int get_lba_sendingstatus() { return _n_lba_sendingstatus; }
	public void set_lba_sendingstatusS(String s) { _sz_lba_sendingstatus = s; }
	public String get_lba_sendingstatusS() { return _sz_lba_sendingstatus; }
	public void set_lba_delivered(int n) { _n_lba_delivered = n; }
	public int get_lba_delivered() { return _n_lba_delivered; }
	public void set_lba_queued(int n) { _n_lba_queued = n; }
	public int get_lba_queued() { return _n_lba_queued; }
	public void set_lba_failed(int n) { _n_lba_failed = n; }
	public int get_lba_failed() { return _n_lba_failed; }
	public void set_lba_processed(int n) { _n_lba_processed = n; }
	public int get_lba_expired() { return _n_lba_expired; }
	public void set_lba_expired(int n) { _n_lba_expired = n; }
	public int get_lba_processed() { return _n_lba_processed; }
	public void set_lba_items(int n) { _n_lba_items = n; }
	public int get_lba_items() { return _n_lba_items; }
	public void set_lba_cancelled(int n) { _n_lba_cancelled = n; }
	public int get_lba_cancelled() { return _n_lba_cancelled; }
	public String get_sms_message_text() { return _sz_sms_messagetext; }
	public void set_sms_message_text(String s) { _sz_sms_messagetext = s; }
	
	
	public void setProjectpk(String sz_projectpk) { m_sz_projectpk = sz_projectpk; }
	public int get_sendingtype() {
		int n = -1;
		switch(get_group()) {
			case 3:
				n = SendProperties.SENDING_TYPE_POLYGON_;
				break;
			case 4:
				n = SendProperties.SENDING_TYPE_GEMINI_STREETCODE_;
				break;
			case 5:
				n = SendProperties.SENDING_TYPE_TAS_COUNTRY_;
				break;
			case 8:
				n = SendProperties.SENDING_TYPE_CIRCLE_;
				break;		
			case 9:
				n = SendProperties.SENDING_TYPE_MUNICIPAL_;
				break;
		}
		return n;
	}
	
	public StatusSending(String [] sz) {
		this(sz[0], sz[1], sz[2], sz[3], sz[4], sz[5], sz[6], sz[7], sz[8], sz[9], sz[10], sz[11], sz[12], sz[13],
			sz[14], sz[15], sz[16], sz[17], sz[18], sz[19], sz[20], sz[21], sz[22], sz[23], sz[24], sz[25], sz[26], sz[27]);
	}
	public StatusSending(String sz_sendingname, String sz_refno, String sz_group, String sz_createdate, String sz_createtime,
						String sz_scheddate, String sz_schedtime, String sz_sendingstatus, String sz_comppk, String sz_deptpk,
						String sz_type, String sz_addresstypes, String sz_profilepk, String sz_queuestatus, String sz_totitem,
						String sz_proc, String sz_altjmp, String sz_alloc, String sz_maxalloc, String sz_oadc, String sz_qreftype,
						String sz_dynacall, String sz_nofax, String sz_linktype, String sz_resendrefno, String sz_messagetext, String sz_actionprofilename,
						String sz_num_dynfiles) {
		pnl_icon = new IconPanel();

		_sz_sendingname = sz_sendingname;
		_n_refno		= new Integer(sz_refno).intValue();
		_n_group		= new Integer(sz_group).intValue();
		_n_createdate	= new Integer(sz_createdate).intValue();
		_n_createtime	= new Integer(sz_createtime).intValue();
		_n_scheddate	= new Integer(sz_scheddate).intValue();
		_n_schedtime	= new Integer(sz_schedtime).intValue();
		_n_sendingstatus= new Integer(sz_sendingstatus).intValue();
		_n_comppk		= new Integer(sz_comppk).intValue();
		_n_deptpk		= new Integer(sz_deptpk).intValue();
		_n_type			= new Integer(sz_type).intValue();
		_n_addresstypes = new Integer(sz_addresstypes).intValue();
		_n_profilepk	= new Integer(sz_profilepk).intValue();
		_n_queuestatus	= new Integer(sz_queuestatus).intValue();
		_n_totitem		= new Integer(sz_totitem).intValue();
		if(_n_totitem<0)
			_n_totitem = 0;
		_n_proc			= new Integer(sz_proc).intValue();
		_n_altjmp		= new Integer(sz_altjmp).intValue();
		_n_alloc		= new Integer(sz_alloc).intValue();
		_n_maxalloc		= new Integer(sz_maxalloc).intValue();
		_sz_oadc		= sz_oadc;
		_n_qreftype		= new Integer(sz_qreftype).intValue();
		_f_dynacall		= new Integer(sz_dynacall).intValue();
		_n_nofax		= new Integer(sz_nofax).intValue();
		_n_linktype		= new Integer(sz_linktype).intValue();
		_n_resendrefno  = new Integer(sz_resendrefno).intValue();
		_sz_sms_messagetext = sz_messagetext;
		_sz_actionprofilename = sz_actionprofilename;
		_n_num_dynfiles = new Integer(sz_num_dynfiles).intValue();
		_m_smsin_stats = new ArrayList<USMSINSTATS>();
		m_this			= this;
		m_sendingname_label.setText(_sz_sendingname);
		
		// Disable goto button on resend, there is no navigation
		//if(_n_resendrefno>0 && _n_type == 2 && PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4)
		if(_n_resendrefno>0)
			pnl_icon.m_btn_goto.setVisible(false);
		
		m_lba_tabbed.setCallback(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if("act_lba_filter_by_operator".equals(e.getActionCommand()))
				{
					Integer operator = (Integer)e.getSource();
					
					pnl_icon.enableOverlayButtons((operator>0 ? true : false));
					/*
					boolean show_resend = false;
					for(int i=0;i<m_lba_by_operator.size();++i) {
						if(m_lba_by_operator.get(i).l_operator == operator.intValue())
							if(m_lba_by_operator.get(i).n_status > 42000)  
								show_resend = true;
					}
					if(show_resend && pnl_cell.m_btn_tas_resend != null)
						pnl_cell.m_btn_tas_resend.setVisible((operator>0&&show_resend?true:false));
					*/
					try
					{
						if(m_filter_status_by_operator!=operator.intValue())
						{
							PAS.get_pas().get_mappane().resetAllOverlays();
						}
					}
					catch(Exception err)
					{
						
					}
					m_filter_status_by_operator = operator.intValue();
					System.out.println("Filter by operator " + m_filter_status_by_operator);
					CalcLbaTotalsFromOperators();
					update_ui();
					//MergeLbaTimestamps();
					//CalcTotalsByCC();
				}
			}
		});
		
		updateLabels();
	}
	public void set_values(StatusSending s) {
		_sz_sendingname = s.get_sendingname();
		_n_refno		= s.get_refno();
		_n_createdate	= s.get_createdate();
		_n_createtime	= s.get_createtime();
		_n_scheddate	= s.get_scheddate();
		_n_schedtime	= s.get_schedtime();
		_n_sendingstatus= s.get_sendingstatus();
		_n_comppk		= s.get_comppk();
		_n_deptpk		= s.get_deptpk();
		_n_type			= s.get_type();
		_n_addresstypes = s.get_addresstypes();
		_n_profilepk	= s.get_profilepk();
		_n_queuestatus	= s.get_queuestatus();
		_n_totitem		= s.get_totitem();
		_n_proc			= s.get_proc();
		_n_altjmp		= s.get_altjmp();
		_n_nofax		= s.get_nofax();
		_n_linktype		= s.get_linktype();
		_n_resendrefno	= s.get_resendrefno();
		_m_smsin_stats	= s._m_smsin_stats;
		//m_lba_tabbed = s.m_lba_tabbed;
		//m_lba_tabbed.last_selected_component = s.m_lba_tabbed.last_selected_component;
		try
		{
			_n_alloc		= s.get_alloc();
			_n_maxalloc		= s.get_maxalloc();
			if(!pnl_voice.m_b_allocset) {
				pnl_voice.set_maxalloc(_n_maxalloc);
				pnl_voice.m_progress.setValue(_n_maxalloc);
			}
		}
		catch(Exception e)
		{
			
		}
		_sz_oadc		= s.get_oadc();
		_n_qreftype		= s.get_qreftype();
		_f_dynacall		= s.get_dynacall();
		_sz_sms_messagetext = s.get_sms_message_text();
		_sz_actionprofilename = s.get_actionprofilename();
		updateLabels();
		//m_sendingname_label.setText(_sz_sendingname);
		if(get_type()!=4) //if LBA, wait update_ui until all statistics are parsed from WS
			update_ui();
	}
	
	public void update_ui() {
		if(get_uipanel()!=null)
			get_uipanel().update_ui();
		
	}
	
	
	public class StatusSendingUI extends DefaultPanel implements ComponentListener {	
		public static final long serialVersionUID = 1;

		public StatusSendingUI() {
			super();
			pnl_voice = new VoicePanel();
			pnl_voice.init_ui();
			pnl_cell = new CellPanel();
			//pnl_resend = new ResendPanel();
			//pnl_resend.setVisible(false);
			if(get_type()==5)
			{
				pnl_lbasmsreply = new LbaSmsReplyPanel(m_this);
			}
			else if(get_type()==2 && get_group()==5)
			{
				//pnl_resend.setVisible(true);
			}
			pnl_pa = new PAPanel();
			addComponentListener(this);
			setPreferredSize(new Dimension(300, 500));
			add_controls();
			enableOverlayButtons(false);
		}
		
		public StatusSending get_status_sending() {
			return get_statussending();
		}
		
		public void init_ui() {
			//initialize static values
			update_ui();
		}
		public void update_ui() {
			//update dynamic values
			try
			{
				pnl_voice.update_ui();
			}
			catch(Exception e)
			{
				
			}
			try
			{
				pnl_cell.updateUi();
			}
			catch(Exception e)
			{
				
			}
			try
			{
				pnl_pa.updateUI();
			}
			catch(Exception e){}
			
			/*
			try
			{
				pnl_resend.updateUi();
			}
			catch(Exception e)
			{
				
			}*/
			
		}
		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
			
			//set_gridconst(0, 0, 1, 1);
			//add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), 1, 1);
			add(pnl_icon, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			// Add filter panel
			set_gridconst(0, inc_panels(), 1, 1);
			add(pnl_voice, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			//set_gridconst(0,inc_panels(), 1, 1);
			//add(pnl_cell, m_gridconst);
			set_gridconst(0,inc_panels(), 1, 1);
			
			if(get_type()==5) {
				add_spacing(DIR_VERTICAL, 10);
				set_gridconst(0, inc_panels(), 1, 1);
				add(pnl_lbasmsreply,m_gridconst);
				pnl_lbasmsreply.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + PAS.l("main_tas_panel_responses") + " "));
			}
			else if(get_type()==6) {
				add(pnl_pa, m_gridconst);
			}
			/*
			else if(get_type()==2 && get_group()==5)
			{
				pnl_resend.setBorder(UMS.Tools.TextFormat.CreateStdBorder(" " + PAS.l("Responses")));
				set_gridconst(0, inc_panels(), 1, 1);
				add(pnl_resend, m_gridconst);				
			}*/
						
			init();
		}
		public void init() {
			setVisible(true);
		}
		public void componentHidden(ComponentEvent e) { }
		public void componentMoved(ComponentEvent e) { }
		public void componentResized(ComponentEvent e) { 
			int voicelba_visible = 0;
			if(pnl_voice.isVisible())
				voicelba_visible++;
			if(pnl_cell.isVisible())
				voicelba_visible++;
			//if(pnl_pa.isVisible())
			//	voicelba_visible++;
			this.setPreferredSize(new Dimension(getWidth(), getHeight()));
			int iconPanelSize = 60;//40+40 +20;
			pnl_icon.setPreferredSize(new Dimension(getWidth()-10, iconPanelSize));
			if(pnl_voice.isVisible())
				pnl_voice.setPreferredSize(new Dimension(getWidth()-10, (getHeight()/voicelba_visible)-iconPanelSize-50)); //-iconPanelSize/2));
			else
				pnl_voice.setPreferredSize(new Dimension(getWidth()-10, 1));
			if(get_type()==5) {
				//pnl_cell.setPreferredSize(new Dimension(getWidth()-10, ((getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize/2)/2));
				pnl_cell.setPreferredSize(new Dimension(getWidth()-10, (getHeight()/2)+40));
				pnl_lbasmsreply.setPreferredSize(new Dimension(getWidth()-10, ((getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize/2)/3));
			}
			else if(get_type()==6) { // Centric
				pnl_pa.setPreferredSize(new Dimension(getWidth()-10, (getHeight()-iconPanelSize)));
			}
			else
				pnl_cell.setPreferredSize(new Dimension(getWidth()-10, (getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize/3));
			revalidate();
		}
		public void componentShown(ComponentEvent e) { }

		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	public void enableOverlayButtons(boolean b_enable)
	{
		pnl_icon.enableOverlayButtons(b_enable);
	}
	public void showOverlayButtons(boolean b_show)
	{
		pnl_icon.showOverlayButtons(b_show);
	}
	
	public class IconPanel extends DefaultPanel {

		public static final long serialVersionUID = 1;
		JButton m_btn_goto = null;
		JButton m_btn_confirm_lba_sending = null;
		JButton m_btn_cancel_lba_sending = null;
		private JCheckBox m_chk_layers_gsm = new JCheckBox("GSM900 " + PAS.l("main_status_gsmcoverage"), false);
		public JCheckBox get_chk_layers_gsm() { return m_chk_layers_gsm; }
		private JCheckBox m_chk_layers_umts = new JCheckBox("UMTS " + PAS.l("main_status_gsmcoverage"), false);
		public JCheckBox get_chk_layers_umts() { return m_chk_layers_umts; }
		//private StdTextLabel m_lbl_lbalanguages = new StdTextLabel("Languages", 150, 11, false);
		private JButton m_btn_lbalanguages = new JButton(ImageLoader.load_icon("message_32.png"));
		private JPopupMenu m_pop_lbalanguages = new JPopupMenu();
		
		public void showLbaLanguages(boolean b_show)
		{
			m_btn_lbalanguages.setVisible(b_show);
		}
		
		private class LBALanguageMenuItem extends JMenuItem
		{
			private LBALanguage lang;
			public LBALanguage getLanguage() { return lang; } 
			public LBALanguageMenuItem(LBALanguage lang)
			{
				super(lang.getSzName() + " (Click to copy message text to clipboard)");
				this.lang = lang;
			}
		}
		Hashtable<Long, LBALanguageMenuItem> hash_menuitems = new Hashtable<Long, LBALanguageMenuItem>();
		public void setLbaLanguages()
		{ 
			try
			{
				if(_lba_languages!=null)
				{
					for(int i=0; i < _lba_languages.size(); i++)
					{
						LBALanguageMenuItem item = new LBALanguageMenuItem(_lba_languages.get(i));
						if(!hash_menuitems.containsKey(_lba_languages.get(i).getLTextpk()))
						{
							item.addActionListener(this);
							item.setActionCommand("act_lbalanguage_selected");
							//item.setToolTipText(_lba_languages.get(i).getSzText());
							item.setToolTipText(StatusPanel.createMessageContentTooltipHtml(_lba_languages.get(i), get_group()));
							m_pop_lbalanguages.add(item);
							hash_menuitems.put(_lba_languages.get(i).getLTextpk(), item);
						}
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}

		public void showOverlayButtons(boolean b_show)
		{
			switch(get_type()) //get_group())
			{
			case 4:
				m_chk_layers_gsm.setVisible(b_show);
				m_chk_layers_umts.setVisible(b_show);
				showLbaLanguages(true);
				break;
			case 5: //TAS
				m_chk_layers_gsm.setVisible(false);
				m_chk_layers_umts.setVisible(false);
				m_btn_lbalanguages.setVisible(false);
				showLbaLanguages(true);
				break;
			default:
				m_chk_layers_gsm.setVisible(false);
				m_chk_layers_umts.setVisible(false);
				m_btn_lbalanguages.setVisible(false);
				showLbaLanguages(false);
				break;
			}
		}
		public void enableOverlayButtons(boolean b_enable)
		{
			m_chk_layers_gsm.setEnabled(b_enable);
			m_chk_layers_umts.setEnabled(b_enable);
			if(!b_enable)
			{
				//m_chk_layers_gsm.setSelected(b);
				//m_chk_layers_umts.setSelected(b);
				if(m_chk_layers_gsm.isSelected())
					m_chk_layers_gsm.doClick();
				if(m_chk_layers_umts.isSelected())
					m_chk_layers_umts.doClick();
				//m_chk_layers_gsm.setToolTipText(PAS.l("main_status_wms_coverage_not_prepared"));
				//m_chk_layers_umts.setToolTipText(PAS.l("main_status_wms_coverage_not_prepared"));
			}
			else
			{
				m_chk_layers_gsm.setToolTipText(String.format(PAS.l("main_status_click_to_show_coverage"),"GSM"));
				m_chk_layers_umts.setToolTipText(String.format(PAS.l("main_status_click_to_show_coverage"),"UMTS"));			
			}
		}
		public IconPanel() {
			//m_btn_goto = new JButton(ImageLoader.load_icon("gnome-searchtool_16x16.jpg"));
			Dimension btn_size = new Dimension(40,40);
			if(PAS.icon_version==2)
				m_btn_goto = new JButton(ImageLoader.load_icon("find_32.png"));
			else
				m_btn_goto = new JButton(ImageLoader.load_icon("search_32.png"));
			m_btn_goto.addActionListener(this);
			m_btn_goto.setPreferredSize(btn_size);
			m_btn_goto.setToolTipText(PAS.l("main_status_show_map_of_sending"));
			
			if(PAS.icon_version==2)
				m_btn_confirm_lba_sending = new JButton(ImageLoader.load_icon("connect_32.png"));
			else
				m_btn_confirm_lba_sending = new JButton(ImageLoader.load_icon("go_32.png"));
			m_btn_confirm_lba_sending.addActionListener(this);
			m_btn_confirm_lba_sending.setPreferredSize(btn_size);
			m_btn_confirm_lba_sending.setVisible(false);
			
			if(PAS.icon_version==2)
				m_btn_cancel_lba_sending = new JButton(ImageLoader.load_icon("disconnect_32.png"));
			else
				m_btn_cancel_lba_sending = new JButton(ImageLoader.load_icon("cancel_32.png"));
			m_btn_cancel_lba_sending.addActionListener(this);
			m_btn_cancel_lba_sending.setPreferredSize(btn_size);
			m_btn_cancel_lba_sending.setVisible(false);
			m_btn_cancel_lba_sending.setToolTipText(PAS.l("main_status_cancel_lba_sending"));
			m_chk_layers_gsm.addActionListener(this);
			m_chk_layers_umts.addActionListener(this);
			m_chk_layers_gsm.setActionCommand("act_show_layers_gsm");
			m_chk_layers_umts.setActionCommand("act_show_layers_umts");
			m_chk_layers_gsm.setVisible(false);
			m_chk_layers_umts.setVisible(false);
			
			m_btn_lbalanguages.setToolTipText(PAS.l("main_status_lba_show_message_content"));
			m_btn_lbalanguages.addActionListener(this);
			m_btn_lbalanguages.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e)
				{
					m_pop_lbalanguages.show(e.getComponent(), 0, m_btn_lbalanguages.getHeight());
				}
			});

			add_controls();
		}
		
		public void _SetToolTipText_Confirm(String sz)
		{
			m_btn_confirm_lba_sending.setToolTipText("<html><b>" + PAS.l("common_confirm")+ "</b><br>" + sz + "</html>");
			m_btn_cancel_lba_sending.setToolTipText("<html><b>" + PAS.l("common_cancel") + "</b><br>" + sz + "</html>");
		}
		
		public void ShowConfirmAndCancel(boolean b)
		{
			m_btn_cancel_lba_sending.setVisible(b);
			m_btn_confirm_lba_sending.setVisible(b);
			if(PAS.icon_version==2)
				m_btn_cancel_lba_sending.setIcon(ImageLoader.load_icon("disconnect_32.png"));
			else
				m_btn_cancel_lba_sending.setIcon(ImageLoader.load_icon("stop2_32.png"));
			if(PAS.icon_version==2)
				m_btn_confirm_lba_sending.setIcon(ImageLoader.load_icon("connect_32.png"));
			else
				m_btn_confirm_lba_sending.setIcon(ImageLoader.load_icon("start_32.png"));
			//m_btn_cancel_lba_sending.getRootPane().putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, Boolean.TRUE);
			//m_btn_confirm_lba_sending.putClientProperty(SubstanceLookAndFeel.BORDER_ANIMATION_KIND, b);
			//m_btn_confirm_lba_sending.putClientProperty(SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, SubstanceLookAndFeel.Side.LEFT);
			//m_btn_cancel_lba_sending.putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, new Boolean(b));
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(m_btn_goto)) {
				Variables.getNavigation().gotoMap(get_shape().calc_bounds());
			} else if(e.getSource().equals(m_btn_cancel_lba_sending))
			{
				//CANCEL
				if(getLBA()!=null)
				{
					new no.ums.pas.core.ws.WSConfirmLBA(getLBA().n_parentrefno, getLBA().sz_jobid, false);
				}
				
			} else if(e.getSource().equals(m_btn_confirm_lba_sending))
			{
				//CONFIRM
				if(getLBA()!=null)
				{
					new no.ums.pas.core.ws.WSConfirmLBA(getLBA().n_parentrefno, getLBA().sz_jobid, true);
				}
				
			}
			else if("act_show_layers_gsm".equals(e.getActionCommand()))
			{
				try
				{
					JCheckBox chk = (JCheckBox)e.getSource();
					String sz_operator = "";
					String jobid = "";
					Iterator<LBASEND> en = m_lba_by_operator.iterator();
					while(en.hasNext())
					{
						LBASEND temp = en.next();
						if(temp.l_operator==m_filter_status_by_operator)
						{
							sz_operator = temp.sz_operator;
							jobid = temp.sz_jobid;
							break;
						}
					}
					if(jobid.length()>0)
					{
						System.out.println("Loading GSM overlay for job="+jobid+" (" + sz_operator + ")");
						PAS.get_pas().get_mappane().showAllOverlays(1, chk.isSelected(), jobid, chk, sz_operator);
					}
					else
						Error.getError().addError(PAS.l("common_error"), "No valid JobId for operator found", new Exception(), Error.SEVERITY_ERROR);
				}
				catch(Exception err)
				{
					
				}
			}
			else if("act_show_layers_umts".equals(e.getActionCommand()))
			{
				try
				{
					JCheckBox chk = (JCheckBox)e.getSource();
					String sz_operator = "";
					String jobid = "";
					Iterator<LBASEND> en = m_lba_by_operator.iterator();
					while(en.hasNext())
					{
						LBASEND temp = en.next();
						if(temp.l_operator==m_filter_status_by_operator)
						{
							sz_operator = temp.sz_operator;
							jobid = temp.sz_jobid;
							break;
						}
					}
					
					System.out.println("Loading UMTS overlay for job="+jobid+" (" + sz_operator + ")");
					PAS.get_pas().get_mappane().showAllOverlays(4, chk.isSelected(), jobid, chk, sz_operator);				
				}
				catch(Exception err)
				{
					
				}
			}
			else if("act_lbalanguage_selected".equals(e.getActionCommand()))
			{
				try
				{
					LBALanguageMenuItem item = (LBALanguageMenuItem)e.getSource();
					StringSelection text = new StringSelection(item.getLanguage().getSzText());
					Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
					System.out.println(item.getLanguage().getSzName());
					clip.setContents(text, text);
				}
				catch(Exception err){
					
				}

			}
		}

		public void add_controls() {
			add_spacing(DIR_VERTICAL, 40);
			m_gridconst.fill = GridBagConstraints.BOTH;
			set_gridconst(inc_xpanels(), inc_panels(), 1, 1);
			add(m_btn_goto, m_gridconst);

			add_spacing(DIR_HORIZONTAL, 10);
			
			set_gridconst(inc_xpanels(), get_panel(), 1, 1);
			add(m_btn_lbalanguages, m_gridconst);
			
			add_spacing(DIR_HORIZONTAL, 20);
			
			
			
			set_gridconst(inc_xpanels(), get_panel(), 1, 1);
			add(m_btn_cancel_lba_sending, m_gridconst);
			
			add_spacing(DIR_HORIZONTAL, 10);
			
			set_gridconst(inc_xpanels(), get_panel(), 1, 1);
			add(m_btn_confirm_lba_sending, m_gridconst);
			
			
			
			m_chk_layers_gsm.setSize(new Dimension(10, 100));
			m_chk_layers_umts.setSize(new Dimension(10, 100));
			set_gridconst(0, inc_panels(), 5, 1);
			add(m_chk_layers_gsm, m_gridconst);
			set_gridconst(0, inc_panels(), 5, 1);
			add(m_chk_layers_umts, m_gridconst);
			
			
			init();
		}

		public void init() {
			setVisible(true);
		}
		
	}
	
	public class ResendStatus extends SearchPanelResults {

		public ResendStatus(String[] sz_columns, int[] n_width,
				boolean[] b_editable, Dimension panelDimension) {
			super(sz_columns, n_width, b_editable, panelDimension);
		}

		@Override
		public boolean is_cell_editable(int row, int col) {
			return false;
		}

		@Override
		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseLDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseRDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void start_search() {
			
		}

		@Override
		protected void valuesChanged() {
			
		}
		
	}
	
	public class ResendPanel extends DefaultPanel implements ComponentListener {
		public ResendPanel()
		{
			m_tab_status.addTab("Received statuses", m_resend_status);
			m_tab_status.setPreferredSize(new Dimension(300, 150));
			m_chk_sent_ok.addActionListener(this);
			m_chk_failed.addActionListener(this);
			m_chk_unknown.addActionListener(this);
			add_controls();
		}
		private JProgressBar m_voice_total_progress = new JProgressBar();
		private ResendStatus m_resend_status = new ResendStatus(new String[] { "Code", "Status", "Number" },new int[] { 50, 100, 100 }, new boolean[] { false, false }, new Dimension(300, 150));
		private JTabbedPane m_tab_status = new JTabbedPane();
		private JCheckBox m_chk_sent_ok = new JCheckBox("Sent OK (250)");
		private JCheckBox m_chk_failed = new JCheckBox("Failed (10)");
		private JCheckBox m_chk_unknown = new JCheckBox("Unknown (70)");
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}

		@Override
		public void add_controls() {
			int x_width = 1;
			super.add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
			add(m_chk_sent_ok, m_gridconst);
			set_gridconst(1, get_panel(), x_width, 1, GridBagConstraints.NORTHWEST);
			add(m_chk_failed, m_gridconst);
			set_gridconst(2, get_panel(), x_width, 1, GridBagConstraints.NORTHWEST);
			add(m_chk_unknown, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), x_width*3, 1, GridBagConstraints.NORTHWEST);
			m_voice_total_progress.setPreferredSize(new Dimension(250, 16));
			add(m_voice_total_progress, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), x_width*3, 1, GridBagConstraints.NORTHWEST);
			add(m_tab_status, m_gridconst);
			
			//super.add_spacing(DIR_VERTICAL, 10);
			
			//n_panel_for_voice_filter = inc_panels();
			
			//set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
			//add(get_statuscodeframe().get_panel(), m_gridconst);
			//add_spacing(DIR_VERTICAL, 10);
			//set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
			//add(PAS.get_pas().get_inhabitantframe().get_panel(), m_gridconst);
			//addComponentListener(this);
			init();

		}

		@Override
		public void init() {
			
			Object[] obj = { new Integer(1000),new String("Sent ok"), new String("92293390")};
			m_resend_status.insert_row(obj, -1);
			//m_resend_status.start_search();
			//m_resend_status.setBorder(BorderFactory.createRaisedBevelBorder());
		}
		public void updateUi() {
			//OpenStatuscodes osc = new OpenStatuscodes();
			ArrayList<Object> objects = PAS.get_pas().get_statuscontroller().get_items();
			// Må ha noe lignende openstatuscodes
			// 
			// Kanskje bruke USMSINSTATUS her?
			m_chk_sent_ok.setText("Sent OK (" + (int)(Math.random()*250) + ")");
			m_chk_failed.setText("Failed (" + (int)(Math.random()*50) + ")");
			m_chk_unknown.setText("Unknown (" + (int)(Math.random()*20) + ")");
			
			// Må vel få inn et array med statuskoder?
		}
		public void componentResized(ComponentEvent e) {
			int w = getWidth() - 20;
			int h = getHeight();
			//m_scroll_cc.setPreferredSize(new Dimension(w-10, h/2));
			//m_scroll_cc.revalidate();
			m_tab_status.setPreferredSize(new Dimension(w, h/2));
			m_voice_total_progress.setPreferredSize(new Dimension(w, 20));
			m_voice_total_progress.setSize(new Dimension(w, 20));
			//m_lba_tabbed.setSize(new Dimension(w, h));
			m_tab_status.revalidate();
			m_voice_total_progress.revalidate();
		}
	}
	
	public class VoicePanel extends DefaultPanel implements ComponentListener {
		public static final long serialVersionUID = 1;
		static final int lbl_width = 170;
		private StdTextLabel m_lbl_name		= new StdTextLabel(PAS.l("common_sendingname") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_refno	= new StdTextLabel(PAS.l("common_refno") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_resendrefno = new StdTextLabel(PAS.l("main_status_resent_from_refno") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_created	= new StdTextLabel(PAS.l("common_created") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_sched	= new StdTextLabel(PAS.l("common_scheduled") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_status	= new StdTextLabel(PAS.l("common_sendingstatus") + ":", lbl_width, 11, true);
		private StdTextLabel m_lbl_type		= new StdTextLabel(PAS.l("common_type") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_addresstypes = new StdTextLabel(PAS.l("common_addresstypes") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_queuestatus  = new StdTextLabel("Queue status:" , lbl_width, 11, false);
		private StdTextLabel m_lbl_items	= new StdTextLabel(PAS.l("common_items") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_proc		= new StdTextLabel(PAS.l("common_processed") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_alloc	= new StdTextLabel(PAS.l("common_allocated") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_maxalloc = new StdTextLabel(PAS.l("common_maxchannels") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_oadc		= new StdTextLabel(PAS.l("common_origin") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_nofax	= new StdTextLabel(PAS.l("common_company_blocklist") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_actionprofile = new StdTextLabel(PAS.l("main_sending_settings_msg_profile") + ":", lbl_width, 11, false);
		
		private JSlider m_progress			= new JSlider(1, 840);
		private JButton m_channels_plus		= new JButton("+");
		private JButton m_channels_minus	= new JButton("-");
		private StdTextLabel m_lbl_setmaxalloc = new StdTextLabel("", 40, 12, true);
		private JButton m_btn_setmaxalloc	= new JButton(String.format("%s %s", PAS.l("common_set"), PAS.l("common_maxchannels")));
		
		private StdTextLabel m_txt_name		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_refno	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_resendrefno = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_created  = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_sched	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_status	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_type		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_addresstypes = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_queuestatus  = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_items	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_proc		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_alloc	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_maxalloc = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_oadc		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_nofax	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_actionprofile = new StdTextLabel("", 250, 11, false);
		public JPopupMenu m_menu_dynfiles = new JPopupMenu();

		private boolean b_is_dynfiles_showing = false;
		private boolean b_is_hovering_dynfiles = false;
		
		private JButton m_btn_resend		= new JButton(PAS.l("main_status_resend"));
		
		public boolean m_b_allocset = false;
		
		public VoicePanel() {
			super();
			m_btn_resend.setEnabled(false);
			setPreferredSize(new Dimension(300, 500));
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4)
				m_lbl_resendrefno.setText(PAS.l("main_status_new_message_from_refno") + ":");
			//setPreferredSize(new Dimension(get_uipanel().getWidth()-4, get_uipanel().getHeight()/2));
			add_controls();
			m_progress.addChangeListener(new ChangeListener( ) {
	            public void stateChanged(ChangeEvent e) {
	                int value = m_progress.getValue( );
	                // Update the time label
	                if(value==get_maxalloc())
	                	m_btn_setmaxalloc.setEnabled(false);
	                else
	                	m_btn_setmaxalloc.setEnabled(true);
	                set_maxalloc(value);
	                }
	    		});
			m_lbl_setmaxalloc.setHorizontalTextPosition(SwingConstants.CENTER);
			m_lbl_setmaxalloc.setHorizontalAlignment(SwingConstants.CENTER);
			m_channels_plus.addActionListener(this);
			m_channels_minus.addActionListener(this);
			
			m_channels_plus.setActionCommand("inc_maxchannels");
			m_channels_minus.setActionCommand("dec_maxchannels");
			
			m_channels_plus.setPreferredSize(new Dimension(46,22));
			m_channels_minus.setPreferredSize(new Dimension(46,22));
			m_progress.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {
					//Substance 3.3
					Color c = SubstanceLookAndFeel.getActiveColorScheme().getMidColor();
					
					//Substance 5.2
					//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getMidColor()
					
					m_lbl_maxalloc.setForeground(c);
					m_txt_maxalloc.setForeground(c);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					m_lbl_maxalloc.setForeground(Color.black);
					m_txt_maxalloc.setForeground(Color.black);
				}
				
			});
			addComponentListener(this);
			//m_progress.setValue(get_maxalloc());
		}
		public void set_maxalloc(int n) {
			m_lbl_setmaxalloc.setText(new Integer(n).toString());
		}
		@Override
		public void componentResized(ComponentEvent e) {
			int w = getWidth()-20;
			int h = getHeight()-10;
			m_voice_progress.setPreferredSize(new Dimension(w,20));
			m_voice_progress.setSize(new Dimension(w,20));
			m_voice_progress.revalidate();
		}
	
		protected void enableResend() {
			if(!m_btn_resend.isEnabled())
				m_btn_resend.setEnabled(true);
		}
		public void init_ui() {
			//initialize static values
			m_btn_setmaxalloc.setActionCommand("act_set_maxalloc");
			m_btn_setmaxalloc.addActionListener(this);
			m_btn_setmaxalloc.setSize(new Dimension(135, 22));
			m_btn_setmaxalloc.setPreferredSize(new Dimension(135, 22));
			
			m_btn_resend.setActionCommand("act_resend");
			m_btn_resend.addActionListener(this);
			m_btn_resend.setSize(new Dimension(50, 32));
			m_btn_resend.setPreferredSize(new Dimension(50, 32));
			
			
			
			m_progress.setPreferredSize(new Dimension(250, 22));
			
			m_txt_name.setText(get_sendingname());
			m_txt_refno.setText(new Integer(get_refno()).toString());
			m_txt_created.setText(TextFormat.format_date(get_createdate()) + " " + TextFormat.format_time(get_createtime(), 4));
			m_txt_sched.setText(TextFormat.format_date(get_scheddate()) + " " + TextFormat.format_time(get_schedtime(), 4));
			m_txt_type.setText(TextFormat.get_sendingtype(get_sendingtype()));
			m_txt_addresstypes.setText(TextFormat.get_addresstypes(get_addresstypes()));
			m_txt_oadc.setText(get_oadc());
			m_progress.setValue(get_maxalloc()); 
			m_txt_nofax.setText(get_nofax()==1 ? PAS.l("common_yes") : PAS.l("common_no"));
			
			m_txt_actionprofile.setText(get_actionprofilename());
			m_txt_actionprofile.setForeground(Color.blue);
			Font use = m_txt_actionprofile.getFont();
			use = use.deriveFont(Font.ROMAN_BASELINE);
			m_txt_actionprofile.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e)
				{
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					//if(get_num_dynfiles()>0)
					{
						if(!b_is_dynfiles_showing){
							m_menu_dynfiles.setVisible(true);
			        		m_menu_dynfiles.show(e.getComponent(), 0, m_txt_actionprofile.getHeight());
			        		b_is_dynfiles_showing = true;
						}
						else
						{
							m_menu_dynfiles.setVisible(false);
							b_is_dynfiles_showing = false;
						}
					}
				}
				public void mouseEntered(MouseEvent e)
				{
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				public void mouseExited(MouseEvent e)
				{
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			});

			/*String sz_label = PAS.l("main_sending_settings_show_msg_profile");
			final StdTextLabel lbl = new StdTextLabel(sz_label, 16, true);
			//Substance 3.3
			lbl.setBackground(SubstanceLookAndFeel.getActiveColorScheme().getUltraDarkColor());
			
			//Substance 5.2
			//lbl.setBackground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraDarkColor());
			lbl.setBorder(UMS.Tools.TextFormat.CreateStdBorder(""));
			lbl.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e)
				{
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					lbl.setOpaque(true);
					lbl.setBackground(SubstanceLookAndFeel.getActiveColorScheme().getLightColor());
				}
				public void mouseExited(MouseEvent e)
				{
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					lbl.setOpaque(false);
					lbl.setBackground(new Color(0,0,0,0));
				}
				public void mousePressed(MouseEvent e)
				{
					openMessageProfile();
				}
			});
			lbl.setPreferredSize(new Dimension(170, 25));*/
			//m_menu_dynfiles.setPreferredSize(new Dimension(130, 0));
			
			//m_menu_dynfiles.add(lbl);
			//JButton btn_open = new JButton(PAS.l("main_sending_settings_show_msg_profile"));
			//btn_open.setPreferredSize(new Dimension(200, 25));
			//btn_open.addActionListener(this);
			//btn_open.setActionCommand("act_open_messageprofile");
			JMenuItem menuitem = new JMenuItem(PAS.l("main_sending_settings_show_msg_profile"));
			String face = UIManager.getString("Common.Fontface");
			Font font = new Font(face, Font.BOLD, 12);
			menuitem.setActionCommand("act_open_messageprofile");
			menuitem.addActionListener(this);
			menuitem.setFont(font);//menuitem.getFont().deriveFont(Font.BOLD));
			//menuitem.setFont(menuitem.getFont().deriveFont(16));

			m_menu_dynfiles.add(menuitem);
			m_menu_dynfiles.addSeparator();
			int i;
			for(i=1; i < get_num_dynfiles()+1; i++)
			{
				//StdTextLabel lblfile = new StdTextLabel(PAS.l("common_file") + " " + (i+1));
				DynAudioFileLabel lblfile = new DynAudioFileLabel(get_refno(), i);
				lblfile.setPreferredSize(new Dimension(170,25));
				lblfile.addMouseListener(new MouseAdapter() {
			        public void mousePressed(MouseEvent evt) {
			        	//start player
			        }
			        public void mouseReleased(MouseEvent evt) {
			        }
			    });
				m_menu_dynfiles.add(lblfile);
			}
			m_menu_dynfiles.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					b_is_hovering_dynfiles = true;
				}
				public void mouseExited(MouseEvent e) {
					b_is_hovering_dynfiles = false;
					//m_menu_dynfiles.setVisible(false);
					//b_is_dynfiles_showing = false;
				}				
			});
			//m_menu_dynfiles.setPreferredSize(new Dimension(150, m_menu_dynfiles.getPreferredSize().height));
			/*m_menu_dynfiles.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(ComponentEvent e) {
				}

				@Override
				public void componentMoved(ComponentEvent e) {
				}

				@Override
				public void componentResized(ComponentEvent e) {
					//lblfile.setPreferredSize(new Dimension(m_menu_dynfiles.getWidth(),25));
					for(int i=0; i < m_menu_dynfiles.getComponentCount(); i++)
						m_menu_dynfiles.getComponent(i).setPreferredSize(new Dimension(m_menu_dynfiles.getWidth(), 25));
				}

				@Override
				public void componentShown(ComponentEvent e) {					
				}
				
			});*/
			update_ui();
		}
		SoundPlayer player = null;
		class DynAudioFileLabel extends DefaultPanel
		{
			protected int refno;
			protected int fileno;
			public DynAudioFileLabel(int refno, int fileno)
			{
				super();
				this.refno = refno;
				this.fileno = fileno;
				add_controls();
				init();
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if("act_play_file".equals(e.getActionCommand()))
				{
					try
					{
						SoundlibFileWav wav = new SoundlibFileWav("/audiofiles/", false, 0, "Audio", "v"+refno + "_" + fileno, "", "wav");
						wav.load_file(this, "act_download_finished");
					}
					catch(Exception err)
					{
						err.printStackTrace();
					}
				}
				else if("act_download_finished".equals(e.getActionCommand()))
				{
					try
					{
						String url = PAS.get_pas().get_sitename() + "/audiofiles/v" + refno + "_" + fileno + ".wav";
						//SoundRecorderPanel m_playpanel = new SoundRecorderPanel(this, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_FILE);
						//m_playpanel.disable_record();
						SoundlibFileWav f = (SoundlibFileWav)e.getSource();
						if(player!=null)
							player.getPlayer().stop();
						player = new SoundPlayer(f.get_file().getPath(), null, null, null, true);
						//player.initialize_player(f.get_file().getPath(), true);

						//m_playpanel.initialize_player(url, false);
						player.getPlayer().play();
					}
					catch(Exception err)
					{
						err.printStackTrace();
					}
					
				}
				
			}

			@Override
			public void add_controls() {
				StdTextLabel lblfile = new StdTextLabel(PAS.l("common_file") + " " + (fileno));
				lblfile.setHorizontalTextPosition(JLabel.LEFT);
				lblfile.setVerticalTextPosition(JLabel.CENTER);
				JButton btn_play = new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("play_media_16.png"));
				btn_play.addActionListener(this);
				btn_play.setActionCommand("act_play_file");
				lblfile.setPreferredSize(new Dimension(50, 25));
				//btn_play.setPreferredSize(new Dimension(20, 20));
				set_gridconst(0, 0, 1, 1);
				add(lblfile, m_gridconst);
				set_gridconst(1, 0, 1, 1);
				add(btn_play, m_gridconst);
			}

			@Override
			public void init() {
				setVisible(true);
			
			}
		}
		public void update_ui() {
			//update dynamic values
			try
			{
				if(get_type()==4 || get_type()==5) //new type. LBA=4, voice=1, sms=2
				{
					pnl_cell.setVisible(true);
					pnl_voice.setVisible(false);
				}
				else
				{
					pnl_cell.setVisible(false);
					pnl_voice.setVisible(true);
					if(get_sendingstatus()==7) {
						enableResend();
					}
				}
			}
			catch(Exception e)
			{
				
			}
			
			// Sjekker om brukeren har rettigheter for å sende
			if(!PAS.get_pas().get_rightsmanagement().cansend() && !PAS.get_pas().get_rightsmanagement().cansimulate()) {
				m_btn_resend.setEnabled(false);
				m_progress.setEnabled(false);
			}
			m_lbl_resendrefno.setVisible((get_resendrefno()>0 ? true : false));
			m_txt_resendrefno.setVisible((get_resendrefno()>0 ? true : false));
			m_txt_resendrefno.setText(new Integer(get_resendrefno()).toString());
				
			m_txt_status.setText(TextFormat.get_statustext_from_code(get_sendingstatus(), get_altjmp()));
			m_txt_queuestatus.setText("");
			m_txt_items.setText(new Integer(get_totitem()).toString());
			m_txt_proc.setText(new Integer(get_proc()).toString());
			m_txt_alloc.setText(new Integer(get_alloc()).toString());
			m_txt_oadc.setText(get_oadc());
			m_txt_name.setText(get_sendingname());
			m_txt_created.setText(TextFormat.format_date(get_createdate()) + " " + TextFormat.format_time(get_createtime(), 4));
			m_txt_sched.setText(TextFormat.format_date(get_scheddate()) + " " + TextFormat.format_time(get_schedtime(), 4));
			if(get_shape()==null)
			{
				m_shape = new PolygonStruct(new Dimension(1,1));
			}
			else if(get_shape().getClass().equals(MunicipalStruct.class))
			{
				String str = TextFormat.get_sendingtype(get_sendingtype()) + " (";
				try
				{
					for(int i=0; i < get_shape().typecast_municipal().getMunicipals().size(); i++)
					{
						if(i>0)
							str += ", ";
						str += get_shape().typecast_municipal().getMunicipals().get(i).get_id() + " ";
						str += get_shape().typecast_municipal().getMunicipals().get(i).get_name();
					}
				}
				catch(Exception e)
				{
				}
				str += ")";
				m_txt_type.setText(str);
				m_txt_type.setToolTipText(str);
				
				
			}
			else
				m_txt_type.setText(TextFormat.get_sendingtype(get_sendingtype()));
			//m_progress.setValue(get_maxalloc());
			//m_progress.setMaximum(PAS.get_pas().get_userinfo().get_default_dept().get_maxalloc());
			for(int i=0;i<PAS.get_pas().get_userinfo().get_departments().size();++i) {
				if(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).get_deptpk() == get_deptpk())
					m_progress.setMaximum(((DeptInfo)PAS.get_pas().get_userinfo().get_departments().get(i)).get_maxalloc());
			}
			m_txt_maxalloc.setText(new Integer(get_maxalloc()).toString());
			m_voice_progress.setMinimum(0);
			m_voice_progress.setMaximum(get_totitem());
			m_voice_progress.setValue(get_proc());
			if(get_totitem()<=0)
				m_voice_progress.setStringPainted(false);
			else
				m_voice_progress.setStringPainted(true);
			String tmp = "";
			switch(get_type())
			{
			case 1:
				tmp = PAS.l("main_status_voicesending");
				break;
			case 2:
				tmp = PAS.l("main_status_smssending");
				_ShowMaxAlloc(false);
				break;
			}
			setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + tmp + "    " + (isSimulation() ? String.format("[%s] ", PAS.l("main_sending_simulated")) : String.format("[%s] ", PAS.l("main_sending_live")))));
			
		}
		protected void _ShowMaxAlloc(boolean b)
		{
			m_lbl_maxalloc.setVisible(b);
			m_txt_maxalloc.setVisible(b);
			m_lbl_alloc.setVisible(b);
			m_txt_alloc.setVisible(b);
			m_lbl_setmaxalloc.setVisible(b);
			m_btn_setmaxalloc.setVisible(b);
			m_progress.setVisible(b);
			m_channels_minus.setVisible(b);
			m_channels_plus.setVisible(b);
		}
		
		
		public void actionPerformed(ActionEvent e) {
			if("act_set_maxalloc".equals(e.getActionCommand())) {
				try {
					m_btn_setmaxalloc.setEnabled(false);
					int n_maxalloc = m_progress.getValue();
					m_b_allocset = true;
					get_sendinglist().set_maxalloc("-1", get_refno(), n_maxalloc, this);
				} catch(Exception err) {
					System.out.println(err.getMessage());
					err.printStackTrace();
					Error.getError().addError("StatusSending","Exception in actionPerformed",err,1);
				}
			}
			else if("act_max_alloc_set".equals(e.getActionCommand())) {
				//WSMaxAlloc.MaxAlloc max = (WSMaxAlloc.MaxAlloc)e.getSource();
				UMAXALLOC max = (UMAXALLOC)e.getSource();
				if(max.getNMaxalloc()<0) {
					JOptionPane.showMessageDialog(null, "Could not set max channels (projectpk: " + max.getNProjectpk() + " refno: " + max.getNRefno() + ")", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if("act_resend".equals(e.getActionCommand())) {
				PAS.get_pas().get_sendcontroller().actionPerformed(new ActionEvent(m_this, ActionEvent.ACTION_PERFORMED, "act_resend"));
			}
			else if("inc_maxchannels".equals(e.getActionCommand()))
			{
				m_progress.setValue(m_progress.getValue()+1);
			}
			else if("dec_maxchannels".equals(e.getActionCommand()))
			{
				m_progress.setValue(m_progress.getValue()-1);
			}
			else if("act_open_messageprofile".equals(e.getActionCommand()))
			{
				openMessageProfile();
			}
			
		}
		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
			
			int text_1_x = 2;
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_status, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_status, m_gridconst);
			
			add_spacing(DIR_VERTICAL, 10);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_name, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_name, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_refno, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_refno, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_resendrefno, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_resendrefno, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_created, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_created, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_sched, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_sched, m_gridconst);
			if(get_sendingtype()== SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
				;
			}
			else {
				set_gridconst(0, inc_panels(), text_1_x, 1);
				add(m_lbl_actionprofile, m_gridconst);
				set_gridconst(text_1_x, get_panel(), 6, 1);
				add(m_txt_actionprofile, m_gridconst);
			}
			
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() != 4) {
				set_gridconst(0, inc_panels(), text_1_x, 1);
				add(m_lbl_type, m_gridconst);
				set_gridconst(text_1_x, get_panel(), 6, 1);
				add(m_txt_type, m_gridconst);
			}
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_items, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_items, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_proc, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_proc, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_oadc, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_oadc, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_nofax, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_nofax, m_gridconst);
			
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_alloc, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_alloc, m_gridconst);
			
			set_gridconst(0, inc_panels(), text_1_x, 1);
			add(m_lbl_maxalloc, m_gridconst);
			set_gridconst(text_1_x, get_panel(), 6, 1);
			add(m_txt_maxalloc, m_gridconst);

			add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), 14, 1);
			add(m_voice_progress, m_gridconst);
			add_spacing(DIR_VERTICAL, 20);
			
			set_gridconst(0, inc_panels(), 3, 1);
			add(m_progress, m_gridconst);
			set_gridconst(3, get_panel(), 1, 1);
			add(m_channels_minus, m_gridconst);
			set_gridconst(4, get_panel(), 1, 1);
			add(m_channels_plus, m_gridconst);
			set_gridconst(5, get_panel(), 1, 1);
			add(m_lbl_setmaxalloc, m_gridconst);
			set_gridconst(6, get_panel(), 1, 1);
			add(m_btn_setmaxalloc, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_btn_resend, m_gridconst);
			
			//Border voice = BorderFactory.createTitledBorder("Voice Broadcast");
			setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(String.format(" %s    ", PAS.l("main_status_voicesending")) + (isSimulation() ? String.format("[%s] ", PAS.l("main_sending_simulated")) : String.format("[%s]", PAS.l("main_sending_live")))));
			
			init();
		}
		public void init() {
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4)
				m_btn_resend.setVisible(false);
			setVisible(true);
		}
		protected void openMessageProfile()
		{
			try {
				OpenBrowser.browse(PAS.get_pas().VB4_URL + "/PAS_msg_profile_dlg.asp?f_setreadonly=True&lProfilePk="+_n_profilepk +"&l_deptpk="+PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()+"&usr="+PAS.get_pas().get_userinfo().get_userid()+"&cmp="+PAS.get_pas().get_userinfo().get_compid()+"&pas="+PAS.get_pas().get_userinfo().get_passwd());
				//new OpenBrowser().showDocument(new java.net.URL(PAS.get_pas().VB4_URL + "PAS_msg_profile_dlg.asp?f_setreadonly=True&lProfilePk="+m_current_profile.get_profilepk()+"&l_deptpk="+PAS.get_pas().get_userinfo().get_default_dept().get_deptpk()+"&usr="+PAS.get_pas().get_userinfo().get_userid()+"&cmp="+PAS.get_pas().get_userinfo().get_compid()+"&pas="+PAS.get_pas().get_userinfo().get_passwd()));
			} catch(Exception err) {
				javax.swing.JOptionPane.showMessageDialog(this, "Error opening web browser", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
				Error.getError().addError("Sending_Settings","Exception in actionPerformed",err,1);
			}
		}

	}
	
	public class CellPanel extends DefaultPanel implements ComponentListener {
		
		public static final long serialVersionUID = 1;
		private StdTextLabel m_lbl_operator = new StdTextLabel(PAS.l("common_operator") + ":", 150, 11, true);
		private StdTextLabel m_lbl_sendingstatus = new StdTextLabel(PAS.l("common_sendingstatus") + ":", 150, 11, true);
		private StdTextLabel m_lbl_items = new StdTextLabel(PAS.l("main_status_subscribers") + ":", 150, 11, false);
		private StdTextLabel m_lbl_processed = new StdTextLabel(PAS.l("common_processed") + ":", 150, 11, false);
		private StdTextLabel m_lbl_queued = new StdTextLabel(PAS.l("common_sending") + ":", 150, 11, false);
		private StdTextLabel m_lbl_failed = new StdTextLabel(PAS.l("main_status_failed") + ":", 150, 11, false);
		private StdTextLabel m_lbl_delivered = new StdTextLabel(PAS.l("main_status_delivered") + ":", 150, 11, false);
		private StdTextLabel m_lbl_expired = new StdTextLabel(PAS.l("main_status_expired") + ":", 150, 11, false);
		
		private StdTextLabel m_txt_sendingstatus = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_items = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_processed = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_queued = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_failed = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_delivered = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_operator = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_expired = new StdTextLabel("", 150, 11, false);
		
		private StdTextLabel [] m_lbl_status_ts = new StdTextLabel[7];
		private StdTextLabel [] m_txt_status_ts = new StdTextLabel[7];
		//private StdTextLabel m_lbl_status_cc = new StdTextLabel("GSM numbers from ", 150, 12, true);
		private JTextArea m_txt_status_cc = new JTextArea(" ", 1, 1);
		private JButton m_btn_tas_resend;
		JScrollPane m_scroll_cc;
		
		
		
		public CellPanel() {
			super();
			setPreferredSize(new Dimension(300, 500));
			
			m_txt_status_cc.setEnabled(false);
			m_txt_status_cc.setBackground(new Color(255,255,255, Color.TRANSLUCENT));
			m_txt_status_cc.setDisabledTextColor(new Color(0, 0, 0));
			m_txt_status_cc.setBorder(BorderFactory.createEmptyBorder());
			m_scroll_cc = new JScrollPane(m_txt_status_cc);
			m_scroll_cc.setPreferredSize(new Dimension(500, 300));
			//m_scroll_cc.setEnabled(false);
			m_scroll_cc.setBackground(new Color(255,255,255, Color.TRANSLUCENT));
			m_scroll_cc.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent e) {
					repaint();
				}
			});
			m_scroll_cc.setBorder(BorderFactory.createEmptyBorder());
			add_controls();
			this.addComponentListener(this);
		}
		
		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
		
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_operator, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_operator, m_gridconst);
			
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_sendingstatus, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_sendingstatus, m_gridconst);
			
			add_spacing(DefaultPanel.DIR_VERTICAL,12);
									
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_queued, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_queued, m_gridconst);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_delivered, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_delivered, m_gridconst);
			
			//add_spacing(DefaultPanel.DIR_VERTICAL,12);
						
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_failed, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_failed, m_gridconst);
			
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_expired, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_expired, m_gridconst);
			
			//add_spacing(DefaultPanel.DIR_VERTICAL,12);
			
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_processed, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_processed, m_gridconst);
			
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_items, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_items, m_gridconst);
			
			add_spacing(DefaultPanel.DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), 7, 1); //5
			add(m_lba_progress, m_gridconst);
			add_spacing(DefaultPanel.DIR_VERTICAL, 10);
			
			set_gridconst(0, inc_panels(), 1, 1);
			m_btn_tas_resend = new JButton("Resend");
			add(m_btn_tas_resend, m_gridconst);
			m_btn_tas_resend.setVisible(false);
			m_btn_tas_resend.addActionListener(this);
			
			set_gridconst(0, inc_panels(), 7, 1); //5
			add(m_lba_tabbed, m_gridconst);
			/*
			if(get_type()==5) {
				add_spacing(DIR_VERTICAL, 10);
				set_gridconst(0, inc_panels(), 1, 1);
				add(new LbaSmsReplyPanel(),m_gridconst);
			}*/
			
			//add timestamp labels
			reset_panels();
			for(int i=0; i < m_lbl_status_ts.length; i++)
			{
				m_lbl_status_ts[i] = new StdTextLabel("", 140, 9, false);
				m_txt_status_ts[i] = new StdTextLabel("", 70, 9, false);
				m_lbl_status_ts[i].setFont(new Font(null, Font.PLAIN, 8));
				m_txt_status_ts[i].setFont(new Font(null, Font.PLAIN, 8));
				set_gridconst(4, inc_panels(), 2, 1);
				add(m_lbl_status_ts[i], m_gridconst);
				set_gridconst(6, get_panel(), 1, 1);
				add(m_txt_status_ts[i], m_gridconst);
			}
					
			
			this.revalidate();
			repaint();
			//init();
		}
		
		public void updateUi() {
			switch(m_filter_status_by_operator)
			{
			case -1:
				m_txt_operator.setText(PAS.l("main_status_lba_nationalities"));
				break;
			default:
			{
				String sz_operator = "";
				Iterator<LBASEND> en = m_lba_by_operator.iterator();
				while(en.hasNext())
				{
					LBASEND temp = en.next();
					if(temp.l_operator==m_filter_status_by_operator)
					{
						sz_operator = temp.sz_operator;
						break;
					}
				}
				m_txt_operator.setText(sz_operator);
			}
				break;
			}
			m_txt_sendingstatus.setText(get_lba_sendingstatusS());// + " " + m_lba.send_ts.get(m_lba.send_ts.size()-1).sz_timestamp);
			m_txt_sendingstatus.setToolTipText(get_lba_sendingstatusS());
			m_txt_delivered.setText(String.valueOf(get_lba_delivered()));
			m_txt_queued.setText(String.valueOf(get_lba_queued()));
			m_txt_failed.setText(String.valueOf(get_lba_failed()));
			m_txt_expired.setText(String.valueOf(get_lba_expired()));
			m_txt_processed.setText((get_lba_processed() >= 0 ? String.valueOf(get_lba_processed()) : PAS.l("common_na")));
			m_txt_items.setText((get_lba_items() >= 0 ? String.valueOf(get_lba_items()) : PAS.l("common_na")));
			
			if(m_filter_status_by_operator > -1 && m_lba.n_status > 42000)
				m_btn_tas_resend.setVisible(true);
			else
				m_btn_tas_resend.setVisible(false);
			
			if(m_lba==null) {
				pnl_cell.setVisible(false);
				return;
			} else
			{
				if(((get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT) ||
						(get_addresstypes() & SendController.SENDTO_TAS_SMS) == SendController.SENDTO_TAS_SMS)
				{
					if(!pnl_cell.isVisible())
						pnl_cell.setVisible(true);
				}
			}
			
			
			/*if(get_lba_sendingstatus()==LBASEND.LBASTATUS_PREPARED_CELLVISION)
			{
				pnl_icon.ShowConfirmAndCancel(true);
				setNeedAttention(true);
			}
			else
			{
				pnl_icon.ShowConfirmAndCancel(false);
				setNeedAttention(false);
			}*/
			int n_one_or_more_operators_ready = 0;
			for(int i=0; i < m_lba_by_operator.size(); i++)
			{
				if(m_lba_by_operator.get(i).n_status==LBASEND.LBASTATUS_PREPARED_CELLVISION ||
					m_lba_by_operator.get(i).n_status==LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE)
				{
					pnl_icon.ShowConfirmAndCancel(true);
					setNeedAttention(true);
					++n_one_or_more_operators_ready;
				}
			}
			if(n_one_or_more_operators_ready<=0)
			{
				pnl_icon.ShowConfirmAndCancel(false);
				setNeedAttention(false);				
			}
			
			if(getLBA()!=null)
			{
				int n_operators_ready_for_confirmation = 0;
				String sz_operators = "";
				for(int i=0; i < m_lba_by_operator.size(); i++)
				{
					//list up all operators ready for confirmation
					sz_operators += "<b>";
					if(m_lba_by_operator.get(i).n_status==LBASEND.LBASTATUS_PREPARED_CELLVISION || m_lba_by_operator.get(i).n_status==LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE)
						sz_operators += m_lba_by_operator.get(i).sz_operator + " - " + PAS.l("common_ready") + "<br>";
					else if(m_lba_by_operator.get(i).n_status < LBASEND.LBASTATUS_PREPARED_CELLVISION)
						sz_operators += m_lba_by_operator.get(i).sz_operator + " - " + PAS.l("common_not_ready") + "<br>";
					else if(m_lba_by_operator.get(i).n_status > LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE)
						sz_operators += m_lba_by_operator.get(i).sz_operator + " - (" + LBASEND.LBASTATUSTEXT(m_lba_by_operator.get(i).n_status)+ ")<br>";
					sz_operators += "</b>";
				}
				pnl_icon._SetToolTipText_Confirm(PAS.l("main_status_locationbased_alert_short") + "&nbsp;" + (getLBA().f_simulation==1 ? PAS.l("main_sending_simulated").toUpperCase() : PAS.l("main_sending_live").toUpperCase()) + "&nbsp;" + PAS.l("common_to") + "&nbsp;" + getLBA().n_items + "&nbsp;" + PAS.l("main_sending_recipients") + "<br><br>" + sz_operators);
			}

			String tooltip = "";
			int n_start_at  = Math.max(0, m_lba.send_ts.size() - m_lbl_status_ts.length);
			for(int i=n_start_at; i < m_lba.send_ts.size(); i++)
			{
				m_lbl_status_ts[i-n_start_at].setText(m_lba.send_ts.get(i).sz_operator + " - " + m_lba.send_ts.get(i).sz_status);
				m_txt_status_ts[i-n_start_at].setText(m_lba.send_ts.get(i).sz_timestamp.substring(0,6) + m_lba.send_ts.get(i).sz_timestamp.substring(8,10) + " " + m_lba.send_ts.get(i).sz_timestamp.substring(11));
			}
			for(int i=m_lba.send_ts.size(); i < m_lbl_status_ts.length; i++)
			{
				m_lbl_status_ts[i].setText("");
				m_txt_status_ts[i].setText("");
			}
			if(get_type()==5) {
				pnl_lbasmsreply.update_responses(_m_smsin_stats);
			}
			
			repaint();
		}
		
		public void setNeedAttention(final boolean b)
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						PAS.get_pas().get_eastcontent().get_statuspanel().putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, b);
						get_uipanel().putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, b);
					}
				});
			}
			catch(Exception e)
			{
				
			}
		}

		public void init() {
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			// Resend
			new WSTasResend(this,get_refno(),m_lba.l_operator);

		}
		
		@Override
		public void componentHidden(ComponentEvent e) {
			
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			
		}

		@Override
		public void componentResized(ComponentEvent e) {
			int w = getWidth() - 20;
			int h = getHeight();
			//m_scroll_cc.setPreferredSize(new Dimension(w-10, h/2));
			//m_scroll_cc.revalidate();
			m_lba_tabbed.setPreferredSize(new Dimension(w, h-220));
			m_lba_progress.setPreferredSize(new Dimension(w, 20));
			m_lba_progress.setSize(new Dimension(w, 20));
			//m_lba_tabbed.setSize(new Dimension(w, h));
			m_lba_tabbed.revalidate();
			m_lba_progress.revalidate();
			
			//m_scroll_cc.setSize(new Dimension(getWidth(), getHeight()/2));

		}

		@Override
		public void componentShown(ComponentEvent e) {
			
		}
		
	}
	
	public class PAPanel extends DefaultPanel implements ComponentListener {

		public static final long serialVersionUID = 1;
		private StdTextLabel m_lbl_message = new StdTextLabel(PAS.l("main_sending_lba_message") + ":", 150,11,true);
		private JTextArea 	 m_txt_message = new JTextArea("",1,1);
		private StdTextLabel m_lbl_operator = new StdTextLabel(PAS.l("common_operator") + ":", 150, 11, true);
		
		private StdTextLabel [] m_lbl_status_ts = new StdTextLabel[7];
		private StdTextLabel [] m_txt_status_ts = new StdTextLabel[7];
		//private StdTextLabel m_lbl_status_cc = new StdTextLabel("GSM numbers from ", 150, 12, true);
		private JTextArea m_txt_status_cc = new JTextArea(" ", 1, 1);
		private JButton m_btn_tas_resend;
		JScrollPane m_scroll_cc;
		
		public PAPanel() {
			super();
			setPreferredSize(new Dimension(300, 500));
			add_controls();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
			ENABLE_GRID_DEBUG = false;
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_message, m_gridconst);
			m_txt_message.setPreferredSize(new Dimension(150,100));
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_message, m_gridconst);
			
			set_gridconst(0, inc_panels(), 2, 1); //5
			//m_pa_tabbed.setPreferredSize(new Dimension(200, 150));
			add(m_pa_tabbed, m_gridconst);
			//m_pa_tabbed.addTab("operator 1", new PAStatusTab());
			//m_pa_tabbed.addTab("operator 2", new PAStatusTab());
			/*
			if(get_type()==5) {
				add_spacing(DIR_VERTICAL, 10);
				set_gridconst(0, inc_panels(), 1, 1);
				add(new LbaSmsReplyPanel(),m_gridconst);
			}*/	
			
			this.revalidate();
			repaint();
			init();
		}

		@Override
		public void init() {
			setVisible(true);
		}
		
	}
}