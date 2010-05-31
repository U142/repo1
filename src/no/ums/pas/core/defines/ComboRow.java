package no.ums.pas.core.defines;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;


public class ComboRow
{
	private Object id;
	private Object [] columns;
	
	public ComboRow(Object id, Object [] cols)
	{
		this.id = id;
		this.columns = cols;
	}
	public Object getId() { return id; }
	public Object [] getVals() { return columns; }
	//public ComboRowCellRenderer newRowCellRenderer() { return new ComboRowCellRenderer.RowCellRenderer(); }

	public Object getVal() {
		return getVals()[0];
	}

	public Object getExtra() {
		return getVals()[1];
	}

}


