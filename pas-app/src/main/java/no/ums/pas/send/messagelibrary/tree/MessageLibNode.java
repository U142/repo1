package no.ums.pas.send.messagelibrary.tree;

import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.ws.pas.UBBMESSAGE;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MessageLibNode extends UMSTreeNode //implements Comparable<MessageLibNode>
{
	public MessageLibNode(UBBMESSAGE m, DefaultTreeModel model)
	{
		super(new Long(m.getNMessagepk()), m, model);
		msg = m;
		
	}
	
	@Override
	public boolean Search(String s)
	{
		if(getMessage()!=null && getMessage().getSzName()!=null && getMessage().getSzName().toUpperCase().indexOf(s.toUpperCase())>=0)
				return true;
		if(getMessage()!=null && getMessage().getSzMessage()!=null && getMessage().getSzMessage().toUpperCase().indexOf(s.toUpperCase())>=0)
			return true;
		return false;
	}

	protected UBBMESSAGE msg;
	public UBBMESSAGE getMessage() { return msg; }
	protected boolean b_is_saved = true;
	public void setIsSaved(boolean b) { b_is_saved = b; }
	public boolean getIsSaved() { 
		return b_is_saved; 
	}
	public void setNodeObject(UBBMESSAGE m)
	{
		msg = m;
		setUserObject(msg);
		
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)this.getParent();
		//if(getMessage().getSzName()!=null && m.getSzName().compareTo(this.getMessage().getSzName())!=0)
		{
			Object[] removedChilds = { this };
			int[] childIndices = { parent.getIndex(this) };
			parent.remove(this);
			//model.nodeStructureChanged(parent);
			model.nodesWereRemoved(parent, childIndices, removedChilds);
			//model.nodeChanged(parent);
			
			//parent.add(this);
			int newidx = parent.getChildCount() - 1;
			//parent.add(this);
			newidx = parent.getChildCount()-1;
			if(newidx<0)
				newidx = 0;
			parent.insert(this, newidx);
			
			int insertedIndex [] = { parent.getIndex(this) };
			//model.nodeStructureChanged(parent);
			model.nodesWereInserted(parent, insertedIndex);
			//model.nodeChanged(this);
			//model.nodeChanged(parent);
			//model.nodeStructureChanged(parent);
			//model.nodeChanged(parent);
		}
		//tree.model.removeNodeFromParent(this);
		//tree.model.reload(this.getParent());
		
	}
	@Override 
	public String toString()
	{
		return msg.getSzName();
	}
	@Override
	public void setUserObject(Object o) {
		super.setUserObject(o);
		
		if(this.getParent()!=null)
		{
			/*List parents_children = new ArrayList();
			for(int i=0; i < this.getParent().getChildCount(); i++)
			{
				parents_children.add(this.getParent().getChildAt(i));
			}*/
			//Collections.sort(this.parent.children());
			//DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.getParent();
			//ArrayList children = Collections.list(node.children());
			//Collections.sort(children);
			/*node.removeAllChildren();
			Iterator childrenIterator = children.iterator();
			while (childrenIterator.hasNext()) {
				node.add((DefaultMutableTreeNode) childrenIterator.next());
			}*/
			
		}
	}
	@Override
	public int compareTo(UMSTreeNode o) {
		//if(this.getMessage().getNDepth()<o.getMessage().getNDepth())
		//	return 1;
		//else if(this.getMessage().getNDepth()>o.getMessage().getNDepth())
		//	return -1;
		MessageLibNode i1 = (MessageLibNode)o;
		return this.getMessage().getSzName().compareToIgnoreCase(i1.getMessage().getSzName());
	}
}