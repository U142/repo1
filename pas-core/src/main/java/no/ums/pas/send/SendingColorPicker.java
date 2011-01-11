package no.ums.pas.send;

import no.ums.pas.ums.tools.colorpicker.ColorPicker;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;


public class SendingColorPicker extends ColorPicker {
	public static final long serialVersionUID = 1;
	public SendingColorPicker(String sz_title, Point p, Color col_init, Component parent) {
		super(sz_title, p, col_init, parent);
	}
	public void onOk(ActionEvent e) {
		SendOptionToolbar tb = (SendOptionToolbar)get_parent();
		ActionEvent col_event = new ActionEvent(get_selected_color(), ActionEvent.ACTION_PERFORMED, "act_set_color");
		tb.actionPerformed(col_event);
	}
	public void stateEvent(ChangeEvent e) {
		ActionEvent col_event = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "act_set_color");
		onOk(col_event);
	}
}