package no.ums.pas.status;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import no.ums.pas.core.defines.*;
import no.ums.pas.core.logon.UserInfo;

import java.awt.*;


public class LBAListCC extends SearchPanelResults
{
	public static final long serialVersionUID = 1;

	public int operator = -1;
	//Class []classes;
	public LBAListCC(String [] cols, int [] width, Dimension size)
	{
		super(cols, width, null, size);
		super.sort(0);
		SetColumnClass(7, JProgressBar.class);
	}
	
	
	
	public void SetRow(Object [] data, int n_index)
	{
		//super.insert_row(data, n_index)
		int index = super.find(data[1], 1, Integer.class); //identify by ccode number
		boolean [] cols = new boolean[] { false, false, true, true, true, true, true, true };
		
		if(index>=0)
		{
			//update
			super.edit_row(data, index, cols);
		}
		else
		{
			super.insert_row(data, n_index);
			//insert
		}
	}
	
	
	@Override
	public void exportToCSV() {
		// TODO Auto-generated method stub
		super.exportToCSV();
	}



	@Override
	public void set_custom_cellrenderer(TableColumn column, int n_col)
	{
		/*if(n_col==6)
		{
			column.setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					return (JProgressBar)value;
				}
			});

		}*/
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