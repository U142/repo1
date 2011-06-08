package no.ums.pas.plugins.centric.send;

import no.ums.pas.ums.tools.TextFormat;
import no.ums.pas.ums.tools.TextFormat.RegExpResult;
import no.ums.ws.common.cb.CBMESSAGEFIELDSBASE;

import javax.swing.*;

public class RROComboBox extends JComboBox
{
	Class classtype;
	protected int n_chars_available = 0;
	public void setCharsAvailable(int n) { 
		n_chars_available = n;
		if(this.getEditor().getItem().toString().length()==0 && n_chars_available>0)
			n_chars_available--;
		//log.debug("Chars available="+n);
	}
	
	protected boolean bSelecting = true;
	public RROComboBox(Class c)
	{
		classtype = c;
	}
	
	@Override
	public void setSelectedItem(Object anObject) {
//		Object curobj = this.getSelectedItem();
		super.setSelectedItem(anObject);
		//log.debug("setSelectedItem="+anObject);
	}
	
	public void updateEditor()
	{
		Object o = super.getSelectedItem();
		if(o.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class))
		{
			String str = ((CBMESSAGEFIELDSBASE)o).getSzName();
			RegExpResult res = TextFormat.RegExpGsm(str);
			if(!res.valid)
			{
				str = res.resultstr;
				this.setSelectedItem(str);
			}
			int avail = n_chars_available;// + this.getEditor().getItem().toString().length();
			if(avail<str.length())
			{
				String szShortStr = str.substring(0, avail);
				//getEditor().setItem(szShortStr);
				this.setSelectedItem(szShortStr);
			}
		}
	}

	@Override
	public Object getSelectedItem() {
		Object o = super.getSelectedItem();
		if(o==null)
			return null;
		if(o.getClass().getSuperclass().equals(CBMESSAGEFIELDSBASE.class))
		{
			String str = ((CBMESSAGEFIELDSBASE)o).getSzName();
			/*if(n_chars_available<str.length())
			{
				String szShortStr = str.substring(0, n_chars_available);
				//CBMESSAGEFIELDSBASE msg = new CBMESSAGEFIELDSBASE();
				//msg.setLPk(-1);
				//msg.setSzName(szShortStr);
				//getEditor().setItem(szShortStr);
				return szShortStr;
			}
			else*/
			{
				return str;
			}
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