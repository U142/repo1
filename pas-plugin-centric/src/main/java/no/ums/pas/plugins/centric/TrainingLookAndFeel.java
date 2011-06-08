package no.ums.pas.plugins.centric;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

public class TrainingLookAndFeel extends WindowsLookAndFeel
{

	private final ColorUIResource primary1 = new ColorUIResource(255, 255, 0);
	private final ColorUIResource primary2 = new ColorUIResource(0, 255, 255);
	private final ColorUIResource primary3 = new ColorUIResource(255, 0, 255);
	
	private UIDefaults defaults = null;
	
	public TrainingLookAndFeel() {
		super();
//		Object newSettings [] = {
//			"TabbedPane.contentOpaque", false,
//			"TabbedPane.tabAreaBackground", new Color(255, 100, 240),
//			"TabbedPane.background", new Color(255, 100, 240)
//		};
		//setCurrentTheme(new TrainingTheme());

		/*defaults = UIManager.getDefaults();
		defaults.putDefaults(newSettings);
		defaults.put("TabbedPane.contentOpaque", false);
		defaults.put("TabbedPane.tabAreaBackground", new Color(255, 100, 240));
		defaults.put("TabbedPane.background", new Color(255, 100, 240));
		
		
		UIDefaults ui = super.getDefaults();
		Enumeration<Object> en = ui.keys();
		for(int i=0; i < ui.size(); i++)
		{
			Object key = en.nextElement();
			log.debug("key = " + key + " value = " + ui.get(key));
		}*/
		UIManager.getDefaults().putDefaults(setMyDefaultColors());

	}
	
	private Object[] setMyDefaultColors() {
		Color c = new ColorUIResource(new Color(250,220,250));
		Color txt = new ColorUIResource(new Color(0,0,0));
			return new Object[] {
		  "Panel.background", c,
	      "TabbedPane.background", c,
	      //"Table.background", c,
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
	      "Menu.foreground", txt,
	      "Button.background", c,
	      "Menu.background", c
		};
	}

	
	@Override
	public String getDescription() {
		return "Centric Training LAF";
	}

	@Override
	public String getID() {
		return "CentricTrainingLAF";
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	@Override
	public String getName() {
		return "Centric Training Look And Feel";
	}

	@Override
	public UIDefaults getDefaults() {
		return super.getDefaults();
	}

	class TrainingTheme extends DefaultMetalTheme
	{
		
		@Override
		public FontUIResource getControlTextFont() {
			// TODO Auto-generated method stub
			return super.getControlTextFont();
		}
	
		@Override
		public FontUIResource getMenuTextFont() {
			return new FontUIResource(new Font("Arial", Font.BOLD, 14));
		}
	
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return super.getName();
		}
	
		@Override
		protected ColorUIResource getPrimary1() {
			return super.getPrimary1();
		}
	
		@Override
		protected ColorUIResource getPrimary2() {
			return super.getPrimary2();
		}
	
		@Override
		protected ColorUIResource getPrimary3() {
			return super.getPrimary3();
		}
	
		@Override
		protected ColorUIResource getSecondary1() {
			// TODO Auto-generated method stub
			return super.getSecondary1();
		}
	
		@Override
		protected ColorUIResource getSecondary2() {
			// TODO Auto-generated method stub
			return super.getSecondary2();
		}
	
		@Override
		protected ColorUIResource getSecondary3() {
			// TODO Auto-generated method stub
			return super.getSecondary3();
		}
	
		@Override
		public FontUIResource getSubTextFont() {
			// TODO Auto-generated method stub
			return super.getSubTextFont();
		}
	
		@Override
		public FontUIResource getSystemTextFont() {
			// TODO Auto-generated method stub
			return super.getSystemTextFont();
		}
	
		@Override
		public FontUIResource getUserTextFont() {
			// TODO Auto-generated method stub
			return super.getUserTextFont();
		}
	
		@Override
		public FontUIResource getWindowTitleFont() {
			// TODO Auto-generated method stub
			return super.getWindowTitleFont();
		}

		@Override
		public ColorUIResource getWindowBackground() {
			return new ColorUIResource(new Color(220, 200, 180));
		}

		@Override
		public ColorUIResource getWindowTitleBackground() {
			return new ColorUIResource(new Color(220, 200, 180));
		}

		@Override
		public ColorUIResource getWindowTitleInactiveBackground() {
			return new ColorUIResource(new Color(0, 0, 0));
		}

		@Override
		public ColorUIResource getDesktopColor() {
			return new ColorUIResource(new Color(220, 200, 180));
		}

		@Override
		public ColorUIResource getWindowTitleForeground() {
			return new ColorUIResource(new Color(220, 200, 180));
		}

		@Override
		public ColorUIResource getControl() {
			return new ColorUIResource(new Color(255,200,255));
		}

		@Override
		public void addCustomEntriesToTable(UIDefaults arg0) {
			// TODO Auto-generated method stub
			super.addCustomEntriesToTable(arg0);
		}

		@Override
		public ColorUIResource getAcceleratorForeground() {
			// TODO Auto-generated method stub
			return super.getAcceleratorForeground();
		}

		@Override
		public ColorUIResource getAcceleratorSelectedForeground() {
			// TODO Auto-generated method stub
			return super.getAcceleratorSelectedForeground();
		}

		@Override
		protected ColorUIResource getBlack() {
			return new ColorUIResource(new Color(50,50,50));
		}

		@Override
		public ColorUIResource getControlDarkShadow() {
			return new ColorUIResource(new Color(50,10,50));
		}

		@Override
		public ColorUIResource getControlDisabled() {
			return new ColorUIResource(new Color(100,30,100));
		}

		@Override
		public ColorUIResource getControlHighlight() {
			return new ColorUIResource(new Color(255,240,255));
		}

		@Override
		public ColorUIResource getControlInfo() {
			return new ColorUIResource(new Color(50,10,50));
		}

		@Override
		public ColorUIResource getControlShadow() {
			return new ColorUIResource(new Color(100,30,100));
		}

		@Override
		public ColorUIResource getControlTextColor() {
			return getBlack();
		}

		@Override
		public ColorUIResource getFocusColor() {
			return new ColorUIResource(new Color(200,200,0));
		}

		@Override
		public ColorUIResource getHighlightedTextColor() {
			// TODO Auto-generated method stub
			return super.getHighlightedTextColor();
		}

		@Override
		public ColorUIResource getInactiveControlTextColor() {
			// TODO Auto-generated method stub
			return super.getInactiveControlTextColor();
		}

		@Override
		public ColorUIResource getInactiveSystemTextColor() {
			// TODO Auto-generated method stub
			return super.getInactiveSystemTextColor();
		}

		@Override
		public ColorUIResource getMenuBackground() {
			return new ColorUIResource(new Color(255,200,255));
		}

		@Override
		public ColorUIResource getMenuDisabledForeground() {
			return new ColorUIResource(new Color(50,50,50));
		}

		@Override
		public ColorUIResource getMenuForeground() {
			return getBlack();
		}

		@Override
		public ColorUIResource getMenuSelectedBackground() {
			return new ColorUIResource(new Color(200,200,0));
		}

		@Override
		public ColorUIResource getMenuSelectedForeground() {
			return getBlack();
		}

		@Override
		public ColorUIResource getPrimaryControl() {
			// TODO Auto-generated method stub
			return super.getPrimaryControl();
		}

		@Override
		public ColorUIResource getPrimaryControlDarkShadow() {
			// TODO Auto-generated method stub
			return super.getPrimaryControlDarkShadow();
		}

		@Override
		public ColorUIResource getPrimaryControlHighlight() {
			// TODO Auto-generated method stub
			return super.getPrimaryControlHighlight();
		}

		@Override
		public ColorUIResource getPrimaryControlInfo() {
			// TODO Auto-generated method stub
			return super.getPrimaryControlInfo();
		}

		@Override
		public ColorUIResource getPrimaryControlShadow() {
			// TODO Auto-generated method stub
			return super.getPrimaryControlShadow();
		}

		@Override
		public ColorUIResource getSeparatorBackground() {
			// TODO Auto-generated method stub
			return super.getSeparatorBackground();
		}

		@Override
		public ColorUIResource getSeparatorForeground() {
			// TODO Auto-generated method stub
			return super.getSeparatorForeground();
		}

		@Override
		public ColorUIResource getSystemTextColor() {
			// TODO Auto-generated method stub
			return super.getSystemTextColor();
		}

		@Override
		public ColorUIResource getTextHighlightColor() {
			// TODO Auto-generated method stub
			return super.getTextHighlightColor();
		}

		@Override
		public ColorUIResource getUserTextColor() {
			// TODO Auto-generated method stub
			return super.getUserTextColor();
		}

		@Override
		protected ColorUIResource getWhite() {
			// TODO Auto-generated method stub
			return super.getWhite();
		}

		@Override
		public ColorUIResource getWindowTitleInactiveForeground() {
			// TODO Auto-generated method stub
			return super.getWindowTitleInactiveForeground();
		}
	};
	
}