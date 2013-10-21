package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.view.PasswordUpdateCtrl;
import no.ums.pas.importer.ImportPolygon;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendObject;
import no.ums.pas.swing.UmsAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

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
                if(PAS.get_pas().get_sendcontroller().create_new_sending(null, true)!=null)
                {
                	//PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
                }
				if(PAS.get_pas().get_parmcontroller()!=null) {
					//PAS.get_pas().get_parmcontroller().clearDrawQueue();
					//PAS.get_pas().get_parmcontroller().setFilled(null);
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
            PAS.get_pas().askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
				
				@Override
				public void Complete(boolean bStatusClosed) {
					
				}
			});
        }
    };

    // act_start_gas
    Action START_GAS = new UmsAction("mainmenu_file_startgas") {
        @Override
        public void actionPerformed(ActionEvent e){
            UserInfo userinfo = PAS.get_pas().get_userinfo();

            try
            {
                String szLanguage = "eng";
                String szLocale = Localization.INSTANCE.getLocale().getCountry();

                if(szLocale.equals("NO"))
                    szLanguage = "nor";
                else if(szLocale.equals("ES"))
                    szLanguage = "spa";
                else if(szLocale.equals("DK"))
                    szLanguage = "dan";
                else if(szLocale.equals("SE"))
                    szLanguage = "sve";

                String url = String.format("%slogon_hash_proc.asp?szUserID=%s&szCompanyID=%s&szPassword=%s",
                    PAS.get_pas().getVB4Url(),
                    URLEncoder.encode(userinfo.get_userid(), "UTF-8"),
                    URLEncoder.encode(userinfo.get_compid(), "UTF-8"),
                    URLEncoder.encode(userinfo.get_passwd(), "UTF-8"),
                    URLEncoder.encode(String.valueOf(userinfo.get_current_department().get_deptpk()), "UTF-8")
                );
            /*,
                URLEncoder.encode(String.valueOf(profilePkSupplier.get()), "UTF-8"),
                URLEncoder.encode(String.valueOf(userinfo.get_default_dept().get_deptpk()), "UTF-8"),
                URLEncoder.encode(szLanguage, "UTF-8")
        );*/
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (IOException e1) {
                    log.warn("Failed to open browser to url %s", url, e1);
                }
            }
            catch (UnsupportedEncodingException e1) {
                log.warn("Failed to encode parameter.", e1);
            }
        }
    };

    // act_fileimport
    Action FILE_IMPORT = new UmsAction("mainmenu_file_import") {
        @Override
        public void actionPerformed(ActionEvent e) {
            SendObject obj = PAS.get_pas().get_sendcontroller().create_new_sending(null, false);
            if (obj != null) {
                new ImportPolygon(obj.get_toolbar(), "act_polygon_imported", false, PAS.get_pas());
            }
        	//PAS.get_pas().invoke_project(true);
        	//new ImportPolygon(null, "act_polygon_imported", false, PAS.get_pas());
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
    
    // act_update_password
    Action UPDATE_PASSWORD = new UmsAction("mainmenu_update_password") {
		@Override
		public void actionPerformed(ActionEvent e) {
			new PasswordUpdateCtrl(PAS.get_pas().get_userinfo()).ShowGUI(true, PAS.get_pas().get_mappane());
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
