package no.ums.pas.core.menus.defines;

import org.jvnet.substance.skin.SubstanceSkin;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.watermark.SubstanceWatermark;

import javax.swing.Icon;

//import org.jvnet.substance.api.SubstanceSkin;
//Substance 3.3


//Substance 5.2
//import org.jvnet.substance.api.SubstanceColorSchemeBundle;
//import org.jvnet.substance.api.SubstanceSkin;


public class SubstanceMenuItem extends CheckItem {
	public static final long serialVersionUID = 1;
	private Object m_theme;
	//Substance 3.3
	public SubstanceTheme get_theme() { return (SubstanceTheme)m_theme; }
	
	//Substance 5.2
	//public SubstanceColorSchemeBundle get_theme() { return (SubstanceColorSchemeBundle)m_theme; }

	public SubstanceSkin get_skin() { return (SubstanceSkin)m_theme; }
	public SubstanceWatermark get_watermark() { return (SubstanceWatermark)m_theme; }

	public SubstanceMenuItem(String sz_value, Object theme) {
		super(sz_value, theme, false);
		m_theme = theme;
	}

	public SubstanceMenuItem(String sz_value, Object theme, Icon ico) {
		super(sz_value, theme, false, ico);
		m_theme = theme;
	}
}