package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.send.SendObject;
import no.ums.pas.swing.UmsAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Actions in the FileMenu.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface FileMenuActions {

    Log log = UmsLog.getLogger(FileMenuActions.class);

    // act_new_sending
    Action NEW_SENDING = new UmsAction("mainmenu_file_newsending") {
        @Override
        public void actionPerformed(ActionEvent e) {
			try {
                PAS.get_pas().get_sendcontroller().create_new_sending();
			    PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
				if(PAS.get_pas().get_parmcontroller()!=null) {
					PAS.get_pas().get_parmcontroller().clearDrawQueue();
					PAS.get_pas().get_parmcontroller().setFilled(null);
				}
			} catch(Exception err) {
				log.error("Failed to create new sending.",err);
			}
        }
    };

    // act_new_project
    Action OPEN_PROJECT = new UmsAction("mainmenu_file_project") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().invoke_project(false);
        }
    };

    // act_close_project
    Action CLOSE_PROJECT = new UmsAction("mainmenu_file_project_close") {
        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().close_active_project(true, true);
        }
    };

    // act_fileimport
    Action FILE_IMPORT = new UmsAction("mainmenu_file_import") {
        @Override
        public void actionPerformed(ActionEvent e) {
            SendObject obj = PAS.get_pas().get_sendcontroller().create_new_sending();
            if (obj != null) {
                new ImportPolygon(obj.get_toolbar(), "act_polygon_imported", false, PAS.get_pas());
            }
        }
    };

    // act_print_map
    Action PRINT_MAP = new UmsAction("mainmenu_file_printmap") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().print_map();
        }
    };

    // act_save_map
    Action SAVE_MAP = new UmsAction("mainmenu_file_savemap") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().save_map();
        }
    };

    // act_exit_application
    Action EXIT = new UmsAction("mainmenu_file_quit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().exit_application();
        }
    };
}
