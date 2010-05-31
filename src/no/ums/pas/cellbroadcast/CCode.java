package no.ums.pas.cellbroadcast;

import java.util.Comparator;

public class CCode extends Object implements Comparator {
		private String sz_ccode;
		private String sz_country;
		private String sz_short;
		private int n_ccode = 0;
		private boolean b_visible = false;
		public boolean isVisible() { return b_visible; }
		public String getCCode() { return sz_ccode; }
		public String getCountry() { return sz_country; }
		public String getShort() { return sz_short; }
		public int getNCCode() { return n_ccode; }
		public String toString() { return getCountry(); }
		public String GetCcodeAndCountry() { return getCCode() + " " + getCountry(); }
		
		public CCode(String code, String country, String sh, String visible) {
			sz_ccode = code;
			sz_country = country;
			sz_country = sz_country.replace("\"", "");
			sz_short = sh;
			try {
				n_ccode = Integer.parseInt(code);
			} catch(Exception e) {
				n_ccode = 0;
			}
			if(visible.equals("1"))
				b_visible = true;
		}
		public int compare(Object o1, Object o2) {
			//Collator c = Collator.getInstance();
			CCode c1 = (CCode)o1;
			CCode c2 = (CCode)o2;
			//return c.compare(c1.getCountry(), c2.getCountry());*/
			return ((c1.toString().toLowerCase().
                    compareTo((c2.toString().toLowerCase()))));
		}
		
		public class Compare implements Comparator
		{
			public final int compare ( Object a, Object b ) /*objects of type StatusItemObject*/
			{
				return ((a.toString().toLowerCase().
	                    compareTo((b.toString().toLowerCase()))));
			}
		}		
		
	}