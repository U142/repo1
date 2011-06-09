package no.ums.pas.entrypoint;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.log.swing.LogFrame;
import no.ums.log.swing.LogMailSender;
import no.ums.log.swing.LogRecordCollector;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mail.Smtp;
import no.ums.pas.pluginbase.DefaultPasScripting;
import no.ums.pas.pluginbase.PasScriptingInterface;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;


public class ExecApp {
    public static PAS m_pas;

    private static final Log log = UmsLog.getLogger(ExecApp.class);


    public static void main(final String[] args) {
        // Enable debug logging when there are no JNLP services available
        final boolean enableDebugLogging = ServiceManager.getServiceNames() == null;
        // Install logging handler and frame
        LogRecordCollector.install(new LogMailSender() {
            @Override
            public void sendMail(String id, String content) {
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
            }
        }, enableDebugLogging);

        //Object connect_timeout = System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", "20000") ;
        //Object read_timeout = System.getProperties().setProperty("sun.net.client.defaultReadTimeout", "3600000" ) ;
        Object connect_timeout;
        Object read_timeout;
        connect_timeout = System.getProperties().get("sun.net.client.defaultConnectTimeout");
        read_timeout = System.getProperties().get("sun.net.client.defaultReadTimeout");
        log.debug("sun.net.client.defaultConnectTimeout=" + connect_timeout);
        log.debug("sun.net.client.defaultReadTimeout=" + read_timeout);
        String sz_sitename = "http://vb4utv/";
        String sz_userid = null;
        String sz_compid = null;
        String sz_import = "";
        String sz_pasws = "";
        String sz_force_wms = null;
        boolean debug = false;
        String sz_codebase = null;
        String sz_addresssearch_url = null;
        String sz_vb4_url = null;
        String codebaseFolder = "";
        String[] arr_args = args;
        String shaPassword = null;

        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                    case 's':
                        sz_sitename = arg.substring(2);
                        break;
                    case 'u':
                        sz_userid = arg.substring(2);
                        break;
                    case 'c':
                        sz_compid = arg.substring(2);
                        break;
                    case 'd':
                        debug = true;
                        break;
                    case 'w':
                        sz_pasws = arg.substring(2);
                        break;
                    case 'm':
                        sz_force_wms = arg.substring(2);
                        break;
                    case 'a':
                        sz_addresssearch_url = arg.substring(2);
                        break;
                    case 'v':
                        sz_vb4_url = arg.substring(2);
                        break;
                    case 'x':
                        codebaseFolder = arg.substring(2);
                        break;
                    case 'p':
                        shaPassword = arg.substring(2);
                        break;
                    case 'f':
                        if (arg.length() > 3) {
                            log.debug("auto import file=" + arg.substring(3));
                            sz_import = arg.substring(3);
                        }
                        //JOptionPane.showInputDialog(args[i].substring(2));
                        break;
                }
            }
        }
        //JFrame.setDefaultLookAndFeelDecorated(true);


        log.debug("Using site: " + sz_sitename);
        log.debug("Using WS: " + sz_pasws);

        if (ServiceManager.getServiceNames() == null) {
            sz_codebase = sz_sitename;
        }
        else {
            try {
                BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
                URL url = basicService.getCodeBase();
                sz_codebase = url.toExternalForm() + codebaseFolder;
                log.debug("Codebase=" + sz_codebase);
            } catch (UnavailableServiceException e) {
                log.error("Failed to initialized JNLP BasicService", e);
            }
        }


        m_pas = new PAS(); //(f_sitename, f_userid, f_compid, f_pasws, f_debug, f_codebase, f_plugin, f_force_wms, f_args);
        m_pas.setSiteName(sz_sitename);
        m_pas.setOverrideUserId(sz_userid);
        m_pas.setOverrideCompId(sz_compid);
        m_pas.setOverrideShaPassword(shaPassword);
        m_pas.setPasWsSite(sz_pasws);
        m_pas.setDebug(debug);
        m_pas.setCodeBase(sz_codebase);
        m_pas.setProgramArguments(args);
        m_pas.setForceWMSSite(sz_force_wms);
        m_pas.setAddressSeachUrl(sz_addresssearch_url);
        m_pas.setVB4Url(sz_vb4_url);
        PAS.pasplugin = loadPlugin();
        PasApplication.init(sz_pasws);

        LogFrame.install();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PAS.pasplugin.onSetInitialLookAndFeel(this.getClass().getClassLoader());
                m_pas.init();
            }
        });
    }


    public static PasScriptingInterface loadPlugin() {
        SortedSet<PasScriptingInterface> plugins = new TreeSet<PasScriptingInterface>(PasScriptingInterface.COMPARATOR);
        plugins.add(new DefaultPasScripting());
        for (PasScriptingInterface plugin : ServiceLoader.load(PasScriptingInterface.class)) {
            plugins.add(plugin);
        }
        PasScriptingInterface plugin = plugins.first();
        plugin.startPlugin();
        return plugin;
    }
}