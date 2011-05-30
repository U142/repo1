package no.ums.pas.ums.tools;

import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ToolTipManager;

import no.ums.pas.localization.Localization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class StdTextArea extends JTextField// JTextArea
{
	public static final String REGEXP_SMS_OADC = "^[a-zA-Z0-9_]*$";
	
	public static final String REGEX_GSM7 = "[^[a-zA-Z0-9 " + "\u0394\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039E" + "\\.\\_\\@\\£\\$\\¥\\è\\é\\ù\\ì\\ò\\Ç\\Ø\\ø\\Å\\å\\Æ\\æ\\ß\\É\\Ä\\Ö\\Ñ\\Ü\\§\\¿\\ä\\ö\\ñ\\ü\\à\\+\\,\\/\\:\\;\\<\\=\\>\\?\\¡\\|\\^\\{\\}\\*\\!\\#\\€\\%\\&\\'\\(\\)\r\n\\\\\\[\\]\"\\~\\-]]";	
	//public static final String REGEX_GSM7 = "[^[a-zA-Z0-9 " + "\u0394\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039E" + "._@£$¥èéùìòÇØøÅåÆæßÉÄÖÑÜ§¿äöñüà+,/:;<=>?¡|^{}*!#€%&'()\r\n\\[]\"~-]]";	

	private boolean bUseRegexpTooltip = false;
	
	private String getTooltipTextBasedOnRegexp()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<b>");
		sb.append(Localization.l("common_valid_input"));
		sb.append("</b><br>");
		if(regexpPattern!=null)
		{
			int start = regexpValidation.indexOf("[");
			int end = regexpValidation.lastIndexOf("]");
			String s = regexpValidation.substring(start+1, end);
			if(start>=0 && end>=0)
			{
				//tooltip = s;
				boolean bRange = false;
				boolean bEscaped = false;
				for(char c : s.toCharArray())
				{
					if(c=='\\' && !bEscaped)
					{
						//ignore
						bEscaped = true;
						continue;
					}
					else if(c=='\\' && bEscaped)
					{
						sb.append(" \\ ");
						bEscaped = false;
					}
					else if(c=='\r')
					{
						sb.append(" CR ");
					}
					else if(c=='\n')
					{
						sb.append(" LF ");
					}
					else if(c=='^' && !bEscaped)
					{
						//ignore
					}
					else if(c=='^' && bEscaped)
					{
						System.out.println("break");
						sb.append(" " + c + " ");
						bEscaped = false;
					}
					else if(c=='[' && !bEscaped)
					{
						//ignore
					}
					else if(c==']' && !bEscaped)
					{
						//ignore
					}
					else if(c=='-' && !bEscaped) //a range found
					{
						bRange = true;
						sb.append(" ");
						sb.append(Localization.l("common_to"));
						sb.append(" ");
						continue;
					}
					else if(bRange)
					{
						sb.append(c + "<br> ");
						bRange = false;
						bEscaped = false;
					}
					else
					{
						sb.append(c + " ");
						bRange = false;
						bEscaped = false;
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		if(!bUseRegexpTooltip)
			return super.getToolTipText(event);
		return createToolTipText();
	}

	private String regexpValidation = null;
	private int stringLengthLimit = 0;
	private Pattern regexpPattern = null;
	
	public String getRegexpValidation() {
		return regexpValidation;
	}
	
	JToolTip tooltip = null;
	Popup m_popup;
		
	
	public String createToolTipText()
	{
		if(!bUseRegexpTooltip)
			return super.getToolTipText();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(getTooltipTextBasedOnRegexp());
		if(stringLengthLimit>0)
		{
			sb.append("<br><br><b>");
			sb.append(Localization.l("common_max_length"));
			sb.append("</b>&nbsp;" + stringLengthLimit);
		}
		sb.append("</html>");
		return sb.toString();
	}
	
	public void setRegexpValidation(String regexpValidation, boolean bSetAsTooltip) {
		this.regexpValidation = regexpValidation;
		regexpPattern = Pattern.compile(getRegexpValidation());	
		bUseRegexpTooltip = bSetAsTooltip;
		if(bUseRegexpTooltip)
		{
			final String tip = createToolTipText();
			StdTextArea.this.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					if(m_popup!=null)
						m_popup.hide();
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					if(tooltip==null)
					{
						tooltip = new JToolTip();
					}
					tooltip.setTipText(tip);
					m_popup = PopupFactory.getSharedInstance().getPopup(StdTextArea.this, 
							tooltip, 
							StdTextArea.this.getLocationOnScreen().x, 
							StdTextArea.this.getLocationOnScreen().y+StdTextArea.this.getHeight());
					m_popup.show();
				}
			});
		}
	}

	public void setRegexpValidation(String regexpValidation) {
		setRegexpValidation(regexpValidation, false);
	}

	public int getStringLengthLimit() {
		return stringLengthLimit;
	}

	public void setStringLengthLimit(int stringLengthLimit) {
		this.stringLengthLimit = stringLengthLimit;
	}

	@Override
	public void copy() {
		if(b_enable_copy)
			super.copy();
	}

	@Override
	public void paste() {
		if(b_enable_paste)
			super.paste();
	}
	public static final long serialVersionUID = 1;
	int m_n_items = 0;
	int m_n_itemsize = 17;
	Dimension m_dim = null;
	protected boolean b_enable_copy = true;
	protected boolean b_enable_paste = true;
	
	public void setEnableCopy(boolean b)
	{
		b_enable_copy = b;
	}
	public void setEnablePaste(boolean b)
	{
		b_enable_paste = b;
	}
	
	public String toString()
	{
		return getText();
	}
	
	public StdTextArea(String sz_text)
	{
		super(sz_text);
		init(true);
	}
	public StdTextArea(String sz_text, boolean bIsHeading)
	{
		super(sz_text);
		init(bIsHeading);
	}
	public StdTextArea(String sz_text, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(true);
	}
	public StdTextArea(String sz_text, boolean bIsHeading, Dimension d) {
		super(sz_text);
		m_dim = new Dimension(d);
		init(bIsHeading);
	}
	public StdTextArea(String sz_text, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(true);
	}
	public StdTextArea(String sz_text, int n_width, int n_fontsize, boolean b_bold) {
		super(sz_text);
		m_dim = new Dimension(n_width, n_fontsize + 4);
		init(true, n_fontsize, b_bold);
	}
	public StdTextArea(String sz_text, boolean bIsHeading, int n_width) {
		super(sz_text);
		m_dim = new Dimension(n_width, 15);
		init(bIsHeading);
	}
	public void init(boolean bIsHeading) {
		init(bIsHeading, 12, false);
	}
	public StdTextArea(boolean bIsHeading, boolean bBold, int nWidth)
	{
		m_dim = new Dimension(nWidth, new JTextField().getPreferredSize().height);
		init(bIsHeading, 0, bBold);
	}
	public void init(boolean bIsHeading, int n_fontsize, boolean b_bold)
	{
		//FontSet this.setFont(new Font("Arial", (!b_bold ? Font.PLAIN : Font.BOLD), n_fontsize));
		//this.setFont(PAS.f().getControlFont());
		if(m_dim==null)
			m_dim = new Dimension(100, 15);
		this.setPreferredSize(m_dim);
		//this.setLineWrap(true);
		//this.setWrapStyleWord(bIsHeading);
		this.setEditable(!bIsHeading);
       		//this.setBounds(20, 10 + m_n_items * m_n_itemsize, 50, m_n_itemsize);
		m_n_items++;
		if(bIsHeading)
		{
			//this.setBackground(Color.lightGray);
			//this.setBackground(SubstanceLookAndFeel.);
			//this.setBackground(SubstanceLookAndFeel.getMenuBackground());
			//this.setBackground(SubstanceLookAndFeel.getCurrentTheme().getMenuBackground());
			this.setBorder(null);
		}
		else
		{
			
			//this.setLayout(PAS.get_pas().getLayout());
			//this.setBorder(new Border());
		}
	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		if(regexpPattern==null && getStringLengthLimit()<=0)
		{
			super.processKeyEvent(e);
			return;
		}
		String oldText = getText();
		int i = e.getKeyCode();
		//check for system chars. arrows, backspace, home, end, insert, delete
		if(i==37 || i==39 || i==40 || i==38 || i==35 || i==36 || i==127 || i == 8)
		{
			super.processKeyEvent(e);
			return;
		}
		if(getStringLengthLimit()>0) //check for string length
		{
			if(this.getText().length()>=getStringLengthLimit())
				return;
		}
		super.processKeyEvent(e);
		
		String newText = getText();
		if(regexpPattern!=null) //check for valid chars
		{
			if(!regexpPattern.matcher(newText).find())
			{
				setText(oldText);
			}
		}
	}
	
	
}