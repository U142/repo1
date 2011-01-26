package no.ums.pas.beans;

import org.jdesktop.beansbinding.PropertyStateEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Helper property accessors.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface PropertyUtil {

    /**
     * This is a property accessor that will properly work for JTextComponent, as it by default
     * doesn't fire events when the text changes, and is as such useless for 2 way synching using
     * beans bindings and {@link org.jdesktop.beansbinding.BeanProperty#create(String)}.
     */
    BindingPropertyHelper<JTextComponent, String> JTEXT_TEXT = new BindingPropertyHelper<JTextComponent, String>() {

        private final Map<JTextComponent, DocumentListener> listeners = new IdentityHashMap<JTextComponent, DocumentListener>();

        @Override
        public Class<? extends String> getWriteType(JTextComponent source) {
            return String.class;
        }

        @Override
        public String getValue(JTextComponent source) {
            return source.getText();
        }

        @Override
        public void setValue(JTextComponent source, String value) {
            source.setText(value);
        }

        @Override
        public boolean isReadable(JTextComponent source) {
            return true;
        }

        @Override
        public boolean isWriteable(JTextComponent source) {
            return source.isEditable();
        }

        @Override
        protected void listeningStarted(final JTextComponent source) {
            DocumentListener listener = new DocumentListener() {
                String oldValue = source.getText();

                @Override
                public void insertUpdate(DocumentEvent e) {
                    firePropertyStateChange(new PropertyStateEvent(JTEXT_TEXT, source, true, oldValue, source.getText(), false, source.isEditable()));
                    oldValue = source.getText();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    insertUpdate(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    insertUpdate(e);
                }
            };
            listeners.put(source, listener);
            source.getDocument().addDocumentListener(listener);
        }

        @Override
        protected void listeningStopped(JTextComponent source) {
            if (listeners.containsKey(source)) {
                source.getDocument().removeDocumentListener(listeners.get(source));
            }
        }
    };

    BindingPropertyHelper<JComboBox, Object> COMBOBOX_SELECTED_ITEM = new BindingPropertyHelper<JComboBox, Object>() {

        private final Map<JComboBox, ActionListener> listeners = new IdentityHashMap<JComboBox, ActionListener>();

        @Override
        public Class<?> getWriteType(JComboBox source) {
            return (source.getItemCount() == 0 || source.getItemAt(0) == null) ? Object.class : source.getItemAt(0).getClass();
        }

        @Override
        public Object getValue(JComboBox source) {
            return source.getSelectedItem();
        }

        @Override
        public void setValue(JComboBox source, Object value) {
            source.setSelectedItem(value);
        }

        @Override
        public boolean isReadable(JComboBox source) {
            return true;
        }

        @Override
        public boolean isWriteable(JComboBox source) {
            return true;
        }

        @Override
        protected void listeningStarted(final JComboBox source) {
            final ActionListener listener = new ActionListener() {
                Object oldValue = source.getSelectedItem();
                @Override
                public void actionPerformed(ActionEvent e) {
                    firePropertyStateChange(new PropertyStateEvent(COMBOBOX_SELECTED_ITEM, source, true, oldValue, source.getSelectedItem(), false, source.isEditable()));
                    oldValue = source.getSelectedItem();
                }
            };
            source.addActionListener(listener);
            listeners.put(source, listener);
        }

        @Override
        protected void listeningStopped(JComboBox source) {
            source.removeActionListener(listeners.get(source));
        }
    };
}
