package no.ums.pas.lang;

import no.ums.pas.beans.BindingFactory;
import org.jdesktop.beansbinding.Property;

import java.beans.PropertyChangeListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface Messages {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Default {
        String value();
    }

    void setLocale(Locale locale);

    Locale getLocale();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    Property<Messages, String> property(String name);

    BindingFactory<Messages, String> autoBind(String property);

}
