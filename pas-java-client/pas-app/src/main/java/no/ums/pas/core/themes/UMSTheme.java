package no.ums.pas.core.themes;

import no.ums.pas.PAS;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.color.ColorScheme;
import org.jvnet.substance.painter.ControlBackgroundComposite;
import org.jvnet.substance.painter.DefaultControlBackgroundComposite;
import org.jvnet.substance.painter.SubstanceGradientPainter;
import org.jvnet.substance.skin.SubstanceSkin;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.watermark.SubstanceNoneWatermark;
import org.jvnet.substance.watermark.SubstanceWatermark;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class UMSWaterMarkHolder extends SubstanceNoneWatermark
{
	Color bg;
	SubstanceWatermark watermark;
	public UMSWaterMarkHolder(SubstanceWatermark wm, Color bg)
	{
		this.bg = bg;
		this.watermark = wm;
	}
	@Override
	public void drawWatermarkImage(Graphics graphics, Component c, int x,
			int y, int width, int height) {
		c.setBackground(bg);
		super.drawWatermarkImage(graphics, c, x, y, width, height);
	}

	@Override
	public boolean isDependingOnTheme() {
		return true;
	}
	
}

class UMSBgComp extends ControlBackgroundComposite
{

	@Override
	public Composite getBackgroundComposite(Component arg0, Container arg1,
			int arg2, boolean arg3) {
		ControlBackgroundComposite result =  new DefaultControlBackgroundComposite();
		return result.getBackgroundComposite(arg0, arg1, arg2, arg3);
	}
	
}

class UMSSkin implements SubstanceSkin
{

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubstanceTheme getTheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubstanceWatermark getWatermark() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean set() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

public class UMSTheme extends org.jvnet.substance.theme.SubstanceSteelBlueTheme
		implements ActionListener {
	public enum THEMETYPE {
		SIMPLE, COMPLEX,
	}

	protected THEMETYPE themetype;

	public UMSTheme(THEMETYPE type) {
		themetype = type;
		col_background = new Color(255, 255, 255, 100);
		col_watermark = new Color(255, 255, 255, 50);
	}

	public void setActiveColorScheme(ColorScheme s) {
		if (themetype == THEMETYPE.SIMPLE)
			col = new UMSColorScheme(s);
		else
			col = new UMSColorScheme(s);
	}

	public void setDisabledColorScheme(ColorScheme s) {
		if (themetype == THEMETYPE.SIMPLE)
			col_disabled = new UMSColorScheme(s);
		else
			col_disabled = new UMSColorScheme(s);
		;
	}

	@Override
	public SubstanceTheme getActiveTitlePaneTheme() {
		if (themetype == THEMETYPE.SIMPLE)
			return this;
		else
			return active_titlepane_theme;
	}

	@Override
	public SubstanceTheme getActiveTheme() {
		if (themetype == THEMETYPE.SIMPLE)
			return this;
		else
			return active_theme;
	}

	@Override
	public ColorScheme getDefaultColorScheme() {
		return col;
	}

	@Override
	public SubstanceTheme getDefaultTheme() {
		if (themetype == THEMETYPE.SIMPLE)
			return this;
		else
			return default_theme;
	}

	@Override
	public SubstanceTheme getDefaultTitlePaneTheme() {
		/*
		 * if(themetype==THEMETYPE.SIMPLE) return this; else return title_theme;
		 */
		return super.getDefaultTitlePaneTheme();
	}

	@Override
	public SubstanceTheme getDisabledTheme() {
		if (themetype == THEMETYPE.SIMPLE)
			return this;
		else
			return disabled_theme;
	}

	@Override
	public SubstanceTheme getHighlightBackgroundTheme() {
		/*
		 * if(themetype==THEMETYPE.SIMPLE) return this; else return
		 * background_theme;
		 */
		return super.getHighlightBackgroundTheme();
		// return this;
	}

	@Override
	public SubstanceTheme getSecondTheme() {
		/*
		 * if(themetype==THEMETYPE.SIMPLE) return this; else return
		 * second_theme;
		 */
		return super.getSecondTheme();
	}

	@Override
	public SubstanceTheme getWatermarkTheme() {
		/*
		 * if(themetype==THEMETYPE.SIMPLE) return this; else return
		 * watermark_theme;
		 */
		// return super.getWatermarkTheme();
		// return new UMSTheme(THEMETYPE.SIMPLE);
		return this;
	}

	/*
	 * public void setTitlePaneTheme(SubstanceTheme t) { title_theme = t; }
	 * 
	 * @Override public SubstanceTheme getFirstTheme() {
	 * if(themetype==THEMETYPE.SIMPLE) return this; else return first_theme; }
	 * public void setHighlightBackgroundTheme(SubstanceTheme t) {
	 * background_theme = t; }
	 */
	public void setDisabledTheme(SubstanceTheme t) {
		disabled_theme = t;
	}

	public void setActiveTitlePaneTheme(SubstanceTheme t) {
		active_titlepane_theme = t;
	}

	/*
	 * public void setSecondTheme(SubstanceTheme t) { second_theme = t; } public
	 * void setFirstTheme(SubstanceTheme t) { first_theme = t; } public void
	 * setWatermarkTheme(SubstanceTheme t) { watermark_theme = t; }
	 */
	public void setDefaultTheme(SubstanceTheme t) {
		default_theme = t;
	}

	public void setActiveTheme(SubstanceTheme t) {
		active_theme = t;
	}

	public void setNonActivePainter(SubstanceGradientPainter g) {
		non_active_painter = g;
		/*
		 * non_active_painter = new SubstanceGradientPainter() {
		 * 
		 * @Override public String getDisplayName() { return "test"; }
		 * 
		 * @Override public BufferedImage getContourBackground(int width, int
		 * height, Shape contour, boolean isDark, ColorScheme colorScheme1,
		 * ColorScheme colorScheme2, float cyclePos, boolean hasShine, boolean
		 * useCyclePosAsInterpolation) { BufferedImage image =
		 * SubstanceCoreUtilities.getBlankImage(width, height); return image; }
		 * };
		 */
	}

	@Override
	public SubstanceGradientPainter getNonActivePainter() {
		/*return new SubstanceGradientPainter() {
			
			@Override
			public String getDisplayName() {
				return "UMS Gradient Painter";
			}
			
			@Override
			public BufferedImage getContourBackground(int arg0, int arg1, Shape arg2,
					boolean arg3, ColorScheme arg4, ColorScheme arg5, float arg6,
					boolean arg7, boolean arg8) {
				return null;
			}
		};*/
		if (non_active_painter != null)
			return non_active_painter;
		return super.getNonActivePainter();
	}

	UMSColorScheme col = new UMSColorScheme();
	UMSColorScheme col_disabled = new UMSColorScheme();
	// SubstanceTheme title_theme = new SubstanceTheme(col, "UMS Title Theme",
	// ThemeKind.BRIGHT);
	SubstanceTheme active_theme = new SubstanceTheme(col, "UMS Active Theme",
			ThemeKind.BRIGHT);
	// SubstanceTheme background_theme = new SubstanceTheme(col,
	// "UMS Background Theme", ThemeKind.BRIGHT);
	SubstanceTheme disabled_theme = new SubstanceTheme(col,
			"UMS Disabled Theme", ThemeKind.BRIGHT);
	SubstanceTheme active_titlepane_theme = new SubstanceTheme(col,
			"UMS Titlepane Theme", ThemeKind.BRIGHT);
	// SubstanceTheme second_theme = new SubstanceTheme(col, "UMS Second Theme",
	// ThemeKind.BRIGHT);
	// SubstanceTheme first_theme = new SubstanceTheme(col, "UMS First Theme",
	// ThemeKind.BRIGHT);
	// SubstanceTheme watermark_theme = new SubstanceTheme(col,
	// "UMS Watermark Theme", ThemeKind.BRIGHT);
	SubstanceTheme default_theme = new SubstanceTheme(col, "UMS Default Theme",
			ThemeKind.BRIGHT);

	SubstanceGradientPainter non_active_painter = null;
	Color col_background;
	Color col_watermark;

	@Override
	public void actionPerformed(ActionEvent e) {
		Color selcol = (Color) e.getSource();
		if (ThemeColorComponent.COL_FOREGROUND.name().equals(
				e.getActionCommand()))
			col.col_foreground = selcol;
		else if (ThemeColorComponent.COL_BACKGROUND.name().equals(
				e.getActionCommand())) {
			col_background = selcol;
			SubstanceWatermark wm = SubstanceLookAndFeel.getCurrentWatermark();
			SubstanceLookAndFeel.setCurrentWatermark(new UMSWaterMarkHolder(wm, col_background));
			SubstanceLookAndFeel.getCurrentWatermark().updateWatermarkImage();
			//PAS.get_pas().setBackground(col_watermarkstamp);
			/*SubstanceLookAndFeel.setCurrentWatermark(new SubstanceWatermark() {
				
				@Override
				public boolean updateWatermarkImage() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void previewWatermark(Graphics arg0, int arg1, int arg2, int arg3,
						int arg4) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isDependingOnTheme() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public String getDisplayName() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void drawWatermarkImage(Graphics arg0, Component arg1, int arg2,
						int arg3, int arg4, int arg5) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void dispose() {
					// TODO Auto-generated method stub
					
				}
				
			});*/
			//SubstanceLookAndFeel.getCurrentWatermark().updateWatermarkImage();
			//Color c = SubstanceColorUtilities.getBackgroundColor(this);
			/*PAS.get_pas().setBackground(col_watermarkstamp);
			PAS.get_pas().get_eastcontent().setBackground(col_watermarkstamp);
			PAS.get_pas().get_eastcontent().get_infopanel().setBackground(col_watermarkstamp);
			PAS.get_pas().get_eastcontent().get_infopanel().m_coorsearch.setBackground(col_watermarkstamp);*/
			//UIManager.put("Panel.background", col_watermarkstamp);
			//SwingUtilities.updateComponentTreeUI(PAS.get_pas());
			
		}
		else if (ThemeColorComponent.COL_WATERMARK.name().equals(e.getActionCommand()))
		{
			col_watermark = selcol;
			//SubstanceLookAndFeel.setCurrentWatermark(new UMSWaterMarkNone(col_background));
			SubstanceLookAndFeel.getCurrentWatermark().updateWatermarkImage();
		}
		else if (ThemeColorComponent.COL_DARK.name().equals(
				e.getActionCommand()))
			col.col_dark = selcol;
		else if (ThemeColorComponent.COL_EXTRA_LIGHT.name().equals(
				e.getActionCommand()))
			col.col_extra_light = selcol;
		else if (ThemeColorComponent.COL_LIGHT.name().equals(
				e.getActionCommand()))
			col.col_light = selcol;
		else if (ThemeColorComponent.COL_MID.name()
				.equals(e.getActionCommand()))
			col.col_mid = selcol;
		else if (ThemeColorComponent.COL_ULTRA_DARK.name().equals(
				e.getActionCommand()))
			col.col_ultra_dark = selcol;
		else if (ThemeColorComponent.COL_ULTRA_LIGHT.name().equals(
				e.getActionCommand()))
			col.col_ultra_light = selcol;

		//PAS.get_pas().repaint();
		SwingUtilities.updateComponentTreeUI(PAS.get_pas());
		PAS.get_pas().repaint();
	}

	double d_saturatefactor = 1.0;
	double d_selected_tab_fade_start = 0.0;
	double d_selected_tab_fade_end = 5.0;

	public void editColor(ThemeColorComponent c) {
		Color toedit = null;
		switch (c) {
		case COL_FOREGROUND:
			toedit = col.col_foreground;
			break;
		case COL_BACKGROUND:
			toedit = new Color(col_background.getRed(), col_background.getGreen(), col_background.getBlue(), 50);
			break;
		case COL_WATERMARK:
			toedit = new Color(col_watermark.getRed(), col_watermark.getGreen(), col_watermark.getBlue(), 50);
			break;
		case COL_DARK:
			toedit = col.col_dark;
			break;
		case COL_EXTRA_LIGHT:
			toedit = col.col_extra_light;
			break;
		case COL_LIGHT:
			toedit = col.col_light;
			break;
		case COL_MID:
			toedit = col.col_mid;
			break;
		case COL_ULTRA_DARK:
			toedit = col.col_ultra_dark;
			break;
		case COL_ULTRA_LIGHT:
			toedit = col.col_ultra_light;
			break;
		}
		if (toedit != null) {
			ThemeColorSelect s = new ThemeColorSelect(toedit, c, this);
			s.show();
		}
	}

	class UMSColorSchemeDisabled implements ColorScheme {

		@Override
		public Color getDarkColor() {
			return new Color(100, 100, 100);
		}

		@Override
		public Color getExtraLightColor() {
			return new Color(140, 140, 140);
		}

		@Override
		public Color getForegroundColor() {
			return new Color(50, 50, 50);
		}

		@Override
		public Color getLightColor() {
			return new Color(130, 130, 130);
		}

		@Override
		public Color getMidColor() {
			return new Color(120, 120, 120);
		}

		@Override
		public Color getUltraDarkColor() {
			return new Color(50, 50, 50);
		}

		@Override
		public Color getUltraLightColor() {
			return new Color(255, 255, 255);
		}

	}

	class UMSColorScheme implements ColorScheme {
		Color col_foreground = new Color(0, 0, 30);
		Color col_dark = new Color(140, 140, 150);
		Color col_extra_light = new Color(140, 140, 240);
		Color col_light = new Color(140, 140, 230);
		Color col_mid = new Color(20, 20, 220);
		Color col_ultra_dark = new Color(10, 10, 150);
		Color col_ultra_light = new Color(140, 140, 255);

		public UMSColorScheme() {

		}

		public UMSColorScheme(ColorScheme s) {
			col_dark = s.getDarkColor();
			col_extra_light = s.getExtraLightColor();
			col_foreground = s.getForegroundColor();
			col_light = s.getLightColor();
			col_mid = s.getMidColor();
			col_ultra_dark = s.getUltraDarkColor();
			col_ultra_light = s.getUltraLightColor();
		}

		@Override
		public Color getDarkColor() {
			return col_dark;
		}

		@Override
		public Color getExtraLightColor() {
			return col_extra_light;
		}

		@Override
		public Color getForegroundColor() {
			return col_foreground;
		}

		@Override
		public Color getLightColor() {
			return col_light;
		}

		@Override
		public Color getMidColor() {
			return col_mid;
		}

		@Override
		public Color getUltraDarkColor() {
			return col_ultra_dark;
		}

		@Override
		public Color getUltraLightColor() {
			return col_ultra_light;
		}

	}

	public void setWatermarkStampColor(Color c) {
		col_watermark = c;
	}

	@Override
	public Color getWatermarkStampColor() {
		return col_watermark;
	}
	

	@Override
	public ColorScheme getColorScheme() {
		return col;
		// return super.getColorScheme();
	}

	@Override
	public String getThemeName() {
		return "UMS Standard Theme";
	}

	@Override
	public ColorScheme getDisabledColorScheme() {
		return col_disabled;
	}

	@Override
	public double getSelectedTabFadeEnd() {
		// return d_selected_tab_fade_end;
		return super.getSelectedTabFadeEnd();
	}

	@Override
	public double getSelectedTabFadeStart() {
		// return d_selected_tab_fade_start;
		return super.getSelectedTabFadeStart();
	}

	@Override
	public SubstanceTheme hueShift(double hueShiftFactor) {
		return super.hueShift(hueShiftFactor);
	}

	@Override
	public SubstanceTheme saturate(double saturateFactor,
			boolean toSaturateEverything) {
		return super.saturate(saturateFactor, toSaturateEverything);
	}

	@Override
	public SubstanceTheme saturate(double saturateFactor) {
		return super.saturate(saturateFactor);
	}

	@Override
	public SubstanceTheme shade(double shadeFactor) {
		return super.shade(shadeFactor);
	}

	@Override
	public SubstanceTheme tint(double tintFactor) {
		return super.tint(tintFactor);
	}

	@Override
	public SubstanceTheme tone(double toneFactor) {
		return super.tone(toneFactor);
	}

}