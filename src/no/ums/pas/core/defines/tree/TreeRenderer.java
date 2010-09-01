package no.ums.pas.core.defines.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;


import no.ums.pas.PAS;
import no.ums.pas.ums.tools.UnderlineHighlightPainter;

import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;
import org.jvnet.substance.SubstanceLookAndFeel;

//import tas.TasPanel.TASTreeTable;


public abstract class TreeRenderer extends DefaultTreeCellRenderer {//SubstanceDefaultTreeCellRenderer {
	public static final long serialVersionUID = 1;
	

	public TreeRenderer(JTree tree, TreeTable table){
		//border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		//border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		this.tree = tree;
		lbl.setHighlighter(new DefaultHighlighter());
		lbl.setBackground(new Color(0,0,0,Color.TRANSLUCENT));
		lbl.setOpaque(false);
		lbl.setEditable(false);
		border = BorderFactory.createEtchedBorder();
		//this.setBorder(border);
		initTable(table);
		 
	}

	protected ImageIcon icon = null;
	protected Border border = null;
	protected JTree tree;
	protected int totalwidth = 10;
	protected int totalheight = 10;
	public void setTotalWidth(int n) { totalwidth = n; }
	public void setTotalHeight(int n) { totalheight = n; }
	
	protected String m_sz_searchstring = "";
	public String getSearchString() { return m_sz_searchstring; }
	public void setSearchString(String s) { m_sz_searchstring = s; } 
	public boolean isFilterActive()
	{
		return (m_sz_searchstring.length()>0 ? true : false);
	}
	//StdTextArea lbl = new StdTextArea("", false);
	protected JTextArea lbl = new JTextArea("");

	protected void initTable(TreeTable table)
	{
		m_tbl_list = table;

		
		//SubstanceDefaultTreeCellRenderer dtcr = (SubstanceDefaultTreeCellRenderer) this;
		DefaultTreeCellRenderer dtcr = this;
		//Substance 3.3
		Color c = SubstanceLookAndFeel.getActiveColorScheme()
				.getUltraLightColor();
		//Substance 5.2
		//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
		
		Color ctrans = new Color(c.getRed(), c.getGreen(), c.getBlue(),
				Color.TRANSLUCENT);
		dtcr.setBackground(ctrans); // new
									// Color(255,255,255,Color.TRANSLUCENT));
		dtcr.setOpaque(false);

		// Finally, set the tree's background color
		tree.setBackground(ctrans); // new
									// Color(255,255,255,Color.TRANSLUCENT));
									// //dtcr.getBackground());//new
									// Color(255,255,255,Color.TRANSLUCENT));
		tree.setOpaque(false);
		this.setBorder(null);
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, Color.TRANSLUCENT));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.setBorder(null);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(this);


	}
	
	/*@Override
	public Color getBackground()
	{
		return new Color(0,0,0,0);
		//return super.getBackground();
	}*/
	/*@Override 
	public Color getBackgroundNonSelectionColor()
	{
		return new Color(0,0,0,0);
	}*/
	
	@Override
	public abstract void setPreferredSize(Dimension d);
	
	public TreeTable m_tbl_list;
	protected UnderlineHighlightPainter painter = new UnderlineHighlightPainter(Color.yellow);

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		//return new JLabel("No renderer defined");
	}

}