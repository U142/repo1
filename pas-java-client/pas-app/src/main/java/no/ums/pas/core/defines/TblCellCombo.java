package no.ums.pas.core.defines;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class TblCellCombo extends JComboBox implements TableCellRenderer {
	public static final long serialVersionUID = 1;
    public TblCellCombo(String[] items) {
        super(items);
        
        
      //FontSet this.setFont(new Font("Arial", Font.BOLD, 10));
        //this.setFont(PAS.f().getControlFont());
        this.setPreferredSize(new Dimension(100, 16));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }
    //public void validate() {}
    //public void revalidate() {}
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
 
}

class ComboEditor extends DefaultCellEditor implements ComboBoxEditor {
	public static final long serialVersionUID = 1;
    public ComboEditor(String[] items) {
        super(new JComboBox(items));
    }
    public void addActionListener(ActionListener cb) {
    	
    }
    public Component getEditorComponent() {
    	return null;
    }
    public Object getItem() {
    	return this;
    }
    public void removeActionListener(ActionListener cb) {
    	
    }
    public void selectAll() {
    	
    }
    public void setItem(Object o) {
    	
    }
}
