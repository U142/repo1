package org.jdesktop.beansbinding;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
interface ListenerHandle<T> {

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

    static class PropertyListenerHandle<T> implements ListenerHandle<T> {

        private final PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                THREAD_STACK_VISITED.get().put(current, null);
                try {
                    delegate.propertyChange(evt);
                } finally {
                    THREAD_STACK_VISITED.get().remove(current);
                }
            }

            @Override
            public String toString() {
                return "Change on [" + propertyName + "] for [" + current + "] -> " + delegate;
            }
        };

        private T current;
        private final String propertyName;
        private final PropertyChangeListener delegate;

        PropertyListenerHandle(T target, String propertyName, PropertyChangeListener delegate) {
            this.propertyName = propertyName;
            this.delegate = delegate;
            changeListenTarget(target);
        }

        @Override
        public ListenerHandle<T> changeListenTarget(T value) {
            if (current != null) {
                try {
                    current.getClass().getMethod("removePropertyChangeListener", String.class, PropertyChangeListener.class).invoke(current, propertyName, listener);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
                }
            }
            current = value;
            if (current != null) {
                try {
                    current.getClass().getMethod("addPropertyChangeListener", String.class, PropertyChangeListener.class).invoke(current, propertyName, listener);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
                }
            }
            return this;
        }
    }
}
