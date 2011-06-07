package no.ums.log.swing;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface LogMailSender {

    void sendMail(String id, String content);
}
