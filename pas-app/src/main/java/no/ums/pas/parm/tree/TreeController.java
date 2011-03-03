package no.ums.pas.parm.tree;


import no.ums.pas.PAS;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.errorhandling.Error;
import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

//Substance 3.3


//Substance 5.2
//import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

// Se jeg har fått en blå balongi!

public class TreeController {

	private TreeGUI gui;

	public TreeGUI get_treegui() {
		return gui;
	}

	private MouseListener museLytter;
	private TreePath selPath;
	private ObjectVO object;
	private DefaultMutableTreeNode newRootFolder;
	public static ArrayList<Object> staticList = new ArrayList<Object>();
	private MainController main;

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

	public TreeController(MainController main) {
		this.main = main;
	}

	public JPanel getTreeController() {
		gui = new TreeGUI(main);
		javax.swing.JTree tree = gui.getTree();

		// Get the tree's cell renderer. If it is a default
		// cell renderer, customize it.
		/*
		 * TreeCellRenderer cr = tree.getCellRenderer(); if (cr instanceof
		 * TreeCellRenderer) { DefaultTreeCellRenderer dtcr =
		 * (DefaultTreeCellRenderer)cr;
		 *  // Set the various colors Color c = dtcr.getBackground();
		 * System.out.println("Transparancy: " + c.getTransparency());
		 * dtcr.setBackgroundSelectionColor(null);
		 * dtcr.setBackgroundNonSelectionColor(new
		 * Color(255,255,255,Color.TRANSLUCENT)); dtcr.setBackground(null);
		 * dtcr.setBorderSelectionColor(null); dtcr.setOpaque(false);
		 * 
		 *  // Finally, set the tree's background color tree.setBackground(new
		 * Color(255,255,255,Color.TRANSLUCENT)); tree.setOpaque(false); }
		 */

		TreeCellRenderer cr = tree.getCellRenderer();
		if (cr instanceof SubstanceDefaultTreeCellRenderer) {
			SubstanceDefaultTreeCellRenderer dtcr = (SubstanceDefaultTreeCellRenderer) cr;

			// Set the various colors
			// Color c = dtcr.getBackground();
			// System.out.println("Transparancy: " + c.getTransparency());
			// dtcr.setBackgroundSelectionColor(null);
			// dtcr.setBackgroundNonSelectionColor(new
			// Color(255,255,255,Color.TRANSLUCENT));
			
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

	public void initiateTree(ArrayList<Object> allElements, boolean update)
			throws ParmException {
		if (update == true) {
			this.gui.getRootnode().removeAllChildren();
			this.gui.getTreeModel().reload();
		}
		Iterator<Object> it = allElements.iterator();
		while (it.hasNext()) {
			addParentToTree(it.next(), null);
		}
	}

	public DefaultMutableTreeNode addParentToTree(Object o,
			DefaultMutableTreeNode parentNode) throws ParmException {
		// if (parentNode == null) throw new
		// IllegalArgumentException("parentNode in addParentToTree is null");
		// System.out.println("addParentToTree(" + o.toString() + ") start");

		if (o.getClass().equals(ObjectVO.class)) { // Object is of class
													// ObjectVO

			ObjectVO newObject = (ObjectVO) o;
			if (parentNode == null)
				parentNode = gui.getRootnode();

			// inserting object into the tree model:
			newRootFolder = new DefaultMutableTreeNode(newObject);
			newObject.setPath(newRootFolder);
			

			// newObject.setPath((TreePath)newRootFolder);
			gui.getTreeModel().insertNodeInto(newRootFolder, parentNode,
					parentNode.getChildCount());
			if (newObject.getParent().equals("o-1")) {
				TreePath path = new TreePath(newRootFolder.getPath());
				// newObject.setPath(newRootFolder);
				gui.getTree().scrollPathToVisible(path);
			}

			// checking if the object contains other objects:
			if (newObject.getList() == null || newObject.getList().isEmpty())
				;
			else
				addChildrenToTree(newRootFolder);
		}

		else if (o.getClass().equals(EventVO.class)) { // Object is of class
														// EventVO
		// if (parentNode == null) throw new ParmException("parentNode is null
		// for Event insertion");

			EventVO newObject = (EventVO) o;
			newRootFolder = new DefaultMutableTreeNode(newObject);
			newObject.setPath(newRootFolder);
			gui.getTreeModel().insertNodeInto(newRootFolder, parentNode,
					parentNode.getChildCount());

			// En test for å sjekke om parent er oppdatert uten at child
			// objektet har endret parentpk
			/*
			 * ObjectVO parent = (ObjectVO)parentNode.getUserObject();
			 * if(newObject.getParentpk() != parent.getPk())
			 * newObject.setParentpk(parent.getPk());
			 */

			// checking if the object contains other objects:
			if (newObject.getAlertListe() == null
					|| newObject.getAlertListe().isEmpty())
				System.out.println(newObject.toString()
						+ " has no children. Goodly. :P");
			else
				addChildrenToTree(newRootFolder);
		} else if (o.getClass().equals(AlertVO.class)) { // Object is of
															// class AlertVO
		// if (parentNode == null) throw new ParmException("parentNode is null
		// for Alert insertion");

			AlertVO newObject = (AlertVO) o;
			newRootFolder = new DefaultMutableTreeNode(newObject);
			newObject.setPath(newRootFolder);
			gui.getTreeModel().insertNodeInto(newRootFolder, parentNode,
					parentNode.getChildCount());

		}
		return newRootFolder;

	}

	private boolean isParent(Object o) throws ParmException {
		boolean parent = false;
		ArrayList<Object> list;

		if (o.getClass().equals(ObjectVO.class)) {
			ObjectVO oVO = (ObjectVO) o;
			list = oVO.getList();

			if (list == null || list.isEmpty())
				;
			else
				parent = true;
		}

		else if (o.getClass().equals(EventVO.class)) {
			EventVO eVO = (EventVO) o;
			list = eVO.getAlertListe();

			if (list == null || list.isEmpty())
				;
			else
				parent = true;
		}

		else if (o.getClass().equals(AlertVO.class)) {
			;
		}

		else
			throw new ParmException(
					"Object to be placed in tree is not of class ObjectVO, EventVO or AlertVO!");

		return parent;
	}

	private void addChildrenToTree(DefaultMutableTreeNode parentNode)
			throws ParmException { // method runs only if parent object
		// contains a list of objects.
		// System.out.println("addChildrenToTree("
		// + parentNode.getUserObject().toString() + ") start");

		Object parentObject = parentNode.getUserObject();

		if (parentObject.getClass().equals(ObjectVO.class)) {
			ObjectVO oVO = (ObjectVO) parentObject;
			ArrayList<Object> objectList = oVO.getList();

			for (int i = 0; i < objectList.size(); i++) { // for each element
				// in the parents
				// list of objects,
				// do following:
				/* remember, childObject could be ObjectVO, EventVO or AlertVO */

				Object childObject = objectList.get(i);
				if (isParent(childObject) == true) { // if the childObject
					// has childObjects
					addParentToTree(childObject, parentNode);
				} else {
					DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(
							childObject);
					if (childObject.getClass().equals(ObjectVO.class)) {
						((ObjectVO) childObject).setPath(leaf);
					} else if (childObject.getClass().equals(EventVO.class)) {
						((EventVO) childObject).setPath(leaf);
					} else if (childObject.getClass().equals(AlertVO.class)) {
						((AlertVO) childObject).setPath(leaf);
					}
					gui.getTreeModel().insertNodeInto(leaf, parentNode,
							parentNode.getChildCount());
				}
			}
		}

		else if (parentObject.getClass().equals(EventVO.class)) {
			EventVO eVO = (EventVO) parentObject;
			ArrayList<Object> objectList = eVO.getAlertListe();

			for (int i = 0; i < objectList.size(); i++) { // for each element
				// in the events
				// list of alerts,
				// do the following:
				AlertVO aVO = (AlertVO) objectList.get(i);
				DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(aVO);
				aVO.setPath(leaf);
				gui.getTreeModel().insertNodeInto(leaf, parentNode,
						parentNode.getChildCount());
			}
		} else if (parentObject.getClass().equals(AlertVO.class)) {
			AlertVO aVO = (AlertVO) parentObject;
			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(aVO);
			aVO.setPath(leaf);
			gui.getTreeModel().insertNodeInto(leaf, parentNode,
					parentNode.getChildCount());

		}
	}

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

			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				// e.getX(), e.getY()
				// gui.createPopupMenu();
				gui.invalidate();
				gui.getPopup().show(e.getComponent(), e.getX(), e.getY());
				gui.getGotoMap().setToolTipText(null);
				int selRow = gui.getTree()
						.getRowForLocation(e.getX(), e.getY());

				selPath = gui.getTree().getPathForLocation(e.getX(), e.getY());

				if (selRow != -1) {
					System.out.println("selRow: " + selRow);
					gui.getEdit().setEnabled(true);
					gui.getDelete().setEnabled(true);

					Object o = findObject(selRow, selPath);
					
					
					if (o.getClass().equals(EventVO.class)) { // if user right
																// clicks Event
						
						if(!((ParmVO)o).hasValidPk()) {
							gui.getEdit().setEnabled(false);
							gui.getDelete().setEnabled(false);
							gui.getNew().setEnabled(false);
							gui.getGenerateSending().setEnabled(false);
							gui.getEvent().setEnabled(false);
							gui.getObject().setEnabled(false);
							gui.getObjectfolder().setEnabled(false);
							gui.getAlert().setEnabled(false);
							gui.getSnapSending().setEnabled(false);
							gui.getSnapSimulation().setEnabled(false);
							gui.getSnapTest().setEnabled(false);
							gui.getQuickSendMenu().setEnabled(false);
							//main.checkRightsManagement();
						} else {
							// gui.getGenerateSending().setVisible(true);
							gui.getGenerateSending().setEnabled(true);
							// gui.getMenuNew().setEnabled(true);
							gui.getEvent().setEnabled(false);
							gui.getObject().setEnabled(false);
							gui.getObjectfolder().setEnabled(false);
							gui.getAlert().setEnabled(true);
							gui.getQuickSendMenu().setEnabled(true);
							gui.getSnapSending().setEnabled(true);
							gui.getSnapSimulation().setEnabled(true);
							gui.getSnapTest().setEnabled(true);
							main.checkRightsManagement(); // disabler snaps igjen
															// hvis bruker ikke har
															// rettigheter for
															// sending
						}
						// Her må jeg ha en if setning der jeg går gjennom alle
						// childs og ser om de har polygon
						EventVO eVO = (EventVO) o;
						
						// Denne er for å unngå dobbel lagring, mulig den kunne vært en bedre plass
						if(eVO.hasValidPk())
							eVO.setTempPk(null);
						
						boolean alertHasPolygon = false;
						boolean eventHasPolygons = false;
						Iterator it = eVO.getAlertListe().iterator();
						while (it.hasNext()) {
							AlertVO vo = (AlertVO)it.next();
							if ((vo).getM_shape() != null) {
								alertHasPolygon = true;
								//break;
							}
							if(vo.getShape() != null && vo.getShape().getClass().equals(PolygonStruct.class))
								eventHasPolygons = true;
							

						}
						if (alertHasPolygon) {
							gui.getGotoMap().setEnabled(true);
							gui.getGenerateSending().setEnabled(true);
							gui.getQuickSendMenu().setEnabled(true);
							gui.getExportPolygon().setEnabled(true);
						} else {
							gui.getGotoMap().setEnabled(false);
							gui.getGenerateSending().setEnabled(false);
							gui.getQuickSendMenu().setEnabled(false);
							gui.getExportPolygon().setEnabled(false);
						}
						gui.getTools().setEnabled(eventHasPolygons);
						
					} else if (o.getClass().equals(ObjectVO.class)) { // if
																		// user
																		// right
																		// clicks
																		// Object
						
						// gui.getGenerateSending().setVisible(false);
						gui.getGenerateSending().setEnabled(false);
						ObjectVO oVO = (ObjectVO) o;
												
						if(oVO.hasValidPk()) {
							// Denne er for å unngå dobbel lagring, mulig den kunne vært en bedre plass
							oVO.setTempPk(null);
							if (!oVO.isObjectFolder()) { // not folder, disable
															// all but new events.
								// gui.getMenuNew().setEnabled(true);
								gui.getEvent().setEnabled(true);
								gui.getObject().setEnabled(false);
								gui.getObjectfolder().setEnabled(false);
								gui.getAlert().setEnabled(false);
								if (oVO.getM_shape() != null)
									gui.getGotoMap().setEnabled(true);
								else
									gui.getGotoMap().setEnabled(false);
								main.checkRightsManagement();
							} else {
								// gui.getMenuNew().setEnabled(true);
								main.checkRightsManagement();
								gui.getEvent().setEnabled(false);
								gui.getObject().setEnabled(true);
								gui.getObjectfolder().setEnabled(true);
								gui.getAlert().setEnabled(false);
								if (oVO.getM_shape() != null)
									gui.getGotoMap().setEnabled(true);
								else
									gui.getGotoMap().setEnabled(false);
							}
						}
						else { // Invalid pk, lockdown
							gui.getEdit().setEnabled(false);
							gui.getDelete().setEnabled(false);
							gui.getNew().setEnabled(false);
							gui.getEvent().setEnabled(false);
							gui.getObject().setEnabled(false);
							gui.getObjectfolder().setEnabled(false);
							gui.getAlert().setEnabled(false);
							if (oVO.getM_shape() != null)
								gui.getGotoMap().setEnabled(true);
							else
								gui.getGotoMap().setEnabled(false);
							//main.checkRightsManagement();
						}
						gui.getSnapSending().setEnabled(false);
						gui.getQuickSendMenu().setEnabled(false);
						gui.getSnapSimulation().setEnabled(false);
						gui.getSnapTest().setEnabled(false);
						gui.getTools().setEnabled(false);
					} else if (o.getClass().equals(AlertVO.class)) { // if
																		// user
																		// right
																		// clicks
																		// Alert
						gui.getObject().setEnabled(false);
						gui.getObjectfolder().setEnabled(false);
						// gui.getGenerateSending().setVisible(true);
						// Denne er for å unngå dobbel lagring, mulig den kunne vært en bedre plass
						((AlertVO)o).setTempPk(null);
						
						
						if(!((ParmVO)o).hasValidPk()) {
							gui.getEdit().setEnabled(false);
							gui.getDelete().setEnabled(false);
							gui.getGenerateSending().setEnabled(false);
							gui.getQuickSendMenu().setEnabled(false);
							gui.getSnapSending().setEnabled(false);
							gui.getSnapTest().setEnabled(false);
							gui.getSnapSimulation().setEnabled(false);
							gui.getTools().setEnabled(false);
							if(((AlertVO) o).getM_shape().getType() == ShapeStruct.SHAPE_GISIMPORT) {
								gui.getGotoMap().setToolTipText(PAS.l("main_parmtab_popup_goto_unavailable_for_gis"));
								gui.getGotoMap().setEnabled(false);
								gui.getExportPolygon().setEnabled(false);
							}
						} 
						else {
							gui.getGenerateSending().setEnabled(true);
							AlertVO temp = (AlertVO) o;
							if (temp.getLBAAreaID() != null && temp.getLBAAreaID().equals("0")) // waiting for
																	// confirmation
																	// from
																	// CellVision
							{
								gui.getEdit().setEnabled(false);
								gui.getDelete().setEnabled(false);
								gui.getGenerateSending().setEnabled(false);
							}
							if (((AlertVO) o).getM_shape() != null && ((AlertVO) o).getM_shape().getType() != ShapeStruct.SHAPE_GISIMPORT) {
								gui.getGotoMap().setEnabled(true);
								if (((AlertVO) o).getM_shape().getType() == ShapeStruct.SHAPE_ELLIPSE
										|| ((AlertVO) o).getM_shape().getType() == ShapeStruct.SHAPE_POLYGON) {
									boolean benable = true;
									if(((AlertVO) o).getM_shape().getType() == ShapeStruct.SHAPE_POLYGON) {
										gui.getTools().setEnabled(true);
										gui.getExportPolygon().setEnabled(true);
									}
									else {
										gui.getTools().setEnabled(false);
										gui.getExportPolygon().setEnabled(false);
									}
									// if this alert has LBA activated, check if it
									// also has got a valid sz_areaid from
									// CellVision
									int adrtypes = ((AlertVO) o).getAddresstypes();
									if (((adrtypes & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT || (adrtypes & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
											&& !((AlertVO) o)
													.hasValidAreaIDFromCellVision()) {
										benable = false;
									}
									gui.getQuickSendMenu().setEnabled(benable);
									gui.getSnapSending().setEnabled(benable);
									gui.getSnapSimulation().setEnabled(benable);
									gui.getSnapTest().setEnabled(benable);
								}
							} else {
								gui.getGotoMap().setEnabled(false);
								if( ((AlertVO)o).getM_shape()!=null && ((AlertVO) o).getM_shape().getType() == ShapeStruct.SHAPE_GISIMPORT) {
									gui.getGotoMap().setToolTipText(PAS.l("main_parmtab_popup_goto_unavailable_for_gis"));
								}
								if(((AlertVO)o).getM_shape()!=null && ((AlertVO) o).getM_shape().getType() != ShapeStruct.SHAPE_POLYGON)
									gui.getExportPolygon().setEnabled(false);
								else
									gui.getExportPolygon().setEnabled(true);
							}
						
						main.checkRightsManagement(); // disable snap og
														// generate sending hvis
														// bruker ikke har
														// tilgang
						}
						gui.getMenuNew().setEnabled(false);
						gui.getNew().setEnabled(false);
						
					}
				} else { // if user right-clicks on nothing
					// gui.getGenerateSending().setVisible(false);
					main.checkRightsManagement();
					gui.getEdit().setEnabled(false);
					gui.getDelete().setEnabled(false);

					gui.getEvent().setEnabled(false);
					gui.getObject().setEnabled(true);
					gui.getObjectfolder().setEnabled(true);
					gui.getAlert().setEnabled(false);
					gui.getGotoMap().setEnabled(false);
					gui.getGenerateSending().setEnabled(false);
					gui.getQuickSendMenu().setEnabled(false);
					gui.getSnapSending().setEnabled(false);
					gui.getSnapSimulation().setEnabled(false);
					gui.getSnapTest().setEnabled(false);
					gui.getTools().setEnabled(false);
					System.out.println("if user right-clicks on nothing");
				}
				// gui.invalidate();
				// gui.validate();
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
			get_treegui().getTreeModel().nodeChanged(selectedNode);

			return selectedObject;
		} catch (ClassCastException ccEx) {
			System.out
					.println("Feil ved konvertering til ObjectVO. Object ikke et Object");
			ccEx.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),
					"TreeController ClassCastException in findObject", ccEx, 1);
		}
		return selectedObject;
	}

	public TreeGUI getGui() {
		return gui;
	}

	public MouseListener getMuseLytter() {
		return museLytter;
	}

	public TreePath getSelPath() {
		return selPath;
	}

	public ObjectVO getObject() {
		return object;
	}

	public void setObject(ObjectVO object) {
		this.object = object;
	}

	public void setSelPath(TreePath path) {
		selPath = path;
	}
}
