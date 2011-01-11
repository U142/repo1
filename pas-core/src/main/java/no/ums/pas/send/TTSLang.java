package no.ums.pas.send;

public class TTSLang extends Object {
	private int m_n_langpk;
	private String m_sz_name;
	public int get_langpk() { return m_n_langpk; }
	public String get_name() { return m_sz_name; }
	public String toString() { return get_name(); }
	public TTSLang(int n_langpk, String sz_name) {
		m_n_langpk = n_langpk;
		m_sz_name  = sz_name;
	}
	public TTSLang(String [] values) {
		this(new Integer(values[0]).intValue(), values[1]);
	}
}

