package org.jdesktop.beansbinding.impl;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListModel;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PathJComboBoxModel extends AbstractPathAccessor<JComboBox, ComboBoxModel> {

    public PathJComboBoxModel() {
        super("model", JComboBox.class, ComboBoxModel.class);
    }

    @Override
    public ComboBoxModel getValue(JComboBox instance) {
        return instance.getModel();
    }

    @Override
    public void setValue(JComboBox instance, ComboBoxModel value) {
        if (value == null) {
            instance.setModel(new DefaultComboBoxModel());
        } else {
            instance.setModel(value);
        }
    }

    @Override
    public ListenerHandle<JComboBox> addPropertyChangeListener(JComboBox tgt, PropertyChangeListener listener) {
        return new ListenerHandleComponentImpl<JComboBox>(tgt, "model", listener);
    }

    @Override
    public boolean isWriteable() {
        return true;
    }
}
