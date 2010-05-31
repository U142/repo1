package no.ums.pas.core.defines;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TblCellColor implements TableCellRenderer {
	private	boolean		m_isSelected = false;
	private	boolean		m_hasFocus = false;
	private Color		m_bgcol = null;
	DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
	
	public TblCellColor()
	{
		super();
	}
	private void configureRenderer(JLabel renderer, Object value) {
	    if ((value != null) && (value instanceof Color)) {
	    	renderer.setBackground((Color)value);
			renderer.setText("");
	    } else {
	    	renderer.setIcon(null);
	    	renderer.setText((String) value);
	    }
	}
	
	public Component getTableCellRendererComponent( JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column )
	{
		tableRenderer = (DefaultTableCellRenderer) tableRenderer
        .getTableCellRendererComponent(table, value, isSelected,
            hasFocus, row, column);
		configureRenderer(tableRenderer, value);
    		
		return tableRenderer;
	}
}