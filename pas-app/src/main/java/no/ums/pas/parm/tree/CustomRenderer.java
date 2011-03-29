package no.ums.pas.parm.tree;

import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.CategoryVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.ums.tools.ImageLoader;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;

//Substance 3.3


//Substance 5.2
//import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public class CustomRenderer extends DefaultTreeCellRenderer { //SubstanceDefaultTreeCellRenderer { //DefaultTreeCellRenderer {
	public static final long serialVersionUID = 1;
	
	private ImageIcon icon = null;
	private Border border = null;
	private JTree tree;
	private JLabel renderer = new JLabel();
	
	public CustomRenderer(JTree tree){
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

		if (o.getClass().equals(ObjectVO.class)){
			initObjectVO(o);
			}
		else if (o.getClass().equals(EventVO.class)){
			initEventVO(o);
		}
		else if (o.getClass().equals(AlertVO.class) ){
			initAlertVO(o);
		}
		if(o.getClass().getSuperclass().equals(ParmVO.class)) {
			setHighlight((ParmVO)o);
		}
		renderer.setPreferredSize(new Dimension(500, tree.getRowHeight()));
		renderer.setSize(new Dimension(500, tree.getRowHeight()));
		renderer.setBounds(0, 0, 500, tree.getRowHeight());
		renderer.setIconTextGap(5);
		//renderer.setSize(tree.getPreferredSize().width, tree.getRowHeight());
		int w = tree.getPreferredSize().width;
		//renderer.setSize(new Dimension(w, tree.getRowHeight()));
		/*if(sel)
		{
			renderer.setBackground(this.getBackgroundSelectionColor());
			renderer.setForeground(this.getForeground());
			renderer.setBorder(BorderFactory.createLineBorder(this.getBorderSelectionColor(), 1));
		}
		else
		{
			renderer.setBackground(this.getBackground());
			renderer.setForeground(this.getTextNonSelectionColor());
			renderer.setBorder(null);
		}
		if(hasFocus)
		{
			renderer.setBorder(BorderFactory.createLineBorder(this.getBorderSelectionColor(), 1));
		}*/
		//renderer.setFont(UIManager.getFont("Tree.font"));	
        return renderer;
	}
	
	private void setHighlight(ParmVO o) {
		if(o.getDragOver()) {
			//setOpaque(true);
			//setBackground(new Color(50, 50, 150, 20));
			renderer.setBorder(border);
		}
		else {
			//setOpaque(false);
			//setBackground(null);
			renderer.setBorder(null);
		}
	}

	private void initAlertVO(Object o)
	{
		//setBackground(new Color(0, 0, 0, 0));
		AlertVO aVO = (AlertVO) o;
		String path = "default non-working path, object";
		if(aVO.hasValidPk())
		{
			//FontSet setFont(new Font("Arial", Font.PLAIN, 11));
		}
		//FontSet else
		//FontSet setFont(new Font("Arial", Font.ITALIC, 11));
		if(!aVO.hasValidAreaIDFromCellVision())
		{
			String areaid = aVO.getLBAAreaID();
			//show this by changing icon
			if(areaid==null || areaid.equals("0"))
			{
				path = "gearwheel_32" + "." + "png";
				icon = ImageLoader.load_icon(path);
		        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
		        icon.setImage(test);
		        renderer.setIcon(icon);
				return;
			}
			else if(areaid.equals("-2"))
			{
				path = "seriouswarning_32" + "." + "png";
				icon = ImageLoader.load_icon(path);
		        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
		        icon.setImage(test);
		        renderer.setIcon(icon);
				return;				
			}
		}
		if(aVO.getLocked()>=1)
		{
			path = "lock_16" + "." + "png";
			icon = ImageLoader.load_icon(path);
	        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
	        icon.setImage(test);
	        renderer.setIcon(icon);
			return;
		}
		//SubstanceImageCreator ic = new SubstanceImageCreator();
		//Icon ic = new ImageIcon(SubstanceImageCreator.getTreeIcon(tree, SubstanceLookAndFeel.getTheme(), false));
		Icon ic =  this.getLeafIcon();
		renderer.setIcon(ic);
		//render default icon
		/*path = "lock" + "." + "gif";
		icon = ImageLoader.load_icon(path);
        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
        icon.setImage(test);
		setIcon(icon);*/
	}
	
	private void initObjectVO(Object o){
		//setBackground(new Color(0, 0, 0, 0));
		ObjectVO oVO = (ObjectVO) o;
		CategoryVO cVO = oVO.getCategoryVO(); // will work when XmlReader is complete.

		String path = "default non-working path, object";
		if(oVO.hasValidPk())
		{
			//FontSet setFont(new Font("Arial", Font.PLAIN, 11));
		}
		//FontSet else
		//FontSet setFont(new Font("Arial", Font.ITALIC, 11));
		
		if (cVO != null){
			path = "cat" +cVO.getCategoryPK().substring(1) + "." + cVO.getFileext();
			//tree.setRowHeight(32);
			if(cVO.getFileext() == null)
				path = "default non-working path, object";
		}
		
		if (!path.equals("default non-working path, object")) {
//				icon = new ImageIcon();
				//System.out.println("Path: " + path);
				icon = ImageLoader.load_icon(path);
		        Image test = icon.getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH);
		        icon.setImage(test);
		        renderer.setIcon(icon);
		} 
		else {
		       //System.err.println("Couldn't find file: " + path);
			renderer.setIcon(null);
		}
		
	}
	
	public void initEventVO(Object o){
		//setBackground(new Color(0, 0, 0, 0));
		EventVO eVO = (EventVO) o;
		CategoryVO cVO = eVO.getCatVO();

		if(eVO.hasValidPk())
		{
			//FontSet setFont(new Font("Arial", Font.PLAIN, 11));
		}
		//FontSet else
		//FontSet setFont(new Font("Arial", Font.ITALIC, 11));
	
		String path = "default non-working path, event";

		if (cVO != null){
			path = "cat" +cVO.getCategoryPK().substring(1) + "." + cVO.getFileext();
			if(cVO.getFileext() == null)
				path = "default non-working path, event";
		}
				
		if (!path.equals("default non-working path, event")) {
			icon = ImageLoader.load_icon(path);
	        
	        Image test = icon.getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH);
	        icon.setImage(test);
	        renderer.setIcon(icon);
			}
		else {
			renderer.setIcon(null);
	       //System.err.println("Couldn't find file: " + path);
		}

	}
	

}
