package no.ums.pas.ui;

import org.jdesktop.application.AbstractBean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogonBean extends AbstractBean {

    private String username;
    private String customer;
    private String password;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public LogonBean() {
    }

    public LogonBean(String username, String customer, String password) {
        this.username = username;
        this.customer = customer;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String oldValue = this.username;
        this.username = username;
        propertyChangeSupport.firePropertyChange("username", oldValue, username);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        String oldValue = this.customer;
        this.customer = customer;
        propertyChangeSupport.firePropertyChange("customer", oldValue, customer);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String oldValue = this.password;
        this.password = password;
        propertyChangeSupport.firePropertyChange("password", oldValue, password);

    }

}
