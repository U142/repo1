package no.ums.pas.core.logon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


import org.geotools.data.ows.Layer;
import org.jvnet.substance.SubstanceLookAndFeel;

public class WmsLayerTree extends JTree
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Layer> getM_layers() {
		return m_layers;
	}

	public void setM_layers(List<Layer> m_layers) {
		this.m_layers = m_layers;
	}

	public DefaultTreeModel getM_model() {
		return m_model;
	}

	public void setM_model(DefaultTreeModel m_model) {
		this.m_model = m_model;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	List<Layer> m_layers;
	protected DefaultTreeModel m_model;
	public DefaultTreeModel getModel()
	{
		return m_model;
	}

	public WmsLayerTree(TreeModel model)
	{
		super(model);
		m_model = (DefaultTreeModel)model;
		setCellRenderer(new LayerRenderer());
		setCellEditor(new CheckBoxNodeEditor(this));
		this.setRowHeight(24);
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	public WmsLayerTree()
	{
		this(new DefaultTreeModel(new DefaultMutableTreeNode()));
	}
	
	public Layer getSelectedLayer()
	{
		return ((LayerCheckBoxNode)((DefaultMutableTreeNode)this.getSelectionPath().getLastPathComponent()).getUserObject()).layer;
		
	}
	
	public void moveNodeUp()
	{
		MutableTreeNode old = (MutableTreeNode)this.getSelectionPath().getLastPathComponent();
		int oldIndex = m_model.getIndexOfChild(old.getParent(), old);
		if(oldIndex>0) //can't move further up
		{
			int newIndex = oldIndex-1;
			MutableTreeNode newNode = (MutableTreeNode)m_model.getChild(old.getParent(), newIndex);
			m_model.insertNodeInto(old, (MutableTreeNode)old.getParent(), newIndex);
			m_model.insertNodeInto(newNode, (MutableTreeNode)newNode.getParent(), oldIndex);
			m_model.nodeChanged(old);
			m_model.nodeChanged(newNode);
			this.scrollPathToVisible(this.getSelectionPath());
			this.updateUI();
		}
	}
	public void moveNodeDown()
	{
		MutableTreeNode old = (MutableTreeNode)this.getSelectionPath().getLastPathComponent();
		int oldIndex = m_model.getIndexOfChild(old.getParent(), old);
		if(oldIndex<m_model.getChildCount(old.getParent())-1) //can't move further down
		{
			int newIndex = oldIndex+1;
			MutableTreeNode newNode = (MutableTreeNode)m_model.getChild(old.getParent(), newIndex);
			m_model.insertNodeInto(old, (MutableTreeNode)old.getParent(), newIndex);
			m_model.insertNodeInto(newNode, (MutableTreeNode)newNode.getParent(), oldIndex);
			m_model.nodeChanged(old);
			m_model.nodeChanged(newNode);
			this.scrollPathToVisible(this.getSelectionPath());
			this.updateUI();
		}
	}
	

	
	
	public List<WmsLayer> getLayers()
	{
		List<WmsLayer> ret = new ArrayList<WmsLayer>();
		TreePath path = new TreePath(((DefaultMutableTreeNode)getModel().getRoot()).getPath());
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (node.getChildCount() > 0) {
            java.util.Enumeration<DefaultMutableTreeNode> e = node.children();
            while(e.hasMoreElements()) {
                DefaultMutableTreeNode n = e.nextElement();
                if(n.getUserObject() instanceof LayerCheckBoxNode)
                {
                	LayerCheckBoxNode chk = (LayerCheckBoxNode)n.getUserObject();
                	ret.add(new WmsLayer(chk.layer.getName(), chk.selected));
                }
            }
        }
		return ret;
	}
	
	public ArrayList<String> getSelectedLayers()
	{
		
		TreePath path = new TreePath(((DefaultMutableTreeNode)getModel().getRoot()).getPath());
		ArrayList<String> ret = new ArrayList<String>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (node.getChildCount() > 0) {
            java.util.Enumeration<DefaultMutableTreeNode> e = node.children();
            while(e.hasMoreElements()) {
                DefaultMutableTreeNode n = e.nextElement();
                if(n.getUserObject() instanceof LayerCheckBoxNode)
                {
                	LayerCheckBoxNode chk = (LayerCheckBoxNode)n.getUserObject();
                	if(chk.selected)
                		ret.add(chk.layer.getName());
                }
            }
        }
		return ret;
	}
    public void selectAllChildren(TreePath path, boolean b) {
        TreeNode node = (TreeNode)path.getLastPathComponent();
        select(node, b);
        if (node.getChildCount() > 0) {
            java.util.Enumeration<TreeNode> e = node.children();
            while(e.hasMoreElements()) {
                TreeNode n = e.nextElement();
                select(n, b);
                selectAllChildren(path.pathByAddingChild(n), b);
            }
        }
    }
    private void select(TreeNode node, boolean b) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
        if(n.getUserObject() instanceof CheckBoxNode)
        {
        	CheckBoxNode chk = (CheckBoxNode)n.getUserObject();
        	chk.selected = b;
        }
    }
	public void populate(List<Layer> layers, List<WmsLayer> check, boolean b_new_url /*check none*/, ActionListener callback)
	{
		this.setEditable(true);
		boolean b_topnode_set = false;
		m_layers = layers;
		Hashtable<Layer, DefaultMutableTreeNode> hash = new Hashtable<Layer, DefaultMutableTreeNode>();
		DefaultMutableTreeNode node;
		DefaultMutableTreeNode topnode = null;
		Layer toplayer = null;
		
		//set top node first
		for(int i=0; i < layers.size(); i++)
		{
			if(m_layers.get(i)!=null && m_layers.get(i).getParent()==null)
			{
				toplayer = layers.get(i);
				topnode = new DefaultMutableTreeNode(new DefaultMutableTreeNode(toplayer));
				m_model.setRoot(topnode);
				hash.put(layers.get(i), topnode);
				b_topnode_set = true;
				break;
			}
		}
		if(!b_topnode_set)
		{
			toplayer = new Layer("Top node");
			topnode = new DefaultMutableTreeNode(toplayer);
			hash.put(toplayer, topnode);
			m_model.setRoot(topnode);
		}

		//add main items
		if(!b_new_url)
		{
			for(WmsLayer l : check){
				String layername = l.layername;
				boolean b_check = l.checked;
				if(hash.containsKey(new Layer(layername)))
					continue;
				DefaultMutableTreeNode parent = hash.get(toplayer);
				if(parent==null)
					parent = topnode;
				//if(layers.contains(new Layer(layername))) //this is a valid layer
				//find layer by name
				Layer currentlayer = null;
				for(Layer w : layers)
				{
					if(w.getName()!=null && w.getName().equals(layername))
					{
						currentlayer = w;
						break;
					}
				}
				if(currentlayer!=null)
				{
					LayerCheckBoxNode chk = new LayerCheckBoxNode(currentlayer.getName(), b_check, currentlayer, callback);
					node = new DefaultMutableTreeNode(chk);
					m_model.insertNodeInto(node, parent, parent.getChildCount());
					hash.put(currentlayer, node);
				}
				
			}
		}
		//add the rest
		for(int i=0; i < layers.size(); i++)
		{
			Layer currentlayer = layers.get(i);
			Layer parentlayer = layers.get(i).getParent();
			if(hash.containsKey(currentlayer))
				continue;
			DefaultMutableTreeNode parent = hash.get(parentlayer);
			if(parent==null)
				parent = topnode;

			Boolean b = false;
			if(b_new_url)
				b = true;
			else if(check.contains(new WmsLayer(layers.get(i).getName(), false)))
				b = true;

			LayerCheckBoxNode chk = new LayerCheckBoxNode(currentlayer.getName(), b.booleanValue(), currentlayer, callback);
			node = new DefaultMutableTreeNode(chk);
			int idx = check.indexOf(new WmsLayer(layers.get(i).getName(), false));
			m_model.insertNodeInto(node, parent, idx>=0 ? idx : 0);
			
			hash.put(currentlayer, node);
		}
		
		final TreePath path = new TreePath(topnode.getPath());
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				//WmsLayerTree.this.updateUI();
				//this.expandPath(path);
				WmsLayerTree.this.fireTreeExpanded(path);
				//selectAllChildren(path, false);
				
			}
		});
		
	}
	
	
	
	@Override
	public String getToolTipText(MouseEvent event) {
		
		TreePath path = getPathForLocation(event.getX(),
											event.getY());
		if(path!=null && path.getLastPathComponent()!=null && path.getLastPathComponent() instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			Object userobj = node.getUserObject();
			if(userobj instanceof LayerCheckBoxNode)
			{
				return ((LayerCheckBoxNode)userobj).layer.getTitle();
			}
		}
		
		return super.getToolTipText(event);
	}


	public class LayerCheckBoxNode extends CheckBoxNode {
		Layer layer;
		ActionListener callback;
		public LayerCheckBoxNode(String text, boolean selected, Layer layer, ActionListener callback)
		{
			super(text, selected);
			this.layer = layer;
			this.callback = callback;
		}
	}
	class CheckBoxNode {
		  String text;

		  boolean selected;

		  public CheckBoxNode(String text, boolean selected) {
		    this.text = text;
		    this.selected = selected;
		  }

		  public boolean isSelected() {
		    return selected;
		  }

		  public void setSelected(boolean newValue) {
		    selected = newValue;
		  }

		  public String getText() {
		    return text;
		  }

		  public void setText(String newValue) {
		    text = newValue;
		  }

		  public String toString() {
		    return getClass().getName() + "[" + text + "/" + selected + "]";
		  }
	}
	protected class LayerCheckBox extends JCheckBox
	{
		public Layer layer;
	}
	protected class LayerRenderer extends DefaultTreeCellRenderer//implements TreeCellRenderer
	{
		private LayerCheckBox checkboxrenderer = new LayerCheckBox();
		private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		public LayerCheckBox getCheckRenderer() { return checkboxrenderer; }
		public DefaultTreeCellRenderer getNormalRenderer() { return renderer; }
		final Color selectionBorderColor = this.getBorderSelectionColor();
		final Color selectionForeground = this.getTextSelectionColor();
		final Color selectionBackground = this.getBackgroundSelectionColor();
		final Color nonselectionBackground = this.getBackgroundNonSelectionColor();
		final Color textForeground = this.getForeground();
		final Color textBackground = this.getBackground(); 
		
		public LayerRenderer()
		{
		}
		public Component getTreeCellRendererComponent(
				JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
	        checkboxrenderer.setOpaque(true);
	        renderer.setOpaque(true);
			
			Component returnValue = null;
	      if ((value != null) && (value instanceof DefaultMutableTreeNode)) 
	      {
	    	  DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) value;
	          Object userObject = ((DefaultMutableTreeNode) value)
	              .getUserObject();
	          if (userObject instanceof CheckBoxNode) 
	          {
	            LayerCheckBoxNode node = (LayerCheckBoxNode) userObject;
	            if(treenode.getChildCount()==0 && treenode.getParent().equals(tree.getModel().getRoot()) || 
	            		treenode.getChildCount()>0 && treenode.getParent().equals(tree.getModel().getRoot()))
	            {
	            	checkboxrenderer.setText(node.getText());
	            	checkboxrenderer.setSelected(node.isSelected());
		            checkboxrenderer.layer = node.layer;
		            returnValue = checkboxrenderer;
	            }
	            else
	            {
	            	renderer.setText(node.getText());
	            	returnValue = renderer;
	            	renderer.setBackground(sel ? selectionBackground : nonselectionBackground);
	            	renderer.setForeground(sel ? selectionForeground : textForeground);
	            	renderer.setBackgroundSelectionColor(selectionBackground);
	            	renderer.setBackgroundNonSelectionColor(nonselectionBackground);
	            	renderer.setTextNonSelectionColor(textNonSelectionColor);
	            	renderer.setTextSelectionColor(textSelectionColor);
	            	return new JLabel(node.getText());
	            }
	            returnValue.setBackground(sel ? selectionBackground : nonselectionBackground);
	            returnValue.setForeground(sel ? selectionForeground : textForeground);
	          }
	          else if (userObject instanceof DefaultMutableTreeNode)
	          {
	        	  Object userObject2 = ((DefaultMutableTreeNode) userObject)
	              .getUserObject();
	        	  if(userObject2 instanceof Layer)
	        	  {
		        	  Layer node = (Layer) userObject2;
		        	  renderer.setText(node.getTitle());
		        	  returnValue = renderer; //super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);;
		        	  returnValue = new JLabel(node.getTitle());
	        	  }
	          }
	          else
	          {
	        	  returnValue = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	          }	          
	      }
	      else {
	    	  return new JLabel(value.toString());
	        //returnValue = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	      }	
	        return returnValue;
		}
	}
	
	public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

		LayerRenderer renderer = new LayerRenderer();

		  ChangeEvent changeEvent = null;

		  JTree tree;

		  public CheckBoxNodeEditor(JTree tree) {
		    this.tree = tree;
		  }

		  public Object getCellEditorValue() {
		    LayerCheckBox checkbox = renderer.getCheckRenderer();
		    LayerCheckBoxNode checkBoxNode = new LayerCheckBoxNode(checkbox.getText(),
		        checkbox.isSelected(), checkbox.layer, null);
		    return checkBoxNode;
		  }

		  public boolean isCellEditable(EventObject event) {
		    boolean returnValue = false;
		    if (event instanceof MouseEvent) {
		    	MouseEvent mouseEvent = (MouseEvent) event;
		    	TreePath path = tree.getPathForLocation(mouseEvent.getX(),
		    							mouseEvent.getY());
		    	//select the node before altering it's check-state
		      	if(path!=null && !path.getLastPathComponent().equals(tree.getLastSelectedPathComponent()))
		      	{
		      		Object o = path.getLastPathComponent();
		      		if(o instanceof DefaultMutableTreeNode)
		      		{
		      			Rectangle rectPath = tree.getPathBounds(path);
		      			rectPath.width = tree.getRowHeight();
		      			rectPath.height = tree.getRowHeight();
		      			if(rectPath.contains(mouseEvent.getPoint()))
		      			{
		      				return true;
		      			}		      			
		      		}
		      		return false;
		      	}
		      	if (path != null) {
		      		Object node = path.getLastPathComponent();
		      		if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
		      			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
		      			Object userObject = treeNode.getUserObject();
		      			if(treeNode.isRoot())
		      				return false;
		      			if(treeNode.getChildCount()==0 && treeNode.getParent().equals(tree.getModel().getRoot()))
		      				return true;
		      			if(treeNode.getChildCount()>0 && treeNode.getParent().equals(tree.getModel().getRoot()))
		      				return true;
		      		}
		      	}
		    }
		    return returnValue;
		  }

		  public Component getTreeCellEditorComponent(JTree tree, Object value,
		      boolean selected, boolean expanded, boolean leaf, int row) {

		    Component editor = renderer.getTreeCellRendererComponent(tree, value,
		        true, expanded, leaf, row, true);

		    // editor always selected / focused
		    ItemListener itemListener = new ItemListener() {
		      public void itemStateChanged(ItemEvent itemEvent) {
		        if (stopCellEditing()) {
		          fireEditingStopped();
		        }
		      }
		    };
		    if (editor instanceof JCheckBox) {
		      ((JCheckBox) editor).addItemListener(itemListener);
		    }

		    return editor;
		  }
		}		
}