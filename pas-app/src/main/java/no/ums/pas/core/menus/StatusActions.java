package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.swing.UmsAction;
import no.ums.pas.swing.UmsTimeAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface StatusActions {

    // act_statusopen
    Action OPEN = new UmsAction("mainmenu_status_open") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().load_status();
        }
    };
    // act_statusexport
    Action EXPORT = new UmsAction("mainmenu_status_export") {

        {
            setEnabled(false);
            OPEN.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("enabled")) {
                        putValue(evt.getPropertyName(), evt.getNewValue());
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_statuscontroller().export_status();
        }
    };

    UmsAction MANUAL_UPDATE = new UmsAction("mainmenu_status_updates_manual", false) {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    PAS.get_pas().get_statuscontroller().set_autoupdate(false);
                    PAS.get_pas().add_event("Status: Manual updates activated", null);
                }
            });
        }
    };

    UmsAction AUTOMATIC_UPDATE = new UmsAction("mainmenu_status_updates_every", true) {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    PAS.get_pas().get_statuscontroller().set_autoupdate(true);
                    PAS.get_pas().add_event("Status: Automatic updates activated", null);
                }
            });
        }
    };

    // act_status_updateseconds
    class UpdateInterval extends UmsTimeAction {

        public UpdateInterval(TimeUnit timeUnit, int count) {
            super(timeUnit, count);
            setEnabled(Boolean.TRUE.equals(AUTOMATIC_UPDATE.getValue(SELECTED_KEY)));
            AUTOMATIC_UPDATE.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(SELECTED_KEY)) {
                        setEnabled((Boolean) evt.getNewValue());
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int updateSeconds = (int) getTimeUnit().toSeconds(getCount());
            PAS.get_pas().get_statuscontroller().set_autoupdate_seconds(updateSeconds);
            PAS.get_pas().add_event("Status: Automatic updates set to every " + updateSeconds, null);
        }
    }


}
