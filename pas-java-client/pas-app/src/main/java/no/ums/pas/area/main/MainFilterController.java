package no.ums.pas.area.main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.AreaController.AreaSource;
import no.ums.pas.area.FilterController;
import no.ums.pas.area.FilterController.FilterSource;
import no.ums.pas.area.server.FilterServerCon;
import no.ums.pas.area.tree.FilterTreeController;
import no.ums.pas.area.tree.FilterTreeGUI;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.event.EventController;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.send.SendProperties;
import no.ums.pas.ums.errorhandling.Error;

/**
 * @author abhinava
 */
public class MainFilterController implements ActionListener, TreeModelListener,
TreeSelectionListener{
    private static final Log log = UmsLog.getLogger(MainFilterController.class);

//	private MainGUI gui;
	private FilterTreeController treeCtrl = new FilterTreeController(this);
	public FilterTreeController getTreeCtrl() { return treeCtrl; }
	private FilterController areaCtrl1;
	DefaultMutableTreeNode remNode;
	private AddressFilterInfoVO area;
	private EventController eventCtrl;
	private Collection<Object> objectList;

	public UserInfo userinfo;
	private ScrollPane treeScrollPane;
	public ScrollPane getTreeScrollPane() { return treeScrollPane; }
	public int tempPK;
	private ParmVO selectedObject = null;
	public ParmVO getSelectedObject() { return selectedObject; }
	
	public FilterController getFilterCtrl() {
		if (this.areaCtrl1 == null) {
			this.areaCtrl1 = new FilterController(this, getMapNavigation());
		}
		return areaCtrl1;
	}
	
	public class ScrollPane extends JScrollPane implements AdjustmentListener {
		public static final long serialVersionUID = 1;
		public ScrollPane(FilterTreeGUI gui) {
			super(gui);
			getVerticalScrollBar().addAdjustmentListener(this);
			getHorizontalScrollBar().addAdjustmentListener(this);
			getVerticalScrollBar().setUnitIncrement(15);
		}

		//make sure to repaint tree when scrolling has occured
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
//			gui.repaint();
			getTreeCtrl().getGui().repaint();
		}
		
	}

	public MainFilterController(String sz_sitename, UserInfo userinfo) {

		this.tempPK = 0;
		this.userinfo = userinfo;
		try {
//			this.createGUI();
			this.initGUI();
		} catch(Exception e) {
			Error.getError().addError("MainFilterController","Exception in MainController",e,1);
		}
		treeCtrl.SetInitializing(true);
	}

	public MainFilterController(String sz_sitename, UserInfo userinfo,boolean inBackground) {
//		initMap(new String [] { sz_sitename } );
		this.tempPK = 0;
		this.userinfo = userinfo;

	}

    public void start() {
    }
	
	public void initGUI() {
		treeCtrl = new FilterTreeController(this);
		treeCtrl.getTreeController(); // must run first to initiate the tree
		try {
			
			
			//call webservice method here to fetch filter list
			FilterServerCon filterSyncUtility = new FilterServerCon();
			filterSyncUtility.execute(null, "fetch");
			filterSyncUtility.join();
			
			ArrayList<AddressFilterInfoVO> arrTemp = filterSyncUtility.getFilterList();
			
			log.debug("arrTemp.size()="+arrTemp.size());
			
			treeCtrl.initiateTree(arrTemp, false);//initialize tree with root node
		} catch(Exception e){
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			//Error.getError().addError("MainController","Exception in initGUI",e,1);
		}
       // instansiating the tree-ScrollPane..
		treeScrollPane = new ScrollPane(treeCtrl.getGui());
		treeScrollPane.setMinimumSize((new Dimension(200, 200)));

	
		treeCtrl.getGui().getTree().addMouseListener(treeCtrl.getMuseLytter());
		treeCtrl.getGui().getTree().addTreeSelectionListener(this);
		treeCtrl.getGui().getTreeModel().addTreeModelListener(this);
		treeCtrl.getGui().getOptionNew().addActionListener(this);
		treeCtrl.getGui().getDelete().addActionListener(this);
		treeCtrl.getGui().getEdit().addActionListener(this);
		treeCtrl.getGui().getGotoMap().addActionListener(this);
		}
	
	public Navigation getMapNavigation() {
		return Variables.getNavigation();
//		return map.getM_navigation();
	}
	
	protected void addShapeToDrawQueue1(ShapeStruct s) {
		//map.addShapeToDrawQueue(s);
		PAS.pasplugin.addShapeToPaint(s);
	}

	public void gotoMap() {
		
	}
	//ProgressMonitor monitor;
	LoadingFrame m_progress;
	public void endSession(boolean bShowProgress) {
		try {
			new Thread("Predefined area endSession thread") {
				public void run() {
                 
				}
			}.start();
		} catch(Exception e) {
			log.debug(e.getMessage());
		}
	}
	
public void actionPerformed(ActionEvent e) {
		// '-- this --
		try
		{
			//treeCtrl.getGui().getDelete() 
			if (e.getSource() == treeCtrl.getGui().getOptionNew()) {
				log.debug("OptionNew clicked");
				PAS.get_pas().getPredefinedFilterController().clearMap();
				try
				{
					AddressFilterInfoVO area = null;
					DefaultMutableTreeNode remNode = null;
					try{
						remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
						Object object = remNode.getUserObject();
						if (object.getClass().equals(AddressFilterInfoVO.class)) {
							area = (AddressFilterInfoVO) object;
							log.debug("area details; name="+area.getName()+";pk="+area.getPk());
						}
					}
					catch(Exception ex)
					{
					}
					

					this.getFilterCtrl().createNewFilter(this,remNode,false,FilterSource.LIBRARY);
				}
				catch(Exception ex)
				{
					log.error("MainFilterController OptionNew click handler exception",ex);
				}
			}
			else if (e.getSource() == treeCtrl.getGui().getEdit()) {
				log.debug("Edit clicked");
				PAS.get_pas().getPredefinedFilterController().gotoMap();
				try
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object object = remNode.getUserObject();
					if (object.getClass().equals(AddressFilterInfoVO.class)) {
						AddressFilterInfoVO filter = (AddressFilterInfoVO) object;
						log.debug("area details; name="+filter.getFilterName()+";description="+filter.getDescription());
						

						this.getFilterCtrl().editFilter(remNode, true, FilterSource.LIBRARY);
					}
				}
				catch(Exception ex)
				{
					log.error("MainFilterController OptionNew click handler exception",ex);
				}
			}
			else if (e.getSource() == treeCtrl.getGui().getDelete()) {
				log.debug("Delete clicked");
				
				try
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object object = remNode.getUserObject();
					if (object.getClass().equals(AddressFilterInfoVO.class)) 
					{
						AddressFilterInfoVO filter = (AddressFilterInfoVO) object;
						log.debug("filter details; name="+filter.getFilterName()+";filterID="+filter.getFilterId());
						String warningMessage = Localization.l("common_delete_are_you_sure") + " '" + filter.getFilterName() + "'?";
//						log.debug("remNode.getChildCount()="+remNode.getChildCount());
						if(remNode.getChildCount() > 0)
							warningMessage = warningMessage + "\n" + Localization.l("common_subnodes") + " " + Localization.l("common_will_be_deleted");
						if(JOptionPane.showConfirmDialog(null, warningMessage, Localization.l("mainmenu_libraries_predefined_filters_delete"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) 
						{

							this.getFilterCtrl().deleteFilter(filter,remNode);
						}
					}
				}
				catch(Exception ex)
				{
					log.error("MainFilterController OptionNew click handler exception",ex);
				}
				
			}
			else if (e.getSource() == treeCtrl.getGui().getGotoMap()) {
				log.debug("GotoMap clicked1");
				PAS.get_pas().getPredefinedFilterController().gotoMap();
		
			}
		
				
		}
		catch(Exception ex)
		{
			log.error("MainFilterController click handler exception", ex);
		}
	}
	
	public void activateEventBtnListener() {
		eventCtrl.getGui().getActionPanel().getBtnCancel().addActionListener(this);
		eventCtrl.getGui().getActionPanel().getBtnSave().addActionListener(this);
	}

	public void addToObjectList(Object object) {
		this.objectList = new ArrayList<Object>();
		this.objectList.add(object);
	}
	public void saveChanges(ParmVO object) {
		if(object.getPk().startsWith("a") || object.getPk().startsWith("e") || object.getPk().startsWith("o")) {
			object.setOperation("update");
			addToObjectList(object);
		}
		else {//insert
			object.setOperation("insert");
			addToObjectList(object);
		}
	}

	public void refreshTree(Collection<Object> c) throws ParmException {
		// Tar imot en liste med oppdaterte objeter samt nye fra tråden
		ArrayList<Object> updatedObjects = null;
		 {
			if (c == null)
				throw new ParmException("collection predefinedareas. is null");
			else if (c.size() == 0)
				log.debug("Updating... no new elements!");

			if (c.size() > 0 && c != null) {

//				**check
				log.debug("Updating... " + c.size() + " new elements!");
				updatedObjects = (ArrayList<Object>) c;
				Object o;
				DefaultMutableTreeNode wantedNode = null;
				for (int i = 0; i < updatedObjects.size(); i++) { // for each
																	//element
																	// in list
					o = updatedObjects.get(i);
					
					if (o.getClass().equals(AddressFilterInfoVO.class)) {
						AddressFilterInfoVO aVO = (AddressFilterInfoVO) updatedObjects.get(i);
						log.debug(aVO);

						String tempPk = aVO.getTempPk();
						if(tempPk==null)
							tempPk = aVO.getAlertpk();
						DefaultMutableTreeNode rootNode = this.treeCtrl.getGui().getRootnode(); 
						int childCount = rootNode.getChildCount();
						DefaultMutableTreeNode almostRootNode;
						
						for (int u = 0; u < childCount; u++) { // for each "root" node in tree
							almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(u) ;
							wantedNode = findNodeByPk(almostRootNode, tempPk);
							aVO.setPath(wantedNode);
							if (wantedNode != null){
								break;
							}
						}

						String parent = aVO.getParent();
							DefaultMutableTreeNode parentNode = getParentNode(parent, this.treeCtrl.getGui().getTree());
							if(parentNode==null)
							{
								log.error("parentNode==null for alertpk %s [%s]", aVO.getAlertpk(), aVO.getName());
							}

							this.treeCtrl.addParentToTree(aVO, parentNode);
                           }

				}
			}
		}
		this.treeCtrl.getGui().repaint();
		treeCtrl.SetInitializing(false);
	}

	// 
	public DefaultMutableTreeNode getParentNode(String parentId,
			javax.swing.JTree tree) throws ParmException {
		if (parentId.length() < 1)
			throw new IllegalArgumentException("parentID has no length");
		DefaultMutableTreeNode returnNode = null;

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree
				.getModel().getRoot();
		int childCount = rootNode.getChildCount();

		DefaultMutableTreeNode almostRootNode;
		for (int i = 0; i < childCount; i++) { // for each "root" node in tree
			almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			returnNode = findNodeByPk(almostRootNode, parentId);
			if (returnNode != null)
				return returnNode;
		}
		return returnNode;
	}

	private DefaultMutableTreeNode findNodeByPk(DefaultMutableTreeNode node,
			String parentPk) throws ParmException {
		if (node == null)
			node = treeCtrl.getGui().getRootnode();
		if (parentPk == null)
			throw new IllegalArgumentException("Passed parentPk is null");

		Object o = node.getUserObject();
		if (o.getClass().equals(AddressFilterInfoVO.class)) {
			AddressFilterInfoVO aVO = (AddressFilterInfoVO) o;
			if (aVO.getAlertpk().equals(parentPk))
				return node;
		}

		int childCount = node.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			DefaultMutableTreeNode result = findNodeByPk(child, parentPk);
			if (result != null)
				return result;
		}

		return null;
	}
	
	public DefaultMutableTreeNode findNodeByPk(String pk) throws ParmException {
		
		DefaultMutableTreeNode node = PAS.get_pas().getPredefinedFilterController().getTreeCtrl().getGui().getRootnode();
		
		for(int i=0;i<node.getChildCount();i++){
			Object o = node.getUserObject();
			if (o.getClass().equals(AddressFilterInfoVO.class)) {
				AddressFilterInfoVO aVO = (AddressFilterInfoVO) o;
				if (aVO.getAlertpk().equals(pk))
					return node;
			}
	
			int childCount = node.getChildCount();
			for (int j = 0; j < childCount; j++) {
				DefaultMutableTreeNode result = findNodeByPk(node, pk);
				if (result != null)
					return result;
			}
		}

		return null;
	}

	// methods for searching for teh node
	public static DefaultMutableTreeNode findNode(javax.swing.JTree aTree,
			Object aUserObject) {
		if (aTree == null)
			throw new IllegalArgumentException("Passed tree is null");
		if (aUserObject == null)
			throw new IllegalArgumentException("Passed user object is null");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) aTree.getModel()
				.getRoot();
		return findNodeImpl(node, aUserObject);
	}

	private static DefaultMutableTreeNode findNodeImpl(
			DefaultMutableTreeNode aNode, Object aUserObject) {
		if (aNode.getUserObject().equals(aUserObject))
			return aNode;
		int childCount = aNode.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) aNode
					.getChildAt(i);
			DefaultMutableTreeNode result = findNodeImpl(child, aUserObject);
			if (result != null)
				return result;
		}
		return null;
	}

	public boolean checkObject(Object object, Object type) {
		boolean isValid = false;
		if (object.getClass().equals(type)) {
			isValid = true;
		}
		return isValid;
	}

	public Object getObjectFromTree() {
		if (treeCtrl.getSelPath() != null) {
			DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl
					.getSelPath().getLastPathComponent();
			Object object = remNode.getUserObject();
			return object;
		} else
			return null;
	}


	public int getHighestTemp() {
		this.tempPK++;
		return this.tempPK;
	}

      public void valueChanged(TreeSelectionEvent e) { // fires when selecting
														// new nodes.

		PAS.get_pas().getPredefinedFilterController().clearMap();
		
		PAS.get_pas().kickRepaint();
		
		// #1: Get selected Object.
		javax.swing.JTree tree = (javax.swing.JTree) e.getSource();

		TreePath path = tree.getSelectionPath();
		this.treeCtrl.setSelPath(path);

		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object o = node.getUserObject();
            if(o.getClass().equals(AddressFilterInfoVO.class)) {
				setSelectedAlert(o);				
			}
         if (o.getClass().equals(AddressFilterInfoVO.class)) { // if user right
																// clicks Alert
         }
        PAS.get_pas().kickRepaint();
			//hihi tiss tass
		}
	}
	
	// Hører til checkIfLocked
	private void checkThis(ArrayList<Object> a, Object o) {
		Iterator<Object> it;
		if(o.getClass().equals(AddressFilterInfoVO.class)) {
			if(((AddressFilterInfoVO)o).getLocked()==1)
				a.add(o);
		}
	}
	
	public void setSelectedAlert(Object o) {
		PAS.get_pas().getPredefinedFilterController().clearMap();
		ParmVO alert = (ParmVO)o;
		selectedObject = alert;
		if(o==null)
			return;
		try {
			ShapeStruct shape = null;
			if(alert.getM_shape() != null) {

				if(alert.getM_shape() instanceof GISShape)
				{
					shape = (ShapeStruct)alert.getM_shape();
					alert.getM_shape().calc_coortopix(getMapNavigation());
                    SendProperties m_sendproperties = this.getFilterCtrl().getSendProperties(true);
                    m_sendproperties.typecast_gis().set_gisFilterlist(((GISShape) shape).getM_gisfilterlist());
					m_sendproperties.set_shapestruct(shape);
                    this.getFilterCtrl().setEditShape(shape);
                    PAS.get_pas().kickRepaint();
                }
				else
				{
					shape = (ShapeStruct)alert.getM_shape().clone();
					alert.getM_shape().calc_coortopix(getMapNavigation());
					//addShapeToDrawQueue(p.getM_shape());
				}
				PAS.get_pas().getPredefinedFilterController().addShapeToDrawQueue(alert.getM_shape());
			}
       } catch(Exception ex) {
			Error.getError().addError("MainFilterController","Exception in valueChanged, error finding parentnode",ex,Error.SEVERITY_ERROR);
		}
	}

	protected void getPolystructCoords(PolygonStruct p, ArrayList<Double> lon, ArrayList<Double> lat){
		for(int i=0;i<lon.size();i++)
			p.add_coor((Double)lon.get(i),(Double)lat.get(i));
	}
	public void setDrawMode(ShapeStruct p) {
		
	}
	
	public void setFilled(ShapeStruct p) {
		
	}
	
	public void treeNodesChanged(TreeModelEvent e) {

	}

	public void treeNodesInserted(TreeModelEvent e) {

	}

	public void treeNodesRemoved(TreeModelEvent e) {
}

	public void treeStructureChanged(TreeModelEvent e) {
	
	}
		

}
