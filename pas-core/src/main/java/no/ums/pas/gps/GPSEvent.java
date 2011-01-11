package no.ums.pas.gps;

import no.ums.pas.ums.tools.TextFormat;

public class GPSEvent extends Object {
	private String _sz_eventpk;
	private String _sz_objectpk;
	private int _n_cmd;
	private int _n_dir;
	private int _n_param1;
	private int _n_param2;
	private String _sz_param1;
	private String _sz_param2;
	private int _n_answered;
	private int _n_pri;
	private int _n_date;
	private int _n_time;
	private int _n_updatedate;
	private int _n_updatetime;
	private String _sz_msgpk;
	private String _sz_statement;
	
	public String get_eventpk() { return _sz_eventpk; }
	public String get_objectpk() { return _sz_objectpk; }
	public int get_cmd() { return _n_cmd; }
	public int get_dir() { return _n_dir; }
	public int get_param1() { return _n_param1; }
	public int get_param2() { return _n_param2; }
	public String get_sparam1() { return _sz_param1; }
	public String get_sparam2() { return _sz_param2; }
	public int get_answered() { return _n_answered; }
	public int get_pri() { return _n_pri; }
	public int get_date() { return _n_date; }
	public int get_time() { return _n_time; }
	public int get_updatedate() { return _n_updatedate; }
	public int get_updatetime() { return _n_updatetime; }
	public String get_msgpk() { return _sz_msgpk; }
	public String get_statement() { return _sz_statement; }
	
	public String get_datetime() { return TextFormat.format_date(get_updatedate()) + " " + TextFormat.format_time(get_updatetime(), 6); }
	public String toString() { return get_datetime(); }
	
	GPSEvent(String sz_eventpk, String sz_objectpk, String sz_cmd, String sz_dir,
			String sz_param1, String sz_param2, String sz_sparam1, String sz_sparam2,
			String sz_answered, String sz_pri, String sz_date, String sz_time,
			String sz_updatedate, String sz_updatetime, String sz_msgpk) {
		this(sz_eventpk, sz_objectpk, new Integer(sz_cmd).intValue(), new Integer(sz_dir).intValue(),
			new Integer(sz_param1).intValue(), new Integer(sz_param2).intValue(), sz_sparam1,
			sz_sparam2, new Integer(sz_answered).intValue(), new Integer(sz_pri).intValue(),
			new Integer(sz_date).intValue(), new Integer(sz_time).intValue(), 
			new Integer(sz_updatedate).intValue(), new Integer(sz_updatetime).intValue(),
			sz_msgpk);
	}
	GPSEvent(String sz_eventpk, String sz_objectpk, int n_cmd, int n_dir, int n_param1,
			int n_param2, String sz_param1, String sz_param2, int n_answered, int n_pri,
			int n_date, int n_time, int n_updatedate, int n_updatetime, String sz_msgpk) {
		_sz_eventpk		= sz_eventpk;
		_sz_objectpk	= sz_objectpk;
		_n_cmd			= n_cmd;
		_n_dir			= n_dir;
		_n_param1		= n_param1;
		_n_param2		= n_param2;
		_sz_param1		= sz_param1;
		_sz_param2		= sz_param2;
		_n_answered		= n_answered;
		_n_pri			= n_pri;
		_n_date			= n_date;
		_n_time			= n_time;
		_n_updatedate	= n_updatedate;
		_n_updatetime	= n_updatetime;
		_sz_msgpk		= sz_msgpk;
		create_statement();
	}
	private void create_statement() {
		switch(get_cmd()) {
			case GPSCmd.CMD_GPRSTIMEOUT:
				_sz_statement = "GPRS Timeout set to TIMEOUT: " + (_n_param1==1 ? "on" : "off") + " TIME: " + _n_param2 + "sec";
				break;
			case GPSCmd.CMD_TCPTIMEOUT:
				_sz_statement = "TCP Timeout set to AUTORECONNECT: " + (_n_param1==1 ? "on" : "off") + " CMD TIMEOUT: " + _n_param2 + "sec";
				break;
			case GPSCmd.CMD_TIMERX_ECONNECTED:
				_sz_statement = "Cyclic timer" + _sz_param1 + " started with " + _n_param1 + "msec interval";
				break;
			case GPSCmd.CMD_ALARM_ONTIMER_SENDCOOR:
				_sz_statement = "Alarm" + _n_param1 + " configured, send coordinates when on cyclic timer" + _sz_param2;
				break;
			case GPSCmd.CMD_ALARM_GPSFIX:
				_sz_statement = "Alarm" + _n_param1 + " configured. Report when GPSFix turns " + (_sz_param1.equals("0") ? "invalid" : "valid");
				break;
			case GPSCmd.CMD_ALARM_ONMOVE_SENDCOOR:
				_sz_statement = "Alarm" + _n_param1 + " configured, send coordinates every " + _sz_param1 + " meters";
				break;
			case GPSCmd.CMD_BAT_CHARGESTATE:
				_sz_statement = "Asked unit for battery chargingstate";
				break;
			case GPSCmd.CMD_BAT_VOLTAGE:
				_sz_statement = "Asked unit for battery voltage";
				break;
			case GPSCmd.CMD_TIMERX_STARTSTOP:
				if(_n_param1==0)
					_sz_statement = "Stopped cyclic timer" + _sz_param1;
				else
					_sz_statement = "Started cyclic timer" + _sz_param1 + ", " + _n_param2 + "msec";
				break;
			case GPSCmd.CMD_GETCOOR:
				_sz_statement = "Asked unit for coordinates";
				break;
			case GPSCmd.CMD_GET_GPSFIX:
				_sz_statement = "Asked unit for GPSFix";
				break;
			case GPSCmd.GPS_REPORT_GPSFIX:
				_sz_statement = "Alarm report GPSFix=" + (_n_param1==1 ? "true" : "false");
				break;
			case GPSCmd.CMD_ALARM_CLEAR:
				_sz_statement = "Delete alarm" + _n_param1;
				break;
			case GPSCmd.CMD_CLIENT_DISCONNECT:
				_sz_statement = "Client disconnected";
				break;
			case GPSCmd.CMD_CLIENT_CONNECT:
				_sz_statement = "Client connected";
				break;
			case GPSCmd.CMD_GPS_SHUTDOWN:
				_sz_statement = "Remote shutdown";
				break;
			default:
				_sz_statement = "Unknown command";
				break;

		}
		/*String s = "";
		switch(_n_answered) {
			case GPSCmd.STATUS_NA:
				s = "Status: N/A";
				break;
			case GPSCmd.STATUS_NOT_LOGGED_ON:
				s = "Status: unit not logged on";
				break;
			case GPSCmd.STATUS_RECEIVED:
				s = "Status: received";
				break;
			case GPSCmd.STATUS_SENT:
				s = "Status: sent";
				break;
			case GPSCmd.STATUS_FINAL_OK:
				s = "Status: OK";
				break;
			case GPSCmd.STATUS_FINAL_ERR:
				s = "Status: unit reported error";
				break;
			case GPSCmd.STATUS_UNKNOWN_COMMAND:
				s = "Status: unknown command";
				break;
		}
		_sz_statement = s + " " + _sz_statement;*/
	}
}



