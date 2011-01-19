package no.ums.pas.importer.gis;

import no.ums.pas.ums.errorhandling.Error;

import java.io.File;
import java.io.FileWriter;


public class GISWriter {
	//protected File m_f1;
	protected File m_file_out;
	protected LineData m_linedata;
	
	//public File get_original_file() { return m_f1; }
	public File get_umsgis_file() { return m_file_out; }
	protected LineData data() { return m_linedata; }
	
	public GISWriter(LineData data, File f_out) {
		m_file_out = f_out;
		m_linedata = data;
	}
	
	public File convert(int n_mun, int n_str, int n_hou, int n_let, int n_namefilter1, int n_namefilter2, int skip) {
		try {
			FileWriter write = new FileWriter(get_umsgis_file());
			LineData.Line line;
			String sz_mun, sz_street, sz_house, sz_letter, sz_namefilter1, sz_namefilter2;
			for(int i=skip; i < data().get_lines().size(); i++) {
				line = (LineData.Line)data().get_lines().get(i);
				try {
					sz_mun = line.get_row(n_mun);
				} catch(Exception e) {
					sz_mun = "0";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
				try {
					sz_street = line.get_row(n_str);
				} catch(Exception e) {
					sz_street = "0";
				}
				try {
					sz_house = line.get_row(n_hou);
				} catch(Exception e) {
					sz_house = "0";
					Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
				try {
					sz_letter = line.get_row(n_let); //******* Her feiler den
				} catch(Exception e) {
					sz_letter = "";
					//Error.getError().addError("GISWriter","Exception in convert",e,1);
				}
				try {
					sz_namefilter1 = line.get_row(n_namefilter1);
				} catch(Exception e) {
					sz_namefilter1 = "";
				}
				try {
					sz_namefilter2 = line.get_row(n_namefilter2);
				} catch(Exception e) {
					sz_namefilter2 = "";
				}
				//if(sz_mun.trim().length()>0 && sz_street.trim().length()>0 && sz_house.trim().length()>0)
					write_line(write, sz_mun, sz_street, sz_house, sz_letter, sz_namefilter1, sz_namefilter2);
				//else
				//	Error.getError().addError("Error reading line", "Line number " + i + " does not contain enough data (" + line.toString() + ")", 0, 1);
			}
			write.close();
			return get_umsgis_file();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("GISWriter","Exception in convert",e,1);
		}
		return null;
	}
	private boolean write_line(FileWriter w, String c1, String c2, String c3, String c4, String c5, String c6) {
		try {
			w.write(c1 + "\t" + c2 + "\t" + c3 + "\t" + c4 + "\t" + c5 + "\t" + c6 + "\r\n");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("GISWriter","Exception in write_line",e,1);
		}
		return false;
	}
	
}