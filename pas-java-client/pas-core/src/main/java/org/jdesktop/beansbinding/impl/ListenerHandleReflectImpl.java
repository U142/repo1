package org.jdesktop.beansbinding.impl;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

/**
* @author Ståle Undheim <su@ums.no>
*/
public class ListenerHandleReflectImpl<T> implements ListenerHandle<T> {

    private static final Log log = UmsLog.getLogger(ListenerHandleReflectImpl.class);

    private T current;
    private final DelegatePropertyChangeListener delegate;

    public ListenerHandleReflectImpl(T target, String propertyName, PropertyChangeListener delegate) {
        this.delegate = new DelegatePropertyChangeListener(propertyName, delegate) {
            @Override
            protected Object getInstance() {
                return current;
            }
        };
        changeListenTarget(target);
    }

    @Override
    public ListenerHandle<T> changeListenTarget(T value) {
        final String propertyName = delegate.getPropertyName();
        if (current != null) {
            try {
                current.getClass().getMethod("removePropertyChangeListener", String.class, PropertyChangeListener.class).invoke(current, propertyName, delegate);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
            } catch (NoSuchMethodException e) {
                log.debug("Cannot bind listener to "+propertyName+" on "+current, e);
                // throw new IllegalStateException("Failed to un-bind property " + propertyName + " on " + current, e);
            }
        }
        current = value;
        if (current != null) {
            try {
                current.getClass().getMethod("addPropertyChangeListener", String.class, PropertyChangeListener.class).invoke(current, propertyName, delegate);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
            } catch (NoSuchMethodException e) {
                log.debug("Cannot bind listener to " + propertyName + " on " + current, e);
                // throw new IllegalStateException("Failed to bind property " + propertyName + " on " + current, e);
            }
        }
        return this;
    }
}
