package no.ums.pas.send.sendpanels;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.localization.Localization;
import no.ums.pas.swing.UmsAction;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
* @author Ståle Undheim <su@ums.no>
*/
public class ShowProfileAction extends UmsAction {

    private final Log logger = UmsLog.getLogger(ShowProfileAction.class);

    private final Supplier<Integer> profilePkSupplier;

    public ShowProfileAction(Supplier<Integer> profilePkSupplier) {
        super("main_sending_settings_show_msg_profile");
        this.profilePkSupplier = Preconditions.checkNotNull(profilePkSupplier, "profilePkSupplier cannot be null.");
        setEnabled(PAS.get_pas().getVB4Url() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserInfo userinfo = PAS.get_pas().get_userinfo();
        try {
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
            String url = String.format("%s/PAS_msg_profile_dlg.asp?f_setreadonly=True&lProfilePk=%s&l_deptpk=%s&usr=%s&cmp=%s&pas=%s&xlang=%s",
                    PAS.get_pas().getVB4Url(),
                    URLEncoder.encode(String.valueOf(profilePkSupplier.get()), "UTF-8"),
                    URLEncoder.encode(String.valueOf(userinfo.get_default_dept().get_deptpk()), "UTF-8"),
                    URLEncoder.encode(userinfo.get_userid(), "UTF-8"),
                    URLEncoder.encode(userinfo.get_compid(), "UTF-8"),
                    URLEncoder.encode(userinfo.get_passwd(), "UTF-8"),
                    URLEncoder.encode(szLanguage, "UTF-8")
            );
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (IOException e1) {
                logger.warn("Failed to open browser to url %s", url, e1);
            }
        } catch (UnsupportedEncodingException e1) {
            logger.warn("Failed to encode parameter.", e1);
        }
    }
}
