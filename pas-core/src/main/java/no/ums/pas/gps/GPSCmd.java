package no.ums.pas.gps;

import java.awt.event.*;

public class GPSCmd extends Object {
	//GET
	public static final int CMD_SIMID		= 1;
	public static final int CMD_OWNNUMBER	= 2;
	public static final int CMD_IMEI		= 3;
	public static final int CMD_GETCOOR		= 4;
	public static final int CMD_BAT_VOLTAGE = 5;
	public static final int CMD_BAT_CHARGESTATE = 6;
	public static final int CMD_GET_GPSFIX	= 7;
	public static final int CMD_GET_DEVICENAME = 8;

	//SET
	public static final int CMD_SMSRESPONSE = 101;
	public static final int CMD_GPRSTIMEOUT = 102;
	public static final int CMD_TCPTIMEOUT	= 103;
	public static final int CMD_SET_DEVICENAME = 104;
	
	//TIMERS
	public static final int CMD_TIMERX_STARTSTOP	= 201;
	public static final int CMD_TIMERX_ECONNECTED	= 202;
	public static final int CMD_TIMERX_BATCHECK		= 203;
	public static final int CMD_TIMERX_BATREPORT	= 204;
	
	//ALARM
	public static final int CMD_ALARM_ONTIMER_SENDCOOR	= 301;
	public static final int CMD_ALARM_ONMOVE_SENDCOOR	= 302;
	public static final int CMD_ALARM_CLEAR				= 303;
	public static final int CMD_ALARM_GPSFIX			= 304;
	public static final int CMD_ALARM_BATTERY_LOW		= 305;
	public static final int CMD_ALARM_BATREPORT			= 306;
	

	//DIV
	public static final int CMD_TCP_STORAGE_CLEAR = 401;
	public static final int CMD_CLIENT_DISCONNECT = 402;
	public static final int CMD_CLIENT_CONNECT	  = 403;
	public static final int CMD_GPS_SHUTDOWN	  = 404;
	
	//GPS Initiated
	public static final int GPS_REPORT_GPSFIX			= 1001;
	
	//SERVER
	public static final int GPSSERVER_REPORT_STARTED			= 2000;
	
	
	//MAIL
	public static final int MAILCTRL_HELO		=	9001;
	public static final int MAILCTRL_MAILFROM	=	9002;
	public static final int MAILCTRL_RCPTTO		=	9003;
	public static final int MAILCTRL_SUBJECT	=	9004;
	public static final int MAILCTRL_HEADER_FROM=	9009;
	public static final int MAILCTRL_BODYSTART	=	9005;
	public static final int MAILCTRL_BODY		=	9006;
	public static final int MAILCTRL_SEND		=	9007;
	public static final int MAILCTRL_QUIT		=	9008;
	
	public static final int STATUS_NA = 0;
	public static final int STATUS_SENT = 1;
	public static final int STATUS_RECEIVED = 2;
	public static final int STATUS_FINAL_OK = 3;
	public static final int STATUS_FINAL_ERR = 4;
	public static final int STATUS_NOT_LOGGED_ON = -1;
	public static final int STATUS_UNKNOWN_COMMAND = -2;
	
	public static final int DIR_OUT = 0;
	public static final int DIR_IN  = 1;
	
	public static final String GPSCMD_ASK_FOR_SIMID = "PFAL,GSM.SIMID";
	public static final String GPSCMD_ASK_FOR_OWNNUMBER = "PFAL,GSM.OwnNumber";
	public static final String GPSCMD_ASK_FOR_IMEI = "PFAL,GSM.IMEI";
	public static final String GPSCMD_ENABLE_SMSRESPONSE = "PFAL,Cnf.Set,GSM.RESPONSE=";
	public static final String GPSCMD_SET_GPRSTIMEOUT = "PFAL,Cnf.Set,GPRS.TIMEOUT=";
	public static final String GPSCMD_SET_TCPTIMEOUT = "PFAL,Cnf.Set,TCP.CLIENT.TIMEOUT=";
	public static final String GPSCMD_BATVOLTAGE = "PFAL,Sys.Bat.Voltage";
	public static final String GPSCMD_BATCHARGESTATE = "PFAL,Sys.Bat.ChargeState";
	public static final String GPSCMD_GET_DEVICENAME = "PFAL,Cnf.Get,DEVICE.NAME";
	public static final String GPSCMD_GET_GPSFIX = "PFAL,TCP.Client.Send,0,\"GPSFix:&(Fix)\"";
	public static final String GPSCMD_TCP_STORAGE_CLEAR = "PFAL,TCP.Storage.Clear";
	public static final String GPSCMD_GPS_SHUTDOWN = "PFAL,Sys.Device.Shutdown";
	
	public int get_status() { return m_n_status; }
	public String get_msgpk() { return m_sz_msgpk; }
	public String get_objectpk() { return m_sz_objectpk; }
	public int get_cmd() { return m_n_cmd; }
	public int get_dir() { return m_n_dir; }
	public int get_lparam1() { return m_n_param1; }
	public int get_lparam2() { return m_n_param2; }
	public String get_sparam1() { return m_sz_param1; }
	public String get_sparam2() { return m_sz_param2; }
	public String get_cmdline() { return m_cmd; }
	
	protected void set_lparam1(int n) { m_n_param1 = n; }
	protected void set_lparam2(int n) { m_n_param2 = n; }
	protected void set_sparam1(String s) { m_sz_param1 = s; }
	protected void set_sparam2(String s) { m_sz_param2 = s; }
	protected void set_status(int n_status) { m_n_status = n_status; }
	private boolean m_b_isanswered = false;
	public void set_isanswered() { m_b_isanswered = true; }
	public boolean get_isanswered() { return m_b_isanswered; }
	public boolean get_success() { return m_b_success; }
	private boolean m_b_success = false;
	public void set_success(boolean b) { m_b_success = b; }
	
	private int m_n_status = STATUS_NA;
	
	public void set_cmdline(String sz_line) {
		m_cmd = sz_line;
	}
	
	//from database
	private String m_sz_msgpk;
	private String m_sz_objectpk;
	private int m_n_cmd;
	private int m_n_dir;
	private int m_n_param1;
	private int m_n_param2;
	private String m_sz_param1;
	private String m_sz_param2;
	private int m_n_pri;
	private boolean m_b_expect_answer = true;
	
	private boolean m_b_isinterrupted = false;
	public void set_interrupted() { m_b_isinterrupted = true; }
	public boolean get_interrupted() { return m_b_isinterrupted; }
	private long m_n_timeout = 20000;
	private long m_n_exec_tick = 0;
	private long m_n_finish_tick = 0;
	public long get_exec_tick() { return m_n_exec_tick; }
	public long get_finish_tick() { return m_n_finish_tick; }
	public void start_exec() {m_n_exec_tick = System.currentTimeMillis(); }
	public void set_finish_tick(long n_milli) { m_n_finish_tick = System.currentTimeMillis(); }
	public long set_timeout(long n) { return m_n_timeout = (n * 1000); }
	public long get_timeout() { return m_n_timeout; }
	public boolean get_expect_answer() { return m_b_expect_answer; }
	public boolean isTimedOut() {
		if((System.currentTimeMillis()-get_exec_tick()) > get_timeout()) {
			return true;
		}
		return false;
	}
	

	private String m_cmd;
	private boolean m_b_waitforanswer = false;
	private ActionListener m_callback;
	
	public String toString() { return m_cmd; }
	public ActionListener get_callback() { return m_callback; }
	public GPSCmd(String sz_msgpk, String sz_objectpk, int n_cmd, int n_dir, int n_param1, int n_param2, 
			String sz_param1, String sz_param2, int n_pri) {
		super();
		m_sz_msgpk		= sz_msgpk;
		m_sz_objectpk	= sz_objectpk;
		m_n_cmd			= n_cmd;
		m_n_dir			= n_dir;
		m_n_param1		= n_param1;
		m_n_param2		= n_param2;
		m_sz_param1		= sz_param1;
		m_sz_param2		= sz_param2;
		m_n_pri			= n_pri;
	}
	public GPSCmd(String sz_msgpk, String sz_objectpk, int n_cmd, int n_dir, int n_param1, int n_param2, 
			String sz_param1, String sz_param2, int n_pri, boolean b_expect_answer) {
		this(sz_msgpk, sz_objectpk, n_cmd, n_dir, n_param1, n_param2, sz_param1, sz_param2, n_pri);
		m_b_expect_answer = b_expect_answer;
	}

	public synchronized static GPSCmd createCmd( String n_msgpk, String n_objectpk, int n_cmd, int n_dir, int n_param1, int n_param2, 
			String sz_param1, String sz_param2, int n_pri, boolean b_expect_answer) {
		GPSCmd cmd = null;
		cmd = createCmd(n_msgpk, n_objectpk, n_cmd, n_dir, n_param1, n_param2, sz_param1, sz_param2, n_pri);
		cmd.m_b_expect_answer = b_expect_answer;
		return cmd;
	}

	public synchronized static GPSCmd createCmd( String n_msgpk, String n_objectpk, int n_cmd, int n_dir, int n_param1, int n_param2, 
			String sz_param1, String sz_param2, int n_pri) {
		GPSCmd cmd = null;
		cmd = new GPSCmd(n_msgpk, n_objectpk, n_cmd, n_dir, n_param1, n_param2, sz_param1, sz_param2, n_pri);
		switch(n_cmd) {
			case CMD_SIMID:
				cmd.set_cmdline(GPSCMD_ASK_FOR_SIMID);
				break;
			case CMD_OWNNUMBER:
				cmd.set_cmdline(GPSCMD_ASK_FOR_OWNNUMBER);
				break;
			case CMD_IMEI:
				cmd.set_cmdline(GPSCMD_ASK_FOR_IMEI);
				break;
			case CMD_SMSRESPONSE:
				cmd.set_cmdline(GPSCMD_ENABLE_SMSRESPONSE + n_param1);
				break;
			case CMD_GPRSTIMEOUT:
				cmd.set_cmdline(GPSCMD_SET_GPRSTIMEOUT + n_param1 + "," + n_param2);
				break;
			case CMD_TCPTIMEOUT:
				cmd.set_cmdline(GPSCMD_SET_TCPTIMEOUT + n_param1 + "," + n_param2);
				break;
			case CMD_TIMERX_ECONNECTED:
				if(sz_param1.equals(""))
					sz_param1 = "0";
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=TCP.Client.eConnected:SYS.Timer" + sz_param1 + ".start=cyclic," + n_param2);
				break;
			case CMD_ALARM_ONTIMER_SENDCOOR:
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=SYS.Timer.e0" + (sz_param1.equals("1") ? "&GPS.Nav.eFix=valid" : "") + ":TCP.Client.Send," + n_param2 + ",\"pos:\"");
				break;
			case CMD_ALARM_ONMOVE_SENDCOOR:
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=GPS.Nav.Position.s0>=" + sz_param1 + /*"&GPS.Nav.eFix=valid*/ ":GPS.Nav.Position0=current&TCP.Client.Send," + n_param2 + ",\"pos:\"");
				break;
			case CMD_TIMERX_STARTSTOP:
				if(n_param1==0)
					cmd.set_cmdline("PFAL,Sys.Timer0.Stop");
				else if(n_param1==1)
					cmd.set_cmdline("PFAL,Sys.Timer0.Start=cyclic," + n_param2);
				break;
			case CMD_GETCOOR:
				cmd.set_cmdline("PFAL,TCP.Client.Send," + n_param1 + ",\"pos:\"");
				break;
			case CMD_BAT_VOLTAGE:
				cmd.set_cmdline(GPSCMD_BATVOLTAGE);
				break;
			case CMD_BAT_CHARGESTATE:
				cmd.set_cmdline(GPSCMD_BATCHARGESTATE);
				break;
			case CMD_TIMERX_BATREPORT:
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=TCP.Client.eConnected:SYS.Timer1.start=cyclic," + n_param2);
				break;
			case CMD_ALARM_BATREPORT:
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=SYS.Timer.e1:TCP.Client.Send,0,\"&(VAkku)\"");
				break;
			case CMD_SET_DEVICENAME:
				cmd.set_cmdline("PFAL,Cnf.Set,DEVICE.NAME=" + sz_param1);
				break;
			case CMD_GET_DEVICENAME:
			 	cmd.set_cmdline(GPSCMD_GET_DEVICENAME);
			 	break;
			case CMD_ALARM_GPSFIX:
				String sz = (sz_param1.equals("0") ? "invalid" : "valid");
				cmd.set_cmdline("PFAL,Cnf.Set,AL" + n_param1 + "=GPS.Nav.eFix=" + sz + ":TCP.Client.Send,0,\"GPSFix:&(Fix)\"");//" + n_param2 + ",\"$(Fix)\"");
			 	break;
			case CMD_GET_GPSFIX:
				cmd.set_cmdline(GPSCMD_GET_GPSFIX);
				break;
			case CMD_TCP_STORAGE_CLEAR:
				cmd.set_cmdline(GPSCMD_TCP_STORAGE_CLEAR);
				break;
			case CMD_ALARM_CLEAR:
				cmd.set_cmdline("PFAL,Cnf.Clear,AL" + n_param1);
				break;
			case CMD_GPS_SHUTDOWN:
				cmd.set_cmdline(GPSCMD_GPS_SHUTDOWN);
				break;
				
			case MAILCTRL_HELO:
				cmd.set_cmdline("HELO " + sz_param1);
				break;
			case MAILCTRL_MAILFROM:
				cmd.set_cmdline("MAIL FROM:<" + sz_param1 + ">");
				break;
			case MAILCTRL_RCPTTO:
				cmd.set_cmdline("RCPT TO:" + sz_param1 + "");
				break;
			case MAILCTRL_HEADER_FROM:
				cmd.set_cmdline("From: \"" + sz_param1 + "\" <" + sz_param2 + ">");
				break;
			case MAILCTRL_SUBJECT:
				cmd.set_cmdline("Subject:" + sz_param1 + "\r\n");
				break;
			case MAILCTRL_BODYSTART:
				cmd.set_cmdline("data");
				break;
			case MAILCTRL_BODY:
				cmd.set_cmdline(sz_param1);
				break;
			case MAILCTRL_SEND:
				cmd.set_cmdline("\r\n.");
				break;
			case MAILCTRL_QUIT:
				cmd.set_cmdline("QUIT");
				break;
			default:
				//STATUS_UNKNOWN_COMMAND;
				break;
		}
		/*
		 	public static final int MAILCTRL_HELO		=	9001;
	public static final int MAILCTRL_MAILFROM	=	9002;
	public static final int MAILCTRL_RCPTTO		=	9003;
	public static final int MAILCTRL_SUBJECT	=	9004;
	public static final int MAILCTRL_BODYSTART	=	9005;
	public static final int MAILCTRL_SEND		=	9006;
	public static final int MAILCTRL_QUIT		=	9007;
		 */
		return cmd;
	}
	
}
/*class CmdSimID extends GPSCmd {
	CmdSimID(String sz_objectpk, ActionListener callback) {
		super(sz_objectpk, 1, GPSCmd.GPSCMD_ASK_FOR_SIMID, DIR_OUT, callback);
	}
}

class CmdOwnNumber extends GPSCmd {
	CmdOwnNumber(String sz_objectpk, ActionListener callback) {
		super(sz_objectpk, 2, GPSCmd.GPSCMD_ASK_FOR_OWNNUMBER, DIR_OUT, callback);
	}
}

class CmdIMEI extends GPSCmd {
	CmdIMEI(String sz_objectpk, ActionListener callback) {
		super(sz_objectpk, 3, GPSCmd.GPSCMD_ASK_FOR_IMEI, DIR_OUT, callback);
	}
}

*/