package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.IdentityHashMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface IPathAccessor<T, V> {

    abstract class Abstract<T, V> implements IPathAccessor<T, V> {

        private final Class<T> type;
        private final Class<V> valueType;
        private final String propertyName;
        private final IdentityHashMap<T, V> values = new IdentityHashMap<T, V>();

        public Abstract(String propertyName, Class<T> type, Class<V> valueType) {
            this.propertyName = propertyName;
            this.type = type;
            this.valueType = valueType;
        }

        @Override
        public void addPropertyChangeListener(T tgt, PropertyChangeListener listener) {
            values.put(tgt, getValue(tgt));
            addPropertyChangeListenerImpl(type.cast(tgt), listener);
        }

        protected abstract void addPropertyChangeListenerImpl(T instance, PropertyChangeListener listener);

        public void update(T instance, PropertyChangeListener propertyChangeListener) {
            ListenerHandle.THREAD_STACK_VISITED.get().put(instance, null);
            try {
                V oldValue = values.put(instance, getValue(instance));
                propertyChangeListener.propertyChange(new PropertyChangeEvent(instance, getPropertyName(), oldValue, values.get(instance)));
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
        public String getPropertyName() {
            return propertyName;
        }
    }

    String getPropertyName();

    V getValue(T instance);

    void setValue(T instance, V value);

    void addPropertyChangeListener(final T tgt, final PropertyChangeListener listener);

    Class<T> getTargetType();

    Class<V> getValueType();

    boolean isWriteable();
}
