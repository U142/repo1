package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class EventPanel extends SearchPanelResults {
	public static final long serialVersionUID = 1;
	private GridLayout	m_gridlayout;
	private JTable m_tbl;
	public TableList m_tbl_list;
	private AdressTblListener m_listener;
	public ImageIcon m_icon_goto;
	//private PAS m_pas;
	private Dimension m_dimension;

	public Dimension get_dimension() { return m_dimension; }
	
	public EventPanel(PAS pas, String [] sz_columns, int [] n_width, Dimension panelDimension)
	{
		super(sz_columns, n_width, null, panelDimension, ListSelectionModel.SINGLE_SELECTION);
		m_dimension = panelDimension;
		//m_pas = pas;

	}
	protected void start_search()
	{
	}
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void valuesChanged() { }
	synchronized void add_row(String sz_text)
	{
		String sz_data[] = new String[] { new Date().toString(), sz_text };
		insert_row(sz_data, 0);
	}
	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}

}