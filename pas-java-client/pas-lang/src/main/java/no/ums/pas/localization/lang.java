package no.ums.pas.localization;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Maps;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class lang {

    interface LangError {

        void logMissing(Locale locale, String key, String valueInDefault);

    }

    public static final LangError NO_ERROR = new LangError() {
        @Override
        public void logMissing(Locale locale, String key, String valueInDefault) {
            // Noop
        }
    };

    private static final ResourceBundle DEFAULT = ResourceBundle.getBundle(lang.class.getName(), new Locale("en", "GB"));

    private final ResourceBundle bundle;
    private final boolean DEBUGMODE;
    private final LangError langError;

    public lang(final Locale locale, boolean debugmode, LangError langError) {
        this.langError = langError;
        DEBUGMODE = debugmode;
        bundle = ResourceBundle.getBundle(getClass().getName(), locale);
    }

    public Locale getLocale() {
        return bundle.getLocale();
    }

    public Map<String,String> valueList(String prefix)
    {
    	Map<String,String> ret = Maps.newHashMap();
    	for(String key : bundle.keySet())
    	{
    		if(key.startsWith(prefix))
    		{
    			ret.put(key, bundle.getString(key));
    		}
    	}
    	return ret;
    }
    
    public String l(String key) {
        if (bundle.containsKey(key)) {
            return (DEBUGMODE) ? "*" + bundle.getString(key) + "*" : bundle.getString(key);
        }
        if (langError != null && DEFAULT.containsKey(key)) {
            langError.logMissing(bundle.getLocale(), key, DEFAULT.getString(key));
        }
        return "[NO STRING]";
    }
}
