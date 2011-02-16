package no.ums.pas.swing;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import no.ums.pas.localization.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

/**
 * Implementation of Action that takes the message key for it's name in the constructor.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class UmsAction implements Action {

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private boolean enabled = true;
    private final Map<String, Object> values = Maps.newHashMap();

    private final String localizedName;

    public UmsAction(@Nonnull String localizedName) {
        this.localizedName = Preconditions.checkNotNull(localizedName, "localizedName cannot be null");
    }

    @Override
    public final Object getValue(String key) {
        return key.equals(NAME) ? Localization.l(localizedName) : values.get(key);
    }

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UmsAction umsAction = (UmsAction) o;

        return localizedName.equals(umsAction.localizedName);

    }

    @Override
    public int hashCode() {
        return localizedName.hashCode();
    }
}
