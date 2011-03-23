package no.ums.pas.ums.tools;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StdIntegerArea extends StdTextArea {
	public static final long serialVersionUID = 1;
	public static final int INTEGER = 1;
	public static final int DOUBLE	= 2;
	public static final int UNSIGNED = 4;
	
	protected int m_n_type;
	protected Double minValue = null;
	protected Double maxValue = null;

	public StdIntegerArea(String sz_text, boolean b_heading, int n_width, int n_type) {
		super(sz_text, b_heading, n_width);
		setType(n_type);
	}
	public StdIntegerArea(String sz_text, boolean b_heading, int n_width, int n_type, boolean signed) {
		super(sz_text, b_heading, n_width);
		setType(n_type);
		setSigned(signed);
	}
	public StdIntegerArea(String sz_text, boolean b_heading, int n_width, int n_type, double minValue, double maxValue)
	{
		super(sz_text, b_heading, n_width);
		setType(n_type);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	public StdIntegerArea(String sz_text, boolean b_heading, int n_width, int n_type, 
							double minValue, double maxValue, boolean signed)
	{
		super(sz_text, b_heading, n_width);
		setType(n_type);
		this.minValue = minValue;
		this.maxValue = maxValue;
		setSigned(signed);
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
		setSigned(false);
	}
	
	
	final static String badchars_int = "`~!@#$%^&*()_+=\\|\"':;?/>.<, ";
	final static String badchars_double = "`~!@#$%^&*()_+=\\|\"':;?/>< ";
	final static String integer_regexp = "[-+]?([0-9]*";
	//final static String float_regexp = "[-+]?([0-9]*\\.[0-9]+|[0-9]+)";
	final static String float_regexp = "^[-+]?\\d*\\.?\\d*$";
	protected String badchars;
	
	public void processKeyEvent(KeyEvent ev) {
		String oldText = getText();
		char c = ev.getKeyChar();
		
		if((c == KeyEvent.VK_MINUS || c == '-') && getDocument().getLength() > 0 && (m_n_type & UNSIGNED) != UNSIGNED) 
		{
			ev.consume();
			String newText = oldText.replace("-", "");
			setText(newText);
		}
		if((Character.isLetter(c) && !ev.isAltDown()) || badchars.indexOf(c) > -1) {
		    ev.consume();
			setText(oldText);
		    return;
		}
		
		
		{
			Pattern p = Pattern.compile(float_regexp);
			super.processKeyEvent(ev);
			try
			{
				String valUnlocalized = getText().replace(",", ".");
				Matcher m = p.matcher(valUnlocalized);
				if(!m.find())
				{
					setText(oldText);
				}
				else
				{
					if(minValue!=null || maxValue!=null)
					{
						if(valUnlocalized.length()<=0)
							return;
						else if(valUnlocalized.equals("-"))
							valUnlocalized += "0";
						else if(valUnlocalized.equals("."))
							valUnlocalized = "0" + valUnlocalized;
						Double d = Double.valueOf(valUnlocalized);
						if((minValue!=null && d<minValue) || (maxValue!=null && d>maxValue))
						{
							d = (minValue!=null && d<minValue ? minValue : maxValue);
							if((m_n_type & INTEGER) == INTEGER)
							{
								setText(Integer.valueOf((int)Math.round(d)).toString());
							}
							else if((m_n_type & DOUBLE) == DOUBLE)
							{
								setText(Double.valueOf(d).toString());
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
}