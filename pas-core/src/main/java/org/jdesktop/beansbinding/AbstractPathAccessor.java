package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.IdentityHashMap;

/**
* @author Ståle Undheim <su@ums.no>
*/
public abstract class AbstractPathAccessor<T, V> implements IPathAccessor<T, V> {

    private final Class<T> type;
    private final Class<V> valueType;
    private final BeanPropertyName propertyName;
    private final IdentityHashMap<T, V> values = new IdentityHashMap<T, V>();

    public AbstractPathAccessor(String name, Class<T> type, Class<V> valueType) {
        this(BeanPropertyName.Factory.of(name), type, valueType);
    }

    public AbstractPathAccessor(BeanPropertyName propertyName, Class<T> type, Class<V> valueType) {
        this.propertyName = propertyName;
        this.type = type;
        this.valueType = valueType;
    }

    @Override
    public ListenerHandle<T> addPropertyChangeListener(T tgt, PropertyChangeListener listener) {
        values.put(tgt, getValue(tgt));
        return addPropertyChangeListenerImpl(type.cast(tgt), listener);
    }

    protected abstract ListenerHandle<T> addPropertyChangeListenerImpl(T instance, PropertyChangeListener listener);

    public void update(T instance, PropertyChangeListener propertyChangeListener) {
        ListenerHandle.THREAD_STACK_VISITED.get().put(instance, null);
        try {
            V oldValue = values.put(instance, getValue(instance));
            propertyChangeListener.propertyChange(new PropertyChangeEvent(instance, getPropertyName().getName(), oldValue, values.get(instance)));
        } finally {
            ListenerHandle.THREAD_STACK_VISITED.get().remove(instance);
        }
    }

    @Override
    public Class<T> getTargetType() {
        return type;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

    @Override
    public BeanPropertyName getPropertyName() {
        return propertyName;
    }
}
