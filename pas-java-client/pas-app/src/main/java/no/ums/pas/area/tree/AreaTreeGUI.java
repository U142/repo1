package no.ums.pas.area.tree;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.area.main.MainAreaController;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;

/**
 * @author sachinn
 */
public class AreaTreeGUI extends DefaultPanel implements ComponentListener {
	public static final long serialVersionUID = 1;
    private static final Log log = UmsLog.getLogger(AreaTreeGUI.class);

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
				if(tip.getClass().equals(AreaVO.class))
				{
					AreaVO avo = (AreaVO)tip;
                    String tiptext = "<html>" + Localization.l("mainmenu_libraries_predefined_areas") + " = " + avo.getAlertpk().substring(1);
//					tiptext += avo.getDescription();
					tiptext += "</html>";
					return tiptext;
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
			this.setRowHeight(24);
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
			log.debug("dropActionChanged");

		}
		public void dragExit(DropTargetEvent arg0) {
			log.debug("dragExit");
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
		      		//**check logic is temorarily removed
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
			    	  //**check temp removed
			      }
		      } catch(Exception e) {
		      	dtde.rejectDrop();
		      }
		    } catch (Exception e) {
		      log.warn(e.getMessage(), e);
		      dtde.rejectDrop();
		    }
		    dtde.getDropTargetContext().getComponent().setCursor(Cursor.getDefaultCursor());
		    dtde.dropComplete(true);
			
		}
		public String getFolderPk(Object o) {
			if(o.getClass().equals(AreaVO.class)) {
				return ((AreaVO)o).getAlertpk();
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

	private JMenuItem optionNew;

	private JMenuItem edit;

	private JMenuItem delete;

	private JScrollPane view;
	
	private JMenuItem gotomap;
	
	private JMenuItem export_polygon;
	
	private MainAreaController main;
	

	
	public JTree getTree() {
		return tree;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public JMenuItem getOptionNew() {
		return optionNew;
	}
	
	public JMenuItem getDelete() {
		return delete;
	}
	
	public JMenuItem getGotoMap() {
		return gotomap;
	}
	
	public JPopupMenu getPopup() {
		return popup;
	}
	
	public JScrollPane getView() {
		return view;
	}

	public DefaultMutableTreeNode getRootnode() {
		return rootnode;
	}

	public JMenuItem getEdit() {
		return edit;
	}
	public JMenuItem getExportPolygon() {
		return export_polygon;
	}
	
	public AreaTreeGUI(MainAreaController main) {
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
		tree.setRowHeight(32);
		
		
		tree.setCellRenderer(new AreaCustomRenderer(tree));
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
//					add(loader, m_gridconst);
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
        optionNew = new JMenuItem(Localization.l("common_new"));
        gotomap = new JMenuItem(Localization.l("main_parmtab_popup_goto"));

        export_polygon = new JMenuItem(Localization.l("main_parmtab_popup_export_polygon"));

		popup.add(optionNew);
		popup.addSeparator();
        edit = new JMenuItem(Localization.l("common_edit"));

		popup.add(edit);
        delete = new JMenuItem(Localization.l("common_delete"));
		popup.add(delete);
		popup.add(gotomap);
		
		popup.addSeparator();
		popup.add(export_polygon);
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
