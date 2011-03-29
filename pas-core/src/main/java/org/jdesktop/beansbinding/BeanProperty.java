package org.jdesktop.beansbinding;

import com.google.common.base.Objects;

import java.beans.PropertyChangeListener;

/**
 * This class represents a property path on an arbitrary object.
 *
 *
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanProperty<BT, PT> {
    public static <BT, PT> BeanProperty<BT, PT> create(String text) {
        return new BeanProperty<BT, PT>(text);
    }

    /**
     * Global cache off all path accessors.
     */
    private static final PathAccessorCache cache = new PathAccessorCache();

    private final String propertyName;

    public BeanProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @SuppressWarnings("unchecked")
    public PT read(BT src) {
        return (PT) getPathAccessor(src).getValue(src);
    }

    public void write(BT target, PT value) {
        if (!ListenerHandle.THREAD_STACK_VISITED.get().containsKey(target) && !Objects.equal(value, read(target))) {
            getPathAccessor(target).setValue(target, value);
        }
    }

    public boolean isWriteableOn(BT target) {
        return getPathAccessor(target).isWriteable();
    }

    public void addPropertyChangeListener(BT src, PropertyChangeListener listener) {
        getPathAccessor(src).addPropertyChangeListener(src, listener);
    }

    private IPathAccessor<BT, PT> getPathAccessor(BT target) {
        return cache.getAccessor((Class<BT>) target.getClass(), propertyName);
    }

    @Override
    public String toString() {
        return "BeanProperty{" + propertyName + '}';
    }

}

