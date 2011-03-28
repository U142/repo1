package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class represents a path to a property, and can read and write values from/to an
 * instance matching it's getter and setter types.
 * <p/>
 * These objects should not be constructed directly, but rather be looked up from
 * {@link PathAccessorCache}.
 * <p/>
 * PathAccessors are also used for nested bean properties. So the bean path "model.nested.name" will
 * call getModel().getNested().getName() for reading, and getModel().getNested().setName(name) for write.
 * Notifications will also be sent to those that listen to "model.nested.name" when
 * getModel().getNested().setName(name) is invoked, getModel.setNested(..) is invoked and setModel(..) is invoked.
 * <p/>
 * PathAccessor syntax:
 * <p/>
 * propertyName[@integer][.propertyName[@integer] ...]
 * <p/>
 * Several properties can be chained by dots. Each name may also have an @integer to indicated an
 * indexed property, and that get and set should be invoked with an additional integer for it's
 * index.
 *
 * @author Ståle Undheim <su@ums.no>
 */
class PathAccessor {

    // Parent path accessor, if any. The propety "nested.name" will have a parent PathAccessor for "nested".
    private final PathAccessor parent;
    // The fill property name, for "nested.name" this will be "nested.name".
    private final String propertyName;
    // The leaf name of the property, for "nested.name" this will be "name".
    private final String leafPropertyName;
    // Getter method from the target class used to get the value from an object.
    private final Method setter;
    // Setter method to update values on a target, null for read only properties.
    private final Method getter;

    PathAccessor(PathAccessor parent, Method setter, Method getter, String propertyName) {
        this.parent = parent;
        this.setter = setter;
        this.getter = getter;
        this.propertyName = propertyName;

        int leafSeperator = propertyName.lastIndexOf('.');
        leafPropertyName = (leafSeperator == -1) ? propertyName : propertyName.substring(leafSeperator + 1);
    }

    /**
     * Returns the actual instance to invoke get and set on.
     * <p/>
     * If this PathAccessor has a parent, we need to read out the leaf value
     * from the parent. The property "nested.name" on an object, should get
     * and set values from the result of object.getNested().
     *
     * @param src the object that this PathAccessor is bound to
     * @return the object to invoke get and set on.
     */
    private Object getInstance(Object src) {
        return (parent == null) ? src : parent.getValue(src);
    }

    /**
     * Reads the value that this PathAccessor identifies on an object.
     *
     * @param instance to read this property from
     * @return the value for this PathAccessor on the target object.
     */
    Object getValue(Object instance) {
        try {
            return invokeGetOn(getter, getInstance(instance));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read property " + propertyName + " by invoking " + getter + " on " + instance, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Failed to read property " + propertyName + " by invoking " + getter + " on " + instance, e);
        }
    }

    /**
     * Writes the value that this PathAccessor identifies on the target instance.
     *
     * @param instance to write to
     * @param value    to write to the instance.
     */
    void setValue(Object instance, Object value) {
        if (setter == null) {
            throw new IllegalStateException("Read only property");
        }
        try {
            invokeSetOn(setter, getInstance(instance), value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to write property " + propertyName + " by invoking " + setter + " on " + instance, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Failed to write property " + propertyName + " by invoking " + setter + " on " + instance, e);
        }
    }

    /**
     * Implementation off the invoke, may be sub classed if the getter takes additional parameters.
     *
     * @param getter   method to read from
     * @param instance to read from
     * @return the value returned from the getter when applied to the instance.
     * @throws IllegalAccessException    If the getter method is not accessible
     * @throws InvocationTargetException if the getter method throws an exception
     */
    protected Object invokeGetOn(Method getter, Object instance) throws IllegalAccessException, InvocationTargetException {
        return getter.invoke(instance);
    }

    /**
     * Implementation off the invoke, may be sub classed if the setter takes additional parameters.
     *
     * @param setter   method to use for writing
     * @param instance to read from
     * @param value    the updated value to write.
     * @throws IllegalAccessException    If the setter method is not accessible
     * @throws InvocationTargetException if the setter method throws an exception
     */
    protected void invokeSetOn(Method setter, Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
        setter.invoke(instance, value);
    }

    /**
     * Adds a property change listener for the property identified by this path accessor to the
     * target object.
     * <p/>
     * The listener will also be notified if any value in the path of this accessor is changed, and
     * thus potentially changing the value being listened to. So if the listener listens to "nested.name",
     * and "nested" is changes, the listener will be notified with the new "name" property value
     * on the "nested" value.
     *
     * @param tgt      object to listen on
     * @param listener to be notified when the property identified by this PathAccessor changes.
     */
    void addPropertyChangeListener(final Object tgt, final PropertyChangeListener listener) {
        // Get the actual instance that we need to listen to. For PathAccessors with parents, we
        // need to get the leaf object to be able to add listeners to it.
        final Object instance = getInstance(tgt);

        // Use the ListenerHandle.Factory to add an appropriate listener for the given property
        // on the instance.
        @SuppressWarnings("unchecked")
        final ListenerHandle<Object> handle = ListenerHandle.Factory.addPropertyChangeListener(instance, leafPropertyName, listener);

        if (parent != null) {
            // We have a parent, so we also need to listen to property changes on the parent
            // path accessor, as that will mean that the instance value also changes. So
            // if we are a PathAccessor for "nested.name", we need to be notified when "nested"
            // changes, to propagate that down to the listener.
            parent.addPropertyChangeListener(tgt, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        // Read oldValue from the parent old value
                        Object oldValue = (evt.getOldValue() == null) ? null : invokeGetOn(getter, evt.getOldValue());
                        // Read the new value from the parent new value
                        Object newValue = (evt.getNewValue() == null) ? null : invokeGetOn(getter, evt.getNewValue());

                        // The value has changed, and as such the handle must now point to the new
                        // value to listen to changes.
                        handle.changeListenTarget(evt.getNewValue());

                        // Notify the listener about the property change.
                        listener.propertyChange(new PropertyChangeEvent(evt.getSource(), leafPropertyName, oldValue, newValue));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Failed to read property " + propertyName + " by invoking " + getter + " on " + instance, e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException("Failed to read property " + propertyName + " by invoking " + getter + " on " + instance, e);
                    }
                }
            });
        }
    }

    /**
     * Returns the type of this PathAccessor
     *
     * @return the type of this PathAccessor
     */
    public Class getValueType() {
        return getter.getReturnType();
    }

    public boolean isWriteable() {
        return setter != null;
    }

    /**
     * Subclass of PathAccessor that will invoke methods with an additional int
     * index on the setter and getter.
     */
    static class Indexed extends PathAccessor {
        private final int index;

        Indexed(PathAccessor parent, Method setter, Method getter, String propertyName, int index) {
            super(parent, setter, getter, propertyName);
            this.index = index;
        }

        @Override
        protected Object invokeGetOn(Method getter, Object instance) throws IllegalAccessException, InvocationTargetException {
            return getter.invoke(instance, index);
        }

        @Override
        protected void invokeSetOn(Method setter, Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
            setter.invoke(instance, index, value);
        }
    }
}
