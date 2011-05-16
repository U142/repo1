package no.ums.pas.swing;

import com.google.common.base.Preconditions;
import no.ums.pas.localization.Localization;

import javax.annotation.Nonnull;

/**
 * Implementation of Action that takes the message key for it's name in the constructor.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class UmsAction extends BaseAction {

    private final String localizedName;

    public UmsAction(@Nonnull String localizedName) {
        this(localizedName, true);
    }

    public UmsAction(@Nonnull String localizedName, boolean enabled) {
        this(localizedName, enabled, false);
    }

    public UmsAction(@Nonnull String localizedName, boolean enabled, boolean selected) {
        super(enabled);
        this.localizedName = Preconditions.checkNotNull(localizedName, "localizedName cannot be null");
        setSelected(selected);
    }

    @Override
    protected String getName() {
        return Localization.l(localizedName);
    }
}
