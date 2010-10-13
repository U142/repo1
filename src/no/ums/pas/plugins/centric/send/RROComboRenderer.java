package no.ums.pas.plugins.centric.send;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import no.ums.ws.parm.CBMESSAGEFIELDSBASE;

public class RROComboRenderer extends DefaultListCellRenderer
{
	protected JComboBox combo;
	JLabel lbl = new JLabel("");
	public RROComboRenderer(JComboBox c)
	{
		combo = c;
	}
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
        super.getListCellRendererComponent(list, value, index, isSelected, 
                cellHasFocus); 
        if(value.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class)) 
        { 
        	RROComboBox rro = (RROComboBox)combo;
        	String szCurrentText = rro.getEditor().getItem().toString();
        	CBMESSAGEFIELDSBASE base = (CBMESSAGEFIELDSBASE)value; 
            lbl.setText(base.getSzName());
            String szText = "<html>";
            
            if(rro.n_chars_available<base.getSzName().length()) //+szCurrentText.length()
            {
            	szText += "<font color=black>";
            	szText += base.getSzName().substring(0, rro.n_chars_available); //+szCurrentText.length()
            	szText += "</font>";
            	szText += "<font color=red>";
            	szText += base.getSzName().substring(rro.n_chars_available); //+szCurrentText.length()
            	szText += "</font>";
            	lbl.setEnabled(false);
            	this.setEnabled(false);
            	super.setEnabled(false);
            }
            else
            	szText += base.getSzName();
            szText += "</html>";
        	super.setText(szText);
        	/*if(isSelected)
        	{
        		lbl.setBackground(Color.red);
        		lbl.setForeground(Color.red);
        	}
        	else
        	{
        		lbl.setBackground(UIManager.getColor("ComboBox.background"));
        		lbl.setForeground(list.getForeground());
        	}
        	return lbl;*/
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