package no.ums.pas.importer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import no.ums.pas.core.mainui.*;
import no.ums.pas.importer.gis.LineData;
import no.ums.pas.importer.gis.LineData.Line;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;

public abstract class FileParser extends Thread {
	int m_n_current_pos = 0;
	protected String m_sz;
	public String data() { return m_sz; }
	File m_f;
	DataInputStream m_dis;
	public File get_file() { return m_f; }
	ArrayList<Object> m_lines = new ArrayList<Object>();
	protected ArrayList<Object> lines() { return m_lines; }
	protected ActionListener m_callback;
	protected String m_action;
	protected String m_action_eof;
	protected Object m_object; /*should be abstract file class*/
	public void set_callback(ActionListener a) { m_callback = a; }
	public void set_action(String s) { m_action = s; }
	public void set_object(Object f) { m_object = f; }
	public ActionListener get_callback() { return m_callback; }
	public String get_action() { return m_action; }
	public Object get_object() { return m_object; }
	private ArrayList<SendObject> m_sendings = new ArrayList<SendObject>();
	protected void add_sending(SendObject obj) { m_sendings.add(obj); }
	protected LoadingFrame m_loadingframe = null;
	protected void set_action_eof(String sz_action) { m_action_eof = sz_action; }
	public String get_action_eof() { return m_action_eof; }
	
	public FileParser(File f, ActionListener callback, String sz_action_eof) {
		super("FileParser thread");
		m_f = f;
		set_action_eof(sz_action_eof);
		set_callback(callback);
	}
	public FileParser(DataInputStream dis, ActionListener callback, String sz_action_eof) {
		super("FileParser thread");
		m_dis = dis;
		set_action_eof(sz_action_eof);
		set_callback(callback);
	}
	public boolean read() {
		return readlines();
	}
	public void printlines() {
		for(int i=0; i < m_lines.size(); i++) {
			//PAS.get_pas().add_event((String)m_lines.get(i), null);
		}
	}
	protected boolean readlines() {
		String sz_line;
		try {
			BufferedReader br;
			if(m_f != null)
				br = new BufferedReader(new InputStreamReader(new FileInputStream(m_f)));
			else
				br = new BufferedReader(new InputStreamReader(m_dis));
			
			while(1==1) {
				sz_line = br.readLine();
				if(sz_line==null)
					break;
				m_lines.add(sz_line);
			}
			br.close();
		} catch (FileNotFoundException ex) {
			Error.getError().addError("FileParser","FileNotFoundException in readlines",ex,1);
			return false;
		} catch (IOException ex) {
			Error.getError().addError("FileParser","IOException in readlines",ex,1);
			return false;
		}
		return true;
	}
	public void begin_parsing() {
		//PAS.get_pas().add_event("begin_parsing()");
		start();
	}
	public void run() {
		//PAS.get_pas().add_event("FileParser.run()");
		//m_loadingframe = new LoadingFrame("Opening file", null);
		//m_loadingframe.set_totalitems(0, "Parsing");
		//m_loadingframe.start_and_show();
		//PAS.get_pas().get_drawthread().set_suspended(true);
		try {
			if(read()) {
				try {
					parse();
				} catch(Exception e1) {
					System.out.println(e1.getMessage());
					e1.printStackTrace();
					Error.getError().addError("FileParser","Exception in run",e1,1);
				}
			}
		} catch(Exception e2) {
			System.out.println(e2.getMessage());
			e2.printStackTrace();
			Error.getError().addError("FileParser","Exception in run",e2,1);
		}
		//PAS.get_pas().get_drawthread().set_suspended(false);
		//m_loadingframe.stop_and_hide();
		try {
			ActionFileLoaded event = new ActionFileLoaded(m_object, ActionEvent.ACTION_PERFORMED, m_action, ImportPolygon.MIME_TYPE_SOSI_);		
			m_callback.actionPerformed(event);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("FileParser","Exception in run",e,1);
		}
		try {
			ActionEvent eof = new ActionEvent(get_object(), ActionEvent.ACTION_PERFORMED, get_action_eof());
			get_callback().actionPerformed(eof);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("FileParser","Exception in run",e,1);
		}

	}
	public abstract boolean parse();
	
	private int find_parameter(String sz_param) { //return linenumber
		int n_ret = -1;
		for(int i=0; i < lines().size(); i++) {
			if(lines().get(i).toString().indexOf(sz_param) >= 0)
				return i;
		}
		return n_ret;
	}
	private String get_data(String sz_linedata, String sz_separator) {
		String ret = null;
		if(sz_separator.length() > 0) {
			int n_ret = sz_linedata.indexOf(sz_separator);
			if(n_ret >= 0) {
				ret = sz_linedata.substring(n_ret+1);
			}
		}
		return ret;
	}
	protected ArrayList<String> split(String sz_data, String sz_separator) {
		ArrayList<String> ret = new ArrayList<String>();
		int n_start = 0;
		int n_end = 0;
		//int n_quotestart = -1;
		//int n_quoteend = -1;
		sz_data = sz_data.trim();
		if(sz_data.length()==0)
			return ret;
		if(sz_data.length()==1) {
			ret.add(sz_data);
			return ret;
		}
		//PAS.get_pas().add_event(sz_data);
		boolean b_quoted = false;
		while(1==1) {
			b_quoted = false;
			if(sz_data.charAt(n_start)=='\"') {
				try {
					n_end = sz_data.indexOf("\"", n_start+1); //search for next quot
					b_quoted = true;
				} catch(Exception e) {
					n_end = sz_data.indexOf(sz_separator, n_end+1);
					//PAS.get_pas().add_event("n_end")
					Error.getError().addError("FileParser","Exception in split",e,1);
				}
			} else {
				try {
					n_end = sz_data.indexOf(sz_separator, n_end+1);
				} catch(Exception e) {
					//PAS.get_pas().add_event("n_end = " + n_end, e);
					Error.getError().addError("FileParser","Exception in split",e,1);
				}
			}
			if(n_end > 0) {
				try {
					ret.add(sz_data.substring((b_quoted ? ++n_start : n_start), (b_quoted ? n_end : n_end)));
				} catch(Exception e) {
					//PAS.get_pas().add_event("error ret.add() " + n_start + " to " + n_end, e);
					Error.getError().addError("FileParser","Exception in split",e,1);
				}
				n_start = n_end+1;
				if(n_start >= sz_data.length())
					break;
			} else {
				if(sz_data.length()-1 > n_start) {
					try {
						ret.add(sz_data.substring(n_start, sz_data.length()));
					} catch(Exception e) {
						Error.getError().addError("FileParser","Exception in split",e,1);
					}
				}
				break;
			}
		}
		/*try {
			PAS.get_pas().add_event("Added: " + ret.toString());
		} catch(Exception e) { }*/
		return ret;
	}
	protected ArrayList<String> get_linedata(String sz_param, String sz_separator) {
		ArrayList<String> ret = null;
		int n_pos = find_parameter(sz_param);
		if(n_pos >= 0) {
			//PAS.get_pas().add_event(get_data(lines().get(n_pos).toString(), sz_separator));
			ret = split(get_data(lines().get(n_pos).toString(), sz_separator), " ");
		}
		return ret;
	}
	protected ArrayList<String> get_linedata(String sz_param) { //default separator ""
		return get_linedata(sz_param, " ");
	}
	public abstract boolean create_values();
}
