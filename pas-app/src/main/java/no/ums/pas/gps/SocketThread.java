package no.ums.pas.gps;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class SocketThread extends Thread implements ActionListener {

    private static final Log log = UmsLog.getLogger(SocketThread.class);

	protected Socket m_client = null;
	public Socket get_clientsocket() { return m_client; }
	protected BufferedReader m_in;
	private FalcomProtocol m_protocol;
	private PrintWriter m_out;
	public FalcomProtocol get_protocol() { return m_protocol; }
	protected boolean m_b_connected = false;
	private ActionListener m_callback;
	protected ActionListener get_callback() { return m_callback; }
	protected final SocketThread.WriteThread m_writethread = new WriteThread();
	public SocketThread.WriteThread get_writethread() { return m_writethread; }
	protected boolean m_b_manual_logoff = false;
	public boolean get_manual_logoff() { return m_b_manual_logoff; }
	public void logoff() { m_b_manual_logoff = true; }
	public void set_interrupt() {
		close_reader();
		try {
			get_writethread().interrupt();
			super.interrupt();
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	public void close_reader() {
		try {
			log.debug("closing reader");
			m_in.close();
		} catch(IOException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public SocketThread(ActionListener callback, Socket client) {
		m_client = client;
		try {
			m_protocol = new FalcomProtocol(this, callback);
		} catch(Exception e) {
			
		}
		m_callback = callback;
		m_b_connected = m_client.isConnected();
		try {
			m_out = new PrintWriter(get_clientsocket().getOutputStream(), true);
		} catch(IOException e) {
			log.debug("Error creating PrintWriter " + e.getMessage());
		}
		try {
			m_in = new BufferedReader(new InputStreamReader(get_clientsocket().getInputStream()));
		} catch(IOException e) {
			log.debug("Error creating BufferedReader " + e.getMessage());
		}
	}
	
	public void add_to_writequeue(GPSCmd cmd) {
		get_writethread().add_to_msgqueue(cmd);
	}
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_set_objectpk".equals(e.getActionCommand())) {
			log.debug("Objectpk " + (String)e.getSource() + " found");
			get_protocol().get_unit().set_objectpk((String)e.getSource());
		} 
		else if("act_cmd_end".equals(e.getActionCommand())) {
			e.setSource(get_writethread().get_current_cmd());
			get_callback().actionPerformed(e);
		}
		else if("act_cmd_success".equals(e.getActionCommand())) {
			//get_writethread().get_current_cmd().set_success(true);
			get_writethread().set_cmd_success(true);
		}
		else if("act_cmd_failed".equals(e.getActionCommand())) {
			//get_writethread().get_current_cmd().set_success(false);
			get_writethread().set_cmd_success(false);
		}
	}
	


	public class WriteThread extends Thread {
		private final List<GPSCmd> m_msgqueue = new ArrayList<GPSCmd>();
		private synchronized List<GPSCmd> get_msgqueue() { return m_msgqueue; }
		//public void add_to_msgqueue(String sz_cmd) { get_msgqueue().add(sz_cmd); }
		public void add_to_msgqueue(GPSCmd cmd) { get_msgqueue().add(cmd); }
		private GPSCmd m_current_cmd = null;
		protected GPSCmd get_current_cmd() { return m_current_cmd; } 
		public void set_cmd_success(boolean b) {
			get_current_cmd().set_success(b);
			//m_current_cmd = null;
		}
		public void set_cmd_isanswered(boolean b) {
			get_current_cmd().set_isanswered();
		}
		
		public void run() {
			log.debug("Write thread started");
			int n_timer = 0;
			int n_sleep = 50;
			int n_ping = 120;
			int n_cmd_timeout = 20;
			while(1==1) {
				try {
					Thread.sleep(n_sleep);
				} catch(InterruptedException e) {
					break;
				}
			    n_timer++;
			    send_from_queue();
			    if(n_timer > (n_ping * 1000 / n_sleep)) {
					n_timer = 0;
					//add_to_writequeue(new GPSCmd("PFAL,Cnf.Get,TCP.Client.Ping"));
					//m_out.println("PFAL,TCP.Client.Ping\r");
					//log.debug("Ping sent");
					//log.debug("Keep-alive");
					//m_out.println("PFAL,Cnf.Get,Sys.GPS.Enable\r");
				}
				
				if(!get_clientsocket().isConnected()) {
					//log.debug("Connection lost");
					set_interrupt();
					break;
				}
			    if(get_protocol().isLoggingOff()) {
			    	//log.debug("Logging off");
					set_interrupt();
			    	break;
			    }
			    if(get_manual_logoff()) {
			    	if(get_msgqueue().size()==0) //wait for messagequeue to clear
				    	break;
			    	//log.debug("Manual logoff");
			    }
			}
			log.debug("Write thread stopped");
			m_b_manual_logoff = true;
		}
		private void send_from_queue() {
			//if(m_current_cmd!=null) {
			if(get_msgqueue().size() > 0) {
				if(m_current_cmd == null || m_current_cmd.get_isanswered()) {
					m_current_cmd = (GPSCmd)get_msgqueue().get(0);
					m_current_cmd.start_exec();
					send_cmd(get_msgqueue().get(0).toString());
					get_msgqueue().remove(0);
					wait_for_timeout();
				}
			}
			//}
		}
		private void wait_for_timeout() {
			while(!m_current_cmd.get_isanswered() && m_current_cmd.get_expect_answer()) {
				try {
					if(m_current_cmd.isTimedOut()) {
						log.debug("Command " + m_current_cmd.get_cmd() + " timed out");
						break;
					}
					Thread.sleep(100);
				} catch(InterruptedException e) {
					log.debug("Command " + m_current_cmd.get_cmd() + " interrupted");
					m_current_cmd.set_interrupted();
					//m_current_cmd = null;
				} catch(Exception e) {
					log.debug(e.getMessage());
					e.printStackTrace();
				}
			}
			try {
				if(m_current_cmd.isTimedOut())
					get_callback().actionPerformed(new ActionEvent(m_current_cmd, ActionEvent.ACTION_PERFORMED, "act_cmd_timeout"));
			} catch(Exception e) {
				log.debug(e.getMessage());
				e.printStackTrace();
			}
			m_current_cmd = null;
		}
		private void send_cmd(String sz_cmd) {
	    	log.debug("OUT: " + sz_cmd + "\r");
	    	//m_out.println(sz_cmd + "\r");
	    	try {
	    		m_out.write(sz_cmd + "\r\n");
	    	} catch(Exception e) {
	    		log.debug("Error writing to msgqueue. Closing connection.");
	    		m_b_manual_logoff = true;
	    	}
	    	m_out.flush();
		}
	}
	
	public void run() {
		get_callback().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_add_connection"));
		try {
			get_clientsocket().setKeepAlive(true);
		} catch(Exception e) {
			log.debug("Could not set keepalive - " + e.getMessage());
		}
		get_writethread().start();
		listen();
		get_callback().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_rem_connection"));
		try {
			get_clientsocket().close();
		} catch(Exception e) {
			log.debug("Error: Could not close connection to client");
		}
	}
	
	protected boolean listen() {
		String inputLine, outputLine;
		String sz_reason = "";
		try {
			
			while(1==1) {
				try {
					while ((inputLine = m_in.readLine()) != null) {	
						try {
							//log.debug("IN : " + get_clientsocket().getInetAddress() + " - " +  inputLine);	
							String sz_commandtext = get_protocol().processInput(inputLine);
							if(sz_commandtext!=null) {
								outputLine = get_clientsocket().getInetAddress() + " - " + sz_commandtext;
								//log.debug("OUT: " + outputLine);
							}
							
						} catch(Exception e) {
							log.debug("Error in parsing inputLine");
							//break;
						}
						if(!get_clientsocket().isConnected()) {
							//log.debug("Connection lost");
							break;
						}
					    if(get_protocol().isLoggingOff()) {
					    	//log.debug("Logging off");
					    	break;
					    }
					    if(get_manual_logoff()) {
					    	//log.debug("Server removing dead thread");
					    	sz_reason = "get_manual_logoff()";
					    	break;
					    }
					    
					    //get_writethread().add_to_msgqueue("$" + inputLine);
					}
					break; //inputLine == null, close connection
				} catch(IOException e) {
					log.debug("Error reading, closing connection");
					break;
				} catch(Exception e) {
					log.debug("Buffered reader forced to close");
					logoff();
				}
				if(!get_clientsocket().isConnected()) {
					sz_reason = "!get_clientsocket().isConnected()";
					break;
				}
			    if(get_protocol().isLoggingOff()) {
			    	//log.debug("Logging off");
					sz_reason = "get_protocol().isLoggingOff()";
			    	break;
			    }
			    if(get_manual_logoff()) {
			    	//log.debug("Server removing dead thread");
			    	sz_reason = "get_manual_logoff()";
			    	break;
			    }
			}
			
		} catch(Exception e) {
			log.debug("Listener error " + e.getMessage());
		}
		try {
			get_clientsocket().close();
		} catch(IOException e) {
			
		}
		log.debug("Listener stopped " + sz_reason);
		get_callback().actionPerformed(new ActionEvent(get_protocol().get_unit(), ActionEvent.ACTION_PERFORMED, "act_unit_logoff"));
		m_b_manual_logoff = true; //ensure writethread to stop
		return true;
		
	}
}