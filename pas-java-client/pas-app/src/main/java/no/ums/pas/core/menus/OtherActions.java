package no.ums.pas.core.menus;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.log.swing.LogFrame;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.SettingsGUI;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.logon.view.SettingsCtrl;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.send.messagelibrary.MessageLibDlg;
import no.ums.pas.swing.UmsAction;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.UMapBounds;
import no.ums.ws.pas.Pasws;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;

import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface OtherActions {
    Log log = UmsLog.getLogger(OtherActions.class);

    // act_show_settings - mainmenu_settings_show
    Action SHOW_SETTINGS = new UmsAction("mainmenu_settings_show") {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
            		new SettingsCtrl(PAS.get_pas(), true, 
            				PAS.get_pas().get_settings(), 
            				PAS.get_pas().get_userinfo().get_mailaccount(),
            				Variables.getUserInfo());
                    /*SettingsGUI sGui = new SettingsGUI(PAS.get_pas());
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
                        sGui.getM_txt_lba_refresh().setText(String.valueOf(PAS.get_pas().get_settings().getLbaRefresh()));
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
                    }*/
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
                    new MessageLibDlg(PAS.get_pas(), true, false);
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
    
    // act_help_showlog - show logging window
    Action HELP_SHOWLOG = new UmsAction("mainmenu_help_showlog") {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		LogFrame.getInstance().setVisible(true);
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
    
    Action DOWNLOAD_DOCUMENTATION = new UmsAction("mainmenu_help_documentation") {

		@Override
		public void actionPerformed(ActionEvent e) {
			PAS.pasplugin.onDownloadDocumentation(Variables.getUserInfo());
		}
    	
    };
    
    Action SET_DEPARTMENT_MAPBOUNDS = new UmsAction("mainmenu_settings_set_mapbounds") {
		@Override
		public void actionPerformed(ActionEvent e) {
			ULOGONINFO logon = new ULOGONINFO();
			UserInfo userinfo = PAS.get_pas().get_userinfo();
			logon.setLComppk(userinfo.get_comppk());
			logon.setLDeptpk(userinfo.get_current_department().get_deptpk());
			logon.setLUserpk(new Long(userinfo.get_userpk()));
			logon.setSzPassword(userinfo.get_passwd());
			logon.setSzUserid(userinfo.get_userid());
			logon.setSzCompid(userinfo.get_compid());
			logon.setSessionid(userinfo.get_sessionid());
			
			try
			{
				URL wsdl = new URL(vars.WSDL_PAS);
				QName service = new QName("http://ums.no/ws/pas/", "pasws");
				UMapBounds bounds = new UMapBounds();
				bounds.setLBo(Variables.getNavigation().get_lbo());
				bounds.setRBo(Variables.getNavigation().get_rbo());
				bounds.setUBo(Variables.getNavigation().get_ubo());
				bounds.setBBo(Variables.getNavigation().get_bbo());
				switch(new Pasws(wsdl, service).getPaswsSoap12().updateMapBounds(logon, bounds))
				{
				case OK:
					NavStruct nav = new NavStruct(
							bounds.getLBo(),
							bounds.getRBo(),
							bounds.getUBo(),
							bounds.getBBo()
							);
					Variables.getUserInfo().set_nav_init(nav);
					PAS.get_pas().get_userinfo().set_nav_init(nav);
					Variables.getUserInfo().get_current_department().set_nav_init(nav);
					break;
				case FAILED:
					JOptionPane.showMessageDialog(PAS.get_pas().get_mainmenu(), "Set default map failed", PAS.l("common_error"), JOptionPane.ERROR_MESSAGE);
					break;
				}
			}
			catch(Exception err)
			{
				log.error("Error updating department mapbounds", e);
			}
		}
    };


}
