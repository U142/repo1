package no.ums.pas.status;

import java.awt.Color;

public class StatusCode extends Object implements Comparable {
	private int m_n_code;
	private boolean m_b_isuserdefined; //if a custom text is set in profile
	private String m_sz_status;
	private int m_n_current_count = 0;
	private Color m_color;
	private boolean m_b_alert = false;
	private int m_n_alertborder = 0;
	private int m_n_maxborder = 5;
	private boolean m_b_borderdirection = true; //true = up, false = down
	private boolean m_b_visible = true;
	private boolean m_b_reserved = false; //for parsing/queue/send
	private boolean m_b_addedtolist = false;
	
	public int get_code() { return m_n_code; }
	public String get_status() { return m_sz_status; }
	public int get_current_count() { return m_n_current_count; }
	public void set_current_count(int n_count) { m_n_current_count = n_count; }
	public Color get_color() { return m_color; }
	public void set_color(Color col) { m_color = col; }
	public void set_alert(boolean b_alert) { m_b_alert = b_alert; }
	public boolean get_alert() { return m_b_alert; }
	public boolean get_visible() { return m_b_visible; }
	public void set_visible(boolean b_visible) { m_b_visible = b_visible; }
	public boolean get_reserved() { return m_b_reserved; }
	public boolean get_addedtolist() { return m_b_addedtolist; }
	public void set_addedtolist() { m_b_addedtolist = true; }
	public String toString() { return new Integer(m_n_code).toString(); }
	public boolean isUserDefined() { return m_b_isuserdefined; }
	public int compareTo(Object obj) {
		//return ((StatusCode)obj).toString().compareTo(this.toString());
		if(this.m_n_code < 0)
		{
			return -1;
		}
		if(this.m_b_isuserdefined && !((StatusCode)obj).isUserDefined())
		{
			return -1;
		}
		if(this.m_b_isuserdefined && ((StatusCode)obj).isUserDefined())
		{
			return (this.m_n_code > ((StatusCode)obj).m_n_code ? 1 : -1);
		}
		return (this.m_n_code > ((StatusCode)obj).m_n_code ? 1 : -1);
			
	}
	
	public void update(StatusCode obj) {
		this.m_sz_status = obj.m_sz_status;
		this.m_n_current_count = obj.m_n_current_count;
	}
	
	public void inc_alertborder() { 
		//m_n_alertborder = (m_n_alertborder + 1) % m_n_maxborder;
		if(m_b_borderdirection)
			m_n_alertborder++;
		else
			m_n_alertborder--;
		if(m_n_alertborder >= m_n_maxborder)
			m_b_borderdirection = false;
		else if(m_n_alertborder <= 0)
			m_b_borderdirection = true;
		
	}
	public int get_alertborder() { return m_n_alertborder; }
	
	public StatusCode(int n_code, String sz_status, boolean b_isuserdefined)
	{
		m_n_code = n_code;
		m_sz_status = sz_status;
	}
	public StatusCode(String [] sz_values)
	{
		m_n_code	= new Integer(sz_values[0]).intValue();
		m_sz_status = sz_values[1];
		try
		{
			m_b_isuserdefined = new Boolean(sz_values[2]);
		}
		catch(Exception e) {
			m_b_isuserdefined = false;
		}
	}
	public StatusCode(int n_code, String sz_status, boolean b_reserved, int n_count, boolean b_isuserdefined) {
		m_n_code = n_code;
		m_sz_status = sz_status;
		m_b_reserved = b_reserved;
		m_n_current_count = n_count;
		m_b_isuserdefined = b_isuserdefined;
	}
}
