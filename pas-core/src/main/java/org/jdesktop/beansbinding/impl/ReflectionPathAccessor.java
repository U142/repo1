package org.jdesktop.beansbinding.impl;

import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ReflectionPathAccessor<T, V> extends AbstractPathAccessor<T, V> {

    private final BeanPropertyAccessor accessor;

    public ReflectionPathAccessor(BeanPropertyName prop, Class<T> type, Class<V> valueType) {
        super(prop.getFullName(), type, valueType);
        this.accessor = BeanPropertyAccessor.Factory.of(prop, type);
    }

    @Override
    public V getValue(T instance) {
        return accessor.<V>read(instance);
    }

    @Override
    public void setValue(T instance, V value) {
        accessor.write(instance, value);
    }

    @Override
    public boolean isWriteable() {
        return accessor.getSetter() != null;
    }

    @Override
    public ListenerHandle<T> addPropertyChangeListener(T instance, PropertyChangeListener listener) {
        return new ListenerHandle.PropertyListenerHandle<T>(instance, getPropertyName().getName(), listener);
    }


}
