package org.jdesktop.beansbinding;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Bindings {

    public static <SS, SV, TS, TV> Binding<SS, SV, TS, TV> createAutoBinding(AutoBinding.UpdateStrategy strategy, SS sourceObject, BeanProperty<SS, SV> sourceProperty, TS targetObject, BeanProperty<TS, TV> targetProperty) {
        return new Binding<SS, SV, TS, TV>(strategy, sourceObject, sourceProperty, targetObject, targetProperty);
    }

}
