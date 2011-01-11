package no.ums.pas.maps;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;

public class MapLayeredPane extends JLayeredPane
{
	public MapLayeredPane(MapFrame basepane)
	{
		super();
		setLayout(new BorderLayout());
		add(basepane, BorderLayout.CENTER);
	}
}