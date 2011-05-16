package org.jdesktop.beansbinding;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Bindings {

    public static <SS, SV, TS, TV> AutoBinding<SS, SV, TS, TV> createAutoBinding(AutoBinding.UpdateStrategy strategy, SS sourceObject, Property<SS, SV> sourceProperty, TS targetObject, Property<TS, TV> targetProperty) {
        return new AutoBinding<SS, SV, TS, TV>(strategy, sourceObject, (BeanProperty<SS, SV>) sourceProperty, targetObject, (BeanProperty<TS, TV>) targetProperty);
    }

    public static <SS, SV, TS, TV> void bind(AutoBinding.UpdateStrategy strategy, SS sourceObject, BeanProperty<SS, SV> sourceProperty, TS targetObject, BeanProperty<TS, TV> targetProperty) {
        createAutoBinding(strategy, sourceObject, sourceProperty, targetObject, targetProperty).bind();
    }

    public static <SS, TS> void bind(AutoBinding.UpdateStrategy strategy, SS sourceObject, String sourceProperty, TS targetObject, String targetProperty) {
        createAutoBinding(strategy, sourceObject, BeanProperty.create(sourceProperty), targetObject, BeanProperty.create(targetProperty)).bind();
    }

}
