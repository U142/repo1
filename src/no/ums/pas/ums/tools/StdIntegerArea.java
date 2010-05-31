package no.ums.pas.ums.tools;
import java.awt.event.KeyEvent;

public class StdIntegerArea extends StdTextArea {
	public static final long serialVersionUID = 1;
	public static final int INTEGER = 1;
	public static final int DOUBLE	= 2;
	public static final int UNSIGNED = 4;
	
	protected int m_n_type;

	public StdIntegerArea(String sz_text, boolean b_heading, int n_width, int n_type) {
		super(sz_text, b_heading, n_width);
		setType(n_type);
	}
	
	
	public void setSigned(boolean b) {
		if(b)
			m_n_type &= ~UNSIGNED;
		else
			m_n_type |= UNSIGNED;
	}
	
	public void setType(int n) {
		m_n_type = n;
		if((m_n_type & INTEGER) == INTEGER)
			badchars = badchars_int;
		else if((m_n_type & DOUBLE) == DOUBLE)
			badchars = badchars_double;
	}
	
	
	final static String badchars_int = "`~!@#$%^&*()_+=\\|\"':;?/>.<, ";
	final static String badchars_double = "`~!@#$%^&*()_+=\\|\"':;?/><, ";
	protected String badchars;
	
	public void processKeyEvent(KeyEvent ev) {
		char c = ev.getKeyChar();
		
		if((Character.isLetter(c) && !ev.isAltDown()) || badchars.indexOf(c) > -1) {
		     ev.consume();
		     return;
		}
		
		
		if(c == '-' && getDocument().getLength() > 0 && (m_n_type & UNSIGNED) != UNSIGNED) 
			ev.consume();
		else
			super.processKeyEvent(ev);
	}
	
}