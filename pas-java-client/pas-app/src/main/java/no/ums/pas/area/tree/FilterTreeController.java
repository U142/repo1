package no.ums.pas.area.tree;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.constants.PredefinedAreaConstants;
import no.ums.pas.area.main.MainFilterController;
import no.ums.pas.area.server.FilterServerCon;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.localization.Localization;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.ums.errorhandling.Error;

import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * @author Abhinava
 */
public class FilterTreeController {


    private static final Log log = UmsLog.getLogger(FilterTreeController.class);

	private FilterTreeGUI gui;

//	public AreaTreeGUI get_treegui() {
//		return gui;
//	}

	private MouseListener museLytter;
	private TreePath selPath;
	private DefaultMutableTreeNode newRootFolder;
	public static ArrayList<Object> staticList = new ArrayList<Object>();
	private MainFilterController main;

//	private HashMap<, DefaultMutableTreeNode> areaNodeMap = new HashMap<FilterVO, DefaultMutableTreeNode>();
	private HashMap<String, DefaultMutableTreeNode> filterNodeMap = new HashMap<String, DefaultMutableTreeNode>();;

	public void SetInitializing(final boolean b) {
		try
		{
			SwingUtilities.invokeLater(new Runnable()  {
				public void run()
				{
					gui.lbl_initializing.setVisible(b);
					gui.loader.setVisible(b);
					//gui.tree.setEnabled(!b);
				}
			});
		} 
		catch(Exception e)
		{
			
		}
	}

	public FilterTreeController(MainFilterController main) {
		this.main = main;
	}

	public JPanel getTreeController() {
		gui = new FilterTreeGUI(main);
		javax.swing.JTree tree = gui.getTree();

	

		TreeCellRenderer cr = tree.getCellRenderer();
		if (cr instanceof SubstanceDefaultTreeCellRenderer) {
			SubstanceDefaultTreeCellRenderer dtcr = (SubstanceDefaultTreeCellRenderer) cr;

			//Substance 3.3
			Color c = SubstanceLookAndFeel.getActiveColorScheme()
					.getUltraLightColor();
			
			//Substance 5.2
			//Color c = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraLightColor();
			
			Color ctrans = new Color(c.getRed(), c.getGreen(), c.getBlue(),
					Color.TRANSLUCENT);
			dtcr.setBackground(ctrans); // new
										// Color(255,255,255,Color.TRANSLUCENT));

			// dtcr.setBorderSelectionColor(null);
			dtcr.setOpaque(false);

			// Finally, set the tree's background color
			tree.setBackground(ctrans); // new
										// Color(255,255,255,Color.TRANSLUCENT));
										// //dtcr.getBackground());//new
										// Color(255,255,255,Color.TRANSLUCENT));
			tree.setOpaque(false);
		}

		// gui.getTree().setPreferredSize(new Dimension(200, 200));
		museLytter = new PopupListener();

		return gui;
	}

	public void initiateTree(ArrayList<AddressFilterInfoVO> allElements, boolean update)
			throws ParmException {
		if (update == true) {
			this.gui.getRootnode().removeAllChildren();
			this.gui.getTreeModel().reload();
		}
		Iterator<AddressFilterInfoVO> it = allElements.iterator();
		while (it.hasNext()) {
			addParentToTree(it.next(), null);
		}
	}

	public DefaultMutableTreeNode addParentToTree(Object o,
			DefaultMutableTreeNode parentNode) throws ParmException {
		// if (parentNode == null) throw new
		// IllegalArgumentException("parentNode in addParentToTree is null");
		// log.debug("addParentToTree(" + o.toString() + ") start");
		AddressFilterInfoVO newObject =null;
		try
		{
			if (o instanceof AddressFilterInfoVO) {
				
				newObject = (AddressFilterInfoVO) o;
				
				if (newObject.getParent()==null) 
				{
					if (parentNode == null)
						parentNode = gui.getRootnode();
				}
				else
				{
					parentNode = filterNodeMap.get(newObject.getParent());
				}
				
				// inserting object into the tree model:
				newRootFolder = new DefaultMutableTreeNode(newObject);
				newObject.setPath(newRootFolder);
	
				// newObject.setPath((TreePath)newRootFolder);
				gui.getTreeModel().insertNodeInto(newRootFolder, parentNode, parentNode.getChildCount());
				if (newObject.getParent()==null) 
				{
					TreePath path = new TreePath(newRootFolder.getPath());
					// newObject.setPath(newRootFolder);
					gui.getTree().scrollPathToVisible(path);
				}
				filterNodeMap.put(newObject.getPk(), newRootFolder);
			}
		}
		catch(Exception ex)
		{
			Error.getError().addError(Localization.l("common_error"),
					"FilterTreeController Exception in addParentToTree", ex, 1);
		}
		
		return newRootFolder;

	}
	
	/*public void addElementToMap(FilterVO key,DefaultMutableTreeNode value)
	{
		areaNodeMap.put(key, value);
	}*/

	private boolean isParent(Object o) throws ParmException {
		boolean parent = false;
		ArrayList<Object> list;

		return parent;
	}

	private void addChildrenToTree(DefaultMutableTreeNode parentNode)
			throws ParmException { // method runs only if parent object
		// contains a list of objects.
		// log.debug("addChildrenToTree("
		// + parentNode.getUserObject().toString() + ") start");

		Object parentObject = parentNode.getUserObject();

		if (parentObject.getClass().equals(AddressFilterInfoVO.class)) {
			AddressFilterInfoVO aVO = (AddressFilterInfoVO) parentObject;
			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(aVO);
			aVO.setPath(leaf);
			gui.getTreeModel().insertNodeInto(leaf, parentNode,
					parentNode.getChildCount());

		}
	}
	
	public void addFilterToTree(AddressFilterInfoVO node,DefaultMutableTreeNode parentNode)
	{
		//call webservice here to save node at backend
		//node.setPk(""+(PredefinedAreaConstants.areaSequence++));
		
		FilterServerCon areaSyncUtility = new FilterServerCon();
		node.setOperation("insert");
		areaSyncUtility.execute(node,"insert");
		
		log.debug("in addAreaToTree nodePk=");
//		node.setPk(nodePk);
		
		//logic to add new area into the respective node of tree
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
		node.setPath(treeNode);
		
		if(parentNode==null)
		{
			log.warn("parentNode not found, so adding this node at the root of the tree");
			parentNode = gui.getRootnode();
			node.setParent("0");
		}
		gui.getTreeModel().insertNodeInto(treeNode, parentNode,
				parentNode.getChildCount());
//		addElementToMap(node, treeNode);
		
		if ("0".equals(node.getParent())) {
			TreePath path = new TreePath(treeNode.getPath());
			gui.getTree().scrollPathToVisible(path);
		}

	}
	
	public void updateFilterToTree(AddressFilterInfoVO filter,DefaultMutableTreeNode currentNode)
	{
		//call webservice here to update area at backend
		FilterServerCon filterSyncUtility = new FilterServerCon();
		filter.setOperation("update");
		filterSyncUtility.execute(filter,"update");
//		log.debug("in updateAreaToTree nodePk="+nodePk +";newArea.pk="+newArea.getPk());
//		newArea.setPk(nodePk);
		
		//logic to edit area
		if (currentNode.getUserObject() instanceof AddressFilterInfoVO)
		{
			AddressFilterInfoVO addressFilter = (AddressFilterInfoVO) currentNode.getUserObject();
			addressFilter.submitShape(filter.getShape());
	
			currentNode.setUserObject(addressFilter);
			addressFilter.setDescription(filter.getDescription());
		}
		else
		{
			log.warn("error in updating area details="+filter.getPk());
		}
	}
	
	public void removeFilterFromTree(AddressFilterInfoVO filterToBeDeleted, DefaultMutableTreeNode currentNode)
	{
		//call web service here to delete area from backend
		FilterServerCon areaSyncUtility = new FilterServerCon();
		filterToBeDeleted.setOperation("delete");
		areaSyncUtility.execute(filterToBeDeleted,"delete");
		
		//logic to edit area
//		DefaultMutableTreeNode treeNode = this.areaNodeMap.get(node);
		
		getGui().getTreeModel().removeNodeFromParent(currentNode);
//		this.areaNodeMap.remove(node);
	}
	
	/*public DefaultMutableTreeNode getNodeFromTree(FilterVO parent)
	{
		return this.areaNodeMap.get(parent);
	}*/

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			gui.revalidate();
			// first we need to select the node that is clicked!
			if (e.isMetaDown()) {
				int row = gui.getTree().getRowForLocation(e.getX(), e.getY());
				gui.getTree().setSelectionInterval(row, row);
			}
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {

//			maybeShowPopup(e);
//			**check
		}

		private void maybeShowPopup(MouseEvent e) {
//			if (e.isPopupTrigger()) {
				// e.getX(), e.getY()
				// gui.createPopupMenu();
			
//			log.debug("inside maybeShowPopup() e.getButton()="+e.getButton());//
			try
			{
				if(PAS.get_pas().getPredefinedAreaController().getAreaCtrl().isLock())
				{
					PAS.get_pas().getPredefinedAreaController().getAreaCtrl().getGui().toFront();
					return;
				}
			}
			catch(NullPointerException npe)
			{}
			
			if(e.getButton() == MouseEvent.BUTTON3)//if right clicked
			{
				gui.invalidate();
				gui.getPopup().show(e.getComponent(), e.getX(), e.getY());
				gui.getGotoMap().setToolTipText(null);
				int selRow = gui.getTree()
						.getRowForLocation(e.getX(), e.getY());

				selPath = gui.getTree().getPathForLocation(e.getX(), e.getY());
				log.debug("selRow: " + selRow);

				int phonebook = PAS.get_pas().get_userinfo().get_current_department().get_userprofile().getPhonebook();
				log.debug("phonebook="+phonebook);
				if (selRow != -1) {
					
					Object o = findObject(selRow, selPath);
					
					// if user right clicks Addrss Filter
					if (o.getClass().equals(AddressFilterInfoVO.class)) { 
						AddressFilterInfoVO area = ((AddressFilterInfoVO)o);
						area.setTempPk(null);
						
						if(phonebook == PredefinedAreaConstants.WRITE)
						{
							gui.getOptionNew().setEnabled(true);
							gui.getEdit().setEnabled(true);
							gui.getDelete().setEnabled(false);
						}
						else if(phonebook >= PredefinedAreaConstants.WRITE_DELETE)
						{
							gui.getOptionNew().setEnabled(true);
							gui.getEdit().setEnabled(true);
							gui.getDelete().setEnabled(true);
						}
						else
						{
							gui.getOptionNew().setEnabled(false);
							gui.getEdit().setEnabled(false);
							gui.getDelete().setEnabled(false);
						}
						
						gui.getGotoMap().setEnabled(true);
					
					}
				} 
				// if user right-clicks on nothing
				else { 
					
					if(phonebook >= PredefinedAreaConstants.WRITE)
						gui.getOptionNew().setEnabled(true);
					else
						gui.getOptionNew().setEnabled(false);
					
					gui.getEdit().setEnabled(false);
					gui.getDelete().setEnabled(false);
					gui.getGotoMap().setEnabled(false);
				//	gui.getExportPolygon().setEnabled(false);
					log.debug("if user right-clicks on nothing");
				}
				// gui.invalidate();
				 gui.validate();
			}
		}
	}

	public Object findObject(int selRow, TreePath selPath) {
		TreePath tp = selPath;

		Object selectedObject = null;

		try {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tp
					.getLastPathComponent();

			selectedObject = selectedNode.getUserObject();
			getGui().getTreeModel().nodeChanged(selectedNode);

			return selectedObject;
		} catch (ClassCastException ccEx) {
			System.out
					.println("Feil ved konvertering til ObjectVO. Object ikke et Object");
			log.warn(ccEx.getMessage(), ccEx);
			Error.getError().addError(Localization.l("common_error"),
					"FilterTreeController ClassCastException in findObject", ccEx, 1);
		}
		return selectedObject;
	}
	/*
	public void selectNode(ParmVO node) {
		
		get_treegui().getTree().setSelectionPath(path)
	}*/
	/*
	private TreePath findNode(TreePath current_path, ParmVO node) {
		ParmVO vo;
		for(int i=0; i<current_path.getPathCount(); i++)
		{
			vo = (ParmVO)current_path.getPathComponent(i);
			if(vo.getPk().equals(node.getPk()))
				return current_path.getPathComponent(i).
		}
	}*/
	
	public TreePath find(JTree tree, Object node) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    return find2(tree, new TreePath(root), node);
	}
	
	private TreePath find2(JTree tree, TreePath parent, Object node) {
	    TreeNode tnode = (DefaultMutableTreeNode)parent.getLastPathComponent();
	    
	    Object o = null;
	    
	    try {
	    	o = ((DefaultMutableTreeNode)tnode).getUserObject();
	    	
	    	if(o != null && ((ParmVO)o).getPk().equals(((ParmVO)node).getPk()))
	    		return parent.pathByAddingChild(tnode);
	    	
	    } catch(Exception e) {}

	    
        // Traverse children
        if (tnode.getChildCount() >= 0) {
            for (Enumeration e=tnode.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                TreePath result = find2(tree, path, node);
                // Found a match
                if (result != null) {
                    return result;
                }
            }
        }

	    // No match at this branch
	    return null;
	}

	public FilterTreeGUI getGui() {
		return gui;
	}

	public MouseListener getMuseLytter() {
		return museLytter;
	}

	public TreePath getSelPath() {
		return selPath;
	}

	public void setSelPath(TreePath path) {
		selPath = path;
	}
}

