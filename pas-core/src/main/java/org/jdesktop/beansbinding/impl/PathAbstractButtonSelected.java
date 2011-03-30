package org.jdesktop.beansbinding.impl;

import javax.swing.AbstractButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
* @author Ståle Undheim <su@ums.no>
*/
class PathAbstractButtonSelected extends AbstractCustomListenerPath<AbstractButton, ChangeListener, Boolean> {
    public PathAbstractButtonSelected() {
        super("selected", AbstractButton.class, Boolean.class);
    }

    @Override
    protected ChangeListener createListener(final UpdateHandle abstractHandle) {
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

}
