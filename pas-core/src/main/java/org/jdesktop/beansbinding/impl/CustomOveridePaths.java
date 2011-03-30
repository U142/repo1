package org.jdesktop.beansbinding.impl;

import org.jdesktop.beansbinding.PathAccessor;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class CustomOveridePaths {

    public static final PathAccessor<JList, Object> JLIST_SELECTED_ELEMENT = new PathJListSelectedElement();
    public static final PathAccessor<JComboBox, Object> JCOMBOBOX_SELECTED_ITEM = new PathComboboxSelectedItem();
    public static final PathAccessor<JTextComponent, String> JTEXTCOMPONENT_TEXT = new PathJTextComponentText();
    public static final PathAccessor<AbstractButton, Boolean> ABSTRACT_BUTTON_SELECTED = new PathAbstractButtonSelected();

    public static final List<? extends PathAccessor> OVERRIDES = Arrays.asList(
            CustomOveridePaths.JCOMBOBOX_SELECTED_ITEM,
            CustomOveridePaths.JLIST_SELECTED_ELEMENT,
            CustomOveridePaths.JTEXTCOMPONENT_TEXT,
            CustomOveridePaths.ABSTRACT_BUTTON_SELECTED);


}
