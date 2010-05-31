package no.ums.pas.ums.tools;

import java.awt.event.KeyEvent;

public class StdTextUTMZone extends StdTextArea {
	public static final long serialVersionUID = 1;
	//final static String badchars = "`~!@#$%^&*()_+=\\|\"':;?/>.<, ";
	final static String legal_chars = "CDEFGHJKLMNPQRSTUVWXcdefghjklmnpqrstuvwx";
	//legal numbers range from 1-60

	public StdTextUTMZone(String sz_text, boolean b_heading, int n_width) {
		super(sz_text, b_heading, n_width);
	}
	
	public String getUTM() {
		String ret = this.getText();
		if(ret.length()==2)
			ret = "0" + ret;
		return ret;
	}

	public void processKeyEvent(KeyEvent ev) {
		char c = ev.getKeyChar();
		
		
		int pos = ((StdTextArea)ev.getSource()).getCaretPosition();
		
		String s = String.valueOf(c);
		int num;
		boolean ok = true;
		if(ev.getKeyCode()==KeyEvent.VK_ENTER || ev.getKeyCode()==KeyEvent.VK_BACK_SPACE) {

		} else {
			switch(pos) {
				case 0:
					try {
						num = Integer.parseInt(s);
					} catch(Exception e) {
						ok = false;
					}
					break;
				case 1:
				case 2:
					if(legal_chars.indexOf(c) < 0) {	
						ok = false;
					} else {
						ev.setKeyChar(s.toUpperCase().charAt(0));
						break;
					}
					if(pos==1) {
						try {
							num = Integer.parseInt(s);
							ok = true;
						} catch(Exception e) {
							ok = false;
						}
					}
					break;
			}
		}
		if(ok)
			super.processKeyEvent(ev);
		else
			ev.consume();
		/*if((Character.isLetter(c) && !ev.isAltDown()) || badchars.indexOf(c) > -1) {
		     ev.consume();
		     return;
		}*/
		
		
		/*if(c == '-' && getDocument().getLength() > 0 && (m_n_type & UNSIGNED) != UNSIGNED) 
			ev.consume();
		else
			super.processKeyEvent(ev);*/
	}
}