package no.ums.pas.cellbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public enum CountryCodes {//implements ActionListener {
	INSTANCE;
	protected final ArrayList<CCode> g_arr_codes = new ArrayList<CCode>();

	CountryCodes() {
		InputStream is = getClass().getResourceAsStream("CountryCodes.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try
		{
			while ((line = br.readLine()) != null) {
				List<String> row = Arrays.asList(line.split("\t"));
				String sz_ccode, sz_cname, sz_cshort, sz_cvisible;
                sz_ccode = (!row.isEmpty()) ? row.get(0) : "-2";
                sz_cname = (row.size() > 1) ? row.get(1).replace("\"", "") : "Unknown";
                sz_cshort = (row.size() > 2) ? row.get(2) : "Unknown";
                sz_cvisible = (row.size() > 3) ? row.get(3) : "0";
				g_arr_codes.add(new CCode(sz_ccode, sz_cname, sz_cshort, sz_cvisible));

			}
		}
		catch(IOException e)
		{
			
		}
		
	}


	public static CCode getCountryByCCode(String ccode) {
		try {
			int n_ccode = Integer.parseInt(ccode);
            for (CCode g_arr_code : INSTANCE.g_arr_codes) {
                if (n_ccode == g_arr_code.getNCCode()) {
                    return g_arr_code;
                }
            }
			return new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
		} catch(Exception e) {
			return new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
			//return "Non numeric ccode to search for [" + ccode + "]";
		}
		//return new CCode("N/A", "CC-File not loaded", "N/A", "0");
	}
	
	public static ArrayList<CCode> getCountryCodes() {
		return INSTANCE.g_arr_codes;
	}

	
}