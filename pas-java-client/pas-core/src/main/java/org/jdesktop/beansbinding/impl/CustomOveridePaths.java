package org.jdesktop.beansbinding.impl;

import org.jdesktop.beansbinding.PathAccessor;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class CustomOveridePaths {

    public static final List<? extends PathAccessor> OVERRIDES = Arrays.asList(
            new PathComboboxSelectedItem(),
            new PathJListSelectedElement(),
            new PathJListSelectedElements(),
            new PathJListModel(),
            new PathJComboBoxModel(),
            new PathJTextComponentText(),
            new PathAbstractButtonSelected());

}
