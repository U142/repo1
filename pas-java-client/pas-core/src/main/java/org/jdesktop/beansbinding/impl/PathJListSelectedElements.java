package org.jdesktop.beansbinding.impl;

import com.google.common.collect.Sets;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PathJListSelectedElements extends AbstractCustomListenerPath<JList, ListSelectionListener, Iterable> {

    public PathJListSelectedElements() {
        super("selectedElements", JList.class, Iterable.class);
    }

    @Override
    protected ListSelectionListener createListener(final UpdateHandle updateHandle) {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateHandle.update();
            }
        };
    }

    @Override
    protected void addListener(JList current, ListSelectionListener listener) {
        current.addListSelectionListener(listener);
    }

    @Override
    protected void removeListener(JList current, ListSelectionListener listener) {
        current.removeListSelectionListener(listener);
    }

    @Override
    public Iterable getValue(JList instance) {
        return Arrays.asList(instance.getSelectedValues());
    }

    @Override
    public void setValue(JList instance, Iterable value) {
        if (value == null) {
            instance.setSelectedIndices(new int[0]);
        }
        else {
            final HashSet values = Sets.newHashSet(value);
            int[] selectedIndexes = new int[values.size()];
            int index = 0;
            for (int i=0; i<instance.getModel().getSize(); i++) {
                if (values.contains(instance.getModel().getElementAt(i))) {
                    selectedIndexes[index++] = i;
                }
            }
            instance.setSelectedIndices(selectedIndexes);
        }
    }

    @Override
    public boolean isWriteable() {
        return true;
    }
}
