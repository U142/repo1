package org.jdesktop.beansbinding;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class CustomOveridePaths {

    protected static final PathAccessor<JList, Object> JLIST_SELECTED_ELEMENT = new ListenerAbstractPathAccessor<JList, ListSelectionListener, Object>("selectedElement", JList.class, Object.class) {

        @Override
        public Object getValue(JList instance) {
            return instance.getSelectedValue();
        }

        @Override
        public void setValue(JList instance, Object value) {
            instance.setSelectedValue(value, true);
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

        @Override
        protected ListSelectionListener createListener(final AbstractListenerHandle<JList, ListSelectionListener, Object> abstractHandle) {
            return new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    abstractHandle.update();
                }
            };
        }

        @Override
        protected void removeListener(JList current, ListSelectionListener listener) {
            current.removeListSelectionListener(listener);
        }

        @Override
        protected void addListener(JList current, ListSelectionListener listener) {
            current.addListSelectionListener(listener);
        }


    };

    protected static final PathAccessor<JComboBox, Object> JCOMBOBOX_SELECTED_ITEM = new ListenerAbstractPathAccessor<JComboBox, ItemListener, Object>("selectedItem", JComboBox.class, Object.class) {
        @Override
        public Object getValue(JComboBox instance) {
            return instance.getSelectedItem();
        }

        @Override
        public void setValue(JComboBox instance, Object value) {
            instance.setSelectedItem(value);
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

        @Override
        protected ItemListener createListener(final AbstractListenerHandle<JComboBox, ItemListener, Object> abstractHandle) {
            return new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    abstractHandle.update();
                }
            };
        }

        @Override
        protected void removeListener(JComboBox current, ItemListener listener) {
            current.removeItemListener(listener);
        }

        @Override
        protected void addListener(JComboBox current, ItemListener listener) {
            current.addItemListener(listener);
        }
    };

    public static final PathAccessor<JTextComponent, String> JTEXTCOMPONENT_TEXT = new ListenerAbstractPathAccessor<JTextComponent, DocumentListener, String>("text", JTextComponent.class, String.class) {

        @Override
        protected DocumentListener createListener(final AbstractListenerHandle<JTextComponent, DocumentListener, String> abstractHandle) {
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

    };

    public static PathAccessor<AbstractButton, Boolean> ABSTRACT_BUTTON_SELECTED = new ListenerAbstractPathAccessor<AbstractButton, ChangeListener, Boolean>("selected", AbstractButton.class, Boolean.class) {
        @Override
        protected ChangeListener createListener(final AbstractListenerHandle<AbstractButton, ChangeListener, Boolean> abstractHandle) {
            return new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    abstractHandle.update();
                }
            };
        }

        @Override
        protected void addListener(AbstractButton current, ChangeListener listener) {
            current.addChangeListener(listener);
        }

        @Override
        protected void removeListener(AbstractButton current, ChangeListener listener) {
            current.removeChangeListener(listener);
        }

        @Override
        public Boolean getValue(AbstractButton instance) {
            return instance.isSelected();
        }

        @Override
        public void setValue(AbstractButton instance, Boolean value) {
            instance.setSelected(value);
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

    };

    static abstract class ListenerAbstractPathAccessor<SRC, LST, VAL> extends AbstractPathAccessor<SRC, VAL> {

        protected ListenerAbstractPathAccessor(String name, Class<SRC> type, Class<VAL> valueType) {
            super(name, type, valueType);
        }

        @Override
        public ListenerHandle<SRC> addPropertyChangeListener(SRC instance, PropertyChangeListener listener) {
            return new AbstractListenerHandle<SRC, LST, VAL>(this, instance, listener) {
                @Override
                protected LST createListener() {
                    return ListenerAbstractPathAccessor.this.createListener(this);
                }

                @Override
                protected void removeListener(SRC current, LST listener) {
                    ListenerAbstractPathAccessor.this.removeListener(current, listener);
                }

                @Override
                protected void addListener(SRC current, LST listener) {
                    ListenerAbstractPathAccessor.this.addListener(current, listener);
                }
            };
        }

        protected abstract LST createListener(AbstractListenerHandle<SRC, LST, VAL> abstractHandle);

        protected abstract void addListener(SRC current, LST listener);

        protected abstract void removeListener(SRC current, LST listener);

    }


    protected static final List<? extends PathAccessor> OVERRIDES = Arrays.asList(
            CustomOveridePaths.JCOMBOBOX_SELECTED_ITEM,
            CustomOveridePaths.JLIST_SELECTED_ELEMENT,
            CustomOveridePaths.JTEXTCOMPONENT_TEXT,
            CustomOveridePaths.ABSTRACT_BUTTON_SELECTED);

    static abstract class AbstractListenerHandle<SRC, LST, VAL> implements ListenerHandle<SRC> {

        private SRC current;
        private final LST listener;
        private VAL currentValue;
        private final PropertyChangeListener propertyChangeListener;
        private final PathAccessor<SRC, VAL> accessor;

        protected AbstractListenerHandle(PathAccessor<SRC, VAL> accessor, SRC instance, PropertyChangeListener propertyChangeListener) {
            this.accessor = accessor;
            this.propertyChangeListener = propertyChangeListener;
            listener = createListener();
            currentValue = (instance == null) ? null : accessor.getValue(instance);
            changeListenTarget(instance);
        }

        protected abstract LST createListener();

        protected abstract void addListener(SRC current, LST listener);

        protected abstract void removeListener(SRC current, LST listener);

        @Override
        public ListenerHandle<SRC> changeListenTarget(SRC value) {
            if (current != null) {
                removeListener(current, listener);
            }
            current = value;
            if (current != null) {
                addListener(current, listener);
            }
            return this;
        }

        protected void update() {
            VAL oldValue = currentValue;
            currentValue = accessor.getValue(current);
            THREAD_STACK_VISITED.get().put(current, null);
            try {
                propertyChangeListener.propertyChange(new PropertyChangeEvent(current, accessor.getPropertyName().getName(), oldValue, currentValue));
            } finally {
                THREAD_STACK_VISITED.get().remove(current);
            }
        }

    }
}
