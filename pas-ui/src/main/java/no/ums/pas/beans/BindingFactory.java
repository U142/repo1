package no.ums.pas.beans;

import no.ums.pas.lang.Messages;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Property;

/**
* @author Ståle Undheim <su@ums.no>
*/
public interface BindingFactory<SS, SV> {
    <T, V> AutoBinding<SS, SV, T, V> to(T target, Property<T, V> prop);

    <T, V> AutoBinding<SS, SV, T, V> to(T target, String propname);
}
