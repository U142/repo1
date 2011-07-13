package no.ums.pas.status;

import no.ums.pas.core.defines.SearchPanelResults;

import java.awt.Dimension;
import java.awt.Point;


public class LBAListCell extends SearchPanelResults
{
	public static final long serialVersionUID = 1;
	public LBAListCell(String [] cols, int [] width, Dimension size)
	{
		super(cols, width, null, size);
	}

	@Override
	public boolean is_cell_editable(int row, int col) {
		return false;
	}

	@Override
	protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent,
			Point p) {
		
	}

	@Override
	protected void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent,
			Point p) {
		
	}

	@Override
	protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent,
			Point p) {
		
	}

	@Override
	protected void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent,
			Point p) {
		
	}

	@Override
	protected void start_search() {
		
	}

	@Override
	protected void valuesChanged() {
		
	}
}