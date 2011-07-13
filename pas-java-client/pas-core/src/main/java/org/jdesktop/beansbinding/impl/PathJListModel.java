package org.jdesktop.beansbinding.impl;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import java.beans.PropertyChangeListener;

/**
 * Override over the default model assignment to convert null values to an empty model.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class PathJListModel extends AbstractPathAccessor<JList, ListModel> {

    public PathJListModel() {
        super("model", JList.class, ListModel.class);
    }

    @Override
    public ListModel getValue(JList instance) {
        return instance.getModel();
    }

    @Override
    public void setValue(JList instance, ListModel value) {
        if (value == null) {
            instance.setModel(new DefaultListModel());
        } else {
            instance.setModel(value);
        }
    }

    @Override
    public ListenerHandle<JList> addPropertyChangeListener(JList tgt, PropertyChangeListener listener) {
        return new ListenerHandleComponentImpl<JList>(tgt, "model", listener);
    }

    @Override
    public boolean isWriteable() {
        return true;
    }
}
