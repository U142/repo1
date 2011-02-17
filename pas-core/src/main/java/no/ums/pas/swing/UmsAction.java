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
public abstract class UmsAction extends BaseAction {

    private final String localizedName;

    public UmsAction(@Nonnull String localizedName) {
        this(localizedName, false);
    }

    public UmsAction(@Nonnull String localizedName, boolean selected) {
        this.localizedName = Preconditions.checkNotNull(localizedName, "localizedName cannot be null");
        setSelected(selected);
    }

    @Override
    protected String getName() {
        return Localization.l(localizedName);
    }
}
