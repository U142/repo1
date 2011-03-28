package org.jdesktop.beansbinding;

/**
 * Implementation off the Beansbinding API for FormDev.
 * <p/>
 * This just constructos a BeanProperty by calling {@link BeanProperty#create(String)}.
 * In our implementation there is no distinction between properties and ELProperty, however
 * FormDev expects to call this method when we specify an expression rather than a single
 * property.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class ELProperty {
    public static <SS, SV> BeanProperty<SS, SV> create(String s) {
        return BeanProperty.create(s);
    }
}
