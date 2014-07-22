package no.ums.pas.area.tree;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.parm.voobjects.ParmVO;

/**
 * @author sachinn
 */
public class AreaCustomRenderer extends DefaultTreeCellRenderer { //SubstanceDefaultTreeCellRenderer { //DefaultTreeCellRenderer {
	public static final long serialVersionUID = 1;
	
	private ImageIcon icon = null;
	private Border border = null;
	private JTree tree;
	private JLabel renderer = new JLabel();
	
	public AreaCustomRenderer(JTree tree){
		//border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		//border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		this.tree = tree;
		border = BorderFactory.createEtchedBorder();
		this.setBorder(border);
		//border = BorderFactory.createCompoundBorder();
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            											boolean leaf, int row, boolean hasFocus){
		renderer = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	
		//renderer.setOpaque(true);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object o = node.getUserObject();
		
		if(o==null)
		{
			setFont(UIManager.getFont("Tree.font"));
			return this;
		}
		renderer.setText(" " + o.toString());

		if (o.getClass().equals(AreaVO.class) ){
			initAreaVO(o);
			setHighlight((ParmVO)o);
		}
		
		renderer.setPreferredSize(new Dimension(500, tree.getRowHeight()));
		renderer.setSize(new Dimension(500, tree.getRowHeight()));
		renderer.setBounds(0, 0, 500, tree.getRowHeight());
		renderer.setIconTextGap(5);
		//renderer.setSize(tree.getPreferredSize().width, tree.getRowHeight());
		int w = tree.getPreferredSize().width;
		
        return renderer;
	}
	
	private void setHighlight(ParmVO o) {
		renderer.setBorder(null);
	}

	
	private void initAreaVO(Object o){
		renderer.setIcon(null);
	}
	
}
