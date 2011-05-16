package no.ums.pas;

import no.ums.auth.AuthToken;

import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface AuthTokenHolder {

    AuthToken getToken();

    void setToken(AuthToken authToken);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}
