package org.jdesktop.beansbinding.validation;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import no.ums.pas.localization.Localization;

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
	
	private static boolean doNotify(JTextComponent c, boolean bIsOk, String toolTip, boolean bForced)
	{
		c.setForeground(bIsOk ? colOkText : colError);
		boolean tooltipShowing = (c.getClientProperty("UToolTipShowing")==null ? false : (Boolean)c.getClientProperty("UToolTipShowing"));
		boolean hasFocus = c.hasFocus();
		if(bForced || (!tooltipShowing && !bIsOk && hasFocus && toolTip!=null && toolTip.length()>0))
		{
			c.setToolTipText(toolTip);
			ActionEvent postTip = new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "");
			c.getActionMap().get("postTip").actionPerformed(postTip);
			System.out.println("PostTip " + toolTip);
			c.putClientProperty("UToolTipShowing", Boolean.TRUE);
			isToolTipShowing = true;
		}
		else if(bIsOk && tooltipShowing)
		{
			ActionEvent postTip = new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "");
			c.getActionMap().get("postTip").actionPerformed(postTip);			
			c.putClientProperty("UToolTipShowing", Boolean.FALSE);
			System.out.println("RemoveTip " + c.getText());
			c.setToolTipText(null);
			isToolTipShowing = false;
		}
		return bIsOk;
	}
	
	private static boolean doNotify(JTextComponent c, boolean bIsOk, String toolTip)
	{
		return doNotify(c, bIsOk, toolTip, false);
	}
	
	public static void forceInvalid(JTextComponent c, String toolTip)
	{
		doNotify(c, false, toolTip, true);
	}
	
	public static void forceValid(JTextComponent c)
	{
		doNotify(c, true, "", true);
	}
	
	public static boolean validateEmail(String text, JTextComponent c)
	{
		return doNotify(c, patternEmail.matcher(text).matches(), Localization.l("common_validation_failed_email"));
	}
	
	public static boolean validateUrl(String text, JTextComponent c)
	{
		return doNotify(c, patternWms.matcher(text).matches(), Localization.l("common_validation_failed_url"));
	}
	public static boolean validateHost(String text, JTextComponent c)
	{
		return doNotify(c, patternEmailServer.matcher(text).matches(), Localization.l("common_validation_failed_hostname"));
	}
}
