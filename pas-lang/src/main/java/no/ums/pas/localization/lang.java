package no.ums.pas.localization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class lang {

    interface LangError {

        void logMissing(Locale locale, String key, String valueInDefault);

    }

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
