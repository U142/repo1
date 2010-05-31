package no.ums.pas.tas.treenodes;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.tree.DefaultTreeModel;

import no.ums.pas.core.defines.tree.UMSTreeNode;


public class CommonTASListItem extends UMSTreeNode
{
	public CommonTASListItem(Object hash_pk, Object o, DefaultTreeModel model)
	{
		super(hash_pk, o, model);
	}
	@Override
	public boolean Search(String s)
	{
		return false;
	}
	
	public boolean b_visible = true;
	public boolean b_added = false;
	public boolean b_selected = false;
	public boolean b_hovered = false;
	public boolean b_update_request_sent = false; //currently not in use
	public Rectangle rect;
	public boolean b_count_in_progress = false;
	public boolean b_send_in_progress = false;
	public boolean b_added_to_sendlist = false;
	public boolean b_count_is_outdated = false;
	public void setCountRequestSent(boolean b)
	{
		if(b_update_request_sent!=b)
		{
			b_update_request_sent = b;
			model.nodeChanged(this);
		}
	}
	public void setAddedToSendList(boolean b)
	{
		b_added_to_sendlist = b;
		
	}
	public boolean isAddedToSendList()
	{
		return b_added_to_sendlist;
	}
	public boolean canRunCountRequest()
	{
		return false;
	}
	public void setCountInProgress(boolean b)
	{
		b_count_in_progress = b;
	}
	public void setSendInProgress(boolean b)
	{
		b_send_in_progress = b;
	}
	public boolean getCountInProgress() { return b_count_in_progress; }
	public boolean getSendInProgress() { return b_send_in_progress; }
	public Color getOutdatedColor()
	{
		return Color.black;
	}
	@Override
	public int compareTo(UMSTreeNode o) {
		return 0;
	}
	@Override
	public String toString()
	{
		return "TASListItem";
	}
}