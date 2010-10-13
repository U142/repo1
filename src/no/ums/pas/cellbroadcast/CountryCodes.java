package no.ums.pas.cellbroadcast;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;



import java.awt.event.*;
import java.util.*;

import no.ums.pas.Installer;
import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.FileParser;
import no.ums.pas.importer.gis.LineData;


public class CountryCodes implements ActionListener {
	
	protected static ArrayList<CCode> g_arr_codes = new ArrayList<CCode>();
	protected static boolean g_b_ccloaded = false;
	protected static CountryCodes g_codes = null;
	
	public static CCode getCountryByCCode(String ccode) {
		if(g_b_ccloaded) {
			try {
				int n_ccode = Integer.parseInt(ccode);
				for(int i=0; i < g_arr_codes.size(); i++) {
					if(n_ccode == ((CCode)g_arr_codes.get(i)).getNCCode())
						return (CCode)g_arr_codes.get(i);
						//return ((CCode)g_arr_codes.get(i)).getCountry().replace("\"", "");
				}
				return new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
			} catch(Exception e) {
				return new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
				//return "Non numeric ccode to search for [" + ccode + "]";
			}
		}
		return new CCode("N/A", "CC-File not loaded", "N/A", "0");
	}
	
	public static ArrayList<CCode> getCountryCodes() {
		return g_arr_codes;
	}
	

	
	
	public CountryCodes()
	{
		g_codes = this;
		init();
		
	}
	
	public void actionPerformed(ActionEvent e){
		if("act_done".equals(e.getActionCommand())) {
			System.out.println("CC file loaded");
			g_b_ccloaded = true;
		}
	}
	
	public boolean init() {
		try
		{
			File f = load();
			if(f!=null) {
				CCParser parser = new CCParser(f, this);
			}
			else {
				CCParser parser = new CCParser(loadStream(), this);
			}
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public static File load() {
		//ClassLoader cl = CountryCodes.class.getClassLoader();
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		try {
			//return new URL("cellbroadcast/countrycodes.txt"); //"cellbroadcast/countrycodes.csv");
			//return new File(cl.getSystemResource("countrycodes.txt").getFile());
			//return new URL(CountryCodes.class.getResource("countrycodes.txt")).;
			String source = PAS.get_pas().get_sitename() + "/countrycodes.csv";
			String dest = StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "/countrycodes.txt";
			if(new Installer().download_and_save(source, dest, true)) {
				return new File(dest);
			}
			return null;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			//Error.getError().addError("ImageLoader","Exception in load_icon",e,1);
			return null;
		}		
	}
	
	public static DataInputStream loadStream() {
		try {
			URL url = new URL(PAS.get_pas().get_sitename() + "/countrycodes.csv");
			URLConnection urlConn;
			
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);
			
			return new DataInputStream(urlConn.getInputStream());
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			//Error.getError().addError("ImageLoader","Exception in load_icon",e,1);
			return null;
		}
	}
	
	
	class CCParser extends FileParser {
		LineData data = null;
		
		CCParser(File f, ActionListener callback) {
			super(f, callback, "act_done");
			this.set_object("CC loader");
			begin_parsing();
		}
		CCParser(DataInputStream dis, ActionListener callback) {
			super(dis, callback, "act_done");
			this.set_object("CC loader");
			begin_parsing();
		}
		
		public boolean create_values() {
			data = new LineData("	");
			for(int i=0; i < lines().size(); i++) {
				data.add_line(lines().get(i).toString());
			}	
			return true;
		}
		public boolean parse() {
			create_values();
			String sz_ccode, sz_cname, sz_cshort, sz_cvisible;
			for(int i=0; i < data.get_lines().size(); i++) {
				try {
					//g_arr_codes.add(new CCode(data.get_fields(i).get(0).toString(), data.get_fields(i).get(1).toString(), data.get_fields(i).get(2).toString()));
					sz_ccode = data.get_fields(i).get(0).toString();
				} catch(Exception e) {
					sz_ccode = "-2";
				}
				try {
					sz_cname = data.get_fields(i).get(1).toString();
					sz_cname.replace("\"", "");
				} catch(Exception e) {
					sz_cname = "Unknown";
				}
				try {
					sz_cshort = data.get_fields(i).get(2).toString();
				} catch(Exception e) {
					sz_cshort = "Unknown";
				}
				try {
					sz_cvisible = data.get_fields(i).get(3).toString();
				} catch(Exception e) {
					sz_cvisible = "0";
				}
				g_arr_codes.add(new CCode(sz_ccode, sz_cname, sz_cshort, sz_cvisible));
			}
			Collections.sort(g_arr_codes, g_arr_codes.get(0));
			for(int i=0;i<g_arr_codes.size();++i) {
				if(g_arr_codes.get(i).getNCCode() == -1) {
					CCode tcc = g_arr_codes.remove(i);
					g_arr_codes.add(0, tcc);
				}
			}
			return true;
		}
	}
	
}
class comperator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}