package no.ums.pas.swing;

import no.ums.pas.localization.Localization;

import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract     class UmsTimeAction extends BaseAction {

    private final TimeUnit timeUnit;
    private final int count;
    private final String timeUnitKey;

    public UmsTimeAction(TimeUnit timeUnit, int count) {
        this.timeUnit = timeUnit;
        switch (timeUnit) {
            case SECONDS:
                timeUnitKey = (count > 1) ? "common_seconds" : "common_second";
                break;
            case MINUTES:
                timeUnitKey = (count > 1) ? "common_minutes" : "common_minute";
                break;
            case HOURS:
                timeUnitKey = (count > 1) ? "common_hours" : "common_hour";
                break;
            case DAYS:
                timeUnitKey = (count > 1) ? "common_days" : "common_day";
                break;
            default:
                throw new IllegalArgumentException("Unsupported timeunit type: " + timeUnit);
        }
        this.count = count;
    }

    @Override
    protected String getName() {
        return String.format("%d %s", count, Localization.l(timeUnitKey));
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getCount() {
        return count;
    }
}
