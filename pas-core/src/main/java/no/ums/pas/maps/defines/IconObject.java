package no.ums.pas.maps.defines;

import javax.swing.ImageIcon;

public class IconObject extends Object {
	//l_picturepk, l_comppk, l_deptpk, sz_name, l_opacity, l_sizetype, sz_fileext
	private String m_sz_picturepk;
	private int m_n_comppk;
	private int m_n_deptpk;
	private String m_sz_name;
	private int m_n_opacity;
	private int m_n_size;
	private String m_sz_fileext;
	private String m_sz_filename;
	public String get_picturepk() { return m_sz_picturepk; }
	public int get_comppk() { return m_n_comppk; }
	public int get_deptpk() { return m_n_deptpk; }
	public String get_name() { return m_sz_name; }
	public int get_opacity() { return m_n_opacity; }
	public int get_size() { return m_n_size; }
	public String get_fileext() { return m_sz_fileext; }
	public String get_filename() { return m_sz_filename; }
	private ImageIcon m_icon = null;
	public ImageIcon get_icon() { return m_icon; }
	public void set_icon(ImageIcon icon) { m_icon = icon; }
	
	public IconObject(String [] sz_values) {
		this(sz_values[0], sz_values[1], sz_values[2], sz_values[3], sz_values[4], sz_values[5],
				   sz_values[6]);
	}
	public IconObject(String sz_picturepk, String sz_comppk, String sz_deptpk, String sz_name, String sz_opacity,
			   String sz_size, String sz_fileext) {
		m_sz_picturepk	= sz_picturepk;
		m_n_comppk		= new Integer(sz_comppk).intValue();
		m_n_deptpk		= new Integer(sz_deptpk).intValue();
		m_sz_name		= sz_name;
		m_n_opacity		= new Integer(sz_opacity).intValue();
		m_n_size		= new Integer(sz_size).intValue();
		m_sz_fileext	= sz_fileext;
		m_sz_filename	= sz_picturepk + "." + sz_fileext;
		
	}
}