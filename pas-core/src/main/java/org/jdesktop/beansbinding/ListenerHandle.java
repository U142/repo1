package org.jdesktop.beansbinding;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    static class Factory {
        static ListenerHandle addPropertyChangeListener(final Object target, final String propertyName, final PropertyChangeListener propertyChangeListener) {
            if (propertyName.equals("text") && target instanceof JTextComponent) {
                return new JTextComponentTextListenerHandle((JTextComponent) target, propertyName, propertyChangeListener);
            } else if (propertyName.equals("selectedItem") && target instanceof JComboBox) {
                return new JComboBoxSelectedItemListenerHandle((JComboBox) target, propertyName, propertyChangeListener);
            } else if (propertyName.equals("selected") && target instanceof AbstractButton) {
                return new AbstractButtonSelectedListenerHandle((AbstractButton) target, propertyName, propertyChangeListener);
            } else {
                return new PropertyListenerHandle(target, propertyName, propertyChangeListener);
            }
        }
    }

    /**
     * Change which instance this handle should be attached to. This should remove
     * the current listener from it's current target, and update the listener to the new
     * value.
     *
     * @param value a new value to listen on.
     */
    void changeListenTarget(T value);


    /**
     * Abstract ListenerHandle to implement handlers that require custom listeners.
     * <p/>
     * Subclasses should call "update()" whenever an event is triggered that causes
     * the value of the object to change.
     * <p/>
     * This class requires that subclasses know how to read the current value from the
     * target object.
     *
     * @param <T> Type that this handler is bound to
     * @param <L> Listener type for this handler.
     */
    static abstract class AbstractListenerHandler<T, L> implements ListenerHandle<T> {

        private final L listener;
        private T current;
        private Object currentValue;
        private final String propertyName;
        private final PropertyChangeListener propertyChangeListener;


        public AbstractListenerHandler(T target, String propertyName, PropertyChangeListener propertyChangeListener) {
            this.listener = createListener();
            this.propertyName = propertyName;
            this.propertyChangeListener = propertyChangeListener;
            changeListenTarget(target);
        }

        @Override
        public void changeListenTarget(T value) {
            if (current != null) {
                removeListener(value, listener);
            }
            current = value;
            if (current != null) {
                currentValue = getValue(current);
                addListener(value, listener);
            }
        }

        /**
         * Implement this method to get the current value of the bound property on an instance.
         *
         * @param instance to get the value for
         * @return the current value for the instance.
         */
        protected abstract Object getValue(T instance);

        /**
         * Creates a new Listener of the desired type. The listener should just invoke
         * {@link #update()} on all events that cause a value change, and the update
         * will broadcast the change to the propertyChangeListener correctly.
         * <p/>
         * Only called once during instance construction.
         *
         * @return a new listener.
         */
        protected abstract L createListener();

        /**
         * Adds a listener to a value.
         * <p/>
         * This method should call the proper add listener method on the value.
         *
         * @param value    to add a listener to.
         * @param listener to add
         */
        protected abstract void addListener(T value, L listener);

        /**
         * Removes a listener from a value.
         * <p/>
         * This method should call the proper remove listener method on the value.
         *
         * @param value    to remove a listener from
         * @param listener to remove
         */
        protected abstract void removeListener(T value, L listener);

        /**
         * Send an updated value to the property change listener.
         * <p/>
         * This method should be invoked by listeners returned from {@link #createListener()}
         * whenver an event occurs that indicates a value change.
         */
        public void update() {
            THREAD_STACK_VISITED.get().put(current, null);
            try {
                final Object oldValue = currentValue;
                currentValue = getValue(current);
                propertyChangeListener.propertyChange(new PropertyChangeEvent(current, propertyName, oldValue, currentValue));
            } finally {
                THREAD_STACK_VISITED.get().remove(current);
            }
        }
    }

    /**
     * ListenerHandle implementation for "text" property on JTextComponents.
     * <p/>
     * Since the "text" property on JTextComponents don't publish change on the "text"
     * property, we need to attach a DocumentListener instead.
     */
    static class JTextComponentTextListenerHandle extends AbstractListenerHandler<JTextComponent, DocumentListener> {

        public JTextComponentTextListenerHandle(JTextComponent target, String propertyName, PropertyChangeListener propertyChangeListener) {
            super(target, propertyName, propertyChangeListener);
        }

        @Override
        protected DocumentListener createListener() {
            return new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }
            };
        }

        @Override
        protected Object getValue(JTextComponent instance) {
            return instance.getText();
        }

        @Override
        protected void addListener(JTextComponent value, DocumentListener listener) {
            value.getDocument().addDocumentListener(listener);
        }

        @Override
        protected void removeListener(JTextComponent value, DocumentListener listener) {
            value.getDocument().removeDocumentListener(listener);
        }

    }

    static class AbstractButtonSelectedListenerHandle extends AbstractListenerHandler<AbstractButton, ChangeListener> {

        public AbstractButtonSelectedListenerHandle(AbstractButton target, String propertyName, PropertyChangeListener propertyChangeListener) {
            super(target, propertyName, propertyChangeListener);
        }

        @Override
        protected Object getValue(AbstractButton instance) {
            return instance.isSelected();
        }

        @Override
        protected ChangeListener createListener() {
            return new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                }
            };
        }

        @Override
        protected void addListener(AbstractButton value, ChangeListener listener) {
            value.addChangeListener(listener);
        }

        @Override
        protected void removeListener(AbstractButton value, ChangeListener listener) {
            value.removeChangeListener(listener);
        }
    }

    static class JComboBoxSelectedItemListenerHandle extends AbstractListenerHandler<JComboBox, ItemListener> {

        public JComboBoxSelectedItemListenerHandle(JComboBox target, String propertyName, PropertyChangeListener propertyChangeListener) {
            super(target, propertyName, propertyChangeListener);
        }

        @Override
        protected Object getValue(JComboBox instance) {
            return instance.getSelectedItem();
        }

        @Override
        protected ItemListener createListener() {
            return new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    update();
                }
            };
        }

        @Override
        protected void addListener(JComboBox value, ItemListener listener) {
            value.addItemListener(listener);
        }

        @Override
        protected void removeListener(JComboBox value, ItemListener listener) {
            value.removeItemListener(listener);
        }
    }

    static class PropertyListenerHandle implements ListenerHandle {

        private final PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                THREAD_STACK_VISITED.get().put(current, null);
                try {
                    propertyChangeListener.propertyChange(evt);
                } finally {
                    THREAD_STACK_VISITED.get().remove(current);
                }
            }

            @Override
            public String toString() {
                return "Change on [" + propertyName + "] for [" + current + "] -> " + propertyChangeListener;
            }
        };

        private Object current;
        private final String propertyName;
        private final PropertyChangeListener propertyChangeListener;

        private PropertyListenerHandle(Object target, String propertyName, PropertyChangeListener propertyChangeListener) {
            this.propertyName = propertyName;
            this.propertyChangeListener = propertyChangeListener;
            changeListenTarget(target);
        }

        @Override
        public void changeListenTarget(Object value) {
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
        }
    }
}
