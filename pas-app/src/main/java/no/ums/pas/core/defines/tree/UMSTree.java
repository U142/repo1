package no.ums.pas.core.defines.tree;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



public abstract class UMSTree extends JTree implements ActionListener, ItemListener, ComponentListener, MouseListener, TreeSelectionListener
{
	public enum TREEMODE
	{
		EDITOR,
		SELECTION_WITH_EDIT,
		SELECTION_ONLY,
	}
	
	public enum TREEICONSIZE
	{
		SMALL,
		MEDIUM,
		LARGE,
		XLARGE,
		XXLARGE,
	}
	
	protected TREEMODE treemode;
	protected TREEICONSIZE iconsize = TREEICONSIZE.MEDIUM;
	
	public void setIconSize(TREEICONSIZE s)
	{
		iconsize = s;
	}
	
	public int getIconSize()
	{
		switch(iconsize)
		{
		case SMALL:
			return 16;
		case MEDIUM:
			return 24;
		case LARGE:
			return 32;
		case XLARGE:
			return 48;
		case XXLARGE:
			return 64;
		}
		return 24;
	}

	public static final String TREE_SELECTION_CHANGED = "act_treeselection_changed";
	public static final String TREE_NODEVALUE_CHANGED = "act_treenodevalue_changed";
	public static final String TREE_TREENODE_DELETED = "act_treenode_deleted";
	public static final String TREE_SELECTION_NULL = "act_treeselection_null";
	
	@Override
	public abstract void valueChanged(TreeSelectionEvent e);

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	public List<Object> getExplodedNodes()
	{
		List<Object> ret = new ArrayList<Object>();
		Enumeration<TreePath> paths = getExpandedDescendants(getPath(top));
		if(paths==null)
			return ret;
		while(paths.hasMoreElements())
		{
			TreePath p = paths.nextElement();			
			UMSTreeNode n = (UMSTreeNode)p.getLastPathComponent();
			Long l = (Long)n.getHashPk();
			if(l!=null)
				ret.add(l);
		}
		return ret;
	}

	
	public List<UMSTreeNode> searchTreeNode(String nodeStr)
	{
		//System.out.println("searchTreeNode");
		List<UMSTreeNode> nodes = new ArrayList<UMSTreeNode>();
		Enumeration<UMSTreeNode> en = top.breadthFirstEnumeration();
		while(en.hasMoreElements())
		{
			UMSTreeNode n = (UMSTreeNode)en.nextElement();
			if(n.Search(nodeStr))
			{
				nodes.add(n);
				//if(n.setFoundInSearch(true))
				n.setFoundInSearch(true);
				try
				{
					model.nodeChanged(n);
				}
				catch(Exception e)
				{
					
				}
			}
			else
			{
				//if(n.setFoundInSearch(false))
				n.setFoundInSearch(false);
				try
				{
					model.nodeChanged(n);
				}
				catch(Exception e)
				{
					
				}
			}
		}
		return nodes;
	}
	
	public void searchTreeNode()
	{
		searchTreeNode(getTreeRenderer().getSearchString());
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
    	TreePath path = getPathForLocation(e.getX(), e.getY());
    	if(path==null)
    	{
    		setSelectedNode(null);
    		return;
    	}
    	setSelectionPath(path);
    	Object o = path.getLastPathComponent();
    	if(o!=null)
    	{
    		selected_node = (UMSTreeNode)o;
    		/*if(o.getClass().equals(CountryListItem.class))
    			m_mi_request_touristcount.setEnabled(true);
    		else
    			m_mi_request_touristcount.setEnabled(false);*/
    		//CommonTASListItem c = (CommonTASListItem)o;
    		//m_mi_request_touristcount.setEnabled(c.canRunCountRequest());
    	}		
    	else
    	{
    		selected_node = null;
    	}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		switch(treemode)
		{
		case SELECTION_ONLY:
			return;
		}
        if ( e.isPopupTrigger()) {
            popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
        }
            		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
	}

	public DefaultTreeModel model;
	public UMSTreeNode top;
	public TreeRenderer getTreeRenderer() { 
		return cr;
	}
	protected TreeRenderer cr;

	public JPopupMenu popup;
	protected TreeUpdater updater;
	protected ActionListener callback;
	protected UMSTreeNode selected_node;
	
	public abstract UMSTreeNode getSelectedNode(); // { return selected_node; }
	public abstract void InitRenderer();
	
	
	public void openFolder(UMSTreeNode node)
	{
		try
		{
			
			expandPath(getPath(node));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public TreePath getPath(UMSTreeNode node)
	{
		try
		{
			return new TreePath(node.getPath());
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			return null;
		}
	}
	public void setSelectedNode(UMSTreeNode node)
	{
		selected_node = node;
		if(node!=null)
		{
			TreePath p = getPath(node);
			this.setSelectionPath(p);
			this.scrollPathToVisible(p);
		}
		else
			this.setSelectionPath(null);
	}
	public void editNode(UMSTreeNode node)
	{
		try
		{
			TreePath path = getPath(node); 
			if(path!=null)
				this.startEditingAtPath(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void startUpdater()
	{
		updater.startDownloadThread((updater.updateinterval<=0 ? true : false));
	}
	
	public void startUpdater(boolean only_once)
	{
		updater.startDownloadThread((updater.updateinterval<=0 || only_once ? true : false));
	}
	
	public void stopUpdater()
	{
		updater.uninit();
	}
	
	public void signalDownloadFinished()
	{
		updater.notifyDownloadDone();
	}
	
	public UMSTree(ActionListener callback, int update_interval)
	{
		this(callback, update_interval, TREEMODE.EDITOR);
	}
	
	public UMSTree(ActionListener callback, int update_interval, TREEMODE treemode)
	{
		//super(UMSTreeNode.newTopNode(null));
		super();
		top = UMSTreeNode.newTopNode(null);
		
		this.treemode = treemode;
		InitRenderer();
		addTreeSelectionListener(this);
		addMouseListener(this);
		this.callback = callback;
		popup = new JPopupMenu();
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
		//top = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(top);
		this.setModel(model);
		this.setShowsRootHandles(false);
		this.setRootVisible(false);
		//super.setModel(model);
		updater = new TreeUpdater(callback, update_interval);
		
	}
	
	
}