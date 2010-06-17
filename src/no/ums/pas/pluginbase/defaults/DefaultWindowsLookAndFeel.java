package no.ums.pas.pluginbase.defaults;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class DefaultWindowsLookAndFeel extends WindowsLookAndFeel
{
	public static UIDefaults defaults;
	public DefaultWindowsLookAndFeel()
	{
		ColorUIResource o = (ColorUIResource)UIManager.get("Panel.background");
		int r = o.getRed();
		int g = o.getGreen();
		int b = o.getBlue();
		defaults = UIManager.getDefaults();
	}
}