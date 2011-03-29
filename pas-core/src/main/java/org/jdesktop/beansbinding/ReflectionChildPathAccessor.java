package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ReflectionChildPathAccessor<P, T, V> extends IPathAccessor.Abstract<P, V> {

    private final IPathAccessor<P, T> parent;
    private final BeanPropertyAccessor accessor;

    public ReflectionChildPathAccessor(IPathAccessor<P, T> parent, BeanPropertyName prop, Class<V> valueType) {
        super(prop.getFullName(), parent.getTargetType(), valueType);
        this.parent = parent;
        this.accessor = prop.getAccessor(parent.getValueType());
    }

    @Override
    public V getValue(P instance) {
        return accessor.read(parent.getValue(instance), getValueType());
    }

    @Override
    public void setValue(P instance, V value) {
        accessor.write(parent.getValue(instance), value);
    }

    @Override
    public boolean isWriteable() {
        return accessor.getSetter() != null;
    }

    @Override
    protected void addPropertyChangeListenerImpl(final P instance, final PropertyChangeListener listener) {
        @SuppressWarnings("unchecked")
        final ListenerHandle<Object> handle = ListenerHandle.Factory.addPropertyChangeListener(parent.getValue(instance), getPropertyName(), listener);
        parent.addPropertyChangeListener(instance, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Read oldValue from the parent old value
                @SuppressWarnings("unchecked")
                V oldValue = (evt.getOldValue() == null) ? null : accessor.read(evt.getOldValue(), getValueType());
                // Read the new value from the parent new value
                @SuppressWarnings("unchecked")
                V newValue = (evt.getNewValue() == null) ? null : accessor.read(evt.getNewValue(), getValueType());

                // The value has changed, and as such the handle must now point to the new
                // value to listen to changes.
                handle.changeListenTarget(evt.getNewValue());

                // Notify the listener about the property change.
                listener.propertyChange(new PropertyChangeEvent(evt.getSource(), getPropertyName(), oldValue, newValue));
            }
        });
    }


}
