package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.swing.UmsAction;

import java.awt.event.ActionEvent;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface ViewOptions {

    // act_togglepolygon
    UmsAction TOGGLE_POLYGON = new UmsAction("mainmenu_view_show_statusshape", true, true) {

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().kickRepaint();
        }
    };

    // act_toggle_showhouses
    UmsAction TOGGLE_HOUSES = new UmsAction("mainmenu_view_show_houses", true, true) {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isSelected()) {
                PAS.get_pas().download_houses();
            }
            PAS.get_pas().kickRepaint();
        }
    };
    // act_view_statuscodes
    UmsAction TOGGLE_STATUSCODES = new UmsAction("mainmenu_view_show_statuscodes", true, true) {

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().kickRepaint();
        }
    };
    // act_show_searchpinpoint
    UmsAction TOGGLE_SEARCHPOINTS = new UmsAction("mainmenu_view_show_search_pinpoint", true, true) {

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().kickRepaint();
        }
    };
}
