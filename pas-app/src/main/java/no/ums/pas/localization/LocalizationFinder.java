package no.ums.pas.localization;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LocalizationFinder
{
    private static final Log log = UmsLog.getLogger(LocalizationFinder.class);

	static Pattern localePattern = Pattern.compile("(^[a-z]{2})(_([A-Z]{2})){0,1}$");
	public static List<Locale> getAvailableLangfiles()
	{
			return Arrays.asList(new Locale("en", "GB"));
	}
	protected static ArrayList<Locale> getAvailableLocales(String bundleName, File dir) {
		try
		{
			boolean b_at_least_one_found = false;
			Pattern bundleFilePattern = Pattern.compile("^"+bundleName+"_([a-z]{2}(_([A-Z]{2}))).properties$");
			ArrayList<Locale> locales = new ArrayList<Locale>();
			String[] bundles = dir.list();
			if(bundles!=null)
				log.debug("Bundles.length: " + bundles.length);
			else
				return null;
			for (int i = 0; i < bundles.length; i++) {
				Matcher m = bundleFilePattern.matcher(bundles[i]);
				if (m.find()) {
					locales.add(getLocale(m.group(1)));
					b_at_least_one_found = true;
				}
			}
			log.debug("Found at least one language=" + b_at_least_one_found);
			return (b_at_least_one_found ? locales : null);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
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