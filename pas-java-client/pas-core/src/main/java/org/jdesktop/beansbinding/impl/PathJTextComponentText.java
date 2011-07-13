package org.jdesktop.beansbinding.impl;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
* @author Ståle Undheim <su@ums.no>
*/
class PathJTextComponentText extends AbstractCustomListenerPath<JTextComponent, DocumentListener, String> {

    public PathJTextComponentText() {
        super("text", JTextComponent.class, String.class);
    }

    @Override
    protected DocumentListener createListener(final UpdateHandle abstractHandle) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                abstractHandle.update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                abstractHandle.update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                abstractHandle.update();
            }
        };
    }

    @Override
    protected void removeListener(JTextComponent current, DocumentListener listener) {
        current.getDocument().removeDocumentListener(listener);
    }

    @Override
    protected void addListener(JTextComponent current, DocumentListener listener) {
        current.getDocument().addDocumentListener(listener);
    }

    @Override
    public String getValue(JTextComponent instance) {
        return instance.getText();
    }

    @Override
    public void setValue(JTextComponent instance, String value) {
        instance.setText(value);
    }

    @Override
    public boolean isWriteable() {
        return true;
    }

}
