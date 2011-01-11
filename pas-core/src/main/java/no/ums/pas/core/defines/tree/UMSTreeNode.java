package no.ums.pas.core.defines.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;

public abstract class UMSTreeNode extends DefaultMutableTreeNode implements Comparable<UMSTreeNode>
{
	protected DefaultTreeModel model;
	public DefaultTreeModel getModel() { return model; }
	protected boolean b_found_in_search = true;
	protected Object hash_pk;
	public Object getHashPk() { return hash_pk; }
	/**
	 * 
	 * @param b - mark node as found/not found in search
	 * returns if the mark has changed
	 */
	public boolean setFoundInSearch(boolean b)
	{
		if(b_found_in_search!=b)
		{
			b_found_in_search = b;
			return true;
		}
		return false;
	}
	public boolean isFoundInSearch()
	{
		return b_found_in_search;
	}
	public UMSTreeNode(Object hash_pk, Object o, DefaultTreeModel model)
	{
		super(o);
		this.hash_pk = hash_pk;
		this.model = model;
	}

	public abstract boolean Search(String s);
	
	@Override
	public abstract int compareTo(UMSTreeNode o);
	
	@Override
    public void insert(final MutableTreeNode newChild, final int childIndex) {
        super.insert(newChild, childIndex);
        if(this.children!=null)
        	Collections.sort(this.children);
    }
	@Override
	public abstract String toString();
	
	public static UMSTreeNode newTopNode(Object o)
	{
		return new UMSTreeNode(null, o, null)
		{
			public int compareTo(UMSTreeNode o)
			{
				return 0;
			}
			public String toString()
			{
				return "TOPNODE";
			}
			public boolean Search(String s)
			{
				return false;
			}
		};
	}
	public TreeNode getChildAt(int index, boolean filterIsActive) {
		if (!filterIsActive) {
		      return super.getChildAt(index);
		}
		if (children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		int realIndex = -1;
		int visibleIndex = -1;
		Enumeration e = children.elements();
		while (e.hasMoreElements()) {
			UMSTreeNode node = (UMSTreeNode) e.nextElement();
			if (node.isFoundInSearch()) {
				visibleIndex++;
			}
			realIndex++;
			if (visibleIndex == index) {
				return (TreeNode) children.elementAt(realIndex);
			}
		}
		//throw new ArrayIndexOutOfBoundsException("index unmatched");
		return null;
	}
	public int getChildCount(boolean filterIsActive) {
		if (!filterIsActive) {
			return super.getChildCount();
		}
		if (children == null) {
			return 0;
		}

		int count = 0;
		Enumeration e = children.elements();
		while (e.hasMoreElements()) {
			UMSTreeNode node = (UMSTreeNode) e.nextElement();
			if (node.isFoundInSearch()) {
				count++;
			}
		}

		return count;
	}	
	
}