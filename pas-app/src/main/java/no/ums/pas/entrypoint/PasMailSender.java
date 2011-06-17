package no.ums.pas.entrypoint;

import no.ums.log.swing.LogFrame;
import no.ums.log.swing.LogMailSender;
import no.ums.pas.PAS;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mail.Smtp;

import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
* @author Ståle Undheim <su@ums.no>
*/
class PasMailSender implements LogMailSender {
    private final String[] args;

    public PasMailSender(String[] args) {
        this.args = args;
    }

    @Override
    public boolean sendMail(String id, String content) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        final UserInfo userinfo = (PAS.get_pas() == null) ? null : PAS.get_pas().get_userinfo();

        pw.printf("Error report\n");
        pw.printf("Error id:   %s\n",id);
        if (userinfo != null) {
            pw.printf("Username:   %s\n", userinfo.get_userid());
            pw.printf("Company:    %s\n", userinfo.get_compid());
            pw.printf("Department: %s\n", userinfo.get_current_department());
        } else {
            pw.printf("User info:  Not logged in\n");
        }
        if (PAS.get_pas() != null) {
            pw.printf("Pas site:   %s\n",PAS.get_pas().get_pasws());
            pw.printf("Code base:  %s\n", PAS.get_pas().get_codebase());
        } else {
            pw.println("PAS not ready yet - args:");
            for (String arg : args) {
                pw.print('\t');
                pw.println(arg);
            }
        }
        pw.println("******************************************");
        pw.println(content);
        pw.close();

        if (userinfo == null || userinfo.get_mailaccount() == null) {
            final MailSendErrorDialog mailSendErrorDialog = new MailSendErrorDialog(LogFrame.getInstance());
            mailSendErrorDialog.getModel().setText(sw.toString());
            mailSendErrorDialog.setVisible(true);
        } else {
            LogFrame.getInstance().setSendMailEnabled(false);
            PAS.pasplugin.onSendErrorMessages(sw.toString(), userinfo.get_mailaccount(), new Smtp.smtp_callback() {

                @Override
                public void finished() {
                    LogFrame.getInstance().setSendMailEnabled(true);
                    JOptionPane.showMessageDialog(LogFrame.getInstance(), "Mail sent");
                }

                @Override
                public void failed(String e) {
                    LogFrame.getInstance().setSendMailEnabled(true);
                    JOptionPane.showMessageDialog(PAS.get_pas(), "Error sending mail, please check your settings");
                }
            });
        }
        return true;
    }
}
