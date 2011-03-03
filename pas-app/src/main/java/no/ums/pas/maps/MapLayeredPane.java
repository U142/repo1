package no.ums.pas.maps;

import javax.swing.JLayeredPane;
import java.awt.BorderLayout;

public class MapLayeredPane extends JLayeredPane
{
	public MapLayeredPane(MapFrame basepane)
	{
		super();
		setLayout(new BorderLayout());
		add(basepane, BorderLayout.CENTER);
	}
}