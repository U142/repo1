package org.jdesktop.beansbinding.impl;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
* @author Ståle Undheim <su@ums.no>
*/
class PathJListSelectedElement extends AbstractCustomListenerPath<JList, ListSelectionListener, Object> {

    public PathJListSelectedElement() {
        super("selectedElement", JList.class, Object.class);
    }

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
    protected ListSelectionListener createListener(final UpdateHandle abstractHandle) {
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


}
