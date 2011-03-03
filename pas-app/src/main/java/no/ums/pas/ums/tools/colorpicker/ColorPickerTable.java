package no.ums.pas.ums.tools.colorpicker;

import no.ums.pas.PAS;
import no.ums.pas.status.StatusCode;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;


public class ColorPickerTable extends ColorPicker {
	public static final long serialVersionUID = 1;
	//SearchPanelResults.TableList m_table;
	JTable m_table;
	PAS m_pas;
	PAS get_pas() { return m_pas; }
	Object m_obj_id;
	int m_n_tablerow;
	
	public ColorPickerTable(PAS pas, String sz_title, Point p, Color init, 
					 Component parent, JTable table, Object obj_id, int n_tablerow)
	{
		super(sz_title, p, init, parent);
		m_obj_id = obj_id;
		m_table = table;
		m_pas = pas;
		m_n_tablerow = n_tablerow;
	}
	public void stateEvent(ChangeEvent e)
	{
		//m_table.setValueAt(get_selected_color(), m_n_tablerow, 5);
		//get_pas().get_statuscontroller().set_statuscolor(((Integer)(m_obj_id)).intValue(), get_selected_color());
		m_table.setValueAt(get_selected_color(), m_n_tablerow, 5);
		get_pas().get_statuscontroller().set_statuscolor((StatusCode)m_obj_id, get_selected_color());
		PAS.get_pas().kickRepaint();
	}
	public void onOk(ActionEvent e)
	{
		
	}
	public void show() {
		super.show();
	}
}