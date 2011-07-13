package org.jdesktop.beansbinding.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * PropertyChangeListener that makes sure to put the target instance
 * in the THREAD_STACK_VISITED set, to avoid looping assignments.
 * @author Ståle Undheim <su@ums.no>
 */
abstract class DelegatePropertyChangeListener implements PropertyChangeListener {

    private final String propertyName;
    private final PropertyChangeListener delegate;

    protected DelegatePropertyChangeListener(String propertyName, PropertyChangeListener delegate) {
        this.propertyName = propertyName;
        this.delegate = delegate;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ListenerHandle.THREAD_STACK_VISITED.get().put(getInstance(), null);
        try {
            delegate.propertyChange(evt);
        } finally {
            ListenerHandle.THREAD_STACK_VISITED.get().remove(getInstance());
        }
    }

    @Override
    public String toString() {
        return "Change on [" + propertyName + "] for [" + getInstance() + "] -> " + delegate;
    }

    public String getPropertyName() {
        return propertyName;
    }

    protected abstract Object getInstance();



}
