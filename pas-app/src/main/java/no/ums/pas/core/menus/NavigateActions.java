package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.swing.UmsAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface NavigateActions {

    // act_pan
    Action PAN = new UmsAction("mainmenu_navigation_pan") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_mainmenu().set_pan();
        }
    };

    // act_zoom
    Action ZOOM = new UmsAction("mainmenu_navigation_zoom") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_mappane().set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_ZOOM);
		    PAS.get_pas().get_mainmenu().reset_buttons_foreground();
        }
    };

    //act_search
    Action SEARCH = new UmsAction("mainmenu_navigation_search") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_mainmenu().get_searchframe().activate();
			PAS.get_pas().get_mainmenu().get_searchframe().toFront();
        }
    };
}
