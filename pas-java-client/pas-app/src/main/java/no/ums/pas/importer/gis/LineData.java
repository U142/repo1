package no.ums.pas.importer.gis;

import java.util.ArrayList;


public class LineData extends Object {
	private ArrayList<Line> m_lines;
	public ArrayList<Line> get_lines() { return m_lines; }
	public Line get(int n_line) { return ((Line)get_lines().get(n_line)); }
	public ArrayList<String> get_fields(int n_line) { return ((Line)get_lines().get(n_line)).get_fields(); }
	private String m_sz_separator;
	public String getSeparator() { return m_sz_separator; }
	
	public LineData(String sz_separator) {
		m_lines = new ArrayList<Line>();
		m_sz_separator = sz_separator;
	}
	public Line add_line(String sz_line) {
		Line line = new Line(sz_line);
		get_lines().add(line);
		return line;
	}
	public Object [] toArray(int n_line) {
		return get(n_line).toArray();
	}
	public Object [] toArray(int n_line, int n_forcecolumns) {
		//log.debug("force = " +toArray(n_line).length);
		if(toArray(n_line).length > n_forcecolumns)
			return toArray(n_line);
		Object [] obj = new Object[n_forcecolumns];
		Object [] src = toArray(n_line);
		for(int i=0; i < n_forcecolumns; i++)
			if(i < src.length)
				obj[i] = src[i];
			else
				obj[i] = new String("");
		return obj;
	}
	public boolean search_line(int n_line, int n_row, String sz_data) {
		if(get_fields(n_line).get(n_row).toString().startsWith(sz_data)) {
			return true;
		}
		else return false;
	}
	
	public class Line extends Object {
		private String raw;
		private ArrayList<String> m_fields;
		public ArrayList<String> get_fields() { return m_fields; }
		public Line(String sz_line) {
			raw = sz_line;
			m_fields = new ArrayList<String>();
			parse(sz_line);
		}
		public String getRawdata() { return raw; }
		public Object [] toArray() {
			return get_fields().toArray();
		}
		public String toString() {
			String sz_ret = "linedata: ";
			for(int i=0; i < get_fields().size(); i++) {
				sz_ret += (i>0 ? m_sz_separator : "") + get_fields().get(i).toString(); 
			}
			return sz_ret;
		}
		public void parse(String sz_line) {
			String [] sz_fields = sz_line.split(m_sz_separator);
			add_fields(sz_fields);
		}
		public void add_fields(String [] sz_fields) {
			for(int i=0; i < sz_fields.length; i++)
				add_field(sz_fields[i]);
		}
		public void add_field(String sz_field) {
			get_fields().add(sz_field);
		}
		public String get_row(int n_column) {
			if(get_fields().size()-1 >= n_column)
				return (String)get_fields().get(n_column);
			else
				return "";
		}
	}
}