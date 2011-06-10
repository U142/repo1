package no.ums.log.swing;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface LogMailSender {

    boolean sendMail(String id, String content);
}
