package org.jdesktop.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractBean {
    public final PropertyChangeSupport prop = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        prop.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        prop.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        prop.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        prop.removePropertyChangeListener(propertyName, listener);
    }

    public void update(String key, Object oldValue, Object newValue) {
        prop.firePropertyChange(key, oldValue, newValue);
    }

    public void firePropertyChange(String key, Object oldValue, Object newValue) {
        prop.firePropertyChange(key, oldValue, newValue);
    }

}
