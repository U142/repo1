package org.jdesktop.beansbinding.impl;

import javax.swing.JComboBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
* @author Ståle Undheim <su@ums.no>
*/
class PathComboboxSelectedItem extends AbstractCustomListenerPath<JComboBox, ItemListener, Object> {
    public PathComboboxSelectedItem() {
        super("selectedItem", JComboBox.class, Object.class);
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

    @Override
    protected ItemListener createListener(final UpdateHandle abstractHandle) {
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
}
