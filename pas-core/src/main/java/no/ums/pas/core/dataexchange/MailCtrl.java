package no.ums.pas.core.dataexchange;


import no.ums.pas.gps.GPSCmd;
import no.ums.pas.gps.SocketThread;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class MailCtrl extends Object {
	ActionListener m_callback;
	private boolean m_b_sentok = false;
	public boolean isSentOk() { return m_b_sentok; }
	private int m_n_lasterrorcode;
	private String m_sz_lasterror;
	public int get_last_msgcode() { return m_n_lasterrorcode; }
	public String get_last_msg() { return m_sz_lasterror; } 
	protected void set_last_msg(int n_code, String sz_error) {
		m_n_lasterrorcode = n_code;
		m_sz_lasterror = sz_error;
	}
	
	public MailCtrl(String sz_helofrom, String sz_mailserver, int n_port, String sz_displayname, String sz_from, List<String> sz_to, 
			ActionListener callback, String sz_subject, String sz_body) {
		super();
		m_callback = callback;
		Socket socket = null;
		try {
			socket = new Socket(sz_mailserver, n_port);
		} catch(Exception e) {
			Error.getError().addError("Mail error", "Could not create a socket to " + sz_mailserver, e, Error.SEVERITY_ERROR);
			return;
		}
		SendMail sendmail = new SendMail(socket);
		send_mail(sendmail, sz_helofrom, sz_displayname, sz_from, sz_to, sz_subject, sz_body);
		
	}
	/*
	public GPSCmd(String sz_msgpk, String sz_objectpk, int n_cmd, int n_dir, int n_param1, int n_param2, 
			String sz_param1, String sz_param2, int n_pri) {
	 */
	public boolean send_mail(SendMail sendmail, String sz_helofrom, String sz_displayname, String sz_from, List<String> sz_to, 
			String sz_subject, String sz_body) {
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_HELO, 0, 0, 0, sz_helofrom, "", 1));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_MAILFROM, 0, 0, 0, sz_from, "", 1));
		for(int i=0; i < sz_to.size(); i++)
			sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_RCPTTO, 0, 0, 0, sz_to.get(i), "", 1));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_BODYSTART, 0, 0, 0, "", "", 1));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_HEADER_FROM, 0, 0, 0, sz_displayname, sz_from, 1, false));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_SUBJECT, 0, 0, 0, sz_subject, "", 1, false));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_BODY, 0, 0, 0, sz_body, "", 1, false));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_SEND, 0, 0, 0, "", "", 1));
		sendmail.add_to_writequeue(GPSCmd.createCmd("0", "0", GPSCmd.MAILCTRL_QUIT, 0, 0, 0, "", "", 1));
		sendmail.logoff();
		return true;
	}
	
	public class SendMail extends SocketThread {
		
		public SendMail(Socket socket) {
			super(m_callback, socket);
		}
		protected boolean listen() {
			System.out.println("Listener started");
			String inputLine;
			try {
				int n_returncode = -1;
				while(1==1) {
					try {
						while ((inputLine = m_in.readLine()) != null) {	
							try {
								//System.out.println(inputLine);
								try {
									n_returncode = new Integer(inputLine.substring(0, 3)).intValue();
									if(n_returncode!=220) { //don't do anything with logon procedure
										switch(n_returncode) {
											case 250: //ok
												m_b_sentok = true;
												set_last_msg(n_returncode, "Message accepted for delivery");
												break;
											case 221: //logoff, ignore
												break;
											case 354: // Intermediate status code
												break;
											default: //some error
												set_last_msg(n_returncode, "Error");
												Error.getError().addError("Error", "Error sending mail", "Return code = " + n_returncode, 0, 1);
												break;
										}
										get_writethread().set_cmd_success(true);
										get_writethread().set_cmd_isanswered(true);
									}							
								} catch(Exception e) {
									//not a number
								}

								/*if(.equals("220")) { //logon ok 
									System.out.println("Logon OK");
								} else {
									
									get_writethread().set_cmd_success(true);
									get_writethread().set_cmd_isanswered(true);									
								}*/
							} catch(Exception e) {
								System.out.println(e.getMessage());
								break;
							}
							if(!get_clientsocket().isConnected()) {
								//System.out.println("Connection lost");
								break;
							}
						    if(get_protocol().isLoggingOff()) {
						    	//System.out.println("Logging off");
						    	break;
						    }
						    /*if(get_manual_logoff()) {
						    	//System.out.println("Server removing dead thread");
						    	sz_reason = "get_manual_logoff()";
						    	break;
						    }			*/			
						}
					} catch(IOException e) {
						System.out.println("Error reading, closing connection");
						break;
					} catch(Exception e) {
						System.out.println("Buffered reader forced to close");
						logoff();
					}
					if(!get_clientsocket().isConnected()) {
						//sz_reason = "!get_clientsocket().isConnected()";
						break;
					}
				    if(get_protocol().isLoggingOff()) {
				    	//System.out.println("Logging off");
						//sz_reason = "get_protocol().isLoggingOff()";
				    	break;
				    }
				    if(get_manual_logoff()) {
				    	//System.out.println("Server removing dead thread");
				    	//sz_reason = "get_manual_logoff()";
				    	break;
				    }					
				}
				
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			System.out.println("Listener stopped");
			callback();
			return true;
		}
	}
	public void callback() {
		if(m_callback!=null) {
			if(get_last_msgcode()==250)
				m_callback.actionPerformed(new ActionEvent(new Integer(this.get_last_msgcode()), ActionEvent.ACTION_PERFORMED, "act_maildelivery_success"));
			else
				m_callback.actionPerformed(new ActionEvent(new Integer(this.get_last_msgcode()), ActionEvent.ACTION_PERFORMED, "act_maildelivery_failed"));
		}
	}
}