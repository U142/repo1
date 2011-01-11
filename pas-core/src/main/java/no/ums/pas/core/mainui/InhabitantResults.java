package no.ums.pas.core.mainui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.ListSelectionModel;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.popupmenus.PUInhabitantList;
import no.ums.pas.status.*;
import no.ums.pas.ums.errorhandling.Error;



public class InhabitantResults extends SearchPanelResults {
	public static final long serialVersionUID = 1;
	private PAS m_pas;
	private InhabitantFrame m_inhabitantframe;
	public InhabitantFrame get_inhabitantframe() { return m_inhabitantframe; }
	//private int n_refno_column = 1;
	public PAS get_pas() { return m_pas; }
	private PUInhabitantList m_popup_list = null;
	public PUInhabitantList get_popup_list() { return m_popup_list; }
	private int m_col_item = 0;
	private int m_col_object = 2;
	
	public InhabitantResults(PAS pas, InhabitantFrame inhabitantframe, String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim)
	{
		super(sz_columns, n_width, b_editable, dim/*new Dimension(800, 200)*/, ListSelectionModel.SINGLE_SELECTION);
		m_pas = pas;
		m_inhabitantframe = inhabitantframe;
		m_popup_list = new PUInhabitantList(get_pas(), "Inhabitant");
	}
	void populate(int n_row, Point p)
	{
		StatusItemObject obj = (StatusItemObject)get_table().getValueAt(n_row, m_col_object);
		get_popup_list().set_statusitemobject(obj);
		//get_popup_list().set_id(obj_item);
	}
	
	protected synchronized void start_search()
	{
		
		
	}
	protected void valuesChanged() { }

	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}	
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		try {
			populate(n_row, p);
			//get_popup_list().postEvent(new Event(this, 0, "act_find"));
			ActionEvent e = new ActionEvent(get_popup_list().get_actionlistener(), ActionEvent.ACTION_PERFORMED, "act_find");
			get_popup_list().get_actionlistener().actionPerformed(e);
			//Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
		} catch(Exception e) { 
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("InhabitantResults","Exception in onMouseLDblClick",e,1);
		}
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		try {
			populate(n_row, p);
			get_popup_list().pop(this.m_tbl, p);
			//get_pas().add_event("Rightclick: open popup menu");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("InhabitantResults","Exception in onMouseRClick",e,1);
		}
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	void onDownloadFinished()
	{
		
	}	
}