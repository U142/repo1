package no.ums.pas.maps;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.tree.DefaultTreeModel;

import org.geotools.data.ows.Layer;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.SettingsGUI;
import no.ums.pas.core.logon.SettingsGUI.WmsLayerTree;
import no.ums.pas.core.mainui.GeneralPanel;

public class WMSLayerSelectorPanel extends DefaultPanel implements MouseListener
{
	class LayerCheckBox extends JCheckBox 
	{
		LayerCheckBox(String s)
		{
			super(s);
			setOpaque(true);
			this.setBackground(new Color(255,255,255,255));
		}
		
	}
	
	boolean tree_added = false;
	WmsLayerTree layertree = new SettingsGUI().new WmsLayerTree(new DefaultTreeModel(null));
	
	public WMSLayerSelectorPanel()
	{
		super();
		setOpaque(true);
		this.setBackground(new Color(255,255,255,128));
		add_controls();
		addMouseListener(this);
	}
	
	public void populate(List<Layer> layers, ArrayList<String> check)
	{
		//layers.add(new Layer("tester"));
		//layers.add(new Layer("tester2"));
		layertree.populate(layers, check, false, this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	@Override
	public void add_controls() {
		/*set_gridconst(0,inc_panels(),1,1);
		add(new LayerCheckBox("Tester1"), get_gridconst());
		set_gridconst(0,inc_panels(),1,1);
		add(new LayerCheckBox("Tester2"), get_gridconst());
		set_gridconst(0,inc_panels(),1,1);
		add(new LayerCheckBox("Tester3"), get_gridconst());	*/
		if(tree_added)
			return;
		set_gridconst(0,inc_panels(),1,1);
		add(layertree, get_gridconst());
		tree_added = true;
	}
	@Override
	public void init() {
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}