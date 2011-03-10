package no.ums.pas.status;

import no.ums.pas.localization.Localization;
import no.ums.ws.common.LBALanguage;

import javax.swing.JProgressBar;
import java.util.ArrayList;
import java.util.Hashtable;

/*
 * contains statistics of LBA sending and lists containing stats pr. CC and stats pr. mobile cell
 */
public class LBASEND
{
	
	//timestamp class for statuscodes
	public class LBASEND_TS implements Comparable<LBASEND_TS>
	{
		@Override
		public int compareTo(LBASEND_TS o) {
			if(Long.parseLong(this.n_timestamp) > Long.parseLong(o.n_timestamp))
				return 1;
			else return -1;
		}
		
		public int n_status;
		public String n_timestamp;
		public String sz_timestamp;
		public String sz_status;
		public int l_operator;
		public String sz_operator;
		public LBASEND_TS(int n_status, String n_timestamp, int l_operator)
		{
			this.n_timestamp = n_timestamp;
			this.n_status = n_status;
			sz_timestamp = no.ums.pas.ums.tools.TextFormat.format_datetime(n_timestamp);
			sz_status = LBASTATUS.get(new Integer(n_status));
			this.l_operator = l_operator;
		}
		public LBASEND_TS(String [] v)
		{
			this(Integer.valueOf(v[0]), v[1], Integer.valueOf(v[2]));
		}
	}
	/*public static String [] LBASTATUSTEXT = {
		"Sent to LBA Server (UMS)",
		"N/A",
		"N/A",
		"Parsing (UMS)",
		"Preparing Area (CellVision)",
		"Ready for confirmation (you need to confirm to send this message)",
		"Sending",
		"Finished",
	};*/
	/*public class LBASTATUS
	{
		public int n_status;
		public String sz_status;
		public LBASTATUS(int status, String sz)
		{
			n_status = status;
			sz_status = sz;
		}
	}*/
	
	/*
	 * errorcodes for PAS are set to
	 * 40.000+ => PAS GUI
	 * 41.000+ => PAS WebServices
	 * 42.000+ => LBAS Server
	 */
	
	public static final int LBASTATUS_DUMMY_OPERATOR = -999;
	public static final int LBASTATUS_GENERAL_ERROR = -2;

	public static final int LBASTATUS_INITED = 100;
	public static final int LBASTATUS_COULD_NOT_PUBLISH_LBA_FILE = 41100;
	public static final int LBASTATUS_SENT_TO_LBA = 199;
	public static final int LBASTATUS_PARSING_LBAS = 200;
	public static final int LBASTATUS_PARSING_LBAS_FAILED_TO_SEND = 290;
	
	public static final int LBASTATUS_PREPARING_CELLVISION = 300;
	public static final int LBASTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 305;
	public static final int LBASTATUS_PREPARED_CELLVISION = 310; // denne settes av LBA server dersom l_Requesttype=1
	public static final int LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 311; //denne settes av LBA server dersom l_Requesttype=1 og CC statistikk er ferdig
	public static final int LBASTATUS_CONFIRMED_BY_USER = 320;
	public static final int LBASTATUS_CANCELLED_BY_USER = 330;
	public static final int LBASTATUS_SENDING = 340;
	
	public static final int TASSTATUS_PREPARING_CELLVISION = 400;
	public static final int TASSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 405;
	public static final int TASSTATUS_PREPARED_CELLVISION = 410; // denne settes av TAS server dersom l_Requesttype=1
	public static final int TASSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 411; //denne settes av TAS server dersom l_Requesttype=1 og CC statistikk er ferdig
	public static final int TASSTATUS_CONFIRMED_BY_USER = 420;
	public static final int TASSTATUS_CANCELLED_BY_USER = 430;
	public static final int TASSTATUS_SENDING = 440;

	public static final int CBSTATUS_PREPARING_CELLVISION = 500;
	public static final int CBSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION = 505;
	public static final int CBSTATUS_PREPARED_CELLVISION = 510; // denne settes av TAS server dersom l_Requesttype=1
	public static final int CBSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE = 511; //denne settes av TAS server dersom l_Requesttype=1 og CC statistikk er ferdig
	public static final int CBSTATUS_CONFIRMED_BY_USER = 520;
	public static final int CBSTATUS_CANCELLED_BY_USER = 530;
	public static final int CBSTATUS_SENDING = 540;
	public static final int CBSTATUS_PAUSED = 590;

	
	public static final int LBASTATUS_CANCEL_IN_PROGRESS = 800;
	public static final int LBASTATUS_FINISHED = 1000;
	public static final int LBASTATUS_CANCELLED = 2000;
	public static final int LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM = 2001;
	public static final int LBASTATUS_CANCELLED_AFTER_LOOKUP = 2002;
	
	public static final int LBASTATUS_EXCEPTION_EXECUTE_AREAALERT = 42001;
	public static final int LBASTATUS_EXCEPTION_PREPARE_AREAALERT = 42002;
	public static final int LBASTATUS_EXCEPTION_EXECUTE_CUSTOMALERT = 42003;
	public static final int LBASTATUS_EXCEPTION_PREPARE_CUSTOMALERT = 42004;
	public static final int LBASTATUS_EXCEPTION_EXECUTE_PREPARED_ALERT = 42005;
	public static final int LBASTATUS_EXCEPTION_CANCEL_PREPARED_ALERT = 42006;
	
	public static final int TASSTATUS_EXCEPTION_EXECUTE_INT_ALERT = 42007;
	public static final int TASSTATUS_EXCEPTION_PREPARE_INT_ALERT = 42008;
	
	public static final int LBASTATUS_FAILED_EXECUTE_AREAALERT = 42011;
	public static final int LBASTATUS_FAILED_PREPARE_AREAALERT = 42012;
	public static final int LBASTATUS_FAILED_EXECUTE_CUSTOMALERT = 42013;
	public static final int LBASTATUS_FAILED_PREPARE_CUSTOMALERT = 42014;
	public static final int LBASTATUS_FAILED_EXECUTE_PREPARED_ALERT = 42015;
	public static final int LBASTATUS_FAILED_CANCEL_PREPARED_ALERT = 42016;
	
	public static final int TASSTATUS_FAILED_EXECUTE_INT_ALERT = 42017;
	public static final int TASSTATUS_FAILED_PREPARE_INT_ALERT = 42018;
	
	public static final int LBASTATUS_MISSING_TAG_TEXTMESSAGES = 42101;
	public static final int LBASTATUS_MISSING_ATTRIBUTE_AREANAME = 42102;
	public static final int LBASTATUS_MISSING_TAG_POLYGON_ELLIPSE = 42103;
	public static final int LBASTATUS_GET_ALERT_MESSAGE_EXCEPTION = 42104;
	public static final int LBASTATUS_GET_ALERT_MESSAGE_FAILED_MISSING_CC_TAG = 42105;
	
	public static final int CELLVISION_JOB_STATUS_ERROR = 42201;
		
	public static String LBASTATUSTEXT(int n_code)
	{
		String sz = LBASTATUS.get(new Integer(n_code));
		if(sz == null || sz.length()==0)
			sz = "N/A ["+ n_code + "]";
		return sz;
	}
	public final static Hashtable<Integer, String> LBASTATUS = new Hashtable<Integer, String>();
	public static void CreateLbaStatusHash()
	{
        LBASTATUS.put(LBASTATUS_INITED, String.format(Localization.l("main_statustext_lba_inited"), "UMS"));
        LBASTATUS.put(LBASTATUS_SENT_TO_LBA, Localization.l("main_statustext_lba_sent_to_server"));
        LBASTATUS.put(LBASTATUS_PARSING_LBAS, Localization.l("main_statustext_lba_parsing"));
        LBASTATUS.put(LBASTATUS_PARSING_LBAS_FAILED_TO_SEND, Localization.l("main_statustext_lba_service_not_available"));

        LBASTATUS.put(LBASTATUS_PREPARING_CELLVISION, String.format(Localization.l("main_statustext_lba_preparing_operator"), "Operator"));
        LBASTATUS.put(LBASTATUS_PREPARED_CELLVISION, String.format(Localization.l("main_statustext_lba_prepared_operator"), "Operator"));
        LBASTATUS.put(LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE, String.format(Localization.l("main_statustext_lba_prepared_operator_count_complete"), "Operator"));
        LBASTATUS.put(LBASTATUS_PROCESSING_SUBSCRIBERS_CELLVISION, String.format(Localization.l("main_statustext_lba_processing_subscribers"), "Operator"));
        LBASTATUS.put(LBASTATUS_CONFIRMED_BY_USER, Localization.l("main_statustext_lba_confirmed_by_user"));
        LBASTATUS.put(LBASTATUS_CANCELLED_BY_USER, Localization.l("main_statustext_lba_cancelled_by_user"));
        LBASTATUS.put(LBASTATUS_SENDING, Localization.l("main_statustext_lba_sending"));

        LBASTATUS.put(TASSTATUS_PREPARING_CELLVISION, String.format(Localization.l("main_statustext_lba_preparing_operator"), "Operator"));
        LBASTATUS.put(TASSTATUS_PREPARED_CELLVISION, String.format(Localization.l("main_statustext_lba_prepared_operator"), "Operator"));
        LBASTATUS.put(TASSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE, String.format(Localization.l("main_statustext_lba_prepared_operator_count_complete"), "Operator"));
        LBASTATUS.put(TASSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION, String.format(Localization.l("main_statustext_lba_processing_subscribers"), "Operator"));
        LBASTATUS.put(TASSTATUS_CONFIRMED_BY_USER, Localization.l("main_statustext_lba_confirmed_by_user"));
        LBASTATUS.put(TASSTATUS_CANCELLED_BY_USER, Localization.l("main_statustext_lba_cancelled_by_user"));
        LBASTATUS.put(TASSTATUS_SENDING, Localization.l("main_statustext_lba_sending"));

        LBASTATUS.put(LBASTATUS_CANCEL_IN_PROGRESS, Localization.l("main_statustext_lba_cancel_in_progress"));
        LBASTATUS.put(LBASTATUS_FINISHED, Localization.l("common_finished"));
        LBASTATUS.put(LBASTATUS_CANCELLED, Localization.l("common_cancelled"));
        LBASTATUS.put(LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM, Localization.l("main_statustext_lba_cancelled_by_user_or_system"));
        LBASTATUS.put(LBASTATUS_CANCELLED_AFTER_LOOKUP, Localization.l("common_cancelled"));
        LBASTATUS.put(LBASTATUS_COULD_NOT_PUBLISH_LBA_FILE, Localization.l("main_statustext_lba_ws_error"));

        LBASTATUS.put(LBASTATUS_EXCEPTION_EXECUTE_AREAALERT, Localization.l("main_statustext_ex_areaalert"));
        LBASTATUS.put(LBASTATUS_EXCEPTION_PREPARE_AREAALERT, Localization.l("main_statustext_ex_areaalert_prepare"));
        LBASTATUS.put(LBASTATUS_EXCEPTION_EXECUTE_CUSTOMALERT, Localization.l("main_statustext_ex_customalert"));
        LBASTATUS.put(LBASTATUS_EXCEPTION_PREPARE_CUSTOMALERT, Localization.l("main_statustext_ex_customalert_prepare"));

        LBASTATUS.put(LBASTATUS_EXCEPTION_EXECUTE_PREPARED_ALERT, Localization.l("main_statustext_ex_preparedalert"));
        LBASTATUS.put(LBASTATUS_EXCEPTION_CANCEL_PREPARED_ALERT, Localization.l("main_statustext_ex_cancel_preparedalert"));

        LBASTATUS.put(TASSTATUS_EXCEPTION_EXECUTE_INT_ALERT, Localization.l("main_statustext_ex_intalert_execute"));
        LBASTATUS.put(TASSTATUS_EXCEPTION_PREPARE_INT_ALERT, Localization.l("main_statustext_ex_intalert_prepare"));

        LBASTATUS.put(TASSTATUS_FAILED_EXECUTE_INT_ALERT, Localization.l("main_statustext_fail_intalert_execute"));
        LBASTATUS.put(TASSTATUS_FAILED_PREPARE_INT_ALERT, Localization.l("main_statustext_fail_intalert_prepare"));

        LBASTATUS.put(LBASTATUS_FAILED_EXECUTE_AREAALERT, Localization.l("main_statustext_fail_areaalert_execute"));
        LBASTATUS.put(LBASTATUS_FAILED_PREPARE_AREAALERT, Localization.l("main_statustext_fail_areaalert_prepare"));
        LBASTATUS.put(LBASTATUS_FAILED_EXECUTE_CUSTOMALERT, Localization.l("main_statustext_fail_customalert_execute"));
        LBASTATUS.put(LBASTATUS_FAILED_PREPARE_CUSTOMALERT, Localization.l("main_statustext_fail_customalert_prepare"));
        LBASTATUS.put(LBASTATUS_FAILED_EXECUTE_PREPARED_ALERT, Localization.l("main_statustext_fail_preparedalert_execute"));
        LBASTATUS.put(LBASTATUS_FAILED_CANCEL_PREPARED_ALERT, Localization.l("main_statustext_fail_preparedalert_cancel"));

        LBASTATUS.put(LBASTATUS_MISSING_TAG_TEXTMESSAGES, Localization.l("main_statustext_missing_tag_textmessages"));
        LBASTATUS.put(LBASTATUS_MISSING_ATTRIBUTE_AREANAME, Localization.l("main_statustext_missing_attribute_areaname"));
        LBASTATUS.put(LBASTATUS_MISSING_TAG_POLYGON_ELLIPSE, Localization.l("main_statustext_missing_tag_polygon_ellipse"));
        LBASTATUS.put(LBASTATUS_GET_ALERT_MESSAGE_EXCEPTION, Localization.l("main_statustext_ex_get_alert_message"));
        LBASTATUS.put(LBASTATUS_GET_ALERT_MESSAGE_FAILED_MISSING_CC_TAG, Localization.l("main_statustext_fail_get_alert_msg_m_cc"));

        LBASTATUS.put(CELLVISION_JOB_STATUS_ERROR, Localization.l("main_statustext_fail_cellvision_job_error"));
	
	}
	
	public boolean NotYetPrepared() {
		return (!HasFailedStatus() &&
			!HasFinalStatus() &&
			n_status<LBASTATUS_PREPARED_CELLVISION);
	}
	
	public boolean IsPrepared() {
		return (n_status==LBASTATUS_PREPARED_CELLVISION || n_status==LBASTATUS_PREPARED_CELLVISION_COUNT_COMPLETE);
	}
	
	public boolean HasFinalStatus(){
		if(n_status==LBASTATUS_FINISHED)
			return true;
		else if(n_status==LBASTATUS_CANCELLED)
			return true;
		else if(n_status>=42000)
			return true;
		return false;
	}
	public boolean HasFailedStatus()
	{
		if(n_status>=42000)
		{
			return true;
		}
		return false;
	}
	
	
	public int n_parentrefno;
	public int n_cbtype;
	public int n_status;
	public int n_response;
	public int n_items;
	public int n_proc;
	public int n_retries;
	public int n_requesttype;
	public String sz_jobid;
	public String sz_areaid;
	public int f_simulation;
	public int l_operator;
	public String sz_operator;
	
	protected int n_delivered = 0;
	protected int n_failed = 0;
	protected int n_queued = 0;
	protected int n_cancelled = 0;
	protected int n_expired = 0;
	
	public int getDelivered() { return n_delivered; }
	public int getFailed() { return n_failed; }
	public int getQueued() { return n_queued; }
	public int getCancelled() { return n_cancelled; }
	public int getExpired() { return n_expired; }
	
	/*parse through detailed stats and populate protected vars*/
	public void CalcStatistics()
	{
		n_delivered = 0;
		n_failed = 0;
		n_queued = 0;
		n_expired = 0;
		if(HasFailedStatus())
			n_failed = n_items;
		switch(n_status)
		{
		case LBASTATUS_CANCELLED:
		case LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM:
			n_cancelled = n_items;
			return;
		}
		for(int i=0; i < hist_cc.size(); i++)
		{
			LBAHISTCC c = hist_cc.get(i);
			n_delivered += c.l_delivered;
			//n_failed += c.l_failed + c.l_unknown; // This was before l_unknown was added in addtohistcc
			n_failed += c.l_failed;
			n_expired += c.l_expired;
			n_queued += c.l_queued + c.l_submitted;
		}
	}
	
	public ArrayList<LBAHISTCC> hist_cc = new ArrayList<LBAHISTCC>();
	public ArrayList<LBAHISTCELL> hist_cell = new ArrayList<LBAHISTCELL>();
	public ArrayList<LBASEND_TS> send_ts = new ArrayList<LBASEND_TS>();
	public ArrayList<LBALanguage> languages = null;
	
	public LBASEND(String [] v)
	{
		try
		{
			n_parentrefno = new Integer(v[0]).intValue();
			n_cbtype = new Integer(v[1]).intValue();
			n_status = new Integer(v[2]).intValue();
			n_response = new Integer(v[3]).intValue();
			n_items = new Integer(v[4]).intValue();
			n_proc = new Integer(v[5]).intValue();
			n_retries = new Integer(v[6]).intValue();
			n_requesttype = new Integer(v[7]).intValue();
			sz_jobid = v[8];
			sz_areaid = v[9];
			f_simulation = new Integer(v[10]).intValue();
			l_operator = new Integer(v[11]).intValue();
			sz_operator = v[12];
		}
		catch(Exception e)
		{
			
		}
	}
	public LBASEND()
	{
		n_parentrefno = -1;
		n_cbtype = -1;
		n_status = -1;
		n_response = -1;
		n_items = 0;
		n_proc = 0;
		n_retries = 0;
		n_requesttype = 0;
		sz_jobid = "";
		sz_areaid = "";
		f_simulation = -1;
		l_operator = -1;
	}
	
	public void addLBALanguage(LBALanguage lang)
	{
		if(languages==null)
			languages = new ArrayList<LBALanguage>();
		languages.add(lang);
	}
	
	public void addHistCC(String [] v)
	{
		hist_cc.add(new LBAHISTCC(v));
	}
	public void addHistCell(String [] v)
	{
		hist_cell.add(new LBAHISTCELL(v));
	}
	public void addSendTS(String [] v)
	{
		send_ts.add(new LBASEND_TS(v));
	}
	public void addToHistCC(LBAHISTCC cc)
	{
		boolean found = false;
		
		//check if this cc is already added
		for(int i=0; i < hist_cc.size(); i++)
		{
			LBAHISTCC temp = hist_cc.get(i);
			if(temp.l_ccode==cc.l_ccode)
			{
				temp.l_delivered += cc.l_delivered;
				temp.l_expired += cc.l_expired;
				temp.l_failed += cc.l_failed;
				temp.l_queued += cc.l_queued;
				temp.l_submitted += cc.l_submitted;
				temp.l_subscribers += cc.l_subscribers;
				temp.l_unknown += cc.l_unknown;
				//temp.l_failed = temp.l_failed + temp.l_unknown;
				found = true;
				break;
			}
		}
		if(!found)
		{
			LBAHISTCC temp = new LBAHISTCC();
			temp.l_ccode = cc.l_ccode;
			temp.l_delivered = cc.l_delivered;
			temp.l_expired = cc.l_expired;
			temp.l_failed = cc.l_failed;
			temp.l_queued = cc.l_queued;
			temp.l_submitted = cc.l_submitted;
			temp.l_subscribers = cc.l_subscribers;
			temp.l_unknown = cc.l_unknown;
			//temp.l_failed = temp.l_failed + temp.l_unknown;
			temp.sz_country = no.ums.pas.cellbroadcast.CountryCodes.getCountryByCCode(String.valueOf(temp.l_ccode)).getCountry();
			hist_cc.add(temp);
		}
	}
	public void addHistCell()
	{
		
	}
	
	public class LBAHISTCC extends Object implements Cloneable
	{
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
		public String sz_country;
		public int l_ccode;
		public int l_delivered;
		public int l_expired;
		public int l_failed;
		public int l_unknown;
		public int l_submitted;
		public int l_queued;
		public int l_subscribers;
		public int l_operator;
		public String toString()
		{
			return sz_country;
		}
		
		public Object [] getListRow() {
			Object [] obj = new Object[8];
			obj[0] = this;
			obj[1] = new Integer(l_ccode);
			obj[2] = new Integer(l_queued);
			obj[3] = new Integer(l_delivered);
			obj[4] = new Integer(l_failed);
			obj[5] = new Integer(l_expired);
			obj[6] = new Integer(l_subscribers);
			JProgressBar progress = new JProgressBar(0, l_subscribers);
			obj[7] = progress;
			progress.setMinimum(0);
			progress.setMaximum(l_subscribers);
			//progress.setValue(l_delivered + l_expired + l_failed + l_unknown); // Unknown is part of l_failed
			progress.setValue(l_delivered + l_expired + l_failed);
			progress.setStringPainted(true);
			if((progress.getMinimum()==progress.getMaximum() && progress.getMaximum() == 0) || HasFinalStatus()) { // Måtte legge dette til for at den skal vise 100%
				progress.setMaximum(progress.getMaximum()+1);
				progress.setValue(progress.getMaximum());
			}
			return obj;
		}
		
		public LBAHISTCC()
		{
			
		}
		public LBAHISTCC(String [] v)
		{
			try
			{
				l_ccode = new Integer(v[0]).intValue();
				l_delivered = new Integer(v[1]).intValue();
				l_expired = new Integer(v[2]).intValue();
				l_failed = new Integer(v[3]).intValue();
				l_unknown = new Integer(v[4]).intValue();
				l_submitted = new Integer(v[5]).intValue();
				l_queued = new Integer(v[6]).intValue();
				l_subscribers = new Integer(v[7]).intValue();
				sz_country = no.ums.pas.cellbroadcast.CountryCodes.getCountryByCCode(String.valueOf(l_ccode)).getCountry();
				l_operator = new Integer(v[8]).intValue();
				l_failed += l_unknown;  // this is added so that we dont need an unknown status column
			}
			catch(Exception e)
			{
			}
		}
	}
	public class LBAHISTCELL
	{
		public LBAHISTCELL(String [] v)
		{
			
		}
	}
	
	
	
}


