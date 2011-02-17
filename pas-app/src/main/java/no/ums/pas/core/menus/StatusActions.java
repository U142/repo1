package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.swing.UmsAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

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

        { setEnabled(false); }

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_statuscontroller().export_status();
        }
    };


}
