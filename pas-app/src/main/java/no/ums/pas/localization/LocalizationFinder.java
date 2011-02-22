package no.ums.pas.localization;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LocalizationFinder
{
	static Pattern localePattern = Pattern.compile("(^[a-z]{2})(_([A-Z]{2})){0,1}$");
	public static ArrayList<Locale> getAvailableLangfiles()
	{
		try
		{
			ClassLoader cl = LocalizationFinder.class.getClassLoader();//PAS.get_pas().getClass().getClassLoader();//LocalizationFinder.class.getClassLoader();
			URL url = LocalizationFinder.class.getResource("/no/ums/pas/localization/");
			File langs = new File(cl.getResource("no/ums/pas/localization/").getFile());
			//System.out.println("Searching for localization files in " + langs.getAbsolutePath());
			ArrayList<Locale> defaults = new ArrayList<Locale>();
			defaults.add(new Locale("en", "GB"));
			return defaults;
			//return getAvailableLocales("lang", langs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ArrayList<Locale> defaults = new ArrayList<Locale>();
			defaults.add(new Locale("en", "GB"));
			return defaults;
		}
	}
	protected static ArrayList<Locale> getAvailableLocales(String bundleName, File dir) {
		try
		{
			boolean b_at_least_one_found = false;
			Pattern bundleFilePattern = Pattern.compile("^"+bundleName+"_([a-z]{2}(_([A-Z]{2}))).properties$");
			ArrayList<Locale> locales = new ArrayList<Locale>();
			String[] bundles = dir.list();
			if(bundles!=null)
				System.out.println("Bundles.length: " + bundles.length);
			else
				return null;
			for (int i = 0; i < bundles.length; i++) {
				Matcher m = bundleFilePattern.matcher(bundles[i]);
				if (m.find()) {
					locales.add(getLocale(m.group(1)));
					b_at_least_one_found = true;
				}
			}
			System.out.println("Found at least one language=" + b_at_least_one_found);
			return (b_at_least_one_found ? locales : null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}		
	public static Locale getLocale(String ident) {
		Locale locale = null;
		Matcher m = localePattern.matcher(ident);
		if (m.find()) {
			locale = (m.group(3) == null)? new Locale(m.group(1)):new Locale(m.group(1),m.group(3));
		}
		return locale;
	}

}