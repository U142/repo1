package org.jdesktop.beansbinding;

import com.google.common.collect.ImmutableMap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.IdentityHashMap;

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
public interface PathAccessor<T, V> {

    ImmutableMap<Class, Object> DEFAULT_VALUES = ImmutableMap.<Class, Object>builder()
            .put(boolean.class, false)
            .put(char.class, '\u0000')
            .put(byte.class, 0)
            .put(short.class, 0)
            .put(int.class, 0)
            .put(long.class, 0l)
            .put(float.class, 0.0)
            .put(double.class, 0.0d)
            .build();

    /**
     * The fully qualified property name that this path accessor is for.
     * @return the full property name.
     */
    BeanPropertyName getPropertyName();

    /**
     * Reads the value that this PathAccessor identifies on an object.
     *
     * @param instance to read this property from
     * @return the value for this PathAccessor on the target object.
     */
    V getValue(T instance);

    /**
     * Writes the value that this PathAccessor identifies on the target instance.
     *
     * @param instance to write to
     * @param value    to write to the instance.
     */
    void setValue(T instance, V value);

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
     * @return A handle that can be used to change the instance to listen on.
     */
    ListenerHandle<T> addPropertyChangeListener(final T tgt, final PropertyChangeListener listener);

    /**
     * Returns the class representing the T type for this class.
     * @return the class representing the T type for this class.
     */
    Class<T> getTargetType();

    /**
     * Returns the class representing the V type for this class.
     * @return the class representing the V type for this class.
     */
    Class<V> getValueType();

    /**
     * Returns true if this property is readable.
     *
     * If this property is not writeable, calling {@link #setValue(Object, Object)} will
     * cause an {@link IllegalStateException}.
     *
     * @return if this path accessor can write.
     */
    boolean isWriteable();
}
