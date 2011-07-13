package no.ums.pas.entrypoint;

import org.jdesktop.beansbinding.AbstractBean;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MailSendErrorModel extends AbstractBean {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String value) {
        final String oldValue = this.text;
        this.text = value;
        firePropertyChange("text", oldValue, value);
    }
}
