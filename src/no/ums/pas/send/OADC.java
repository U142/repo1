package no.ums.pas.send;

public class OADC extends Object {
	private String m_sz_number;
	private int m_n_deptpk;
	private String m_sz_descr;
	public String get_number() { return m_sz_number; }
	public int get_deptpk() { return m_n_deptpk; }
	public String get_descr() { return m_sz_descr; }
	public String toString() { return get_number() + " " + get_descr(); }
	public OADC(String sz_number, int n_deptpk, String sz_descr) {
		m_sz_number = sz_number;
		m_n_deptpk	= n_deptpk;
		m_sz_descr	= sz_descr;
	}
	public OADC(String [] vals) {
		this(vals[0], new Integer(vals[1]).intValue(), vals[2]);
	}
}
