package no.ums.pas.parm.tree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import javax.swing.JTree;
import javax.swing.ImageIcon;



import javax.swing.*;
import javax.swing.border.*;


//Substance 3.3
import no.ums.pas.parm.voobjects.*;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.ws.pas.tas.ULBACONTINENT;
import no.ums.ws.pas.tas.ULBACOUNTRY;

import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;
import org.jvnet.substance.SubstanceImageCreator;


//Substance 5.2
//import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public class CustomRenderer extends SubstanceDefaultTreeCellRenderer { //DefaultTreeCellRenderer {
	public static final long serialVersionUID = 1;
	
	private ImageIcon icon = null;
	private Border border = null;
	private JTree tree;
	
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
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	
		
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object o = node.getUserObject();
		
		if(o==null)
		{
			setFont(UIManager.getFont("Tree.font"));
			return this;
		}
		
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
		setFont(UIManager.getFont("Tree.font"));		

        return this;
	}
	
	private void setHighlight(ParmVO o) {
		if(o.getDragOver()) {
			//setOpaque(true);
			//setBackground(new Color(50, 50, 150, 20));
			this.setBorder(border);
		}
		else {
			//setOpaque(false);
			//setBackground(null);
			setBorder(null);
		}
	}

	private void initAlertVO(Object o)
	{
		setBackground(new Color(0, 0, 0, 0));
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
				setIcon(icon);
				return;
			}
			else if(areaid.equals("-2"))
			{
				path = "seriouswarning_32" + "." + "png";
				icon = ImageLoader.load_icon(path);
		        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
		        icon.setImage(test);
				setIcon(icon);
				return;				
			}
		}
		if(aVO.getLocked()>=1)
		{
			path = "lock" + "." + "gif";
			icon = ImageLoader.load_icon(path);
	        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
	        icon.setImage(test);
			setIcon(icon);
			return;
		}
		//SubstanceImageCreator ic = new SubstanceImageCreator();
		//Icon ic = new ImageIcon(SubstanceImageCreator.getTreeIcon(tree, SubstanceLookAndFeel.getTheme(), false));
		Icon ic =  this.getDisabledIcon();
		setIcon(ic);
		//render default icon
		/*path = "lock" + "." + "gif";
		icon = ImageLoader.load_icon(path);
        Image test = icon.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH);
        icon.setImage(test);
		setIcon(icon);*/
	}
	
	private void initObjectVO(Object o){
		setBackground(new Color(0, 0, 0, 0));
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
				setIcon(icon);
		} 
		else {
		       //System.err.println("Couldn't find file: " + path);
		}
		
	}
	
	public void initEventVO(Object o){
		setBackground(new Color(0, 0, 0, 0));
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
			setIcon(icon);
			}
		else {
	       //System.err.println("Couldn't find file: " + path);
		}

	}
	

}
