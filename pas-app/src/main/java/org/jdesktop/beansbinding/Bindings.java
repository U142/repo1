package org.jdesktop.beansbinding;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Bindings {

    public static <SS, TS, V> Binding<SS, TS, V> createAutoBinding(AutoBinding.UpdateStrategy strategy, SS sourceObject, BeanProperty<SS, V> sourceProperty, TS targetObject, BeanProperty<TS, V> targetProperty) {
        return new Binding<SS, TS, V>(strategy, sourceObject, sourceProperty, targetObject, targetProperty);
    }
}
