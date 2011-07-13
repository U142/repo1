package org.jdesktop.beansbinding;

import com.google.common.base.Objects;
import org.jdesktop.beansbinding.impl.ListenerHandle;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * This class represents a property path on an arbitrary object.
 *
 *
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanProperty<BT, PT> implements Property<BT, PT> {
    public static <BT, PT> BeanProperty<BT, PT> create(String text) {
        return new BeanProperty<BT, PT>(text);
    }

    public static void addPropertyChangeListener(String property, Object model, PropertyChangeListener listener) {
        create(property).addPropertyChangeListener(model, listener);
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
        return getPathAccessor(src).getValue(src);
    }

    public void write(BT target, PT value) {
        if (!ListenerHandle.THREAD_STACK_VISITED.get().containsKey(target)) {
            getPathAccessor(target).setValue(target, value);
        }
    }

    public boolean isWriteableOn(BT target) {
        return getPathAccessor(target).isWriteable();
    }

    public void addPropertyChangeListener(BT src, PropertyChangeListener listener) {
        getPathAccessor(src).addPropertyChangeListener(src, listener);
    }

    private PathAccessor<BT, PT> getPathAccessor(BT target) {
        @SuppressWarnings("unchecked")
        final Class<BT> targetClass = (Class<BT>) target.getClass();
        return cache.getAccessor(targetClass, propertyName);
    }

    @Override
    public String toString() {
        return "BeanProperty{" + propertyName + '}';
    }

    public Class<? extends PT> getType(BT model) {
        return getPathAccessor(model).getValueType();
    }

    @SuppressWarnings("unchecked")
    public <X extends PT> BeanProperty<BT, X> castValue() {
        return (BeanProperty<BT, X>) this;
    }

    public String describe(BT target) {
        return getPathAccessor(target).toString();
    }
}

