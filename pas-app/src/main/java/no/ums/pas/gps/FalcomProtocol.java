package no.ums.pas.gps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FalcomProtocol {
	private static final int STATE_WAIT_FOR_LOGON_	= 0;
	private static final int STATE_CONNECTED_		= 1;
	private static final int STATE_DISCONNECTED_	= 2;
	private static final int STATE_IDLE_			= 3;
	private static final int STATE_KEEPALIVE_		= 4;
	private static final int STATE_WAIT_FOR_ANSWER_	= 5;
	
	
	private static final String CMD_LOGON_ = "$<MSG.Info.ServerLogin>";
	private static final String INFO_DEVICE_NAME_ = "$DeviceName";
	private static final String INFO_DEVICE_SOFTWARE_ = "$Software";
	private static final String INFO_DEVICE_HARDWARE_ = "$Hardware";
	private static final String INFO_DEVICE_IMEI_ = "$IMEI";
	private static final String INFO_DEVICE_LOCALIP_ = "$LocalIP";
	private static final String INFO_DEVICE_CMDVERSION_ = "$CmdVersion";
	private static final String CMD_PING_ = "ping";
	private static final String POS_GPGLL_ = "GPGLL";
	private static final String POS_GPGGA_ = "GPGGA";
	private static final String POS_GPRMC_ = "GPRMC";
	private static final String POS_GPGSV_ = "GPGSV";
	private static final String POS_GPGSA_ = "GPGSA";
	private static final String POS_GPVTG_ = "GPVTG";
	private static final String POS_GPAREA_ = "GPAREA";
	private static final String CMD_RET_END_ = "$<end>";
	private static final String CMD_RET_SUCCESS_ = "$SUCCESS";
	private static final String CMD_RET_FAILED_ = "$ERROR";
	private static final String CMD_RET_LOGOUT_ = "quit";
	private static final String CMD_RET_BATTERY_VOLTAGE_ = "$battery voltage:";
	
	private static final String CMD_ASK_BATTERY_VOLTAGE_ = "$<SYS.Bat.Voltage>"; //$battery voltage: 4.1 V
	private static final String CMD_ASK_BATTERY_CHARGESTATE_ = "$<SYS.Bat.ChargeState>"; //$battery is charging
	private static final String CMD_ASK_SET_DEVICENAME_ = "$<CNF.Set>"; //$DEVICE.NAME written to flash
	private static final String CMD_ASK_GET_DEVICENAME_ = "$<CNF.Get>"; //$DEVICE.NAME=Modda
	private static final String CMD_ASK_GETCOOR_ = "$<TCP.Client.Send>"; //$enqueued 53 Bytes to TCP buffer
	private static final String CMD_ASK_TIMERX_STARTSTOP_ = "$<SYS.Timer0.Start>"; //"$Timer 0 initialized and started" "$Timer 0 stopped"
	private static final String CMD_ASK_ALARM_ONMOVE_SENDCOOR_ = "$<CNF.Set>";  //$alarm 0 successfully configured
																				//$AL0 written to flash
	private static final String CMD_ASK_ALARM_ONTIMER_SENDCOOR_ = "$<CNF.Set>"; //$alarm 0 successfully configured
																				//$AL0 written to flash
	private static final String CMD_ASK_TIMERX_ECONNECTED_ = "$<CNF.Set>"; 	//$alarm 0 successfully configured
																			//$AL0 written to flash
	private static final String CMD_ASK_TIMERX_BATTERYVOLTAGE_ = "$<CNF.Set>";
	private static final String CMD_ASK_TCPTIMEOUT_ = "$<CNF.Set>"; //$TCP setting updated
																	//$TCP.CLIENT.TIMEOUT written to flash
	private static final String CMD_ASK_SET_GPRSTIMEOUT_ = "$<CNF.Set>"; //$GPRS.TIMEOUT written to flash
	private static final String CMD_ASK_ENABLE_SMSRESPONSE_ = "$<CNF.Set>"; //$GSM setting updated
																			//$GSM.RESPONSE written to flash
	private static final String CMD_ASK_IMEI_ = "$<GSM.IMEI>"; //$IMEI:352021003719382
	private static final String CMD_ASK_OWNNUMBER_ = "$<GSM.OwnNumber>"; //$phone number:˚2 ?Tù
																		 //?? (SIM voice number)
	private static final String CMD_ASK_SIMID_ = "$<GSM.SIMID>"; //$SIMID:242010102112949 (mobile subscriber ID)
	private static final String CMD_RET_GPSFIX_ = "$GPSFix:";
	private static final String CMD_RET_POSBUTTON_ = "$<sfal.msg.pos_button:Send position>"; //<end>
	
	
	    
	private int state = STATE_WAIT_FOR_LOGON_;
	private boolean m_b_disconnect = false;
	private boolean m_b_connected = false;
	private SteppUnit m_unit;
	public SteppUnit get_unit() { return m_unit; }
	private ActionListener m_callback;
	protected ActionListener get_callback() { return m_callback; }
	protected SocketThread m_thread;
	public SocketThread get_thread() { return m_thread; }
	
	public boolean isLoggingOff() { return m_b_disconnect; }

	FalcomProtocol(SocketThread thread, ActionListener callback) {
		m_unit = new SteppUnit();
		m_thread = thread;
		m_callback = callback;
	}
	
	public String processInput(String theInput) {
		String sz_output = null;
		System.out.println(new java.util.Date().toString() + " " + get_unit().get_imei() + " - " + theInput);
		
		//check for quit first
		try {
			if(theInput.substring(0, CMD_RET_LOGOUT_.length()).equals(CMD_RET_LOGOUT_)) {
				disconnect();
				return "DISCONNECTED";
			}
		} catch(Exception e) { }
		
		if (state == STATE_WAIT_FOR_LOGON_) {
			if(logon(theInput)) {
				sz_output = "CONNECTED";
				state = STATE_CONNECTED_;
				set_connected();
			}
			else {
				disconnect();
				sz_output = "LOGON FAILED";
				state = STATE_DISCONNECTED_;
			}
			System.out.println(sz_output);
		} else if(state == STATE_CONNECTED_) {
			String sz_cmd = get_values(theInput, "=")[0];
			if(sz_cmd.equals(INFO_DEVICE_NAME_))
				get_unit().set_id(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(INFO_DEVICE_SOFTWARE_))
				get_unit().set_firmware(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(INFO_DEVICE_HARDWARE_))
				get_unit().set_hardware(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(INFO_DEVICE_IMEI_))
				get_unit().set_imei(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(INFO_DEVICE_LOCALIP_))
				get_unit().set_localip(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(INFO_DEVICE_CMDVERSION_))
				get_unit().set_cmdversion(get_values(theInput, "=")[1]);
			else if(sz_cmd.equals(CMD_RET_END_)) {
				verify_client();
				//set_cmd_end();
				state = STATE_IDLE_;
			}
		} else if(state == STATE_IDLE_) {
			if(theInput.indexOf(CMD_PING_) >= 0) {
				System.out.println(get_unit().get_objectpk() + " ping");
			}
			/*
			else if(theInput.indexOf(POS_GPGLL_) >= 0) {
				//GPGLL,0000.0000,N,00000.0000,E,000051.000,V<end>
				try {
					if(get_unit()!=null) {
						Position pos = new Position(get_unit(), new PosGPGLL(get_values(theInput, ",")));
						add_position(pos);
					}
				} catch(Exception e) {
					System.out.println("Failed to add position " + e.getMessage());
				}
			}
			else if(theInput.indexOf(POS_GPVTG_) >= 0) {
				Position pos = new Position(get_unit(), new PosGPVTG(get_values(theInput, ",")));
				add_position(pos);
			}
			else if(theInput.indexOf(POS_GPRMC_) >= 0) {
				Position pos = new Position(get_unit(), new PosGPRMC(get_values(theInput, ",")));
				add_position(pos);				
			}
			else if(theInput.indexOf(POS_GPGGA_) >= 0) {
				Position pos = new Position(get_unit(), new PosGPGGA(get_values(theInput, ",")));
				add_position(pos);
			}*/
			else if(theInput.indexOf(CMD_RET_GPSFIX_) >= 0) {
				String vals[] = get_values(theInput, ":");
				boolean b = false;
				try {
					b = (vals[1].equals("1") ? true : false);
					get_unit().set_gpsfix(b);
					set_gpsfix(get_unit());
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			else if(theInput.indexOf(CMD_RET_BATTERY_VOLTAGE_) >= 0) {
				String vals[] = get_values(theInput, ":");
				try {
					//System.out.println("\"" + new String(vals[1].trim().substring(0, vals[1].trim().length()-1).trim()) + "\"");
					Double d = new Double(new String(vals[1].trim().substring(0, vals[1].trim().length()-1).trim()));
					get_unit().set_battery(d.doubleValue());
					set_batteryvoltage(get_unit());
					//System.out.println("Battery voltage=" + d);
				} catch(Exception e) {
					System.out.println("Error: battery voltage");
				}
			}
			else if(theInput.indexOf(CMD_RET_SUCCESS_) >= 0) {
				set_cmd_success(true);
			}
			else if(theInput.indexOf(CMD_RET_FAILED_) >= 0) {
				set_cmd_success(false);
			}
			else if(theInput.indexOf(CMD_RET_END_) >= 0) {
				set_cmd_end();
			}
		}
		return sz_output;
	}
	protected synchronized void set_batteryvoltage(SteppUnit unit) {
		get_callback().actionPerformed(new ActionEvent(unit, ActionEvent.ACTION_PERFORMED, "act_cmd_setbatteryvoltage"));
	}
	protected synchronized void set_gpsfix(SteppUnit unit) {
		get_callback().actionPerformed(new ActionEvent(unit, ActionEvent.ACTION_PERFORMED, "act_cmd_setgpsfix"));
	}
	/*protected synchronized void add_position(Position pos) {
		get_callback().actionPerformed(new ActionEvent(pos, ActionEvent.ACTION_PERFORMED, "act_add_position"));
	}*/
	
	private void verify_client() {
		get_callback().actionPerformed(new ActionEvent(get_thread(), ActionEvent.ACTION_PERFORMED, "act_verify_client"));
	}
	private void set_cmd_end() {
		get_thread().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_cmd_end"));
	}
	private void set_cmd_success(boolean b) {
		if(b) {
			get_thread().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_cmd_success"));
		} else
			get_thread().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_cmd_failed"));
	}
	
	public String [] get_values(String in, String separator) {
		String [] sz_ret;
		sz_ret = in.split(separator);
		return sz_ret;
	}
	
	public boolean logon(String sz_logonstring) {
		try {
			if(sz_logonstring.substring(0, CMD_LOGON_.length()).equals(CMD_LOGON_)) {
				return true;
			}
		} catch(Exception e) { }
		return false;
	}
	public void set_connected() {
		m_b_connected = true;
		get_unit().set_connected(true);
		
	}
	public void disconnect() {
		state = STATE_DISCONNECTED_;
		get_unit().set_connected(false);
		m_b_disconnect = true;
	}
}
