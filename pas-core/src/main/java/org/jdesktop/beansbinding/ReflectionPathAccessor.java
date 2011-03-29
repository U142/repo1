package org.jdesktop.beansbinding;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ReflectionPathAccessor<T, V> extends IPathAccessor.Abstract<T, V> {

    private final BeanPropertyAccessor accessor;
    private final String name;

    public ReflectionPathAccessor(BeanPropertyName prop, Class<T> type, Class<V> valueType) {
        super(prop.getFullName(), type, valueType);
        this.accessor = prop.getAccessor(type);
        this.name = prop.getName();
    }

    @Override
    public V getValue(T instance) {
        return accessor.read(instance, getValueType());
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
    protected void addPropertyChangeListenerImpl(T instance, PropertyChangeListener listener) {
        ListenerHandle.Factory.addPropertyChangeListener(instance, name, listener);
    }


}
