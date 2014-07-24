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
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.event.EventController;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.area.AreaController;
import no.ums.pas.area.AreaController.AreaSource;
import no.ums.pas.area.server.AreaServerCon;
import no.ums.pas.area.tree.AreaTreeController;
import no.ums.pas.area.tree.AreaTreeGUI;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.ums.errorhandling.Error;

/**
 * @author sachinn
 */
public class MainAreaController implements ActionListener, TreeModelListener,
TreeSelectionListener{
    private static final Log log = UmsLog.getLogger(MainAreaController.class);

//	private MainGUI gui;
	private AreaTreeController treeCtrl = new AreaTreeController(this);
	public AreaTreeController getTreeCtrl() { return treeCtrl; }
	private AreaController areaCtrl;
	DefaultMutableTreeNode remNode;
	private AreaVO area;
	private EventController eventCtrl;
	private Collection<Object> objectList;
//	private MapController mapCtrl;
//	private MapPanel map;
	public UserInfo userinfo;
	private ScrollPane treeScrollPane;
	public ScrollPane getTreeScrollPane() { return treeScrollPane; }
	public int tempPK;
	private ParmVO selectedObject = null;
	public ParmVO getSelectedObject() { return selectedObject; }
	
	public AreaController getAreaCtrl() {
		return areaCtrl;
	}
	
	public class ScrollPane extends JScrollPane implements AdjustmentListener {
		public static final long serialVersionUID = 1;
		public ScrollPane(AreaTreeGUI gui) {
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

	public MainAreaController(String sz_sitename, UserInfo userinfo) {
//		initMap(new String [] { sz_sitename } );
		this.tempPK = 0;
		this.userinfo = userinfo;
		try {
//			this.createGUI();
			this.initGUI();
		} catch(Exception e) {
			Error.getError().addError("MainAreaController","Exception in MainController",e,1);
		}
		treeCtrl.SetInitializing(true);
	}

    public void start() {
    }
	
	public void initGUI() {
		treeCtrl = new AreaTreeController(this);
		treeCtrl.getTreeController(); // must run first to initiate the tree
		try {
			
			//call webservice method here to fetch area list
			AreaServerCon areaSyncUtility = new AreaServerCon();
			areaSyncUtility.execute(null, "fetch");
			areaSyncUtility.join();
			
			ArrayList<AreaVO> arrTemp = areaSyncUtility.getAreaList();
			
			log.debug("arrTemp.size()="+arrTemp.size());
			/*AreaVO area = new AreaVO();
			area.setParent("0");
			area.setPk("0");
			area.setName("Areas Root");
			arrTemp.add(area);*/
			treeCtrl.initiateTree(arrTemp, false);//initialize tree with root node
		} catch(Exception e){
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			//Error.getError().addError("MainController","Exception in initGUI",e,1);
		}
		
//		gui = new MainGUI();
		
		// instansiating the tree-ScrollPane..
		treeScrollPane = new ScrollPane(treeCtrl.getGui());
		treeScrollPane.setMinimumSize((new Dimension(200, 200)));

		/*//gui.getItmExit().addActionListener(this);
		gui.getEdit().addActionListener(this);
		gui.getDelete().addActionListener(this);
		gui.getObject().addActionListener(this);
		gui.getObjectfolder().addActionListener(this);
//		gui.getEvent().addActionListener(this);
//		gui.getAlert().addActionListener(this);
		gui.getRefresh().addActionListener(this);*/

		treeCtrl.getGui().getTree().addMouseListener(treeCtrl.getMuseLytter());
		treeCtrl.getGui().getTree().addTreeSelectionListener(this);
		treeCtrl.getGui().getTreeModel().addTreeModelListener(this);
		treeCtrl.getGui().getOptionNew().addActionListener(this);
		treeCtrl.getGui().getDelete().addActionListener(this);
		treeCtrl.getGui().getEdit().addActionListener(this);
		treeCtrl.getGui().getGotoMap().addActionListener(this);
		treeCtrl.getGui().getExportPolygon().addActionListener(this);

	}
	
	/*public void createGUI() throws FileNotFoundException, ParmException {
		gui = new MainGUI();
		initGUI();
		gui.setTitle("Population Alert & Risc Management");
		gui.getMFile().setText("File");
		gui.getMView().setText("View");

		gui.getItmExit().setText("Exit");
		gui.getItmExit().setMnemonic(java.awt.event.KeyEvent.VK_X);

		gui.getMFile().add(gui.getMenuNew());
		gui.getMFile().add(gui.getEdit());
		gui.getEdit().setEnabled(false);
		gui.getDelete().setEnabled(false);
		gui.getMFile().add(gui.getDelete());
		gui.getMFile().addSeparator();
		gui.getMFile().add(gui.getItmExit());

		gui.getMView().add(gui.getRefresh());

		gui.getMenubar().add(gui.getMFile());
		gui.getMenubar().add(gui.getMView());


		// instansiating the map-ScrollPane..
		JScrollPane mapScrollPane = new JScrollPane(map);

		mapScrollPane.setMinimumSize((new Dimension(0, 0)));
		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treeScrollPane, mapScrollPane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerLocation(250);
		splitpane.setContinuousLayout(true);

		gui.getContentPane().add(splitpane);
		gui.getItmExit().addActionListener(this);
		gui.getEdit().addActionListener(this);
		gui.getDelete().addActionListener(this);
		gui.getObject().addActionListener(this);
		gui.getObjectfolder().addActionListener(this);
//		gui.getEvent().addActionListener(this);
//		gui.getAlert().addActionListener(this);
		gui.getRefresh().addActionListener(this);

		treeCtrl.getGui().getTree().addMouseListener(treeCtrl.getMuseLytter());
		treeCtrl.getGui().getTree().addTreeSelectionListener(this);
		treeCtrl.getGui().getTreeModel().addTreeModelListener(this);
		treeCtrl.getGui().getOptionNew().addActionListener(this);
		treeCtrl.getGui().getDelete().addActionListener(this);
		treeCtrl.getGui().getEdit().addActionListener(this);
		treeCtrl.getGui().getGotoMap().addActionListener(this);
		treeCtrl.getGui().getExportPolygon().addActionListener(this);

		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		gui.setSize(1300, 900);
		gui.setVisible(true);
	}*/

	public Navigation getMapNavigation() {
		return Variables.getNavigation();
//		return map.getM_navigation();
	}
	/*public Dimension getMapSize1() {
		return map.getSize();
	}
	protected void clearDrawQueue1() {
		map.clearDrawQueue();
	}
	protected void drawLayers1() {
		map.drawLayers();
	}*/
	protected void addShapeToDrawQueue1(ShapeStruct s) {
		//map.addShapeToDrawQueue(s);
		PAS.pasplugin.addShapeToPaint(s);
	}
	/*protected void mapRedraw1() {
		map.redraw();
	}
	public void mapClear() { //override
		this.map.clear();
	}
	public void updateShape(ShapeStruct p) { //override
		this.map.updateShape(p);
	}
	public void updateShapeFilled(ShapeStruct p) { //override
		this.map.updateShape(p);
	}
	protected Graphics getMapGraphics1() {
		return map.getGraphics();
	}
	public ShapeStruct getMapShape1() {
		return map.getM_shape();
	}*/
	public void gotoMap() {
		
	}
	//ProgressMonitor monitor;
	LoadingFrame m_progress;
	public void endSession(boolean bShowProgress) {
		try {
			new Thread("Predefined area endSession thread") {
				public void run() {
                    /*m_progress = new LoadingFrame(Localization.l("main_parmtab_closing_parm"), null);
                    m_progress.set_totalitems(0, Localization.l("main_parmtab_closing_parm"));
					m_progress.start_and_show();*/
//					**check
					/*treeCtrl.SetInitializing(true);
					treeCtrl.get_treegui().loader.set_text("Closing PARM");
					treeCtrl.get_treegui().loader.set_totalitems(0, "Closing PARM");*/
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
				PAS.get_pas().getPredefinedAreaController().clearMap();
				try
				{
					AreaVO area = null;
					DefaultMutableTreeNode remNode = null;
					try{
						remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
						Object object = remNode.getUserObject();
						if (object.getClass().equals(AreaVO.class)) {
							area = (AreaVO) object;
							log.debug("area details; name="+area.getName()+";pk="+area.getPk());
						}
					}
					catch(Exception ex)
					{
					}
					
					if (this.areaCtrl == null) {
						this.areaCtrl = new AreaController(this, getMapNavigation());
					}
					this.areaCtrl.createNewArea(this,remNode,false,AreaSource.LIBRARY);
				}
				catch(Exception ex)
				{
					log.error("MainAreaController OptionNew click handler exception",ex);
				}
			}
			else if (e.getSource() == treeCtrl.getGui().getEdit()) {
				log.debug("Edit clicked");
				PAS.get_pas().getPredefinedAreaController().gotoMap();
				try
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object object = remNode.getUserObject();
					if (object.getClass().equals(AreaVO.class)) {
						AreaVO area = (AreaVO) object;
						log.debug("area details; name="+area.getName()+";pk="+area.getPk());
						
						if (this.areaCtrl == null) {
							this.areaCtrl = new AreaController(this, getMapNavigation());
						}
						this.areaCtrl.editArea(remNode, true, AreaSource.LIBRARY);
					}
				}
				catch(Exception ex)
				{
					log.error("MainAreaController OptionNew click handler exception",ex);
				}
			}
			else if (e.getSource() == treeCtrl.getGui().getDelete()) {
				log.debug("Delete clicked");
				
				try
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object object = remNode.getUserObject();
					if (object.getClass().equals(AreaVO.class)) 
					{
						AreaVO area = (AreaVO) object;
						log.debug("area details; name="+area.getName()+";pk="+area.getPk());
						String warningMessage = Localization.l("common_delete_are_you_sure") + " '" + area.getName() + "'?";
//						log.debug("remNode.getChildCount()="+remNode.getChildCount());
						if(remNode.getChildCount() > 0)
							warningMessage = warningMessage + "\n" + Localization.l("common_subnodes") + " " + Localization.l("common_will_be_deleted");
						if(JOptionPane.showConfirmDialog(null, warningMessage, Localization.l("mainmenu_libraries_predefined_areas_delete"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) 
						{
							if (this.areaCtrl == null) 
								this.areaCtrl = new AreaController(this, getMapNavigation());
							this.areaCtrl.deleteArea(area,remNode);
						}
					}
				}
				catch(Exception ex)
				{
					log.error("MainAreaController OptionNew click handler exception",ex);
				}
				
			}
			else if (e.getSource() == treeCtrl.getGui().getGotoMap()) {
				log.debug("GotoMap clicked");
				PAS.get_pas().getPredefinedAreaController().gotoMap();
				/*try
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object object = remNode.getUserObject();
					if (object.getClass().equals(AreaVO.class)) {
						AreaVO area = (AreaVO) object;
						log.debug("area details; name="+area.getName()+";pk="+area.getPk());
						PAS.get_pas().getPredefinedAreaController().gotoMap();
					}
				}
				catch(Exception ex)
				{
					log.error("MainAreaController OptionNew click handler exception",ex);
				}*/
			}
			else if (e.getSource() == treeCtrl.getGui().getExportPolygon()) {
				log.debug("ExportPolygon clicked");
				DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
				Object o = remNode.getUserObject();			
				if(o.getClass().equals(AreaVO.class))
				{
					//AlertVO exp = (AlertVO)o;
					AreaVO [] exp = new AreaVO[1];
					exp[0] = (AreaVO)o;
					PAS.get_pas().actionPerformed(new ActionEvent(exp, ActionEvent.ACTION_PERFORMED, "act_export_alert_polygon"));
				}
			}
				
		}
		catch(Exception ex)
		{
			log.error("MainAreaController click handler exception", ex);
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
		/*if (this.treeCtrl.getGui().getRootnode().isLeaf()) {
			updateTree();
		}*/
		//**check above if block

		/*else*/ {
			if (c == null)
				throw new ParmException("collection predefinedareas. is null");
			else if (c.size() == 0)
				log.debug("Updating... no new elements!");

			if (c.size() > 0 && c != null) {
				//xmlreader.sortListAscending((ArrayList) c);
//				ListSorter ls = new ListSorter();
//				Collections.sort((ArrayList<Object>)c,ls);
//				**check
				log.debug("Updating... " + c.size() + " new elements!");
				updatedObjects = (ArrayList<Object>) c;
				Object o;
				DefaultMutableTreeNode wantedNode = null;
				for (int i = 0; i < updatedObjects.size(); i++) { // for each
																	//element
																	// in list
					o = updatedObjects.get(i);
					
					if (o.getClass().equals(AreaVO.class)) {
						AreaVO aVO = (AreaVO) updatedObjects.get(i);
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
						/*else { // new element.
*/							String parent = aVO.getParent();
							DefaultMutableTreeNode parentNode = getParentNode(parent, this.treeCtrl.getGui().getTree());
							if(parentNode==null)
							{
								log.error("parentNode==null for alertpk %s [%s]", aVO.getAlertpk(), aVO.getName());
							}

							this.treeCtrl.addParentToTree(aVO, parentNode);
					/*	}*/
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
		if (o.getClass().equals(AreaVO.class)) {
			AreaVO aVO = (AreaVO) o;
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
		
		DefaultMutableTreeNode node = PAS.get_pas().getPredefinedAreaController().getTreeCtrl().getGui().getRootnode();
		
		for(int i=0;i<node.getChildCount();i++){
			Object o = node.getUserObject();
			if (o.getClass().equals(AreaVO.class)) {
				AreaVO aVO = (AreaVO) o;
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

	/*private void initMap(String[] args) {
		mapCtrl = new MapController(args);
		map = mapCtrl.getMapPanel();
	}*/

	public int getHighestTemp() {
		this.tempPK++;
		return this.tempPK;
	}

	/*public MapPanel getMap() {
		return map;
	}*/
	

	public void valueChanged(TreeSelectionEvent e) { // fires when selecting
														// new nodes.
		// clear map
		/*mapClear(); // På alert må jeg finne parent og tegne opp alle for så å sette den valgte til filled
		updateShape(null);
		updateShapeFilled(null);*/
		PAS.get_pas().getPredefinedAreaController().clearMap();
		
		PAS.get_pas().kickRepaint();
		
		// #1: Get selected Object.
		javax.swing.JTree tree = (javax.swing.JTree) e.getSource();

		TreePath path = tree.getSelectionPath();
		this.treeCtrl.setSelPath(path);

		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object o = node.getUserObject();
			/*gui.getEdit().setEnabled(true);*/

			//PolygonStruct p = null;

			if(o.getClass().equals(AreaVO.class)) {
				setSelectedAlert(o);				
			}
			// register the event invoker with map-panel..

			// enable/disable the correct menu-items..

			/*gui.getEdit().setEnabled(true);
			gui.getDelete().setEnabled(true);*/
			if (o.getClass().equals(AreaVO.class)) { // if user right
																// clicks Alert
//				gui.getMenuNew().setEnabled(false);
			}

			/*else { // if user right-clicks on nothing
				gui.getEdit().setEnabled(false);
				gui.getDelete().setEnabled(false);

				gui.getMenuNew().setEnabled(true);
//				gui.getEvent().setEnabled(false);
				gui.getObject().setEnabled(true);
				gui.getObjectfolder().setEnabled(true);
//				gui.getAlert().setEnabled(false);
			}*/
			//mapRedraw();
			PAS.get_pas().kickRepaint();
			//hihi tiss tass
		}
	}
	
	// Hører til checkIfLocked
	private void checkThis(ArrayList<Object> a, Object o) {
		Iterator<Object> it;
		if(o.getClass().equals(AreaVO.class)) {
			if(((AreaVO)o).getLocked()==1)
				a.add(o);
		}
	}
	
	public void setSelectedAlert(Object o) {
		PAS.get_pas().getPredefinedAreaController().clearMap();
		ParmVO alert = (ParmVO)o;
		selectedObject = alert;
		if(o==null)
			return;
		try {
			ShapeStruct shape = null;
			if(alert.getM_shape() != null) {
				shape = (ShapeStruct)alert.getM_shape().clone();
				alert.getM_shape().calc_coortopix(getMapNavigation());
				//addShapeToDrawQueue(p.getM_shape());
				PAS.get_pas().getPredefinedAreaController().addShapeToDrawQueue(alert.getM_shape());
			}
			
//			DefaultMutableTreeNode dmt = findNodeByPk(((AreaVO)o).getParent());
//			**check
			/*DefaultMutableTreeNode dmt = this.getTreeCtrl().getNodeFromTree((AreaVO)o);
			if(dmt == null) { // Denne er dersom det har skjedd en refresh mot databasen og parent har fått oppdatert pk
				dmt = findNodeByPk(((AreaVO)o).getAlertpk());
				dmt = (DefaultMutableTreeNode)dmt.getParent();
			}*/
			
			// Her må jeg legge til objektet i edit
			// Dette må gjøres i send option toolbar egentlig
		} catch(Exception ex) {
			Error.getError().addError("MainAreaController","Exception in valueChanged, error finding parentnode",ex,Error.SEVERITY_ERROR);
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
//		DefaultMutableTreeNode[] parentList = (DefaultMutableTreeNode[]) e.getPath();
//		DefaultMutableTreeNode parent = parentList[0];
//		log.debug("Deleted node parent: " + parent.getUserObject());
	}

	public void treeStructureChanged(TreeModelEvent e) {
	
	}
		
		
	/*public static void main(String[] args) {
		try {
			new MainController(args);
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in main",e,1);
		} catch (ParmException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in main",e,1);
		}
	}*/

}
