package no.ums.pas.plugins.centric.send;

import javax.swing.JComboBox;

import no.ums.ws.parm.CBMESSAGEFIELDSBASE;
import no.ums.ws.parm.CBORIGINATOR;
import no.ums.ws.parm.CBREACTION;
import no.ums.ws.parm.CBRISK;

public class RROComboBox extends JComboBox
{
	Class classtype;
	protected boolean bSelecting = true;
	public RROComboBox(Class c)
	{
		classtype = c;
	}
	
	@Override
	public void setSelectedItem(Object anObject) {
		super.setSelectedItem(anObject);
		//System.out.println("setSelectedItem="+anObject);
	}

	@Override
	public Object getSelectedItem() {
		Object o = super.getSelectedItem();
		if(o==null)
			return null;
		if(o.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class))
		{
			return ((CBMESSAGEFIELDSBASE)o).getSzName();
		}
		else
			return o.toString();
		/*if(o==null)
			return null;
		if(o.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class))
		{
			return super.getSelectedItem();
		}
		CBMESSAGEFIELDSBASE ret = null;
		if(classtype.equals(CBRISK.class))
			ret = new CBRISK();
		else if(classtype.equals(CBREACTION.class))
			ret = new CBREACTION();
		else if(classtype.equals(CBORIGINATOR.class))
			ret = new CBORIGINATOR();
		ret.setLPk(-1);
		ret.setSzName(super.getSelectedItem().toString());
		return ret;*/
	}
	
}