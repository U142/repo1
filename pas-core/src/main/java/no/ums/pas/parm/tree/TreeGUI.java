package no.ums.pas.parm.tree;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class TreeGUI extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;

	// private Container c;

	
	private DefaultTreeModel treeModel;

	private DefaultMutableTreeNode rootnode;

	protected Tree tree;
	
	int preferredwidth = 200;
	Dimension preferred = new Dimension(200, 200);
	public void setWidth(int w)
	{
		preferredwidth = w;
		preferred.width = w;
	}
	
	//return the preferred width and the dynamic height
	@Override
	public Dimension getPreferredSize()
	{
		
		return new Dimension(preferredwidth, super.getPreferredSize().height);
	}
	
	
	public class Tree extends JTree implements DragSourceListener, DropTargetListener, DragGestureListener, ComponentListener, TreeSelectionListener {
		public static final long serialVersionUID = 1;

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(preferredwidth, super.getPreferredSize().height);
		}
		@Override
		public Dimension getMinimumSize()
		{
			return new Dimension(preferredwidth, super.getMinimumSize().height);
		}
		
		@Override
		public String getToolTipText(java.awt.event.MouseEvent e)
		{
			Object tip = null;
			TreePath path = getPathForLocation(e.getX(), e.getY());
			if(path!=null)
			{
				tip = path.getLastPathComponent();
			}
			if(tip!=null)
			{
				tip = ((DefaultMutableTreeNode)tip).getUserObject();
				if(tip.getClass().equals(AlertVO.class))
				{
					AlertVO avo = (AlertVO)tip;
					String tiptext = "<html>" + PAS.l("main_parmtab_popup_alert") + " = " + avo.getAlertpk().substring(1);
					if(SendController.HasType(avo.getAddresstypes(), SendController.SENDTO_CELL_BROADCAST_TEXT))
					{
						tiptext += "<br><br><table>";
						tiptext += "<th colspan=2 width=200>" + PAS.l("main_status_locationbased_alert") + "</th>";
						for(int i=0; i < avo.getOperators().size(); i++)
						{
							tiptext +="<tr><td>";
							tiptext += avo.getOperators().get(i).sz_operatorname + "";
							tiptext += "</td><td>";
							tiptext += AlertVO.LBAStatusToString(avo.getOperators().get(i).l_status);
							tiptext += "</td>";
						}
						tiptext += "</table>";
					}
					tiptext += "</html>";
					return tiptext;
				}
				else if(tip.getClass().equals(EventVO.class))
				{
					EventVO evo = (EventVO)tip;
					return PAS.l("main_parmtab_popup_event") + " = " + evo.getEventPk().substring(1);
				}
				else if(tip.getClass().equals(ObjectVO.class))
				{
					ObjectVO ovo = (ObjectVO)tip;
					return (ovo.isObjectFolder() ? PAS.l("main_parmtab_popup_objectfolder") : PAS.l("main_parmtab_popup_object")) + " = " + ovo.getObjectPK().substring(1);
				}
			}
			return "";
		}
	
		DragSource m_source;
		DropTarget m_droptarget;
		DragGestureRecognizer m_recognizer;
		DefaultMutableTreeNode m_oldnode;
		TreeNode m_targetnode;
		DataFlavor m_source_flavor;
		Transferable transferable;
		
		public Tree(DefaultTreeModel model) {
			super(model);
			m_source = new DragSource();
			m_recognizer = m_source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
			m_droptarget = new DropTarget (this, this);
		}
		public void dragGestureRecognized(DragGestureEvent e) {			
			m_oldnode = get_selectednode();
			if(m_oldnode==null)
				return;
			transferable = (Transferable) m_oldnode.getUserObject();
			e.startDrag(DragSource.DefaultMoveDrop, transferable, this);
		}
		public DefaultMutableTreeNode get_selectednode() {
			TreePath path = this.getSelectionPath();
			if(path == null || path.getPathCount() <= 1)
				return null;
			return (DefaultMutableTreeNode)path.getLastPathComponent();
		}
		public void dragDropEnd(DragSourceDropEvent e) {
			if(e.getDropSuccess()) {
			}
			e.getDragSourceContext().setCursor(Cursor.getDefaultCursor());
			e.getDragSourceContext().getComponent().setCursor(Cursor.getDefaultCursor());
			
		    Point pt = e.getLocation();
		    DragSourceContext dtc = e.getDragSourceContext();
		    JTree tree = (JTree) dtc.getComponent();
		    TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
		    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
		    setCellHighlight(parent.getUserObject(), false);
		}
		public void dragEnter(DragSourceDragEvent e) {
		      int action = e.getDropAction();
		      if((action & DnDConstants.ACTION_MOVE)!=0) {
		        e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		      }
		      else 
		      	e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);

		}
		public void dragExit(DragSourceEvent e) {
			e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
		}
		Object highlighted = null;
		public void resetCellHighlight() {
			if(highlighted!=null) {
				setCellHighlight(highlighted, false);
				highlighted = null;
			}
		}
		public void setCellHighlight(Object o, boolean b) {
			ParmVO vo = (ParmVO)o;
			vo.setDragOver(b);
			highlighted = o;
			repaint();
		}
		public void dragOver(DragSourceDragEvent e) {
		      int action = e.getDropAction();
		      if((action & DnDConstants.ACTION_MOVE)!=0)
		        e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		      else 
		      	e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
		}
		public void dropActionChanged(DragSourceDragEvent e) {
			System.out.println("dropActionChanged");

		}
		public void dragExit(DropTargetEvent arg0) {
			System.out.println("dragExit");
		}
		public void dragOver(DropTargetDragEvent dtde) {
		    Point pt = dtde.getLocation();
		    //DropTargetContext dtc = dtde.getDropTargetContext();
		    DropTargetContext dtc = dtde.getDropTargetContext();
		    JTree tree = (JTree) dtc.getComponent();
		    TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
		    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
		      Transferable tr = dtde.getTransferable();
		      DataFlavor[] flavors = tr.getTransferDataFlavors();

		      try {
		      	for (int i = 0; i < flavors.length; i++) {
				    if(tr.getTransferData(flavors[i]).getClass().equals(AlertVO.class)) {
				    	if(parent.getUserObject().getClass().equals(EventVO.class)) {
				    		EventVO dest = (EventVO)parent.getUserObject();
				    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveDrop);
						  	dtde.acceptDrag(DnDConstants.ACTION_MOVE);
				    		resetCellHighlight();
				    		setCellHighlight(dest, true);
				    	}
				    	else {
				    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveNoDrop);
				    		dtde.rejectDrag();
				    	}
				    }
				    else if(tr.getTransferData(flavors[i]).getClass().equals(EventVO.class)) {
				    	if(parent.getUserObject().getClass().equals(ObjectVO.class)) {
				    		ObjectVO dest = (ObjectVO)parent.getUserObject();
				    		//only accept events on objects, not folders
				    		if(!dest.isObjectFolder()) {
					    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveDrop);
							  	dtde.acceptDrag(DnDConstants.ACTION_MOVE);
					    		resetCellHighlight();
					    		setCellHighlight(dest, true);
				    		}
				    		else {
					    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveNoDrop);
					    		dtde.rejectDrag();
					    		resetCellHighlight();
					    		setCellHighlight(dest, false);
				    		}
				    	}
				    	else {
				    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveNoDrop);
				    		dtde.rejectDrag();
				    	}
				    }
				    else if(tr.getTransferData(flavors[i]).getClass().equals(ObjectVO.class)) {
				    	ObjectVO dragged = (ObjectVO)tr.getTransferData(flavors[i]);
				    	if(parent.getUserObject().getClass().equals(ObjectVO.class)) {
				    		ObjectVO dest = (ObjectVO)parent.getUserObject();
				    		//only accept objectfolder child of objectfolder or object child of objectfolder
				    		if((dragged.isObjectFolder() && dest.isObjectFolder()) || (!dragged.isObjectFolder() && dest.isObjectFolder())) {
					    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveDrop);
					    		dtde.acceptDrag(DnDConstants.ACTION_MOVE);
					    		resetCellHighlight();
					    		setCellHighlight(dest, true);
				    		} else {
					    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveNoDrop);
					    		dtde.rejectDrag();	
					    		resetCellHighlight();
					    		setCellHighlight(dest, false);
				    		}
				    	}
				    	else  {
				    		dtde.getDropTargetContext().getComponent().setCursor(DragSource.DefaultMoveNoDrop);
				    		dtde.rejectDrag();
				    	}
				    }
		      	}
		      }
			   catch(Exception err) {
			   }
		}
//		  private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
//		    Point p = dtde.getLocation();
//		    DropTargetContext dtc = dtde.getDropTargetContext();
//		    JTree tree = (JTree) dtc.getComponent();
//		    TreePath path = tree.getClosestPathForLocation(p.x, p.y);
//		    return (TreeNode) path.getLastPathComponent();
//		  }
		  public void dragEnter(DropTargetDragEvent dtde) {

		  }

		  public void drop(DropTargetDropEvent dtde) {
			dtde.acceptDrop (DnDConstants.ACTION_MOVE);
		    Point pt = dtde.getLocation();
		    DropTargetContext dtc = dtde.getDropTargetContext();
		    JTree tree = (JTree) dtc.getComponent();
		    TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
		    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
		    try {
		      Transferable tr = dtde.getTransferable();
		      DataFlavor[] flavors = tr.getTransferDataFlavors();
		      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		      String sz_newParentPk = getFolderPk(parent.getUserObject());
		      try {
			      for (int i = 0; i < flavors.length; i++) {
				      if(dtde.getTransferable().getTransferData(flavors[i]).getClass().equals(AlertVO.class)) {
				      	try {
				      		AlertVO dragged = ((AlertVO)tr.getTransferData(flavors[i]));
				      		if(!dragged.getAlertpk().startsWith("a"))
				      		{
			      				JOptionPane.showInternalMessageDialog(this, "This alert must be inserted by server before modifying it's location", "Can not move alert", JOptionPane.INFORMATION_MESSAGE);
			      				return;
				      		}
				      		EventVO dep = (EventVO)PAS.get_pas().get_parmcontroller().findNodeByPk(dragged.getParent()).getUserObject();
				      		if(parent.getUserObject().getClass().equals(EventVO.class)) {	
				      			EventVO dest = (EventVO)parent.getUserObject();
				      			//dest.getAlertListe().remove(dragged);
				      			if(dragged.getLocked()>=1) {
				      				JOptionPane.showInternalMessageDialog(this, "You need to unlock this alert before it can be moved.", "Alert is locked for external execution", JOptionPane.INFORMATION_MESSAGE);
				      				resetCellHighlight();
				      				setCellHighlight(dest, false);
				      				dtde.rejectDrop();
				      				return;
				      			}
				      			DefaultMutableTreeNode node = dragged.getPath();
						      	model.removeNodeFromParent(node);
						      	model.insertNodeInto(node, parent, 0);
						      	dragged.setParent(sz_newParentPk);
						      	main.saveChanges(dragged);
						      	dep.removeAlerts(dragged);
						      	dest.addAlerts(dragged);
						      	System.out.println("Moved to parent: " + parent.getUserObject().getClass());
				      			resetCellHighlight();
				      			setCellHighlight(dest, false);
				      		}
				      		else {
				      			dtde.rejectDrop();
				      		}

				      	} catch(Exception e) {
				      		//Error.getError().addError("Error in tree", "Could not move", e, Error.SEVERITY_ERROR);		
				      	}
				      }
				      else if(dtde.getTransferable().getTransferData(flavors[i]).getClass().equals(EventVO.class)) {
					    try {
					    	EventVO dragged = ((EventVO)tr.getTransferData(flavors[i]));
					    	if(parent.getUserObject().getClass().equals(ObjectVO.class)) {
					    		ObjectVO dest = (ObjectVO)parent.getUserObject();
					    		ObjectVO dep = (ObjectVO)PAS.get_pas().get_parmcontroller().findNodeByPk(dragged.getParentpk()).getUserObject();
					    		//only accept event drop on objects, not objectfolders
					    		if(!dest.isObjectFolder()) {
						      		DefaultMutableTreeNode node = dragged.getPath();
							      	model.removeNodeFromParent(node);
							      	model.insertNodeInto(node, parent, 0);
							      	dragged.setParentpk(sz_newParentPk);
							      	main.saveChanges(dragged);
							      	dest.getList().add(dragged);
							      	dep.getList().remove(dragged);
							      	System.out.println("Moved to parent: " + parent.getUserObject().getClass());
					    		}
					    		else {
					    			dtde.rejectDrop();
					    		}
				      			resetCellHighlight();
				      			setCellHighlight(dest, false);

					    	}
				      		else {
				      			dtde.rejectDrop();
				      			//return;
				      		}
					    
				      	} catch(Exception e) {
				      		//Error.getError().addError("Error in tree", "Could not move", e, Error.SEVERITY_ERROR);
				      	}
				      }
				      else if(dtde.getTransferable().getTransferData(flavors[i]).getClass().equals(ObjectVO.class)) {
				      	try {
				      		ObjectVO dragged = (ObjectVO)tr.getTransferData(flavors[i]);
				      		
				      		if(parent.getUserObject().getClass().equals(ObjectVO.class)) {
				      			ObjectVO dest = (ObjectVO)parent.getUserObject();
				      			ObjectVO dep = (ObjectVO)PAS.get_pas().get_parmcontroller().findNodeByPk(dragged.getParent()).getUserObject();
				      			
				      			if(!dragged.equals(dest) && ((dragged.isObjectFolder() && dest.isObjectFolder()) || (!dragged.isObjectFolder() && dest.isObjectFolder()))) {
							      	DefaultMutableTreeNode node = dragged.getPath();
							      	model.removeNodeFromParent(node);
							      	model.insertNodeInto(node, parent, 0);
							      	dragged.setParent(sz_newParentPk);
							      	main.saveChanges(dragged);
							      	dest.getList().add(dragged);
							      	dep.getList().remove(dragged);
							      	System.out.println("Moved to parent: " + parent.getUserObject().getClass());
				      			} else
				      				dtde.rejectDrop();
				      			resetCellHighlight();
				      			setCellHighlight(dest, false);
				      		}
				      		else {
				      			dtde.rejectDrop();
				      			//return;
				      		}
				      	} catch(Exception e) {
				      		//Error.getError().addError("Error in tree", "Could not move", e, Error.SEVERITY_ERROR);
				      	}
				      }
			      }
		      } catch(UnsupportedFlavorException e) {
		      	dtde.rejectDrop();
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		      dtde.rejectDrop();
		    }
		    dtde.getDropTargetContext().getComponent().setCursor(Cursor.getDefaultCursor());
		    dtde.dropComplete(true);
			
		}
		public String getFolderPk(Object o) {
			if(o.getClass().equals(ObjectVO.class)) {
				return ((ObjectVO)o).getObjectPK();
			}
			else if(o.getClass().equals(EventVO.class)) {
				return ((EventVO)o).getEventPk();
			}
			else if(o.getClass().equals(AlertVO.class)) {
				return ((AlertVO)o).getAlertpk();
			}
			else return "o-1";
		}
		public void dropActionChanged(DropTargetDragEvent arg0) {
		
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
		public void valueChanged(TreeSelectionEvent e) {
			//ParmVO vo = (ParmVO)this.get_selectednode().getUserObject();
			//if(vo.getPk() != PAS.get_pas().get_parmcontroller().get_parmpanel()
		}
	}

	private JPopupMenu popup;

	private JMenu menuNew;

	private JMenuItem objectfolder;

	private JMenuItem object;
	
	private JMenuItem event;

	private JMenuItem alert;

	private JMenuItem edit;

	private JMenuItem delete;

	private JMenu submenu; // Quicksendmenu
	
	private JScrollPane view;
	
	private JMenuItem gotomap;
	
	private JMenuItem generateSending;
	
	private JMenuItem snapshot_livesending;
	private JMenuItem snapshot_simulation;
	private JMenuItem snapshot_test;
	
	private JMenu tools;
	private JMenuItem export_polygon;
	
	private MainController main;
	

	
	public JTree getTree() {
		return tree;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public JMenu getMenuNew() {
		return menuNew;
	}
	
	public JMenuItem getEvent(){
		return event;
	}

	public JMenuItem getObject() {
		return object;
	}

	public JMenuItem getObjectfolder() {
		return objectfolder;
	}

	public JMenuItem getDelete() {
		return delete;
	}
	
	public JMenuItem getGotoMap() {
		return gotomap;
	}
	
	public JMenuItem getGenerateSending() {
		return generateSending;
	}
	public JMenuItem getSnapSending() {
		return snapshot_livesending;
	}
	public JMenuItem getSnapSimulation() {
		return snapshot_simulation;
	}
	public JMenuItem getSnapTest() {
		return snapshot_test;
	}

	public JPopupMenu getPopup() {
		return popup;
	}

	public JMenuItem getNew() {
		return menuNew;
	}
	
	public JScrollPane getView() {
		return view;
	}

	public JMenuItem getAlert() {
		return alert;
	}

	public DefaultMutableTreeNode getRootnode() {
		return rootnode;
	}

	public JMenuItem getEdit() {
		return edit;
	}
	public JMenu getTools() {
		return tools;
	}
	public JMenuItem getExportPolygon() {
		return export_polygon;
	}
	
	public JMenu getQuickSendMenu() {
		return submenu;
	}
	
	public TreeGUI(MainController main) {
		super();
		this.main = main;
				m_gridconst.fill = GridBagConstraints.BOTH;
				setLayout(new FlowLayout(FlowLayout.CENTER));
				createPopupMenu();
				createTree();
		addComponentListener(this);
	}

	public void createTree() {
		rootnode = new DefaultMutableTreeNode("Rotnode");
		treeModel = new DefaultTreeModel(rootnode);
		
//		tree = new Tree(treeModel){
//            public void paintComponent(Graphics g)
//            {
//            	//ImageIcon icon = ImageLoader.load_icon("C:\\Program Files\\UMS Population Alert System\\PARM\\testbg\\parm_menu_bg.gif");
//            	ImageIcon icon = ImageLoader.load_icon("cat1.gif");
//            	//  Scale image to size of component
//                Dimension d = getSize();
//                // Her må jeg bare ha en gjennomsiktig gif
//                g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
//                setOpaque( false );
//                super.paintComponent(g);
//            }
//        };
		
		tree = new Tree(treeModel);
		tree.setRowHeight(0);
		
		
		tree.setCellRenderer(new CustomRenderer(tree));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);

		lbl_initializing = new StdTextLabel("", true, 250);
		set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
		lbl_initializing.setPreferredSize(new Dimension(100, 50));
		lbl_initializing.setIcon(ImageLoader.load_icon("bigrotation2.gif"));
		//add(Box.createVerticalGlue());
		//add(lbl_initializing);
		
		loader = new LoadingPanel("0%", new Dimension(getWidth(), 20), 0, false);
		try
		{
			loader.get_progress().setBorder(null);
			loader.get_progress().setOpaque(false);
			Color c = loader.get_progress().getBackground();
			Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
			loader.get_progress().setBackground(c2);
			loader.get_progress().setBorderPainted(false);
			loader.get_progress().setCursor(new Cursor(Cursor.WAIT_CURSOR));
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					loader.setPreferredSize(new Dimension(getWidth(), 40));
				}
			});
		}
		catch(Exception e)
		{
			
		}
		//loader.setForeground(c2);
		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					m_gridconst.ipadx = 0; m_gridconst.ipady = 0; m_gridconst.fill = GridBagConstraints.BOTH;
					add(loader, m_gridconst);
					set_gridconst(0, 0, 1, 1, GridBagConstraints.NORTH);
					m_gridconst.anchor = GridBagConstraints.NORTHWEST;
					add(tree);
				}
			});
		//add(Box.createHorizontalGlue());
		//add(Box.createVerticalGlue());
		
	
	
	}
	public StdTextLabel lbl_initializing;
	public LoadingPanel loader;

	public void createPopupMenu() {

		popup = new JPopupMenu("PopupMenu");
		menuNew = new JMenu(PAS.l("common_new"));
		objectfolder = new JMenuItem(PAS.l("main_parmtab_popup_objectfolder"));
		object = new JMenuItem(PAS.l("main_parmtab_popup_object"));
		alert = new JMenuItem(PAS.l("main_parmtab_popup_alert"));
		event = new JMenuItem(PAS.l("main_parmtab_popup_event"));
		gotomap = new JMenuItem(PAS.l("main_parmtab_popup_goto"));
		generateSending = new JMenuItem(PAS.l("main_parmtab_popup_generate_sending"));
		snapshot_livesending = new JMenuItem(PAS.l("main_parmtab_popup_quicksend_send"));
		snapshot_simulation = new JMenuItem(PAS.l("main_parmtab_popup_quicksend_simulate"));
		snapshot_test = new JMenuItem(PAS.l("main_parmtab_popup_quicksend_test"));
		
		tools = new JMenu(PAS.l("main_parmtab_popup_tools"));
		export_polygon = new JMenuItem(PAS.l("main_parmtab_popup_export_polygon"));

		menuNew.add(objectfolder);
		menuNew.add(object);
		menuNew.add(event);
		menuNew.add(alert);
		
		//menuNew.add(gotomap);

		popup.add(menuNew);
		popup.addSeparator();
		edit = new JMenuItem(PAS.l("common_edit"));

		popup.add(edit);
		delete = new JMenuItem(PAS.l("common_delete"));
		popup.add(delete);
		popup.add(gotomap);
		popup.add(generateSending);
		submenu = new JMenu(PAS.l("main_parmtab_popup_quicksend"));
		submenu.add(snapshot_livesending);
		submenu.add(snapshot_simulation);
		submenu.add(snapshot_test);
		submenu.setEnabled(true);
		
		popup.add(submenu);
		popup.add(tools);
		tools.add(export_polygon);
		tools.setEnabled(false);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		/*tree.setSize(new Dimension(getWidth(), getHeight()));
		tree.setPreferredSize(new Dimension(getWidth(), getHeight()));
		tree.invalidate();*/
		//tree.setPreferredSize(new Dimension(getWidth(), getMinimumSize().height));
		//tree.setPreferredSize(new Dimension(getWidth(), tree.getMinimumSize().height));
		//tree.revalidate();
		loader.setPreferredSize(new Dimension(getWidth(), 30));
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void add_controls() {
		
	}

	@Override
	public void init() {
		
	}

}
