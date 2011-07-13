package no.ums.pas.swing;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class ListRenderer<T> extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, (value instanceof String || value == null) ? value : getRepr((T) value), index, isSelected, cellHasFocus);
    }

    protected abstract Object getRepr(T value);


}
