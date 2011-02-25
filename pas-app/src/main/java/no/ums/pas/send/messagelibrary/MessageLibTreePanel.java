package no.ums.pas.send.messagelibrary;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.*;
import no.ums.pas.core.defines.tree.UMSTree.TREEICONSIZE;
import no.ums.pas.core.defines.tree.UMSTree.TREEMODE;
import no.ums.pas.core.ws.WSMessageLib;
import no.ums.pas.core.ws.WSMessageLibDelete;
import no.ums.pas.send.messagelibrary.tree.MessageLibNode;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdSearchArea;
import no.ums.ws.pas.UBBMESSAGE;
import no.ums.ws.pas.UBBMESSAGELIST;
import no.ums.ws.pas.UBBMODULEDEF;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
//import Send.MessageLibrary.MessageLibPanel.MessageLibTree.MessageLibNode;

public class MessageLibTreePanel extends DefaultPanel
{
	public static final int MESSAGELIB_UPDATE_INTERVAL = 10;
	
	TREEMODE mode;
	MsgLibLoader loader = new MsgLibLoader();
	MessageLibTree tree;
	public MessageLibTree getTree() { return tree; }
	public MsgLibLoader getLoader() { return loader; }
	public JScrollPane getScrollPane() { return pane; }
	JScrollPane pane;
	ActionListener callback;
	JLabel lbl_loader = new JLabel(no.ums.pas.ums.tools.ImageLoader.load_icon("refresh_64.png"));
	
	public MessageLibTreePanel(ActionListener callback, int UPDATE_INTERVAL, boolean b_editormode)
	{
		this(callback, UMSTree.TREEMODE.EDITOR, UPDATE_INTERVAL, b_editormode);
	}
	
	public MessageLibTreePanel(ActionListener callback, UMSTree.TREEMODE mode, int UPDATE_INTERVAL, boolean b_editormode)
	{
		super();
		this.callback = callback;
		//tree.setPreferredSize(new Dimension(200, 200));
		tree = new MessageLibTree(this, mode, UPDATE_INTERVAL);
		tree.setIconSize(TREEICONSIZE.MEDIUM);
		this.mode = mode;
		//tree.setBorder(UMS.Tools.TextFormat.CreateStdBorder("Message library"));
		pane = new JScrollPane(tree);
		tree.setRowHeight(tree.getIconSize());
		pane.setOpaque(false);
		//pane.setBackground(new Color(0,0,0,0));
		//pane.setBorder(null);
		
		//this.setBackground(new Color(255,0,0,127));
		this.setOpaque(false);
		lbl_loader.setVisible(false);
		//MessageLibDlg.this.add(pane);


		//tree.setCellRenderer(cr);
		tree.setRootVisible(false);
		tree.setLargeModel(false);
		tree.setShowsRootHandles(true);
		tree.setBorder(null);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		add_controls();
		addComponentListener(this);
		setVisible(true);
		//loader.Download();
	}
	
	
	@Override
	public void add_controls() {
		set_gridconst(0, 0, 1, 1);
		add(pane, m_gridconst);
		set_gridconst(0,0,1,1);
		add(lbl_loader, m_gridconst);
	}

	@Override
	public void init() {
		
	}
	/** Add new elements directly to hashtable
	 * 
	 */
	public void AddToHash(MessageLibNode node)
	{
		try
		{
			loader.hash_messages.put(node.getMessage().getNMessagepk(), node);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void RemoveFromTree(MessageLibNode n)
	{
		try
		{
			tree.model.removeNodeFromParent(n);
			callback.actionPerformed(new ActionEvent(n.getMessage(), ActionEvent.ACTION_PERFORMED, UMSTree.TREE_TREENODE_DELETED));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void RemoveFromHash(UBBMESSAGE msg)
	{
		//find node
		try
		{
			MessageLibNode node = loader.hash_messages.get(msg.getNMessagepk());
			if(node!=null)
			{
				tree.model.removeNodeFromParent(node);
				loader.hash_messages.remove(msg.getNMessagepk());
				callback.actionPerformed(new ActionEvent(msg, ActionEvent.ACTION_PERFORMED, UMSTree.TREE_TREENODE_DELETED));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(TreeUpdater.LOADING_START.equals(e.getActionCommand()))
		{
			if(loader.n_server_timestamp<=0)
			{
				pane.setVisible(false);
				lbl_loader.setVisible(true);
			}
			loader.Download();
		}
		else if(TreeUpdater.LOADING_FINISHED.equals(e.getActionCommand()))
		{
			lbl_loader.setVisible(false);
			pane.setVisible(true);
			if(loader.isFirst())
			{
				loader.setFirstLoadDone();
				try
				{
					tree.setExplodedNodes(PAS.get_pas().get_settings().getMessageLibExplodedNodes());
				}
				catch(Exception err)
				{
					
				}
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		tree.stopUpdater();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		//tree.setPreferredSize(new Dimension(getWidth()-10, getHeight()-10));
		int w = getWidth();
		int h = getHeight();
		System.out.println(w + " " + h);
		pane.setPreferredSize(new Dimension(w, h));
		tree.getTreeRenderer().setTotalWidth(getWidth());
		this.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		tree.startUpdater(true);

	}

	
	
	public class MessageLibTree extends UMSTree
	{
		private static final long serialVersionUID = 1L;

		public void setExplodedNodes(List<Object> pks)
		{
			if(pks==null)
				return;
			Iterator<Object> it = pks.iterator();
			while(it.hasNext())
			{
				Object pk = (Object)it.next();
				Long l_pk = Long.parseLong(pk.toString()); //new Long(-1);
				/*if(pk.getClass().equals(Long.class))
					l_pk = (Long)pk;
				else if(pk.getClass().equals(String.class))
					l_pk = Long.parseLong(pk.toString());*/
				
				try
				{
					//this.expandPath(getPath(loader.hash_messages.get(pk)));
					UMSTreeNode node = loader.hash_messages.get(new Long(l_pk));
					final TreePath path = getPath(node);
					if(path==null)
						continue;
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							expandPath(path);
						}
					});
				}
				catch(Exception e)
				{
					//System.out.println("Could not expand treepath for pk=" + pk);
				}
			}
		}
		public MessageLibNode newNode(UBBMESSAGE m)
		{
			return new MessageLibNode(m, model);
		}
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(getSelectedNode()==null)
			{
				//empty node, disable delete in popup
				menu_item_delete.setEnabled(false);
			}
			else
			{
				menu_item_delete.setEnabled(true);
			}
			super.mouseReleased(e);
		}
		
		@Override
		public void InitRenderer() {
			final String [] cols = new String [] { "", "" };
			final int [] width = new int [] { 20, 250 };
			final boolean [] b_editable = new boolean [] { false, false };
			TreeTable table = new TreeTable(cols, width, b_editable, new Dimension(300, 100))
			{
				@Override
				public void set_custom_cellrenderer(TableColumn column, final int n_col) {
					column.setCellRenderer(new DefaultTableCellRenderer() {
						public static final long serialVersionUID = 1;
					    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					        Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
				        	renderer.setForeground(Color.black);
					        return renderer;
					    }
					});
				}
			};
			cr = new TreeRenderer(this, table) 
			{
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus){
					
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					Object o = node.getUserObject();
					if(o==null)// && node.equals(MessageLibDlg.this.tree.top))
					{
						this.setText("Root");
						return this;
						
					}
					MessageLibNode msgnode;
					try
					{
						msgnode = (MessageLibNode)node;
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						return this;
					}
					//setBackground(new Color(0,0,0,0));
					int w = totalwidth; //getWidth();
					this.setPreferredSize(new Dimension(100, 30));
					UBBMESSAGE msg = (UBBMESSAGE)o;

					if(!leaf)
					{
						this.setIcon(ImageLoader.load_icon("folder2_" + getIconSize() + ".png"));
					}
					else if(msgnode.getMessage().getFTemplate()==1)
					{
						//this.setIcon(ImageLoader.load_icon("mobile_" + getIconSize() + ".png"));
						this.setIcon(ImageLoader.load_icon("file_" + getIconSize() + ".png"));
					}
					else
					{
						//this.setIcon(ImageLoader.load_icon("phone_" + getIconSize() + ".png"));
						this.setIcon(ImageLoader.load_icon("speaker3d_" + getIconSize() + ".png"));
					}
					this.setText(msg.getSzName());
					setOpaque(true);
					if(sel)
					{
						this.setBackground(SystemColor.textHighlight);
						this.setForeground(SystemColor.textHighlightText);
					}
					else
					{
						this.setBackground(SystemColor.control);
						this.setForeground(SystemColor.textText);
					}
					if(isFilterActive())
					{
						if(msgnode.isFoundInSearch())
						{
							Color c1 = SubstanceLookAndFeel.getActiveColorScheme().getExtraLightColor();
							this.setForeground(Color.black);
							this.setText("*" + this.getText() + "*");
						}
						else
						{
							this.setForeground(new Color(0,0,0,70));
						}
					}

					setPreferredSize(new Dimension(800, 40));
					if(msg!=null && msg.getSzMessage()!=null)
					{
						if(msg.getSzMessage().trim().length() == 0)
							this.setForeground(Color.red);
					}
					//return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					return this;
				}
				@Override
				protected void initTable(TreeTable table)
				{
					String [] data = new String [] { " ", " ", };
					table.m_tbl.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
					table.insert_row(data, 0);
					table.m_tbl.setBackground(new Color(0,0,0,0));
					table.m_tbl.setOpaque(false);
					table.m_tbl.getTableHeader().setBackground(new Color(0,0,0,0));
					table.m_tbl.getTableHeader().setResizingAllowed(true);
					table.m_tbl.getTableHeader().setOpaque(false);
					table.m_tbl.setIntercellSpacing(new Dimension(0,0));
					table.m_tbl.setShowGrid(false);
					table.revalidate();
					super.initTable(table);
				}
				@Override
				public void setPreferredSize(Dimension d)
				{
					TableColumn col1 = m_tbl_list.m_tbl.getColumnModel().getColumn(0);
					TableColumn col2 = m_tbl_list.m_tbl.getColumnModel().getColumn(1);
					col1.setWidth(20);
					col2.setWidth(400);
					int w = totalwidth;//getWidth();
					col1.setResizable(false);
					col2.setResizable(false);
					//super.setPreferredSize(d);
				}
			};

		}

		@Override
		public void setSelectedNode(UMSTreeNode n)
		{
			super.setSelectedNode(n);
			if(n==null)
			{
				this.setSelectionPath(null);
				MessageLibTreePanel.this.callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, UMSTree.TREE_SELECTION_NULL));
			}
			
		}
		@Override 
		public MessageLibNode getSelectedNode() { return (MessageLibNode)selected_node; }
		@Override
		public void actionPerformed(ActionEvent e) {
			if("act_new_txt_template".equals(e.getActionCommand()))
			{
				UBBMESSAGE newmsg = new UBBMESSAGE();
				final MessageLibNode newnode = new MessageLibNode(newmsg, model);
				//List<UBBMESSAGE> listmsg = new ArrayList<UBBMESSAGE>();
				//listmsg.add(newmsg);
				newnode.setIsSaved(false);
				newmsg.setSzName("New Entry");
				newmsg.setFTemplate(1);
				newmsg.setNLangpk(-1);
				newmsg.setSzDescription("");
				newmsg.setSzMessage("");
				newmsg.setNMessagepk(-1);
				newmsg.setNLangpk(1);
				newmsg.setNType(UBBMODULEDEF.DIALOGUE);
				newmsg.setNDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
				if(getSelectedNode()!=null)
				{
					newmsg.setNParentpk(getSelectedNode().getMessage().getNMessagepk());
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							getSelectedNode().add(newnode);
							model.nodeStructureChanged(getSelectedNode());
							//setEditable(true);
							openFolder(getSelectedNode());
							openFolder(newnode);
							setSelectedNode(newnode);
							//editNode(newnode);
							//startEditingAtPath(getPath(newnode));
						}
					});
				}
				else
				{
					newmsg.setNParentpk(-1);
					top.add(newnode);
					model.nodeStructureChanged(top);
					setSelectedNode(newnode);
				}
				//updateTree(loader.hash_messages, listmsg);
				//AddToHash(loader.hash_messages, newnode);

				
			}
			else if("act_edit".equals(e.getActionCommand()))
			{
				
			}
			else if("act_delete".equals(e.getActionCommand()))
			{
				MessageLibNode node = getSelectedNode();
				if(node==null)
					return;
				if(node.getMessage().getNMessagepk()<=0) //new entry
				{
					RemoveFromTree(node);
					return;
				}
				if(JOptionPane.showConfirmDialog(MessageLibTreePanel.this, PAS.l("common_are_you_sure"), PAS.l("common_delete") + " " + node.getMessage().getSzName() + "?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.OK_OPTION)
				{
					WSMessageLibDelete del = new WSMessageLibDelete(this, node.getMessage());
					del.start();
				}
			}
			else if("act_messagelib_deleted".equals(e.getActionCommand()))
			{
				UBBMESSAGE msg = (UBBMESSAGE)e.getSource();
				if(msg.isBValid())
				{
					RemoveFromHash(msg);
				}
			}
			else if(StdSearchArea.ACTION_SEARCH_CLEARED.equals(e.getActionCommand()))
			{
				getTreeRenderer().setSearchString("");
				this.searchTreeNode("");
			}
			else if(StdSearchArea.ACTION_SEARCH_UPDATED.equals(e.getActionCommand()))
			{
				getTreeRenderer().setSearchString((String)e.getSource());
				List<UMSTreeNode> l = this.searchTreeNode((String)e.getSource());
				for(int i=0; i < l.size(); i++)
				{
					this.scrollPathToVisible(getPath(l.get(i)));
				}
			}
		}

		JMenu menu_new;
		JMenuItem menu_item_new_txt_template;
		JMenuItem menu_item_delete;
		JMenuItem menu_select;
		
		public MessageLibTree(ActionListener callback)
		{
			this(callback, UMSTree.TREEMODE.EDITOR, 60000);
		}
		
		
		public MessageLibTree(ActionListener callback, UMSTree.TREEMODE treemode, int UPDATEINTERVAL) {
			super(callback, UPDATEINTERVAL, treemode);
			tree = this;
			top = UMSTreeNode.newTopNode(new UBBMESSAGE());//new MessageLibTree.MessageLibNode(new UBBMESSAGE());
			super.model = model = new DefaultTreeModel(top);
			/*{
				@Override
				public Object getChild(Object parent, int index) {
					if (getTreeRenderer().isFilterActive()) {
						if (parent instanceof UMSTreeNode) {
							return ((UMSTreeNode) parent).getChildAt(index,
									getTreeRenderer().isFilterActive());
						}
					}
					return ((TreeNode) parent).getChildAt(index);
				}
				@Override
				public int getChildCount(Object parent) {
					if (getTreeRenderer().isFilterActive()) {
						if (parent instanceof UMSTreeNode) {
							return ((UMSTreeNode) parent).getChildCount(getTreeRenderer().isFilterActive());
						}
					}
					return ((TreeNode) parent).getChildCount();
				}
				
			};*/
			super.setModel(model);
			menu_select = new JMenu(PAS.l("common_select"));
			menu_new = new JMenu(PAS.l("common_new"));
			menu_item_new_txt_template = new JMenuItem("New text template");
			menu_item_new_txt_template.addActionListener(this);
			menu_item_new_txt_template.setActionCommand("act_new_txt_template");
			menu_new.add(menu_item_new_txt_template);
			//item = new JMenuItem("New TTS file");
			//menu_new.add(item);
			//item = new JMenuItem("New Recorded audio");
			//menu_new.add(item);

			menu_item_delete = new JMenuItem(PAS.l("common_delete"));
			menu_item_delete.addActionListener(this);
			menu_item_delete.setActionCommand("act_delete");
			switch(treemode)
			{
			case EDITOR:
			case SELECTION_WITH_EDIT:
				popup.add(menu_new);
				popup.add(menu_item_delete);
				break;
			case SELECTION_ONLY:
				//popup.add(menu_select);
				break;
				
			}

		}
		@Override
		public void valueChanged(TreeSelectionEvent e)
		{
			if(tree.getLastSelectedPathComponent()!=null && tree.getLastSelectedPathComponent().getClass().equals(MessageLibNode.class))
			{
				MessageLibNode dmtn =(MessageLibNode) tree.getLastSelectedPathComponent();
				if(dmtn!=null)
					MessageLibTreePanel.this.callback.actionPerformed(new ActionEvent(dmtn, ActionEvent.ACTION_PERFORMED, UMSTree.TREE_SELECTION_CHANGED));
			}

		}
		
		public void removeDeleted(Hashtable<Long, MessageLibNode> hash, List<UBBMESSAGE> list)
		{
			synchronized (hash) 
			{
				for(int i=0; i < list.size(); i++)
				{
					UBBMESSAGE msg = list.get(i);
					RemoveFromHash(msg);
				}
			}
		}
		public void updateTree(Hashtable<Long, MessageLibNode> hash, List<UBBMESSAGE> list)
		{
			synchronized(hash)
			{
				for(int i=0; i < list.size(); i++)
				{
					UBBMESSAGE msg = list.get(i);
					MessageLibNode node = new MessageLibNode(msg, model);
					if(hash.containsKey(msg.getNMessagepk()))
					{
						MessageLibNode toupdate = hash.get(msg.getNMessagepk());
						if(toupdate.getMessage().getNTimestamp()!=msg.getNTimestamp()) //run update IF it's not our own update
						{
							updateMessage(toupdate.getMessage(), msg);
							toupdate.setNodeObject(msg);
							model.nodeChanged(toupdate);
							MessageLibTreePanel.this.callback.actionPerformed(new ActionEvent(toupdate, ActionEvent.ACTION_PERFORMED, UMSTree.TREE_NODEVALUE_CHANGED));
						}
						else
						{
							System.out.println("MessageLibTree - own update received");
						}
					}
					else
					{
						hash.put(msg.getNMessagepk(), node);
						if(msg.getNParentpk() > 0 && hash.containsKey(msg.getNParentpk()))
						{
							DefaultMutableTreeNode parent = hash.get(msg.getNParentpk());
							parent.add(node);
							model.nodeStructureChanged(parent);
						}
						else
						{
							top.add(node);
							model.nodeStructureChanged(top);
						}
					}
				}
				TreePath path = new TreePath(top);
				tree.expandPath(path);
				//loader.SortCollection();
				//tree.updateUI();
			}
			if(getTreeRenderer().getSearchString().length()>0)
			{
				searchTreeNode(getTreeRenderer().getSearchString());
			}

		}
		protected void updateMessage(UBBMESSAGE m, UBBMESSAGE newmsg)
		{
			m.setFTemplate(newmsg.getFTemplate());
			m.setNCategorypk(newmsg.getNCategorypk());
			m.setNDepth(newmsg.getNDepth());
			m.setNDeptpk(newmsg.getNDeptpk());
			m.setNIvrcode(newmsg.getNIvrcode());
			m.setNLangpk(newmsg.getNLangpk());
			m.setNParentpk(newmsg.getNParentpk());
			m.setNTimestamp(newmsg.getNTimestamp());
			m.setNType(newmsg.getNType());
			m.setSzDescription(newmsg.getSzDescription());
			m.setSzFilename(newmsg.getSzFilename());
			m.setSzMessage(newmsg.getSzMessage());
			m.setSzName(newmsg.getSzName());
			m.setSzNumber(newmsg.getSzNumber());
		}

	}

	public class MsgLibLoader implements ActionListener
	{
		long n_server_timestamp = 0;
		boolean b_firsttime = true;
		public void setFirstLoadDone()
		{
			b_firsttime = false;
		}
		public boolean isFirst() { return b_firsttime; }
		Hashtable<Long, MessageLibNode> hash_messages = new Hashtable<Long, MessageLibNode>();
		Vector<MessageLibNode> vector_sorted = new Vector<MessageLibNode>();
		
		public void SortCollection()
		{
			synchronized(vector_sorted)
			{
				vector_sorted = new Vector(hash_messages.values());
				Collections.sort(vector_sorted);
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if("act_download_finished".equals(e.getActionCommand()))
			{
				UBBMESSAGELIST list = (UBBMESSAGELIST)e.getSource();
				n_server_timestamp = list.getNServertimestamp();
				//updateHash(list);
				try
				{
					tree.updateTree(hash_messages, list.getList().getUBBMESSAGE());
					tree.removeDeleted(hash_messages, list.getDeleted().getUBBMESSAGE());
				}
				catch(Exception err)
				{
					err.printStackTrace();
				}
				tree.signalDownloadFinished();
				System.out.println("Messagelib timestamp=" + n_server_timestamp);				
			}
		}
		
			
		/*protected void updateHash(UBBMESSAGELIST list)
		{
			synchronized(hash_messages)
			{
				for(int i=0; i < list.getList().getUBBMESSAGE().size(); i++)
				{
					UBBMESSAGE msg = list.getList().getUBBMESSAGE().get(i);
					if(hash_messages.containsKey(msg.getNMessagepk()))
					{
						MessageLibTree.MessageLibNode toupdate = hash_messages.get(msg.getNMessagepk());
						updateMessage(toupdate.getMessage(), msg);
					}
					else
					{
						//hash_messages.put(msg.getNMessagepk(), msg);
					}
				}
			}
		}*/
	
		public void Download()
		{
			new WSMessageLib(this, n_server_timestamp).start();
		}
	}
}