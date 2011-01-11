package no.ums.pas.parm.event;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldLimit extends PlainDocument{
	public static final long serialVersionUID = 1;
	int maxLength = -1;
	public TextFieldLimit(int len){ this.maxLength = len;}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	  {
	    if (str != null && maxLength > 0 && this.getLength() + str.length() > maxLength)
	    {
	      return;
	    }
	    super.insertString(offs, str, a);
	  }

}
