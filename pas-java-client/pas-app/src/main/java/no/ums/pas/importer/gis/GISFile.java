package no.ums.pas.importer.gis;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.importer.FileParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

public class GISFile implements ActionListener {

    private static final Log log = UmsLog.getLogger(GISFile.class);

	public static final String [] EXPECTED_SEPARATORS_ = new String [] { "	", ";", ",", "|" };
	private GISParser m_gisparser = null;
	public GISParser get_parser() { return m_gisparser; }
	private PreviewFrame m_preview;
	public void set_preview(PreviewFrame preview) { m_preview = preview; }
	private ActionListener m_callback;
	protected boolean m_b_is_alert = false;
	public boolean getIsAlert() { return m_b_is_alert; }
	protected ActionListener get_callback() { return m_callback; }
	private File m_gisfile;
	private URL m_gisurl;
	public File get_file() { return m_gisfile; }
	public URL get_url() { return m_gisurl; }
	private String encoding;

    public void setImportType(String importType) {
        this.importType = importType;
    }

    private String importType;
	public void set_encoding(String encoding) { this.encoding = encoding; }

	
	public GISFile() {
	}
	public boolean parse(File f, ActionListener callback, String sz_action, boolean bIsAlert) {
		m_gisparser = new GISParser(f, callback);
		m_gisfile = f;
		m_callback = callback;
		m_b_is_alert = bIsAlert;
		get_parser().set_callback(this);
		get_parser().set_action(sz_action);
		get_parser().set_object(this);
		get_parser().begin_parsing(encoding);
		return true;
	}
	public boolean parse(URL url, ActionListener callback, String sz_action, boolean bIsAlert) {
		m_gisparser = new GISParser(url, callback);
		m_gisfile = null;
		m_gisurl = url;
		m_callback = callback;
		m_b_is_alert = bIsAlert;
		get_parser().set_callback(this);
		get_parser().set_action(sz_action);
		get_parser().set_object(this);
		get_parser().begin_parsing(encoding);
		return true;		
	}
	public void actionPerformed(ActionEvent e) {
		if("act_gis_imported_eof".equals(e.getActionCommand())) {
			log.debug("show preview");
			show_preview();
		}
	}
	public void show_preview() {
		if(m_preview == null)
			m_preview = new PreviewFrame(this);
		else {
			m_preview.m_panel.get_previewlist().m_panel.start_search();
		}
		m_preview.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(350, 400));
		if(get_callback()!=null)
			get_callback().actionPerformed(new ActionEvent(m_preview, ActionEvent.ACTION_PERFORMED, "act_register_gis_previewframe"));
	}

	public class GISParser extends FileParser {
		private String m_sz_separator;
//		private int m_b_skiplines = 1;
		private int m_n_max_columns = 0;
		protected void set_max_columns(int n) { if(n > get_max_columns()) m_n_max_columns = n; }
		public int get_max_columns() { return m_n_max_columns; }
		private LineData data;
		public LineData get_linedata() { return data; }
		public String get_separator() { return m_sz_separator; }
		
		public GISParser(File f, ActionListener callback) {
			super(f, callback, "act_gis_imported_eof");
		}
		public GISParser(URL url, ActionListener callback) {
			super(url, callback, "act_gis_imported_eof");
		}
		public boolean create_values() {
			log.debug("create_values");
			//find separator
			m_sz_separator = find_separator();
			if(m_sz_separator.length()==0) {
				//ERROR: manually define one!!
			}
			data = new LineData(m_sz_separator);
			for(int i=0; i < lines().size(); i++) {
				data.add_line(lines().get(i).toString());
			}
			//log.debug("show_preview start");
			//show_preview();
			//log.debug("show_preview end");

			
			return true;
		}
		
		public boolean parse() {
			create_values();
			//printlines();
			return true;
		}
		
		
		public String find_separator() {
			String sz_ret = "";
			int n_separator_inst = 0;
			int n_lines_validated = 0;
			boolean b_possible_match = false;
			int n_num_lines_to_validate = (lines().size());
			for(int i=0; i < EXPECTED_SEPARATORS_.length; i++) {
				//evaluate based on X first lines (or max lines)
				for(int lines = 0; lines < n_num_lines_to_validate; lines++) {
					if(lines().get(lines).toString().trim().length() == 0)
						continue; //skip line
					n_separator_inst = find_separator_instances(lines().get(lines).toString(), EXPECTED_SEPARATORS_[i]);
					//log.debug("separator " + n_separator_inst);
					if(n_separator_inst == 0) {
						b_possible_match = false;
						break;
					}
					b_possible_match = true;
					set_max_columns(n_separator_inst + 1);
					n_lines_validated ++;
				}
				if(n_lines_validated > 0) {//== n_num_lines_to_validate) {
					return EXPECTED_SEPARATORS_[i];
				}
				
			}
			return sz_ret;
		}
		public int find_separator_instances(String sz_line, String sz_separator) {
			int n = 0;
			int n_index = 0;
			while(1==1) {
				n_index = sz_line.indexOf(sz_separator, n_index);
				if(n_index == -1)
					break;
				n++;
				n_index++;
				if(n_index >= sz_line.length()) //end of line
					break;
			}
			return n;
		}
	}
}