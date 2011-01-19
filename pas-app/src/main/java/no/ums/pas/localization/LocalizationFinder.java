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
			//System.out.println(new File(PAS.class.getClass().getResource("/localization/").getPath()).toString());
			//File langs = new File(PAS.class.getClass().getResource("/localization/").getPath());//PAS.class.getClass().getResource("/localization").getPath()+File.separatorChar);
			//File langs = new File(LocalizationFinder.class.getClass().getResource("/localization/").getPath());
			ClassLoader cl = LocalizationFinder.class.getClassLoader();//PAS.get_pas().getClass().getClassLoader();//LocalizationFinder.class.getClassLoader();
			//System.out.println("ClassLoader: " + cl);
			//File langs = new File(cl.getResource("no/ums/pas/localization/").getFile());
			URL url = LocalizationFinder.class.getResource("/no/ums/pas/localization/");
			//URLClassLoader ucl = new URLClassLoader(new URL[] { new URL(PAS.get_pas().get_codebase() + "/localization.jar") },cl );
			//System.out.println("Loading URL " + url);
			File langs = new File(cl.getResource("no/ums/pas/localization/").getFile());
			System.out.println("Searching for localization files in " + langs.getAbsolutePath());
			return getAvailableLocales("lang", langs);
			
			/*ClassFile file = new ClassPath().getDirectory("localization/");
			File path = new File(file.getPath());
			return getAvailableLocales("lang", path);*/
			
			//File langs = new File(LocalizationFinder.class.getClass().getResource("/").getPath() + "localization");
			//return getAvailableLocales("lang", langs);
			
			/*ResourceBundle bundle;
			bundle = ResourceBundle.getBundle("localization/lang");
			bundle = ResourceBundle.getBundle("localization/lang");
			System.out.println(bundle);
			return null;*/
			
			//String sz_path = "jar:https://secure.ums2.no/vb4utv/java_pas/localization.jar!/localization/";
			/*String sz_path = "jar:localization.jar/";
			File path = new File(sz_path);
			return getAvailableLocales("lang", path);
			*/
			
			
			
			
			//Path:jar:https://secure.ums2.no/vb4utv/java_pas/localization.jar!/localization/
			//Arranged path:jar:https://secure.ums2.no/vb4utv/java_pas/localization.jar!/localization/

			/*ClassLoader cl = LocalizationFinder.class.getClassLoader();
			URL url_langs = cl.getResource("localization/lang_en_GB.properties");
			String sz_path = url_langs.toString().substring(0, url_langs.toString().lastIndexOf('/')+1);
			System.out.println("Path:" + sz_path);
			sz_path = sz_path.replace("file:/", "");
			System.out.println("Arranged path:" + sz_path);
			File path = new File(sz_path);
			return getAvailableLocales("lang", path);*/
			
			
			
			
			/*String pckgname = "/localization/";
			File directory=null;
			try {
				directory=new File(Thread.currentThread().getContextClassLoader().getResource('/' + pckgname.replace('.', '/')).getPath());
				} 
			catch(NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
			}
			return getAvailableLocales("lang", directory);*/
			
			/*JarFile jarFile = new JarFile("localization.jar");
			Enumeration<JarEntry> en = jarFile.entries();
			while(en.hasMoreElements())
			{
				System.out.println(en.nextElement().getName());
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ArrayList<Locale> defaults = new ArrayList<Locale>();
			//defaults.add(new Locale("no", "NO"));
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