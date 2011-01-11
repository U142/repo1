package no.ums.pas.core.defines;

import javax.swing.table.AbstractTableModel;

public class ComboRowTableModel extends AbstractTableModel {

	private ComboRow row;
	private int cols;
	public ComboRowTableModel(ComboRow row, int cols) {
		this.row = row;
		this.cols = cols;
	}

	public int getRowCount() {
		return 1;
	}

	public int getColumnCount() {
		return cols;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(row.getVals().length > columnIndex)
		{
			if(row.getVals()[columnIndex]!=null)
				return row.getVals()[columnIndex].toString();
			else
				return null;
		}
		else
			return "N/A";
	}
}