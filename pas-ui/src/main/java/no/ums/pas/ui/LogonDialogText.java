package no.ums.pas.ui;

import no.ums.pas.lang.Messages;

import java.io.Serializable;
import no.ums.pas.lang.MessageFactory;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface LogonDialogText extends Messages, Serializable {

    LogonDialogText INSTANCE = MessageFactory.getMessages(LogonDialogText.class);

    @Default("Language:")
    String getLanguage();

    @Default("Username:")
    String getUsername();

    @Default("Customer:")
    String getCustomer();

    @Default("Password:")
    String getPassword();

    @Default("Login")
    String getLogin();

    @Default("Error connecting to server")
    String getLoginServerError();

    @Default("Login was cancelled")
    String getLoginCanceledError();

    @Default("Wrong username/password")
    String getLoginFailed();

}
