package no.ums.pas.swing;

import com.google.common.collect.Maps;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class BaseAction implements Action {

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private boolean enabled = true;
    private final Map<String, Object> values = Maps.newHashMap();

    @Override
    public final Object getValue(String key) {
        return key.equals(NAME) ? getName() : values.get(key);
    }

    protected abstract String getName();

    @Override
    public final void putValue(String key, Object newValue) {
        if (key.equals("enabled")) {
            setEnabled((newValue instanceof Boolean) ? (Boolean) newValue : false);
        } else {
            // Remove the entry for key if newValue is null
            // else put in the newValue for key.
            final Object oldValue = (newValue == null) ? values.remove(key) : values.put(key, newValue);
            changeSupport.firePropertyChange(key, oldValue, newValue);
        }
    }

    @Override
    public final void setEnabled(boolean b) {
        if (b != enabled) {
            boolean old = enabled;
            enabled = b;
            changeSupport.firePropertyChange("enabled", old, b);
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    public final boolean isSelected() {
        return getValue(SELECTED_KEY) == Boolean.TRUE;
    }

    public final void setSelected(boolean selected) {
        putValue(SELECTED_KEY, selected);
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && o.getClass() == getClass();

    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public JRadioButtonMenuItem asRadio() {
        return new JRadioButtonMenuItem(this);
    }

    public JCheckBoxMenuItem asChecked() {
        return new JCheckBoxMenuItem(this);
    }
}
