package no.ums.pas.plugins.centric.send;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import no.ums.ws.parm.CBMESSAGEFIELDSBASE;

public class RROComboRenderer extends DefaultListCellRenderer
{
	protected JComboBox combo;
	public RROComboRenderer(JComboBox c)
	{
		combo = c;
	}
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
        super.getListCellRendererComponent(list, value, index, isSelected, 
                cellHasFocus); 
        if(value.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class)) 
        { 
        	CBMESSAGEFIELDSBASE base = (CBMESSAGEFIELDSBASE)value; 
            //lbl.setText(base.getSzName());
        	super.setText(base.getSzName());
        	//if(value!=null)
        	//	combo.getEditor().setItem(value);
        }
        else
        {
        	System.out.println("not combo");
        }
        return this; 
    } 

}