package no.ums.pas.send.messagelibrary;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.defines.tree.UMSTree.TREEMODE;
import no.ums.pas.send.messagelibrary.tree.MessageLibNode;
import no.ums.pas.ums.tools.StdSearchArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.UBBMESSAGE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;



public class MessageLibPanel extends DefaultPanel implements ComponentListener
{
	protected MessageLibTreePanel treepanel;
	protected MessageEditPanel editpanel;
	protected StdSearchArea search;
	protected StdTextLabel lbl_treeinfo;
	protected ActionListener callback = null;
	
	public MessageLibPanel(ActionListener callback, int UPDATE_INTERVAL, boolean b_editor_mode, boolean b_enable_multi_cc)
	{
		super();
		this.callback = callback;
		treepanel = new MessageLibTreePanel(this, (b_editor_mode ? TREEMODE.EDITOR : TREEMODE.SELECTION_ONLY), UPDATE_INTERVAL, b_editor_mode);
		editpanel = new MessageEditPanel(this, b_editor_mode, b_enable_multi_cc);
		search = new StdSearchArea("", false, PAS.l("common_search"));
		search.addActionListener(this);
		lbl_treeinfo = new StdTextLabel("  " + PAS.l("main_message_library_tree_info"));
		addComponentListener(this);
		add_controls();
		init();
		//treepanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		setPreferredSize(new Dimension(getWidth(), getHeight()));
	}
	public void Start(boolean b_only_once)
	{
		treepanel.tree.startUpdater(b_only_once);
	}
	public void Stop()
	{
		treepanel.tree.stopUpdater();

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(UMSTree.TREE_SELECTION_CHANGED.equals(e.getActionCommand()))
		{
			MessageLibNode node = (MessageLibNode)e.getSource();
			editpanel.setActiveMessage(node);
			treepanel.setEnabled(false);
			
		}
		else if(UMSTree.TREE_NODEVALUE_CHANGED.equals(e.getActionCommand()))
		{
			MessageLibNode node = (MessageLibNode)e.getSource();
			treepanel.AddToHash(node);
			if(editpanel.isActiveMessage(node.getMessage()))
			{
				editpanel.setActiveMessage(node);
				treepanel.tree.setSelectedNode(node);
				System.out.println("Messagelib - update current message due to update");
			}
			treepanel.tree.searchTreeNode();
		}
		else if(UMSTree.TREE_SELECTION_NULL.equals(e.getActionCommand()))
		{
			editpanel.setActiveMessage(null);
		}
		else if(UMSTree.TREE_TREENODE_DELETED.equals(e.getActionCommand()))
		{
			UBBMESSAGE msg = (UBBMESSAGE)e.getSource();
			//if(msg.getNMessagepk(equals(editpanel.getActiveMessage().getMessage()))
			if(editpanel.isActiveMessage(msg))
			{
				//the active record is deleted
				editpanel.setActiveMessage(null);
				System.out.println("Messagelib - Reset current message selection due to deletion");
			}
		}
		else if(StdSearchArea.ACTION_SEARCH_CLEARED.equals(e.getActionCommand()))
		{
			treepanel.tree.actionPerformed(e);
		}
		else if(StdSearchArea.ACTION_SEARCH_UPDATED.equals(e.getActionCommand()))
		{
			treepanel.tree.actionPerformed(e);
		}
		else if(MessageLibDlg.ACT_MESSAGE_SELECTED.equals(e.getActionCommand()))
		{
			MessageLibNode node = (MessageLibNode)e.getSource();
			if(callback!=null)
				callback.actionPerformed(new ActionEvent(node.getMessage(), ActionEvent.ACTION_PERFORMED, MessageLibDlg.ACT_MESSAGE_SELECTED));
		}
		else if(MessageLibDlg.ACT_MESSAGE_SELECTION_CANCELLED.equals(e.getActionCommand()))
		{
			callback.actionPerformed(e);
		}
	}

	@Override
	public void add_controls() {
		add_spacing(DIR_VERTICAL, 5);
		set_gridconst(0, inc_panels(), 1, 1);
		add(search, m_gridconst);
		if(editpanel.isEditorMode())
		{
			set_gridconst(1, get_panel(), 1, 1);
			add(lbl_treeinfo, m_gridconst);
		}
		add_spacing(DIR_VERTICAL, 5);
		set_gridconst(0, inc_panels(), 2, 1);
		add(treepanel, m_gridconst);
		set_gridconst(0, inc_panels(), 2, 1);
		add(editpanel, m_gridconst);
	}

	@Override
	public void init() {
		setVisible(true);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = (getHeight()) - 340;
		//treepanel.pane.setPreferredSize(new Dimension(w, h));
		search.setPreferredSize(new Dimension(200, 25));
		lbl_treeinfo.setPreferredSize(new Dimension(280,25));
		treepanel.setPreferredSize(new Dimension(w, h));
		editpanel.setPreferredSize(new Dimension(w, 300));
		//super.componentResized(e);
	}
	
}