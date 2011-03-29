package org.jdesktop.beansbinding;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class CustomOveridePaths {
    protected static final IPathAccessor<JList,Object> JLIST_SELECTED_ELEMENT = new AbstractPathAccessor<JList, Object>("selectedElement", JList.class, Object.class) {

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
        protected ListenerHandle<JList> addPropertyChangeListenerImpl(final JList instance, final PropertyChangeListener listener) {
            final ListSelectionListener selectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    update(instance, listener);
                }
            };
            instance.addListSelectionListener(selectionListener);
            return new ListenerHandle<JList>() {

                JList current = instance;

                @Override
                public ListenerHandle<JList> changeListenTarget(JList value) {
                    if (current != null) {
                        current.removeListSelectionListener(selectionListener);
                    }
                    current = value;
                    if (current != null) {
                        current.addListSelectionListener(selectionListener);
                    }
                    return this;
                }
            };
        }
    };

    protected static final IPathAccessor<JComboBox,Object> JCOMBOBOX_SELECTED_ITEM = new AbstractPathAccessor<JComboBox, Object>("selectedItem", JComboBox.class, Object.class) {
        @Override
        protected ListenerHandle<JComboBox> addPropertyChangeListenerImpl(final JComboBox instance, final PropertyChangeListener listener) {
            final ItemListener itemListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    update(instance, listener);
                }
            };
            instance.addItemListener(itemListener);
            return new ListenerHandle<JComboBox>() {

                JComboBox current = instance;

                @Override
                public ListenerHandle<JComboBox> changeListenTarget(JComboBox value) {
                    if (current != null) {
                        current.removeItemListener(itemListener);
                    }
                    current = value;
                    if (current != null) {
                        current.addItemListener(itemListener);
                    }
                    return this;
                }
            };
        }

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
    };
}
