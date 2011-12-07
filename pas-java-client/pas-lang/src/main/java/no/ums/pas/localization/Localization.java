package no.ums.pas.localization;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.beans.PropertyChangeSupport;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.UIManager;

/**
 * Central handle for localization messages.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public enum Localization {

    INSTANCE;

    public static String l(String key) {
        return INSTANCE.val.l(key);
    }
    
    public static Map<String,String> valuesStartingWith(String prefix)
    {
    	return INSTANCE.val.valueList(prefix);
    }

    private lang val = new lang(new Locale("en", "GB"), false, new DefaultLangError());


    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Locale getLocale() {
        return val.getLocale();
    }

    public void setLocale(final Locale locale) {
        if (!locale.equals(val.getLocale())) {
            final Locale old = getLocale();
            val = new lang(locale, false, new DefaultLangError());
            propertyChangeSupport.firePropertyChange("locale", old, locale);
            
            ResourceBundle prb = ResourceBundle.getBundle(lang.class.getName(), new Locale(locale.getLanguage(), locale.getCountry()));
            for(String key : prb.keySet())
            {
            	UIManager.getDefaults().put(key, prb.getString(key));
            }
            //TODO: Finne ut hvorfor ikke lasting av bundle funker automagisk. det funker kun ved lokal kjøring.
            //UIManager.getDefaults().addResourceBundle(String.format("no/ums/pas/localization/lang_%s_%s", locale.getLanguage(), locale.getCountry()));
        }
    }


    private static class DefaultLangError implements lang.LangError {

        private static final Log log = UmsLog.getLogger(DefaultLangError.class);

        @Override
        public void logMissing(Locale locale, String key, String valueInDefault) {
            log.warn("Missing key for locale [%s], key [%s], default value: [%s]", locale, key, valueInDefault);
        }
    }
}
