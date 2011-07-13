package org.jdesktop.beansbinding.impl;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.awt.Component;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ListenerHandleComponentImpl<T extends Component> implements ListenerHandle<T> {

    private static final Log log = UmsLog.getLogger(ListenerHandleComponentImpl.class);

    private T current;
    private final DelegatePropertyChangeListener delegate;

    public ListenerHandleComponentImpl(T target, String propertyName, PropertyChangeListener delegate) {
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
        if (current != null) {
            current.removePropertyChangeListener(delegate.getPropertyName(), delegate);
        }
        current = value;
        if (current != null) {
            current.addPropertyChangeListener(delegate.getPropertyName(), delegate);
        }
        return this;
    }
}
