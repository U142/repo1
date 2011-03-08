package org.jdesktop.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractBean {
	private final PropertyChangeSupport prop = new PropertyChangeSupport(this);
	
	private void addPropertyChangeListener(PropertyChangeListener listener) {
		prop.addPropertyChangeListener(listener);
	}
	private void removePropertyChangeListener(PropertyChangeListener listener) {
		prop.removePropertyChangeListener(listener);
	}
	private void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		prop.addPropertyChangeListener(propertyName, listener);
	}
	private void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		prop.removePropertyChangeListener(propertyName, listener);
	}
	
	public void update(String key, Object oldValue, Object newValue)
	{
		prop.firePropertyChange(key, oldValue, newValue);
	}

}
