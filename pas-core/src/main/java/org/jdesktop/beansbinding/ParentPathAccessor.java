package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ParentPathAccessor<P, T, V> extends AbstractPathAccessor<P, V> {

    private final IPathAccessor<P, T> parent;
    private final IPathAccessor<T, V> child;

    public ParentPathAccessor(IPathAccessor<P, T> parent, IPathAccessor<T, V> child) {
        super(parent.getPropertyName() + "." + child.getPropertyName(), parent.getTargetType(), child.getValueType());
        this.parent = parent;
        this.child = child;
    }

    @Override
    protected ListenerHandle<P> addPropertyChangeListenerImpl(P instance, final PropertyChangeListener listener) {
        final ListenerHandle<T> handle = child.addPropertyChangeListener(parent.getValue(instance), listener);
        // We have a parent, so we also need to listen to property changes on the parent
        // path accessor, as that will mean that the instance value also changes. So
        // if we are a PathAccessor for "nested.name", we need to be notified when "nested"
        // changes, to propagate that down to the listener.
        return parent.addPropertyChangeListener(instance, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Cast the instance types to T so we can work in a typesafe manner.
                T oldInstance = parent.getValueType().cast(evt.getOldValue());
                T newInstance = parent.getValueType().cast(evt.getNewValue());

                // Read oldValue from the parent old value
                V oldValue = (evt.getOldValue() == null) ? null : child.getValue(oldInstance);
                // Read the new value from the parent new value
                V newValue = (evt.getNewValue() == null) ? null : child.getValue(newInstance);

                // The value has changed, and as such the handle must now point to the new
                // value to listen to changes.
                handle.changeListenTarget(newInstance);

                // Notify the listener about the property change.
                listener.propertyChange(new PropertyChangeEvent(evt.getSource(), child.getPropertyName().getName(), oldValue, newValue));
            }
        });
    }

    @Override
    public V getValue(P instance) {
        return child.getValue(parent.getValue(instance));
    }

    @Override
    public void setValue(P instance, V value) {
        child.setValue(parent.getValue(instance), value);
    }

    @Override
    public boolean isWriteable() {
        return child.isWriteable();
    }
}
