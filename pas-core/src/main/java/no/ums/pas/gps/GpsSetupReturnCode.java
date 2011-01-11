package no.ums.pas.gps;

public class GpsSetupReturnCode extends Object {
		
	private int m_n_answered = 0;
	private String m_sz_objectpk = "";
	private String m_sz_msgpk = "";
	private String m_sz_text = "";
	private String m_sz_answertext = "";
	public int get_answercode() { return m_n_answered; }
	public String get_msgpk() { return m_sz_msgpk; }
	public String get_text() { return m_sz_text; }
	public String get_answertext() { return m_sz_answertext; }
	public String get_objectpk() { return m_sz_objectpk; }
	protected void set_answertext(String s) { m_sz_answertext = s; }
	
	public static String create_returntext(int ANSW) {
		String ret = "";
		switch(ANSW) {
		case GPSCmd.STATUS_NA:
			ret = "Message not hanled by server";
			break;
		case GPSCmd.STATUS_SENT:
			ret = "Sent to unit";
			break;
		case GPSCmd.STATUS_RECEIVED:
			ret = "Alarm";
			break;
		case GPSCmd.STATUS_FINAL_OK:
			ret = "Success";
			break;
		case GPSCmd.STATUS_FINAL_ERR:
			ret = "Error";
			break;
		case GPSCmd.STATUS_NOT_LOGGED_ON:
			ret = "Unit not logged on";
			break;
		case GPSCmd.STATUS_UNKNOWN_COMMAND:
			ret = "Unknown command";
			break;
		default:
			ret = "Returncode not recognized";
			break;
		}
		return ret;
	}
	
	public GpsSetupReturnCode(String sz_objectpk, int n_answered, String sz_msgpk, String sz_text) {
		super();
		m_sz_objectpk	= sz_objectpk;
		m_n_answered	= n_answered;
		m_sz_msgpk		= sz_msgpk;
		m_sz_text		= sz_text;
		switch(get_answercode()) {
			case GPSCmd.STATUS_NA:
				set_answertext("Message could not be sent from the server");
				break;
			case GPSCmd.STATUS_SENT:
				set_answertext("Message sent, no reply from unit");
				break;
			case GPSCmd.STATUS_RECEIVED:
				set_answertext("Message received by unit");
				break;
			case GPSCmd.STATUS_FINAL_OK:
				set_answertext("Unit successfully updated");
				break;
			case GPSCmd.STATUS_FINAL_ERR:
				set_answertext("Unit could not be updated");
				break;
			case GPSCmd.STATUS_NOT_LOGGED_ON:
				set_answertext("Could not send message to the unit because it's not logged on to the GPS Server");
				break;
			/*
	public static final int STATUS_NA = 0;
	public static final int STATUS_SENT = 1;
	public static final int STATUS_RECEIVED = 2;
	public static final int STATUS_FINAL_OK = 3;
	public static final int STATUS_FINAL_ERR = 4;
	public static final int STATUS_NOT_LOGGED_ON = -1;
		*		*/
		}
	}
}
