package no.ums.pas.status;


import com.google.common.base.Supplier;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.TileCacheGsmCoverage;
import no.ums.map.tiled.TileCacheUmtsCoverage;
import no.ums.map.tiled.TileLookupImpl;
import no.ums.pas.PAS;
import no.ums.pas.area.AreaController;
import no.ums.pas.area.AreaController.AreaSource;
import no.ums.pas.area.constants.PredefinedAreaConstants;
import no.ums.pas.area.main.MainAreaController;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.laf.ULookAndFeel;
import no.ums.pas.core.mainui.StatusPanel;
import no.ums.pas.core.ws.WSCancelSending;
import no.ums.pas.core.ws.WSCancelSending.ICallback;
import no.ums.pas.core.ws.WSTasResend;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MunicipalStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.sendpanels.ShowProfileAction;
import no.ums.pas.sound.SoundPlayer;
import no.ums.pas.sound.SoundlibFileWav;
import no.ums.pas.status.LBASEND.LBAHISTCC;
import no.ums.pas.status.LBASEND.LBASEND_TS;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.common.LBALanguage;
import no.ums.ws.common.UCancelSendingResponse;
import no.ums.ws.common.USMSINSTATS;
import no.ums.ws.pas.UMAXALLOC;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


public class StatusSending {

    private static final Log log = UmsLog.getLogger(StatusSending.class);

	protected StatusSending m_this;
	public StatusSending get_statussending() { return m_this; }
	private StatusSendingList m_sendinglist = null;
	private LBASEND m_lba = null;
	private ArrayList<LBASEND> m_lba_by_operator = new ArrayList<LBASEND>();
	
	public void ResetLbaByOperator() {
		m_lba_by_operator.clear();
	}
	public void addLbaOperator(LBASEND lba)
	{
		m_lba_by_operator.add(lba);
	}
	public int getNumberOfOperators() {
		return m_lba_by_operator.size();
	}
	
	public float get_percentage() {
		float percent = 0;
		switch(_n_type) {
		case 1:	// VOICE
			if(this._n_sendingstatus == 7 || this._n_sendingstatus == 1000)
				percent = 100.0f;
			else if(this.get_proc()>0)
				percent = this.get_proc() * 100.0f / this.get_totitem();
			else
				percent = 0;
			break;
		case 2: // SMS
			if(this._n_sendingstatus == 7 || this._n_sendingstatus == 1000)
				percent = 100.0f;
			else if(this.get_proc()>0)
				percent = this.get_proc() * 100.0f / this.get_totitem();
			else
				percent = 0;
			break;
		case 4: // LBA
		case 5: // TAS
			if(m_lba != null && (this.m_lba.n_status == LBASEND.LBASTATUS_CANCELLED || this.m_lba.n_status == LBASEND.LBASTATUS_FINISHED || this.m_lba.n_status > 42000 || this.m_lba.n_status == -1))
				percent = 100.0f;
			else if(m_lba != null) {
				//float[] f_percent = new float[m_lba_by_operator.size()];
				for(int i=0;i<m_lba_by_operator.size();i++) {
					if(m_lba_by_operator.get(i).n_items > 0) {
						percent += ((m_lba_by_operator.get(i).n_cancelled + m_lba_by_operator.get(i).n_proc) * 100.0f / m_lba_by_operator.get(i).n_items) / m_lba_by_operator.size();
					}
					else {
						percent += 0;
					}
				}
				//percent = (this.m_lba.n_cancelled + this.m_lba.n_proc) * 100.0f / m_lba.n_items;
			}
			else
				percent = 0;
			break;
		}
		return percent;
	}
	public LBASEND getLBA() { return m_lba; }
	public boolean hasLBA() { return (m_lba != null); }
	
	
	private StatusSendingUI m_uipanel;
	public StatusSendingUI get_uipanel() { return m_uipanel; }
	private JProgressBar m_lba_progress= new JProgressBar();
	private JProgressBar m_voice_progress = new JProgressBar();
	private LBATabbedPane m_lba_tabbed = new LBATabbedPane();
	private JTabbedPane m_pa_tabbed = new JTabbedPane();
	private StdTextLabel m_sendingname_label = new StdTextLabel("", 11, false);
	public StdTextLabel getTotalSendingnameLabel() { return m_sendingname_label; }
	protected int n_sending_completion_percent = 0;

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
		if(_n_type == 4) { // LBA
			m_lbl_processed_and_total.setText(get_lba_processed() + " / " + get_lba_items());
		}
		else {
			m_lbl_processed_and_total.setText(get_proc() + " / " + get_totitem());
		}
		return m_lbl_processed_and_total;
	}
	public JLabel getCompletionPercentLabel() {
		m_lbl_completion_percent.setText(Math.round(get_percentage()) + "%");
		return m_lbl_completion_percent;
	}
	
	public String toString()
	{
        return getTotalSendingnameLabel().getText();
	}
	public String getSendingname() { 
		return _sz_sendingname;
	}

    public void setSendingnameLabel(String sz)
	{
		String sz_newtext = "<html><font style=\"font-size:9px;\">" + _sz_sendingname + "&nbsp;&nbsp;&nbsp;<b>" + sz + "</font></b></html>"; //font-face:Arial; 
        m_sendingname_label.setText(sz_newtext);
        m_sendingname_label.revalidate();
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
		float n_voice_percent;
		if(m_lba!=null && 
				(((get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT)) ||
				(((get_addresstypes() & SendController.SENDTO_TAS_SMS) == SendController.SENDTO_TAS_SMS)))
		{
			if(get_lba_items()>0) {
				n_lba_percent = 0;
				for(int i=0;i<m_lba_by_operator.size();i++) {
					if(m_lba_by_operator.get(i).n_items > 0) {
						n_lba_percent += ((m_lba_by_operator.get(i).n_cancelled + m_lba_by_operator.get(i).n_proc) * 100.0f / m_lba_by_operator.get(i).n_items) / m_lba_by_operator.size();
					}
					else {
						n_lba_percent += 0;
					}					
				}
			}
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
		n_sending_completion_percent = Math.round(n_percent);
		setSendingnameLabel(Math.round(n_percent) + "%");
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
					pnl_icon.init_ui();
					get_uipanel().init_ui();
				/*}
			});*/
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
		}

	}
	
	/*
	 * sum all operators
	 */
	public void CalcLbaTotalsFromOperators()
	{
		LBASEND lba = new LBASEND();
        for (LBASEND aM_lba_by_operator : m_lba_by_operator) {
            if (m_filter_status_by_operator != aM_lba_by_operator.l_operator && m_filter_status_by_operator > -1)
                continue;
            lba.n_cancelled += aM_lba_by_operator.n_cancelled;
            lba.n_delivered += aM_lba_by_operator.n_delivered;
            lba.n_failed += aM_lba_by_operator.n_failed;
            lba.n_items += aM_lba_by_operator.n_items == -1 ? 0 : aM_lba_by_operator.n_items;
            lba.n_proc += aM_lba_by_operator.n_proc == -1 ? 0 : aM_lba_by_operator.n_proc;
            lba.n_queued += aM_lba_by_operator.n_queued;
            lba.n_expired += aM_lba_by_operator.n_expired;
            lba.l_created_ts = aM_lba_by_operator.l_created_ts;
            lba.l_started_ts = aM_lba_by_operator.l_started_ts;
            lba.l_expires_ts = aM_lba_by_operator.l_expires_ts;

            lba.n_cbtype = aM_lba_by_operator.n_cbtype;
            lba.f_simulation = aM_lba_by_operator.f_simulation;
            lba.l_operator = aM_lba_by_operator.l_operator;
            lba.n_parentrefno = aM_lba_by_operator.n_parentrefno;
            lba.n_requesttype = aM_lba_by_operator.n_requesttype;
            lba.n_response = aM_lba_by_operator.n_response;
            lba.n_retries = aM_lba_by_operator.n_retries;
            if (lba.n_status > aM_lba_by_operator.n_status || lba.n_status < 0)
                lba.n_status = aM_lba_by_operator.n_status;
            lba.sz_areaid = aM_lba_by_operator.sz_areaid;
            lba.sz_jobid = aM_lba_by_operator.sz_jobid;

            lba.send_ts = aM_lba_by_operator.send_ts;
            lba.hist_cc = aM_lba_by_operator.hist_cc;
            lba.hist_cell = aM_lba_by_operator.hist_cell;

        }
		//log.debug("Worst operator status for refno="+lba.n_parentrefno+": "+lba.n_status);
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
        for (LBASEND aM_lba_by_operator : m_lba_by_operator) {
            if (m_filter_status_by_operator != aM_lba_by_operator.l_operator && m_filter_status_by_operator > -1)
                continue;
            for (int ts = 0; ts < aM_lba_by_operator.send_ts.size(); ts++) {
                LBASEND_TS temp = aM_lba_by_operator.send_ts.get(ts);
                temp.sz_operator = aM_lba_by_operator.sz_operator;
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

        for (LBASEND aM_lba_by_operator : m_lba_by_operator) {
            if (m_filter_status_by_operator != aM_lba_by_operator.l_operator && m_filter_status_by_operator > -1)
                continue;
            for (int cc = 0; cc < aM_lba_by_operator.hist_cc.size(); cc++) {
                LBAHISTCC cctemp = aM_lba_by_operator.hist_cc.get(cc); //to be added to cclist
                Integer ccode = cctemp.l_ccode;
                if (added.containsKey(ccode)) //already added ccode, accumulate
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

                } else { //new ccode, make new
                    LBAHISTCC newcc;
                    try {
                        newcc = (LBAHISTCC) cctemp.clone();
                        cclist.add(newcc);
                        added.put(cctemp.l_ccode, newcc);
                    } catch (CloneNotSupportedException e) {
                        throw new IllegalStateException("Failed to clone LBAHISTCC", e);
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
			else if(m_lba.n_status<LBASEND.LBASTATUS_PREPARED_CELLVISION &&
					!m_lba.HasFailedStatus() && !m_lba.HasFinalStatus())
			{
				m_lba_progress.setMaximum(1);
				m_lba_progress.setValue(1);
				m_lba_progress.setIndeterminate(true);
				m_lba_progress.setStringPainted(false);			
			}
			else if(m_lba.n_items==0)
			{
				m_lba_progress.setMaximum(1);
				m_lba_progress.setValue(0);
				m_lba_progress.setIndeterminate(false);
				m_lba_progress.setStringPainted(false);				
			}
			else
			{
				m_lba_progress.setIndeterminate(false);
				m_lba_progress.setMinimum(0);
				
				m_lba_progress.setMaximum(100);
				float percentTotal = 0;
				for(LBASEND ls : m_lba_by_operator)
				{
					if(m_filter_status_by_operator==ls.l_operator || m_filter_status_by_operator==-1)
					{
						float fTmp = (ls.n_proc + ls.getCancelled())*1.0f/(ls.n_items* (m_filter_status_by_operator==-1 ? m_lba_by_operator.size() : 1));
						percentTotal += fTmp;
					}
				}
				m_lba_progress.setValue((int)(percentTotal*100));
				//m_lba_progress.setMaximum(m_lba.n_items);
				
				//m_lba_progress.setValue(m_lba.n_proc + m_lba.getCancelled());
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
                set_lba_sendingstatusS(Localization.l("common_unknown_status") + " [" + get_lba_sendingstatus() + "]");
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
                        {
                            dateFormat = new SimpleDateFormat("dd'" + Localization.l("common_days_short") + " 'HH'" + Localization.l("common_hours_short") + " 'mm'" + Localization.l("common_minutes_short") + " 'ss'" + Localization.l("common_seconds_short") + "'");
                        }
						else if(diff >= 1000*60*60)//one hour or more
                        {
                            dateFormat = new SimpleDateFormat("HH'" + Localization.l("common_hours_short") + " 'mm'" + Localization.l("common_minutes_short") + " 'ss'" + Localization.l("common_seconds_short") + "'");
                        }
						else {
                            dateFormat = new SimpleDateFormat("mm'" + Localization.l("common_minutes_short") + " 'ss'" + Localization.l("common_seconds_short") + "'");
                        }

                        sz_time_used = "    [" + Localization.l("common_completed") + " " + dateFormat.format(new java.util.Date(diff)) + "] ";
					}
					catch(Exception timeerr)
					{
                        log.warn("Failed to format text", timeerr);
					}
				}
			}
			String sz_type;
			switch(get_group())
			{
			case 5:

                sz_type = Localization.l("main_status_traveller_alert");
				break;
			default:
                sz_type = Localization.l("main_status_locationbased_alert");
				break;
			}
			if(m_lba.f_simulation==2) {
                pnl_cell.setBorder(TextFormat.CreateStdBorder(" " + sz_type + "    [" + Localization.l("common_silent").toUpperCase() + " " + Localization.l("main_sending_live") + "]" + sz_time_used));
            }
			else if(m_lba.f_simulation==1) {
                pnl_cell.setBorder(TextFormat.CreateStdBorder(" " + sz_type + "    [" + Localization.l("main_sending_simulated") + "]" + sz_time_used)); //JobID=" + m_lba.sz_jobid));//BorderFactory.createTitledBorder("Location Based Alert (Simulated) -- JobID " + m_lba.sz_jobid));
            }
			else if(m_lba.f_simulation==0) {
                pnl_cell.setBorder(TextFormat.CreateStdBorder(" " + sz_type + "    [" + Localization.l("main_sending_live") + "]" + sz_time_used));// JobID=" + m_lba.sz_jobid));//BorderFactory.createTitledBorder("Location Based Alert (Live sending) -- JobID " + m_lba.sz_jobid));
            }
		}
		catch(Exception e)
		{
            log.warn("Failed to set LBA", e);
		}
		try
		{
			if(pnl_cell!=null)
			{
				pnl_cell.init();
			}
		}
		catch(Exception e)
		{
		    log.warn("Failed to init cell", e);
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
			log.debug("Break");
		
		if(s.shapeID == ShapeStruct.SHAPE_POLYGON)
		{
			double lon=0,lat=0;
			for(int i=0;i<((PolygonStruct)s).get_coors_lat().size();++i) {
				if(lon == ((PolygonStruct)s).get_coors_lon().get(i) && lat == ((PolygonStruct)s).get_coors_lat().get(i));
					log.debug("Break");
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
	private int _n_resend_addresstypes;
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
    private String _sz_sms_messagetext;
	private String _sz_actionprofilename;
	private int _n_num_dynfiles;
	private boolean _b_marked_as_cancelled = false;
	private boolean cancelRequestSent = false;


	public boolean isCancelRequestSent() {
		return cancelRequestSent;
	}
	public void setCancelRequestSent(boolean cancelRequestSent) {
		this.cancelRequestSent = cancelRequestSent;
	}
	
	private List<USMSINSTATS> _m_smsin_stats;

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
	/*
  	    VOICE = 1,
        SMS = 2,
        EMAIL = 3,
        LBA = 4,
        TAS = 5,
	 */
	public int get_sendchannel() { return _n_type; }
	public int get_addresstypes() { return _n_addresstypes; }
	public int get_resend_addresstypes() { return _n_resend_addresstypes; }
	public void set_resend_addresstypes(int addresstypes) { _n_resend_addresstypes = addresstypes; }
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
	public boolean isMarkedAsCancelled() { return _b_marked_as_cancelled; }
	
	public boolean isSimulation() { return (get_dynacall() == 2); }
	
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

    public String get_sms_message_text() { return _sz_sms_messagetext; }


    public void setProjectpk(String sz_projectpk) { m_sz_projectpk = sz_projectpk; }
	
	public boolean HasFinalStatus()
	{
		boolean b_ret = false;
		switch(get_type())
		{
		case 1: //voice
		case 2: //sms
		case 3: //email
			if(get_sendingstatus() < 0 || get_sendingstatus()>=7)
				b_ret = true;
			break;
		case 4: //lba
		case 5: //tas
			b_ret = (m_lba != null && m_lba.HasFinalStatus());
			break;
		}
		return b_ret;
	}
	
	
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
		this(sz[0], sz[1], sz[2], sz[3], sz[4], sz[5], sz[6], sz[7], sz[8], sz[9], 
				sz[10], sz[11], sz[12], sz[13],
			sz[14], sz[15], sz[16], sz[17], sz[18], sz[19], sz[20], 
			sz[21], sz[22], sz[23], sz[24], sz[25], sz[26], sz[27],
			sz[28]);
	}
	public StatusSending(String sz_sendingname, String sz_refno, String sz_group, String sz_createdate, String sz_createtime,
						String sz_scheddate, String sz_schedtime, String sz_sendingstatus, String sz_comppk, String sz_deptpk,
						String sz_type, String sz_addresstypes, String sz_profilepk, String sz_queuestatus, String sz_totitem,
						String sz_proc, String sz_altjmp, String sz_alloc, String sz_maxalloc, String sz_oadc, String sz_qreftype,
						String sz_dynacall, String sz_nofax, String sz_linktype, String sz_resendrefno, String sz_messagetext, String sz_actionprofilename,
						String sz_num_dynfiles, String sz_marked_as_cancelled) {
		pnl_icon = new IconPanel();

		_sz_sendingname = sz_sendingname;
		_n_refno		= Integer.valueOf(sz_refno);
		_n_group		= Integer.valueOf(sz_group);
		_n_createdate	= Integer.valueOf(sz_createdate);
		_n_createtime	= Integer.valueOf(sz_createtime);
		_n_scheddate	= Integer.valueOf(sz_scheddate);
		_n_schedtime	= Integer.valueOf(sz_schedtime);
		_n_sendingstatus= Integer.valueOf(sz_sendingstatus);
		_n_comppk		= Integer.valueOf(sz_comppk);
		_n_deptpk		= Integer.valueOf(sz_deptpk);
		_n_type			= Integer.valueOf(sz_type);
		_n_addresstypes = Integer.valueOf(sz_addresstypes);
		_n_profilepk	= Integer.valueOf(sz_profilepk);
		_n_queuestatus	= Integer.valueOf(sz_queuestatus);
		_n_totitem		= Integer.valueOf(sz_totitem);
		if(_n_totitem<0)
			_n_totitem = 0;
		_n_proc			= Integer.valueOf(sz_proc);
		_n_altjmp		= Integer.valueOf(sz_altjmp);
		_n_alloc		= Integer.valueOf(sz_alloc);
		_n_maxalloc		= Integer.valueOf(sz_maxalloc);
		_sz_oadc		= sz_oadc;
		_n_qreftype		= Integer.valueOf(sz_qreftype);
		_f_dynacall		= Integer.valueOf(sz_dynacall);
		_n_nofax		= Integer.valueOf(sz_nofax);
		_n_linktype		= Integer.valueOf(sz_linktype);
		_n_resendrefno  = Integer.valueOf(sz_resendrefno);
		_sz_sms_messagetext = sz_messagetext;
		_sz_actionprofilename = sz_actionprofilename;
		_n_num_dynfiles = Integer.valueOf(sz_num_dynfiles);
		_b_marked_as_cancelled = Boolean.parseBoolean(sz_marked_as_cancelled);
		_m_smsin_stats = new ArrayList<USMSINSTATS>();
		m_this			= this;
		m_sendingname_label.setText(_sz_sendingname);
		
		// Disable goto button on resend, there is no navigation
		//if(_n_resendrefno>0 && _n_type == 2 && PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4)
		//if(_n_resendrefno>0)
		//	pnl_icon.m_btn_goto.setVisible(false);
		
		m_lba_tabbed.setCallback(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if("act_lba_filter_by_operator".equals(e.getActionCommand()))
				{
					Integer operator = (Integer)e.getSource();
					
					pnl_icon.enableOverlayButtons((operator > 0));
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
						if(m_filter_status_by_operator!= operator)
						{
							PAS.get_pas().get_mappane().resetAllOverlays();
						}
					}
					catch(Exception err)
					{
					    log.warn("Failed to reset all overlays", err);
					}
					m_filter_status_by_operator = operator;
					//log.debug("Filter by operator " + m_filter_status_by_operator);
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
			if(pnl_voice!=null && !pnl_voice.m_b_allocset) {
				pnl_voice.set_maxalloc(_n_maxalloc);
				pnl_voice.m_progress.setValue(_n_maxalloc);
			}
		}
		catch(Exception e)
		{
		    log.warn("Failed to set variables", e);
		}
		_sz_oadc		= s.get_oadc();
		_n_qreftype		= s.get_qreftype();
		_f_dynacall		= s.get_dynacall();
		_sz_sms_messagetext = s.get_sms_message_text();
		_sz_actionprofilename = s.get_actionprofilename();
		_b_marked_as_cancelled = s.isMarkedAsCancelled();
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
			    log.warn("Failed to update ui", e);
			}
			try
			{
				pnl_cell.updateUi();
			}
			catch(Exception e)
			{
				log.warn("Failed to update cell ui", e);
			}
			try
			{
				pnl_pa.updateUI();
			}
			catch(Exception e){
                log.warn("Failed to update pnl ui", e);
            }
			
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
			set_gridconst(0,inc_panels(), 1, 1);
			add(pnl_cell, m_gridconst);
			set_gridconst(0,inc_panels(), 1, 1);
			
			if(get_type()==5) {
				add_spacing(DIR_VERTICAL, 10);
				set_gridconst(0, inc_panels(), 1, 1);
				add(pnl_lbasmsreply,m_gridconst);
                pnl_lbasmsreply.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + Localization.l("main_tas_panel_responses") + " "));
			}
			else if(get_type()==6) {
				add(pnl_pa, m_gridconst);
			}
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
			int w = getWidth()-5;
			pnl_icon.setPreferredSize(new Dimension(w, iconPanelSize));
			if(pnl_voice.isVisible())
				pnl_voice.setPreferredSize(new Dimension(w, (getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize));
				//pnl_voice.setPreferredSize(new Dimension(getWidth()-10, (getHeight()/voicelba_visible)-iconPanelSize-50)); //-iconPanelSize/2));
			else
				pnl_voice.setPreferredSize(new Dimension(w, 1));
			if(get_type()==5) {
				//pnl_cell.setPreferredSize(new Dimension(getWidth()-10, ((getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize/2)/2));
				pnl_cell.setPreferredSize(new Dimension(w, (getHeight()/2)+40));
				//pnl_lbasmsreply.setPreferredSize(new Dimension(w, ((getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize/2)/3));
				pnl_lbasmsreply.setPreferredSize(new Dimension(w, (getHeight()/2)-40-iconPanelSize-100));

			}
			else if(get_type()==6) { // Centric
				pnl_pa.setPreferredSize(new Dimension(w, (getHeight()-iconPanelSize)));
			}
			else if(pnl_cell.isVisible())
				pnl_cell.setPreferredSize(new Dimension(w, (getHeight()-iconPanelSize)/voicelba_visible-iconPanelSize));
			else
				pnl_cell.setPreferredSize(new Dimension(w, 1));
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
		JButton m_btn_save_area = null;
		JButton m_btn_confirm_lba_sending = null;
		JButton m_btn_cancel_lba_sending = null;
		private JCheckBox m_chk_layers_gsm = new JCheckBox("GSM900 " + Localization.l("main_status_gsmcoverage"), false);

        private JCheckBox m_chk_layers_umts = new JCheckBox("UMTS " + Localization.l("main_status_gsmcoverage"), false);

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
				super(lang.getSzName() + " (" + Localization.l("common_click_to_copy_message_to_clipboard") + ")");
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
                    for (LBALanguage _lba_language : _lba_languages) {
                        LBALanguageMenuItem item = new LBALanguageMenuItem(_lba_language);
                        if (!hash_menuitems.containsKey(_lba_language.getLTextpk())) {
                            item.addActionListener(this);
                            item.setActionCommand("act_lbalanguage_selected");
                            //item.setToolTipText(_lba_languages.get(i).getSzText());
                            item.setToolTipText(StatusPanel.createMessageContentTooltipHtml(_lba_language, get_group()));
                            m_pop_lbalanguages.add(item);
                            hash_menuitems.put(_lba_language.getLTextpk(), item);
                        }
                    }
				}
			}
			catch(Exception e)
			{
				log.warn("Failed to set lba languages", e);
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
                m_chk_layers_gsm.setToolTipText(String.format(Localization.l("main_status_click_to_show_coverage"),"GSM"));
                m_chk_layers_umts.setToolTipText(String.format(Localization.l("main_status_click_to_show_coverage"),"UMTS"));
			}
		}
		public void init_ui()
		{
			if(PAS.icon_version==2)
				m_btn_confirm_lba_sending = ULookAndFeel.newUButtonAttention(ImageLoader.load_icon("connect_32.png"));
			else
				m_btn_confirm_lba_sending = new JButton(ImageLoader.load_icon("go_32.png"));
			m_btn_confirm_lba_sending.addActionListener(this);
			m_btn_confirm_lba_sending.setPreferredSize(btn_size);
			m_btn_confirm_lba_sending.setVisible(false);

			//m_btn_confirm_lba_sending.setUI(ULookAndFeel.newUButtonUIAttention(m_btn_confirm_lba_sending));
			
			if(PAS.icon_version==2)
				m_btn_cancel_lba_sending = ULookAndFeel.newUButtonAttention(ImageLoader.load_icon("disconnect_32.png"));
			else
				m_btn_cancel_lba_sending = new JButton(ImageLoader.load_icon("cancel_32.png"));
			m_btn_cancel_lba_sending.addActionListener(this);
			m_btn_cancel_lba_sending.setPreferredSize(btn_size);
			m_btn_cancel_lba_sending.setVisible(false);
            m_btn_cancel_lba_sending.setToolTipText(Localization.l("main_status_cancel_lba_sending"));
			add_controls();

		}
		Dimension btn_size = new Dimension(80,40);
		public IconPanel() {
			//m_btn_goto = new JButton(ImageLoader.load_icon("gnome-searchtool_16x16.jpg"));
			if(PAS.icon_version==2)
				m_btn_goto = new JButton(ImageLoader.load_icon("find_32.png"));
			else
				m_btn_goto = new JButton(ImageLoader.load_icon("search_32.png"));
			m_btn_goto.addActionListener(this);
			m_btn_goto.setPreferredSize(btn_size);
            m_btn_goto.setToolTipText(Localization.l("main_status_show_map_of_sending"));
			
            m_btn_save_area = new JButton(ImageLoader.load_icon("save_area.png"));
            m_btn_save_area.addActionListener(this);
            m_btn_save_area.setPreferredSize(btn_size);
            m_btn_save_area.setToolTipText(Localization.l("main_sending_adr_btn_save_predefined_area"));

			m_chk_layers_gsm.addActionListener(this);
			m_chk_layers_umts.addActionListener(this);
			m_chk_layers_gsm.setActionCommand("act_show_layers_gsm");
			m_chk_layers_umts.setActionCommand("act_show_layers_umts");
			m_chk_layers_gsm.setVisible(false);
			m_chk_layers_umts.setVisible(false);

            m_btn_lbalanguages.setToolTipText(Localization.l("main_status_lba_show_message_content"));
			m_btn_lbalanguages.addActionListener(this);
			m_btn_lbalanguages.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e)
				{
					m_pop_lbalanguages.show(e.getComponent(), 0, m_btn_lbalanguages.getHeight());
				}
			});

		}
		
		public void _SetToolTipText_Confirm(String sz)
		{
            m_btn_confirm_lba_sending.setToolTipText("<html><b>" + Localization.l("common_confirm") + "</b><br>" + sz + "</html>");
            m_btn_cancel_lba_sending.setToolTipText("<html><b>" + Localization.l("common_cancel") + "</b><br>" + sz + "</html>");
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
			m_btn_cancel_lba_sending.putClientProperty(ULookAndFeel.WINDOW_MODIFIED, b);
			m_btn_confirm_lba_sending.putClientProperty(ULookAndFeel.WINDOW_MODIFIED, b);
		}

        public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(m_btn_goto)) {
				Variables.getNavigation().gotoMap(get_shape().calc_bounds());
			} else if(e.getSource().equals(m_btn_cancel_lba_sending))
			{
				//CANCEL
				if(getLBA()!=null)
				{
					log.debug("Cancel LBA");
					new no.ums.pas.core.ws.WSConfirmLBA(getLBA().n_parentrefno, getLBA().sz_jobid, false);
				}
				
			} else if(e.getSource().equals(m_btn_confirm_lba_sending))
			{
				//CONFIRM
				if(getLBA()!=null)
				{
					log.debug("Confirm LBA");
					new no.ums.pas.core.ws.WSConfirmLBA(getLBA().n_parentrefno, getLBA().sz_jobid, true);
				}
				
			}
			else if(e.getSource().equals(m_btn_save_area))
			{
				log.debug("save area button from status view clicked1");
				if(!PAS.get_pas().get_eastcontent().lockFocusToAreasTab())
				{
					try
					{
						MainAreaController mainAreaController = PAS.get_pas().getPredefinedAreaController();
						AreaController areaCtrl = null;
						if(mainAreaController==null)
						{
							PAS.get_pas().initPredefinedAreaController();
							mainAreaController = PAS.get_pas().getPredefinedAreaController();
						}
						areaCtrl = mainAreaController.getAreaCtrl();
						if (areaCtrl == null) {
							areaCtrl = new AreaController(mainAreaController, mainAreaController.getMapNavigation());
						}
		//				AreaVO area = null;
						areaCtrl.setEditMode(false);
						areaCtrl.createNewArea(this,null, false,AreaSource.STATUS);
						areaCtrl.setActiveShape(get_shape());
					}
					catch(Exception ex)
					{
						log.error("error",ex);
					}
				}
			}
			else if("act_save_predefined_area_cancel".equals(e.getActionCommand()))
			{
//				m_btn_save_area.setEnabled(false);
//				log.debug("in status view act_save_predefined_area_cancel called");
			}
			else if("act_save_predefined_area_complete".equals(e.getActionCommand()))
			{
//				m_btn_save_area.setEnabled(false);
//				log.debug("in status view act_save_predefined_area_complete called");
			}
			else if("act_show_layers_gsm".equals(e.getActionCommand()))
			{
                // Disable the current GSM overlay, if present
                PAS.get_pas().get_mappane().removeTileOverlay("GSM");
                final JCheckBox chk = (JCheckBox) e.getSource();
                if (chk.isSelected()) {
                    for (LBASEND lbasend : m_lba_by_operator) {
                        if (lbasend.l_operator == m_filter_status_by_operator) {
                            log.debug("Loading GSM overlay for job=" + lbasend.sz_jobid + " (" + lbasend.sz_operator + ")");
                            PAS.get_pas().get_mappane().putTileOverlay("GSM", new TileLookupImpl(TileCacheGsmCoverage.of(lbasend.sz_jobid)));
                            break;
                        }
                    }
                }
            }
			else if("act_show_layers_umts".equals(e.getActionCommand()))
			{
                // Disable the current UMTS overlay, if present
                PAS.get_pas().get_mappane().removeTileOverlay("UMTS");
                final JCheckBox chk = (JCheckBox) e.getSource();
                if (chk.isSelected()) {
                    for (LBASEND lbasend : m_lba_by_operator) {
                        if (lbasend.l_operator == m_filter_status_by_operator) {
                            log.debug("Loading UMTS overlay for job=" + lbasend.sz_jobid + " (" + lbasend.sz_operator + ")");
                            PAS.get_pas().get_mappane().putTileOverlay("UMTS", new TileLookupImpl(TileCacheUmtsCoverage.of(lbasend.sz_jobid)));
                            break;
                        }
                    }
                }
            }
			else if("act_lbalanguage_selected".equals(e.getActionCommand()))
			{
				try
				{
					LBALanguageMenuItem item = (LBALanguageMenuItem)e.getSource();
					StringSelection text = new StringSelection(item.getLanguage().getSzText());
					Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
					log.debug(item.getLanguage().getSzName());
					clip.setContents(text, text);
				}
				catch(Exception err){
					log.warn("Failed to select lba language", err);
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
			
			add_spacing(DIR_HORIZONTAL, 10);

			set_gridconst(inc_xpanels(), get_panel(), 1, 1);
			add(m_btn_save_area, m_gridconst);
			
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
			boolean enableSaveArea = false;
			try{
				if(get_shape() instanceof PolygonStruct || get_shape() instanceof EllipseStruct)
					enableSaveArea = true;
			} catch(NullPointerException npe) {

			} catch (ClassCastException cce) {

			}

			//check for predefined areas's access
			boolean hasPredefinedAreaAccess = false;
			long storedareas = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_storedareas();
			int phonebook = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().getPhonebook();
//			log.debug("in init storedareas=" + storedareas + ";phonebook="+phonebook);
			if((storedareas > 0) && (phonebook > PredefinedAreaConstants.READ_ONLY))
				hasPredefinedAreaAccess = true;

			enableSaveArea = enableSaveArea && hasPredefinedAreaAccess;
			m_btn_save_area.setVisible(enableSaveArea);
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
			
			Object[] obj = {1000, "Sent ok", "92293390"};
			m_resend_status.insert_row(obj, -1);
			//m_resend_status.start_search();
			//m_resend_status.setBorder(BorderFactory.createRaisedBevelBorder());
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
		private StdTextLabel m_lbl_name		= new StdTextLabel(Localization.l("common_sendingname") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_refno	= new StdTextLabel(Localization.l("common_refno") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_resendrefno = new StdTextLabel(Localization.l("main_status_resent_from_refno") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_created	= new StdTextLabel(Localization.l("common_created") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_sched	= new StdTextLabel(Localization.l("common_scheduled") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_status	= new StdTextLabel(Localization.l("common_sendingstatus") + ":", lbl_width, 11, true);
        private StdTextLabel m_lbl_type		= new StdTextLabel(Localization.l("common_type") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_items	= new StdTextLabel(Localization.l("common_items") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_sms_recipients = new StdTextLabel(Localization.l("common_sms_recipients") + ":", lbl_width, 11, false);
		private StdTextLabel m_lbl_sms_total = new StdTextLabel(Localization.l("common_sms_total") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_proc		= new StdTextLabel(Localization.l("common_processed") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_alloc	= new StdTextLabel(Localization.l("common_allocated") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_maxalloc = new StdTextLabel(Localization.l("common_maxchannels") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_oadc		= new StdTextLabel(Localization.l("common_origin") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_nofax	= new StdTextLabel(Localization.l("common_company_blocklist") + ":", lbl_width, 11, false);
        private StdTextLabel m_lbl_actionprofile = new StdTextLabel(Localization.l("main_sending_settings_msg_profile") + ":", lbl_width, 11, false);
        private JSlider m_progress			= new JSlider(1, 840);
		private JButton m_channels_plus		= new JButton("+");
		private JButton m_channels_minus	= new JButton("-");
		private StdTextLabel m_lbl_setmaxalloc = new StdTextLabel("", 40, 12, true);
		private JButton m_btn_setmaxalloc	= new JButton(String.format("%s %s", Localization.l("common_set"), Localization.l("common_maxchannels")));

        {
            m_btn_setmaxalloc = new JButton(String.format("%s %s", Localization.l("common_set"), Localization.l("common_maxchannels")));
        }

        {
            m_btn_setmaxalloc = new JButton(String.format("%s %s", Localization.l("common_set"), Localization.l("common_maxchannels")));
        }

        {
            m_btn_setmaxalloc = new JButton(String.format("%s %s", Localization.l("common_set"), Localization.l("common_maxchannels")));
        }

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
		private StdTextLabel m_txt_sms_recipients = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_sms_total = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_proc		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_alloc	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_maxalloc = new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_oadc		= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_nofax	= new StdTextLabel("", 250, 11, false);
		private StdTextLabel m_txt_actionprofile = new StdTextLabel("", 250, 11, false);
		public JPopupMenu m_menu_dynfiles = new JPopupMenu();

		private boolean b_is_dynfiles_showing = false;

        private JButton m_btn_resend		= new JButton(Localization.l("main_status_resend"));
		private JButton m_btn_kill			= new JButton(Localization.l("common_kill_sending"));
        public boolean m_b_allocset = false;
		
		public VoicePanel() {
			super();
			m_btn_resend.setEnabled(false);
			m_btn_kill.setEnabled(false);
			setPreferredSize(new Dimension(300, 500));
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
                m_lbl_resendrefno.setText(Localization.l("main_status_new_message_from_refno") + ":");
            }
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
					//Color c = SubstanceLookAndFeel.getActiveColorScheme().getMidColor();
					Color c = SystemColor.controlShadow;
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
			m_lbl_setmaxalloc.setText(Integer.toString(n));
		}
		@Override
		public void componentResized(ComponentEvent e) {
			int w = getWidth()-20;
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
			m_btn_resend.setSize(new Dimension(135, 22));
			m_btn_resend.setPreferredSize(new Dimension(135, 22));
			
			m_btn_kill.setActionCommand("act_kill_sending");
			m_btn_kill.addActionListener(this);
			m_btn_kill.setSize(new Dimension(135, 22));
			m_btn_kill.setPreferredSize(new Dimension(135, 22));
			
			m_progress.setPreferredSize(new Dimension(250, 22));
			
			m_txt_name.setText(get_sendingname());
			m_txt_refno.setText(Integer.toString(get_refno()));
			m_txt_created.setText(TextFormat.format_date(get_createdate()) + " " + TextFormat.format_time(get_createtime(), 4));
			m_txt_sched.setText(TextFormat.format_date(get_scheddate()) + " " + TextFormat.format_time(get_schedtime(), 4));
			m_txt_type.setText(TextFormat.get_sendingtype(get_sendingtype()));
			m_txt_addresstypes.setText(TextFormat.get_addresstypes(get_addresstypes()));
			m_txt_oadc.setText(get_oadc());
			m_progress.setValue(get_maxalloc());
            m_txt_nofax.setText(get_nofax()==1 ? Localization.l("common_yes") : Localization.l("common_no"));
			switch(get_type())
			{
			case 1:
				m_txt_actionprofile.setText(get_actionprofilename());
				m_txt_actionprofile.setForeground(Color.blue);
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
				break;
			case 2:
				remove(m_txt_actionprofile);
				remove(m_lbl_actionprofile);
				//m_txt_actionprofile.setText("");
				//m_lbl_actionprofile.setText("");
				break;
			}

            JMenuItem menuitem = new JMenuItem(new ShowProfileAction(new Supplier<Integer>() {
                @Override
                public Integer get() {
                    return _n_profilepk;
                }
            }));
            String face = UIManager.getString("Common.Fontface");
			Font font = new Font(face, Font.BOLD, 12);
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
                }
				public void mouseExited(MouseEvent e) {
                    //m_menu_dynfiles.setVisible(false);
					//b_is_dynfiles_showing = false;
				}				
			});
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
						log.warn(err.getMessage(), err);
					}
				}
				else if("act_download_finished".equals(e.getActionCommand()))
				{
					try
					{
                        SoundlibFileWav f = (SoundlibFileWav)e.getSource();
						if(player!=null)
							player.getPlayer().stop();
						if(f.get_file().getPath()!=null)
						{
							player = new SoundPlayer(f.get_file().getPath(), true);
							player.getPlayer().play();
						}
					}
					catch(Exception err)
					{
						log.warn(err.getMessage(), err);
					}
					
				}
				
			}

			@Override
			public void add_controls() {
                StdTextLabel lblfile = new StdTextLabel(Localization.l("common_file") + " " + (fileno));
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
					if(pnl_cell!=null)
						pnl_cell.setVisible(true);
					if(pnl_voice!=null)
						pnl_voice.setVisible(false);
					if(m_btn_kill!=null)
						m_btn_kill.setVisible(false);
				}
				else
				{
					if(pnl_cell!=null)
					{
						pnl_cell.setVisible(false);
					}
					if(pnl_voice!=null)
					{
						pnl_voice.setVisible(true);
					}
					m_btn_kill.setVisible(true);
					if(get_sendingstatus()==7) {
						enableResend();
					} 
					boolean b_send_rights = Variables.getUserInfo().get_departments().getDepartment(get_deptpk()).get_userprofile().get_send()>0;
					m_btn_kill.setEnabled(get_sendingstatus()>0 && get_sendingstatus()<7 && !StatusSending.this.isMarkedAsCancelled() && !isCancelRequestSent() && b_send_rights);
					m_btn_kill.setVisible(get_sendingstatus()>0 && get_sendingstatus()<7 && !StatusSending.this.isMarkedAsCancelled() && !isCancelRequestSent() && b_send_rights);
				}
			}
			catch(Exception e)
			{
				log.warn("Failed to update ui", e);
			}
			
			// Sjekker om brukeren har rettigheter for å sende
			if(!PAS.get_pas().get_rightsmanagement().cansend() && !PAS.get_pas().get_rightsmanagement().cansimulate()) {
				m_btn_resend.setEnabled(false);
				m_progress.setEnabled(false);
			}
			
			
			
			m_lbl_resendrefno.setVisible((get_resendrefno() > 0));
			m_txt_resendrefno.setVisible((get_resendrefno() > 0));
			m_txt_resendrefno.setText(Integer.toString(get_resendrefno()));
				
			m_txt_status.setText(TextFormat.get_statustext_from_code(get_sendingstatus(), get_altjmp(), isMarkedAsCancelled()));
			m_txt_queuestatus.setText("");
			
			if(get_type() == 2) { // SMS
				int part = 1;
				if(no.ums.pas.ums.tools.Utils.get_gsmsize(get_statussending().get_sms_message_text())>160)
					part = (int)Math.ceil(no.ums.pas.ums.tools.Utils.get_gsmsize(get_statussending().get_sms_message_text())/(double)(160-8));
 
				m_txt_sms_recipients.setText(Integer.toString(part));
				m_txt_sms_total.setText(Integer.toString((get_totitem() * part)));
			}
			
			m_txt_items.setText(Integer.toString(get_totitem()));
			m_txt_proc.setText(Integer.toString(get_proc()));
			m_txt_alloc.setText(Integer.toString(get_alloc()));
			if(get_oadc().trim().length() < 1)
				m_txt_oadc.setText(Localization.l("main_status_oadc_hidden"));
			else
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
                    log.warn("Failed to iterate over municipals", e);
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
				if(PAS.get_pas().get_userinfo().get_departments().get(i).get_deptpk() == get_deptpk())
					m_progress.setMaximum(PAS.get_pas().get_userinfo().get_departments().get(i).get_maxalloc());
			}
			m_txt_maxalloc.setText(Integer.toString(get_maxalloc()));
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
                tmp = Localization.l("main_status_voicesending");
				break;
			case 2:
                tmp = Localization.l("main_status_smssending");
				if(PAS.get_pas().get_userinfo().get_current_department().get_userprofile().get_sms() == 0)
					m_btn_resend.setEnabled(false);
				_ShowMaxAlloc(false);
				break;
			}
            setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + tmp + "    " + (isSimulation() ? String.format("[%s] ", Localization.l("main_sending_simulated")) : String.format("[%s] ", Localization.l("main_sending_live")))));
			
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
					log.debug(err.getMessage());
					log.warn(err.getMessage(), err);
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
				if((m_this.get_addresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) > 0)
					m_this.set_resend_addresstypes(m_this.get_addresstypes() - SendController.SENDTO_CELL_BROADCAST_TEXT);
				else
					m_this.set_resend_addresstypes(m_this.get_addresstypes());
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
			else if("act_kill_sending".equals(e.getActionCommand()))
			{
				String szConfirmHeader = String.format(Localization.l("common_kill_sending_are_you_sure"), StatusListObject.getChannel(get_type()));
				
				//ask first
				StringBuilder szAsk = new StringBuilder();
				
				szAsk.append("<html>");
				
				//test if there are more active sendings in this project
				if(!Variables.getStatusController().allSendingsFinished(StatusSending.this))
				{
					szAsk.append("<font color=red>");
					szAsk.append(Localization.l("common_kill_sending_still_active_channels"));
					szAsk.append("</font>");
					szAsk.append("<br>");
					szAsk.append("<table>");
					for(StatusSending ss : Variables.getStatusController().getNonFinalSendings(StatusSending.this))
					{
						szAsk.append("<tr><td>");
						szAsk.append(ss.get_refno());
						szAsk.append("</td><td>");
						szAsk.append(StatusListObject.getChannel(ss.get_type()));
						szAsk.append("</td><td>");
						szAsk.append(String.format("\"%s\"", ss.get_sendingname()));
						szAsk.append("</td></tr>");
					}
					szAsk.append("</table>");
					szAsk.append("<br><br>");
				}
				
				szAsk.append("<font color=red>");
				szAsk.append(szConfirmHeader); //String.format(Localization.l("common_kill_sending_are_you_sure"), StatusListObject.getChannel(get_type())));
				szAsk.append("</font>");
				szAsk.append("<br>");
				szAsk.append("<table>");
				szAsk.append("<tr><td>");
				szAsk.append(get_refno());
				szAsk.append("</td><td>");
				szAsk.append(StatusListObject.getChannel(get_type()));
				szAsk.append("</td><td>");
				szAsk.append(String.format("\"%s\"", get_sendingname()));
				szAsk.append("</td></tr>");
				szAsk.append("</table>");
				

				szAsk.append("</html>");
				
				if(JOptionPane.showConfirmDialog(this, szAsk, szConfirmHeader, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					setCancelRequestSent(true);
					m_btn_kill.setEnabled(false);

					new WSCancelSending(get_refno(), new ICallback() {
						@Override
						public void onFinished(UCancelSendingResponse response) {
							switch(response.getResponse())
							{
							case OK:
								m_btn_kill.setEnabled(false);
								break;
							case ALREADY_MARKED_AS_CANCELLED:
							case ERROR:
							case FAILED_USER_RESTRICTED:
								JOptionPane.showMessageDialog(m_btn_kill, Localization.l("common_kill_sending_failed") + "\n" + response.getResponse().toString(), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
								m_btn_kill.setEnabled(true);
								break;
							}
							setCancelRequestSent(false);
						}
					}).execute();
				}
			}
		}
		public void add_controls() {
			//m_gridconst.fill = GridBagConstraints.BOTH;
			
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
			if(get_sendingtype()!= SendProperties.SENDING_TYPE_TAS_COUNTRY_) {
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
			
			if(get_type() == 2) { // SMS
				set_gridconst(0, inc_panels(), text_1_x, 1);
				add(m_lbl_sms_recipients, m_gridconst);
				set_gridconst(text_1_x, get_panel(), 6, 1);
				add(m_txt_sms_recipients, m_gridconst);
				
				set_gridconst(0, inc_panels(), text_1_x, 1);
				add(m_lbl_sms_total, m_gridconst);
				set_gridconst(text_1_x, get_panel(), 6, 1);
				add(m_txt_sms_total, m_gridconst);
			}
			
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
			add_spacing(DIR_VERTICAL, 20);
			
			set_gridconst(0, inc_panels(), 2, 1);
			add(m_btn_resend, m_gridconst);
			
			set_gridconst(2, get_panel(), 4, 1);
			add(m_btn_kill, m_gridconst);
			
			//Border voice = BorderFactory.createTitledBorder("Voice Broadcast");
            setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(String.format(" %s    ", Localization.l("main_status_voicesending")) + (isSimulation() ? String.format("[%s] ", Localization.l("main_sending_simulated")) : String.format("[%s]", Localization.l("main_sending_live")))));
			
			init();
		}
		public void init() {
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights() == 4)
				m_btn_resend.setVisible(false);
			setVisible(true);
		}

    }
	
	public class CellPanel extends DefaultPanel implements ComponentListener {
		
		public static final long serialVersionUID = 1;
		private StdTextLabel m_lbl_refno = new StdTextLabel(Localization.l("common_refno") + ":", 150, 11, true);
        private StdTextLabel m_lbl_operator = new StdTextLabel(Localization.l("common_operator") + ":", 150, 11, true);
        private StdTextLabel m_lbl_sendingstatus = new StdTextLabel(Localization.l("common_sendingstatus") + ":", 150, 11, true);
        private StdTextLabel m_lbl_items = new StdTextLabel(Localization.l("main_status_subscribers") + ":", 150, 11, false);
        private StdTextLabel m_lbl_processed = new StdTextLabel(Localization.l("common_processed") + ":", 150, 11, false);
        private StdTextLabel m_lbl_queued = new StdTextLabel(Localization.l("common_sending") + ":", 150, 11, false);
        private StdTextLabel m_lbl_failed = new StdTextLabel(Localization.l("main_status_failed") + ":", 150, 11, false);
        private StdTextLabel m_lbl_delivered = new StdTextLabel(Localization.l("main_status_delivered") + ":", 150, 11, false);
        private StdTextLabel m_lbl_expired = new StdTextLabel(Localization.l("main_status_expired") + ":", 150, 11, false);
        private StdTextLabel m_lbl_expires_ts = new StdTextLabel(Localization.l("main_status_lba_expires_timestamp") + ": ", 150, 11, false);
        private StdTextLabel m_txt_refno = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_sendingstatus = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_items = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_processed = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_queued = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_failed = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_delivered = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_operator = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_expired = new StdTextLabel("", 150, 11, false);
		private StdTextLabel m_txt_expires_ts = new StdTextLabel("", 150, 11, false);
		
		private StdTextLabel [] m_lbl_status_ts = new StdTextLabel[7];
		private StdTextLabel [] m_txt_status_ts = new StdTextLabel[7];
		//private StdTextLabel m_lbl_status_cc = new StdTextLabel("GSM numbers from ", 150, 12, true);
		private JTextArea m_txt_status_cc = new JTextArea(" ", 1, 1);
		private JButton m_btn_tas_resend;
		JScrollPane m_scroll_cc;
		
		
		
		public CellPanel() {
			super();
			setPreferredSize(new Dimension(300, 850));
			
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

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_refno, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_refno, m_gridconst);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_expires_ts, m_gridconst);
			set_gridconst(1, get_panel(), 2, 1);
			add(m_txt_expires_ts, m_gridconst);

			
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

            m_btn_tas_resend = new JButton(Localization.l("main_status_resend"));
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
                m_txt_operator.setText(Localization.l("main_status_lba_nationalities"));
                if(m_lba!=null)
                	m_txt_expires_ts.setText(TextFormat.format_datetime(m_lba.l_expires_ts));
				break;
			default:
			{
				String sz_operator = "";
                for (LBASEND temp : m_lba_by_operator) {
                    if (temp.l_operator == m_filter_status_by_operator) {
                        sz_operator = temp.sz_operator;
                        m_txt_expires_ts.setText(TextFormat.format_datetime(temp.l_expires_ts));
                        break;
                    }
                }
				m_txt_operator.setText(sz_operator);
			}
				break;
			}
			m_txt_refno.setText(String.valueOf(get_refno()));
			m_txt_sendingstatus.setText(get_lba_sendingstatusS());// + " " + m_lba.send_ts.get(m_lba.send_ts.size()-1).sz_timestamp);
			m_txt_sendingstatus.setToolTipText(get_lba_sendingstatusS());
			m_txt_delivered.setText(String.valueOf(get_lba_delivered()));
			m_txt_queued.setText(String.valueOf(get_lba_queued()));
			m_txt_failed.setText(String.valueOf(get_lba_failed()));
			m_txt_expired.setText(String.valueOf(get_lba_expired()));
            m_txt_processed.setText((get_lba_processed() >= 0 ? String.valueOf(get_lba_processed()) : Localization.l("common_na")));
            m_txt_items.setText((get_lba_items() >= 0 ? String.valueOf(get_lba_items()) : Localization.l("common_na")));
			
			if(m_filter_status_by_operator > -1 && m_lba.n_status > 42000) {
				m_btn_tas_resend.setVisible(true);
				m_btn_tas_resend.setEnabled(true);
			}
			else {
				m_btn_tas_resend.setVisible(false);
				m_btn_tas_resend.setEnabled(false);
			}
			
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

            for (LBASEND aM_lba_by_operator : m_lba_by_operator) {
                if (aM_lba_by_operator.n_status == LBASEND.LBASTATUS_PREPARED_CELLVISION ||
                        aM_lba_by_operator.n_status == LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE) {
                }
            }
			pnl_icon.ShowConfirmAndCancel(m_lba.IsPrepared());
			setNeedAttention(m_lba.IsPrepared());

			int n_total_items = 0;
			if(getLBA()!=null)
			{
                String sz_operators = "";
                for (LBASEND aM_lba_by_operator : m_lba_by_operator) {
                    //list up all operators ready for confirmation
                    sz_operators += "<b>";
                    if (aM_lba_by_operator.n_status == LBASEND.LBASTATUS_PREPARED_CELLVISION || aM_lba_by_operator.n_status == LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE) {
                        sz_operators += aM_lba_by_operator.sz_operator + " - " + Localization.l("common_ready") + "<br>";
                        n_total_items += (aM_lba_by_operator.n_items > 0 ? aM_lba_by_operator.n_items : 0);
                    } else if (aM_lba_by_operator.n_status < LBASEND.LBASTATUS_PREPARED_CELLVISION) {
                        sz_operators += aM_lba_by_operator.sz_operator + " - " + Localization.l("common_not_ready") + "<br>";
                    } else if (aM_lba_by_operator.n_status > LBASEND.LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE)
                        sz_operators += aM_lba_by_operator.sz_operator + " - (" + LBASEND.LBASTATUSTEXT(aM_lba_by_operator.n_status) + ")<br>";
                    sz_operators += "</b>";
                }
				String sz_sendtext = "Unknown";
				switch(getLBA().f_simulation)
				{
				case 1:
                    sz_sendtext = Localization.l("main_sending_simulated").toUpperCase();
					break;
				case 0:
                    sz_sendtext = Localization.l("main_sending_live").toUpperCase();
					break;
				case 2:
                    sz_sendtext = Localization.l("common_silent").toUpperCase() + " " + Localization.l("main_sending_live").toUpperCase();
					break;
				}
                pnl_icon._SetToolTipText_Confirm(Localization.l("main_status_locationbased_alert_short") + "&nbsp;" + sz_sendtext + "&nbsp;" + Localization.l("common_to") + "&nbsp;" + n_total_items + "&nbsp;" + Localization.l("main_sending_recipients") + "<br><br>" + sz_operators);
			}

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
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    get_uipanel().get_status_sending().getTotalSendingnameLabel().putClientProperty(ULookAndFeel.WINDOW_MODIFIED, b);
                    get_uipanel().get_status_sending().getTotalSendingnameLabel().putClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED, b);
                }
            });
		}

		public void init() {
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			// Resend
			if(_n_type == SendProperties.SENDING_TYPE_TAS_COUNTRY_)
				new WSTasResend(this,get_refno(),m_lba.l_operator);
			else {
				m_this.set_resend_addresstypes(SendController.SENDTO_CELL_BROADCAST_TEXT);
				PAS.get_pas().get_sendcontroller().actionPerformed(new ActionEvent(m_this, ActionEvent.ACTION_PERFORMED, "act_resend"));
			}

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
			m_lba_tabbed.setPreferredSize(new Dimension(w, h-240));
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
		private StdTextLabel m_lbl_message = new StdTextLabel(Localization.l("main_sending_lba_message") + ":", 150,11,true);
        private JTextArea 	 m_txt_message = new JTextArea("",1,1);

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
