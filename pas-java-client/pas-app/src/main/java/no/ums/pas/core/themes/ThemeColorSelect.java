package no.ums.pas.core.themes;

import no.ums.pas.PAS;
import no.ums.pas.ums.tools.colorpicker.ColorPicker;

import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class ThemeColorSelect extends ColorPicker
{
	Color selected;
	ActionListener callback;
	ThemeColorComponent coltype;
	ThemeColorSelect(Color col, ThemeColorComponent coltype, ActionListener callback)
	{
		super("Color", new Point(0,0), col, PAS.get_pas());
		selected = col;
		this.coltype = coltype;
		this.callback = callback;
	}
	@Override
	public void onOk(ActionEvent e) {
		selected = this.get_selected_color();
		callback.actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, coltype.name()));
	}

	@Override
	public void stateEvent(ChangeEvent e) {
		selected = this.get_selected_color();
		callback.actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, coltype.name()));		
	}
	
}