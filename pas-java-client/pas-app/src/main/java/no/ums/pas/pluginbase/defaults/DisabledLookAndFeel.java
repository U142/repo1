package no.ums.pas.pluginbase.defaults;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import java.util.Enumeration;

public class DisabledLookAndFeel extends WindowsLookAndFeel
{

    private static final Log log = UmsLog.getLogger(DisabledLookAndFeel.class);

	public DisabledLookAndFeel()
	{
		super();
		UIDefaults d = UIManager.getDefaults();
		Enumeration<Object> en = d.keys();
		while(en.hasMoreElements())
		{
			Object key = en.nextElement();
			Object o = d.get(key);
			log.debug(key + " = " + o);
		}
		UIManager.getDefaults().putDefaults(setMyDefaultColors());
	}
	private Object[] setMyDefaultColors() {
		Color c = new ColorUIResource(new Color(250,250,250));
		Color txt = new ColorUIResource(new Color(200,200,200));
			return new Object[] {
		  "Panel.background", c,
	      "TabbedPane.background", c,
	      "Table.background", c,
	      "Label.background", c,
	      "RadioButton.background", c,
	      "InternalFrame.activeTitleBackground", c,
	      //"Label.foreground", txt,
	      "TabbedPane.foreground", txt,
	      "RadioButton.foreground", txt,
	      "MenuItem.foreground", txt,
	      "List.foreground", txt,
	      //"TextField.foreground", txt,
	      "Button.foreground", txt,
	      "CheckBoxMenuItem.foreground", txt,
	      "TextArea.foreground", txt,
	      "Menu.foreground", txt
		};
	}
	@Override
	public String getDescription() {
		
		return "PAS look and feel";
	}

	@Override
	public String getID() {
		return super.getID();
	}

	@Override
	public String getName() {
		return "Disabled LAF";
	}

	@Override
	protected void initSystemColorDefaults(UIDefaults table) {
		/*String SCMB_BLUE = "#00007F";
		String[] defaultSystemColors = new String [] {
		"desktop", "#005C5C", 
		"activeCaption", "#000080", 
		"activeCaptionText", SCMB_BLUE, 
		"activeCaptionBorder", "#C0C0C0", 
		"inactiveCaption", "#808080", 
		"inactiveCaptionText", "#C0C0C0", 
		"inactiveCaptionBorder", "#C0C0C0", 
		"window", "#FFFFFF", 
		"windowBorder", "#000000", 
		"windowText", "#000000", 
		"menu", "#C0C0C0", 
		"menuPressedItemB", "#000080",
		"menuPressedItemF", SCMB_BLUE, 
		"menuText", SCMB_BLUE, 
		"text", "#C0C0C0", 
		"textText", SCMB_BLUE, 
		"textHighlight", "#000080", 
		"textHighlightText", "#FFFFFF",
		"textInactiveText", "#646464", 
		"control", "#D6D3CE", 
		"controlText", SCMB_BLUE, 
		"controlHighlight", "#E0E0E0", 
		"controlLtHighlight", "#FFFFFF",
		"controlShadow", "#808080", 
		"controlDkShadow", "#000000", 
		"scrollbar", "#E0E0E0", 
		"info", "#FFFFFF",
		"infoText", SCMB_BLUE
		};

		loadSystemColors(atablerg0, defaultSystemColors, isNativeLookAndFeel());*/
	       table.put("control", new ColorUIResource(Color.green));
	       table.put("menu", new ColorUIResource(Color.green));
		   super.initSystemColorDefaults(table);
		
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
	
}