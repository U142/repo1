package org.jdesktop.beansbinding.impl;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Represents a handle to a listener, where we can change which
 * object is being listened to. This is used by the PathAccessor to update
 * the listen target when a part in a PathAccessor path changes.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface ListenerHandle<T> {

    /**
     * This holds a list of all bean properties visited in the current thread stack.
     * The purpose of this set is to avoid circular updates. If there is a
     * {@link org.jdesktop.beansbinding.AutoBinding.UpdateStrategy#READ_WRITE} strategy between property a and b,
     * setting the value on a, should cause the property b to be update, however that invocation should not cause
     * a to be set as well. This set is to protect against those instances.
     */
    static final ThreadLocal<Map<Object, Object>> THREAD_STACK_VISITED = new ThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new IdentityHashMap<Object, Object>();
        }
    };

    /**
     * Change which instance this handle should be attached to. This should remove
     * the current listener from it's current target, and update the listener to the new
     * value.
     *
     * @param value a new value to listen on.
     *
     * @return this instance.
     */
    ListenerHandle<T> changeListenTarget(T value);

}
