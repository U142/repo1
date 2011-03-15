package org.jdesktop.validation;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public class VisibleValidation {
	public static final Color colError = Color.red;
	public static final Color colOkText = SystemColor.textText;
	
	public static final Pattern patternEmail = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}" +
			"\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\" + 
			".)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

	public static final Pattern patternEmailServer = Pattern.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$");

	public static final Pattern patternWms = Pattern.compile("(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");

	public static final Border oldBorder = new JTextField().getBorder();
	
	public static boolean isToolTipShowing = false;
	
	private static boolean doNotify(JTextComponent c, boolean bIsOk, String toolTip)
	{
		c.setForeground(bIsOk ? colOkText : colError);
		boolean tooltip = (c.getClientProperty("UToolTipShowing")==null ? false : (Boolean)c.getClientProperty("UToolTipShowing"));
		if(!tooltip && !bIsOk && c.hasFocus() && toolTip!=null && toolTip.length()>0)
		{
			c.setToolTipText(toolTip);
			ActionEvent postTip = new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "");
			c.getActionMap().get("postTip").actionPerformed(postTip);
			System.out.println("PostTip");
			c.putClientProperty("UToolTipShowing", Boolean.TRUE);
		}
		else if(bIsOk && tooltip)
		{
			ActionEvent postTip = new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "");
			c.getActionMap().get("postTip").actionPerformed(postTip);			
			c.putClientProperty("UToolTipShowing", Boolean.FALSE);
		}
		return bIsOk;
	}
	
	public static boolean validateEmail(String text, JTextComponent c)
	{
		return doNotify(c, patternEmail.matcher(text).matches(), "Write a valid e-mail address");
	}
	
	public static boolean validateUrl(String text, JTextComponent c)
	{
		return doNotify(c, patternWms.matcher(text).matches(), "Write a valid URL");
	}
	public static boolean validateHost(String text, JTextComponent c)
	{
		return doNotify(c, patternEmailServer.matcher(text).matches(), "Write a valid host");
	}
}
