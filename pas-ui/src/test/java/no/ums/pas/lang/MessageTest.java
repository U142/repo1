package no.ums.pas.lang;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface MessageTest extends Messages {

    @Default("Message One")
    String getMessageOne();

    @Default("Formatted message [%d]")
    String getFormattedMessage(int value);
}
