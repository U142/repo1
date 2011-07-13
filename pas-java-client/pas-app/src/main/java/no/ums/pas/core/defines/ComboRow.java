package no.ums.pas.core.defines;

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


