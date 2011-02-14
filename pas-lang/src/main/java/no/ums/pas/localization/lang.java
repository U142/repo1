package no.ums.pas.localization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class lang {

    interface LangError {

        void appendBodyFiltered(String s);

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

    public String l(String s) {
        if (bundle.containsKey(s)) {
            return (DEBUGMODE) ? "*" + bundle.getString(s) + "*" : bundle.getString(s);
        }
        if (langError != null && DEFAULT.containsKey(s)) {
            langError.appendBodyFiltered("\n" + s + " = " + DEFAULT.getString(s) + "\n");
        }
        return "[NO STRING]";
    }
}
