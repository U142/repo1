package no.ums.pas.core.defines.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.defines.SearchPanelResults.TableDateFormatter;


public abstract class TreeTable extends SearchPanelResults
{
	public TreeTable(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim)
	{
		super(sz_columns, n_width, b_editable, dim);
	}
	@Override 
	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, 0, width, this.getHeight());
	}

	@Override
	public boolean is_cell_editable(int row, int col) {
		return false;
	}

	@Override
	protected void onMouseLClick(int n_row, int n_col,
			Object[] rowcontent, Point p) {
		
	}

	@Override
	protected void onMouseLDblClick(int n_row, int n_col,
			Object[] rowcontent, Point p) {
		System.out.println("Doubleclick");
	}

	@Override
	protected void onMouseRClick(int n_row, int n_col,
			Object[] rowcontent, Point p) {
		
	}

	@Override
	protected void onMouseRDblClick(int n_row, int n_col,
			Object[] rowcontent, Point p) {
		
	}

	@Override
	protected void start_search() {
		
	}

	@Override
	protected void valuesChanged() {
	}
	@Override
	public abstract void set_custom_cellrenderer(TableColumn column, final int n_col);
}