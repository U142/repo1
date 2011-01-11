package no.ums.pas.plugins.centric.send;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import no.ums.pas.ums.tools.Utils;
import no.ums.ws.parm.CBMESSAGEFIELDSBASE;

public class RROComboEditor extends BasicComboBoxEditor
{
	public RROComboEditor()
	{
		super();
		getEditorComponent().addFocusListener(this);
		JTextComponent c = (JTextComponent)getEditorComponent();
		Utils.disableTextComponentFeature(c, Utils.TEXT_FEATURE_CUT | Utils.TEXT_FEATURE_PASTE);
		//field.addFocusListener(this);
		
	}
	JFormattedTextField field = new JFormattedTextField();
	@Override
	public Component getEditorComponent() {
		return super.getEditorComponent();
		//return field;
	}
	@Override
	public Object getItem() {
		return super.getItem();
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		super.focusGained(arg0);
		((JTextComponent)getEditorComponent()).selectAll();
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		super.focusLost(arg0);
	}
	@Override
	public void setItem(Object anObject) {
		super.setItem(anObject);
		if(anObject==null)
		{
			//field.setValue("");
			return;
		}
		if(anObject.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class))
		{
			CBMESSAGEFIELDSBASE base = (CBMESSAGEFIELDSBASE)anObject;
			field.setValue(base.getSzName());
			((JTextComponent)getEditorComponent()).setText(base.getSzName());
		}
	}

	
}