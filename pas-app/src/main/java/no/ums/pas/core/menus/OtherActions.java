package no.ums.pas.core.menus;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.SettingsGUI;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.swing.UmsAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface OtherActions {

    // act_show_settings - mainmenu_settings_show
    Action SHOW_SETTINGS = new UmsAction("mainmenu_settings_show") {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SettingsGUI sGui = new SettingsGUI(PAS.get_pas());
                    if (PAS.get_pas().get_settings() != null) {
                        if (PAS.get_pas().get_settings().getUsername().length() < 1)
                            sGui.getM_txt_username().setText(PAS.get_pas().get_userinfo().get_userid());
                        else
                            sGui.getM_txt_username().setText(PAS.get_pas().get_settings().getUsername());
                        if (PAS.get_pas().get_settings().getCompany().length() < 1)
                            sGui.getM_txt_company().setText(PAS.get_pas().get_userinfo().get_compid());
                        else
                            sGui.getM_txt_company().setText(PAS.get_pas().get_settings().getCompany());
                        if (PAS.get_pas().get_settings().parm())
                            sGui.getM_chk_start_parm().setSelected(true);
                        else
                            sGui.getM_chk_start_parm().setSelected(false);
                        /*if(PAS.get_pas().get_settings().fleetcontrol())
                                  sGui.getM_chk_start_fleetcontrol().setSelected(true);
                              else
                                  sGui.getM_chk_start_fleetcontrol().setSelected(false);*/
                        sGui.getM_txt_lba_refresh().setText(String.valueOf(PAS.get_pas().get_settings().getLbaRefresh()));
                        //if(PAS.get_pas().get_settings().getMapServer())
                        sGui.setMapServer(PAS.get_pas().get_settings().getMapServer());
                        sGui.setWmsUser(PAS.get_pas().get_settings().getWmsUsername());
                        sGui.setWmsPassword(PAS.get_pas().get_settings().getWmsPassword());
                        sGui.setWmsSite(PAS.get_pas().get_settings().getWmsSite());
                        if (PAS.get_pas().get_settings().getPanByDrag())
                            sGui.getM_btn_pan_by_drag().doClick();
                        else
                            sGui.getM_btn_pan_by_click().doClick();
                    }
                    if (PAS.get_pas().get_userinfo().get_mailaccount() != null) {
                        sGui.getM_txt_mail_displayname().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_displayname());
                        sGui.getM_txt_mail_address().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_mailaddress());
                        sGui.getM_txt_mail_outgoing().setText(PAS.get_pas().get_userinfo().get_mailaccount().get_mailserver());
                    }
                }
            });
        }
    };
    // act_messagelib - main_sending_audio_type_library
    Action SHOW_MESSAGELIB = new UmsAction("main_sending_audio_type_library") {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new MessageLibDlg(PAS.get_pas());
                }
            });
        }
    };

    // act_gps_open - mainmenu_fleetcontrol_download_mapobjects
    Action GPS_OPEN = new UmsAction("mainmenu_fleetcontrol_download_mapobjects") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.get_pas().get_gpscontroller().start_download(false);
            PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_GPS_LIST_);
        }
    };
    // act_gps_new - mainmenu_fleetcontrol_newobject
    Action GPS_NEW = new UmsAction("mainmenu_fleetcontrol_newobject") {
        @Override
        public void actionPerformed(ActionEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    // act_start_parm - mainmenu_parm_start
    Action PARM_START = new UmsAction("mainmenu_parm_start") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(PAS.get_pas().get_parmcontroller()==null && !PAS.isParmOpen()) {
				PAS.setParmOpen(PAS.pasplugin.onStartParm());
			}
        }
    };
    // act_refresh_parm - common_refresh
    Action PARM_REFRESH = new UmsAction("common_refresh") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.pasplugin.onRefreshParm();
        }
    };
    // act_close_parm - common_close
    Action PARM_CLOSE = new UmsAction("common_close") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.pasplugin.onCloseParm();
        }
    };

    // act_help_about - mainmenu_help_about
    Action HELP_ABOUT = new UmsAction("mainmenu_help_about") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.pasplugin.onHelpAbout();
        }
    };
    // act_trainingmode - mainmenu_trainingmode
    Action HELP_TRAININGMODE = new UmsAction("mainmenu_trainingmode") {
        @Override
        public void actionPerformed(ActionEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };
    // act_address_book - common_address_book
    Action ADDRESS_BOOK = new UmsAction("common_address_book") {
        @Override
        public void actionPerformed(ActionEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };
    // act_show_contact_information - common_contact_information
    Action SHOW_CONTACT_INFO = new UmsAction("common_contact_information") {
        @Override
        public void actionPerformed(ActionEvent e) {
            PAS.pasplugin.onShowContactinformation();
        }
    };

}
